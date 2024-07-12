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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
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
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableWithChildNodesExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
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
import net.sourceforge.squirrel_sql.plugins.derby.tab.ProcedureSourceTab;
import net.sourceforge.squirrel_sql.plugins.derby.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.derby.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.derby.tab.ViewSourceTab;
import net.sourceforge.squirrel_sql.plugins.derby.tokenizer.DerbyQueryTokenizer;
import net.sourceforge.squirrel_sql.plugins.derby.types.DerbyClobDataTypeComponentFactory;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.sql.Driver;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * The main controller class for the Derby plugin.
 *
 * @author manningr
 */
public class DerbyPlugin extends DefaultSessionPlugin
{

   private static final String UNKNOWN_DBNAME = "<unknown>";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DerbyPlugin.class);

   /**
    * Logger for this class.
    */
   private final static ILogger s_log = LoggerController.createLogger(DerbyPlugin.class);

   /**
    * API for the Obejct Tree.
    */
   private IObjectTreeAPI _treeAPI;

   static interface i18n
   {
      //i18n[DerbyPlugin.showViewSource=Show view source]
      String SHOW_VIEW_SOURCE = s_stringMgr.getString("DerbyPlugin.showViewSource");

      //i18n[DerbyPlugin.showProcedureSource=Show procedure source]
      String SHOW_PROCEDURE_SOURCE = s_stringMgr.getString("DerbyPlugin.showProcedureSource");

      //i18n[DerbyPlugin.prefsTitle=Derby]
      String PREFS_TITLE = s_stringMgr.getString("DerbyPlugin.prefsTitle");

      //i18n[DerbyPlugin.prefsHint=Set preferences for Derby Plugin]
      String PREFS_HINT = s_stringMgr.getString("DerbyPlugin.prefsHint");

      //i18n[DerbyPlugin.embeddedDerby.title=Embedded Derby]
      String EMBEDDED_DERBY_TITLE = s_stringMgr.getString("DerbyPlugin.embeddedDerby.title");

      //i18n[DerbyPlugin.embeddedDerby.inMemoryDropQuestion=Drop in-memory database: {0}?]
      String DROP_DB_QUESTION = s_stringMgr.getString("DerbyPlugin.embeddedDerby.inMemoryDropQuestion");

      //i18n[DerbyPlugin.embeddedDerby.inMemoryDropOption=Drop]
      String DROP_OPTION = s_stringMgr.getString("DerbyPlugin.embeddedDerby.inMemoryDropOption");

      //i18n[DerbyPlugin.embeddedDerby.inMemoryKeepOption=Keep]
      String KEEP_OPTION = s_stringMgr.getString("DerbyPlugin.embeddedDerby.inMemoryKeepOption");
   }

   /**
    * manages our query tokenizing preferences
    */
   private PluginQueryTokenizerPreferencesManager _prefsManager = null;

   private Map<String, List<WeakReference<ISession>>> _embeddedSessions = new HashMap<>();

   // Reference to the JDBC driver so we could perform shutdown,
   // even when session references have been cleared.
   private volatile ISQLDriver _embeddedDriver;

   private volatile boolean _appShuttingDown;

   /**
    * Return the internal name of this plugin.
    *
    * @return the internal name of this plugin.
    */
   public String getInternalName()
   {
      return "derby";
   }

   /**
    * Return the descriptive name of this plugin.
    *
    * @return the descriptive name of this plugin.
    */
   public String getDescriptiveName()
   {
      return "Derby Plugin";
   }

   /**
    * Returns the current version of this plugin.
    *
    * @return the current version of this plugin.
    */
   public String getVersion()
   {
      return "0.13";
   }

   /**
    * Returns the authors name.
    *
    * @return the authors name.
    */
   public String getAuthor()
   {
      return "Rob Manning";
   }

   /**
    * Returns a comma separated list of other contributors.
    *
    * @return Contributors names.
    */
   public String getContributors()
   {
      return "Alex Pivovarov";
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getChangeLogFileName()
    */
   public String getChangeLogFileName()
   {
      return "changes.txt";
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getHelpFileName()
    */
   public String getHelpFileName()
   {
      return "doc/readme.html";
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getLicenceFileName()
    */
   public String getLicenceFileName()
   {
      return "licence.txt";
   }

   /**
    * Load this plugin.
    *
    * @param   app    Application API.
    */
   public synchronized void load(IApplication app) throws PluginException
   {
      super.load(app);
      app.addApplicationListener(() -> _appShuttingDown = true);

      String derbySystemHome = System.getProperty("derby.system.home", "");
      if (derbySystemHome.isEmpty())
      {
         try
         {
            derbySystemHome = getPluginUserSettingsFolder().getAbsolutePath();
            System.setProperty("derby.system.home", derbySystemHome);
            s_log.info("derby.system.home set to: " + derbySystemHome);
         }
         catch (IOException e)
         {
            s_log.warn("Could not initialize derby.system.home", e);
         }
      }
   }

   /**
    * Create panel for the Global Properties dialog.
    *
    * @return properties panel.
    */
   public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
   {
      PluginQueryTokenizerPreferencesPanel _prefsPanel = new DerbyPluginPreferencesPanel(_prefsManager);

      PluginGlobalPreferencesTab tab = new PluginGlobalPreferencesTab(_prefsPanel);

      tab.setHint(i18n.PREFS_HINT);
      tab.setTitle(i18n.PREFS_TITLE);

      return new IGlobalPreferencesPanel[]{tab};
   }

   /**
    * Initialize this plugin.
    */
   public synchronized void initialize() throws PluginException
   {
      super.initialize();

      //Add session ended listener -- needs for Embedded Derby DB
      _app.getSessionManager().addSessionListener(new SessionListener());


      _prefsManager = new PluginQueryTokenizerPreferencesManager();
      _prefsManager.initialize(this, new DerbyPreferenceBean());

      /** Need to do this to pickup user's prefs from file */
      DerbyPreferenceBean prefBean = (DerbyPreferenceBean) _prefsManager.getPreferences();

      if (prefBean.isReadClobsFully())
      {
         /* Register custom DataTypeComponent factory for Derby CLOB type */
         Main.getApplication().getDataTypeComponentFactoryRegistry().registerDataTypeFactory(new DerbyClobDataTypeComponentFactory());
      }
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin#allowsSessionStartedInBackground()
    */
   public boolean allowsSessionStartedInBackground()
   {
      return true;
   }

   /**
    * Session has been started. Update the tree api in using the event thread
    *
    * @param session Session that has started.
    * @return <TT>true</TT> if session is Oracle in which case this plugin
    * is interested in it.
    */
   public PluginSessionCallback sessionStarted(final ISession session)
   {
      if (!isPluginSession(session))
      {
         return null;
      }

      DerbyPreferenceBean prefBean = (DerbyPreferenceBean) _prefsManager.getPreferences();

      if (prefBean.isInstallCustomQueryTokenizer())
      {
         DerbyQueryTokenizer tokenizer =
               new DerbyQueryTokenizer(prefBean.getStatementSeparator(), prefBean.getLineComment(), prefBean.isRemoveMultiLineComments(), prefBean.isRemoveLineComments());

         session.setQueryTokenizer(tokenizer);
      }

      GUIUtils.processOnSwingEventThread(() -> updateTreeApi(session.getSessionInternalFrame().getObjectTreeAPI()));

      if (isEmbeddedDerby(session))
      {
         _embeddedSessions.computeIfAbsent(getDatabaseName(session), k -> new ArrayList<>())
               .add(new WeakReference<>(session));

         _embeddedDriver = session.getDriver();
      }

      return new PluginSessionCallback()
      {
         @Override
         public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
         {
         }

         @Override
         public void additionalSQLTabOpened(AdditionalSQLTab additionalSQLTab)
         {
         }

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
      };
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin#isPluginSession(net.sourceforge.squirrel_sql.client.session.ISession)
    */
   @Override
   protected boolean isPluginSession(ISession session)
   {
      return DialectFactory.isDerby(session.getMetaData());
   }

   /**
    * @param objectTreeAPI
    */
   private void updateTreeApi(IObjectTreeAPI objectTreeAPI)
   {
      DerbyPreferenceBean prefBean = (DerbyPreferenceBean) _prefsManager.getPreferences();

      _treeAPI = objectTreeAPI;

      _treeAPI.addDetailTab(DatabaseObjectType.PROCEDURE, new ProcedureSourceTab(i18n.SHOW_PROCEDURE_SOURCE, prefBean.getStatementSeparator()));

      _treeAPI.addDetailTab(DatabaseObjectType.VIEW, new ViewSourceTab(i18n.SHOW_VIEW_SOURCE));

      _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
      _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER_TYPE_DBO, new DatabaseObjectInfoTab());

      TableWithChildNodesExpander trigExp = new TableWithChildNodesExpander();
      trigExp.setTableTriggerExtractor(new DerbyTableTriggerExtractorImpl());
      _treeAPI.addExpander(DatabaseObjectType.TABLE, trigExp);

      _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
      _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerSourceTab("The source of the trigger"));

   }

   /**
    * A session listener that shutdown Embedded Derby when session and
    * connection are already closed
    *
    * @author Alex Pivovarov
    */
   private class SessionListener extends SessionAdapter
   {
      @Override
      public void sessionClosed(SessionEvent evt)
      {
         if (flushEmbeddedSessions())
         {
            shutdownEmbeddedDerby();
         }
      }
   }

   private boolean shouldDrop(String dbName)
   {
      if (_appShuttingDown)
      {
         return true;
      }

      int option = JOptionPane.showOptionDialog(null, // Application-modal
                                                MessageFormat.format(i18n.DROP_DB_QUESTION, dbName.replaceFirst("^memory:", "")),
                                                i18n.EMBEDDED_DERBY_TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                                                null, new Object[]{i18n.DROP_OPTION, i18n.KEEP_OPTION}, i18n.KEEP_OPTION);
      return option == JOptionPane.YES_OPTION;
   }

   private boolean flushEmbeddedSessions()
   {
      if (_embeddedSessions.isEmpty())
      {
         return false;
      }

      for (Iterator<Map.Entry<String, List<WeakReference<ISession>>>> iter = _embeddedSessions.entrySet().iterator(); iter.hasNext(); )
      {
         Map.Entry<String, List<WeakReference<ISession>>> entry = iter.next();
         if (flushEmbeddedSessions(entry.getValue()))
         {
            String dbName = entry.getKey();
            if (dbName.startsWith("memory:"))
            {
               if (shouldDrop(dbName))
               {
                  shutdownEmbeddedDerby(dbName, true);
                  iter.remove();
               }
            }
            else if (!dbName.equals(UNKNOWN_DBNAME))
            {
               shutdownEmbeddedDerby(dbName);
               iter.remove();
            }
         }
      }
      return _embeddedSessions.isEmpty();
   }

   private boolean flushEmbeddedSessions(List<WeakReference<ISession>> list)
   {
      if (list.isEmpty())
      {
         return false;
      }

      Iterator<WeakReference<ISession>> iter = list.iterator();
      while (iter.hasNext())
      {
         ISession session = iter.next().get();
         if (session == null || session.isClosed())
         {
            iter.remove();
         }
      }
      return list.isEmpty();
   }

   static boolean isEmbeddedDerby(final ISession session)
   {
      return session.getDriver().getDriverClassName().startsWith("org.apache.derby.jdbc.EmbeddedDriver");
   }

   private static String getDatabaseName(ISession session)
   {
      try
      {
         String jdbcURL = session.getMetaData().getURL();
         int semIdx = jdbcURL.indexOf(';');
         return (semIdx > 0) ? jdbcURL.substring("jdbc:derby:".length(), semIdx)
               : jdbcURL.substring("jdbc:derby:".length());
      }
      catch (SQLException e)
      {
         s_log.warn("Could not determine Embedded Derby database name", e);
         return UNKNOWN_DBNAME;
      }
   }

   /**
    * Shutdown Embedded Derby DB and reload JDBC Driver
    *
    * @author Alex Pivovarov
    */
   private void shutdownEmbeddedDerby()
   {
      shutdownEmbeddedDerby("");
   }

   /**
    * Shutdown a single Embedded Derby database.
    *
    * @param dbName the database name to shut down;
    */
   private void shutdownEmbeddedDerby(String dbName)
   {
      shutdownEmbeddedDerby(dbName, false);
   }

   private void shutdownEmbeddedDerby(String dbName, boolean drop)
   {
      try
      {
         ISQLDriver iSqlDr = _embeddedDriver;
         if (_embeddedDriver == null)
         {
            s_log.warn("shutdownEmbeddedDerby: driver reference is null");
            return;
         }
         //the code bellow is only for Embedded Derby Driver
         IIdentifier drId = iSqlDr.getIdentifier();
         SQLDriverManager sqlDrMan = _app.getSQLDriverManager();
         //Getting java.sql.Driver to run shutdown command
         Driver jdbcDr = sqlDrMan.getJDBCDriver(drId);
         //Shutdown Embedded Derby DB
         try
         {
            jdbcDr.connect("jdbc:derby:" + dbName + ";"
                                 + (drop ? "drop" : "shutdown") + "=true", new Properties());
         }
         catch (SQLException e)
         {
            //it is always thrown as said in Embedded Derby API.
            //So it is not error it is info
            if (Arrays.asList("08006", "XJ015").contains(e.getSQLState()))
            {
               _app.getMessageHandler().showMessage(e.getMessage());
            }
            else
            {
               _app.getMessageHandler().showErrorMessage(e);
            }
         }
         //Re-registering driver is necessary for Embedded Derby
         if (dbName.isEmpty())
         {
            sqlDrMan.registerSQLDriver(iSqlDr);
            _embeddedDriver = null;
         }
      }
      catch (Exception e)
      {
         s_log.error(e.getMessage(), e);
      }
   }
}
