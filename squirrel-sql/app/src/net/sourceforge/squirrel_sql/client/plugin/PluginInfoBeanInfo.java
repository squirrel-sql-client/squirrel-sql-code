package net.sourceforge.squirrel_sql.client.plugin;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
/**
 * This is the <CODE>BeanInfo</CODE> class for <CODE>Folder</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public final class PluginInfoBeanInfo extends SimpleBeanInfo
{
	public interface PropertyNames
	{
		String AUTHOR = "author";
		String CONTRIBUTORS = "contributors";
		String DESCRIPTIVE_NAME = "descriptiveName";
		String INTERNAL_NAME = "internalName";
		String IS_LOADED = "isLoaded";
		String PLUGIN_CLASS_NAME = "pluginClassName";
		String VERSION = "version";
		String WEB_SITE = "webSite";
	}

	private static PropertyDescriptor[] s_descriptors;

	public PluginInfoBeanInfo() throws IntrospectionException
	{
		super();
		if (s_descriptors == null)
		{
			s_descriptors = new PropertyDescriptor[8];
			int idx = 0;
			s_descriptors[idx++] = 	new PropertyDescriptor(
					PropertyNames.PLUGIN_CLASS_NAME, PluginInfo.class,
					"getPluginClassName", null);
			s_descriptors[idx++] = 	new PropertyDescriptor(
					PropertyNames.IS_LOADED, PluginInfo.class,
					"isLoaded", null);
			s_descriptors[idx++] = 	new PropertyDescriptor(
					PropertyNames.INTERNAL_NAME, PluginInfo.class,
					"getInternalName", null);
			s_descriptors[idx++] = 	new PropertyDescriptor(
					PropertyNames.DESCRIPTIVE_NAME, PluginInfo.class,
					"getDescriptiveName", null);
			s_descriptors[idx++] = 	new PropertyDescriptor(
					PropertyNames.AUTHOR, PluginInfo.class,
					"getAuthor", null);
			s_descriptors[idx++] = 	new PropertyDescriptor(
					PropertyNames.CONTRIBUTORS, PluginInfo.class,
					"getContributors", null);
			s_descriptors[idx++] = 	new PropertyDescriptor(
					PropertyNames.WEB_SITE, PluginInfo.class,
					"getWebSite", null);
			s_descriptors[idx++] = 	new PropertyDescriptor(
					PropertyNames.VERSION, PluginInfo.class,
					"getVersion", null);
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return s_descriptors;
	}
}
