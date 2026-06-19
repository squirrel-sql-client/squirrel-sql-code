package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class McpUiHandle
{
   public static final McpUiHandle INACTIVE = new McpUiHandle();
   private int _port;
   private String _connectKey;

   private McpUiHandle()
   {
      // For internal use only
   }

   public McpUiHandle(int port, String connectKey)
   {
      _port = port;
      _connectKey = connectKey;
   }

   public boolean isActive()
   {
      return this != INACTIVE;
   }

   public JPanel equipWithMcpConfigBar(JSplitPane sqlPanelSplitPane)
   {
      checkActive();



      JPanel ret = new JPanel(new BorderLayout());
      ret.add(new McpBarCtrl().getMcpBarPanel(), BorderLayout.NORTH);
      ret.add(sqlPanelSplitPane, BorderLayout.CENTER);

      return ret;
   }

   private void checkActive()
   {
      if(false == isActive())
      {
         throw new IllegalStateException("inactive McpUiHandle");
      }
   }
}
