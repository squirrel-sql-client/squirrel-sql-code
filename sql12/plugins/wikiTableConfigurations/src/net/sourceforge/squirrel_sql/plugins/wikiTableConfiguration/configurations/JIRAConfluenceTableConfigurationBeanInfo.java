/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.wikiTableConfiguration.configurations;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;

/**
 * A BeanInfo, that only provides the enabled property.
 * @author Thomas Hackel
 *
 */
public class JIRAConfluenceTableConfigurationBeanInfo extends SimpleBeanInfo{
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
			PropertyDescriptor[] s_descr = new PropertyDescriptor[1];

			s_descr[0] = new PropertyDescriptor("enabled", JIRAConfluenceTableConfiguration.class, "isEnabled", "setEnabled");
			return s_descr;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
