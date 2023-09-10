package net.sourceforge.squirrel_sql.plugins.sqlscript.sqltofile;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.gui.action.fileexport.*;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryHolder;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.ProgressAbortFactoryCallbackImpl;
import org.apache.commons.lang3.StringUtils;

public class ExportToMultiSheetMsExcelHandler
{
   private final static ILogger s_log = LoggerController.createLogger(ExportToMultiSheetMsExcelHandler.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ExportToMultiSheetMsExcelHandler.class);

   public static final String MS_EXCEL_WORKBOOK_PREFIX = "@msExcelWorkbook";
   public static final String SHEET_PREFIX = "@sheet";
   private final ISession _session;
   private final ISQLPanelAPI _sqlPaneAPI;

   private boolean _abortExecution = false;

   public ExportToMultiSheetMsExcelHandler(ISession session, ISQLPanelAPI sqlPaneAPI)
   {
      _session = session;
      _sqlPaneAPI = sqlPaneAPI;
   }

   public static boolean containsMyMarkers(String initialSql)
   {
      return StringUtils.containsIgnoreCase(initialSql, MS_EXCEL_WORKBOOK_PREFIX) || StringUtils.containsIgnoreCase(initialSql, SHEET_PREFIX);
   }

   public static boolean willBeHandledByMe(QueryHolder sql)
   {
      if(null == sql)
      {
         return false;
      }

      return isWorkbookPrefixed(sql) || isSheetPrefixed(sql);
   }

   private static boolean isSheetPrefixed(QueryHolder sql)
   {
      return StringUtils.startsWithIgnoreCase(sql.getQuery().trim(), SHEET_PREFIX);
   }

   private static boolean isWorkbookPrefixed(QueryHolder sql)
   {
      return StringUtils.startsWithIgnoreCase(sql.getQuery().trim(), MS_EXCEL_WORKBOOK_PREFIX);
   }

   public String exportToMsExcel(String initialSql)
   {
      IQueryTokenizer queryTokenizer = _session.getQueryTokenizer();

      queryTokenizer.setScriptToTokenize(initialSql);

      StringBuilder sqlsNotToWriteToFile = new StringBuilder();

      MsExcelWorkbookList workbooks = new MsExcelWorkbookList();

      while (queryTokenizer.hasQuery())
      {
         QueryHolder query = queryTokenizer.nextQuery();

         if (false == willBeHandledByMe(query))
         {
            sqlsNotToWriteToFile.append(query);

            if (1 == queryTokenizer.getSQLStatementSeparator().length())
            {
               sqlsNotToWriteToFile.append(queryTokenizer.getSQLStatementSeparator()).append("\n");
            }
            else
            {
               sqlsNotToWriteToFile.append(" ").append(queryTokenizer.getSQLStatementSeparator()).append("\n");
            }

            continue;
         }

         String sqlWithPrefix = query.getQuery().trim();

         int contentBeginMarkerPos = sqlWithPrefix.indexOf('\'');
         if (-1 == contentBeginMarkerPos)
         {
            if (isWorkbookPrefixed(query))
            {
               Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("ExportToMultiSheetMsExcelHandler.noWorkbookFileBeginMarker"));
            }
            else if (isSheetPrefixed(query))
            {
               Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("ExportToMultiSheetMsExcelHandler.noSheetNameBeginMarker"));
            }
            else
            {
               throw new IllegalStateException("Should not get here as method willBeHandledByMe() returned true. Concerned SQL: " + query.getQuery());
            }
            continue;
         }

