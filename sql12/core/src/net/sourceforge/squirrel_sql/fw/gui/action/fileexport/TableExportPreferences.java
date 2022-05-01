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
   private String _limitRows = "100";

   // PREF_KEY_USE_GLOBAL_PREFS_FORMATING
   private boolean _useGlobalPrefsFormating = true;

   // PREF_KEY_EXECUTE_COMMAND
   private boolean _executeCommand = false;

   // PREF_KEY_COMMAND
   private String _command = "openoffice.org-2.0 -calc %file";

   // PREF_KEY_LINE_SEPERATOR;
   private String _lineSeperator = LineSeparator.DEFAULT.name();
   private boolean _useColoring;

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

   public boolean isExportComplete()
   {
      return _exportComplete;
   }

   public void setExportComplete(boolean exportComplete)
   {
      _exportComplete = exportComplete;
   }

   public String getLimitRows()
   {
      return _limitRows;
   }

   public void setLimitRows(String limitRows)
   {
      _limitRows = limitRows;
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
}
