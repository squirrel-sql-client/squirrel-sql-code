package org.squirrelsql.session.schemainfo;

import org.squirrelsql.services.CaseInsensitiveString;

public class SchemaQualifiedTableName
{
   private final CaseInsensitiveString _schema;
   private final CaseInsensitiveString _tableName;

   public SchemaQualifiedTableName(String schema, String tableName)
   {
      _schema = (null == schema ? null : new CaseInsensitiveString(schema));
      _tableName = new CaseInsensitiveString(tableName);
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
