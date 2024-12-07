package net.sourceforge.squirrel_sql.client.session.mainpanel.rowcolandsum;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

public class DeviationAndColumn
{
   private ColumnDisplayDefinition _columnDisplayDefinition;
   private Double _deviation;

   public DeviationAndColumn(ColumnDisplayDefinition columnDisplayDefinition, double deviation)
   {
      _columnDisplayDefinition = columnDisplayDefinition;
      _deviation = deviation;
   }

   public ColumnDisplayDefinition getColumnDisplayDefinition()
   {
      return _columnDisplayDefinition;
   }


   public Double getDeviation()
   {
      return _deviation;
   }
}
