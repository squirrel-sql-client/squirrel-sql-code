package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001 Colin Bell
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
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

/**
 * The current session. This is a "subset" of the real session which can be
 * used by plugins.
 */
public interface ISession extends IHasIdentifier
{
	public interface IMainPanelTabIndexes extends MainPanel.ITabIndexes
	{
	}

	/**
	 * Return the Application API object.
	 *
	 * @return the Application API object.
	 */
	IApplication getApplication();

	/**
	 * Return the current SQL connection object.
	 *
	 * @return the current SQL connection object.
	 */
	SQLConnection getSQLConnection();

	/**
	 * Return the driver used to connect to the database.
	 *
	 * @return the driver used to connect to the database.
	 */
	ISQLDriver getDriver();

	/**
	 * Return the alias used to connect to the database.
	 *
	 * @return the alias used to connect to the database.
	 */
	ISQLAlias getAlias();

	/**
	 * Return the properties for this session.
	 *
	 * @return the properties for this session.
	 */
	SessionProperties getProperties();

	/**
	 * Reconnect to the database.
	 */
	void reconnect();

	Object getPluginObject(IPlugin plugin, String key);
	Object putPluginObject(IPlugin plugin, String key, Object obj);
	void removePluginObject(IPlugin plugin, String key);

	void setMessageHandler(IMessageHandler handler);
	IMessageHandler getMessageHandler();

	/**
	 * Return the API object for the Object Tree.
	 * 
	 * @param	plugin	Plugin requesting the API.
	 * 
	 * @return	the API object for the Object Tree.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if null IPlugin passed.
	 */
	IObjectTreeAPI getObjectTreeAPI(IPlugin plugin);

	/**
	 * Return the API object for the SQL panel.
	 * 
	 * @param	plugin	Plugin requesting the API.
	 * 
	 * @return	the API object for the SQL panel.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if null IPlugin passed.
	 */
	ISQLPanelAPI getSQLPanelAPI(IPlugin plugin);



	SessionSheet getSessionSheet();

	/**
	 * Select a tab in the main tabbed pane.
	 *
	 * @param	tabIndex   The tab to select. @see #IMainTabIndexes
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if an invalid <TT>tabIndex</TT> passed.
	 */
	void selectMainTab(int tabIndex) throws IllegalArgumentException;

	/**
	 * Add a tab to the main tabbed panel.
	 *
	 * @param	tab	 The tab to be added.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IMainPanelTab</TT> passed.
	 */
	void addMainTab(IMainPanelTab tab);
}
