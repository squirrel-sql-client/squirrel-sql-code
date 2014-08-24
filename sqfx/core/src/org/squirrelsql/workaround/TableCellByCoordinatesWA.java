package org.squirrelsql.workaround;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;

public class TableCellByCoordinatesWA
{
   public static TableCell findTableCellForPoint(TableView tablView, double x, double y)
   {
      return _getTableCell(tablView, x, y);
   }

   private static TableCell _getTableCell(Parent parent, double x, double y)
   {
      for (Node node : parent.getChildrenUnmodifiable())
      {
         Bounds boundsInParent = node.getBoundsInParent();
         Bounds layoutBounds = node.getLayoutBounds();
         Bounds boundsInLocal = node.getBoundsInLocal();

         double localX;
         double localY;

//         if (parent instanceof Group)
//         {
//            localX = x;
//            localY = y;
//         }
//         else
//         {
         localX = x - (boundsInParent.getMinX() /*- layoutBounds.getMinX()*/ - boundsInLocal.getMinX());
         localY = y - (boundsInParent.getMinY() /*- layoutBounds.getMinY()*/ - boundsInLocal.getMinY());
//         }

         if(node instanceof TableCell && null != ((TableCell) node).getItem() && node.isVisible() && parent.isVisible() && node.contains(localX, localY) )
         {
            //System.out.println(node + "X=" + localX + " Y=" + localY + "; In Parent PX=" + boundsInParent.getMinX() + " PY=" + boundsInParent.getMinY()+ " visi" + node.isVisible());
            return (TableCell) node;

         }

         //System.out.println("   X=" + localX + " Y=" + localY + "; In Parent PX=" + boundsInParent.getMinX() + " PY=" + boundsInParent.getMinY() + " " + parent.getClass().getName() + " -> " + node.getClass().getName());


         if(node instanceof Parent)
         {
            TableCell tableCell = _getTableCell((Parent) node, localX, localY);

            if(null != tableCell)
            {
               return tableCell;
            }
         }
      }

      return null;
   }
}
