package net.sourceforge.squirrel_sql.client.session.mcp.ui;

public class McpCallApproveCtrl
{
   private final McpUiProps _mcpUiProps;

   public McpCallApproveCtrl(String call, McpUiProps mcpUiProps)
   {
      _mcpUiProps = mcpUiProps;
   }

   public boolean isApproved()
   {
      if( _mcpUiProps.isApproveAllAiCalls())
      {
         return false;
      }
      else
      {
         return true;
      }
   }
}
