package org.squirrelsql.table;

import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.CollectionUtil;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.SQLResult;
import org.squirrelsql.session.StatementChannel;
import org.squirrelsql.session.StatementExecutionState;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class TableLoaderFactory
{
   public static TableLoader loadDataFromResultSet(ResultSet res, String ... excludeColNames)
   {
      return _loadDataFromResultSet(res, new StatementChannel(), excludeColNames);
   }
   private static TableLoader _loadDataFromResultSet(ResultSet res, StatementChannel statementChannel, String ... excludeColNames)
   {
      try
      {
         TableLoader tableLoader = new TableLoader();

         ResultSetMetaData metaData = res.getMetaData();
         for(int i=1; i <= metaData.getColumnCount(); ++i)
         {
            final String colName = metaData.getColumnName(i);
            if (false == CollectionUtil.contains(excludeColNames, (t) -> colName.equalsIgnoreCase(t)))
            {
               tableLoader.addColumn(colName);
            }
         }

         while(res.next())
         {
            ArrayList row = new ArrayList();

            for(int i=1; i <= metaData.getColumnCount(); ++i)
            {
               final String colName = metaData.getColumnName(i);

               if(statementChannel.isCanceled())
               {
                  new MessageHandler(TableLoaderFactory.class, MessageHandlerDestination.MESSAGE_PANEL).warning("Building output was canceled. Result is not complete.");
                  return tableLoader;
               }

               if (false == CollectionUtil.contains(excludeColNames, (t) -> colName.equalsIgnoreCase(t)))
               {
                  row.add(res.getObject(i));
               }
            }

            tableLoader.addRow(row);
         }

         return tableLoader;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

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

         }
         catch (SQLException e)
         {
            statementChannel.setStatementExecutionState(StatementExecutionState.ERROR);
            return new SQLResult(e);
         }
         statementChannel.setStatementExecutionState(StatementExecutionState.BUILDING_OUTPUT);

         buildingOutputTimeBegin = System.currentTimeMillis();
         TableLoader tableLoader = _loadDataFromResultSet(res, statementChannel);
         buildingOutputTimeEnd = System.currentTimeMillis();
         statementChannel.setStatementExecutionState(StatementExecutionState.FINSHED);

         return new SQLResult(tableLoader, executionTimeEnd - executionTimeBegin, buildingOutputTimeEnd - buildingOutputTimeBegin);
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
