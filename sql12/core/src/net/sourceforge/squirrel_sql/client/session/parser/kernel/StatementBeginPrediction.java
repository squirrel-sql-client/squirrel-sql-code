package net.sourceforge.squirrel_sql.client.session.parser.kernel;


import java.util.ArrayList;

public class StatementBeginPrediction
{
   static int predictNextStatementBegin(String sqlEditorText, int startPos, ParseTerminateRequestCheck check)
   {
      int commentIntervals[][] = calculateCommentIntervals(sqlEditorText, check);

      int ret = startPos;

      int openBracketsCount = 0;
      int literalDelimsCount = 0;

      while( sqlEditorText.length() > ret)
      {
         if('(' == sqlEditorText.charAt(ret))
         {
            ++openBracketsCount;
         }
         else if(')' == sqlEditorText.charAt(ret))
         {
            --openBracketsCount;
         }
         else if('\'' == sqlEditorText.charAt(ret))
         {
            ++literalDelimsCount;
         }


         if(   false == isInBrackets(openBracketsCount)
            && false == isInLiteral(literalDelimsCount)
            && false == isInComment(ret, commentIntervals)
            && startsWithBeginKeyWord(sqlEditorText, ret)
             )
         {
            break;
         }

         ++ret;


         // Just Parsing stop request check.
         check.check();
      }


      return ret;
   }

   private static boolean isInLiteral(int literalDelimsCount)
   {
      return 1 == literalDelimsCount % 2;
   }

   private static boolean isInBrackets(int openBracketsCount)
   {
      return 0 < openBracketsCount;
   }

   private static int[][] calculateCommentIntervals(String sqlEditorText, ParseTerminateRequestCheck check)
   {
      ArrayList<int[]> ret = new ArrayList<>();
      boolean inMultiLineComment = false;
      boolean inLineComment = false;
      boolean isaSlash = false;
      boolean isaStar = false;
      boolean isaMinus = false;

      int[] curComment = null;

      for(int i=0; i < sqlEditorText.length(); ++i)
      {
         check.check();

         if('*' == sqlEditorText.charAt(i) && isaSlash && false == inMultiLineComment && false == inLineComment)
         {
            inMultiLineComment = true;
            curComment = new int[]{i-1, -1};
         }
         else if('/' == sqlEditorText.charAt(i) && isaStar && false == inLineComment && inMultiLineComment)
         {
            inMultiLineComment = false;
            curComment[1] = i;
            ret.add(curComment);
            curComment = null;

         }
         else if('-' == sqlEditorText.charAt(i) && isaMinus && false == inMultiLineComment && false == inLineComment)
         {
            inLineComment = true;
            curComment = new int[]{i-1, -1};
         }
         else if('\n' == sqlEditorText.charAt(i) && false == inMultiLineComment && inLineComment)
         {
            inLineComment = false;
            curComment[1] = i;
            ret.add(curComment);
            curComment = null;
         }



         if('/' == sqlEditorText.charAt(i))
         {
            isaSlash = true;
         }
         else if('*' == sqlEditorText.charAt(i))
         {
            isaStar = true;
         }
         else if('-' == sqlEditorText.charAt(i))
         {
            isaMinus = true;
         }
         else
         {
            isaSlash = false;
            isaStar = false;
            isaMinus = false;
         }
      }

      if(null != curComment)
      {
         curComment[1] = sqlEditorText.length();
      }

      return ret.toArray(new int[ret.size()][]);


   }

   private static boolean isInComment(int ret, int commentIntervals[][])
   {
      for(int i=0; i < commentIntervals.length; ++i)
      {
         if(commentIntervals[i][0] <= ret && ret <= commentIntervals[i][1])
         {
            return true;
         }
      }

      return false;
   }

   private static boolean startsWithBeginKeyWord(String sqlEditorText, int ret)
   {
      return   startsWithIgnoreCase(sqlEditorText,ret, "SELECT")
            || startsWithIgnoreCase(sqlEditorText, ret, "UPDATE")
            || startsWithIgnoreCase(sqlEditorText, ret, "DELETE")
            || startsWithIgnoreCase(sqlEditorText, ret, "INSERT")
            || startsWithIgnoreCase(sqlEditorText, ret, "ALTER")
            || startsWithIgnoreCase(sqlEditorText, ret, "CREATE")
            || startsWithIgnoreCase(sqlEditorText, ret, "DROP");
   }

   private static boolean startsWithIgnoreCase(String sqlEditorText, int ret, String keyWord)
   {
      int beginPos = ret;
      int endPos;

      if(ret == 0)
      {
         // Either are at teh beginning ...
         beginPos = 0;
      }
      else if(Character.isWhitespace(sqlEditorText.charAt(ret-1)))
      {
         // or a white space must be in front of the keyword.
         beginPos = ret;
      }
      else
      {
         return false;
      }

      if(sqlEditorText.length() == beginPos + keyWord.length())
      {
         endPos = beginPos + keyWord.length();
      }
      else if(sqlEditorText.length() > beginPos + keyWord.length() && Character.isWhitespace(sqlEditorText.charAt(beginPos + keyWord.length())))
      {
         endPos = beginPos + keyWord.length();
      }
      else
      {
         return false;
      }

      return keyWord.equalsIgnoreCase(sqlEditorText.substring(beginPos, endPos));
   }
}
