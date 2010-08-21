package net.sourceforge.squirrel_sql.plugins.mysql.action;
/*
 * Copyright (C) 2003 Colin Bell
 * colbell@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This abstract command is a MySQL command that takes a comma separated list
 * of tables as a parameter.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
abstract class AbstractTableListCommand implements ICommand
{
	/** Current session. */
	private ISession _session;

	/** Current plugin. */
	private final MysqlPlugin _plugin;

	/**
	 * Ctor specifying the current session.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a?<TT>null</TT> <TT>ISession</TT>,
	 * 			<TT>Resources</TT> or <TT>MysqlPlugin</TT> passed.
	 */
	public AbstractTableListCommand(ISession session, MysqlPlugin plugin)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("MysqlPlugin == null");
		}

		_session = session;
		_plugin = plugin;
	}

	/**
	 * Execute this command.
	 */
	public void execute()
	{
		final IObjectTreeAPI api = _session.getSessionInternalFrame().getObjectTreeAPI();
		final IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();

		// Get the names of all the selected tables in a comma separated list,
		StringBuffer tableList = new StringBuffer(512);
		for (int i = 0; i < dbObjs.length; ++i)
		{
			tableList.append(dbObjs[i].getQualifiedName()).append(",");
		}
		if (tableList.length() > 0)
		{
			tableList.setLength(tableList.length() - 1); // Remove trailing ","
		}

		// Execute the SQL command in the SQL tab and then display the SQL tab.
		final StringBuffer cmd = new StringBuffer(512);
		cmd.append(getMySQLCommand())
			.append(' ')
			.append(tableList);
		final String cmdStr = checkSQL(cmd.toString());
		if (cmdStr != null && cmdStr.length() > 0)
		{
			_session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript(cmdStr, true);
			_session.getSessionInternalFrame().getSQLPanelAPI().executeCurrentSQL();
			_session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
		}
	}

	/**
	 * Last chance for subclass to modify the SQL command prior to executing
	 * it. The default behaviour is just to return the SQL string passed in.
	 *
	 * @return	The SQL command to execute.
	 */
	protected String checkSQL(String sql)
	{
		return sql;
	}

	/**
	 * Retrieve the MySQL command to run.
	 *
	 * @return	the MySQL command to run.
	 */
	protected abstract String getMySQLCommand();
}
