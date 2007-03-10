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
import java.util.HashSet;
import java.util.List;

import net.sourceforge.squirrel_sql.client.db.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.client.gui.ProgessCallBackDialog;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
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
    
    private static interface i18n {
                
        //i18n[DropTablesCommand.progressDialogTitle=Analyzing tables to drop]
        String PROGRESS_DIALOG_TITLE = 
            s_stringMgr.getString("DropTablesCommand.progressDialogTitle");
        
        //i18n[DropTablesCommand.loadingPrefix=Analyzing table:]
        String LOADING_PREFIX = 
            s_stringMgr.getString("DropTablesCommand.loadingPrefix");        
    }
    
    /** 
     * A set of materialized view names in the same schema as the table(s) 
     * being dropped
     */
    private HashSet<String> matViewLookup = null;
    
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
        List<ITableInfo> tables = dropTableDialog.getTableInfoList();
        boolean cascadeConstraints = dropTableDialog.getCascadeConstraints();
        
        ArrayList<String> result = new ArrayList<String>();
        try {
            List<ITableInfo> orderedTables = getOrderedTables(tables);
            
            dialect = DialectFactory.getDialect(_session, DialectFactory.DEST_TYPE); 
            String sep = _session.getQueryTokenizer().getSQLStatementSeparator();
            
            for (ITableInfo info : orderedTables) {
                boolean isMaterializedView = isMaterializedView(info, _session);
                String sql = dialect.getTableDropSQL(info, 
                                                     cascadeConstraints,                         
                                                     isMaterializedView);
                StringBuilder dropSQL = new StringBuilder(); 
                dropSQL.append(sql);
                dropSQL.append(sep);
                result.add(dropSQL.toString());                
            }            
        } catch (UnsupportedOperationException e2) {
            //i18n[DropTablesCommand.unsupportedOperationMsg=The {0} 
            //dialect doesn't support dropping tables]
            String msg = 
                s_stringMgr.getString("DropTablesCommand.unsupportedOperationMsg",
                                      dialect.getDisplayName());
            _session.getMessageHandler().showMessage(msg);
        } catch (UserCancelledOperationException e) {
            // user cancelled selecting a dialect. do nothing?
        }
        return result.toArray(new String[result.size()]);
    }
    
    private List<ITableInfo> getOrderedTables(List<ITableInfo> tables) {
        List<ITableInfo> result = tables;
        SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();
        
        try {
            ProgessCallBackDialog cb = 
                new ProgessCallBackDialog(dropTableDialog,
                                          i18n.PROGRESS_DIALOG_TITLE,
                                          tables.size());
            
            cb.setLoadingPrefix(i18n.LOADING_PREFIX);
            
            result = SQLUtilities.getDeletionOrder(tables, md, cb);
        } catch (SQLException e) {
            s_log.error(
                "Encountered exception while attempting to order tables " +
                "according to constraints: "+e.getMessage(), e);
        }
        return result;
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
        if (!DialectFactory.isOracleSession(session)) {
            // Only Oracle supports materialized views directly.
            return false;
        }
        if (matViewLookup == null) {
            initMatViewLookup(session, ti.getSchemaName());
        }
        return matViewLookup.contains(ti.getSimpleName());
    }

    private void initMatViewLookup(ISession session, String schema) {
        matViewLookup = new HashSet<String>();
        // There is no good way using JDBC metadata to tell if the table is a 
        // materialized view.  So, we need to query the data dictionary to find
        // that out.  Get all table names whose comment indicates that they are
        // a materialized view.
        String sql = 
            "SELECT TABLE_NAME FROM ALL_TAB_COMMENTS " +
            "where COMMENTS like 'snapshot%' " +
            "and OWNER = ? ";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = session.getSQLConnection().prepareStatement(sql);
            stmt.setString(1, schema);
            rs = stmt.executeQuery();
            if (rs.next()) {
                String tableName = rs.getString(1);
                matViewLookup.add(tableName);
            }
        } catch (SQLException e) {
            s_log.error(
                "Unexpected exception while attempting to find mat. views " +
                "in schema: "+schema, e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ex) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException ex) {}            
        }
        
    }
    
    private class ShowSQLListener implements ActionListener, SQLResultListener {
        
        
        /* (non-Javadoc)
         * @see net.sourceforge.squirrel_sql.plugins.refactoring.commands.DropTablesCommand.SQLResultListener#finished(java.lang.String[])
         */
        public void finished(String[] sql) {
            if (sql.length == 0) {
//              TODO: tell the user no changes
                return;
            }
            StringBuffer script = new StringBuffer();
            for (int i = 0; i < sql.length; i++) {
                script.append(sql[i]);
                script.append("\n\n");                
            }

            ErrorDialog sqldialog = 
                new ErrorDialog(dropTableDialog, script.toString());
            //i18n[DropTablesCommand.sqlDialogTitle=Drop Table SQL]
            String title = 
                s_stringMgr.getString("DropTablesCommand.sqlDialogTitle");
            sqldialog.setTitle(title);
            sqldialog.setVisible(true);                
        }

        public void actionPerformed( ActionEvent e) {
            _session.getApplication().getThreadPool().addTask(new GetSQLTask(this));
        }
    }
    
    private class DropTablesActionListener implements ActionListener, SQLResultListener {

        /* (non-Javadoc)
         * @see net.sourceforge.squirrel_sql.plugins.refactoring.commands.DropTablesCommand.SQLResultListener#finished(java.lang.String[])
         */
        public void finished(String[] sqls) {
            for (int i = 0; i < sqls.length; i++) {
                String sql = sqls[i];
                if (s_log.isInfoEnabled()) {
                    s_log.info("DropTablesCommand: executing SQL - "+sql);
                }
                CommandExecHandler handler = new CommandExecHandler(_session);
                SQLExecuterTask executer = 
                    new SQLExecuterTask(_session, sql, handler);
                executer.run();                            
            }
            GUIUtils.processOnSwingEventThread(new Runnable() {
                public void run() {
                    dropTableDialog.setVisible(false);
                }
            });
        }

        public void actionPerformed(ActionEvent e) {
            if (dropTableDialog == null) {
                return;
            }
            _session.getApplication().getThreadPool().addTask(new GetSQLTask(this));
        }
        
    }
    
    public interface SQLResultListener {
        public void finished(String[] sql);
    }
    
    public class GetSQLTask implements Runnable {
        
        private SQLResultListener _listener;
        
        public GetSQLTask(SQLResultListener listener) {
            _listener = listener;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            String[] sql = getSQLFromDialog();
            _listener.finished(sql);
        }
    }

}
