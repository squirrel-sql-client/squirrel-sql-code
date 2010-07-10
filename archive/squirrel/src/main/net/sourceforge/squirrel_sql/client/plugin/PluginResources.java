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
import net.sourceforge.squirrel_sql.fw.util.Resources;

public class PluginResources extends Resources {
    private IPlugin _plugin;

    public PluginResources(String rsrcBundleBaseName, IPlugin plugin)
            throws IllegalArgumentException {
        super(rsrcBundleBaseName, getClassLoader(plugin));
        _plugin = plugin;
    }

    private static ClassLoader getClassLoader(IPlugin plugin)
            throws IllegalArgumentException {
        if (plugin == null) {
            throw new IllegalArgumentException("Null IPlugin passed");
        }
        return plugin.getClass().getClassLoader();
    }
}

