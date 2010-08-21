package net.sourceforge.squirrel_sql.plugins.graph;

import java.awt.*;

public class FoldingPoint
{
   private Point _unzoomedPoint;
   private Zoomer _zoomer;
   private Point _zoomedPointBuffer = new Point();

   public FoldingPoint(Point unZoomedPoint, Zoomer zoomer)
   {
      _unzoomedPoint = unZoomedPoint;

      if(_unzoomedPoint.x < 0)
      {
         _unzoomedPoint.x = 0;
      }
      if(_unzoomedPoint.y < 0)
      {
         _unzoomedPoint.y = 0;
      }


      _zoomer = zoomer;
   }

   public FoldingPoint(FoldingPoint fp)
   {
      _unzoomedPoint = fp._unzoomedPoint;
      _zoomer = fp._zoomer;
   }

   Point getZoomedPoint()
   {
      if(_zoomer.isEnabled())
      {
         double zoom = _zoomer.getZoom();
         _zoomedPointBuffer.x = (int)(_unzoomedPoint.x*zoom+0.5);
         _zoomedPointBuffer.y = (int)(_unzoomedPoint.y*zoom+0.5);

      }
      else
      {
         _zoomedPointBuffer.x = _unzoomedPoint.x;
         _zoomedPointBuffer.y = _unzoomedPoint.y;
      }
      return _zoomedPointBuffer;
   }

   public boolean equals(Object obj)
   {
      FoldingPoint other = (FoldingPoint) obj;
      return _unzoomedPoint.equals(other._unzoomedPoint);
   }

   public int hashCode()
   {
      return _unzoomedPoint.hashCode();
   }

   public Point getUnZoomedPoint()
   {
      return _unzoomedPoint;
   }
}
