package net.sourceforge.squirrel_sql.plugins.mssql;

/*
 * Copyright (C) 2004 Ryan Walberg <generalpf@yahoo.com>
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
import javax.swing.JMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.IPluginResourcesFactory;
import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginResourcesFactory;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginGlobalPreferencesTab;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableWithChildNodesExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.resources.IResources;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.mssql.action.GenerateSqlAction;
import net.sourceforge.squirrel_sql.plugins.mssql.action.IndexDefragAction;
import net.sourceforge.squirrel_sql.plugins.mssql.action.ScriptProcedureAction;
import net.sourceforge.squirrel_sql.plugins.mssql.action.ScriptProcedureExecAction;
import net.sourceforge.squirrel_sql.plugins.mssql.action.ShowStatisticsAction;
import net.sourceforge.squirrel_sql.plugins.mssql.action.ShrinkDatabaseAction;
import net.sourceforge.squirrel_sql.plugins.mssql.action.ShrinkDatabaseFileAction;
import net.sourceforge.squirrel_sql.plugins.mssql.action.TruncateLogAction;
import net.sourceforge.squirrel_sql.plugins.mssql.action.UpdateStatisticsAction;
import net.sourceforge.squirrel_sql.plugins.mssql.event.IndexIterationListener;
import net.sourceforge.squirrel_sql.plugins.mssql.exp.MssqlTableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.mssql.gui.MonitorPanel;
import net.sourceforge.squirrel_sql.plugins.mssql.prefs.MSSQLPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.mssql.prefs.PreferencesManager;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.dbfile.DatabaseFile;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.dbfile.DatabaseFileInfo;
import net.sourceforge.squirrel_sql.plugins.mssql.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.mssql.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.mssql.tab.ViewSourceTab;
import net.sourceforge.squirrel_sql.plugins.mssql.tokenizer.MSSQLQueryTokenizer;
import net.sourceforge.squirrel_sql.plugins.mssql.util.MssqlIntrospector;

public class MssqlPlugin extends net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin
{
   private final static ILogger s_log = LoggerController.createLogger(MssqlPlugin.class);

   private IResources _resources;

   private IPluginResourcesFactory _resourcesFactory = new PluginResourcesFactory();

   /**
    * @param resourcesFactory the resourcesFactory to set
    */
   public void setResourcesFactory(IPluginResourcesFactory resourcesFactory)
   {
      _resourcesFactory = resourcesFactory;
   }

   public interface IMenuResourceKeys
   {
      String SHOW_STATISTICS = "show_statistics";
      String INDEXDEFRAG = "indexdefrag";
      String SHRINKDBFILE = "shrinkdbfile";
      String MSSQL = "mssql";
   }


   private IObjectTreeAPI _treeAPI;
   private JMenu _mssqlMenu;
   private ISession _session;
   private int[] indexColumnIndices = new int[]{6};

   /**
    * manages our query tokenizing preferences
    */
   private PluginQueryTokenizerPreferencesManager _prefsManager = null;

   /**
    * The database name that appears in the border label of the pref panel
    */
   private static final String SCRIPT_SETTINGS_BORDER_LABEL_DBNAME = "MS SQL-Server";

   /**
    * Internationalized strings for this class.
    */
   private static final StringManager s_stringMgr =
         StringManagerFactory.getStringManager(MssqlPlugin.class);

   interface i18n
   {
      // i18n[SybaseASEPlugin.title=SybaseASE]
      String title = s_stringMgr.getString("MssqlPlugin.title");

      // i18n[SybaseASEPlugin.hint=Preferences for SybaseASE]
      String hint = s_stringMgr.getString("MssqlPlugin.hint");
   }

   public MssqlPlugin()
   {
      super();
   }

   public String getChangeLogFileName()
   {
      return "changes.txt";
   }

   public String getContributors()
   {
      return "Rob Manning";
   }

   public net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel[] getGlobalPreferencePanels()
   {
      boolean includeProcSepPref = false;

      PluginQueryTokenizerPreferencesPanel _prefsPanel =
            new PluginQueryTokenizerPreferencesPanel(
                  _prefsManager,
                  SCRIPT_SETTINGS_BORDER_LABEL_DBNAME,
                  includeProcSepPref);

      PluginGlobalPreferencesTab tab = new PluginGlobalPreferencesTab(_prefsPanel);

      tab.setHint(i18n.hint);
      tab.setTitle(i18n.title);

      return new IGlobalPreferencesPanel[]{tab};
   }

   public String getHelpFileName()
   {
      return "doc/readme.html";
   }

   public String getLicenceFileName()
   {
      return "licence.txt";
   }

   public net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel[] getNewSessionPropertiesPanels()
   {
      net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel[] retValue;

      retValue = super.getNewSessionPropertiesPanels();
      return retValue;
   }

   public net.sourceforge.squirrel_sql.client.plugin.IPluginDatabaseObjectType[] getObjectTypes(net.sourceforge.squirrel_sql.client.session.ISession iSession)
   {
      net.sourceforge.squirrel_sql.client.plugin.IPluginDatabaseObjectType[] retValue;

      retValue = super.getObjectTypes(iSession);
      return retValue;
   }

   public FileWrapper getPluginAppSettingsFolder() throws java.io.IOException
   {
      FileWrapper retValue;

      retValue = super.getPluginAppSettingsFolder();
      return retValue;
   }

   public FileWrapper getPluginUserSettingsFolder() throws java.io.IOException
   {
      FileWrapper retValue;

      retValue = super.getPluginUserSettingsFolder();
      return retValue;
   }

   public net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel[] getSessionPropertiesPanels(net.sourceforge.squirrel_sql.client.session.ISession iSession)
   {
      net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel[] retValue;

      retValue = super.getSessionPropertiesPanels(iSession);
      return retValue;
   }

   public void initialize() throws net.sourceforge.squirrel_sql.client.plugin.PluginException
   {
      super.initialize();
      PreferencesManager.initialize(this);
      final IApplication app = getApplication();
      final ActionCollection coll = app.getActionCollection();

      coll.add(new GenerateSqlAction(app, _resources, this));
      coll.add(new ScriptProcedureAction(app, _resources, this));
      coll.add(new ScriptProcedureExecAction(app, _resources, this));
      coll.add(new ShrinkDatabaseAction(app, _resources, this));
      coll.add(new TruncateLogAction(app, _resources, this));
      coll.add(new UpdateStatisticsAction(app, _resources, this));

      _mssqlMenu = createFullMssqlMenu();

      app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, _mssqlMenu);
      super.registerSessionMenu(_mssqlMenu);

      _prefsManager = new PluginQueryTokenizerPreferencesManager();
      _prefsManager.initialize(this, new MSSQLPreferenceBean());
   }

   public void load(net.sourceforge.squirrel_sql.client.IApplication iApplication) throws net.sourceforge.squirrel_sql.client.plugin.PluginException
   {
      super.load(iApplication);

      _resources = _resourcesFactory.createResource(getClass().getName(), this);
   }

   public void sessionCreated(net.sourceforge.squirrel_sql.client.session.ISession iSession)
   {
      super.sessionCreated(iSession);
   }

   public void sessionEnding(net.sourceforge.squirrel_sql.client.session.ISession iSession)
   {
      super.sessionEnding(iSession);
   }

   public boolean allowsSessionStartedInBackground()
   {
      return true;
   }

   public PluginSessionCallback sessionStarted(final ISession iSession)
   {
      if (!isPluginSession(iSession))
      {
         return null;
      }
      installMssqlQueryTokenizer(iSession);
      GUIUtils.processOnSwingEventThread(() -> updateTreeApi(iSession));

      return new MssqlPluginSessionCallback();
   }

   @Override
   protected boolean isPluginSession(ISession session)
   {
      return DialectFactory.isMSSQLServer(session.getMetaData());
   }

   /**
    * Determines from the user's preference whether or not to install the
    * custom query tokenizer, and if so configure installs it.
    *
    * @param session the session to install the custom query tokenizer in.
    */
   private void installMssqlQueryTokenizer(ISession session)
   {
      IQueryTokenizerPreferenceBean _prefs = _prefsManager.getPreferences();

      if (_prefs.isInstallCustomQueryTokenizer())
      {
         session.setQueryTokenizer(new MSSQLQueryTokenizer(_prefs));
      }
   }


   private void updateTreeApi(ISession iSession)
   {
      _treeAPI = iSession.getSessionInternalFrame().getObjectTreeAPI();

      _treeAPI.addToPopup(DatabaseObjectType.CATALOG, addToMssqlCatalogMenu(null));
      _treeAPI.addToPopup(DatabaseObjectType.TABLE, addToMssqlTableMenu(null));
      _treeAPI.addToPopup(DatabaseObjectType.PROCEDURE, addToMssqlProcedureMenu(null));

      _treeAPI.addDetailTab(DatabaseObjectType.VIEW, new ViewSourceTab());
      _session = iSession;

      TableWithChildNodesExpander trigExp = new TableWithChildNodesExpander();
      trigExp.setTableTriggerExtractor(new MssqlTableTriggerExtractorImpl());
      _treeAPI.addExpander(DatabaseObjectType.TABLE, trigExp);

      _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
      _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerSourceTab("The source of the trigger"));

      MonitorPanel monitorPanel = new MonitorPanel();
      iSession.addMainTab(monitorPanel);
   }

   public void unload()
   {
      super.unload();
   }

   public String getAuthor()
   {
      return "Ryan Walberg";
   }

   public String getDescriptiveName()
   {
      return "Microsoft SQL Server Assistant";
   }

   public String getInternalName()
   {
      return "mssql";
   }

   public String getVersion()
   {
      return "0.4";
   }

   @SuppressWarnings("unchecked")
   private void removeActionsOfType(ActionCollection coll, java.lang.Class classType)
   {
      java.lang.Object obj;
      java.util.Iterator<Action> iter = coll.actions();
      while (iter.hasNext())
      {
         obj = iter.next();
         if (obj.getClass() == classType)
            iter.remove();
      }
   }

   private JMenu addToMssqlTableMenu(JMenu menu)
   {
      final IApplication app = getApplication();
      final ActionCollection coll = app.getActionCollection();
      final MssqlPlugin plugin = this;

      final JMenu mssqlMenu;
      if (menu == null)
         mssqlMenu = _resources.createMenu(IMenuResourceKeys.MSSQL);
      else
         mssqlMenu = menu;

      _resources.addToMenu(coll.get(UpdateStatisticsAction.class), mssqlMenu);

      final JMenu showStatisticsMenu = _resources.createMenu(IMenuResourceKeys.SHOW_STATISTICS);
      showStatisticsMenu.addMenuListener(new MenuListener()
                                         {
                                            public void menuSelected(MenuEvent e)
                                            {
                                               onShowStatistics(e, coll, app, plugin);
                                            }

                                            public void menuDeselected(MenuEvent e)
                                            {
                                            }

                                            public void menuCanceled(MenuEvent e)
                                            {
                                            }
                                         }
      );

      final JMenu indexDefragMenu = _resources.createMenu(IMenuResourceKeys.INDEXDEFRAG);
      indexDefragMenu.addMenuListener(new MenuListener()
                                      {
                                         public void menuSelected(MenuEvent e)
                                         {
                                            onIndexDefrag(e, coll, app);
                                         }

                                         public void menuDeselected(MenuEvent e)
                                         {
                                         }

                                         public void menuCanceled(MenuEvent e)
                                         {
                                         }
                                      }
      );

      mssqlMenu.add(showStatisticsMenu);
      mssqlMenu.add(indexDefragMenu);

      return mssqlMenu;
   }

   private void onShowStatistics(MenuEvent e, ActionCollection coll, IApplication app, MssqlPlugin plugin)
   {
      final JMenu menu = (JMenu) e.getSource();
      menu.removeAll();
      removeActionsOfType(coll, ShowStatisticsAction.class);
      iterateIndexes(new IndexIterationListener()
      {
         public void indexSpotted(final ITableInfo tableInfo, final String indexName)
         {
            final ShowStatisticsAction showStatisticsAction = new ShowStatisticsAction(app, _resources, plugin, tableInfo, indexName);
            showStatisticsAction.setSession(_session);
            coll.add(showStatisticsAction);
            _resources.addToMenu(showStatisticsAction, menu);
         }
      });
   }

   private void onIndexDefrag(MenuEvent e, ActionCollection coll, IApplication app)
   {
      final JMenu menu = (JMenu) e.getSource();
      menu.removeAll();
      removeActionsOfType(coll, IndexDefragAction.class);
      app.getThreadPool().addTask(new IteratorIndexesTask(menu));
   }

   private class IteratorIndexesTask implements Runnable
   {
      JMenu _menu = null;
      final IApplication app = getApplication();
      final ActionCollection coll = app.getActionCollection();
      final MssqlPlugin plugin = MssqlPlugin.this;

      public IteratorIndexesTask(JMenu menu)
      {
         _menu = menu;
      }

      public void run()
      {
         iterateIndexes(new IndexIterationListener()
         {
            public void indexSpotted(final ITableInfo tableInfo, final String indexName)
            {
               onIndexSpotted(tableInfo, indexName);
            }
         });
      }

      private void onIndexSpotted(ITableInfo tableInfo, String indexName)
      {
         final IndexDefragAction indexDefragAction = new IndexDefragAction(app, _resources, plugin, tableInfo, indexName);
         indexDefragAction.setSession(_session);
         GUIUtils.processOnSwingEventThread(new Runnable()
         {
            public void run()
            {
               coll.add(indexDefragAction);
               _resources.addToMenu(indexDefragAction, _menu);
            }
         });
      }
   }

   private void iterateIndexes(IndexIterationListener listener)
   {
      /* this should just bring back one table, i hope. */
      final IDatabaseObjectInfo[] dbObjs = _treeAPI.getSelectedDatabaseObjects();

      if (dbObjs.length != 1)
      {
         s_log.error("iterateIndexes: more than one item is selected");
         return;
      }
      if (dbObjs[0].getDatabaseObjectType() != DatabaseObjectType.TABLE)
      {
         s_log.error("iterateIndexes: selected item isn't a table");
         return;
      }

      ITableInfo tableInfo = (ITableInfo) dbObjs[0];

      ISQLConnection conn = _session.getSQLConnection();
      SQLDatabaseMetaData metaData = conn.getSQLMetaData();

      try
      {
         ResultSetDataSet rsds =
               metaData.getIndexInfo(tableInfo, indexColumnIndices, false);
         String indexName = "";
         while (rsds.next(null))
         {
            String thisIndexName = (String) rsds.get(0);
            if (thisIndexName != null)
            {
               if (!indexName.equals(thisIndexName))
               {
                  listener.indexSpotted(tableInfo, thisIndexName);
                  indexName = thisIndexName;
               }
            }
         }
      }
      catch (DataSetException ex)
      {
         s_log.error("Unable to show indices for table " +
               tableInfo.getSimpleName(), ex);
         // fine, don't show any indexes.
         //throw new WrappedSQLException(ex);
      }
   }

   private JMenu createFullMssqlMenu()
   {
      final IApplication app = getApplication();
      final ActionCollection coll = app.getActionCollection();

      final JMenu mssqlMenu = _resources.createMenu(IMenuResourceKeys.MSSQL);

      _resources.addToMenu(coll.get(GenerateSqlAction.class), mssqlMenu);

      addToMssqlCatalogMenu(mssqlMenu);
      addToMssqlTableMenu(mssqlMenu);
      addToMssqlProcedureMenu(mssqlMenu);

      return mssqlMenu;
   }

   private JMenu addToMssqlCatalogMenu(JMenu menu)
   {
      final IApplication app = getApplication();
      final ActionCollection coll = app.getActionCollection();
      final MssqlPlugin plugin = this;

      final JMenu mssqlMenu;
      if (menu == null)
         mssqlMenu = _resources.createMenu(IMenuResourceKeys.MSSQL);
      else
         mssqlMenu = menu;

      _resources.addToMenu(coll.get(ShrinkDatabaseAction.class), mssqlMenu);
      _resources.addToMenu(coll.get(TruncateLogAction.class), mssqlMenu);

      final JMenu shrinkDBFileMenu = _resources.createMenu(IMenuResourceKeys.SHRINKDBFILE);
      shrinkDBFileMenu.addMenuListener(new MenuListener()
                                       {
                                          public void menuSelected(MenuEvent e)
                                          {
                                             onShrinkDBFile(e, coll, app, plugin);
                                          }

                                          public void menuDeselected(MenuEvent e)
                                          {
                                          }

                                          public void menuCanceled(MenuEvent e)
                                          {
                                          }
                                       }
      );
      mssqlMenu.add(shrinkDBFileMenu);

      return mssqlMenu;
   }

   private void onShrinkDBFile(MenuEvent e, ActionCollection coll, IApplication app, MssqlPlugin plugin)
   {
      final JMenu menu = (JMenu) e.getSource();
      menu.removeAll();
      removeActionsOfType(coll, ShrinkDatabaseFileAction.class);

      final ObjectTreeNode[] nodes = _treeAPI.getSelectedNodes();
      if (nodes.length != 1)
         return;

      try
      {
         if (nodes[0].getDatabaseObjectType() != DatabaseObjectType.CATALOG)
            return;

         DatabaseFileInfo info = MssqlIntrospector.getDatabaseFileInfo(nodes[0].toString(), _session.getSQLConnection());
         Object[] files = info.getDataFiles();
         for (int i = 0; i < files.length; i++)
         {
            DatabaseFile file = (DatabaseFile) files[i];
            final ShrinkDatabaseFileAction shrinkDatabaseFileAction = new ShrinkDatabaseFileAction(app, _resources, plugin, nodes[0].toString(), file);
            shrinkDatabaseFileAction.setSession(_session);
            coll.add(shrinkDatabaseFileAction);
            _resources.addToMenu(shrinkDatabaseFileAction, menu);
         }
         menu.addSeparator();
         files = info.getLogFiles();
         for (int i = 0; i < files.length; i++)
         {
            DatabaseFile file = (DatabaseFile) files[i];
            final ShrinkDatabaseFileAction shrinkDatabaseFileAction = new ShrinkDatabaseFileAction(app, _resources, plugin, nodes[0].toString(), file);
            shrinkDatabaseFileAction.setSession(_session);
            coll.add(shrinkDatabaseFileAction);
            _resources.addToMenu(shrinkDatabaseFileAction, menu);
         }
      }
      catch (java.sql.SQLException ex)
      {
         s_log.error("Exception while attempting to shrink database file", ex);
         // fine, don't add any data files.
         //throw new WrappedSQLException(ex);
      }
   }

   private JMenu addToMssqlProcedureMenu(JMenu menu)
   {
      final IApplication app = getApplication();
      final ActionCollection coll = app.getActionCollection();

      final JMenu mssqlMenu;
      if (menu == null)
         mssqlMenu = _resources.createMenu(IMenuResourceKeys.MSSQL);
      else
         mssqlMenu = menu;

      _resources.addToMenu(coll.get(ScriptProcedureAction.class), mssqlMenu);
      _resources.addToMenu(coll.get(ScriptProcedureExecAction.class), mssqlMenu);

      return mssqlMenu;
   }

   private class MssqlPluginSessionCallback implements PluginSessionCallback
   {
      public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
      {
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
      }
   }
}
