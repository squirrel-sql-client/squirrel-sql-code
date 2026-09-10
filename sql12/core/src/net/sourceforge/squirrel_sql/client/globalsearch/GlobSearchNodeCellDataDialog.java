package net.sourceforge.squirrel_sql.client.globalsearch;

import net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup.CellDataWindow;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.FirstSearchResult;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class GlobSearchNodeCellDataDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GlobSearchNodeCellDataDialog.class);

   private final CellDataWindow _cellDataWindow;
   private FirstSearchResult _firstSearchResult;

   public GlobSearchNodeCellDataDialog(CellDataWindow cellDataWindow)
   {
      _cellDataWindow = cellDataWindow;
   }

   public boolean executeSearch(String textToSearch, GlobalSearchType globalSearchType)
   {
      _firstSearchResult = SearchExecutor.searchInCellDataDialog(_cellDataWindow, textToSearch, globalSearchType);
      return _firstSearchResult.hasResult();
   }

   @Override
   public String toString()
   {
      return s_stringMgr.getString("GlobSearchNodeCellDataDialog.dialog.name", _cellDataWindow.getCellDataWindowAdapter().getTitle());
   }

   public FirstSearchResult getSearchExecutorResult()
   {
      return _firstSearchResult;
   }

   public void bringDialogToFront()
   {
      _cellDataWindow.getCellDataWindowAdapter().toFront();
   }
}
