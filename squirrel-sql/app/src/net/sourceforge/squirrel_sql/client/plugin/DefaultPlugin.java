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
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;

public abstract class DefaultPlugin implements IPlugin {
    private IApplication _app;

    /**
     * Called on application startup before application started up.
     *
     * @param   app     Application API.
     */
    public void load(IApplication app) throws PluginException {
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }
        _app = app;
    }

    /**
     * Called on application startup after application started.
     */
    public void initialize() throws PluginException {
    }

    /**
     * Called when app shutdown.
     */
    public void unload() {
    }

    /**
     * Returns the home page for this plugin.
     *
     * @return  the home page for this plugin.
     */
    public String getWebSite() {
        return Version.getWebSite();
    }

    protected final IApplication getApplication() {
        return _app;
    }

    /**
     * Create panels for the Global Preferences dialog.
     *
     * @return  <TT>null</TT> to indicate that this plugin doesn't require
     *          any panels in the Global Preferences Dialog.
     */
    public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
        return null;
    }
}