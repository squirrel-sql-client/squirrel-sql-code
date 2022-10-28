package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecutorPanel;

import java.util.List;
import java.util.stream.Collectors;

public class MultipleSqlResultExportChannel
{
   public List<SqlResultTabHandle> getSqlResultTabHandles()
   {
      final ISQLPanelAPI sqlPanelApi = Main.getApplication().getSessionManager().getActiveSession().getSQLPanelAPIOfActiveSessionWindow();

      List<IResultTab> tabs = sqlPanelApi.getSQLResultExecuter().getAllSqlResultTabs();

      return tabs.stream().map(t -> new SqlResultTabHandle((ResultTab)t, (SQLResultExecutorPanel)sqlPanelApi.getSQLResultExecuter())).collect(Collectors.toList());
   }
}
