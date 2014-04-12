package org.squirrelsql.aliases;

import org.squirrelsql.services.SQLUtil;
import org.squirrelsql.session.schemainfo.StructItemCatalog;
import org.squirrelsql.session.schemainfo.StructItemProcedureType;
import org.squirrelsql.session.schemainfo.StructItemSchema;
import org.squirrelsql.session.schemainfo.StructItemTableType;

public class AliasPropertiesSchema
{
   private String _qualifiedSchemaName;
   private String _catalogName;

   public AliasPropertiesSchema(StructItemSchema schema)
   {
      _qualifiedSchemaName = schema.getQualifiedName();
   }

   public AliasPropertiesSchema(StructItemCatalog catalog)
   {
      _catalogName = catalog.getCatalog();
   }

   public AliasPropertiesSchema() // For deserialization only
   {
   }


   public boolean matches(StructItemTableType structItemTableType)
   {
      if(null != _qualifiedSchemaName)
      {
         String qualifiedSchema = SQLUtil.getQualifiedName(structItemTableType.getCatalog(), structItemTableType.getSchema());
         return _qualifiedSchemaName.equalsIgnoreCase(qualifiedSchema);
      }
      else
      {
         return _catalogName.equalsIgnoreCase(structItemTableType.getCatalog());
      }
   }

   public boolean matches(StructItemProcedureType structItemProcedureType)
   {
      if(null != _qualifiedSchemaName)
      {
         String qualifiedSchema = SQLUtil.getQualifiedName(structItemProcedureType.getCatalog(), structItemProcedureType.getSchema());
         return _qualifiedSchemaName.equalsIgnoreCase(qualifiedSchema);
      }
      else
      {
         return _catalogName.equalsIgnoreCase(structItemProcedureType.getCatalog());
      }
   }


   @Override
   public String toString()
   {
      if (null == _qualifiedSchemaName)
      {
         return _catalogName + "[Catalog]";
      }
      else
      {
         return _qualifiedSchemaName;
      }
   }

   public String getQualifiedSchemaName()
   {
      return _qualifiedSchemaName;
   }

   public void setQualifiedSchemaName(String qualifiedSchemaName)
   {
      _qualifiedSchemaName = qualifiedSchemaName;
   }

   public String getCatalogName()
   {
      return _catalogName;
   }

   public void setCatalogName(String catalogName)
   {
      _catalogName = catalogName;
   }

}
