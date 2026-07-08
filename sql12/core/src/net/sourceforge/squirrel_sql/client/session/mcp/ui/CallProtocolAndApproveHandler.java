package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.client.session.mcp.server.McpCall;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class CallProtocolAndApproveHandler
{
   public final static ILogger s_log = LoggerController.createLogger(CallProtocolAndApproveHandler.class);

   private final ISession _session;
   private final AdditionalSQLTab _mcpSqlTab;
   private final McpUiProps _mcpUiProps;

   public CallProtocolAndApproveHandler(ISession session, AdditionalSQLTab mcpSqlTab, McpUiProps mcpUiProps)
   {
      _session = session;
      _mcpSqlTab = mcpSqlTab;
      _mcpUiProps = mcpUiProps;
   }

   public boolean callStart(McpCall call, Object callArgs)
   {
      return GUIUtils.callOnSwingEventThread(() -> _callStart(call, callArgs), true);
   }

   private boolean _callStart(McpCall call, Object callArgs)
   {
      String callString = call.createCallString(callArgs);

      if(_mcpUiProps.isApproveAllAiCalls())
      {
         McpCallApproveCtrl mcpCallApproveCtrl = new McpCallApproveCtrl(callString, _mcpUiProps);

         if(false == mcpCallApproveCtrl.isApproved())
         {
            return false;
         }
      }

      _mcpSqlTab.getSQLPanelAPI().appendSQLScript("\n\n" + callString);

      return true;
   }

   public void callFinished(McpCall call)
   {

   }

   public void callFailed(McpCall call, Object callArgs, Exception e)
   {
      s_log.error(call.createCallString(callArgs), e);
   }
}
