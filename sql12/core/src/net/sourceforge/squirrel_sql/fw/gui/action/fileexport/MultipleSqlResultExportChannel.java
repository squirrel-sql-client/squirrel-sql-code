package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecutorPanel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleSqlResultExportChannel
{
   public List<SqlResultTabHandle> getSqlResultTabHandles()
   {
      final ISession activeSession = Main.getApplication().getSessionManager().getActiveSession();

      if(null == activeSession)
      {
         return Collections.emptyList();
      }

      final ISQLPanelAPI sqlPanelApi = activeSession.getSQLPanelAPIOfActiveSessionWindow(true);

      if(null == sqlPanelApi)
      {
         return Collections.emptyList();
      }

      List<IResultTab> tabs = sqlPanelApi.getSQLResultExecuter().getAllSqlResultTabs();

      return tabs.stream().map(t -> createSqlResultTabHandle(sqlPanelApi, (ResultTab) t, activeSession)).collect(Collectors.toList());
   }

   private static SqlResultTabHandle createSqlResultTabHandle(ISQLPanelAPI sqlPanelApi, ResultTab t, ISession session)
   {
      return new SqlResultTabHandle(t, (SQLResultExecutorPanel) sqlPanelApi.getSQLResultExecuter(), session);
   }
}
