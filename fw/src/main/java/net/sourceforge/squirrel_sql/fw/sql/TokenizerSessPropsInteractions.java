package net.sourceforge.squirrel_sql.fw.sql;

public class TokenizerSessPropsInteractions
{
   boolean _tokenizerDefinesStatementSeparator = false;
   boolean _tokenizerDefinesStartOfLineComment = false;
   boolean _tokenizerDefinesRemoveMultiLineComment = false;

   public boolean isTokenizerDefinesStatementSeparator()
   {
      return _tokenizerDefinesStatementSeparator;
   }

   public void setTokenizerDefinesStatementSeparator(boolean tokenizerDefinesStatementSeparator)
   {
      _tokenizerDefinesStatementSeparator = tokenizerDefinesStatementSeparator;
   }

   public boolean isTokenizerDefinesStartOfLineComment()
   {
      return _tokenizerDefinesStartOfLineComment;
   }

   public void setTokenizerDefinesStartOfLineComment(boolean tokenizerDefinesStartOfLineComment)
   {
      _tokenizerDefinesStartOfLineComment = tokenizerDefinesStartOfLineComment;
   }

   public boolean isTokenizerDefinesRemoveMultiLineComment()
   {
      return _tokenizerDefinesRemoveMultiLineComment;
   }

   public void setTokenizerDefinesRemoveMultiLineComment(boolean tokenizerDefinesRemoveMultiLineComment)
   {
      _tokenizerDefinesRemoveMultiLineComment = tokenizerDefinesRemoveMultiLineComment;
   }
}
