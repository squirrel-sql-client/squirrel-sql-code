package org.squirrelsql.session.sql.bookmark;

import java.util.ArrayList;
import java.util.List;

public class BookmarkPersistence
{
   private List<SquirrelBookmarkPersistence> _squirrelBookmarkPersistences = new ArrayList<>();
   private List<Bookmark> _userBookmarks = new ArrayList<>();


   public BookmarkPersistence()
   {
      for (Bookmark bookmark : SQuirreLBookmarks.BOOKMARKS)
      {
         SquirrelBookmarkPersistence squirrelBookmarkPersistence = new SquirrelBookmarkPersistence();

         squirrelBookmarkPersistence.setSelShortcut(bookmark.getSelShortcut());
         squirrelBookmarkPersistence.setUseAsAbbreviation(bookmark.isUseAsAbbreviation());
         squirrelBookmarkPersistence.setUseAsBookmark(bookmark.isUseAsBookmark());

         _squirrelBookmarkPersistences.add(squirrelBookmarkPersistence);
      }

   }

   public List<SquirrelBookmarkPersistence> getSquirrelBookmarkPersistences()
   {
      return _squirrelBookmarkPersistences;
   }

   public void setSquirrelBookmarkPersistences(List<SquirrelBookmarkPersistence> squirrelBookmarkPersistences)
   {
      _squirrelBookmarkPersistences = new ArrayList<>();

      for (Bookmark bookmark : SQuirreLBookmarks.BOOKMARKS)
      {
         SquirrelBookmarkPersistence squirrelBookmarkPersistence = new SquirrelBookmarkPersistence();

         squirrelBookmarkPersistence.setSelShortcut(bookmark.getSelShortcut());
         squirrelBookmarkPersistence.setUseAsAbbreviation(bookmark.isUseAsAbbreviation());
         squirrelBookmarkPersistence.setUseAsBookmark(bookmark.isUseAsBookmark());

         SquirrelBookmarkPersistence overWrite = BookmarkUtil.findMatchingSquirrelBookmarkPersistence(bookmark, squirrelBookmarkPersistences);

         if(null != overWrite)
         {
            squirrelBookmarkPersistence.setUseAsAbbreviation(overWrite.isUseAsAbbreviation());
            squirrelBookmarkPersistence.setUseAsBookmark(overWrite.isUseAsBookmark());
         }

         _squirrelBookmarkPersistences.add(squirrelBookmarkPersistence);
      }
   }

   public List<Bookmark> getUserBookmarks()
   {
      return _userBookmarks;
   }

   public void setUserBookmarks(List<Bookmark> userBookmarks)
   {
      _userBookmarks = userBookmarks;
   }
}
