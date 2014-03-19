package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;

public class ResultSetWrapper
{
   private final ResultSet _resultSet;
   private final StatementCallback _parent;
   private int _countRowsRead = 0;
   private boolean _resNextHasReturnedFalse;
   private boolean _followUpBlockReached;
   private boolean limitRead = false;

   public ResultSetWrapper(ResultSet resultSet, StatementCallback parent)
   {
      _resultSet = resultSet;
      _parent = parent;
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

      closeStatementAndResultSet();
   }

   private boolean isContinueReadActive()
   {
      if (null != _parent && _parent.isContinueReadActive())
      {
         return true;
      }
      return false;
   }

   public boolean next(BlockMode blockMode) throws SQLException
   {
      if(false == isContinueReadActive())
      {
         return _nextOnResultSet();
      }

      if (BlockMode.FIRST_BLOCK == blockMode)
      {
         boolean ret = _countRowsRead < _parent.getFirstBlockCount() && _nextOnResultSet();

         if(false == ret)
         {
            _countRowsRead = 0;
         }

         return ret;
      }
      else if (BlockMode.FOLLOW_UP_BLOCK == blockMode)
      {
         _followUpBlockReached = true;

         boolean ret = _countRowsRead < _parent.getContinueBlockCount() && _nextOnResultSet();

         if(false == ret)
         {
            _countRowsRead = 0;
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
         ++_countRowsRead;
      }
      else
      {
         _resNextHasReturnedFalse = true;
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
      return _resNextHasReturnedFalse || false == isContinueReadActive();
   }

   public void closeStatementAndResultSet()
   {
      try
      {
         //System.out.println("######ResultSetWrapper.closeStatementAndResultSet");
         SQLUtilities.closeResultSet(_resultSet);
      }
      finally
      {
         if (null != _parent)
         {
            _parent.closeStatementIfContinueReadActive();
         }
      }
   }

   public boolean areAllPossibleResultsOfSQLRead()
   {
      if(false == _resNextHasReturnedFalse)
      {
         return false;
      }

      if(null != _parent && _parent.isMaxRowsWasSet())
      {
         return !(_countRowsRead == _parent.getMaxRowsCount());
      }

      return true;
   }

	public boolean isLimitRead() {
		return limitRead;
	}
	
	public void setLimitRead(boolean limitRead) {
		this.limitRead = limitRead;
	}
   
   
}
