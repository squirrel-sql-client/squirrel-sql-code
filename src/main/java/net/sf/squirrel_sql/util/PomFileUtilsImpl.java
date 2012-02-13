package net.sf.squirrel_sql.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;

public class PomFileUtilsImpl implements PomFileUtils
{
	private File pomFile = null;

	private String groupId = null;

	private String artifactId = null;

	private String projectName = null;
	
	private List<Dependency> dependencies = null;

	private Set<Artifact> artifacts = null;

	/**
	 * @see net.sf.squirrel_sql.util.PomFileUtils#setPomFile(java.io.File)
	 */
	@Override
	public void setPomFile(File f) throws IOException
	{
		this.pomFile = f;
		initializeAttributes();
	}

	@Override
	public File getPomFile()
	{
		return pomFile;
	}

	/**
	 * @see net.sf.squirrel_sql.util.PomFileUtils#getGroupId()
	 */
	@Override
	public String getGroupId()
	{
		return groupId;
	}

	/**
	 * @see net.sf.squirrel_sql.util.PomFileUtils#getArtifactId()
	 */
	@Override
	public String getArtifactId()
	{
		return artifactId;
	}

	/**
	 * @see net.sf.squirrel_sql.util.PomFileUtils#getProjectName()
	 */
	@Override
	public String getProjectName()
	{
		return projectName;
	}

	private void initializeAttributes() throws IOException
	{
		Model model = null;
		FileReader reader = null;
		MavenXpp3Reader mavenreader = new MavenXpp3Reader();
		try
		{
			reader = new FileReader(pomFile);
			model = mavenreader.read(reader);
			model.setPomFile(pomFile);
		}
		catch (Exception ex)
		{
		}
		MavenProject project = new MavenProject(model);
		this.artifactId = project.getArtifactId();
		this.groupId = project.getGroupId();
		this.projectName = project.getName();
		this.dependencies = project.getDependencies();
		this.artifacts = project.getArtifacts();

	}

	/**
	 * @see net.sf.squirrel_sql.util.PomFileUtils#getArtifacts()
	 */
	@Override
	public Set<Artifact> getArtifacts()
	{
		return artifacts;
	}

	/**
	 * @see net.sf.squirrel_sql.util.PomFileUtils#getDependencies()
	 */
	@Override
	public List<Dependency> getDependencies()
	{
		return dependencies;
	}
}
