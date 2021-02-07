package net.sourceforge.squirrel_sql.client.session.mainpanel.findresultcolumn;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class TableNameAccess
{
   static String getTableName(ExtTableColumn extTableColumn)
   {
      if(false == StringUtilities.isEmpty(extTableColumn.getColumnDisplayDefinition().getTableName(), true))
      {
         return extTableColumn.getColumnDisplayDefinition().getTableName();
      }
      else if(   null != extTableColumn.getColumnDisplayDefinition().getResultMetaDataTable()
              && false == StringUtilities.isEmpty(extTableColumn.getColumnDisplayDefinition().getResultMetaDataTable().getTableName(), true))
      {
         return extTableColumn.getColumnDisplayDefinition().getResultMetaDataTable().getTableName();
      }

      return null;
   }
}
