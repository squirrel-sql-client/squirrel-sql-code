package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.util.ArrayList;
import net.sf.jsqlparser.schema.Table;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableQualifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class TableAndAliasParseResultCreator
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TableAndAliasParseResultCreator.class);


   public static TableAndAliasParseResult createTableAndAliasParseResultForStatement(ISession session,
                                                                                     StatementBounds statementBounds,
                                                                                     ParsingResult parsingResult,
                                                                                     ArrayList<ErrorInfo> errorInfosBuffer,
                                                                                     ParseTerminateRequestCheck parseTerminateRequestCheck)
   {
      TableAndAliasParseResult singleStatementTableAndAliasParseResultBuffer;
      singleStatementTableAndAliasParseResultBuffer = new TableAndAliasParseResult();

      parseTerminateRequestCheck.check();

      for(Table table : parsingResult.getTables())
      {
         parseTerminateRequestCheck.check();

         TableQualifier tableQualifier = new TableQualifier(table.getFullyQualifiedName());

         ITableInfo[] tableInfos = session.getSchemaInfo().getITableInfos(StringUtilities.stripDoubleQuotes(tableQualifier.getCatalog()),
                                                                          StringUtilities.stripDoubleQuotes(tableQualifier.getSchema()),
                                                                          StringUtilities.stripDoubleQuotes(tableQualifier.getTableName()));

         parseTerminateRequestCheck.check();

         if(0 == tableInfos.length)
         {
            int beginPos = statementBounds.getBeginPos() + table.getASTNode().jjtGetFirstToken().absoluteBegin;
            int endPos = statementBounds.getBeginPos() + table.getASTNode().jjtGetFirstToken().absoluteEnd;
            errorInfosBuffer.add(new ErrorInfo(s_stringMgr.getString("parserthread.undefinedTable"), beginPos, endPos));
         }
         else if(null != table.getAlias())
         {
            singleStatementTableAndAliasParseResultBuffer.addTableAliasInfo(new TableAliasParseInfo(table.getAlias().getName(), table.getFullyQualifiedName(), statementBounds.getBeginPos(), statementBounds.getEndPos()));
         }
         else
         {
            singleStatementTableAndAliasParseResultBuffer.addTableParseInfo(new TableParseInfo(table.getFullyQualifiedName(), statementBounds.getBeginPos(), statementBounds.getEndPos()));
         }
      }

      if(aliasesOrTablesMayGotLostByParserErrors(singleStatementTableAndAliasParseResultBuffer, parsingResult))
      {
         singleStatementTableAndAliasParseResultBuffer = HeuristicSQLTableAndAliasParser.parse(statementBounds, session.getSchemaInfo());
      }
      return singleStatementTableAndAliasParseResultBuffer;
   }

   private static boolean aliasesOrTablesMayGotLostByParserErrors(TableAndAliasParseResult tableAndAliasParseResult, ParsingResult parsingResult)
   {
      return tableAndAliasParseResult.isEmpty() && false == parsingResult.getParseErrors().isEmpty();
   }
}
