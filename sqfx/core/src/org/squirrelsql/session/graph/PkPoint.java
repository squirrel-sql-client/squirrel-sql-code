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

   public double getX()
   {
      return _x;
   }

   public double getY()
   {
      return _y;
   }
}
