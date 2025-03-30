package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLPanelApiInfo;

import java.util.List;

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

      // To access cell details popup
      Main.getApplication().getPinnedCellDataDialogHandler();

   }
}
