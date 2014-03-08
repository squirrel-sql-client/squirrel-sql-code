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

   @Override
   public String getObjectTypeName()
   {
      if (TableTypes.TABLE.toString().equalsIgnoreCase(_tableInfo.getTableType()))
      {
         return TableTypes.TABLE.toString();
      }
      else if (TableTypes.VIEW.toString().equalsIgnoreCase(_tableInfo.getTableType()))
      {
         return TableTypes.VIEW.toString();
      }
      else
      {
         return "TABLE/" + _tableInfo.getTableType();
      }
   }

   public TableInfo getTableInfo()
   {
      return _tableInfo;
   }
}
