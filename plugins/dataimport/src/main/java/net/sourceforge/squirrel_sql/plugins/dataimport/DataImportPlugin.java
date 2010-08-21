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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallbackAdaptor;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.plugins.dataimport.action.ImportTableDataAction;
import net.sourceforge.squirrel_sql.plugins.dataimport.prefs.PreferencesManager;

/**
 * Plugin to import data into a table
 * 
 * @author Thorsten Mürell
 */
public class DataImportPlugin extends DefaultSessionPlugin {
	private Resources resources = null;

	/**
	 * Return the internal name of this plugin.
	 *
	 * @return  the internal name of this plugin.
	 */
	public String getInternalName() {
		return "dataimport";
	}

	/**
	 * Return the descriptive name of this plugin.
	 *
	 * @return  the descriptive name of this plugin.
	 */
	public String getDescriptiveName() {
		return "Data Import Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return  the current version of this plugin.
	 */
	public String getVersion() {
		return "0.05";
	}

	/**
	 * Returns the authors name.
	 *
	 * @return  the authors name.
	 */
	public String getAuthor() {
		return "Thorsten Mürell";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getContributors()
	 */
	@Override
	public String getContributors() {
		return "Guido Wojke";
	}


	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getChangeLogFileName()
	 */
	@Override
	public String getChangeLogFileName() {
		return "changes.txt";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getLicenceFileName()
	 */
	@Override
	public String getLicenceFileName() {
		return "licence.txt";
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getHelpFileName()
	 */
	@Override
	public String getHelpFileName() {
		return "readme.html";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#load(net.sourceforge.squirrel_sql.client.IApplication)
	 */
	@Override
	public void load(IApplication app) throws PluginException {
		super.load(app);
		resources = new Resources(getClass().getName(), this);
	}

	/**
	 * Initialize this plugin.
	 */
	@Override
	public synchronized void initialize() throws PluginException {
		super.initialize();
		
		PreferencesManager.initialize(this);

		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();

		coll.add(new ImportTableDataAction(app, resources));
	}

	/**
	 * Application is shutting down so save preferences.
	 */
	@Override
	public void unload() {
		super.unload();
		PreferencesManager.unload();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin#allowsSessionStartedInBackground()
	 */
	@Override
	public boolean allowsSessionStartedInBackground()
	{
		return true;
	}

	/**
	 * Called when a session started.
	 *
	 * @param   session     The session that is starting.
	 *
	 * @return  <TT>true</TT> to indicate that this plugin is
	 *          applicable to passed session.
	 */
	public PluginSessionCallback sessionStarted(final ISession session) {
		updateTreeApi(session);
		return new PluginSessionCallbackAdaptor(this);
	}

	/**
	 * @param session
	 */
	private void updateTreeApi(ISession session) {
		IObjectTreeAPI treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();
		final ActionCollection coll = getApplication().getActionCollection();

		treeAPI.addToPopup(DatabaseObjectType.TABLE, coll.get(ImportTableDataAction.class));        
	}

	/**
	 * Create preferences panel for the Global Preferences dialog.
	 *
	 * @return  Preferences panel.
	 */
	@Override
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
		// Not yet ready
        // DataImportGlobalPreferencesTab tab = new DataImportGlobalPreferencesTab();
        // return new IGlobalPreferencesPanel[] { tab };
        return new IGlobalPreferencesPanel[] { };
	}
}