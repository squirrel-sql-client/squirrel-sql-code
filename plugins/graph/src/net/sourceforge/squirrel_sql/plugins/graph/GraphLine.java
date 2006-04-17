package net.sourceforge.squirrel_sql.plugins.graph;

import java.awt.*;


public class GraphLine
{
   private Point _beg;
   private Point _end;
   private FoldingPoint _fpBeg;
   private FoldingPoint _fpEnd;


   public GraphLine(Point begin, Point end)
   {
      this._beg = begin;
      this._end = end;
   }

   public GraphLine(FoldingPoint begin, Point end)
   {
      this._fpBeg = begin;
      this._end = end;
   }

   public GraphLine(Point begin, FoldingPoint end)
   {
      this._beg = begin;
      this._fpEnd = end;
   }

   public GraphLine(FoldingPoint begin, FoldingPoint end)
   {
      this._fpBeg = begin;
      this._fpEnd = end;
   }



   public GraphLine(GraphLine line)
   {

      if(null == line.getBegin())
      {
         _fpBeg = new FoldingPoint(line._fpBeg);
      }
      else
      {
         _beg = new Point(line._beg);
      }

      if(null == line.getEnd())
      {
         _fpEnd = new FoldingPoint(line._fpEnd);
      }
      else
      {
         _beg = new Point(line._end);
      }
   }


   public Point getBegin()
   {
      if(null == _beg)
      {
         return _fpBeg.getZoomedPoint();
      }
      else
      {
         return _beg;
      }
   }

   public Point getEnd()
   {
      if(null == _end)
      {
         return _fpEnd.getZoomedPoint();
      }
      else
      {
         return _end;
      }
   }
}
