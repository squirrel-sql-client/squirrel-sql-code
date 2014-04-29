package org.squirrelsql.session.sql.syntax;

public interface ISyntaxHighlightTokenMatcher
{
   boolean isError(int offset, int len);

   boolean isTable(char[] buffer, int offset, int len);

   boolean isFunction(char[] buffer, int offset, int len);

   boolean isStatementSeparator(char[] buffer, int offset, int len);

   boolean isColumn(char[] buffer, int offset, int len);

   boolean isKeyword(char[] buffer, int offset, int len);
}
