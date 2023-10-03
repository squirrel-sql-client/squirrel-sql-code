package net.sourceforge.squirrel_sql.client.session.action.sqlscript.sqltofile;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryHolder;

public class SQLToFileHandler implements ISQLExecutionListener
{
   private ISession _session;
   private ISQLPanelAPI _sqlPaneAPI;



   public SQLToFileHandler(ISession session, ISQLPanelAPI sqlPaneAPI)
   {
      _session = session;
      _sqlPaneAPI = sqlPaneAPI;
   }

   @Override
   public String statementExecuting(String initialSql)
   {
      if(ExportToFileHandler.containsMyMarker(initialSql))
      {
         return new ExportToFileHandler(_session, _sqlPaneAPI).exportToFile(initialSql);
      }
      else if(ExportToMultiSheetMsExcelHandler.containsMyMarkers(initialSql))
      {
         return new ExportToMultiSheetMsExcelHandler(_session, _sqlPaneAPI).exportToMsExcel(initialSql);
      }

      return initialSql;

   }


   @Override
   public void statementExecuted(QueryHolder sql) {}

   @Override
   public void executionFinished() {}
}
