package net.sourceforge.squirrel_sql.plugins.graph;

import java.awt.*;


public class GraphLine
{
   public Point beg;
   public Point end;

   public GraphLine(Point begin, Point end)
   {
      this.beg = begin;
      this.end = end;
   }
}
