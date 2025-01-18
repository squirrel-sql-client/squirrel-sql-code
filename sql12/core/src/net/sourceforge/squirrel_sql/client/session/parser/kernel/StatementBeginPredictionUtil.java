package net.sourceforge.squirrel_sql.client.session.parser.kernel;

public class StatementBeginPredictionUtil
{
   static boolean startsWithIgnoreCase(String sqlEditorText, int beginPos, String keyWord)
   {
      int curPos = beginPos;
      int endPos;

      if(beginPos == 0)
      {
         // Either are at teh beginning ...
         curPos = 0;
      }
      else if(Character.isWhitespace(sqlEditorText.charAt(beginPos - 1)))
      {
         // or a white space must be in front of the keyword.
         curPos = beginPos;
      }
      else
      {
         return false;
      }

      if(sqlEditorText.length() == curPos + keyWord.length())
      {
         endPos = curPos + keyWord.length();
      }
      else if(sqlEditorText.length() > curPos + keyWord.length() && Character.isWhitespace(sqlEditorText.charAt(curPos + keyWord.length())))
      {
         endPos = curPos + keyWord.length();
      }
      else
      {
         return false;
      }

      return keyWord.equalsIgnoreCase(sqlEditorText.substring(curPos, endPos));
   }
}
