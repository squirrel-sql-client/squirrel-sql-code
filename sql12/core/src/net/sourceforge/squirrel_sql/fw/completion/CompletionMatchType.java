package net.sourceforge.squirrel_sql.fw.completion;

import net.sourceforge.squirrel_sql.plugins.codecompletion.prefs.CodeCompletionPreferences;
import org.apache.commons.lang3.StringUtils;

public enum CompletionMatchType
{
   DEFAULT,
   CAMEL_CASE,
   CONTAINS;

   public static CompletionMatchType of(boolean useCompletionPrefs, CodeCompletionPreferences prefs)
   {
      if(false == useCompletionPrefs)
      {
         return DEFAULT;
      }

      if(prefs.isMatchCamelCase())
      {
         return CAMEL_CASE;
      }
      else if (prefs.isMatchContains())
      {
         return CONTAINS;
      }

      return DEFAULT;

   }

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
