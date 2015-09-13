package org.squirrelsql.session.sql.tablesearch;

import org.squirrelsql.table.SearchMatchCheck;
import org.squirrelsql.table.SquirrelDefaultTableCell;
import org.squirrelsql.table.TableLoader;

public class SearchResultHandler
{
   public SearchResultHandler(TableLoader resultTableLoader)
   {
      resultTableLoader.getSquirrelDefaultTableCellChannel().setSearchMatchCheck(new SearchMatchCheck()
      {
         @Override
         public boolean isSearchMatch(Object valueToRender, SquirrelDefaultTableCell cell)
         {
            return true;
         }
      });
   }

   public void find(boolean forward)
   {

   }

   public void highlightAll()
   {

   }

   public void unhighlightAll()
   {

   }
}
