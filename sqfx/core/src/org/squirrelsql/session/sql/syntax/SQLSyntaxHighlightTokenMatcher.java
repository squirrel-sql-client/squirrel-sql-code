package org.squirrelsql.session.sql.syntax;

import javafx.beans.value.ObservableObjectValue;
import org.squirrelsql.session.schemainfo.SchemaCache;

public class SQLSyntaxHighlightTokenMatcher implements ISyntaxHighlightTokenMatcher
{
   private final ObservableObjectValue<SchemaCache> _schemaCacheValue;

   public SQLSyntaxHighlightTokenMatcher(ObservableObjectValue<SchemaCache> schemaCacheValue)
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
