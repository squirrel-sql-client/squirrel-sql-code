package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
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
 * @version 	$Id: DeleteTablesCommand.java,v 1.1 2006-02-04 16:05:55 manningr Exp $
 * @author		Rob Manning
 */
public class DeleteTablesCommand implements ICommand
{
    /** Logger for this class. */
    private final ILogger s_log =
        LoggerController.createLogger(DeleteTablesCommand.class);
    
    /** Internationalized strings for this class */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DeleteTablesCommand.class);
    
    /** Current session. */
	private final ISession _session;

	/** Tables to be deleted. */
	private final IDatabaseObjectInfo[] _tables;
    
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
	public DeleteTablesCommand(ISession session, IDatabaseObjectInfo[] tables)
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
        final SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();
		final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < _tables.length; i++)
		{
			final ITableInfo ti = (ITableInfo)_tables[i];
            buf.append("DELETE FROM ").append(ti.getQualifiedName());
            try {
                buf.append(" ").append(md.getCascadeClause());
            } catch (SQLException e) {
                s_log.error(
                    "DeleteTablesCommand.execute: Enountered SQLException " +
                    "while attempting to find to the cascade clause for " +
                    "delete statements - "+e.getMessage());
                return;
            }
            buf.append(sqlSep).append(" ").append('\n');
		}
        SQLExecuterTask executer = 
                        new SQLExecuterTask(_session, buf.toString(), null);
        
        // Execute the sql synchronously
		executer.run();
        
		// Use this to run asynch
		// _session.getApplication().getThreadPool().addTask(executer);
	}    
}
