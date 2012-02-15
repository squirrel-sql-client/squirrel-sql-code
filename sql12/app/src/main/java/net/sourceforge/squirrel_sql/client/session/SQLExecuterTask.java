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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfoUpdateCheck;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetUpdateableTableModelListener;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataModelImplementationDetails;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class can be used to execute SQL.
 * <p/>It implements Runnable so it can be executed as a thread
 * (asynchronus execution)
 *  or standalone in the main Swing thread (synchronus execution).
 */
public class SQLExecuterTask implements Runnable, IDataSetUpdateableTableModel
{


   /** Logger for this class. */
   private static final ILogger s_log = LoggerController.createLogger(SQLExecuterTask.class);

   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(SQLExecuterTask.class);


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
   private DataSetUpdateableTableModelImpl _dataSetUpdateableTableModel;
   private SchemaInfoUpdateCheck _schemaInfoUpdateCheck;
   private IQueryTokenizer _tokenizer = null;
   /** Whether or not to check if the schema should be updated */
   private boolean schemaCheck = true;

   public SQLExecuterTask(ISession session, String sql,ISQLExecuterHandler handler)
   {
      this(session, sql, handler, new ISQLExecutionListener[0]);
   }

   public SQLExecuterTask(ISession session, String sql, ISQLExecuterHandler handler, ISQLExecutionListener[] executionListeners)
   {
      if (sql == null) {
          if (s_log.isDebugEnabled()) {
              s_log.debug("init(): expected non-null sql");
              return;
          }
      }
      _session = session;
      _schemaInfoUpdateCheck = new SchemaInfoUpdateCheck(_session);
      _sql = sql;
      _tokenizer = _session.getQueryTokenizer();
      _tokenizer.setScriptToTokenize(_sql);
      _handler = handler;
      if (_handler == null) {
          _handler = new DefaultSQLExecuterHandler(session);
      }
      _executionListeners = executionListeners;
      _dataSetUpdateableTableModel = new DataSetUpdateableTableModelImpl();
      _dataSetUpdateableTableModel.setSession(_session);
   }

   public void setExecutionListeners(ISQLExecutionListener[] executionListeners) {
       _executionListeners = executionListeners;
   }

   /**
    * Returns the number of queries that the tokenizer found in _sql.
    * @return
    */
   public int getQueryCount() {
       return _tokenizer.getQueryCount();
   }

   public void setSchemaCheck(boolean aBoolean) {
       schemaCheck = aBoolean;
   }

