package net.sourceforge.squirrel_sql.client.session.parser;

import java.util.List;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.ErrorInfo;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ParenthesedSelectParseResult;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAndAliasParseResult;

public class ParserEventsAdapter implements ParserEventsListener
{
   @Override
   public void tableAndAliasParseResultFound(TableAndAliasParseResult TableAndAliasParseResult)
   {
   }

   @Override
   public void parenthesedSelectParseResultFound(ParenthesedSelectParseResult parenthesedSelectParseResult)
   {
   }

   @Override
   public void errorsFound(List<ErrorInfo> errorInfos)
   {
   }
}
