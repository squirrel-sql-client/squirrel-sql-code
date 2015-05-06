/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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

package net.sourceforge.squirrel_sql.plugins.dbdiff;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbdiff.actions.CompareAction;
import net.sourceforge.squirrel_sql.plugins.dbdiff.actions.SelectAction;
import net.sourceforge.squirrel_sql.plugins.dbdiff.prefs.DBDiffPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.dbdiff.prefs.DBDiffPreferencesPanel;
import net.sourceforge.squirrel_sql.plugins.dbdiff.prefs.DefaultPluginGlobalPreferencesTab;
import net.sourceforge.squirrel_sql.plugins.dbdiff.prefs.DefaultPluginPreferencesManager;
import net.sourceforge.squirrel_sql.plugins.dbdiff.prefs.IPluginPreferencesManager;

/**
 * The class that sets up the various resources required by SQuirreL to implement a plugin. This plugin
 * implements the ability to diff tables and various other table-related objects from one database to another.
 */
public class DBDiffPlugin extends DefaultSessionPlugin implements SessionInfoProvider
{

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(DBDiffPlugin.class);

	private PluginResources _resources;

	private ISession diffSourceSession = null;

	private ISession diffDestSession = null;

	private IDatabaseObjectInfo[] selectedSourceDatabaseObjects = null;

	private IDatabaseObjectInfo[] selectedDestDatabaseObjects = null;

	private IPluginPreferencesManager pluginPreferencesManager = new DefaultPluginPreferencesManager();

	private IScriptFileManager scriptFileManager = new ScriptFileManager();

