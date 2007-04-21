package org.firebirdsql.squirrel;

import java.sql.SQLException;

import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
//import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.FirebirdObjectTreeListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;

import org.firebirdsql.squirrel.act.ActivateIndexAction;
import org.firebirdsql.squirrel.act.DeactivateIndexAction;
import org.firebirdsql.squirrel.exp.DatabaseExpander;
import org.firebirdsql.squirrel.exp.IndexParentExpander;
import org.firebirdsql.squirrel.exp.TableExpander;
import org.firebirdsql.squirrel.tab.DomainDetailsTab;
import org.firebirdsql.squirrel.tab.GeneratorDetailsTab;
import org.firebirdsql.squirrel.tab.IndexInfoTab;
import org.firebirdsql.squirrel.tab.ProcedureSourceTab;
import org.firebirdsql.squirrel.tab.TriggerDetailsTab;
import org.firebirdsql.squirrel.tab.TriggerSourceTab;
import org.firebirdsql.squirrel.tab.ViewSourceTab;

public class FirebirdPlugin extends DefaultSessionPlugin {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(FirebirdPlugin.class);

    /** Logger for this class. */
    private final static ILogger s_log = LoggerController.createLogger(FirebirdPlugin.class);

    /** API for the Obejct Tree. */
    private IObjectTreeAPI _treeAPI;

	/** Plugin resources. */
	private PluginResources _resources;

	/** Firebird menu. */
	private JMenu _firebirdMenu;

    /**
     * Return the internal name of this plugin.
     *
     * @return  the internal name of this plugin.
     */
    public String getInternalName()
    {
        return "firebird";
    }

    /**
     * Return the descriptive name of this plugin.
     *
     * @return  the descriptive name of this plugin.
     */
    public String getDescriptiveName()
    {
        return "Firebird Plugin";
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
        return "Roman Rokytskyy";
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

		_resources = new FirebirdResources(getClass().getName(), this);
	}

	/**
	 * Initialize this plugin.
	 */
	public synchronized void initialize() throws PluginException
	{
		super.initialize();
		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

		coll.add(new ActivateIndexAction(app, _resources, this));
		coll.add(new DeactivateIndexAction(app, _resources, this));

		_firebirdMenu = createFirebirdMenu();
		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, _firebirdMenu);
        super.registerSessionMenu(_firebirdMenu);
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
    * Session has been started. If this is an Oracle session then
    * register an extra expander for the Schema nodes to show
    * Oracle Packages.
    *
    * @param   session     Session that has started.
    *
    * @return  <TT>true</TT> if session is Oracle in which case this plugin
    *                          is interested in it.
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
       return new PluginSessionCallback()
       {
           public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
           {
               // Supports Session main window only
           }

           public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
           {
               // Supports Session main window only
           }
       };
   }

    @Override
    protected boolean isPluginSession(ISession session) {
        return DialectFactory.isFirebird(session.getMetaData());
    }
    
    private void updateTreeApi(ISession session) {
        _treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();

        // Tabs to add to the database node.
        _treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(IObjectTypes.TRIGGER_PARENT, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexInfoTab());

        // Expanders.
        _treeAPI.addExpander(IObjectTypes.INDEX_PARENT, new IndexParentExpander());

        _treeAPI.addExpander(DatabaseObjectType.SESSION, new DatabaseExpander(this));
        _treeAPI.addExpander(DatabaseObjectType.TABLE, new TableExpander(this));
        _treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new GeneratorDetailsTab());
        _treeAPI.addDetailTab(DatabaseObjectType.DATATYPE, new DomainDetailsTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
        // i18n[firebird.showTrigger=Show trigger source]
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, 
                new TriggerSourceTab(s_stringMgr.getString("firebird.showTrigger")));
        // i18n[firebird.showProcedureSource=Show procedure source]
        _treeAPI.addDetailTab(DatabaseObjectType.PROCEDURE, 
                new ProcedureSourceTab(s_stringMgr.getString("firebird.showProcedureSource")));
        // i18n[firebird.showView=Show view source]
        _treeAPI.addDetailTab(DatabaseObjectType.VIEW, 
                new ViewSourceTab(s_stringMgr.getString("firebird.showView")));


        final ActionCollection coll = getApplication().getActionCollection();
        _treeAPI.addToPopup(DatabaseObjectType.INDEX, coll.get(ActivateIndexAction.class));
        _treeAPI.addToPopup(DatabaseObjectType.INDEX, coll.get(DeactivateIndexAction.class));        
    }
    
	/**
	 * Create menu containing all Firebird actions.
	 *
	 * @return	The menu object.
	 */
	private JMenu createFirebirdMenu()
	{
		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

		final JMenu firebirdMenu = _resources.createMenu(FirebirdResources.IMenuResourceKeys.FIREBIRD);

		_resources.addToMenu(coll.get(ActivateIndexAction.class), firebirdMenu);
		_resources.addToMenu(coll.get(DeactivateIndexAction.class), firebirdMenu);

		return firebirdMenu;
	}

}
