package org.squirrelsql.table;

public class SquirrelDefaultTableCellChannel
{
   private SearchMatchCheck _searchMatchCheck;

   public SearchMatch getSearchMatch(Object valueToRender, SquirrelDefaultTableCell cell)
   {
      if (null == _searchMatchCheck)
      {
         return SearchMatch.MATCH_NONE;
      }


      return _searchMatchCheck.getSearchMatch(valueToRender, cell);
   }

   public void setSearchMatchCheck(SearchMatchCheck searchMatchCheck)
   {
      _searchMatchCheck = searchMatchCheck;
   }
}
