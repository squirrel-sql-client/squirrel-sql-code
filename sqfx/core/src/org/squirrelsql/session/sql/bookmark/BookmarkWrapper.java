package org.squirrelsql.session.sql.bookmark;

import org.squirrelsql.services.Dao;

import java.util.ArrayList;
import java.util.List;

public class BookmarkWrapper
{
   private boolean _useAsBookmark = true;
   private boolean _useAsAbbreviation = false;
   private Bookmark _bookmark;

   private BookmarkWrapperType _bookmarkWrapperType;

   private ArrayList<BookmarkWrapper> _kids = new ArrayList<>();

   public BookmarkWrapper(Bookmark bookmark)
   {
      _bookmark = bookmark;
      _bookmarkWrapperType = BookmarkWrapperType.BOOKMARK;
   }

   public BookmarkWrapper(BookmarkWrapperType bookmarkWrapperType)
   {
      _bookmarkWrapperType = bookmarkWrapperType;
   }

   public List<BookmarkWrapper> getKids()
   {
      return _kids;
   }

   public String getSql()
   {
      if(BookmarkWrapperType.BOOKMARK == _bookmarkWrapperType)
      {
         return _bookmark.getSql();
      }

      return null;
   }

   public void setSql(String sql)
   {
      if(BookmarkWrapperType.BOOKMARK == _bookmarkWrapperType)
      {
         _bookmark.setSql(sql);
      }
   }

   public String getSelShortcut()
   {
      if(BookmarkWrapperType.BOOKMARK == _bookmarkWrapperType)
      {
         return _bookmark.getSelShortcut();
      }

      return _bookmarkWrapperType.getNodeName();
   }

   public void setSelShortcut(String selShortcut)
   {
      if(BookmarkWrapperType.BOOKMARK == _bookmarkWrapperType)
      {
         _bookmark.setSelShortcut(selShortcut);
      }
   }

   public String getDescription()
   {
      if(BookmarkWrapperType.BOOKMARK == _bookmarkWrapperType)
      {
         return _bookmark.getDescription();
      }

      return null;
   }

   public void setDescription(String description)
   {
      if(BookmarkWrapperType.BOOKMARK == _bookmarkWrapperType)
      {
         _bookmark.setDescription(description);
      }
   }


   public boolean isUseAsBookmark()
   {
      if(BookmarkWrapperType.BOOKMARK == _bookmarkWrapperType)
      {
         return _bookmark.isUseAsBookmark();
      }

      return false;
   }

   public void setUseAsBookmark(boolean useAsBookmark)
   {
      if(BookmarkWrapperType.BOOKMARK == _bookmarkWrapperType)
      {
         _bookmark.setUseAsBookmark(useAsBookmark);
      }
   }

   public boolean isUseAsAbbreviation()
   {
      if(BookmarkWrapperType.BOOKMARK == _bookmarkWrapperType)
      {
         return _bookmark.isUseAsAbbreviation();
      }

      return false;
   }

   public void setUseAsAbbreviation(boolean useAsAbbreviation)
   {
      if(BookmarkWrapperType.BOOKMARK == _bookmarkWrapperType)
      {
         _bookmark.setUseAsAbbreviation(useAsAbbreviation);
      }
   }

   public Bookmark getBookmark()
   {
      return _bookmark;
   }

   public BookmarkWrapperType getBookmarkWrapperType()
   {
      return _bookmarkWrapperType;
   }

   public static BookmarkWrapper createWrapperTree()
   {
      BookmarkWrapper root = new BookmarkWrapper(BookmarkWrapperType.ROOT_NODE);

      BookmarkPersistence bookmarkPersistence = Dao.loadBookmarkPersistence();

      BookmarkWrapper userBookmarksNode = new BookmarkWrapper(BookmarkWrapperType.USER_BOOKMARKS_NODE);
      root.getKids().add(userBookmarksNode);

      for (Bookmark bookmark : bookmarkPersistence.getUserBookmarks())
      {
         userBookmarksNode.getKids().add(new BookmarkWrapper(bookmark));
      }



      BookmarkWrapper squirrelBookmarksNode = new BookmarkWrapper(BookmarkWrapperType.SQUIRREL_BOOKMARKS_NODE);
      root.getKids().add(squirrelBookmarksNode);

      for (Bookmark bookmark : SQuirreLBookmarks.BOOKMARKS)
      {
         SquirrelBookmarkPersistence sbp = BookmarkUtil.findMatchingSquirrelBookmarkPersistence(bookmark, bookmarkPersistence.getSquirrelBookmarkPersistences());

         if (null != sbp)
         {
            bookmark.setUseAsAbbreviation(sbp.isUseAsAbbreviation());
            bookmark.setUseAsBookmark(sbp.isUseAsBookmark());
         }

         squirrelBookmarksNode.getKids().add(new BookmarkWrapper(bookmark));
      }



      return root;
   }


}
