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
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>SQLDriver</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLDriverBeanInfo extends SimpleBeanInfo
{
	private interface IPropertyNames extends ISQLDriver.IPropertyNames
	{
		// Empty body.
	}

	private final static Class<?> CLAZZ = SQLDriver.class;
	
	private static PropertyDescriptor[] s_descr;

	public SQLDriverBeanInfo() throws IntrospectionException
	{
		super();
		if (s_descr == null)
		{
			s_descr = new PropertyDescriptor[7];
			s_descr[0] = new PropertyDescriptor(IPropertyNames.NAME, CLAZZ, "getName", "setName");
			s_descr[1] = new PropertyDescriptor(IPropertyNames.DRIVER_CLASS, CLAZZ, "getDriverClassName", "setDriverClassName");
			s_descr[2] = new PropertyDescriptor(IPropertyNames.ID, CLAZZ, "getIdentifier", "setIdentifier");
			s_descr[3] = new PropertyDescriptor(IPropertyNames.URL, CLAZZ, "getUrl", "setUrl");
			s_descr[4] = new PropertyDescriptor(IPropertyNames.JARFILE_NAME, CLAZZ, "getJarFileName", "setJarFileName");
			s_descr[5] = new IndexedPropertyDescriptor(IPropertyNames.JARFILE_NAMES, CLAZZ,
								"getJarFileNameWrappers", "setJarFileNameWrappers",
								"getJarFileNameWrapper", "setJarFileNameWrapper");
            s_descr[6] = new PropertyDescriptor(IPropertyNames.WEBSITE_URL, CLAZZ, "getWebSiteUrl", "setWebSiteUrl");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_descr;
	}
}
