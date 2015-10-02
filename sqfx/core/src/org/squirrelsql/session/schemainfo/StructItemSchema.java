package org.squirrelsql.session.schemainfo;

import org.squirrelsql.services.SQLUtil;

import java.util.List;

public class StructItemSchema extends StructItem
{
   private final String _schema;
   private final String _catalogName;

   public StructItemSchema(String schema, String catalogName)
   {

      _schema = schema;
      _catalogName = catalogName;
   }

   public String getCatalog()
   {
      return _catalogName;
   }

   public String getSchema()
   {
      return _schema;
   }


   public String getQualifiedName()
   {
      return SQLUtil.getQualifiedName(_catalogName, _schema);
   }


   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      StructItemSchema that = (StructItemSchema) o;

      if (_catalogName != null ? !_catalogName.equals(that._catalogName) : that._catalogName != null) return false;
      if (_schema != null ? !_schema.equals(that._schema) : that._schema != null) return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      int result = _schema != null ? _schema.hashCode() : 0;
      result = 31 * result + (_catalogName != null ? _catalogName.hashCode() : 0);
      return result;
   }

   public String getItemName()
   {
      return "Schema " + SQLUtil.getQualifiedName(_catalogName, _schema);
   }


}
