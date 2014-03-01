package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

public class ScriptTarget
{
	public static final String DB_OBJECT_TYPE_TABLE = "TABLE";
	public static final String DB_OBJECT_TYPE_VIEW = "VIEW";
	public static final String DB_OBJECT_TYPE_PROCEDURE = "PROCEDURE";
	public static final String DB_OBJECT_TYPE_CONNECTION = "CONNECTION";
	public static final String DB_OBJECT_TYPE_SQL_STATEMENT = "SQL";

	private String m_targetInfo;
	private String m_targetType;

	public ScriptTarget(String targetName, String targetType)
	{
		m_targetInfo = targetName;
		m_targetType = targetType;
	}

	public String getTargetType()
	{
		return m_targetType;
	}

	public String getTargetInfo()
	{
		return m_targetInfo;
	}
}
