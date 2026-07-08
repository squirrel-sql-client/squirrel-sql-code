package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import net.sourceforge.squirrel_sql.fw.props.Props;

public class McpUiPropsUtil
{
   private static final String PREF_APPLY_ALIASES_READ_ONLY_RULES = "McpBarCtrl.applyAliasesReadOnlyRules";
   private static final String PREF_APPROVE_ALL_AI_CALLS = "McpBarCtrl.approveAllAiCalls";
   private static final String PREF_ALLOW_ACCESS_FORM_LOCALHOST_ONLY = "McpBarCtrl.allowAccessFormLocalhostOnly";

   public static boolean isApproveAllAiCalls()
   {
      return Props.getBoolean(PREF_APPROVE_ALL_AI_CALLS, true);
   }

   public static boolean isApplyAliasesReadOnlyRules()
   {
      return Props.getBoolean(PREF_APPLY_ALIASES_READ_ONLY_RULES, true);
   }

   public static boolean isAllowAccessFormLocalhostOnly()
   {
      return Props.getBoolean(PREF_ALLOW_ACCESS_FORM_LOCALHOST_ONLY, true);
   }

   public static void setApplyAliasesReadOnlyRules(boolean b)
   {
      Props.putBoolean(PREF_APPLY_ALIASES_READ_ONLY_RULES, b);
   }

   public static void setApproveAllAiCalls(boolean b)
   {
      Props.putBoolean(PREF_APPROVE_ALL_AI_CALLS, b);
   }

   public static void setAllowAccessFormLocalhostOnly(boolean b)
   {
      Props.putBoolean(PREF_ALLOW_ACCESS_FORM_LOCALHOST_ONLY, b);
   }

   public static McpUiProps createMcpUiPropsInstance()
   {
      return
         new McpUiProps()
               .setApproveAllAiCalls(isApproveAllAiCalls())
               .setApplyAliasesReadOnlyRules(isApplyAliasesReadOnlyRules())
               .setAllowAccessFormLocalhostOnly(isAllowAccessFormLocalhostOnly());
   }

}
