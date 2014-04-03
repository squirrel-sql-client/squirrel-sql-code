package org.squirrelsql.session.schemainfo;

import org.squirrelsql.services.I18n;
import org.squirrelsql.services.JDBCTypeMapper;
import org.squirrelsql.services.sqlwrap.SQLConnection;
import org.squirrelsql.table.TableLoader;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class DataTypesLoader
{

   public static TableLoader loadTypes(SQLConnection sqlConnection)
   {
      try
      {
         ResultSet res = sqlConnection.getDatabaseMetaData().getTypeInfo();

         ResultSetMetaData resMetaData = res.getMetaData();

         TableLoader tableLoader = new TableLoader();
         int columnCount = resMetaData.getColumnCount();
         for (int i = 1; i <= columnCount; i++)
         {
            tableLoader.addColumn(resMetaData.getColumnLabel(i));
         }


         while(res.next())
         {
            tableLoader.addRow(getNextRow(res, columnCount));
         }
         return tableLoader;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private static ArrayList getNextRow(ResultSet rs, int columnCount)
   {
      try
      {
         I18n i18n = new I18n(DataTypesLoader.class);

         ArrayList row = new ArrayList();
         for (int idx = 1; idx <= columnCount; ++idx)
         {
            switch (idx)
            {
               case 2:
                  // DATA_TYPE column of result set.
                  // int data = _rs.getInt(idx);
                  int data = rs.getInt(idx);
                  StringBuilder sBuf = new StringBuilder();
                  sBuf.append(String.valueOf(data))
                        .append(" [")
                        .append(JDBCTypeMapper.getJdbcTypeName(data))
                        .append("]");
                  row.add(sBuf.toString());
                  break;

               case 3:
               case 14:
               case 15:
               case 18:
                  Object oBuf18 = rs.getObject(idx);
                  if (oBuf18 != null && !(oBuf18 instanceof Integer))
                  {
                     if (oBuf18 instanceof Number)
                     {
                        row.add(((Number) oBuf18).intValue());
                     }
                     else
                     {
                        row.add(new Integer(oBuf18.toString()));
                     }
                  }
                  else
                  {
                     row.add(oBuf18);
                  }
                  break;

               case 7:
                  // NULLABLE column of result set.
                  short nullable = rs.getShort(idx);
                  switch (nullable)
                  {
                     case DatabaseMetaData.typeNoNulls:
                        row.add(i18n.t("DatabaseMetaData.nullableTypeNoNulls"));
                        break;
                     case DatabaseMetaData.typeNullable:
                        row.add(i18n.t("DatabaseMetaData.nullableTypeNullable"));
                        break;
                     case DatabaseMetaData.typeNullableUnknown:
                        row.add(i18n.t("DatabaseMetaData.nullableTypeNullableUnknown"));
                        break;
                     default:
                        row.add(nullable + "[error]");
                        break;
                  }
                  break;

               case 8:
               case 10:
               case 11:
               case 12:
                  // boolean columns
                  // _row[i] = _rs.getBoolean(idx) ? "true" : "false";
                  Object oBuf12 = rs.getObject(idx);
                  if (oBuf12 != null && !(oBuf12 instanceof Boolean))
                  {
                     if (oBuf12 instanceof Number)
                     {
                        if (((Number) oBuf12).intValue() == 0)
                        {
                           row.add(Boolean.FALSE);
                        }
                        else
                        {
                           row.add(Boolean.TRUE);
                        }
                     }
                     else
                     {
                        row.add(Boolean.valueOf(oBuf12.toString()));
                     }
                  }
                  else
                  {
                     row.add(oBuf12);
                  }
                  break;

               case 9:
                  // SEARCHABLE column of result set.
                  short searchable = rs.getShort(idx);
                  switch (searchable)
                  {
                     case DatabaseMetaData.typePredNone:
                        row.add(i18n.t("DatabaseMetaData.searchableTypePredNone"));
                        break;
                     case DatabaseMetaData.typePredChar:
                        row.add(i18n.t("DatabaseMetaData.searchableTypePredChar"));
                        break;
                     case DatabaseMetaData.typePredBasic:
                        row.add(i18n.t("DatabaseMetaData.searchableTypePredBasic"));
                        break;
                     case DatabaseMetaData.typeSearchable:
                        row.add(i18n.t("DatabaseMetaData.searchableTypeSearchable"));
                        break;
                     default:
                        row.add(searchable + "[error]");
                        break;
                  }
                  break;

               case 16:
               case 17:
                  row.add("unused");
                  break;

               default:
                  row.add(rs.getString(idx));
                  break;

            }
         }
         return row;
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }


}
