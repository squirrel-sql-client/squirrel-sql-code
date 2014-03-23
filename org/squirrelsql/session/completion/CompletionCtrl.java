package org.squirrelsql.session.completion;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.sql.SQLTextAreaServices;

public class CompletionCtrl
{
   private final Session _session;
   private final SQLTextAreaServices _sqlTextAreaServices;
   private TableCompletionCandidate _lastSeenTable;

   public CompletionCtrl(Session session, SQLTextAreaServices sqlTextAreaServices)
   {
      _session = session;
      _sqlTextAreaServices = sqlTextAreaServices;
   }

   public void completeCode()
   {
      _completeCode(false, null);

   }

   private void _completeCode(boolean showPopupForSizeOne, Double formerXPos)
   {
      String tokenAtCarret = _sqlTextAreaServices.getTokenAtCarret();


      TokenParser tokenParser = new TokenParser(tokenAtCarret);

      ObservableList<CompletionCandidate> completions = new Completor(_session.getSchemaCache(), _lastSeenTable).getCompletions(tokenParser);

      if(0 == completions.size())
      {
         return;
      }

      if(1 == completions.size() && false == showPopupForSizeOne)
      {
         executeCompletion(completions.get(0));
         return;
      }

      Popup pp = new Popup();

      ListView<CompletionCandidate> listView = new ListView<>();
      listView.setItems(completions);

      listView.getSelectionModel().selectFirst();


      listView.setOnKeyTyped(keyEvent -> onHandleKeyOnPopupList((KeyEvent)keyEvent, pp, listView));

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

      double x;
      if (null == formerXPos)
      {
         x = cl.getX() - _sqlTextAreaServices.getStringWidth(tokenParser.getUncompletedSplit());
      }
      else
      {
         // This formerXPos is a workaround because the completion list jumps horizontally when the user enters chars while the completion list is open.
         x = formerXPos;
      }

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
         onCompletionSelected(keyEvent, pp, listView);
      }
      else if(27 == keyEvent.getCharacter().charAt(0)) // ESCAPE Key
      {
         pp.hide();
         keyEvent.consume();
      }
      else
      {
         Double formerXPos = pp.getX();
         pp.hide();
         Platform.runLater(() -> onCompleteNextChar(formerXPos));
      }
   }

   private void onCompleteNextChar(Double formerXPos)
   {
      if (false == Utils.isEmptyString(_sqlTextAreaServices.getTokenAtCarret()))
      {
         _completeCode(true, formerXPos);
      }
   }

   private void onCompletionSelected(KeyEvent keyEvent, Popup pp, ListView<CompletionCandidate> listView)
   {
      pp.hide();
      CompletionCandidate selItem = listView.getSelectionModel().getSelectedItems().get(0);
      executeCompletion(selItem);
      keyEvent.consume();
   }

   private void executeCompletion(CompletionCandidate completionCandidate)
   {
      if(completionCandidate instanceof TableCompletionCandidate)
      {
         _lastSeenTable = (TableCompletionCandidate)completionCandidate;
      }

      TokenParser tokenParser = new TokenParser(_sqlTextAreaServices.getTokenAtCarret());
      _sqlTextAreaServices.replaceTokenAtCarretBy(tokenParser.getCompletedSplitsStringLength(), completionCandidate.getReplacement());
   }

}
