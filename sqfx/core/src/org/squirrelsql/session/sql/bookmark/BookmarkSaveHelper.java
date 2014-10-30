package org.squirrelsql.session.sql.bookmark;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import org.squirrelsql.services.Dao;
import org.squirrelsql.services.FXMessageBox;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Utils;

import java.util.List;

public class BookmarkSaveHelper
{
   private static I18n _i18n = new I18n(BookmarkSaveHelper.class);


   public static boolean save(Stage dialog, BookmarkEditView view, TreeTableView<BookmarkWrapper> treeTableView)
   {

      if(Utils.isEmptyString(view.txtKey.getText()))
      {
         FXMessageBox.showInfoOk(dialog, _i18n.t("bookmark.edit.no.name"));
         return false;
      }

      if(Utils.isEmptyString(view.txtDescription.getText()))
      {
         FXMessageBox.showInfoOk(dialog, _i18n.t("bookmark.edit.no.description"));
         return false;
      }

      if(Utils.isEmptyString(view.txtSQL.getText()))
      {
         FXMessageBox.showInfoOk(dialog, _i18n.t("bookmark.edit.no.sql"));
         return false;
      }

      TreeItem<BookmarkWrapper> selectedItem = treeTableView.getSelectionModel().getSelectedItem();

      BookmarkWrapper bookmarkWrapper = findDuplicateUniqueSelectionKey(view, selectedItem, treeTableView);
      if(null != bookmarkWrapper)
      {
         FXMessageBox.showInfoOk(dialog, _i18n.t("bookmark.edit.none.unique.selection.key", bookmarkWrapper.getDescription()));
         return false;
      }


      if(null != selectedItem && null != selectedItem.getParent() && selectedItem.getParent().getValue().getBookmarkWrapperType() == BookmarkWrapperType.SQUIRREL_BOOKMARKS_NODE)
      {
         saveChangesToSquirrelBookmark(view, selectedItem);
         return true;
      }

      if(null == selectedItem || selectedItem == treeTableView.getRoot())
      {
         Bookmark newBookmark = new Bookmark(view.txtKey.getText(), view.txtDescription.getText(), view.txtSQL.getText());
         newBookmark.setUseAsBookmark(view.chkBookmark.isSelected());
         newBookmark.setUseAsAbbreviation(view.chkAbbreviation.isSelected());
         BookmarkWrapper newWrapper = new BookmarkWrapper(newBookmark);


         BookmarkPersistence bookmarkPersistence = Dao.loadBookmarkPersistence();
         bookmarkPersistence.getUserBookmarks().add(newBookmark);

         Dao.writeBookmarkPersistence(bookmarkPersistence);

         TreeItem<BookmarkWrapper> newItem = new TreeItem<>(newWrapper);
         getUserBookmarkParent(treeTableView).getChildren().add(newItem);

         treeTableView.getSelectionModel().select(newItem);

         return true;
      }


      BookmarkPersistence bookmarkPersistence = Dao.loadBookmarkPersistence();

      for (Bookmark bookmark : bookmarkPersistence.getUserBookmarks())
      {
         if(selectedItem.getValue().getSelShortcut().equalsIgnoreCase(bookmark.getSelShortcut()))
         {
            BookmarkWrapper value = selectedItem.getValue();
            value.setSelShortcut(view.txtKey.getText());
            value.setDescription(view.txtDescription.getText());
            value.setSql(view.txtSQL.getText());
            value.setUseAsBookmark(view.chkBookmark.isSelected());
            value.setUseAsAbbreviation(view.chkAbbreviation.isSelected());


            bookmark.setSelShortcut(view.txtKey.getText());
            bookmark.setDescription(view.txtDescription.getText());
            bookmark.setSql(view.txtSQL.getText());
            bookmark.setUseAsBookmark(view.chkBookmark.isSelected());
            bookmark.setUseAsAbbreviation(view.chkAbbreviation.isSelected());

            Dao.writeBookmarkPersistence(bookmarkPersistence);

            ///////////////////////////////
            // repaint
            selectedItem.setValue(null);
            selectedItem.setValue(value);
            //
            ////////////////////////////////


            return true;
         }
      }

      throw new IllegalStateException("How could I get here?");
   }

