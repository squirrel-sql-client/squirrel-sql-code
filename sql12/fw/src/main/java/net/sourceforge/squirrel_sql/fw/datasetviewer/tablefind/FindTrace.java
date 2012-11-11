package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import java.awt.*;
import java.util.HashSet;

public class FindTrace
{
   private HashSet<Point> _trace = new HashSet<Point>();

   private Point _pointBuffer = new Point();
   private Point _current;

   public void add(int curRow, int curCol)
   {
      _current = new Point(curRow, curCol);
      _trace.add(_current);
   }

   public boolean contains(int viewRow, int viewColumn)
   {
      _pointBuffer.setLocation(viewRow, viewColumn);
      return _trace.contains(_pointBuffer);
   }

   public boolean isCurrent(int viewRow, int viewColumn)
   {
      if(null == _current)
      {
         return false;
      }

      _pointBuffer.setLocation(viewRow, viewColumn);
      return _current.equals(_pointBuffer);
   }

   public Point getCurrent()
   {
      return _current;
   }

   public void clear()
   {
      _current = null;
      _trace.clear();
   }
}
