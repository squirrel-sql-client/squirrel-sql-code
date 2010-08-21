package net.sourceforge.squirrel_sql.plugins.mysql.action;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;
/**
 * Generate a &quot;CREATE TABLE&quot; script for the currently
 * selected tables.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class CreateMysqlTableScriptCommand implements ICommand
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(CreateMysqlTableScriptCommand.class);

	/** Current session. */
	private ISession _session;

	/** Current plugin. */
	private final MysqlPlugin _plugin;

	/**
	 * Ctor specifying the current session.
	 */
	public CreateMysqlTableScriptCommand(ISession session, MysqlPlugin plugin)
	{
		super();
		_session = session;
		_plugin = plugin;
	}

	/**
	 * Execute this command. Place the "create table" script in the SQL entry panel.
	 */
	public void execute() throws BaseException
	{
		final ISQLConnection conn = _session.getSQLConnection();
		final StringBuffer buf = new StringBuffer(2048);
		final String sep = " " + _session.getQueryTokenizer().getSQLStatementSeparator();

		try
		{
			final Statement stmt = conn.createStatement();
			try
			{
				IObjectTreeAPI api = _session.getSessionInternalFrame().getObjectTreeAPI();
				IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();
				for (int i = 0; i < dbObjs.length; ++i)
				{
					final ResultSet rs = stmt.executeQuery("show create table "
												+ dbObjs[i].getQualifiedName());
					try
					{
						if (rs.next())
						{
							buf.append(rs.getString(2)).append(sep).append('\n');
						}
					}
					finally
					{
						rs.close();
					}
				}
			}
			finally
			{
				try
				{
					stmt.close();
				}
				catch (Exception ex)
				{
					s_log.error("Error occured closing PreparedStatement", ex);
				}
			}

			_session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript(buf.toString(), true);
			_session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
		}
		catch (SQLException ex)
		{
			throw new BaseException(ex);
		}
	}
}
