package net.sourceforge.squirrel_sql.fw.sql;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class TableQualifier
{
   private String _catalog;
   private String _schema;
   private String _tableName;

   public TableQualifier(String catalog, String schema, String tableName)
   {
      _catalog = catalog;
      _schema = schema;
      _tableName = tableName;
   }

   public TableQualifier(String name)
   {
      String[] splits = name.split("\\.");

      List<String> list = Arrays.asList(splits);

      Collections.reverse(list);

      _tableName = list.get(0);

      if(1 < list.size())
      {
         _schema = list.get(1);
      }

      if(2 < list.size())
      {
         _catalog = list.get(2);
      }
   }

   public String getQualifiedTableName(boolean quoted)
   {
      return SQLUtilities.getQualifiedTableName(_catalog, _schema, _tableName, quoted);
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

   public boolean matches(TableQualifier tableQualifier)
   {
      return    StringUtils.equalsIgnoreCase(_catalog, tableQualifier._catalog)
             && StringUtils.equalsIgnoreCase(_schema, tableQualifier._schema)
             && StringUtils.equalsIgnoreCase(_tableName, tableQualifier._tableName);
   }
}
