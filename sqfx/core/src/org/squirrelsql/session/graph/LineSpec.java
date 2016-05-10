package org.squirrelsql.session.graph;

import java.util.ArrayList;
import java.util.List;

public class LineSpec
{
   private final List<PkPoint> _pkPoints;
   private final double _pkGatherPointX;
   private final double _pkGatherPointY;
   private final double _fkGatherPointX;
   private final double _fkGatherPointY;
   private final List<FkPoint> _fkPoints;

   public LineSpec(List<PkPoint> pkPoints, double pkGatherPointX, double pkGatherPointY, double fkGatherPointX, double fkGatherPointY, List<FkPoint> fkPoints)
   {
      _pkPoints = pkPoints;
      _pkGatherPointX = pkGatherPointX;
      _pkGatherPointY = pkGatherPointY;
      _fkGatherPointX = fkGatherPointX;
      _fkGatherPointY = fkGatherPointY;
      _fkPoints = fkPoints;
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
}
