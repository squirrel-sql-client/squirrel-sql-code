package net.sourceforge.squirrel_sql.client;
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
import java.awt.Font;

import javax.swing.Action;
import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.db.DataCache;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;

/**
 * Defines the API to do callbacks on the application.
 */
public interface IApplication {
	public interface IMenuIDs extends MainFrame.IMenuIDs {
	}

	IPlugin getDummyAppPlugin();
	PluginManager getPluginManager();

	ActionCollection getActionCollection();

	SQLDriverManager getSQLDriverManager();

	DataCache getDataCache();

	SquirrelPreferences getSquirrelPreferences();

	SquirrelResources getResources();

	/**
	 * Return the main frame.
	 *
	 * @return	The main frame for the app.
	 */
	MainFrame getMainFrame();

	/**
	 * Return the thread pool for this app.
	 *
	 * @return	the thread pool for this app.
	 */
	TaskThreadPool getThreadPool();

	/**
	 * Return the collection of <TT>FontInfo </TT> objects for this app.
	 * 
	 * @return	the collection of <TT>FontInfo </TT> objects for this app.
	 */
	FontInfoStore getFontInfoStore();

	/**
	 * Return the factory object used to create the SQL entry panel.
	 *
	 * @return	the factory object used to create the SQL entry panel.
	 */
	public ISQLEntryPanelFactory getSQLEntryPanelFactory();

	/**
	 * Set the factory object used to create the SQL entry panel.
	 *
	 * @param	factory	the factory object used to create the SQL entry panel.
	 */
	public void setSQLEntryPanelFactory(ISQLEntryPanelFactory factory);

	/**
	 * Add a hierarchical menu to a menu.
	 *
	 * @param	menuId	ID of menu to add to. @see #IMenuIDs
	 * @param	menu	The menu that will be added.
	 */
	void addToMenu(int menuId, JMenu menu);

	/**
	 * Add an <TT>Action</TT> to a menu.
	 *
	 * @param	menuId	ID of menu to add to. @see #IMenuIDs
	 * @param	action	The action to be added.
	 */
	public void addToMenu(int menuId, Action action);

	/**
	 * Application startup processing.
	 */
	void startup();

	/**
	 * Application shutdown processing.
	 */
	void shutdown();
}


