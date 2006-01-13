package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2002-2004 Johan Compagner
 * jcompagner@j-com.nl
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * @version 	$Id: DropTablesCommand.java,v 1.8 2006-01-13 16:21:53 manningr Exp $
 * @author		Johan Compagner
 */
public class DropTablesCommand implements ICommand
{
    /** Logger for this class. */
    private final ILogger s_log =
        LoggerController.createLogger(DropTablesCommand.class);
    
    /** Internationalized strings for this class */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DropTablesCommand.class);
    
    /** Current session. */
	private final ISession _session;

	/** Tables to be deleted. */
	private final IDatabaseObjectInfo[] _tables;

    /** whether or not one of the drop statements failed */
    private boolean exceptionOccurred = false;
    
    /** stores the result of the drop operation. Key is SQL statement */
    private HashMap resultMap = new HashMap();
    
    /** pattern to match the eol char */
    private Pattern pattern = Pattern.compile("\\n");
    
	/**
	 * Ctor.
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
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (tables == null)
		{
			throw new IllegalArgumentException("IDatabaseObjectInfo[] == null");
		}

		_session = session;
		_tables = tables;
	}

	/**
	 * Drop selected tables in the object tree.
	 */
	public void execute()
	{
		final String sqlSep = _session.getProperties().getSQLStatementSeparator();
		final StringBuffer buf = new StringBuffer();
        boolean isFrontBase = isFrontBaseSession();
        for (int i = 0; i < _tables.length; i++)
		{
            StringBuffer dropSql = new StringBuffer();
			final ITableInfo ti = (ITableInfo)_tables[i];
		    if (isFrontBase && "BASE TABLE".equals(ti.getType())) {
                dropSql.append("drop table ");
		    } else {
                dropSql.append("drop ").append(ti.getType());
		    }
            dropSql.append(" ").append(ti.getQualifiedName()).append(" ");
            if (isFrontBase) {
                dropSql.append(" CASCADE ");
            }
            DropTableResult dtr = new DropTableResult(ti, dropSql.toString());
            resultMap.put(dropSql.toString(), dtr);
            buf.append(dropSql).append(sqlSep).append(" ").append('\n');
		}

        // a callback handler that will allow us to record the result of each
        // drop sql statement
        DropTablesExecutionHandler handler = 
            new DropTablesExecutionHandler(_session);
        
        SQLExecuterTask executer = 
                        new SQLExecuterTask(_session, buf.toString(), handler);
        
        // Execute the sql synchronously
		executer.run();
        
		// Use this to run asynch
		// _session.getApplication().getThreadPool().addTask(executer);
	}
    
    /**
     * Helper method to determine whether we are talking to FrontBase or not.
     * 
     * @return true if FrontBase; false otherwise.
     */
    private boolean isFrontBaseSession() {
        boolean result = false;
        SQLConnection con = _session.getSQLConnection();
        String productName = null;
        if (con != null) {
            SQLDatabaseMetaData md = con.getSQLMetaData();
            if (md != null) {
                try {
                    productName = md.getDatabaseProductName();
                } catch (SQLException e) { /* Do Nothing */ }
            }
        }
        if ("frontbase".equalsIgnoreCase(productName)) {
            result = true;
        }
        return result;
    }
 
    /**
     * Returns the result of the drop operation.  Key is the ITableInfo.
     * 
     * @return true if all objects were dropped successfully; false otherwise. 
     */
    public HashMap getResult() {
        HashMap result = new HashMap();
        Iterator i = resultMap.values().iterator();
        while (i.hasNext()) {
            DropTableResult dtr = (DropTableResult)i.next();
            result.put(dtr.getTableInfo(), dtr);
        }
        return result;
    }
    
    /**
     * Returns a boolean value indicating whether or not an exception occurred
     * while executing the drop table statement(s).
     * 
     * @return Returns the exceptionOccurred.
     */
    public boolean getExceptionOccurred() {
        return exceptionOccurred;
    }

    /**
     * A handler that will notify us when the drop operation failed.
     */
    private class DropTablesExecutionHandler extends DefaultSQLExecuterHandler {
        
        public DropTablesExecutionHandler(ISession session) {
            super(session);
        }
        
        /** 
         * Called when the SQLExecutor terminates due to an exception
         */
        public void sqlExecutionException(Throwable ex, String postErrorString) {
            // show the error message in the status area
            super.sqlExecutionException(ex, postErrorString);
            exceptionOccurred = true;
        }

        /** 
         * Called when the SQLExecutor succesfully completes execution of a sql
         * statement.
         */
        public void sqlExecutionComplete(SQLExecutionInfo info, 
                                         int processedStatementCount, 
                                         int statementCount) 
        {
            if (info == null) {
                throw new IllegalArgumentException("info == null");
            }
            if (info.getSQL() == null) {
                throw new IllegalArgumentException("info.getSQL() == null");
            }
            String sql = info.getSQL();
            
            // strip the eol char from the statement if there is one
            int eolIdx = sql.indexOf("\n");
            if (eolIdx != -1) {
                Matcher m = pattern.matcher(sql);
                sql = m.replaceAll("");
            }
            
            // record the result of the drop sql statement in resultMap
            DropTableResult dtr = (DropTableResult)resultMap.get(sql);
            if (dtr != null) {
                dtr.setResult(true);
            } else {
                String msg = 
                    s_stringMgr.getString("DropTablesCommand.error.sqlnotfound",
                                          sql);
                s_log.error(msg);
            }
        }
    }
}
