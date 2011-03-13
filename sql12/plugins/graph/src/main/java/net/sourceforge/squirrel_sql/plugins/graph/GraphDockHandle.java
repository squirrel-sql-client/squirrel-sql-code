package net.sourceforge.squirrel_sql.plugins.graph;

import javax.swing.*;

public class GraphDockHandle
{
   private GraphDockHandleListener _graphDockHandleListener;
   private JPanel _panel;
   private int _lastHeight;
   private boolean _showing;

   public GraphDockHandle(GraphDockHandleListener graphDockHandleListener, JPanel panel, int height)
   {
      _graphDockHandleListener = graphDockHandleListener;
      _panel = panel;
      _lastHeight = height;
   }

   public void show()
   {
      _showing = true;
      _graphDockHandleListener.show(_panel, _lastHeight);
   }

   public int getLastHeigth()
   {
      return _lastHeight;
   }

   public void hide()
   {
      _showing = false;
      _lastHeight = _panel.getHeight();
      _graphDockHandleListener.hide();
   }

   public boolean isShowing()
   {
      return _showing;
   }
}
