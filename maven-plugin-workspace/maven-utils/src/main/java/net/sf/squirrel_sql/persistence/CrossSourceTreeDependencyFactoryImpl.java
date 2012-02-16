package net.sf.squirrel_sql.persistence;

public class CrossSourceTreeDependencyFactoryImpl implements CrossSourceTreeDependencyFactory
{
	/**
	 * @see net.sf.squirrel_sql.persistence.CrossSourceTreeDependencyFactory#create(java.lang.String, java.lang.String)
	 */
	@Override
	public CrossSourceTreeDependency create(String treeRootDir, String dependUponTreeRootDir) {
		return new CrossSourceTreeDependencyImpl(treeRootDir, dependUponTreeRootDir);
	}
}
