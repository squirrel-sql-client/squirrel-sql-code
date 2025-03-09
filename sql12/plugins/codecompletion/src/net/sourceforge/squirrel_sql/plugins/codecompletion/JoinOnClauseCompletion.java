package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.JoinOnClauseParseInfo;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAliasParseInfo;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAndAliasParseResult;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableParseInfo;
import net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions.InnerJoin;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class JoinOnClauseCompletion
{
   private final ISession _session;
   private TableAndAliasParseResult _tableAndAliasParseResult = new TableAndAliasParseResult();

   public JoinOnClauseCompletion(ISession session)
   {
      _session = session;
   }

   public void replaceLastTableAndAliasParseResult(TableAndAliasParseResult tableAndAliasParseResult)
   {
      _tableAndAliasParseResult = tableAndAliasParseResult;
   }

   public List<JoinOnCauseCompletionInfo> getJoinOnClausesCompletionInfos(String textTillCaret)
   {
      List<JoinOnCauseCompletionInfo> ret = new ArrayList<>();

      JoinLookupResult joinLookupResult = getJoinLookupResult(textTillCaret);
      if(joinLookupResult.isEndsWithJoinKeyword())
      {
         // Appears to be of no real use because
         // - probably joins between existing tables are already there
         // - to display all foreign keys the tables in the statement have seems to unspecific
      }
      else if(null != joinLookupResult.getJoinOnClauseParseInfo())
      {
         for(JoinOnClauseParseInfo joinOnClauseParseInfo : _tableAndAliasParseResult.getAllJoinOnClauseParseInfosReadOnly())
         {
            if(joinOnClauseParseInfo != joinLookupResult.getJoinOnClauseParseInfo() && isInfoInStatementOfCaretPos(joinOnClauseParseInfo, textTillCaret.length()))
            {
               String completionFunctionCommand = "#i," + joinOnClauseParseInfo.getTableOrAliasName() + "," + joinLookupResult.getJoinOnClauseParseInfo().getTableOrAliasName() + ",";

               InnerJoin innerJoinCompletionFunction = new InnerJoin(_session);
               innerJoinCompletionFunction.replaceLastTableAndAliasParseResult(_tableAndAliasParseResult);

               CodeCompletionInfo[] functionResults = innerJoinCompletionFunction.getFunctionResults(completionFunctionCommand, textTillCaret.length());

               if(null == functionResults)
               {
                  continue;
               }

               for(CodeCompletionInfo functionResult : functionResults)
               {
                  if(false == functionResult.getCompletionString().trim().endsWith(".")) // Leave out dummy completions for now
                  {
                     ret.add(new JoinOnCauseCompletionInfo(functionResultToCompletionStringStartingAtOn(functionResult)));
                  }
               }
            }
         }
      }

      return ret;
   }

   private static String functionResultToCompletionStringStartingAtOn(CodeCompletionInfo functionResult)
   {
      String functionCompletionString = functionResult.getCompletionString();
      return functionCompletionString.substring(StringUtils.indexOfIgnoreCase(functionCompletionString, " ON ")).trim();
   }

   private boolean isInfoInStatementOfCaretPos(JoinOnClauseParseInfo tableParseInfo, int caretPos)
   {
      return tableParseInfo.getStatBegin() <= caretPos && caretPos <= tableParseInfo.getStatEnd();
   }

   private JoinLookupResult getJoinLookupResult(String textTillCaret)
   {
      int[] nextStartPosRef = new int[]{textTillCaret.length() - 1};

      // Completion for JOIN keyword
      String firstToken = getNextToken(textTillCaret, nextStartPosRef);
      if(StringUtils.equalsIgnoreCase("JOIN", firstToken))
      {
         return JoinLookupResult.ofEndsWithJoinKeyword();
      }

      // Completion for table alias
      TableAliasParseInfo tableAliasParseInfo = _tableAndAliasParseResult.getAliasInStatementAt(firstToken, textTillCaret.length());
      if(null != tableAliasParseInfo)
      {
         String token = getNextToken(textTillCaret, nextStartPosRef);

         if(StringUtils.equalsIgnoreCase("AS", token))
         {
            token = getNextToken(textTillCaret, nextStartPosRef);
            if(StringUtils.equalsIgnoreCase(token, tableAliasParseInfo.getTableName()))
            {
               token = getNextToken(textTillCaret, nextStartPosRef);
               if(StringUtils.equalsIgnoreCase("JOIN", token))
               {
                  return JoinLookupResult.ofTableAlias(tableAliasParseInfo);
               }
            }
            return JoinLookupResult.empty();
         }
         else if(StringUtils.equalsIgnoreCase(token, tableAliasParseInfo.getTableName())) // no AS
         {
            token = getNextToken(textTillCaret, nextStartPosRef);
            if(StringUtils.endsWithIgnoreCase("JOIN", token))
            {
               return JoinLookupResult.ofTableAlias(tableAliasParseInfo);
            }
            return JoinLookupResult.empty();
         }
         return JoinLookupResult.empty();
      }

      // Completion for table
      TableParseInfo tableParseInfo = _tableAndAliasParseResult.getTableInStatementAt(firstToken, textTillCaret.length());
      if(null != tableParseInfo)
      {
         String token = getNextToken(textTillCaret, nextStartPosRef);
         if(StringUtils.equalsIgnoreCase("JOIN", token))
         {
            return JoinLookupResult.ofTable(tableParseInfo);
         }
         return JoinLookupResult.empty();
      }

      return JoinLookupResult.empty();
   }

   private static String getNextToken(String textTillCaret, int[] nextStartPos)
   {
      StringBuilder token = new StringBuilder();

      for(int i = nextStartPos[0] ; i >= 0 ; i--)
      {
         char c = textTillCaret.charAt(i);
         if(Character.isWhitespace(c))
         {
            if(0 < token.length())
            {
               nextStartPos[0] = i - 1;
               return token.reverse().toString();
            }
            else
            {
               continue;
            }
         }
         token.append(c);
      }

      nextStartPos[0] = -1;

      if(0 < token.length())
      {
         return token.reverse().toString();
      }

      return null;
   }

   public static void main(String[] args)
   {
      String s = "INNER JOIN  \narticles\n art  ";
      int[] startPos = {s.length() - 1};

      System.out.println(getNextToken(s, startPos));
      System.out.println(getNextToken(s, startPos));
      System.out.println(getNextToken(s, startPos));

      s = "   ";
      startPos[0] = s.length() - 1;
      System.out.println(getNextToken(s, startPos));

      s = "";
      startPos[0] = s.length() - 1;
      System.out.println(getNextToken(s, startPos));

   }
}
