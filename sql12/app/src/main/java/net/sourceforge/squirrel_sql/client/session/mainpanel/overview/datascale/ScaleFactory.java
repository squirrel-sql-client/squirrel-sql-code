package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ScaleFactory
{
   /**
    * 2^(DEFAULT_CALL_DEPTH-1) is the maximum number of intervals that will be generated.
    */
   public static final int DEFAULT_CALL_DEPTH = 4;
   private IndexedColumn _indexedColumn;
   private int _callDepth;

   public ScaleFactory(List<Object[]> rows, int colIx, ColumnDisplayDefinition colDef, int callDepth)
   {
      _indexedColumn = IndexedColumnFactory.create(rows, colIx, colDef);
      _callDepth = callDepth;
   }

   public DataScale createScale(DataScaleListener dataScaleListener)
   {
      Object min =  _indexedColumn.getMin();
      Object max =  _indexedColumn.getMax();

      DataScale ret = new DataScale(_indexedColumn.getColumnName(), dataScaleListener, _indexedColumn.getColumnIndex(), _indexedColumn.getColumnDisplayDefinition());

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
            ip = (- bsRet - 1);
         }
         else
         {
            //ip = _indexedColumn.getFirstIndexOfVal(bsRet);
            ip = _indexedColumn.getLastIndexOfVal(bsRet)+1;
         }

         if(null == lastIx)
         {
            //ret.addInterval(new Interval(_indexedColumn, 0, Math.max(0, ip-1), lastBorder, border));

//            int firstIx = 0;
//            int endIx = Math.max(0, ip - 1);
//            if (1 == endIx - firstIx)
//            {
//               ret.addInterval(new Interval(_indexedColumn, firstIx, firstIx, _indexedColumn.get(firstIx), _indexedColumn.get(firstIx)));
//               ret.addInterval(new Interval(_indexedColumn, endIx, endIx, _indexedColumn.get(endIx), _indexedColumn.get(endIx)));
//            }
//            else
//            {
//               ret.addInterval(new Interval(_indexedColumn, 0, Math.max(0, ip-1), lastBorder, border));
//            }

            int firstIx = 0;
            int endIx = Math.max(0, ip - 1);
            if (1 == createBorders(_indexedColumn.get(firstIx), _indexedColumn.get(endIx)).size())
            {
               // This makes sure that the first interval can always drilled down to one single value.
               // Without this it might happen that the first interval contains two different values and cannot be further drilled down.
               int lastIndexOfVal = _indexedColumn.getLastIndexOfVal(firstIx);
               ret.addInterval(new Interval(_indexedColumn, firstIx, lastIndexOfVal, _indexedColumn.get(firstIx), _indexedColumn.get(firstIx)));
               if (lastIndexOfVal+1 <= endIx)
               {
                  ret.addInterval(new Interval(_indexedColumn, lastIndexOfVal+1, endIx, _indexedColumn.get(endIx), _indexedColumn.get(endIx)));
               }
            }
            else
            {
               ret.addInterval(new Interval(_indexedColumn, 0, Math.max(0, ip-1), lastBorder, border));
            }


            lastIx = ip;
         }
         else if(ip > lastIx)
         {
            ret.addInterval(new Interval(_indexedColumn, lastIx, Math.max(0, ip-1), lastBorder, border));
            lastIx = ip;
         }
         lastBorder = border;
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

//      if(0 == ret.size())
//      {
//         // There was no value that fits between min and max.
//         // Now calling methods expect us to return min as a border.
//         ret.add(min);
//      }

      //ret.add(min);
      ret.add(max);

      return ret;
   }

   private void divide(Object min, Object max, HashSet<Object> ret, int[] callDepth)
   {
       ++callDepth[0];

      if(_callDepth == callDepth[0])
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
