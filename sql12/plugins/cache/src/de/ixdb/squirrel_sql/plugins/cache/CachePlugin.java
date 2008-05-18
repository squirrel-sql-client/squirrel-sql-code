package de.ixdb.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.*;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.io.File;
import java.io.IOException;

/**
 * The SQL Script plugin class.
 */
public class CachePlugin extends DefaultSessionPlugin
{

   /** Logger for this class. */
   private static ILogger s_log = LoggerController.createLogger(CachePlugin.class);

   /** The app folder for this plugin. */
   private File _pluginAppFolder;

   private PluginResources _resources;

   /** Folder to store user settings in. */
   private File _userSettingsFolder;

   /**
    * Return the internal name of this plugin.
    *
    * @return  the internal name of this plugin.
    */
   public String getInternalName()
   {
      return "cache";
   }

   /**
    * Return the descriptive name of this plugin.
    *
    * @return  the descriptive name of this plugin.
    */
   public String getDescriptiveName()
   {
      return "Plugin for the Intersystems Cache DB";
   }

   /**
    * Returns the current version of this plugin.
    *
    * @return  the current version of this plugin.
    */
   public String getVersion()
   {
      return "0.01";
   }

   /**
    * Returns the authors name.
    *
    * @return  the authors name.
    */
   public String getAuthor()
   {
      return "Gerd Wagner";
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
    * Returns the name of the Help file for the plugin. This should
    * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
    * directory.
    *
    * @return	the Help file name or <TT>null</TT> if plugin doesn't have
    * 			a help file.
    */
   public String getHelpFileName()
   {
      return "readme.txt";
   }

   /**
    * Returns the name of the Licence file for the plugin. This should
    * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
    * directory.
    *
    * @return	the Licence file name or <TT>null</TT> if plugin doesn't have
    * 			a licence file.
    */
   public String getLicenceFileName()
   {
      return "licence.txt";
   }

   /**
    * @return	Comma separated list of contributors.
    */
   public String getContributors()
   {
      return "Andreas Schneider";
   }

   /**
    * Create preferences panel for the Global Preferences dialog.
    *
    * @return  Preferences panel.
    */
   public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
   {
      return new IGlobalPreferencesPanel[0];
   }

   /**
    * Initialize this plugin.
    */
   public synchronized void initialize() throws PluginException
   {
      super.initialize();
      IApplication app = getApplication();


      // Folder within plugins folder that belongs to this
      // plugin.
      try
      {
         _pluginAppFolder = getPluginAppSettingsFolder();
      }
      catch (IOException ex)
      {
         throw new PluginException(ex);
      }

      // Folder to store user settings.
      try
      {
         _userSettingsFolder = getPluginUserSettingsFolder();
      }
      catch (IOException ex)
      {
         throw new PluginException(ex);
      }

      _resources = new CachePluginResources("de.ixdb.squirrel_sql.plugins.cache.cache", this);

      // Load plugin preferences.
      ActionCollection coll = app.getActionCollection();
      coll.add(new ScriptViewAction(app, _resources, this));
      coll.add(new ScriptFunctionAction(app, _resources, this));
      coll.add(new ScriptCdlAction(app, _resources, this));
      coll.add(new ShowNamespacesAction(app, _resources, this));
      coll.add(new ShowQueryPlanAction(app, _resources, this));
      coll.add(new ShowProcessesAction(app, _resources, this));

//		coll.add(new ScriptProcedureAction(app, _resources, this, _userSettingsFolder));
//		coll.add(new RefreshRepositoryAction(app, _resources, this, _userSettingsFolder));
   }

   /**
    * Application is shutting down so save data.
    */
   public void unload()
   {
      super.unload();
   }

   /**
    * Called when a session started. Add commands to popup menu
    * in object tree.
    *
    * @param   session	 The session that is starting.
    *
    * @return  <TT>true</TT> to indicate that this plugin is
    *		  applicable to passed session.
    */
   public PluginSessionCallback sessionStarted(ISession session)
   {
      try
      {
         if(-1 != session.getSQLConnection().getConnection().getMetaData().getDriverName().toUpperCase().indexOf("CACHE"))
         {
            ActionCollection coll = getApplication().getActionCollection();
            IObjectTreeAPI otApi = session.getSessionInternalFrame().getObjectTreeAPI();
            otApi.addToPopup(DatabaseObjectType.VIEW, coll.get(ScriptViewAction.class));
            otApi.addToPopup(DatabaseObjectType.SESSION, coll.get(ShowNamespacesAction.class));
            otApi.addToPopup(DatabaseObjectType.SESSION, coll.get(ShowProcessesAction.class));
            otApi.addToPopup(DatabaseObjectType.PROCEDURE, coll.get(ScriptFunctionAction.class));
            otApi.addToPopup(DatabaseObjectType.PROCEDURE, coll.get(ScriptCdlAction.class));
            otApi.addToPopup(DatabaseObjectType.TABLE, coll.get(ScriptCdlAction.class));
            otApi.addToPopup(DatabaseObjectType.VIEW, coll.get(ScriptCdlAction.class));


            ISQLPanelAPI sqlApi = session.getSessionInternalFrame().getSQLPanelAPI();
            sqlApi.addToSQLEntryAreaMenu(coll.get(ShowQueryPlanAction.class));

            session.addSeparatorToToolbar();
            session.addToToolbar(coll.get(ShowNamespacesAction.class));
            session.addToToolbar(coll.get(ShowProcessesAction.class));
            session.addToToolbar(coll.get(ShowQueryPlanAction.class));
            session.getSessionInternalFrame().addToToolsPopUp("cachequeryplan", coll.get(ShowQueryPlanAction.class));


            return new PluginSessionCallback()
            {
               public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
               {
                  //plugin supports only Session main window
               }

               public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
               {
                  //plugin supports only Session main window
               }
            };
         }
         else
         {
            return null;
         }
      }
      catch(Exception e)
      {
         s_log.error("Could not get driver name", e);
         return null;
      }
   }

}
