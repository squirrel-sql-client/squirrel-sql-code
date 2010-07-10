package net.sourceforge.squirrel_sql.plugins.mysql;
/*
 * Copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class CreateMysqlTableScriptCommand implements ICommand
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
		final SQLConnection conn = _session.getSQLConnection();
		final StringBuffer buf = new StringBuffer(2048);
		final char sep = _session.getProperties().getSQLStatementSeparatorChar();

		try
		{
			final Statement stmt = conn.createStatement();
			try
			{
				IObjectTreeAPI api = _session.getObjectTreeAPI(_plugin);
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
	
			_session.getSQLPanelAPI(_plugin).appendSQLScript(buf.toString(), true);
			_session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
		}
		catch (SQLException ex)
		{
			throw new BaseException(ex);
		}
	}
}