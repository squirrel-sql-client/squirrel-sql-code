package org.squirrelsql.session.sql.features;

import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.progress.Progressible;
import org.squirrelsql.session.sql.StatementChannel;
import org.squirrelsql.table.SQLExecutor;
import org.squirrelsql.table.StatementExecution;

public class SqlToTableHelper
{
   public static void exportToTable(Progressible progressible, DbConnectorResult dbConnectorResult, String sql, String tableName)
   {

      int stepMax = 5;

      int step = 0;
      progressible.update("Start executing SQL", step++, stepMax);

      StatementChannel statementChannel = new StatementChannel();
      statementChannel.setStateChannelListener(statementExecutionState -> progressible.update("   " + "SQL execution state: " + statementExecutionState));
      statementChannel.setFireStateChangesToEventQueue(false);

      progressible.setCancelCallback(() -> cancelStatementIfRunning(statementChannel));
      StatementExecution statementExecution = SQLExecutor.processQuery(dbConnectorResult, sql, 1, statementChannel);


      if(0 == statementExecution.getQueryResults().size())
      {
         progressible.update("Statement canceled");
         sleep();

         return;
      }

      progressible.update("Finished executing SQL", step++, stepMax);
      sleep();

      statementExecution.getQueryResults().get(0);


      progressible.update("Start analyzing result executing SQL", step++, stepMax);
      sleep();

      if(progressible.isCancelled())
      {
         return;
      }


      progressible.update("Start creating table", step++, stepMax);
      sleep();


      if(progressible.isCancelled())
      {
         return;
      }

      progressible.update("Start writing table", step++, stepMax);
      sleep();

      progressible.update(step++, stepMax);

   }

   private static void cancelStatementIfRunning(StatementChannel statementChannel)
   {
      statementChannel.cancelStatement();
   }

   private static void sleep()
   {
      try
      {
         Thread.sleep(500);
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }

}
