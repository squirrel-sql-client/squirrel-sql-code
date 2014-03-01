package net.sourceforge.squirrel_sql.fw.sql.dbobj.adapter;
/*
 * Copyright (C) 2004 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfoBeanInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
/**
 * <tt>BeanInfo</tt> class for <tt>BestRowIdentifierAdapter</tt>
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class BestRowIdentifierAdapterBeanInfo
							extends DatabaseObjectInfoBeanInfo
							implements BestRowIdentifierAdapter.IPropertyNames
{
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(BestRowIdentifierAdapterBeanInfo.class);

	private static PropertyDescriptor[] s_dscrs;

	public BestRowIdentifierAdapterBeanInfo() throws IntrospectionException
	{
		super();
		synchronized (getClass())
		{
			if (s_dscrs == null)
			{
				final Class<BestRowIdentifierAdapter> clazz = 
				    BestRowIdentifierAdapter.class;
				final PropertyDescriptor[] sub = new PropertyDescriptor[7];
				sub[0] = new PropertyDescriptor(COLUMN_NAME, clazz, "getColumnName", null);
				sub[0].setDisplayName(s_stringMgr.getString("BestRowIdentifierAdapterBeanInfo.columnName"));
				sub[1] = new PropertyDescriptor(SQL_DATA_TYPE, clazz, "getSQLDataType", null);
				sub[1].setDisplayName(s_stringMgr.getString("BestRowIdentifierAdapterBeanInfo.sqlDataType"));
				sub[2] = new PropertyDescriptor(TYPE_NAME, clazz, "getTypeName", null);
				sub[2].setDisplayName(s_stringMgr.getString("BestRowIdentifierAdapterBeanInfo.typeName"));
				sub[3] = new PropertyDescriptor(PRECISION, clazz, "getPrecision", null);
				sub[3].setDisplayName(s_stringMgr.getString("BestRowIdentifierAdapterBeanInfo.precision"));
				sub[4] = new PropertyDescriptor(SCALE, clazz, "getScale", null);
				sub[4].setDisplayName(s_stringMgr.getString("BestRowIdentifierAdapterBeanInfo.scale"));
				sub[5] = new PropertyDescriptor(SCOPE, clazz, "getScope", null);
				sub[5].setDisplayName(s_stringMgr.getString("BestRowIdentifierAdapterBeanInfo.scope"));
				sub[6] = new PropertyDescriptor(PSEUDO, clazz, "getPseudoColumn", null);
				sub[6].setDisplayName(s_stringMgr.getString("BestRowIdentifierAdapterBeanInfo.pseudo"));
				PropertyDescriptor[] base = new PropertyDescriptor[0];
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
