package org.squirrelsql.session.sql.bookmark;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import org.squirrelsql.session.completion.CompletionUtil;
import org.squirrelsql.session.sql.SQLTextAreaServices;
import org.squirrelsql.workaround.KeyMatchWA;

import java.util.ArrayList;

public class BookmarkManager
{
   private SQLTextAreaServices _sqlTextAreaServices;


   private ArrayList<Bookmark> _bookmarks = new ArrayList<>();

   public BookmarkManager(SQLTextAreaServices sqlTextAreaServices)
   {
      _sqlTextAreaServices = sqlTextAreaServices;

      _bookmarks.add(new Bookmark("SELECT * FROM articles", "art", "articles"));
      _bookmarks.add(new Bookmark("SELECT * FROM receipts", "recs", "receipts"));
      _bookmarks.add(new Bookmark("SELECT * FROM receipts\nINNER JOIN receipt_lines ON receipts.id = receipt_lines.receipt_id", "reclines", "receipt_lines"));

      initDisplaySpaces();

   }

   private void initDisplaySpaces()
   {
      Bookmark max = _bookmarks.stream().max((b1, b2) -> b1.getSelShortcut().length() - b2.getSelShortcut().length()).get();
      _bookmarks.forEach(b -> b.setDisplaySpace(max.getSelShortcut().length() - b.getSelShortcut().length() + 3));
   }

   public void execBookmark()
   {
      Popup pp = new Popup();

      BorderPane bp = new BorderPane();

      TextField txt = new TextField();
      txt.setFont(_sqlTextAreaServices.getFont());


      bp.setTop(txt);

      ListView<Bookmark> listView = new ListView<>();

      initDisplaySpaces();

      listView.setItems(FXCollections.observableList(_bookmarks));

      CompletionUtil.prepareCompletionList(listView, _sqlTextAreaServices);

      bp.setCenter(listView);

      bp.setStyle("-fx-border-color: lightblue; -fx-border-width: 2;");

      pp.getContent().add(bp);
      Point2D cl = _sqlTextAreaServices.getCarretLocationOnScreen();

      pp.focusedProperty().addListener((observable, oldValue, newValue) -> hideIfNotFocused(newValue, pp));


      listView.setOnKeyTyped(keyEvent -> onHandleKeyOnPopup((KeyEvent) keyEvent, pp, listView));



      pp.show(_sqlTextAreaServices.getTextArea(), cl.getX(), cl.getY());
   }

   private void onHandleKeyOnPopup(KeyEvent keyEvent, Popup pp, ListView<Bookmark> listView)
   {
      if (KeyMatchWA.matches(keyEvent, new KeyCodeCombination(KeyCode.ENTER)))
      {
         System.out.println("####### bookmark selected");
      }
      else if(KeyMatchWA.matches(keyEvent, new KeyCodeCombination(KeyCode.ESCAPE)))
      {
         pp.hide();
         keyEvent.consume();
      }
      else
      {

      }

   }

   private void hideIfNotFocused(Boolean newValue, Popup pp)
   {
      if(false == newValue)
      {
         pp.hide();
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
