package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FindTrace
{
   private HashSet<Point> _trace = new HashSet<>();

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

   public Set<Point> getCellsFound()
   {
      return _trace;
   }

   public List<Integer> getRowsFound()
   {
      HashSet<Integer> uniqueRows = new HashSet<>();


      for (Point point : _trace)
      {
         uniqueRows.add(point.x);
      }

      ArrayList<Integer> ret = new ArrayList<>(uniqueRows);

      Collections.sort(ret);

      return ret;
   }
}
