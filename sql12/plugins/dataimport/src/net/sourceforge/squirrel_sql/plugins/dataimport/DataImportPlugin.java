package net.sourceforge.squirrel_sql.plugins.dataimport;
/*
 * Copyright (C) 2007 Thorsten Mürell
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
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.IPluginResourcesFactory;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResourcesFactory;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.fw.resources.IResources;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.plugins.dataimport.action.ImportTableDataAction;

import javax.swing.JMenu;

/**
 * Plugin to import data into a table
 *
 * @author Thorsten Mürell
 */
public class DataImportPlugin extends DefaultSessionPlugin
{

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
    * Return the internal name of this plugin.
    *
    * @return the internal name of this plugin.
    */
   public String getInternalName()
   {
      return "dataimport";
   }

   /**
    * Return the descriptive name of this plugin.
    *
    * @return the descriptive name of this plugin.
    */
   public String getDescriptiveName()
   {
      return "Data Import Plugin";
   }

   /**
    * Returns the current version of this plugin.
    *
    * @return the current version of this plugin.
    */
   public String getVersion()
   {
      return "1.0";
   }

   /**
    * Returns the authors name.
    *
    * @return the authors name.
    */
   public String getAuthor()
   {
      return "Thorsten M\u00FCrell,Gerd Wagner";
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getContributors()
    */
   @Override
   public String getContributors()
   {
      return "Guido Wojke,P_W999";
   }


   /**
    * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getChangeLogFileName()
    */
   @Override
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
      return "licence.txt";
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getHelpFileName()
    */
   @Override
   public String getHelpFileName()
   {
      return "doc/readme.html";
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#load(net.sourceforge.squirrel_sql.client.IApplication)
    */
   @Override
   public void load(IApplication app) throws PluginException
   {
      super.load(app);
      _resources = this._resourcesFactory.createResource(getClass().getName(), this);
   }

   /**
    * Initialize this plugin.
    */
   @Override
   public synchronized void initialize() throws PluginException
   {
      super.initialize();

      IApplication app = getApplication();
      ActionCollection coll = app.getActionCollection();

      coll.add(new ImportTableDataAction(app, _resources));

      // Looks for menu.dataimport.title in DataImportPlugin.properties
      JMenu menu = _resources.createMenu(getInternalName());

      app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);
      _resources.addToMenu(coll.get(ImportTableDataAction.class), menu);

   }

   /**
    * Application is shutting down so save preferences.
    */
   @Override
   public void unload()
   {
      super.unload();
   }

   /**
    * Called when a session started.
    *
    * @param session The session that is starting.
    * @return <TT>true</TT> to indicate that this plugin is
    * applicable to passed session.
    */
   public PluginSessionCallback sessionStarted(final ISession session)
   {
      updateTreeApi(session.getSessionInternalFrame().getObjectTreeAPI());

      ActionCollection coll = getApplication().getActionCollection();
      session.addSeparatorToToolbar();
      session.addToToolbar(coll.get(ImportTableDataAction.class));



      ISQLPanelAPI sqlPanelAPI = session.getSessionInternalFrame().getMainSQLPanelAPI();
      initSQLPanelApi(sqlPanelAPI);

      return new PluginSessionCallback()
      {
         @Override
         public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
         {
            updateTreeApi(objectTreeInternalFrame.getObjectTreeAPI());
         }

         @Override
         public void objectTreeInSQLTabOpened(ObjectTreePanel objectTreePanel)
         {
            updateTreeApi(objectTreePanel);
         }

         @Override
         public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
         {
            initSQLPanelApi(sqlInternalFrame.getMainSQLPanelAPI());
         }

         @Override
         public void additionalSQLTabOpened(AdditionalSQLTab additionalSQLTab)
         {
            initSQLPanelApi(additionalSQLTab.getSQLPanelAPI());
         }
      };
   }

   private void initSQLPanelApi(ISQLPanelAPI sqlPanelAPI)
   {
      ActionCollection coll = getApplication().getActionCollection();
      sqlPanelAPI.addToToolsPopUp("import", coll.get(ImportTableDataAction.class));
   }

   /**
    * @param objectTreeAPI
    */
   private void updateTreeApi(IObjectTreeAPI objectTreeAPI)
   {
      final ActionCollection coll = getApplication().getActionCollection();
      objectTreeAPI.addToPopup(DatabaseObjectType.TABLE, coll.get(ImportTableDataAction.class));
      objectTreeAPI.addToPopup(DatabaseObjectType.TABLE_TYPE_DBO, coll.get(ImportTableDataAction.class));
      objectTreeAPI.addToPopup(DatabaseObjectType.SESSION, coll.get(ImportTableDataAction.class));
      objectTreeAPI.addToPopup(DatabaseObjectType.SCHEMA, coll.get(ImportTableDataAction.class));
      objectTreeAPI.addToPopup(DatabaseObjectType.CATALOG, coll.get(ImportTableDataAction.class));
   }

   /**
    * Create preferences panel for the Global Preferences dialog.
    *
    * @return Preferences panel.
    */
   @Override
   public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
   {
      return new IGlobalPreferencesPanel[0];
   }
}
