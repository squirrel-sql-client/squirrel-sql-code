package net.sourceforge.squirrel_sql.client.session;

import java.util.concurrent.CompletableFuture;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SqlPanelExecutionFuture
{
   private final static ILogger s_log = LoggerController.createLogger(SqlPanelExecutionFuture.class);

   public static final SqlPanelExecutionFuture EMPTY = new SqlPanelExecutionFuture();

   private CompletableFuture<ResultTab> result = new CompletableFuture<>();
   private String _vetoMsg;
   private boolean _emptySql;

   public String getVetoMsg()
   {
      return _vetoMsg;
   }

   public boolean isEmptySql()
   {
      return _emptySql;
   }

   public SqlPanelExecutionResult waitForSqlResult()
   {
      ResultTab sqlsResultTab = result.join();
      return new SqlPanelExecutionResult(sqlsResultTab);
   }

   public void setAddedResultTab(ResultTab tab)
   {
      result.complete(tab);
   }

   public void veto(String vetoMsg)
   {
      _vetoMsg = vetoMsg;
   }

   public void emptySql()
   {
      _emptySql = true;
   }
}
