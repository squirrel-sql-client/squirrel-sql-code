package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2003-2004 Jason Height
 * jmheight@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terdims of the GNU Lesser General Public
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
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
//JASON: Check the SQLExecutertask class against this one.
//JASON: Check usage of this class
/**
 * This class can be used to execute SQL.
 * <p/>It implements Runnable so it can be executed as a thread (asynchronus
 * execution) or standalone in the main Swing thread (synchronus execution).
 */
public class SQLExecuter implements Runnable
{
	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(SQLExecuter.class);

	/** The call back object*/
	private ISQLExecuterHandler _handler;

	/** Current session. */
	private ISession _session;

	/** SQL passed in to be executed. */
	private String _sql;
	private Statement _stmt;
	private boolean _stopExecution = false;

	private int _currentQueryIndex = 0;

	public SQLExecuter(ISession session, String sql, ISQLExecuterHandler handler)
	{
		super();
		_session = session;
		_sql = sql;
		_handler = handler;
	}

	public void run()
	{
		try
		{
			final SQLConnection conn = _session.getSQLConnection();
			final SessionProperties props = _session.getProperties();
			_stmt = conn.createStatement();
			try
			{
				final boolean correctlySupportsMaxRows = conn.getSQLMetaData()
						.correctlySupportsSetMaxRows();
				if (correctlySupportsMaxRows && props.getSQLLimitRows())
				{
					try
					{
						_stmt.setMaxRows(props.getSQLNbrRowsToShow());
					}
					catch (Exception e)
					{
						s_log.error("Can't Set MaxRows", e);
					}
				}

				// Retrieve all the statements to execute.
				final QueryTokenizer qt = new QueryTokenizer(_sql, props
						.getSQLStatementSeparator(), props
						.getStartOfLineComment());
				List queryStrings = new ArrayList();
				while (qt.hasQuery())
				{
					queryStrings.add(qt.nextQuery());
				}

				_currentQueryIndex = 0;

				// Process each individual query.
				boolean maxRowsHasBeenSet = correctlySupportsMaxRows;
				while (!queryStrings.isEmpty())
				{
					String querySql = (String)queryStrings.remove(0);
					if (querySql != null)
					{
						if (_handler != null)
							_handler.sqlToBeExecuted(querySql);
						if (querySql != null)
						{
							// Some driver don't correctly support setMaxRows. In
							// these cases use setMaxRows only if this is a
							// SELECT.
							if (!correctlySupportsMaxRows
									&& props.getSQLLimitRows())
							{
								if ("SELECT".length() < querySql.trim()
										.length()
										&& "SELECT".equalsIgnoreCase(querySql
												.trim().substring(0,
														"SELECT".length())))
								{
									if (!maxRowsHasBeenSet)
									{
										try
										{
											_stmt.setMaxRows(props
													.getSQLNbrRowsToShow());
										}
										catch (Exception e)
										{
											s_log.error("Can't Set MaxRows", e);
										}
										maxRowsHasBeenSet = true;
									}
								}
								else if (maxRowsHasBeenSet)
								{
									_stmt.close();
									_stmt = conn.createStatement();
									maxRowsHasBeenSet = false;
								}
							}
							try
							{
								if (!processQuery(querySql))
								{
									break;
								}
							}
							catch (SQLException ex)
							{
								if (props.getAbortOnError())
								{
									throw ex;
								}
								handleError(ex);
							}
							catch (DataSetException ex)
							{
								if (props.getAbortOnError())
								{
									throw ex;
								}
								handleError(ex);
							}
						}
					}
				}

			}
			finally
			{
				try
				{
					_stmt.close();
				}
				finally
				{
					_stmt = null;
				}
			}
		}
		catch (Throwable ex)
		{
			handleError(ex);
		}
		finally
		{
			if (_stopExecution)
			{
				if (_handler != null)
					_handler.sqlExecutionCancelled();
				try
				{
					if (_stmt != null)
					{
						_stmt.cancel();
					}
				}
				catch (Throwable th)
				{
					s_log.error("Error occured cancelling SQL", th);
				}
			}
		}
	}

