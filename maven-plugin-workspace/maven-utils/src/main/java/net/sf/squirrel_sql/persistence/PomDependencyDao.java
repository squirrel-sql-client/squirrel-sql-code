package net.sf.squirrel_sql.persistence;

import java.util.List;

public interface PomDependencyDao
{

	void insertDependency(PomDependency d);

	List<CrossSourceTreeDependency> findCrossTreeDependencies();

	public abstract List<PomDependency> findDependenciesBySourceTreePath(String treeRootDir);
	
}