package org.squirrelsql.session.schemainfo;

/**
 * Created by gerd on 16.02.14.
 */
public class QualifiedTableName
{
   private final String _catalog;
   private final String _schema;
   private final String _name;

   public QualifiedTableName(String catalog, String schema, String name)
   {
      _catalog = catalog;
      _schema = schema;
      _name = name;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      QualifiedTableName that = (QualifiedTableName) o;

      if (_catalog != null ? !_catalog.equals(that._catalog) : that._catalog != null) return false;
      if (_name != null ? !_name.equals(that._name) : that._name != null) return false;
      if (_schema != null ? !_schema.equals(that._schema) : that._schema != null) return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      int result = _catalog != null ? _catalog.hashCode() : 0;
      result = 31 * result + (_schema != null ? _schema.hashCode() : 0);
      result = 31 * result + (_name != null ? _name.hashCode() : 0);
      return result;
   }
}
