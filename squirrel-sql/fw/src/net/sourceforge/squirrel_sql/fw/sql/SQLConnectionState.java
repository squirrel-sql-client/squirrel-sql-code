package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2002 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This class will save the state of an <TT>SQLConnection</TT> and
 * can apply the saved state to another <TT>SQLConnection</TT>. 
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLConnectionState
{
	private final static ILogger s_log =
		LoggerController.createLogger(SQLConnectionState.class);

	private Integer _transIsolation;
	private String _catalog;
	private boolean _autoCommit;

	public SQLConnectionState()
	{
		super();
	}

	public void saveState(SQLConnection conn) throws SQLException
	{
		saveState(conn, null);
	}

	public void saveState(SQLConnection conn, IMessageHandler msgHandler)
		throws SQLException
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}

		try
		{
			_transIsolation = new Integer(conn.getTransactionIsolation());
		}
		catch (SQLException ex)
		{
			s_log.error("Error saving transaction isolation", ex);
			if (msgHandler == null)
			{
				throw ex;
			}
			msgHandler.showErrorMessage(ex);
		}

		try
		{
			_catalog = conn.getCatalog();
		}
		catch (SQLException ex)
		{
			s_log.error("Error saving current catalog", ex);
			if (msgHandler == null)
			{
				throw ex;
			}
			msgHandler.showErrorMessage(ex);
		}

		try
		{
			_autoCommit = conn.getAutoCommit();
		}
		catch (SQLException ex)
		{
			s_log.error("Error saving autocommit state", ex);
			if (msgHandler == null)
			{
				throw ex;
			}
			msgHandler.showErrorMessage(ex);
		}
	}

	public void restoreState(SQLConnection conn)
		throws SQLException
	{
		restoreState(conn, null);
	}

	public void restoreState(SQLConnection conn, IMessageHandler msgHandler)
		throws SQLException
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}

		if (_transIsolation != null)
		{
			try
			{
				conn.setTransactionIsolation(_transIsolation.intValue());
			}
			catch (SQLException ex)
			{
				s_log.error("Error restoring transaction isolation", ex);
				if (msgHandler == null)
				{
					throw ex;
				}
				msgHandler.showErrorMessage(ex);
			}
		}

		if (_catalog != null)
		{
			try
			{
				conn.setCatalog(_catalog);
			}
			catch (SQLException ex)
			{
				s_log.error("Error restoring current catalog", ex);
				if (msgHandler == null)
				{
					throw ex;
				}
				msgHandler.showErrorMessage(ex);
			}
		}

		try
		{
			conn.setAutoCommit(_autoCommit);
		}
		catch (SQLException ex)
		{
			s_log.error("Error restoring autocommit", ex);
			if (msgHandler == null)
			{
				throw ex;
			}
			msgHandler.showErrorMessage(ex);
		}
	}
}

