package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import java.sql.SQLException;

import javax.swing.JComponent;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.event.ISessionListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.SQLFilterClauses;
/**
 * The current session.
 */
public interface ISession extends IHasIdentifier
{
	public interface IMainPanelTabIndexes extends MainPanel.ITabIndexes
	{
	}

	/**
	 * Retrieve whether this session has been closed.
	 *
	 * @return	<TT>true</TT> if session closed else <TT>false</TT>.
	 */
	boolean isClosed();

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
	 * Close this session.
	 *
	 * @throws	SQLException
	 * 			Thrown if an error closing the SQL connection. The session
	 * 			will still be closed even though the connection may not have
	 *			been.
	 */
	void close() throws SQLException;

	/**
	 * Close the current connection to the database.
	 *
	 * @throws	SQLException	if an SQL error occurs.
	 */
	void closeSQLConnection() throws SQLException;

	/**
	 * Set the session sheet for this session.
	 *
	 * @param	sheet	Sheet for this session.
	 */
	void setSessionSheet(SessionSheet child);

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

	SQLFilterClauses getSQLFilterClauses();

	/**
	 * Retrieve the schema information object for this session.
	 */
	SchemaInfo getSchemaInfo();

	/**
	 * Select a tab in the main tabbed pane.
	 *
	 * @param	tabIndex	The tab to select. @see #IMainTabIndexes
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

	/**
	 * Add component to the session sheets status bar.
	 *
	 * @param	comp	Component to add.
	 */
	void addToStatusBar(JComponent comp);

	/**
	 * Remove component to the session sheets status bar.
	 *
	 * @param	comp	Component to remove.
	 */
	void removeFromStatusBar(JComponent comp);

	/**
	 * Add a listener to this session
	 *
	 * @param	lis		The listener to add.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> listener passed.
	 */
	void addSessionListener(ISessionListener lis);

	/**
	 * Remove a listener from this session
	 *
	 * @param	lis		The listener to remove.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> listener passed.
	 */
	void removeSessionListener(ISessionListener lis);

	/**
	 * Retrieve the descriptive title of this session.
	 *
	 * @return		The descriptive title of this session.
	 */
	String getTitle();
}
