package net.sourceforge.squirrel_sql.plugins.dataimport;
/*
 * Copyright (C) 2001 Like Gao
 * lgao@gmu.edu
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
import java.util.HashMap;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.plugins.dataimport.action.ImportTableDataAction;
import net.sourceforge.squirrel_sql.plugins.dataimport.gui.DataImportGlobalPreferencesTab;
import net.sourceforge.squirrel_sql.plugins.dataimport.prefs.PreferencesManager;

public class DataImportPlugin extends DefaultSessionPlugin {
	/** Plugin preferences. */
//	private Preferences _lafPrefs;

	/** The app folder for this plugin. */
	private File _pluginAppFolder;

	/** Folder to store user settings in. */
	private File _userSettingsFolder;

	private HashMap<ISession, FileImportTab> sessionMap = 
		new HashMap<ISession, FileImportTab>();

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
		return "0.02";
	}

	/**
	 * Returns the authors name.
	 *
	 * @return  the authors name.
	 */
	public String getAuthor() {
		return "Like Gao";
	}

	@Override
	public String getContributors() {
		return "Thorsten Mürell";
	}


	@Override
	public String getChangeLogFileName() {
		return "changes.txt";
	}

	@Override
	public String getLicenceFileName() {
		return "licence.txt";
	}

	@Override
	public void load(IApplication app) throws PluginException {
		super.load(app);
		resources = new Resources(getClass().getName(), this);
	}

	/**
	 * Initialize this plugin.
	 */
	public synchronized void initialize() throws PluginException {
		super.initialize();
		// Folder within plugins folder that belongs to this
		// plugin.
		try {
			_pluginAppFolder = getPluginAppSettingsFolder();
		} catch (IOException ex) {
			throw new PluginException(ex);
		}

		// Folder to store user settings.
		try {
			_userSettingsFolder = getPluginUserSettingsFolder();
		} catch (IOException ex) {
			throw new PluginException(ex);
		}

		PreferencesManager.initialize(this);

		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();

		coll.add(new ImportTableDataAction(app, resources));
	}

	/**
	 * Application is shutting down so save preferences.
	 */
	public void unload() {
		super.unload();
		PreferencesManager.unload();
	}

	public boolean allowsSessionStartedInBackground()
	{
		return true;
	}

	/**
	 * Called when a session started. Add File Import tab to session window.
	 *
	 * @param   session     The session that is starting.
	 *
	 * @return  <TT>true</TT> to indicate that this plugin is
	 *          applicable to passed session.
	 */
	public PluginSessionCallback sessionStarted(final ISession session) {
		/*
		GUIUtils.processOnSwingEventThread(new Runnable() {
			public void run() {
				session.addMainTab(new FileImportTab(session));        
			}
		});
		*/

		updateTreeApi(session);
		return new PluginSessionCallback()
		{
			public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
			{
				// Only supports Session main window
			}

			public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
			{
				// Only supports Session main window
			}
		};
	}

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
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
        DataImportGlobalPreferencesTab tab = new DataImportGlobalPreferencesTab();
        return new IGlobalPreferencesPanel[] { tab };
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin#sessionEnding(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	@Override
	public void sessionEnding(ISession session) {
		FileImportTab tab = sessionMap.get(session);
		if (tab != null) {
//			tab.sessionEnding(session);
		}
	}
}