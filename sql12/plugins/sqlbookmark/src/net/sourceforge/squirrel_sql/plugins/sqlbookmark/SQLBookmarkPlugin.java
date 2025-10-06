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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.IPluginResourcesFactory;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResourcesFactory;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.resources.IResources;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Main entry into the SQL Bookmark plugin.
 * <p/>
 * This plugin allows you to maintain a set of frequently used SQL
 * scripts for easy playback. There is also a parameter replacement
 * syntax available for the SQL files.
 *
 * @author Joseph Mocker
 */
public class SQLBookmarkPlugin extends DefaultSessionPlugin
{
   private final static ILogger s_log = LoggerController.createLogger(SQLBookmarkPlugin.class);

   private ArrayList<ISQLPanelAPI> _sqlPanelAPIsListeningForBookmarks = new ArrayList<>();

   private static final String BOOKMARKS_PROPS_FILE = "bookmarks.properties";

   static final String BOOKMARK_PROP_DEFAULT_MARKS_IN_POPUP = "squirrelMarksInPopup";
   static final String BOOKMARK_PROP_USE_CONTAINS_TO_FILTER_BOOKMARKS = "useContainsToFilterBookmarks";

   private Properties _boomarkProps;

   private interface IMenuResourceKeys
   {
      String BOOKMARKS = "bookmarks";
   }

   public static final String RESOURCE_PATH =
      "net.sourceforge.squirrel_sql.plugins.sqlbookmark.sqlbookmark";

   private static ILogger logger =
      LoggerController.createLogger(SQLBookmarkPlugin.class);

	private IResources _resources;

	private IPluginResourcesFactory _resourcesFactory = new PluginResourcesFactory();
	/**
	 * @param resourcesFactory the resourcesFactory to set
	 */
	public void setResourcesFactory(IPluginResourcesFactory resourcesFactory)
	{
		_resourcesFactory = resourcesFactory;
	}


   /**
    * The bookmark menu
    */
   private JMenu menu;

   /**
    * All the current bookmarkManager
    */
   private BookmarkManager bookmarkManager;

   /**
    * Returns the plugin version.
    *
    * @return the plugin version.
    */
   public String getVersion()
   {
      return "2.0.1";
   }

   /**
    * Returns the authors name.
    *
    * @return the authors name.
    */
   public String getAuthor()
   {
      return "Joseph Mocker";
   }

   public String getContributors()
   {
      return "Gerd Wagner";
   }


   /**
    * Return the internal name of this plugin.
    *
    * @return the internal name of this plugin.
    */
   public String getInternalName()
   {
      return "sqlbookmark";
   }

   /**
    * Return the descriptive name of this plugin.
    *
    * @return the descriptive name of this plugin.
    */
   public String getDescriptiveName()
   {
      return "SQL Bookmark Plugin";
   }

   /**
    * Returns the name of the Help file for the plugin.
    *
    * @return the help file name.
    */
   public String getHelpFileName()
   {
      return "doc/readme.html";
   }

   /**
    * Returns the name of the Help file for the plugin.
    *
    * @return the license file name.
    */
   public String getLicenceFileName()
   {
      return "licence.txt";
   }

   /**
    * Returns the name of the change log for the plugin. This should
    * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
    * directory.
    *
    * @return the changelog file name or <TT>null</TT> if plugin doesn't have
    * a change log.
    */
   public String getChangeLogFileName()
   {
      return "changes.txt";
   }

   /**
    * Return the plugin resources. Used by other classes.
    *
    * @return plugin resources.
    */
   protected IResources getResources()
   {
      return _resources;
   }

   /**
    * Get and return a string from the plugin resources.
    *
    * @param name name of the resource string to return.
    * @return resource string.
    */
   protected String getResourceString(String name)
   {
      return _resources.getString(name);
   }

   /**
    * Returns a handle to the current bookmark manager.
    *
    * @return the bookmark manager.
    */
   BookmarkManager getBookmarkManager()
   {
      return bookmarkManager;
   }

   /**
    * Set the bookmark manager.
    *
    * @param bookmarks new manager to register.
    */
   protected void setBookmarkManager(BookmarkManager bookmarks)
   {
      this.bookmarkManager = bookmarks;
   }


