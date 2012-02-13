package net.sf.squirrel_sql.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;

public interface PomFileUtils
{

	public abstract void setPomFile(File f) throws IOException;

	public abstract String getGroupId();

	public abstract String getArtifactId();

	public abstract String getProjectName();

	public abstract List<Dependency> getDependencies();

	public abstract Set<Artifact> getArtifacts();

	public abstract File getPomFile();

}