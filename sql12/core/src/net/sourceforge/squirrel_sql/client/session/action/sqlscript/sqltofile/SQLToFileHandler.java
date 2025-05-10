package net.sourceforge.squirrel_sql.client.session.action.sqlscript.sqltofile;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SQLExecutionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.ToBeExecutedNextDecision;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryHolder;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.ArrayList;
import java.util.List;

public class SQLToFileHandler extends SQLExecutionAdapter
{
   public final static ILogger s_log = LoggerController.createLogger(SQLToFileHandler.class);

   private ISession _session;
   private ISQLPanelAPI _sqlPaneAPI;

   private List<QueryHolder> _excelExportQueryHolders = new ArrayList<>();
   private List<QueryHolder> _fileExportQueryHolders = new ArrayList<>();

   public SQLToFileHandler(ISession session, ISQLPanelAPI sqlPaneAPI)
   {
      _session = session;
      _sqlPaneAPI = sqlPaneAPI;
   }

   @Override
   public ToBeExecutedNextDecision toBeExecutedNext(QueryHolder querySql)
   {
      if(ExportToFileHandler.containsMyMarker(querySql.getOriginalQuery()))
      {
         if(false == ExportToFileHandler.willBeHandledByMe(new QueryHolder(querySql.getQuery(), querySql.getOriginalQuery())))
         {
            flushFileExports();
            return ToBeExecutedNextDecision.EXECUTE;
         }

         _fileExportQueryHolders.add(querySql);
         return ToBeExecutedNextDecision.DO_NOT_EXECUTE;
      }
      else if(ExportToMultiSheetMsExcelHandler.containsMyMarkers(querySql.getOriginalQuery()))
      {
         if(false == ExportToMultiSheetMsExcelHandler.willBeHandledByMe(new QueryHolder(querySql.getQuery(), querySql.getOriginalQuery())))
         {
            flushExcelExports();
            return ToBeExecutedNextDecision.EXECUTE;
         }

         _excelExportQueryHolders.add(querySql);
         return ToBeExecutedNextDecision.DO_NOT_EXECUTE;
      }

      flushFileExports();
      flushExcelExports();
      return ToBeExecutedNextDecision.EXECUTE;
   }

   private void flushFileExports()
   {
      if(_fileExportQueryHolders.isEmpty())
      {
         return;
      }

      try
      {
         GUIUtils.processOnSwingEventThread(() -> new ExportToFileHandler(_session, _sqlPaneAPI).exportToFile(_fileExportQueryHolders), true);
      }
      finally
      {
         _fileExportQueryHolders.clear();
      }
   }

   private void flushExcelExports()
   {
      if(_excelExportQueryHolders.isEmpty())
      {
         return;
      }

      try
      {
         GUIUtils.processOnSwingEventThread(() -> new ExportToMultiSheetMsExcelHandler(_session, _sqlPaneAPI).exportToMsExcel(_excelExportQueryHolders), true);
      }
      finally
      {
         _excelExportQueryHolders.clear();
      }
   }


   @Override
   public void executionFinished()
   {
      flushFileExports();
      flushExcelExports();
   }
}
