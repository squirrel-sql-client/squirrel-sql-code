package org.squirrelsql.session.completion;

import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.schemainfo.StructItemSchema;

public class TableCompletionCandidate extends CompletionCandidate
{
   private TableInfo _tableInfo;
   private StructItemSchema _schema;
   private boolean _showQualifiedHint;

   public TableCompletionCandidate(TableInfo tableInfo, StructItemSchema schema)
   {
      _tableInfo = tableInfo;
      _schema = schema;
   }

   @Override
   public String getReplacement()
   {
      return _tableInfo.getName();
   }

   @Override
   public String getObjectTypeName()
   {
      String ret;

      if (TableTypes.TABLE.toString().equalsIgnoreCase(_tableInfo.getTableType()))
      {
         ret = TableTypes.TABLE.toString();
      }
      else if (TableTypes.VIEW.toString().equalsIgnoreCase(_tableInfo.getTableType()))
      {
         ret = TableTypes.VIEW.toString();
      }
      else
      {
         ret = "TABLE/" + _tableInfo.getTableType();
      }

      if(_showQualifiedHint)
      {
         ret += " in " + CompletorUtil.getCatalogSchemaString(_tableInfo.getStructItemSchema());
      }

      return ret;
   }

   public TableInfo getTableInfo()
   {
      return _tableInfo;
   }

   public String getSimpleName()
   {
      return _tableInfo.getName();
   }

   public void setShowQualifiedHint(boolean showQualifiedHint)
   {
      _showQualifiedHint = showQualifiedHint;
   }
}
