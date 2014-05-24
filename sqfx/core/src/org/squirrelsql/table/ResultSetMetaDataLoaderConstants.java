package org.squirrelsql.table;

public enum ResultSetMetaDataLoaderConstants
{
   COLUMN_INDEX("Column index"),
   GET_COLUMN_NAME("getColumnName"),
   GET_COLUMN_TYPE_NAME("getColumnTypeName"),
   GET_COLUMN_TYPE("getColumnType"),
   GET_COLUMN_CLASS_NAME("getColumnClassName"),
   IS_NULLABLE("isNullable"),
   GET_COLUMN_LABEL("getColumnLabel"),
   GET_PRECISION("getPrecision"),
   GET_SCALE("getScale"),
   GET_TABLE_NAME("getTableName"),
   GET_SCHEMA_NAME("getSchemaName"),
   GET_CATALOG_NAME("getCatalogName"),
   GET_COLUMN_DISPLAY_SIZE("getColumnDisplaySize"),
   IS_AUTOINCREMENT("isAutoIncrement"),
   IS_CASESENSITIVE("isCaseSensitive"),
   IS_CURRENCY("isCurrency"),
   IS_WRITABLE("isWritable"),
   IS_DEFINITELY_WRITABLE("isDefinitelyWritable"),
   IS_READONLY("isReadOnly"),
   IS_SEARCHABLE("isSearchable"),
   IS_SIGNED("isSigned");
   private String _metaDataColumnName;


   ResultSetMetaDataLoaderConstants(String metaDataColumnName)
   {
      _metaDataColumnName = metaDataColumnName;
   }

   public String getMetaDataColumnName()
   {
      return _metaDataColumnName;
   }
}
