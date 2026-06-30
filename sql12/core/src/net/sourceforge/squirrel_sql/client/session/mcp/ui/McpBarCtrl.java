package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SQLPanelEvent;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.client.session.mcp.server.SquirrelMcpHttpServer;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class McpBarCtrl
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(McpBarCtrl.class);
   private final static ILogger s_log = LoggerController.createLogger(McpBarCtrl.class);

   private final ISession _session;
   private AdditionalSQLTab _mcpSqlTab;

   private final McpBarPanel _panel;
   private ISQLPanelAdapter _mcpSqlTabListener;
   private boolean _inOnStartStopMcpServer = false;
   private SquirrelMcpHttpServer _squirrelMcpHttpServer;

   public McpBarCtrl(ISession session)
   {
      _session = session;
      _panel = new McpBarPanel();

      _panel.chkApplyAliasesReadOnlyRules.setSelected(McpUiProps.isApplyAliasesReadOnlyRules());

      _panel.chkApproveAllAiCalls.setSelected(McpUiProps.isApproveAllAiCalls());
      _panel.chkAllowAccessFormLocalhostOnly.setSelected(McpUiProps.isAllowAccessFormLocalhostOnly());

      _panel.chkApplyAliasesReadOnlyRules.addActionListener(e -> onConfigChanged());
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

               _squirrelMcpHttpServer = new SquirrelMcpHttpServer();

               _squirrelMcpHttpServer.start(port, new McpServerContext(_session, _mcpSqlTab));

               _panel.txtMcpPort.setText("" + port);
               _panel.btnStartStopMcpServer.setText(_panel.getMcpServerToggleTextStop());
               _panel.btnCopyAiInfoPrompt.setEnabled(true);

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
               _panel.btnCopyAiInfoPrompt.setEnabled(false);
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
               _panel.btnCopyAiInfoPrompt.setEnabled(false);
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
      McpUiProps.setApplyAliasesReadOnlyRules(_panel.chkApplyAliasesReadOnlyRules.isSelected());
      McpUiProps.setApproveAllAiCalls(_panel.chkApproveAllAiCalls.isSelected());
      McpUiProps.setAllowAccessFormLocalhostOnly(_panel.chkAllowAccessFormLocalhostOnly.isSelected());

      updateEnabled();
   }

   private void updateEnabled()
   {
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