   private static BookmarkWrapper findDuplicateUniqueSelectionKey(BookmarkEditView view, TreeItem<BookmarkWrapper> selectedItem, TreeTableView<BookmarkWrapper> treeTableView)
   {
      for (TreeItem<BookmarkWrapper> item : getUserBookmarkParent(treeTableView).getChildren())
      {
         if(item == selectedItem)
         {
            continue;
         }

         if (matches(view, item))
         {
            return item.getValue();
         }
      }

      for (TreeItem<BookmarkWrapper> item : getSquirrelBookmarkParent(treeTableView).getChildren())
      {
         if(item == selectedItem)
         {
            continue;
         }

         if (matches(view, item))
         {
            return item.getValue();
         }
      }

      return null;
   }

   private static TreeItem<BookmarkWrapper> getSquirrelBookmarkParent(TreeTableView<BookmarkWrapper> treeTableView)
   {
      return treeTableView.getRoot().getChildren().get(1);
   }

   private static TreeItem<BookmarkWrapper> getUserBookmarkParent(TreeTableView<BookmarkWrapper> treeTableView)
   {
      return treeTableView.getRoot().getChildren().get(0);
   }

   private static boolean matches(BookmarkEditView view, TreeItem<BookmarkWrapper> item)
   {
      if(item.getValue().getSelShortcut().equalsIgnoreCase(view.txtKey.getText()))
      {
         if(
                (view.chkAbbreviation.isSelected() && item.getValue().isUseAsAbbreviation())
             || (view.chkBookmark.isSelected() && item.getValue().isUseAsBookmark())
           )
         {
            return true;
         }
      }
      return false;
   }

   private static void saveChangesToSquirrelBookmark(BookmarkEditView view, TreeItem<BookmarkWrapper> item)
   {
      BookmarkPersistence bookmarkPersistence = Dao.loadBookmarkPersistence();

      List<SquirrelBookmarkPersistence> squirrelBookmarkPersistences = bookmarkPersistence.getSquirrelBookmarkPersistences();

      BookmarkWrapper bookmarkWrapper = item.getValue();
      SquirrelBookmarkPersistence sbp = BookmarkUtil.findMatchingSquirrelBookmarkPersistence(bookmarkWrapper.getBookmark(), squirrelBookmarkPersistences);

      if(null == sbp)
      {
         sbp = new SquirrelBookmarkPersistence();
         bookmarkPersistence.getSquirrelBookmarkPersistences().add(sbp);
      }

      sbp.setUseAsBookmark(view.chkBookmark.isSelected());
      sbp.setUseAsAbbreviation(view.chkAbbreviation.isSelected());


      bookmarkWrapper.setUseAsBookmark(view.chkBookmark.isSelected());
      bookmarkWrapper.setUseAsAbbreviation(view.chkAbbreviation.isSelected());


      ///////////////////////
      // repaint
      item.setValue(null);
      item.setValue(bookmarkWrapper);
      //
      ///////////////////////

      Dao.writeBookmarkPersistence(bookmarkPersistence);
   }


   public static void delete(Stage dialog, BookmarkEditView view, TreeTableView<BookmarkWrapper> treeTableView)
   {
      TreeItem<BookmarkWrapper> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
      int selectedIndex = treeTableView.getSelectionModel().getSelectedIndex();

      if(null == selectedItem)
      {
         FXMessageBox.showInfoOk(dialog, _i18n.t("bookmark.delete.no.selection"));
         return;
      }

      BookmarkWrapper value = selectedItem.getValue();

      BookmarkPersistence bookmarkPersistence = Dao.loadBookmarkPersistence();

      for (Bookmark bookmark : bookmarkPersistence.getUserBookmarks())
      {
         if(value.getSelShortcut().equalsIgnoreCase(bookmark.getSelShortcut()))
         {
            bookmarkPersistence.getUserBookmarks().remove(bookmark);
            break;
         }
      }

      Dao.writeBookmarkPersistence(bookmarkPersistence);

      selectedItem.getParent().getChildren().remove(selectedItem);

      if(selectedIndex < treeTableView.getExpandedItemCount())
      {
         treeTableView.getSelectionModel().selectIndices(selectedIndex);
      }
      else if(0 < selectedIndex)
      {
         treeTableView.getSelectionModel().selectIndices(selectedIndex - 1);
      }
      else
      {
         treeTableView.getSelectionModel().clearSelection();
      }


      if(treeTableView.getSelectionModel().getSelectedItem() == treeTableView.getRoot())
      {
         treeTableView.getSelectionModel().clearSelection();
      }
   }
}
