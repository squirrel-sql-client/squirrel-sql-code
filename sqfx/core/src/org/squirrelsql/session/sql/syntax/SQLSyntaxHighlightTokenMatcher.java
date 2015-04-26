package org.squirrelsql.session.sql.syntax;

import org.squirrelsql.session.schemainfo.SchemaCacheProperty;

public class SQLSyntaxHighlightTokenMatcher implements ISyntaxHighlightTokenMatcher
{
   private final SchemaCacheProperty _schemaCacheValue;

   public SQLSyntaxHighlightTokenMatcher(SchemaCacheProperty schemaCacheValue)
   {
      _schemaCacheValue = schemaCacheValue;
   }

   @Override
   public boolean isError(int offset, int len)
   {
      return false;
   }

   @Override
   public boolean isTable(char[] buffer, int offset, int len)
   {
      return 0 < _schemaCacheValue.get().getTables(buffer, offset, len).size();
   }

   @Override
   public boolean isFunction(char[] buffer, int offset, int len)
   {
      return _schemaCacheValue.get().isProcedure(buffer, offset, len);
   }

   @Override
   public boolean isStatementSeparator(char[] buffer, int offset, int len)
   {
      return false;
   }

   @Override
   public boolean isColumn(char[] buffer, int offset, int len)
   {
      return _schemaCacheValue.get().isColumn(buffer, offset, len);
   }

   @Override
   public boolean isKeyword(char[] buffer, int offset, int len)
   {
      return _schemaCacheValue.get().isKeyword(buffer, offset, len);
   }
}
