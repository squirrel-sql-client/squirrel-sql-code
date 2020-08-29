package net.sourceforge.squirrel_sql.client.session.parser.kernel;


public class StatementBeginPrediction
{
   static int predictNextStatementBegin(String sqlEditorText, int startPos, ParseTerminateRequestCheck check)
   {
      SqlCommentHelper sqlCommentHelper = new SqlCommentHelper(sqlEditorText, check);

      int ret = startPos;

      int openBracketsCount = 0;
      int literalDelimsCount = 0;

      while( sqlEditorText.length() > ret)
      {
         if ('\'' == sqlEditorText.charAt(ret))
         {
            if (false == sqlCommentHelper.isInComment(ret))
            {
               ++literalDelimsCount;
            }
         }

         if (false == sqlCommentHelper.isInComment(ret) && false == SqlLiteralHelper.isInLiteral(literalDelimsCount))
         {
            if ('(' == sqlEditorText.charAt(ret))
            {
               ++openBracketsCount;
            }
            else if (')' == sqlEditorText.charAt(ret))
            {
                  --openBracketsCount;
            }
         }


         if(
               false == SqlLiteralHelper.isInLiteral(literalDelimsCount)
            && false == sqlCommentHelper.isInComment(ret)
            && false == isInBrackets(openBracketsCount)
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

   private static boolean isInBrackets(int openBracketsCount)
   {
      return 0 < openBracketsCount;
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
