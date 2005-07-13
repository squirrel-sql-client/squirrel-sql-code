package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001-2004 Johan Companger
 * jcompagner@j-com.nl
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
 * jmheight@users.sourceforge.net
 *
 * Modifications copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications copyright (C) 2001-2005 Glenn Griffin
 * gwghome@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.properties.EditWhereCols;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetUpdateableTableModelListener;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

//JASON: Check the Old SQLExecutertask class against this one.
/**
 * This class can be used to execute SQL.
 * <p/>It implements Runnable so it can be executed as a thread
 * (asynchronus execution)
 *  or standalone in the main Swing thread (synchronus execution).
 */
public class SQLExecuterTask implements Runnable, IDataSetUpdateableTableModel
{

	/**
	 * We need to save the name of the SessionProperties display class at the time
	 * that the table was forced into edit mode so that if the properties get changed
	 * while we are in forced edit mode, we will change back to match the new
	 * Session Properties.
	 */
	String sqlOutputClassNameAtTimeOfForcedEdit = "";

	/**
	 * Remember whether or not the user has forced us into editing mode
	 * when the SessionProperties says to use read-only mode.
	 */
	boolean editModeForced = false;

	/**
	 * Defines the object that info is to be displayed for.
	 * This only applys for SELECTs with only one table
	 * (which may need to allow editing).
	 */
	private TableInfo ti = null;

	/**
	 * Remember which column contains the rowID; if no rowID, this is -1
	 * which does not match any legal column index.
	 * Note that for this class, since the list of columns to include is given
	 * by the user, we never include any pseudo-column automatically in the
	 * ResultSet, and thus we never have any legal column index here.
	 */
	int _rowIDcol = -1;

	/**
	 * This is the long name of the current table including everything that might be able to distinguish it
	 * from another table of the same name in a different DB.
	 */
	String fullTableName = null;

	// string to be passed to user when table name is not found or is ambiguous
	private final String TI_ERROR_MESSAGE =
		"Cannot edit table because table cannot be found\nor table name is not unique in DB.";


	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(SQLExecuterTask.class);

	/** The call back object*/
	private ISQLExecuterHandler _handler;

	/** Current session. */
	private ISession _session;

	/** SQL passed in to be executed. */
	private String _sql;
	private Statement _stmt;
	private boolean _stopExecution = false;

	private int _currentQueryIndex = 0;
   private ISQLExecutionListener[] _executionListeners;
   private Vector _dataSetUpdateableTableModelListener = new Vector();

   public SQLExecuterTask(ISession session, String sql,ISQLExecuterHandler handler)
	{
      this(session, sql, handler, new ISQLExecutionListener[0]);
	}

   public SQLExecuterTask(ISession session, String sql, ISQLExecuterHandler handler, ISQLExecutionListener[] executionListeners)
   {
      _session = session;
      _sql = sql;
      _handler = handler;
      _executionListeners = executionListeners;
   }

