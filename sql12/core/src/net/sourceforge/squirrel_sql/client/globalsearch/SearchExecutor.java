package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.ResultTabProvider;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultDataSetAndCellDetailDisplayHandler;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.DataSetViewerFindRemoteControl;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.FirstSearchResult;

public class SearchExecutor
{
   public static FirstSearchResult searchInResultTable(ResultTabProvider resultTabProvider, String textToSearch, GlobalSearchType globalSearchType)
   {
      resultTabProvider.getResultTab().setSQLResultTabSelected();
      DataSetViewerFindRemoteControl remoteControl = resultTabProvider.getResultTab().getDataSetViewerFindRemoteControlOfSQLQueryResultTabOrNull();

      if(null != remoteControl)
      {
         return remoteControl.executeFindTillFirstResult(textToSearch, globalSearchType);
      }

      return FirstSearchResult.EMPTY;
   }

   public static FirstSearchResult searchInDetailDisplay(ResultDataSetAndCellDetailDisplayHandler detailDisplayHandler, String textToSearch, GlobalSearchType globalSearchType)
   {
      throw new UnsupportedOperationException("TODO");
   }
}
