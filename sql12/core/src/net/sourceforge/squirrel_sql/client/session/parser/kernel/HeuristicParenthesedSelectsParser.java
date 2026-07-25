package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import org.apache.commons.lang3.StringUtils;

public class HeuristicParenthesedSelectsParser
{
   public static List<ParenthesedSelectInfo> parse(StatementBounds statementBounds, ISession session, ArrayList<ErrorInfo> errorInfosBuffer)
   {
      List<ParenthesedSelectInfo> ret = new ArrayList<>();
      ret.addAll(findForSubSelects(statementBounds, session, errorInfosBuffer));
      ret.addAll(findForWithSelects(statementBounds, session, errorInfosBuffer));

      return ret;
   }

   private static List<ParenthesedSelectInfo> findForWithSelects(StatementBounds statementBounds, ISession session, ArrayList<ErrorInfo> errorInfosBuffer)
   {
      List<ParenthesedSelectInfo> ret = new ArrayList<>();
      List<TableColumnInfo> columns = new ArrayList<>();

      TokenHistory tokenHistory = new TokenHistory();
      HeuristicTokenParser tokenParser = new HeuristicTokenParser(statementBounds);

      int[] i = new int[]{0};
      String token = null;

      boolean inWithClause = false;
      int parenthesedWithSelectBracketCount = 0;
      boolean inParanthesedWithSelectClause = false;
      String currentWithAlias = null;


      while(i[0] < statementBounds.getStatement().length())
      {
         tokenHistory.addPrevious(token);
         token = tokenParser.nextToken(i, statementBounds.getStatement());

         if(tokenHistory.isEmpty() && StringUtils.equalsIgnoreCase(token, "WITH"))
         {
            // First token is WITH --> WITH clause is started
            inWithClause = true;
         }
         else if(inWithClause)
         {
            boolean parenthesedWithSelectBracketCountJustChangedFromZeroToOne = false;

            if(StringUtils.equalsIgnoreCase(token, "("))
            {
               ++parenthesedWithSelectBracketCount;
               parenthesedWithSelectBracketCountJustChangedFromZeroToOne = (1 == parenthesedWithSelectBracketCount);
            }
            else if(StringUtils.equalsIgnoreCase(token, ")"))
            {
               --parenthesedWithSelectBracketCount;
            }

            if(0 == parenthesedWithSelectBracketCount && StringUtils.equalsIgnoreCase(token, "SELECT"))
            {
               inWithClause = false;
               inParanthesedWithSelectClause = false;
            }

            if(parenthesedWithSelectBracketCountJustChangedFromZeroToOne)
            {
               String withAliasCandidate;
               if(StringUtils.equalsIgnoreCase(tokenHistory.previous(0), "AS"))
               {
                  withAliasCandidate = tokenHistory.previous(1);
               }
               else
               {
                  withAliasCandidate = tokenHistory.previous(0);
               }

               if(      StringUtils.isNotBlank(withAliasCandidate)
                     && Character.isJavaIdentifierStart(withAliasCandidate.charAt(0))
                     && Character.isJavaIdentifierStart(withAliasCandidate.charAt(withAliasCandidate.length() - 1))
                     && false == session.getSchemaInfo().isKeyword(token)
                     && columns.isEmpty())
               {
                  currentWithAlias = withAliasCandidate;
               }
            }
         }

         if(inWithClause && 1 == parenthesedWithSelectBracketCount)
         {
            // We do not support nested inner SELECTs
            if(StringUtils.equalsIgnoreCase("SELECT", token))
            {
               inParanthesedWithSelectClause = true;
            }
            else if(StringUtils.equalsIgnoreCase("FROM", token))
            {
               if(inParanthesedWithSelectClause)
               {
                  maybeAddColumn(session, tokenHistory, columns);
               }
               inParanthesedWithSelectClause = false;

               if(false == StringUtils.isBlank(currentWithAlias))
               {
                  ret.add(new ParenthesedSelectInfo(statementBounds, errorInfosBuffer, currentWithAlias, columns));
                  columns = new ArrayList<>();
               }
            }
            else if(inParanthesedWithSelectClause && StringUtils.equalsIgnoreCase(",", token))
            {
               maybeAddColumn(session, tokenHistory, columns);
            }
         }
      }

      return ret;
   }

