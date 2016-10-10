package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class FkSpec
{
   private final double _fkGatherPointX;
   private final double _fkGatherPointY;
   private final String _fkNameOrId;
   private ArrayList<FkPoint> _fkPoints;
   private FkProps _fkProps;
   private boolean _nonDB;

   public FkSpec(String fkNameOrId, ArrayList<FkPoint> fkPoints, TableWindowSide windowSide)
   {
      this(fkNameOrId, fkPoints, windowSide, false);
   }

   public FkSpec(String fkNameOrId, ArrayList<FkPoint> fkPoints, TableWindowSide windowSide, boolean nonDB)
   {
      _nonDB = nonDB;
      if(0 == fkPoints.size())
      {
         throw new IllegalArgumentException("There must be at least one fkPoint");
      }

      _fkNameOrId = fkNameOrId;
      _fkPoints = fkPoints;


      double maxY = Double.MIN_VALUE;
      double minY = Double.MAX_VALUE;

      for (FkPoint fkPoint : fkPoints)
      {
         minY = Math.min(minY, fkPoint.getPoint().getY());
         maxY = Math.max(maxY, fkPoint.getPoint().getY());
      }

      double midY = (maxY - minY) / 2d + minY;


      if(TableWindowSide.LEFT == windowSide)
      {
         _fkGatherPointX = fkPoints.get(0).getPoint().getX() + GraphConstants.X_GATHER_DIST;
      }
      else
      {
         _fkGatherPointX = fkPoints.get(0).getPoint().getX() - GraphConstants.X_GATHER_DIST;
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

   public List<FkPoint> getFkPoints()
   {
      return _fkPoints;
   }

   public boolean isSelected()
   {
      return _fkProps.isSelected();
   }

   public void setSelected(boolean selected)
   {
      _fkProps.setSelected(selected);
   }

   public String getFkNameOrId()
   {
      return _fkNameOrId;
   }

   public void setFkProps(FkProps fkProps)
   {
      _fkProps = fkProps;
   }

   public void addFoldingPoint(Point2D p)
   {
      _fkProps.addFoldingPoint(p);
   }

   public List<Point2D> getFoldingPoints()
   {
      return _fkProps.getFoldingPoints();
   }

   public void removeFoldingPoint(Point2D fp)
   {
      _fkProps.removeFoldingPoint(fp);
   }

   public void replaceFoldingPoint(Point2D oldFP, Point2D newFP)
   {
      _fkProps.replaceFoldingPoint(oldFP, newFP);
   }

   public void addFoldingPointAt(Point2D fp, int listIndex)
   {
      _fkProps.addFoldingPointAt(fp, listIndex);
   }

   public boolean isNonDB()
   {
      return _nonDB;
   }

   public void setJoinConfig(JoinConfig joinConfig)
   {
      _fkProps.setJoinConfig(joinConfig);
   }

   public JoinConfig getJoinConfig()
   {
      return _fkProps.getJoinConfig();
   }
}
