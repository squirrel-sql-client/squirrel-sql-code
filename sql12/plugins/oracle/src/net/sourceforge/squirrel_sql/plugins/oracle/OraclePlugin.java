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
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.IAliasPropertiesPanelController;
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
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeProps;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeTimestamp;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeTimestampStatics;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IObjectTypes;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaDataFactory;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaDataFactory;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLSchemaUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.oracle.SGAtrace.NewSGATraceWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.dboutput.NewDBOutputWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.exception.OracleExceptionFormatter;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.ConstraintParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.DefaultDatabaseExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.InstanceParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.OracleTableParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.PackageExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.ProcedureExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.SchemaExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.SessionParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.TableExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.TriggerParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.UserParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.explainplan.ExplainPlanExecutor;
import net.sourceforge.squirrel_sql.plugins.oracle.invalidobjects.NewInvalidObjectsWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.prefs.OraclePluginPreferencesPanel;
import net.sourceforge.squirrel_sql.plugins.oracle.prefs.OraclePreferenceBean;
import net.sourceforge.squirrel_sql.plugins.oracle.sessioninfo.NewSessionInfoWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.sqlloader.control.GenerateControlFileAction;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.ConstraintColumnInfoTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.ConstraintDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.ConstraintSourceTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.IndexColumnInfoTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.IndexDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.IndexSourceTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.InstanceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.LobDetailsTab;
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
import net.sourceforge.squirrel_sql.plugins.oracle.types.OracleXmlTypeDataTypeComponentFactory;

