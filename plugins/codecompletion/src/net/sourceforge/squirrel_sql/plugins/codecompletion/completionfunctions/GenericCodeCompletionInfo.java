package net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions;

import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfo;


public class GenericCodeCompletionInfo extends CodeCompletionInfo
{
   private String _completionString;
   private String _toString;

   public GenericCodeCompletionInfo(String completionString)
   {
      _completionString = completionString;
      _toString = _completionString.replace('\n', ' ');
   }

   public String getCompareString()
   {
      return _completionString;
   }

   public String toString()
   {

      return _toString;
   }
}
