package net.sourceforge.squirrel_sql.fw.sql.databasemetadata;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class SQLSchema implements Comparable<SQLSchema>
{
   private final String _schema;
   private final String _catalog;

   public SQLSchema(String schema, String catalog)
   {
      _schema = schema;
      _catalog = catalog;
   }

   public String getSchema()
   {
      return _schema;
   }

   public String getCatalog()
   {
      return _catalog;
   }


   @Override
   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      if(o == null || getClass() != o.getClass())
      {
         return false;
      }
      SQLSchema sqlSchema = (SQLSchema) o;
      return Objects.equals(_catalog, sqlSchema._catalog) && Objects.equals(_schema, sqlSchema._schema);
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(_catalog, _schema);
   }

   @Override
   public int compareTo(SQLSchema o)
   {
      if(null == o)
      {
         return -1;
      }

      int ret = StringUtils.compareIgnoreCase(_catalog, o._catalog);
      if( 0 != ret )
      {
         return ret;
      }
      return StringUtils.compareIgnoreCase(_schema, o._schema);

   }
}
