package net.sourceforge.squirrel_sql.client.globalsearch;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLPanelApiInfo;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.ResultTabProvider;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultDataSetAndCellDetailDisplayHandler;

public class GlobalSearcher
{
   /**
    * Information on how to access SQL result tabs of SqlPanels can be found in
    * {@link net.sourceforge.squirrel_sql.fw.gui.action.fileexport.MultipleSqlResultExportChannel}
    */
   public void searchGlobally(String textToSearch, GlobalSearchType globalSearchType)
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
               GlobSearchNodeResultTab gsnResultTab = new GlobSearchNodeResultTab(resultTab);
               gsnSQLPanel.addGlobSearchNodeResultTab(gsnResultTab);

               gsnResultTab.setResultTab(new GlobSearchNodeResultTabSqlResTable(resultTab));

               ResultDataSetAndCellDetailDisplayHandler detailDisplayHandler = resultTab.getResultTab().getResultsTabsDetailDisplayHandler();
               if( detailDisplayHandler.isOpen() )
               {
                  gsnResultTab.setDetailDisplay(new GlobSearchNodeResultDetailDisplay(detailDisplayHandler));
               }
            }
         }
      }

      new GlobalSearchCtrl(globSearchNodeSessions, textToSearch, globalSearchType);
   }
}
