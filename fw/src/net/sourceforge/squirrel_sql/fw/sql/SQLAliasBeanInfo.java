package net.sourceforge.squirrel_sql.fw.sql;
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
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLAliasBeanInfo extends SimpleBeanInfo
{
	private static PropertyDescriptor[] s_desc;
	private static Class CLAZZ = SQLAlias.class;

	private interface IPropNames extends ISQLAlias.IPropertyNames
	{
		// Empty body.
	}

	public SQLAliasBeanInfo() throws IntrospectionException
	{
		super();
		if (s_desc == null)
		{
			s_desc = new PropertyDescriptor[10];
			s_desc[0] = new PropertyDescriptor(IPropNames.ID, CLAZZ, "getIdentifier", "setIdentifier");
			s_desc[1] = new PropertyDescriptor(IPropNames.NAME, CLAZZ, "getName", "setName");
			s_desc[2] = new PropertyDescriptor(IPropNames.URL, CLAZZ, "getUrl", "setUrl");
			s_desc[3] = new PropertyDescriptor(IPropNames.USER_NAME, CLAZZ, "getUserName", "setUserName");
			s_desc[4] = new PropertyDescriptor(IPropNames.DRIVER, CLAZZ, "getDriverIdentifier", "setDriverIdentifier");
			s_desc[5] = new PropertyDescriptor(IPropNames.USE_DRIVER_PROPERTIES, CLAZZ, "getUseDriverProperties", "setUseDriverProperties");
			s_desc[6] = new PropertyDescriptor(IPropNames.DRIVER_PROPERTIES, CLAZZ, "getDriverProperties", "setDriverProperties");
			s_desc[7] = new PropertyDescriptor(IPropNames.PASSWORD, CLAZZ, "getPassword", "setPassword");
//			s_desc[8] = new PropertyDescriptor(IPropNames.PASSWORD_SAVED, CLAZZ, "isPasswordSaved", "setPasswordSaved");
			s_desc[8] = new PropertyDescriptor(IPropNames.AUTO_LOGON, CLAZZ, "isAutoLogon", "setAutoLogon");
			s_desc[9] = new PropertyDescriptor(IPropNames.CONNECT_AT_STARTUP, CLAZZ, "isConnectAtStartup", "setConnectAtStartup");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_desc;
	}
}

