package net.sourceforge.squirrel_sql.fw.sql.commentandliteral;

public class SQLCommentAndLiteralHandler
{
   private static final String MULTI_LINE_COMMENT_END = "*/";
   private static final String MULTI_LINE_COMMENT_BEGIN = "/*";

   private String _script;
   private String _lineCommentBegin;
   private boolean _removeMultiLineComment;

   private boolean _isInLiteral = false;
   private boolean _isInMultiLineComment = false;
   private boolean _isInLineComment = false;
   private int _literalSepCount = 0;

   public SQLCommentAndLiteralHandler(String script, String lineCommentBegin, boolean removeMultiLineComment)
   {
      _script = script;
      _lineCommentBegin = lineCommentBegin;
      _removeMultiLineComment = removeMultiLineComment;
   }

   public NextPositionResult nextPosition(int posInScript)
   {
      if(_script.length() <= posInScript)
      {
         throw new IllegalStateException("Script position out of bounds: " + posInScript + " >= " + _script.length());
      }

      NextPositionResult ret = new NextPositionResult(posInScript);

      char c = _script.charAt(posInScript);

      if(false == _isInLiteral)
      {
         ///////////////////////////////////////////////////////////
         // Handling of comments

         // We look backwards
         if(_isInLineComment && _script.startsWith("\n", posInScript - "\n".length()))
         {
            _isInLineComment = false;
         }

         // We look backwards
         if(   _isInMultiLineComment
            && _script.startsWith(MULTI_LINE_COMMENT_END, posInScript - MULTI_LINE_COMMENT_END.length())
            && ( posInScript >= 3 && false == _script.startsWith(MULTI_LINE_COMMENT_BEGIN, posInScript - 3) ) // Treats /*/
         )
         {
            _isInMultiLineComment = false;
         }


         if(false == _isInLineComment && false == _isInMultiLineComment)
         {
            // We look forward
            _isInMultiLineComment = _script.startsWith(MULTI_LINE_COMMENT_BEGIN, posInScript);
            _isInLineComment = _script.startsWith(_lineCommentBegin, posInScript);

//            if(_isInMultiLineComment && _removeMultiLineComment)
//            {
//               // NOTE: THIS CURRENTLY BREAKS MULTILINE-COMMENTS IN QueryHolder._originalQuery AND THUS IN SQL-HISTORY.
//               // E.G.: "/*My Multiline\nArticles\n*/\nSELECT * FROM articles"
//               // IT ALREADY DID BEFORE FIXING BUG #1329. IT SHOULD BE FIXED.
//
//               // skip ahead so the cursor is now immediately after the begin comment string
//               ret.setNextPosition(posInScript + MULTI_LINE_COMMENT_BEGIN.length() + 1);
//            }
         }

         if((_isInMultiLineComment && _removeMultiLineComment) || _isInLineComment)
         {
            // This is responsible that comments are not in curQuery
            // curOriginalQuery.append(c);
            // continue;
            return ret.setNextPositionAction(NextPositionAction.SKIP);
         }
         //
         ////////////////////////////////////////////////////////////
      }

      // curQuery.append(c);
      // curOriginalQuery.append(c);

      if ('\'' == c)
      {
         if(false == _isInLiteral)
         {
            _isInLiteral = true;
         }
         else
         {
            ++_literalSepCount;
         }
      }
      else
      {
         if(0 != _literalSepCount % 2)
         {
            _isInLiteral = false;
         }
         _literalSepCount = 0;
      }

      return ret.setNextPositionAction(NextPositionAction.APPEND);
   }


   public boolean isInLiteral()
   {
      return _isInLiteral;
   }

   public boolean isInMultiLineComment()
   {
      return _isInMultiLineComment;
   }

   public boolean isInLineComment()
   {
      return _isInLineComment;
   }
}
