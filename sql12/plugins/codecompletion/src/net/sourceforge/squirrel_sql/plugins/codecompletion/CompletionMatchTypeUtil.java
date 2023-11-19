package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.fw.completion.CompletionMatchType;
import net.sourceforge.squirrel_sql.plugins.codecompletion.prefs.CodeCompletionPreferences;

public class CompletionMatchTypeUtil
{
   public static CompletionMatchType matchTypeOf(boolean useCompletionPrefs, CodeCompletionPreferences prefs)
   {
      if (false == useCompletionPrefs)
      {
         return CompletionMatchType.DEFAULT;
      }

      if (prefs.isMatchCamelCase())
      {
         return CompletionMatchType.CAMEL_CASE;
      }
      else if (prefs.isMatchContains())
      {
         return CompletionMatchType.CONTAINS;
      }

      return CompletionMatchType.DEFAULT;

   }
}
