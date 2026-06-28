package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;

public class SqlPanelExecutionResult
{
   private final ResultTab _sqlsResultTab;

   public SqlPanelExecutionResult(ResultTab sqlsResultTab)
   {
      _sqlsResultTab = sqlsResultTab;
   }

   public ResultTab getSqlResultTab()
   {
      return _sqlsResultTab;
   }
}