         int contentEndMarkerPos = sqlWithPrefix.indexOf('\'', contentBeginMarkerPos + 1);
         if (-1 == contentEndMarkerPos)
         {
            if (isWorkbookPrefixed(query))
            {
               Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("ExportToMultiSheetMsExcelHandler.noWorkbookFileEndMarker"));
            }
            else if (isSheetPrefixed(query))
            {
               Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("ExportToMultiSheetMsExcelHandler.noSheetNameEndMarker"));
            }
            else
            {
               throw new IllegalStateException("Should not get here as method willBeHandledByMe() returned true. Concerned SQL: " + query.getQuery());
            }
            continue;
         }

         if (isWorkbookPrefixed(query))
         {
            String workbook = sqlWithPrefix.substring(contentBeginMarkerPos + 1, contentEndMarkerPos).trim();
            if(FileEndings.XLS.fileEndsWith(workbook) || FileEndings.XLSX.fileEndsWith(workbook))
            {
               workbooks.addCurrentWorkbook(workbook);
            }
            else
            {
               workbooks.addCurrentWorkbook(workbook + "." + FileEndings.XLSX.get());
            }

            if (sqlWithPrefix.length() <= contentEndMarkerPos)
            {
               Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("ExportToMultiSheetMsExcelHandler.missingSheetNameForWorkbook"));
               continue;
            }

            String afterWorkbookSql = sqlWithPrefix.substring(contentEndMarkerPos + 1).trim();
            if (false == isSheetPrefixed(new QueryHolder(afterWorkbookSql)))
            {
               Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("ExportToMultiSheetMsExcelHandler.missingSheetNameForWorkbook"));
               continue;
            }

            int sheetBeginMarkerPos = sqlWithPrefix.indexOf('\'');
            if (-1 == sheetBeginMarkerPos)
            {
               Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("ExportToMultiSheetMsExcelHandler.noSheetNameBeginMarker"));
               continue;
            }

            int sheetEndMarkerPos = sqlWithPrefix.indexOf('\'', sheetBeginMarkerPos + 1);
            if (-1 == sheetEndMarkerPos)
            {
               Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("ExportToMultiSheetMsExcelHandler.noSheetNameEndMarker"));
               continue;
            }

            String sheetName = sqlWithPrefix.substring(sheetBeginMarkerPos + 1, sheetEndMarkerPos).trim();
            String sheetSql = sqlWithPrefix.substring(sheetEndMarkerPos + 1).trim();
            workbooks.addSheetToCurrentWorkbook(sheetName, sheetSql);
         }
         else if (isSheetPrefixed(query))
         {
            String sheetName = sqlWithPrefix.substring(contentBeginMarkerPos + 1, contentEndMarkerPos).trim();
            String sheetSql = sqlWithPrefix.substring(contentEndMarkerPos + 1).trim();
            workbooks.addSheetToCurrentWorkbook(sheetName, sheetSql);
         }

         if (workbooks.hasExportReadyWorkbook())
         {
            callExportWorkbook(workbooks.checkoutExportReadyWorkbook());
         }

         if (_abortExecution)
         {
            _abortExecution = false;
            break;
         }
      }

      MsExcelWorkbook workbook = workbooks.checkoutCurrentWorkbook();

      if(null != workbook)
      {
         callExportWorkbook(workbook);
      }

      if (0 == sqlsNotToWriteToFile.length())
      {
         return null;
      }
      else
      {
         return sqlsNotToWriteToFile.toString();
      }
   }

   private void callExportWorkbook(MsExcelWorkbook workbook)
   {
      ////////////////////////////////////////////////////////////
      // Preparations to call exporter
      final String sqlsJoined;
      if(1 == _session.getProperties().getSQLStatementSeparator().length())
      {
         sqlsJoined = String.join(_session.getProperties().getSQLStatementSeparator() + "\n", workbook.getSqlList());
      }
      else
      {
         sqlsJoined = String.join(" " + _session.getProperties().getSQLStatementSeparator() + "\n", workbook.getSqlList());
      }

      DialectType dialectType = DialectFactory.getDialectType(_session.getMetaData());

      //ResultSetExport[] refResultSetExport = new ResultSetExport[1];
      ProgressAbortFactoryCallbackImpl progressAbortCallback = new ProgressAbortFactoryCallbackImpl(_session, sqlsJoined, () -> workbook.getWorkbookFile());
      //refResultSetExport[0] = new ResultSetExport(_session.getSQLConnection().getConnection(), workbook.getSqlList(), dialectType, progressAbortCallback, _sqlPaneAPI.getOwningFrame());
      //
      ////////////////////////////////////////////////////////////

      Exporter exporter = new Exporter(() -> progressAbortCallback.getOrCreate(), new ExportControllerProxy(_sqlPaneAPI.getOwningFrame(), workbook));
      exporter.export();

   }
}
