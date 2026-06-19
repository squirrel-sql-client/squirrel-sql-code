package net.sourceforge.squirrel_sql.client.session.mcp.ui;

public class McpBarCtrl
{
   private McpBarPanel _panel;

   public McpBarCtrl()
   {
      _panel = new McpBarPanel();
   }

   public McpBarPanel getMcpBarPanel()
   {
      return _panel;
   }
}
