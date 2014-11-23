package org.squirrelsql.session.sql.bookmark;

import javafx.application.Platform;
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
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.completion.CompletionUtil;
import org.squirrelsql.session.sql.SQLTextAreaServices;
import org.squirrelsql.workaround.KeyMatchWA;

import java.util.ArrayList;
import java.util.List;

public class FilteredPopupHandler
{
   private I18n _i18n = new I18n(getClass());
   private SQLTextAreaServices _sqlTextAreaServices;
   private ArrayList<Bookmark> _bookmarks;
   private TextField _txtFilter;
   private ListView<Bookmark> _listView;
   private Popup _popup;
   private BorderPane _borderPane;

   public FilteredPopupHandler(SQLTextAreaServices sqlTextAreaServices, List<Bookmark> bookmarks)
   {
      _sqlTextAreaServices = sqlTextAreaServices;
      _bookmarks = new ArrayList<>(bookmarks);

      _popup = new Popup();

      _txtFilter = new TextField();
      _txtFilter.setFont(_sqlTextAreaServices.getFont());

      _listView = new ListView<>();

      _borderPane = new BorderPane();
      _borderPane.setTop(_txtFilter);
      _borderPane.setCenter(_listView);
      _borderPane.setStyle("-fx-border-color: lightblue; -fx-border-width: 2;");

      _popup.getContent().add(_borderPane);

      _popup.focusedProperty().addListener((observable, oldValue, newValue) -> hideIfNotFocused(newValue));

      _listView.setOnMouseClicked(event -> onMouseClickedList(event, _listView, _popup, _sqlTextAreaServices));
      _listView.setFocusTraversable(false);
      _listView.focusedProperty().addListener((observable, oldValue, newValue) -> _txtFilter.requestFocus());

      _txtFilter.setOnKeyPressed(keyEvent -> onHandleKeyOnTxt((KeyEvent) keyEvent));

      _txtFilter.removeEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> onEventFilterHandler(keyEvent));
      _txtFilter.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> onEventFilterHandler(keyEvent));


   }

   void showPopup()
   {
      if (_showPopup(false))
      {
         Point2D cl = _sqlTextAreaServices.getCarretLocationOnScreen();
         _popup.show(_sqlTextAreaServices.getTextArea(), cl.getX(), cl.getY());
         _txtFilter.requestFocus();
      }
   }

   private boolean _showPopup(boolean forceDisplay)
   {


      filterPopupList();
      initDisplaySpaces(_listView.getItems());

      if (false == forceDisplay)
      {
         if(0 == _bookmarks.size())
         {
            new MessageHandler(BookmarkManager.class, MessageHandlerDestination.MESSAGE_PANEL).info(_i18n.t("no.bookmarks.defined"));
            return false;
         }
         else if( 1 == _bookmarks.size())
         {
            runBookmark(_bookmarks.get(0));
            return false;
         }
      }

      CompletionUtil.prepareCompletionList(_listView, _sqlTextAreaServices);

      _borderPane.requestLayout();

      return true;
   }

   private void initDisplaySpaces(List<Bookmark> bookmarks)
   {
      if(0 == bookmarks.size())
      {
         return;
      }

      Bookmark max = bookmarks.stream().max((b1, b2) -> b1.getSelShortcut().length() - b2.getSelShortcut().length()).get();
      bookmarks.forEach(b -> b.setDisplaySpace(max.getSelShortcut().length() - b.getSelShortcut().length() + 3));
   }


   private void onEventFilterHandler(KeyEvent keyEvent)
   {
      if (KeyMatchWA.matches(keyEvent, new KeyCodeCombination(KeyCode.UP)))
      {
         int selIx = _listView.getSelectionModel().getSelectedIndex();
         if(0 < selIx)
         {
            _listView.getSelectionModel().select(selIx - 1);
         }

         keyEvent.consume();
      }
      else if (KeyMatchWA.matches(keyEvent, new KeyCodeCombination(KeyCode.DOWN)))
      {
         int selIx = _listView.getSelectionModel().getSelectedIndex();
         if(_listView.getItems().size()-1 > selIx)
         {
            _listView.getSelectionModel().select(selIx + 1);
         }

         keyEvent.consume();
      }
   }

   private  void onMouseClickedList(MouseEvent event, ListView<Bookmark> listView, Popup popup, SQLTextAreaServices sqlTextAreaServices)
   {
      if(Utils.isDoubleClick(event))
      {
         runSelectedListItem(listView, popup, sqlTextAreaServices);
      }
   }

   private void runBookmark(Bookmark bookmark)
   {
      _sqlTextAreaServices.insertAtCarret("\n" + bookmark.getSql());
   }

   private void onHandleKeyOnTxt(KeyEvent keyEvent)
   {
      if(false == checkPopupFinish(keyEvent))
      {
         Platform.runLater(() -> _showPopup(true));
      }

   }

   private boolean checkPopupFinish(KeyEvent keyEvent)
   {
      if (KeyMatchWA.matches(keyEvent, new KeyCodeCombination(KeyCode.ENTER)))
      {
         boolean b = runSelectedListItem(_listView, _popup, _sqlTextAreaServices);
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

   private  boolean runSelectedListItem(ListView<Bookmark> listView, Popup popup, SQLTextAreaServices sqlTextAreaServices)
   {
      Bookmark selectedItem = listView.getSelectionModel().getSelectedItem();

      if(null == selectedItem)
      {
         return false;
      }
      popup.hide();
      runBookmark(selectedItem);
      return true;
   }

   private void hideIfNotFocused(Boolean focused)
   {
      if(false == focused)
      {
         _popup.hide();
      }
   }

   private void filterPopupList()
   {
      ArrayList<Bookmark> toRemove = new ArrayList<>();

      for (Bookmark bookmark : _bookmarks)
      {
         if(false == bookmark.getSelShortcut().toLowerCase().startsWith(_txtFilter.getText().toLowerCase()))
         {
            toRemove.add(bookmark);
         }
      }

      ObservableList<Bookmark> observableList = FXCollections.observableList((List<Bookmark>) _bookmarks.clone());

      observableList.removeAll(toRemove);

      _listView.setItems(observableList);

      if (0 < observableList.size())
      {
         _listView.getSelectionModel().select(0);
      }
   }
}
