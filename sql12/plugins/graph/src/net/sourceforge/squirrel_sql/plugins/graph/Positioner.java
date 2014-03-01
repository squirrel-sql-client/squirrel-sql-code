package net.sourceforge.squirrel_sql.plugins.graph;

import java.awt.*;

public class Positioner
{
   private Point _refPoint;
   private Point _dropPoint;

   public Positioner()
   {
   }

   public Positioner(Point dropPoint)
   {
      _dropPoint = dropPoint;
   }


   public Point getRefPoint()
   {
      return _refPoint;
   }

   public void setRefPoint(Point refPoint)
   {
      _refPoint = refPoint;
   }

   public Point getDropPointClone()
   {
      if(null == _dropPoint)
      {
         return null;
      }

      return new Point(_dropPoint);
   }
}
