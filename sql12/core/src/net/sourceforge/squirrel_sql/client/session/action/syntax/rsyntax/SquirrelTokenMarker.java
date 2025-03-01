package net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax;

import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcher;
import org.fife.ui.rsyntaxtextarea.Token;

public class SquirrelTokenMarker extends SquirrelTokenMakerBase
{
   private static int _curTokenArrayIndex =Token.DEFAULT_NUM_TOKEN_TYPES;

   public static final int TOKEN_IDENTIFIER_TABLE = _curTokenArrayIndex++;
   public static final int TOKEN_IDENTIFIER_DATA_TYPE = _curTokenArrayIndex++;
   public static final int TOKEN_IDENTIFIER_COLUMN = _curTokenArrayIndex++;
   public static final int TOKEN_IDENTIFIER_FUNCTION = _curTokenArrayIndex++;
   public static final int TOKEN_IDENTIFIER_STATEMENT_SEPARATOR = _curTokenArrayIndex++;
//   public static final int TOKEN_IDENTIFIER_ERROR = _curTokenArrayIndex++;
//   public static final int TOKEN_IDENTIFIER_SCHEMA = _curTokenArrayIndex++;
//   public static final int TOKEN_IDENTIFIER_CATALOG = _curTokenArrayIndex++;


   private ISyntaxHighlightTokenMatcher _syntaxHighlightTokenMatcher;
   private final CtrlDownHyperlinkHandler _ctrlDownHyperlinkHandler;

   public static int getNumTokenTypes()
   {
      return _curTokenArrayIndex;
   }

   public SquirrelTokenMarker(final SquirrelRSyntaxTextArea squirrelRSyntaxTextArea, ISyntaxHighlightTokenMatcher syntaxHighlightTokenMatcher)
   {
      _syntaxHighlightTokenMatcher = syntaxHighlightTokenMatcher;
      _ctrlDownHyperlinkHandler = new CtrlDownHyperlinkHandler(squirrelRSyntaxTextArea);

   }

   public void addToken(char[] array, int start, int end, int tokenType, int startOffset, boolean hyperlink)
   {
      int len = end + 1 - start;

      out(array, "Bev", start, end, startOffset);

      if(_syntaxHighlightTokenMatcher.isError(startOffset, len))
      {
         //out(array, "Err", start, end, startOffset);

         // Errors must be first.
         super.addToken(array, start, end, Token.ERROR_IDENTIFIER, startOffset, hyperlink);
      }
      else if(Token.IDENTIFIER == tokenType)
      {

         //out(array, "NoErr", start, end, startOffset);

         if(_syntaxHighlightTokenMatcher.isKeyword(array, start, len))
         {
            super.addToken(array, start, end, Token.RESERVED_WORD, startOffset, hyperlink);
         }
         else if(_syntaxHighlightTokenMatcher.isTable(array, start, len))
         {
            super.addToken(array, start, end, TOKEN_IDENTIFIER_TABLE, startOffset, _ctrlDownHyperlinkHandler.isShowTablesAsHyperlink());
         }
         else if(_syntaxHighlightTokenMatcher.isDataType(array, start, len))
         {
            super.addToken(array, start, end, TOKEN_IDENTIFIER_DATA_TYPE, startOffset, hyperlink);
         }
         else if(_syntaxHighlightTokenMatcher.isColumn(array, start, len))
         {
            super.addToken(array, start, end, TOKEN_IDENTIFIER_COLUMN, startOffset, hyperlink);
         }
         else if(_syntaxHighlightTokenMatcher.isFunction(array, start, len))
         {
            super.addToken(array, start, end, TOKEN_IDENTIFIER_FUNCTION, startOffset, hyperlink);
         }
         else if(_syntaxHighlightTokenMatcher.isStatementSeparator(array, start, len))
         {
            super.addToken(array, start, end, TOKEN_IDENTIFIER_STATEMENT_SEPARATOR, startOffset, hyperlink);
         }
         else
         {
            super.addToken(array, start, end, tokenType, startOffset, hyperlink);
         }
      }
      else if(Token.LITERAL_STRING_DOUBLE_QUOTE == tokenType)
      {
         // final CaseInsensitiveString buf = new CaseInsensitiveString();
         // buf.setCharBuffer(array, start, len);
         // final String toString = buf.toString();
         // System.out.println(toString);

         // Tables, columns or function may be surrounded by double quotes.
         // This block is needed for adequate coloring of those.
         // See CaseInsensitiveString.setCharBuffer(char[], int, int, boolean)
         if(_syntaxHighlightTokenMatcher.isTable(array, start, len))
         {
            super.addToken(array, start, end, TOKEN_IDENTIFIER_TABLE, startOffset, _ctrlDownHyperlinkHandler.isShowTablesAsHyperlink());
         }
         else if(_syntaxHighlightTokenMatcher.isColumn(array, start, len))
         {
            super.addToken(array, start, end, TOKEN_IDENTIFIER_COLUMN, startOffset, hyperlink);
         }
         else if(_syntaxHighlightTokenMatcher.isFunction(array, start, len))
         {
            super.addToken(array, start, end, TOKEN_IDENTIFIER_FUNCTION, startOffset, hyperlink);
         }
         else
         {
            super.addToken(array, start, end, tokenType, startOffset, hyperlink);
         }
      }
      else
      {
         super.addToken(array, start, end, tokenType, startOffset, hyperlink);
      }
   }

   private void out(char[] array, final String type, int start, int end, int startOffset)
   {
//      String s = new String(array).substring(start, end+1);
//
//      if("articlesd".equals(s))
//      {
//         System.out.println(type + ": -->" + s + "<-- startOffset=" + startOffset + " start=" + start + " end=" + end);
//      }
   }
}
