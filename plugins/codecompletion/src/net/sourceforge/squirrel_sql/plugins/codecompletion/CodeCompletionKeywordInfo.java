package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfo;

public class CodeCompletionKeywordInfo extends CodeCompletionInfo
{
   private String _keyword;

   public CodeCompletionKeywordInfo(String keyword)
   {
      _keyword = keyword;
   }

   public String getCompletionString()
   {
      return _keyword;
   }

   public String toString()
   {
      return _keyword;
   }
}
