/*
 * Copyright (C) 2003 Joseph Mocker
 * mock-sf@misfit.dhs.org
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

package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.prefs.Preferences;

import javax.swing.*;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.*;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

/**
 * Main entry into the SQL Bookmark plugin. 
 *
 * This plugin allows you to maintain a set of frequently used SQL 
 * scripts for easy playback. There is also a parameter replacement
 * syntax available for the SQL files.
 *
 * @author      Joseph Mocker
 **/
public class SQLBookmarkPlugin extends DefaultSessionPlugin {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SQLBookmarkPlugin.class);

   private static final String PREF_KEY_DEFAULT_BOOKMARKS_LOADED = "Squirrel.sqlbookmark.defaultbookmarksloaded";

   private interface IMenuResourceKeys {
	String BOOKMARKS = "bookmarks";
    }

    private static String RESOURCE_PATH =
	"net.sourceforge.squirrel_sql.plugins.sqlbookmark.sqlbookmark";
    
    private static ILogger logger = 
	LoggerController.createLogger(SQLBookmarkPlugin.class);

    /** The app folder for this plugin. */
    private File pluginAppFolder;
    
    /** Folder to store user settings in. */
    protected File userSettingsFolder;
    
    private PluginResources resources;

    /** The bookmark menu */
    private JMenu menu;

    /** All the current bookmarkManager */
    private BookmarkManager bookmarkManager;

    /**
     * Returns the plugin version.
     *
     * @return  the plugin version.
     */
    public String getVersion() {
	return "1.0";
    }
    
    /**
     * Returns the authors name.
     *
     * @return  the authors name.
     */
    public String getAuthor() {
	return "Joseph Mocker";
    }

   public String getContributors()
   {
      return "Gerd Wagner";
   }


    /**
     * Return the internal name of this plugin.
     *
     * @return  the internal name of this plugin.
     */
    public String getInternalName() {
	return "sqlbookmark";
    }
    
    /**
     * Return the descriptive name of this plugin.
     *
     * @return  the descriptive name of this plugin.
     */
    public String getDescriptiveName() {
	return "SQL Bookmark Plugin";
    }

    /**
     * Returns the name of the Help file for the plugin. 
     *
     * @return	the help file name.
     */
    public String getHelpFileName() {
	return "readme.html";
    }

    /**
     * Returns the name of the Help file for the plugin. 
     *
     * @return	the license file name.
     */
	public String getLicenceFileName() {
	return "licence.txt";
    }

	/**
	 * Returns the name of the change log for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the changelog file name or <TT>null</TT> if plugin doesn't have
	 * 			a change log.
	 */
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

    /**
     * Return the plugin resources. Used by other classes.
     *
     * @return	plugin resources.
     */
    protected PluginResources getResources() {
	return resources;
    }
    
    /**
     * Get and return a string from the plugin resources. 
     *
     * @param name name of the resource string to return.
     * @return	resource string.
     */
    protected String getResourceString(String name) {
	return resources.getString(name);
    }

    /**
     * Returns a handle to the current bookmark manager.
     *
     * @return	the bookmark manager.
     */
    BookmarkManager getBookmarkManager() {
	return bookmarkManager;
    }

    /**
     * Set the bookmark manager.
     *
     * @param bookmarks new manager to register.
     */
    protected void setBookmarkManager(BookmarkManager bookmarks) {
	this.bookmarkManager = bookmarks;
    }
    
    /**
     * Initialize this plugin.
     */
    public synchronized void initialize() throws PluginException {
	super.initialize();

	IApplication app = getApplication();

	PluginManager pmgr = app.getPluginManager();
	
	// Folder within plugins folder that belongs to this
	// plugin.
	try {
	    pluginAppFolder = getPluginAppSettingsFolder();
	} catch (IOException ex) {
	    throw new PluginException(ex);
	}
	
	// Folder to store user settings.
	try {
	    userSettingsFolder = getPluginUserSettingsFolder();
	} catch (IOException ex) {
	    throw new PluginException(ex);
	}

	// Load resources such as menu items, etc...
	resources = new SQLBookmarkResources(RESOURCE_PATH, this);
	
	bookmarkManager = new BookmarkManager(userSettingsFolder);
	// Load plugin preferences.
	try {
	    bookmarkManager.load();

      if(false == Preferences.userRoot().getBoolean(PREF_KEY_DEFAULT_BOOKMARKS_LOADED, false))
      {
         Bookmark[] defaultBookmarks = DefaultBookmarksFactory.getDefaultBookmarks();

         for (int i = 0; i < defaultBookmarks.length; i++)
         {
            if(null == bookmarkManager.get(defaultBookmarks[i].getName()))
            {
               bookmarkManager.add(defaultBookmarks[i]);
            }
            else
            {
               String altName = defaultBookmarks[i].getName() + "_1";
               if(null == bookmarkManager.get(altName))
               {
                  defaultBookmarks[i].setName(altName);
                  bookmarkManager.add(defaultBookmarks[i]);
               }
            }
         }

         Preferences.userRoot().putBoolean(PREF_KEY_DEFAULT_BOOKMARKS_LOADED, true);
			// i18n[sqlbookmark.defaultsAdded=Default bookmarks have been added. See menu File --> Global Preferences --> Bookmarks]
			getApplication().getMessageHandler().showMessage(s_stringMgr.getString("sqlbookmark.defaultsAdded"));
      }
	}
	catch (IOException e) {
	    if (!(e instanceof FileNotFoundException))
		logger.error("Problem loading bookmarkManager", e);
	}
	
	ActionCollection coll = app.getActionCollection();
	coll.add(new AddBookmarkAction(app, resources, this));
	coll.add(new RunBookmarkAction(app, resources, this));
	createMenu();

	rebuildMenu();
    }

   public PluginSessionCallback sessionStarted(ISession session)
   {

      ActionCollection coll = getApplication().getActionCollection();
      session.addSeparatorToToolbar();
      session.addToToolbar(coll.get(AddBookmarkAction.class));
      session.getSessionInternalFrame().addToToolsPopUp("bookmarkadd", coll.get(AddBookmarkAction.class));

      ISQLPanelAPI sqlPaneAPI = session.getSessionInternalFrame().getSQLPanelAPI();
      CompleteBookmarkAction cba = new CompleteBookmarkAction(session.getApplication(), resources, sqlPaneAPI.getSQLEntryPanel(), session, this);
      JMenuItem item = sqlPaneAPI.addToSQLEntryAreaMenu(cba);
      resources.configureMenuItem(cba, item);
      JComponent comp = sqlPaneAPI.getSQLEntryPanel().getTextComponent();
      comp.registerKeyboardAction(cba, resources.getKeyStroke(cba), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      session.getSessionInternalFrame().addToToolsPopUp("bookmarkselect", cba);



      PluginSessionCallback ret = new PluginSessionCallback()
      {
         public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
         {
            ActionCollection coll = getApplication().getActionCollection();
            sqlInternalFrame.addSeparatorToToolbar();
            sqlInternalFrame.addToToolbar(coll.get(AddBookmarkAction.class));
            sqlInternalFrame.addToToolsPopUp("bookmarkadd", coll.get(AddBookmarkAction.class));

            ISQLPanelAPI sqlPaneAPI = sqlInternalFrame.getSQLPanelAPI();
            CompleteBookmarkAction cba = new CompleteBookmarkAction(sess.getApplication(), resources, sqlPaneAPI.getSQLEntryPanel(), sess, SQLBookmarkPlugin.this);
            JMenuItem item = sqlPaneAPI.addToSQLEntryAreaMenu(cba);
            resources.configureMenuItem(cba, item);
            JComponent comp = sqlPaneAPI.getSQLEntryPanel().getTextComponent();
            comp.registerKeyboardAction(cba, resources.getKeyStroke(cba), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            sqlInternalFrame.addToToolsPopUp("bookmarkselect", cba);
         }

         public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
         {
         }
      };
      return ret;
   }


    /**
     * Rebuild the Sessions->Bookmarks menu
     *
     */
    protected void rebuildMenu() {
	ActionCollection coll = getApplication().getActionCollection();

	menu.removeAll();
	resources.addToMenu(coll.get(AddBookmarkAction.class), menu);
	menu.add(new JSeparator());

	for (Iterator i = bookmarkManager.iterator(); i.hasNext(); ) {
	    Object o = i.next();
	    logger.error(o.getClass().getName());
	    Bookmark bookmark = (Bookmark) o;

	    addBookmarkItem(bookmark);
	}
    }

    /**
     * Create the initial Sessions->Bookmark menu
     *
     */
    private void createMenu() {
	IApplication app = getApplication();

	menu = resources.createMenu(IMenuResourceKeys.BOOKMARKS);
	
	app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);
    }

    /**
     * Add new bookmark to Sessions->Bookmark menu
     *
     * @param bookmark the bookmark to add.
     */
    protected void addBookmarkItem(Bookmark bookmark) {
	IApplication app = getApplication();
	ActionCollection coll = app.getActionCollection();

	SquirrelAction action = 
	    (SquirrelAction) coll.get(RunBookmarkAction.class);

	JMenuItem item = new JMenuItem(action);
	item.setText(bookmark.getName());
	
	menu.add(item);
    }

    /**
     * Create and return a preferences object.
     *
     * @return The global preferences object.
     */
    public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
	return new IGlobalPreferencesPanel[] {
	    new SQLBookmarkPreferencesPanel(this)
		};
    }
}
    
