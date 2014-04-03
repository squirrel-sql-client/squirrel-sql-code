package org.squirrelsql.session.schemainfo;

import org.squirrelsql.aliases.Alias;
import org.squirrelsql.drivers.DriversUtil;
import org.squirrelsql.drivers.SQLDriver;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.SQLUtil;
import org.squirrelsql.services.sqlwrap.SQLConnection;
import org.squirrelsql.table.TableLoader;

import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;

public class DataBaseMetaDataLoader
{
   private final static HashSet<String> _ignoreMethods = new HashSet<>();
   static
   {
      _ignoreMethods.add("getCatalogs");
      _ignoreMethods.add("getConnection");
      _ignoreMethods.add("getSchemas");
      _ignoreMethods.add("getTableTypes");
      _ignoreMethods.add("getTypeInfo");
      _ignoreMethods.add("fail");
      _ignoreMethods.add("hashCode");
      _ignoreMethods.add("toString");
      _ignoreMethods.add("getNumericFunctions");
      _ignoreMethods.add("getStringFunctions");
      _ignoreMethods.add("getSystemFunctions");
      _ignoreMethods.add("getTimeDateFunctions");
      _ignoreMethods.add("getSQLKeywords");
   }


   private static final String UNSUPPORTED = "unsupported";

   public static TableLoader loadMetaData(Alias alias, SQLConnection sqlConnection)
   {
      I18n i18n = new I18n(DataBaseMetaDataLoader.class);


      TableLoader tableLoader = new TableLoader();
      tableLoader.addColumn(i18n.t("objecttree.details.alias.metadata.propertyName"));
      tableLoader.addColumn(i18n.t("objecttree.details.alias.metadata.value"));

      SQLDriver driver = DriversUtil.findDriver(alias.getDriverId());

      tableLoader.addRow("JDBC Driver CLASSNAME", driver.getDriverClassName());
      tableLoader.addRow("JDBC Driver CLASSPATH", DriversUtil.getJarFileNamesListString(driver));
      tableLoader.addRow("getTimeOpened", new Date());

      DatabaseMetaData md = sqlConnection.getDatabaseMetaData();

      Method[] methods = DatabaseMetaData.class.getMethods();
      for (int i = 0; i < methods.length; ++i)
      {
         final Method method = methods[i];
         if (method.getParameterTypes().length == 0
               && method.getReturnType() != Void.TYPE
               && false ==_ignoreMethods.contains(method.getName()))
         {
            tableLoader.addRow(generateLine(md, method));
         }
      }
      return tableLoader;
   }

   private static Object[] generateLine(DatabaseMetaData md, Method getter)
   {
      try
      {
         final Object[] line = new Object[2];
         line[0] = getter.getName();
         if (line[0].equals("getDefaultTransactionIsolation"))
         {
            line[1] = UNSUPPORTED;
            final int isol = md.getDefaultTransactionIsolation();
            switch (isol)
            {
               case java.sql.Connection.TRANSACTION_NONE:
               {
                  line[1] = "TRANSACTION_NONE";
                  break;
               }
               case java.sql.Connection.TRANSACTION_READ_COMMITTED:
               {
                  line[1] = "TRANSACTION_READ_COMMITTED";
                  break;
               }
               case java.sql.Connection.TRANSACTION_READ_UNCOMMITTED:
               {
                  line[1] = "TRANSACTION_READ_UNCOMMITTED";
                  break;
               }
               case java.sql.Connection.TRANSACTION_REPEATABLE_READ:
               {
                  line[1] = "TRANSACTION_REPEATABLE_READ";
                  break;
               }
               case java.sql.Connection.TRANSACTION_SERIALIZABLE:
               {
                  line[1] = "TRANSACTION_SERIALIZABLE";
                  break;
               }
               default:
               {
                  line[1] = "" + isol + "?";
                  break;
               }
            }

         }
         else if (line[0].equals("getClientInfoProperties"))
         {
            Object obj = executeGetter(md, getter);
            if (obj instanceof ResultSet)
            {
               ResultSet rs = (ResultSet) obj;
               try
               {
                  StringBuilder tmp = new StringBuilder();
                  while (rs.next())
                  {
                     tmp.append(rs.getString(1)).append("\t");
                     tmp.append(rs.getInt(2)).append("\t");
                     tmp.append(rs.getString(3)).append("\t");
                     tmp.append(rs.getString(4)).append("\n");
                  }
                  line[1] = tmp.toString();
               }
               finally
               {
                  SQLUtil.close(rs);
               }
            }
            else
            {
               line[1] = obj;
            }
         }
         else
         {
            Object obj = executeGetter(md, getter);
            line[1] = obj;
         }
         return line;
      }
      catch (SQLException e)
      {
         throw  new RuntimeException(e);
      }
   }

   protected static Object executeGetter(Object bean, Method getter)
   {
      try
      {
         return getter.invoke(bean, (Object[])null);
      }
      catch (Throwable th)
      {
         return UNSUPPORTED;
      }
   }

   public static TableLoader loadNumericFunctions(SQLConnection sqlConnection)
   {
      try
      {
         I18n i18n = new I18n(SchemaCache.class);
         DatabaseMetaData databaseMetaData = sqlConnection.getDatabaseMetaData();
         return buildCommaSeparatedTable(i18n.t("schemacache.numeric.functions"), databaseMetaData.getNumericFunctions());
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static TableLoader loadStringFunctions(SQLConnection sqlConnection)
   {
      try
      {
         I18n i18n = new I18n(SchemaCache.class);
         DatabaseMetaData databaseMetaData = sqlConnection.getDatabaseMetaData();
         return buildCommaSeparatedTable(i18n.t("schemacache.string.functions"), databaseMetaData.getStringFunctions());
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }




   private static TableLoader buildCommaSeparatedTable(String columnName, String commaSepString) throws SQLException
   {
      TableLoader table = new TableLoader();

      table.addColumn(columnName);

      for (String fct : commaSepString.split(","))
      {
         table.addRow(fct);
      }

      return table;
   }


   public static TableLoader loadSystemFunctions(SQLConnection sqlConnection)
   {
      try
      {
         I18n i18n = new I18n(SchemaCache.class);
         DatabaseMetaData databaseMetaData = sqlConnection.getDatabaseMetaData();
         return buildCommaSeparatedTable(i18n.t("schemacache.system.functions"), databaseMetaData.getSystemFunctions());
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static TableLoader loadTimeDateFunctions(SQLConnection sqlConnection)
   {
      try
      {
         I18n i18n = new I18n(SchemaCache.class);
         DatabaseMetaData databaseMetaData = sqlConnection.getDatabaseMetaData();
         return buildCommaSeparatedTable(i18n.t("schemacache.timeDate.functions"), databaseMetaData.getTimeDateFunctions());
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public static TableLoader loadKeyWords(SQLConnection sqlConnection)
   {
      try
      {
         I18n i18n = new I18n(SchemaCache.class);
         DatabaseMetaData databaseMetaData = sqlConnection.getDatabaseMetaData();
         return buildCommaSeparatedTable(i18n.t("schemacache.keywords"), databaseMetaData.getSQLKeywords());
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }
}
