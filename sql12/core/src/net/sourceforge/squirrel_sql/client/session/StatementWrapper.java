package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetWrapper;
import net.sourceforge.squirrel_sql.fw.datasetviewer.StatementCallback;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;

public class StatementWrapper implements StatementCallback
{
   private static final ILogger s_log = LoggerController.createLogger(StatementWrapper.class);

   private boolean _maxRowsWasSet;
   private ISession _session;
   private Statement _statement;
   private ArrayList<ResultSetWrapper> _resultSetWrappers = new ArrayList<ResultSetWrapper>();
   private boolean _hasReturnedResultSet;

   public StatementWrapper(Statement stmt)
   {
      this(stmt, null);
   }

   public StatementWrapper(Statement statement, ISession session)
   {
      _session = session;
      _statement = statement;
   }


   public void setMaxRows()
   {
      if(null == _session || isContinueReadActive())
      {
         return;
      }


      if(_session.getProperties().getSQLLimitRows())
      {
         _maxRowsWasSet = true;
         try
         {
            _statement.setMaxRows(_session.getProperties().getSQLNbrRowsToShow());
         }
         catch (Exception e)
         {
            s_log.error("Can't Set MaxRows", e);
         }
      }

   }

   @Override
   public boolean isMaxRowsWasSet()
   {
      return _maxRowsWasSet;
   }

   @Override
   public int getMaxRowsCount()
   {
      return _session.getProperties().getSQLNbrRowsToShow();
   }

   public void setFetchSize()
   {
      if(null == _session)
      {
         return;
      }


      if(_session.getProperties().getSQLUseFetchSize() && _session.getProperties().getSQLFetchSize() > 0)
      {
         try
         {
            _statement.setFetchSize(_session.getProperties().getSQLFetchSize());
         }
         catch (Exception e)
         {
            s_log.error("Can't Set FetchSize", e);
         }
      }
   }


   public void cancel() throws SQLException
   {
      if (null != _statement)
      {
         _statement.cancel();
      }
   }

   public ResultSetWrapper getResultSetWrapper() throws SQLException
   {
      ResultSet resultSet = _statement.getResultSet();

      if(null == resultSet)
      {
         // Happens when executing procs with more than on result set
         return null;
      }

      _hasReturnedResultSet = true;


      ResultSetWrapper resultSetWrapper = new ResultSetWrapper(resultSet, this);
      _resultSetWrappers.add(resultSetWrapper);
      return resultSetWrapper;
   }

   public boolean getMoreResults() throws SQLException
   {
      if(isContinueReadActive() && _hasReturnedResultSet)
      {

         // _statement.getMoreResults() will implicitly close any current ResultSet. (see API doc of Statement.getMoreResults())
         // So when continueRead is active we cannot support multipple result sets anymore.
         // Another reason why not to choose this option. :-)
         return false;
      }

      return _statement.getMoreResults();
   }

   /**
    * Some drivers, such as SQLite, don't properly support getMaxRows/setMaxRows for statements.
    *
    * @return the max number of rows that could be returned by this statement
    */
   public int getMaxRows()
   {
      int result = 0;
      try
      {
         result = _statement.getMaxRows();
      }
      catch (SQLException e)
      {
         if (s_log.isDebugEnabled())
         {
            s_log.debug("Unexpected exception: " + e.getMessage(), e);
         }
      }
      return result;
   }

   public SQLWarning getWarnings() throws SQLException
   {
      return _statement.getWarnings();
   }

   public void clearWarnings() throws SQLException
   {
      _statement.clearWarnings();
   }

   public boolean execute(String sql) throws SQLException
   {
      return _statement.execute(sql);
   }

   public int getUpdateCount() throws SQLException
   {
      if(isContinueReadActive() && _hasReturnedResultSet)
      {
         // This is needed to stop the executer to ask for more results
         return -1;
      }

      return _statement.getUpdateCount();
   }

   @Override
   public boolean isContinueReadActive()
   {
      return _session.getProperties().getSQLReadOn();
   }

   @Override
   public int getFirstBlockCount()
   {
      return _session.getProperties().getSQLReadOnBlockSize();
   }

   @Override
   public int getContinueBlockCount()
   {
      return _session.getProperties().getSQLReadOnBlockSize();
   }

   @Override
   public void closeStatementIfContinueReadActive()
   {
      if (false == isContinueReadActive())
      {
         return;
      }

      _closeStatement();
   }

   public void closeIfContinueReadIsNotActive() throws SQLException
   {
      if(isContinueReadActive())
      {
         return;
      }

      _closeStatement();
   }

   private void _closeStatement()
   {
      try
      {
         SQLUtilities.closeStatement(_statement);
      }
      finally
      {
         _statement = null;
      }
   }


}
