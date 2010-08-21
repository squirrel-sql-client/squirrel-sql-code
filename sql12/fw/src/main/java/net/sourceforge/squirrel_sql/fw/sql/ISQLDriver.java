package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
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
import java.beans.PropertyChangeListener;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.util.beanwrapper.StringWrapper;

public interface ISQLDriver extends IHasIdentifier, Comparable<ISQLDriver>
{
	/**
	 * JavaBean property names for this class.
	 */
	public interface IPropertyNames
	{
		String DRIVER_CLASS = "driverClassName";
		String ID = "identifier";
		String JARFILE_NAME = "jarFileName";
		String JARFILE_NAMES = "jarFileNames";
		String NAME = "name";
		String URL = "url";
        String WEBSITE_URL = "websiteUrl";
	}

	/**
	 * Assign data from the passed <CODE>ISQLDriver</CODE> to this one. This
	 * does <B>not</B> copy the identifier.
	 *
	 * @param	rhs	<CODE>ISQLDriver</CODE> to copy data from.
	 *
	 * @exception	ValidationException
	 *				Thrown if an error occurs assigning data from
	 *				<CODE>rhs</CODE>.
	 */
	void assignFrom(ISQLDriver rhs) throws ValidationException;

	/**
	 * Compare this <TT>ISQLDriver</TT> to another object. If the passed object
	 * is a <TT>ISQLDriver</TT>, then the <TT>getName()</TT> functions of the two
	 * <TT>ISQLDriver</TT> objects are used to compare them. Otherwise, it throws a
	 * ClassCastException (as <TT>ISQLDriver</TT> objects are comparable only to
	 * other <TT>ISQLDriver</TT> objects).
	 */
	int compareTo(ISQLDriver rhs);

	IIdentifier getIdentifier();

	String getDriverClassName();

	void setDriverClassName(String driverClassName)
		throws ValidationException;

	/**
	 * @deprecated	Replaced by getJarFileURLs().
	 */
	String getJarFileName();

	void setJarFileName(String value) throws ValidationException;

	StringWrapper[] getJarFileNameWrappers();

	StringWrapper getJarFileNameWrapper(int idx) throws ArrayIndexOutOfBoundsException;


	void setJarFileNameWrappers(StringWrapper[] value);

	void setJarFileNameWrapper(int idx, StringWrapper value) throws ArrayIndexOutOfBoundsException;

	String[] getJarFileNames();
	void setJarFileNames(String[] values);

	String getUrl();

	void setUrl(String url) throws ValidationException;

	String getName();

	void setName(String name) throws ValidationException;

	boolean isJDBCDriverClassLoaded();
	void setJDBCDriverClassLoaded(boolean cl);

	void addPropertyChangeListener(PropertyChangeListener listener);
	void removePropertyChangeListener(PropertyChangeListener listener);
    
    String getWebSiteUrl();
    
    void setWebSiteUrl(String url) throws ValidationException;
}
