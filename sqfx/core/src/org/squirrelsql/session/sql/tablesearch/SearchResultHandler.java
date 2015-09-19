package org.squirrelsql.session.sql.tablesearch;

import org.squirrelsql.services.Utils;
import org.squirrelsql.table.SquirrelDefaultTableCell;
import org.squirrelsql.table.TableLoader;

public class SearchResultHandler
{
   private String _currentCboEditorText;
   private TableSearchType _currentSearchType;

   private SearchResult _searchResult = new SearchResult();
   private TableLoader _resultTableLoader;

   public SearchResultHandler(TableLoader resultTableLoader)
   {
      _resultTableLoader = resultTableLoader;
      _resultTableLoader.getSquirrelDefaultTableCellChannel().setSearchMatchCheck((valueToRender, cell) -> _searchResult.getSearchMatch(valueToRender, cell));
   }

   public void find(boolean forward, String cboEditorText, TableSearchType searchType)
   {
      if(searchCriteriaChanged(cboEditorText, searchType))
      {
         _currentCboEditorText = cboEditorText;
         _currentSearchType = searchType;
         _searchResult.reset();
      }

      SearchCell startCell = _searchResult.getStartCell(_resultTableLoader, forward);
      int startCol = startCell.getCol();

      for (int row = startCell.getRow(); row < _resultTableLoader.size(); row++)
      {
         for (int col = startCol; col < _resultTableLoader.getColumnCount(); col++)
         {
            if(matches(_resultTableLoader.getCellAsString(row, col)))
            {
               _searchResult.setCurrentMatchCell(row, col);
               _resultTableLoader.getTableView().scrollTo(row);
               _resultTableLoader.getTableView().scrollToColumnIndex(col);
               _resultTableLoader.getTableView().refresh();

               return;
            }
         }
         startCol = 0;
      }
   }


   private boolean searchCriteriaChanged(String cboEditorText, TableSearchType searchType)
   {
      if(Utils.compareRespectEmpty(cboEditorText, _currentCboEditorText) && searchType.equals(_currentSearchType))
      {
         return false;
      }
      return true;
   }

   public void highlightAll(String cboEditorText, TableSearchType selectedItem)
   {

   }

   public void unhighlightAll()
   {

   }



   /////////////////////////////////
   // TODO
   private boolean matches(String toTest)
   {
      return Utils.compareRespectEmpty(_currentCboEditorText, toTest);
   }

   private boolean checkMatch(Object valueToRender, SquirrelDefaultTableCell cell)
   {
      return matches("" + valueToRender);
   }
   //
   ///////////////////////////////

}
