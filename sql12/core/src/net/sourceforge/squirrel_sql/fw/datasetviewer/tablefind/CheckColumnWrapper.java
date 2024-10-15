package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.TableNameAccess;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class CheckColumnWrapper
{
   private final ColumnDisplayDefinition _cdd;

   private boolean _toSearch = true;

   public CheckColumnWrapper(ColumnDisplayDefinition cdd)
   {
      _cdd = cdd;
   }

   String getColumnName()
   {
      String tableName = TableNameAccess.getTableName(_cdd);

      if(StringUtilities.isEmpty(tableName, true))
      {
         return _cdd.getColumnName();
      }
      else
      {
         return _cdd.getColumnName() + " (" + tableName + ")";
      }
   }

   public ColumnDisplayDefinition getColumnDisplayDefinition()
   {
      return _cdd;
   }

   public boolean isToSearch()
   {
      return _toSearch;
   }

   public void setToSearch(boolean toSearch)
   {
      _toSearch = toSearch;
   }
}
