package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import java.util.Comparator;
import java.util.List;

public class DoubleIndexedColumn extends IndexedColumn
{
   private DoubleRange _doubleRange = new DoubleRange();

   public DoubleIndexedColumn(List<Object[]> rows, int colIx, ColumnDisplayDefinition colDef)
   {
      super(rows, colIx, colDef);

      Comparator comparator = new Comparator<Object>()
      {
         @Override
         public int compare(Object ix1, Object ix2)
         {
            if( ix1 instanceof NoIx && ix2 instanceof NoIx)
            {
               return compareDouble(((NoIx) ix1).get(), ((NoIx) ix2).get());
            }
            else if(ix1 instanceof NoIx)
            {
               return compareDouble(((NoIx) ix1).get(), getRow((Integer) ix2));
            }
            else if(ix2 instanceof NoIx)
            {
               return compareDouble(getRow((Integer) ix1), ((NoIx) ix2).get());
            }
            else
            {
               return compareDouble(getRow((Integer) ix1), getRow((Integer) ix2));
            }
         }
      };

      _doubleRange.beginInit();
      sortIx(comparator);
      _doubleRange.endInit();

   }


   private int compareDouble(Object o1, Object o2)
   {
      Double d1 = null;
      if (null != o1)
      {
         d1 = ((Number)o1).doubleValue();
      }

      Double d2 = null;
      if (null != o2)
      {
         d2 = ((Number)o2).doubleValue();

      }

      _doubleRange.init(d1);
      _doubleRange.init(d2);

      if(d1 == null && d2 != null)
      {
         return -1;
      }
      else if(d1 != null && d2 == null)
      {
         return 1;
      }
      else if(d1 == null && d2 == null)
      {
         return 0;
      }

      return d1.compareTo(d2);

   }



   @Override
   public Calculator getCalculator()
   {
      return new Calculator()
      {
         @Override
         public Object getMid(Object min, Object max)
         {
            return onGetMid(min, max);
         }
      };
   }

   @Override
   public String calculateDist(Object beginData, Object endData)
   {
      if(null == beginData || null == endData)
      {
         return "" + 0;
      }


      return String.format("%.3f",(((Number)endData).doubleValue() - ((Number)beginData).doubleValue()));

   }

   private Object onGetMid(Object min, Object max)
   {

      Double doubleMin;
      if(null == min)
      {
         doubleMin = _doubleRange.getMin() - 1;
      }
      else
      {
         doubleMin = ((Number)min).doubleValue();
      }

      Double doubleMax;
      if(null == max)
      {
         doubleMax = _doubleRange.getMin() - 1;
      }
      else
      {
         doubleMax = ((Number)max).doubleValue();
      }


      return doubleMin + ((doubleMax - doubleMin) / 2);
   }

   private static class DoubleRange
   {
      private double _max = 0;
      private double _min = 0;
      private boolean _initializing;
      private boolean _isInit;

      public void init(Double d)
      {
         if(false == _initializing)
         {
            return;
         }


         if(null == d)
         {
            return;
         }


         if(false == _isInit)
         {
            _min = d;
            _max = d;
            _isInit = true;
         }

         _min = Math.min(_min, d);
         _max = Math.max(_max, d);



      }

      public double getMin()
      {
         return _min;
      }

      void beginInit()
      {
         _initializing = true;
      }

      void endInit()
      {
         _initializing = false;
      }

   }
}
