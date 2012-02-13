package net.sf.squirrel_sql.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;


@Entity
@Table(name="PomFile",
	uniqueConstraints = {@UniqueConstraint(columnNames={"projectGroupId", "projectArtifactId"})}
)
public class PomFileImpl implements PomFile
{
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Long id;

	@Version
	private Long version;

	@Column(nullable = false)
	private String treeRootDir;

	@Column(nullable = false, unique = true)
	private String path;

	@Column(nullable = false)
	private String projectGroupId;

	@Column(nullable = false)
	private String projectArtifactId;

	@Column(nullable = false)
	private String projectName;
	
	public PomFileImpl()
	{
	}

	public PomFileImpl(String treeRootDir, String path, String projectGroupId, String projectArtifactId,
		String projectName)
	{
		this.treeRootDir = treeRootDir;
		this.path = path;
		this.projectGroupId=projectGroupId;
		this.projectArtifactId=projectArtifactId;
		this.projectName = projectName;
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.PomFile#getId()
	 */
	@Override
	public long getId()
	{
		return id;
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.PomFile#getPath()
	 */
	@Override
	public String getPath()
	{
		return path;
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.PomFile#getProjectName()
	 */
	@Override
	public String getProjectName()
	{
		return projectName;
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.PomFile#getTreeRootDir()
	 */
	@Override
	public String getTreeRootDir()
	{
		return treeRootDir;
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.PomFile#getVersion()
	 */
	@Override
	public long getVersion()
	{
		return version;
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.PomFile#getProjectGroupId()
	 */
	@Override
	public String getProjectGroupId()
	{
		return projectGroupId;
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.PomFile#getProjectArtifactId()
	 */
	@Override
	public String getProjectArtifactId()
	{
		return projectArtifactId;
	}

	@Override
	public String toString()
	{
		return "PomFileImpl [id=" + id + ", version=" + version + ", treeRootDir=" + treeRootDir + ", path="
			+ path + ", projectGroupId=" + projectGroupId + ", projectArtifactId=" + projectArtifactId
			+ ", projectName=" + projectName + "]";
	}

}
