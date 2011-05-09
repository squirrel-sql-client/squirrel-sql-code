package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.plugins.graph.GraphControllerFacade;

import javax.swing.*;

public class GraphDockHandle
{
   private GraphControllerFacade _graphControllerFacade;
   private JPanel _panel;
   private int _lastHeight;
   private boolean _showing;

   public GraphDockHandle(GraphControllerFacade graphControllerFacade, JPanel panel, int height)
   {
      _graphControllerFacade = graphControllerFacade;
      _panel = panel;
      _lastHeight = height;
   }

   public void show()
   {
      _showing = true;
      _graphControllerFacade.showDock(_panel, _lastHeight);
   }

   public int getLastHeigth()
   {
      return _lastHeight;
   }

   public void hide()
   {
      _showing = false;
      _lastHeight = _panel.getHeight();
      _graphControllerFacade.hideDock();
   }

   public boolean isShowing()
   {
      return _showing;
   }
}
