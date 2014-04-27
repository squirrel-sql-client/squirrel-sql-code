package org.squirrelsql.session.sql.syntax;

public class SQLSyntaxHighlightTokenMatcher implements ISyntaxHighlightTokenMatcher
{
   @Override
   public boolean isError(int offset, int len)
   {
      return false;
   }

   @Override
   public boolean isTable(char[] buffer, int offset, int len)
   {
      return false;
   }

   @Override
   public void removeSQLTokenListener(SQLTokenListener tl)
   {

   }

   @Override
   public void addSQLTokenListener(SQLTokenListener tl)
   {

   }

   @Override
   public boolean isFunction(char[] buffer, int offset, int len)
   {
      return false;
   }

   @Override
   public boolean isDataType(char[] buffer, int offset, int len)
   {
      return false;
   }

   @Override
   public boolean isStatementSeparator(char[] buffer, int offset, int len)
   {
      return false;
   }

   @Override
   public boolean isColumn(char[] buffer, int offset, int len)
   {
      return false;
   }

   @Override
   public boolean isKeyword(char[] buffer, int offset, int len)
   {
      return false;
   }

}
