package net.sourceforge.squirrel_sql.client.session.parser;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.ErrorInfo;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAndAliasParseResult;

import java.util.List;

public interface ParserEventsListener
{
	void tableAndAliasParseResultFound(TableAndAliasParseResult tableAndAliasParseResult);
	void errorsFound(List<ErrorInfo> errorInfos);
}
