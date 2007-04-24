package net.sourceforge.squirrel_sql.plugins.sqlparam;
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
 * Plugin resources.
 *
 * @author Thorsten Mürell
 */
public final class Resources extends PluginResources
{
	public Resources(IPlugin plugin) {
		super(Resources.class.getName(), plugin);
	}
}
