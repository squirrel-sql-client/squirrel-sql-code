package net.sourceforge.squirrel_sql.fw.gui.textfind;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.EditableComboBoxHandler;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class TextFindCtrl
{
   public static final String PREF_KEY_TEXT_FIND_PREF_PREFIX = "TextFindCtrl.PREF_KEY_TEXT_FIND_PREF_PREFIX";
   public static final String PREF_KEY_SELECTED_TEXT_MODE = "TextFindCtrl.PREF_KEY_SELECTED_TEXT_MODE";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TextFindCtrl.class);

   private final JTextComponent _textComponentToSearch;
   private final JScrollPane _textComponentToSearchScrollPane;
   private final boolean _permanent;
   private final JPanel _containerPanel;
   private final TextFindPanel _findPanel;
   private final EditableComboBoxHandler _editableComboBoxHandler;

   int _nextOccurrenceToFind = 1;
   private String _lastTextToFind;
   private DefaultHighlighter.DefaultHighlightPainter _highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.orange);

   public TextFindCtrl(JTextComponent textComponentToSearch, JScrollPane textComponentToSearchScrollPane)
   {
      this(textComponentToSearch, textComponentToSearchScrollPane, false);
   }

   public TextFindCtrl(JTextComponent textComponentToSearch, JScrollPane textComponentToSearchScrollPane, boolean permanent)
   {
      _textComponentToSearch = textComponentToSearch;
      _textComponentToSearchScrollPane = textComponentToSearchScrollPane;
      _permanent = permanent;

      _containerPanel = new JPanel(new BorderLayout());
      if (null != textComponentToSearchScrollPane)
      {
         _containerPanel.add(_textComponentToSearchScrollPane, BorderLayout.CENTER);
      }
      else
      {
         _containerPanel.add(_textComponentToSearch, BorderLayout.CENTER);
      }

      _findPanel = new TextFindPanel(_permanent);

      _editableComboBoxHandler = new EditableComboBoxHandler(_findPanel.cboTextToFind, PREF_KEY_TEXT_FIND_PREF_PREFIX);

      _findPanel.btnDown.addActionListener(e -> onFind(true));
      _findPanel.btnUp.addActionListener(e -> onFind(false));
      _findPanel.btnMarkAll.addActionListener(e -> onToggleMarkAll());
      _findPanel.btnConfig.addActionListener(e -> onConfig());
      _findPanel.btnHide.addActionListener(e -> closeFind());

      initKeyStrokes();

      if(_permanent)
      {
         openFind();
      }
   }

   private void onConfig()
   {
      JPopupMenu popup = new JPopupMenu();

      TextFindMode selectedMode = getSelectedFindMode();

      ButtonGroup bg = new ButtonGroup();
      for (TextFindMode mode : TextFindMode.values())
      {
         JRadioButtonMenuItem radMnu = new JRadioButtonMenuItem(mode.getDisplayName());
         if(mode == selectedMode)
         {
            radMnu.setSelected(true);
         }
         radMnu.addActionListener(e -> Props.putString(PREF_KEY_SELECTED_TEXT_MODE, mode.name()));
         bg.add(radMnu);
         popup.add(radMnu);
      }

      popup.show(_findPanel.btnConfig, 0,_findPanel.btnConfig.getHeight());
   }

   private static TextFindMode getSelectedFindMode()
   {
      return TextFindMode.valueOf(Props.getString(PREF_KEY_SELECTED_TEXT_MODE, TextFindMode.CONTAINS_IGNORE_CASE.name()));
   }

   private void onFind(boolean next)
   {
      try
      {
         _findPanel.btnMarkAll.setSelected(false);

         if(StringUtilities.isEmpty(_editableComboBoxHandler.getItem(), true))
         {
            Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("TextFindCtrl.text.to.find.missing"));
            _nextOccurrenceToFind = 1;
            return;
         }

         _editableComboBoxHandler.saveCurrentItem();
         _textComponentToSearch.getHighlighter().removeAllHighlights();

         if(false == _editableComboBoxHandler.getItem().equals(_lastTextToFind))
         {
            _nextOccurrenceToFind = 1;
         }

         _lastTextToFind = _editableComboBoxHandler.getItem();

         if(false == next)
         {
            if (_nextOccurrenceToFind > 2)
            {
               _nextOccurrenceToFind -= 2;
            }
            else
            {
               _nextOccurrenceToFind = 1;
            }
         }

         MatchBounds matchBounds =
               TextFinder.findNthOccurrence(_textComponentToSearch.getText(), _editableComboBoxHandler.getItem(), _nextOccurrenceToFind, getSelectedFindMode());

         if(null != matchBounds)
         {
            //////////////////////////////////////////////////////////////////////
            // Scrolls correctly and does not interfere with highlighting.
            _textComponentToSearch.setSelectionStart(matchBounds.getBeginIx());
            _textComponentToSearch.setSelectionEnd(matchBounds.getBeginIx());
            //
            //////////////////////////////////////////////////////////////////////

            _textComponentToSearch.getHighlighter().addHighlight(matchBounds.getBeginIx(), matchBounds.getEndIx(), _highlightPainter);
            ++_nextOccurrenceToFind;
         }
         else
         {
            if(1 == _nextOccurrenceToFind)
            {
               String msg = s_stringMgr.getString("TextFindCtrl.could.not.find", _editableComboBoxHandler.getItem());
               Main.getApplication().getMessageHandler().showWarningMessage(msg);
            }
            else
            {
               String msg = s_stringMgr.getString("TextFindCtrl.last.occurrence.reached", _editableComboBoxHandler.getItem());
               Main.getApplication().getMessageHandler().showMessage(msg);
            }
            _nextOccurrenceToFind = 1;
         }
      }
      catch (BadLocationException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private void onToggleMarkAll()
   {
      try
      {
         if(false == _findPanel.btnMarkAll.isSelected())
         {
            _textComponentToSearch.getHighlighter().removeAllHighlights();
            //if (null != _textComponentToSearch.getText() && 0 < _textComponentToSearch.getText().length())
            //{
            //   _textComponentToSearch.setCaretPosition(0);
            //}
            return;
         }


         if(StringUtilities.isEmpty(_editableComboBoxHandler.getItem(), true))
         {
            Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("TextFindCtrl.text.to.find.missing"));
            return;
         }

         int nextOccurrenceToFind = 1;
         MatchBounds matchBounds =
               TextFinder.findNthOccurrence(_textComponentToSearch.getText(), _editableComboBoxHandler.getItem(), nextOccurrenceToFind, getSelectedFindMode());

         boolean firstMatch = true;
         while (null != matchBounds)
         {
            //////////////////////////////////////////////////////////////////////
            // Scrolls correctly and does not interfere with highlighting.
            if (firstMatch)
            {
               _textComponentToSearch.setSelectionStart(matchBounds.getBeginIx());
               _textComponentToSearch.setSelectionEnd(matchBounds.getBeginIx());
               firstMatch = false;
            }
            //
            //////////////////////////////////////////////////////////////////////

            _textComponentToSearch.getHighlighter().addHighlight(matchBounds.getBeginIx(), matchBounds.getEndIx(), _highlightPainter);
            ++nextOccurrenceToFind;

            matchBounds =
                  TextFinder.findNthOccurrence(_textComponentToSearch.getText(), _editableComboBoxHandler.getItem(), nextOccurrenceToFind, getSelectedFindMode());
         }
      }
      catch (BadLocationException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }


   private void initKeyStrokes()
   {
      Action findNextAction = new AbstractAction("TextFindCtrl.FindNext")
      {
         public void actionPerformed(ActionEvent e)
         {
            onFindButtonKeyStroke(true);
         }
      };

      Action findPrevAction = new AbstractAction("TextFindCtrl.FindPrev")
      {
         public void actionPerformed(ActionEvent e)
         {
            onFindButtonKeyStroke(false);
         }
      };
      Action escapeAction = new AbstractAction("TextFindCtrl.Escape")
      {
         public void actionPerformed(ActionEvent e)
         {
            _findPanel.btnHide.doClick();
         }
      };

      JComponent comp = (JComponent) _findPanel.cboTextToFind.getEditor().getEditorComponent();
      comp.registerKeyboardAction(findNextAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);
      comp.registerKeyboardAction(findNextAction, KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0, false), JComponent.WHEN_FOCUSED);

      comp.registerKeyboardAction(findPrevAction, KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.SHIFT_DOWN_MASK, false), JComponent.WHEN_FOCUSED);

      comp.registerKeyboardAction(escapeAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), JComponent.WHEN_FOCUSED);
   }

   private void onFindButtonKeyStroke(boolean next)
   {
      if (next)
      {
         _findPanel.btnDown.doClick();
      }
      else
      {
         _findPanel.btnUp.doClick();
      }
   }


   public JPanel getContainerPanel()
   {
      return _containerPanel;
   }

   public void toggleFind()
   {
      if (1 == _containerPanel.getComponents().length)
      {
         openFind();
      }
      else if (2 == _containerPanel.getComponents().length)
      {
         closeFind();
      }
   }

   private void openFind()
   {
      _containerPanel.remove(_findPanel);
      _containerPanel.add(_findPanel, BorderLayout.SOUTH);
      _containerPanel.doLayout();
      _findPanel.doLayout();
      _findPanel.cboTextToFind.doLayout();
      _editableComboBoxHandler.focus();
   }

   private void closeFind()
   {
      if (false == _permanent)
      {
         _editableComboBoxHandler.saveCurrentItem();
         _containerPanel.remove(_findPanel);
         _containerPanel.doLayout();
      }

      _nextOccurrenceToFind = 1;

      _findPanel.btnMarkAll.setSelected(false);
      _textComponentToSearch.getHighlighter().removeAllHighlights();
   }
}
