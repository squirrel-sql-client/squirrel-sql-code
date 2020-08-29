package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableQualifier;

import java.util.ArrayList;
import java.util.List;

/**
 * JSQLParser stops providing table alias information as soon as it runs into an SQL error.
 * When JSQLParser runs into an SQL error, this class provides table alias information in a heuristic way.
 */
public class HeuristicSQLAliasParser
{
   private StringBuilder _token = new StringBuilder();

   private SqlCommentHelper _sqlCommentHelper;
   private int literalDelimsCount = 0;

   public List<TableAliasInfo> parse(StatementBounds statementBounds, SchemaInfo schemaInfo)
   {
      _sqlCommentHelper = new SqlCommentHelper(statementBounds.getStatement());

      ArrayList<TableAliasInfo> ret = new ArrayList<>();

      int[] i = new int[]{0};

      boolean lastTokenAllowedTableAliasDefinition = false;
      String currentAliasableTable = null;

      while(i[0] < statementBounds.getStatement().length())
      {
         String token = nextToken(i, statementBounds.getStatement());

         if(0 == token.length())
         {
            lastTokenAllowedTableAliasDefinition = false;
            currentAliasableTable = null;
         }
         else if("JOIN".equalsIgnoreCase(token) || "FROM".equalsIgnoreCase(token))
         {
            lastTokenAllowedTableAliasDefinition = true;
            currentAliasableTable = null;
         }
         else if(lastTokenAllowedTableAliasDefinition)
         {

            TableQualifier tableQualifier = new TableQualifier(token);
            ITableInfo[] tableInfos = new ITableInfo[0];

            try
            {
               tableInfos = schemaInfo.getITableInfos(tableQualifier.getCatalog(), tableQualifier.getSchema(), tableQualifier.getTableName());
            }
            catch (Exception e)
            {
               // This may for example break in the constructor of FilterMatcher for example if token is a bracket.
            }

            lastTokenAllowedTableAliasDefinition = false;
            currentAliasableTable = null;
            if(0 < tableInfos.length)
            {
               currentAliasableTable = token;
            }
         }
         else if(null != currentAliasableTable && false == schemaInfo.isKeyword(token))
         {
            ret.add(new TableAliasInfo(token, currentAliasableTable, statementBounds.getBeginPos()));

            lastTokenAllowedTableAliasDefinition = false;
            currentAliasableTable = null;
         }
         else
         {
            lastTokenAllowedTableAliasDefinition = false;
            currentAliasableTable = null;
         }
      }

      return ret;
   }

   private String nextToken(int[] i, String sql)
   {
      _token.setLength(0);
      for (int j = i[0]; j < sql.length(); j++)
      {
         char c = sql.charAt(j);

         if (isLiteralDelimiter(c))
         {
            if (false == _sqlCommentHelper.isInComment(j))
            {
               ++literalDelimsCount;
            }
         }

         if (_sqlCommentHelper.isInComment(j) || SqlLiteralHelper.isInLiteral(literalDelimsCount))
         {
            if (0 < _token.length())
            {
               i[0] = j+1;
               return _token.toString();
            }

            // When we arrive here _token is empty
            continue;
         }

         if(Character.isWhitespace(c))
         {
            if(0 == _token.length())
            {
               continue;
            }
            else
            {
               i[0] = j+1;
               return _token.toString();
            }
         }

         if(isSepartor(c))
         {
            if(0 == _token.length())
            {
               i[0] = j+1;
               return _token.append(c).toString();
            }
            else
            {
               i[0] = j;
               return _token.toString();
            }
         }

         _token.append(c);
      }

      i[0] = sql.length();
      return _token.toString();
   }

   private boolean isLiteralDelimiter(char c)
   {
      return '\'' == c;
   }

   private boolean isSepartor(char c)
   {
      return ',' == c || '(' == c || ')' == c;
   }

}
