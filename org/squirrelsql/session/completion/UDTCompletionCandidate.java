package org.squirrelsql.session.completion;

import org.squirrelsql.session.UDTInfo;
import org.squirrelsql.session.schemainfo.StructItemSchema;

/**
 * Created by gerd on 16.02.14.
 */
public class UDTCompletionCandidate extends CompletionCandidate
{
   private final UDTInfo _udtInfo;
   private final StructItemSchema _schema;

   public UDTCompletionCandidate(UDTInfo udtInfo, StructItemSchema schema)
   {
      _udtInfo = udtInfo;
      _schema = schema;
   }

   @Override
   public String getReplacement()
   {
      return CompletorUtil.getCatalogSchemaString(_schema) + "." + _udtInfo.getName();
   }
}
