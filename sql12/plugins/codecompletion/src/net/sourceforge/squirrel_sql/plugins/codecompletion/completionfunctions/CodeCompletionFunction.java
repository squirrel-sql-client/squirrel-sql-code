package net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions;

import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfo;


public abstract class CodeCompletionFunction extends CodeCompletionInfo
{
   public abstract CodeCompletionInfo[] getFunctionResults(String functionSting);

   protected boolean functionMatches(String functionSting)
   {
      return functionSting.toUpperCase().startsWith(getCompareString().toUpperCase());
   }

}
