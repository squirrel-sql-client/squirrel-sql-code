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
      double signum = Math.signum(Math.cos(_arrowAngle));

      double offset;
      if(signum < 0)
      {
         offset = 0;
      }
      else
      {
         offset = -GraphConstants.IMAGE_WIDTH;
      }

      return _x + offset;
   }

   public double getArrowY()
   {

      double h = GraphConstants.IMAGE_HEIGHT;
      double w = GraphConstants.IMAGE_WIDTH;

      double offset;

      if (isBelowGatherPointY())
      {
         offset = -h * Math.sin(_angleFromSimpleTriangle) - w/2.0 * Math.cos(_angleFromSimpleTriangle);
      }
      else
      {
         offset = - w/2.0 * Math.cos(_angleFromSimpleTriangle);
      }

      return _y + offset;
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
