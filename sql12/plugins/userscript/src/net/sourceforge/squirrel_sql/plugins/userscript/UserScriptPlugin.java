package net.sourceforge.squirrel_sql.plugins.userscript;

/*
 * Copyright (C) 2004 Gerd Wagner
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.userscript.kernel.UserScriptAdmin;

import javax.swing.*;
import java.net.URLClassLoader;
import java.util.Hashtable;

/**
 * The SQL Script plugin class.
 */
public class UserScriptPlugin extends DefaultSessionPlugin
{

   private interface IMenuResourceKeys
   {
      String USER_SCRIPT = "userscript";
   }

   /**
    * Logger for this class.
    */
   @SuppressWarnings("unused")
   private static ILogger s_log = LoggerController.createLogger(UserScriptPlugin.class);

   private PluginResources _resources;

   private Hashtable<IIdentifier, UserScriptAdmin> _userScriptAdminsBySessionId = 
       new Hashtable<IIdentifier, UserScriptAdmin>();
   private URLClassLoader m_userScriptClassLoader;

   /**
    * Return the internal name of this plugin.
    *
    * @return the internal name of this plugin.
    */
   public String getInternalName()
   {
      return "userscript";
   }

   /**
    * Return the descriptive name of this plugin.
    *
    * @return the descriptive name of this plugin.
    */
   public String getDescriptiveName()
   {
      return "User Scripts Plugin";
   }

   /**
    * Returns the current version of this plugin.
    *
    * @return the current version of this plugin.
    */
   public String getVersion()
   {
      return "0.01";
   }

   /**
    * Returns the authors name.
    *
    * @return the authors name.
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
    * a change log.
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
    * a help file.
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
    * a licence file.
    */
   public String getLicenceFileName()
   {
      return "licence.txt";
   }


   /**
    * Initialize this plugin.
    */
   public synchronized void initialize() throws PluginException
   {
      super.initialize();
      IApplication app = getApplication();

      _resources =
         new PluginResources(
            "net.sourceforge.squirrel_sql.plugins.userscript.userscript",
            this);



      ActionCollection coll = app.getActionCollection();
      coll.add(new UserScriptAction(app, _resources, this));
      coll.add(new UserScriptSQLAction(app, _resources, this));

      createMenu();
   }

   /**
    * Application is shutting down so save data.
    */
   public void unload()
   {
      super.unload();
   }

   public boolean allowsSessionStartedInBackground()
   {
      return true;
   }

   /**
    * Called when a session started. Add commands to popup menu
    * in object tree.
    *
    * @param session The session that is starting.
    * @return <TT>true</TT> to indicate that this plugin is
    *         applicable to passed session.
    */
   public PluginSessionCallback sessionStarted(final ISession session)
   {
      

      GUIUtils.processOnSwingEventThread(new Runnable() {
          public void run() {
              ActionCollection coll = getApplication().getActionCollection();
              IObjectTreeAPI api = 
                  session.getSessionInternalFrame().getObjectTreeAPI();

              api.addToPopup(DatabaseObjectType.TABLE, coll.get(UserScriptAction.class));
              api.addToPopup(DatabaseObjectType.PROCEDURE, coll.get(UserScriptAction.class));
              api.addToPopup(DatabaseObjectType.SESSION, coll.get(UserScriptAction.class));              
          }
      });


      UserScriptAdmin adm = new UserScriptAdmin(this, session);
      _userScriptAdminsBySessionId.put(session.getIdentifier(), adm);

      PluginSessionCallback ret = new PluginSessionCallback()
      {
         public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
         {
            // TODO
            // Plugin supports only the main session window
         }

         public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
         {
            // TODO
            // Plugin supports only the main session window
         }
      };
      return ret;
   }


   private void createMenu()
   {
      IApplication app = getApplication();
      ActionCollection coll = app.getActionCollection();

      JMenu menu = _resources.createMenu(IMenuResourceKeys.USER_SCRIPT);

      _resources.addToMenu(coll.get(UserScriptSQLAction.class), menu);

      app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);
   }

   public UserScriptAdmin getUserScriptAdmin(ISession session)
   {
      return _userScriptAdminsBySessionId.get(session.getIdentifier());
   }

   public URLClassLoader getUserScriptClassLoader()
   {
      return m_userScriptClassLoader;
   }

   public void setUserScriptClassLoader(URLClassLoader urlClassLoader)
   {
      m_userScriptClassLoader = urlClassLoader;
   }

}
