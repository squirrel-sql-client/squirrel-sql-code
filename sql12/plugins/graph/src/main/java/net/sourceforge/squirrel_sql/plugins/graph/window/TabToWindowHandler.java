package net.sourceforge.squirrel_sql.plugins.graph.window;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.plugins.graph.GraphMainPanelTab;
import net.sourceforge.squirrel_sql.plugins.graph.GraphPanelController;
import net.sourceforge.squirrel_sql.plugins.graph.GraphPlugin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TabToWindowHandler
{
   private GraphMainPanelTab _graphMainPanelTab;
   private ISession _session;

   public TabToWindowHandler(GraphPanelController panelController, ISession session, GraphPlugin plugin)
   {
      _session = session;
      _graphMainPanelTab = new GraphMainPanelTab(panelController, plugin);
      _graphMainPanelTab.getToWindowButton().addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            toWindow();
         }
      });
   }

   private void toWindow()
   {
      Dimension size = _graphMainPanelTab.getComponent().getSize();
      Point screenLoc = GUIUtils.getScreenLocationFor(_graphMainPanelTab.getComponent());

      Rectangle tabBoundsOnScreen = new Rectangle();
      tabBoundsOnScreen.x =screenLoc.x;
      tabBoundsOnScreen.y =screenLoc.y;
      tabBoundsOnScreen.width = size.width;
      tabBoundsOnScreen.height = size.height;



      final int tabIdx = _session.getSessionSheet().removeMainTab(_graphMainPanelTab);

      GraphWindowControllerListener listener = new GraphWindowControllerListener()
      {
         @Override
         public void closing(int tabIdx)
         {
            onWindowClosing(tabIdx);
         }
      };

      GraphWindowController graphWindowController =
            new GraphWindowController(_session, _graphMainPanelTab, tabIdx, tabBoundsOnScreen, listener);


   }

   private void onWindowClosing(int tabIdx)
   {
      if(tabIdx <_session.getSessionSheet().getTabCount())
      {
         _session.getSessionSheet().insertMainTab(_graphMainPanelTab, tabIdx);
      }
      else
      {
         _session.getSessionSheet().addMainTab(_graphMainPanelTab);
      }
   }

   public void showGraph()
   {
      _session.getSessionSheet().addMainTab(_graphMainPanelTab);
   }

   public void removeGraph()
   {
      _session.getSessionSheet().removeMainTab(_graphMainPanelTab);
   }

   public void renameGraph(String newName)
   {
      int index = _session.getSessionSheet().removeMainTab(_graphMainPanelTab);
      _graphMainPanelTab.setTitle(newName);
      _session.getSessionSheet().insertMainTab(_graphMainPanelTab, index);
   }

   public String getTitle()
   {
      return _graphMainPanelTab.getTitle();
   }

   public void setTitle(String title)
   {
      _graphMainPanelTab.setTitle(title);
   }
}
