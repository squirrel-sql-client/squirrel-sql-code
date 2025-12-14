package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import java.nio.charset.Charset;

public class TableExportPreferences
{

   // PREF_KEY_CSV_FILE
   private String _file = null;

   // PREF_KEY_CSV_ENCODING
   private String _encoding = Charset.defaultCharset().name();

   // PREF_KEY_WITH_HEADERS
   private boolean _withHeaders = true;


   // PREF_KEY_SEPERATOR_TAB
   private boolean _seperatorTab = false;

   // PREF_KEY_SEPERATOR_CHAR
   private String _seperatorChar = ",";

   // PREF_KEY_FORMAT_CSV
   private boolean _formatCSV = true;

   // FORMAT XLSX
   //PREF_KEY_FORMAT_XLS
   private boolean _formatXLS = false;

   // FORMAT XLS
   // PREF_KEY_FORMAT_XLS_OLD
   private boolean _formatXLSOld = false;

   // PREF_KEY_FORMAT_XML
   private boolean _formatXML = false;

   // PREF_KEY_FORMAT_JSON
   private boolean _formatJSON = false;

   // PREF_KEY_EXPORT_COMPLETE
   private boolean _exportComplete = true;

	// PREF_KEY_LIMIT_ROWS
   private String _rowsLimit = "100";

   // PREF_KEY_USE_GLOBAL_PREFS_FORMATING
   private boolean _useGlobalPrefsFormating = true;

   // PREF_KEY_RENDER_GROUPING_SEPARATOR
   private boolean _renderGroupingSeparator;

   // PREF_KEY_EXECUTE_COMMAND
   private boolean _executeCommand = false;

   // PREF_KEY_COMMAND
   private String _command = "openoffice.org-2.0 -calc %file";

   // PREF_KEY_LINE_SEPERATOR;
   private String _lineSeperator = LineSeparator.DEFAULT.name();
   private boolean _useColoring;
   private boolean _exportMultipleSQLResults;
   private boolean _limitRowsChecked;
   private boolean _excelAutoFilter;
   private boolean _excelFirstRowFrozen;
   private boolean _excelFirstRowBold;
   private boolean _excelFirstRowCentered;

   private boolean _excelFontNoSelection = true;
   private String _excelFontFamily;
   private int _excelFontSize;
   private boolean _excelFontBold;
   private boolean _excelFontItalic;
   private boolean _excelHeaderFontNoSelection = true;
   private String _excelHeaderFontFamily;
   private int _excelHeaderFontSize;
   private boolean _excelHeaderFontBold;
   private boolean _excelHeaderFontItalic;
   private boolean _excelExportSQLStatementInAdditionalSheet;
   private String _excelSheetNameFileNormalized;
   private boolean _excelReplaceSheets;

   public boolean isFormatXLS()
   {
      return _formatXLS;
   }

   public void setFormatXLS(boolean formatXLS)
   {
      _formatXLS = formatXLS;
   }

   public String getFile()
   {
      return _file;
   }

   public void setFile(String file)
   {
      _file = file;
   }

   public String getEncoding()
   {
      return _encoding;
   }

   public void setEncoding(String csvEncoding)
   {
      _encoding = csvEncoding;
   }

   public boolean isWithHeaders()
   {
      return _withHeaders;
   }

   public void setWithHeaders(boolean withHeaders)
   {
      _withHeaders = withHeaders;
   }

   public boolean isSeperatorTab()
   {
      return _seperatorTab;
   }

   public void setSeperatorTab(boolean seperatorTab)
   {
      _seperatorTab = seperatorTab;
   }

   public String getSeperatorChar()
   {
      return _seperatorChar;
   }

   public void setSeperatorChar(String seperatorChar)
   {
      _seperatorChar = seperatorChar;
   }

   public boolean isFormatCSV()
   {
      return _formatCSV;
   }

   public void setFormatCSV(boolean formatCSV)
   {
      _formatCSV = formatCSV;
   }

   public boolean isFormatXLSOld()
   {
      return _formatXLSOld;
   }

   public void setFormatXLSOld(boolean formatXLSOld)
   {
      _formatXLSOld = formatXLSOld;
   }

   public boolean isFormatXML()
   {
      return _formatXML;
   }

   public void setFormatXML(boolean formatXml)
   {
      _formatXML = formatXml;
   }

   public boolean isFormatJSON()
   {
      return _formatJSON;
   }

   public void setFormatJSON(boolean formatJSON)
   {
      _formatJSON = formatJSON;
   }

   public boolean isExportCompleteTableOrSingleFile()
   {
      return _exportComplete;
   }

   public void setExportComplete(boolean exportComplete)
   {
      _exportComplete = exportComplete;
   }

   public String getRowsLimit()
   {
      return _rowsLimit;
   }

   public void setRowsLimit(String rowsLimit)
   {
      _rowsLimit = rowsLimit;
   }

   public boolean isUseGlobalPrefsFormating()
   {
      return _useGlobalPrefsFormating;
   }

   public void setUseGlobalPrefsFormating(boolean useGlobalPrefsFormating)
   {
      _useGlobalPrefsFormating = useGlobalPrefsFormating;
   }

   public boolean isExecuteCommand()
   {
      return _executeCommand;
   }

   public void setExecuteCommand(boolean executeCommand)
   {
      _executeCommand = executeCommand;
   }

   public String getCommand()
   {
      return _command;
   }

