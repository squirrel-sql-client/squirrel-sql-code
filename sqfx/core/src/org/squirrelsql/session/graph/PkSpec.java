package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class PkSpec
{
   private final double _pkGatherPointX;
   private final double _pkGatherPointY;
   private ArrayList<PkPoint> _pkPoints = new ArrayList<>();


   public PkSpec(ArrayList<Point2D> pkPoint2Ds, TableWindowSide windowSide)
   {
      if(0 == pkPoint2Ds.size())
      {
         throw new IllegalArgumentException("There must be at least one pkPoint");
      }

      double maxY = Double.MIN_VALUE;
      double minY = Double.MAX_VALUE;

      for (Point2D pkPoint : pkPoint2Ds)
      {
         minY = Math.min(minY, pkPoint.getY());
         maxY = Math.max(maxY, pkPoint.getY());
      }

      double midY = (maxY - minY) / 2d + minY;


      if(TableWindowSide.LEFT == windowSide)
      {
         _pkGatherPointX = pkPoint2Ds.get(0).getX() - GraphConstants.X_GATHER_DIST;

      }
      else
      {
         _pkGatherPointX = pkPoint2Ds.get(0).getX() + GraphConstants.X_GATHER_DIST;
      }

      _pkGatherPointY = midY;


      for (Point2D pkPoint : pkPoint2Ds)
      {
         double arrowAngle;

         if(TableWindowSide.LEFT == windowSide)
         {
            arrowAngle = Math.atan((pkPoint.getY() - midY) / GraphConstants.X_GATHER_DIST);
         }
         else
         {
            arrowAngle = Math.PI - Math.atan((pkPoint.getY() - midY) / GraphConstants.X_GATHER_DIST);
         }

         _pkPoints.add(new PkPoint(pkPoint.getX(), pkPoint.getY(), arrowAngle));
      }
   }

   public List<PkPoint> getPkPoints()
   {
      return _pkPoints;
   }

   public double getPkGatherPointX()
   {
      return _pkGatherPointX;
   }

   public double getPkGatherPointY()
   {
      return _pkGatherPointY;
   }

}
