package org.gjt.sp.jedit.syntax;

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


/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>SyntaxStyle</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SyntaxStyleBeanInfo extends SimpleBeanInfo
{
	private static PropertyDescriptor[] s_descr;
	private final static Class s_cls = SyntaxStyle.class;

	public SyntaxStyleBeanInfo() throws IntrospectionException
	{
		super();

		if (s_descr == null)
		{
			s_descr = new PropertyDescriptor[4];
			s_descr[0] = new PropertyDescriptor(SyntaxStyle.IPropertyNames.ITALIC,
					s_cls, "isItalic", "setItalic");
			s_descr[1] = new PropertyDescriptor(SyntaxStyle.IPropertyNames.BOLD,
					s_cls, "isBold", "setBold");
			s_descr[2] = new PropertyDescriptor(SyntaxStyle.IPropertyNames.TEXT_RGB,
					s_cls, "getTextRGB", "setTextRGB");
			s_descr[3] = new PropertyDescriptor(SyntaxStyle.IPropertyNames.BACKGROUND_RGB,
					s_cls, "getBackgroundRGB", "setBackgroundRGB");
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_descr;
	}
}
