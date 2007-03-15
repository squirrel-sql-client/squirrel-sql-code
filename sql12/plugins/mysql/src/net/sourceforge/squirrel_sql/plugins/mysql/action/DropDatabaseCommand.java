package net.sourceforge.squirrel_sql.plugins.mysql.action;
/*
 * Copyright (C) 2003 Arun Kapilan.P
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
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * DropDatabaseCommand.java
 *
 * Created on June 9, 2003, 2:59 PM
 *
 * @author Arun Kapilan.P
 */
public class DropDatabaseCommand implements ICommand
{
	/** Current session. */
	private ISession _session;

	/** Current plugin. */
	private final MysqlPlugin _plugin;

	/** Databases to be dropped. */
	private final IDatabaseObjectInfo[] _dbs;

	/**
	 * Ctor specifying the current session.
	 *
	 * @param	session	Current session.
	 * @param	dbs		Array of databases to be dropped.
	 */
	public DropDatabaseCommand(ISession session, MysqlPlugin plugin,
									IDatabaseObjectInfo[] dbs)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("IClientSession == null");
		}
		if (dbs == null)
		{
			throw new IllegalArgumentException("Databases array is null");
		}

		_session = session;
		_plugin = plugin;
		_dbs = dbs;
	}

	public void execute()
	{
		if (_dbs.length > 0)
		{
			final String sqlSep = 
                _session.getQueryTokenizer().getSQLStatementSeparator();
			final StringBuffer buf = new StringBuffer();
			for (int i = 0; i < _dbs.length; i++)
			{
				final IDatabaseObjectInfo ti = _dbs[i];
				buf.append("drop database ")
					.append(ti.getQualifiedName())
					.append(" ")
					.append(sqlSep)
					.append('\n');
			}
			_session.getSessionInternalFrame().getSQLPanelAPI().executeSQL(buf.toString());
		}
	}
}
