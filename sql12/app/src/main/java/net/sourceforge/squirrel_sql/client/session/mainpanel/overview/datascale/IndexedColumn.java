package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class IndexedColumn
{
   private ArrayList<Integer> _ix;
   private List<Object[]> _rows;
   private int _colIx;
   private ColumnDisplayDefinition _colDef;
   private Comparator _comparator;

   public IndexedColumn(List<Object[]> rows, int colIx, ColumnDisplayDefinition colDef)
   {
      _rows = rows;
      _colIx = colIx;
      _colDef = colDef;
      _ix = new ArrayList<Integer>(rows.size());

      for (int i = 0; i < rows.size(); i++)
      {
         _ix.add(i);
      }
   }

   public void sortIx(Comparator comparator)
   {
      _comparator = comparator;
      Collections.sort(_ix, _comparator);
   }

   public int getColumnIndex()
   {
      return _colIx;
   }

   public Object get(int ix)
   {
      return _rows.get(_ix.get(ix))[_colIx];
   }

   public Object getRow(Integer rowIx)
   {
      return _rows.get(rowIx)[_colIx];
   }

   public int getRowIx(int ix)
   {
      return _ix.get(ix);
   }



   public int binarySearch(Object border)
   {
      return Collections.binarySearch(_ix, new NoIx(border), _comparator);
   }

   public String getColumnName()
   {
      return _colDef.getColumnName();
   }

   public Object getMin()
   {
      return _rows.get(_ix.get(0))[_colIx];
   }

   public Object getMax()
   {
      return _rows.get(_ix.get(_ix.size() - 1))[_colIx];
   }

   public int size()
   {
      return _ix.size();
   }

   public int getLastIndexOfVal(int startIx)
   {
      int ret = startIx;

      for (int i = startIx + 1; i < _ix.size(); i++)
      {
         if(0 != _comparator.compare(new NoIx(get(startIx)), new NoIx(get(i))) )
         {
            break;
         }

         ++ret;
      }

      return ret;
   }

   public int getFirstIndexOfVal(int startIx)
   {
      for (int i = startIx - 1; 0 <= i ; i--)
      {
         if(0 != _comparator.compare(new NoIx(get(startIx)), new NoIx(get(i))) )
         {
            return i;
         }
      }

      return 0;
   }


   public int compareObjects(Object o1, Object o2)
   {
      return _comparator.compare(new NoIx(o1), new NoIx(o2));
   }


   public String renderObject(Object o)
   {
      return CellComponentFactory.renderObject(o, _colDef);
   }

   public abstract Calculator getCalculator();


   public Comparator<? super Object> getComparator()
   {
      return _comparator;
   }

   public List<Object[]> getResultRows(int firstIx, int lastIx)
   {
      ArrayList<Object[]> ret = new ArrayList<Object[]>(lastIx - firstIx + 1);

      for(int i= firstIx; i <= lastIx; ++i)
      {
         ret.add(_rows.get(_ix.get(i)));
      }

      return ret;
   }

   public ColumnDisplayDefinition getColumnDisplayDefinition()
   {
      return _colDef;
   }

   public abstract String calculateDist(Object beginData, Object endData);
}
