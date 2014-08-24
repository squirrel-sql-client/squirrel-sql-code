package org.squirrelsql.workaround;

import com.sun.javafx.scene.control.skin.LabeledText;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import java.lang.reflect.Field;

public class TableCellByCoordinatesWA
{
   public static TableCell getTableCellForPointInTable(TableView parent, double x, double y)
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


            System.out.println(node + "X=" + localX + " Y=" + localY + "; In Parent PX=" + boundsInParent.getMinX() + " PY=" + boundsInParent.getMinY()+ " visi" + node.isVisible());
         }

         //System.out.println("   X=" + localX + " Y=" + localY + "; In Parent PX=" + boundsInParent.getMinX() + " PY=" + boundsInParent.getMinY() + " " + parent.getClass().getName() + " -> " + node.getClass().getName());


         if(node instanceof Parent)
         {
            getTableCellForPointInTable((TableView) node, localX, localY);
         }
      }

      return null;
   }
}
