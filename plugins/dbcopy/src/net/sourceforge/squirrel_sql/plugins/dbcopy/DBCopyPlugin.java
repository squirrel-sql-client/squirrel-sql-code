package net.sourceforge.squirrel_sql.plugins.dbcopy;

/*
 * Copyright (C) 2005 Rob Manning
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
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbcopy.actions.CopyTableAction;
import net.sourceforge.squirrel_sql.plugins.dbcopy.actions.PasteTableAction;
import net.sourceforge.squirrel_sql.plugins.dbcopy.gui.DBCopyGlobalPreferencesTab;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.PreferencesManager;

/**
 * The class that sets up the various resources required by SQuirreL to implement a plugin. This plugin
 * implements the ability to copy tables and various other table-related objects from one database to another.
 */
public class DBCopyPlugin extends DefaultSessionPlugin implements SessionInfoProvider
{

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(DBCopyPlugin.class);

	private PluginResources _resources;

	private ISession copySourceSession = null;

	private ISession copyDestSession = null;

	private IDatabaseObjectInfo[] selectedDatabaseObjects = null;

	private IDatabaseObjectInfo selectedDestDatabaseObject = null;

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.ISessionPlugin#sessionStarted(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	public PluginSessionCallback sessionStarted(final ISession session)
	{
		IObjectTreeAPI api = session.getObjectTreeAPIOfActiveSessionWindow();
		addMenuItemsToContextMenu(api);
		return new DBCopyPluginSessionCallback(this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin#sessionEnding(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	public void sessionEnding(final ISession session)
	{
		if (session.equals(copySourceSession))
		{
			copySourceSession = null;
			// Can't paste from a session that is no longer around.
			setPasteMenuEnabled(false);
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getInternalName()
	 */
	public String getInternalName()
	{
		return "dbcopy";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getDescriptiveName()
	 */
	public String getDescriptiveName()
	{
		return "DBCopy Plugin";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getAuthor()
	 */
	public String getAuthor()
	{
		return "Rob Manning";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getContributors()
	 */
	public String getContributors()
	{
		return "Dan Dragut";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getVersion()
	 */
	public String getVersion()
	{
		return "1.14";
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
		return "license.txt";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#initialize()
	 */
	public void initialize() throws PluginException
	{
		super.initialize();
		// md = new MemoryDiagnostics();
		if (s_log.isDebugEnabled())
		{
			s_log.debug("Initializing DB Copy Plugin");
		}
		_resources = new DBCopyPluginResources("net.sourceforge.squirrel_sql.plugins.dbcopy.dbcopy", this);
		PreferencesManager.initialize(this);

		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();
		coll.add(new CopyTableAction(app, _resources, this));
		coll.add(new PasteTableAction(app, _resources, this));

		setPasteMenuEnabled(false);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#unload()
	 */
	public void unload()
	{
		super.unload();
		copySourceSession = null;
		setPasteMenuEnabled(false);
		PreferencesManager.unload();
	}

	/**
	 * @param enabled
	 */
	public void setCopyMenuEnabled(boolean enabled)
	{
		final ActionCollection coll = getApplication().getActionCollection();
		CopyTableAction copyAction = (CopyTableAction) coll.get(CopyTableAction.class);
		copyAction.setEnabled(enabled);
	}

	/**
	 * @param enabled
	 */
	public void setPasteMenuEnabled(final boolean enabled)
	{
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				final ActionCollection coll = getApplication().getActionCollection();
				PasteTableAction pasteAction = (PasteTableAction) coll.get(PasteTableAction.class);
				pasteAction.setEnabled(enabled);
			}
		});
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
		DBCopyGlobalPreferencesTab tab = new DBCopyGlobalPreferencesTab();
		return new IGlobalPreferencesPanel[] { tab };
	}

	/**
	 * @param coll
	 * @param api
	 */
	protected void addMenuItemsToContextMenu(final IObjectTreeAPI api)
	{
		// final IObjectTreeAPI api = Compat.getIObjectTreeAPI(session, this);
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

	/**
	 * @param api
	 * @param coll
	 */
	private void addToPopup(IObjectTreeAPI api, ActionCollection coll)
	{

		api.addToPopup(DatabaseObjectType.TABLE_TYPE_DBO, coll.get(CopyTableAction.class));

		api.addToPopup(DatabaseObjectType.TABLE_TYPE_DBO, coll.get(PasteTableAction.class));

		// Copy action object tree types
		api.addToPopup(DatabaseObjectType.TABLE, coll.get(CopyTableAction.class));

		api.addToPopup(DatabaseObjectType.TABLE, coll.get(PasteTableAction.class));

		// Paste action object tree types
		api.addToPopup(DatabaseObjectType.SCHEMA, coll.get(PasteTableAction.class));

		// MySQL shows databases as "CATALOGS" not "SCHEMAS"
		api.addToPopup(DatabaseObjectType.CATALOG, coll.get(PasteTableAction.class));

		api.addToPopup(DatabaseObjectType.SESSION, coll.get(PasteTableAction.class));

	}

	private class DBCopyPluginResources extends PluginResources
	{
		DBCopyPluginResources(String rsrcBundleBaseName, IPlugin plugin)
		{
			super(rsrcBundleBaseName, plugin);
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#getCopySourceSession()
	 */
	public ISession getCopySourceSession()
	{
		return copySourceSession;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#setCopySourceSession(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	public void setCopySourceSession(ISession session)
	{
		if (session != null)
		{
			copySourceSession = session;
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#getSourceSelectedDatabaseObjects()
	 */
	public IDatabaseObjectInfo[] getSourceSelectedDatabaseObjects()
	{
		return selectedDatabaseObjects;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#getCopyDestSession()
	 */
	public ISession getCopyDestSession()
	{
		return copyDestSession;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#setDestCopySession(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	public void setDestCopySession(ISession session)
	{
		copyDestSession = session;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#getDestSelectedDatabaseObject()
	 */
	public IDatabaseObjectInfo getDestSelectedDatabaseObject()
	{
		return selectedDestDatabaseObject;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider#setDestSelectedDatabaseObject(net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo)
	 */
	public void setDestSelectedDatabaseObject(IDatabaseObjectInfo info)
	{
		selectedDestDatabaseObject = info;
	}
}
