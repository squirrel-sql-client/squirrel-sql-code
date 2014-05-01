package org.squirrelsql.session.parser;

import org.squirrelsql.session.parser.kernel.ErrorInfo;
import org.squirrelsql.session.parser.kernel.TableAliasInfo;

public interface ParserEventsListener
{
	void aliasesFound(TableAliasInfo[] aliasInfos);
	void errorsFound(ErrorInfo[] errorInfos);
}
