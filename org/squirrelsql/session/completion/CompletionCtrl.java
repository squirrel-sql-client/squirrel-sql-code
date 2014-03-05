package org.squirrelsql.session.completion;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.SQLTextAreaServices;
import org.squirrelsql.session.Session;

public class CompletionCtrl
{
   private final Session _session;
   private final SQLTextAreaServices _sqlTextAreaServices;

   public CompletionCtrl(Session session, SQLTextAreaServices sqlTextAreaServices)
   {
      _session = session;
      _sqlTextAreaServices = sqlTextAreaServices;
   }

   public void completeCode()
   {
      _completeCode(false);

   }

   private void _completeCode(boolean showPopupForSizeOne)
   {
      String tokenAtCarret = _sqlTextAreaServices.getTokenAtCarret();

      System.out.println("### Completing for token >" + tokenAtCarret + "<");


      ObservableList<CompletionCandidate> completions = new Completor(_session.getSchemaCache()).getCompletions(tokenAtCarret);

      if(0 == completions.size())
      {
         return;
      }

      if(1 == completions.size() && false == showPopupForSizeOne)
      {
         _sqlTextAreaServices.replaceTokenAtCarretBy(completions.get(0).getReplacement());
         return;
      }

      Popup pp = new Popup();

      ListView<CompletionCandidate> listView = new ListView<>();
      listView.setItems(completions);

      listView.getSelectionModel().selectFirst();


      listView.setOnKeyTyped(keyEvent -> onHandleKeyOnPopupList(keyEvent, pp, listView));

      pp.focusedProperty().addListener((observable, oldValue, newValue) -> hideIfNotFocused(newValue, pp));

      listView.setPrefHeight(Math.min(listView.getItems().size(), 15) * 24 + 3);

//      Font font = listView.cellFactoryProperty().get().call(listView).getFont();
      FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(new Label().getFont());

      double maxItemWidth = 0;
      for (CompletionCandidate completionCandidate : listView.getItems())
      {
         maxItemWidth = Math.max(fontMetrics.computeStringWidth(completionCandidate.toString()), maxItemWidth);
      }
      listView.setPrefWidth(maxItemWidth + 35);

      pp.getContent().add(listView);
      Point2D cl = _sqlTextAreaServices.getCarretLocationOnScreen();

      double x = cl.getX() - _sqlTextAreaServices.getStringWidth(tokenAtCarret);

      pp.show(_sqlTextAreaServices.getTextArea(), x, cl.getY() + _sqlTextAreaServices.getFontHight() + 4);
   }

   private void hideIfNotFocused(Boolean newValue, Popup pp)
   {
      if(false == newValue)
      {
         pp.hide();
      }
   }

   private void onHandleKeyOnPopupList(KeyEvent keyEvent, Popup pp, ListView<CompletionCandidate> listView)
   {
      // if (keyEvent.getCode() == KeyCode.ENTER) doesn't work
      if (("\r".equals(keyEvent.getCharacter()) || "\n".equals(keyEvent.getCharacter())))
      {
         pp.hide();
         CompletionCandidate selItem = listView.getSelectionModel().getSelectedItems().get(0);
         _sqlTextAreaServices.replaceTokenAtCarretBy(selItem.getReplacement());
         keyEvent.consume();
      }
      else if(27 == keyEvent.getCharacter().charAt(0)) // ESCAPE Key
      {
         pp.hide();
         keyEvent.consume();
      }
      else
      {
         pp.hide();
         if (false == Utils.isEmptyString(_sqlTextAreaServices.getTokenAtCarret()))
         {
            Platform.runLater(() -> _completeCode(true));
         }
      }
   }
}
