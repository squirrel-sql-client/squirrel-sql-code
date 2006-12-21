package net.sourceforge.squirrel_sql.fw.sql;
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
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

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
	private SQLDriverPropertyCollection _connProps;

	public SQLConnectionState()
	{
		super();
	}

   public void saveState(SQLConnection conn, SessionProperties sessionProperties, IMessageHandler msgHandler)
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
			String msg =
				"Error saving transaction isolation.\n" +
				"This might happen when reconnecting a Session to restore a broken connection.\n" +
				"The new connection will use the default transaction isolation.";

			s_log.error(msg, ex);
			if (msgHandler == null)
			{
				throw ex;
			}
			msgHandler.showErrorMessage(msg);
		}

		try
		{
			_catalog = conn.getCatalog();
		}
		catch (SQLException ex)
		{
			String msg =
				"Error saving current catalog.\n" +
				"This might happen when reconnecting a Session to restore a broken connection.\n" +
				"The new connection will use the default catalog.";

			s_log.error(msg, ex);
			if (msgHandler == null)
			{
				throw ex;
			}
			msgHandler.showErrorMessage(msg);
		}

		try
		{
         // In case the connection won't be able to tell its Auto Commit state,
         // this is the best default we have.
         _autoCommit = sessionProperties.getAutoCommit();

         _autoCommit = conn.getAutoCommit();
		}
		catch (SQLException ex)
		{
			String msg =
				"Error saving autocommit state.\n" +
				"This might happen when reconnecting a Session to restore a broken connection.\n" +
				"The new connection will use the autocommit state.";


			s_log.error(msg, ex);
			if (msgHandler == null)
			{
				throw ex;
			}
			msgHandler.showErrorMessage(msg);
		}

		_connProps = conn.getConnectionProperties();
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

	public SQLDriverPropertyCollection getConnectionProperties()
	{
		return _connProps;
	}

	public boolean getAutoCommit()
	{
		return _autoCommit;
	}
}

