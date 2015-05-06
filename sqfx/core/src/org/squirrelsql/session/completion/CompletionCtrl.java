package org.squirrelsql.session.completion;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.action.StdActionCfg;
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

      StdActionCfg.SQL_CODE_COMPLETION.setAction(this::completeCode);
   }

   private void completeCode()
   {
      _completeCode(false);

   }

   private void _completeCode(boolean showPopupForSizeOne)
   {
      String tokenAtCarret = _sqlTextAreaServices.getTokenTillCarret();


      TokenParser tokenParser = new TokenParser(tokenAtCarret);

      ObservableList<CompletionCandidate> completions = new Completor(_session.getSchemaCacheValue(), _currentTableInfosNextToCursor, _currentAliasInfos).getCompletions(tokenParser);

      if(0 == completions.size())
      {
         return;
      }

      if(1 == completions.size() && false == showPopupForSizeOne)
      {
         executeCompletion(completions.get(0), false);
         return;
      }

      ListView<CompletionCandidate> listView = new ListView<>();
      listView.setItems(completions);

      listView.getSelectionModel().selectFirst();


      listView.setOnKeyTyped(keyEvent -> onHandleKeyOnPopupList((KeyEvent)keyEvent, listView));

      listView.setOnMouseClicked(event -> onMouseClickedList(event, listView));

      _sqlTextAreaServices.getCaretPopup().getPopup().focusedProperty().addListener((observable, oldValue, newValue) -> hideIfNotFocused(newValue));

      CompletionUtil.prepareCompletionList(listView, _sqlTextAreaServices);

      _sqlTextAreaServices.getCaretPopup().setContent(listView);
      positionAndShowPopup(tokenParser);


   }

   private void positionAndShowPopup(TokenParser tokenParser)
   {
      String uncompletedSplit = tokenParser.getUncompletedSplit();

      _sqlTextAreaServices.getCaretPopup().showAtCaretBottom(-_sqlTextAreaServices.getStringWidth(uncompletedSplit));
   }

   private void hideIfNotFocused(Boolean newValue)
   {
      if(false == newValue)
      {
         closePopup();
      }
   }

   private void closePopup()
   {
      _sqlTextAreaServices.getCaretPopup().hideAndClearContent();
   }

   private void onHandleKeyOnPopupList(KeyEvent keyEvent, ListView<CompletionCandidate> listView)
   {
      if (KeyMatchWA.matches(keyEvent, new KeyCodeCombination(KeyCode.ENTER)))
      {
         onCompletionSelected(keyEvent, listView, false);
      }
      else if (KeyMatchWA.matches(keyEvent, new KeyCodeCombination(KeyCode.TAB)))
      {
         onCompletionSelected(keyEvent, listView, true);
      }
      else if(KeyMatchWA.matches(keyEvent, new KeyCodeCombination(KeyCode.ESCAPE)))
      {
         closePopup();
         keyEvent.consume();
      }
      else
      {
         closePopup();
         Platform.runLater(() -> onCompleteNextChar());
      }
   }

   private void onCompleteNextChar()
   {
      if (false == Utils.isEmptyString(_sqlTextAreaServices.getTokenTillCarret()))
      {
         _completeCode(true);
      }
   }

   private void onCompletionSelected(KeyEvent keyEvent, ListView<CompletionCandidate> listView, boolean removeSucceedingChars)
   {
      finishCompletion(listView, removeSucceedingChars);
      keyEvent.consume();
   }

   private void finishCompletion(ListView<CompletionCandidate> listView, boolean removeSucceedingChars)
   {
      closePopup();
      CompletionCandidate selItem = listView.getSelectionModel().getSelectedItems().get(0);
      executeCompletion(selItem, removeSucceedingChars);
   }

   private void onMouseClickedList(MouseEvent event, ListView<CompletionCandidate> listView)
   {
      if(Utils.isDoubleClick(event))
      {
         finishCompletion(listView, false);
      }
   }


   private void executeCompletion(CompletionCandidate completionCandidate, boolean removeSucceedingChars)
   {
      TokenParser tokenParser = new TokenParser(_sqlTextAreaServices.getTokenTillCarret());
      _sqlTextAreaServices.replaceTokenAtCarretBy(tokenParser.getCompletedSplitsStringLength(), removeSucceedingChars, completionCandidate.getReplacement());
   }

}
