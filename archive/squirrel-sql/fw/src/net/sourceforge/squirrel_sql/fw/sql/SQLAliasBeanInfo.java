package net.sourceforge.squirrel_sql.fw.sql;
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
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>SQLAlias</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLAliasBeanInfo extends SimpleBeanInfo {
	private static PropertyDescriptor[] s_descriptors;

	public SQLAliasBeanInfo() throws IntrospectionException {
		super();
		if (s_descriptors == null) {
			s_descriptors = new PropertyDescriptor[5];
			s_descriptors[0] = new PropertyDescriptor(ISQLAlias.IPropertyNames.ID, SQLAlias.class, "getIdentifier", "setIdentifier");
			s_descriptors[1] = new PropertyDescriptor(ISQLAlias.IPropertyNames.NAME, SQLAlias.class, "getName", "setName");
			s_descriptors[2] = new PropertyDescriptor(ISQLAlias.IPropertyNames.URL, SQLAlias.class, "getUrl", "setUrl");
			s_descriptors[3] = new PropertyDescriptor(ISQLAlias.IPropertyNames.USER_NAME, SQLAlias.class, "getUserName", "setUserName");
			s_descriptors[4] = new PropertyDescriptor(ISQLAlias.IPropertyNames.DRIVER, SQLAlias.class, "getDriverIdentifier", "setDriverIdentifier");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors() {
		return s_descriptors;
	}
}

