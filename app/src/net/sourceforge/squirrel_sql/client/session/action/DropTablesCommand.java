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
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
/**
 * @version 	$Id: DropTablesCommand.java,v 1.6 2004-08-18 12:10:43 colbell Exp $
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
		for (int i = 0; i < _tables.length; i++)
		{
			final ITableInfo ti = (ITableInfo)_tables[i];
			//buf.append("drop table ")
			buf.append("drop ")
				.append(ti.getType())
				.append(" ")
				.append(ti.getQualifiedName())
				.append(" ")
				.append(sqlSep)
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
}
