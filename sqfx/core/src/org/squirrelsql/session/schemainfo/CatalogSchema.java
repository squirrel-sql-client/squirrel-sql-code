package org.squirrelsql.session.schemainfo;

public interface CatalogSchema
{
   String getCatalog();
   String getSchema();

   default boolean matchesRespectNull(String catalog, String schema)
   {
      return   (null == catalog || catalog.equalsIgnoreCase(getCatalog()))
            && (null == schema || schema.equalsIgnoreCase(getSchema()));

   }
}
