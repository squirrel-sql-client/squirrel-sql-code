package org.squirrelsql.table;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class TableLoaderFactory
{
   public static TableLoader loadDataFromResultSet(ResultSet res)
   {
      try
      {
         TableLoader tableLoader = new TableLoader();

         ResultSetMetaData metaData = res.getMetaData();
         for(int i=1; i <= metaData.getColumnCount(); ++i)
         {
            tableLoader.addColumn(metaData.getColumnName(i));
         }

         while(res.next())
         {
            ArrayList row = new ArrayList();

            for(int i=1; i <= metaData.getColumnCount(); ++i)
            {
               row.add(res.getObject(i));
            }

            tableLoader.addRow(row);
         }

         return tableLoader;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }
}
