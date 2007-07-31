package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import java.util.Vector;
import java.util.Hashtable;

public class ScriptTargetCollection
{
	private Vector<ScriptTarget> m_targets = new Vector<ScriptTarget>();
	private Hashtable<String, String> m_containedTargetTypes = 
        new Hashtable<String, String>();

	public void add(ScriptTarget target)
	{
		m_targets.add(target);
		m_containedTargetTypes.put(target.getTargetType(), target.getTargetType());
	}

	public ScriptTarget[] getAll()
	{
		return m_targets.toArray(new ScriptTarget[m_targets.size()]);
	}

	public String[] getContainedTargetTypes()
	{
		return m_containedTargetTypes.keySet().toArray(new String[0]);
	}

	public int size()
	{
		return m_targets.size();
	}
}
