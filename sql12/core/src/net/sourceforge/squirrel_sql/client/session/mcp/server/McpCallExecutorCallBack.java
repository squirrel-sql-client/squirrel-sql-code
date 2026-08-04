package net.sourceforge.squirrel_sql.client.session.mcp.server;

@FunctionalInterface
public interface McpCallExecutorCallBack
{
  Object executeCall();
}
