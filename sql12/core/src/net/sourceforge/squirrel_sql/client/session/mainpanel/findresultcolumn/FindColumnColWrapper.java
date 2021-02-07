package net.sourceforge.squirrel_sql.client.session.mainpanel.findresultcolumn;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;

public class FindColumnColWrapper
{
   private ExtTableColumn _extTableColumn;
   private boolean _dataSetContainsdifferentTables;

   public FindColumnColWrapper(ExtTableColumn extTableColumn, boolean dataSetContainsdifferentTables)
   {
      _extTableColumn = extTableColumn;
      _dataSetContainsdifferentTables = dataSetContainsdifferentTables;
   }

   @Override
   public String toString()
   {
      String tableNamePostFix = "";

      if(_dataSetContainsdifferentTables && null != TableNameAccess.getTableName(_extTableColumn))
      {
         tableNamePostFix = "  (" + TableNameAccess.getTableName(_extTableColumn) + ")";
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

   public String getMatchString(boolean findInTableNames)
   {
      if (findInTableNames)
      {
         return toString();
      }
      else
      {
         return _extTableColumn.getColumnDisplayDefinition().getColumnName();
      }
   }
}
