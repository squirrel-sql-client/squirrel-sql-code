package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

public class ScriptProps
{
	private Script[] m_scripts;
	private ClassPathEntry[] m_extraClassPath;

	public ScriptProps()
	{
	}

	public ScriptProps(Script[] scripts, ClassPathEntry[] extraClassPath)
	{
		m_extraClassPath = extraClassPath;
		setScripts(scripts);
	}

	public ClassPathEntry[] getExtraClassPath()
	{
		return m_extraClassPath;
	}

	public void setExtraClassPath(ClassPathEntry[] extraClassPath)
	{
		m_extraClassPath = extraClassPath;
	}


	public Script[] getScripts()
	{
		return m_scripts;
	}

	public void setScripts(Script[] scripts)
	{
		m_scripts = scripts;
	}
}