   public Object getExternalService()
   {
      return new BookmarksExternalServiceImpl(this);
   }

   /**
    * Initialize this plugin.
    */
   public synchronized void initialize() throws PluginException
   {
      super.initialize();

      IApplication app = getApplication();

      // Load resources such as menu items, etc...
      _resources = _resourcesFactory.createResource(RESOURCE_PATH, this);

      bookmarkManager = new BookmarkManager(this);
      // Load plugin preferences.
      try
      {
         bookmarkManager.load();
      }
      catch (IOException e)
      {
         if (!(e instanceof FileNotFoundException))
         {
            logger.error("Problem loading bookmarkManager", e);
         }
      }

      ActionCollection coll = app.getActionCollection();
      coll.add(new AddBookmarkAction(app, _resources, this));
      coll.add(new EditBookmarksAction(app, _resources, this));
      coll.add(new RunBookmarkAction(app, _resources, this));
      createMenu();

      rebuildMenu();
   }

   public boolean allowsSessionStartedInBackground()
   {
      return true;
   }

   public PluginSessionCallback sessionStarted(final ISession session)
   {
      GUIUtils.processOnSwingEventThread(new Runnable()
      {
         public void run()
         {
            addBookmarkAction(session);
         }
      });

      PluginSessionCallback ret = new PluginSessionCallback()
      {
         public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
         {
            ActionCollection coll = getApplication().getActionCollection();
            sqlInternalFrame.addSeparatorToToolbar();
            sqlInternalFrame.addToToolbar(coll.get(AddBookmarkAction.class));
            sqlInternalFrame.addToToolbar(coll.get(EditBookmarksAction.class));

            initSqlPanel(sqlInternalFrame.getMainSQLPanelAPI());
         }

         public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
         {
         }

         @Override
         public void objectTreeInSQLTabOpened(ObjectTreePanel objectTreePanel)
         {

         }

         @Override
         public void additionalSQLTabOpened(AdditionalSQLTab additionalSQLTab)
         {
            initSqlPanel(additionalSQLTab.getSQLPanelAPI());
         }
      };
      return ret;
   }


   private void addBookmarkAction(ISession session)
   {
      ActionCollection coll = getApplication().getActionCollection();
      session.addSeparatorToToolbar();
      session.addToToolbar(coll.get(AddBookmarkAction.class));
      session.addToToolbar(coll.get(EditBookmarksAction.class));

      initSqlPanel(session.getSessionInternalFrame().getMainSQLPanelAPI());
   }

   private void initSqlPanel(ISQLPanelAPI sqlPanelAPI)
   {
      ActionCollection coll = Main.getApplication().getActionCollection();
      sqlPanelAPI.addToToolsPopUp("bookmarkadd", coll.get(AddBookmarkAction.class));
      sqlPanelAPI.addToToolsPopUp("bookmarkedit", coll.get(EditBookmarksAction.class));
      sqlPanelAPI.addToToolsPopUp("bookmarkselect", registerBookmarkSelectKeyStroke(sqlPanelAPI));
   }

   private CompleteBookmarkAction registerBookmarkSelectKeyStroke(ISQLPanelAPI sqlPaneAPI)
   {
      CompleteBookmarkAction cba = new CompleteBookmarkAction(Main.getApplication(), _resources, sqlPaneAPI.getSQLEntryPanel(), SQLBookmarkPlugin.this);
      JMenuItem item = sqlPaneAPI.addToSQLEntryAreaMenu(cba);
      _resources.configureMenuItem(cba, item);
      JComponent comp = sqlPaneAPI.getSQLEntryPanel().getTextComponent();
      comp.registerKeyboardAction(cba, _resources.getKeyStroke(cba), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      return cba;
   }


   /**
    * Rebuild the Sessions->Bookmarks menu
    */
   protected void rebuildMenu()
   {
      ActionCollection coll = getApplication().getActionCollection();

      menu.removeAll();
      _resources.addToMenu(coll.get(AddBookmarkAction.class), menu);
      _resources.addToMenu(coll.get(EditBookmarksAction.class), menu);
      menu.add(new JSeparator());

      for (Iterator<Bookmark> i = bookmarkManager.iterator(); i.hasNext();)
      {
         Object o = i.next();
         Bookmark bookmark = (Bookmark) o;

         addBookmarkItem(bookmark);
      }

      String defaultMarksInPopup =
         getBookmarkProperties().getProperty(SQLBookmarkPlugin.BOOKMARK_PROP_DEFAULT_MARKS_IN_POPUP, "" + false);

      if(Boolean.valueOf(defaultMarksInPopup).booleanValue())
      {
         Bookmark[] defaultBookmarks = DefaultBookmarksFactory.getDefaultBookmarks();

         for (int i = 0; i < defaultBookmarks.length; i++)
         {
            addBookmarkItem(defaultBookmarks[i]);
         }
      }

   }




   /**
    * Create the initial Sessions->Bookmark menu
    */
   private void createMenu()
   {
      IApplication app = getApplication();

      menu = _resources.createMenu(IMenuResourceKeys.BOOKMARKS);

      app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);
   }

