package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import net.sourceforge.squirrel_sql.fw.props.Props;

public class McpBarCtrl
{
   public static final String PREF_AI_QUERY_AS_RES_TAB = "McpBarCtrl.aiQueryAsResTab";
   public static final String PREF_AI_QUERY_VIA_JDBC = "McpBarCtrl.executeSqlViaDirectJdbcApi";

   public static final String PREF_APPLY_ALIASES_READ_ONLY_RULES = "McpBarCtrl.applyAliasesReadOnlyRules";
   public static final String PREF_ALLOW_JDBC_EXECUTE_QUERY_ONLY = "McpBarCtrl.allowJdbcExecuteQueryOnly";

   public static final String PREF_APPROVE_ALL_AI_CALLS = "McpBarCtrl.approveAllAiCalls";
   public static final String PREF_ALLOW_ACCESS_FORM_LOCALHOST_ONLY = "McpBarCtrl.allowAccessFormLocalhostOnly";


   private McpBarPanel _panel;

   public McpBarCtrl()
   {
      _panel = new McpBarPanel();

      _panel.radAiQueryAsResTab.setSelected(Props.getBoolean(PREF_AI_QUERY_AS_RES_TAB, true));
      _panel.radExecuteSqlViaDirectJdbcApi.setSelected(Props.getBoolean(PREF_AI_QUERY_VIA_JDBC, false));

      _panel.chkApplyAliasesReadOnlyRules.setSelected(Props.getBoolean(PREF_APPLY_ALIASES_READ_ONLY_RULES, true));
      _panel.chkAllowJdbcExecuteQueryOnly.setSelected(Props.getBoolean(PREF_ALLOW_JDBC_EXECUTE_QUERY_ONLY, true));

      _panel.chkApproveAllAiCalls.setSelected(Props.getBoolean(PREF_APPROVE_ALL_AI_CALLS, true));
      _panel.chkAllowAccessFormLocalhostOnly.setSelected(Props.getBoolean(PREF_ALLOW_ACCESS_FORM_LOCALHOST_ONLY, true));

      _panel.radAiQueryAsResTab.addActionListener(e -> onConfigChanged());
      _panel.radExecuteSqlViaDirectJdbcApi.addActionListener(e -> onConfigChanged());
      _panel.chkApplyAliasesReadOnlyRules.addActionListener(e -> onConfigChanged());
      _panel.chkAllowJdbcExecuteQueryOnly.addActionListener(e -> onConfigChanged());
      _panel.chkApproveAllAiCalls.addActionListener(e -> onConfigChanged());
      _panel.chkAllowAccessFormLocalhostOnly.addActionListener(e -> onConfigChanged());

      updateEnabled();
   }

   private void onConfigChanged()
   {
      Props.putBoolean(PREF_AI_QUERY_AS_RES_TAB, _panel.radAiQueryAsResTab.isSelected());
      Props.putBoolean(PREF_AI_QUERY_VIA_JDBC, _panel.radExecuteSqlViaDirectJdbcApi.isSelected());

      Props.putBoolean(PREF_APPLY_ALIASES_READ_ONLY_RULES, _panel.chkApplyAliasesReadOnlyRules.isSelected());
      Props.putBoolean(PREF_ALLOW_JDBC_EXECUTE_QUERY_ONLY, _panel.chkAllowJdbcExecuteQueryOnly.isSelected());

      Props.putBoolean(PREF_APPROVE_ALL_AI_CALLS, _panel.chkApproveAllAiCalls.isSelected());
      Props.putBoolean(PREF_ALLOW_ACCESS_FORM_LOCALHOST_ONLY, _panel.chkAllowAccessFormLocalhostOnly.isSelected());

      updateEnabled();
   }

   private void updateEnabled()
   {
      _panel.chkApplyAliasesReadOnlyRules.setEnabled(_panel.radAiQueryAsResTab.isSelected());
      _panel.chkAllowJdbcExecuteQueryOnly.setEnabled(_panel.radExecuteSqlViaDirectJdbcApi.isSelected());
   }

   public McpBarPanel getMcpBarPanel()
   {
      return _panel;
   }
}
