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
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltypecheck.DataChangesAllowedCheck;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltypecheck.SQLTypeCheck;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.schemainfo.DatabaseUpdateInfosListener;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfoUpdateCheck;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetWrapper;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryHolder;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.SwingUtilities;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class can be used to execute SQL.
 * <p/>It implements Runnable so it can be executed as a thread
 * (asynchronus execution)
 *  or standalone in the main Swing thread (synchronus execution).
 */
public class SQLExecuterTask implements Runnable
{
   private static final ILogger s_log = LoggerController.createLogger(SQLExecuterTask.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLExecuterTask.class);

   /** The call back object*/
   private ISQLExecuterHandler _handler;

   /** Current session. */
   private ISession _session;

   /** SQL passed in to be executed. */
   private String _sql;
   private boolean _cancelExecution = false;

   private int _currentQueryIndex = 0;
   private List<ISQLExecutionListener> _executionListeners = new ArrayList<>();
   private SchemaInfoUpdateCheck _schemaInfoUpdateCheck;
   private IQueryTokenizer _tokenizer = null;
   /** Whether or not to check if the schema should be updated */
   private boolean _schemaCheck = true;
   private StatementWrapper _currentStatementWrapper;
   private String _tableToBeEdited;
   private boolean _executeEditableCheck = true;

   public SQLExecuterTask(ISession session, String sql,ISQLExecuterHandler handler)
   {
      this(session, sql, handler, new ISQLExecutionListener[0]);
   }

   public SQLExecuterTask(ISession session, String sql, ISQLExecuterHandler handler, ISQLExecutionListener[] executionListeners)
   {
      this(session, sql, handler, Arrays.asList(executionListeners), null);
   }

   public SQLExecuterTask(ISession session, String sql, ISQLExecuterHandler handler, List<ISQLExecutionListener> executionListeners, String tableToBeEdited)
   {
      _tableToBeEdited = tableToBeEdited;
      _session = session;
      _schemaInfoUpdateCheck = new SchemaInfoUpdateCheck(_session);
      _sql = sql;
      _tokenizer = _session.getQueryTokenizer();
      _tokenizer.setScriptToTokenize(_sql);
      _handler = handler;
      if (_handler == null)
      {
          _handler = new DefaultSQLExecuterHandler(session);
      }
      _executionListeners = executionListeners;
   }

   public void clearExecutionListeners()
   {
       _executionListeners.clear();
   }

   public void setExecuteEditableCheck(boolean executeEditableCheck)
   {
      _executeEditableCheck = executeEditableCheck;
   }

   /**
    * Returns the number of queries that the tokenizer found in _sql.
    * @return
    */
   public int getQueryCount() {
       return _tokenizer.getQueryCount();
   }

   public void setSchemaCheck(boolean schemaCheck)
   {
      _schemaCheck = schemaCheck;
   }

   public void run()
   {
      runDirect(null);
   }

