package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class FkSpec
{
   private final double _fkGatherPointX;
   private final double _fkGatherPointY;
   private final String _fkName;
   private ArrayList<Point2D> _fkPoints;
   private PersistentFkProps _persistentFkProps;

   public FkSpec(String fkName, ArrayList<Point2D> fkPoints, TableWindowSide windowSide)
   {
      if(0 == fkPoints.size())
      {
         throw new IllegalArgumentException("There must be at least one fkPoint");
      }

      _fkName = fkName;
      _fkPoints = fkPoints;


      double maxY = Double.MIN_VALUE;
      double minY = Double.MAX_VALUE;

      for (Point2D pkPoint : fkPoints)
      {
         minY = Math.min(minY, pkPoint.getY());
         maxY = Math.max(maxY, pkPoint.getY());
      }

      double midY = (maxY - minY) / 2d + minY;


      if(TableWindowSide.LEFT == windowSide)
      {
         _fkGatherPointX = fkPoints.get(0).getX() + GraphConstants.X_GATHER_DIST;
      }
      else
      {
         _fkGatherPointX = fkPoints.get(0).getX() - GraphConstants.X_GATHER_DIST;
      }

      _fkGatherPointY = midY;
   }

   public double getFkGatherPointX()
   {
      return _fkGatherPointX;
   }

   public double getFkGatherPointY()
   {
      return _fkGatherPointY;
   }

   public List<Point2D> getFkPoints()
   {
      return _fkPoints;
   }

   public boolean isSelected()
   {
      return _persistentFkProps.isSelected();
   }

   public void setSelected(boolean selected)
   {
      _persistentFkProps.setSelected(selected);
   }

   public String getFkName()
   {
      return _fkName;
   }

   public void setPersistentFkProps(PersistentFkProps persistentFkProps)
   {
      _persistentFkProps = persistentFkProps;
   }

   public void addFoldingPoint(Point2D p)
   {
      _persistentFkProps.addFoldingPoint(p);
   }

   public List<Point2D> getFoldingPoints()
   {
      return _persistentFkProps.getFoldingPoints();
   }
}
