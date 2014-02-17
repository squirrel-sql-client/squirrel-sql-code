package org.squirrelsql.session.completion;

import org.squirrelsql.session.schemainfo.StructItemSchema;

/**
 * Created by gerd on 16.02.14.
 */
public class CompletorUtil
{
   public static String getCatalogSchemaString(StructItemSchema schema)
   {
      String ret = "";

      if(null != schema.getCatalog())
      {
         ret += schema.getCatalog() + ".";
      }

      if(null != schema.getSchema())
      {
         ret += schema.getSchema() + ".";
      }
      return ret;
   }
}
