package net.sourceforge.squirrel_sql.client.session.mcp.ui;

public class McpCallApproveCtrl
{
   public McpCallApproveCtrl(String call)
   {

   }

   public boolean isApproved()
   {
      if(McpUiProps.isApproveAllAiCalls())
      {
         return false;
      }
      else
      {
         return true;
      }
   }
}
