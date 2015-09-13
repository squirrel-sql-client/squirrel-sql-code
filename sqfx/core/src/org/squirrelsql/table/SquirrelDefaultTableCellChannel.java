package org.squirrelsql.table;

public class SquirrelDefaultTableCellChannel
{
   private SearchMatchCheck _searchMatchCheck;

   public boolean isSearchMatch(Object valueToRender, SquirrelDefaultTableCell cell)
   {
      if (null == _searchMatchCheck)
      {
         return false;
      }


      return _searchMatchCheck.isSearchMatch(valueToRender, cell);
   }

   public void setSearchMatchCheck(SearchMatchCheck searchMatchCheck)
   {
      _searchMatchCheck = searchMatchCheck;
   }
}
