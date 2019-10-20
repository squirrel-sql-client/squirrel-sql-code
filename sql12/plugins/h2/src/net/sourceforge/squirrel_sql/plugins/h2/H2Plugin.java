package net.sourceforge.squirrel_sql.plugins.h2;

/*
 * Copyright (C) 2007 Rob Manning
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

import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallbackAdaptor;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.SchemaExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableWithChildNodesExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.h2.exp.H2SequenceInodeExpanderFactory;
import net.sourceforge.squirrel_sql.plugins.h2.exp.H2TableIndexExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.h2.exp.H2TableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.h2.tab.*;


/**
 * The main controller class for the H2 plugin.
 * 
 * @author manningr
 */
public class H2Plugin extends DefaultSessionPlugin
{

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(H2Plugin.class);

	/** Logger for this class. */
	@SuppressWarnings("unused")
	private final static ILogger s_log = LoggerController.createLogger(H2Plugin.class);


	static interface i18n
	{
		// i18n[PostgresPlugin.showIndexSource=Show index source]
		String SHOW_INDEX_SOURCE = s_stringMgr.getString("PostgresPlugin.showIndexSource");

		// i18n[PostgresPlugin.showViewSource=Show view source]
		String SHOW_VIEW_SOURCE = s_stringMgr.getString("PostgresPlugin.showViewSource");

		// i18n[PostgresPlugin.showProcedureSource=Show procedure source]
		String SHOW_PROCEDURE_SOURCE = s_stringMgr.getString("PostgresPlugin.showProcedureSource");
	}

	/**
	 * Return the internal name of this plugin.
	 * 
	 * @return the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "h2";
	}

	/**
	 * Return the descriptive name of this plugin.
	 * 
	 * @return the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "H2 Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 * 
	 * @return the current version of this plugin.
	 */
	public String getVersion()
	{
		return "0.02";
	}

	/**
	 * Returns the authors name.
	 * 
	 * @return the authors name.
	 */
	public String getAuthor()
	{
		return "Rob Manning";
	}

	/**
	 * Returns a comma separated list of other contributors.
	 * 
	 * @return Contributors names.
	 */
	public String getContributors()
	{
		return "";
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
		return "doc/readme.html";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getLicenceFileName()
	 */
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	public boolean allowsSessionStartedInBackground()
	{
		return true;
	}

	/**
	 * Session has been started. Update the tree api in using the event thread
	 * 
	 * @param session
	 *           Session that has started.
	 * @return <TT>true</TT> if session is Oracle in which case this plugin is interested in it.
	 */
	public PluginSessionCallback sessionStarted(final ISession session)
	{
		if (!isPluginSession(session))
		{
			return null;
		}

		GUIUtils.processOnSwingEventThread(() -> updateTreeApi(session.getSessionInternalFrame().getObjectTreeAPI()));

		return new PluginSessionCallback()
		{
			@Override
			public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
			{
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
				updateTreeApi(objectTreePanel);
			}
		};
	}

	@Override
	protected boolean isPluginSession(ISession session)
	{
		return DialectFactory.isH2(session.getMetaData());
	}

	private void updateTreeApi(IObjectTreeAPI objectTreeAPI)
	{
		IQueryTokenizer qt = objectTreeAPI.getSession().getQueryTokenizer();
		String stmtSep = qt.getSQLStatementSeparator();

		// Expanders - trigger and index expanders are added inside the table
		// expander
		objectTreeAPI.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander(new H2SequenceInodeExpanderFactory(), DatabaseObjectType.SEQUENCE));

		TableWithChildNodesExpander tableExp = new TableWithChildNodesExpander();
		tableExp.setTableIndexExtractor(new H2TableIndexExtractorImpl());
		tableExp.setTableTriggerExtractor(new H2TableTriggerExtractorImpl());
		objectTreeAPI.addExpander(DatabaseObjectType.TABLE, tableExp);

		// View Tab
		objectTreeAPI.addDetailTab(DatabaseObjectType.VIEW, new ViewSourceTab(i18n.SHOW_VIEW_SOURCE, stmtSep));

		// Index tab
		objectTreeAPI.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
		objectTreeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexDetailsTab());
		objectTreeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexSourceTab(i18n.SHOW_INDEX_SOURCE, stmtSep));

		// Trigger tabs
		objectTreeAPI.addDetailTab(DatabaseObjectType.TRIGGER_TYPE_DBO, new DatabaseObjectInfoTab());
		objectTreeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
		objectTreeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());

		// H2 uses Java classes that implement the "Trigger" interface to operate
		// on database tables rows when an action triggers them. Therefore, there
		// is currently no way to access the source for a trigger. Hopefully this
		// will change at some point in the future.
		// treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerSourceTab("The source of the trigger"));

		// Sequence tabs
		objectTreeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
		objectTreeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new SequenceDetailsTab());

	}

}
