package org.squirrelsql.session.completion;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.parser.kernel.TableAliasInfo;
import org.squirrelsql.session.sql.SQLTextAreaServices;
import org.squirrelsql.session.sql.syntax.LexAndParseResultListener;
import org.squirrelsql.workaround.KeyMatchWA;

import java.util.ArrayList;
import java.util.List;

public class CompletionCtrl
{
   private final Session _session;
   private final SQLTextAreaServices _sqlTextAreaServices;
   private List<TableInfo> _currentTableInfosNextToCursor = new ArrayList<>();
   private TableAliasInfo[] _currentAliasInfos = new TableAliasInfo[0];

   public CompletionCtrl(Session session, SQLTextAreaServices sqlTextAreaServices)
   {
      _session = session;
      _sqlTextAreaServices = sqlTextAreaServices;

      _sqlTextAreaServices.setLexAndParseResultListener(new LexAndParseResultListener()
      {
         @Override
         public void currentTableInfosNextToCursor(List<TableInfo> tableInfos)
         {
            _currentTableInfosNextToCursor = tableInfos;
         }

         @Override
         public void aliasesFound(TableAliasInfo[] aliasInfos)
         {
            _currentAliasInfos = aliasInfos;
         }
      });
   }

   public void completeCode()
   {
      _completeCode(false, null);

   }

   private void _completeCode(boolean showPopupForSizeOne, Double formerXPos)
   {
      String tokenAtCarret = _sqlTextAreaServices.getTokenAtCarret();


      TokenParser tokenParser = new TokenParser(tokenAtCarret);

      ObservableList<CompletionCandidate> completions = new Completor(_session.getSchemaCache(), _currentTableInfosNextToCursor, _currentAliasInfos).getCompletions(tokenParser);

      if(0 == completions.size())
      {
         return;
      }

      if(1 == completions.size() && false == showPopupForSizeOne)
      {
         executeCompletion(completions.get(0), false);
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

//      int pos = _sqlTextAreaServices.getTextArea().getCaretPosition();
//      int col = _sqlTextAreaServices.getTextArea().getCaretColumn();
//
//      System.out.println("col = " + col + ", pos = " + pos);
//
//      _sqlTextAreaServices.getTextArea().setPopupAtCaret(pp);
//      pp.show(AppState.get().getPrimaryStage());
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
      if (KeyMatchWA.matches(keyEvent, new KeyCodeCombination(KeyCode.ENTER)))
      {
         onCompletionSelected(keyEvent, pp, listView, false);
      }
      else if (KeyMatchWA.matches(keyEvent, new KeyCodeCombination(KeyCode.TAB)))
      {
         onCompletionSelected(keyEvent, pp, listView, true);
      }
      else if(KeyMatchWA.matches(keyEvent, new KeyCodeCombination(KeyCode.ESCAPE)))
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

   private void onCompletionSelected(KeyEvent keyEvent, Popup pp, ListView<CompletionCandidate> listView, boolean removeSucceedingChars)
   {
      pp.hide();
      CompletionCandidate selItem = listView.getSelectionModel().getSelectedItems().get(0);
      executeCompletion(selItem, removeSucceedingChars);
      keyEvent.consume();
   }

   private void executeCompletion(CompletionCandidate completionCandidate, boolean removeSucceedingChars)
   {
      TokenParser tokenParser = new TokenParser(_sqlTextAreaServices.getTokenAtCarret());
      _sqlTextAreaServices.replaceTokenAtCarretBy(tokenParser.getCompletedSplitsStringLength(), removeSucceedingChars, completionCandidate.getReplacement());
   }

}
