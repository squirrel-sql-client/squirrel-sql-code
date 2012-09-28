package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import java.util.Comparator;
import java.util.List;

public class BooleanIndexedColumn extends IndexedColumn
{

   public BooleanIndexedColumn(List<Object[]> rows, int colIx, ColumnDisplayDefinition colDef)
   {
      super(rows, colIx, colDef);

      Comparator comparator = new Comparator<Object>()
      {
         @Override
         public int compare(Object ix1, Object ix2)
         {
            if( ix1 instanceof NoIx && ix2 instanceof NoIx)
            {
               return compareBoolean(((NoIx) ix1).get(), ((NoIx) ix2).get());
            }
            else if(ix1 instanceof NoIx)
            {
               return compareBoolean(((NoIx) ix1).get(), getRow((Integer) ix2));
            }
            else if(ix2 instanceof NoIx)
            {
               return compareBoolean(getRow((Integer) ix1), ((NoIx) ix2).get());
            }
            else
            {
               return compareBoolean(getRow((Integer) ix1), getRow((Integer) ix2));
            }
         }
      };

      sortIx(comparator);

   }


   private int compareBoolean(Object o1, Object o2)
   {
      Boolean b1 = asBoolean(o1);

      Boolean b2 = asBoolean(o2);


      if(b1 == null && b2 != null)
      {
         return -1;
      }
      else if(b1 != null && b2 == null)
      {
         return 1;
      }
      else if(b1 == null && b2 == null)
      {
         return 0;
      }

      return b1.compareTo(b2);

   }

   private Boolean asBoolean(Object o)
   {
      Boolean b = null;
      if (null != o)
      {
         if(o instanceof Number)
         {
            b = 0 != ((Number)o).intValue();
         }
         else
         {
            b = (Boolean)o;
         }
      }
      return b;
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
      return null;
   }

   private Object onGetMid(Object min, Object max)
   {
      return asBoolean(min) && asBoolean(max);
   }
}
