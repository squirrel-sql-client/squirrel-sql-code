package net.sf.squirrel_sql;

import java.io.IOException;
import java.util.List;

public interface MavenMultiTreeDependencyAnalyzer
{

	public abstract void analyzePaths() throws IOException;

	public abstract void setSourceTreePaths(List<String> sourceTreePaths);

}