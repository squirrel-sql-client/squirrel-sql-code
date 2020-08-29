package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.parser.StringProvider;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.ArrayList;

public class JSqlParserAdapter
{
   static ParsingResult executeParsing(StatementBounds statementBounds) throws ParseException
   {
      CCJSqlParser parser = new CCJSqlParser(new StringProvider(statementBounds.getStatement()));
      parser.setErrorRecovery(true);

      ArrayList<Table> tables = new ArrayList<>();

      TablesNamesFinder tablesNamesFinder = new TablesNamesFinder()
      {
         @Override
         public void visit(Table table)
         {
            super.visit(table);
            tables.add(table);
         }
      };

      Statement statement = null ;

      try
      {
         // For example for
         // "SELECT suppliers. FROM suppliers su where su.id  = '-- Hahh"
         // the following error is raised:
         // net.sf.jsqlparser.parser.TokenMgrException: Lexical error
         //   at line 1, column 61.  Encountered: <EOF> after : "\'-- Hahh "
         //	  at net.sf.jsqlparser.parser.CCJSqlParserTokenManager.getNextToken(CCJSqlParserTokenManager.java:4736)
         //
         // But a parse error is still returned which makes it kind of Ok.

         statement = parser.Statement();
      }
      catch (Exception e)
      {
      }

      if (null != statement) // Also a problem that occurs these days
      {
         try
         {
            tablesNamesFinder.getTableList(statement);
         }
         catch (UnsupportedOperationException e)
         {
            // TableFinder still got a lot of those, e.g. from drop statements.
            // We ignore them here for now.
            //e.printStackTrace();
         }
      }


      return new ParsingResult(tables, parser.getParseErrors());
   }
}
