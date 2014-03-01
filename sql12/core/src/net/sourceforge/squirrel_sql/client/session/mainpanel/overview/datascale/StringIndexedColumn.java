package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import java.util.Comparator;
import java.util.List;


/**
 * This class can handle any type though the way it compares is inadequate for certain types. 
 */
public class StringIndexedColumn extends IndexedColumn
{
   private CharRange _charRange = new CharRange();

   public StringIndexedColumn(List<Object[]> rows, int colIx, ColumnDisplayDefinition colDef)
   {
      super(rows, colIx, colDef);

      Comparator comparator = new Comparator<Object>()
      {
         @Override
         public int compare(Object ix1, Object ix2)
         {
            if( ix1 instanceof NoIx && ix2 instanceof NoIx)
            {
               return compareString(((NoIx) ix1).get(), ((NoIx) ix2).get());
            }
            else if(ix1 instanceof NoIx)
            {
               return compareString(((NoIx) ix1).get(), getRow((Integer) ix2));
            }
            else if(ix2 instanceof NoIx)
            {
               return compareString(getRow((Integer) ix1), ((NoIx) ix2).get());
            }
            else
            {
               return compareString(getRow((Integer) ix1), getRow((Integer) ix2));
            }
         }
      };

      _charRange.beginInit();
      sortIx(comparator);
      _charRange.endInit();

   }

   private int compareString(Object o1, Object o2)
   {
      String s1 = null;
      if (null != o1)
      {
         s1 = o1.toString();
      }

      String s2 = null;
      if (null != o2)
      {
         s2 = o2.toString();
      }

      _charRange.init(s1);
      _charRange.init(s2);

      if(s1 == null && s2 != null)
      {
         return -1;
      }
      else if(s1 != null && s2 == null)
      {
         return 1;
      }
      else if(s1 == null && s2 == null)
      {
         return 0;
      }

      return s1.compareTo(s2);

   }


   public Calculator getCalculator()
   {
      return new StringCalculator(_charRange);
   }

   @Override
   public String calculateDist(Object beginData, Object endData)
   {
      return null;
   }
}
