package net.sourceforge.squirrel_sql.plugins.favs;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>Folder</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public final class FolderBeanInfo extends SimpleBeanInfo {


	/**
	 * See http://tinyurl.com/63no6t for discussion of the proper thread-safe way to implement
	 * getPropertyDescriptors().
	 * 
	 * @see java.beans.SimpleBeanInfo#getPropertyDescriptors()
	 */
	@Override		public PropertyDescriptor[] getPropertyDescriptors() {
		try
		{
			PropertyDescriptor[] s_descriptors = new PropertyDescriptor[3];
			s_descriptors[0] = new PropertyDescriptor(Folder.IPropertyNames.ID, Folder.class, "getIdentifier", "setIdentifier");
			s_descriptors[1] = new PropertyDescriptor(Folder.IPropertyNames.NAME, Folder.class, "getName", "setName");
			s_descriptors[2] = new IndexedPropertyDescriptor(Folder.IPropertyNames.SUB_FOLDERS, Folder.class, "getSubFolders", "setSubFolders", "getSubFolder", "setSubFolder");

			return s_descriptors;
		}
		catch (IntrospectionException e)
		{
			throw new Error(e);
		}
	}
}
