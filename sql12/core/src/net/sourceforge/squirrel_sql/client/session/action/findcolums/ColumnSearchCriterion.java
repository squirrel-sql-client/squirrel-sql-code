package net.sourceforge.squirrel_sql.client.session.action.findcolums;

import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;
import net.sourceforge.squirrel_sql.client.session.schemainfo.FilterMatcher;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class ColumnSearchCriterion
{
   private boolean _findInObjectName;
   private boolean _findInColumnName;
   private boolean _findInColumnTypeName;
   private boolean _findInRemarks;

   private FilterMatcher _fullMatcher;

   private FilterMatcher _columnMatcher;
   private FilterMatcher _tableMatcher;

   public void setFilterString(String filterString)
   {
      _fullMatcher = null;
      _columnMatcher = null;
      _tableMatcher = null;

      if(StringUtilities.isEmpty(filterString, true))
      {
         return;
      }


      _fullMatcher = new FilterMatcher(prepareSearchString(filterString), null);

      final String[] splits = filterString.split("\\.");
      if(1 < splits.length)
      {
         _columnMatcher = new FilterMatcher(prepareSearchString(splits[splits.length - 1]), null);
         _tableMatcher = new FilterMatcher(prepareSearchString(splits[splits.length - 2]), null);
      }
   }

   private String prepareSearchString(String filterString)
   {
      if (false == filterString.contains("%") && false == filterString.contains("_"))
      {
         return "%" + filterString.toLowerCase().trim() + "%";
      }
      else
      {
         return filterString.toLowerCase().trim();
      }
   }

   public void setFindInObjectName(boolean findInObjectName)
   {
      _findInObjectName = findInObjectName;
   }

   public void setFindInColumnName(boolean findInColumnName)
   {
      _findInColumnName = findInColumnName;
   }

   public void setFindInColumnTypeName(boolean findInColumnTypeName)
   {
      _findInColumnTypeName = findInColumnTypeName;
   }

   public void setFindInRemarks(boolean findInRemarks)
   {
      _findInRemarks = findInRemarks;
   }

   public boolean matches(ExtendedColumnInfo columnInfo)
   {
      if(null == _fullMatcher)
      {
         // Allows to list all columns
         return true;
      }

      if(_findInColumnTypeName && null != columnInfo.getColumnType() && _fullMatcher.matches(columnInfo.getColumnType().toLowerCase()))
      {
         return true;
      }
      else if(_findInRemarks && null != columnInfo.getRemarks() && _fullMatcher.matches(columnInfo.getRemarks().toLowerCase()))
      {
         return true;
      }
      else if (_findInObjectName && null != columnInfo.getSimpleTableName() && _fullMatcher.matches(columnInfo.getSimpleTableName().toLowerCase()))
      {
         return true;
      }
      else if (_findInColumnName && null != columnInfo.getColumnName() && _fullMatcher.matches(columnInfo.getColumnName().toLowerCase()))
      {
         return true;
      }
      else if (   _findInObjectName && _findInColumnName
               && null != _columnMatcher && null != _tableMatcher
               && null != columnInfo.getColumnName() && null != columnInfo.getSimpleTableName()
               && _columnMatcher.matches(columnInfo.getColumnName().toLowerCase()) && _tableMatcher.matches(columnInfo.getSimpleTableName().toLowerCase()) )
      {
         return true;
      }


      return false;
   }
}
