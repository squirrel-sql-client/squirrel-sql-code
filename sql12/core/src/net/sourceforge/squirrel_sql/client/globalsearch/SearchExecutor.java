package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.ResultTabProvider;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.DataSetViewerFindRemoteControl;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.FirstSearchResult;

public class SearchExecutor
{
   public static FirstSearchResult search(ResultTabProvider resultTabProvider, String textToSearch, GlobalSearchType globalSearchType)
   {
      resultTabProvider.getResultTab().setSQLResultTabSelected();
      DataSetViewerFindRemoteControl remoteControl = resultTabProvider.getResultTab().getDataSetViewerFindRemoteControlOfSQLQueryResultTabOrNull();

      if(null != remoteControl)
      {
         return remoteControl.executeFindTillFirstResult(textToSearch, globalSearchType);
      }

      return FirstSearchResult.EMPTY;
   }
}
