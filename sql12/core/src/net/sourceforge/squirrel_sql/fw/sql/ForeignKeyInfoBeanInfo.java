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
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>ForeignKeyInfo</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ForeignKeyInfoBeanInfo extends DatabaseObjectInfoBeanInfo
									implements ForeignKeyInfo.IPropertyNames
{
	private static PropertyDescriptor[] s_dscrs;

	public ForeignKeyInfoBeanInfo() throws IntrospectionException
	{
		super();
		synchronized (getClass())
		{
			if (s_dscrs == null)
			{
				PropertyDescriptor[] sub = new PropertyDescriptor[2];
				final Class<ForeignKeyInfo> clazz = ForeignKeyInfo.class;
				sub[0] = new PropertyDescriptor(PK_CATALOG_NAME,
								clazz, "getPrimaryKeyCatalogName", null);
				sub[1] = new PropertyDescriptor(PK_SCHEMA_NAME,
								clazz, "getPrimaryKeySchemaName", null);
				PropertyDescriptor[] base = super.getPropertyDescriptors();
				if (base == null)
				{
					base = new PropertyDescriptor[0];
				}
				s_dscrs = new PropertyDescriptor[base.length + sub.length];
				System.arraycopy(base, 0, s_dscrs, 0, base.length);
				System.arraycopy(sub, 0, s_dscrs, base.length, sub.length);
			}
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_dscrs;
	}
}
