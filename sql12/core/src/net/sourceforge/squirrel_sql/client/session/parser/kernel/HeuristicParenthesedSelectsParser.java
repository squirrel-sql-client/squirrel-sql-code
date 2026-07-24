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
      // TODO
      return new ArrayList<>();
   }

   private static List<ParenthesedSelectInfo> findForSubSelects(StatementBounds statementBounds, ISession session, ArrayList<ErrorInfo> errorInfosBuffer)
   {
      List<ParenthesedSelectInfo> ret = new ArrayList<>();

      List<TableColumnInfo> columns = new ArrayList<>();

      TokenHistory tokenHistory = new TokenHistory();
      HeuristicTokenParser tokenParser = new HeuristicTokenParser(statementBounds);

      int[] i = new int[]{0};

      boolean waitingForAliasOfParanthesedSelect = false;
      boolean inParanthesedSelectClause = false;
      int paranthesedSelectBracketCount = 0;

      String token = null;

      while(i[0] < statementBounds.getStatement().length())
      {
         tokenHistory.addPrevious(token);
         token = tokenParser.nextToken(i, statementBounds.getStatement());

         if(StringUtils.equalsIgnoreCase(token, "(") && isPreviousTokenAllowedSubSelect(tokenHistory))
         {
            paranthesedSelectBracketCount = 1;
         }
         else if(0 < paranthesedSelectBracketCount)
         {
            if(StringUtils.equalsIgnoreCase("(", token))
            {
               ++paranthesedSelectBracketCount;
            }
            else if(StringUtils.equalsIgnoreCase(")", token))
            {
               --paranthesedSelectBracketCount;

               if(0 == paranthesedSelectBracketCount)
               {
                  waitingForAliasOfParanthesedSelect = true;
               }
            }
            else if(1 == paranthesedSelectBracketCount)
            {
               // We do not support nested inner SELECTs
               if(StringUtils.equalsIgnoreCase("SELECT", token))
               {
                  inParanthesedSelectClause = true;
               }
               else if(StringUtils.equalsIgnoreCase("FROM", token))
               {
                  if(inParanthesedSelectClause)
                  {
                     maybeAddColumn(session, tokenHistory, columns);
                  }
                  inParanthesedSelectClause = false;
               }
               else if(inParanthesedSelectClause && StringUtils.equalsIgnoreCase(",", token))
               {
                  maybeAddColumn(session, tokenHistory, columns);
               }
            }
            else
            {
               // Fallback for FROM above
               inParanthesedSelectClause = false;
            }
         }
         else if(waitingForAliasOfParanthesedSelect)
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
                  ret.add(new ParenthesedSelectInfo(statementBounds, errorInfosBuffer, token, new ArrayList<>(columns)));
                  columns.clear();
               }
               waitingForAliasOfParanthesedSelect = false;
            }
         }
      }

      return ret;
   }

   private static boolean isPreviousTokenAllowedSubSelect(TokenHistory tokenHistory)
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
