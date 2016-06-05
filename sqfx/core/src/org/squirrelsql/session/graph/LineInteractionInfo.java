package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class LineInteractionInfo
{
   private LineSpec _clickedOnLineSpec;
   private Point2D _clickedOnFoldingPoint;
   private ArrayList<LineSpec> _allLineSpecsCache;

   public void clear()
   {
      _clickedOnLineSpec = null;
      _clickedOnFoldingPoint = null;
   }

   public void setClickedOnFoldingPoint(Point2D clickedOnFoldingPoint, LineSpec clickedOnLineSpec)
   {
      _clickedOnFoldingPoint = clickedOnFoldingPoint;
      _clickedOnLineSpec = clickedOnLineSpec;

   }

   public void setClickedOnLineSpec(LineSpec clickedOnLineSpec)
   {
      _clickedOnLineSpec = clickedOnLineSpec;
   }

   public boolean isClickOnFoldingPoint()
   {
      return null != _clickedOnFoldingPoint;
   }

   public Point2D getClickedOnFoldingPoint()
   {
      return _clickedOnFoldingPoint;
   }

   public boolean isClickOnLineSpec()
   {
      return null != _clickedOnLineSpec && null == _clickedOnFoldingPoint;
   }

   public void setAllLineSpecsCache(ArrayList<LineSpec> allLineSpecs)
   {
      _allLineSpecsCache = allLineSpecs;
   }

   public List<LineSpec> getAllLineSpecsCache()
   {
      return _allLineSpecsCache;
   }

   public LineSpec getClickedOnLineSpec()
   {
      return _clickedOnLineSpec;
   }

   public void moveFoldingPointTo(double x, double y)
   {
      Point2D point2D = new Point2D(x, y);

      _clickedOnLineSpec.replaceFoldingPoint(_clickedOnFoldingPoint, point2D);
      _clickedOnFoldingPoint = point2D;
   }

   public void removeFoldingPoint()
   {
      _clickedOnLineSpec.removeFoldingPoint(_clickedOnFoldingPoint);
   }
}
