package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

public class ArrowDrawer
{
   private static Rotate createRotation(double angle, double px, double py)
   {
      double angleInDeg = 360 / (2 * Math.PI) * angle;
      return new Rotate( angleInDeg, px, py);
   }

   public static void drawArrow(GraphicsContext gc, PkPoint pkPoint, Color arrowColor)
   {
      Rotate r = createRotation(pkPoint.getArrowAngle(), 0, 0);
      gc.save();
      gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());

      Point2D p = createRotation( - pkPoint.getArrowAngle(),0,0).transform(pkPoint.getX(), pkPoint.getY());

      if (Color.BLUE.equals(arrowColor))
      {
         gc.drawImage(GraphConstants.ARROW_RIGHT_IMAGE_BLUE, p.getX() - GraphConstants.IMAGE_WIDTH, p.getY() - GraphConstants.IMAGE_HEIGHT / 2.0);
      }
      else
      {
         gc.drawImage(GraphConstants.ARROW_RIGHT_IMAGE, p.getX() - GraphConstants.IMAGE_WIDTH, p.getY() - GraphConstants.IMAGE_HEIGHT / 2.0);
      }

      //gc.fillOval(pkPoint.getX() - 2, pkPoint.getY() - 2, 4, 4);

      gc.restore();
   }
}