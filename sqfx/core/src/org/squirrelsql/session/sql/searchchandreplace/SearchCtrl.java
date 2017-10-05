package org.squirrelsql.session.sql.searchchandreplace;

import javafx.scene.layout.BorderPane;
import org.squirrelsql.Props;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.EditableComboCtrl;
import org.squirrelsql.services.EditableComboCtrlEnterListener;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.action.StdActionCfg;
import org.squirrelsql.session.sql.SQLTextAreaServices;

public class SearchCtrl
{
   private BorderPane _borderPane;
   private SQLTextAreaServices _sqlTextAreaServices;
   private EditableComboCtrl _editableComboCtrl;

   private MessageHandler _messageHandler = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);
   private I18n _i18n = new I18n(getClass());

   private int _nextStartPos = 0;
   private String _currentFindText;
   private SearchView _searchView;
   private boolean _foundPositionSelected;
   private boolean _eofReached;


   public SearchCtrl(BorderPane borderPane, SQLTextAreaServices sqlTextAreaServices)
   {
      _borderPane = borderPane;
      _sqlTextAreaServices = sqlTextAreaServices;
      StdActionCfg.SEARCH_IN_TEXT.setAction(this::onOpenSearch);
   }

   private void onOpenSearch()
   {
      FxmlHelper<SearchView> fxmlHelper = new FxmlHelper<>(SearchView.class);

      init(fxmlHelper.getView());

      _borderPane.setTop(fxmlHelper.getRegion());
   }

   private void init(SearchView searchView)
   {
      _searchView = searchView;
      _searchView.btnClose.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.CLOSE));

      _searchView.btnFindNext.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.ARROW_DOWN));
      _searchView.btnFindNext.setOnAction(e -> onFind(true));
      _searchView.btnFindPrevious.setOnAction(e -> onFind(false));

      _searchView.btnFindPrevious.setGraphic(new Props(getClass()).getImageView(GlobalIconNames.ARROW_UP));

      EditableComboCtrlEnterListener listener = new EditableComboCtrlEnterListener()
      {
         @Override
         public void enterPressed()
         {
            onFind(true);
         }

         @Override
         public void escapePressed()
         {
            onCLose();
         }
      };

      _editableComboCtrl = new EditableComboCtrl(_searchView.cboSearchText, getClass().getName(), listener);

      _editableComboCtrl.requestFocus();


      _searchView.btnClose.setOnAction(e -> onCLose());
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

      int matchPos = getNextMatchingPos(text, toFind, forward);

      _foundPositionSelected = false;
      _eofReached = false;


      if (-1 != matchPos)
      {
         _sqlTextAreaServices.getTextArea().selectRange(matchPos, matchPos + toFind.length());
         _foundPositionSelected = true;
      }
      else
      {
         _eofReached = true;

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


   private int getNextMatchingPos(String text, String toFind, boolean forward)
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
      if(false == _searchView.chkMatchCase.isSelected())
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

      if(_searchView.chkWholeWord.isSelected())
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

   public static SearchCtrl create(BorderPane borderPane, SQLTextAreaServices sqlTextAreaServices, SearchView searchView)
   {
      SearchCtrl ret = new SearchCtrl(borderPane, sqlTextAreaServices);
      ret.init(searchView);
      return ret;
   }

   public boolean isFoundPositionSelected()
   {
      return _foundPositionSelected;
   }

   public void findNext()
   {
      onFind(true);
   }

   public boolean isEOFReached()
   {
      return _eofReached;
   }

   public void increaseNextStartPosBy(int incr)
   {
      _nextStartPos += incr;
      _nextStartPos = Math.min(_nextStartPos, _sqlTextAreaServices.getTextArea().getText().length());
   }
}

