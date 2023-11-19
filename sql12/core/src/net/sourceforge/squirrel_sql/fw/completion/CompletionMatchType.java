package net.sourceforge.squirrel_sql.fw.completion;

import org.apache.commons.lang3.StringUtils;

public enum CompletionMatchType
{
   DEFAULT,
   CAMEL_CASE,
   CONTAINS;

   public boolean match(String testString, String dbObjectName)
   {
      switch (this)
      {
         case CAMEL_CASE:
            return CamelCaseMatcher.matchesCamelCase(testString, dbObjectName);
         case CONTAINS:
            return StringUtils.containsIgnoreCase(dbObjectName, testString);
         default:
            return false;
      }
   }
}