   public void runDirect(DatabaseUpdateInfosListener databaseUpdateInfosListener)
   {
      if (_sql == null)
      {
         return;
      }

      String lastExecutedStatement = null;
      int statementCount = 0;
      final SessionProperties props = _session.getProperties();


      ArrayList<String> sqlExecErrorMsgs = new ArrayList<>();

      ISQLConnection conn = null;
      try
      {
         conn = _session.checkOutUserQuerySQLConnection();

         final boolean correctlySupportsMaxRows = conn.getSQLMetaData().correctlySupportsSetMaxRows();

         if(_tokenizer.getQueryCount() == 0)
         {
            throw new IllegalArgumentException("No SQL selected for execution.");
         }

         _currentQueryIndex = 0;

         int processedStatementCount = 0;
         statementCount = _tokenizer.getQueryCount();

         _handler.sqlStatementCount(statementCount);

         while (_tokenizer.hasQuery() && !_cancelExecution)
         {
            QueryHolder querySql = _tokenizer.nextQuery();
            if (querySql == null)
            {
               continue;
            }

            if(false == DataChangesAllowedCheck.checkSqlExecutionAllowed(_session, querySql))
            {
               continue;
            }

            ++processedStatementCount;
            if (_handler != null)
            {
               _handler.sqlToBeExecuted(querySql);
            }

            _currentStatementWrapper = createStatementWrapper(conn);
            if (correctlySupportsMaxRows)
            {
               _currentStatementWrapper.setMaxRows();
            }


            // Some driver don't correctly support setMaxRows. In
            // these cases use setMaxRows only if this is a
            // SELECT.
            if (false == correctlySupportsMaxRows)
            {
               if (SQLTypeCheck.isSelectStatement(querySql.getQuery()))
               {
                  _currentStatementWrapper.setMaxRows();
               }
               else if (_currentStatementWrapper.isMaxRowsWasSet())
               {
                  _currentStatementWrapper.closeIfContinueReadIsNotActive();
                  _currentStatementWrapper = createStatementWrapper(conn);
               }
            }

            try
            {
               lastExecutedStatement = querySql.getQuery();

               if (!processQuery(querySql, processedStatementCount, statementCount, _currentStatementWrapper, conn))
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
               if (_cancelExecution)
               {
                  break;
               }
               else
               {
                  if (props.getAbortOnError())
                  {
                     throw ex;
                  }
                  else
                  {
                     if (1 < statementCount)
                     {
                        sqlExecErrorMsgs.add(handleError(ex, "Error occurred in:\n" + lastExecutedStatement));
                     }
                     else
                     {
                        sqlExecErrorMsgs.add(handleError(ex, null));
                     }
                  }
               }
            }
            finally
            {
               _currentStatementWrapper.closeIfContinueReadIsNotActive();
            }
         }
      }
      catch (final Throwable ex)
      {
         if(props.getAbortOnError() && 1 < statementCount)
         {
            sqlExecErrorMsgs.add(handleError(ex, "Error occurred in:\n" + lastExecutedStatement));
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
         _session.returnUserQuerySQLConnection(conn);

         if (_cancelExecution)
         {
            if (_handler != null)
            {
               _handler.sqlExecutionCancelled();
            }
         }
         if (_handler != null)
         {
            _handler.sqlCloseExecutionHandler(sqlExecErrorMsgs, lastExecutedStatement);
         }

         if (_schemaCheck)
         {
             try
             {
                _schemaInfoUpdateCheck.flush(databaseUpdateInfosListener);
             }
             catch (Throwable t)
             {
                s_log.error("Could not update cache ", t);
             }
         }

         SwingUtilities.invokeLater(() -> fireExecutionListenersFinshed());
      }
   }

   private StatementWrapper createStatementWrapper(ISQLConnection conn) throws SQLException
   {
      StatementWrapper ret = new StatementWrapper(conn.createStatement(), _session);
      ret.setFetchSize();
      ret.setQueryTimeOut();

      return ret;
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

   public void cancel()
   {
      if(_cancelExecution)
      {
         return;
      }
      _handler.sqlExecutionCancelled();
      // i18n[SQLResultExecuterPanel.canceleRequested=Query execution cancel requested by user.]
      String msg = s_stringMgr.getString("SQLResultExecuterPanel.canceleRequested");
      _session.getApplication().getMessageHandler().showMessage(msg);

      _cancelExecution = true;

      if (null != _currentStatementWrapper)
      {
         CancelStatementThread cst = new CancelStatementThread(_currentStatementWrapper, _session.getApplication().getMessageHandler());
         cst.tryCancel();
      }
   }

   private boolean processQuery(QueryHolder sql, int processedStatementCount, int statementCount, StatementWrapper statementWrapper, ISQLConnection conn) throws SQLException
   {
      ++_currentQueryIndex;

      final SQLExecutionInfo exInfo = new SQLExecutionInfo(	_currentQueryIndex, sql, statementWrapper.getMaxRows(), _tableToBeEdited);
      boolean firstResultIsResultSet = statementWrapper.execute(sql.getQuery());
      exInfo.sqlExecutionComplete();

      // Display any warnings generated by the SQL execution.
      handleAllWarnings(conn, statementWrapper);

      boolean supportsMultipleResultSets = conn.getSQLMetaData().supportsMultipleResultSets();
      boolean inFirstLoop = true;

      DataSetUpdateableTableModelImpl dataSetUpdateableTableModel = new DataSetUpdateableTableModelImpl();
      dataSetUpdateableTableModel.setSession(_session);

      // Loop while we either have a ResultSet to process or rows have
      // been updated/inserted/deleted.
      while (true)
      {
         // User has cancelled the query execution.
         if (_cancelExecution)
         {
            return false;
         }


         int updateCount = statementWrapper.getUpdateCount();

         ResultSetWrapper res = null;
         if (inFirstLoop && firstResultIsResultSet)
         {
            res = statementWrapper.getResultSetWrapper();
         }
         else if(false == inFirstLoop)
         {
            res = statementWrapper.getResultSetWrapper();
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
               if (!processResultSet(res, exInfo, dataSetUpdateableTableModel))
               {
                  return false;
               }

               if (_cancelExecution)
               {
                  return false;
               }
               
               // Each call to _stmt.getMoreResults() places the to the next output.
               // As long as it is a ResultSet, we process it ...
               if(supportsMultipleResultSets && statementWrapper.getMoreResults())
               {
                  res = statementWrapper.getResultSetWrapper();
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
            	updateCount = statementWrapper.getUpdateCount();
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

         if (!statementWrapper.getMoreResults() && -1 == updateCount)
         {
            // There is no need to close result sets if we call _stmt.getMoreResults() because it
            // implicitly closes any current ResultSet.
            // ON DB2 version 7.1 it is even harmful to close a ResultSet explicitly.
            // _stmt.getMoreResults() will never return true anymore if you do.
            break;
         }
         inFirstLoop = false;
      }

      SwingUtilities.invokeLater(() -> fireExecutionListeners(sql));

      if (_handler != null)
      {
         _handler.sqlExecutionComplete(exInfo, processedStatementCount, statementCount);
      }




      if (_executeEditableCheck)
      {
         // Do not create this object when _executeEditableCheck is false, i.e. in CLI mode.
         EditableSqlCheck edittableCheck = new EditableSqlCheck(exInfo, _session);

         if(edittableCheck.allowsEditing())
         {
            TableInfo ti = getTableName(edittableCheck.getTableNameFromSQL());
            dataSetUpdateableTableModel.setTableInfo(ti);
         }
         else
         {
            dataSetUpdateableTableModel.setTableInfo(null);
         }
      }
      else
      {
         dataSetUpdateableTableModel.setTableInfo(null);
      }

      if (_schemaCheck)
      {
          _schemaInfoUpdateCheck.addExecutionInfo(exInfo);
      }

      return true;
   }


   private void fireExecutionListeners(final QueryHolder sql)
   {
      for (ISQLExecutionListener executionListener : _executionListeners.toArray(new ISQLExecutionListener[0]))
      {
         executionListener.statementExecuted(sql);
      }
   }

   private void fireExecutionListenersFinshed()
   {
      for (ISQLExecutionListener executionListener : _executionListeners.toArray(new ISQLExecutionListener[0]))
      {
         executionListener.executionFinished();
      }
   }



   private boolean processResultSet(final ResultSetWrapper rs, final SQLExecutionInfo exInfo, DataSetUpdateableTableModelImpl dataSetUpdateableTableModel)
   {
      if (_cancelExecution)
      {
         return false;
      }

      if (_handler != null)
      {
         try
         {
            _handler.sqlResultSetAvailable(rs, exInfo, dataSetUpdateableTableModel);
         }
         catch (DataSetException ex)
         {
            if (_cancelExecution)
            {
               return false;
            }
            else
            {
               _session.showMessage(ex);
               s_log.error("Error reading ResultSet for SQL: "
                     + exInfo.getSQL(), ex);
            }
         }
      }

      handleResultSetWarnings(rs.getResultSet());

      rs.closeIfContinueReadIsNotActive();
      return true;
   }

   private void handleAllWarnings(ISQLConnection conn, StatementWrapper stmtWrapper)
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
         handleWarnings(stmtWrapper.getWarnings());
         stmtWrapper.clearWarnings();
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

         if (simpleName.equalsIgnoreCase(tableNameFromSQL)
            || nameWithSchema.equalsIgnoreCase(tableNameFromSQL)
            || nameWithSchemaAndCatalog.equalsIgnoreCase(tableNameFromSQL))
         {
            return (TableInfo) tables[i];
         }
      }
      // ok, that didn't work - let's see if the table looks fully qualified.
      // if so, we'll split the name from the schema/catalog and try that.
      String[] parts = tableNameFromSQL.split("\\.");
      if (parts.length == 2)
      {
         String catalogOrSchema = parts[0];
         String simpleName = parts[1];
         tables = _session.getSchemaInfo().getITableInfos(catalogOrSchema, null, simpleName);
         if (tables != null && tables.length > 0)
         {
            return (TableInfo) tables[0];
         }
         // Ok, maybe catalogOrSchema was really a schema instead.
         tables = _session.getSchemaInfo().getITableInfos(null, catalogOrSchema, simpleName);
         if (tables != null && tables.length > 0)
         {
            return (TableInfo) tables[0];
         }
      }

      //////////////////////////////////////////////////////////////
      // If we didn't already, try again with double quotes removed.
      String tableNameFromSQLDoubleQuotesRemoved = tableNameFromSQL.replaceAll("\"", "");

      if(tableNameFromSQL.equals(tableNameFromSQLDoubleQuotesRemoved))
      {
         return null;
      }
      else
      {
         return getTableName(tableNameFromSQLDoubleQuotesRemoved);
      }
      //
      //////////////////////////////////////////////////////////////
   }
}
