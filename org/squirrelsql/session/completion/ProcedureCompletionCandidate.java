package org.squirrelsql.session.completion;

import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.schemainfo.StructItemSchema;

/**
 * Created by gerd on 16.02.14.
 */
public class ProcedureCompletionCandidate extends CompletionCandidate
{
   private final ProcedureInfo _procedureInfo;
   private final StructItemSchema _schema;

   public ProcedureCompletionCandidate(ProcedureInfo procedureInfo, StructItemSchema schema)
   {
      _procedureInfo = procedureInfo;
      _schema = schema;
   }

   @Override
   public String getReplacement()
   {
      return CompletorUtil.getCatalogSchemaPrefix(_schema) + "." + _procedureInfo.getName();
   }
}