   /**
    * Add new bookmark to Sessions->Bookmark menu
    *
    * @param bookmark the bookmark to add.
    */
   protected void addBookmarkItem(Bookmark bookmark)
   {
      IApplication app = getApplication();
      ActionCollection coll = app.getActionCollection();

      JMenuItem item = new JMenuItem(coll.get(RunBookmarkAction.class));
      item.setText(bookmark.getName());

      menu.add(item);
   }

   /**
    * Create and return a preferences object.
    *
    * @return The global preferences object.
    */
   public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
   {
      return new IGlobalPreferencesPanel[]{
         new SQLBookmarkPreferencesController(this)
      };
   }

   public void addSQLPanelAPIListeningForBookmarks(ISQLPanelAPI sqlApi)
   {
      if(false == _sqlPanelAPIsListeningForBookmarks.contains(sqlApi))
      {
         _sqlPanelAPIsListeningForBookmarks.add(sqlApi);
      }
   }

   public void removeSQLPanelAPIListeningForBookmarks(ISQLPanelAPI sqlApi)
   {
      _sqlPanelAPIsListeningForBookmarks.remove(sqlApi);
   }

   public void removeALLSQLPanelsAPIListeningForBookmarks()
   {
      _sqlPanelAPIsListeningForBookmarks = new ArrayList<ISQLPanelAPI>();
   }

   public ISQLPanelAPI[] getSQLPanelAPIsListeningForBookmarks()
   {
      return _sqlPanelAPIsListeningForBookmarks.toArray(new ISQLPanelAPI[_sqlPanelAPIsListeningForBookmarks.size()]);
   }


   Properties getBookmarkProperties()
   {
      FileInputStream fis = null;
      try
      {
         if(null == _boomarkProps)
         {
            FileWrapper usf = getPluginUserSettingsFolder();
            FileWrapper bookmarkPropsFile = fileWrapperFactory.create(usf, BOOKMARKS_PROPS_FILE);

            if(false == bookmarkPropsFile.exists())
            {
               _boomarkProps = new Properties();
            }
            else
            {
               fis = bookmarkPropsFile.getFileInputStream();
               _boomarkProps = new Properties();
               _boomarkProps.load(fis);
            }
         }
         return _boomarkProps;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      } finally {
          if (fis != null) {
              try {
                  fis.close();
              } catch (IOException ex) {
                  s_log.error("Unable to close output stream: "+ex.getMessage(),
                              ex);
              }
          }
      }
   }

   void saveBookmarkProperties()
   {
       FileOutputStream fos = null;
      try
      {
         if(null == _boomarkProps)
         {
            return;
         }

         FileWrapper usf = getPluginUserSettingsFolder();
         FileWrapper boomarkPropsFile = fileWrapperFactory.create(usf, BOOKMARKS_PROPS_FILE);
         fos = boomarkPropsFile.getFileOutputStream();
         _boomarkProps.store(fos, "Bookmark properties");
      } catch (IOException e) {
          throw new RuntimeException(e);
      } finally {
          if (fos != null) {
              try {
                  fos.close();
              } catch (IOException ex) {
                  s_log.error("Unable to close output stream: "+ex.getMessage(),
                              ex);
              }
          }
      }
   }



}
    
