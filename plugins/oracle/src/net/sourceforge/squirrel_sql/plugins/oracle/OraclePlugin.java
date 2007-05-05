package net.sourceforge.squirrel_sql.plugins.oracle;
/*
 * Copyright (C) 2002-2003 Colin Bell
 * colbell@users.sourceforge.net
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

import java.io.File;
import java.io.IOException;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.IAliasPropertiesPanelController;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IAllowedSchemaChecker;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.oracle.SGAtrace.NewSGATraceWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.dboutput.NewDBOutputWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.DefaultDatabaseExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.InstanceParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.PackageExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.ProcedureExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.SchemaExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.SessionParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.TableExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.TriggerParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.UserParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.explainplan.ExplainPlanExecuter;
import net.sourceforge.squirrel_sql.plugins.oracle.gui.OracleGlobalPreferencesTab;
import net.sourceforge.squirrel_sql.plugins.oracle.invalidobjects.NewInvalidObjectsWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.prefs.OraclePreferenceBean;
import net.sourceforge.squirrel_sql.plugins.oracle.prefs.PreferencesManager;
import net.sourceforge.squirrel_sql.plugins.oracle.sessioninfo.NewSessionInfoWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.IndexColumnInfoTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.IndexDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.InstanceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.ObjectSourceTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.OptionsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.SequenceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.SessionDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.SessionStatisticsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.SnapshotSourceTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.TriggerColumnInfoTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.UserDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.ViewSourceTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tokenizer.OracleQueryTokenizer;

/**
 * Oracle plugin class.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class OraclePlugin extends DefaultSessionPlugin
{
   /**
    * Logger for this class.
    */
   private final static ILogger s_log = LoggerController.createLogger(OraclePlugin.class);


   /**
    * Internationalized strings for this class.
    */
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(OraclePlugin.class);


   private PluginResources _resources;

   /**
    * A list of Oracle sessions that are open so we'll know when none are left
    */
   private ArrayList<ISession> oracleSessions = new ArrayList<ISession>();
   /**
    * SQL to find schemas to which the logged in user has access
    */
   private static String SCHEMA_ACCESS_SQL =
      "SELECT DISTINCT OWNER FROM ALL_OBJECTS";
   /**
    * SQL to determine whether or not this account is a DBA account
    */
   private static String DBA_ROLE_SQL =
      "SELECT GRANTED_ROLE FROM USER_ROLE_PRIVS";


   private static final String ORACLE_ALIAS_PREFS_FILE = "oracleAliasPrefs.xml";
   
   private Hashtable<IIdentifier, OracleAliasPrefs> _oracleAliasPrefsByAliasIdentifier = 
       new Hashtable<IIdentifier, OracleAliasPrefs>();

   /**
    * Return the internal name of this plugin.
    *
    * @return	the internal name of this plugin.
    */
   public String getInternalName()
   {
      return "oracle";
   }

   /**
    * Return the descriptive name of this plugin.
    *
    * @return	the descriptive name of this plugin.
    */
   public String getDescriptiveName()
   {
      return "Oracle Plugin";
   }

   /**
    * Returns the current version of this plugin.
    *
    * @return	the current version of this plugin.
    */
   public String getVersion()
   {
      return "0.17";
   }

   /**
    * Returns the authors name.
    *
    * @return	the authors name.
    */
   public String getAuthor()
   {
      return "Colin Bell";
   }

    /**
     * Returns a comma separated list of other contributors.
     *
     * @return  Contributors names.
     */
    public String getContributors()
    {
        return "Alexander Buloichik, Rob Manning";
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
      return "readme.html";
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getLicenceFileName()
    */
   public String getLicenceFileName()
   {
      return "licence.txt";
   }

   /**
    * Create panel for the Global Properties dialog.
    * 
    * @return  properties panel.
    */
   public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
       OracleGlobalPreferencesTab tab = new OracleGlobalPreferencesTab();
       return new IGlobalPreferencesPanel[] { tab };
   }
   
   
   public void initialize() throws PluginException
   {
      try
      {
         super.initialize();

         final IApplication app = getApplication();

         _resources = new OracleResources(
            "net.sourceforge.squirrel_sql.plugins.oracle.oracle",
            this);


         ActionCollection coll = app.getActionCollection();
         coll.add(new NewDBOutputWorksheetAction(app, _resources));
         coll.add(new NewInvalidObjectsWorksheetAction(app, _resources));
         coll.add(new NewSessionInfoWorksheetAction(app, _resources));
         coll.add(new NewSGATraceWorksheetAction(app, _resources));


         app.getSessionManager().addAllowedSchemaChecker(new IAllowedSchemaChecker()
         {
            public String[] getAllowedSchemas(ISQLConnection con, ISQLAliasExt alias)
            {
               return onGetAllowedSchemas(con, alias);
            }
         });

         File f = getGlobalPrefsFile();

         if(f.exists())
         {
            XMLBeanReader xbr = new XMLBeanReader();
            xbr.load(f, getClass().getClassLoader());

            for(Iterator i=xbr.iterator(); i.hasNext();)
            {
               OracleAliasPrefs buf = (OracleAliasPrefs) i.next();
               _oracleAliasPrefsByAliasIdentifier.put(buf.getAliasIdentifier(), buf);
            }

         }
         else
         {
            _oracleAliasPrefsByAliasIdentifier = 
                new Hashtable<IIdentifier, OracleAliasPrefs>();
         }
         PreferencesManager.initialize(this);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void load(IApplication app) throws PluginException
   {
      super.load(app);
   }

   private File getGlobalPrefsFile()
      throws IOException
   {
      return new File(getPluginUserSettingsFolder().getPath() + File.separator + ORACLE_ALIAS_PREFS_FILE);
   }

   public void unload()
   {
      try
      {
         File f = getGlobalPrefsFile();

         XMLBeanWriter xbw = new XMLBeanWriter();

         xbw.addToRoot(_oracleAliasPrefsByAliasIdentifier.values().iterator());


         xbw.save(f);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

   }


   /**
    * Create Alias prefs panel.
    */
   public IAliasPropertiesPanelController[] getAliasPropertiesPanelControllers(SQLAlias alias)
   {
      if(false == isOracle(alias))
      {
         return new IAliasPropertiesPanelController[0];
      }

      OracleAliasPrefs aliasPrefs = 
          _oracleAliasPrefsByAliasIdentifier.get(alias.getIdentifier());

      if (null == aliasPrefs)
      {
         aliasPrefs = new OracleAliasPrefs();
         aliasPrefs.setAliasIdentifier(alias.getIdentifier());
         _oracleAliasPrefsByAliasIdentifier.put(alias.getIdentifier(), aliasPrefs);
      }

      return new OracleAliasPrefsPanelController[]{new OracleAliasPrefsPanelController(aliasPrefs)};
   }

   public void aliasCopied(SQLAlias source, SQLAlias target)
   {
      if(false == isOracle(source) || false == isOracle(target))
      {
         return;
      }

      OracleAliasPrefs sourcePrefs = 
          _oracleAliasPrefsByAliasIdentifier.get(source.getIdentifier());

      if(null != sourcePrefs)
      {
         OracleAliasPrefs targetPrefs = (OracleAliasPrefs) Utilities.cloneObject(sourcePrefs, getClass().getClassLoader());
         targetPrefs.setAliasIdentifier(target.getIdentifier());
         _oracleAliasPrefsByAliasIdentifier.put(targetPrefs.getAliasIdentifier(), targetPrefs);
      }
   }

   public void aliasRemoved(SQLAlias alias)
   {
      _oracleAliasPrefsByAliasIdentifier.remove(alias.getIdentifier());
   }

    /**
     * Called when a session shutdown.
     *
     * @param   session The session that is ending.
     */
    public void sessionEnding(ISession session)
    {
        super.sessionEnding(session);
        oracleSessions.remove(session);
    }   

   public PluginSessionCallback sessionStarted(final ISession session)
   {
      if (!isOracle(session.getAlias()))
      {
         return null;
      }
      OraclePreferenceBean _prefs = PreferencesManager.getPreferences();
      if (_prefs.isInstallCustomQueryTokenizer()) {
          session.setQueryTokenizer(new OracleQueryTokenizer(_prefs));
      }
      GUIUtils.processOnSwingEventThread(new Runnable()
      {
         public void run()
         {
            addActions(session);
         }
      });

      oracleSessions.add(session);


      PluginSessionCallback ret = new PluginSessionCallback()
      {
         public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
         {
            onSQLInternaFrameOpened(sqlInternalFrame, sess);
         }

         public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
         {
            onObjectTreeInternalFrameOpened(objectTreeInternalFrame);
         }

      };

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            ISQLPanelAPI sqlPaneAPI = session.getSessionSheet().getSQLPaneAPI();
            sqlPaneAPI.addExecutor(new ExplainPlanExecuter(session, sqlPaneAPI));
            updateObjectTree(session.getSessionSheet().getObjectTreePanel());
         }
      });


      return ret;
   }

   @Override
   protected boolean isPluginSession(ISession session) {
       return DialectFactory.isOracle(session.getMetaData());
   }
   
   private void addActions(ISession session)
   {
      ActionCollection coll = getApplication().getActionCollection();
      session.addSeparatorToToolbar();
      session.addToToolbar(coll.get(NewDBOutputWorksheetAction.class));
      session.addToToolbar(coll.get(NewInvalidObjectsWorksheetAction.class));
      session.addToToolbar(coll.get(NewSessionInfoWorksheetAction.class));
      session.addToToolbar(coll.get(NewSGATraceWorksheetAction.class));


      session.getSessionInternalFrame().addToToolsPopUp("oracleoutput", coll.get(NewDBOutputWorksheetAction.class));
      session.getSessionInternalFrame().addToToolsPopUp("oracleinvalid", coll.get(NewInvalidObjectsWorksheetAction.class));
      session.getSessionInternalFrame().addToToolsPopUp("oracleinfo", coll.get(NewSessionInfoWorksheetAction.class));
      session.getSessionInternalFrame().addToToolsPopUp("oraclesga", coll.get(NewSGATraceWorksheetAction.class));

   }


   private void onSQLInternaFrameOpened(SQLInternalFrame sqlInternalFrame, final ISession session)
   {
      final ISQLPanelAPI panel = sqlInternalFrame.getSQLPanelAPI();
      panel.addExecutor(new ExplainPlanExecuter(session, panel));
   }

   private void onObjectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame)
   {
      final IObjectTreeAPI objTree = objectTreeInternalFrame.getObjectTreeAPI();
      updateObjectTree(objTree);
   }



   /**
     * Return a node expander for the object tree for a particular default node type.
     * <p/> A plugin could return non null here if they wish to override the default node
     * expander bahaviour. Most plugins should return null here.
     */
    public INodeExpander getDefaultNodeExpander(ISession session, DatabaseObjectType type) {
      boolean isOracle = isOracle(session.getAlias());
      if ((type == DatabaseObjectType.PROC_TYPE_DBO) && isOracle) {
          return new ProcedureExpander();
      }
      if (type == DatabaseObjectType.DATABASE_TYPE_DBO && isOracle) {
          return new DefaultDatabaseExpander(session);
      }
      return null;
    }


   private boolean isOracle(ISQLAliasExt alias)
   {
      IIdentifier driverIdentifier = alias.getDriverIdentifier();
      Driver jdbcDriver = getApplication().getSQLDriverManager().getJDBCDriver(driverIdentifier);

      if(null == jdbcDriver)
      {
         return false;
      }

      return jdbcDriver.getClass().getName().startsWith("oracle.");
   }

   private String[] onGetAllowedSchemas(ISQLConnection con, ISQLAliasExt alias)
   {
      if(isOracle(alias))
      {
         OracleAliasPrefs prefs = 
             _oracleAliasPrefsByAliasIdentifier.get(alias.getIdentifier());

         if(null == prefs)
         {
            prefs = new OracleAliasPrefs();
            prefs.setAliasIdentifier(alias.getIdentifier());
            _oracleAliasPrefsByAliasIdentifier.put(prefs.getAliasIdentifier(), prefs);
         }

         return getAccessibleSchemas(prefs, con);
      }
      else
      {
         return null;
      }
   }



   /**
    * Returns an array of schema names that represent schemas in which there
    * exist tables that the user associated with the specified session has
    * privilege to access.
    *
    * @param session the session to retrieve schemas for
    * @param con
    * @return an array of strings representing the names of accessible schemas
    */
   private String[] getAccessibleSchemas(OracleAliasPrefs aliasPrefs, ISQLConnection con)
   {
      String[] result = null;
      ResultSet rs = null;
      Statement stmt = null;
      SQLDatabaseMetaData md = con.getSQLMetaData();
      String currentUserName = null;
      try
      {
         if (hasSystemPrivilege(con) || aliasPrefs.isLoadAllSchemas())
         {
            result = md.getSchemas();
         }
         else
         {
            currentUserName = md.getUserName();
            stmt = con.getConnection().createStatement();
            rs = stmt.executeQuery(SCHEMA_ACCESS_SQL);
            ArrayList<String> tmp = new ArrayList<String>();
            while (rs.next())
            {
               tmp.add(rs.getString(1));
            }
            if (currentUserName != null && !tmp.contains(currentUserName))
            {
               tmp.add(currentUserName);
            }

            tmp.remove("SYS");

            if(aliasPrefs.isLoadAccessibleSchemasAndSYS())
            {
               tmp.add("SYS");
            }

            result = tmp.toArray(new String[tmp.size()]);
         }
      }
      catch (SQLException e)
      {
         // i18n[DefaultDatabaseExpander.error.retrieveschemaprivs=Unable to retrieve schema privileges]
         String msg = s_stringMgr.getString("DefaultDatabaseExpander.error.retrieveschemaprivs");
         s_log.error(msg, e);
      }
      finally
      {
         if (rs != null) try
         {
            rs.close();
         }
         catch (SQLException e)
         {
         }
         if (stmt != null) try
         {
            stmt.close();
         }
         catch (SQLException e)
         {
         }
      }
      return result;
   }

   /**
    * Checks whether or not the user associated with the specified session has
    * been granted the DBA privilege.
    *
    * @param session the session to check
    * @return true if the user has the DBA privilege; false otherwise.
    */
   private boolean hasSystemPrivilege(ISQLConnection con)
   {
      boolean result = false;
      Statement stmt = null;
      ResultSet rs = null;
      try
      {
         stmt = con.createStatement();
         rs = stmt.executeQuery(DBA_ROLE_SQL);
         while (rs.next())
         {
            String role = rs.getString(1);
            if ("DBA".equalsIgnoreCase(role))
            {
               result = true;
               break;
            }
         }
      }
      catch (SQLException e)
      {
         // i18n[DefaultDatabaseExpander.error.retrieveuserroles=Unable to retrieve user roles]
         String msg =
            s_stringMgr.getString("DefaultDatabaseExpander.error.retrieveuserroles");
         s_log.error(msg, e);
      }
      finally
      {
         if (rs != null) try
         {
            rs.close();
         }
         catch (SQLException e)
         {
         }
         if (stmt != null) try
         {
            stmt.close();
         }
         catch (SQLException e)
         {
         }
      }
      return result;
   }


   /**
    * Check if we can run query.
    *
    * @param session session
    * @param query   query text
    * @return true if query works fine
    */
   public static boolean checkObjectAccessible(final ISession session, final String query)
   {
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      try
      {
         pstmt = session.getSQLConnection().prepareStatement(query);
         rs = pstmt.executeQuery();
         return true;
      }
      catch (SQLException ex)
      {
         return false;
      }
      finally
      {
         try
         {
            if (rs != null)
            {
               rs.close();
            }
            if (pstmt != null)
            {
               pstmt.close();
            }
         }
         catch (SQLException ex)
         {
         }
      }
   }

    private void updateObjectTree(final IObjectTreeAPI objTree) {
        ISession session = objTree.getSession();
        addDetailTab(objTree, DatabaseObjectType.SESSION, new OptionsTab());
        addDetailTab(objTree, IObjectTypes.CONSUMER_GROUP, new DatabaseObjectInfoTab());
        addDetailTab(objTree, DatabaseObjectType.FUNCTION, new DatabaseObjectInfoTab());
        addDetailTab(objTree, DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
        addDetailTab(objTree, DatabaseObjectType.INDEX, new IndexColumnInfoTab());
        addDetailTab(objTree, DatabaseObjectType.INDEX, new IndexDetailsTab());
        addDetailTab(objTree, IObjectTypes.LOB, new DatabaseObjectInfoTab());
        addDetailTab(objTree, DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
        addDetailTab(objTree, DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
        addDetailTab(objTree, IObjectTypes.TRIGGER_PARENT, new DatabaseObjectInfoTab());
        addDetailTab(objTree, IObjectTypes.TYPE, new DatabaseObjectInfoTab());

        // Expanders.
        addExpander(objTree, DatabaseObjectType.SCHEMA, new SchemaExpander(OraclePlugin.this));
        addExpander(objTree, DatabaseObjectType.TABLE, new TableExpander());
        addExpander(objTree, IObjectTypes.PACKAGE, new PackageExpander());
        addExpander(objTree, IObjectTypes.USER_PARENT, new UserParentExpander(session));
        addExpander(objTree, IObjectTypes.SESSION_PARENT, new SessionParentExpander());
        addExpander(objTree, IObjectTypes.INSTANCE_PARENT, new InstanceParentExpander(OraclePlugin.this));
        addExpander(objTree, IObjectTypes.TRIGGER_PARENT, new TriggerParentExpander());

        addDetailTab(objTree, DatabaseObjectType.PROCEDURE, new ObjectSourceTab("PROCEDURE", "Show stored procedure source"));
        addDetailTab(objTree, DatabaseObjectType.FUNCTION, new ObjectSourceTab("FUNCTION", "Show function source"));
        addDetailTab(objTree, IObjectTypes.PACKAGE, new ObjectSourceTab("PACKAGE", "Specification", "Show package specification"));
        addDetailTab(objTree, IObjectTypes.PACKAGE, new ObjectSourceTab("PACKAGE BODY", "Body", "Show package body"));
        addDetailTab(objTree, IObjectTypes.TYPE, new ObjectSourceTab("TYPE", "Specification", "Show type specification"));
        addDetailTab(objTree, IObjectTypes.TYPE, new ObjectSourceTab("TYPE BODY", "Body", "Show type body"));
        addDetailTab(objTree, IObjectTypes.INSTANCE, new InstanceDetailsTab());
        addDetailTab(objTree, DatabaseObjectType.SEQUENCE, new SequenceDetailsTab());
        addDetailTab(objTree, IObjectTypes.SESSION, new SessionDetailsTab());
        addDetailTab(objTree, IObjectTypes.SESSION, new SessionStatisticsTab());
        addDetailTab(objTree, DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
        addDetailTab(objTree, DatabaseObjectType.TRIGGER, new TriggerSourceTab());
        addDetailTab(objTree, DatabaseObjectType.TRIGGER, new TriggerColumnInfoTab());
        addDetailTab(objTree, DatabaseObjectType.USER, new UserDetailsTab(session));

        addDetailTab(objTree, DatabaseObjectType.VIEW, new ViewSourceTab());
        addDetailTab(objTree, DatabaseObjectType.TABLE, new SnapshotSourceTab());
        
        // This fixes the issue where the tree is getting constructed prior to 
        // the Oracle plugin expanders being registered.(USERS, SESSIONS, 
        // INSTANCES nodes have no children until the tree is refreshed).  Even
        // though this is a hack, it doesn't seem to negatively impact 
        // performance even when loading all schemas.
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                objTree.refreshTree();
            }
        });
    }

   private void addExpander(final IObjectTreeAPI objTree,
                            final DatabaseObjectType dboType,
                            final INodeExpander exp)
   {
      objTree.addExpander(dboType, exp);
   }

   private void addDetailTab(final IObjectTreeAPI objTree,
                             final DatabaseObjectType dboType,
                             final IObjectTab tab)
   {
      objTree.addDetailTab(dboType, tab);
   }
}
