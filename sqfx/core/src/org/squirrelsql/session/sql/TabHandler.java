package org.squirrelsql.session.sql;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.squirrelsql.services.Utils;
import org.squirrelsql.workaround.KeyMatchWA;

public class TabHandler
{
   public static final String TAB_SPACES = "   ";

   private SQLTextAreaServices _sqlTextAreaServices;

   public TabHandler(SQLTextAreaServices sqlTextAreaServices)
   {
      _sqlTextAreaServices = sqlTextAreaServices;

      // Filters(Capturing) before Handlers(Bubbling)
      // see http://docs.oracle.com/javafx/2/events/processing.htm
      _sqlTextAreaServices.getTextArea().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
      {
         @Override
         public void handle(KeyEvent event)
         {
            onHandleKeyEvent(event);
         }
      });

   }

   private void onHandleKeyEvent(KeyEvent event)
   {
      if (KeyMatchWA.matches(event, new KeyCodeCombination(KeyCode.TAB)))
      {
         onTabSelection(true);
         event.consume();
      }
      else if (KeyMatchWA.matches(event, new KeyCodeCombination(KeyCode.TAB, KeyCombination.SHIFT_DOWN)))
      {
         onTabSelection(false);
         event.consume();
      }
   }

   private void onTabSelection(boolean forward)
   {
      String selectedText = _sqlTextAreaServices.getTextArea().getSelectedText();

      if (Utils.isEmptyString(selectedText))
      {
         handelTabWithoutSelection(forward);
      }
      else
      {
         handelTabWithSelection(forward, selectedText);
      }
   }

   private void handelTabWithoutSelection(boolean forward)
   {
      if (forward)
      {
         CodeArea textArea = _sqlTextAreaServices.getTextArea();
         textArea.insertText(textArea.getCaretPosition(), TAB_SPACES);
      }
      else
      {
         String replacement;
         String lineAtCaret = _sqlTextAreaServices.getLineAtCaret();
         if(lineAtCaret.startsWith(TAB_SPACES))
         {
            replacement = lineAtCaret.substring(TAB_SPACES.length());
         }
         else
         {
            replacement = lineAtCaret.trim();
         }

         _sqlTextAreaServices.replaceLineAtCaret(replacement);

         if(replacement.length() == lineAtCaret.length())
         {
            _sqlTextAreaServices.updateHighlighting();
         }

      }
   }

   private void handelTabWithSelection(boolean forward, String selectedText)
   {
      String replacement = "";

      boolean maybeUnchanged = false;

      if (forward)
      {
         String[] splits = selectedText.split("\n");

         for (String split : splits)
         {
            replacement += TAB_SPACES + split + "\n";
         }

      }
      else
      {
         String[] splits = selectedText.split("\n");

         for (String split : splits)
         {
            if (split.startsWith(TAB_SPACES))
            {
               replacement += split.substring(TAB_SPACES.length()) + "\n";
            }
            else
            {
               replacement += split.trim() + "\n";
               maybeUnchanged = true;
            }
         }
      }

      _sqlTextAreaServices.replaceSelection(replacement, true);


      if(maybeUnchanged)
      {
         _sqlTextAreaServices.updateHighlighting();
      }
   }
}
