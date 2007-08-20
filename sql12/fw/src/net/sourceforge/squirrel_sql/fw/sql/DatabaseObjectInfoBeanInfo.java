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
 * This is the <CODE>BeanInfo</CODE> class for <CODE>DatabaseObjectInfo</CODE>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DatabaseObjectInfoBeanInfo extends SimpleBeanInfo
{
	private static PropertyDescriptor[] s_dscrs;

	public DatabaseObjectInfoBeanInfo() throws IntrospectionException
	{
		super();
		synchronized (getClass())
		{
			if (s_dscrs == null)
			{
				s_dscrs = new PropertyDescriptor[4];
				final Class<DatabaseObjectInfo> clazz = DatabaseObjectInfo.class;
				int idx = 0;
				s_dscrs[idx++] = new PropertyDescriptor(
								DatabaseObjectInfo.IPropertyNames.CATALOG_NAME,
								clazz, "getCatalogName", null);
				s_dscrs[idx++] = new PropertyDescriptor(
								DatabaseObjectInfo.IPropertyNames.SCHEMA_NAME,
								clazz, "getSchemaName", null);
				s_dscrs[idx++] = new PropertyDescriptor(
								DatabaseObjectInfo.IPropertyNames.SIMPLE_NAME,
								clazz, "getSimpleName", null);
				s_dscrs[idx++] = new PropertyDescriptor(
								DatabaseObjectInfo.IPropertyNames.QUALIFIED_NAME,
								clazz, "getQualifiedName", null);
			}
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_dscrs;
	}
}
