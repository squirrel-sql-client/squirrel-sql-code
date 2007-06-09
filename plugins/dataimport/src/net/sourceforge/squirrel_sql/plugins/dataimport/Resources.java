package net.sourceforge.squirrel_sql.plugins.dataimport;
/*
 * Copyright (C) 2007 Thorsten Mürell
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

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

/**
 * Resources class
 * 
 * @author Thorsten Mürell
 */
public class Resources extends PluginResources {
	/**
	 * Standard constructor
	 * 
	 * @param rsrcBundleBaseName
	 * @param plugin
	 */
	public Resources(String rsrcBundleBaseName, IPlugin plugin) {
		super(rsrcBundleBaseName, plugin);
	}
}
