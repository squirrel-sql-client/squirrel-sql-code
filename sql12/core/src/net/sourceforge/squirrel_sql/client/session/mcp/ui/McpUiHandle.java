package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;

public class McpUiHandle
{
   public static final McpUiHandle INACTIVE = new McpUiHandle(true);

   private boolean _inactive;

   private ISession _session;
   private AdditionalSQLTab _mcpSqlTab;
   private McpBarCtrl _mcpBarCtrl;

   /**
    * For internal use only
    */
   private McpUiHandle(boolean inactive)
   {
      _inactive = inactive;
   }

   public McpUiHandle(ISession session)
   {
      this(false);
      _session = session;
   }

   public boolean isActive()
   {
      return this != INACTIVE;
   }

   public JPanel equipWithMcpConfigBar(JSplitPane sqlPanelSplitPane)
   {
      checkActive();

      JPanel ret = new JPanel(new BorderLayout());
      _mcpBarCtrl = new McpBarCtrl(_session);
      ret.add(_mcpBarCtrl.getMcpBarPanel(), BorderLayout.NORTH);
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

   public void setMcpSqlTab(AdditionalSQLTab mcpSqlTab)
   {
      checkActive();
      _mcpBarCtrl.setMcpSqlTab(mcpSqlTab);
   }
}
