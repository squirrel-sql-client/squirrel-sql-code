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
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public final class PluginInfoBeanInfo extends SimpleBeanInfo
{

	private interface IPropNames extends PluginInfo.IPropertyNames
	{
		// Empty body, purely to shorten the interface name for convienience.
	}

	/**
	 * See http://tinyurl.com/63no6t for discussion of the proper thread-safe way to implement
	 * getPropertyDescriptors().
	 * 
	 * @see java.beans.SimpleBeanInfo#getPropertyDescriptors()
	 */
	@Override	
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] s_descr = new PropertyDescriptor[8];

			s_descr[0] =
				new PropertyDescriptor(IPropNames.PLUGIN_CLASS_NAME, PluginInfo.class, "getPluginClassName", null);
			s_descr[1] = new PropertyDescriptor(IPropNames.IS_LOADED, PluginInfo.class, "isLoaded", null);
			s_descr[2] =
				new PropertyDescriptor(IPropNames.INTERNAL_NAME, PluginInfo.class, "getInternalName", null);
			s_descr[3] =
				new PropertyDescriptor(IPropNames.DESCRIPTIVE_NAME, PluginInfo.class, "getDescriptiveName", null);
			s_descr[4] = new PropertyDescriptor(IPropNames.AUTHOR, PluginInfo.class, "getAuthor", null);
			s_descr[5] =
				new PropertyDescriptor(IPropNames.CONTRIBUTORS, PluginInfo.class, "getContributors", null);
			s_descr[6] = new PropertyDescriptor(IPropNames.WEB_SITE, PluginInfo.class, "getWebSite", null);
			s_descr[7] = new PropertyDescriptor(IPropNames.VERSION, PluginInfo.class, "getVersion", null);

			return s_descr;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
