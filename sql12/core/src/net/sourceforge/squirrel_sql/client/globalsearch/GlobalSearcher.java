package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLPanelApiInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup.CellDataDialog;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ShowCellDetailCtrl;

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

      for(ISession openSession : openSessions)
      {
         List<SQLPanelApiInfo> sqlPanelApiInfos =  openSession.getAllSQLPanelApiInfos();
      }

      Set<CellDataDialog> openCellDataDialogs = Main.getApplication().getGlobalCellDataDisplayManager().getOpenCellDataDialogs();
      Set<ShowCellDetailCtrl> openCellDetailCtrls = Main.getApplication().getGlobalCellDataDisplayManager().getCellDetailCtrls();
   }
}
