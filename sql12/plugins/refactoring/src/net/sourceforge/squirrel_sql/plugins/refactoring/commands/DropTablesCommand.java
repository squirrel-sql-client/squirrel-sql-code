package net.sourceforge.squirrel_sql.plugins.refactoring.commands;
/*
 * Copyright (C) 2006 Rob Manning
 * manningr@user.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.client.db.dialects.DialectFactory;
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


public class DropTablesCommand extends AbstractRefactoringCommand
{
    /** Logger for this class. */
    private final ILogger s_log =
        LoggerController.createLogger(DropTablesCommand.class);
    
    /** Internationalized strings for this class */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DropTablesCommand.class);
    
	/**
	 *
	 * @param	session		Current session..
	 * @param	tables		Array of <TT>IDatabaseObjectInfo</TT> objects
	 * 						representing the tables to be deleted.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public DropTablesCommand(ISession session, IDatabaseObjectInfo[] tables)
	{
		super(session, tables);
	}

	/**
	 * Drop selected tables in the object tree.
	 */
	public void execute()
	{
        try {
            super.showDropTableDialog(new DropTablesActionListener(), 
                                      new ShowSQLListener());
        } catch (Exception e) {
            s_log.error("Unexpected exception "+e.getMessage(), e);
        }
	}

    @Override
    protected String[] getSQLFromDialog() {
        HibernateDialect dialect = null; 
        ITableInfo[] infos = dropTableDialog.getTableInfos();
        boolean cascadeConstraints = dropTableDialog.getCascadeConstraints();
        
        ArrayList result = new ArrayList();
        try {
            dialect = DialectFactory.getDialect(_session, DialectFactory.DEST_TYPE); 
            
            for (int i = 0; i < infos.length; i++) {
                ITableInfo info = infos[i];
                boolean isMaterializedView = isMaterializedView(info, _session);
                String dropSQL = 
                    dialect.getTableDropSQL(info, 
                                            cascadeConstraints, 
                                            isMaterializedView);
                result.add(dropSQL);
            }
        } catch (UnsupportedOperationException e2) {
            //i18n[DropPrimaryKeyCommand.unsupportedOperationMsg=The {0} 
            //dialect doesn't support dropping primary keys]
            String msg = 
                s_stringMgr.getString("DropPrimaryKeyCommand.unsupportedOperationMsg", 
                                      dialect.getDisplayName());
                                      
            _session.getMessageHandler().showMessage(msg);
        } catch (UserCancelledOperationException e) {
            // user cancelled selecting a dialect. do nothing?
        }
        return (String[])result.toArray(new String[result.size()]);
    }
    
    /**
     * Returns a boolean value indicating whether or not the specified table 
     * info is not only a table, but also a materialized view.
     * 
     * @param ti
     * @param session
     * @return
     */
    private boolean isMaterializedView(ITableInfo ti,
                                      ISession session)
    {
        boolean result = false;
        if (!DialectFactory.isOracleSession(session)) {
            // Only Oracle supports materialized views directly.
            return false;
        }
        // There is no good way using JDBC metadata to tell if the table is a 
        // materialized view.  So, we need to query the data dictionary to find
        // that out.
        String sql = 
            "SELECT COMMENTS FROM ALL_TAB_COMMENTS " +
            "where TABLE_NAME  = ? " +
            "and OWNER = ? ";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = session.getSQLConnection().prepareStatement(sql);
            // Oracle always stores object names in uppercase
            stmt.setString(1, ti.getSimpleName().toUpperCase());
            stmt.setString(2, ti.getSchemaName().toUpperCase());
            rs = stmt.executeQuery();
            if (rs.next()) {
                String comments = rs.getString(1);
                if (!rs.wasNull() && comments.startsWith("snapshot")) {
                    result = true;
                }
            }
        } catch (SQLException e) {
            if (rs != null) try { rs.close(); } catch (SQLException ex) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException ex) {}
        }
        return result;
    }

    
    private class ShowSQLListener implements ActionListener {
        public void actionPerformed( ActionEvent e) {
            String[] sql = getSQLFromDialog();
            if (sql.length == 0) {
//              TODO: tell the user no changes
                return;
            }
            StringBuffer script = new StringBuffer();
            for (int i = 0; i < sql.length; i++) {
                script.append(sql[i]);
                script.append(";\n\n");                
            }

            ErrorDialog sqldialog = 
                new ErrorDialog(dropTableDialog, script.toString());
            //i18n[DropTablesCommand.sqlDialogTitle=Drop Table SQL]
            String title = 
                s_stringMgr.getString("DropTablesCommand.sqlDialogTitle");
            sqldialog.setTitle(title);
            sqldialog.setVisible(true);                
        }
    }
    
    private class DropTablesActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (dropTableDialog == null) {
                System.err.println("dialog was null");
                return;
            }
            
            CommandExecHandler handler = new CommandExecHandler(_session);
            
            String[] sqls = getSQLFromDialog();
            for (int i = 0; i < sqls.length; i++) {
                String sql = sqls[i];
                s_log.info("DropTablesCommand: executing SQL - "+sql);
                SQLExecuterTask executer = 
                    new SQLExecuterTask(_session, sql, handler);
                executer.run();                            
            }
            dropTableDialog.setVisible(false);
        }
        
    }
    

}
