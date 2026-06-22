package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;

public record McpServerContext(ISession session, AdditionalSQLTab mcpSqlTab)
{


   //
   // Just delegates to methods of McpUiProps
   public static boolean isApproveAllAiCalls()
   {
      return McpUiProps.isApproveAllAiCalls();
   }

   public static boolean isAllowJdbcExecuteQueryOnly()
   {
      return McpUiProps.isAllowJdbcExecuteQueryOnly();
   }

   public static boolean isApplyAliasesReadOnlyRules()
   {
      return McpUiProps.isApplyAliasesReadOnlyRules();
   }

   public static boolean isExecuteSqlViaDirectJdbcApi()
   {
      return McpUiProps.isExecuteSqlViaDirectJdbcApi();
   }

   public static boolean isAiQueryAsResTab()
   {
      return McpUiProps.isAiQueryAsResTab();
   }

}
