package org.squirrelsql.session.completion;

import org.squirrelsql.session.ProcedureInfo;
import org.squirrelsql.session.schemainfo.StructItemSchema;

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
      return _procedureInfo.getName();
   }

   @Override
   public String getObjectTypeName()
   {
      return "PROCEDURE";
   }
}
