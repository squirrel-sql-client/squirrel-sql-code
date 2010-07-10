package net.sourceforge.squirrel_sql.client.plugin;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
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
import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;

/**
 * Base interface for all plugins.
 */
public interface IPlugin
{
	/**
	 * Called on application startup before application started up.
	 *
	 * @param	app	Application API.
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
	 * @return	the name by which this plugin is uniquely identified.
	 */
	String getInternalName();

	/**
	 * Returns the descriptive name for this plugin.
	 *
	 * @return	the descriptive name for this plugin.
	 */
	String getDescriptiveName();

	/**
	 * Returns the authors name.
	 *
	 * @return	the authors name.
	 */
	String getAuthor();

	/**
	 * Returns a comma separated list of other contributors.
	 *
	 * @return	Contributors names.
	 */
	String getContributors();

	/**
	 * Returns the home page for this plugin.
	 *
	 * @return	the home page for this plugin.
	 */
	String getWebSite();

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return	the current version of this plugin.
	 */
	String getVersion();

	/**
	 * Returns the name of the Help file for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the Help file name or <TT>null</TT> if plugin doesn't have
	 * 			a help file.
	 */
	String getHelpFileName();

	/**
	 * Returns the name of the change log for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the changelog file name or <TT>null</TT> if plugin doesn't have
	 * 			a change log.
	 */
	String getChangeLogFileName();

	/**
	 * Returns the name of the licence file for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the licence file name or <TT>null</TT> if plugin doesn't have
	 * 			a change log.
	 */
	String getLicenceFileName();

	/**
	 * Create panels for the Global Preferences dialog.
	 *
	 * @return	Array of <TT>IGlobalPreferencesPanel</TT> objects. Return
	 *			empty array of <TT>null</TT> if this plugin doesn't require
	 *			any panels in the Global Preferences Dialog.
	 */
	IGlobalPreferencesPanel[] getGlobalPreferencePanels();

	/**
	 * Create panels for the New Session Properties dialog.
	 *
	 * @return	Array of <TT>INewSessionPropertiesPanel</TT> objects. Return
	 *			empty array of <TT>null</TT> if this plugin doesn't require
	 *			any panels in the New Session Properties Dialog.
	 */
	INewSessionPropertiesPanel[] getNewSessionPropertiesPanels();

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
	File getPluginAppSettingsFolder() throws IOException, IllegalStateException;

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
	File getPluginUserSettingsFolder() throws IllegalStateException, IOException;
}