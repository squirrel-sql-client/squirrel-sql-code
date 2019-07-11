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

      Statement statement = parser.Statement();

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
