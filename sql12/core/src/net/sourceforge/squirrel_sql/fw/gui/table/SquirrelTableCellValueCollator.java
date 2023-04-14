package net.sourceforge.squirrel_sql.fw.gui.table;

import java.text.Collator;

public class SquirrelTableCellValueCollator
{
   private final Collator _collator = Collator.getInstance();

   public SquirrelTableCellValueCollator()
   {
      _collator.setStrength(Collator.PRIMARY);
      _collator.setStrength(Collator.TERTIARY);
   }

   /**
    *
    * @param iAscending Pass 1 for ascending and -1 for descending
    */
   public int compareTableCellValues(Object data1, Object data2, int iAscending, boolean allDataIsString, boolean nullIsHighest)
   {
      try
      {
         if (data1 == null && data2 == null)
         {
            return 0;
         }
         if (data1 == null)
         {
            return (nullIsHighest ? 1 : -1) * iAscending;
         }
         if (data2 == null)
         {
            return (nullIsHighest ? -1 : 1) * iAscending;
         }
//				Comparable c1 = (Comparable)data1;
//				return c1.compareTo(data2) * _iAscending;

         if (!allDataIsString)
         {
            final Comparable c1 = (Comparable)data1;
            return c1.compareTo(data2) * iAscending;
         }
         //				return _collator.compare(data1.toString(), data2.toString()) * _iAscending;
         return _collator.compare((String)data1, (String)data2) * iAscending;
      }
      catch (ClassCastException ex)
      {
         return data1.toString().compareTo(data2.toString()) * iAscending;
      }
   }

}
