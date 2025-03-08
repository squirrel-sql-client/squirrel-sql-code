package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAliasParseInfo;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAndAliasParseResult;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableParseInfo;
import net.sourceforge.squirrel_sql.plugins.codecompletion.completionfunctions.InnerJoin;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class JoinOnCompletion
{
   private final ISession _session;
   private TableAndAliasParseResult _tableAndAliasParseResult = new TableAndAliasParseResult();

   public JoinOnCompletion(ISession session)
   {
      _session = session;
   }

   public void replaceLastTableAndAliasParseResult(TableAndAliasParseResult tableAndAliasParseResult)
   {
      _tableAndAliasParseResult = tableAndAliasParseResult;
   }

   public List<JoinOnCompletionInfo> getJoinOnClausesCompletionInfos(String textTillCaret)
   {
      List<JoinOnCompletionInfo> ret = new ArrayList<>();

      JoinLookupResult joinLookupResult = getJoinLookupResult(textTillCaret);
      if(joinLookupResult.isEndsWithJoinKeyword())
      {
         for(TableParseInfo tableParseInfo1 : _tableAndAliasParseResult.getTableParseInfosReadOnly())
         {
            for(TableParseInfo tableParseInfo2 : _tableAndAliasParseResult.getTableParseInfosReadOnly())
            {
               if(    tableParseInfo1 == tableParseInfo2
                   || false == isInfoInStatementOfCaretPos(tableParseInfo1, textTillCaret.length())
                   || false == isInfoInStatementOfCaretPos(tableParseInfo2, textTillCaret.length())
               )
               {
                  continue;
               }

               CodeCompletionInfo[] functionResults = new InnerJoin(_session).getFunctionResults("#i," + tableParseInfo1.getTableName() + "," + tableParseInfo2.getTableName() + ",");

               for(CodeCompletionInfo functionResult : functionResults)
               {
                  if(false == functionResult.getCompletionString().trim().endsWith(".")) // If it ends with . it's just a dummy result.
                  {
                     ret.add(new JoinOnCompletionInfo(functionResult.getCompletionString().substring("INNER JOIN ".length()).trim()));
                  }
               }
            }
         }
      }
      else if(null != joinLookupResult.getTableAliasParseInfo())
      {
         // TODO later
      }
      else if(null != joinLookupResult.getTableParseInfo())
      {
         for(TableParseInfo tableParseInfo : _tableAndAliasParseResult.getTableParseInfosReadOnly())
         {
            if(tableParseInfo != joinLookupResult.getTableParseInfo() && isInfoInStatementOfCaretPos(tableParseInfo, textTillCaret.length()))
            {
               CodeCompletionInfo[] functionResults = new InnerJoin(_session).getFunctionResults("#i," + tableParseInfo.getTableName() + "," + joinLookupResult.getTableParseInfo().getTableName() + ",");

               if(null == functionResults)
               {
                  continue;
               }

               for(CodeCompletionInfo functionResult : functionResults)
               {
                  if(false == functionResult.getCompletionString().trim().endsWith("."))
                  {
                     ret.add(new JoinOnCompletionInfo(functionResultToCompletionStringStartingAtOn(functionResult)));
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

   private boolean isInfoInStatementOfCaretPos(TableParseInfo tableParseInfo, int caretPos)
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
