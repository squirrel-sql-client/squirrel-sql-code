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

import net.sourceforge.squirrel_sql.client.db.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.client.gui.db.ColumnListDialog;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
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
 * user and making the ones that are selected become the primary key for
 * the table
 * 
 * @author rmmannin
 *
 */
public class AddPrimaryKeyCommand extends AbstractRefactoringCommand {
    
    /** Logger for this class. */
    private final static ILogger log = 
                       LoggerController.createLogger(RemoveColumnCommand.class);
    
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(AddPrimaryKeyCommand.class);
        
    /**
     * Ctor specifying the current session.
     */
    public AddPrimaryKeyCommand(ISession session, IDatabaseObjectInfo[] info)
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
            if (tableHasPrimaryKey()) {
                //i18n[AddPrimaryKeyCommand.primaryKeyExists=Table {0} already 
                //has a primary key]
                String msg = 
                    s_stringMgr.getString("AddPrimaryKeyCommand.primaryKeyExists", 
                                           ti.getSimpleName());
                _session.getMessageHandler().showErrorMessage(msg);
                return;
            }
            
            super.showColumnListDialog(new AddPrimaryKeyActionListener(), 
                                       new ShowSQLListener(), 
                                       ColumnListDialog.ADD_PRIMARY_KEY_MODE);
        } catch (Exception e) {
            _session.getMessageHandler().showErrorMessage(e);
            log.error("Unexpected exception "+e.getMessage(), e);
        }
        
        
    }

    protected String[] getSQLFromDialog() {
        TableColumnInfo[] columns = columnListDialog.getSelectedColumnList();
        HibernateDialect dialect = null; 
            
        
        String[] result = null;
        try {
            dialect = DialectFactory.getDialect(_session, DialectFactory.DEST_TYPE);

            String pkName = columnListDialog.getPrimaryKeyName();
            
            result = dialect.getAddPrimaryKeySQL(pkName, columns, (ITableInfo)_info[0]);
        } catch (UnsupportedOperationException e2) {
            //i18n[AddPrimaryKeyCommand.unsupportedOperationMsg=The {0} 
            //dialect doesn't support adding primary keys to tables]
            String msg = 
                s_stringMgr.getString("AddPrimaryKeyCommand.unsupportedOperationMsg", 
                                      dialect.getDisplayName());
                                      
            _session.getMessageHandler().showErrorMessage(msg);
        } catch (UserCancelledOperationException e) {
            // user cancelled selecting a dialog. do nothing?
        }
        return result;
        
    }
    
    
    private class ShowSQLListener implements ActionListener {
        public void actionPerformed( ActionEvent e) {
            String[] addPKSQLs = getSQLFromDialog();
            if (addPKSQLs == null || addPKSQLs.length == 0) {
//              TODO: tell the user no changes
                return;
            }

            StringBuffer script = new StringBuffer();
            for (int i = 0; i < addPKSQLs.length; i++) {
                script.append(addPKSQLs[i]);
                script.append(";\n\n");
            }
            
            ErrorDialog sqldialog = 
                new ErrorDialog(columnListDialog, script.toString());
            //i18n[AddPrimaryKeyCommand.sqlDialogTitle=Add Primary Key SQL]
            String title = 
                s_stringMgr.getString("AddPrimaryKeyCommand.sqlDialogTitle");
            sqldialog.setTitle(title);
            sqldialog.setVisible(true);                
            
        }
    }
    
    private class AddPrimaryKeyActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (columnListDialog == null) {
                System.err.println("dialog was null");
                return;
            }
            
            CommandExecHandler handler = new CommandExecHandler(_session);
            
            String[] addPKSQLs = getSQLFromDialog();
            if (addPKSQLs != null) {
                for (int i = 0; i < addPKSQLs.length; i++) {
                    String addPKSQL = addPKSQLs[i];
                    log.info("AddPrimaryKeyCommand: executing SQL - "+addPKSQL);
                    SQLExecuterTask executer = 
                        new SQLExecuterTask(_session, addPKSQL, handler);
                    executer.run();
                }
            }
            columnListDialog.setVisible(false);
        }
        
    }
        
}