package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.SQLPanelApiInfo;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.ResultTabProvider;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultDataSetAndCellDetailDisplayHandler;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.FirstSearchResult;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class GlobSearchNodeResultTabSqlResTable
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GlobSearchNodeResultTabSqlResTable.class);

   private ResultTabProvider _resultTabProvider;
   private FirstSearchResult _firstSearchResult;

   public GlobSearchNodeResultTabSqlResTable(ResultTabProvider resultTabProvider)
   {
      this._resultTabProvider = resultTabProvider;
   }

   @Override
   public String toString()
   {
      return s_stringMgr.getString("GlobSearchNodeResultTabSearchable.sql", _resultTabProvider.getResultTab().getViewableSqlString());
   }

   public boolean executeSearch(String textToSearch, GlobalSearchType globalSearchType)
   {
      _firstSearchResult = SearchExecutor.searchInResultTable(_resultTabProvider, textToSearch, globalSearchType);

      ResultDataSetAndCellDetailDisplayHandler detailDisplayHandler = _resultTabProvider.getResultTab().getResultsTabsDetailDisplayHandler();
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

   public void bringResultToFront(SQLPanelApiInfo parentSqlPanelApiInfo)
   {
      ISQLPanelAPI sqlPanelApi = parentSqlPanelApiInfo.getSqlPanelApi();
      int caretPos = sqlPanelApi.getCaretPosition();

      switch(sqlPanelApi.getSQLPanelPosition())
      {
         case MAIN_TAB_IN_SESSION_WINDOW:
            SessionUtils.activateMainSqlTab(sqlPanelApi.getSession().getSessionInternalFrame(), caretPos, false);
            break;
         case ADDITIONAL_TAB_IN_SESSION_WINDOW:
            SessionUtils.activateAdditionalSqlTab(sqlPanelApi.getSession().getSessionInternalFrame(), parentSqlPanelApiInfo.getParentAdditionalSQLTab(), caretPos, false);
            break;
         case IN_SQL_WORKSHEET:
            SessionUtils.activateSqlInternalFrame(parentSqlPanelApiInfo.getParentSqlInternalFrame(), caretPos, false);
            break;
      }

      sqlPanelApi.getSQLResultExecuter().selectResultTab(_resultTabProvider.getResultTab());
   }
}
