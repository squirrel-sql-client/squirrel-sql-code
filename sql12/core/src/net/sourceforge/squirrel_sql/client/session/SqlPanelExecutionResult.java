package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;
import org.apache.commons.lang3.StringUtils;

public record SqlPanelExecutionResult(ResultTab sqlResultTab, String vetoMsg, boolean emptySql, String errorMsg, String lastExecutedStatement)
{
   public boolean hasError()
   {
      return false == StringUtils.isBlank(errorMsg) || false == StringUtils.isBlank(vetoMsg) || emptySql;
   }

   public String composeErrorMessage()
   {
      String ret = "";

      if( emptySql )
      {
         ret += "No SQL statement supplied.";
      }

      if( false == StringUtils.isBlank(vetoMsg))
      {
         ret += "Veto: " + vetoMsg;
      }

      if( false == StringUtils.isBlank(errorMsg))
      {
         ret += "Error message: " + errorMsg;
      }

      return ret;
   }
}
