package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsAdapter;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAndAliasParseResult;
import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions.CodeCompletionFunction;
import net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions.InnerJoin;
import net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions.Join;
import net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions.LeftJoin;
import net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions.RightJoin;

import java.util.stream.Stream;


public class CompletionFunctionsModel
{
   CodeCompletionFunction[] _completionFunctions;

   CompletionFunctionsModel(ISession session, IIdentifier sqlEntryPanelIdentifier)
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

      session.getParserEventsProcessor(sqlEntryPanelIdentifier).addParserEventsListener(new ParserEventsAdapter()
      {
         @Override
         public void tableAndAliasParseResultFound(TableAndAliasParseResult tableAndAliasParseResult)
         {
            onTableAndAliasParseResultFound(tableAndAliasParseResult);
         }
      });

   }

   private void onTableAndAliasParseResultFound(TableAndAliasParseResult tableAndAliasParseResult)
   {
      Stream.of(_completionFunctions).forEach(cf -> cf.replaceLastTableAndAliasParseResult(tableAndAliasParseResult));
   }

   public CodeCompletionInfo[] getCompletions()
   {
      return _completionFunctions;
   }

   public CompletionCandidates getCompletionCandidates(String textTillCaret)
   {
      int lastIndexOfLineFeed = textTillCaret.lastIndexOf('\n');
      int lastIndexOfHash = textTillCaret.lastIndexOf('#');

      if(lastIndexOfHash <= lastIndexOfLineFeed)
      {
         return null;
      }

      String functionSting = textTillCaret.substring(lastIndexOfHash);


      for (int i = 0; i < _completionFunctions.length; i++)
      {
         CodeCompletionInfo[] functionResults = _completionFunctions[i].getFunctionResults(functionSting, textTillCaret.length());

         if(null != functionResults)
         {
            return new CompletionCandidates(functionResults, lastIndexOfHash, functionSting);
         }
      }
      return null;
   }
}
