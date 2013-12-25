package org.squirrelsql.table;

import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.CollectionUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class TableLoaderFactory
{
   public static TableLoader loadDataFromResultSet(ResultSet res, String ... excludeColNames)
   {
      try
      {
         TableLoader tableLoader = new TableLoader();

         ResultSetMetaData metaData = res.getMetaData();
         for(int i=1; i <= metaData.getColumnCount(); ++i)
         {
            final String colName = metaData.getColumnName(i);
            if (false == CollectionUtil.contains(excludeColNames, (t) -> colName.equalsIgnoreCase(t)))
            {
               tableLoader.addColumn(colName);
            }
         }

         while(res.next())
         {
            ArrayList row = new ArrayList();

            for(int i=1; i <= metaData.getColumnCount(); ++i)
            {
               final String colName = metaData.getColumnName(i);

               if (false == CollectionUtil.contains(excludeColNames, (t) -> colName.equalsIgnoreCase(t)))
               {
                  row.add(res.getObject(i));
               }
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

   public static TableLoader loadDataFromSQL(DbConnectorResult dbConnectorResult, String sql, Integer maxResults)
   {
      try
      {
         Statement stat = dbConnectorResult.getSQLConnection().getConnection().createStatement();

         if (null != maxResults)
         {
            stat.setMaxRows(maxResults);
         }

         ResultSet res = stat.executeQuery(sql);
         return loadDataFromResultSet(res);
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }
}
