package net.sourceforge.squirrel_sql.plugins.favs;
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
import java.io.IOException;

import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

public class SavedQueriesPlugin extends DefaultPlugin {
	private PluginResources _resources;
	private FoldersCache _cache;

	private interface IMenuResourceKeys {
		String QUERIES = "queries";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getInternalName()
	 */
	public String getInternalName() {
		return "favs";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getDescriptiveName()
	 */
	public String getDescriptiveName() {
		return "Saved Queries Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return  the current version of this plugin.
	 */
	public String getVersion() {
		return "0.1";
	}

	/**
	 * Returns the authors name.
	 *
	 * @return  the authors name.
	 */
	public String getAuthor() {
		return "Udi Ipalawatte";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getChangeLogFileName()
	 */
	@Override
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getHelpFileName()
	 */
	@Override
	public String getHelpFileName()
	{
		return "readme.txt";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getLicenceFileName()
	 */
	@Override
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#initialize()
	 */
	public void initialize() throws PluginException {
		super.initialize();
		IApplication app = getApplication();
		try {
			_cache = new FoldersCache(app, getPluginUserSettingsFolder());
		} catch (IOException ex) {
			throw new PluginException(ex);
		}
		_cache.load();

		_resources = new PluginResources("net.sourceforge.squirrel_sql.plugins.favs.saved_queries", this);

		ActionCollection coll = app.getActionCollection();

		coll.add(new DeleteSavedQueriesFolderAction(app, _resources));
		coll.add(new NewSavedQueriesFolderAction(app, _resources));
		coll.add(new OrganizeSavedQueriesAction(app, _resources, _cache));
		coll.add(new RenameSavedQueriesFolderAction(app, _resources));

		createMenu();
	}

	/**
	 * @see IPlugin#unload()
	 */
	public void unload() {
		_cache.save();
		super.unload();
	}
	private void createMenu() {
		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();

		JMenu menu = _resources.createMenu(IMenuResourceKeys.QUERIES);
		_resources.addToMenu(coll.get(OrganizeSavedQueriesAction.class), menu);
		menu.addSeparator();

		app.addToMenu(IApplication.IMenuIDs.PLUGINS_MENU, menu);
	}
}


