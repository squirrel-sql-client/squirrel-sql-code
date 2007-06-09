package net.sourceforge.squirrel_sql.plugins.refactoring.commands;

/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.gui.db.ColumnDetailDialog;
import net.sourceforge.squirrel_sql.client.gui.db.ColumnListDialog;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.DBUtil;

import org.hibernate.HibernateException;

/**
 * Implements showing a list of columns for a selected table to the 
 * user and dropping the ones that are selected when the user presses the 
 * drop column(s) button.
 * 
 * @author rmmannin
 *
 */
public class ModifyColumnCommand extends AbstractRefactoringCommand
{
    
    /** Logger for this class. */
    private final static ILogger log = 
                       LoggerController.createLogger(RemoveColumnCommand.class);
    
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(RemoveColumnCommand.class);
    
    private ColumnListDialog listDialog = null;
    
    private MainFrame mainFrame = null;    
    
    private TableColumnInfo columnToModify = null;
    
    private HibernateDialect dialect = null;    
    /**
     * Ctor specifying the current session.
     */
    public ModifyColumnCommand(ISession session, IDatabaseObjectInfo[] info)
    {
        super(session, info);
    }
    
    /**
     * Execute this command. Save the session and selected objects in the plugin
     * for use in paste command.
     */
    public void execute()
    {
        if (! (_info[0] instanceof ITableInfo)) {
            return;
        }
        //Show the user a dialog with a list of columns and ask them to select
        // one or more columns to drop
        try {
            ITableInfo ti = (ITableInfo)_info[0];
            TableColumnInfo[] columns = 
                _session.getSQLConnection().getSQLMetaData().getColumnInfo(ti);
            
            // Don't show columns dialog if only one column exists to be modified
            if (columns.length == 1) {
                columnToModify = columns[0];
                showColumnDetailsDialog();
                return;
            }
            
            if (listDialog == null) {
                listDialog = 
                    new ColumnListDialog(columns, 
                                         ColumnListDialog.MODIFY_COLUMN_MODE);
                ActionListener listener = 
                    new ColumnListSelectionActionListener();
                listDialog.addColumnSelectionListener(listener);
                mainFrame = _session.getApplication().getMainFrame();
                listDialog.setLocationRelativeTo(mainFrame);
                listDialog.setSingleSelection();
            }
            listDialog.setTableName(ti.getQualifiedName());
            listDialog.setVisible(true);
        } catch (SQLException e) {
            log.error("Unexpected exception "+e.getMessage(), e);
        }
    }

    protected void getSQLFromDialog(SQLResultListener listener) {
        TableColumnInfo to = columnDetailDialog.getColumnInfo();
        String dbName = columnDetailDialog.getSelectedDBName();
        HibernateDialect dialect = DialectFactory.getDialect(dbName);
        
        String[] result = null;
        try {
            result = DBUtil.getAlterSQLForColumnChange(columnToModify, to, dialect);
        } catch (HibernateException e1) {
            String dataType = columnDetailDialog.getSelectedTypeName();
            // TODO: I18N
            JOptionPane.showMessageDialog(columnDetailDialog, 
                    "The "+dialect.getDisplayName()+" dialect doesn't support the type "+dataType, 
                    "Missing Dialect Type Mapping", 
                    JOptionPane.ERROR_MESSAGE);            
        } catch (UnsupportedOperationException e2) {
            //i18n[ModifyColumnCommand.unsupportedOperationMsg=The {0} dialect
            //doesn's support modifying columns]
            /*
            String msg = 
                s_stringMgr.getString("ModifyColumnCommand.unsupportedOperationMsg",
                                      dbName);
                                      */
            // Better to use exception message as it contains more info
            _session.showMessage(e2.getMessage());
        }
        listener.finished(result);
        
    }
    
    private void showColumnDetailsDialog() {
        try {
            dialect =  
                DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                          _session.getApplication().getMainFrame(), 
                                          _session.getMetaData());
            String dbName = dialect.getDisplayName();                
            columnDetailDialog = 
                new ColumnDetailDialog(ColumnDetailDialog.MODIFY_MODE);
            columnDetailDialog.setExistingColumnInfo(columnToModify);
            columnDetailDialog.setTableName(_info[0].getQualifiedName());
            columnDetailDialog.addShowSQLListener(new ShowSQLButtonListener());
            columnDetailDialog.addEditSQLListener(new EditSQLListener());
            columnDetailDialog.addExecuteListener(new OKButtonListener());
            mainFrame = _session.getApplication().getMainFrame();
            columnDetailDialog.setLocationRelativeTo(mainFrame);
            columnDetailDialog.setSelectedDialect(dbName);
            columnDetailDialog.setVisible(true);
        } catch (UserCancelledOperationException ex) {
            ex.printStackTrace();
        }        
    }
    
    private class ColumnListSelectionActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (listDialog == null) {
                System.err.println("dialog was null");
                return;
            }
            listDialog.setVisible(false);
            TableColumnInfo[] colInfos = listDialog.getSelectedColumnList();
            if (colInfos == null || colInfos.length != 1) {
                // TODO I18N
                _session.showMessage("Exactly one column must be selected to modify");
                return;
            }
            columnToModify = colInfos[0];
            showColumnDetailsDialog();
        }
    }
        
    private class OKButtonListener implements ActionListener, SQLResultListener {

        public void finished(String[] sqls) {
            CommandExecHandler handler = new CommandExecHandler(_session);
            
            if (sqls == null || sqls.length == 0) {
                // TODO: tell the user no changes
                return;
            }            
            // TODO: execute SQL.  Maybe should put it on the SQLEditor first?
            // No, that should be configurable.

            for (int i = 0; i < sqls.length; i++) {
                String sql = sqls[i];
                
                log.info("ModifyColumnCommand: executing SQL - "+sql);
                
                SQLExecuterTask executer = 
                    new SQLExecuterTask(_session, 
                                        sql, 
                                        handler);
    
                // Execute the sql synchronously
                executer.run();                
                
                if (handler.exceptionEncountered()) {
                    // Stop processing statements
                    break;
                }
            }
            columnDetailDialog.setVisible(false);
        }
        
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            getSQLFromDialog(this);
        }
        
    }

    private class ShowSQLButtonListener implements ActionListener, SQLResultListener {

        public void finished(String[] sqls) {
            if (sqls == null || sqls.length == 0) {
//              TODO: tell the user no changes
                return;
            }

            StringBuffer script = new StringBuffer();
            for (int i = 0; i < sqls.length; i++) {
                script.append(sqls[i]);
                script.append(";\n\n");
            }
            
            ErrorDialog sqldialog = 
                new ErrorDialog(columnDetailDialog, script.toString());
            //i18n[ModifyColumnCommand.sqlDialogTitle=Modify column SQL]
            String title = 
                s_stringMgr.getString("ModifyColumnCommand.sqlDialogTitle");
            sqldialog.setTitle(title);
            sqldialog.setVisible(true);                            
        }
        
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            getSQLFromDialog(this);
        }
        
    }
    
}