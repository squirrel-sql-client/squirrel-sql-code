package net.sourceforge.squirrel_sql.client.session.parser.kernel;

public class HeuristicTokenParser
{
   private StringBuilder _token = new StringBuilder();

   private SqlCommentHelper _sqlCommentHelper;
   private int literalDelimsCount = 0;


   public HeuristicTokenParser(StatementBounds statementBounds)
   {
      _sqlCommentHelper = new SqlCommentHelper(statementBounds.getStatement());
   }

   public String nextToken(int[] i, String sql)
   {
      _token.setLength(0);
      for (int j = i[0]; j < sql.length(); j++)
      {
         char c = sql.charAt(j);

         if (isLiteralDelimiter(c))
         {
            if (false == _sqlCommentHelper.isInComment(j))
            {
               ++literalDelimsCount;
            }
         }

         if (_sqlCommentHelper.isInComment(j) || SqlLiteralHelper.isInLiteral(literalDelimsCount))
         {
            if (0 < _token.length())
            {
               i[0] = j+1;
               return _token.toString();
            }

            // When we arrive here _token is empty
            continue;
         }

         if(Character.isWhitespace(c))
         {
            if(0 == _token.length())
            {
               continue;
            }
            else
            {
               i[0] = j+1;
               return _token.toString();
            }
         }

         if(isSepartor(c))
         {
            if(0 == _token.length())
            {
               i[0] = j+1;
               return _token.append(c).toString();
            }
            else
            {
               i[0] = j;
               return _token.toString();
            }
         }

         _token.append(c);
      }

      i[0] = sql.length();
      return _token.toString();
   }

   private boolean isLiteralDelimiter(char c)
   {
      return '\'' == c;
   }

   private boolean isSepartor(char c)
   {
      return ',' == c || '(' == c || ')' == c;
   }

}
