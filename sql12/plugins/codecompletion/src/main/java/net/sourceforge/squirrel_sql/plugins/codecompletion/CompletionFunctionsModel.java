package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions.*;
import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;


public class CompletionFunctionsModel
{
   CodeCompletionFunction[] _completionFunctions;

   CompletionFunctionsModel(ISession session)
   {

      _completionFunctions =
         new CodeCompletionFunction[]
         {
            //new TestCompletionFunction(),
            new Join(session),
            new InnerJoin(session),
            new LeftJoin(session),
            new RightJoin(session)
         };
   }

   public CodeCompletionInfo[] getCompletions()
   {
      return _completionFunctions;
   }

   public CompletionCandidates getCompletionCandidates(String textTillCarret)
   {
      int lastIndexOfLineFeed = textTillCarret.lastIndexOf('\n');
      int lastIndexOfHash = textTillCarret.lastIndexOf('#');

      if(lastIndexOfHash <= lastIndexOfLineFeed)
      {
         return null;
      }

      String functionSting = textTillCarret.substring(lastIndexOfHash);


      for (int i = 0; i < _completionFunctions.length; i++)
      {
         CodeCompletionInfo[] functionResults = _completionFunctions[i].getFunctionResults(functionSting);

         if(null != functionResults)
         {
            return new CompletionCandidates(functionResults, lastIndexOfHash, functionSting);
         }
      }
      return null;
   }
}
