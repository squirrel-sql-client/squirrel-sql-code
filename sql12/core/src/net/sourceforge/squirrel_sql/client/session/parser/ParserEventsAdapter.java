package net.sourceforge.squirrel_sql.client.session.parser;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.ErrorInfo;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAndAliasParseResult;

import java.util.List;

public class ParserEventsAdapter implements ParserEventsListener
{
   public void tableAndAliasParseResultFound(TableAndAliasParseResult TableAndAliasParseResult)
   {
   }

   public void errorsFound(List<ErrorInfo> errorInfos)
   {
   }
}
