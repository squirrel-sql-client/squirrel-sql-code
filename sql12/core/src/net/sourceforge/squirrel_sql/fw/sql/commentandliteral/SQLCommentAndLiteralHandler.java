package net.sourceforge.squirrel_sql.fw.sql.commentandliteral;


/**
 * This code used to be part of {@link net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer#setScriptToTokenize(String)}
 * and was moved here on 3/29/2020 to fix bug #1329.
 *
 * Moving the code was necessary to implement {@link SQLCommentRemover}
 */
public class SQLCommentAndLiteralHandler
{
   private static final String MULTI_LINE_COMMENT_END = "*/";
   private static final String MULTI_LINE_COMMENT_BEGIN = "/*";

   private String _script;
   private String _lineCommentBegin;
   private boolean _removeMultiLineComment;
   private boolean _removeLineComment;

   private boolean _isInLiteral = false;
   private boolean _isInMultiLineComment = false;
   private boolean _isInLineComment = false;
   private int _literalSepCount = 0;

   public SQLCommentAndLiteralHandler(String script, String lineCommentBegin, boolean removeMultiLineComment, boolean removeLineComment)
   {
      _script = script;
      _lineCommentBegin = lineCommentBegin;
      _removeMultiLineComment = removeMultiLineComment;
      _removeLineComment = removeLineComment;
   }

   public NextPositionAction nextPosition(int posInScript)
   {
      if(_script.length() <= posInScript)
      {
         throw new IllegalStateException("Script position out of bounds: " + posInScript + " >= " + _script.length());
      }

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
            && isInBeginningOfMultiLineComment(posInScript) // Treats /*/
         )
         {
            _isInMultiLineComment = false;
         }


         if(false == _isInLineComment && false == _isInMultiLineComment)
         {
            // We look forward
            _isInMultiLineComment = _script.startsWith(MULTI_LINE_COMMENT_BEGIN, posInScript);
            _isInLineComment = _script.startsWith(_lineCommentBegin, posInScript);
         }

         if((_isInMultiLineComment && _removeMultiLineComment) || (_isInLineComment && _removeLineComment))
         {
            // This is responsible that comments are not in curQuery
            // curOriginalQuery.append(c);
            // continue;
            return NextPositionAction.SKIP;
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

      return NextPositionAction.APPEND;
   }

   private boolean isInBeginningOfMultiLineComment(int posInScript)
   {
      final int backwardCount = MULTI_LINE_COMMENT_BEGIN.length() + 1;
      return posInScript >= backwardCount && false == _script.startsWith(MULTI_LINE_COMMENT_BEGIN, posInScript - backwardCount);
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
