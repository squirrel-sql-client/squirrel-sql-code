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
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.plugins.oracle.expander.DatabaseExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.InstanceParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.PackageExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.SchemaExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.SessionParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.TableExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.TriggerParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.expander.UserParentExpander;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.InstanceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.ObjectSourceTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.OptionsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.SequenceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.SessionDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.SessionStatisticsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.TriggerColumnInfoTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.oracle.tab.UserDetailsTab;

import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
/**
 * Oracle plugin class.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class OraclePlugin extends DefaultSessionPlugin
{
	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(OraclePlugin.class);

	/** API for the Obejct Tree. */
	private IObjectTreeAPI _treeAPI;

	/**
	 * Return the internal name of this plugin.
	 *
	 * @return	the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "oracle";
	}

	/**
	 * Return the descriptive name of this plugin.
	 *
	 * @return	the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "Oracle Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return	the current version of this plugin.
	 */
	public String getVersion()
	{
		return "0.11";
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
	 * Application is shutting down so save preferences.
	 */
	public void unload()
	{
		super.unload();
	}

	/**
	 * Session has been started. If this is an Oracle session then
	 * register an extra expander for the Schema nodes to show
	 * Oracle Packages.
	 *
	 * @param	session		Session that has started.
	 *
	 * @return	<TT>true</TT> if session is Oracle in which case this plugin
	 * 							is interested in it.
	 */
	public boolean sessionStarted(ISession session)
	{
		boolean isOracle = false;
		if( super.sessionStarted(session))
		{
			isOracle = isOracle(session);
			if (isOracle)
			{
				_treeAPI = session.getObjectTreeAPI(this);

				// Tabs to add to the database node.
				_treeAPI.addDetailTab(DatabaseObjectType.SESSION, new OptionsTab());

				_treeAPI.addDetailTab(IObjectTypes.CONSUMER_GROUP, new DatabaseObjectInfoTab());
				_treeAPI.addDetailTab(DatabaseObjectType.FUNCTION, new DatabaseObjectInfoTab());
				_treeAPI.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
				_treeAPI.addDetailTab(IObjectTypes.LOB, new DatabaseObjectInfoTab());
				_treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
				_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
				_treeAPI.addDetailTab(IObjectTypes.TRIGGER_PARENT, new DatabaseObjectInfoTab());
				_treeAPI.addDetailTab(IObjectTypes.TYPE, new DatabaseObjectInfoTab());

				// Expanders.
				_treeAPI.addExpander(DatabaseObjectType.SESSION, new DatabaseExpander());
				_treeAPI.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander(this));
				_treeAPI.addExpander(DatabaseObjectType.TABLE, new TableExpander());
				_treeAPI.addExpander(IObjectTypes.PACKAGE, new PackageExpander());
				_treeAPI.addExpander(IObjectTypes.USER_PARENT, new UserParentExpander());
				_treeAPI.addExpander(IObjectTypes.SESSION_PARENT, new SessionParentExpander());
				_treeAPI.addExpander(IObjectTypes.INSTANCE_PARENT, new InstanceParentExpander(this));
				_treeAPI.addExpander(IObjectTypes.TRIGGER_PARENT, new TriggerParentExpander());

				_treeAPI.addDetailTab(DatabaseObjectType.PROCEDURE, new ObjectSourceTab("PROCEDURE", "Show stored procedure source"));
				_treeAPI.addDetailTab(DatabaseObjectType.FUNCTION, new ObjectSourceTab("FUNCTION", "Show function source"));
				_treeAPI.addDetailTab(IObjectTypes.INSTANCE, new InstanceDetailsTab());
				_treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new SequenceDetailsTab());
				_treeAPI.addDetailTab(IObjectTypes.SESSION, new SessionDetailsTab());
				_treeAPI.addDetailTab(IObjectTypes.SESSION, new SessionStatisticsTab());
				_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
				_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerSourceTab("Show trigger source"));
				_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerColumnInfoTab());
				_treeAPI.addDetailTab(DatabaseObjectType.USER, new UserDetailsTab());
			}
		}
		return isOracle;
	}

	private boolean isOracle(ISession session)
	{
		final String ORACLE = "oracle";
		String dbms = null;
		try
		{
			dbms = session.getSQLConnection().getSQLMetaData().getDatabaseProductName();
		}
		catch (SQLException ex)
		{
			s_log.debug("Error in getDatabaseProductName()", ex);
		}
		return dbms != null && dbms.toLowerCase().startsWith(ORACLE);
	}
}
