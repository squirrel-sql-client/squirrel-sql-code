package net.sourceforge.squirrel_sql.client.plugin;
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
public class SessionPluginInfo extends PluginInfo {
	public SessionPluginInfo(PluginInfo pi) throws IllegalArgumentException {
		super(getPassedPluginClassName(pi));
		assignFrom(pi);
	}

	public ISessionPlugin getSessionPlugin() {
		return (ISessionPlugin)getPlugin();
	}

	void setPlugin(IPlugin value) throws IllegalArgumentException {
		if (value == null) {
			throw new IllegalArgumentException("Null IPlugin passed");
		}
		if (!(value instanceof ISessionPlugin)) {
			throw new IllegalArgumentException("Plugin not an ISessionPlugin");
		}
		super.setPlugin(value);
	}
	

	private static String getPassedPluginClassName(PluginInfo pi)
			throws IllegalArgumentException {
		if (pi == null) {
			throw new IllegalArgumentException("Null PluginInfo passed");
		}
		return pi.getPluginClassName();
	}
}

