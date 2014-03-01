package net.sourceforge.squirrel_sql.client.gui.mainframe;

/*
 * Copyright (C) 2001 Colin Bell
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
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class MainFrameWindowStateBeanInfo extends SimpleBeanInfo
{
	public BeanInfo[] getAdditionalBeanInfo()
	{
		try
		{
			BeanInfo superBeanInfo = Introspector.getBeanInfo(MainFrameWindowState.class.getSuperclass());
			return new BeanInfo[] { superBeanInfo };
		}
		catch (IntrospectionException ex)
		{
			return new BeanInfo[0];
		}
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
			PropertyDescriptor[] result = new PropertyDescriptor[2];
			result[0] =
				new PropertyDescriptor(MainFrameWindowState.IPropertyNames.ALIASES_WINDOW_STATE,
					MainFrameWindowState.class, "getAliasesWindowState", "setAliasesWindowState");
			result[1] =
				new PropertyDescriptor(MainFrameWindowState.IPropertyNames.DRIVERS_WINDOW_STATE,
					MainFrameWindowState.class, "getDriversWindowState", "setDriversWindowState");
			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
