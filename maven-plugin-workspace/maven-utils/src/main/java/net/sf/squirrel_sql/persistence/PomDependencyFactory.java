package net.sf.squirrel_sql.persistence;


public interface PomDependencyFactory
{

	public abstract PomDependency createDependency(PomFile pomFile, PomFile dependsUponPomFile, String type);

}