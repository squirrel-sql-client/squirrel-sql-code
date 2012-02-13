package net.sf.squirrel_sql.persistence;


public class PomFileFactoryImpl implements PomFileFactory
{
	/**
	 * @see net.sf.squirrel_sql.persistence.PomFileFactory#createPomFile(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public PomFile createPomFile(String treeRootDir, String path, String projectGroupId,
		String projectArtifactId, String projectName)
	{
		return new PomFileImpl(treeRootDir, path, projectGroupId, projectArtifactId, projectName);
	}
}
