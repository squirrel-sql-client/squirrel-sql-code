package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.client.globalsearch.GlobalSearchType;
import net.sourceforge.squirrel_sql.fw.gui.textfind.MatchBounds;
import net.sourceforge.squirrel_sql.fw.gui.textfind.TextFindMode;
import net.sourceforge.squirrel_sql.fw.gui.textfind.TextFinder;

public class FirstSearchResult
{
   public static final FirstSearchResult EMPTY = new FirstSearchResult();
   private String _contendOfMatchingTableCell;
   private boolean _hasResult;
   private MatchBounds _matchBounds;

   public FirstSearchResult(String contendOfMatchingTableCell, String textToSearch, GlobalSearchType globalSearchType)
   {
      _contendOfMatchingTableCell = contendOfMatchingTableCell;

      _matchBounds = TextFinder.findNthOccurrence(contendOfMatchingTableCell, textToSearch, 1, TextFindMode.ofGlobalSearchType(globalSearchType));

      _hasResult = (null != _matchBounds);
   }

   private FirstSearchResult()
   {
      _hasResult = false;
   }

   public boolean hasResult()
   {
      return _hasResult;
   }

   public String getCellTextTillFirstOccurrence()
   {
      if(null != _contendOfMatchingTableCell)
      {
         return _contendOfMatchingTableCell.substring(0, _matchBounds.getBeginIx());
      }

      return "This is ";
   }

   public String getFirstMatchingText()
   {
      if(null != _contendOfMatchingTableCell)
      {
         return _contendOfMatchingTableCell.substring(_matchBounds.getBeginIx(), _matchBounds.getEndIx());
      }

      return "just";
   }

   public String getCellTextAfterFirstOccurrence()
   {
      if(null != _contendOfMatchingTableCell)
      {
         if(_contendOfMatchingTableCell.length() <= _matchBounds.getEndIx())
         {
            return "";
         }

         return _contendOfMatchingTableCell.substring(_matchBounds.getEndIx(), _contendOfMatchingTableCell.length());
      }

      return " a test.";
   }
}
