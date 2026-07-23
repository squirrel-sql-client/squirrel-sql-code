package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;

public class HeuristicParenthesedSelectsParser
{
   public static List<ParenthesedSelectInfo> parse(StatementBounds statementBounds, SchemaInfo schemaInfo)
   {
      List<ParenthesedSelectInfo> ret = new ArrayList<>();

      HeuristicTokenParser tokenParser = new HeuristicTokenParser(statementBounds);

      //int[] i = new int[]{0};
      //
      //boolean lastTokenAllowedTableAliasDefinition = false;
      //String currentAliasableTable = null;
      //
      //while(i[0] < statementBounds.getStatement().length())
      //{
      //   String token = tokenParser.nextToken(i, statementBounds.getStatement());
      //
      //   if(0 == token.length())
      //   {
      //      lastTokenAllowedTableAliasDefinition = false;
      //      currentAliasableTable = null;
      //   }
      //   else if("JOIN".equalsIgnoreCase(token) || "FROM".equalsIgnoreCase(token))
      //   {
      //      lastTokenAllowedTableAliasDefinition = true;
      //      currentAliasableTable = null;
      //   }
      //   else if(lastTokenAllowedTableAliasDefinition)
      //   {
      //
      //      TableQualifier tableQualifier = new TableQualifier(token);
      //      ITableInfo[] tableInfos = new ITableInfo[0];
      //
      //      try
      //      {
      //         tableInfos = schemaInfo.getITableInfos(tableQualifier.getCatalog(), tableQualifier.getSchema(), tableQualifier.getTableName());
      //      }
      //      catch (Exception e)
      //      {
      //         // This may for example break in the constructor of FilterMatcher for example if token is a bracket.
      //      }
      //
      //      lastTokenAllowedTableAliasDefinition = false;
      //      currentAliasableTable = null;
      //      if(0 < tableInfos.length)
      //      {
      //         currentAliasableTable = token;
      //         ret.addTableParseInfo(new TableParseInfo(currentAliasableTable, statementBounds.getBeginPos(), statementBounds.getEndPos()));
      //      }
      //   }
      //   else if(null != currentAliasableTable && false == schemaInfo.isKeyword(token))
      //   {
      //      TableAliasParseInfo tableAliasParseInfo = new TableAliasParseInfo(token, currentAliasableTable, statementBounds.getBeginPos(), statementBounds.getEndPos());
      //      ret.addTableAliasInfo(tableAliasParseInfo);
      //      ret.removeFromTableParseInfos(tableAliasParseInfo);
      //
      //      lastTokenAllowedTableAliasDefinition = false;
      //      currentAliasableTable = null;
      //   }
      //   else
      //   {
      //      lastTokenAllowedTableAliasDefinition = false;
      //      currentAliasableTable = null;
      //   }
      //}

      return new ArrayList<>();
   }
}
