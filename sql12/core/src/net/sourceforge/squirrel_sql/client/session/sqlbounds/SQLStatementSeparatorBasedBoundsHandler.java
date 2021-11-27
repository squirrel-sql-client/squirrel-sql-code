package net.sourceforge.squirrel_sql.client.session.sqlbounds;

import net.sourceforge.squirrel_sql.fw.sql.commentandliteral.SQLCommentAndLiteralHandler;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.IQueryTokenizer;

public class SQLStatementSeparatorBasedBoundsHandler
{
   public static int nextIndexOfStateSep(IQueryTokenizer tokenizer, String sql, int pos)
   {
      if(0 == sql.length())
      {
         return 0;
      }

      final String separator = tokenizer.getSQLStatementSeparator();
      int sepPos = getNextSepPos(sql, pos, separator);

      while(sepPos != -1)
      {
         if( sepPos >= pos && isValidSeparator(tokenizer, sql, separator, sepPos) )
         {
            return sepPos + separator.length() - 1;
         }

         sepPos = getNextSepPos(sql, sepPos + 1, separator);

      }

      return sql.length() - 1;
   }

   public static int previousIndexOfStateSep(IQueryTokenizer tokenizer, String sql, int pos)
   {
      if(0 == sql.length())
      {
         return 0;
      }

      final String separator = tokenizer.getSQLStatementSeparator();

      int sepPos = getPreviousSepPos(sql, pos, separator);
      while(sepPos > 0)
      {
         if( sepPos < pos && isValidSeparator(tokenizer, sql, separator, sepPos) )
         {
            return toFirstNonWhiteSpace(sql, sepPos + separator.length());
         }
         sepPos = getPreviousSepPos(sql, sepPos - 1, separator);
      }

      return toFirstNonWhiteSpace(sql, 0);
   }

   private static boolean isValidSeparator(IQueryTokenizer tokenizer, String sql, String separator, int sepPos)
   {
      return false == isCommentOrLiteral(sql, sepPos, tokenizer)
             && (1 == separator.length() || isSurroundedByWhiteSpaces(sql, sepPos, separator));
   }


   private static int toFirstNonWhiteSpace(String sql, int sepPos)
   {
      for (int i = sepPos; i < sql.length(); i++)
      {
          if(false == Character.isWhitespace(sql.charAt(i)))
          {
             return i;
          }
      }

      return sql.length() - 1;

   }

   private static int getPreviousSepPos(String sql, int pos, String sqlStatementSeparator)
   {
      int prevSepPos = 0;
      int sepPos = sql.indexOf(sqlStatementSeparator);

      while ( sepPos >= 0 && sepPos < pos )
      {
         prevSepPos = sepPos;
         sepPos = sql.indexOf(sqlStatementSeparator, prevSepPos + 1);
      }

      return prevSepPos;
   }

   private static int getNextSepPos(String sql, int pos, String sqlStatementSeparator)
   {
      int sepPos = sql.indexOf(sqlStatementSeparator, pos);

//      while(-1 != sepPos
//            && false == (1 == sqlStatementSeparator.length() || isSurroundedByWhiteSpaces(sql, sepPos, sqlStatementSeparator)))
//      {
//         sepPos = sql.indexOf(sqlStatementSeparator, sepPos + 1);
//      }

      return sepPos;
   }


   private static boolean isSurroundedByWhiteSpaces(String sql, int sepPos, String statementSeparator)
   {
      boolean ret = true;
      if(sepPos > 0)
      {
         ret = Character.isWhitespace(sql.charAt(sepPos - 1));
      }

      if(sepPos + statementSeparator.length() < sql.length())
      {
         ret = ret && Character.isWhitespace(sql.charAt(sepPos + statementSeparator.length()));
      }

      return ret;
   }



   private static boolean isCommentOrLiteral(String sql, int sepPos, IQueryTokenizer tokenizer)
   {
      final SQLCommentAndLiteralHandler clh = new SQLCommentAndLiteralHandler(sql, tokenizer.getLineCommentBegin(), false, false);

      for (int i = 0; i <= sepPos; i++)
      {
         clh.nextPosition(i);
      }

      return clh.isInLiteral() || clh.isInLineComment() || clh.isInMultiLineComment();
   }


}
