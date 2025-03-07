package net.sourceforge.squirrel_sql.client.session.parser.kernel;


import net.sourceforge.squirrel_sql.fw.sql.TableQualifier;

public class TableAliasParseInfo
{
	public static final int POSITION_NON = -1;

	private String _aliasName;
	private TableQualifier _tableQualifier;
	private String _tableName;
	private int _statBegin;
	private int _statEnd;

	public TableAliasParseInfo(String aliasName, String tableName, int statBegin, int statEnd)
	{
		_aliasName = aliasName;
		_tableQualifier = new TableQualifier(tableName);
		_tableName = tableName;
		_statBegin = statBegin;
		_statEnd = statEnd;
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

	public int getStatEnd()
	{
		return _statEnd;
	}

	public TableQualifier getTableQualifier()
	{
		return _tableQualifier;
	}

	public boolean isInStatementOfAlias(int colPos)
	{
		if(POSITION_NON == colPos)
		{
			return true;
		}

		return colPos >= _statBegin && colPos <= _statEnd;
	}
}
