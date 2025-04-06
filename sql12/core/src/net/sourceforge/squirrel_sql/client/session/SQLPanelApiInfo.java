package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecutor;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.ResultTabProvider;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SQLPanelApiInfo
{
   private SessionInternalFrame _parentSessionMainWindow;

   private SQLInternalFrame _parentSqlInternalFrame;

   private AdditionalSQLTab _parentAdditionalSQLTab;

   private ISQLPanelAPI _sqlPanelApi;

   public SQLPanelApiInfo(SessionInternalFrame parentSessionMainWindow, ISQLPanelAPI mainSQLPanelAPI)
   {
      _parentSessionMainWindow = parentSessionMainWindow;
      _sqlPanelApi = mainSQLPanelAPI;

      // Note:
      //_sessionMainWindow.getTitleWithoutFile();

   }

   public SQLPanelApiInfo(SessionInternalFrame parentSessionMainWindow, AdditionalSQLTab parentAdditionalSQLTab, ISQLPanelAPI additionalTabSqlPanelApi)
   {
      _parentSessionMainWindow = parentSessionMainWindow;
      _parentAdditionalSQLTab = parentAdditionalSQLTab;
      _sqlPanelApi = additionalTabSqlPanelApi;

      // Note:
      //_additionalSQLTab.getTitleWithoutFile();
   }

   public SQLPanelApiInfo(SQLInternalFrame parentSqlInternalFrame, ISQLPanelAPI sqlInternalFramePanelApi)
   {
      _parentSqlInternalFrame = parentSqlInternalFrame;
      _sqlPanelApi = sqlInternalFramePanelApi;

      // Note:
      //_sqlInternalFrame.getTitleWithoutFile();
   }


   public static SQLPanelApiInfo ofSQLInternalFrame(SQLInternalFrame sqlInternalFrame)
   {
      sqlInternalFrame.getMainSQLPanelAPI();
      return new SQLPanelApiInfo(sqlInternalFrame, sqlInternalFrame.getMainSQLPanelAPI());
   }

   public static List<SQLPanelApiInfo> ofSessionMainWindow(SessionInternalFrame sessionMainWindow)
   {
      List<SQLPanelApiInfo> ret = new ArrayList<>();
      sessionMainWindow.getMainSQLPanelAPI();
      ret.add(new SQLPanelApiInfo(sessionMainWindow, sessionMainWindow.getMainSQLPanelAPI()));

      for(AdditionalSQLTab additionalSQLTab : sessionMainWindow.getSession().getSessionPanel().getAdditionalSQLTabs())
      {
         ret.add(new SQLPanelApiInfo(sessionMainWindow, additionalSQLTab, additionalSQLTab.getSQLPanelAPI()));
      }

      return ret;
   }

   public List<ResultTabProvider> getAllOpenResultTabs()
   {
      ISQLResultExecutor sqlResultExecuter = _sqlPanelApi.getSQLResultExecuter();
      if (sqlResultExecuter != null)
      {
         sqlResultExecuter.getAllSqlResultTabs().stream().map(t -> new ResultTabProvider(t)).collect(Collectors.toList());
      }

      return List.of();
   }
}
