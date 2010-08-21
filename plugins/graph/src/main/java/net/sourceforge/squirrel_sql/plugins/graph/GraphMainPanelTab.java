package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.mainpanel.BaseMainPanelTab;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.*;


public class GraphMainPanelTab extends BaseMainPanelTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GraphMainPanelTab.class);

	private GraphDesktopController _desktopController;
	// i18n[graph.newGraph=New table graph]
	private String _title = s_stringMgr.getString("graph.newGraph");

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
		// i18n[graph.rightClickTable=Right click table in object tree to add to graph]
		return s_stringMgr.getString("graph.rightClickTable");
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
