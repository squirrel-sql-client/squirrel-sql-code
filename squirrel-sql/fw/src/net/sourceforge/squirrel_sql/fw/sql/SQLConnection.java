package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SQLConnection
{
	private final static ILogger s_log =
		LoggerController.createLogger(SQLConnection.class);

	private Connection _conn;
	private DatabaseMetaData _md;

	/** MetaData for this connection. */
	private SQLDatabaseMetaData _metaData;

	private boolean _autoCommitOnClose = false;

	private Date _timeOpened = Calendar.getInstance().getTime();
	private Date _timeClosed;

	public SQLConnection(Connection conn)
	{
		super();
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}
		_conn = conn;
	}

	public void close() throws SQLException
	{
		SQLException savedEx = null;
		if (_conn != null)
		{
			s_log.debug("Closing connection");
			try
			{
				if (!_conn.getAutoCommit())
				{
					if (_autoCommitOnClose)
					{
						_conn.commit();
					}
					else
					{
						_conn.rollback();
					}
				}
			}
			catch (SQLException ex)
			{
				savedEx = ex;
			}
			_conn.close();
			_conn = null;
			_md = null;
			_timeClosed = Calendar.getInstance().getTime();
			if (savedEx != null)
			{
				s_log.debug("Connection close failed", savedEx);
				throw savedEx;
			}
			s_log.debug("Connection closed successfully");
		}
	}

	public void commit() throws SQLException
	{
		validateConnection();
		_conn.commit();
	}

	public void rollback() throws SQLException
	{
		validateConnection();
		_conn.rollback();
	}

	public boolean getAutoCommit() throws SQLException
	{
		validateConnection();
		return _conn.getAutoCommit();
	}

	public void setAutoCommit(boolean value) throws SQLException
	{
		validateConnection();
		_conn.setAutoCommit(value);
	}

	public boolean getCommitOnClose()
	{
		return _autoCommitOnClose;
	}

	public void setCommitOnClose(boolean value)
	{
		_autoCommitOnClose = value;
	}

	public Statement createStatement() throws SQLException
	{
		validateConnection();
		return _conn.createStatement();
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException
	{
		validateConnection();
		return _conn.prepareStatement(sql);
	}

	/**
	 * Retrieve the time that this connection was opened. Note that this time
	 * is the time that this <TT>SQLConnection</TT> was created, not the time
	 * that the <TT>java.sql.Connection</TT> object that it is wrapped around
	 * was opened.
	 * 
	 * @return	Time connection opened.
	 */
	public Date getTimeOpened()
	{
		return _timeOpened;
	}

	/**
	 * Retrieve the time that this connection was closed. If this connection
	 * is still opened then <TT>null</TT> will be returned..
	 * 
	 * @return	Time connection closed.
	 */
	public Date getTimeClosed()
	{
		return _timeClosed;
	}
	
	/**
	 * Retrieve the metadata for this connection.
	 * 
	 * @return	The <TT>SQLMetaData</TT> object.
	 */
	public synchronized SQLDatabaseMetaData getSQLMetaData()
	{
		if (_metaData == null)
		{
			_metaData = new SQLDatabaseMetaData(this);
		}
		return _metaData;
	}

//todo: get rid of this method.
//	public synchronized DatabaseMetaData getMetaData() throws SQLException
//	{
//		validateConnection();
//		if (_md == null)
//		{
//			_md = getConnection().getMetaData();
//		}
//		return _md;
//	}

	public Connection getConnection()
	{
		return _conn;
	}

	public String getCatalog() throws SQLException
	{
		validateConnection();
		return getConnection().getCatalog();
	}

	public void setCatalog(String catalogName)
		throws SQLException
	{
		validateConnection();
		getConnection().setCatalog(catalogName);
	}

	public SQLWarning getWarnings() throws SQLException
	{
		validateConnection();
		return _conn.getWarnings();
	}

	protected void validateConnection() throws SQLException
	{
		if (_conn == null)
		{
			throw new SQLException("No connection");
		}
	}
}