	public static final String BUNDLE_BASE_NAME = "net.sourceforge.squirrel_sql.plugins.dbdiff.dbdiff";

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.ISessionPlugin#
	 *      sessionStarted(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	@Override
	public PluginSessionCallback sessionStarted(final ISession session)
	{
		addMenuItemsToContextMenu(session);
		return new DBDiffPluginSessionCallback(this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getInternalName()
	 */
	@Override
	public String getInternalName()
	{
		return "dbdiff";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getDescriptiveName()
	 */
	@Override
	public String getDescriptiveName()
	{
		return "DBDiff Plugin";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getAuthor()
	 */
	@Override
	public String getAuthor()
	{
		return "Rob Manning";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getContributors()
	 */
	@Override
	public String getContributors()
	{
		return "";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getVersion()
	 */
	@Override
	public String getVersion()
	{
		return "1.0";
	}

	/**
	 * Returns the name of the Help file for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 * 
	 * @return the Help file name or <TT>null</TT> if plugin doesn't have a help file.
	 */
	@Override
	public String getHelpFileName()
	{
		return "doc/readme.html";
	}

	/**
	 * Returns the name of the change log for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 * 
	 * @return the changelog file name or <TT>null</TT> if plugin doesn't have a change log.
	 */
	@Override
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getLicenceFileName()
	 */
	@Override
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	@Override
	public void initialize() throws PluginException
	{
		super.initialize();

		if (s_log.isDebugEnabled())
		{
			s_log.debug("Initializing DB Diff Plugin");
		}

		_resources = new DBDiffPluginResources(DBDiffPlugin.BUNDLE_BASE_NAME, this);
		pluginPreferencesManager.initialize(this, DBDiffPreferenceBean.class);

		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

		final SelectAction selectAction = new SelectAction(app, _resources, this);
		selectAction.setPluginPreferencesManager(pluginPreferencesManager);
		final CompareAction compareAction = new CompareAction(app, _resources, this);
		compareAction.setPluginPreferencesManager(pluginPreferencesManager);

		coll.add(selectAction);
		coll.add(compareAction);

	}

	/**
	 * Create panel for the Global Properties dialog.
	 * 
	 * @return properties panel.
	 */
	@Override
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		final DBDiffPreferencesPanel preferencesPanel = new DBDiffPreferencesPanel(pluginPreferencesManager);
		final DefaultPluginGlobalPreferencesTab tab = new DefaultPluginGlobalPreferencesTab(preferencesPanel);
		return new IGlobalPreferencesPanel[] { tab };
	}

	@Override
	public void unload()
	{
		super.unload();
		diffSourceSession = null;
		diffDestSession = null;
		pluginPreferencesManager.unload();
		scriptFileManager.cleanupScriptFiles();
	}

	/**
	 * @param selectedSourceDatabaseObjects
	 *           The selectedDatabaseObjects to set.
	 */
	public void setSelectedDatabaseObjects(IDatabaseObjectInfo[] dbObjArr)
	{
		if (dbObjArr != null)
		{
			selectedSourceDatabaseObjects = dbObjArr;
			for (int i = 0; i < dbObjArr.length; i++)
			{
				if (s_log.isDebugEnabled())
				{
					s_log.debug("setSelectedDatabaseObjects: IDatabaseObjectInfo[" + i + "]=" + dbObjArr[i]);
				}
			}
		}
	}

	/**
	 * @param coll
	 * @param api
	 */
	protected void addMenuItemsToContextMenu(ISession session)
	{
		final IObjectTreeAPI api = session.getObjectTreeAPIOfActiveSessionWindow();
		final ActionCollection coll = getApplication().getActionCollection();

		if (SwingUtilities.isEventDispatchThread())
		{
			addToPopup(api, coll);
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					addToPopup(api, coll);
				}
			});
		}
	}

	private void addToPopup(IObjectTreeAPI api, ActionCollection coll)
	{

		// Uses menu.dbdiff.* in dbdiff.properties
		final JMenu dbdiffMenu = _resources.createMenu("dbdiff");

		final JMenuItem selectItem = new JMenuItem(coll.get(SelectAction.class));
		final JMenuItem compareItem = new JMenuItem(coll.get(CompareAction.class));
		dbdiffMenu.add(selectItem);
		dbdiffMenu.add(compareItem);

		api.addToPopup(DatabaseObjectType.CATALOG, dbdiffMenu);
		api.addToPopup(DatabaseObjectType.SCHEMA, dbdiffMenu);
		api.addToPopup(DatabaseObjectType.TABLE_TYPE_DBO, dbdiffMenu);
		api.addToPopup(DatabaseObjectType.TABLE, dbdiffMenu);

	}

	public void setCompareMenuEnabled(boolean enabled)
	{
		final ActionCollection coll = getApplication().getActionCollection();
		final CompareAction compareAction = (CompareAction) coll.get(CompareAction.class);
		compareAction.setEnabled(enabled);
	}

	// Interface SessionInfoProvider implementation

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider#getSourceSession()
	 */
	@Override
	public ISession getSourceSession()
	{
		return diffSourceSession;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider#
	 *      setSourceSession(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	@Override
	public void setSourceSession(ISession session)
	{
		if (session != null)
		{
			diffSourceSession = session;
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider#getSourceSelectedDatabaseObjects()
	 */
	@Override
	public IDatabaseObjectInfo[] getSourceSelectedDatabaseObjects()
	{
		return selectedSourceDatabaseObjects;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider#getDestSession()
	 */
	@Override
	public ISession getDestSession()
	{
		return diffDestSession;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider#
	 *      setDestSession(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	@Override
	public void setDestSession(ISession session)
	{
		diffDestSession = session;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider#getDestSelectedDatabaseObjects()
	 */
	@Override
	public IDatabaseObjectInfo[] getDestSelectedDatabaseObjects()
	{
		return selectedDestDatabaseObjects;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider#
	 *      setDestSelectedDatabaseObjects(net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo[])
	 */
	@Override
	public void setDestSelectedDatabaseObjects(IDatabaseObjectInfo[] infos)
	{
		selectedDestDatabaseObjects = infos;
	}

	@Override
	public void setSourceSelectedDatabaseObjects(IDatabaseObjectInfo[] infos)
	{
		this.selectedSourceDatabaseObjects = infos;
	}

	/**
	 * @param pluginPreferencesManager
	 *           the pluginPreferencesManager to set
	 */
	public void setPluginPreferencesManager(IPluginPreferencesManager pluginPreferencesManager)
	{
		Utilities.checkNull("setPluginPreferencesManager", pluginPreferencesManager, "pluginPreferencesManager");
		this.pluginPreferencesManager = pluginPreferencesManager;
	}

	/**
	 * @param scriptFileManager
	 *           the scriptFileManager to set
	 */
	public void setScriptFileManager(IScriptFileManager scriptFileManager)
	{
		this.scriptFileManager = scriptFileManager;
	}

	/**
	 * @return the scriptFileManager
	 */
	public IScriptFileManager getScriptFileManager()
	{
		return scriptFileManager;
	}

}
