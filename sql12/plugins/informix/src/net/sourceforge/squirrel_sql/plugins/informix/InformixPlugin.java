package net.sourceforge.squirrel_sql.plugins.informix;

/*
 * Copyright (C) 2006 Rob Manning
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
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.informix.exception.InformixExceptionFormatter;
import net.sourceforge.squirrel_sql.plugins.informix.exp.InformixSequenceInodeExpanderFactory;
import net.sourceforge.squirrel_sql.plugins.informix.exp.InformixTableIndexExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.informix.exp.InformixTableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.informix.tab.IndexDetailsTab;
import net.sourceforge.squirrel_sql.plugins.informix.tab.ProcedureSourceTab;
import net.sourceforge.squirrel_sql.plugins.informix.tab.SequenceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.informix.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.informix.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.informix.tab.ViewSourceTab;

/**
 * The main controller class for the Informix plugin.
 * 
 * @author manningr
 */
public class InformixPlugin extends DefaultSessionPlugin
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(InformixPlugin.class);

	/** Logger for this class. */
	@SuppressWarnings("unused")
	private final static ILogger s_log = LoggerController.createLogger(InformixPlugin.class);

	static interface i18n
	{
		// i18n[InformixPlugin.showViewSource=Show view source]
		String SHOW_VIEW_SOURCE = s_stringMgr.getString("InformixPlugin.showViewSource");

		// i18n[InformixPlugin.showProcedureSource=Show procedure source]
		String SHOW_PROCEDURE_SOURCE = s_stringMgr.getString("InformixPlugin.showProcedureSource");
	}

	/**
	 * Return the internal name of this plugin.
	 * 
	 * @return the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "informix";
	}

	/**
	 * Return the descriptive name of this plugin.
	 * 
	 * @return the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "Informix Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 * 
	 * @return the current version of this plugin.
	 */
	public String getVersion()
	{
		return "0.03";
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
	@Override
	public String getContributors()
	{
		return "Doug Lawry";
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
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin#allowsSessionStartedInBackground()
	 */
	@Override
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

		InformixExceptionFormatter formatter = new InformixExceptionFormatter(session);
		session.setExceptionFormatter(formatter);
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

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin#isPluginSession(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	@Override
	protected boolean isPluginSession(ISession session)
	{
		return DialectFactory.isInformix(session.getMetaData());
	}

	/**
	 * Add Informix-specific tabs when an informix session is started.
	 * @param objectTreeAPI
	 */
	private void updateTreeApi(IObjectTreeAPI objectTreeAPI)
	{
		objectTreeAPI.addDetailTab(DatabaseObjectType.PROCEDURE, new ProcedureSourceTab(i18n.SHOW_PROCEDURE_SOURCE));

		objectTreeAPI.addDetailTab(DatabaseObjectType.VIEW, new ViewSourceTab(i18n.SHOW_VIEW_SOURCE));

		objectTreeAPI.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
		objectTreeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexDetailsTab());
		objectTreeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
		objectTreeAPI.addDetailTab(DatabaseObjectType.TRIGGER_TYPE_DBO, new DatabaseObjectInfoTab());
		objectTreeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
		objectTreeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new SequenceDetailsTab());

		// Expanders - trigger and index expanders are added inside the table
		// expander
		objectTreeAPI.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander(new InformixSequenceInodeExpanderFactory(), DatabaseObjectType.SEQUENCE));

		TableWithChildNodesExpander tableExp = new TableWithChildNodesExpander();
		tableExp.setTableIndexExtractor(new InformixTableIndexExtractorImpl());
		tableExp.setTableTriggerExtractor(new InformixTableTriggerExtractorImpl());
		objectTreeAPI.addExpander(DatabaseObjectType.TABLE, tableExp);

		objectTreeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
		objectTreeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerSourceTab("The source of the trigger"));

	}

}
