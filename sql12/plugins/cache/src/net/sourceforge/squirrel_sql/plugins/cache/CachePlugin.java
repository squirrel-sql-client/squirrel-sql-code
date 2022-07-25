package net.sourceforge.squirrel_sql.plugins.cache;

import javax.swing.Action;
import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.plugins.cache.tap.CacheViewSourceTab;

/**
 * Plugin to show query statistics and plan for the Intersystems Cache database.
 */
public class CachePlugin extends DefaultSessionPlugin
{

	private Action _statisticsAndQueryPlanAction;

	private interface IMenuResourceKeys
	{
		String MENU = "cache";
	}


	private CachePluginResources _resources;

	/**
	 * Return the internal name of this plugin.
	 * 
	 * @return the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "cache";
	}

	/**
	 * Return the descriptive name of this plugin.
	 * 
	 * @return the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "Intersystems Cache/IRIS Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 * 
	 * @return the current version of this plugin.
	 */
	public String getVersion()
	{
		return "0.01";
	}

	/**
	 * Returns the authors name.
	 * 
	 * @return the authors name.
	 */
	public String getAuthor()
	{
		return "Gerd Wagner, Martin Weissenborn";
	}


	/**
	 * Returns the name of the change log for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 * 
	 * @return the changelog file name or <TT>null</TT> if plugin doesn't have a change log.
	 */
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	/**
	 * Returns the name of the Help file for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 * 
	 * @return the Help file name or <TT>null</TT> if plugin doesn't have a help file.
	 */
	public String getHelpFileName()
	{
		return "doc/readme.txt";
	}

	/**
	 * Returns the name of the Licence file for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 * 
	 * @return the Licence file name or <TT>null</TT> if plugin doesn't have a licence file.
	 */
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	/**
	 * @return Comma separated list of contributors.
	 */
	public String getContributors()
	{
		return "";
	}

	/**
	 * Create preferences panel for the Global Preferences dialog.
	 * 
	 * @return Preferences panel.
	 */
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		return new IGlobalPreferencesPanel[0];
	}

	/**
	 * Initialize this plugin.
	 */
	public synchronized void initialize() throws PluginException
	{
		_resources = new CachePluginResources(this);
	}

	/**
	 * Called when a session started. Add commands to popup menu in object tree.
	 * 
	 * @param session
	 *           The session that is starting.
	 * @return An implementation of PluginSessionCallback or null to indicate the plugin does not work with
	 *         this session
	 */
	public PluginSessionCallback sessionStarted(ISession session)
	{
		if(    false == DialectFactory.isIntersystemsCache(session.getMetaData())
			 && false == DialectFactory.isIntersystemsIris(session.getMetaData()))
		{
			return null;
		}

		addStatisticsAndQueryPlanSessionMenuAction(session);

		GUIUtils.processOnSwingEventThread(() -> updateTreeApi(session.getSessionInternalFrame().getObjectTreeAPI()));

		return new PluginSessionCallback()
		{
			@Override
			public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
			{
				initSqlInternalFrame(sqlInternalFrame, session);
			}

			@Override
			public void additionalSQLTabOpened(AdditionalSQLTab additionalSQLTab)
			{

			}

			@Override
			public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
			{
				updateTreeApi(objectTreeInternalFrame.getObjectTreeAPI());
			}

			@Override
			public void objectTreeInSQLTabOpened(ObjectTreePanel objectTreePanel)
			{

			}
		};
	}

	private void updateTreeApi(IObjectTreeAPI objectTreeAPI)
	{
		CacheViewSourceTab viewSourceTab = new CacheViewSourceTab(objectTreeAPI.getSession());
		objectTreeAPI.addDetailTab(DatabaseObjectType.VIEW, viewSourceTab);
		objectTreeAPI.refreshTree();
	}


	private void initSqlInternalFrame(SQLInternalFrame sqlInternalFrame, ISession session)
	{
		if( false == DialectFactory.isIntersystemsCache(session.getMetaData()) )
		{
			return;
		}

		ActionCollection coll = Main.getApplication().getActionCollection();

		if( null == _statisticsAndQueryPlanAction )
		{
			_statisticsAndQueryPlanAction = new StatisticsAndQueryPlanAction(_resources);
			coll.add(_statisticsAndQueryPlanAction);
		}

		JMenu menu = _resources.createMenu(IMenuResourceKeys.MENU);
		Main.getApplication().addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);


		_resources.addToMenu(_statisticsAndQueryPlanAction, menu);

		sqlInternalFrame.addSeparatorToToolbar();
		sqlInternalFrame.addToToolbar(coll.get(StatisticsAndQueryPlanAction.class));
	}

	private void addStatisticsAndQueryPlanSessionMenuAction(ISession session)
	{

		ActionCollection coll = Main.getApplication().getActionCollection();

		if( null == _statisticsAndQueryPlanAction )
		{
			_statisticsAndQueryPlanAction = new StatisticsAndQueryPlanAction(_resources);
			coll.add(_statisticsAndQueryPlanAction);

			JMenu menu = _resources.createMenu(IMenuResourceKeys.MENU);
			Main.getApplication().addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);
			_resources.addToMenu(_statisticsAndQueryPlanAction, menu);
		}

		session.addSeparatorToToolbar();
		session.addToToolbar(coll.get(StatisticsAndQueryPlanAction.class));
	}



}
