package net.sf.squirrel_sql.persistence;

public interface CrossSourceTreeDependency
{

	/**
	 * @return
	 */
	public abstract String getTreeRootDir();

	/**
	 * @return
	 */
	public abstract String getDependUponTreeRootDir();

}