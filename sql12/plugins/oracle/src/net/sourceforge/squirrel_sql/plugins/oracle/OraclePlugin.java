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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLInternalFrame;
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
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.oracle.SGAtrace.NewSGATraceWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.dboutput.NewDBOutputWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.DatabaseExpander;
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
import net.sourceforge.squirrel_sql.plugins.oracle.invalidobjects.NewInvalidObjectsWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.sessioninfo.NewSessionInfoWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.IndexColumnInfoTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.IndexDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.InstanceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.ObjectSourceTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.OptionsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.SequenceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.SessionDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.SessionStatisticsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.TriggerColumnInfoTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.UserDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.ViewSourceTab;

/**
 * Oracle plugin class.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class OraclePlugin extends DefaultSessionPlugin
{
	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(OraclePlugin.class);

       private PluginResources _resources;

       private NewDBOutputWorksheetAction _newDBOutputWorksheet;
       private NewInvalidObjectsWorksheetAction _newInvalidObjectsWorksheet;
       private NewSessionInfoWorksheetAction _newSessionInfoWorksheet;
       private NewSGATraceWorksheetAction _newSGATraceWorksheet;

       /** A list of Oracle sessions that are open so we'll know when none are left */
       private ArrayList oracleSessions = new ArrayList();
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
                super.initialize();

                final IApplication app = getApplication();

                _resources = new OracleResources(
                        "net.sourceforge.squirrel_sql.plugins.oracle.oracle",
                        this);

                app.getWindowManager().addSessionSheetListener(new OraclePluginFactory());

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
	}

	/**
	 * Application is shutting down so save preferences.
	 */
	public void unload() // throws PluginException
		{
		super.unload();
	}

   public boolean allowsSessionStartedInBackground()
   {
      return true;
   }

   public PluginSessionCallback sessionStarted(ISession session)
   {
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

    /**
     * Return a node expander for the object tree for a particular default node type.
     * <p/> A plugin could return non null here if they wish to override the default node
     * expander bahaviour. Most plugins should return null here.
     */
    public INodeExpander getDefaultNodeExpander(ISession session, DatabaseObjectType type) {
      boolean isOracle = isOracle(session);
      if ((type == IObjectTreeAPI.PROC_TYPE_DBO) && isOracle) {
          return new ProcedureExpander();
      }
      if (type == IObjectTreeAPI.DATABASE_TYPE_DBO && isOracle) {
          return new DefaultDatabaseExpander(session); 
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

    
    public class OraclePluginSessionListener extends SessionAdapter {
        
        public void sessionActivated(SessionEvent evt) {
          final ISession session = evt.getSession();
          final boolean enable = isOracle(session);
          GUIUtils.processOnSwingEventThread(new Runnable() {
              public void run() {
                  _newDBOutputWorksheet.setEnabled(enable);
                  _newInvalidObjectsWorksheet.setEnabled(enable);
                  _newSessionInfoWorksheet.setEnabled(enable);
                  _newSGATraceWorksheet.setEnabled(enable);     
              }
          });
        }
        
        public void sessionClosing(SessionEvent evt) {
            final ISession session = evt.getSession();
            if (isOracle(session)) {
                int idx = oracleSessions.indexOf(session);
                if (idx != -1) {
                    oracleSessions.remove(idx);
                }
            }
            // if the last oracle session is closing, then disable the 
            // worksheets
            if (oracleSessions.size() == 0) {
                GUIUtils.processOnSwingEventThread(new Runnable() {
                    public void run() {
                        _newDBOutputWorksheet.setEnabled(false);
                        _newInvalidObjectsWorksheet.setEnabled(false);
                        _newSessionInfoWorksheet.setEnabled(false);
                        _newSGATraceWorksheet.setEnabled(false);     
                    }
                });
            }
        }
      }
    /** This class listens to new frames as they are opened and adds
     *  object from this plugins.
     */
    public class OraclePluginFactory extends InternalFrameAdapter {
        public void internalFrameOpened(InternalFrameEvent e) {
            
            if (e.getInternalFrame() instanceof ISQLInternalFrame) {
                final ISQLPanelAPI panel = ((ISQLInternalFrame)e.getInternalFrame()).getSQLPanelAPI();
                final ISession session = panel.getSession();
                session.getApplication().getThreadPool().addTask(new Runnable() {
                    public void run() {
                        if (!isOracle(session)) {
                            return;
                        }                        
                        GUIUtils.processOnSwingEventThread(new Runnable() {
                            public void run() {
                                panel.addExecutor(new ExplainPlanExecuter(session, panel));
                            }
                        });                      
                    }
                });
            }
            if (e.getInternalFrame() instanceof IObjectTreeInternalFrame) {
                final IObjectTreeAPI objTree = ((IObjectTreeInternalFrame)e.getInternalFrame()).getObjectTreeAPI();
                final ISession session = objTree.getSession();
                session.getApplication().getThreadPool().addTask(new Runnable() {
                    public void run() {
                        if (!isOracle(session)) {
                            return;
                        }
                        // This will use the event dispatch thread where appropritate
                        updateObjectTree(objTree);
                    }
                });
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
        addExpander(objTree, DatabaseObjectType.SESSION, new DatabaseExpander());
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
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                objTree.addExpander(dboType, exp);
            }
        });
    }
    
    private void addDetailTab(final IObjectTreeAPI objTree,
                              final DatabaseObjectType dboType,
                              final IObjectTab tab)
    {
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                objTree.addDetailTab(dboType, tab);
            }
        });
    }
}
