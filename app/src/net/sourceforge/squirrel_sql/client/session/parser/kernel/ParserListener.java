package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.completions.SQLStatement;

public interface ParserListener
{
	void statementAdded(SQLStatement statement);
}
