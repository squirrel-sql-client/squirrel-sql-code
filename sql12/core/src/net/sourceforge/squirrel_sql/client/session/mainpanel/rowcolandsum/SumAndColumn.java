package net.sourceforge.squirrel_sql.client.session.mainpanel.rowcolandsum;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

public class SumAndColumn
{
   private ColumnDisplayDefinition _columnDisplayDefinition;

   private Double _doubleSum;
   private Long _longSum;

   public SumAndColumn(ColumnDisplayDefinition columnDisplayDefinition, long longSum)
   {
      _columnDisplayDefinition = columnDisplayDefinition;
      _longSum = longSum;
   }

   public SumAndColumn(ColumnDisplayDefinition columnDisplayDefinition, double doubleSum)
   {
      _columnDisplayDefinition = columnDisplayDefinition;
      _doubleSum = doubleSum;
   }

   public ColumnDisplayDefinition getColumnDisplayDefinition()
   {
      return _columnDisplayDefinition;
   }

   public Double getDoubleSum()
   {
      return _doubleSum;
   }

   public Long getLongSum()
   {
      return _longSum;
   }

   public Object getSum()
   {
      if (null == _longSum)
      {
         return Double.valueOf(_doubleSum);
      }
      else
      {
         return Long.valueOf(_longSum);
      }
   }
}
