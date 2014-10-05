package org.squirrelsql.session.sql;

import org.squirrelsql.session.action.StandardActionConfiguration;

public class BookmarkManager
{
   private SQLTextAreaServices _sqlTextAreaServices;

   public BookmarkManager(SQLTextAreaServices sqlTextAreaServices)
   {
      _sqlTextAreaServices = sqlTextAreaServices;
   }

   public void execBookmark()
   {
      System.out.println("BookmarkManager.execBookmark on " + StandardActionConfiguration.EXEC_BOOKMARK.getActionConfiguration().getKeyCodeCombination());
   }

   public void execAbreviation()
   {
      if(_sqlTextAreaServices.getTokenAtCarret().equalsIgnoreCase("sf"))
      {
         _sqlTextAreaServices.replaceTokenAtCarretBy("SELECT * FROM");
      }
   }
}