   public void run()
   {
       if (_sql == null) {
           if (s_log.isDebugEnabled()) {
               s_log.debug("init(): expected non-null sql.  Skipping execution");
           }
           return;
       }

      String lastExecutedStatement = null;
      int statementCount = 0;
      final SessionProperties props = _session.getProperties();

      ArrayList<String> sqlExecErrorMsgs = new ArrayList<String>();

      try
      {
         final ISQLConnection conn = _session.getSQLConnection();
         _stmt = conn.createStatement();

         try
         {
            if(props.getSQLUseFetchSize() && props.getSQLFetchSize() > 0)
            {
            	setFetchSize(props);
            }
             
             
            final boolean correctlySupportsMaxRows = conn.getSQLMetaData()
                  .correctlySupportsSetMaxRows();
            if (correctlySupportsMaxRows && props.getSQLLimitRows())
            {
               setMaxRows(props);
            }

            if(_tokenizer.getQueryCount() == 0)
            {
               throw new IllegalArgumentException("No SQL selected for execution.");
            }

            _currentQueryIndex = 0;

            // Process each individual query.
            boolean maxRowsHasBeenSet = correctlySupportsMaxRows;
            int processedStatementCount = 0;
            statementCount = _tokenizer.getQueryCount();

            _handler.sqlStatementCount(statementCount);

            while (_tokenizer.hasQuery() && !_stopExecution)
            {
               String querySql = _tokenizer.nextQuery();
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
                     if (isSelectStatement(querySql))
                     {
                        if (!maxRowsHasBeenSet)
                        {
                           setMaxRows(props);
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
                     // If the user has cancelled the query, don't bother logging
                     // an error message.  It is likely that the cancel request
                     // interfered with the attempt to fetch results from the
                     // ResultSet, which is to be expected when the Statement is
                     // closed.  So, let's not bug the user with obvious error
                     // messages that we can do nothing about.
                     if (_stopExecution) {
                         break;
                     } else {
                         if (props.getAbortOnError())
                         {
                            throw ex;
                         }
                         else
                         {
                            if(1 < statementCount)
                            {
                               sqlExecErrorMsgs.add(handleError(ex, "Error occured in:\n" + lastExecutedStatement));
                            }
                            else
                            {
                               sqlExecErrorMsgs.add(handleError(ex, null));
                            }
                         }
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
      catch (final Throwable ex)
      {
         if(props.getAbortOnError() && 1 < statementCount)
         {
            sqlExecErrorMsgs.add(handleError(ex, "Error occured in:\n" + lastExecutedStatement));
         }
         else
         {
            sqlExecErrorMsgs.add(handleError(ex, null));
         }

         if(false == ex instanceof SQLException)
         {
            s_log.error("Unexpected exception when executing SQL: " + ex, ex);
            enableEventQueueOutOfMemoryHandling(ex);
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
            _handler.sqlCloseExecutionHandler(sqlExecErrorMsgs, lastExecutedStatement);
         }

         if (schemaCheck) {
             try
             {
                _schemaInfoUpdateCheck.flush();
             }
             catch (Throwable t)
             {
                s_log.error("Could not update cache ", t);
             }
         }

         fireExecutionListenersFinshed();
      }
   }

   private void enableEventQueueOutOfMemoryHandling(final Throwable ex)
   {
      if(ex instanceof OutOfMemoryError)
      {
         Runnable runnable = new Runnable()
         {
            public void run()
            {
               throw new RuntimeException(ex);
            }
         };

         SwingUtilities.invokeLater(runnable);
      }
   }

   /**
	 * Set the fetchSize Arrtibute for the SQL-Statement;
	 */
	private void setFetchSize(SessionProperties props) 
	{
	    try
		{
		   _stmt.setFetchSize(props.getSQLFetchSize());
		}
		catch (Exception e)
		{
		   s_log.error("Can't Set FetchSize", e);
		}
	}

	private void setMaxRows(final SessionProperties props)
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

	/**
	 * Returns a boolean indicating whether or not the specified querySql appears to be a SELECT statement.
	 *
	 * @param querySql
	 *           the SQL statement to check
	 * @return true if it is a SELECT statement; false otherwise.
	 */
	private boolean isSelectStatement(String querySql)
	{
		return "SELECT".length() < querySql.trim()
		      .length()
		      && "SELECT".equalsIgnoreCase(querySql
		            .trim().substring(0,
		                  "SELECT".length()));
	}

   public void cancel()
   {
      if(_stopExecution)
      {
         return;
      }
      _handler.sqlExecutionCancelled();
      // i18n[SQLResultExecuterPanel.canceleRequested=Query execution cancel requested by user.]
      String msg = s_stringMgr.getString("SQLResultExecuterPanel.canceleRequested");
      _session.getApplication().getMessageHandler().showMessage(msg);

      _stopExecution = true;
      if (_stmt != null)
      {
         CancelStatementThread cst = new CancelStatementThread(_stmt, _session.getApplication().getMessageHandler());
         cst.tryCancel();
      }
   }

   private boolean processQuery(String sql, int processedStatementCount, int statementCount) throws SQLException
   {
      ++_currentQueryIndex;

      final SQLExecutionInfo exInfo = new SQLExecutionInfo(	_currentQueryIndex, sql, getMaxRows(_stmt));
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
         	boolean moreResultsReceived = false;
            while(true)
            {
               if (!processResultSet(res, exInfo))
               {
                  return false;
               }

               if (_stopExecution)
               {
                  return false;
               }
               
               // Each call to _stmt.getMoreResults() places the to the next output.
               // As long as it is a ResultSet, we process it ...
               if(supportsMultipleResultSets && _stmt.getMoreResults())
               {
                  res = _stmt.getResultSet();
                  moreResultsReceived = true;
               }
               else
               {
                  break;
               }
            }

            if (moreResultsReceived) {
            	// ... now we have reached an output that is not a result. We now have to ask for this 
            	// outputs update count - but only if we received more results.
            	updateCount = _stmt.getUpdateCount();
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

      if (edittableCheck.allowsEditing())
      {
         TableInfo ti = getTableName(edittableCheck.getTableNameFromSQL());
         _dataSetUpdateableTableModel.setTableInfo(ti);
      }
      else
      {
         _dataSetUpdateableTableModel.setTableInfo(null);
      }
      if (schemaCheck) {
          _schemaInfoUpdateCheck.addExecutionInfo(exInfo);
      }

      return true;
   }

   /**
    * Some drivers, such as SQLite, don't properly support getMaxRows/setMaxRows for statements.
    *
    * @param stmt the statement to get the max rows that could be returned in a result set for.
    *
    * @return the max number of rows that could be returned by this statement
    */
   private int getMaxRows(Statement stmt) {
   	int result = 0;
   	try
		{
			result = stmt.getMaxRows();
		}
		catch (SQLException e)
		{
			if (s_log.isDebugEnabled()) {
				s_log.debug("Unexpected exception: "+e.getMessage(), e);
			}
		}
		return result;
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

   private void fireExecutionListenersFinshed()
   {
      // This method is called from a thread.
      // In case listeners update Swing controls we invoke later here.
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            for (int i = 0; i < _executionListeners.length; i++)
            {
               _executionListeners[i].executionFinished();
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

      if (_handler != null) {
            try {
                _handler.sqlResultSetAvailable(rs, exInfo, this);
            } catch (DataSetException ex) {
                if (_stopExecution) {
                    return false;
                } else {
                    _session.showMessage(ex);
                    s_log.error("Error reading ResultSet for SQL: "
                            + exInfo.getSQL(), ex);
                }
            }
        }

      handleResultSetWarnings(rs);
      SQLUtilities.closeResultSet(rs);
      return true;
   }

   private void handleAllWarnings(ISQLConnection conn, Statement stmt)
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
            s_log.debug("Driver doesn't handle "
                        + "Connection.getWarnings()/clearWarnings()", th);
         }
      }

      try
      {
         handleWarnings(stmt.getWarnings());
         stmt.clearWarnings();
      }
      catch (Throwable th)
      {
         s_log.debug("Driver doesn't handle "
                    + "Statement.getWarnings()/clearWarnings()", th);
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
         _session.showMessage(th);
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

   private String handleError(Throwable th, String postErrorString)
   {
      if (_handler != null)
      {
         return _handler.sqlExecutionException(th, postErrorString);
      }

      return null;
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
      ITableInfo[] tables = _session.getSchemaInfo().getITableInfos();

      // filter the list of all DB objects looking for things with the given name
      for (int i = 0; i < tables.length; ++i)
      {
         String simpleName = tables[i].getSimpleName().toUpperCase();
         String nameWithSchema = simpleName;
         String nameWithSchemaAndCatalog = simpleName;

         if (null != tables[i].getSchemaName() && 0 < tables[i].getSchemaName().length())
         {
            nameWithSchema = tables[i].getSchemaName().toUpperCase() + "." + nameWithSchema;
            nameWithSchemaAndCatalog = nameWithSchema;
         }

         if (null != tables[i].getCatalogName() && 0 < tables[i].getCatalogName().length())
         {
            nameWithSchemaAndCatalog = tables[i].getCatalogName().toUpperCase() + "." + nameWithSchema;
         }

         if (simpleName.equals(tableNameFromSQL)
            || nameWithSchema.equals(tableNameFromSQL)
            || nameWithSchemaAndCatalog.equals(tableNameFromSQL))
         {
            return (TableInfo) tables[i];
         }
      }
      // ok, that didn't work - let's see if the table looks fully qualified.
      // if so, we'll split the name from the schema/catalog and try that.
      String[] parts = tableNameFromSQL.split("\\.");
      if (parts.length == 2)
      {
         String catalog = parts[0];
         String simpleName = parts[1];
         tables = _session.getSchemaInfo().getITableInfos(catalog, null, simpleName);
         if (tables != null && tables.length > 0)
         {
            return (TableInfo) tables[0];
         }
         // Ok, maybe catalog was really a schema instead.
         tables = _session.getSchemaInfo().getITableInfos(null, catalog, simpleName);
         if (tables != null && tables.length > 0)
         {
            return (TableInfo) tables[0];
         }
      }
      return null;

   }


   ////////////////////////////////////////////////////////
   // Implementataion of IDataSetUpdateableTableModel:
   // Delegation to _dataSetUpdateableTableModel
   public String getWarningOnCurrentData(Object[] values, ColumnDisplayDefinition[] colDefs, int col, Object oldValue)
   {
      return _dataSetUpdateableTableModel.getWarningOnCurrentData(values, colDefs, col, oldValue);
   }

   public String getWarningOnProjectedUpdate(Object[] values, ColumnDisplayDefinition[] colDefs, int col, Object newValue)
   {
      return _dataSetUpdateableTableModel.getWarningOnProjectedUpdate(values, colDefs, col, newValue);
   }

   public Object reReadDatum(Object[] values, ColumnDisplayDefinition[] colDefs, int col, StringBuffer message)
   {
      return _dataSetUpdateableTableModel.reReadDatum(values, colDefs, col, message);
   }

   public String updateTableComponent(Object[] values, ColumnDisplayDefinition[] colDefs, int col, Object oldValue, Object newValue)
   {
      return _dataSetUpdateableTableModel.updateTableComponent(values, colDefs, col, oldValue, newValue);
   }

   public int getRowidCol()
   {
      return _dataSetUpdateableTableModel.getRowidCol();
   }

   public String deleteRows(Object[][] rowData, ColumnDisplayDefinition[] colDefs)
   {
      return _dataSetUpdateableTableModel.deleteRows(rowData, colDefs);
   }

   public String[] getDefaultValues(ColumnDisplayDefinition[] colDefs)
   {
      return _dataSetUpdateableTableModel.getDefaultValues(colDefs);
   }

   public String insertRow(Object[] values, ColumnDisplayDefinition[] colDefs)
   {
      return _dataSetUpdateableTableModel.insertRow(values, colDefs);
   }

   public void addListener(DataSetUpdateableTableModelListener l)
   {
      _dataSetUpdateableTableModel.addListener(l);
   }

   public void removeListener(DataSetUpdateableTableModelListener l)
   {
      _dataSetUpdateableTableModel.removeListener(l);
   }

   public void forceEditMode(boolean mode)
   {
      _dataSetUpdateableTableModel.forceEditMode(mode);
   }

   public boolean editModeIsForced()
   {
      return _dataSetUpdateableTableModel.editModeIsForced();
   }

   //
   //////////////////////////////////////////////////////////////////////////////////


}
