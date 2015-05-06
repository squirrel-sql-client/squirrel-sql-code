/*
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

package net.sourceforge.squirrel_sql.plugins.vertica;

import java.sql.SQLException;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallbackAdaptor;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.SchemaExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.plugins.vertica.VerticaObjectType;
import net.sourceforge.squirrel_sql.plugins.vertica.exp.TableExpander;
import net.sourceforge.squirrel_sql.plugins.vertica.exp.UDFExpanderFactory;
import net.sourceforge.squirrel_sql.plugins.vertica.exp.UDTExpanderFactory;
import net.sourceforge.squirrel_sql.plugins.vertica.exp.SequenceExpanderFactory;
import net.sourceforge.squirrel_sql.plugins.vertica.exp.SysTableExpanderFactory;
import net.sourceforge.squirrel_sql.plugins.vertica.exp.ProjectionParentExpander;
import net.sourceforge.squirrel_sql.plugins.vertica.tab.ProjectionTab;
import net.sourceforge.squirrel_sql.plugins.vertica.tab.ContentPlusTab;
import net.sourceforge.squirrel_sql.plugins.vertica.tab.DBObjectSourceTab;
import net.sourceforge.squirrel_sql.plugins.vertica.tab.SequenceDetailTab;
import net.sourceforge.squirrel_sql.plugins.vertica.tab.UDFDetailTab;
import net.sourceforge.squirrel_sql.plugins.vertica.tab.UDTDetailTab;
import net.sourceforge.squirrel_sql.plugins.vertica.tab.ProjectionDetailTab;

/**
 * The main controller class for the Vertica plugin.
 */
public class VerticaPlugin extends DefaultSessionPlugin
{

    private final static StringManager s_stringMgr = StringManagerFactory.getStringManager(VerticaPlugin.class);

	private final static ILogger s_log = LoggerController.createLogger(VerticaPlugin.class);

	static interface i18n
	{

    }

	// Internal name of this plugin
	public String getInternalName()
	{
		return "vertica";
	}

	// Descriptive name of this plugin
	public String getDescriptiveName()
	{
		return "Vertica Plugin";
	}

	public String getVersion()
	{
		return "0.20";
	}

	public String getAuthor()
	{
		return "Vertica Team";
	}

	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	public String getHelpFileName()
	{
		return "doc/readme.html";
	}

	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	/**
	 * Load this plugin.
	 * 
	 * @param app
	 *           Application API.
	 */
	public synchronized void load(IApplication app) throws PluginException
	{
		super.load(app);
	}

	/**
	 * Initialize this plugin.
	 */
	public synchronized void initialize() throws PluginException
	{
		super.initialize();
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
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				updateObjectTree(session.getObjectTreeAPIOfActiveSessionWindow());
			}
		});


		return new PluginSessionCallbackAdaptor(this);
	}

	@Override
	protected boolean isPluginSession(ISession session)
	{
        try
        {
            String driverName = session.getSQLConnection().getConnection().getMetaData().getDatabaseProductName().toUpperCase();
            if (driverName.startsWith("VERTICA DATABASE"))
                // Activate this plug-in if this is a Vertica session
                return true;
        }
        catch (SQLException e)
        {}

        return false;
	}

	private void updateObjectTree(final IObjectTreeAPI objTree)
	{
        // Schema Expanders...
		
        objTree.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander(new SysTableExpanderFactory(), 
					                                                DatabaseObjectType.TABLE_TYPE_DBO));
		objTree.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander(new SequenceExpanderFactory(), 
					                                                DatabaseObjectType.SEQUENCE_TYPE_DBO));
		objTree.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander(new UDTExpanderFactory(), 
				VerticaObjectType.VUDT));
		objTree.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander(new UDFExpanderFactory(), 
					                                                DatabaseObjectType.UDF_TYPE_DBO));

		objTree.addExpander(DatabaseObjectType.TABLE,              new TableExpander());
        objTree.addExpander(VerticaObjectType.PROJECTION_PARENT,   new ProjectionParentExpander());

        // Object Detail Tabs...
        objTree.addDetailTab(DatabaseObjectType.TABLE,     new ContentPlusTab((ObjectTreePanel)objTree));
        objTree.addDetailTab(DatabaseObjectType.TABLE,     new ProjectionTab());
		objTree.addDetailTab(DatabaseObjectType.TABLE,     new DBObjectSourceTab());
	    
	    objTree.addDetailTab(DatabaseObjectType.VIEW,      new ContentPlusTab((ObjectTreePanel)objTree));
		objTree.addDetailTab(DatabaseObjectType.VIEW,      new DBObjectSourceTab());

        objTree.addDetailTab(DatabaseObjectType.SEQUENCE,  new DatabaseObjectInfoTab());
        objTree.addDetailTab(DatabaseObjectType.SEQUENCE,  new SequenceDetailTab());
        //objTree.addDetailTab(DatabaseObjectType.SEQUENCE,  new DBObjectSourceTab());

        objTree.addDetailTab(DatabaseObjectType.UDF,       new DatabaseObjectInfoTab());
        objTree.addDetailTab(DatabaseObjectType.UDF,       new UDFDetailTab());
        objTree.addDetailTab(DatabaseObjectType.UDF,       new DBObjectSourceTab());
        
        objTree.addDetailTab(VerticaObjectType.VUDT,       new DatabaseObjectInfoTab());
        objTree.addDetailTab(VerticaObjectType.VUDT,       new UDFDetailTab());
        //objTree.addDetailTab(VerticaObjectType.VUDT,       new DBObjectSourceTab());
        
       // objTree.addDetailTab(DatabaseObjectType.UDT,       new DatabaseObjectInfoTab());
       // objTree.addDetailTab(DatabaseObjectType.UDT,       new UDFDetailTab());
       // objTree.addDetailTab(DatabaseObjectType.UDT,       new DBObjectSourceTab());

        objTree.addDetailTab(VerticaObjectType.PROJECTION, new DatabaseObjectInfoTab());
        objTree.addDetailTab(VerticaObjectType.PROJECTION, new ProjectionDetailTab());
        objTree.addDetailTab(VerticaObjectType.PROJECTION, new DBObjectSourceTab());
	}
	
}
