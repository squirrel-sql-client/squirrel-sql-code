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

	private final static Class s_cls = SQLDriver.class;
	private static PropertyDescriptor[] s_descriptors;

	public SQLDriverBeanInfo() throws IntrospectionException
	{
		super();
		if (s_descriptors == null)
		{
			s_descriptors = new PropertyDescriptor[6];
			s_descriptors[0] =
				new PropertyDescriptor(
					ISQLDriver.IPropertyNames.NAME,
					s_cls,
					"getName",
					"setName");
			s_descriptors[1] =
				new PropertyDescriptor(
					ISQLDriver.IPropertyNames.DRIVER_CLASS,
					s_cls,
					"getDriverClassName",
					"setDriverClassName");
			s_descriptors[2] =
				new PropertyDescriptor(
					ISQLDriver.IPropertyNames.ID,
					s_cls,
					"getIdentifier",
					"setIdentifier");
			s_descriptors[3] =
				new PropertyDescriptor(
					ISQLDriver.IPropertyNames.URL,
					s_cls,
					"getUrl",
					"setUrl");
			//s_descriptors[4] =
			//	new PropertyDescriptor(
			//		ISQLDriver.IPropertyNames.USES_CLASSPATH,
			//		s_cls,
			///		"getUsesClassPath",
			//		"setUsesClassPath");
			s_descriptors[4] =
				new PropertyDescriptor(
					ISQLDriver.IPropertyNames.JARFILE_NAME,
					s_cls,
					"getJarFileName",
					"setJarFileName");
			//s_descriptors[6] =
			//	new PropertyDescriptor(
			//		ISQLDriver.IPropertyNames.PLUGIN_NAMES,
			//		s_cls,
			//		"getPluginNames",
			//		"setPluginNames");
			s_descriptors[5] =
				new IndexedPropertyDescriptor(
					ISQLDriver.IPropertyNames.JARFILE_NAMES,
					s_cls,
					"getJarFileNameWrappers",
					"setJarFileNameWrappers",
					"getJarFileNameWrapper",
					"setJarFileNameWrapper");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_descriptors;
	}
}