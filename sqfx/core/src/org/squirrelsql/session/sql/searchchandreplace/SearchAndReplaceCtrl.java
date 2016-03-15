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

   private int _nextStartPos = 0;
   private String _currentFindText;
   private MessageHandler _messageHandler = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);
   private I18n _i18n = new I18n(getClass());

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
      fxmlHelper.getView().btnFindNext.setOnAction(e -> onFindNext());

      fxmlHelper.getView().btnFindPrevious.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.ARROW_UP));

      _editableComboCtrl = new EditableComboCtrl(fxmlHelper.getView().cboSearchText, getClass().getName(), () -> onFindNext());

      _editableComboCtrl.requestFocus();


      fxmlHelper.getView().btnClose.setOnAction(e -> onCLose());
      _borderPane.setTop(fxmlHelper.getRegion());
   }

   private void onFindNext()
   {
      _editableComboCtrl.addCurrentTextToHistory();

      String toFind = _editableComboCtrl.getText();

      if(Utils.isEmptyString(toFind))
      {
         return;
      }


      String text = _sqlTextAreaServices.getTextArea().getText();

      int oldStartPos = 0;

      if (Utils.compareRespectEmpty(toFind, _currentFindText))
      {
         oldStartPos = _nextStartPos;
      }

      int matchPos = moveCarretToNextMatchingPos(text, toFind);

      if(-1 != matchPos)
      {
         _sqlTextAreaServices.getTextArea().selectRange(matchPos, matchPos + toFind.length());
      }
      else
      {
         _sqlTextAreaServices.getTextArea().deselect();

         if(0 == oldStartPos)
         {
            _messageHandler.info(_i18n.t("SearchAndReplaceCtrl.noMatchFound"));
         }
         else
         {
            _messageHandler.info(_i18n.t("SearchAndReplaceCtrl.noMoreMatchFound"));
         }

      }
   }

   private int moveCarretToNextMatchingPos(String text, String toFind)
   {
      if(false == Utils.compareRespectEmpty(toFind, _currentFindText))
      {
         _currentFindText = toFind;
         _nextStartPos = 0;
      }

      int pos = text.indexOf(toFind, _nextStartPos);

      if(-1 == pos)
      {
         _nextStartPos = 0;
      }
      else
      {
         _nextStartPos = pos + toFind.length();
      }


      return pos;
   }

   private void onCLose()
   {
      _borderPane.setTop(null);
   }
}

