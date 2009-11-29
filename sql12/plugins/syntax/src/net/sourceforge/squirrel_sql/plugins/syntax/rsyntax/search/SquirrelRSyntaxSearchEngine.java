package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.search;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.SquirrelRSyntaxTextArea;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.fife.ui.rtextarea.SearchEngine;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.event.*;
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

      if (replace)
      {
         _dialog = new SquirrelReplaceDialog(_session.getApplication().getMainFrame());
      }
      else
      {
         _dialog = new SquirrelFindDialog(_session.getApplication().getMainFrame());
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

      (_dialog).setSearchParameters(comboBoxStrings, false, false, false, false, false);

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

      int count = SearchEngine.replaceAll(_squirrelRSyntaxTextArea, _lastSearchString,
                  _dialog.getReplaceString(),
                  _dialog.isMatchCase(), _dialog.isWholeWord(), _dialog.isRegExp());


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

         boolean found = SearchEngine.replace(
            _squirrelRSyntaxTextArea,
            _lastSearchString,
            _dialog.getReplaceString(),
            !_dialog.isSearchUp(),
            _dialog.isMatchCase(),
            _dialog.isWholeWord(),
            _dialog.isRegExp());

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

               found = SearchEngine.replace(
                  _squirrelRSyntaxTextArea,
                  _lastSearchString,
                  _dialog.getReplaceString(),
                  !_dialog.isSearchUp(),
                  _dialog.isMatchCase(),
                  _dialog.isWholeWord(),
                  _dialog.isRegExp());

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
      _squirrelRSyntaxTextArea.clearMarkAllHighlights();
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _squirrelRSyntaxTextArea.markAll(
               _dialog.getReplaceString(),
               _dialog.isMatchCase(),
               _dialog.isWholeWord(),
               _dialog.isRegExp());
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
            _squirrelRSyntaxTextArea.markAll(
               _lastSearchString,
               _dialog.isMatchCase(),
               _dialog.isWholeWord(),
               _dialog.isRegExp());
         }


         boolean found = SearchEngine.find(
            _squirrelRSyntaxTextArea,
            _lastSearchString,
            !_dialog.isSearchUp(),
            _dialog.isMatchCase(),
            _dialog.isWholeWord(),
            _dialog.isRegExp());

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

               found = SearchEngine.find(
                  _squirrelRSyntaxTextArea,
                  _lastSearchString,
                  !_dialog.isSearchUp(),
                  _dialog.isMatchCase(),
                  _dialog.isWholeWord(),
                  _dialog.isRegExp());

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

      boolean found =
         SearchEngine.find(
            _squirrelRSyntaxTextArea,
            _lastSearchString,
            !searchDialogState.isSearchUp(),
            searchDialogState.isMatchCase(),
            searchDialogState.isWholeWord(),
            searchDialogState.isRegExp());

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

         found = SearchEngine.find(
            _squirrelRSyntaxTextArea,
            _lastSearchString,
            !searchDialogState.isSearchUp(),
            searchDialogState.isMatchCase(),
            searchDialogState.isWholeWord(),
            searchDialogState.isRegExp());

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

      _squirrelRSyntaxTextArea.markAll(
         _lastSearchString,
         _storedSearchDialogState.isMatchCase(),
         _storedSearchDialogState.isWholeWord(),
         _storedSearchDialogState.isRegExp());
   }

   public void unmarkAll()
   {
      _squirrelRSyntaxTextArea.clearMarkAllHighlights();
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
