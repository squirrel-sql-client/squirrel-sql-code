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
   private static final String PREF_KEY_EXPORT_MULTIPLE_SQL_RESULTS = "SquirrelSQL.csvexport.exportMultipleSQLResults";
   private static final String PREF_KEY_USE_GLOBAL_PREFS_FORMATING = "SquirrelSQL.csvexport.useGlobalPrefsFomating";
   private static final String PREF_KEY_RENDER_GROUPING_SEPARATOR = "SquirrelSQL.csvexport.renderGroupingSeparator";
   private static final String PREF_KEY_EXECUTE_COMMAND = "SquirrelSQL.csvexport.executeCommand";
   private static final String PREF_KEY_COMMAND = "SquirrelSQL.csvexport.commandString";
   private static final String PREF_KEY_FORMAT_CSV = "SquirrelSQL.csvexport.formatCSV";

   private static final String PREF_KEY_FORMAT_XLS = "SquirrelSQL.csvexport.formatXLS"; // is xlsx
   private static final String PREF_KEY_USE_COLORING = "SquirrelSQL.csvexport.useColoring";
   private static final String PREF_KEY_FORMAT_XLS_OLD = "SquirrelSQL.csvexport.formatXLS_OLD"; // is xls
   private static final String PREF_KEY_EXCEL_AUTO_FILTER = "SquirrelSQL.csvexport.excelAutoFilter";
   private static final String PREF_KEY_EXCEL_FIRST_ROW_FROZEN = "SquirrelSQL.csvexport.excelFirstRowFrozen";
   private static final String PREF_KEY_EXCEL_FIRST_ROW_BOLD = "SquirrelSQL.csvexport.excelFirstRowBold";
   private static final String PREF_KEY_EXCEL_FIRST_ROW_CENTERED = "SquirrelSQL.csvexport.excelFirstRowCentered";

   private static final String PREF_KEY_FORMAT_XML = "SquirrelSQL.csvexport.formatXML";
   private static final String PREF_KEY_FORMAT_JSON = "SquirrelSQL.csvexport.formatJSON";

   private static final String PREF_KEY_LIMIT_ROWS = "SquirrelSQL.sqlexport.limitRows";
   private static final String PREF_KEY_LIMIT_ROWS_CHECKED = "SquirrelSQL.sqlexport.limitRowsChecked";


   public static TableExportPreferences loadPreferences()
   {
      TableExportPreferences ret = new TableExportPreferences();

      ret.setFile(Props.getString(PREF_KEY_FILE, ret.getFile()));
      ret.setEncoding(Props.getString(PREF_KEY_ENCODING, ret.getEncoding()));
      ret.setWithHeaders(Props.getBoolean(PREF_KEY_WITH_HEADERS, ret.isWithHeaders()));
      ret.setSeperatorTab(Props.getBoolean(PREF_KEY_SEPERATOR_TAB, ret.isSeperatorTab()));
      ret.setSeperatorChar(Props.getString(PREF_KEY_SEPERATOR_CHAR, ret.getSeperatorChar()));
      ret.setLineSeperator(Props.getString(PREF_KEY_LINE_SEPERATOR, ret.getLineSeperator()));
      ret.setExportComplete(Props.getBoolean(PREF_KEY_EXPORT_COMPLETE, ret.isExportCompleteTableOrSingleFile()));
      ret.setExportMultipleSQLResults(Props.getBoolean(PREF_KEY_EXPORT_MULTIPLE_SQL_RESULTS, ret.isExportMultipleSQLResults()));
      ret.setUseGlobalPrefsFormating(Props.getBoolean(PREF_KEY_USE_GLOBAL_PREFS_FORMATING, ret.isUseGlobalPrefsFormating()));
      ret.setRenderGroupingSeparator(Props.getBoolean(PREF_KEY_RENDER_GROUPING_SEPARATOR, ret.isRenderGroupingSeparator()));
      ret.setExecuteCommand(Props.getBoolean(PREF_KEY_EXECUTE_COMMAND, ret.isExecuteCommand()));
      ret.setCommand(Props.getString(PREF_KEY_COMMAND, ret.getCommand()));
      ret.setFormatCSV(Props.getBoolean(PREF_KEY_FORMAT_CSV, ret.isFormatCSV()));

      ret.setFormatXLS(Props.getBoolean(PREF_KEY_FORMAT_XLS, ret.isFormatXLS()));
      ret.setUseColoring(Props.getBoolean(PREF_KEY_USE_COLORING, ret.isUseColoring()));
      ret.setFormatXLSOld(Props.getBoolean(PREF_KEY_FORMAT_XLS_OLD, ret.isFormatXLSOld()));
      ret.setExcelAutoFilter(Props.getBoolean(PREF_KEY_EXCEL_AUTO_FILTER, ret.isExcelAutoFilter()));
      ret.setExcelFirstRowFrozen(Props.getBoolean(PREF_KEY_EXCEL_FIRST_ROW_FROZEN, ret.isExcelFirstRowFrozen()));
      ret.setExcelFirstRowBold(Props.getBoolean(PREF_KEY_EXCEL_FIRST_ROW_BOLD, ret.isExcelFirstRowBold()));
      ret.setExcelFirstRowCentered(Props.getBoolean(PREF_KEY_EXCEL_FIRST_ROW_CENTERED, ret.isExcelFirstRowCentered()));

      ret.setFormatXML(Props.getBoolean(PREF_KEY_FORMAT_XML, ret.isFormatXML()));
      ret.setFormatJSON(Props.getBoolean(PREF_KEY_FORMAT_JSON, ret.isFormatJSON()));

      ret.setRowsLimit(Props.getString(PREF_KEY_LIMIT_ROWS, ret.getRowsLimit()));
      ret.setLimitRowsChecked(Props.getBoolean(PREF_KEY_LIMIT_ROWS_CHECKED, ret.isLimitRowsChecked()));

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
      Props.putBoolean(PREF_KEY_EXPORT_COMPLETE, prefs.isExportCompleteTableOrSingleFile());
      Props.putBoolean(PREF_KEY_EXPORT_MULTIPLE_SQL_RESULTS, prefs.isExportMultipleSQLResults());
      Props.putBoolean(PREF_KEY_USE_GLOBAL_PREFS_FORMATING, prefs.isUseGlobalPrefsFormating());
      Props.putBoolean(PREF_KEY_RENDER_GROUPING_SEPARATOR, prefs.isRenderGroupingSeparator());
      Props.putBoolean(PREF_KEY_EXECUTE_COMMAND, prefs.isExecuteCommand());
      Props.putString(PREF_KEY_COMMAND, prefs.getCommand());
      Props.putBoolean(PREF_KEY_FORMAT_CSV, prefs.isFormatCSV());

      Props.putBoolean(PREF_KEY_FORMAT_XLS, prefs.isFormatXLS());
      Props.putBoolean(PREF_KEY_USE_COLORING, prefs.isUseColoring());
      Props.putBoolean(PREF_KEY_FORMAT_XLS_OLD, prefs.isFormatXLSOld());
      Props.putBoolean(PREF_KEY_EXCEL_AUTO_FILTER, prefs.isExcelAutoFilter());
      Props.putBoolean(PREF_KEY_EXCEL_FIRST_ROW_FROZEN, prefs.isExcelFirstRowFrozen());
      Props.putBoolean(PREF_KEY_EXCEL_FIRST_ROW_BOLD, prefs.isExcelFirstRowBold());
      Props.putBoolean(PREF_KEY_EXCEL_FIRST_ROW_CENTERED, prefs.isExcelFirstRowCentered());

      Props.putBoolean(PREF_KEY_FORMAT_XML, prefs.isFormatXML());
      Props.putBoolean(PREF_KEY_FORMAT_JSON, prefs.isFormatJSON());

      Props.putString(PREF_KEY_LIMIT_ROWS, prefs.getRowsLimit());
      Props.putBoolean(PREF_KEY_LIMIT_ROWS_CHECKED   , prefs.isLimitRowsChecked());
   }

   public static TableExportPreferences createExportPreferencesForFile(String fileName)
   {
      TableExportPreferences prefs = loadPreferences();

      prefs.setFile(fileName);

      if(FileEndings.CSV.fileEndsWith(fileName))
      {
         prefs.setFormatCSV(true);
         prefs.setFormatXLSOld(false);
         prefs.setFormatXLS(false);
         prefs.setFormatXML(false);
         prefs.setFormatJSON(false);
      }
      else if(FileEndings.XLS.fileEndsWith(fileName))
      {
         prefs.setFormatCSV(false);
         prefs.setFormatXLSOld(true);
         prefs.setFormatXLS(false);
         prefs.setFormatXML(false);
         prefs.setFormatJSON(false);
      }
      else if(FileEndings.XLSX.fileEndsWith(fileName))
      {
         prefs.setFormatCSV(false);
         prefs.setFormatXLSOld(false);
         prefs.setFormatXLS(true);
         prefs.setFormatXML(false);
         prefs.setFormatJSON(false);
      }
      else if(FileEndings.XML.fileEndsWith(fileName))
      {
         prefs.setFormatCSV(false);
         prefs.setFormatXLSOld(false);
         prefs.setFormatXLS(false);
         prefs.setFormatXML(true);
         prefs.setFormatJSON(false);
      }
      else if(FileEndings.JSON.fileEndsWith(fileName))
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
