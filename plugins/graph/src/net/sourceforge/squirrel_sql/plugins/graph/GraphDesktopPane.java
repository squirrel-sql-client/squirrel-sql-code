package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.gui.ScrollableDesktopPane;

import java.awt.*;
import java.util.Vector;
import java.util.Arrays;


public class GraphDesktopPane extends ScrollableDesktopPane
{
   private Vector _graphComponents = new Vector();

   public void paint(Graphics g)
   {
      super.paintComponent(g);
      super.paintBorder(g);

      for (int i = 0; i < _graphComponents.size(); i++)
      {
         ((GraphComponent)_graphComponents.elementAt(i)).paint(g);
      }

      super.paintChildren(g);
   }

   public void putGraphComponents(GraphComponent[] graphComponents)
   {
      for (int i = 0; i < graphComponents.length; i++)
      {
         if(false == _graphComponents.contains(graphComponents[i]))
         {
            _graphComponents.add(graphComponents[i]);
         }
      }
   }

   public void removeGraphComponents(GraphComponent[] graphComponents)
   {
      _graphComponents.removeAll(Arrays.asList(graphComponents));
   }

   public Vector getGraphComponents()
   {
      return _graphComponents;
   }

}
