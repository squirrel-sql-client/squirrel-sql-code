package net.sourceforge.squirrel_sql.plugins.dbdiff;

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

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbdiff.actions.CompareAction;
import net.sourceforge.squirrel_sql.plugins.dbdiff.actions.SelectAction;

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

	private IDatabaseObjectInfo[] selectedDatabaseObjects = null;

	private IDatabaseObjectInfo[] selectedDestDatabaseObjects = null;

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.plugin.ISessionPlugin#sessionStarted(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	public PluginSessionCallback sessionStarted(final ISession session)
	{
		addMenuItemsToContextMenu(session);
		return new DBDiffPluginSessionCallback(this);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getInternalName()
	 */
	public String getInternalName()
	{
		return "dbdiff";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getDescriptiveName()
	 */
	public String getDescriptiveName()
	{
		return "DBDiff Plugin";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getAuthor()
	 */
	public String getAuthor()
	{
		return "Rob Manning";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getContributors()
	 */
	public String getContributors()
	{
		return "";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getVersion()
	 */
	public String getVersion()
	{
		return "0.01";
	}

	/**
	 * Returns the name of the Help file for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 * 
	 * @return the Help file name or <TT>null</TT> if plugin doesn't have a help file.
	 */
	public String getHelpFileName()
	{
		return "readme.html";
	}

	/**
	 * Returns the name of the change log for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 * 
	 * @return the changelog file name or <TT>null</TT> if plugin doesn't have a change log.
	 */
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

	public void initialize() throws PluginException
	{
		super.initialize();

		if (s_log.isDebugEnabled())
		{
			s_log.debug("Initializing DB Diff Plugin");
		}

		_resources = new DBDiffPluginResources("net.sourceforge.squirrel_sql.plugins.dbdiff.dbdiff", this);
		// PreferencesManager.initialize(this);

		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();
		coll.add(new SelectAction(app, _resources, this));
		coll.add(new CompareAction(app, _resources, this));

	}

	public void unload()
	{
		super.unload();
		/*
		diffSourceSession = null;
		setPasteMenuEnabled(false);
		PreferencesManager.unload();
		*/
	}

	/**
	 * @param selectedDatabaseObjects
	 *           The selectedDatabaseObjects to set.
	 */
	public void setSelectedDatabaseObjects(IDatabaseObjectInfo[] dbObjArr)
	{
		if (dbObjArr != null)
		{
			selectedDatabaseObjects = dbObjArr;
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
	 * Create panel for the Global Properties dialog.
	 * 
	 * @return properties panel.
	 */
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		/*
		DBCopyGlobalPreferencesTab tab = new DBCopyGlobalPreferencesTab();
		return new IGlobalPreferencesPanel[] { tab };
		*/
		return new IGlobalPreferencesPanel[0];
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

	private class DBDiffPluginResources extends PluginResources
	{
		DBDiffPluginResources(String rsrcBundleBaseName, IPlugin plugin)
		{
			super(rsrcBundleBaseName, plugin);
		}
	}

	private void addToPopup(IObjectTreeAPI api, ActionCollection coll)
	{

		// Uses menu.dbdiff.* in dbdiff.properties
		JMenu dbdiffMenu = _resources.createMenu("dbdiff");

		JMenuItem selectItem = new JMenuItem(coll.get(SelectAction.class));
		JMenuItem compareItem = new JMenuItem(coll.get(CompareAction.class));
		dbdiffMenu.add(selectItem);
		dbdiffMenu.add(compareItem);

		api.addToPopup(DatabaseObjectType.CATALOG, dbdiffMenu);
		api.addToPopup(DatabaseObjectType.SCHEMA, dbdiffMenu);
		api.addToPopup(DatabaseObjectType.TABLE_TYPE_DBO, dbdiffMenu);
		api.addToPopup(DatabaseObjectType.TABLE, dbdiffMenu);

	}

	/*
	private class DBCopyPluginResources extends PluginResources {
	DBCopyPluginResources(String rsrcBundleBaseName, IPlugin plugin) {
	    super(rsrcBundleBaseName, plugin);
	}
	}
	*/
	public void setCompareMenuEnabled(boolean enabled)
	{
		final ActionCollection coll = getApplication().getActionCollection();
		CompareAction compareAction = (CompareAction) coll.get(CompareAction.class);
		compareAction.setEnabled(enabled);
	}

	// Interface SessionInfoProvider implementation

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#getCopySourceSession()
	 */
	public ISession getDiffSourceSession()
	{
		return diffSourceSession;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#setCopySourceSession(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	public void setDiffSourceSession(ISession session)
	{
		if (session != null)
		{
			diffSourceSession = session;
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#getSelectedDatabaseObjects()
	 */
	public IDatabaseObjectInfo[] getSourceSelectedDatabaseObjects()
	{
		return selectedDatabaseObjects;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#getCopyDestSession()
	 */
	public ISession getDiffDestSession()
	{
		return diffDestSession;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#setDestCopySession(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	public void setDestDiffSession(ISession session)
	{
		diffDestSession = session;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#getDestSelectedDatabaseObject()
	 */
	public IDatabaseObjectInfo[] getDestSelectedDatabaseObjects()
	{
		return selectedDestDatabaseObjects;
	}

	public void setDestSelectedDatabaseObjects(IDatabaseObjectInfo[] info)
	{
		selectedDestDatabaseObjects = info;
	}
}
