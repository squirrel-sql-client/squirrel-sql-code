package net.sourceforge.squirrel_sql.client.session.parser.kernel;


public class StatementBeginPrediction
{
   private boolean _statementBeganByWithClause;

   int predictNextStatementBegin(String sqlEditorText, int startPos, ParseTerminateRequestCheck check)
   {
      SqlCommentHelper sqlCommentHelper = new SqlCommentHelper(sqlEditorText, check);

      int ret = startPos;

      int openBracketsCount = 0;
      int literalDelimsCount = 0;

      UnionKeyWordCheck unionKeyWordCheck = new UnionKeyWordCheck();

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
            if('(' == sqlEditorText.charAt(ret))
            {
               ++openBracketsCount;
            }
            else if(')' == sqlEditorText.charAt(ret))
            {
               --openBracketsCount;
            }
         }

         if(
               false == SqlLiteralHelper.isInLiteral(literalDelimsCount)
            && false == sqlCommentHelper.isInComment(ret)
            && false == isInBrackets(openBracketsCount)
            && startsWithBeginKeyWord(sqlEditorText, ret, unionKeyWordCheck)
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

   private boolean isInBrackets(int openBracketsCount)
   {
      return 0 < openBracketsCount;
   }


   private boolean startsWithBeginKeyWord(String sqlEditorText, int beginPos, UnionKeyWordCheck unionKeyWordCheck)
   {
      if(StatementBeginPredictionUtil.startsWithIgnoreCase(sqlEditorText, beginPos, "WITH"))
      {
         //if(_statementBeganByWithClause)
         //{
         //   // We have the second or more WITHs
         //   return false;
         //}

         _statementBeganByWithClause = true;
         return true;
      }

      boolean ret = isSelectBegin(sqlEditorText, beginPos, unionKeyWordCheck)
            || StatementBeginPredictionUtil.startsWithIgnoreCase(sqlEditorText, beginPos, "UPDATE")
            || StatementBeginPredictionUtil.startsWithIgnoreCase(sqlEditorText, beginPos, "DELETE")
            || StatementBeginPredictionUtil.startsWithIgnoreCase(sqlEditorText, beginPos, "INSERT")
            || StatementBeginPredictionUtil.startsWithIgnoreCase(sqlEditorText, beginPos, "ALTER")
            || StatementBeginPredictionUtil.startsWithIgnoreCase(sqlEditorText, beginPos, "CREATE")
            || StatementBeginPredictionUtil.startsWithIgnoreCase(sqlEditorText, beginPos, "DROP");

      if(ret)
      {
         if(_statementBeganByWithClause)
         {
            _statementBeganByWithClause = false;
            return false;
         }
         unionKeyWordCheck.reset();
      }

      return ret;
   }

   private boolean isSelectBegin(String sqlEditorText, int beginPos, UnionKeyWordCheck unionKeyWordCheck)
   {
      unionKeyWordCheck.check(sqlEditorText, beginPos);
      boolean isSelectStart = StatementBeginPredictionUtil.startsWithIgnoreCase(sqlEditorText, beginPos, "SELECT");

      if(false == isSelectStart)
      {
         return false;
      }

      if(false == unionKeyWordCheck.previousWasUnionOrUnionAll())
      {
         return true;
      }
      else
      {
         unionKeyWordCheck.reset();
         return false;
      }
   }

}
