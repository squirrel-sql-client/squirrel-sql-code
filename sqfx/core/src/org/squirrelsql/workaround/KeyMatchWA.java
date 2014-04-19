package org.squirrelsql.workaround;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;

public class KeyMatchWA
{
   public static boolean matches(KeyEvent keyEvent, KeyCodeCombination keyCodeCombination)
   {
      // return keyCodeCombination.match(keyEvent) does not work

      if(null == keyCodeCombination)
      {
         return false;
      }

      if( "DOWN".equals(keyCodeCombination.getControl().name()) && false == keyEvent.isControlDown())
      {
         return false;
      }

      if( "DOWN".equals(keyCodeCombination.getAlt().name()) && false == keyEvent.isAltDown())
      {
         return false;
      }

      if( "DOWN".equals(keyCodeCombination.getShift().name()) && false == keyEvent.isShiftDown())
      {
         return false;
      }

      if(keyEvent.getCharacter().equalsIgnoreCase(keyCodeCombination.getCode().getName()))
      {
         return true;
      }

      if(
                ("\r".equals(keyEvent.getCharacter()) || "\n".equals(keyEvent.getCharacter()))
            &&  (keyCodeCombination.getCode().equals(KeyCode.ENTER))
        )
      {
         return true;
      }

      if(
                " ".equals(keyEvent.getCharacter())
            &&  keyCodeCombination.getCode().equals(KeyCode.SPACE)
        )
      {
         return true;
      }

      return false;
   }
}
