package org.squirrelsql.session;

import org.squirrelsql.table.ResultSetMetaDataLoaderConstants;
import org.squirrelsql.table.TableLoader;

import java.util.ArrayList;
import java.util.List;

public class ColumnInfo
{
   private int _columnIndex;
   private final String _colName;
   private final int _colType;
   private final String _colTypeName;
   private final Integer _colSize;
   private Integer _decDigits;
   private final boolean _nullable;
   private final String _remarks;

   public ColumnInfo(int columnIndex, String colName, int colType, String colTypeName, Integer colSize, Integer decDigits, boolean nullable, String remarks)
   {
      _columnIndex = columnIndex;
      _colName = colName;
      _colType = colType;
      _colTypeName = colTypeName;
      _colSize = colSize;
      _decDigits = decDigits;
      _nullable = nullable;
      _remarks = remarks;
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

   private String getSizes()
   {
      if(null == _colSize)
      {
        return "";
      }

      return "(" + _colSize + (null == _decDigits || 0 == _decDigits ? "" : "," + _decDigits) + ")";

   }

   public static List<ColumnInfo> createColumnInfosFromTableMetaData(TableLoader tableColumnMetaDataTableLoader)
   {
      List<ColumnInfo> ret = new ArrayList<>();

      for (int i = 0; i < tableColumnMetaDataTableLoader.size(); i++)
      {
         int columnIndex = i;

         String colName = tableColumnMetaDataTableLoader.getCellAsString(ColumnMetaProps.COLUMN_NAME.getPropName(), i);
         int colType = tableColumnMetaDataTableLoader.getCellAsInt(ColumnMetaProps.DATA_TYPE.getPropName(), i);
         String colTypeName = tableColumnMetaDataTableLoader.getCellAsString(ColumnMetaProps.TYPE_NAME.getPropName(), i);
         Integer colSize = tableColumnMetaDataTableLoader.getCellAsInteger(ColumnMetaProps.COLUMN_SIZE.getPropName(), i);
         Integer decDigits = tableColumnMetaDataTableLoader.getCellAsInteger(ColumnMetaProps.DECIMAL_DIGITS.getPropName(), i);

         String nullablePropValue = tableColumnMetaDataTableLoader.getCellAsString(ColumnMetaProps.IS_NULLABLE.getPropName(), i);
         boolean nullable = ColumnMetaProps.isYes(nullablePropValue);

         String remarks = tableColumnMetaDataTableLoader.getCellAsString(ColumnMetaProps.REMARKS.getPropName(), i);

         ret.add(new ColumnInfo(columnIndex, colName, colType, colTypeName, colSize, decDigits, nullable, remarks));

      }
      return ret;
   }


   public static List<ColumnInfo> createColumnInfosFromResultMetaData(TableLoader resultMetaDataTableLoader)
   {
      List<ColumnInfo> ret = new ArrayList<>();

      for (int i = 0; i < resultMetaDataTableLoader.size(); i++)
      {
         int columnIndex = resultMetaDataTableLoader.getCellAsInt(ResultSetMetaDataLoaderConstants.COLUMN_INDEX.getMetaDataColumnName(), i);
         String colName = resultMetaDataTableLoader.getCellAsString(ResultSetMetaDataLoaderConstants.GET_COLUMN_NAME.getMetaDataColumnName(), i);
         int colType = resultMetaDataTableLoader.getCellAsInt(ResultSetMetaDataLoaderConstants.GET_COLUMN_TYPE.getMetaDataColumnName(), i);
         String colTypeName = resultMetaDataTableLoader.getCellAsString(ResultSetMetaDataLoaderConstants.GET_COLUMN_TYPE_NAME.getMetaDataColumnName(), i);
         Integer colSize = resultMetaDataTableLoader.getCellAsInteger(ResultSetMetaDataLoaderConstants.GET_PRECISION.getMetaDataColumnName(), i);
         Integer decDigits = resultMetaDataTableLoader.getCellAsInteger(ResultSetMetaDataLoaderConstants.GET_SCALE.getMetaDataColumnName(), i);

         String nullablePropValue = resultMetaDataTableLoader.getCellAsString(ResultSetMetaDataLoaderConstants.IS_NULLABLE.getMetaDataColumnName(), i);
         boolean nullable = ColumnMetaProps.isYes(nullablePropValue);

         String remarks = null;

         ret.add(new ColumnInfo(columnIndex, colName, colType, colTypeName, colSize, decDigits, nullable, remarks));

      }
      return ret;
   }
}
