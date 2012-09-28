package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import java.util.Comparator;
import java.util.List;

public class IntegerIndexedColumn extends IndexedColumn
{
   private IntegerRange _intRange = new IntegerRange();

   public IntegerIndexedColumn(List<Object[]> rows, int colIx, ColumnDisplayDefinition colDef)
   {
      super(rows, colIx, colDef);

      Comparator comparator = new Comparator<Object>()
      {
         @Override
         public int compare(Object ix1, Object ix2)
         {
            if( ix1 instanceof NoIx && ix2 instanceof NoIx)
            {
               return compareInteger(((NoIx) ix1).get(), ((NoIx) ix2).get());
            }
            else if(ix1 instanceof NoIx)
            {
               return compareInteger(((NoIx) ix1).get(), getRow((Integer) ix2));
            }
            else if(ix2 instanceof NoIx)
            {
               return compareInteger(getRow((Integer) ix1), ((NoIx) ix2).get());
            }
            else
            {
               return compareInteger(getRow((Integer) ix1), getRow((Integer) ix2));
            }
         }
      };

      _intRange.beginInit();
      sortIx(comparator);
      _intRange.endInit();


   }


   private int compareInteger(Object o1, Object o2)
   {
      Integer i1 = null;
      if (null != o1)
      {
         i1 = ((Number)o1).intValue();
      }

      Integer i2 = null;
      if (null != o2)
      {
         i2 = ((Number)o2).intValue();
      }

      _intRange.init(i1);
      _intRange.init(i2);

      if(i1 == null && i2 != null)
      {
         return -1;
      }
      else if(i1 != null && i2 == null)
      {
         return 1;
      }
      else if(i1 == null && i2 == null)
      {
         return 0;
      }

      return i1.compareTo(i2);

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

      return "" + ((Integer)endData - (Integer)beginData);
   }

   private Object onGetMid(Object min, Object max)
   {

      Integer intMin;
      if(null == min)
      {
         intMin = _intRange.getMin() - 1;
      }
      else
      {
         intMin = ((Number)min).intValue();
      }

      Integer intMax;
      if(null == max)
      {
         intMax = _intRange.getMin() - 1;
      }
      else
      {
         intMax = ((Number)max).intValue();
      }


      return intMin + ((intMax - intMin) / 2);
   }

   private static class IntegerRange
   {
      private int _max = 0;
      private int _min = 0;
      private boolean _initializing;

      private boolean _isInit;


      public void init(Integer i)
      {

         if(false == _initializing)
         {
            return;
         }

         if(null == i)
         {
            return;
         }


         if(false == _isInit)
         {
            _min = i;
            _max = i;
            _isInit = true;
         }

         _min = Math.min(_min, i);
         _max = Math.max(_max, i);

      }

      public int getMin()
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
