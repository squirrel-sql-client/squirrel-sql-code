package net.sourceforge.squirrel_sql.client.session.parser.kernel;


import net.sourceforge.squirrel_sql.fw.sql.TableQualifier;

public class TableAliasInfo
{
	private String _aliasName;
	private TableQualifier _tableQualifier;
	private String _tableName;
	private int _statBegin;

	public TableAliasInfo(String aliasName, String tableName, int statBegin)
	{
		_aliasName = aliasName;
		_tableQualifier = new TableQualifier(tableName);
		_tableName = tableName;
		_statBegin = statBegin;
	}

	public String getAliasName()
	{
		return _aliasName;
	}

	public String getTableName()
	{
		return _tableName;
	}

	public int getStatBegin()
	{
		return _statBegin;
	}

	public TableQualifier getTableQualifier()
	{
		return _tableQualifier;
	}
}
