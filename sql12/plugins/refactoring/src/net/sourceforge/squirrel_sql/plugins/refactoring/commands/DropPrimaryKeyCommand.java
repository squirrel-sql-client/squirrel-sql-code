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
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Implements showing a list of primay key columns for a selected table to the 
 * user, allowing the user to drop the primary key or view the SQL that will do
 * this.
 * 
 * @author manningr
 *
 */
public class DropPrimaryKeyCommand extends AbstractRefactoringCommand {
    
    /** Logger for this class. */
    private final static ILogger log = 
                       LoggerController.createLogger(RemoveColumnCommand.class);
    
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DropPrimaryKeyCommand.class);
        
    /**
     * Ctor specifying the current session.
     */
    public DropPrimaryKeyCommand(ISession session, IDatabaseObjectInfo info)
    {
        super(session, info);
    }
    
    /**
     * Execute this command. 
     */
    public void execute()
    {
        if (! (_info instanceof ITableInfo)) {
            return;
        }
        try {
            // TODO: ensure that the table has a primary key
            super.showColumnListDialog(new DropPrimaryKeyActionListener(), 
                                       new ShowSQLListener(), 
                                       ColumnListDialog.DROP_PRIMARY_KEY_MODE);
        } catch (Exception e) {
            log.error("Unexpected exception "+e.getMessage(), e);
        }
        
        
    }

    private String getSQLFromDialog() {
        HibernateDialect dialect = null; 
        
        String result = null;
        try {
            dialect = DialectFactory.getDialect(_session, DialectFactory.DEST_TYPE);            
            result = 
                dialect.getDropPrimaryKeySQL(this.pkName, 
                                             columnListDialog.getTableName());
        } catch (UnsupportedOperationException e2) {
            //i18n[DropPrimaryKeyCommand.unsupportedOperationMsg=The {0} 
            //dialect doesn't support dropping primary keys]
            String msg = 
                s_stringMgr.getString("DropPrimaryKeyCommand.unsupportedOperationMsg", 
                                      dialect.getDisplayName());
                                      
            _session.getMessageHandler().showMessage(msg);
        } catch (UserCancelledOperationException e) {
            // user cancelled selecting a dialog. do nothing?
        }
        return result;
        
    }
    
    
    private class ShowSQLListener implements ActionListener {
        public void actionPerformed( ActionEvent e) {
            String sql = getSQLFromDialog();
            if (sql == null || sql.equals("")) {
//              TODO: tell the user no changes
                return;
            }
            StringBuffer script = new StringBuffer();
            script.append(sql);
            script.append(";\n\n");

            ErrorDialog sqldialog = 
                new ErrorDialog(columnListDialog, script.toString());
            //i18n[DropPrimaryKeyCommand.sqlDialogTitle=Drop Primary Key SQL]
            String title = 
                s_stringMgr.getString("DropPrimaryKeyCommand.sqlDialogTitle");
            sqldialog.setTitle(title);
            sqldialog.setVisible(true);                
        }
    }
    
    private class DropPrimaryKeyActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (columnListDialog == null) {
                System.err.println("dialog was null");
                return;
            }
            
            CommandExecHandler handler = new CommandExecHandler(_session);
            
            String sql = getSQLFromDialog();

            log.info("DropPrimaryKeyCommand: executing SQL - "+sql);
            SQLExecuterTask executer = 
                new SQLExecuterTask(_session, sql, handler);
            executer.run();            
            
            columnListDialog.setVisible(false);
        }
        
    }
        
}