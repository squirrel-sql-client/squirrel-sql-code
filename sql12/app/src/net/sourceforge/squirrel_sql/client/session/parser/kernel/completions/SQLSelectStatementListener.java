package net.sourceforge.squirrel_sql.client.session.parser.kernel.completions;

public interface SQLSelectStatementListener
{
	void aliasDefined(String tableName, String aliasName);
}
