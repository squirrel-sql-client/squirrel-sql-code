package org.squirrelsql.table;

import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.SQLResult;
import org.squirrelsql.session.StatementChannel;
import org.squirrelsql.session.StatementExecutionState;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLExecutor
{
   public static SQLResult loadDataFromSQL(DbConnectorResult dbConnectorResult, String sql, Integer maxResults, StatementChannel statementChannel)
   {
      Statement stat = null;
      ResultSet res = null;
      try
      {
         stat = dbConnectorResult.getSQLConnection().getConnection().createStatement();

         if (null != maxResults)
         {
            stat.setMaxRows(maxResults);
         }

         long executionTimeBegin;
         long executionTimeEnd;
         long buildingOutputTimeBegin;
         long buildingOutputTimeEnd;

         try
         {
            statementChannel.setStatementExecutionState(StatementExecutionState.EXECUTING);

            statementChannel.setCancelCandidate(stat);
            executionTimeBegin = System.currentTimeMillis();
            res = stat.executeQuery(sql);
            executionTimeEnd = System.currentTimeMillis();
            statementChannel.setCancelCandidate(null);

            if(statementChannel.isCanceled())
            {
               return new SQLResult(new SQLException("Statement canceled while executing"));
            }
            statementChannel.setStatementExecutionState(StatementExecutionState.BUILDING_OUTPUT);

            buildingOutputTimeBegin = System.currentTimeMillis();
            TableLoader tableLoader = TableLoaderFactory.loadDataFromResultSet(res, statementChannel);
            buildingOutputTimeEnd = System.currentTimeMillis();
            statementChannel.setStatementExecutionState(StatementExecutionState.FINSHED);

            return new SQLResult(tableLoader, executionTimeEnd - executionTimeBegin, buildingOutputTimeEnd - buildingOutputTimeBegin);
         }
         catch (SQLException e)
         {
            statementChannel.setStatementExecutionState(StatementExecutionState.ERROR);
            return new SQLResult(e);
         }
      }
      catch (Throwable e)
      {
         statementChannel.setStatementExecutionState(StatementExecutionState.ERROR);
         throw new RuntimeException(e);
      }
      finally
      {
         Utils.close(res);
         Utils.close(stat);
      }
   }
}
