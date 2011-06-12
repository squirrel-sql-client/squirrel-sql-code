/*
 * Copyright (C) 2011 Adam Winn
 *
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

package net.sourceforge.squirrel_sql.plugins.greenplum;

import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginGlobalPreferencesTab;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.SchemaExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.preferences.BaseQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.greenplum.exp.GreenplumExtTableInodeExpanderFactory;
import net.sourceforge.squirrel_sql.plugins.greenplum.tab.GreenplumExternalTableDetailsTab;

/**
 * The main controller class for the Greenplum plugin.
 */
public class GreenplumPlugin extends DefaultSessionPlugin
{

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GreenplumPlugin.class);

    private static ILogger s_log = LoggerController.createLogger(GreenplumPlugin.class);

	/** manages our query tokenizing preferences */
	private PluginQueryTokenizerPreferencesManager _prefsManager = null;	
	
	static interface i18n
	{		
		// i18n[GreenplumPlugin.prefsHint=Preferences for Greenplum]
		String PREFS_HINT = s_stringMgr.getString("GreenplumPlugin.prefsHint");
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getAuthor()
	 */
	@Override
	public String getAuthor()
	{
		return "Adam Winn";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getDescriptiveName()
	 */
	@Override
	public String getDescriptiveName()
	{
		return "Greenplum External Tables Plugin";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getInternalName()
	 */
	@Override
	public String getInternalName()
	{
		return "greenplum";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getVersion()
	 */
	@Override
	public String getVersion()
	{
		return "0.01";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getChangeLogFileName()
	 */
	@Override
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getHelpFileName()
	 */
	@Override
	public String getHelpFileName()
	{
		return ""; //"readme.html";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getLicenceFileName()
	 */
	@Override
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin#allowsSessionStartedInBackground()
	 */
	@Override
	public boolean allowsSessionStartedInBackground()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin#isPluginSession(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	@Override
	protected boolean isPluginSession(ISession session)
	{
        return DialectFactory.isGreenplum(session.getMetaData());
	}

//	/**
//	 * Create panel for the Global Properties dialog.
//	 *
//	 * @return properties panel.
//	 */
//	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
//	{
//		PluginQueryTokenizerPreferencesPanel _prefsPanel = new PluginQueryTokenizerPreferencesPanel(_prefsManager, "Greenplum");
//
//		PluginGlobalPreferencesTab tab = new PluginGlobalPreferencesTab(_prefsPanel);
//
//		tab.setHint(i18n.PREFS_HINT);
//		tab.setTitle("Greenplum");
//
//		return new IGlobalPreferencesPanel[] { tab };
//	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#initialize()
	 */
	@Override
	public void initialize() throws PluginException
	{
		_prefsManager = new PluginQueryTokenizerPreferencesManager();
      _prefsManager.initialize(this, new BaseQueryTokenizerPreferenceBean());
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.ISessionPlugin#sessionStarted(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	@Override
	public PluginSessionCallback sessionStarted(final ISession session)
	{
		if(!DialectFactory.isGreenplum(session.getMetaData()))
        {
		    return null;
		}

		GUIUtils.processOnSwingEventThread(new Runnable()
        {

			@Override
			public void run()
			{
				updateObjectTree(session.getObjectTreeAPIOfActiveSessionWindow());
			}
			
		});
		
		return null;
	}

	private void updateObjectTree(final IObjectTreeAPI objTree)
	{
		// ////// Object Tree Expanders ////////

		// Schema Expander - external tables
		objTree.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander(new GreenplumExtTableInodeExpanderFactory(), DatabaseObjectType.TABLE_TYPE_DBO));

        objTree.addDetailTab(DatabaseObjectType.TABLE_TYPE_DBO, new GreenplumExternalTableDetailsTab());
	}


}
