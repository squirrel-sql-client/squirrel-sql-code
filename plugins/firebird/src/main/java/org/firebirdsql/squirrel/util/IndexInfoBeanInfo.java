package org.firebirdsql.squirrel.util;

/*
 * Copyright (C) 2002 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>IndexInfo</CODE>.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class IndexInfoBeanInfo extends SimpleBeanInfo
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(IndexInfoBeanInfo.class);

	private static interface IPropertyNames extends IndexInfo.IPropertyNames
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
			PropertyDescriptor[] result = new PropertyDescriptor[10];
			result[0] = new PropertyDescriptor(IPropertyNames.NAME, IndexInfo.class);
			result[0].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.name"));
			result[1] = new PropertyDescriptor(IPropertyNames.DESCRIPTION, IndexInfo.class);
			result[1].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.description"));
			result[2] = new PropertyDescriptor(IPropertyNames.RELATION_NAME, IndexInfo.class);
			result[2].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.relationname"));
			result[3] = new PropertyDescriptor(IPropertyNames.ID, IndexInfo.class);
			result[3].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.id"));
			result[4] = new PropertyDescriptor(IPropertyNames.UNIQUE, IndexInfo.class);
			result[4].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.unique"));
			result[5] = new PropertyDescriptor(IPropertyNames.SEGMENT_COUNT, IndexInfo.class);
			result[5].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.segmentcount"));
			result[6] = new PropertyDescriptor(IPropertyNames.ACTIVE, IndexInfo.class);
			result[6].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.active"));
			result[7] = new PropertyDescriptor(IPropertyNames.EXPRESSION_SOURCE, IndexInfo.class);
			result[7].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.expressionsource"));
			result[8] = new PropertyDescriptor(IPropertyNames.FOREIGN_KEY_CONSTRAINT, IndexInfo.class);
			result[8].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.foreignkeyconstraint"));
			result[9] = new PropertyDescriptor(IPropertyNames.SYSTEM_DEFINED, IndexInfo.class);
			result[9].setDisplayName(s_stringMgr.getString("IndexInfoBeanInfo.systemdefined"));

			return result;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
