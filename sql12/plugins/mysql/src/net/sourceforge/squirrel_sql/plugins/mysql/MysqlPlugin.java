package net.sourceforge.squirrel_sql.plugins.mysql;
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
import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginGlobalPreferencesTab;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableWithTriggersExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.mysql.action.AlterTableAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.AnalyzeTableAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.CheckTableAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.CopyTableAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.CreateDatabaseAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.CreateMysqlTableScriptAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.DropDatabaseAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.ExplainSelectTableAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.ExplainTableAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.OptimizeTableAction;
import net.sourceforge.squirrel_sql.plugins.mysql.action.RenameTableAction;
import net.sourceforge.squirrel_sql.plugins.mysql.expander.MysqlTableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.mysql.expander.SessionExpander;
import net.sourceforge.squirrel_sql.plugins.mysql.expander.UserParentExpander;
import net.sourceforge.squirrel_sql.plugins.mysql.prefs.MysqlPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.DatabaseStatusTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.MysqlProcedureSourceTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.MysqlTriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.MysqlTriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.MysqlViewSourceTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.OpenTablesTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.ProcessesTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.ShowColumnsTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.ShowIndexesTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.ShowLogsTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.ShowMasterLogsTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.ShowMasterStatusTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.ShowSlaveStatusTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.ShowVariablesTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.TableStatusTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tab.UserGrantsTab;
import net.sourceforge.squirrel_sql.plugins.mysql.tokenizer.MysqlQueryTokenizer;
/**
 * MySQL plugin class.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MysqlPlugin extends DefaultSessionPlugin
{
    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(MysqlPlugin.class);
               
	/** Logger for this class. */
    @SuppressWarnings("unused")
	private final static ILogger s_log = 
        LoggerController.createLogger(MysqlPlugin.class);

	/** Plugin resources. */
	private PluginResources _resources;

	/** API for the Obejct Tree. */
	private IObjectTreeAPI _treeAPI;

	/** MySQL menu. */
	private JMenu _mySQLMenu;

	/** manages our query tokenizing preferences */
	private PluginQueryTokenizerPreferencesManager _prefsManager = null;

    
	interface i18n {
	    // i18n[MysqlPlugin.title=MySQL]
	    String title = s_stringMgr.getString("MysqlPlugin.title");

	    // i18n[MysqlPlugin.hint=Preferences for MySQL]
	    String hint = s_stringMgr.getString("MysqlPlugin.hint");
        
        //i18n[MysqlPlugin.showProcedureSource=Show procedure source]
        String SHOW_PROCEDURE_SOURCE =
            s_stringMgr.getString("MysqlPlugin.showProcedureSource");

        //i18n[MysqlPlugin.showTriggerSource=Show trigger source]
        String SHOW_TRIGGER_SOURCE =
            s_stringMgr.getString("MysqlPlugin.showTriggerSource");
        
        //i18n[MysqlPlugin.showViewSource=Show view source]
        String SHOW_VIEW_SOURCE = 
            s_stringMgr.getString("MysqlPlugin.showViewSource");
	}
    
	/**
	 * Return the internal name of this plugin.
	 *
	 * @return	the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "mysql";
	}

	/**
	 * Return the descriptive name of this plugin.
	 *
	 * @return	the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "MySQL Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return	the current version of this plugin.
	 */
	public String getVersion()
	{
		return "0.33";
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
	 * Load this plugin.
	 *
	 * @param	app	 Application API.
	 */
	public synchronized void load(IApplication app) throws PluginException
	{
		super.load(app);

		// Folder to store user settings.
//		try
//		{
//			_userSettingsFolder = getPluginUserSettingsFolder();
//		}
//		catch (IOException ex)
//		{
//			throw new PluginException(ex);
//		}

		_resources = new MysqlResources(getClass().getName(), this);
	}

	/**
	 * Retrieve the name of the change log.
	 *
	 * @return	The name of the change log.
	 */
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	/**
	 * Retrieve the name of the help file.
	 *
	 * @return	The nane of the help file.
	 */
	public String getHelpFileName()
	{
		return "readme.html";
	}

	/**
	 * Retrieve the name of the licence file.
	 *
	 * @return	The nane of the licence file.
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
	    PluginQueryTokenizerPreferencesPanel _prefsPanel = 
	        new PluginQueryTokenizerPreferencesPanel(_prefsManager,
	                _prefsManager.getPreferences(), "MySQL");

	    PluginGlobalPreferencesTab tab = new PluginGlobalPreferencesTab(_prefsPanel);

	    tab.setHint(i18n.hint);
	    tab.setTitle(i18n.title);

	    return new IGlobalPreferencesPanel[] { tab };
	}
    
    
	/**
	 * Initialize this plugin.
	 */
	public synchronized void initialize() throws PluginException
	{
		super.initialize();

		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

		coll.add(new AnalyzeTableAction(app, _resources, this));
		coll.add(new CreateMysqlTableScriptAction(app, _resources, this));
		coll.add(new CheckTableAction.ChangedCheckTableAction(app, _resources, this));
		coll.add(new CheckTableAction.ExtendedCheckTableAction(app, _resources, this));
		coll.add(new CheckTableAction.FastCheckTableAction(app, _resources, this));
		coll.add(new CheckTableAction.MediumCheckTableAction(app, _resources, this));
		coll.add(new CheckTableAction.QuickCheckTableAction(app, _resources, this));
		coll.add(new ExplainSelectTableAction(app, _resources, this));
		coll.add(new ExplainTableAction(app, _resources, this));
		coll.add(new OptimizeTableAction(app, _resources, this));
		coll.add(new RenameTableAction(app, _resources, this));

		coll.add(new CreateDatabaseAction(app, _resources, this));
		coll.add(new DropDatabaseAction(app, _resources, this));
		coll.add(new AlterTableAction(app, _resources, this));
//		coll.add(new CreateTableAction(app, _resources, this));
		coll.add(new CopyTableAction(app, _resources, this));

		_mySQLMenu = createFullMysqlMenu();
		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, _mySQLMenu);
        super.registerSessionMenu(_mySQLMenu);   
        

        _prefsManager = new PluginQueryTokenizerPreferencesManager();
        _prefsManager.initialize(this, new MysqlPreferenceBean());
	}

	/**
	 * Application is shutting down so save preferences.
	 */
	public void unload()
	{
		super.unload();
	}

   public boolean allowsSessionStartedInBackground()
   {
      return true;
   }

   /**
    * Session has been started. If this is a MySQL session
    * then setup MySQL tabs etc.
    *
    * @param	session		Session that has started.
    *
    * @return	<TT>true</TT> if session is MySQL in which case this plugin
    * 			is interested in it.
    */
   public PluginSessionCallback sessionStarted(final ISession session)
   {
       if (!isPluginSession(session)) {
           return null;
       }
       
       GUIUtils.processOnSwingEventThread(new Runnable() {
           public void run() {
               updateTreeApi(session);
           }
       });
       
       installMysqlQueryTokenizer(session);
       
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
    
    @Override
    protected boolean isPluginSession(ISession session) {
        return DialectFactory.isMySQL(session.getMetaData());
    }
    
    /**
     * Determines from the user's preference whether or not to install the 
     * custom query tokenizer, and if so configure installs it.
     * 
     * @param session the session to install the custom query tokenizer in.
     */
    private void installMysqlQueryTokenizer(ISession session) {

        IQueryTokenizerPreferenceBean _prefs = _prefsManager.getPreferences();
        
        if (_prefs.isInstallCustomQueryTokenizer()) {
            session.setQueryTokenizer(new MysqlQueryTokenizer(_prefs));
        }
        
    }
    
    private void updateTreeApi(ISession session) {
        _treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();
        final ActionCollection coll = getApplication().getActionCollection();
        

        // Show users in the object tee.
        _treeAPI.addExpander(DatabaseObjectType.SESSION, new SessionExpander());
        _treeAPI.addExpander(IObjectTypes.USER_PARENT, new UserParentExpander(this));

        
        // Tabs to add to the database node.
        _treeAPI.addDetailTab(DatabaseObjectType.SESSION, new DatabaseStatusTab());
        _treeAPI.addDetailTab(DatabaseObjectType.SESSION, new ProcessesTab());
        _treeAPI.addDetailTab(DatabaseObjectType.SESSION, new ShowVariablesTab());
        _treeAPI.addDetailTab(DatabaseObjectType.SESSION, new ShowLogsTab());
        _treeAPI.addDetailTab(DatabaseObjectType.SESSION, new ShowMasterStatusTab());
        _treeAPI.addDetailTab(DatabaseObjectType.SESSION, new ShowMasterLogsTab());
        _treeAPI.addDetailTab(DatabaseObjectType.SESSION, new ShowSlaveStatusTab());

        // Tabs to add to the catalog nodes.
        _treeAPI.addDetailTab(DatabaseObjectType.CATALOG, new OpenTablesTab());
        _treeAPI.addDetailTab(DatabaseObjectType.CATALOG, new TableStatusTab());

        // Tabs to add to the table nodes.
        _treeAPI.addDetailTab(DatabaseObjectType.TABLE, new ShowColumnsTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TABLE, new ShowIndexesTab());

        
        // Tabs to add to the user nodes.
        _treeAPI.addDetailTab(DatabaseObjectType.USER, new UserGrantsTab());

        // Options in popup menu.
        _treeAPI.addToPopup(coll.get(CreateDatabaseAction.class));

//              _treeAPI.addToPopup(DatabaseObjectType.SESSION, coll.get(CreateTableAction.class));
//              _treeAPI.addToPopup(DatabaseObjectType.CATALOG, coll.get(CreateTableAction.class));
        _treeAPI.addToPopup(DatabaseObjectType.CATALOG, coll.get(DropDatabaseAction.class));

        _treeAPI.addToPopup(DatabaseObjectType.TABLE, createMysqlTableMenu());  
        
        updateTreeApiForMysql5(session);
    }
    
    private void updateTreeApiForMysql5(ISession session) {
        if (!DialectFactory.isMySQL5(session.getMetaData())) {
            return;
        }
        String stmtSep = session.getQueryTokenizer().getSQLStatementSeparator();
        
        MysqlProcedureSourceTab procSourceTab =
            new MysqlProcedureSourceTab(i18n.SHOW_PROCEDURE_SOURCE, stmtSep);
        _treeAPI.addDetailTab(DatabaseObjectType.PROCEDURE, procSourceTab);
        
        // Tab to add to view nodes.
        MysqlViewSourceTab viewSourceTab = 
            new MysqlViewSourceTab(i18n.SHOW_VIEW_SOURCE, stmtSep);
        _treeAPI.addDetailTab(DatabaseObjectType.VIEW, viewSourceTab);  
        
        // Show triggers for tables
        TableWithTriggersExpander trigExp = new TableWithTriggersExpander();
        trigExp.setTableTriggerExtractor(new MysqlTableTriggerExtractorImpl());
        _treeAPI.addExpander(DatabaseObjectType.TABLE, trigExp);        
        
        // tabs for triggers
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, 
                              new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER,
                              new MysqlTriggerDetailsTab());
        MysqlTriggerSourceTab trigSourceTab = 
            new MysqlTriggerSourceTab(i18n.SHOW_TRIGGER_SOURCE, stmtSep);
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, trigSourceTab);
        
    }
    
	/**
	 * Create menu containing actions relevant for table nodes in the object
	 * tree.
	 *
	 * @return	The menu object.
	 */
	private JMenu createMysqlTableMenu()
	{
		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

		final JMenu mysqlMenu = _resources.createMenu(MysqlResources.IMenuResourceKeys.MYSQL);

		_resources.addToMenu(coll.get(CreateMysqlTableScriptAction.class), mysqlMenu);

		_resources.addToMenu(coll.get(AnalyzeTableAction.class), mysqlMenu);
		_resources.addToMenu(coll.get(ExplainTableAction.class), mysqlMenu);
		_resources.addToMenu(coll.get(ExplainSelectTableAction.class), mysqlMenu);
		_resources.addToMenu(coll.get(OptimizeTableAction.class), mysqlMenu);

		final JMenu checkTableMenu = _resources.createMenu(MysqlResources.IMenuResourceKeys.CHECK_TABLE);
		_resources.addToMenu(coll.get(CheckTableAction.ChangedCheckTableAction.class), checkTableMenu);
		_resources.addToMenu(coll.get(CheckTableAction.ExtendedCheckTableAction.class), checkTableMenu);
		_resources.addToMenu(coll.get(CheckTableAction.FastCheckTableAction.class), checkTableMenu);
		_resources.addToMenu(coll.get(CheckTableAction.MediumCheckTableAction.class), checkTableMenu);
		_resources.addToMenu(coll.get(CheckTableAction.QuickCheckTableAction.class), checkTableMenu);
		mysqlMenu.add(checkTableMenu);

		_resources.addToMenu(coll.get(AlterTableAction.class), mysqlMenu);
		_resources.addToMenu(coll.get(CopyTableAction.class), mysqlMenu);
		_resources.addToMenu(coll.get(RenameTableAction.class), mysqlMenu);

		return mysqlMenu;
	}

	/**
	 * Create menu containing all MYSQL actions.
	 *
	 * @return	The menu object.
	 */
	private JMenu createFullMysqlMenu()
	{
		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

		final JMenu mysqlMenu = _resources.createMenu(MysqlResources.IMenuResourceKeys.MYSQL);

		_resources.addToMenu(coll.get(CreateDatabaseAction.class), mysqlMenu);
//		_resources.addToMenu(coll.get(DropDatabaseAction.class), mysqlMenu);

		_resources.addToMenu(coll.get(CreateMysqlTableScriptAction.class), mysqlMenu);
//		_resources.addToMenu(coll.get(CreateTableAction.class), mysqlMenu);

		_resources.addToMenu(coll.get(AnalyzeTableAction.class), mysqlMenu);
		_resources.addToMenu(coll.get(ExplainTableAction.class), mysqlMenu);
		_resources.addToMenu(coll.get(ExplainSelectTableAction.class), mysqlMenu);
		_resources.addToMenu(coll.get(OptimizeTableAction.class), mysqlMenu);

		final JMenu checkTableMenu = _resources.createMenu(MysqlResources.IMenuResourceKeys.CHECK_TABLE);
		_resources.addToMenu(coll.get(CheckTableAction.ChangedCheckTableAction.class), checkTableMenu);
		_resources.addToMenu(coll.get(CheckTableAction.ExtendedCheckTableAction.class), checkTableMenu);
		_resources.addToMenu(coll.get(CheckTableAction.FastCheckTableAction.class), checkTableMenu);
		_resources.addToMenu(coll.get(CheckTableAction.MediumCheckTableAction.class), checkTableMenu);
		_resources.addToMenu(coll.get(CheckTableAction.QuickCheckTableAction.class), checkTableMenu);
		mysqlMenu.add(checkTableMenu);

		return mysqlMenu;
	}

}
