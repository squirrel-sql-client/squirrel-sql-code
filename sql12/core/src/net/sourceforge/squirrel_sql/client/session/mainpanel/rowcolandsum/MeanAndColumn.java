package net.sourceforge.squirrel_sql.client.session.mainpanel.rowcolandsum;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

public class MeanAndColumn
{
   private ColumnDisplayDefinition _columnDisplayDefinition;
   private Double _mean;

   public MeanAndColumn(ColumnDisplayDefinition columnDisplayDefinition, double mean)
   {
      _columnDisplayDefinition = columnDisplayDefinition;
      _mean = mean;
   }

   public ColumnDisplayDefinition getColumnDisplayDefinition()
   {
      return _columnDisplayDefinition;
   }


   public Double getMean()
   {
      return _mean;
   }
}
