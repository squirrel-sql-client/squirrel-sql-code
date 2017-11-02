package net.sourceforge.squirrel_sql.client.session.mainpanel.findcolumn;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class FindColumnColWrapper
{
   private ExtTableColumn _extTableColumn;

   public FindColumnColWrapper(ExtTableColumn extTableColumn)
   {
      _extTableColumn = extTableColumn;
   }

   @Override
   public String toString()
   {
      String tableNamePostFix = "";

      if(false == StringUtilities.isEmpty(_extTableColumn.getColumnDisplayDefinition().getTableName(), true))
      {
         tableNamePostFix = " (" + _extTableColumn.getColumnDisplayDefinition().getTableName() + ")";
      }


      return _extTableColumn.getColumnDisplayDefinition().getColumnName() + tableNamePostFix;
   }

   public ExtTableColumn getExtTableColumn()
   {
      return _extTableColumn;
   }


   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      FindColumnColWrapper that = (FindColumnColWrapper) o;

      return _extTableColumn != null ? _extTableColumn.equals(that._extTableColumn) : that._extTableColumn == null;
   }

   @Override
   public int hashCode()
   {
      return _extTableColumn != null ? _extTableColumn.hashCode() : 0;
   }
}
