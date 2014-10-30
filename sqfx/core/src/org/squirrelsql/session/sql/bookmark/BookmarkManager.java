package org.squirrelsql.session.sql.bookmark;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.SessionTabContext;
import org.squirrelsql.session.sql.SQLTextAreaServices;

import java.util.ArrayList;
import java.util.List;

public class BookmarkManager
{
   private SQLTextAreaServices _sqlTextAreaServices;

   private I18n _i18n = new I18n(getClass());

   private List<Bookmark> _bookmarks = new ArrayList<>();
   private List<Bookmark> _abbreviations = new ArrayList<>();

   public BookmarkManager(SQLTextAreaServices sqlTextAreaServices, SessionTabContext sessionTabContext)
   {
      _sqlTextAreaServices = sqlTextAreaServices;

      sessionTabContext.bookmarksChangedProperty().addListener((observable, oldValue, newValue) -> reInitBookmarks());

      reInitBookmarks();
   }

   private void reInitBookmarks()
   {
      ArrayList<BookmarkWrapper> allBookmarks = new ArrayList<>();


      BookmarkWrapper root = BookmarkWrapper.createWrapperTree();

      allBookmarks.addAll(root.getKids().get(0).getKids());
      allBookmarks.addAll(root.getKids().get(1).getKids());

      _bookmarks = new ArrayList<>();
      _abbreviations = new ArrayList<>();

      for (BookmarkWrapper bookmarkWrapper : allBookmarks)
      {
         if(bookmarkWrapper.isUseAsBookmark())
         {
            _bookmarks.add(bookmarkWrapper.getBookmark());
         }
         if(bookmarkWrapper.isUseAsAbbreviation())
         {
            _abbreviations.add(bookmarkWrapper.getBookmark());
         }
      }
   }

   public void showBookmarkPopup()
   {
      new FilteredPopupHandler(_sqlTextAreaServices, _bookmarks).showPopup();
   }


   public void execAbreviation()
   {
      for (Bookmark abbreviation : _abbreviations)
      {
         if(_sqlTextAreaServices.getTokenAtCarret().equalsIgnoreCase(abbreviation.getSelShortcut()))
         {
            _sqlTextAreaServices.replaceTokenAtCarretBy(abbreviation.getSql());
            break;
         }
      }
   }

}
