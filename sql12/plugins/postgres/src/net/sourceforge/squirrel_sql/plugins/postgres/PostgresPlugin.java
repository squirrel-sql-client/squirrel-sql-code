package net.sourceforge.squirrel_sql.plugins.postgres;

/*
 * Copyright (C) 2007 Rob Manning manningr@users.sourceforge.net This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version. This library is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser
 * General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.IPluginResourcesFactory;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResourcesFactory;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableIndexExtractor;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.SchemaExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableWithChildNodesExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaDataFactory;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaDataFactory;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.IResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.postgres.actions.VacuumDatabaseAction;
import net.sourceforge.squirrel_sql.plugins.postgres.actions.VacuumTableAction;
import net.sourceforge.squirrel_sql.plugins.postgres.exp.PostgresSequenceInodeExpanderFactory;
import net.sourceforge.squirrel_sql.plugins.postgres.exp.PostgresTableIndexExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.postgres.exp.PostgresTableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.postgres.explain.ExplainExecuterPanel;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.ActiveConnections;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.IndexDetailsTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.IndexSourceTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.LockTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.ProcedureSourceTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.SequenceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.ViewSourceTab;
import net.sourceforge.squirrel_sql.plugins.postgres.types.PostgreSqlArrayTypeDataTypeComponentFactory;
import net.sourceforge.squirrel_sql.plugins.postgres.types.PostgreSqlGeometryTypeDataTypeComponentFactory;
import net.sourceforge.squirrel_sql.plugins.postgres.types.PostgreSqlOtherTypeDataTypeComponentFactory;
import net.sourceforge.squirrel_sql.plugins.postgres.types.PostgreSqlUUIDTypeDataTypeComponentFactory;
import net.sourceforge.squirrel_sql.plugins.postgres.types.PostgreSqlXmlTypeDataTypeComponentFactory;

/**
 * The main controller class for the Postgres plugin.
 * 
 * @author manningr
 */
public class PostgresPlugin extends DefaultSessionPlugin implements ISQLDatabaseMetaDataFactory {

    private IResources _resources;

    private IPluginResourcesFactory _resourcesFactory = new PluginResourcesFactory();

    /**
     * @param resourcesFactory the resourcesFactory to set
     */
    public void setResourcesFactory(final IPluginResourcesFactory resourcesFactory) {
        _resourcesFactory = resourcesFactory;
    }

    /** Logger for this class. */
    @SuppressWarnings("unused")
    private final static ILogger s_log = LoggerController.createLogger(PostgresPlugin.class);

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(PostgresPlugin.class);

    static interface i18n {

        // i18n[PostgresPlugin.showIndexSource=Show index source]
        String SHOW_INDEX_SOURCE = s_stringMgr.getString("PostgresPlugin.showIndexSource");

        // i18n[PostgresPlugin.showViewSource=Show view source]
        String SHOW_VIEW_SOURCE = s_stringMgr.getString("PostgresPlugin.showViewSource");

        // i18n[PostgresPlugin.showProcedureSource=Show procedure source]
        String SHOW_PROCEDURE_SOURCE = s_stringMgr.getString("PostgresPlugin.showProcedureSource");
    }

    public interface IMenuResourceKeys {

        String POSTGRES = "postgres";
    }

    /**
     * Return the internal name of this plugin.
     * 
     * @return the internal name of this plugin.
     */
    @Override
    public String getInternalName() {
        return "postgres";
    }

    /**
     * Return the descriptive name of this plugin.
     * 
     * @return the descriptive name of this plugin.
     */
    @Override
    public String getDescriptiveName() {
        return "Postgres Plugin";
    }

    /**
     * Returns the current version of this plugin.
     * 
     * @return the current version of this plugin.
     */
    @Override
    public String getVersion() {
        return "0.22";
    }

    /**
     * Returns the authors name.
     * 
     * @return the authors name.
     */
    @Override
    public String getAuthor() {
        return "Rob Manning";
    }

    /**
     * Returns a comma separated list of other contributors.
     * 
     * @return Contributors names.
     */
    @Override
    public String getContributors() {
        return "Daniel Regli, Yannick Winiger, Jarosław Jarmołowicz";
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getChangeLogFileName()
     */
    @Override
    public String getChangeLogFileName() {
        return "changes.txt";
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getHelpFileName()
     */
    @Override
    public String getHelpFileName() {
        return "doc/readme.html";
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getLicenceFileName()
     */
    @Override
    public String getLicenceFileName() {
        return "licence.txt";
    }

    /**
     * Load this plugin.
     * 
     * @param app Application API.
     */
    @Override
    public synchronized void load(final IApplication app) throws PluginException {
        super.load(app);

        _resources = _resourcesFactory.createResource(getClass().getName(), this);
    }

    /**
     * Initialize this plugin.
     */
    @Override
    public synchronized void initialize() throws PluginException {
        super.initialize();

        final IApplication app = getApplication();
        final ActionCollection col = getApplication().getActionCollection();

        col.add(new VacuumTableAction(app, _resources));
        col.add(new VacuumDatabaseAction(app, _resources));

        final JMenu sessionMenu = createSessionMenu(col);
        app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, sessionMenu);
        super.registerSessionMenu(sessionMenu);

        CellComponentFactory.registerDataTypeFactory(new PostgreSqlGeometryTypeDataTypeComponentFactory(app.getSessionManager()));
        CellComponentFactory.registerDataTypeFactory(new PostgreSqlUUIDTypeDataTypeComponentFactory());
        CellComponentFactory.registerDataTypeFactory(new PostgreSqlArrayTypeDataTypeComponentFactory());
        CellComponentFactory.registerDataTypeFactory(new PostgreSqlXmlTypeDataTypeComponentFactory());
        CellComponentFactory.registerDataTypeFactory(new PostgreSqlOtherTypeDataTypeComponentFactory("interval"));

		SQLDatabaseMetaDataFactory.registerOverride(DialectType.POSTGRES, this);
    }

    @Override
    public boolean allowsSessionStartedInBackground() {
        return true;
    }

    /**
     * Session has been started. Update the tree api in using the event thread
     * 
     * @param session Session that has started.
     * @return <TT>true</TT> if session is Oracle in which case this plugin is interested in it.
     */
    @Override
    public PluginSessionCallback sessionStarted(final ISession session) {
        if (!isPluginSession(session)) {
            return null;
        }

        GUIUtils.processOnSwingEventThread(new Runnable() {

            @Override
            public void run() {
                updateTreeApi(session);
            }
        });

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                session.getSQLPanelAPIOfActiveSessionWindow().addExecutor(new ExplainExecuterPanel(session));
            }
        });

