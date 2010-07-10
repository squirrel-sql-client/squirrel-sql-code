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
import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;

/**
 * Base interface for all plugins.
 */
public interface IPlugin {
    /**
     * Called on application startup before application started up.
     *
     * @param   app     Application API.
     */
    void load(IApplication app) throws PluginException;

    /**
     * Called on application startup after application started.
     */
    void initialize() throws PluginException;

    /**
     * Called when app shutdown.
     */
    void unload() throws PluginException;

    /**
     * Returns the name by which this plugin is uniquely identified.
     *
     * @return  the name by which this plugin is uniquely identified.
     */
    String getInternalName();

    /**
     * Returns the descriptive name for this plugin.
     *
     * @return  the descriptive name for this plugin.
     */
    String getDescriptiveName();

    /**
     * Returns the authors name.
     *
     * @return  the authors name.
     */
    String getAuthor();

    /**
     * Returns the home page for this plugin.
     *
     * @return  the home page for this plugin.
     */
    String getWebSite();

    /**
     * Returns the current version of this plugin.
     *
     * @return  the current version of this plugin.
     */
    String getVersion();

    /**
     * Create panels for the Global Preferences dialog.
     *
     * @return  Array of <TT>IGlobalPreferencesPanel</TT> objects. Return
     *          empty array of <TT>null</TT> if this plugin doesn't require
     *          any panels in the Global Preferences Dialog.
     */
    IGlobalPreferencesPanel[] getGlobalPreferencePanels();

	/**
	 * Return the folder with the Squirrel application folder
	 * that belongs to this plugin. If it doesn't exist then
	 * create it. This would normally be
	 * <PRE>
	 * &lt;squirrel_app&gt;/plugins/&lt;plugin_internal_name&gt;
	 * </PRE>
	 * 
	 * @return	Plugins application folder.
	 * 
	 * @throws	IllegalStateException
	 *			if plugin doesn't have an internal name.
	 * 
	 * @throws	IOException
	 * 			An error occured retrieving/creating the folder.
	 */
    File getPluginAppSettingsFolder()
		throws IOException, IllegalStateException;

	/**
	 * Return the folder with the users home directory
	 * that belongs to this plugin. If it doesn't exist then
	 * create it. This would normally be
	 * <PRE>
	 * &lt;user_home&gt;/.squirrel-sql/plugins/&lt;plugin_internal_name&gt;
	 * </PRE>
	 * 
	 * @return	Plugins user folder.
	 * 
	 * @throws	IllegalStateException
	 *			if plugin doesn't have an internal name.
	 * 
	 * @throws	IOException
	 * 			An error occured retrieving/creating the folder.
	 */
    public File getPluginUserSettingsFolder()
            throws IllegalStateException, IOException;
}

