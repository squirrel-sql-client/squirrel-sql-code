package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.sql.Connection;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * This command will convert the current SQL into native
 * format and append it to the SQL entry area.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ShowNativeSQLCommand implements ICommand
{
	/** Current session. */
	private final ISession _session;

	/**
	 * Ctor.
	 *
	 * @param	session		Current session.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public ShowNativeSQLCommand(ISession session)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

		_session = session;
	}

	public void execute()
	{
		Connection conn = _session.getSQLConnection().getConnection();
		try
		{
			IPlugin plugin = _session.getApplication().getDummyAppPlugin();
			ISQLPanelAPI api = _session.getSQLPanelAPI(plugin);
			String sql = conn.nativeSQL(api.getSQLScriptToBeExecuted());
			if (sql.length() > 0)
			{
				api.appendSQLScript("\n" + sql, true);
			}
		}
		catch (SQLException ex)
		{
			_session.getMessageHandler().showErrorMessage(ex);
		}
	}
}