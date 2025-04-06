package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.ResultTabProvider;
import net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup.CellDataDialog;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultDataSetAndCellDetailDisplayHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GlobalSearcher
{
   /**
    * Information on how to access SQL result tabs of SqlPanels can be found in
    * {@link net.sourceforge.squirrel_sql.fw.gui.action.fileexport.MultipleSqlResultExportChannel}
    */
   public void searchGlobally(String toSearch, GlobalSearchType globalType)
   {
      List<ISession> openSessions = Main.getApplication().getSessionManager().getOpenSessions();


      List<ResultTabProvider> resultTabProviders = new ArrayList<>();
      for(ISession openSession : openSessions)
      {
         openSession.getAllSQLPanelApiInfos().forEach(pnlInfo -> resultTabProviders.addAll(pnlInfo.getAllOpenResultTabs()));
      }

      // TODO For now just shows how to access the ResultDataSetAndCellDetailDisplayHandler of a ResultTab.
      if(null != resultTabProviders.get(0).getResultTab())
      {
         ResultDataSetAndCellDetailDisplayHandler resultsTabsDetailDisplayHandler = resultTabProviders.get(0).getResultTab().getResultsTabsDetailDisplayHandler();
      }

      Set<CellDataDialog> openCellDataDialogs = Main.getApplication().getGlobalCellDataDialogManager().getOpenCellDataDialogs();
   }
}
