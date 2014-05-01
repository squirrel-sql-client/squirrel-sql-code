package org.squirrelsql.session.parser.kernel;

import org.squirrelsql.session.parser.kernel.completions.SQLStatement;

public interface ParserListener
{
	void statementAdded(SQLStatement statement);
}
