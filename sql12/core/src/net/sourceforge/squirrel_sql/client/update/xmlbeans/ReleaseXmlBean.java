/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This class defines the data items that compose a release.
 * 
 * @author manningr
 */
public class ReleaseXmlBean implements Serializable
{

	private static final long serialVersionUID = -7311033877370497900L;

	/** The name of the release (like "snapshot", "stable", etc.) */
	private String name;

	/** The version of the release */
	private String version;

	private Date createTime;

	private Date lastModifiedTime;

	/** Artifacts are things like jarfiles, property files, and the like */
	Set<ModuleXmlBean> modules = new HashSet<ModuleXmlBean>();

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'_'HH:mm:ss.SSSZ");

	public ReleaseXmlBean()
	{
		createTime = new Date();
		lastModifiedTime = createTime;
	}

	public ReleaseXmlBean(String name, String version)
	{
		this();
		this.name = name;
		this.version = version;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *           the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
		lastModifiedTime = new Date();
	}

	/**
	 * @return the version
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * @param version
	 *           the version to set
	 */
	public void setVersion(String version)
	{
		this.version = version;
		lastModifiedTime = new Date();
	}

	/**
	 * @return the artifacts
	 */
	public Set<ModuleXmlBean> getModules()
	{
		return modules;
	}

	/**
	 * @param modules
	 *           the modules to set
	 */
	public void setModules(Set<ModuleXmlBean> modules)
	{
		this.modules = modules;
		lastModifiedTime = new Date();
	}

	/**
	 * @param module
	 */
	public void addmodule(ModuleXmlBean module)
	{
		if (module == null) { return; }
		this.modules.add(module);
		lastModifiedTime = new Date();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final ReleaseXmlBean other = (ReleaseXmlBean) obj;
		if (name == null)
		{
			if (other.name != null) return false;
		}
		else if (!name.equals(other.name)) return false;
		if (version == null)
		{
			if (other.version != null) return false;
		}
		else if (!version.equals(other.version)) return false;
		return true;
	}

	/**
	 * @return the createTime
	 */
	public String getCreateTime()
	{

		return dateFormat.format(createTime);
	}

	/**
	 * @param createTime
	 *           the createTime to set
	 */
	public void setCreateTime(Date createTime)
	{
		if (createTime == null) {
			return;
		}
		this.createTime = createTime;
	}

	/**
	 * @return the lastModifiedTime
	 */
	public String getLastModifiedTime()
	{
		return dateFormat.format(lastModifiedTime);
	}

	/**
	 * @param lastModifiedTime
	 *           the lastModifiedTime to set
	 */
	public void setLastModifiedTime(Date lastModifiedTime)
	{
		if (lastModifiedTime == null) { return; }
		this.lastModifiedTime = lastModifiedTime;
	}

	/**
	 * Constructs a <code>String</code> with all attributes in name = value format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	public String toString()
	{
		final String TAB = "    ";

		StringBuilder retValue = new StringBuilder();

		retValue.append("ReleaseXmlBean ( ")
			.append(super.toString())
			.append(TAB)
			.append("name = ")
			.append(this.name)
			.append(TAB)
			.append("version = ")
			.append(this.version)
			.append(TAB)
			.append("createTime = ")
			.append(this.createTime)
			.append(TAB)
			.append("lastModifiedTime = ")
			.append(this.lastModifiedTime)
			.append(TAB)
			.append("modules = ")
			.append(this.modules)
			.append(TAB)
			.append("dateFormat = ")
			.append(this.dateFormat)
			.append(TAB)
			.append(" )");

		return retValue.toString();
	}

}
