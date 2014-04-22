package org.squirrelsql.workaround;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Path;
import javafx.stage.Window;

//Test commit
public class CarretLocationOnScreenWA
{
   public static Point2D getCarretLocationOnScreen(TextArea sqlTextArea)
   {
      Path caret = findCaret(sqlTextArea);
      return findScreenLocation(caret);
   }

   private static Path findCaret(Parent parent)
   {
      // Warning: this is an ENORMOUS HACK
      for (Node n : parent.getChildrenUnmodifiable())
      {
         if (n instanceof Path)
         {
            return (Path) n;
         }
         else if (n instanceof Parent)
         {
            Path p = findCaret((Parent) n);
            if (p != null)
            {
               return p;
            }
         }
      }
      return null;
   }

   private static Point2D findScreenLocation(Node node)
   {
      double x = 0;
      double y = 0;
      for (Node n = node; n != null; n = n.getParent())
      {
         Bounds parentBounds = n.getBoundsInParent();
         x += parentBounds.getMinX();
         y += parentBounds.getMinY();
      }
      Scene scene = node.getScene();
      x += scene.getX();
      y += scene.getY();
      Window window = scene.getWindow();
      x += window.getX();
      y += window.getY();
      Point2D screenLoc = new Point2D(x, y);
      return screenLoc;
   }

}
