/**
 * 
 */
package net.sf.squirrel_sql.persistence;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;


/**
 * @author manningr
 *
 */
@Entity
@Table(name="Dependency",
uniqueConstraints = {@UniqueConstraint(columnNames={"pomFileId", "dependsUponPomFileId", "type"})}
)
public class PomDependencyImpl implements PomDependency
{
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Long Id;
	
	@Version
	private Long version;
	
	@OneToOne(cascade=CascadeType.ALL, targetEntity=PomFileImpl.class)
	@JoinColumn(name = "pomFileId", nullable=false)
	private PomFile pomFile;
	
	@OneToOne(cascade=CascadeType.ALL, targetEntity=PomFileImpl.class)
	@JoinColumn(name = "dependsUponPomFileId", nullable=false)
	private PomFile dependsUponPomFile;
	
	@Column(nullable=false)
	private String type;

	public PomDependencyImpl() {}
	
	public PomDependencyImpl(PomFile pomFile, PomFile dependsUponPomFile, String type) {
		this.pomFile = pomFile;
		this.dependsUponPomFile = dependsUponPomFile;
		this.type = type;
	}
	
	/**
	 * @see net.sf.squirrel_sql.persistence.PomDependency#getPomFile()
	 */
	@Override
	public PomFile getPomFile()
	{
		return pomFile;
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.PomDependency#setPomFile(net.sf.squirrel_sql.persistence.PomFileImpl)
	 */
	@Override
	public void setPomFile(PomFile pomFile)
	{
		this.pomFile = pomFile;
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.PomDependency#getDependsUponPomFile()
	 */
	@Override
	public PomFile getDependsUponPomFile()
	{
		return dependsUponPomFile;
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.PomDependency#setDependsUponPomFile(net.sf.squirrel_sql.persistence.PomFileImpl)
	 */
	@Override
	public void setDependsUponPomFile(PomFile dependsUponPomFile)
	{
		this.dependsUponPomFile = dependsUponPomFile;
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.PomDependency#getId()
	 */
	@Override
	public Long getId()
	{
		return Id;
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.PomDependency#getVersion()
	 */
	@Override
	public Long getVersion()
	{
		return version;
	}

	/**
	 * @see net.sf.squirrel_sql.persistence.PomDependency#getType()
	 */
	@Override
	public String getType()
	{
		return type;
	}

	@Override
	public String toString()
	{
		return "PomDependencyImpl [Id=" + Id + ", version=" + version + ", pomFile=" + pomFile
			+ ", dependsUponPomFile=" + dependsUponPomFile + ", type=" + type + "]";
	}


}