import javax.swing.SwingUtilities;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Oracle plugin class.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class OraclePlugin extends DefaultSessionPlugin implements ISQLDatabaseMetaDataFactory {
	/**
	 * Logger for this class.
	 */
	private final static ILogger s_log = LoggerController.createLogger(OraclePlugin.class);

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(OraclePlugin.class);

	private OraclePluginResources _resources;

	/**
	 * A list of Oracle sessions that are open so we'll know when none are left
	 */
	private final ArrayList<ISession> oracleSessions = new ArrayList<ISession>();

	/**
	 * SQL to find schemas to which the logged in user has access
	 */
	//private static String SCHEMA_ACCESS_SQL = "SELECT DISTINCT OWNER FROM ALL_OBJECTS";
   private static String SCHEMA_ACCESS_SQL = "SELECT owner FROM ALL_OBJECTS group by owner";


	/**
	 * SQL to determine whether or not this account is a DBA account
	 */
	private static String DBA_ROLE_SQL = "SELECT GRANTED_ROLE FROM USER_ROLE_PRIVS";

	private static final String ORACLE_ALIAS_PREFS_FILE = "oracleAliasPrefs.xml";

	private Hashtable<IIdentifier, OracleAliasPrefs> _oracleAliasPrefsByAliasIdentifier = new Hashtable<IIdentifier, OracleAliasPrefs>();

	/** manages our query tokenizing preferences */
	private PluginQueryTokenizerPreferencesManager _prefsManager = null;

	public static final String BUNDLE_BASE_NAME = "net.sourceforge.squirrel_sql.plugins.oracle.oracle";
   private IObjectTypes _objectTypes;

	@Override
	public SQLDatabaseMetaData fetchMeta(final ISQLConnection conn)
	{
		return new SQLDatabaseMetaData(conn)
		{
			@Override
			public String getOptionalPseudoColumnForDataSelection(final ITableInfo ti)
			{
				return "ROWID";
			}
		};
	}

	interface i18n
	{
		// i18n[OraclePlugin.title=Oracle]
		String title = s_stringMgr.getString("OraclePlugin.title");

		// i18n[OraclePlugin.hint=Preferences for Oracle]
		String hint = s_stringMgr.getString("OraclePlugin.hint");

		// i18n[OraclePlugin.timestampWarning=The setting to use string literals
		// for timestamps may result in the inability to edit tables containing
		// these columns. If this problem occurs, open
		// Global Preferences -> Data Type Controls and set Timestamps to use
		// "JDBC standard escape format"]
		String timestampWarning = s_stringMgr.getString("OraclePlugin.timestampWarning");
	}

	/**
	 * Return the internal name of this plugin.
	 * 
	 * @return the internal name of this plugin.
	 */
	@Override
	public String getInternalName()
	{
		return "oracle";
	}

	/**
	 * Return the descriptive name of this plugin.
	 * 
	 * @return the descriptive name of this plugin.
	 */
	@Override
	public String getDescriptiveName()
	{
		return "Oracle Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 * 
	 * @return the current version of this plugin.
	 */
	@Override
	public String getVersion()
	{
		return "0.21";
	}

	/**
	 * Returns the authors name.
	 * 
	 * @return the authors name.
	 */
	@Override
	public String getAuthor()
	{
		return "Colin Bell";
	}

	/**
	 * Returns a comma separated list of other contributors.
	 * 
	 * @return Contributors names.
	 */
	@Override
	public String getContributors()
	{
		return "Alexander Buloichik, Rob Manning";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getChangeLogFileName()
	 */
	@Override
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getHelpFileName()
	 */
	@Override
	public String getHelpFileName()
	{
		return "doc/readme.html";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getLicenceFileName()
	 */
	@Override
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	/**
	 * Create panel for the Global Properties dialog.
	 * 
	 * @return properties panel.
	 */
	@Override
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		PluginQueryTokenizerPreferencesPanel _prefsPanel = new OraclePluginPreferencesPanel(_prefsManager);

		PluginGlobalPreferencesTab tab = new PluginGlobalPreferencesTab(_prefsPanel);

		tab.setHint(i18n.hint);
		tab.setTitle(i18n.title);

		return new IGlobalPreferencesPanel[]{tab};
	}

	@Override
	public void initialize() throws PluginException
	{
		try
		{
			super.initialize();

			final IApplication app = getApplication();

			_resources = new OraclePluginResources(OraclePlugin.BUNDLE_BASE_NAME, this);
         _objectTypes = new ObjectTypes(_resources);

         ActionCollection coll = app.getActionCollection();
			coll.add(new NewDBOutputWorksheetAction(app, _resources));
			coll.add(new NewInvalidObjectsWorksheetAction(app, _resources));
			coll.add(new NewSessionInfoWorksheetAction(app, _resources));
			coll.add(new NewSGATraceWorksheetAction(app, _resources));

			app.getSessionManager().addAllowedSchemaChecker((con, alias) -> onGetAllowedSchemas(con, alias));

			File f = getGlobalPrefsFile();

			if (f.exists())
			{
				XMLBeanReader xbr = new XMLBeanReader();
				xbr.load(f, getClass().getClassLoader());

				for (Iterator<Object> i = xbr.iterator(); i.hasNext();)
				{
					OracleAliasPrefs buf = (OracleAliasPrefs) i.next();
					_oracleAliasPrefsByAliasIdentifier.put(buf.getAliasIdentifier(), buf);
				}

			}
         else
			{
				_oracleAliasPrefsByAliasIdentifier = new Hashtable<IIdentifier, OracleAliasPrefs>();
			}
			_prefsManager = new PluginQueryTokenizerPreferencesManager();
			_prefsManager.initialize(this, new OraclePreferenceBean());

			/* Register custom DataTypeComponent factory for Oracles XMLType */
         Main.getApplication().getDataTypeComponentFactoryRegistry().registerDataTypeFactory(new OracleXmlTypeDataTypeComponentFactory());

         SQLDatabaseMetaDataFactory.registerOverride(DialectType.ORACLE, this);
		}
      catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void load(IApplication app) throws PluginException
	{
		super.load(app);
	}

	private File getGlobalPrefsFile() throws IOException
	{
		return new File(getPluginUserSettingsFolder().getPath() + File.separator + ORACLE_ALIAS_PREFS_FILE);
	}

	@Override
	public void unload()
	{
		try
		{
			File f = getGlobalPrefsFile();

			XMLBeanWriter xbw = new XMLBeanWriter();

			Collection<OracleAliasPrefs> set = _oracleAliasPrefsByAliasIdentifier.values();
			if (set.size() > 0)
			{
				xbw.addIteratorToRoot(set.iterator());
			}

			xbw.save(f);
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}

	}

	/**
	 * Create Alias prefs panel.
	 */
	@Override
	public IAliasPropertiesPanelController[] getAliasPropertiesPanelControllers(SQLAlias alias)
	{
		if (false == isOracle(alias))
		{
			return new IAliasPropertiesPanelController[0];
		}

		OracleAliasPrefs aliasPrefs = _oracleAliasPrefsByAliasIdentifier.get(alias.getIdentifier());

		if (null == aliasPrefs)
		{
			aliasPrefs = new OracleAliasPrefs();
			aliasPrefs.setAliasIdentifier(alias.getIdentifier());
			_oracleAliasPrefsByAliasIdentifier.put(alias.getIdentifier(), aliasPrefs);
		}

		return new OracleAliasPrefsPanelController[]
			{ new OracleAliasPrefsPanelController(aliasPrefs) };
	}

	@Override
	public void aliasCopied(SQLAlias source, SQLAlias target)
	{
		if (false == isOracle(source) || false == isOracle(target))
		{
			return;
		}

		OracleAliasPrefs sourcePrefs = _oracleAliasPrefsByAliasIdentifier.get(source.getIdentifier());

		if (null != sourcePrefs)
		{
			OracleAliasPrefs targetPrefs = Utilities.cloneObject(sourcePrefs, getClass().getClassLoader());
			targetPrefs.setAliasIdentifier(target.getIdentifier());
			_oracleAliasPrefsByAliasIdentifier.put(targetPrefs.getAliasIdentifier(), targetPrefs);
		}
	}

	@Override
	public void aliasRemoved(SQLAlias alias)
	{
		_oracleAliasPrefsByAliasIdentifier.remove(alias.getIdentifier());
	}

	/**
	 * Called when a session shutdown.
	 * 
	 * @param session
	 *        The session that is ending.
	 */
	@Override
	public void sessionEnding(ISession session)
	{
		super.sessionEnding(session);
		oracleSessions.remove(session);
	}

	@Override
	public PluginSessionCallback sessionStarted(final ISession session)
	{
		if (!isOracle(session))
		{
			return null;
		}
		OraclePreferenceBean prefs = (OraclePreferenceBean) _prefsManager.getPreferences();
		if (prefs.isInstallCustomQueryTokenizer())
		{
			session.setQueryTokenizer(new OracleQueryTokenizer(prefs));
		}

		if (prefs.isShowErrorOffset())
		{
			OracleExceptionFormatter formatter = new OracleExceptionFormatter();
			formatter.setSession(session);
			session.setExceptionFormatter(formatter);
		}

		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			@Override
			public void run()
			{
				addActions(session);
			}
		});

		oracleSessions.add(session);

		checkTimestampSetting(session);

		PluginSessionCallback ret = new PluginSessionCallback()
		{
			@Override
			public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
			{
				onSQLInternaFrameOpened(sqlInternalFrame, sess);
			}

			@Override
			public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame,ISession sess)
			{
				updateObjectTree(objectTreeInternalFrame.getObjectTreeAPI());
			}

			@Override
			public void objectTreeInSQLTabOpened(ObjectTreePanel objectTreePanel)
			{
				updateObjectTree(objectTreePanel);
			}

			@Override
         public void additionalSQLTabOpened(AdditionalSQLTab additionalSQLTab)
         {
            onAdditionalSQLTabOpened(additionalSQLTab);
         }

      };

		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				ISQLPanelAPI sqlPaneAPI = session.getSessionPanel().getMainSQLPaneAPI();
				initSQLPanel(session, sqlPaneAPI);
				updateObjectTree(session.getSessionPanel().getObjectTreePanel());
			}
		});

		setTimezoneForSession(session, prefs);

		return ret;
	}

	private void setTimezoneForSession(ISession session, OraclePreferenceBean prefs)
	{
		if (!prefs.getInitSessionTimezone())
		{
			if (s_log.isInfoEnabled())
			{
				s_log.info("setTimezoneForSession: user preference for init session timezone is disabled.  "
				      + "Local Timezone data types may not be displayed correctly.");
			}
			return;
		}

		Connection con = session.getSQLConnection().getConnection();
		String timezoneStr = prefs.getSessionTimezone();
      try
      {
         if (s_log.isInfoEnabled())
         {
            s_log.info("setTimezoneForSession: attempting to set the session timezone to : " + timezoneStr);
         }

         Method setSessionTimeZoneMethod = con.getClass().getMethod("setSessionTimeZone", String.class);
         if (setSessionTimeZoneMethod != null)
         {
            setSessionTimeZoneMethod.setAccessible(true);
            setSessionTimeZoneMethod.invoke(con, timezoneStr);
         }
         else
         {
            s_log.error("setTimezoneForSession: setSessionTimeZoneMethod returned by reflection was null.  "
               + "Skipped setting session timezone");
         }
      }
      catch (Exception e)
      {
         s_log.error("Unexpected exception while trying to set session timezone: " + e.getMessage(), e);
      }
   }

	/**
	 * This will check the setting for using timestamps in where clauses and display a warning message to the
	 * user if string literal - which is known not to work correctly in Oracle - is set to be used.
	 */
	private void checkTimestampSetting(ISession session)
	{

		String tsClassName = DataTypeTimestamp.class.getName();
		String timeStampWhereClauseUsage = DataTypeProps.getProperty(tsClassName, DataTypeTimestamp.WHERE_CLAUSE_USAGE_KEY);

		if (timeStampWhereClauseUsage != null)
		{
			int timeStampWhereClauseUsageInt = Integer.parseInt(timeStampWhereClauseUsage);
			if (DataTypeTimestampStatics.USE_STRING_FORMAT == timeStampWhereClauseUsageInt)
			{
				session.showWarningMessage(i18n.timestampWarning);
				s_log.warn(i18n.timestampWarning);
			}
		}

	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin#
	 * 			isPluginSession(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	@Override
	protected boolean isPluginSession(ISession session)
	{
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

		ISQLPanelAPI sqlPanelAPI = session.getSessionInternalFrame().getMainSQLPanelAPI();

		sqlPanelAPI.addToToolsPopUp("oracleoutput", coll.get(NewDBOutputWorksheetAction.class));
		sqlPanelAPI.addToToolsPopUp("oracleinvalid", coll.get(NewInvalidObjectsWorksheetAction.class));
		sqlPanelAPI.addToToolsPopUp("oracleinfo", coll.get(NewSessionInfoWorksheetAction.class));
		sqlPanelAPI.addToToolsPopUp("oraclesga", coll.get(NewSGATraceWorksheetAction.class));
	}

	private void onSQLInternaFrameOpened(SQLInternalFrame sqlInternalFrame, final ISession session)
	{
		final ISQLPanelAPI panel = sqlInternalFrame.getMainSQLPanelAPI();
		initSQLPanel(session, panel);
	}


	private void onAdditionalSQLTabOpened(AdditionalSQLTab additionalSQLTab)
	{
		final ISQLPanelAPI panel = additionalSQLTab.getSQLPanelAPI();
		initSQLPanel(additionalSQLTab.getSession(), panel);
	}

	private void initSQLPanel(ISession session, ISQLPanelAPI sqlPanelAPI)
	{
		sqlPanelAPI.addExecutor(new ExplainPlanExecutor(session, sqlPanelAPI));
	}


	/**
	 * Return a node expander for the object tree for a particular default node type. <p/> A plugin could
	 * return non null here if they wish to override the default node expander bahaviour. Most plugins should
	 * return null here.
	 */
	@Override
	public INodeExpander getDefaultNodeExpander(ISession session, DatabaseObjectType type)
	{
		boolean isOracle = isOracle(session.getAlias());
		boolean isOracleWithFlashBack = isOracleWithFlashBack(session);
		if ((type == DatabaseObjectType.PROC_TYPE_DBO) && isOracle)
		{
			return new ProcedureExpander();
		}
		if (type == DatabaseObjectType.DATABASE_TYPE_DBO && isOracle)
		{
			return new DefaultDatabaseExpander(session, _objectTypes);
		}
		if (type == DatabaseObjectType.TABLE_TYPE_DBO && isOracleWithFlashBack)
		{
			OraclePreferenceBean prefs = (OraclePreferenceBean) _prefsManager.getPreferences();
			return new OracleTableParentExpander(prefs);
		}
		return null;
	}

	private boolean isOracleWithFlashBack(ISession session)
	{
		boolean result = false;
		if (DialectFactory.isOracle(session.getMetaData()))
		{
			// Not all Oracle's, just 10g and above.
			try
			{
				int version = session.getMetaData().getDatabaseMajorVersion();
				if (version >= 10)
				{
					result = true;
				}
			} catch (SQLException e)
			{
				s_log.error("Unexpected exception while attempting to get " + "the database version", e);
			}
		}
		return result;
	}

	private boolean isOracle(ISession session)
	{
		boolean result = false;
		if (DialectFactory.isOracle(session.getMetaData()))
		{
			result = true;
		}
		return result;
	}

	private boolean isOracle(SQLAlias alias)
	{
		IIdentifier driverIdentifier = alias.getDriverIdentifier();
		Driver jdbcDriver = getApplication().getSQLDriverManager().getJDBCDriver(driverIdentifier);

		if (null == jdbcDriver)
		{
			return false;
		}

		return jdbcDriver.getClass().getName().startsWith("oracle.");
	}

	private String[] onGetAllowedSchemas(ISQLConnection con, SQLAlias alias)
	{
		if (isOracle(alias))
		{
			OracleAliasPrefs prefs = _oracleAliasPrefsByAliasIdentifier.get(alias.getIdentifier());

			if (null == prefs)
			{
				prefs = new OracleAliasPrefs();
				prefs.setAliasIdentifier(alias.getIdentifier());
				_oracleAliasPrefsByAliasIdentifier.put(prefs.getAliasIdentifier(), prefs);
			}

			return getAccessibleSchemas(prefs, con);
		} else
		{
			return null;
		}
	}

	/**
	 * Returns an array of schema names that represent schemas in which there exist tables that the user
	 * associated with the specified session has privilege to access.
	 * 
	 * @param session
	 *        the session to retrieve schemas for
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
				result = SQLSchemaUtil.toSchemaNameArray(md.getSchemas());
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

				if (aliasPrefs.isLoadAccessibleSchemasAndSYS())
				{
					tmp.add("SYS");
				}

				result = tmp.toArray(new String[tmp.size()]);
			}
		} catch (SQLException e)
		{
			// i18n[DefaultDatabaseExpander.error.retrieveschemaprivs=Unable to retrieve schema privileges]
			String msg = s_stringMgr.getString("DefaultDatabaseExpander.error.retrieveschemaprivs");
			s_log.error(msg, e);
		} finally
		{
			SQLUtilities.closeResultSet(rs, true);
		}
		return result;
	}

	/**
	 * Checks whether or not the user associated with the specified session has been granted the DBA privilege.
	 * 
	 * @param session
	 *        the session to check
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
		} catch (SQLException e)
		{
			// i18n[DefaultDatabaseExpander.error.retrieveuserroles=Unable to retrieve user roles]
			String msg = s_stringMgr.getString("DefaultDatabaseExpander.error.retrieveuserroles");
			s_log.error(msg, e);
		} finally
		{
			SQLUtilities.closeResultSet(rs, true);
		}
		return result;
	}

	/**
	 * Check if we can run query.
	 * 
	 * @param session
	 *        session
	 * @param query
	 *        query text
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
		} catch (SQLException ex)
		{
			return false;
		} finally
		{
			SQLUtilities.closeResultSet(rs, true);
		}
	}

	private void updateObjectTree(final IObjectTreeAPI objTree)
	{
		ISession session = objTree.getSession();
		addDetailTab(objTree, DatabaseObjectType.SESSION, new OptionsTab());
		addDetailTab(objTree, _objectTypes.getConsumerGroup(), new DatabaseObjectInfoTab());
		addDetailTab(objTree, DatabaseObjectType.FUNCTION, new DatabaseObjectInfoTab());
		addDetailTab(objTree, DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
		addDetailTab(objTree, DatabaseObjectType.INDEX, new IndexColumnInfoTab());
		addDetailTab(objTree, DatabaseObjectType.INDEX, new IndexDetailsTab());
		addDetailTab(objTree, DatabaseObjectType.INDEX, new IndexSourceTab());
		addDetailTab(objTree, _objectTypes.getLob(), new DatabaseObjectInfoTab());
		addDetailTab(objTree, _objectTypes.getLob(), new LobDetailsTab());
		addDetailTab(objTree, DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
		addDetailTab(objTree, DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
		addDetailTab(objTree, _objectTypes.getTriggerParent(), new DatabaseObjectInfoTab());
		addDetailTab(objTree, _objectTypes.getType(), new DatabaseObjectInfoTab());
		addDetailTab(objTree, _objectTypes.getConstraint(), new DatabaseObjectInfoTab());
		
		// Expanders.
		addExpander(objTree, DatabaseObjectType.SCHEMA, new SchemaExpander(_objectTypes));
		addExpander(objTree, DatabaseObjectType.TABLE, new TableExpander(_objectTypes));
		addExpander(objTree, _objectTypes.getPackage(), new PackageExpander());
		addExpander(objTree, _objectTypes.getUserParent(), new UserParentExpander(session));
		addExpander(objTree, _objectTypes.getSessionParent(), new SessionParentExpander(_objectTypes));
		addExpander(objTree, _objectTypes.getInstanceParent(), new InstanceParentExpander(_objectTypes));
		addExpander(objTree, _objectTypes.getTriggerParent(), new TriggerParentExpander());
		addExpander(objTree, _objectTypes.getConstraintParent(), new ConstraintParentExpander(_objectTypes));

		addDetailTab(objTree, DatabaseObjectType.PROCEDURE, new ObjectSourceTab(
		   "PROCEDURE", "Show stored procedure source"));
		addDetailTab(objTree, DatabaseObjectType.FUNCTION, new ObjectSourceTab(
		   "FUNCTION", "Show function source"));
		addDetailTab(objTree, _objectTypes.getPackage(), new ObjectSourceTab(
		   "PACKAGE", "Specification", "Show package specification"));
		addDetailTab(objTree, _objectTypes.getPackage(), new ObjectSourceTab(
		   "PACKAGE BODY", "Body", "Show package body"));
		addDetailTab(objTree, _objectTypes.getType(), new ObjectSourceTab(
		   "TYPE", "Specification", "Show type specification"));
		
		addDetailTab(objTree, _objectTypes.getType(), new ObjectSourceTab("TYPE BODY", "Body", "Show type body"));
		addDetailTab(objTree, _objectTypes.getInstance(), new InstanceDetailsTab());
		addDetailTab(objTree, DatabaseObjectType.SEQUENCE, new SequenceDetailsTab());
		addDetailTab(objTree, _objectTypes.getSession(), new SessionDetailsTab());
		addDetailTab(objTree, _objectTypes.getSession(), new SessionStatisticsTab());
		addDetailTab(objTree, DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
		addDetailTab(objTree, DatabaseObjectType.TRIGGER, new TriggerSourceTab());
		addDetailTab(objTree, DatabaseObjectType.TRIGGER, new TriggerColumnInfoTab());
		addDetailTab(objTree, DatabaseObjectType.USER, new UserDetailsTab(session));
		addDetailTab(objTree, _objectTypes.getConstraint(), new ConstraintDetailsTab());
		addDetailTab(objTree, _objectTypes.getConstraint(), new ConstraintColumnInfoTab());
		addDetailTab(objTree, _objectTypes.getConstraint(), new ConstraintSourceTab());
		
		addDetailTab(objTree, DatabaseObjectType.VIEW, new ViewSourceTab());
		addDetailTab(objTree, DatabaseObjectType.TABLE, new SnapshotSourceTab());
		
		/* Adds a popup menu for SQL*Loader control files generation */
		session.getSessionInternalFrame().getObjectTreeAPI().addToPopup(
				DatabaseObjectType.TABLE, new GenerateControlFileAction(getApplication(), _resources, session));


		// This fixes the issue where the tree is getting constructed prior to
		// the Oracle plugin expanders being registered.(USERS, SESSIONS,
		// INSTANCES nodes have no children until the tree is refreshed). Even
		// though this is a hack, it doesn't seem to negatively impact
		// performance even when loading all schemas.
		GUIUtils.processOnSwingEventThread(() -> objTree.refreshTree());
	}

	private void addExpander(final IObjectTreeAPI objTree, final DatabaseObjectType dboType, final INodeExpander exp)
	{
		objTree.addExpander(dboType, exp);
	}

	private void addDetailTab(final IObjectTreeAPI objTree, final DatabaseObjectType dboType, final IObjectTab tab)
	{
		objTree.addDetailTab(dboType, tab);
	}
}
