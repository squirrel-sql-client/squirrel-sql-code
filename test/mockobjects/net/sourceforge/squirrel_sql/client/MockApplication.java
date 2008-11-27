/*
 * Copyright (C) 2006 Rob Manning
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
package net.sourceforge.squirrel_sql.client;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.client.gui.db.DataCache;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.MockSQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.MockSessionManager;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistory;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.MockMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;

public class MockApplication implements IApplication {

    TaskThreadPool threadPool = null;
    IPluginManager pluginManager = null;
    ActionCollection actions = null;
    SquirrelResources resource = null;
    SquirrelPreferences prefs = null;
    SQLHistory history = null;
    MockSQLEntryPanelFactory sqlEntryPanelFactory = null;
    IMessageHandler messageHandler = null;
    MockSessionManager sessionHandler = null;
    FontInfoStore fontInfoStore = null;
    
    public MockApplication() {
        resource = 
            new SquirrelResources("net.sourceforge.squirrel_sql.client.resources.squirrel");
        prefs = SquirrelPreferences.load();
        threadPool = new TaskThreadPool();
        pluginManager = new PluginManager(this);
        actions = new ActionCollection(this);
        history = new SQLHistory();
        sqlEntryPanelFactory = new MockSQLEntryPanelFactory();
        messageHandler = new MockMessageHandler();
        sessionHandler = new MockSessionManager(this);
        fontInfoStore = new FontInfoStore();
    }
    
    public MockSessionManager getMockSessionManager() {
    	return sessionHandler;
    }
    
    public IPlugin getDummyAppPlugin() {
        
    	System.err.println("MockApplication.getDummyAppPlugin: stub not yet implemented");
        return null;
    }

    public IPluginManager getPluginManager() {
        return pluginManager;
    }

    public WindowManager getWindowManager() {
        
    	System.err.println("MockApplication.getWindowManager: stub not yet implemented");    	
        return null;
    }

    public ActionCollection getActionCollection() {
        return actions;
    }

    public SQLDriverManager getSQLDriverManager() {
        
    	System.err.println("MockApplication.getSQLDriverManager: stub not yet implemented");    	
        return null;
    }

    public DataCache getDataCache() {
        
    	System.err.println("MockApplication.getDataCache: stub not yet implemented");
        return null;
    }

    public SquirrelPreferences getSquirrelPreferences() {
        return prefs;
    }

    public SquirrelResources getResources() {
        return resource;
    }

    public IMessageHandler getMessageHandler() {
        return messageHandler;
    }

    public SessionManager getSessionManager() {
        return sessionHandler;
    }

    public void showErrorDialog(String msg) {
        
    	System.err.println("MockApplication.showErrorDialog(String): stub not yet implemented");
    }

    public void showErrorDialog(Throwable th) {
        
    	System.err.println("MockApplication.showErrorDialog(Throwable): stub not yet implemented");
    }

    public void showErrorDialog(String msg, Throwable th) {
        
    	System.err.println("MockApplication.showErrorDialog(String, Throwable): stub not yet implemented");
    }

    public MainFrame getMainFrame() {
        
       throw new UnsupportedOperationException("getMainFrame"); 
    	//System.err.println("MockApplication.getMainFrame: stub not yet implemented");
        //return null;
    }

    public TaskThreadPool getThreadPool() {
        return threadPool;
    }

    public FontInfoStore getFontInfoStore() {
        return fontInfoStore;
    }

    public ISQLEntryPanelFactory getSQLEntryPanelFactory() {
        return sqlEntryPanelFactory;
    }

    public SQLHistory getSQLHistory() {
    	return history;
    }

    public void setSQLEntryPanelFactory(ISQLEntryPanelFactory factory) {
        
    	System.err.println("MockApplication.setSQLEntryPanelFactory: stub not yet implemented");
    }

    public void addToMenu(int menuId, JMenu menu) {
        
    	System.err.println("MockApplication.addToMenu(int, JMenu): stub not yet implemented");
    }

    public void addToMenu(int menuId, Action action) {
        
    	System.err.println("MockApplication.addToMenu(int, Action): stub not yet implemented");
    }

    public void addToStatusBar(JComponent comp) {
        
    	System.err.println("MockApplication.addToStatusBar: stub not yet implemented");
    }

    public void removeFromStatusBar(JComponent comp) {
        
    	System.err.println("MockApplication.removeFromStatusBar: stub not yet implemented");
    }

    public void startup() {
        
    	System.err.println("MockApplication.startup: stub not yet implemented");
    }

    public boolean shutdown() {
        
    	System.err.println("MockApplication.shutdown: stub not yet implemented");
        return false;
    }

    public void openURL(String url) {
        
    	System.err.println("MockApplication.openURL: stub not yet implemented");
    }


    /**
     * @see net.sourceforge.squirrel_sql.client.IApplication#saveApplicationState()
     */
    public void saveApplicationState() {
        
        
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.IApplication#savePreferences(net.sourceforge.squirrel_sql.client.preferences.PreferenceType)
     */
    public void savePreferences(PreferenceType preferenceType) {
        
        
    }

	/**
	 * @see net.sourceforge.squirrel_sql.client.IApplication#addApplicationListener(net.sourceforge.squirrel_sql.client.ApplicationListener)
	 */
	public void addApplicationListener(ApplicationListener l)
	{
		
		
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.IApplication#removeApplicationListener(net.sourceforge.squirrel_sql.client.ApplicationListener)
	 */
	public void removeApplicationListener(ApplicationListener l)
	{
		
		
	}

}
