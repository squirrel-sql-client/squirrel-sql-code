package net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions;

import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfo;


public class TestCompletionFunction extends CodeCompletionFunction
{
   public String getCompareString()
   {
      return "#gwa";
   }

   public String getCompletionString()
   {
      return "#gwa,<table>";
   }

   public String toString()
   {
      return getCompletionString() + " test";
   }

   public CodeCompletionInfo[] getFunctionResults(String functionSting)
   {
      if(false == functionMatches(functionSting))
      {
         return null;
      }

      return new CodeCompletionInfo[]{new GenericCodeCompletionInfo("Hallo gwa comp")};
   }
}
