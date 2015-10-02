package org.squirrelsql.session.schemainfo;

import org.squirrelsql.services.SQLUtil;

public class StructItemUDTType extends StructItem implements CatalogSchema
{
   private final String _catalog;
   private final String _schema;

   public StructItemUDTType(String catalog, String schema)
   {
      _catalog = catalog;
      _schema = schema;
   }

   @Override
   public boolean shouldLoad(SchemaCacheConfig schemaCacheConfig)
   {
      return schemaCacheConfig.shouldLoadUDTs(this);
   }


   public String getCatalog()
   {
      return _catalog;
   }

   public String getSchema()
   {
      return _schema;
   }


   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      StructItemUDTType that = (StructItemUDTType) o;

      if (_catalog != null ? !_catalog.equals(that._catalog) : that._catalog != null) return false;
      if (_schema != null ? !_schema.equals(that._schema) : that._schema != null) return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      int result = _catalog != null ? _catalog.hashCode() : 0;
      result = 31 * result + (_schema != null ? _schema.hashCode() : 0);
      return result;
   }

   public String getItemName()
   {
      return SQLUtil.getQualifiedName(_catalog, _schema) + " - UDT";
   }

}
