package net.sourceforge.squirrel_sql.client.session;

import java.util.concurrent.CompletableFuture;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SqlPanelExecutionFuture
{
   private final static ILogger s_log = LoggerController.createLogger(SqlPanelExecutionFuture.class);

   public static final SqlPanelExecutionFuture EMPTY = new SqlPanelExecutionFuture();

   private CompletableFuture<ResultTab> _result = new CompletableFuture<>();
   private String _vetoMsg;
   private boolean _emptySql;
   private String _errorMsg;
   private String _lastExecutedStatement;

   public SqlPanelExecutionResult waitForSqlResult()
   {
      ResultTab sqlsResultTab = _result.join();
      return new SqlPanelExecutionResult(sqlsResultTab, _vetoMsg, _emptySql, _errorMsg, _lastExecutedStatement);
   }

   public void setAddedResultTab(ResultTab tab)
   {
      _result.complete(tab);
   }

   public void setVeto(String vetoMsg)
   {
      _vetoMsg = vetoMsg;
      _result.complete(null);
   }

   public void setSqlEmptyError()
   {
      _emptySql = true;
      _result.complete(null);
   }

   public void setError(String errorMsg, String lastExecutedStatement)
   {
      _errorMsg = errorMsg;
      _lastExecutedStatement = lastExecutedStatement;
      _result.complete(null);
   }
}
