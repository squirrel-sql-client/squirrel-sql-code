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

import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
/**
 * @version 	$Id: DropTablesCommand.java,v 1.7 2005-11-14 02:19:43 manningr Exp $
 * @author		Johan Compagner
 */
public class DropTablesCommand implements ICommand
{
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
	public DropTablesCommand(ISession session, IDatabaseObjectInfo[] tables)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (tables == null)
		{
			throw new IllegalArgumentException("Tables array is null");
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
			final ITableInfo ti = (ITableInfo)_tables[i];
		    if (isFrontBase && "BASE TABLE".equals(ti.getType())) {
		        buf.append("drop table ");
		    } else {
		        buf.append("drop ").append(ti.getType());
		    }
			buf.append(" ")
			.append(ti.getQualifiedName())
			.append(" ");
            if (isFrontBase) {
                buf.append(" CASCADE ");
            }
			buf.append(sqlSep)
			.append(" ")
			.append('\n');
		}

		// Execute the sql synchronously
		SQLExecuterTask executer = new SQLExecuterTask(_session, buf.toString(),
									new DefaultSQLExecuterHandler(_session));
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
}
