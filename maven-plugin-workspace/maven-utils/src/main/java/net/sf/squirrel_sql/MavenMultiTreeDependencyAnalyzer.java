package net.sf.squirrel_sql;

import java.io.IOException;
import java.util.List;

public interface MavenMultiTreeDependencyAnalyzer
{

	void analyzePaths() throws IOException;

	void setSourceTreePaths(List<String> sourceTreePaths);

	void detectCycles() throws Exception;
}