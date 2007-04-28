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

/**
 * Implements showing a list of columns for a selected table to the 
 * user and dropping the ones that are selected when the user presses the 
 * drop column(s) button.
 * 
 * @author rmmannin
 *
 */
public class RemoveColumnCommand extends AbstractRefactoringCommand
{
    
    /** Logger for this class. */
    private final static ILogger log = 
                       LoggerController.createLogger(RemoveColumnCommand.class);
    
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(RemoveColumnCommand.class);
    
    /**
     * Ctor specifying the current session.
     */
    public RemoveColumnCommand(ISession session, IDatabaseObjectInfo[] info)
    {
        super(session, info);
    }
    
    /**
     * Execute this command. 
     */
    public void execute()
    {
        if (! (_info[0] instanceof ITableInfo)) {
            return;
        }
        try {
            ITableInfo ti = (ITableInfo)_info[0];
            TableColumnInfo[] columns = 
                _session.getSQLConnection().getSQLMetaData().getColumnInfo(ti);

            if (columns.length < 2) {
                //i18n[RemoveColumnAction.singleObjectMessage=The table's only 
                //column cannot be removed - a table must have a least one column]
                String msg = 
                    s_stringMgr.getString("RemoveColumnAction.singleColumnMessage");
                _session.getMessageHandler().showErrorMessage(msg);
                return;
            }
            
            try {
                HibernateDialect dialect =  
                    DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                              _session.getApplication().getMainFrame(), 
                                              _session.getMetaData());
                if (!dialect.supportsDropColumn()) {
                    //i18n[RemoveColumnAction.removeColumnNotSupported=This
                    //database ({0}) does not support dropping columns]
                    String msg = 
                        s_stringMgr.getString("RemoveColumnAction.removeColumnNotSupported",
                                              dialect.getDisplayName());
                    _session.getMessageHandler().showErrorMessage(msg);
                    return;                    
                }
            } catch (UserCancelledOperationException e) {
                log.info("User cancelled add column request");
                return;
            }        
            
            //Show the user a dialog with a list of columns and ask them to select
            // one or more columns to drop
            super.showColumnListDialog(new DropActionListener(), 
                                       new DropSQLActionListener(), 0);
        } catch (SQLException e) {
            log.error("Unexpected exception "+e.getMessage(), e);
        }
        
        
    }

    protected void getSQLFromDialog(SQLResultListener listener) {
        TableColumnInfo[] columns = columnListDialog.getSelectedColumnList();
        
        
        HibernateDialect dialect = null; 
            
        
        String[] result = new String[columns.length];
        try {
            dialect = DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                                _session.getApplication().getMainFrame(), 
                                                _session.getMetaData());
            // TODO: add configuration for whether or not to qualify names.
            String tableName = _info[0].getQualifiedName();
            for (int i = 0; i < columns.length; i++) {
                TableColumnInfo info = columns[i];
                String columnName = info.getColumnName();
                result[i] = dialect.getColumnDropSQL(tableName, columnName);
            }
        } catch (UnsupportedOperationException e2) {
            //i18n[RemoveColumnCommand.unsupportedOperationMsg=The {0} dialect
            //doesn's support dropping columns]
            String msg = 
                s_stringMgr.getString("RemoveColumnCommand.unsupportedOperationMsg", 
                                      dialect.getDisplayName());
                                      
            _session.getMessageHandler().showMessage(msg);
        } catch (UserCancelledOperationException e) {
            // user cancelled selecting a dialog. do nothing?
        }
        listener.finished(result);        
    }
    
    
    private class DropSQLActionListener implements ActionListener, 
                                                   SQLResultListener 
    {
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
                new ErrorDialog(columnListDialog, script.toString());
            //i18n[RemoveColumnCommand.sqlDialogTitle=Remove Column SQL]
            String title = 
                s_stringMgr.getString("RemoveColumnCommand.sqlDialogTitle");
            sqldialog.setTitle(title);
            sqldialog.setVisible(true);                                        
        }
        
        public void actionPerformed(ActionEvent e) {
            getSQLFromDialog(this);
        }
    }
    
    private class DropActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            // TODO: should probably be using the ActionEvent and not directly
            // referencing the columnListDialog.
            if (columnListDialog == null) {
                System.err.println("dialog was null");
                return;
            }
            HibernateDialect dialect = null;
            try {
                dialect =  
                    DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                              _session.getApplication().getMainFrame(), 
                                              _session.getMetaData());
            } catch (UserCancelledOperationException ex) {
                log.info("User cancelled add column request");
                return;                
            }
            
            CommandExecHandler handler = new CommandExecHandler(_session);
            // For each column that the user selected, issue the correct drop column
            // statement.  This may be db-specific
            TableColumnInfo[] columns = columnListDialog.getSelectedColumnList();
            for (int i = 0; i < columns.length; i++) {
                TableColumnInfo column = columns[i];
                String dropSQL = 
                    dialect.getColumnDropSQL(column.getTableName(), 
                                             column.getColumnName());
                log.info("AddColumnCommand: executing SQL - "+dropSQL);
                SQLExecuterTask executer = 
                    new SQLExecuterTask(_session, 
                                        dropSQL, 
                                        handler);
    
                // Execute the sql synchronously
                executer.run();                
                
                if (handler.exceptionEncountered()) {
                    // Stop processing statements
                    break;
                }
                
            }
            columnListDialog.setVisible(false);
        }
        
    }
        
}