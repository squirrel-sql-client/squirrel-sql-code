package net.sourceforge.squirrel_sql.client.session.parser.kernel;



public class TableAliasInfo
{
	public String aliasName;
	public String tableName;
	public int statBegin;

	public TableAliasInfo(String aliasName, String tableName, int statBegin)
	{
		this.aliasName = aliasName;
		this.tableName = tableName;
		this.statBegin = statBegin;
	}
}
