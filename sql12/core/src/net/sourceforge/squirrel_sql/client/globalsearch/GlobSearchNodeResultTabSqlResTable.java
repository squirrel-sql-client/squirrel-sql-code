package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.ResultTabProvider;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultDataSetAndCellDetailDisplayHandler;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.FirstSearchResult;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class GlobSearchNodeResultTabSqlResTable
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GlobSearchNodeResultTabSqlResTable.class);

   private ResultTabProvider _resultTab;
   private FirstSearchResult _firstSearchResult;

   public GlobSearchNodeResultTabSqlResTable(ResultTabProvider resultTab)
   {
      this._resultTab = resultTab;
   }

   @Override
   public String toString()
   {
      return s_stringMgr.getString("GlobSearchNodeResultTabSearchable.sql", _resultTab.getResultTab().getViewableSqlString());
   }

   public boolean executeSearch(String textToSearch, GlobalSearchType globalSearchType)
   {
      _firstSearchResult = SearchExecutor.searchInResultTable(_resultTab, textToSearch, globalSearchType);

      ResultDataSetAndCellDetailDisplayHandler detailDisplayHandler = _resultTab.getResultTab().getResultsTabsDetailDisplayHandler();
      if( detailDisplayHandler.isOpen() )
      {
         SearchExecutor.searchInDetailDisplay(detailDisplayHandler, textToSearch, globalSearchType);
      }

      return _firstSearchResult.hasResult();
   }

   public FirstSearchResult getSearchExecutorResult()
   {
      return _firstSearchResult;
   }
}
