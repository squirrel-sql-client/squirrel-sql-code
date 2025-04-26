package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup.CellDataDialog;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.FirstSearchResult;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class GlobSearchNodeCellDataDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GlobSearchNodeCellDataDialog.class);

   private final CellDataDialog _cellDataDialog;
   private FirstSearchResult _firstSearchResult;

   public GlobSearchNodeCellDataDialog(CellDataDialog cellDataDialog)
   {
      _cellDataDialog = cellDataDialog;
   }

   public boolean executeSearch(String textToSearch, GlobalSearchType globalSearchType)
   {
      _firstSearchResult = SearchExecutor.searchInCellDataDialog(_cellDataDialog, textToSearch, globalSearchType);
      return _firstSearchResult.hasResult();
   }

   @Override
   public String toString()
   {
      return s_stringMgr.getString("GlobSearchNodeCellDataDialog.dialog.name", _cellDataDialog.getTitle());
   }

   public FirstSearchResult getSearchExecutorResult()
   {
      return _firstSearchResult;
   }
}
