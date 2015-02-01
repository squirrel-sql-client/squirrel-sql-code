package org.squirrelsql.session.sql.features;

import org.squirrelsql.AppState;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.progress.Progressable;
import org.squirrelsql.session.sql.SQLResult;
import org.squirrelsql.session.sql.StatementChannel;
import org.squirrelsql.table.SQLExecutor;
import org.squirrelsql.table.StatementExecution;
import org.squirrelsql.table.tableedit.SQLResultMetaDataFacade;

import java.util.ArrayList;
import java.util.List;

public class SqlToTable
{

   public enum TableExistsOption
   {
      DROP,
      APPEND,
      DO_NOTHING

   }

   private final Progressable _progressable;
   private final DbConnectorResult _dbConnectorResult;

   private String _script;

   public SqlToTable(Progressable progressable, DbConnectorResult dbConnectorResult)
   {
      _progressable = progressable;
      _dbConnectorResult = dbConnectorResult;
   }

   public void exportToTable(String sql, String tableName, TableExistsOption opt, boolean scriptOnly)
   {

      I18n i18n = new I18n(SqlToTable.class);

      ArrayList<SQLAndMessage> sqlsToExecute = new ArrayList<>();

      int stepCountFixed = 4;
      int stepCountEstimated = 2;

      int step = 0;
      _progressable.update(i18n.t("SqlToTableHelper.StartExecutingSQL"), step++, stepCountFixed + stepCountEstimated);

      StatementExecution statementExecution = executeSql(_progressable, _dbConnectorResult, sql);


      if(0 == statementExecution.getQueryResults().size())
      {
         _progressable.update(i18n.t("SqlToTableHelper.noQueryResult"));
         return;
      }

      _progressable.update(i18n.t("SqlToTableHelper.FinishedExecutingSQL"), step++, stepCountFixed + stepCountEstimated);

      SQLResult sqlResult = statementExecution.getQueryResults().get(0);



      if(_progressable.isCancelled())
      {
         return;
      }


      _progressable.update(i18n.t("SqlToTableHelper.StartAnalyzing"), step++, stepCountFixed + stepCountEstimated);
      SQLResultMetaDataFacade metaDataFacade = sqlResult.getResultMetaDataTableLoaderFacade();

      SQLToTableHelper sqlToTableHelper = new SQLToTableHelper(tableName, metaDataFacade);


      SQLResultMetaDataFacade metaDataOfAlreadyExisitingTable = getMetaDataOfAlreadyExitingTable(_dbConnectorResult, tableName);

      if(null == metaDataOfAlreadyExisitingTable)
      {
         sqlsToExecute.add(new SQLAndMessage(i18n.t("SqlToTableHelper.StartCreatingTable", tableName), sqlToTableHelper.getCreateSql()));
      }
      else
      {
         if(opt == TableExistsOption.DO_NOTHING)
         {
            _progressable.postShouldReadMessage(i18n.t("SqlToTableHelper.tableExistsDoNothing", tableName));
            return;
         }
         else if(opt == TableExistsOption.DROP)
         {
            sqlsToExecute.add(new SQLAndMessage(i18n.t("SqlToTableHelper.StartDroppingTable", tableName), "DROP TABLE " + tableName));
            sqlsToExecute.add(new SQLAndMessage(i18n.t("SqlToTableHelper.StartCreatingTable", tableName), sqlToTableHelper.getCreateSql()));
         }
         else if(opt == TableExistsOption.APPEND)
         {
            if(false == sqlToTableHelper.columnsMatch(metaDataOfAlreadyExisitingTable))
            {
               _progressable.postShouldReadMessage(i18n.t("SqlToTableHelper.tableExistsColumnsDontMatchDoNothing", tableName));
               return;
            }
         }
      }

      sqlsToExecute.add(new SQLAndMessage(i18n.t("SqlToTableHelper.StartWritingTableData", tableName), sqlToTableHelper.getInsertSql(tableName, sql)));

      if(_progressable.isCancelled())
      {
         return;
      }

      if (scriptOnly)
      {

         _progressable.update(i18n.t("SqlToTableHelper.ScriptGenerationSuccess", tableName), step++, stepCountFixed + stepCountEstimated);
      }
      else
      {
         stepCountEstimated = sqlsToExecute.size();
         for (SQLAndMessage sqlAndMessage : sqlsToExecute)
         {
            if(_progressable.isCancelled())
            {
               return;
            }
            _progressable.update(sqlAndMessage.getMsg(), step++, stepCountFixed + stepCountEstimated);

            executeSql(_progressable, _dbConnectorResult, sqlAndMessage.getSql());
         }

         _progressable.update(i18n.t("SqlToTableHelper.TableWriteSuccess", tableName), step++, stepCountFixed + stepCountEstimated);
      }

      _script = "";

      for (SQLAndMessage sqlAndMessage : sqlsToExecute)
      {
         String sep = AppState.get().getSettingsManager().getSettings().getStatementSeparator();

         if (1 < sep.length())
         {
            _script += sqlAndMessage.getSql() + "\n" + sep + "\n";
         }
         else
         {
            _script += sqlAndMessage.getSql() + sep + "\n";
         }
      }

   }

   public String getScript()
   {
      return _script;
   }

   private StatementExecution executeSql(Progressable progressable, DbConnectorResult dbConnectorResult, String sql)
   {
      StatementChannel statementChannel = new StatementChannel();
      statementChannel.setStateChannelListener(statementExecutionState -> progressable.update("   " + "SQL execution state: " + statementExecutionState));
      statementChannel.setFireStateChangesToEventQueue(false);

      progressable.setCancelCallback(() -> cancelStatementIfRunning(statementChannel));
      return SQLExecutor.processQuery(dbConnectorResult, sql, 1, statementChannel);
   }

   private SQLResultMetaDataFacade getMetaDataOfAlreadyExitingTable(DbConnectorResult dbConnectorResult, String tableName)
   {
      List<SQLResult> queryResults = SQLExecutor.processQuery(dbConnectorResult, "SELECT * FROM " + tableName, 1, new StatementChannel()).getQueryResults();

      if(0 < queryResults.size())
      {
         return queryResults.get(0).getResultMetaDataTableLoaderFacade();
      }

      return null;
   }


   private void cancelStatementIfRunning(StatementChannel statementChannel)
   {
      statementChannel.cancelStatement();
   }

   private static class SQLAndMessage
   {

      private final String _msg;
      private final String _sql;

      public SQLAndMessage(String msg, String sql)
      {
         _msg = msg;
         _sql = sql;
      }

      public String getMsg()
      {
         return _msg;
      }

      public String getSql()
      {
         return _sql;
      }
   }

}
