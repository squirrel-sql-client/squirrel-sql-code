package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import java.util.Hashtable;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.plugins.sqlscript.prefs.SQLScriptPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.sqlscript.prefs.SQLScriptPreferencesManager;


public class ScriptUtil
{
	
   private static SQLScriptPreferenceBean prefs = 
	   							SQLScriptPreferencesManager.getPreferences();
    
   Hashtable<String, String> _uniqueColNames = new Hashtable<String, String>();

   /**
    * This method provides unique column names.
    * Use a new instance of this class for
    * every meta data result set
    */
   public String getColumnDef(String sColumnName, String sType, int columnSize, int decimalDigits)
   {
      String decimalDigitsString = 0 == decimalDigits ? "" : "," + decimalDigits;

      sColumnName = makeColumnNameUnique(sColumnName);

      StringBuffer sbColDef = new StringBuffer();
      String sLower = sType.toLowerCase();
      sbColDef.append(sColumnName).append(" ");
      sbColDef.append(sType);

      if (sLower.indexOf("char") != -1)
      {
         sbColDef.append("(");
         sbColDef.append(columnSize).append(decimalDigitsString);
         sbColDef.append(")");
      }
      else if (sLower.equals("numeric"))
      {
         sbColDef.append("(");
         sbColDef.append(columnSize).append(decimalDigitsString);
         sbColDef.append(")");
      }
      else if (sLower.equals("number"))
      {
         sbColDef.append("(");
         sbColDef.append(columnSize).append(decimalDigitsString);
         sbColDef.append(")");
      }
      else if (sLower.equals("decimal"))
      {
         sbColDef.append("(");
         sbColDef.append(columnSize).append(decimalDigitsString);
         sbColDef.append(")");
      }

      return sbColDef.toString();
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
	   String result = ti.getSimpleName();
	   if (prefs.isQualifyTableNames()) { 
		   result = ti.getQualifiedName();
	   }
	   return result;
   }

}
