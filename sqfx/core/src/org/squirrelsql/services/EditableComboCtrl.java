package org.squirrelsql.services;

import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import org.squirrelsql.workaround.FocusNodeWA;
import org.squirrelsql.workaround.KeyMatchWA;

public class EditableComboCtrl
{
   private static final String PREF_RECENT_SEARCH_STRING_PREFIX = "recentSearchString_";
   private static final int MAX_RECENT_SEARCH_STRINGS = 5;


   private ComboBox<String> _cboSearchString;
   private String _prefsPrefix;
   private EditableComboCtrlEnterListener _listener;
   private Pref _pref = new Pref(getClass());

   public EditableComboCtrl(ComboBox<String> cboSearchString, String prefsPrefix, EditableComboCtrlEnterListener listener)
   {
      _cboSearchString = cboSearchString;
      _prefsPrefix = prefsPrefix;
      _listener = listener;
      _cboSearchString.setEditable(true);
      loadRecentSearchStrings(_cboSearchString);

      _cboSearchString.setOnKeyPressed(this::onHandleKeyEvent);

   }

   private void onHandleKeyEvent(KeyEvent event)
   {
      if (null != _listener)
      {
         if(KeyMatchWA.matches(event, new KeyCodeCombination(KeyCode.ENTER)))
         {
            _listener.enterPressed();
         }
         else if(KeyMatchWA.matches(event, new KeyCodeCombination(KeyCode.ESCAPE)))
         {
            _listener.escapePressed();
         }
      }
   }

   private void loadRecentSearchStrings(ComboBox<String> cbo)
   {
      for (int i=0; i < MAX_RECENT_SEARCH_STRINGS; ++i)
      {
         String searchString = _pref.getString(getRecentSearchStringPrefBegin() + i, null);

         if(null != searchString)
         {
            cbo.getItems().add(searchString);
         }
      }
   }

   private String getRecentSearchStringPrefBegin()
   {
      return _prefsPrefix + "." + PREF_RECENT_SEARCH_STRING_PREFIX;
   }

   public String getText()
   {
      return _cboSearchString.getEditor().getText();
   }

   public void addCurrentTextToHistory()
   {
      String editorText = getText();

      int caretPosition = _cboSearchString.getEditor().getCaretPosition();

      if(Utils.isEmptyString(editorText))
      {
         return;
      }

      _cboSearchString.getItems().remove(editorText);
      _cboSearchString.getItems().add(0, editorText);
      _cboSearchString.getSelectionModel().select(0);
      writeRecentSearchString(_cboSearchString);

      _cboSearchString.getEditor().positionCaret(caretPosition);
   }

   private void writeRecentSearchString(ComboBox<String> cbo)
   {
      for (int i=0; i < MAX_RECENT_SEARCH_STRINGS; ++i)
      {
         if(cbo.getItems().size() <= i)
         {
            return;
         }

         _pref.set(getRecentSearchStringPrefBegin() + i, cbo.getItems().get(i));
      }
   }

   public void requestFocus()
   {
      FocusNodeWA.forceFocus(_cboSearchString.getEditor());
   }
}
