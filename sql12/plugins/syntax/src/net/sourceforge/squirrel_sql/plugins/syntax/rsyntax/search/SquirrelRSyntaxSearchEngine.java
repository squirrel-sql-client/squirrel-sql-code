package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.search;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.SquirrelRSyntaxTextArea;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.fife.ui.rtextarea.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;

public class SquirrelRSyntaxSearchEngine
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SquirrelRSyntaxSearchEngine.class);


   private ISession _session;
   private SquirrelRSyntaxTextArea _squirrelRSyntaxTextArea;
   private ISquirrelSearchDialog _dialog;
   private boolean _foundAtLeastOne;
   private String _lastSearchString;
   private CaretState _storedCaretState;
   private SearchDialogState _storedSearchDialogState;

   public SquirrelRSyntaxSearchEngine(ISession session, SquirrelRSyntaxTextArea squirrelRSyntaxTextArea)
   {
      _session = session;
      _squirrelRSyntaxTextArea = squirrelRSyntaxTextArea;
   }

   public void find(ActionEvent squirrelEvt)
   {
      _session.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("SquirrelRSyntaxSearchEngine.findUsageHint"));
      search(squirrelEvt, false);
   }

   public void replace(ActionEvent squirrelEvt)
   {
      search(squirrelEvt, true);
   }



   public void search(ActionEvent squirrelEvt, boolean replace)
   {
      if(null != _dialog)
      {
         _dialog.requestFocus();
         return;
      }


      Frame owningFrame = GUIUtils.getOwningFrame(_squirrelRSyntaxTextArea);

      if (replace)
      {
         _dialog = new SquirrelReplaceDialog(owningFrame, _squirrelRSyntaxTextArea);
      }
      else
      {
         _dialog = new SquirrelFindDialog(owningFrame, _squirrelRSyntaxTextArea);
      }

      _dialog.addFindActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onFind();
         }
      });

      _dialog.addReplaceActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onReplace();
         }
      });

      _dialog.addReplaceAllActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onReplaceAll();
         }
      });


      Vector<String> comboBoxStrings = new Vector<String>();

      if(null != _lastSearchString)
      {
         comboBoxStrings.add(_lastSearchString);
      }

