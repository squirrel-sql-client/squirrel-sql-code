package net.sourceforge.squirrel_sql.client.session.mcp.server;

public class McpCallExecutor
{
   private final McpCallExecutorCallBack _mcpCallExecutorCallBack;
   private final McpCall _mcpCall;

   private Object _callResult;

   public McpCallExecutor(McpCall mcpCall, McpCallExecutorCallBack mcpCallExecutorCallBack)
   {
      _mcpCall = mcpCall;
      _mcpCallExecutorCallBack = mcpCallExecutorCallBack;
   }

   public <T> T executeCall()
   {
      if(null == _callResult)
      {
         _callResult = _mcpCallExecutorCallBack.executeCall();
      }

      return (T) _callResult;
   }

   public McpCall getMcpCall()
   {
      return _mcpCall;
   }
}
