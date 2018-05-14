package net.sourceforge.squirrel_sql.fw.gui.action;

import java.util.prefs.Preferences;

public class TableExportPreferencesDAO
{
   private static final String PREF_KEY_CSV_FILE = "SquirrelSQL.csvexport.csvfile";
   private static final String PREF_KEY_CSV_ENCODING = "SquirrelSQL.csvexport.csvencoding";
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

      ret.setCsvFile(Preferences.userRoot().get(PREF_KEY_CSV_FILE, ret.getCsvFile()));
      ret.setCsvEncoding(Preferences.userRoot().get(PREF_KEY_CSV_ENCODING, ret.getCsvEncoding()));
      ret.setWithHeaders(Preferences.userRoot().getBoolean(PREF_KEY_WITH_HEADERS, ret.isWithHeaders()));
      ret.setSeperatorTab(Preferences.userRoot().getBoolean(PREF_KEY_SEPERATOR_TAB, ret.isSeperatorTab()));
      ret.setSeperatorChar(Preferences.userRoot().get(PREF_KEY_SEPERATOR_CHAR, ret.getSeperatorChar()));
      ret.setLineSeperator(Preferences.userRoot().get(PREF_KEY_LINE_SEPERATOR, ret.getLineSeperator()));
      ret.setExportComplete(Preferences.userRoot().getBoolean(PREF_KEY_EXPORT_COMPLETE, ret.isExportComplete()));
      ret.setUseGlobalPrefsFormating(Preferences.userRoot().getBoolean(PREF_KEY_USE_GLOBAL_PREFS_FORMATING, ret.isUseGlobalPrefsFormating()));
      ret.setExecuteCommand(Preferences.userRoot().getBoolean(PREF_KEY_EXECUTE_COMMAND, ret.isExecuteCommand()));
      ret.setCommand(Preferences.userRoot().get(PREF_KEY_COMMAND, ret.getCommand()));
      ret.setFormatCSV(Preferences.userRoot().getBoolean(PREF_KEY_FORMAT_CSV, ret.isFormatCSV()));
      ret.setFormatXLS(Preferences.userRoot().getBoolean(PREF_KEY_FORMAT_XLS, ret.isFormatXLS()));
      ret.setFormatXLSOld(Preferences.userRoot().getBoolean(PREF_KEY_FORMAT_XLS_OLD, ret.isFormatXLSOld()));
      ret.setFormatXML(Preferences.userRoot().getBoolean(PREF_KEY_FORMAT_XML, ret.isFormatXML()));
      ret.setFormatJSON(Preferences.userRoot().getBoolean(PREF_KEY_FORMAT_JSON, ret.isFormatJSON()));

      ret.setLimitRows(Preferences.userRoot().get(PREF_KEY_LIMIT_ROWS, ret.getLimitRows()));

      return ret;
   }

   public static void savePreferences(TableExportPreferences prefs)
   {
      Preferences.userRoot().put(PREF_KEY_CSV_FILE, prefs.getCsvFile());
      Preferences.userRoot().put(PREF_KEY_CSV_ENCODING, prefs.getCsvEncoding());
      Preferences.userRoot().putBoolean(PREF_KEY_WITH_HEADERS, prefs.isWithHeaders());
      Preferences.userRoot().putBoolean(PREF_KEY_SEPERATOR_TAB, prefs.isSeperatorTab());
      Preferences.userRoot().put(PREF_KEY_SEPERATOR_CHAR, prefs.getSeperatorChar());
      Preferences.userRoot().put(PREF_KEY_LINE_SEPERATOR, prefs.getLineSeperator());
      Preferences.userRoot().putBoolean(PREF_KEY_EXPORT_COMPLETE, prefs.isExportComplete());
      Preferences.userRoot().putBoolean(PREF_KEY_USE_GLOBAL_PREFS_FORMATING, prefs.isUseGlobalPrefsFormating());
      Preferences.userRoot().putBoolean(PREF_KEY_EXECUTE_COMMAND, prefs.isExecuteCommand());
      Preferences.userRoot().put(PREF_KEY_COMMAND, prefs.getCommand());
      Preferences.userRoot().putBoolean(PREF_KEY_FORMAT_CSV, prefs.isFormatCSV());
      Preferences.userRoot().putBoolean(PREF_KEY_FORMAT_XLS, prefs.isFormatXLS());
      Preferences.userRoot().putBoolean(PREF_KEY_FORMAT_XLS_OLD, prefs.isFormatXLSOld());
      Preferences.userRoot().putBoolean(PREF_KEY_FORMAT_XML, prefs.isFormatXML());
      Preferences.userRoot().putBoolean(PREF_KEY_FORMAT_JSON, prefs.isFormatJSON());

      Preferences.userRoot().put(PREF_KEY_LIMIT_ROWS, prefs.getLimitRows());
   }
}
