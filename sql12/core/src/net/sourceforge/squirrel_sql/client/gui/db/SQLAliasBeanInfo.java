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

	interface IPropertyNames
	{
		String AUTO_LOGON = "autoLogon";
		String CONNECT_AT_STARTUP = "connectAtStartup";
		String DRIVER = "driverIdentifier";
		String DRIVER_PROPERTIES = "driverProperties";
		String ID = "identifier";
		String NAME = "name";
		String PASSWORD = "password";
		String ENCRYPT_PASSWORD = "encryptPassword"; // Renamed from PasswordEncrypted because of bug #1409
		String READ_ONLY = "readOnly";
		String URL = "url";
		String USE_DRIVER_PROPERTIES = "useDriverProperties";
		String ALIAS_VERSION_TIME_MILLIS = "aliasVersionTimeMills";
		String USER_NAME = "userName";
		String SCHEMA_PROPERTIES = "schemaProperties";
		String COLOR_PROPERTIES = "colorProperties";
		String CONNECTION_PROPERTIES = "connectionProperties";
		String NON_DEFAULT_PROXY_SETTINGS_NAME = "nonDefaultProxySettingsName";
	}

	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		try
		{
			PropertyDescriptor[] result =
				new PropertyDescriptor[] {
					new PropertyDescriptor(IPropertyNames.ID, SQLAlias.class, "getIdentifier", "setIdentifier"),
					new PropertyDescriptor(IPropertyNames.NAME, SQLAlias.class, "getName", "setName"),
					new PropertyDescriptor(IPropertyNames.URL, SQLAlias.class, "getUrl", "setUrl"),
					new PropertyDescriptor(IPropertyNames.USER_NAME, SQLAlias.class, "getUserName", "setUserName"),
					new PropertyDescriptor(IPropertyNames.DRIVER, SQLAlias.class, "getDriverIdentifier","setDriverIdentifier"),
					new PropertyDescriptor(IPropertyNames.USE_DRIVER_PROPERTIES, SQLAlias.class,"getUseDriverProperties", "setUseDriverProperties"),
					new PropertyDescriptor(IPropertyNames.DRIVER_PROPERTIES, SQLAlias.class,"getDriverPropertiesClone", "setDriverProperties"),
					new PropertyDescriptor(IPropertyNames.PASSWORD, SQLAlias.class, "getPassword", "setPassword"),
					new PropertyDescriptor(IPropertyNames.ENCRYPT_PASSWORD, SQLAlias.class, "isEncryptPassword", "setEncryptPassword"), // Renamed from PasswordEncrypted because of bug #1409
					new PropertyDescriptor(IPropertyNames.AUTO_LOGON, SQLAlias.class, "isAutoLogon", "setAutoLogon"),
					new PropertyDescriptor(IPropertyNames.READ_ONLY, SQLAlias.class, "isReadOnly", "setReadOnly"),
					new PropertyDescriptor(IPropertyNames.CONNECT_AT_STARTUP, SQLAlias.class, "isConnectAtStartup", "setConnectAtStartup"),
					new PropertyDescriptor(IPropertyNames.ALIAS_VERSION_TIME_MILLIS, SQLAlias.class, "getAliasVersionTimeMills", "setAliasVersionTimeMills"),
					new PropertyDescriptor(IPropertyNames.SCHEMA_PROPERTIES, SQLAlias.class, "getSchemaProperties","setSchemaProperties"),
					new PropertyDescriptor(IPropertyNames.COLOR_PROPERTIES, SQLAlias.class, "getColorProperties","setColorProperties"),
					new PropertyDescriptor(IPropertyNames.CONNECTION_PROPERTIES, SQLAlias.class, "getConnectionProperties","setConnectionProperties"),
					new PropertyDescriptor(IPropertyNames.NON_DEFAULT_PROXY_SETTINGS_NAME, SQLAlias.class, "getNonDefaultProxySettingsName","setNonDefaultProxySettingsName")
			};
			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
