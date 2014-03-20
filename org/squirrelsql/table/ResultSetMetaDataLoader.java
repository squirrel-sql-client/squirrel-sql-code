package org.squirrelsql.table;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class ResultSetMetaDataLoader
{
   public static TableLoader loadMetaData(ResultSet res)
   {
      try
      {

         TableLoader tl = new TableLoader();

         tl.addColumn("Column index");
         tl.addColumn("getColumnName");
         tl.addColumn("getColumnTypeName");
         tl.addColumn("getColumnType");
         tl.addColumn("getColumnClassName");
         tl.addColumn("isNullable");
         tl.addColumn("getColumnLabel");
         tl.addColumn("getPrecision");
         tl.addColumn("getScale");
         tl.addColumn("getTableName");
         tl.addColumn("getSchemaName");
         tl.addColumn("getCatalogName");
         tl.addColumn("getColumnDisplaySize");
         tl.addColumn("isAutoIncrement");
         tl.addColumn("isCaseSensitive");
         tl.addColumn("isCurrency");
         tl.addColumn("isWritable");
         tl.addColumn("isDefinitelyWritable");
         tl.addColumn("isReadOnly");
         tl.addColumn("isSearchable");
         tl.addColumn("isSigned");

         ResultSetMetaData md = res.getMetaData();
         for (int i = 1; i <= md.getColumnCount(); i++)
         {
            ArrayList row = new ArrayList();

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
