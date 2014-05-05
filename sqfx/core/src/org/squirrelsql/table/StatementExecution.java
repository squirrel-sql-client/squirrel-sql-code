package org.squirrelsql.table;

import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.session.sql.SQLResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatementExecution
{
   private List<SQLResult> _sqlResults = new ArrayList<>();
   private long _executionTimeBegin;
   private long _executionTimeEnd;
   private long _processingResultsTimeBegin;
   private long _processinngResultsTimeEnd;

   public void addRes(SQLResult ... sqlResults)
   {
      _sqlResults.addAll(Arrays.asList(sqlResults));

   }
   public void addRes(List<SQLResult> sqlResults)
   {
      _sqlResults.addAll(sqlResults);
   }

   public void setExecutionTimeBegin(long executionTimeBegin)
   {
      _executionTimeBegin = executionTimeBegin;
   }

   public void setExecutionTimeEnd(long executionTimeEnd)
   {
      _executionTimeEnd = executionTimeEnd;
   }

   public void setProcessingResultsTimeBegin(long processingResultsTimeBegin)
   {
      _processingResultsTimeBegin = processingResultsTimeBegin;
   }

   public long getExecutionTime()
   {
      return _executionTimeEnd - _executionTimeBegin;
   }

   public void setProcessinngResultsTimeEnd(long processinngResultsTimeEnd)
   {
      _processinngResultsTimeEnd = processinngResultsTimeEnd;
   }

   public long getProcessinngResultsTime()
   {
      return _processinngResultsTimeEnd - _processingResultsTimeBegin;
   }

   public long getCompleteTime()
   {
      return getExecutionTime() + getProcessinngResultsTime();
   }

   public SQLException getFirstSqlException()
   {
      for (SQLResult sqlResult : _sqlResults)
      {
         if(null != sqlResult.getSqlException())
         {
            return sqlResult.getSqlException();
         }
      }

      return null;
   }

   public List<SQLResult> getQueryResults()
   {
      return CollectionUtil.filter(_sqlResults, (rs) -> null != rs.getResultTableLoader());
   }

   public List<SQLResult> getUpdateCounts()
   {
      return CollectionUtil.filter(_sqlResults, (rs) -> null != rs.getUpdateCount());
   }

   public int getBestQueryCount()
   {
      if(0 == getQueryResults().size())
      {
         return 0;
      }
      else
      {
         return getQueryResults().get(0).getResultTableLoader().size();
      }
   }
}
