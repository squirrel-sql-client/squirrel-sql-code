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
import java.net.URL;

public final class PluginInfo {
    private String _pluginClassName;
    private IPlugin _plugin;
    private boolean _loaded;

    PluginInfo(String pluginClassName)
            throws IllegalArgumentException {
        super();
        if (pluginClassName == null) {
            throw new IllegalArgumentException("Null pluginClassName passed");
        }

        _pluginClassName = pluginClassName;
    }
    public String getPluginClassName() {
        return _pluginClassName;
    }

    public boolean isLoaded() {
        return _loaded;
    }

    /**
     * Return the <TT>IPlugin</TT>. Warning this will be
     * <TT>null</TT> if the plugin could not be
     * instantiated.
     *
     * @return  the <TT>IPlugin</TT>.
     */
    public IPlugin getPlugin() {
        return _plugin;
    }
    void setPlugin(IPlugin value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("Null IPlugin passed");
        }
        _plugin = value;
    }

    void setLoaded(boolean value) {
        _loaded = value;
    }
}

