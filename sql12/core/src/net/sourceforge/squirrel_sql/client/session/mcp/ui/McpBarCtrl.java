package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SQLPanelEvent;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class McpBarCtrl
{
   private static final String PREF_AI_QUERY_AS_RES_TAB = "McpBarCtrl.aiQueryAsResTab";
   private static final String PREF_AI_QUERY_VIA_JDBC = "McpBarCtrl.executeSqlViaDirectJdbcApi";

   private static final String PREF_APPLY_ALIASES_READ_ONLY_RULES = "McpBarCtrl.applyAliasesReadOnlyRules";
   private static final String PREF_ALLOW_JDBC_EXECUTE_QUERY_ONLY = "McpBarCtrl.allowJdbcExecuteQueryOnly";

   private static final String PREF_APPROVE_ALL_AI_CALLS = "McpBarCtrl.approveAllAiCalls";
   private static final String PREF_ALLOW_ACCESS_FORM_LOCALHOST_ONLY = "McpBarCtrl.allowAccessFormLocalhostOnly";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(McpBarCtrl.class);
   private final static ILogger s_log = LoggerController.createLogger(McpBarCtrl.class);



   private final ISession _session;
   private AdditionalSQLTab _mcpSqlTab;

   private final McpBarPanel _panel;
   private ISQLPanelAdapter _mcpSqlTabListener;
   private boolean _inOnStartStopMcpServer = false;

   public McpBarCtrl(ISession session)
   {
      _session = session;
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

      _panel.btnStartStopMcpServer.addActionListener(e -> onStartStopMcpServer());

      _panel.btnCopyAiInfoPrompt.addActionListener(e -> onCopyAiInfoPrompt());
      _panel.btnSaveAiConfigMd.addActionListener(e -> onSaveAiConfigMd());

      updateEnabled();
   }

   private void onMcpSqlTabClosed(SQLPanelEvent evt)
   {
      if(_panel.btnStartStopMcpServer.isSelected())
      {
         _panel.btnStartStopMcpServer.doClick();
      }

      evt.getSourceSqlPanel().removeSQLPanelListener(_mcpSqlTabListener);
   }

   private void onStartStopMcpServer()
   {
      if(_inOnStartStopMcpServer)
      {
         return;
      }

      try
      {

         if(_panel.btnStartStopMcpServer.isSelected())
         {
            try
            {
               int port = Main.getApplication().getSessionMcpStateManager().getSessionMcpState(_session).getSessionsMcpPort();

               // TODO Start MCP-Server here

               _panel.txtMcpPort.setText("" + port);
               _panel.btnStartStopMcpServer.setText(_panel.getMcpServerToggleTextStop());
               _panel.btnCopyAiInfoPrompt.setEnabled(true);

               String msg = s_stringMgr.getString("McpBarCtrl.message.mcp.started.for.session", _panel.txtMcpPort.getText(), _session.getTitle());
               Main.getApplication().getMessageHandler().showMessage(msg);
               s_log.info(msg);

            }
            catch(Throwable e)
            {
               String msg = s_stringMgr.getString("McpBarCtrl.message.mcp.start.failed.for.session", _session.getTitle());
               Main.getApplication().getMessageHandler().showErrorMessage(msg, e);
               s_log.error(msg, e);

               _panel.btnStartStopMcpServer.setSelected(false);
               _panel.btnStartStopMcpServer.setText(_panel.getMcpServerToggleTextStart());
               _panel.txtMcpPort.setText(null);
               _panel.btnCopyAiInfoPrompt.setEnabled(false);
               throw Utilities.wrapRuntime(e);
            }
         }
         else
         {
            try
            {
               // TODO Stop MCP-Server here

               String msg = s_stringMgr.getString("McpBarCtrl.message.mcp.stopped.for.session", _panel.txtMcpPort.getText(), _session.getTitle());
               Main.getApplication().getMessageHandler().showMessage(msg);
               s_log.info(msg);
            }
            finally
            {
               _panel.btnStartStopMcpServer.setSelected(false);
               _panel.btnStartStopMcpServer.setText(_panel.getMcpServerToggleTextStart());
               _panel.txtMcpPort.setText(null);
               _panel.btnCopyAiInfoPrompt.setEnabled(false);
            }
         }
      }
      finally
      {
         _inOnStartStopMcpServer = false;
      }
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
      _panel.btnCopyAiInfoPrompt.setEnabled(_panel.btnStartStopMcpServer.isSelected());
   }

   public McpBarPanel getMcpBarPanel()
   {
      return _panel;
   }

   public void setMcpSqlTab(AdditionalSQLTab mcpSqlTab)
   {
      _mcpSqlTab = mcpSqlTab;

      _mcpSqlTabListener = new ISQLPanelAdapter()
      {
         @Override
         public void sqlEntryAreaClosed(SQLPanelEvent evt)
         {
            onMcpSqlTabClosed(evt);
         }
      };
      _mcpSqlTab.getSQLPanelAPI().addSQLPanelListener(_mcpSqlTabListener);

   }

   private void onCopyAiInfoPrompt()
   {
      System.out.println("McpBarCtrl.onCopyAiInfoPrompt");
      // TODO AI: Implement
   }

   private void onSaveAiConfigMd()
   {
      System.out.println("McpBarCtrl.onSaveAiConfigMd");
      // TODO AI: In own dialog: Write file and offer to copy the prompt that makes AI read the file.
   }

}
