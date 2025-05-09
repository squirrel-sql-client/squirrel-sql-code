package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.ResultTabProvider;
import net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup.CellDataDialog;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultDataSetAndCellDetailDisplayHandler;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.FirstSearchResult;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.GlobalFindRemoteControl;

public class SearchExecutor
{
   public static FirstSearchResult searchInResultTable(ResultTabProvider resultTabProvider, String textToSearch, GlobalSearchType globalSearchType)
   {
      resultTabProvider.getResultTab().setSQLResultTabSelected();
      GlobalFindRemoteControl remoteControl = resultTabProvider.getResultTab().getDataSetViewerFindRemoteControlOfSQLQueryResultTabOrNull();

      if(null != remoteControl)
      {
         return remoteControl.executeFindTillFirstResult(textToSearch, globalSearchType);
      }

      return FirstSearchResult.EMPTY;
   }

   public static FirstSearchResult searchInDetailDisplay(ResultDataSetAndCellDetailDisplayHandler detailDisplayHandler, String textToSearch, GlobalSearchType globalSearchType)
   {
      GlobalFindRemoteControl remoteControl = detailDisplayHandler.getCellDetailFindRemoteControlOrNull();

      if(null == remoteControl)
      {
         return FirstSearchResult.EMPTY;
      }

      return remoteControl.executeFindTillFirstResult(textToSearch, globalSearchType);
   }

   public static FirstSearchResult searchInCellDataDialog(CellDataDialog cellDataDialog, String textToSearch, GlobalSearchType globalSearchType)
   {
      GlobalFindRemoteControl globalFindRemoteControl = cellDataDialog.getCellDetailFindRemoteControlOrNull();
      if(null == globalFindRemoteControl)
      {
         return FirstSearchResult.EMPTY;
      }

      return globalFindRemoteControl.executeFindTillFirstResult(textToSearch, globalSearchType);
   }
}
