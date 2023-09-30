package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.dialects.DialectUtils;
import net.sourceforge.squirrel_sql.fw.dialects.DialectUtils2;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.SelectSQLInfo;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlscript.prefs.SQLScriptPreferencesManager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class ScriptUtil
{
   private static ILogger s_log = LoggerController.createLogger(ScriptUtil.class);

   public static String createSelectScriptString(IDatabaseObjectInfo[] dbObjs, IObjectTreeAPI objectTreeAPI)
   {
      List<SelectSQLInfo> sqls = createSelectSQLs(dbObjs, objectTreeAPI);

      StringBuilder script = new StringBuilder();
      sqls.forEach(sql -> script.append(sql.getSelectStatement()).append(getStatementSeparator(objectTreeAPI.getSession())).append('\n'));

      return script.append("\n").toString();
   }

   public static List<SelectSQLInfo> createSelectSQLs(IDatabaseObjectInfo[] dbObjs, IObjectTreeAPI objectTreeAPI)
   {
      try
      {
         ArrayList<SelectSQLInfo> ret = new ArrayList<>();
         ISQLConnection conn = objectTreeAPI.getSession().getSQLConnection();

         boolean isJdbcOdbc = conn.getSQLMetaData().getURL().startsWith("jdbc:odbc:");
         if (isJdbcOdbc)
         {
            Main.getApplication().getMessageHandler().showErrorMessage("CreateSelectScriptCommand.JDBC_ODBCBridge.warn");
         }

         for (IDatabaseObjectInfo dbObj : dbObjs)
         {
            if (false == dbObj instanceof ITableInfo)
            {
               continue;
            }

            StringBuilder buf = new StringBuilder();

            ITableInfo ti = (ITableInfo) dbObj;

            buf.append("SELECT ");

            TableColumnInfo[] infos = conn.getSQLMetaData().getColumnInfo(ti);
            for (int i = 0; i < infos.length; i++)
            {
               if (0 < i)
               {
                  buf.append(',');
               }

               DialectType dialectType = DialectFactory.getDialectType(objectTreeAPI.getSession().getMetaData());

               if (SQLScriptPreferencesManager.getPreferences().isUseDoubleQuotes())
               {
                  buf.append(getColumnName(infos[i], SQLScriptPreferencesManager.getPreferences().isUseDoubleQuotes()));
               }
               else
               {
                  // Former version before Preferences.isUseDoubleQuotes() was respected.
                  // Maybe this whole if-else should simply be replaced by return ScriptUtil.getColumnName(infos[i]);
                  buf.append(DialectUtils2.checkColumnDoubleQuotes(dialectType, infos[i].getColumnName()));
               }
            }

            buf.append(" FROM ").append(getTableName(ti));
            ret.add(new SelectSQLInfo(ti, buf.toString()));
            buf.setLength(0);

         }
         return ret;
      }
      catch (Exception e)
      {
         Main.getApplication().getMessageHandler().showErrorMessage(e);
         throw Utilities.wrapRuntime(e);
      }
   }

   /**
    * This method provides unique column names.
    * Use a new instance of this class for
    * every metadata result set
    */
   public static String getColumnDef(String sColumnName, String sType, int columnSize, int decimalDigits)
   {
      sColumnName = makeColumnNameUnique(sColumnName);

      return SQLUtilities.createColumnDefinitionString(sColumnName, sType, columnSize, decimalDigits);
   }

   /**
    * This method provides unique column names.
    * Use a new instance of this class for
    * every meta data result set.
    *
    *
    */
   public static String makeColumnNameUnique(String sColumnName)
   {
      Hashtable<String, String> uniqueColNamesRef = new Hashtable<>();

      return makeColumnNameUniqueIntern(uniqueColNamesRef, sColumnName, 0);
   }

   private static String makeColumnNameUniqueIntern(Hashtable<String, String> uniqueColNamesRef, String sColumnName, int postFixSeed)
   {

      String upperCaseColumnName = sColumnName.toUpperCase();
      String sRet = sColumnName;

      if(0 < postFixSeed)
      {
         sRet += "_" + postFixSeed;
         upperCaseColumnName += "_" + postFixSeed;
      }

      if(null == uniqueColNamesRef.get(upperCaseColumnName))
      {
         uniqueColNamesRef.put(upperCaseColumnName,upperCaseColumnName);
         return sRet;
      }
      else
      {
         return makeColumnNameUniqueIntern(uniqueColNamesRef, sColumnName, ++postFixSeed);
      }
   }

   public static String getStatementSeparator(ISession session)
   {
      String statementSeparator = session.getQueryTokenizer().getSQLStatementSeparator();

      if (1 < statementSeparator.length())
      {
         statementSeparator = "\n" + statementSeparator + "\n";
      }

      return statementSeparator;
   }

   /**
    * Use specified preferences to decide whether or not to "qualify" the 
    * specified table with it's schema/catalog.  If schema is not available then
    * catalog will be used. If neither is available, then only the table name 
    * will be used.
    * 
    * @param prefs
    * @param ti
    * @return
    */
   public static String getTableName(ITableInfo ti) 
   {
      return getTableName(ti, SQLScriptPreferencesManager.getPreferences().isQualifyTableNames(), SQLScriptPreferencesManager.getPreferences().isUseDoubleQuotes());
   }

   public static String getTableName(ITableInfo ti, boolean qualifyTableNames, boolean useDoubleQuotes)
   {
      return DialectUtils.formatQualified(ti.getSimpleName(), ti.getSchemaName(), qualifyTableNames, useDoubleQuotes);
   }

   public static String getColumnName(TableColumnInfo info)
   {
      return getColumnName(info, SQLScriptPreferencesManager.getPreferences().isUseDoubleQuotes());
   }

   public static String getColumnName(TableColumnInfo info, boolean useDoubleQuotes)
   {
      return getColumnName(info.getColumnName(), useDoubleQuotes);
   }

   public static String getColumnName(String columnName)
   {
      return getColumnName(columnName, SQLScriptPreferencesManager.getPreferences().isUseDoubleQuotes());
   }

   public static String getColumnName(String columnName, boolean useDoubleQuotes)
   {
      final String unquotedColumnName = StringUtilities.stripDoubleQuotes(columnName);

      if(useDoubleQuotes)
      {
         return "\"" + unquotedColumnName + "\"";
      }
      else
      {
         return unquotedColumnName;
      }
   }


   public static boolean isQualifyTableRequired()
   {
      return SQLScriptPreferencesManager.getPreferences().isQualifyTableNames();
   }
}
