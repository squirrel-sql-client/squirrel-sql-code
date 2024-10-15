package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class TableNameAccess
{
   public static String getTableName(ExtTableColumn extTableColumn)
   {
      return getTableName(extTableColumn.getColumnDisplayDefinition());
   }

   public static String getTableName(ColumnDisplayDefinition columnDisplayDefinition)
   {
      if(false == StringUtilities.isEmpty(columnDisplayDefinition.getTableName(), true))
      {
         return columnDisplayDefinition.getTableName();
      }
      else if(   null != columnDisplayDefinition.getResultMetaDataTable()
              && false == StringUtilities.isEmpty(columnDisplayDefinition.getResultMetaDataTable().getTableName(), true))
      {
         return columnDisplayDefinition.getResultMetaDataTable().getTableName();
      }

      return null;
   }
}
