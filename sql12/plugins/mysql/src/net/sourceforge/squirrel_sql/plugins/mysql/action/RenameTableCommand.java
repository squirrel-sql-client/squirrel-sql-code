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
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * This command will rename a database table.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class RenameTableCommand
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(RenameTableCommand.class);

	/** Current session. */
	private final ISession _session;

	/** Current plugin. */
	private final IPlugin _plugin;

	/** Table to be renamed. */
	private final ITableInfo _ti;

	/** New name for table. */
	private final String _newTableName;

	/**
	 * Ctor.
	 *
	 * @param	session			Session to rename table within.
	 * @param	plugin			Current plugin.
	 * @param	ti				Points to table to be renamed.
	 * @param	newTableName	New name for table.
	 */
	RenameTableCommand(ISession session, IPlugin plugin, ITableInfo ti,
						String newTableName)
	{
		super();

		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("IPlugin == null");
		}
		if (ti == null)
		{
			throw new IllegalArgumentException("ITableInfo == null");
		}
		if (newTableName == null || newTableName.length() == 0)
		{
			throw new IllegalArgumentException("New table name empty");
		}

		_session = session;
		_plugin = plugin;
		_ti = ti;
		_newTableName = newTableName;
	}

	public void execute()
	{
		String cmd = "rename table " + _ti.getQualifiedName() + " to " + _newTableName;
      SQLExecuterTask executer = new SQLExecuterTask(_session, cmd, new DefaultSQLExecuterHandler(_session));
      executer.run();
      _session.getSchemaInfo().reloadAllTables();
	}
}
