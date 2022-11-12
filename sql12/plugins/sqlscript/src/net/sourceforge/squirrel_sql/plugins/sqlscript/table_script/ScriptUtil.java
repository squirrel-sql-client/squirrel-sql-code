package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.dialects.DialectUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.plugins.sqlscript.prefs.SQLScriptPreferencesManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;


public class ScriptUtil
{

   private Hashtable<String, String> _uniqueColNames = new Hashtable<>();

   /**
    * Create a {@link Statement} that will stream the result instead of loading into the memory.
    *
    * @param connection the connection to use
    * @param dialectType
    * @return A Statement, that will stream the result.
    * @throws SQLException
    * @see http://javaquirks.blogspot.com/2007/12/mysql-streaming-result-set.html
    * @see http://dev.mysql.com/doc/refman/5.0/en/connector-j-reference-implementation-notes.html
    */
   public static Statement createStatementForStreamingResults(Connection connection, DialectType dialectType) throws SQLException
   {
      Statement stmt;

      if (DialectType.MYSQL5 == dialectType)
      {
         /*
          * MYSQL will load the whole result into memory. To avoid this, we must use the streaming mode.
          *
          * http://javaquirks.blogspot.com/2007/12/mysql-streaming-result-set.html
          * http://dev.mysql.com/doc/refman/5.0/en/connector-j-reference-implementation-notes.html
          */
         stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
         stmt.setFetchSize(Integer.MIN_VALUE);
      }
      else
      {
         stmt = connection.createStatement();
      }
      return stmt;

   }

   /**
    * This method provides unique column names.
    * Use a new instance of this class for
    * every meta data result set
    */
   public String getColumnDef(String sColumnName, String sType, int columnSize, int decimalDigits)
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
   public String makeColumnNameUnique(String sColumnName)
   {
      return makeColumnNameUniqueIntern(sColumnName, 0);
   }

   private String makeColumnNameUniqueIntern(String sColumnName, int postFixSeed)
   {
      String upperCaseColumnName = sColumnName.toUpperCase();
      String sRet = sColumnName;

      if(0 < postFixSeed)
      {
         sRet += "_" + postFixSeed;
         upperCaseColumnName += "_" + postFixSeed;
      }

      if(null == _uniqueColNames.get(upperCaseColumnName))
      {
         _uniqueColNames.put(upperCaseColumnName,upperCaseColumnName);
         return sRet;
      }
      else
      {
         return makeColumnNameUniqueIntern(sColumnName, ++postFixSeed);
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
