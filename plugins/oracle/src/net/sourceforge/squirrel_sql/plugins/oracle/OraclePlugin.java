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
import net.sourceforge.squirrel_sql.client.IApplication;
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
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
import net.sourceforge.squirrel_sql.plugins.oracle.SGAtrace.NewSGATraceWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.dboutput.NewDBOutputWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.*;
import net.sourceforge.squirrel_sql.plugins.oracle.explainplan.ExplainPlanExecuter;
import net.sourceforge.squirrel_sql.plugins.oracle.invalidobjects.NewInvalidObjectsWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.sessioninfo.NewSessionInfoWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Oracle plugin class.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class OraclePlugin extends DefaultSessionPlugin
{
   /** Logger for this class. */
   private final static ILogger s_log = LoggerController.createLogger(OraclePlugin.class);


   /** Internationalized strings for this class. */
   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(OraclePlugin.class);


       private PluginResources _resources;

       private NewDBOutputWorksheetAction _newDBOutputWorksheet;
       private NewInvalidObjectsWorksheetAction _newInvalidObjectsWorksheet;
       private NewSessionInfoWorksheetAction _newSessionInfoWorksheet;
       private NewSGATraceWorksheetAction _newSGATraceWorksheet;

       /** A list of Oracle sessions that are open so we'll know when none are left */
       private ArrayList oracleSessions = new ArrayList();
   /** SQL to find schemas to which the logged in user has access */
   private static String SCHEMA_ACCESS_SQL =
       "SELECT DISTINCT OWNER FROM USER_TAB_PRIVS";
   /** SQL to determine whether or not this account is a DBA account */
   private static String DBA_ROLE_SQL =
       "SELECT GRANTED_ROLE FROM USER_ROLE_PRIVS";


   private HashMap _allowedSchemasBySessionID = new HashMap();
   private static final String ORACLE_PREFS_FILE = "oraclePrefs.xml";
   private OracleGlobalPrefs _oracleGlobalPrefs;
   private OraclePrefsPanelController _oraclePrefsPanelController;

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
      return "0.16";
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

   public void initialize() throws PluginException
   {
      try
      {
         super.initialize();

         final IApplication app = getApplication();

         _resources = new OracleResources(
            "net.sourceforge.squirrel_sql.plugins.oracle.oracle",
            this);

         //Add the actions to the action bar.
         _newDBOutputWorksheet = new NewDBOutputWorksheetAction(app, _resources);
         _newDBOutputWorksheet.setEnabled(false);
         addToToolBar(_newDBOutputWorksheet);

         _newInvalidObjectsWorksheet = new NewInvalidObjectsWorksheetAction(app, _resources);
         _newInvalidObjectsWorksheet.setEnabled(false);
         addToToolBar(_newInvalidObjectsWorksheet);

         _newSessionInfoWorksheet = new NewSessionInfoWorksheetAction(app, _resources);
         _newSessionInfoWorksheet.setEnabled(false);
         addToToolBar(_newSessionInfoWorksheet);

         _newSGATraceWorksheet = new NewSGATraceWorksheetAction(app, _resources);
         _newSGATraceWorksheet.setEnabled(false);
         addToToolBar(_newSGATraceWorksheet);


         app.getSessionManager().addSessionListener(new OraclePluginSessionListener());

         app.getSessionManager().addAllowedSchemaChecker(new IAllowedSchemaChecker()
         {
            public String[] getAllowedSchemas(ISession session)
            {
               return onGetAllowedSchemas(session);
            }
         });

         File f = getGlobalPrefsFile();

         if(f.exists())
         {
            XMLBeanReader xbr = new XMLBeanReader();
            xbr.load(f, getClass().getClassLoader());
            _oracleGlobalPrefs = (OracleGlobalPrefs) xbr.iterator().next();
         }
         else
         {
            _oracleGlobalPrefs = new OracleGlobalPrefs();
         }
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
      return new File(getPluginUserSettingsFolder().getPath() + File.separator + ORACLE_PREFS_FILE);
   }

   public void unload()
   {
      try
      {
         File f = getGlobalPrefsFile();

         XMLBeanWriter xbw = new XMLBeanWriter(_oracleGlobalPrefs);
         xbw.save(f);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

   }


   /**
       * Create preferences panel for the Global Preferences dialog.
       *
       * @return  Preferences panel.
       */
      public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
      {
         if(null == _oraclePrefsPanelController)
         {
            _oraclePrefsPanelController = new OraclePrefsPanelController(_oracleGlobalPrefs);
         }

         return
            new OraclePrefsPanelController[]
               {
                  _oraclePrefsPanelController
               };
      }

   public PluginSessionCallback sessionStarted(final ISession session)
   {
      if (!isOracle(session))
      {
         return null;
      }


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


   public void sessionEnding(ISession session)
   {
      _allowedSchemasBySessionID.remove(session.getIdentifier());
   }

   /**
     * Return a node expander for the object tree for a particular default node type.
     * <p/> A plugin could return non null here if they wish to override the default node
     * expander bahaviour. Most plugins should return null here.
     */
    public INodeExpander getDefaultNodeExpander(ISession session, DatabaseObjectType type) {
      boolean isOracle = isOracle(session);
      if ((type == DatabaseObjectType.PROC_TYPE_DBO) && isOracle) {
          return new ProcedureExpander();
      }
      if (type == DatabaseObjectType.DATABASE_TYPE_DBO && isOracle) {
          return new DefaultDatabaseExpander(session, this);
      }
      return null;
    }

    /**
     * Adds the specified action to the session main frame tool bar in such a 
     * way that GUI work is done in the event dispatch thread.
     * @param action
     */
    public void addToToolBar(final Action action) {
        final IApplication app = getApplication();
        if (SwingUtilities.isEventDispatchThread()) {
            app.getMainFrame().addToToolBar(action);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    app.getMainFrame().addToToolBar(action);
                }
            });
        }
    }


   private boolean isOracle(ISession session)
   {
      final String ORACLE = "oracle";
      String dbms = null;
        if (oracleSessions.contains(session)) {
            return true;
        }
        String prodName = session.getDatabaseProductName();
        if (prodName != null) {
            if (prodName.toLowerCase().startsWith(ORACLE)) {
                oracleSessions.add(session);
                return true;
            } else {
                return false;
            }
        }
      try
      {
            SQLConnection con = session.getSQLConnection();
            if (con != null) {
                SQLDatabaseMetaData data = con.getSQLMetaData();
                if (data != null) {
                    dbms = data.getDatabaseProductName();
                }
            }
      }
      catch (SQLException ex)
      {
         s_log.debug("Error in getDatabaseProductName()", ex);
      }
        if (dbms != null && dbms.toLowerCase().startsWith(ORACLE)) {
            oracleSessions.add(session);
            return true;
        }
        return false;
   }

   private String[] onGetAllowedSchemas(ISession session)
   {
      if(isOracle(session))
      {
         String[] ret = (String[]) _allowedSchemasBySessionID.get(session.getIdentifier());
         if(null == ret)
         {
            ret = getAccessibleSchemas(session);
            _allowedSchemasBySessionID.put(session.getIdentifier(), ret);
         }

         return ret;
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
    * @return an array of strings representing the names of accessible schemas
    */
   public String[] getAccessibleSchemas(ISession session)
   {
      String[] result = null;
      ResultSet rs = null;
      Statement stmt = null;
      SQLConnection con = session.getSQLConnection();
      SQLDatabaseMetaData md = con.getSQLMetaData();
      String currentUserName = null;
      try
      {
         if (hasSystemPrivilege(session))
         {
            result = md.getSchemas();
         }
         else
         {
            currentUserName = md.getUserName();
            stmt = con.getConnection().createStatement();
            rs = stmt.executeQuery(SCHEMA_ACCESS_SQL);
            ArrayList tmp = new ArrayList();
            while (rs.next())
            {
               tmp.add(rs.getString(1));
            }
            if (currentUserName != null && !tmp.contains(currentUserName))
            {
               tmp.add(currentUserName);
            }

            tmp.remove("SYS");

            if(_oracleGlobalPrefs.isLoadSysSchema())
            {
               tmp.add("SYS");
            }

            result = (String[]) tmp.toArray(new String[tmp.size()]);
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
    private boolean hasSystemPrivilege(ISession session) {
        boolean result = false;
        Statement stmt = null;
        ResultSet rs = null;
        SQLConnection con = session.getSQLConnection();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(DBA_ROLE_SQL);
            while (rs.next()) {
                String role = rs.getString(1);
                if ("DBA".equalsIgnoreCase(role)) {
                    result = true;
                    break;
                }
            }
        } catch (SQLException e) {
            // i18n[DefaultDatabaseExpander.error.retrieveuserroles=Unable to retrieve user roles]
            String msg =
                s_stringMgr.getString("DefaultDatabaseExpander.error.retrieveuserroles");
            s_log.error(msg, e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
        }
        return result;
    }


   public class OraclePluginSessionListener extends SessionAdapter
   {

      public void sessionActivated(SessionEvent evt)
      {
         final ISession session = evt.getSession();
         final boolean enable = isOracle(session);
         _newDBOutputWorksheet.setEnabled(enable);
         _newInvalidObjectsWorksheet.setEnabled(enable);
         _newSessionInfoWorksheet.setEnabled(enable);
         _newSGATraceWorksheet.setEnabled(enable);
      }

      public void sessionClosing(SessionEvent evt)
      {
         final ISession session = evt.getSession();
         if (isOracle(session))
         {
            int idx = oracleSessions.indexOf(session);
            if (idx != -1)
            {
               oracleSessions.remove(idx);
            }
         }
         // if the last oracle session is closing, then disable the
         // worksheets
         if (oracleSessions.size() == 0)
         {
            _newDBOutputWorksheet.setEnabled(false);
            _newInvalidObjectsWorksheet.setEnabled(false);
            _newSessionInfoWorksheet.setEnabled(false);
            _newSGATraceWorksheet.setEnabled(false);
         }
      }
   }

/**
* Check if we can run query.
*
* @param session session
* @param query query text
* @return true if query works fine
*/
    public static boolean checkObjectAccessible(final ISession session, final String query) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = session.getSQLConnection().prepareStatement(query);
            rs = pstmt.executeQuery();
            return true;
        } catch (SQLException ex) {
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException ex) {}
        }
    }

    private void updateObjectTree(IObjectTreeAPI objTree) {
        ISession session = objTree.getSession();
        DatabaseObjectInfoTab dboit = new DatabaseObjectInfoTab();
        addDetailTab(objTree, DatabaseObjectType.SESSION, new OptionsTab());
        addDetailTab(objTree, IObjectTypes.CONSUMER_GROUP, dboit);
        addDetailTab(objTree, DatabaseObjectType.FUNCTION, dboit);
        addDetailTab(objTree, DatabaseObjectType.INDEX, dboit);
        addDetailTab(objTree, DatabaseObjectType.INDEX, new IndexColumnInfoTab());
        addDetailTab(objTree, DatabaseObjectType.INDEX, new IndexDetailsTab());
        addDetailTab(objTree, IObjectTypes.LOB, dboit);
        addDetailTab(objTree, DatabaseObjectType.SEQUENCE, dboit);
        addDetailTab(objTree, DatabaseObjectType.TRIGGER, dboit);
        addDetailTab(objTree, IObjectTypes.TRIGGER_PARENT, dboit);
        addDetailTab(objTree, IObjectTypes.TYPE, dboit);

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
