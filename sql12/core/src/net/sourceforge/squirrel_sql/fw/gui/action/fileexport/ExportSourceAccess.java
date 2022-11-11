package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JTable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ExportSourceAccess
{
   static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ExportSourceAccess.class);
   static ILogger s_log = LoggerController.createLogger(ExportSourceAccess.class);

   private UITableExportData _uiTableExportData;
   private JDBCResultSetExportData _jdbcResultSetExportData;

   private boolean _exportMultipleResults;
   private MultipleSqlResultExportDestinationInfo _currentExportDestinationInfo;


   public ExportSourceAccess(JTable table)
   {
      _uiTableExportData = new UITableExportData();
      _uiTableExportData._table = table;
   }

   public ExportSourceAccess(String sql, Statement stmt, DialectType dialect)
   {
      _jdbcResultSetExportData = new JDBCResultSetExportData();
      _jdbcResultSetExportData._sql = sql;
      _jdbcResultSetExportData._stmt = stmt;
      _jdbcResultSetExportData._dialect = dialect;
   }

   public boolean isUITableMissingBlobData(String separatorChar)
   {
      if(isResultSetExport())
      {
         return false;
      }
      else
      {
         return ExportUtil.isUITableMissingBlobData(_uiTableExportData._table, separatorChar);
      }
   }

   public ExportDataInfoList createExportData(ProgressAbortCallback progressController) throws ExportDataException
   {
      if(isResultSetExport())
      {
         return createResultSetExportData(progressController);
      }
      else
      {
         return createTableExportDataInfoList();
      }
   }

   private boolean isResultSetExport()
   {
      return null != _jdbcResultSetExportData;
   }

   private ExportDataInfoList createResultSetExportData(ProgressAbortCallback progressController) throws ExportDataException
   {
      try
      {
         progress(progressController, s_stringMgr.getString("ResultSetExportCommand.executingQuery"));
         if (_jdbcResultSetExportData._exportComplete == false)
         {
            _jdbcResultSetExportData._stmt.setMaxRows(_jdbcResultSetExportData._maxRows);
         }
         ResultSet resultSet = _jdbcResultSetExportData._stmt.executeQuery(_jdbcResultSetExportData._sql);
         return ExportDataInfoList.single(new ResultSetExportData(resultSet, _jdbcResultSetExportData._dialect));
      }
      catch (SQLException e)
      {
         s_log.error(s_stringMgr.getString("ResultSetExportCommand.errorExecuteStatement"), e);
         throw new ExportDataException(s_stringMgr.getString("ResultSetExportCommand.errorExecuteStatement"), e);
      }
   }

   private ExportDataInfoList createTableExportDataInfoList()
   {
      if(_uiTableExportData._sqlResultDataSetViewersExportDataList.isEmpty())
      {
         if(_exportMultipleResults)
         {
            // Happens when multiple SQL result export was chosen with empty export list.
            return ExportDataInfoList.EMPTY;
         }
         else
         {
            // This is the default behavior, i.e. export of single table.
            return ExportDataInfoList.single(new JTableExportData(_uiTableExportData._table, false == _uiTableExportData._exportUITableSelection));
         }
      }
      else
      {
         return new ExportDataInfoList(_uiTableExportData._sqlResultDataSetViewersExportDataList, _currentExportDestinationInfo);
      }
   }


   public void progress(ProgressAbortCallback progressController, String task)
   {
      if(progressController != null)
      {
         progressController.currentlyLoading(task);
      }
   }


   public TableExportPreferences getPreferences()
   {
      final TableExportPreferences prefs = TableExportPreferencesDAO.loadPreferences();

      if(isResultSetExport())
      {
         /////////////////////////////////////////////////////////////////////////////////////////////////
         // If useColoring was true for a file export a XSSFWorkbook instead of a SXSSFWorkbook was used.
         // This would result in much higher memory usage and much longer export time.
         // See DataExportExcelWriter.beforeWorking(...)
         prefs.setUseColoring(false);
         //
         /////////////////////////////////////////////////////////////////////////////////////////////////
      }

      return prefs;
   }

   public void prepareResultSetExport(boolean exportComplete, int maxRows, MultipleSqlResultExportDestinationInfo currentExportDestinationInfo, boolean exportMultipleResults)
   {
      _jdbcResultSetExportData._exportComplete = exportComplete;
      _jdbcResultSetExportData._maxRows = maxRows;

      _currentExportDestinationInfo = currentExportDestinationInfo;
      _exportMultipleResults = exportMultipleResults;
   }

   public void prepareSqlResultDataSetViewersExport(List<ExportDataInfo> sqlResultDataSetViewersExportDataList, boolean exportUITableSelection, MultipleSqlResultExportDestinationInfo currentExportDestinationInfo, boolean exportMultipleResults)
   {
      // if _exportMultipleResults then export _uiTableExportData.sqlResultDataSetViewersExportDataList
      // else export _uiTableExportData._table only

      _uiTableExportData._sqlResultDataSetViewersExportDataList = sqlResultDataSetViewersExportDataList;
      _uiTableExportData._exportUITableSelection = exportUITableSelection;


      _currentExportDestinationInfo = currentExportDestinationInfo;
      _exportMultipleResults = exportMultipleResults;
   }
}
