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

   public static int[] getLineBoundsAtCursor(JTextComponent textComponent)
   {
      String text = textComponent.getText();
      int caretPos = textComponent.getCaretPosition();

      if(0 == text.length())
      {
         return new int[]{0,0};
      }

      int beg = getPrevNewLineOrTextBegin(caretPos, text);
      int end = getNextNewLineOrTextEnd(caretPos, text);

      return new int[]{beg, end};
   }

   private static int getNextNewLineOrTextEnd(int caretPos, String text)
   {
      if(caretPos == text.length())
      {
         return text.length();
      }

      if('\n' == text.charAt(caretPos))
      {
         return caretPos + 1;
      }

      final int nextNL = text.indexOf('\n', caretPos);

      if(-1 == nextNL)
      {
         return text.length();
      }
      else
      {
         return nextNL + 1;
      }
   }

   private static int getPrevNewLineOrTextBegin(int caretPos, String text)
   {
      if(caretPos == 0)
      {
         return 0;
      }

      int pos = caretPos;
      if(caretPos == text.length() || '\n' == text.charAt(caretPos) )
      {
         pos = caretPos -1;
      }

      for (int i = pos;; --i)
      {
         if(i <= 0)
         {
            return 0;
         }

         if('\n' == text.charAt(i))
         {
            return i + 1;
         }
      }

   }

   /**
    * See also {@link net.sourceforge.squirrel_sql.client.session.action.syntax.CtrlLeftRightStopUtil#STOP_AT}
    * and {@link net.sourceforge.squirrel_sql.plugins.codecompletion.CompleteCodeAction#getNextStopCharPos}
    */
   private static boolean isParseStop(char c, boolean treatDotAsStop)
   {
      return
         '(' == c ||
         ')' == c ||
         ',' == c ||
         ';' == c ||
         '=' == c ||
         '>' == c ||
         '<' == c ||
         '\'' == c ||
         '/' == c ||
         ':' == c ||
         '"' == c ||
         Character.isWhitespace(c) ||
         (treatDotAsStop && '.' == c);
   }
}
