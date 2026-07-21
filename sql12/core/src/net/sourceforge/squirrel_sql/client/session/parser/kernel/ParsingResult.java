package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;

public class ParsingResult
{
   private final List<Table> _tables;
   private final List<ParenthesedSelect> _parenthesedSelects;
   private final List<ParseException> _parseErrors;

   public ParsingResult(ArrayList<Table> tables, ArrayList<ParenthesedSelect> parenthesedSelects, List<ParseException> parseErrors)
   {
      _tables = tables;
      _parenthesedSelects = parenthesedSelects;
      _parseErrors = parseErrors;
   }

   public List<Table> getTables()
   {
      return _tables;
   }

   public List<ParenthesedSelect> getParenthesedSelects()
   {
      return _parenthesedSelects;
   }

   public List<ParseException> getParseErrors()
   {
      return _parseErrors;
   }
}
