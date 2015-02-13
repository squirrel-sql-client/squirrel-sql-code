package org.squirrelsql.session.sql;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.squirrelsql.workaround.KeyMatchWA;

public class CtrlLeftRightHandler
{
   public static final char[] STOP_AT = new char[]{'.', '(', ')', '\'', '\n', ',', '=', '<', '>'};
   private CodeArea _sqlTextArea;

   public CtrlLeftRightHandler(CodeArea sqlTextArea)
   {
      _sqlTextArea = sqlTextArea;

      sqlTextArea.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
      {
         @Override
         public void handle(KeyEvent event)
         {
            if (KeyMatchWA.matches(event, new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN)))
            {
               onLeftCtrl();
               event.consume();
            }
            if (KeyMatchWA.matches(event, new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN)))
            {
               onRightCtrl();
               event.consume();
            }
            if (KeyMatchWA.matches(event, new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN)))
            {
               onLeftCtrlShift();
               event.consume();
            }
            if (KeyMatchWA.matches(event, new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN)))
            {
               onRightCtrlShift();
               event.consume();
            }
         }
      });

   }

   private void onLeftCtrlShift()
   {
      moveCtrlLeft(true);
   }

   private void onLeftCtrl()
   {
      moveCtrlLeft(false);
      clearSelectionAnchor();
   }

   private void moveCtrlLeft(boolean select)
   {
      String text = _sqlTextArea.getText();
      int pos = _sqlTextArea.getCaretPosition() - 1;

      if (pos < 0)
      {
         return;
      }


      for (; pos > 0; --pos)
      {
         if (isToStopAt(text.charAt(pos - 1), text.charAt(pos)))
         {
            break;
         }
      }

      if (select)
      {
         moveCaretAndSelect(pos);
      }
      else
      {
         _sqlTextArea.positionCaret(pos);
      }
   }


   private void onRightCtrlShift()
   {
      moveCtrlRight(true);
   }

   private void onRightCtrl()
   {
      moveCtrlRight(false);
      clearSelectionAnchor();
   }

   private void clearSelectionAnchor()
   {
      _sqlTextArea.selectRange(_sqlTextArea.getCaretPosition(), _sqlTextArea.getCaretPosition());
   }

   private void moveCtrlRight(boolean select)
   {
      String text = _sqlTextArea.getText();
      int pos = _sqlTextArea.getCaretPosition() + 1;

      if (pos > text.length())
      {
         return;
      }


      for (; pos < text.length(); ++pos)
      {
         if (isToStopAt(text.charAt(pos), text.charAt(pos - 1)))
         {
            break;
         }
      }

      if (select)
      {
         moveCaretAndSelect(pos);
      }
      else
      {
         _sqlTextArea.positionCaret(pos);
      }
   }

   private void moveCaretAndSelect(int pos)
   {
      _sqlTextArea.selectRange(_sqlTextArea.getAnchor(), pos);
   }


   private boolean isToStopAt(char toCheck, char former)
   {
      if (isInStopAtArray(former) || isInStopAtArray(toCheck))
      {
         return true;
      }
      else if (false == Character.isWhitespace(former) && Character.isWhitespace(toCheck) ||
            Character.isWhitespace(former) && false == Character.isWhitespace(toCheck))
      //     else if(Character.isWhitespace(former) && false == Character.isWhitespace(toCheck))
      {
         return true;
      }

      return false;
   }

   private boolean isInStopAtArray(char toCheck)
   {
      for (int i = 0; i < STOP_AT.length; i++)
      {
         if (toCheck == STOP_AT[i])
         {
            return true;
         }
      }

      return false;

   }
}
