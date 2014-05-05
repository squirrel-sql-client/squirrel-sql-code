package org.squirrelsql.session;

import java.io.Serializable;

public class DBSchema implements Serializable
{
   private static final long serialVersionUID = -2545218384101456392L;

   private final String _schema;
   private final String _catalog;

   public DBSchema(String schema, String catalog)
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
}
