package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;

import java.util.List;

public class LineSpec
{
   private final PkSpec _pkSpec;
   private final FkSpec _fkSpec;

   public LineSpec(PkSpec pkSpec, FkSpec fkSpec)
   {
      _pkSpec = pkSpec;
      _fkSpec = fkSpec;
   }

   public List<PkPoint> getPkPoints()
   {
      return _pkSpec.getPkPoints();
   }

   public double getPkGatherPointX()
   {
      return _pkSpec.getPkGatherPointX();
   }

   public double getPkGatherPointY()
   {
      return _pkSpec.getPkGatherPointY();
   }

   public double getFkGatherPointX()
   {
      return _fkSpec.getFkGatherPointX();
   }

   public double getFkGatherPointY()
   {
      return _fkSpec.getFkGatherPointY();
   }

   public List<Point2D> getFkPoints()
   {
      return _fkSpec.getFkPoints();
   }

   public boolean isSelected()
   {
      return _fkSpec.isSelected();
   }

   public void setSelected(boolean selected)
   {
      _fkSpec.setSelected(selected);
   }

   public void addFoldingPoint(Point2D p)
   {
      _fkSpec.addFoldingPoint(p);
   }

   public List<Point2D> getFoldingPoints()
   {
      return _fkSpec.getFoldingPoints();
   }

   public void removeFoldingPoint(Point2D fp)
   {
      _fkSpec.removeFoldingPoint(fp);
   }
}
