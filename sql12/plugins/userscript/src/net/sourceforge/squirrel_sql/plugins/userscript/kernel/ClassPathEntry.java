package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

/**
 * This class exeist because of a bug in the XMLBeanReader.
 * It can't read a String[] into a bean.
 *
 */
public class ClassPathEntry
{
	private String m_entry;

	public ClassPathEntry(String entry)
	{
		m_entry = entry;
	}

	public ClassPathEntry()
	{
	}

	public String getEntry()
	{
		return m_entry;
	}

	public void setEntry(String entry)
	{
		m_entry = entry;
	}

}
