package net.sourceforge.squirrel_sql.client.session.sqlbounds;

import net.sourceforge.squirrel_sql.fw.sql.commentandliteral.SQLCommentAndLiteralHandler;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.IQueryTokenizer;

/**
 * Handles the option to use statement separators instead of empty lines as bounds of SQL to execute.
 */
public class SQLStatementSeparatorBasedBoundsHandler
{
   public static int nextIndexOfStateSep(String sql, int pos, IQueryTokenizer tokenizer)
   {
      if(0 == sql.length())
      {
         return 0;
      }

      pos = correctPositionIfInOrRightBehindSeparator(sql, pos, tokenizer);

      final String separator = tokenizer.getSQLStatementSeparator();
      int sepPos = getNextSepPos(sql, pos, separator);

      while(sepPos != -1)
      {
         if( sepPos >= pos && isValidSeparator(sql, sepPos, tokenizer) )
         {
            return sepPos + separator.length();
         }

         sepPos = getNextSepPos(sql, sepPos + 1, separator);

      }

      return sql.length() - 1;
   }

   public static int previousIndexOfStateSep(String sql, int pos, IQueryTokenizer tokenizer)
   {
      if(0 == sql.length())
      {
         return 0;
      }

      pos = correctPositionIfInOrRightBehindSeparator(sql, pos, tokenizer);

      final String separator = tokenizer.getSQLStatementSeparator();

      int sepPos = getPreviousSepPos(sql, pos, separator);
      while(sepPos > 0)
      {
         if( sepPos < pos && isValidSeparator(sql, sepPos, tokenizer) )
         {
            return toFirstNonWhiteSpace(sql, sepPos + separator.length());
         }
         sepPos = getPreviousSepPos(sql, sepPos - 1, separator);
      }

      return toFirstNonWhiteSpace(sql, 0);
   }

   private static boolean isValidSeparator(String sql, int sepPos, IQueryTokenizer tokenizer)
   {
      String separator = tokenizer.getSQLStatementSeparator();

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

   private static int correctPositionIfInOrRightBehindSeparator(String sql, int pos, IQueryTokenizer tokenizer)
   {
      final String separator = tokenizer.getSQLStatementSeparator();
      final int prevSepPos = getPreviousSepPos(sql, pos, separator);

      if(pos <= prevSepPos + separator.length())
      {
         // Here we are in or right behind a separator
         if(isValidSeparator(sql, prevSepPos, tokenizer))
         {
            return prevSepPos;
         }
      }

      return pos;
   }
}
