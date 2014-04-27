package org.squirrelsql.session.sql.syntax;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenTypes;

public class TokenToCssStyleMapper
{
   public static String getTokenStyle(Token token)
   {
      switch (token.getType())
      {
         case TokenTypes.COMMENT_EOL:
         case TokenTypes.COMMENT_MULTILINE:
         case TokenTypes.COMMENT_DOCUMENTATION:
         case TokenTypes.COMMENT_KEYWORD:
         case TokenTypes.COMMENT_MARKUP:
            return "comment";

         case TokenTypes.RESERVED_WORD:
         case TokenTypes.RESERVED_WORD_2:
            return "keyword";

         case TokenTypes.LITERAL_BOOLEAN:
         case TokenTypes.LITERAL_NUMBER_DECIMAL_INT:
         case TokenTypes.LITERAL_NUMBER_FLOAT:
         case TokenTypes.LITERAL_NUMBER_HEXADECIMAL:
         case TokenTypes.LITERAL_STRING_DOUBLE_QUOTE:
         case TokenTypes.LITERAL_CHAR:
         case TokenTypes.LITERAL_BACKQUOTE:
            return "literal";

         case Token.ERROR_IDENTIFIER:
            return "error";

         default:

            if (SquirrelTokenMarker.TOKEN_IDENTIFIER_TABLE == token.getType())
            {
               return "table";
            }
            else if (SquirrelTokenMarker.TOKEN_IDENTIFIER_COLUMN == token.getType())
            {
               return "column";
            }
            else if (SquirrelTokenMarker.TOKEN_IDENTIFIER_FUNCTION == token.getType())
            {
               return "function";
            }
            else if (SquirrelTokenMarker.TOKEN_IDENTIFIER_STATEMENT_SEPARATOR == token.getType())
            {
               return "separator";
            }
            else
            {
               return "sqldefault";
            }

      }
   }
}