        return new PluginSessionCallback() {

            @Override
            public void sqlInternalFrameOpened(final SQLInternalFrame sqlInternalFrame, final ISession sess) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        sqlInternalFrame.getSQLPanelAPI().addExecutor(new ExplainExecuterPanel(sess));
                    }
                });
            }

            @Override
            public void objectTreeInternalFrameOpened(final ObjectTreeInternalFrame objectTreeInternalFrame,
                final ISession sess) {
                // Plugin supports only the main session window
            }
        };
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin#isPluginSession(net.sourceforge.squirrel_sql.client.session.ISession)
     */
    @Override
    protected boolean isPluginSession(final ISession session) {
        return DialectFactory.isPostgreSQL(session.getMetaData());
    }

    /**
     * @param session the current session whose tree API should be updated
     */
    private void updateTreeApi(final ISession session) {
        final IObjectTreeAPI _treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();
        final String stmtSep = session.getQueryTokenizer().getSQLStatementSeparator();
        final ActionCollection col = getApplication().getActionCollection();

        // ////// Object Tree Expanders ////////
        // Schema Expanders - sequence
        _treeAPI.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander(new PostgresSequenceInodeExpanderFactory(),
            DatabaseObjectType.SEQUENCE));

        // Table Expanders - trigger and index
        // expander
        final TableWithChildNodesExpander tableExpander = new TableWithChildNodesExpander();

        // extractors
        final ITableIndexExtractor indexExtractor = new PostgresTableIndexExtractorImpl();
        final ITableTriggerExtractor triggerExtractor = new PostgresTableTriggerExtractorImpl();

        tableExpander.setTableTriggerExtractor(triggerExtractor);
        tableExpander.setTableIndexExtractor(indexExtractor);

        _treeAPI.addExpander(DatabaseObjectType.TABLE, tableExpander);

        // ////// Detail Tabs ////////
        // Procedure tab
        _treeAPI.addDetailTab(DatabaseObjectType.PROCEDURE, new ProcedureSourceTab(i18n.SHOW_PROCEDURE_SOURCE));

        // View Tab
        _treeAPI.addDetailTab(DatabaseObjectType.VIEW, new ViewSourceTab(i18n.SHOW_VIEW_SOURCE, stmtSep));

        // Index tab
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexDetailsTab());
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexSourceTab(i18n.SHOW_INDEX_SOURCE, stmtSep));

        // Trigger tabs
        _treeAPI.addDetailTab(IObjectTypes.TRIGGER_PARENT, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerSourceTab("The source of the trigger"));

        // Sequence tabs
        _treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new SequenceDetailsTab());

        // Lock tab
        _treeAPI.addDetailTab(DatabaseObjectType.SESSION, new LockTab());
        
        // Active connections
        _treeAPI.addDetailTab(DatabaseObjectType.SESSION, new ActiveConnections());

        // ////// Popup Menus ////////
        final JMenu tableMenu = _resources.createMenu(IMenuResourceKeys.POSTGRES);
        _resources.addToMenu(col.get(VacuumTableAction.class), tableMenu);
        _treeAPI.addToPopup(DatabaseObjectType.TABLE, tableMenu);

        _treeAPI.addToPopup(DatabaseObjectType.SESSION, createSessionMenu(col));
    }

    /**
     * Creates the postgres session menu from the actions in the specified collection
     * 
     * @param col the ActionCollection to pull postgres actions from
     * @return the JMenu to add to the session menu
     */
    private JMenu createSessionMenu(final ActionCollection col) {
        final JMenu sessionMenu = _resources.createMenu(IMenuResourceKeys.POSTGRES);
        _resources.addToMenu(col.get(VacuumDatabaseAction.class), sessionMenu);
        return sessionMenu;
    }

   @Override
   public SQLDatabaseMetaData fetchMeta(final ISQLConnection conn)
   {
      return new SQLDatabaseMetaData(conn)
      {

         @Override
         public synchronized String[] getDataTypesSimpleNames() throws SQLException
         {
            String sql = "SELECT t.typname FROM pg_catalog.pg_type t" + " JOIN pg_catalog.pg_namespace n ON (t.typnamespace = n.oid) " + " WHERE n.nspname != 'pg_toast' " + " AND typelem = 0 AND typrelid = 0";

            List<String> retn = new ArrayList<String>();
            ResultSet rs = conn.createStatement().executeQuery(sql);
            try
            {
               while (rs.next())
               {
                  retn.add(rs.getString(1));
               }
            }
            finally
            {
               SQLUtilities.closeResultSet(rs);
            }


            return retn.toArray(new String[retn.size()]);
         }

      };
   }
}
