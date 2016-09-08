package org.squirrelsql.session.graph;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.squirrelsql.session.graph.graphdesktop.Window;

import java.util.List;

public class GraphColumnFinder
{
   private Pane _desktopPane;

   public GraphColumnFinder(Pane desktopPane)
   {
      _desktopPane = desktopPane;
   }

   public GraphColumn findNonDbPkCol(GraphColumn fkCol, String fkId)
   {
      for (Node tableNode : _desktopPane.getChildren())
      {
         TableWindowCtrl tableCtrl = ((Window) tableNode).getCtrl();

         List<GraphColumn> graphColumns = tableCtrl.getGraphColumns();

         for (GraphColumn col : graphColumns)
         {
            if(fkCol.isMyNonDbPkCol(col, fkId))
            {
               return col;
            }
         }
      }

      return null;
   }
}
