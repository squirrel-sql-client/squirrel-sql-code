package net.sourceforge.squirrel_sql.plugins.derby;

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

import java.net.MalformedURLException;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginGlobalPreferencesTab;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableWithChildNodesExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.derby.exp.DerbyTableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.derby.prefs.DerbyPluginPreferencesPanel;
import net.sourceforge.squirrel_sql.plugins.derby.prefs.DerbyPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.derby.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.derby.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.derby.tab.ViewSourceTab;
import net.sourceforge.squirrel_sql.plugins.derby.tokenizer.DerbyQueryTokenizer;
import net.sourceforge.squirrel_sql.plugins.derby.types.DerbyClobDataTypeComponentFactory;

/**
 * The main controller class for the Derby plugin.
 * 
 * @author manningr
 */
public class DerbyPlugin extends DefaultSessionPlugin {

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DerbyPlugin.class);

   /** Logger for this class. */
   private final static ILogger s_log = LoggerController.createLogger(DerbyPlugin.class);

   /** API for the Obejct Tree. */
   private IObjectTreeAPI _treeAPI;

   static interface i18n {
      //i18n[DerbyPlugin.showViewSource=Show view source]
      String SHOW_VIEW_SOURCE = s_stringMgr.getString("DerbyPlugin.showViewSource");

      //i18n[DerbyPlugin.showProcedureSource=Show procedure source]
      String SHOW_PROCEDURE_SOURCE = s_stringMgr.getString("DerbyPlugin.showProcedureSource");

      //i18n[DerbyPlugin.prefsTitle=Derby]
      String PREFS_TITLE = s_stringMgr.getString("DerbyPlugin.prefsTitle");

      //i18n[DerbyPlugin.prefsHint=Set preferences for Derby Plugin]
      String PREFS_HINT = s_stringMgr.getString("DerbyPlugin.prefsHint");
   }

   /** manages our query tokenizing preferences */
   private PluginQueryTokenizerPreferencesManager _prefsManager = null;

   /**
    * Return the internal name of this plugin.
    *
    * @return  the internal name of this plugin.
    */
   public String getInternalName() {
      return "derby";
   }

   /**
    * Return the descriptive name of this plugin.
    *
    * @return  the descriptive name of this plugin.
    */
   public String getDescriptiveName() {
      return "Derby Plugin";
   }

   /**
    * Returns the current version of this plugin.
    *
    * @return  the current version of this plugin.
    */
   public String getVersion() {
      return "0.12";
   }

   /**
    * Returns the authors name.
    *
    * @return  the authors name.
    */
   public String getAuthor() {
      return "Rob Manning";
   }

   /**
    * Returns a comma separated list of other contributors.
    *
    * @return  Contributors names.
    */
   public String getContributors() {
      return "";
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getChangeLogFileName()
    */
   public String getChangeLogFileName() {
      return "changes.txt";
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getHelpFileName()
    */
   public String getHelpFileName() {
      return "readme.html";
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getLicenceFileName()
    */
   public String getLicenceFileName() {
      return "licence.txt";
   }

   /**
    * Load this plugin.
    *
    * @param	app	 Application API.
    */
   public synchronized void load(IApplication app) throws PluginException {
      super.load(app);
   }

   /**
    * Create panel for the Global Properties dialog.
    * 
    * @return  properties panel.
    */
   public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
      PluginQueryTokenizerPreferencesPanel _prefsPanel = new DerbyPluginPreferencesPanel(_prefsManager);

      PluginGlobalPreferencesTab tab = new PluginGlobalPreferencesTab(_prefsPanel);

      tab.setHint(i18n.PREFS_HINT);
      tab.setTitle(i18n.PREFS_TITLE);

      return new IGlobalPreferencesPanel[] { tab };
   }

   /**
    * Initialize this plugin.
    */
   public synchronized void initialize() throws PluginException {
      super.initialize();
      
      //Add session ended listener -- needs for Embedded Derby DB
      _app.getSessionManager().addSessionListener(new SessionListener());

      

      _prefsManager = new PluginQueryTokenizerPreferencesManager();
      _prefsManager.initialize(this, new DerbyPreferenceBean());

      /** Need to do this to pickup user's prefs from file */
      DerbyPreferenceBean prefBean = (DerbyPreferenceBean) _prefsManager.getPreferences();

      if (prefBean.isReadClobsFully()) {
         /* Register custom DataTypeComponent factory for Derby CLOB type */
         CellComponentFactory.registerDataTypeFactory(new DerbyClobDataTypeComponentFactory(),
                                                      2005,
                                                      "CLOB");
      }      
   }

   /**
    * Application is shutting down so save preferences.
    */
   public void unload() {
      super.unload();
   }

   public boolean allowsSessionStartedInBackground() {
      return true;
   }

   /**
    * Session has been started. Update the tree api in using the event thread
    *
    * @param   session     Session that has started.
    *
    * @return  <TT>true</TT> if session is Oracle in which case this plugin
    *                          is interested in it.
    */
   public PluginSessionCallback sessionStarted(final ISession session) {
      if (!isPluginSession(session)) {
         return null;
      }
      if (s_log.isInfoEnabled()) {
         s_log.info("Installing Derby query tokenizer");
      }
      DerbyPreferenceBean prefBean = (DerbyPreferenceBean) _prefsManager.getPreferences();
      if (prefBean.isInstallCustomQueryTokenizer()) {
         DerbyQueryTokenizer tokenizer = new DerbyQueryTokenizer(prefBean.getStatementSeparator(),
                                                                 prefBean.getLineComment(),
                                                                 prefBean.isRemoveMultiLineComments());
         session.setQueryTokenizer(tokenizer);
      }
      
      GUIUtils.processOnSwingEventThread(new Runnable() {
         public void run() {
            updateTreeApi(session);
         }
      });

      return new PluginSessionCallback() {
         public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame,
               ISession sess) {
            // Supports Session main window only
         }

         public void objectTreeInternalFrameOpened(
               ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess) {
            // Supports Session main window only
         }
      };

   }

   @Override
   protected boolean isPluginSession(ISession session) {
      return DialectFactory.isDerby(session.getMetaData());
   }

   private void updateTreeApi(ISession session) {

      _treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();

      //_treeAPI.addDetailTab(DatabaseObjectType.PROCEDURE, 
      //        new ProcedureSourceTab(i18n.SHOW_PROCEDURE_SOURCE));

      _treeAPI.addDetailTab(DatabaseObjectType.VIEW,
                            new ViewSourceTab(i18n.SHOW_VIEW_SOURCE));

      //_treeAPI.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
      //_treeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexDetailsTab());
      _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER,
                            new DatabaseObjectInfoTab());
      _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER_TYPE_DBO,
                            new DatabaseObjectInfoTab());
      //_treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
      //_treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new SequenceDetailsTab());        

      // Expanders - trigger and index expanders are added inside the table
      // expander
      //_treeAPI.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander());
      TableWithChildNodesExpander trigExp = new TableWithChildNodesExpander();
      trigExp.setTableTriggerExtractor(new DerbyTableTriggerExtractorImpl());
      _treeAPI.addExpander(DatabaseObjectType.TABLE, trigExp);

      _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
      _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER,
                            new TriggerSourceTab("The source of the trigger"));

   }

   /**
    * A session listener that shutdown Embedded Derby when session and
    * connection are already closed
    * 
    * @author Alex Pivovarov
    */
   private class SessionListener extends SessionAdapter {
      @Override
      public void sessionClosed(SessionEvent evt) {
         ISession session = evt.getSession();
         shutdownEmbeddedDerby(session);
      }
   }
   
   /**
    * Shutdown Embedded Derby DB and reload JDBC Driver
    * 
    * @param session
    *           Current session.
    * 
    * @author Alex Pivovarov
    */
   private void shutdownEmbeddedDerby(final ISession session) {
      try {
         ISQLDriver iSqlDr = session.getDriver();
         if (!(iSqlDr.getDriverClassName().startsWith("org.apache.derby.jdbc.EmbeddedDriver"))) {
            return;
         }
         //the code bellow is only for Embedded Derby Driver
         IIdentifier drId = iSqlDr.getIdentifier();
         SQLDriverManager sqlDrMan = _app.getSQLDriverManager();
         //Getting java.sql.Driver to run shutdown command
         Driver jdbcDr = sqlDrMan.getJDBCDriver(drId);
         //Shutdown Embedded Derby DB
         try {
            jdbcDr.connect("jdbc:derby:;shutdown=true", new Properties());
         } catch (SQLException e) {
            //it is always thrown as said in Embedded Derby API.
            //So it is not error it is info
            s_log.info(e.getMessage());
         }
         //Re-registering driver is necessary for Embedded Derby
         sqlDrMan.registerSQLDriver(iSqlDr);
      } catch (RuntimeException e) {
         s_log.error(e.getMessage(),e);
      } catch (MalformedURLException e) {
         s_log.error(e.getMessage(),e);
      } catch (IllegalAccessException e) {
         s_log.error(e.getMessage(),e);
      } catch (InstantiationException e) {
         s_log.error(e.getMessage(),e);
      } catch (ClassNotFoundException e) {
         s_log.error(e.getMessage(),e);
      }
   }

   
}
