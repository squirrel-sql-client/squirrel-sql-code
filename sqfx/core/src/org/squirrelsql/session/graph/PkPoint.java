package org.squirrelsql.session.graph;

public class PkPoint
{
   private final double _x;
   private final double _y;
   private final double _arrowAngle;

   /**
    * Values are between 0 and PI/2
    */
   private double _angleFromSimpleTriangle;

   public PkPoint(double x, double y, double arrowAngle, double angleFromSimpleTriangle)
   {
      _x = x;
      _y = y;
      _arrowAngle = arrowAngle;
      _angleFromSimpleTriangle = angleFromSimpleTriangle;
   }

   public double getArrowAngle()
   {
      return _arrowAngle;
   }

   public double getAngleFromSimpleTriangle()
   {
      return _angleFromSimpleTriangle;
   }

   public double getArrowX()
   {
      double h = GraphConstants.IMAGE_HEIGHT;
      double w = GraphConstants.IMAGE_WIDTH;
      double cos = Math.cos(_arrowAngle);
      double sin = Math.sin(_arrowAngle);

      return _x - (cos*w + sin*h/2);

   }

   public double getArrowY()
   {
      double h = GraphConstants.IMAGE_HEIGHT;
      double w = GraphConstants.IMAGE_WIDTH;
      double cos = Math.cos(_arrowAngle);
      double sin = Math.sin(_arrowAngle);

      return _y - (-sin*w + cos*h/2);
   }

   /**
    * Note: Y Axis is turned upside down
    */
   private boolean isBelowGatherPointY()
   {
      return 0 < _arrowAngle && _arrowAngle < Math.PI;
   }

   public double getX()
   {
      return _x;
   }

   public double getY()
   {
      return _y;
   }
}
