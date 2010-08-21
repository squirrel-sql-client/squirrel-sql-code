package net.sourceforge.squirrel_sql.client.session;

import javax.swing.text.JTextComponent;

public class SQLEntryPanelUtil
{
   public static int[] getWordBoundsAtCursor(JTextComponent textComponent, boolean qualified)
   {
      String text = textComponent.getText();
      int caretPos = textComponent.getCaretPosition();

      int[] beginAndEndPos = new int[2];

      int lastIndexOfText = Math.max(0,text.length()-1);
      beginAndEndPos[0] = Math.min(caretPos, lastIndexOfText); // The Math.min is for the Caret at the end of the text
      while(0 < beginAndEndPos[0])
      {
         if(isParseStop(text.charAt(beginAndEndPos[0] - 1), false == qualified))
         {
            break;
         }
         --beginAndEndPos[0];
      }

      beginAndEndPos[1] = caretPos;
      while(beginAndEndPos[1] < text.length() && false == isParseStop(text.charAt(beginAndEndPos[1]), true))
      {
         ++beginAndEndPos[1];
      }
      return beginAndEndPos;
   }

   static boolean isParseStop(char c, boolean treatDotAsStop)
   {
      return
         '(' == c ||
         ')' == c ||
         ',' == c ||
         ';' == c ||
         '\'' == c ||
         Character.isWhitespace(c) ||
         (treatDotAsStop && '.' == c);
   }
}
