package net.sourceforge.squirrel_sql.plugins.graph;

import java.awt.*;


public class GraphLine
{
   public Point beg;
   public Point end;

   public boolean begIsFoldingPoint;
   public boolean endIsFoldingPoint;

   public GraphLine(Point begin, Point end, boolean begIsFoldingPoint, boolean endIsFoldingPoint)
   {
      this.beg = begin;
      this.end = end;
      this.begIsFoldingPoint = begIsFoldingPoint;
      this.endIsFoldingPoint = endIsFoldingPoint;
   }

   public GraphLine(GraphLine line, double zoom)
   {
      beg = new Point(line.beg);
      end = new Point(line.end);
      begIsFoldingPoint = line.begIsFoldingPoint;
      endIsFoldingPoint = line.endIsFoldingPoint;

      if(line.begIsFoldingPoint)
      {
         beg.x = (int) (line.beg.x*zoom +0.5);
         beg.y = (int) (line.beg.y*zoom +0.5);
      }
      if(line.endIsFoldingPoint)
      {
         end.x = (int) (line.beg.x*zoom +0.5);
         end.y = (int) (line.beg.y*zoom +0.5);
      }
   }


}
