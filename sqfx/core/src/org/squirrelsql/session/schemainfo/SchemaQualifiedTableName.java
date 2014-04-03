package org.squirrelsql.session.schemainfo;

public class SchemaQualifiedTableName
{
   private final String _schema;
   private final String _tableName;

   public SchemaQualifiedTableName(String schema, String tableName)
   {
      _schema = schema;
      _tableName = tableName;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SchemaQualifiedTableName that = (SchemaQualifiedTableName) o;

      if (_schema != null ? !_schema.equals(that._schema) : that._schema != null) return false;
      if (_tableName != null ? !_tableName.equals(that._tableName) : that._tableName != null) return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      int result = _schema != null ? _schema.hashCode() : 0;
      result = 31 * result + (_tableName != null ? _tableName.hashCode() : 0);
      return result;
   }
}
