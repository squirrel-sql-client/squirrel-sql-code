package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.util.ArrayList;

public class SqlCommentHelper
{
   private final int[][] _commentIntervals;

   public SqlCommentHelper(String statement)
   {
      this(statement, () -> {});
   }

   public SqlCommentHelper(String sqlText, ParseTerminateRequestCheck check)
   {
      _commentIntervals = calculateCommentIntervals(sqlText, check);
   }

   private int[][] calculateCommentIntervals(String sqlEditorText, ParseTerminateRequestCheck check)
   {
      ArrayList<int[]> ret = new ArrayList<>();
      boolean inMultiLineComment = false;
      boolean inLineComment = false;
      boolean isaSlash = false;
      boolean isaStar = false;
      boolean isaMinus = false;

      int[] curComment = null;

      int literalDelimsCount = 0;


      for(int i=0; i < sqlEditorText.length(); ++i)
      {
         check.check();


         if (false == inLineComment && false == inMultiLineComment)
         {
            if ('\'' == sqlEditorText.charAt(i))
            {
               ++literalDelimsCount;
            }

            if(SqlLiteralHelper.isInLiteral(literalDelimsCount))
            {
               continue;
            }
         }

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
         ret.add(curComment);
      }

      return ret.toArray(new int[ret.size()][]);
   }

   boolean isInComment(int pos)
   {
      for(int i=0; i < _commentIntervals.length; ++i)
      {
         if(_commentIntervals[i][0] <= pos && pos <= _commentIntervals[i][1])
         {
            return true;
         }
      }

      return false;
   }

   public boolean isCommentBegin(int pos)
   {
      for(int i=0; i < _commentIntervals.length; ++i)
      {
         if(_commentIntervals[i][0] == pos)
         {
            return true;
         }
      }

      return false;
   }
}
