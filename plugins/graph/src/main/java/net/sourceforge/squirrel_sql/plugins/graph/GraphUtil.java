package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphUtil
{
   public static ArrayList<ColumnInfo> createColumnInfos(ISession session, String catalog, String schema, String tableName)
   {
      try
      {
         SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();

         ArrayList<ColumnInfo> ret = new ArrayList<ColumnInfo>();
         TableColumnInfo[] infos = md.getColumnInfo(catalog, schema, tableName);

         for (int i = 0; i < infos.length; i++)
         {
            TableColumnInfo info = infos[i];
            String columnName = info.getColumnName();
            String columnType = info.getTypeName();
            int columnSize = info.getColumnSize();
            int decimalDigits = info.getDecimalDigits();
            boolean nullable = "YES".equalsIgnoreCase(info.isNullable());

            ColumnInfo colInfo = new ColumnInfo(columnName,
                                                columnType,
                                                columnSize,
                                                decimalDigits,
                                                nullable);

            ret.add(colInfo);

         }


         PrimaryKeyInfo[] pkinfos = md.getPrimaryKey(catalog, schema, tableName);

         for (int i = 0; i < pkinfos.length; i++)
         {
            PrimaryKeyInfo info = pkinfos[i];
            for (int j = 0; j < ret.size(); j++)
            {
               if (ret.get(j).getName().equalsIgnoreCase(info.getColumnName()))
               {
                  ret.get(j).markPrimaryKey();
               }
            }
         }

         return ret;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static ColumnInfo findColumnInfo(String colName, ColumnInfo[] colInfos)
   {
      return findColumnInfo(colName, Arrays.asList(colInfos));
   }

   public static ColumnInfo findColumnInfo(String colName, List<ColumnInfo> colInfos)
   {
      for (int i = 0; i < colInfos.size(); i++)
      {
         if(colInfos.get(i).getName().equalsIgnoreCase(colName))
         {
            return colInfos.get(i);
         }
      }

      throw new IllegalArgumentException("Column " + colName + " not found");
   }


   public static ColumnInfo createColumnInfo(ISession session, String pkCat, String pkSchem, String pkTable, String pkColName)
   {
      return findColumnInfo(pkColName, createColumnInfos(session, pkCat, pkSchem, pkTable));
   }

   public static boolean columnsMatch(ArrayList<ColumnInfo> cols1, ArrayList<ColumnInfo> cols2)
   {
      if(cols2.size() != cols1.size())
      {
         return false;
      }

      for (ColumnInfo pkCol : cols2)
      {
         boolean found = false;
         for (ColumnInfo otherPkCol : cols1)
         {
            if(pkCol.equals(otherPkCol))
            {
               found = true;
               break;
            }
         }

         if(false == found)
         {
            return false;
         }
      }

      return true;
   }

   public static String createGraphFileName(String url, String title)
   {
      return createGraphFileNamePrefixForUrl(url) + StringUtilities.javaNormalize(title);
   }

   public static String createGraphFileNamePrefixForUrl(String url)
   {
      return StringUtilities.javaNormalize(url) + ".";
   }
}