   public void run()
	{
      String lastExecutedStatement = null;
      int statementCount = 0;
      final SessionProperties props = _session.getProperties();
		try
		{
			final SQLConnection conn = _session.getSQLConnection();
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
				boolean queriesFound = false;
            while (qt.hasQuery())
				{
               queriesFound = true;
					queryStrings.add(qt.nextQuery());
				}
            if(false == queriesFound)
            {
               throw new IllegalArgumentException("No SQL selected for execution.");
            }

				/**
				 * ?? The following code was in the original version of this
				 * file and was removed at some point.  It appears to implement
				 * a potentially useful functionality, and there does not seem to be any
				 * corresponding mechanism that does not use the sqlPanel
				 * variable which is not present any more.
				 * The mechanism is to let a plugin review/modify as a single
				 * batch ALL of the
				 * sql statements that are about to be executed.
				 * There is a similar but not identical mechanism used further
				 * down in the loop that seems to let plugins work on
				 * individual SQL statements just before they are executed,
				 * but that is not the same as operating on all of them
				 * in one group (eg: plugin may want to add or remove statements
				 * depending on what else is happening in the SQL).
				 *
				 * Do we need to re-add this? -- GWG, May 2005

				//	Allow plugins to modify the requested SQL prior to execution.
				queryStrings = _sqlPanel.fireAllSQLToBeExecutedEvent(queryStrings);

				{
					final int queryCount = queryStrings.size();
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							_cancelPanel.setQueryCount(queryCount);
						}
					});
				}
				??? END of missing code
				**/

				_currentQueryIndex = 0;

				// Process each individual query.
				boolean maxRowsHasBeenSet = correctlySupportsMaxRows;
            int processedStatementCount = 0;
            statementCount = queryStrings.size();

            _handler.sqlStatementCount(statementCount);

				while (!queryStrings.isEmpty())
				{
					String querySql = (String)queryStrings.remove(0);
					if (querySql != null)
					{
                  ++processedStatementCount;
						if (_handler != null)
                  {
							_handler.sqlToBeExecuted(querySql);
                  }

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
                     lastExecutedStatement = querySql;

                     if (!processQuery(querySql, processedStatementCount, statementCount))
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
                     else
                     {
                        handleError(ex, null);
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
         if(props.getAbortOnError() && 1 < statementCount)
         {
            handleError(ex, "Error occured in:\n" + lastExecutedStatement);
         }
         else
         {
            handleError(ex, null);
         }
		}
		finally
		{
			if (_stopExecution)
			{
				if (_handler != null)
            {
					_handler.sqlExecutionCancelled();
            }
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
         if (_handler != null)
         {
            _handler.sqlCloseExecutionHandler();
         }
		}
	}

	public void cancel()
	{
		_stopExecution = true;
	}

	private boolean processQuery(String sql, int processedStatementCount, int statementCount) throws SQLException
	{
		++_currentQueryIndex;

		final SQLExecutionInfo exInfo = new SQLExecutionInfo(	_currentQueryIndex, sql, _stmt.getMaxRows());
      boolean firstResultIsResultSet = _stmt.execute(sql);
		exInfo.sqlExecutionComplete();

		// Display any warnings generated by the SQL execution.
		handleAllWarnings(_session.getSQLConnection(), _stmt);

      boolean supportsMultipleResultSets = _session.getSQLConnection().getSQLMetaData().supportsMultipleResultSets();
      boolean inFirstLoop = true;

      // Loop while we either have a ResultSet to process or rows have
      // been updated/inserted/deleted.
		while (true)
		{
			// User has cancelled the query execution.
			if (_stopExecution)
			{
				return false;
			}


         int updateCount = _stmt.getUpdateCount();

         ResultSet res = null;
         if (inFirstLoop && firstResultIsResultSet)
         {
            res = _stmt.getResultSet();
         }
         else if(false == inFirstLoop)
         {
            res = _stmt.getResultSet();
         }


         if (-1 != updateCount)
         {
            if (_handler != null)
            {
               _handler.sqlDataUpdated(updateCount);
            }
         }
         if (null != res)
         {
            if (!processResultSet(res, exInfo))
            {
               return false;
            }
         }

         if (false == supportsMultipleResultSets)
         {
            // This is (a logically not sufficent) try to cope with the problem that there are the following
            // contradictory rules in the JDBC API Doc:
            // Statement.getResultSet():
            // This method should be called only once per result.
            // Statement.getUpdateCount():
            // This method should be called only once per result.
            // Statement.getMoreResults():
            // There are no more results when the following is true: (!getMoreResults() && (getUpdateCount() == -1)
            //
            // If getMoreResults() returns false, we don't know if we have more results, we only know that it isn't
            // a result set. Since we called getUpdateCount() before getMoreResults() because we would like to know
            // the update count of the first result, we might not be allowed to call getUpdateCount() again.
            //
            // The Intersystems Cache Driver for example always returns the same updateCount on simple
            // INSERT, UPDATE, DELETE statements not matter if getMoreResults() was called. So updateCount never
            // gets -1 and this will loop forever. When I discussed the issue with the Intersystems people they
            // just told me not to call getUpdateCount() twice. That simple. My hope is that this will cure
            // problems with DBs that just don't care for multiple result sets.
            try
            {
               if (res != null) {
                   res.close();
               }
            }
            catch (Throwable th)
            {
               s_log.error("Error closing ResultSet", th);
            }
            break;
         }

         if (!_stmt.getMoreResults() && -1 == updateCount)
         {
            // There is no need to close result sets if we call _stmt.getMoreResults() because it
            // implicitly closes any current ResultSet.
            // ON DB2 version 7.1 it is even harmful to close a ResultSet explicitly.
            // _stmt.getMoreResults() will never return true anymore if you do.
            break;
         }
         inFirstLoop = false;
		}

      fireExecutionListeners(sql);

      if (_handler != null)
      {
         _handler.sqlExecutionComplete(exInfo, processedStatementCount, statementCount);
      }

      EditableSqlCheck edittableCheck = new EditableSqlCheck(exInfo); 


      if (edittableCheck.allowsEditing()) {
         // Get a list of all tables matching this name in DB
         ti = getTableName(edittableCheck.getTableNameFromSQL());
      }


		return true;
	}

   private void fireExecutionListeners(final String sql)
   {
      // This method is called from a thread.
      // In case listeners update Swing controls we invoke later here.
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            for (int i = 0; i < _executionListeners.length; i++)
            {
               _executionListeners[i].statementExecuted(sql);
            }
         }
      });
   }

   private boolean processResultSet(final ResultSet rs, final SQLExecutionInfo exInfo)
	{
		if (_stopExecution)
		{
			return false;
		}

		if (_handler != null)
			_handler.sqlResultSetAvailable(rs, exInfo, this);

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
				s_log
						.debug(
								"Driver doesn't handle Connection.getWarnings()/clearWarnings()",
								th);
			}
		}

