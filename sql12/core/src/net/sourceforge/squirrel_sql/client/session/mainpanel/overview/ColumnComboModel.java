package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.DataScaleTable;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.IndexedColumnFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import java.util.ArrayList;

public class ColumnComboModel
{
   private ColumnDisplayDefinition _columnDefinition;
   private int _columnIndexInDataScale;

   public ColumnComboModel(ColumnDisplayDefinition columnDefinition, int columnIndexInDataScale)
   {
      _columnDefinition = columnDefinition;
      _columnIndexInDataScale = columnIndexInDataScale;
   }

   public static ColumnComboModel[] createColumnComboModels(DataScaleTable dataScaleTable, boolean numbersOnly)
   {
      ArrayList<ColumnComboModel> ret = new ArrayList<ColumnComboModel>();

      for (int i = 0; i < dataScaleTable.getColumnDisplayDefinitions().length; i++)
      {
         ColumnDisplayDefinition columnDisplayDefinition = dataScaleTable.getColumnDisplayDefinitions()[i];

         if(numbersOnly && false == IndexedColumnFactory.isNumber(columnDisplayDefinition))
         {
            continue;
         }

         ret.add(new ColumnComboModel(columnDisplayDefinition, i));
      }

      return ret.toArray(new ColumnComboModel[ret.size()]);
   }

   @Override
   public String toString()
   {
      return _columnDefinition.getColumnName();
   }

   @Override
   public int hashCode()
   {
      return (_columnDefinition.getFullTableColumnName() + "+++" +_columnIndexInDataScale).hashCode();
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ColumnComboModel that = (ColumnComboModel) o;
      return _columnDefinition.getFullTableColumnName().equals(that._columnDefinition.getFullTableColumnName()) && _columnIndexInDataScale == that._columnIndexInDataScale;
   }

   public ColumnDisplayDefinition getColumnDisplayDefinition()
   {
      return _columnDefinition;
   }

   public int getColumnIndexInDataScale()
   {
      return _columnIndexInDataScale;
   }
}
