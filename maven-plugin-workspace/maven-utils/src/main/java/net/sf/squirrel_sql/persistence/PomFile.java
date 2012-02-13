package net.sf.squirrel_sql.persistence;

public interface PomFile
{

	public abstract long getId();

	public abstract String getPath();

	public abstract String getProjectName();

	public abstract String getTreeRootDir();

	public abstract long getVersion();

	public abstract String getProjectArtifactId();

	public abstract String getProjectGroupId();

}