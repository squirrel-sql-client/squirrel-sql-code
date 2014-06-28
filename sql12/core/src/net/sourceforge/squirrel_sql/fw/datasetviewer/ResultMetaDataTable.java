package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;

public class ResultMetaDataTable
{
   private final String _catalogName;
   private final String _schemaName;
   private final String _tableName;

   public ResultMetaDataTable(String catalogName, String schemaName, String tableName)
   {
      _catalogName = catalogName;
      _schemaName = schemaName;
      _tableName = tableName;
   }

   public String getCatalogName()
   {
      return _catalogName;
   }

   public String getSchemaName()
   {
      return _schemaName;
   }

   public String getTableName()
   {
      return _tableName;
   }

   public String getQualifiedName()
   {
      return SQLUtilities.getQualifiedTableName(_catalogName, _schemaName, _tableName);
   }
}
