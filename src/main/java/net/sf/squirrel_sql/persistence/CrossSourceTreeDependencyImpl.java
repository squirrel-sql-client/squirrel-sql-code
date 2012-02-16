package net.sf.squirrel_sql.persistence;

public class CrossSourceTreeDependencyImpl implements CrossSourceTreeDependency
{
	private final String treeRootDir;
	private final String dependUponTreeRootDir;
	
	public CrossSourceTreeDependencyImpl(String treeRootDir, String dependUponTreeRootDir) {
		this.treeRootDir = treeRootDir;
		this.dependUponTreeRootDir = dependUponTreeRootDir;
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.CrossSourceTreeDependency#getTreeRootDir()
	 */
	@Override
	public String getTreeRootDir()
	{
		return treeRootDir;
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.CrossSourceTreeDependency#getDependUponTreeRootDir()
	 */
	@Override
	public String getDependUponTreeRootDir()
	{
		return dependUponTreeRootDir;
	}
	
	
}
