/*
 * Copyright (C) 2009 Rob Manning
 * manningr@users.sourceforge.net
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

package net.sourceforge.squirrel_sql.plugins.netezza;

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
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.netezza.exp.NetezzaExtTableInodeExpanderFactory;
import net.sourceforge.squirrel_sql.plugins.netezza.exp.NetezzaSequenceInodeExpanderFactory;
import net.sourceforge.squirrel_sql.plugins.netezza.exp.NetezzaSynonymInodeExpanderFactory;
import net.sourceforge.squirrel_sql.plugins.netezza.prefs.NetezzaPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.netezza.tab.ProcedureSourceTab;
import net.sourceforge.squirrel_sql.plugins.netezza.tab.SynonymDetailsTab;
import net.sourceforge.squirrel_sql.plugins.netezza.tab.SynonymSourceTab;
import net.sourceforge.squirrel_sql.plugins.netezza.tab.ViewSourceTab;
import net.sourceforge.squirrel_sql.plugins.netezza.tokenizer.NetezzaQueryTokenizer;

/**
 * The main controller class for the Netezza plugin.
 */
public class NetezzaPlugin extends DefaultSessionPlugin
{

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(NetezzaPlugin.class);

	/** manages our query tokenizing preferences */
	private PluginQueryTokenizerPreferencesManager _prefsManager = null;	
	
	static interface i18n
	{		
		// i18n[NetezzaPlugin.prefsHint=Preferences for Netezza]
		String PREFS_HINT = s_stringMgr.getString("NetezzaPlugin.prefsHint");
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getAuthor()
	 */
	@Override
	public String getAuthor()
	{
		return "Rob Manning";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getDescriptiveName()
	 */
	@Override
	public String getDescriptiveName()
	{
		return "Netezza Plugin";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getInternalName()
	 */
	@Override
	public String getInternalName()
	{
		return "netezza";
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
		return "readme.html";
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
		return DialectFactory.isNetezza(session.getMetaData());
	}

	/**
	 * Create panel for the Global Properties dialog.
	 * 
	 * @return properties panel.
	 */
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		PluginQueryTokenizerPreferencesPanel _prefsPanel =
			new PluginQueryTokenizerPreferencesPanel(_prefsManager, "Netezza");

		PluginGlobalPreferencesTab tab = new PluginGlobalPreferencesTab(_prefsPanel);

		tab.setHint(i18n.PREFS_HINT);
		tab.setTitle("Netezza");

		return new IGlobalPreferencesPanel[] { tab };
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#initialize()
	 */
	@Override
	public void initialize() throws PluginException
	{
		_prefsManager = new PluginQueryTokenizerPreferencesManager();
		_prefsManager.initialize(this, new NetezzaPreferenceBean());
	}	
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.ISessionPlugin#sessionStarted(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	@Override
	public PluginSessionCallback sessionStarted(final ISession session)
	{
		if (!DialectFactory.isNetezza(session.getMetaData())) {
			return null;
		}
		
		session.setQueryTokenizer(new NetezzaQueryTokenizer(_prefsManager.getPreferences()));
		
		GUIUtils.processOnSwingEventThread(new Runnable() {

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
		String stmtSep = _prefsManager.getPreferences().getStatementSeparator();
		
		// ////// Object Tree Expanders ////////
		
		// Schema Expander - sequences
		objTree.addExpander(DatabaseObjectType.SCHEMA, 
			new SchemaExpander(new NetezzaSequenceInodeExpanderFactory(), 
					DatabaseObjectType.SEQUENCE_TYPE_DBO));

		// Schema Expander - synonyms
		objTree.addExpander(DatabaseObjectType.SCHEMA, 
			new SchemaExpander(new NetezzaSynonymInodeExpanderFactory(), 
					DatabaseObjectType.SYNONYM_TYPE_DBO));

		// Schema Expander - external tables
		objTree.addExpander(DatabaseObjectType.SCHEMA, 
			new SchemaExpander(new NetezzaExtTableInodeExpanderFactory(), 
					DatabaseObjectType.TABLE_TYPE_DBO));		
		
		// ////// Object Tree Detail Tabs ////////
		objTree.addDetailTab(DatabaseObjectType.PROCEDURE, new ProcedureSourceTab(stmtSep));
		
		// Netezza data dictionary lacks sequence details needed for a details tab or a source tab.
		objTree.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
		
		objTree.addDetailTab(DatabaseObjectType.SYNONYM, new DatabaseObjectInfoTab());
		objTree.addDetailTab(DatabaseObjectType.SYNONYM, new SynonymDetailsTab());
		objTree.addDetailTab(DatabaseObjectType.SYNONYM, new SynonymSourceTab(stmtSep));
		
		objTree.addDetailTab(DatabaseObjectType.VIEW, new DatabaseObjectInfoTab());
		objTree.addDetailTab(DatabaseObjectType.VIEW, new ViewSourceTab(stmtSep));
	}


}
