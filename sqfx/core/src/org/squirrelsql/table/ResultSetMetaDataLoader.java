package org.squirrelsql.table;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultSetMetaDataLoader
{
   public static TableLoader loadMetaData(ResultSet res)
   {
      try
      {

         TableLoader tl = new TableLoader();

         tl.addColumn(ResultSetMetaDataLoaderConstants.COLUMN_INDEX.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.GET_COLUMN_NAME.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.GET_COLUMN_TYPE_NAME.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.GET_COLUMN_TYPE.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.GET_COLUMN_CLASS_NAME.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.IS_NULLABLE.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.GET_COLUMN_LABEL.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.GET_PRECISION.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.GET_SCALE.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.GET_TABLE_NAME.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.GET_SCHEMA_NAME.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.GET_CATALOG_NAME.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.GET_COLUMN_DISPLAY_SIZE.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.IS_AUTOINCREMENT.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.IS_CASESENSITIVE.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.IS_CURRENCY.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.IS_WRITABLE.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.IS_DEFINITELY_WRITABLE.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.IS_READONLY.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.IS_SEARCHABLE.getMetaDataColumnName());
         tl.addColumn(ResultSetMetaDataLoaderConstants.IS_SIGNED.getMetaDataColumnName());

         ResultSetMetaData md = res.getMetaData();
         for (int i = 1; i <= md.getColumnCount(); i++)
         {
            List row = new ArrayList();

            row.add(i); // Column index
            row.add(md.getColumnName(i)); // getColumnName
            row.add(md.getColumnTypeName(i)); // getColumnTypeName
            row.add(md.getColumnType(i)); // getColumnType
            row.add(md.getColumnClassName(i)); // getColumnClassName
            row.add(md.isNullable(i)); // isNullable
            row.add(md.getColumnLabel(i)); // getColumnLabel
            row.add(md.getPrecision(i)); // getPrecision
            row.add(md.getScale(i)); // getScale
            row.add(md.getTableName(i)); // getTableName
            row.add(md.getSchemaName(i)); // getSchemaName
            row.add(md.getCatalogName(i)); // getCatalogName
            row.add(md.getColumnDisplaySize(i)); // getColumnDisplaySize
            row.add(md.isAutoIncrement(i)); // isAutoIncrement
            row.add(md.isCaseSensitive(i)); // isCaseSensitive
            row.add(md.isCurrency(i)); // isCurrency
            row.add(md.isWritable(i)); // isWritable
            row.add(md.isDefinitelyWritable(i)); // isDefinitelyWritable
            row.add(md.isReadOnly(i)); // isReadOnly
            row.add(md.isSearchable(i)); // isSearchable
            row.add(md.isSigned(i)); // isSigned

            tl.addRow(row);
         }


         return tl;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }
}
