package net.sourceforge.squirrel_sql.plugins.refactoring.commands;

/*
 * Copyright (C) 2005 Rob Manning
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.db.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.client.gui.db.ColumnDetailDialog;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.DBUtil;

import org.hibernate.HibernateException;



public class AddColumnCommand implements ICommand
{
    
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(AddColumnCommand.class);

    /** Logger for this class. */
    private final static ILogger log = 
                      LoggerController.createLogger(AddColumnCommand.class);
    
    /** Current session */
    private ISession _session;
    
    /** Selected table */
    private final IDatabaseObjectInfo _info;
    
    
    private ColumnDetailDialog dialog = null;
    
    private HibernateDialect dialect = null;
    
    private MainFrame mainFrame = null;
    
    /**
     * Ctor specifying the current session.
     */
    public AddColumnCommand(ISession session, IDatabaseObjectInfo info)
    {
        super();
        _session = session;
        _info = info;
    }
    
    /**
     * Execute this command. Save the session and selected objects in the plugin
     * for use in paste command.
     */
    public void execute()
    {
        String tableName = _info.getSimpleName();
        try {
            dialect =  
                DialectFactory.getDialect(_session, DialectFactory.DEST_TYPE);
            String dbName = dialect.getDisplayName();
            String title = 
                s_stringMgr.getString("AddColumnCommand.addColumnDialogTitle");
            dialog = new ColumnDetailDialog(ColumnDetailDialog.ADD_MODE);
            dialog.setTableName(tableName);
            dialog.addOKListener(new AddButtonListener());
            dialog.addShowSQLListener(new ShowSQLButtonListener());
            dialog.addDialectListListener(new DialectListListener());
            mainFrame = _session.getApplication().getMainFrame();
            dialog.setLocationRelativeTo(mainFrame);
            dialog.setSelectedDialect(dbName);
            dialog.setVisible(true);
        } catch (UserCancelledOperationException e) {
            log.info("User cancelled add column request");
            return;
        }        
        
    }

    private String[] getSQLFromDialog() {
        TableColumnInfo info = dialog.getColumnInfo();
        String[] result = null;
        try {
            result = DBUtil.getAlterSQLForColumnAddition(dialog.getTableName(), 
                                                         info, 
                                                         dialect);
        } catch (HibernateException e1) {
            String dataType = dialog.getSelectedTypeName();
            JOptionPane.showMessageDialog(
                    dialog, 
                    "The "+dialect.getDisplayName()+" dialect doesn't support the type "+dataType, 
                    "Missing Dialect Type Mapping", 
                    JOptionPane.ERROR_MESSAGE);            
        } catch (UnsupportedOperationException e2) {
            String dbName = dialect.getDisplayName();
            //i18n[AddColumnCommand.unsupportedOperationMsg=The {0} dialect
            //doesn's support adding columns to tables]
            String msg = 
                s_stringMgr.getString("AddColumnCommand.unsupportedOperationMsg",
                                      dbName);
            _session.getMessageHandler().showMessage(msg);

        }
        return result;
        
    }
    
    private class AddButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String columnName = dialog.getColumnInfo().getColumnName();
            String tableName = dialog.getTableName();
            if (!isColumnNameUnique(columnName)) {
                JOptionPane.showMessageDialog(
                        dialog, 
                        "Table "+tableName+" already has a column called "+columnName, 
                        "Problem", 
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            String[] sqls = getSQLFromDialog();
            if (sqls == null) {
                return;
            }
            AddColumnExecHandler handler = new AddColumnExecHandler(_session);            
            // TODO: execute SQL.  Maybe should put it on the SQLEditor first?
            // No, that should be configurable.

            for (int i = 0; i < sqls.length; i++) {
                String sql = sqls[i];
                
                log.info("AddColumnCommand: executing SQL - "+sql);
                
                SQLExecuterTask executer = 
                    new SQLExecuterTask(_session, 
                                        sql, 
                                        handler);
    
                // Execute the sql synchronously
                executer.run();                
                
                if (handler.exceptionEncountered) {
                    // Stop processing statements
                    break;
                }
            }
            dialog.setVisible(false);
        }
        
        /**
         * Tests to see if the specified column name already exists.
         * @param columnName the column name to check.
         * @return true if the column name doesn't exists; false otherwise
         */
        private boolean isColumnNameUnique(String columnName) {
            boolean result = true;
            SQLDatabaseMetaData d = _session.getSQLConnection().getSQLMetaData();
            try {
            TableColumnInfo[] columnInfos = d.getColumnInfo((ITableInfo)_info);
                for (int i = 0; i < columnInfos.length; i++) {
                    TableColumnInfo columnInfo = columnInfos[i];
                    String existingColumnName = columnInfo.getColumnName();
                    if (columnName.equalsIgnoreCase(existingColumnName)) {
                        result = false;
                        break;
                    }
                }
            } catch (SQLException e) {
                log.error("Unexpected exception - "+e.getMessage(), e);
            }
            return result;
        }
    }

    private class AddColumnExecHandler extends DefaultSQLExecuterHandler {
        private boolean exceptionEncountered = false;
        
        public AddColumnExecHandler(ISession session) {
            super(session);
        }

        /* (non-Javadoc)
         * @see net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler#sqlExecutionException(java.lang.Throwable, java.lang.String)
         */
        public void sqlExecutionException(Throwable th, String postErrorString) {
            super.sqlExecutionException(th, postErrorString);
            exceptionEncountered = true;
        }
    }
    
    private class ShowSQLButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String[] sqls = getSQLFromDialog();
            
            if (sqls != null) {
                StringBuffer script = new StringBuffer();
                for (int i = 0; i < sqls.length; i++) {
                    script.append(sqls[i]);
                    script.append(";\n\n");
                }
                
                ErrorDialog sqldialog = 
                    new ErrorDialog(dialog, script.toString());
                //i18n[AddColumnCommand.sqlDialogTitle=Add column SQL]
                String title = 
                    s_stringMgr.getString("AddColumnCommand.sqlDialogTitle");
                sqldialog.setTitle(title);
                sqldialog.setVisible(true);
            }
        }
        
    }
    
    private class DialectListListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            String dbName = dialog.getSelectedDBName();
            dialect = DialectFactory.getDialect(dbName);
        }
    }
}