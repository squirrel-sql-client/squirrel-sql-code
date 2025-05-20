package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLPanelApiInfo;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.ResultTabProvider;

import java.util.ArrayList;
import java.util.List;

public class GlobalSearchUtil
{
   /**
    * Information on how to access SQL result tabs of SqlPanels can be found in
    * {@link net.sourceforge.squirrel_sql.fw.gui.action.fileexport.MultipleSqlResultExportChannel}
    */
   public static NodesToSearch getNodesToSearch()
   {
      List<ISession> openSessions = Main.getApplication().getSessionManager().getOpenSessions();
      List<GlobSearchNodeSession> globSearchNodeSessions = new ArrayList<>();

      for( ISession openSession : openSessions )
      {
         GlobSearchNodeSession gsnSession = new GlobSearchNodeSession(openSession);
         globSearchNodeSessions.add(gsnSession);

         for( SQLPanelApiInfo sqlPanelApiInfo : openSession.getAllSQLPanelApiInfos() )
         {
            GlobSearchNodeSqlPanel gsnSQLPanel = new GlobSearchNodeSqlPanel(sqlPanelApiInfo);
            gsnSession.addGlobSearchNodeSQLPanel(gsnSQLPanel);

            for( ResultTabProvider resultTab : gsnSQLPanel.getSqlPanelApiInfo().getAllOpenResultTabs() )
            {
               GlobSearchNodeResultTabSqlResTable nodeResultTabSqlResTable = new GlobSearchNodeResultTabSqlResTable(resultTab);
               gsnSQLPanel.addGlobSearchNodeResultTabSqlResTable(nodeResultTabSqlResTable);
            }
         }
      }

      List<GlobSearchNodeCellDataDialog> globSearchNodeCellDataDialogs =
            Main.getApplication().getGlobalCellDataDialogManager().getOpenCellDataDialogs().stream()
                // Exclude the pinned dialog because it will automatically positioned when result data matches are found.
                .filter(ocd -> false == Main.getApplication().getGlobalCellDataDialogManager().isPinned(ocd))
                .map(ocd -> new GlobSearchNodeCellDataDialog(ocd)).toList();

      NodesToSearch result = new NodesToSearch(globSearchNodeSessions, globSearchNodeCellDataDialogs);
      return result;
   }

}
