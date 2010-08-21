package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001-20064 Colin Bell
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
 * This is the <CODE>BeanInfo</CODE> class for <CODE>WindowState</CODE>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class WindowStateBeanInfo extends SimpleBeanInfo
{

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
			PropertyDescriptor[] s_dscrs = new PropertyDescriptor[3];
			s_dscrs[0] = new PropertyDescriptor(WindowState.IPropertyNames.BOUNDS,
												WindowState.class,
												"getBounds", "setBounds");
			s_dscrs[1] = new PropertyDescriptor(WindowState.IPropertyNames.VISIBLE,
												WindowState.class,
												"isVisible", "setVisible");
			s_dscrs[2] = new PropertyDescriptor(WindowState.IPropertyNames.FRAME_EXTENDED_STATE,
					WindowState.class,
					"getFrameExtendedState", "setFrameExtendedState");
			
			return s_dscrs;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
