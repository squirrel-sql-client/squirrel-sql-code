package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

public class ColumnComboModel
{
   private ColumnDisplayDefinition _columnDefinition;

   public ColumnComboModel(ColumnDisplayDefinition columnDefinition)
   {
      _columnDefinition = columnDefinition;
   }

   public static ColumnComboModel[] createColumnComboModels(ColumnDisplayDefinition[] columnDefinitions)
   {
      ColumnComboModel[] ret = new ColumnComboModel[columnDefinitions.length];

      for (int i = 0; i < columnDefinitions.length; i++)
      {
         ret[i] = new ColumnComboModel(columnDefinitions[i]);
      }

      return ret;
   }

   @Override
   public String toString()
   {
      return _columnDefinition.getColumnName();
   }

   @Override
   public int hashCode()
   {
      return _columnDefinition.getFullTableColumnName().hashCode();
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ColumnComboModel that = (ColumnComboModel) o;
      return _columnDefinition.getFullTableColumnName().equals(that._columnDefinition.getFullTableColumnName());
   }
}
