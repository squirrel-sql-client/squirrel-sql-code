package net.sourceforge.squirrel_sql.client.session.action.syntax;

public class CtrlLeftRightStopUtil
{
   /**
    * See also {@link net.sourceforge.squirrel_sql.client.session.SQLEntryPanelUtil#isParseStop(char, boolean)}
    * and {@link net.sourceforge.squirrel_sql.plugins.codecompletion.CompleteCodeAction#getNextStopCharPos(int)}
    */
   private static final char[] STOP_AT = new char[]{'.', '(', ')', '\'', '\n', ',', '=', '<', '>', '"'};

   public static int getStopToTheLeftPos(int pos, String text)
   {
      for(; pos > 0; --pos)
      {
         if(isToStopAt(text.charAt(pos - 1), text.charAt(pos)))
         {
            break;
         }
      }
      return pos;
   }

   public static int getStopToTheRightPos(int pos, String text)
   {
      for(; pos < text.length(); ++pos)
      {
         if(isToStopAt(text.charAt(pos), text.charAt(pos - 1)))
         {
            break;
         }
      }
      return pos;
   }

   private static boolean isToStopAt(char toCheck, char former)
   {
      if(isInStopAtArray(former) || isInStopAtArray(toCheck))
      {
         return true;
      }
      else if(false == Character.isWhitespace(former) && Character.isWhitespace(toCheck) ||
            Character.isWhitespace(former) && false == Character.isWhitespace(toCheck))
      //     else if(Character.isWhitespace(former) && false == Character.isWhitespace(toCheck))
      {
         return true;
      }

      return false;
   }

   private static boolean isInStopAtArray(char toCheck)
   {
      for(int i = 0; i < STOP_AT.length; i++)
      {
         if(toCheck == STOP_AT[i])
         {
            return true;
         }
      }

      return false;
   }
}
