package net.sourceforge.squirrel_sql.plugins.graph;

import javax.swing.*;

public class GraphDockHandleFactory
{
   private GraphDockHandleListener _graphDockHandleListener;

   public GraphDockHandleFactory(GraphDockHandleListener graphDockHandleListener)
   {
      _graphDockHandleListener = graphDockHandleListener;
   }

   public GraphDockHandle createHandle(int height, JPanel panel)
   {
      return new GraphDockHandle(_graphDockHandleListener, panel, height);
   }
}
