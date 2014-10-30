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


      boolean ctrlDown = "DOWN".equals(keyCodeCombination.getControl().name());
      boolean shiftDown = "DOWN".equals(keyCodeCombination.getShift().name());

      if( ctrlDown && false == keyEvent.isControlDown())
      {
         return false;
      }

      if( "DOWN".equals(keyCodeCombination.getAlt().name()) && false == keyEvent.isAltDown())
      {
         return false;
      }

      if( shiftDown && false == keyEvent.isShiftDown())
      {
         return false;
      }

      if(keyEvent.getCode().equals(KeyCode.UP) && keyCodeCombination.getCode().equals(KeyCode.UP))
      {
         return true;
      }

      if(keyEvent.getCode().equals(KeyCode.DOWN) && keyCodeCombination.getCode().equals(KeyCode.DOWN))
      {
         return true;
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
                "\t".equals(keyEvent.getCharacter())
            &&  keyCodeCombination.getCode().equals(KeyCode.TAB)
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

      if(27 == keyEvent.getCharacter().charAt(0) && keyCodeCombination.getCode().equals(KeyCode.ESCAPE))
      {
         return true;
      }



      if(ctrlDown && false == shiftDown)
      {
         return checkFunnyCharacterShiftingByCtrl(keyEvent, keyCodeCombination);
      }



      return false;
   }

   private static boolean checkFunnyCharacterShiftingByCtrl(KeyEvent keyEvent, KeyCodeCombination keyCodeCombination)
   {
      String cStr = keyEvent.getCharacter();

      if(0 == cStr.length())
      {
         return false;
      }


      if(cStr.charAt(0) > 26) // lower case z
      {
         return false;
      }

      char c = (char) (cStr.charAt(0) + 'a' - 1);

      cStr =  "" + Character.valueOf(c);


      if(cStr.equalsIgnoreCase(keyCodeCombination.getCode().getName()))
      {
         return true;
      }

      return false;
   }
}
