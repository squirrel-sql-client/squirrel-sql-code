package org.squirrelsql.session;

import org.squirrelsql.session.schemainfo.FullyQualifiedTableName;
import org.squirrelsql.table.ResultSetMetaDataLoaderConstants;
import org.squirrelsql.table.TableLoader;

import java.util.ArrayList;
import java.util.List;

public class ColumnInfo
{
   private final String _tableName;
   private final String _schemaName;
   private final String _catalogName;
   private int _columnIndex;
   private final String _colName;
   private final int _colType;
   private final String _colTypeName;
   private final Integer _colSize;
   private Integer _decDigits;
   private final boolean _nullable;
   private final String _remarks;

   public ColumnInfo(String tableName, String schemaName, String catalogName, int columnIndex, String colName, int colType, String colTypeName, Integer colSize, Integer decDigits, boolean nullable, String remarks)
   {
      _tableName = tableName;
      _schemaName = schemaName;
      _catalogName = catalogName;
      _columnIndex = columnIndex;
      _colName = colName;
      _colType = colType;
      _colTypeName = colTypeName;
      _colSize = colSize;
      _decDigits = decDigits;
      _nullable = nullable;
      _remarks = remarks;
   }

   public String getTableName()
   {
      return _tableName;
   }

   public String getSchemaName()
   {
      return _schemaName;
   }

   public String getCatalogName()
   {
      return _catalogName;
   }

   public int getColumnIndex()
   {
      return _columnIndex;
   }

   public Integer getDecDigits()
   {
      return _decDigits;
   }

   public String getColName()
   {
      return _colName;
   }

   public int getColType()
   {
      return _colType;
   }

   public String getColTypeName()
   {
      return _colTypeName;
   }

   public Integer getColSize()
   {
      return _colSize;
   }

   public boolean isNullable()
   {
      return _nullable;
   }

   public String getRemarks()
   {
      return _remarks;
   }

   public String getDescription()
   {
      return _colName + " " + _colTypeName + getSizes() + " " + (_nullable ? "NULL" : "NOT NULL");
   }

   public String getFullTableName()
   {
      return new FullyQualifiedTableName(_catalogName, _schemaName, _tableName).toString();
   }

   public String getFullTableColumnName()
   {
      return getFullTableName() + "." + _colName;
   }

   private String getSizes()
   {
      if(null == _colSize)
      {
        return "";
      }

      return "(" + _colSize + (null == _decDigits || 0 == _decDigits ? "" : "," + _decDigits) + ")";

   }

   public static List<ColumnInfo> createColumnInfosFromTableMetaData(TableInfo tableInfo, TableLoader tableColumnMetaDataTableLoader)
   {
      List<ColumnInfo> ret = new ArrayList<>();

      for (int i = 0; i < tableColumnMetaDataTableLoader.size(); i++)
      {
         int columnIndex = i;

         String colName = ColumnMetaProps.COLUMN_NAME.getCellAsString(tableColumnMetaDataTableLoader, i);
         int colType = ColumnMetaProps.DATA_TYPE.getCellAsInt(tableColumnMetaDataTableLoader, i);
         String colTypeName = ColumnMetaProps.TYPE_NAME.getCellAsString(tableColumnMetaDataTableLoader, i);
         Integer colSize = ColumnMetaProps.COLUMN_SIZE.getCellAsInteger(tableColumnMetaDataTableLoader, i);
         Integer decDigits = ColumnMetaProps.DECIMAL_DIGITS.getCellAsInteger(tableColumnMetaDataTableLoader, i);

         String nullablePropValue = ColumnMetaProps.IS_NULLABLE.getCellAsString(tableColumnMetaDataTableLoader, i);
         boolean nullable = ColumnMetaProps.isYes(nullablePropValue);

         String remarks = ColumnMetaProps.REMARKS.getCellAsString(tableColumnMetaDataTableLoader, i);

         ret.add(new ColumnInfo(tableInfo.getName(), tableInfo.getSchema(), tableInfo.getCatalog(), columnIndex, colName, colType, colTypeName, colSize, decDigits, nullable, remarks));

      }
      return ret;
   }


   public static List<ColumnInfo> createColumnInfosFromResultMetaData(TableLoader resultMetaDataTableLoader)
   {
      List<ColumnInfo> ret = new ArrayList<>();

      for (int i = 0; i < resultMetaDataTableLoader.size(); i++)
      {
         String tableName = resultMetaDataTableLoader.getCellAsString(ResultSetMetaDataLoaderConstants.GET_TABLE_NAME.getMetaDataColumnName(), i);
         String schemaName = resultMetaDataTableLoader.getCellAsString(ResultSetMetaDataLoaderConstants.GET_SCHEMA_NAME.getMetaDataColumnName(), i);
         String catalogName = resultMetaDataTableLoader.getCellAsString(ResultSetMetaDataLoaderConstants.GET_CATALOG_NAME.getMetaDataColumnName(), i);

         int columnIndex = resultMetaDataTableLoader.getCellAsInt(ResultSetMetaDataLoaderConstants.COLUMN_INDEX.getMetaDataColumnName(), i);
         String colName = resultMetaDataTableLoader.getCellAsString(ResultSetMetaDataLoaderConstants.GET_COLUMN_NAME.getMetaDataColumnName(), i);
         int colType = resultMetaDataTableLoader.getCellAsInt(ResultSetMetaDataLoaderConstants.GET_COLUMN_TYPE.getMetaDataColumnName(), i);
         String colTypeName = resultMetaDataTableLoader.getCellAsString(ResultSetMetaDataLoaderConstants.GET_COLUMN_TYPE_NAME.getMetaDataColumnName(), i);
         Integer colSize = resultMetaDataTableLoader.getCellAsInteger(ResultSetMetaDataLoaderConstants.GET_PRECISION.getMetaDataColumnName(), i);
         Integer decDigits = resultMetaDataTableLoader.getCellAsInteger(ResultSetMetaDataLoaderConstants.GET_SCALE.getMetaDataColumnName(), i);

         String nullablePropValue = resultMetaDataTableLoader.getCellAsString(ResultSetMetaDataLoaderConstants.IS_NULLABLE.getMetaDataColumnName(), i);
         boolean nullable = ColumnMetaProps.isYes(nullablePropValue);

         String remarks = null;

         ret.add(new ColumnInfo(tableName, schemaName, catalogName, columnIndex, colName, colType, colTypeName, colSize, decDigits, nullable, remarks));

      }
      return ret;
   }

   public boolean matches(ColumnInfo columnInfo)
   {
      return getFullTableColumnName().equals(columnInfo.getFullTableColumnName());
   }

}
