package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.props.Props;

public class TableExportPreferencesDAO
{
   private static final String PREF_KEY_FILE = "SquirrelSQL.csvexport.csvfile";
   private static final String PREF_KEY_ENCODING = "SquirrelSQL.csvexport.csvencoding";
   private static final String PREF_KEY_WITH_HEADERS = "SquirrelSQL.csvexport.withColumnHeaders";
   private static final String PREF_KEY_SEPERATOR_TAB = "SquirrelSQL.csvexport.sepearatorTab";
   private static final String PREF_KEY_SEPERATOR_CHAR = "SquirrelSQL.csvexport.sepearatorChar";
   private static final String PREF_KEY_LINE_SEPERATOR = "SquirrelSQL.csvexport.lineSeparator";
   private static final String PREF_KEY_EXPORT_COMPLETE = "SquirrelSQL.csvexport.exportcomplete";
   private static final String PREF_KEY_USE_GLOBAL_PREFS_FORMATING = "SquirrelSQL.csvexport.useGlobalPrefsFomating";
   private static final String PREF_KEY_EXECUTE_COMMAND = "SquirrelSQL.csvexport.executeCommand";
   private static final String PREF_KEY_COMMAND = "SquirrelSQL.csvexport.commandString";
   private static final String PREF_KEY_FORMAT_CSV = "SquirrelSQL.csvexport.formatCSV";
   private static final String PREF_KEY_FORMAT_XLS = "SquirrelSQL.csvexport.formatXLS"; // is xlsx
   private static final String PREF_KEY_FORMAT_XLS_OLD = "SquirrelSQL.csvexport.formatXLS_OLD"; // is xls
   private static final String PREF_KEY_FORMAT_XML = "SquirrelSQL.csvexport.formatXML";
   private static final String PREF_KEY_FORMAT_JSON = "SquirrelSQL.csvexport.formatJSON";

   private static final String PREF_KEY_LIMIT_ROWS = "SquirrelSQL.sqlexport.limitRows";


   public static TableExportPreferences loadPreferences()
   {
      TableExportPreferences ret = new TableExportPreferences();

      ret.setFile(Props.getString(PREF_KEY_FILE, ret.getFile()));
      ret.setEncoding(Props.getString(PREF_KEY_ENCODING, ret.getEncoding()));
      ret.setWithHeaders(Props.getBoolean(PREF_KEY_WITH_HEADERS, ret.isWithHeaders()));
      ret.setSeperatorTab(Props.getBoolean(PREF_KEY_SEPERATOR_TAB, ret.isSeperatorTab()));
      ret.setSeperatorChar(Props.getString(PREF_KEY_SEPERATOR_CHAR, ret.getSeperatorChar()));
      ret.setLineSeperator(Props.getString(PREF_KEY_LINE_SEPERATOR, ret.getLineSeperator()));
      ret.setExportComplete(Props.getBoolean(PREF_KEY_EXPORT_COMPLETE, ret.isExportComplete()));
      ret.setUseGlobalPrefsFormating(Props.getBoolean(PREF_KEY_USE_GLOBAL_PREFS_FORMATING, ret.isUseGlobalPrefsFormating()));
      ret.setExecuteCommand(Props.getBoolean(PREF_KEY_EXECUTE_COMMAND, ret.isExecuteCommand()));
      ret.setCommand(Props.getString(PREF_KEY_COMMAND, ret.getCommand()));
      ret.setFormatCSV(Props.getBoolean(PREF_KEY_FORMAT_CSV, ret.isFormatCSV()));
      ret.setFormatXLS(Props.getBoolean(PREF_KEY_FORMAT_XLS, ret.isFormatXLS()));
      ret.setFormatXLSOld(Props.getBoolean(PREF_KEY_FORMAT_XLS_OLD, ret.isFormatXLSOld()));
      ret.setFormatXML(Props.getBoolean(PREF_KEY_FORMAT_XML, ret.isFormatXML()));
      ret.setFormatJSON(Props.getBoolean(PREF_KEY_FORMAT_JSON, ret.isFormatJSON()));

      ret.setLimitRows(Props.getString(PREF_KEY_LIMIT_ROWS, ret.getLimitRows()));

      return ret;
   }

   public static void savePreferences(TableExportPreferences prefs)
   {
      Props.putString(PREF_KEY_FILE, prefs.getFile());
      Props.putString(PREF_KEY_ENCODING, prefs.getEncoding());
      Props.putBoolean(PREF_KEY_WITH_HEADERS, prefs.isWithHeaders());
      Props.putBoolean(PREF_KEY_SEPERATOR_TAB, prefs.isSeperatorTab());
      Props.putString(PREF_KEY_SEPERATOR_CHAR, prefs.getSeperatorChar());
      Props.putString(PREF_KEY_LINE_SEPERATOR, prefs.getLineSeperator());
      Props.putBoolean(PREF_KEY_EXPORT_COMPLETE, prefs.isExportComplete());
      Props.putBoolean(PREF_KEY_USE_GLOBAL_PREFS_FORMATING, prefs.isUseGlobalPrefsFormating());
      Props.putBoolean(PREF_KEY_EXECUTE_COMMAND, prefs.isExecuteCommand());
      Props.putString(PREF_KEY_COMMAND, prefs.getCommand());
      Props.putBoolean(PREF_KEY_FORMAT_CSV, prefs.isFormatCSV());
      Props.putBoolean(PREF_KEY_FORMAT_XLS, prefs.isFormatXLS());
      Props.putBoolean(PREF_KEY_FORMAT_XLS_OLD, prefs.isFormatXLSOld());
      Props.putBoolean(PREF_KEY_FORMAT_XML, prefs.isFormatXML());
      Props.putBoolean(PREF_KEY_FORMAT_JSON, prefs.isFormatJSON());

      Props.putString(PREF_KEY_LIMIT_ROWS, prefs.getLimitRows());
   }

   public static TableExportPreferences createExportPreferencesForFile(String fileName)
   {
      TableExportPreferences prefs = loadPreferences();

      prefs.setFile(fileName);

      if(fileName.toUpperCase().endsWith("CSV"))
      {
         prefs.setFormatCSV(true);
         prefs.setFormatXLSOld(false);
         prefs.setFormatXLS(false);
         prefs.setFormatXML(false);
         prefs.setFormatJSON(false);
      }
      else if(fileName.toUpperCase().endsWith("XLS"))
      {
         prefs.setFormatCSV(false);
         prefs.setFormatXLSOld(true);
         prefs.setFormatXLS(false);
         prefs.setFormatXML(false);
         prefs.setFormatJSON(false);
      }
      else if(fileName.toUpperCase().endsWith("XLSX"))
      {
         prefs.setFormatCSV(false);
         prefs.setFormatXLSOld(false);
         prefs.setFormatXLS(true);
         prefs.setFormatXML(false);
         prefs.setFormatJSON(false);
      }
      else if(fileName.toUpperCase().endsWith("XML"))
      {
         prefs.setFormatCSV(false);
         prefs.setFormatXLSOld(false);
         prefs.setFormatXLS(false);
         prefs.setFormatXML(true);
         prefs.setFormatJSON(false);
      }
      else if(fileName.toUpperCase().endsWith("JSON"))
      {
         prefs.setFormatCSV(false);
         prefs.setFormatXLSOld(false);
         prefs.setFormatXLS(false);
         prefs.setFormatXML(false);
         prefs.setFormatJSON(true);
      }
      // else use the prefs predefined format

      return prefs;
   }
}
