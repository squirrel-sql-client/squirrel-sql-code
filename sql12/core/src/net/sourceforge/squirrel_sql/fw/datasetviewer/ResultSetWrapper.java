package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.timeoutproxy.TimeOutUtil;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetWrapper
{
   private static final ILogger s_log = LoggerController.createLogger(ResultSetWrapper.class);

   private final ResultSet _resultSet;
   private final StatementCallback _statementCallback;


   private ResultSetReadState _readState = new ResultSetReadState();
   private boolean _followUpBlockReached;

   public ResultSetWrapper(ResultSet resultSet, StatementCallback statementCallback)
   {
      _resultSet = resultSet;
      _statementCallback = statementCallback;
   }

   public ResultSetWrapper(ResultSet resultSet)
   {
      this(resultSet, null);
   }

   public ResultSet getResultSet()
   {
      return _resultSet;
   }

   public void closeIfContinueReadIsNotActive()
   {
      if (isContinueReadActive())
      {
         return;
      }

      ResultSetWrapperSessionLocal sessionLocal = getResultSetWrapperSessionLocal();
      if (false == sessionLocal.isCallingIsLastFailed())
      {
         try
         {
            _readState.setWasLastResultRowRead(TimeOutUtil.callWithTimeout(() -> _readState.isResNextHasReturnedFalse() || false == _resultSet.next()));
            //_resultSet.isLast() does not work for Oracle and probably others too
            //_readState.setWasLastResultRowRead(TimeOutUtil.callWithTimeout(() -> _resultSet.isLast()));
         }
         catch (Throwable e)
         {
            s_log.warn("Failed to call ResultSet.next() to detect if the last row read was the last row of the ResultSet. " +
                  "Limit rows display my be erroneous when number of rows exactly matches rows limit.", e);
            sessionLocal.setCallingIsLastFailed();
         }
      }

      closeStatementAndResultSet();
   }

   private ResultSetWrapperSessionLocal getResultSetWrapperSessionLocal()
   {
      ResultSetWrapperSessionLocal ret = (ResultSetWrapperSessionLocal) _statementCallback.getSession().getSessionLocal(ResultSetWrapper.class);
      if(null == ret)
      {
         ret = new ResultSetWrapperSessionLocal();
         _statementCallback.getSession().putSessionLocal(ResultSetWrapper.class, ret);
      }
      return ret;
   }

   private boolean isContinueReadActive()
   {
      if (null != _statementCallback && _statementCallback.isContinueReadActive())
      {
         return true;
      }
      return false;
   }

   public boolean next(BlockMode blockMode) throws SQLException
   {
      if(false == isContinueReadActive() )
      {
         if (null == _statementCallback || false == _statementCallback.isMaxRowsWasSet() || _readState.getCountRowsRead() <  _statementCallback.getMaxRowsCount())
         {
            return _nextOnResultSet();
         }
         else
         {
            return false;
         }
      }

      if (BlockMode.FIRST_BLOCK == blockMode)
      {
         boolean ret = _readState.getCountRowsRead() < _statementCallback.getFirstBlockCount() && _nextOnResultSet();

         if(false == ret)
         {
            _readState.setCountRowsRead(0);
         }

         return ret;
      }
      else if (BlockMode.FOLLOW_UP_BLOCK == blockMode)
      {
         _followUpBlockReached = true;

         boolean ret = _readState.getCountRowsRead() < _statementCallback.getContinueBlockCount() && _nextOnResultSet();

         if(false == ret)
         {
            _readState.setCountRowsRead(0);
         }

         return ret;
      }
      else if (BlockMode.INDIFFERENT == blockMode)
      {
         return _nextOnResultSet();
      }
      else
      {
         throw new IllegalStateException("Unknown BlockMode " + blockMode);
      }
   }

   private boolean _nextOnResultSet() throws SQLException
   {
      boolean ret = _resultSet.next();

      if(ret)
      {
         _readState.incCountRowsRead();
      }
      else
      {
         _readState.setResNextHasReturnedFalse(true);
         if (_followUpBlockReached)
         {
            // If continue read is not active the resultset will be closed by the SQLExecuterTask.
            closeStatementAndResultSet();
         }
      }
      return ret;
   }

   public boolean isAllResultsRead()
   {
      return _readState.isResNextHasReturnedFalse() || false == isContinueReadActive();
   }

   public void closeStatementAndResultSet()
   {
      try
      {
         SQLUtilities.closeResultSet(_resultSet);
      }
      finally
      {
         if (null != _statementCallback)
         {
            _statementCallback.closeStatementIfContinueReadActive();
         }
      }
   }

   public boolean areAllPossibleResultsOfSQLRead()
   {
      return _readState.isResNextHasReturnedFalse();
   }

   public boolean isResultLimitedByMaxRowsCount()
   {
      if(_readState.isResNextHasReturnedFalse())
      {
         // All results were read, so MaxRowsCount had no effect.
         return false;
      }

      return !_readState.wasLastResultRowRead();
   }
}
