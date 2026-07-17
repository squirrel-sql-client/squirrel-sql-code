package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SQLPanelEvent;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.client.session.mcp.server.SquirrelMcpHttpServer;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

public class McpBarCtrl
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(McpBarCtrl.class);
   private final static ILogger s_log = LoggerController.createLogger(McpBarCtrl.class);

   private final ISession _session;
   private AdditionalSQLTab _mcpSqlTab;
   private final McpUiProps _mcpUiProps;

   private final McpBarPanel _panel;
   private ISQLPanelAdapter _mcpSqlTabListener;
   private boolean _inOnStartStopMcpServer = false;
   private SquirrelMcpHttpServer _squirrelMcpHttpServer;

   public McpBarCtrl(ISession session)
   {
      _session = session;
      _panel = new McpBarPanel();

      _mcpUiProps = McpUiPropsUtil.createMcpUiPropsInstance();

      _panel.chkApplyAliasesReadOnlyRules.setSelected(_mcpUiProps.isApplyAliasesReadOnlyRules());

      _panel.chkApproveAllAiCalls.setSelected(_mcpUiProps.isApproveAllAiCalls());
      _panel.chkAllowAccessFormLocalhostOnly.setSelected(_mcpUiProps.isAllowAccessFormLocalhostOnly());

      _panel.chkApplyAliasesReadOnlyRules.addActionListener(e -> onConfigChanged());
      _panel.chkApproveAllAiCalls.addActionListener(e -> onConfigChanged());
      _panel.chkAllowAccessFormLocalhostOnly.addActionListener(e -> onConfigChanged());

      _panel.btnStartStopMcpServer.addActionListener(e -> onStartStopMcpServer());
      _panel.btnCopyAiInfoPrompt.addActionListener(e -> onCopyAiInfoPrompt());

      _panel.btnSaveAiConfigMd.addActionListener(e -> onSaveAiConfigMd());

      _panel.btnMcpInfo.addActionListener(e -> onShowInfoDialog());
      updateEnabled();
   }

   private void onShowInfoDialog()
   {
      new McpAiInfoDlg(GUIUtils.getOwningFrame(_panel));
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

               _squirrelMcpHttpServer = new SquirrelMcpHttpServer();

               _squirrelMcpHttpServer.start(port, new McpServerContext(_session, _mcpSqlTab, _mcpUiProps));

               _panel.txtMcpPort.setText("" + port);
               _panel.btnStartStopMcpServer.setText(_panel.getMcpServerToggleTextStop());
               updateEnabled();

               String msg = s_stringMgr.getString("McpBarCtrl.message.mcp.started.for.session", _panel.txtMcpPort.getText(), _session.getTitle());
               Main.getApplication().getMessageHandler().showMessage(msg);
               s_log.info(msg);

            }
            catch(Throwable e)
            {
               stopHttpServer(true);
               String msg = s_stringMgr.getString("McpBarCtrl.message.mcp.start.failed.for.session", _session.getTitle());
               Main.getApplication().getMessageHandler().showErrorMessage(msg, e);
               s_log.error(msg, e);

               _panel.btnStartStopMcpServer.setSelected(false);
               _panel.btnStartStopMcpServer.setText(_panel.getMcpServerToggleTextStart());
               _panel.txtMcpPort.setText(null);
               updateEnabled();
               throw Utilities.wrapRuntime(e);
            }
         }
         else
         {
            try
            {
               stopHttpServer(false);
               String msg = s_stringMgr.getString("McpBarCtrl.message.mcp.stopped.for.session", _panel.txtMcpPort.getText(), _session.getTitle());
               Main.getApplication().getMessageHandler().showMessage(msg);
               s_log.info(msg);
            }
            finally
            {
               _panel.btnStartStopMcpServer.setSelected(false);
               _panel.btnStartStopMcpServer.setText(_panel.getMcpServerToggleTextStart());
               _panel.txtMcpPort.setText(null);
               updateEnabled();
            }
         }
      }
      finally
      {
         _inOnStartStopMcpServer = false;
      }
   }

   private void stopHttpServer(boolean silently)
   {
      if(null != _squirrelMcpHttpServer)
      {
         try
         {
            _squirrelMcpHttpServer.stop();
         }
         catch(Throwable e)
         {
            if(false == silently)
            {
               String msg = s_stringMgr.getString("McpBarCtrl.warn.mcp.server.stop.failed", _squirrelMcpHttpServer.getPort(), _session.getTitle());
               Main.getApplication().getMessageHandler().showWarningMessage(msg, e);
            }
         }
      }
   }

   private void onConfigChanged()
   {
      _mcpUiProps.setApplyAliasesReadOnlyRules(_panel.chkApplyAliasesReadOnlyRules.isSelected());
      McpUiPropsUtil.setApplyAliasesReadOnlyRules(_panel.chkApplyAliasesReadOnlyRules.isSelected());

      _mcpUiProps.setApproveAllAiCalls(_panel.chkApproveAllAiCalls.isSelected());
      McpUiPropsUtil.setApproveAllAiCalls(_panel.chkApproveAllAiCalls.isSelected());

      _mcpUiProps.setAllowAccessFormLocalhostOnly(_panel.chkAllowAccessFormLocalhostOnly.isSelected());
      McpUiPropsUtil.setAllowAccessFormLocalhostOnly(_panel.chkAllowAccessFormLocalhostOnly.isSelected());

      updateEnabled();
   }

   private void updateEnabled()
   {
      _panel.btnCopyAiInfoPrompt.setEnabled(_panel.btnStartStopMcpServer.isSelected());
      _panel.chkAllowAccessFormLocalhostOnly.setEnabled(!_panel.btnStartStopMcpServer.isSelected());
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
      int sessionMcpPort = Main.getApplication().getSessionMcpStateManager().getSessionMcpState(_session).getSessionsMcpPort();

      // No I18n, AIs speak English.
      String aiMsg =
            """
            A SQuirreL SQL MCP server is now running on port %d. \
            Its endpoint is http://127.0.0.1:%d/squirrel-sql-mcp and it speaks JSON-RPC 2.0 over HTTP POST, \
            as described in the SQuirreL AI configuration you imported earlier. To confirm the connection works, \
            call the MCP tool getSessionName (it takes no arguments) and tell me the SQuirreL session name it returns.
            """.formatted(sessionMcpPort, sessionMcpPort);

      ClipboardUtil.copyToClip(StringUtils.trim(aiMsg));

      Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("McpBarCtrl.copyAiInfoPrompt.msg", aiMsg));
   }

   private void onSaveAiConfigMd()
   {
      AiConfigMdWriter.saveAiConfigMd(_panel);
   }

}
