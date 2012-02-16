package net.sf.squirrel_sql.persistence;

public interface CrossSourceTreeDependencyFactory
{

	public abstract CrossSourceTreeDependency create(String treeRootDir, String dependUponTreeRootDir);

}