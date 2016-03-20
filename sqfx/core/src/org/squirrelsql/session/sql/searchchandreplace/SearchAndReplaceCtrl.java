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
   private SearchAndReplaceView _searchAndReplaceView;


   public SearchAndReplaceCtrl(BorderPane borderPane, SQLTextAreaServices sqlTextAreaServices)
   {
      _borderPane = borderPane;
      _sqlTextAreaServices = sqlTextAreaServices;
      StdActionCfg.SEARCH_IN_TEXT.setAction(this::onSearch);
   }

   private void onSearch()
   {
      FxmlHelper<SearchAndReplaceView> fxmlHelper = new FxmlHelper<>(SearchAndReplaceView.class);


      _searchAndReplaceView = fxmlHelper.getView();
      _searchAndReplaceView.btnClose.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.CLOSE));

      _searchAndReplaceView.btnFindNext.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.ARROW_DOWN));
      _searchAndReplaceView.btnFindNext.setOnAction(e -> onFind(true));
      _searchAndReplaceView.btnFindPrevious.setOnAction(e -> onFind(false));

      _searchAndReplaceView.btnFindPrevious.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.ARROW_UP));

      _editableComboCtrl = new EditableComboCtrl(_searchAndReplaceView.cboSearchText, getClass().getName(), () -> onFind(true));

      _editableComboCtrl.requestFocus();


      _searchAndReplaceView.btnClose.setOnAction(e -> onCLose());
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

      pos = getNextPos(text, toFind, forward, _nextStartPos);

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

   private int getNextPos(String textToFindIn, String toFind, boolean forward, int startPos)
   {
      if(false == _searchAndReplaceView.chkMatchCase.isSelected())
      {
         textToFindIn = textToFindIn.toLowerCase();
         toFind = toFind.toLowerCase();
      }

      int pos;
      if (forward)
      {
         pos = textToFindIn.indexOf(toFind, startPos);
      }
      else
      {
         pos = textToFindIn.substring(0, startPos).lastIndexOf(toFind, startPos);
      }

      if(_searchAndReplaceView.chkWholeWord.isSelected())
      {
         if(pos < 0)
         {
            return pos;
         }

         if(isSurroundedByWhiteSpace(textToFindIn, toFind, pos))
         {
            return pos;
         }
         else
         {
            return -1;
         }
      }
      else
      {
         return pos;
      }
   }

   private boolean isSurroundedByWhiteSpace(String textToFindIn, String toFind, int startPos)
   {
      return isBorderOrWhiteSpace(textToFindIn, startPos - 1) && isBorderOrWhiteSpace(textToFindIn, startPos + toFind.length());
   }

   private boolean isBorderOrWhiteSpace(String text, int pos)
   {
      if(pos < 0 || text.length() <= pos)
      {
         return true;
      }

      return Character.isWhitespace(text.charAt(pos));
   }

   private void onCLose()
   {
      _borderPane.setTop(null);
   }
}

