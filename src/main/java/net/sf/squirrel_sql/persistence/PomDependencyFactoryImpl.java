package net.sf.squirrel_sql.persistence;


public class PomDependencyFactoryImpl implements PomDependencyFactory
{
	/**
	 * @see net.sf.squirrel_sql.persistence.PomFileFactory#createPomFile(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public PomDependency createDependency(PomFile pomFile, PomFile dependsUponPomFile, String type)
	{
		return new PomDependencyImpl(pomFile, dependsUponPomFile, type);
	}
}
