package org.squirrelsql.table;

import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.SQLResult;
import org.squirrelsql.session.StatementChannel;
import org.squirrelsql.session.StatementExecutionState;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SQLExecutor
{
   public static StatementExecution processQuery(DbConnectorResult dbConnectorResult, String sql, Integer maxResults, StatementChannel statementChannel)
   {
      Statement stat = null;
      try
      {
         stat = dbConnectorResult.getSQLConnection().getConnection().createStatement();

         if (null != maxResults)
         {
            stat.setMaxRows(maxResults);
         }

         StatementExecution ret = new StatementExecution();
         try
         {
            statementChannel.setStatementExecutionState(StatementExecutionState.EXECUTING);

            ret.setExecutionTimeBegin(System.currentTimeMillis());
            statementChannel.setCancelCandidate(stat);
            boolean firstResultIsResultSet = stat.execute(sql);
            statementChannel.setCancelCandidate(null);
            ret.setExecutionTimeEnd(System.currentTimeMillis());


            if(statementChannel.isCanceled())
            {
               ret.addRes(new SQLResult(new SQLException("Statement canceled while executing")));
            }
            statementChannel.setStatementExecutionState(StatementExecutionState.PROCESSING_RESULTS);

            ret.setProcessingResultsTimeBegin(System.currentTimeMillis());
            ret.addRes(processResults(stat, firstResultIsResultSet, dbConnectorResult, statementChannel, maxResults));
            ret.setProcessinngResultsTimeEnd(System.currentTimeMillis());
            statementChannel.setStatementExecutionState(StatementExecutionState.FINSHED);

            return ret;
         }
         catch (SQLException e)
         {
            statementChannel.setStatementExecutionState(StatementExecutionState.ERROR);

            ret.addRes(new SQLResult(e));
            return ret;
         }
      }
      catch (Throwable e)
      {
         statementChannel.setStatementExecutionState(StatementExecutionState.ERROR);
         throw new RuntimeException(e);
      }
      finally
      {
         Utils.close(stat);
      }
   }

   private static ArrayList<SQLResult> processResults(Statement stat, boolean firstResultIsResultSet, DbConnectorResult dbConnectorResult, StatementChannel statementChannel, Integer maxResults) throws SQLException
   {
      ResultSet res = null;

      ArrayList<SQLResult> results = new ArrayList<>();

      boolean supportsMultipleResultSets = dbConnectorResult.getSQLConnection().getDatabaseMetaData().supportsMultipleResultSets();
      boolean inFirstLoop = true;

      // Loop while we either have a ResultSet to process or rows have
      // been updated/inserted/deleted.
      while (true)
      {
         // User has cancelled the query execution.
         if (statementChannel.isCanceled())
         {
            break;
         }

         int updateCount = stat.getUpdateCount();

         res = null;
         if (inFirstLoop && firstResultIsResultSet)
         {
            res = stat.getResultSet();
         }
         else if(false == inFirstLoop)
         {
            res = stat.getResultSet();
         }

         if (-1 != updateCount)
         {
            results.add(new SQLResult(updateCount));
         }

         if (null != res)
         {
            boolean moreResultsReceived = false;
            while(true)
            {

               TableLoader resultTableLoader = TableLoaderFactory.loadDataFromResultSet(res, statementChannel);
               TableLoader resultMetaDataTableLoader = ResultSetMetaDataLoader.loadMetaData(res);


               results.add(new SQLResult(resultTableLoader, resultMetaDataTableLoader, maxResults));

               if(statementChannel.isCanceled())
               {
                  break;
               }

               // Each call to _stmt.getMoreResults() places the to the next output.
               // As long as it is a ResultSet, we process it ...
               if(supportsMultipleResultSets && stat.getMoreResults())
               {
                  res = stat.getResultSet();
                  moreResultsReceived = true;
               }
               else
               {
                  break;
               }
            }

            if (moreResultsReceived)
            {
               // ... now we have reached an output that is not a result. We now have to ask for this
               // outputs update count - but only if we received more results.
               updateCount = stat.getUpdateCount();
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


         if (!stat.getMoreResults() && -1 == updateCount)
         {
            // There is no need to close result sets if we call _stmt.getMoreResults() because it
            // implicitly closes any current ResultSet.
            // ON DB2 version 7.1 it is even harmful to close a ResultSet explicitly.
            // _stmt.getMoreResults() will never return true anymore if you do.
            break;
         }
         inFirstLoop = false;

      }

      return results;
   }

}
