package net.sf.squirrel_sql.persistence;

public interface PomFileDao
{

	public abstract void insertPom(PomFile pomFile);

	public abstract PomFile findByGroupIdAndArtifactId(String groupId, String artifactId);

}