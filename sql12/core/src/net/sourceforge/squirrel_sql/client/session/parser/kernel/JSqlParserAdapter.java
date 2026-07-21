package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.parser.StringProvider;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.commons.lang3.StringUtils;

public class JSqlParserAdapter
{
   static ParsingResult executeParsing(StatementBounds statementBounds) throws ParseException
   {
      CCJSqlParser parser = new CCJSqlParser(new StringProvider(statementBounds.getStatement()));
      parser.setErrorRecovery(true);

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

      if (null == statement) // Also a problem that occurs these days
      {
         return new ParsingResult(new ArrayList<>(), new ArrayList<>(), parser.getParseErrors());
      }

      ArrayList<Table> tables = findTables(statement);

      ArrayList<ParenthesedSelect> parenthesedSelects = findParenthesedSelects(statement);

      return new ParsingResult(tables, parenthesedSelects, parser.getParseErrors());
   }

   private static ArrayList<Table> findTables(Statement statement)
   {
      ArrayList<Table> ret = new ArrayList<>();

      TablesNamesFinder tablesNamesFinder = new TablesNamesFinder()
      {
         @Override
         protected String extractTableName(Table table)
         {
            if( null != table && false == ret.contains(table))
            {
               ret.add(table);
            }
            return super.extractTableName(table);
         }
      };

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

      return ret;
   }

   private static ArrayList<ParenthesedSelect> findParenthesedSelects(Statement statement)
   {
      ArrayList<ParenthesedSelect> ret = new ArrayList<>();

      if(statement instanceof Select select && null != select.getWithItemsList())
      {
         List<WithItem<?>> withItemsList = select.getWithItemsList();
         for(WithItem<?> withItem : withItemsList)
         {
            if(withItem.getParenthesedStatement() instanceof ParenthesedSelect parenthesedSelect)
            {
               if(null != withItem.getAlias() && StringUtils.isNotBlank(withItem.getAlias().getName()) )
               {
                  // Maybe dangerous
                  parenthesedSelect.setAlias(withItem.getAlias());
               }

               ret.add(parenthesedSelect);
            }
         }
      }
     if(statement instanceof PlainSelect plainSelect)
      {
         List<Join> joins = plainSelect.getJoins();

         for(Join join : joins)
         {
            if(join.getFromItem() instanceof ParenthesedSelect parenthesedSelect)
            {
               ret.add(parenthesedSelect);
            }
         }
      }
      return ret;
   }


   /////////////////////////////////////////////////////////////////
   ///////////////////// TEST //////////////////////////////////////
   /////////////////////////////////////////////////////////////////
   public static void main(String[] args)
   {
      String sql1 =
            """
            select * from articles
            inner  join
            (
            SELECT * FROM suppliers
            ) AS blip on blip.id = articles.supplier_id
            LEFT JOIN article_groups ON article_groups.id = articles.article_group_id
            where
            """;

      try
      {
         ParsingResult parsingResult = executeParsing(new StatementBounds(sql1, 0, sql1.length()));
         System.out.println("parsingResult = " + parsingResult);

      }
      catch(ParseException e)
      {
         throw new RuntimeException(e);
      }
   }
}
