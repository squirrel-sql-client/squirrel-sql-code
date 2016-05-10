package org.squirrelsql.session.graph;

public class PkPoint
{
   private final double _x;
   private final double _y;
   private final double _arrowAngle;

   public PkPoint(double x, double y, double arrowAngle)
   {
      _x = x;
      _y = y;
      _arrowAngle = arrowAngle;
   }

   public double getArrowAngle()
   {
      return _arrowAngle;
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
         offset = - GraphConstants.IMAGE_WIDTH;
      }

      return _x + offset;
   }

   public double getArrowY()
   {
      return _y - GraphConstants.IMAGE_HEIGHT / 2.0 ;
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