   private static List<ParenthesedSelectInfo> findForSubSelects(StatementBounds statementBounds, ISession session, ArrayList<ErrorInfo> errorInfosBuffer)
   {
      List<ParenthesedSelectInfo> ret = new ArrayList<>();

      List<TableColumnInfo> columns = new ArrayList<>();

      TokenHistory tokenHistory = new TokenHistory();
      HeuristicTokenParser tokenParser = new HeuristicTokenParser(statementBounds);

      int[] i = new int[]{0};

      boolean waitingForAliasOfParanthesedSubSelect = false;
      boolean inParanthesedSubSelectClause = false;
      int paranthesedSubSelectBracketCount = 0;

      String token = null;

      while(i[0] < statementBounds.getStatement().length())
      {
         tokenHistory.addPrevious(token);
         token = tokenParser.nextToken(i, statementBounds.getStatement());

         if(StringUtils.equalsIgnoreCase(token, "(") && doesPreviousTokenAllowSubSelect(tokenHistory))
         {
            paranthesedSubSelectBracketCount = 1;
         }
         else if(0 < paranthesedSubSelectBracketCount)
         {
            if(StringUtils.equalsIgnoreCase("(", token))
            {
               ++paranthesedSubSelectBracketCount;
            }
            else if(StringUtils.equalsIgnoreCase(")", token))
            {
               --paranthesedSubSelectBracketCount;

               if(0 == paranthesedSubSelectBracketCount)
               {
                  waitingForAliasOfParanthesedSubSelect = true;
               }
            }
            else if(1 == paranthesedSubSelectBracketCount)
            {
               // We do not support nested inner SELECTs
               if(StringUtils.equalsIgnoreCase("SELECT", token))
               {
                  inParanthesedSubSelectClause = true;
               }
               else if(StringUtils.equalsIgnoreCase("FROM", token))
               {
                  if(inParanthesedSubSelectClause)
                  {
                     maybeAddColumn(session, tokenHistory, columns);
                  }
                  inParanthesedSubSelectClause = false;
               }
               else if(inParanthesedSubSelectClause && StringUtils.equalsIgnoreCase(",", token))
               {
                  maybeAddColumn(session, tokenHistory, columns);
               }
            }
            else
            {
               // Fallback for FROM above
               inParanthesedSubSelectClause = false;
            }
         }
         else if(waitingForAliasOfParanthesedSubSelect)
         {
            if(StringUtils.equalsIgnoreCase(token, "AS"))
            {
               // continueWaiting
            }
            else
            {
               if(    Character.isJavaIdentifierStart(token.charAt(0))
                   && false == session.getSchemaInfo().isKeyword(token)
                   && false == columns.isEmpty())
               {
                  ret.add(new ParenthesedSelectInfo(statementBounds, errorInfosBuffer, token, columns));
                  columns = new ArrayList<>();
               }
               waitingForAliasOfParanthesedSubSelect = false;
            }
         }
      }

      return ret;
   }

   private static boolean doesPreviousTokenAllowSubSelect(TokenHistory tokenHistory)
   {
      return StringUtils.equalsIgnoreCase(tokenHistory.previous(0), "JOIN") || StringUtils.equalsIgnoreCase(tokenHistory.previous(0), "FROM");
   }

   private static void maybeAddColumn(ISession session, TokenHistory tokenHistory, List<TableColumnInfo> columns)
   {
      String previousToken = tokenHistory.previous(0);
      if(   Character.isJavaIdentifierStart(previousToken.charAt(0))
         && Character.isJavaIdentifierStart(previousToken.charAt(previousToken.length() - 1))
         && false == session.getSchemaInfo().isKeyword(previousToken))
      {
         columns.add(ParserUtil.createTableColumnInfoFromName(session, previousToken, 1 + columns.size()));
      }
   }
}
