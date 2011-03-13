package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.mainpanel.BaseMainPanelTab;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;


public class GraphMainPanelTab extends BaseMainPanelTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GraphMainPanelTab.class);

	private GraphPanelController _panelController;
	// i18n[graph.newGraph=New table graph]

   private JPanel _tabComponent;

   private JLabel _lblTitle;

   public GraphMainPanelTab(GraphPanelController panelController, GraphPlugin plugin)
   {
      _panelController = panelController;
      _tabComponent = new JPanel(new BorderLayout(3,0));
      _tabComponent.setOpaque(false);

      _lblTitle = new JLabel(s_stringMgr.getString("graph.newGraph"));
      _lblTitle.setOpaque(false);

      _tabComponent.add(_lblTitle, BorderLayout.CENTER);
      ImageIcon icon = new GraphPluginResources(plugin).getIcon(GraphPluginResources.IKeys.TO_WINDOW);

      JButton btnToWindow = new JButton(icon);
      btnToWindow.setBorder(BorderFactory.createEmptyBorder());
      btnToWindow.setOpaque(false);
      _tabComponent.add(btnToWindow, BorderLayout.EAST);
   }


   protected void refreshComponent()
   {
      _panelController.repaint();
   }

   public String getTitle()
   {
      return _lblTitle.getText();
   }


   @Override
   public Component getTabComponent()
   {
      // return _tabComponent;
      return null;

   }

   public String getHint()
   {
		// i18n[graph.rightClickTable=Right click table in object tree to add to graph]
		return s_stringMgr.getString("graph.rightClickTable");
   }

   public Component getComponent()
   {
      return _panelController.getGraphPanel();
   }

   public void setTitle(String title)
   {
      _lblTitle.setText(title);
   }
}