//      SearchDialogSearchContext sdsc = new SearchDialogSearchContext();
//      (_dialog).setSearchContext(sdsc);
//
//      (_dialog).setSearchParameters(comboBoxStrings, false, false, false, false, false);


      String selectedText = _squirrelRSyntaxTextArea.getSelectedText();

      if (null != selectedText)
      {
         _dialog.setSearchString(selectedText);
      }

      _dialog.setVisible(true);

      _dialog.addClosingListener(new SearchDialogClosingListener(){
         @Override
         public void searchDialogClosing()
         {
            onDialogClosing();
         }
      });

      storeCaretState();
      _foundAtLeastOne = false;
   }

   private void onReplaceAll()
   {
      _lastSearchString = _dialog.getSearchString();

      SearchContext sc = new SearchContext(_lastSearchString);
      sc.setWholeWord(_dialog.isWholeWord());
      sc.setMatchCase(_dialog.isMatchCase());
      sc.setRegularExpression(_dialog.isRegExp());
      sc.setReplaceWith(_dialog.getReplaceString());

      SearchResult searchResult = SearchEngine.replaceAll(_squirrelRSyntaxTextArea, sc);
      int count = searchResult.getCount();

//      int count = SearchEngine.replaceAll(
//            _squirrelRSyntaxTextArea,
//            _lastSearchString,
//            _dialog.getReplaceString(),
//            _dialog.isMatchCase(),
//            _dialog.isWholeWord(),
//            _dialog.isRegExp());


      if (_dialog.isMarkAll() && null != _dialog.getReplaceString() && 0 < _dialog.getReplaceString().length())
      {
         invokeMarkAllForReplaceLater();
      }

      _session.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("syntax.SquirrelRSyntaxSearchEngine.countOccurencesReplaced", count));
   }

   private void onReplace()
   {
      try
      {

         _lastSearchString = _dialog.getSearchString();

         SearchContext sc = new SearchContext(_lastSearchString);
         sc.setWholeWord(_dialog.isWholeWord());
         sc.setMatchCase(_dialog.isMatchCase());
         sc.setRegularExpression(_dialog.isRegExp());
         sc.setReplaceWith(_dialog.getReplaceString());

         boolean found = SearchEngine.replace(_squirrelRSyntaxTextArea, sc).wasFound();


//         boolean found = SearchEngine.replace(
//            _squirrelRSyntaxTextArea,
//            _lastSearchString,
//            _dialog.getReplaceString(),
//            !_dialog.isSearchUp(),
//            _dialog.isMatchCase(),
//            _dialog.isWholeWord(),
//            _dialog.isRegExp());

         if (_dialog.isMarkAll() && null != _dialog.getReplaceString() && 0 < _dialog.getReplaceString().length())
         {
            invokeMarkAllForReplaceLater();
         }


         if(false == found)
         {
            String msg = s_stringMgr.getString("syntax.SquirrelRSyntaxSearchEngine.noMatchRestart");
            if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(_dialog.getDialog(), msg))
            {
               if (_dialog.isSearchUp())
               {
                  _squirrelRSyntaxTextArea.setCaretPosition(_squirrelRSyntaxTextArea.getText().length());
               }
               else
               {
                  _squirrelRSyntaxTextArea.setCaretPosition(0);
               }


               found = SearchEngine.replace(_squirrelRSyntaxTextArea, sc).wasFound();


//               found = SearchEngine.replace(
//                  _squirrelRSyntaxTextArea,
//                  _lastSearchString,
//                  _dialog.getReplaceString(),
//                  !_dialog.isSearchUp(),
//                  _dialog.isMatchCase(),
//                  _dialog.isWholeWord(),
//                  _dialog.isRegExp());

               if (_dialog.isMarkAll() && null != _dialog.getReplaceString() && 0 < _dialog.getReplaceString().length())
               {
                  invokeMarkAllForReplaceLater();
               }

               if(false == found)
               {
                  JOptionPane.showMessageDialog(_dialog.getDialog(), s_stringMgr.getString("syntax.SquirrelRSyntaxSearchEngine.noMatch"));
               }
               else
               {
                  _foundAtLeastOne = true;
               }

            }
         }
         else
         {
            _foundAtLeastOne = true;
         }

      }
      catch (PatternSyntaxException pse)
      {
//         // There was a problem with the user's regex search string.
         String msg = s_stringMgr.getString("syntax.SquirrelRSyntaxSearchEngine.RegExErrMsg");
         String title = s_stringMgr.getString("syntax.SquirrelRSyntaxSearchEngine.RegExErrTitle", pse.toString());
         JOptionPane.showMessageDialog(_dialog.getDialog(), msg, title, JOptionPane.ERROR_MESSAGE);
      }
   }

   private void invokeMarkAllForReplaceLater()
   {
      //_squirrelRSyntaxTextArea.clearMarkAllHighlights();
      _squirrelRSyntaxTextArea.getHighlighter().removeAllHighlights();



      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            SearchContext sc = new SearchContext(_lastSearchString);
            sc.setWholeWord(_dialog.isWholeWord());
            sc.setMatchCase(_dialog.isMatchCase());
            sc.setRegularExpression(_dialog.isRegExp());
            sc.setReplaceWith(_dialog.getReplaceString());

//            _squirrelRSyntaxTextArea.markAll(
            SearchEngine.markAll(_squirrelRSyntaxTextArea, sc);
         }
      });
   }


   private void storeCaretState()
   {
      _storedCaretState = new CaretState(_squirrelRSyntaxTextArea);
   }

   private void onDialogClosing()
   {
      _storedSearchDialogState = new SearchDialogState(_dialog); 

      if(false == _foundAtLeastOne)
      {
         _storedCaretState.restoreCaretState(_squirrelRSyntaxTextArea);
      }
      _dialog = null;

   }

   private void onFind()
   {
      try
      {

         _lastSearchString = _dialog.getSearchString();
         if (_dialog.isMarkAll())
         {

            SearchContext sc = new SearchContext(_lastSearchString);
            sc.setWholeWord(_dialog.isWholeWord());
            sc.setMatchCase(_dialog.isMatchCase());
            sc.setRegularExpression(_dialog.isRegExp());
            sc.setReplaceWith(_dialog.getReplaceString());

//            _squirrelRSyntaxTextArea.markAll(
            SearchEngine.markAll(_squirrelRSyntaxTextArea, sc);

         }

         SearchContext sc = new SearchContext(_lastSearchString);
         sc.setWholeWord(_dialog.isWholeWord());
         sc.setMatchCase(_dialog.isMatchCase());
         sc.setRegularExpression(_dialog.isRegExp());

         boolean found = SearchEngine.find(_squirrelRSyntaxTextArea, sc).wasFound();


//         boolean found = SearchEngine.find(
//            _squirrelRSyntaxTextArea,
//            _lastSearchString,
//            !_dialog.isSearchUp(),
//            _dialog.isMatchCase(),
//            _dialog.isWholeWord(),
//            _dialog.isRegExp());

         if(false == found)
         {
            String msg = s_stringMgr.getString("syntax.SquirrelRSyntaxSearchEngine.noMatchRestart");
            if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(_dialog.getDialog(), msg))
            {
               if (_dialog.isSearchUp())
               {
                  _squirrelRSyntaxTextArea.setCaretPosition(_squirrelRSyntaxTextArea.getText().length());
               }
               else
               {
                  _squirrelRSyntaxTextArea.setCaretPosition(0);
               }

               found = SearchEngine.find(_squirrelRSyntaxTextArea, sc).wasFound();


//               found = SearchEngine.find(
//                  _squirrelRSyntaxTextArea,
//                  _lastSearchString,
//                  !_dialog.isSearchUp(),
//                  _dialog.isMatchCase(),
//                  _dialog.isWholeWord(),
//                  _dialog.isRegExp());

               if(false == found)
               {
                  JOptionPane.showMessageDialog(_dialog.getDialog(), s_stringMgr.getString("syntax.SquirrelRSyntaxSearchEngine.noMatch"));
               }
               else
               {
                  _foundAtLeastOne = true;
               }

            }
         }
         else
         {
            _foundAtLeastOne = true;
         }

      }
      catch (PatternSyntaxException pse)
      {
//         // There was a problem with the user's regex search string.
         String msg = s_stringMgr.getString("syntax.SquirrelRSyntaxSearchEngine.RegExErrMsg");
         String title = s_stringMgr.getString("syntax.SquirrelRSyntaxSearchEngine.RegExErrTitle", pse.toString());
         JOptionPane.showMessageDialog(_dialog.getDialog(), msg, title, JOptionPane.ERROR_MESSAGE);
      }
   }

   public void findSelected(ActionEvent evt)
   {
      String selectedText = _squirrelRSyntaxTextArea.getSelectedText();

      if(null == selectedText || 0 == selectedText.length())
      {
         return;
      }

      _lastSearchString = selectedText;

      _storedSearchDialogState = SearchDialogState.createForLastFind();
      _repeatLastFind(_storedSearchDialogState);
   }

   private void _repeatLastFind(SearchDialogState searchDialogState)
   {

      SearchContext sc = new SearchContext(_lastSearchString);
      sc.setSearchForward(!searchDialogState.isSearchUp());
      sc.setMatchCase(searchDialogState.isMatchCase());
      sc.setWholeWord(searchDialogState.isWholeWord());
      sc.setRegularExpression(searchDialogState.isRegExp());

      boolean found = SearchEngine.find(_squirrelRSyntaxTextArea, sc).wasFound();


//      boolean found =
//         SearchEngine.find(
//            _squirrelRSyntaxTextArea,
//            _lastSearchString,
//            !searchDialogState.isSearchUp(),
//            searchDialogState.isMatchCase(),
//            searchDialogState.isWholeWord(),
//            searchDialogState.isRegExp());

      if(false == found)
      {
         storeCaretState();
         if (searchDialogState.isSearchUp())
         {
            _squirrelRSyntaxTextArea.setCaretPosition(_squirrelRSyntaxTextArea.getText().length());
         }
         else
         {
            _squirrelRSyntaxTextArea.setCaretPosition(0);
         }

         found = SearchEngine.find(_squirrelRSyntaxTextArea, sc).wasFound();

         if(false == found)
         {
            _storedCaretState.restoreCaretState(_squirrelRSyntaxTextArea);
         }
      }
   }

   public void repeatLastFind(ActionEvent evt)
   {
      if(null == _lastSearchString || 0 == _lastSearchString.length() || null == _storedSearchDialogState)
      {
         return;
      }

      _repeatLastFind(_storedSearchDialogState);
   }

   public void markSelected(ActionEvent evt)
   {
      String selectedText = _squirrelRSyntaxTextArea.getSelectedText();

      if(null == selectedText || 0 == selectedText.length())
      {
         return;
      }

      _lastSearchString = selectedText;
      _storedSearchDialogState = SearchDialogState.createForLastFind();


      SearchContext sc = new SearchContext(_lastSearchString);
      sc.setWholeWord(_storedSearchDialogState.isWholeWord());
      sc.setMatchCase(_storedSearchDialogState.isMatchCase());
      sc.setRegularExpression(_storedSearchDialogState.isRegExp());

//            _squirrelRSyntaxTextArea.markAll(
      SearchEngine.markAll(_squirrelRSyntaxTextArea, sc);

   }

   public void unmarkAll()
   {
      try
      {
         Method method = RTextArea.class.getDeclaredMethod("clearMarkAllHighlights");
         method.setAccessible(true);
         method.invoke(_squirrelRSyntaxTextArea);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void goToLine()
   {
      SquirrelGoToDialog squirrelGoToDialog = new SquirrelGoToDialog(_session.getApplication().getMainFrame());

      squirrelGoToDialog.setMaxLineNumberAllowed(_squirrelRSyntaxTextArea.getLineCount());
      squirrelGoToDialog.setVisible(true);


      int lineNumber = squirrelGoToDialog.getLineNumber();
      if (lineNumber > 0)
      {

         try
         {
            _squirrelRSyntaxTextArea.setCaretPosition(_squirrelRSyntaxTextArea.getLineStartOffset(lineNumber - 1));
         }
         catch (BadLocationException ble)
         {
            JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), s_stringMgr.getString("syntax.SquirrelRSyntaxSearchEngine.InvalidLineNumber"));
         }
      }
   }
}
