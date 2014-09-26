package org.squirrelsql.session.sql;

public class BookmarkManager
{
   private SQLTextAreaServices _sqlTextAreaServices;

   public BookmarkManager(SQLTextAreaServices sqlTextAreaServices)
   {
      _sqlTextAreaServices = sqlTextAreaServices;
   }

   public void autocorrOrAbrev()
   {
      if(_sqlTextAreaServices.getTokenAtCarret().equalsIgnoreCase("sf"))
      {
         _sqlTextAreaServices.replaceTokenAtCarretBy("SELECT * FROM");
      }

   }
}