		try
		{
			handleWarnings(stmt.getWarnings());
			stmt.clearWarnings();
		}
		catch (Throwable th)
		{
			s_log
					.debug(
							"Driver doesn't handle Statement.getWarnings()/clearWarnings()",
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

	private void handleError(Throwable th, String postErrorString)
	{
		if (_handler != null)
			_handler.sqlExecutionException(th, postErrorString);
	}




	/*
	 *
	 *
	 * Implement IDataSetUpdateableModel interface
	 * and IDataSetUpdateableTableModel interface
	 *
	 * TODO: THIS CODE WAS COPIED FROM ContentsTab.  IT SHOULD PROBABLY
	 * BE PUT INTO A COMMON LOCATION AND SHARED BY BOTH THIS
	 * CLASS AND ContentsTab.
	 *
	 *
	 */


	/**
	 * return the name of the table that is unambiguous across DB accesses,
	 * including the same DB on different machines.
	 * This function is static because it is used elsewhere to generate the same
	 * name as is used within instances of this class.
	 *
	 * @return the name of the table that is unique for this DB access
	 */
	public static String getUnambiguousTableName(ISession session, String name) {
		return session.getAlias().getUrl()+":"+name;
	}

	/**
	 * Get the full name of this table, creating that name the first time we are called
	 */
	private String getFullTableName() {
		if (fullTableName == null) {
			try {
				final String name = ti.getQualifiedName();
				fullTableName = getUnambiguousTableName(_session, name);
			}
			catch (Exception e) {
					// not sure what to do with this exception???
			}
		}
		return fullTableName;
	}

	/**
	 * If the user forces us into edit mode, remember that they did so for this table.
	 */
	public void forceEditMode(boolean mode)
	{
		editModeForced = mode;
		sqlOutputClassNameAtTimeOfForcedEdit =
			_session.getProperties().getTableContentsOutputClassName();

      DataSetUpdateableTableModelListener[] listeners =
         (DataSetUpdateableTableModelListener[]) _dataSetUpdateableTableModelListener.toArray(new DataSetUpdateableTableModelListener[0]);

      for (int i = 0; i < listeners.length; i++)
      {
         listeners[i].forceEditMode(mode);
      }


		/**
		 * Tell the GUI to rebuild itself.
		 * This is not a clean way to do that, since we are telling the
		 * SessionProperties listeners that a property has changed when
		 * in reality none of them have done so, but this does cause the
		 * GUI to be rebuilt.
		 */
//		_session.getProperties().forceTableContentsOutputClassNameChange();
	}

	/**
	 * The fw needs to know whether we are in forced edit mode or not
	 * so it can decide whether or not to let the user undo that mode.
	 */
	public boolean editModeIsForced()
	{
		return editModeForced;
	}

	/**
	 * If the user has forced us into editing mode, use the EDITABLE_TABLE form, but
	 * otherwise use whatever form the user specified in the Session Preferences.
	 */
	protected String getDestinationClassName()
	{
		if (editModeForced)
		{
			if (_session.getProperties().getTableContentsOutputClassName().equals(
				sqlOutputClassNameAtTimeOfForcedEdit))
			{
				return _session.getProperties().getEditableTableOutputClassName();
			}
			// forced edit mode ended because user changed the Session Properties
			editModeForced = false;
		}

		// if the user selected Editable Table in the Session Properties,
		// then the display will be an editable table; otherwise the display is read-only
		return _session.getProperties().getTableContentsOutputClassName();
	}

	/**
	 * Link from fw to check on whether there are any unusual conditions
	 * in the current data that the user needs to be aware of before updating.
	 */
	public String getWarningOnCurrentData(
		Object[] values,
		ColumnDisplayDefinition[] colDefs,
		int col,
		Object oldValue)
	{

		// if we could not identify which table to edit, tell user
		if (ti == null)
			return TI_ERROR_MESSAGE;

		String whereClause = getWhereClause(values, colDefs, col, oldValue);

		// It is possible for a table to contain only columns of types that
		// we cannot process or do selects on, so check for that.
		// Since this check is on the structure of the table rather than the contents,
		// we only need to do it once (ie: it is not needed in getWarningOnProjectedUpdate)
		if (whereClause.length() == 0)
			return "The table has no columns that can be SELECTed on.\nAll rows will be updated.\nDo you wish to proceed?";

		final ISession session = _session;
		final SQLConnection conn = session.getSQLConnection();

		int count = -1;	// start with illegal number of rows matching query

		try
		{
			final Statement stmt = conn.createStatement();
			try
			{
				final ResultSet rs = stmt.executeQuery("select count(*) from "
									+ ti.getQualifiedName() + whereClause);
				rs.next();
				count = rs.getInt(1);
			}
			finally
			{
				stmt.close();
			}
		}
		catch (SQLException ex)
		{
			return "Exception seen during check on DB.  Exception was:\n"+
				ex.getMessage() +
				"\nUpdate is probably not safe to do.\nDo you wish to proceed?";
		}

		if (count == -1)
			return "Unknown error during check on DB.  Update is probably not safe.\nDo you wish to proceed?";

		if (count == 0)
			return "This row in the Database has been changed since you refreshed the data.\nNo rows will be updated by this operation.\nDo you wish to proceed?";

		if (count > 1)
			return "This operation will update " + count + " identical rows.\nDo you wish to proceed?";

		// no problems found, so do not return a warning message.
		return null;	// nothing for user to worry about
	}

	/**
	 * Link from fw to check on whether there are any unusual conditions
	 * that will occur after the update has been done.
	 */
	public String getWarningOnProjectedUpdate(
		Object[] values,
		ColumnDisplayDefinition[] colDefs,
		int col,
		Object newValue)
	{
      try
      {
         // if we could not identify which table to edit, tell user
         if (ti == null)
            return TI_ERROR_MESSAGE;

         String whereClause = getWhereClause(values, colDefs, col, newValue);

         final ISession session = _session;
         final SQLConnection conn = session.getSQLConnection();

         int count = -1;	// start with illegal number of rows matching query

         try
         {
            final Statement stmt = conn.createStatement();
            try
            {
               final ResultSet rs = stmt.executeQuery("select count(*) from "
                              + ti.getQualifiedName() + whereClause);
               rs.next();
               count = rs.getInt(1);
            }
            finally
            {
               stmt.close();
            }
         }
         catch (SQLException ex)
         {
            return "Exception seen during check on DB.  Exception was:\n"+
               ex.getMessage() +
               "\nUpdate is probably not safe to do.\nDo you wish to proceed?";
         }

         if (count == -1)
            return "Unknown error during check on DB.  Update is probably not safe.\nDo you wish to proceed?";

         // There are some fields that cannot be used in a WHERE clause, either
         // because there cannot be an exact match (e.g. REAL, FLOAT), or
         // because we may not have the actual data in hand (BLOB/CLOB), or
         // because the data cannot be expressed in a string form (e.g. BINARY).
         // An update to one of those fields
         // will look like we are replacing one row with an identical row (because
         // we can only "see" the fields that we know how to do WHEREs on).  Therefore,
         // when we are updating them, there should be exactly one row that matches
         // all of our other fields, and when we are not updating one of these
         // special types of fields, there should be
         // no rows that exactly match our criteria (we hope).
         //
         // We determine whether this field is one that cannot be used in the WHERE
         // clause by checking the value returned for that field to use in the
         // WHERE clause.  Any field that can be used there will return something
         // of the form "<fieldName> = <value>", and a field that cannot be
         // used will return a null or zero-length string.
         String databaseProductName = _session.getSQLConnection().getSQLMetaData().getDatabaseProductName();

         if (CellComponentFactory.getWhereClauseValue(colDefs[col], values[col], databaseProductName) == null ||
            CellComponentFactory.getWhereClauseValue(colDefs[col], values[col], databaseProductName).length() == 0) {
               if (count > 1)
                  return "This operation will result in " + count +" identical rows.\nDo you wish to proceed?";
         }
         else {
            // the field being updated is one whose contents
            //should be visible in the WHERE clause
            if (count > 0)
               return "This operation will result in " + count + " identical rows.\nDo you wish to proceed?";
         }

         // no problems found, so do not return a warning message.
         return null;	// nothing for user to worry about
      }
      catch (SQLException e)
      {
         throw new  RuntimeException(e);
      }

   }

	/**
	 * Re-read the value for a single cell in the table, if possible.
	 * If there is a problem, the message has a non-zero length when this returns.
	 */
	public Object reReadDatum(
		Object[] values,
		ColumnDisplayDefinition[] colDefs,
		int col,
		StringBuffer message) {

		// if we could not identify which table to edit, tell user
		if (ti == null)
			return TI_ERROR_MESSAGE;

		// get WHERE clause
		// The -1 says to ignore the last arg and use the contents of the values array
		// for the column that we care about.  However, since the data in
		// that column has been limited, when getWhereClause calls that
		// DataType with that value, the DataType will see that the data has
		// been limited and therefore cannnot be used in the WHERE clause.
		// In some cases it may be possible for the DataType to use the
		// partial data, such as "matches <data>*", but that may not be
		// standard accross all Databases and thus may be risky.
		String whereClause = getWhereClause(values, colDefs, -1, null);

		final ISession session = _session;
		final SQLConnection conn = session.getSQLConnection();

		Object wholeDatum = null;

		try
		{
			final Statement stmt = conn.createStatement();
			final String queryString =
				"SELECT " + colDefs[col].getLabel() +" FROM "+ti.getQualifiedName() +
				whereClause;

			try
			{
				ResultSet rs = stmt.executeQuery(queryString);

				// There should be one row in the data, so try to move to it
				if (rs.next() == false) {
					// no first row, so we cannot retrieve the data
					throw new SQLException(
						"Could not find any row in DB matching current row in table");
				}

				// we have at least one row, so try to retrieve the object
				// Do Not limit the read of this data
				wholeDatum = CellComponentFactory.readResultSet(colDefs[col], rs, 1, false);

				//  There should not be more than one row in the DB that matches
				// the table, and if there is we cannot determine which one to read,
				// so check that there are no more
				if (rs.next() == true) {
					// multiple rows - not good
					wholeDatum = null;
					throw new SQLException(
						"Muliple rows in DB match current row in table - cannot re-read data.");
				}
			}
			finally
			{
				stmt.close();
			}
		}
		catch (Exception ex)
		{
			message.append(
				"There was a problem reported while re-reading the DB.  The DB message was:\n"+
				ex.getMessage());

			// It would be nice to tell the user what happened, but if we try to
			// put up a dialog box at this point, we run into trouble in some
			// cases where the field continually tries to re-read after the dialog
			// closes (because it is being re-painted).
		}


		// return the whole contents of this column in the DB
		return wholeDatum;
	};

	/**
	 * link from fw to this for updating data
	 */
	public String updateTableComponent(
		Object[] values,
		ColumnDisplayDefinition[] colDefs,
		int col,
		Object oldValue,
		Object newValue)
	{
		// if we could not identify which table to edit, tell user
		if (ti == null)
			return TI_ERROR_MESSAGE;

		// get WHERE clause using original value
		String whereClause = getWhereClause(values, colDefs, col, oldValue);

		final ISession session = _session;
		final SQLConnection conn = session.getSQLConnection();

		int count = -1;

		try
		{
			final String sql = "UPDATE " + ti.getQualifiedName() +
				" SET " + colDefs[col].getLabel() + " = ? " +
				whereClause;
			s_log.debug(sql);
			final PreparedStatement pstmt = conn.prepareStatement(sql);
			try
			{
				// have the DataType object fill in the appropriate kind of value
				// into the first (and only) variable position in the prepared stmt
				CellComponentFactory.setPreparedStatementValue(
					colDefs[col], pstmt, newValue, 1);
				count = pstmt.executeUpdate();
			}
			finally
			{
				pstmt.close();
			}
		}
		catch (SQLException ex)
		{
			return "There was a problem reported during the update.  The DB message was:\n"+
				ex.getMessage() +
				"\nThis may or may not be serious depending on the above message."+
				"\nThe data was probably not changed in the database."+
				"\nYou may need to refresh the table to get an accurate view of the current data.";
		}

		if (count == -1)
			return "Unknown problem during update.\nNo count of updated rows was returned.\nDatabase may be corrupted!";

		if (count == 0)
			return "No rows updated.";

		// everything seems to have worked ok
		return null;
	}


	/**
	 * Let fw get the rowIDcol
	 */
	public int getRowidCol()
	{
		return _rowIDcol;
	}


	/**
	 * helper function to create a WHERE clause to search the DB for matching rows.
	 * If the col number is < 0, then the colValue is ignored
	 * and the WHERE clause is constructed using only the values[].
	 */
	private String getWhereClause(
		Object[] values,
		ColumnDisplayDefinition[] colDefs,
		int col,
		Object colValue)
	{
      try
      {
         StringBuffer whereClause = new StringBuffer("");

         // For tables that have a lot of columns, the user may have limited the set of columns
         // to use in the where clause, so see if there is a table of col names
         HashMap colNames = (EditWhereCols.get(getFullTableName()));

         for (int i=0; i< colDefs.length; i++) {

            // if the user has said to not use this column, then skip it
            if (colNames != null) {
               // the user has restricted the set of columns to use.
               // If this name is NOT in the list, then skip it; otherwise we fall through
               // and use the column in the WHERE clause
               if (colNames.get(colDefs[i].getLabel()) == null)
                  continue;	// go on to the next item
            }

            // for the column that is being changed, use the value
            // passed in by the caller (which may be either the
            // current value or the new replacement value)
            Object value = values[i];
            if (i == col)
               value = colValue;

            // convert user representation of null into an actual null
            if (value != null && value.toString().equals("<null>"))
               value = null;

            // do different things depending on data type
            String databaseProductName = _session.getSQLConnection().getSQLMetaData().getDatabaseProductName();
            String clause = CellComponentFactory.getWhereClauseValue(colDefs[i], value, databaseProductName);

            if (clause != null && clause.length() > 0)
               if (whereClause.length() == 0)
               {
                  whereClause.append(clause);
               }
               else
               {
                  whereClause.append(" AND ");
                  whereClause.append(clause);
               }
         }

         // insert the "WHERE" at the front if there is anything in the clause
         if (whereClause.length() == 0)
            return "";

         whereClause.insert(0, " WHERE ");
         return whereClause.toString();
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }


	/**
	 * Delete a set of rows from the DB.
	 * If the delete succeeded this returns a null string.
	 * The deletes are done within a transaction
	 * so they are either all done or all not done.
	 */
	public String deleteRows(Object[][] rowData, ColumnDisplayDefinition[] colDefs) {

		// if we could not identify which table to edit, tell user
		if (ti == null)
			return TI_ERROR_MESSAGE;

		// get the SQL session
		final ISession session = _session;
		final SQLConnection conn = session.getSQLConnection();

		// string used as error indicator and description of problems seen
		// when checking for 0 or mulitple matches in DB
		String rowCountErrorMessage = "";

		// for each row in table, count how many rows match where clause
		// if not exactly one, generate message describing situation
		for (int i = 0; i < rowData.length; i++) {
			// get WHERE clause for the selected row
			// the -1 says to just use the contents of the values without
			// any substitutions
			String whereClause = getWhereClause(rowData[i], colDefs, -1, null);

			// count how many rows this WHERE matches
			try {
				// do the delete and add the number of rows deleted to the count
				final Statement stmt = conn.createStatement();
				try
				{
					ResultSet rs = stmt.executeQuery("SELECT count(*) FROM " +
						ti.getQualifiedName()+whereClause);

					rs.next();
					if (rs.getInt(1) != 1) {
						if (rs.getInt(1) == 0)
							rowCountErrorMessage += "\n   Row "+ (i+1) +" did not match any row in DB";
						else
							rowCountErrorMessage += "\n   Row "+ (i+1) +" matched "+rs.getInt(1)+" rows in DB";
					}
				}
				finally
				{
					stmt.close();
				}
			}
			catch (Exception e) {
				// some kind of problem - tell user
				return "While preparing for delete, saw exception:\n" + e;
			}
		}

		// if the rows do not match 1-for-1 to DB, ask user if they
		// really want to do delete
		if (rowCountErrorMessage.length() > 0) {
			int option = JOptionPane.showConfirmDialog(null,
				"There may be a mismatch between the table and the DB:\n"+
				rowCountErrorMessage +
				"\nDo you wish to proceed with the deletes anyway?",
				"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if ( option != JOptionPane.YES_OPTION) {
				return "Delete canceled at user request.";
			}
		}

		// for each row in table, do delete and add to number of rows deleted from DB
		for (int i = 0; i < rowData.length; i++) {
			// get WHERE clause for the selected row
			// the -1 says to just use the contents of the values without
			// any substitutions
			String whereClause = getWhereClause(rowData[i], colDefs, -1, null);

			// try to delete
			try {
				// do the delete and add the number of rows deleted to the count
				final Statement stmt = conn.createStatement();
				try
				{
					stmt.executeUpdate("DELETE FROM " +
						ti.getQualifiedName() + whereClause);
				}
				finally
				{
					stmt.close();
				}
			}
			catch (Exception e) {
				// some kind of problem - tell user
				return "One of the delete operations failed with exception:\n" + e +
						"\nDatabase is in an unknown state and may be corrupted.";
			}
		}

		return null;	// hear no evil, see no evil
	}

	/**
	 * Let fw get the list of default values for the columns
	 * to be used when creating a new row
	 */
	public String[] getDefaultValues(ColumnDisplayDefinition[] colDefs) {

		// we return something valid even if there is a DB error
		final String[] defaultValues = new String[colDefs.length];

		// if we could not identify which table to edit, just return
		if (ti == null)
		{
			return defaultValues;
		}

		final ISession session = _session;
		final SQLConnection conn = session.getSQLConnection();

		DatabaseMetaData dmd = null;
		try
		{
			dmd = conn.getSQLMetaData().getJDBCMetaData();
			final ResultSet rs =
				dmd.getColumns(ti.getCatalogName(), ti.getSchemaName(),
					ti.getSimpleName(), "");
			try
			{
				// read the DB MetaData info and fill in the value, if any
				// Note that the ResultSet info and the colDefs should be
				// in the same order, but we cannot guarantee that.
				int expectedColDefIndex = 0;
				while (rs.next()) {
					// get the column name
					String colName = rs.getString(4);

					// get the default value
					String defValue = rs.getString(13);

					// if value was null, we do not need to do
					// anything else with this column.
					// Also assume that a value of "" is equivilent to null
					if (defValue != null &&  defValue.length() > 0) {
						// find the entry in colDefs matching this column
						if (colDefs[expectedColDefIndex].getLabel().equals(colName)) {
							// DB cols are in same order as colDefs
							defaultValues[expectedColDefIndex] = defValue;
						}
						else {
							// colDefs not in same order as DB, so search for
							// matching colDef entry
							// Note: linear search here will NORMALLY be not too bad
							// because most tables do not have huge numbers of columns.
							for (int i=0; i<colDefs.length; i++) {
								if (colDefs[i].getLabel().equals(colName)) {
									defaultValues[i] = defValue;
									break;
								}
							}
						}
					}

					// assuming that the columns in table match colDefs,
					// bump the index to point to the next colDef entry
					expectedColDefIndex++;
				} // while
			}
			finally
			{
				rs.close();
			}
		}
		catch (Exception ex)
		{
			s_log.error("Error retrieving default column values", ex);
		}

		return defaultValues;
	}


	/**
	 * Insert a row into the DB.
	 * If the insert succeeds this returns a null string.
	 */
	public String insertRow(Object[] values, ColumnDisplayDefinition[] colDefs) {

		// if we could not identify which table to edit, tell user
		if (ti == null)
			return TI_ERROR_MESSAGE;

		final ISession session = _session;
		final SQLConnection conn = session.getSQLConnection();

		int count = -1;

		try
		{
			// start the string for use in the prepared statment
			StringBuffer buf = new StringBuffer(
				"INSERT INTO " + ti.getQualifiedName() + " VALUES (");

			// add a variable position for each of the columns
			for (int i=0; i<colDefs.length; i++) {
				if (i != _rowIDcol)
					buf.append(" ?,");
			}

			// replace the last "," with ")"
			buf.setCharAt(buf.length()-1, ')');

			final PreparedStatement pstmt = conn.prepareStatement(buf.toString());

			try
			{
				// have the DataType object fill in the appropriate kind of value
				// into the appropriate variable position in the prepared stmt
				for (int i=0; i<colDefs.length; i++) {
					if (i != _rowIDcol) {
						CellComponentFactory.setPreparedStatementValue(
							colDefs[i], pstmt, values[i], i+1);
					}
				}
				count = pstmt.executeUpdate();
			}
			finally
			{
				pstmt.close();
			}
		}
		catch (SQLException ex)
		{
			return "Exception seen during check on DB.  Exception was:\n"+
				ex.getMessage() +
				"\nInsert was probably not completed correctly.  DB may be corrupted!";
		}

		if (count != 1)
			return "Unknown problem during update.\nNo count of inserted rows was returned.\nDatabase may be corrupted!";

		// insert succeeded
		return null;
	}

   public void addListener(DataSetUpdateableTableModelListener l)
   {
      _dataSetUpdateableTableModelListener.add(l);
   }

   public void removeListener(DataSetUpdateableTableModelListener l)
   {
      _dataSetUpdateableTableModelListener.remove(l);
   }


   /**
	* Get the full name info for the table that is being referred to in the
	* SQL query.
	* Since we do not know the catalog, schema, or the actual name used in
	* this DB to refer to "table" types, we cannot filter the initial query on any of
	* those criteria.  Thus the only thing we can do is get all of the names
	* of everything in the DB, then scan for things matching the name of the
	* table as entered by the user in the SQL query.  If there are no objects
	* with that name or multiple objects with that name, we do not allow editing.
	* This method was originally copied from TableTypeExpander.createChildren
	* and heavilly modified.
	*
	* @param	tableNameInSQL	Name of the table as typed by the user in the SQL query.
	*
	* @return	A  <TT>TableInfo</TT> object for the only DB object
	* 	with the given name, or null if there is none or more than one with that name.
	*/
	public TableInfo getTableName(String tableNameFromSQL)
	{
		TableInfo table = null;
		int count = 0;
		try
		{
			final SQLConnection conn = _session.getSQLConnection();
			final SQLDatabaseMetaData md = conn.getSQLMetaData();
//			final ITableInfo[] tables = md.getTables(null, null, "%", null);
			final ITableInfo[] tables = md.getAllTables();

			// filter the list of all DB objects looking for things with the given name
			for (int i = 0; i < tables.length; ++i)
			{

            String simpleName = tables[i].getSimpleName().toUpperCase();
            String nameWithSchema = (tables[i].getSchemaName() + "." + tables[i].getSimpleName()).toUpperCase();
            String nameWithSchemaAndCatalog = (tables[i].getSchemaName() + "." + tables[i].getSchemaName() + "." + tables[i].getSimpleName()).toUpperCase();


            if (   simpleName.equals(tableNameFromSQL)
                || nameWithSchema.equals(tableNameFromSQL)
                || nameWithSchemaAndCatalog.equals(tableNameFromSQL))
            {
					count++;
					table = (TableInfo)tables[i];
				}
/***
				else {
					tables[i] = null;
				}
*****/
			}

		}
		catch (Exception e) {
			count = 0;
		}

		// if there are no objects with that name, we cannot edit.
		// if there are multiple objects with that name, we cannot edit
		//  because we do not know which object to work on.
		if (count != 1)
			return null;

		// we have the one and only table
		return table;
   }
}