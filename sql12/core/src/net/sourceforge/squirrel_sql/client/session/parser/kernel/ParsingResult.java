package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.schema.Table;

import java.util.ArrayList;
import java.util.List;

public class ParsingResult
{
   private final ArrayList<Table> _tables;
   private final List<ParseException> _parseErrors;

   public ParsingResult(ArrayList<Table> tables, List<ParseException> parseErrors)
   {
      _tables = tables;
      _parseErrors = parseErrors;
   }

   public ArrayList<Table> getTables()
   {
      return _tables;
   }

   public List<ParseException> getParseErrors()
   {
      return _parseErrors;
   }
}
