package org.squirrelsql.session.sql.bookmark;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;
import org.squirrelsql.session.completion.CompletionUtil;
import org.squirrelsql.session.sql.SQLTextAreaServices;
import org.squirrelsql.workaround.KeyMatchWA;

import java.util.ArrayList;
import java.util.List;

public class BookmarkManager
{
   private SQLTextAreaServices _sqlTextAreaServices;

   private I18n _i18n = new I18n(getClass());

   private ArrayList<Bookmark> _bookmarks = new ArrayList<>();
   private Popup _popup = new Popup();

   public BookmarkManager(SQLTextAreaServices sqlTextAreaServices)
   {
      _sqlTextAreaServices = sqlTextAreaServices;

      _bookmarks.add(new Bookmark("SELECT * FROM articles", "art", "articles"));
      _bookmarks.add(new Bookmark("SELECT * FROM receipts", "recs", "receipts"));
      _bookmarks.add(new Bookmark("SELECT * FROM receipts\nINNER JOIN receipt_lines ON receipts.id = receipt_lines.receipt_id", "reclines", "receipt_lines"));
   }

   private void initDisplaySpaces()
   {
      if(0 == _bookmarks.size())
      {
         return;
      }

      Bookmark max = _bookmarks.stream().max((b1, b2) -> b1.getSelShortcut().length() - b2.getSelShortcut().length()).get();
      _bookmarks.forEach(b -> b.setDisplaySpace(max.getSelShortcut().length() - b.getSelShortcut().length() + 3));
   }

   public void showBookmarkPopup()
   {
      _popup.hide();

      BorderPane bp = new BorderPane();

      TextField txt = new TextField();
      txt.setFont(_sqlTextAreaServices.getFont());


      bp.setTop(txt);

      ListView<Bookmark> listView = new ListView<>();

      initDisplaySpaces();

      if(0 == _bookmarks.size())
      {
         new MessageHandler(this.getClass(), MessageHandlerDestination.MESSAGE_PANEL).info(_i18n.t("no.bookmarks.defined"));
         return;
      }
      else if( 1 == _bookmarks.size())
      {
         runBookmark(_bookmarks.get(0));
         return;
      }

      filterPopupList("", listView);

      CompletionUtil.prepareCompletionList(listView, _sqlTextAreaServices);

      bp.setCenter(listView);

      bp.setStyle("-fx-border-color: lightblue; -fx-border-width: 2;");

      _popup.getContent().add(bp);
      Point2D cl = _sqlTextAreaServices.getCarretLocationOnScreen();

      _popup.focusedProperty().addListener((observable, oldValue, newValue) -> hideIfNotFocused(newValue));

      listView.setOnKeyTyped(keyEvent -> onHandleKeyOnPopup((KeyEvent) keyEvent, txt, listView));

      listView.setOnMouseClicked(event -> onMouseClickedList(event, listView));

      _popup.show(_sqlTextAreaServices.getTextArea(), cl.getX(), cl.getY());

      txt.setEditable(false);
      txt.focusedProperty().addListener((observable, oldValue, newValue) -> listView.requestFocus());
      txt.setFocusTraversable(false);
      listView.requestFocus();
   }

   private void onMouseClickedList(MouseEvent event, ListView<Bookmark> listView)
   {
      if(event.getClickCount() >= 2)
      {
         runSelectedListItem(listView);
      }
   }

   private void runBookmark(Bookmark bookmark)
   {
      _sqlTextAreaServices.insertAtCarret("\n" + bookmark.getSql());
   }

   private void onHandleKeyOnPopup(KeyEvent keyEvent, TextField txt, ListView<Bookmark> listView)
   {
      if(false == checkPopupFinish(keyEvent, listView))
      {
         if ("\b".equals(keyEvent.getCharacter()))
         {
            String text = txt.getText();
            if(0 < text.length())
            {
               text = text.substring(0, text.length() -1);
               txt.setText(text);
            }
         }
         else
         {
            txt.appendText(keyEvent.getCharacter());
         }

         filterPopupList(txt.getText(), listView);

         keyEvent.consume();
      }

   }

   private void filterPopupList(String selShortcutStart, ListView<Bookmark> listView)
   {
      ArrayList<Bookmark> toRemove = new ArrayList<>();

      for (Bookmark bookmark : _bookmarks)
      {
         if(false == bookmark.getSelShortcut().toLowerCase().startsWith(selShortcutStart.toLowerCase()))
         {
            toRemove.add(bookmark);
         }
      }

      ObservableList<Bookmark> observableList = FXCollections.observableList((List<Bookmark>) _bookmarks.clone());

      observableList.removeAll(toRemove);

      listView.setItems(observableList);

      if (0 < observableList.size())
      {
         listView.getSelectionModel().select(0);
      }
   }

   private boolean checkPopupFinish(KeyEvent keyEvent, ListView<Bookmark> listView)
   {
      if (KeyMatchWA.matches(keyEvent, new KeyCodeCombination(KeyCode.ENTER)))
      {
         boolean b = runSelectedListItem(listView);
         keyEvent.consume();
         return b;
      }
      else if (KeyMatchWA.matches(keyEvent, new KeyCodeCombination(KeyCode.ESCAPE)))
      {
         _popup.hide();
         keyEvent.consume();
         return true;
      }

      return false;
   }

   private boolean runSelectedListItem(ListView<Bookmark> listView)
   {
      Bookmark selectedItem = listView.getSelectionModel().getSelectedItem();

      if(null == selectedItem)
      {
         return false;
      }
      _popup.hide();
      runBookmark(selectedItem);
      return true;
   }

   private void hideIfNotFocused(Boolean newValue)
   {
      if(false == newValue)
      {
         _popup.hide();
      }
   }

   public void execAbreviation()
   {
      if(_sqlTextAreaServices.getTokenAtCarret().equalsIgnoreCase("sf"))
      {
         _sqlTextAreaServices.replaceTokenAtCarretBy("SELECT * FROM");
      }
      else if(_sqlTextAreaServices.getTokenAtCarret().equalsIgnoreCase("ob"))
      {
         _sqlTextAreaServices.replaceTokenAtCarretBy("ORDER BY");
      }
      else if(_sqlTextAreaServices.getTokenAtCarret().equalsIgnoreCase("gb"))
      {
         _sqlTextAreaServices.replaceTokenAtCarretBy("GROUP BY");
      }
   }
}
