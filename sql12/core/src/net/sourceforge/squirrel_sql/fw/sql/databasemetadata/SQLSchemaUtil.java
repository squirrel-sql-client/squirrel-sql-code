package net.sourceforge.squirrel_sql.fw.sql.databasemetadata;

import java.util.List;

public class SQLSchemaUtil
{
   public static SQLSchema ofSchemaName(String schema)
   {
      return new SQLSchema(schema, null);
   }

   public static SQLSchema ofSchemaAndCatalog(String schema, String catalog)
   {
      return new SQLSchema(schema, catalog);
   }

   public static String[] toSchemaNameArray(List<SQLSchema> schemas)
   {
      return schemas.stream().map(s -> s.getSchema()).toArray(String[]::new);
   }
}
