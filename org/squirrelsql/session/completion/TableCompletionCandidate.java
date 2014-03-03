package org.squirrelsql.session.completion;

import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.schemainfo.StructItemSchema;

public class TableCompletionCandidate extends CompletionCandidate
{
   private TableInfo _tableInfo;
   private StructItemSchema _schema;

   public TableCompletionCandidate(TableInfo tableInfo, StructItemSchema schema)
   {
      _tableInfo = tableInfo;
      _schema = schema;
   }

   @Override
   public String getReplacement()
   {

      return CompletorUtil.getCatalogSchemaPrefix(_schema) + _tableInfo.getName();
   }

}
