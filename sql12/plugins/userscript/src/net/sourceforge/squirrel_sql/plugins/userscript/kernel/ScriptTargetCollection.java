package net.sourceforge.squirrel_sql.plugins.userscript.kernel;

import java.util.Vector;
import java.util.Hashtable;

public class ScriptTargetCollection
{
	private Vector m_targets = new Vector();
	private Hashtable m_containedTargetTypes = new Hashtable();

	public void add(ScriptTarget target)
	{
		m_targets.add(target);
		m_containedTargetTypes.put(target.getTargetType(), target.getTargetType());
	}

	public ScriptTarget[] getAll()
	{
		return (ScriptTarget[]) m_targets.toArray(new ScriptTarget[m_targets.size()]);
	}

	public String[] getContainedTargetTypes()
	{
		return (String[]) m_containedTargetTypes.keySet().toArray(new String[0]);
	}

	public int size()
	{
		return m_targets.size();
	}
}
