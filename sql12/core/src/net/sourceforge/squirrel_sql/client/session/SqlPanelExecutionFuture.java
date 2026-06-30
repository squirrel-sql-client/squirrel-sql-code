package net.sourceforge.squirrel_sql.client.session;

import java.util.concurrent.CompletableFuture;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SqlPanelExecutionFuture
{
   private final static ILogger s_log = LoggerController.createLogger(SqlPanelExecutionFuture.class);

   public static final SqlPanelExecutionFuture NULL_FUTURE = new SqlPanelExecutionFuture();

   private final CompletableFuture<ResultTab> _result = new CompletableFuture<>();
   private String _vetoMsg;
   private boolean _emptySql;
   private String _errorMsg;
   private String _lastExecutedStatement;
   private boolean _canceled;
   private String _updateMessage;
   private boolean _touched;

   public SqlPanelExecutionResult waitForSqlResult()
   {
      if(this == NULL_FUTURE)
      {
         throw new IllegalStateException("Cannot wait for NULL_FUTURE");
      }

      ResultTab sqlsResultTab = _result.join();
      return new SqlPanelExecutionResult(sqlsResultTab, _vetoMsg, _emptySql, _errorMsg, _lastExecutedStatement, _canceled, _updateMessage);
   }

   public void setAddedResultTab(ResultTab tab)
   {
      if(this == NULL_FUTURE)
      {
         return;
      }
      _touched = true;

      _result.complete(tab);
   }

   public void setVeto(String vetoMsg)
   {
      if(this == NULL_FUTURE)
      {
         return;
      }
      _touched = true;

      _vetoMsg = vetoMsg;
      _result.complete(null);
   }

   public void setSqlEmptyError()
   {
      if(this == NULL_FUTURE)
      {
         return;
      }
      _touched = true;

      _emptySql = true;
      _result.complete(null);
   }

   public void setError(String errorMsg, String lastExecutedStatement)
   {
      if(this == NULL_FUTURE)
      {
         return;
      }
      _touched = true;


      _errorMsg = errorMsg;
      _lastExecutedStatement = lastExecutedStatement;
      _result.complete(null);
   }

   public void setCanceled()
   {
      if(this == NULL_FUTURE)
      {
         return;
      }
      _touched = true;


      _canceled = true;
      _result.complete(null);
   }

   public void setUpdateMessage(String updateMessage)
   {
      if(this == NULL_FUTURE)
      {
         return;
      }
      _touched = true;


      _updateMessage = updateMessage;
      _result.complete(null);
   }

   public boolean isNullFuture()
   {
      return NULL_FUTURE == this;
   }

   public void sqlExecutionFinished()
   {
      if(this == NULL_FUTURE)
      {
         return;
      }

      if(false == _touched)
      {
         // Last resort to keep the thread from getting stuck.
         _errorMsg = "INTERNAL ERROR: The execution lead to no result";
         s_log.error(new IllegalStateException(_errorMsg));
         _result.complete(null);
      }

   }
}
