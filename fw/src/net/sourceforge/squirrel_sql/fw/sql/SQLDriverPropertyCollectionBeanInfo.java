package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2003 Colin Bell
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
 * This is the <CODE>BeanInfo</CODE> class for <CODE>SQLDriverPropertyCollection</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLDriverPropertyCollectionBeanInfo extends SimpleBeanInfo
{
	private static PropertyDescriptor[] s_desc;

	private interface IPropNames extends SQLDriverPropertyCollection.IPropertyNames
	{
		// Empty body.
	}

	public SQLDriverPropertyCollectionBeanInfo() throws IntrospectionException
	{
		super();
		if (s_desc == null)
		{
			final Class<SQLDriverPropertyCollection> clazz = 
			    SQLDriverPropertyCollection.class;
			s_desc = new PropertyDescriptor[1];
			s_desc[0] = new IndexedPropertyDescriptor(IPropNames.DRIVER_PROPERTIES,
							clazz,
							"getDriverProperties", "setDriverProperties",
							"getDriverProperty", "setDriverProperty");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_desc;
	}
}

