package net.sourceforge.squirrel_sql.client.plugin;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>PluginInfo</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public final class PluginInfoBeanInfo extends SimpleBeanInfo
{
	private static PropertyDescriptor[] s_descr;

	private interface IPropNames extends PluginInfo.IPropertyNames
	{
		// Empty body, purely to shorten the interface name for convienience. 
	}

	public PluginInfoBeanInfo() throws IntrospectionException
	{
		super();
		if (s_descr == null)
		{
			final Class<PluginInfo> CLAZZ = PluginInfo.class;
			s_descr = new PropertyDescriptor[8];

			s_descr[0] = new PropertyDescriptor(IPropNames.PLUGIN_CLASS_NAME, CLAZZ,
												"getPluginClassName", null);
			s_descr[1] = new PropertyDescriptor(IPropNames.IS_LOADED, CLAZZ,
												"isLoaded", null);
			s_descr[2] = new PropertyDescriptor(IPropNames.INTERNAL_NAME, CLAZZ,
												"getInternalName", null);
			s_descr[3] = new PropertyDescriptor(IPropNames.DESCRIPTIVE_NAME, CLAZZ,
												"getDescriptiveName", null);
			s_descr[4] = new PropertyDescriptor(IPropNames.AUTHOR, CLAZZ,
												"getAuthor", null);
			s_descr[5] = new PropertyDescriptor(IPropNames.CONTRIBUTORS, CLAZZ,
												"getContributors", null);
			s_descr[6] = new PropertyDescriptor(IPropNames.WEB_SITE, CLAZZ,
												"getWebSite", null);
			s_descr[7] = new PropertyDescriptor(IPropNames.VERSION, CLAZZ,
												"getVersion", null);
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_descr;
	}
}
