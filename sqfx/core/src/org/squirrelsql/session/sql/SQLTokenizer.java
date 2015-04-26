package org.squirrelsql.session.sql;

import org.squirrelsql.AppState;

import java.util.ArrayList;
import java.util.Iterator;

public class SQLTokenizer
{
   private String _lineCommentBegin = "--";
   private boolean _removeMultiLineComment = true;



   private Iterator<String> _iterator;
   private ArrayList<String> _sqls;
   private boolean _getFirstSqlWasCalled;
   private String _currentSql;

   public SQLTokenizer(String script)
   {
      _sqls = new ArrayList<>();

//      _sqls.add("Select * from articles");
//      _sqls.add("Select * from discounts");
//      _sqls.add("Select * from sales_prices");
//      _sqls.add("Select * from receipts");
//
//      _iterator = _sqls.iterator();


      String MULTI_LINE_COMMENT_END = "*/";
      String MULTI_LINE_COMMENT_BEGIN = "/*";

      script = script.replace('\r', ' ');

      StringBuffer curQuery = new StringBuffer();

      boolean isInLiteral = false;
      boolean isInMultiLineComment = false;
      boolean isInLineComment = false;
      int literalSepCount = 0;


      for (int i = 0; i < script.length(); ++i)
      {
         char c = script.charAt(i);

         if(false == isInLiteral)
         {
            ///////////////////////////////////////////////////////////
            // Handling of comments

            // We look backwards
            if(isInLineComment && script.startsWith("\n", i - "\n".length()))
            {
               isInLineComment = false;
            }

            // We look backwards
            if(isInMultiLineComment && script.startsWith(MULTI_LINE_COMMENT_END, i - MULTI_LINE_COMMENT_END.length()))
            {
               isInMultiLineComment = false;
            }


            if(false == isInLineComment && false == isInMultiLineComment)
            {
               // We look forward
               isInMultiLineComment = script.startsWith(MULTI_LINE_COMMENT_BEGIN, i);
               isInLineComment = script.startsWith(_lineCommentBegin, i);

               if(isInMultiLineComment && _removeMultiLineComment)
               {
                  // skip ahead so the cursor is now immediately after the begin comment string
                  i+=MULTI_LINE_COMMENT_BEGIN.length()+1;
               }
            }

            if((isInMultiLineComment && _removeMultiLineComment) || isInLineComment)
            {
               // This is responsible that comments are not in curQuery
               continue;
            }
            //
            ////////////////////////////////////////////////////////////
         }

         curQuery.append(c);

         if ('\'' == c)
         {
            if(false == isInLiteral)
            {
               isInLiteral = true;
            }
            else
            {
               ++literalSepCount;
            }
         }
         else
         {
            if(0 != literalSepCount % 2)
            {
               isInLiteral = false;
            }
            literalSepCount = 0;
         }


         String statementSeparator = AppState.get().getSettingsManager().getSettings().getStatementSeparator();

         int querySepLen =
               getLenOfQuerySepIfAtLastCharOfQuerySep(script, i, statementSeparator, isInLiteral);

         if(-1 < querySepLen && !isInMultiLineComment)
         {
            int newLength = curQuery.length() - querySepLen;
            if(-1 < newLength && curQuery.length() > newLength)
            {
               curQuery.setLength(newLength);

               String newQuery = curQuery.toString().trim();
               if(0 < newQuery.length())
               {
                  _sqls.add(curQuery.toString().trim());
               }
            }
            curQuery.setLength(0);
         }
      }

      String lastQuery = curQuery.toString().trim();
      if(0 < lastQuery.length())
      {
         _sqls.add(lastQuery.trim());
      }

      _iterator = _sqls.iterator();
   }


   public String getFirstSql()
   {
      if (false == _getFirstSqlWasCalled)
      {
         _currentSql = _iterator.next();
         _getFirstSqlWasCalled = true;
      }
      return _currentSql;
   }

   public boolean hasMoreSqls()
   {
      return _iterator.hasNext();
   }

   public String nextSql()
   {
      _currentSql = _iterator.next();
      return _currentSql;
   }


   private int getLenOfQuerySepIfAtLastCharOfQuerySep(String sql, int i, String querySep, boolean inLiteral)
   {
      if(inLiteral)
      {
         return -1;
      }

      char c = sql.charAt(i);

      if(1 == querySep.length() && c == querySep.charAt(0))
      {
         return 1;
      }
      else
      {
         int fromIndex = i - querySep.length();
         if(0 > fromIndex)
         {
            return -1;
         }

         int querySepIndex = sql.indexOf(querySep, fromIndex);

         if(0 > querySepIndex)
         {
            return -1;
         }

         if(Character.isWhitespace(c))
         {
            if(querySepIndex + querySep.length() == i)
            {
               if(0 == querySepIndex)
               {
                  return querySep.length() + 1;
               }
               else if(Character.isWhitespace(sql.charAt(querySepIndex - 1)))
               {
                  return querySep.length() + 2;
               }
            }
         }
         else if(sql.length() -1 == i)
         {
            if(querySepIndex + querySep.length() - 1 == i)
            {
               if(0 == querySepIndex)
               {
                  return querySep.length();
               }
               else if(Character.isWhitespace(sql.charAt(querySepIndex - 1)))
               {
                  return querySep.length() + 1;
               }
            }
         }

         return -1;
      }
   }

   public String getCurrentSql()
   {
      return _currentSql;
   }
}
