package org.squirrelsql.session.completion;

import org.squirrelsql.session.schemainfo.StructItemSchema;

/**
 * Created by gerd on 16.02.14.
 */
public class SchemaCompletionCandidate extends CompletionCandidate
{
   private StructItemSchema _schema;

   public SchemaCompletionCandidate(StructItemSchema schema)
   {
      _schema = schema;
   }

   @Override
   public String getReplacement()
   {
      String ret = "";

      if( null != _schema.getCatalog())
      {
         ret += _schema.getCatalog();
      }

      return ret + _schema.getSchema();
   }


}
