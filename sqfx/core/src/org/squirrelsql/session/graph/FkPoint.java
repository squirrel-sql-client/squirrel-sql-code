package org.squirrelsql.session.graph;

public class FkPoint
{
   private final double _x;
   private final double _y;

   public FkPoint(double x, double y)
   {
      _x = x;
      _y = y;
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
