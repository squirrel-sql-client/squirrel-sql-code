package org.squirrelsql.session.sql.syntax;

import org.fife.ui.rsyntaxtextarea.Token;

import java.util.List;

public class TokenLine
{
   private final List<Token> _tokens;
   private final String _line;
   private final int _formerInitialTokenType;

   public TokenLine(List<Token> tokens, String line, int formerInitialTokenType)
   {
      _tokens = tokens;
      _line = line;
      _formerInitialTokenType = formerInitialTokenType;
   }

   public List<Token> getTokens()
   {
      return _tokens;
   }

   public int getNextInitialTokenType()
   {
      if(0 == _tokens.size())
      {
         return _formerInitialTokenType;
      }

      if(_line.endsWith("'") && false == _line.endsWith("''"))
      {
         return SquirrelTokenMakerBase.YYINITIAL;
      }

      return _tokens.get(_tokens.size() - 1).getType();
   }

   public int getLineLength()
   {
      return _line.length() + SyntaxConstants.CODE_AREA_LINE_SEP.length();
   }
}
