package net.sf.squirrel_sql.persistence;

public interface PomFileFactory
{

	public abstract PomFile createPomFile(String treeRootDir, String path, String projectGroupId,
		String projectArtifactId, String projectName);

}