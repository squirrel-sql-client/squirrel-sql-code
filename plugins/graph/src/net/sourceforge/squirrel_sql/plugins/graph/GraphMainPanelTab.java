package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.mainpanel.BaseMainPanelTab;
import java.awt.*;


public class GraphMainPanelTab extends BaseMainPanelTab
{
   private GraphDesktopController _desktopController;
   private String _title = "New table graph";

   public GraphMainPanelTab(GraphDesktopController desktopController)
   {
      _desktopController = desktopController;
   }


   protected void refreshComponent()
   {
      _desktopController.repaint();
   }

   public String getTitle()
   {
      return _title;
   }

   public String getHint()
   {
      return "Right click table in object tree to add to graph";
   }

   public Component getComponent()
   {
      return _desktopController.getGraphPanel();
   }

   public void setTitle(String title)
   {
      _title = title;      
   }

}
