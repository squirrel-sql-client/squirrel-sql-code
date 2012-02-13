package net.sf.squirrel_sql.persistence;


public interface PomDependency
{

	public abstract PomFile getPomFile();

	public abstract void setPomFile(PomFile pomFile);

	public abstract PomFile getDependsUponPomFile();

	public abstract void setDependsUponPomFile(PomFile dependsUponPomFile);

	public abstract Long getId();

	public abstract Long getVersion();

	public abstract String getType();

}