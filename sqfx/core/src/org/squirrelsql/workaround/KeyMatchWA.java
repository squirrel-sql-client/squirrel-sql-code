package org.squirrelsql.workaround;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;

public class KeyMatchWA
{
   public static boolean matches(KeyEvent keyEvent, KeyCodeCombination keyCodeCombination)
   {
      if(null == keyCodeCombination)
      {
         return false;
      }

      if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED)
      {
         // This does not work for KEY_TYPED,
         // see http://docs.oracle.com/javafx/2/api/javafx/scene/input/KeyCodeCombination.html#match%28javafx.scene.input.KeyEvent%29

         // On the difference between KEY_TYPED and KEY_PRESSED,
         // see http://docs.oracle.com/javafx/2/api/javafx/scene/input/KeyEvent.html

         return keyCodeCombination.match(keyEvent);
      }



      // Key typed is almost everywhere replaced by key pressed because of the nicer matching.
      // One place where key typed is still used is for the code completion list listener.
      // The code below is more than is needed for that. It results from times when key typed
      // was used more and thus much matching needed to be done manually.


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
