package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ScaleFactory
{
   /**
    * 2^(CALL_DEPTH-1) is the maximum number of intervals that will be generated.
    */
   private static final int CALL_DEPTH = 4;
   private IndexedColumn _indexedColumn;

   public ScaleFactory(List<Object[]> rows, int colIx, ColumnDisplayDefinition colDef)
   {
      _indexedColumn = IndexedColumnFactory.create(rows, colIx, colDef);
   }

   public DataScale createScale(DataScaleListener dataScaleListener)
   {
      Object min =  _indexedColumn.getMin();
      Object max =  _indexedColumn.getMax();

      DataScale ret = new DataScale(_indexedColumn.getColumnName(), dataScaleListener, _indexedColumn.getColumnIndex());

      if(0 == _indexedColumn.compareObjects(min, max))
      {
         ret.addInterval(new Interval(_indexedColumn, 0, _indexedColumn.size() - 1, min, max));
         return ret;
      }


      Object[] borders = createBorders(min, max).toArray(new Object[0]);

      sortBorders(borders);


      Integer lastIx = null;

      Object lastBorder = min;
      for (Object border : borders)
      {
         int bsRet = _indexedColumn.binarySearch(border);

         int ip;

         if( 0 > bsRet)
         {
            ip = (- bsRet - 1) - 1;
         }
         else
         {
            ip = _indexedColumn.getLastIndexOfVal(bsRet);
         }

         if(null == lastIx)
         {
            ret.addInterval(new Interval(_indexedColumn, 0, ip, lastBorder, border));
            lastBorder = border;
            lastIx = ip + 1;
         }
         else if(ip > lastIx)
         {
            ret.addInterval(new Interval(_indexedColumn, lastIx, ip, lastBorder, border));
            lastBorder = border;
            lastIx = ip + 1;
         }
      }

      if (_indexedColumn.size() > lastIx)
      {
         ret.addInterval(new Interval(_indexedColumn, lastIx, _indexedColumn.size() - 1, lastBorder, max));
      }

//      System.out.println("sumWeights = " + ret.getSumWeights());
//      System.out.println("sumLens = " + ret.getSumLens());

      return ret;
   }

   private void sortBorders(Object[] borders)
   {
      NoIx[] noIxes = new NoIx[borders.length];

      for (int i = 0; i < noIxes.length; i++)
      {
         noIxes[i] = new NoIx(borders[i]);
      }

      Arrays.sort(noIxes, _indexedColumn.getComparator());


      for (int i = 0; i < noIxes.length; i++)
      {
         borders[i] = noIxes[i].get();
      }
   }


   private HashSet<Object> createBorders(Object min, Object max)
   {
      HashSet<Object> ret = new HashSet<Object>();

      int callDepth[] = new int[]{0};

      divide(min, max, ret, callDepth);

      if(0 == ret.size())
      {
         // There was no value that fits between min and max.
         // Now calling methods expect us to return min as a border.
         ret.add(min);
      }

      return ret;
   }

   private void divide(Object min, Object max, HashSet<Object> ret, int[] callDepth)
   {
       ++callDepth[0];

      if(CALL_DEPTH == callDepth[0])
      {
         return;
      }

      Object mid = _indexedColumn.getCalculator().getMid(min, max);

      if (0 != _indexedColumn.compareObjects(min, mid))
      {
         ret.add(mid);
      }

      divide(min, mid, ret, new int[]{callDepth[0]});
      divide(mid, max, ret, new int[]{callDepth[0]});

      --callDepth[0];
   }

}
