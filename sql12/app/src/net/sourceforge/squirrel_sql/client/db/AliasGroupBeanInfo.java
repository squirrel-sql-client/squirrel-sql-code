package net.sourceforge.squirrel_sql.client.db;
/*
 * Copyright (C) 2002 -2006 Colin Bell
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
 * This is the <CODE>BeanInfo</CODE> class for <CODE>AliasGroup</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AliasGroupBeanInfo extends SimpleBeanInfo
{
	private static final Class<AliasGroup> CLAZZ = AliasGroup.class;

	private static interface IPropertyNames extends AliasGroup.IPropertyNames
	{
		// Empty body.
	}
	
	private static PropertyDescriptor[] s_descr;

	public AliasGroupBeanInfo() throws IntrospectionException
	{
		super();
		if (s_descr == null)
		{
			s_descr = new PropertyDescriptor[2];
			s_descr[0] = new PropertyDescriptor(IPropertyNames.ID, CLAZZ, "getIdentifier", "setIdentifier");
			s_descr[0] = new PropertyDescriptor(IPropertyNames.NAME, CLAZZ, "getName", "setName");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_descr;
	}
}

