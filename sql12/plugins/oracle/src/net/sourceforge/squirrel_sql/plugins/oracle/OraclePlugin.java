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
import java.sql.SQLException;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLInternalFrame;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.plugins.oracle.dboutput.NewDBOutputWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.DatabaseExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.InstanceParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.PackageExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.SchemaExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.SessionParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.TableExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.TriggerParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.UserParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.explainplan.ExplainPlanExecuter;
import net.sourceforge.squirrel_sql.plugins.oracle.invalidobjects.NewInvalidObjectsWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.sessioninfo.NewSessionInfoWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.SGAtrace.NewSGATraceWorksheetAction;
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

import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;

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
		return "0.13";
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
                app.getMainFrame().addToActionBar(_newDBOutputWorksheet);

                _newInvalidObjectsWorksheet = new NewInvalidObjectsWorksheetAction(app, _resources);
                _newInvalidObjectsWorksheet.setEnabled(false);
                app.getMainFrame().addToActionBar(_newInvalidObjectsWorksheet);

                _newSessionInfoWorksheet = new NewSessionInfoWorksheetAction(app, _resources);
                _newSessionInfoWorksheet.setEnabled(false);
                app.getMainFrame().addToActionBar(_newSessionInfoWorksheet);

                _newSGATraceWorksheet = new NewSGATraceWorksheetAction(app, _resources);
                _newSGATraceWorksheet.setEnabled(false);
                app.getMainFrame().addToActionBar(_newSGATraceWorksheet);


                app.getSessionManager().addSessionListener(new OraclePluginSessionListener());
	}

	/**
	 * Application is shutting down so save preferences.
	 */
	public void unload() // throws PluginException
		{
		super.unload();
	}

	private boolean isOracle(ISession session)
	{
		final String ORACLE = "oracle";
		String dbms = null;
		try
		{
			dbms = session.getSQLConnection().getSQLMetaData().getDatabaseProductName();
		}
		catch (SQLException ex)
		{
			s_log.debug("Error in getDatabaseProductName()", ex);
		}
		return dbms != null && dbms.toLowerCase().startsWith(ORACLE);
	}

        public class OraclePluginSessionListener extends SessionAdapter {
          public void sessionActivated(SessionEvent evt) {
            final ISession session = evt.getSession();

            boolean enable = isOracle(session);
            _newDBOutputWorksheet.setEnabled(enable);
            _newInvalidObjectsWorksheet.setEnabled(enable);
            _newSessionInfoWorksheet.setEnabled(enable);
            _newSGATraceWorksheet.setEnabled(enable);
          }
        }

        /** This class listens to new frames as they are opened and adds
         *  object from this plugins.
         */
        public class OraclePluginFactory extends InternalFrameAdapter {
          public void internalFrameOpened(InternalFrameEvent e) {
            if (e.getInternalFrame() instanceof ISQLInternalFrame) {
              ISQLPanelAPI panel = ((ISQLInternalFrame)e.getInternalFrame()).getSQLPanelAPI();
              ISession session = panel.getSession();
              if (isOracle(session))
                panel.addExecutor(new ExplainPlanExecuter(session, panel));
            }
            if (e.getInternalFrame() instanceof IObjectTreeInternalFrame) {
              IObjectTreeAPI objTree = ((IObjectTreeInternalFrame)e.getInternalFrame()).getObjectTreeAPI();
              ISession session = objTree.getSession();
              if (isOracle(session)) {
                objTree.addDetailTab(DatabaseObjectType.SESSION, new OptionsTab());
                objTree.addDetailTab(IObjectTypes.CONSUMER_GROUP, new DatabaseObjectInfoTab());
                objTree.addDetailTab(DatabaseObjectType.FUNCTION, new DatabaseObjectInfoTab());
                objTree.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
                objTree.addDetailTab(DatabaseObjectType.INDEX, new IndexColumnInfoTab());
                objTree.addDetailTab(DatabaseObjectType.INDEX, new IndexDetailsTab());
                objTree.addDetailTab(IObjectTypes.LOB, new DatabaseObjectInfoTab());
                objTree.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
                objTree.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
                objTree.addDetailTab(IObjectTypes.TRIGGER_PARENT, new DatabaseObjectInfoTab());
                objTree.addDetailTab(IObjectTypes.TYPE, new DatabaseObjectInfoTab());

                // Expanders.
                objTree.addExpander(DatabaseObjectType.SESSION, new DatabaseExpander());
                objTree.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander(OraclePlugin.this));
                objTree.addExpander(DatabaseObjectType.TABLE, new TableExpander());
                objTree.addExpander(IObjectTypes.PACKAGE, new PackageExpander());
                objTree.addExpander(IObjectTypes.USER_PARENT, new UserParentExpander());
                objTree.addExpander(IObjectTypes.SESSION_PARENT, new SessionParentExpander());
                objTree.addExpander(IObjectTypes.INSTANCE_PARENT, new InstanceParentExpander(OraclePlugin.this));
                objTree.addExpander(IObjectTypes.TRIGGER_PARENT, new TriggerParentExpander());

                objTree.addDetailTab(DatabaseObjectType.PROCEDURE, new ObjectSourceTab("PROCEDURE", "Show stored procedure source"));
                objTree.addDetailTab(DatabaseObjectType.FUNCTION, new ObjectSourceTab("FUNCTION", "Show function source"));
                objTree.addDetailTab(IObjectTypes.PACKAGE, new ObjectSourceTab("PACKAGE", "Specification", "Show package specification"));
                objTree.addDetailTab(IObjectTypes.PACKAGE, new ObjectSourceTab("PACKAGE BODY", "Body", "Show package body"));
                objTree.addDetailTab(IObjectTypes.TYPE, new ObjectSourceTab("TYPE", "Specification", "Show type specification"));
                objTree.addDetailTab(IObjectTypes.TYPE, new ObjectSourceTab("TYPE BODY", "Body", "Show type body"));
                objTree.addDetailTab(IObjectTypes.INSTANCE, new InstanceDetailsTab());
                objTree.addDetailTab(DatabaseObjectType.SEQUENCE, new SequenceDetailsTab());
                objTree.addDetailTab(IObjectTypes.SESSION, new SessionDetailsTab());
                objTree.addDetailTab(IObjectTypes.SESSION, new SessionStatisticsTab());
                objTree.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
                objTree.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerSourceTab());
                objTree.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerColumnInfoTab());
                objTree.addDetailTab(DatabaseObjectType.USER, new UserDetailsTab());
              }
            }
          }
        }
}
