package net.sourceforge.squirrel_sql.client;
/*
 * Copyright (C) 2001-2006 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.client.gui.db.DataCache;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DesktopStyle;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistory;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfigurationFactory;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;
/**
 * Defines the API to do callbacks on the application.
 */
public interface IApplication
{
   public interface IMenuIDs extends MainFrame.IMenuIDs
	{
		// Empty body.
	}

	/**
	 * Return the dummy plugin used internally by the SQuirreL client
	 * when a plugin is needed for accessing API functions.
	 *
	 * @return	the dummy plugin used internally by the SQuirreL client.
	 */
	IPlugin getDummyAppPlugin();

	/**
	 * Return the plugin manager responsible for this applications plugins.
	 *
	 * @return	the plugin manager responsible for this applications plugins.
	 */
	IPluginManager getPluginManager();

	/**
	 * Return the manager responsible for windows.
	 *
	 * @return	the manager responsible for windows.
	 */
	WindowManager getWindowManager();

	ActionCollection getActionCollection();

	SQLDriverManager getSQLDriverManager();

	DataCache getDataCache();

	SquirrelPreferences getSquirrelPreferences();

   DesktopStyle getDesktopStyle();

	SquirrelResources getResources();


	/**
	 * Retrieves the message handler for the application.
	 *
	 * @return	Application level message handler.
	 */
	IMessageHandler getMessageHandler();

	/**
	 * Return an array of all the sessions currently active.
	 *
	 * @return	array of all active sessions.
	 */
	//ISession[] getActiveSessions();

	/**
	 * Retrieve the object that manages sessions.
	 *
	 * @return	<TT>SessionManager</TT>.
	 */
	SessionManager getSessionManager();

	/**
	 * Display an error message dialog.
	 *
	 * @param	msg		The error msg.
	 */
	void showErrorDialog(String msg);

	/**
	 * Display an error message dialog.
	 *
	 * @param	th		The Throwable that caused the error
	 */
	void showErrorDialog(Throwable th);

	/**
	 * Display an error message dialog.
	 *
	 * @param	msg		The error msg.
	 * @param	th		The Throwable that caused the error
	 */
	void showErrorDialog(String msg, Throwable th);

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
	ISQLEntryPanelFactory getSQLEntryPanelFactory();

	/**
	 * Retrieve the application level SQL History object.
	 *
	 * @return		the application level SQL History object.
	 */
	SQLHistory getSQLHistory();

	/**
	 * Set the factory object used to create the SQL entry panel.
	 *
	 * @param	factory	the factory object used to create the SQL entry panel.
	 */
	void setSQLEntryPanelFactory(ISQLEntryPanelFactory factory);

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
	void addToMenu(int menuId, Action action);

	/**
	 * Add component to the main frames status bar.
	 *
	 * @param	comp	Component to add.
	 */
	void addToStatusBar(JComponent comp);

	/**
	 * Remove component to the main frames status bar.
	 *
	 * @param	comp	Component to remove.
	 */
	void removeFromStatusBar(JComponent comp);

	/**
	 * Application startup processing.
	 */
	void startup();

	/**
	 * Application shutdown processing.
	 * 
	 * @param whether or not to update the launch script before shutdown.
	 */
	boolean shutdown(boolean updateLaunchScript);
    
    /**
     * Launches the specified url in the system default web-browser
     *  
     * @param url the URL of the web page to display.
     */
	void openURL(String url);

    /**
     * Saves off preferences and all state present in the application.
     */
    void saveApplicationState();
    
    /**
     * Persists the specified category of preferences to file.
     * 
     * @param preferenceType the enumerated type that indicates what category
     *                       of preferences to be persisted. 
     */
    public void savePreferences(PreferenceType preferenceType);
    

   void addApplicationListener(ApplicationListener l);

   void removeApplicationListener(ApplicationListener l);


   /**
    * Returns a factory for WIKI table configurations.
    * This factory is managing the system and user-specific configurations for WIKI tables.
    * @return A factory for WIKI table configurations.
    * @see IWikiTableConfigurationFactory
    */
   IWikiTableConfigurationFactory getWikiTableConfigFactory();

   MultipleWindowsHandler getMultipleWindowsHandler();
}