   public void setCommand(String command)
   {
      _command = command;
   }

   public String getLineSeperator()
   {
      return _lineSeperator;
   }

   public void setLineSeperator(String lineSeperator)
   {
      _lineSeperator = lineSeperator;
   }

   public boolean isUseColoring()
   {
      return _useColoring;
   }

   public void setUseColoring(boolean useColoring)
   {
      _useColoring = useColoring;
   }

   public boolean isExportMultipleSQLResults()
   {
      return _exportMultipleSQLResults;
   }

   public void setExportMultipleSQLResults(boolean exportMultipleSQLResults)
   {
      _exportMultipleSQLResults = exportMultipleSQLResults;
   }

   public boolean isLimitRowsChecked()
   {
      return _limitRowsChecked;
   }

   public void setLimitRowsChecked(boolean limitRowsChecked)
   {
      _limitRowsChecked = limitRowsChecked;
   }

   public void setExcelAutoFilter(boolean excelAutoFilter)
   {
      _excelAutoFilter = excelAutoFilter;
   }

   public boolean isExcelAutoFilter()
   {
      return _excelAutoFilter;
   }

   public void setExcelFirstRowFrozen(boolean excelFirstRowFrozen)
   {
      _excelFirstRowFrozen = excelFirstRowFrozen;
   }

   public boolean isExcelFirstRowFrozen()
   {
      return _excelFirstRowFrozen;
   }

   public void setExcelFirstRowBold(boolean excelFirstRowBold)
   {
      _excelFirstRowBold = excelFirstRowBold;
   }

   public boolean isExcelFirstRowBold()
   {
      return _excelFirstRowBold;
   }

   public boolean isExcelFirstRowCentered()
   {
      return _excelFirstRowCentered;
   }

   public void setExcelFirstRowCentered(boolean excelFirstCentered)
   {
      _excelFirstRowCentered = excelFirstCentered;
   }

   public boolean isRenderGroupingSeparator()
   {
      return _renderGroupingSeparator;
   }

   public void setRenderGroupingSeparator(boolean renderGroupingSeparator)
   {
      _renderGroupingSeparator = renderGroupingSeparator;
   }

   public boolean isExcelFontNoSelection()
   {
      return _excelFontNoSelection;
   }

   public void setExcelFontNoSelection(boolean excelFontNoSelection)
   {
      _excelFontNoSelection = excelFontNoSelection;
   }

   public boolean isExcelHeaderFontNoSelection()
   {
      return _excelHeaderFontNoSelection;
   }

   public void setExcelHeaderFontNoSelection(boolean excelHeaderFontNoSelection)
   {
      _excelHeaderFontNoSelection = excelHeaderFontNoSelection;
   }

   public String getExcelFontFamily()
   {
      return _excelFontFamily;
   }

   public void setExcelFontFamily(String excelFontFamily)
   {
      _excelFontFamily = excelFontFamily;
   }

   public int getExcelFontSize()
   {
      return _excelFontSize;
   }

   public void setExcelFontSize(int excelFontSize)
   {
      _excelFontSize = excelFontSize;
   }

   public boolean isExcelFontBold()
   {
      return _excelFontBold;
   }

   public void setExcelFontBold(boolean excelFontBold)
   {
      _excelFontBold = excelFontBold;
   }

   public boolean isExcelFontItalic()
   {
      return _excelFontItalic;
   }

   public void setExcelFontItalic(boolean excelFontItalic)
   {
      _excelFontItalic = excelFontItalic;
   }

   public String getExcelHeaderFontFamily()
   {
      return _excelHeaderFontFamily;
   }

   public void setExcelHeaderFontFamily(String excelHeaderFontFamily)
   {
      _excelHeaderFontFamily = excelHeaderFontFamily;
   }

   public int getExcelHeaderFontSize()
   {
      return _excelHeaderFontSize;
   }

   public void setExcelHeaderFontSize(int excelHeaderFontSize)
   {
      _excelHeaderFontSize = excelHeaderFontSize;
   }

   public boolean isExcelHeaderFontBold()
   {
      return _excelHeaderFontBold;
   }

   public void setExcelHeaderFontBold(boolean excelHeaderFontBold)
   {
      _excelHeaderFontBold = excelHeaderFontBold;
   }

   public boolean isExcelHeaderFontItalic()
   {
      return _excelHeaderFontItalic;
   }

   public void setExcelHeaderFontItalic(boolean excelHeaderFontItalic)
   {
      _excelHeaderFontItalic = excelHeaderFontItalic;
   }

   public boolean isExcelExportSQLStatementInAdditionalSheet()
   {
      return _excelExportSQLStatementInAdditionalSheet;
   }
   public void setExcelExportSQLStatementInAdditionalSheet(boolean excelExportSQLStatementInAdditionalSheet)
   {
      _excelExportSQLStatementInAdditionalSheet = excelExportSQLStatementInAdditionalSheet;
   }

   public void setExcelSheetNameFileNormalized(String excelSheetNameFileNormalized)
   {
      _excelSheetNameFileNormalized = excelSheetNameFileNormalized;
   }

   public String getExcelSheetNameFileNormalized()
   {
      return _excelSheetNameFileNormalized;
   }

   public void setExcelReplaceSheets(boolean excelReplaceSheets)
   {
      _excelReplaceSheets = excelReplaceSheets;
   }

   public boolean isExcelReplaceSheets()
   {
      return _excelReplaceSheets;
   }
}
