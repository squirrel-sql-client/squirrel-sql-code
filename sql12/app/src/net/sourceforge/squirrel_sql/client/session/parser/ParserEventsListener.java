package net.sourceforge.squirrel_sql.client.session.parser;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAliasInfo;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ErrorInfo;

public interface ParserEventsListener
{
	void aliasesFound(TableAliasInfo[] aliasInfos);
	void errorsFound(ErrorInfo[] errorInfos);
}
