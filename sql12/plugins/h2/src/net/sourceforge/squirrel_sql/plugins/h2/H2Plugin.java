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

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.h2.exp.SchemaExpander;
import net.sourceforge.squirrel_sql.plugins.h2.exp.TableExpander;
import net.sourceforge.squirrel_sql.plugins.h2.tab.IndexDetailsTab;
import net.sourceforge.squirrel_sql.plugins.h2.tab.IndexSourceTab;
import net.sourceforge.squirrel_sql.plugins.h2.tab.SequenceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.h2.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.h2.tab.ViewSourceTab;


/**
 * The main controller class for the H2 plugin.
 * 
 * @author manningr
 */
public class H2Plugin extends DefaultSessionPlugin {
    
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(H2Plugin.class);

    /** Logger for this class. */
    private final static ILogger s_log = 
        LoggerController.createLogger(H2Plugin.class);

    /** API for the Obejct Tree. */
    private IObjectTreeAPI _treeAPI;

    static interface i18n {
        //i18n[PostgresPlugin.showIndexSource=Show index source]
        String SHOW_INDEX_SOURCE = 
            s_stringMgr.getString("PostgresPlugin.showIndexSource");
        
        //i18n[PostgresPlugin.showViewSource=Show view source]
        String SHOW_VIEW_SOURCE = 
            s_stringMgr.getString("PostgresPlugin.showViewSource");
        
        //i18n[PostgresPlugin.showProcedureSource=Show procedure source]
        String SHOW_PROCEDURE_SOURCE =
            s_stringMgr.getString("PostgresPlugin.showProcedureSource");
    }
    
    /**
     * Return the internal name of this plugin.
     *
     * @return  the internal name of this plugin.
     */
    public String getInternalName()
    {
        return "h2";
    }

    /**
     * Return the descriptive name of this plugin.
     *
     * @return  the descriptive name of this plugin.
     */
    public String getDescriptiveName()
    {
        return "H2 Plugin";
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
        return "Rob Manning";
    }

    /**
     * Returns a comma separated list of other contributors.
     *
     * @return  Contributors names.
     */    
    public String getContributors() {
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
     * @param   session     Session that has started.
     *
     * @return  <TT>true</TT> if session is Oracle in which case this plugin
     *                          is interested in it.
     */
    public PluginSessionCallback sessionStarted(final ISession session)
    {
       boolean isPostgres = false;
       isPostgres = isPostgres(session);
       if (isPostgres)
       {
           GUIUtils.processOnSwingEventThread(new Runnable() {
               public void run() {
                   updateTreeApi(session);
               }
           });
       }
       if (false == isPostgres)
       {
          return null;
       }

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

    private void updateTreeApi(ISession session) {
        
        _treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();
        // Expanders - trigger and index expanders are added inside the table
        // expander
        _treeAPI.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander());        
        _treeAPI.addExpander(DatabaseObjectType.TABLE, new TableExpander());
                
        // View Tab
        _treeAPI.addDetailTab(DatabaseObjectType.VIEW, 
                              new ViewSourceTab(i18n.SHOW_VIEW_SOURCE));
        
        // Index tab
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexDetailsTab());
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, 
                              new IndexSourceTab(i18n.SHOW_INDEX_SOURCE));

        // Trigger tabs
        _treeAPI.addDetailTab(IObjectTypes.TRIGGER_PARENT, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
        
        // H2 uses Java classes that implement the "Trigger" interface to operate 
        // on database tables rows when an action triggers them.  Therefore, there
        // is currently no way to access the source for a trigger.  Hopefully this
        // will change at some point in the future.
        //_treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerSourceTab("The source of the trigger"));

        // Sequence tabs
        _treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new SequenceDetailsTab());        

        
        
    }
    
    private boolean isPostgres(ISession session)
    {
        final String h2 = "h2";
        String dbms = null;
        try
        {
            dbms = session.getSQLConnection().getSQLMetaData().getDatabaseProductName();
        }
        catch (SQLException ex)
        {
				s_log.error("Unexpected exception from getDatabaseProductName()", ex);
        }
        return dbms != null && dbms.toLowerCase().startsWith(h2);
    }

}
