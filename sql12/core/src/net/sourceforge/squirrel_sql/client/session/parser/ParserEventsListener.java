package net.sourceforge.squirrel_sql.client.session.parser;

import java.util.List;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.ErrorInfo;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ParenthesedSelectParseResult;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAndAliasParseResult;

public interface ParserEventsListener
{
	void tableAndAliasParseResultFound(TableAndAliasParseResult tableAndAliasParseResult);
	void parenthesedSelectParseResultFound(ParenthesedSelectParseResult parenthesedSelectParseResult);
	void errorsFound(List<ErrorInfo> errorInfos);
}
