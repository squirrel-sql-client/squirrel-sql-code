package net.sourceforge.squirrel_sql.fw.sql;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ColumnQualifier
{
   private String _catalog;
   private String _schema;
   private String _tableName;
   private String _columnName;

   public ColumnQualifier(String name)
   {
      String[] splits = StringUtils.split(name, ".");

      List<String> list = Arrays.asList(splits);

      Collections.reverse(list);

      _columnName = list.get(0);
      if(1 < list.size())
      {
         _tableName = list.get(1);
      }

      if(2 < list.size())
      {
         _schema = list.get(2);
      }

      if(3 < list.size())
      {
         _catalog = list.get(3);
      }
   }

   public String getCatalog()
   {
      return _catalog;
   }

   public String getSchema()
   {
      return _schema;
   }

   public String getTableName()
   {
      return _tableName;
   }

   public String getColumnName()
   {
      return _columnName;
   }
}
