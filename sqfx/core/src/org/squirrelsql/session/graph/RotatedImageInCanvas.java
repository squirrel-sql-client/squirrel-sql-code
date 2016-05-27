package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

/** Rotates images round pivot points and places them in a canvas */
public class RotatedImageInCanvas
{
   /**
    * Sets the transform for the GraphicsContext to rotate around a pivot point.
    *  @param angle the angle of rotation.
    * @param px    the x pivot co-ordinate for the rotation (in canvas co-ordinates).
    * @param py    the y pivot co-ordinate for the rotation (in canvas co-ordinates).
    */
   private static Rotate createRotation(double angle, double px, double py)
   {
      double angleInDeg = 360 / (2 * Math.PI) * angle;
      return new Rotate( angleInDeg, px, py);
   }

   /**
    * Draws an image on a graphics context.
    * <p>
    * The image is drawn at (arrowX, arrowY) rotated by angle pivoted around the point:
    * (arrowX + image.getWidth() / 2, arrowY + image.getHeight() / 2)
    * @param gc    the graphics context the image is to be drawn on.
    * @param angle the angle of rotation.
    * @param arrowX  the top left x co-ordinate where the image will be plotted (in canvas co-ordinates).
    * @param arrowY  the top left y co-ordinate where the image will be plotted (in canvas co-ordinates).
    * @param x
    * @param y
    */
   public static void drawRotatedImage(GraphicsContext gc, Image image, double angle, double arrowX, double arrowY, double x, double y)
   {
      double drX = x - GraphConstants.IMAGE_WIDTH;
      double drY = y - GraphConstants.IMAGE_HEIGHT / 2.0;

      //Rotate r = createRotation(angle, drX, drY);
      Rotate r = createRotation(angle, 0, 0);
      gc.save();
      gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());

      Point2D p = createRotation(-angle,0,0).transform(x, y);

      gc.drawImage(GraphConstants.ARROW_RIGHT_IMAGE, p.getX() - GraphConstants.IMAGE_WIDTH, p.getY() - GraphConstants.IMAGE_HEIGHT / 2.0);

//      Rotate r = createRotation(angle, arrowX + image.getWidth() / 2, arrowY + image.getHeight() / 2);
//      gc.save();
//      gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
//      gc.drawImage(image, arrowX, arrowY);

      //Point2D ret = r.transform(arrowX + GraphConstants.IMAGE_WIDTH, arrowY + GraphConstants.IMAGE_HEIGHT/2);
//      Point2D ret = new Point2D(x, y);
//      gc.fillOval(ret.getX() - 2, ret.getY() - 2, 4, 4);
      gc.fillOval(p.getX() - 2, p.getY() - 2, 4, 4);

      gc.restore(); // back to original state (before rotation)

//      Point2D ret = r.transform(arrowX + GraphConstants.IMAGE_WIDTH, arrowY + GraphConstants.IMAGE_HEIGHT/2);
//      gc.fillOval(ret.getX() - 2, ret.getY() - 2, 4, 4);
   }
}