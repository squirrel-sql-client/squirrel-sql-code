package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TimestampIndexedColumn extends IndexedColumn
{
   private LongRange _longRange = new LongRange();

   public TimestampIndexedColumn(List<Object[]> rows, int colIx, ColumnDisplayDefinition colDef)
   {
      super(rows, colIx, colDef);

      Comparator comparator = new Comparator<Object>()
      {
         @Override
         public int compare(Object ix1, Object ix2)
         {
            if( ix1 instanceof NoIx && ix2 instanceof NoIx)
            {
               return compareUtilDate(((NoIx) ix1).get(), ((NoIx) ix2).get());
            }
            else if(ix1 instanceof NoIx)
            {
               return compareUtilDate(((NoIx) ix1).get(), getRow((Integer) ix2));
            }
            else if(ix2 instanceof NoIx)
            {
               return compareUtilDate(getRow((Integer) ix1), ((NoIx) ix2).get());
            }
            else
            {
               return compareUtilDate(getRow((Integer) ix1), getRow((Integer) ix2));
            }
         }
      };

      _longRange.beginInit();
      sortIx(comparator);
      _longRange.endInit();

   }


   private int compareUtilDate(Object o1, Object o2)
   {
      Long t1 = null;
      if (null != o1)
      {
         t1 = ((Date)o1).getTime();
      }

      Long t2 = null;
      if (null != o2)
      {
         t2 = ((Date)o2).getTime();
      }

      _longRange.init(t1);
      _longRange.init(t2);


      if(t1 == null && t2 != null)
      {
         return -1;
      }
      else if(t1 != null && t2 == null)
      {
         return 1;
      }
      else if(t1 == null && t2 == null)
      {
         return 0;
      }

      return t1.compareTo(t2);

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

   private Object onGetMid(Object min, Object max)
   {

      Long longMin;
      if(null == min)
      {
         longMin = _longRange.getMin() - 1;
      }
      else
      {
         longMin = ((Date)min).getTime();
      }

      Long longMax;
      if(null == max)
      {
         longMax = _longRange.getMin() - 1;
      }
      else
      {
         longMax = ((Date)max).getTime();
      }


      return new Timestamp(longMin + ((longMax - longMin) / 2));
   }

   private static class LongRange
   {
      private long _max = System.currentTimeMillis();
      private long _min = _max;
      private boolean _initializing;
      private boolean _isInit;

      public void init(Long l)
      {
         if(false == _initializing)
         {
            return;
         }


         if(null == l)
         {
            return;
         }


         if(false == _isInit)
         {
            _min = l;
            _max = l;
            _isInit = true;
         }

         _min = Math.min(_min, l);
         _max = Math.max(_max, l);

      }

      public long getMin()
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