package net.sourceforge.squirrel_sql.plugins.SybaseASE;

import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.gui.SybaseGlobalPreferencesTab;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.prefs.PreferencesManager;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.prefs.SybasePreferenceBean;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.tokenizer.SybaseQueryTokenizer;

/**
 * The Example plugin class.
 */
public class SybaseASEPlugin extends DefaultSessionPlugin
{
	private PluginResources _resources;

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
        SybaseGlobalPreferencesTab tab = new SybaseGlobalPreferencesTab();
        return new IGlobalPreferencesPanel[] { tab };
	}
    
	/**
	 * Initialize this plugin.
	 */
	public synchronized void initialize() throws PluginException
	{
		_resources = new PluginResources("net.sourceforge.squirrel_sql.plugins.SybaseASE.SybaseASE", this);
        PreferencesManager.initialize(this);
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
		try
		{
         String driverName = session.getSQLConnection().getConnection().getMetaData().getDriverName();
         if(false == driverName.toUpperCase().startsWith("JCONNECT"))
         {
            // Plugin knows only how to script Views and Stored Procedures on DB2.
            // So if it's not a DB2 Session we tell SQuirreL the Plugin should not be used.
            return null;
         }
         SybasePreferenceBean _prefs = PreferencesManager.getPreferences();
         session.setQueryTokenizer(new SybaseQueryTokenizer(_prefs));
         // Add context menu items to the object tree's view and procedure nodes.
         IObjectTreeAPI otApi = session.getSessionInternalFrame().getObjectTreeAPI();
         otApi.addToPopup(DatabaseObjectType.VIEW, new ScriptSybaseASEViewAction(getApplication(), _resources, session));
         otApi.addToPopup(DatabaseObjectType.PROCEDURE, new ScriptSybaseASEProcedureAction(getApplication(), _resources, session));


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
		catch(Exception e)
		{
         throw new RuntimeException(e);
		}
	}

}
