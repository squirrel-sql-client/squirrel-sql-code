package net.sourceforge.squirrel_sql.plugins.codecompletion;

public class CodeCompletionFunctionInfo extends CodeCompletionInfo
{
   private String _function;

   public CodeCompletionFunctionInfo(String function)
   {
      _function = function;
   }

   public String getCompletionString()
   {
      return _function;
   }

   public String toString()
   {
      return _function;
   }
}
