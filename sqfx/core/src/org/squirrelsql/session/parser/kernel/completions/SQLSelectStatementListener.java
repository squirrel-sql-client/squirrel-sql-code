package org.squirrelsql.session.parser.kernel.completions;

public interface SQLSelectStatementListener
{
	void aliasDefined(String tableName, String aliasName);
}
