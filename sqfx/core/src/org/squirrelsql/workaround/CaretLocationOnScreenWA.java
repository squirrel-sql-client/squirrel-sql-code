package org.squirrelsql.workaround;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Path;
import javafx.stage.Window;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.skin.StyledTextAreaVisual;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

public class CaretLocationOnScreenWA
{



   public static Point2D getCaretLocationOnScreen(CodeArea sqlTextArea)
   {
      try
      {

         // Old Version
//         Field fieldVisual = sqlTextArea.getSkin().getClass().getDeclaredField("visual");
//         fieldVisual.setAccessible(true);
//
//         Object visual = fieldVisual.get(sqlTextArea.getSkin());
//
//         Method m = StyledTextAreaVisual.class.getDeclaredMethod("getCaretBoundsOnScreen");
//
//         m.setAccessible(true);
//         Optional<Bounds> buf = (Optional<Bounds>) m.invoke((StyledTextAreaVisual) visual);
//         Bounds ret = buf.get();
//         return new Point2D(Math.max(0d,ret.getMinX()), ret.getMinY());

         //return ((StyledTextAreaSkin)sqlTextArea.getSkin()).getCaretLocationOnScreen();


         // New Version, since 0.6.1 but is not reliable.
         Field fieldVisual = sqlTextArea.getSkin().getClass().getDeclaredField("visual");
         fieldVisual.setAccessible(true);

         Object visual = fieldVisual.get(sqlTextArea.getSkin());

         Field fieldNode = visual.getClass().getDeclaredField("node");
         fieldNode.setAccessible(true);
         Object styledTextAreaView = fieldNode.get(visual);
         Method m = styledTextAreaView.getClass().getDeclaredMethod("getCaretBoundsOnScreen");
         m.setAccessible(true);
         Optional opt = (Optional) m.invoke(styledTextAreaView);
         Bounds bounds = (Bounds) opt.get();
         return new Point2D(Math.max(0d, bounds.getMinX()), bounds.getMinY());

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

//   public static Point2D getCarretLocationOnScreen(TextArea sqlTextArea)
//   {
//      Path caret = findCaret(sqlTextArea);
//      return findScreenLocation(caret);
//   }
//
//   private static Path findCaret(Parent parent)
//   {
//      // Warning: this is an ENORMOUS HACK
//      for (Node n : parent.getChildrenUnmodifiable())
//      {
//         if (n instanceof Path)
//         {
//            return (Path) n;
//         }
//         else if (n instanceof Parent)
//         {
//            Path p = findCaret((Parent) n);
//            if (p != null)
//            {
//               return p;
//            }
//         }
//      }
//      return null;
//   }
//
//   private static Point2D findScreenLocation(Node node)
//   {
//      double x = 0;
//      double y = 0;
//      for (Node n = node; n != null; n = n.getParent())
//      {
//         Bounds parentBounds = n.getBoundsInParent();
//         x += parentBounds.getMinX();
//         y += parentBounds.getMinY();
//      }
//      Scene scene = node.getScene();
//      x += scene.getX();
//      y += scene.getY();
//      Window window = scene.getWindow();
//      x += window.getX();
//      y += window.getY();
//      Point2D screenLoc = new Point2D(x, y);
//      return screenLoc;
//   }

}
