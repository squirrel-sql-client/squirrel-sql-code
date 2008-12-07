/*
 * Copyright (C) 2006 Rob Manning
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
package net.sourceforge.squirrel_sql.fw.sql;

import java.beans.PropertyChangeListener;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.util.beanwrapper.StringWrapper;

public class MockSQLDriver extends SQLDriver implements ISQLDriver
{

	private static final long serialVersionUID = 1L;

	private String driverClassName = null;

	private String url = null;

	private String name = "mockSQLDriver";

	IIdentifier id = new UidIdentifierFactory().createIdentifier();

	public MockSQLDriver(String aClassName, String url)
	{
		driverClassName = aClassName;
		this.url = url;
	}

	public void assignFrom(ISQLDriver rhs) throws ValidationException
	{
		System.err.println("MockSQLDriver.assignFrom: stub not yet implemented");
	}

	public int compareTo(ISQLDriver rhs)
	{
		System.err.println("MockSQLDriver.compareTo: stub not yet implemented");
		return 0;
	}

	public IIdentifier getIdentifier()
	{
		System.err.println("MockSQLDriver.getIdentifier: stub not yet implemented");
		return null;
	}

	public String getDriverClassName()
	{
		return driverClassName;
	}

	public void setDriverClassName(String aClassName) throws ValidationException
	{
		driverClassName = aClassName;
	}

	public StringWrapper[] getJarFileNameWrappers()
	{
		System.err.println("MockSQLDriver.getJarFileNameWrappers: stub not yet implemented");
		return null;
	}

	public StringWrapper getJarFileNameWrapper(int idx) throws ArrayIndexOutOfBoundsException
	{
		System.err.println("MockSQLDriver.getJarFileNameWrapper: stub not yet implemented");
		return null;
	}

	public void setJarFileNameWrappers(StringWrapper[] value)
	{
		System.err.println("MockSQLDriver.setJarFileNameWrappers: stub not yet implemented");
	}

	public void setJarFileNameWrapper(int idx, StringWrapper value) throws ArrayIndexOutOfBoundsException
	{
		System.err.println("MockSQLDriver.setJarFileNameWrapper: stub not yet implemented");
	}

	public String[] getJarFileNames()
	{
		System.err.println("MockSQLDriver.getJarFileNames: stub not yet implemented");
		return null;
	}

	public void setJarFileNames(String[] values)
	{
		System.err.println("MockSQLDriver.setJarFileNames: stub not yet implemented");
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url) throws ValidationException
	{
		this.url = url;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name) throws ValidationException
	{
		this.name = name;
	}

	public boolean isJDBCDriverClassLoaded()
	{
		System.err.println("MockSQLDriver.isJDBCDriverClassLoaded: stub not yet implemented");
		return false;
	}

	public void setJDBCDriverClassLoaded(boolean cl)
	{
		System.err.println("MockSQLDriver.setJDBCDriverClassLoaded: stub not yet implemented");
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		System.err.println("MockSQLDriver.addPropertyChangeListener: stub not yet implemented");
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		System.err.println("MockSQLDriver.removePropertyChangeListener: stub not yet implemented");
	}

	public String getWebSiteUrl()
	{
		return null;
	}

	public void setWebSiteUrl(String url) throws ValidationException
	{
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		final MockSQLDriver other = (MockSQLDriver) obj;
		if (id == null)
		{
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		return true;
	}

}
