/*
 * Copyright (C) 2008 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client.plugin;

import java.net.URL;
import java.util.Comparator;

/**
 * A simple ordering comparator for ensuring that the Refactoring plugin is always loaded last.
 * Since it requires that the objects for vendor-specific plugins be in the tree prior to loading, the 
 * refactoring plugin should always be loaded last. 
 * 
 * @author manningr
 */
public class PluginLoadOrderComparator implements Comparator<URL>
{

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(URL plugin1, URL plugin2)
	{
		if (plugin1.toString().endsWith("refactoring.jar")) {
			return 1;
		}
		if (plugin2.toString().endsWith("refactoring.jar")) {
			return -1;
		}
		return 0;
	}

}
