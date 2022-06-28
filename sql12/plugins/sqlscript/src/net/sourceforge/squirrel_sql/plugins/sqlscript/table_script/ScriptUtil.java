package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.plugins.sqlscript.prefs.SQLScriptPreferencesManager;

import java.util.Hashtable;


public class ScriptUtil
{

   private Hashtable<String, String> _uniqueColNames = new Hashtable<>();

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

   public static boolean isQualifyTableRequired()
   {
      return SQLScriptPreferencesManager.getPreferences().isQualifyTableNames();
   }
}
