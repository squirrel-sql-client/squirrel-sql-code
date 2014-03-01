package net.sourceforge.squirrel_sql.client.gui.db;

/*
 * Copyright (C) 2001-2003 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>SQLAlias</CODE>.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLAliasBeanInfo extends SimpleBeanInfo
{

	private interface IPropNames extends ISQLAlias.IPropertyNames
	{
		// Empty body.
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
			PropertyDescriptor[] result =
				new PropertyDescriptor[] {
						new PropertyDescriptor(IPropNames.ID, SQLAlias.class, "getIdentifier", "setIdentifier"),
						new PropertyDescriptor(IPropNames.NAME, SQLAlias.class, "getName", "setName"),
						new PropertyDescriptor(IPropNames.URL, SQLAlias.class, "getUrl", "setUrl"),
						new PropertyDescriptor(IPropNames.USER_NAME, SQLAlias.class, "getUserName", "setUserName"),
						new PropertyDescriptor(IPropNames.DRIVER, SQLAlias.class, "getDriverIdentifier",
							"setDriverIdentifier"),
						new PropertyDescriptor(IPropNames.USE_DRIVER_PROPERTIES, SQLAlias.class,
							"getUseDriverProperties", "setUseDriverProperties"),
						new PropertyDescriptor(IPropNames.DRIVER_PROPERTIES, SQLAlias.class,
							"getDriverPropertiesClone", "setDriverProperties"),
						new PropertyDescriptor(IPropNames.PASSWORD, SQLAlias.class, "getPassword", "setPassword"),
						new PropertyDescriptor(IPropNames.AUTO_LOGON, SQLAlias.class, "isAutoLogon", "setAutoLogon"),
						new PropertyDescriptor(IPropNames.CONNECT_AT_STARTUP, SQLAlias.class, "isConnectAtStartup",
							"setConnectAtStartup"),
						new PropertyDescriptor(IPropNames.SCHEMA_PROPERTIES, SQLAlias.class, "getSchemaProperties",
							"setSchemaProperties"),
						new PropertyDescriptor(IPropNames.COLOR_PROPERTIES, SQLAlias.class, "getColorProperties",
							"setColorProperties"),
							new PropertyDescriptor(IPropNames.CONNECTION_PROPERTIES, SQLAlias.class, "getConnectionProperties",
							"setConnectionProperties")};
			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
