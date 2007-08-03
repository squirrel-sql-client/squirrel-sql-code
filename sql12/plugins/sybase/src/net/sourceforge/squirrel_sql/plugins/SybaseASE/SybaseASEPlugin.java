package net.sourceforge.squirrel_sql.plugins.SybaseASE;

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
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableWithChildNodesExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.exp.SybaseTableIndexExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.exp.SybaseTableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.prefs.SybasePreferenceBean;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.tab.ViewSourceTab;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.tokenizer.SybaseQueryTokenizer;

/**
 * The Example plugin class.
 */
public class SybaseASEPlugin extends DefaultSessionPlugin
{
    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(SybaseASEPlugin.class);
               
    /** Logger for this class. */
    @SuppressWarnings("unused")
    private final static ILogger s_log = 
        LoggerController.createLogger(SybaseASEPlugin.class);
                
	private PluginResources _resources;

    /** manages our query tokenizing preferences */
    private PluginQueryTokenizerPreferencesManager _prefsManager = null;
    
    /** The database name that appears in the border label of the pref panel */
    private static final String SCRIPT_SETTINGS_BORDER_LABEL_DBNAME = "Sybase";
        
    static interface i18n {
        // i18n[SybaseASEPlugin.title=SybaseASE]
        String title = s_stringMgr.getString("SybaseASEPlugin.title");

        // i18n[SybaseASEPlugin.hint=Preferences for SybaseASE]
        String hint = s_stringMgr.getString("SybaseASEPlugin.hint");
        
        //i18n[SybaseASEPlugin.showViewSource=Show view source]
        String SHOW_VIEW_SOURCE = 
            s_stringMgr.getString("SybaseASEPlugin.showViewSource");
        
        // i18n[SybaseASEPlugin.triggerHint=Show trigger source]
        String TRIGGER_HINT=s_stringMgr.getString("SybaseASEPlugin.triggerHint");

        
    }
    
    
	/**
	 * Return the internal name of this plugin.
	 *
	 * @return  the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "sybase";
	}

	/**
	 * Return the descriptive name of this plugin.
	 *
	 * @return  the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "SybaseASE Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return  the current version of this plugin.
	 */
	public String getVersion()
	{
		return "0.01";
	}

	/**
	 * Returns the authors name.
	 *
	 * @return  the authors name.
	 */
	public String getAuthor()
	{
		return "Ken McCullough";
	}

	/**
	 * Returns the name of the change log for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the changelog file name or <TT>null</TT> if plugin doesn't have
	 * 			a change log.
	 */
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	/**
	 * Returns the name of the Help file for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the Help file name or <TT>null</TT> if plugin doesn't have
	 * 			a help file.
	 */
	public String getHelpFileName()
	{
		return "readme.txt";
	}

	/**
	 * Returns the name of the Licence file for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the Licence file name or <TT>null</TT> if plugin doesn't have
	 * 			a licence file.
	 */
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	/**
	 * @return	Comma separated list of contributors.
	 */
	public String getContributors()
	{
		return "";
	}

	/**
	 * Create preferences panel for the Global Preferences dialog.
	 *
	 * @return  Preferences panel.
	 */
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
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

        return new IGlobalPreferencesPanel[] { tab };
	}
    
    /**
     * Determines from the user's preference whether or not to install the 
     * custom query tokenizer, and if so configure installs it.
     * 
     * @param session the session to install the custom query tokenizer in.
     */
    private void installSybaseQueryTokenizer(ISession session) {
        IQueryTokenizerPreferenceBean _prefs = _prefsManager.getPreferences();
        
        if (_prefs.isInstallCustomQueryTokenizer()) {
            session.setQueryTokenizer(new SybaseQueryTokenizer(_prefs));
        }
    }
    
	/**
	 * Initialize this plugin.
	 */
	public synchronized void initialize() throws PluginException
	{
		_resources = new PluginResources("net.sourceforge.squirrel_sql.plugins.SybaseASE.SybaseASE", this);
        _prefsManager = new PluginQueryTokenizerPreferencesManager();
        _prefsManager.initialize(this, new SybasePreferenceBean());

	}


	/**
	 * Called when a session started. Add commands to popup menu
	 * in object tree.
	 *
	 * @param   session	 The session that is starting.
	 *
	 * @return An implementation of PluginSessionCallback or null to indicate
	 * the plugin does not work with this session
	 */
	public PluginSessionCallback sessionStarted(ISession session)
	{
	    if (!isPluginSession(session)) {
	        return null;
	    }
        installSybaseQueryTokenizer(session);
        String stmtSep = session.getQueryTokenizer().getSQLStatementSeparator();

	    // Add context menu items to the object tree's view and procedure nodes.
	    IObjectTreeAPI otApi = session.getSessionInternalFrame().getObjectTreeAPI();
	    otApi.addToPopup(DatabaseObjectType.VIEW, new ScriptSybaseASEViewAction(getApplication(), _resources, session));
	    otApi.addToPopup(DatabaseObjectType.PROCEDURE, new ScriptSybaseASEProcedureAction(getApplication(), _resources, session));

        otApi.addDetailTab(DatabaseObjectType.VIEW, 
                new ViewSourceTab(i18n.SHOW_VIEW_SOURCE, stmtSep));
        
        TableWithChildNodesExpander tableExp = new TableWithChildNodesExpander();
        tableExp.setTableIndexExtractor(new SybaseTableIndexExtractorImpl());
        tableExp.setTableTriggerExtractor(new SybaseTableTriggerExtractorImpl());
        otApi.addExpander(DatabaseObjectType.TABLE, tableExp);
        
        otApi.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
        //otApi.addDetailTab(DatabaseObjectType.INDEX, new IndexDetailsTab());        
        otApi.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
        otApi.addDetailTab(DatabaseObjectType.TRIGGER_TYPE_DBO, new DatabaseObjectInfoTab());
        
        otApi.addDetailTab(DatabaseObjectType.TRIGGER, 
                           new TriggerSourceTab(i18n.TRIGGER_HINT, stmtSep));

        
	    return new PluginSessionCallback()
	    {
	        public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
	        {
	            //plugin supports Session main window only
	        }

	        public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
	        {
	            //plugin supports Session main window only
	        }
	    };
	}

    @Override
    protected boolean isPluginSession(ISession session) {
        return DialectFactory.isSyBase(session.getMetaData());
    }
    
}
