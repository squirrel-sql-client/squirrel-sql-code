package org.squirrelsql.session.sql.tablesearch;

import org.squirrelsql.services.I18n;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;
import org.squirrelsql.services.Utils;
import org.squirrelsql.table.TableLoader;

public class SearchResultHandler
{
   private String _currentCboEditorText;
   private TableSearchType _currentSearchType;
   private boolean _caseSensitive;

   private SearchResult _searchResult = new SearchResult();
   private TableLoader _resultTableLoader;
   private MessageHandler _mh = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);
   private I18n _i18n = new I18n(getClass());

   public SearchResultHandler(TableLoader resultTableLoader)
   {
      _resultTableLoader = resultTableLoader;
      _resultTableLoader.getSquirrelDefaultTableCellChannel().setSearchMatchCheck((valueToRender, cell) -> _searchResult.getSearchMatch(valueToRender, cell));
   }

   public void find(boolean forward, String cboEditorText, TableSearchType searchType, boolean caseSensitive)
   {
      if(searchCriteriaChanged(cboEditorText, searchType, caseSensitive))
      {
         _currentCboEditorText = cboEditorText;
         _currentSearchType = searchType;
         _caseSensitive = caseSensitive;
         _searchResult.reset();
      }

      SearchCell startCell = _searchResult.getStartCell(_resultTableLoader, forward);

      if (forward)
      {
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
      else
      {
         int startCol = startCell.getCol();

         for (int row = startCell.getRow(); row >= 0; row--)
         {
            for (int col = startCol; col >= 0 ; col--)
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
            startCol = _resultTableLoader.getColumnCount() - 1;
         }
      }

      if (forward)
      {

         _mh.info(_i18n.t("search.reached.end"));
      }
      else
      {
         _mh.info(_i18n.t("search.reached.begin"));
      }


      _searchResult.resetCurrentMatchCell();
   }


   private boolean searchCriteriaChanged(String cboEditorText, TableSearchType searchType, boolean caseSensitive)
   {
      if(Utils.compareRespectEmpty(cboEditorText, _currentCboEditorText) && searchType.equals(_currentSearchType) && caseSensitive == _caseSensitive)
      {
         return false;
      }
      return true;
   }

   public void highlightAll(String cboEditorText, TableSearchType searchType, boolean caseSensitive)
   {
      _currentCboEditorText = cboEditorText;
      _currentSearchType = searchType;
      _caseSensitive = caseSensitive;


      _searchResult.reset();


      int matchCount = 0;
      for (int row = 0; row < _resultTableLoader.size(); row++)
      {
         for (int col = 0; col < _resultTableLoader.getColumnCount(); col++)
         {
            if(matches(_resultTableLoader.getCellAsString(row, col)))
            {
               ++ matchCount;
               _searchResult.setCurrentMatchCell(row, col);
            }
         }
      }

      _mh.info(_i18n.t("match.count.found", matchCount));

      _searchResult.resetCurrentMatchCell();

      _resultTableLoader.getTableView().refresh();

   }

   public void unhighlightAll()
   {
      _searchResult.reset();
      _resultTableLoader.getTableView().refresh();
   }



   private boolean matches(String cellData)
   {
      if(false == _caseSensitive)
      {
         if (TableSearchType.REG_EX != _currentSearchType)
         {
            _currentCboEditorText = _currentCboEditorText.toLowerCase();
         }
         cellData = cellData.toLowerCase();
      }

      switch (_currentSearchType)
      {
         case CONTAINS:
            return cellData.contains(_currentCboEditorText);
         case STARTS_WITH:
            return cellData.startsWith(_currentCboEditorText);
         case ENDS_WITH:
            return cellData.endsWith(_currentCboEditorText);
         case REG_EX:
            return cellData.matches(_currentCboEditorText);
      }

      throw new IllegalArgumentException("Unknown search type " + _currentSearchType);
   }

}
