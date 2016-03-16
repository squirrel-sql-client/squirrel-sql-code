package org.squirrelsql.session.sql.searchchandreplace;

import javafx.scene.layout.BorderPane;
import org.squirrelsql.Props;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.*;
import org.squirrelsql.session.action.StdActionCfg;
import org.squirrelsql.session.sql.SQLTextAreaServices;

public class SearchAndReplaceCtrl
{
   private BorderPane _borderPane;
   private SQLTextAreaServices _sqlTextAreaServices;
   private EditableComboCtrl _editableComboCtrl;

   private MessageHandler _messageHandler = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);
   private I18n _i18n = new I18n(getClass());

   private int _nextStartPos = 0;
   private String _currentFindText;


   public SearchAndReplaceCtrl(BorderPane borderPane, SQLTextAreaServices sqlTextAreaServices)
   {
      _borderPane = borderPane;
      _sqlTextAreaServices = sqlTextAreaServices;
      StdActionCfg.SEARCH_IN_TEXT.setAction(this::onSearch);
   }

   private void onSearch()
   {
      FxmlHelper<SearchAndReplaceView> fxmlHelper = new FxmlHelper<>(SearchAndReplaceView.class);


      fxmlHelper.getView().btnClose.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.CLOSE));

      fxmlHelper.getView().btnFindNext.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.ARROW_DOWN));
      fxmlHelper.getView().btnFindNext.setOnAction(e -> onFind(true));
      fxmlHelper.getView().btnFindPrevious.setOnAction(e -> onFind(false));

      fxmlHelper.getView().btnFindPrevious.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.ARROW_UP));

      _editableComboCtrl = new EditableComboCtrl(fxmlHelper.getView().cboSearchText, getClass().getName(), () -> onFind(true));

      _editableComboCtrl.requestFocus();


      fxmlHelper.getView().btnClose.setOnAction(e -> onCLose());
      _borderPane.setTop(fxmlHelper.getRegion());
   }

   private void onFind(boolean forward)
   {
      _editableComboCtrl.addCurrentTextToHistory();

      String toFind = _editableComboCtrl.getText();

      if (Utils.isEmptyString(toFind))
      {
         return;
      }


      String text = _sqlTextAreaServices.getTextArea().getText();

      int oldStartPos;

      if (forward)
      {
         oldStartPos = 0;
      }
      else
      {
         oldStartPos = text.length();
      }


      if (Utils.compareRespectEmpty(toFind, _currentFindText))
      {
         oldStartPos = _nextStartPos;
      }

      int matchPos = moveCaretToNextMatchingPos(text, toFind, forward);

      if (-1 != matchPos)
      {
         _sqlTextAreaServices.getTextArea().selectRange(matchPos, matchPos + toFind.length());
      }
      else
      {
         _sqlTextAreaServices.getTextArea().deselect();

         if (forward)
         {
            if (0 == oldStartPos)
            {
               _messageHandler.info(_i18n.t("SearchAndReplaceCtrl.noMatchFound"));
            }
            else
            {
               _messageHandler.info(_i18n.t("SearchAndReplaceCtrl.noMoreMatchFound.restartBegin"));
            }
         }
         else
         {
            if (text.length() == oldStartPos)
            {
               _messageHandler.info(_i18n.t("SearchAndReplaceCtrl.noMatchFound"));
            }
            else
            {
               _messageHandler.info(_i18n.t("SearchAndReplaceCtrl.noMoreMatchFound.restartEnd"));
            }
         }
      }
   }


   private int moveCaretToNextMatchingPos(String text, String toFind, boolean forward)
   {
      if (false == Utils.compareRespectEmpty(toFind, _currentFindText))
      {
         _currentFindText = toFind;

         if (forward)
         {
            _nextStartPos = 0;
         }
         else
         {
            _nextStartPos = text.length();
         }
      }

      int pos;

      if (forward)
      {
         pos = text.indexOf(toFind, _nextStartPos);
      }
      else
      {
         pos = text.substring(0, _nextStartPos).lastIndexOf(toFind, _nextStartPos);
      }

      if (-1 == pos)
      {
         if (forward)
         {
            _nextStartPos = 0;
         }
         else
         {
            _nextStartPos = text.length();
         }
      }
      else
      {
         if (forward)
         {
            _nextStartPos = pos + toFind.length();
         }
         else
         {
            _nextStartPos = pos;
         }
      }


      return pos;
   }

   private void onCLose()
   {
      _borderPane.setTop(null);
   }
}