	public void cancel()
	{
		_stopExecution = true;
	}

	private boolean processQuery(String querySql) throws SQLException,
			DataSetException
	{
		++_currentQueryIndex;

		final SQLExecutionInfo exInfo = new SQLExecutionInfo(
				_currentQueryIndex, querySql, _stmt.getMaxRows());
		
		boolean rc = _stmt.execute(querySql);
		exInfo.sqlExecutionComplete();

		// Display any warnings generated by the SQL execution.
		handleAllWarnings(_session.getSQLConnection(), _stmt);

		// If no ResultSet was returned by the executed query, see if any rows
		// were modified.
		int updateCount = -1;
		if (!rc)
		{
			updateCount = _stmt.getUpdateCount();
		}

		// Loop while we either have a ResultSet to process or rows have
		// been updated/inserted/deleted.
		while (rc || (updateCount != -1))
		{
			// User has cancelled the query execution.
			if (_stopExecution)
			{
				return false;
			}

			// We have a ResultSet to process.
			if (rc)
			{
				final ResultSet rs = _stmt.getResultSet();
				if (rs != null)
				{
					if (!processResultSet(rs, exInfo))
					{
						return false;
					}
				}
			}

			// No ResultSet returned so rows must have been modified.
			else
			{
				if (_handler != null)
					_handler.sqlDataUpdated(updateCount);
			}

			// Are there any more results to process?
			rc = _stmt.getMoreResults();
			if (!rc)
			{
				updateCount = _stmt.getUpdateCount();
			}
		}

		if (_handler != null)
			_handler.sqlExecutionComplete(exInfo);

		return true;
	}

	private boolean processResultSet(final ResultSet rs,
			final SQLExecutionInfo exInfo) throws DataSetException
	{
		if (_stopExecution)
		{
			return false;
		}

		if (_handler != null)
			_handler.sqlResultSetAvailable(rs, exInfo);

		handleResultSetWarnings(rs);

		try
		{
			rs.close();
		}
		catch (Throwable th)
		{
			s_log.error("Error closing ResultSet", th);
		}

		return true;
	}

	private void handleAllWarnings(SQLConnection conn, Statement stmt)
	{
		// If SQL executing produced warnings then write them out to the session
		// message handler. TODO: This is a pain. PostgreSQL sends "raise
		// notice" messages to the connection, not to the statment so they will
		// be mixed up with warnings from other statements.
		synchronized (conn)
		{
			try
			{
				handleWarnings(conn.getWarnings());
				conn.getConnection().clearWarnings();
			}
			catch (Throwable th)
			{
				s_log.debug("Driver doesn't handle Connection.getWarnings()/clearWarnings()", th);
			}
		}

		try
		{
			handleWarnings(stmt.getWarnings());
			stmt.clearWarnings();
		}
		catch (Throwable th)
		{
			s_log.debug("Driver doesn't handle Statement.getWarnings()/clearWarnings()",
							th);
		}
	}

	private void handleResultSetWarnings(ResultSet rs)
	{
		try
		{
			handleWarnings(rs.getWarnings());
		}
		catch (Throwable th)
		{
			s_log.error("Can't get warnings from ResultSet", th);
			_session.getMessageHandler().showMessage(th);
		}
	}

	private void handleWarnings(SQLWarning sw)
	{
		if (_handler != null)
		{
			try
			{
				while (sw != null)
				{
					_handler.sqlExecutionWarning(sw);
					sw = sw.getNextWarning();
				}
			}
			catch (Throwable th)
			{
				s_log.debug("Driver/DBMS can't handle SQLWarnings", th);
			}
		}
	}

	private void handleError(Throwable th)
	{
		if (_handler != null)
		{
			_handler.sqlExecutionException(th);
		}
	}

}