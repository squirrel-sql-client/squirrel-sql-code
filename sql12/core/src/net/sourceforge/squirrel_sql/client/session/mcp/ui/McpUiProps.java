package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import net.sourceforge.squirrel_sql.fw.props.Props;

public class McpUiProps
{
   private static final String PREF_AI_QUERY_AS_RES_TAB = "McpBarCtrl.aiQueryAsResTab";
   private static final String PREF_AI_QUERY_VIA_JDBC = "McpBarCtrl.executeSqlViaDirectJdbcApi";
   private static final String PREF_APPLY_ALIASES_READ_ONLY_RULES = "McpBarCtrl.applyAliasesReadOnlyRules";
   private static final String PREF_ALLOW_JDBC_EXECUTE_QUERY_ONLY = "McpBarCtrl.allowJdbcExecuteQueryOnly";
   private static final String PREF_APPROVE_ALL_AI_CALLS = "McpBarCtrl.approveAllAiCalls";
   private static final String PREF_ALLOW_ACCESS_FORM_LOCALHOST_ONLY = "McpBarCtrl.allowAccessFormLocalhostOnly";

   public static boolean isApproveAllAiCalls()
   {
      return Props.getBoolean(PREF_APPROVE_ALL_AI_CALLS, true);
   }

   public static boolean isAllowJdbcExecuteQueryOnly()
   {
      return Props.getBoolean(PREF_ALLOW_JDBC_EXECUTE_QUERY_ONLY, true);
   }

   public static boolean isApplyAliasesReadOnlyRules()
   {
      return Props.getBoolean(PREF_APPLY_ALIASES_READ_ONLY_RULES, true);
   }

   public static boolean isExecuteSqlViaDirectJdbcApi()
   {
      return Props.getBoolean(PREF_AI_QUERY_VIA_JDBC, false);
   }

   public static boolean isAiQueryAsResTab()
   {
      return Props.getBoolean(PREF_AI_QUERY_AS_RES_TAB, true);
   }

   public static boolean isAllowAccessFormLocalhostOnly()
   {
      return Props.getBoolean(PREF_ALLOW_ACCESS_FORM_LOCALHOST_ONLY, true);
   }


   public static void setAiQueryAsResTab(boolean b)
   {
      Props.putBoolean(PREF_AI_QUERY_AS_RES_TAB, b);
   }

   public static void setExecuteSqlViaDirectJdbcApi(boolean b)
   {
      Props.putBoolean(PREF_AI_QUERY_VIA_JDBC, b);
   }

   public static void setApplyAliasesReadOnlyRules(boolean b)
   {
      Props.putBoolean(PREF_APPLY_ALIASES_READ_ONLY_RULES, b);
   }

   public static void setAllowJdbcExecuteQueryOnly(boolean b)
   {
      Props.putBoolean(PREF_ALLOW_JDBC_EXECUTE_QUERY_ONLY, b);
   }

   public static void setApproveAllAiCalls(boolean b)
   {
      Props.putBoolean(PREF_APPROVE_ALL_AI_CALLS, b);
   }

   public static void setAllowAccessFormLocalhostOnly(boolean b)
   {
      Props.putBoolean(PREF_ALLOW_ACCESS_FORM_LOCALHOST_ONLY, b);
   }

}
