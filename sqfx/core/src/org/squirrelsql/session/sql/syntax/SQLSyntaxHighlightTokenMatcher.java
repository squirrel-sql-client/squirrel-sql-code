package org.squirrelsql.session.sql.syntax;

import org.squirrelsql.session.schemainfo.SchemaCache;

public class SQLSyntaxHighlightTokenMatcher implements ISyntaxHighlightTokenMatcher
{
   private final SchemaCache _schemaCache;

   public SQLSyntaxHighlightTokenMatcher(SchemaCache schemaCache)
   {
      _schemaCache = schemaCache;
   }

   @Override
   public boolean isError(int offset, int len)
   {
      return false;
   }

   @Override
   public boolean isTable(char[] buffer, int offset, int len)
   {
      return _schemaCache.isTable(buffer, offset, len);
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
      return _schemaCache.isProcedure(buffer, offset, len);
   }

   @Override
   public boolean isStatementSeparator(char[] buffer, int offset, int len)
   {
      return false;
   }

   @Override
   public boolean isColumn(char[] buffer, int offset, int len)
   {
      return _schemaCache.isColumn(buffer, offset, len);
   }

   @Override
   public boolean isKeyword(char[] buffer, int offset, int len)
   {
      return _schemaCache.isKeyword(buffer, offset, len);
   }

}
