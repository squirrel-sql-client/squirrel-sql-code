package net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAndAliasParseResult;
import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfo;


public abstract class CodeCompletionFunction extends CodeCompletionInfo
{
   public abstract CodeCompletionInfo[] getFunctionResults(String functionSting, int caretPos);

   protected boolean functionMatches(String functionSting)
   {
      return functionSting.toUpperCase().startsWith(getCompareString().toUpperCase());
   }

   public abstract void replaceLastTableAndAliasParseResult(TableAndAliasParseResult tableAndAliasParseResult);
}
