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
import net.sourceforge.squirrel_sql.plugins.informix.exp.SchemaExpander;
import net.sourceforge.squirrel_sql.plugins.informix.exp.TableExpander;
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
public class InformixPlugin extends DefaultSessionPlugin {
    
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(InformixPlugin.class);

    /** Logger for this class. */
    private final static ILogger s_log = 
        LoggerController.createLogger(InformixPlugin.class);

    /** API for the Obejct Tree. */
    private IObjectTreeAPI _treeAPI;

    static interface i18n {
        //i18n[InformixPlugin.showViewSource=Show view source]
        String SHOW_VIEW_SOURCE = 
            s_stringMgr.getString("InformixPlugin.showViewSource");
        
        //i18n[InformixPlugin.showProcedureSource=Show procedure source]
        String SHOW_PROCEDURE_SOURCE =
            s_stringMgr.getString("InformixPlugin.showProcedureSource");
    }
    
    /**
     * Return the internal name of this plugin.
     *
     * @return  the internal name of this plugin.
     */
    public String getInternalName()
    {
        return "informix";
    }

    /**
     * Return the descriptive name of this plugin.
     *
     * @return  the descriptive name of this plugin.
     */
    public String getDescriptiveName()
    {
        return "Informix Plugin";
    }

    /**
     * Returns the current version of this plugin.
     *
     * @return  the current version of this plugin.
     */
    public String getVersion()
    {
        return "0.02";
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
        return "Doug Lawry";
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
       boolean isInformix = false;
       isInformix = isInformix(session);
       if (isInformix)
       {
           GUIUtils.processOnSwingEventThread(new Runnable() {
               public void run() {
                   updateTreeApi(session);
               }
           });
       }
       if (false == isInformix)
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
        //DatabaseObjectInfoTab dboit = new DatabaseObjectInfoTab();
        
        _treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();
        
        _treeAPI.addDetailTab(DatabaseObjectType.PROCEDURE, 
                new ProcedureSourceTab(i18n.SHOW_PROCEDURE_SOURCE));
        
        _treeAPI.addDetailTab(DatabaseObjectType.VIEW, 
                              new ViewSourceTab(i18n.SHOW_VIEW_SOURCE));
        
        
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexDetailsTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(IObjectTypes.TRIGGER_PARENT, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new SequenceDetailsTab());        
        
        // Expanders - trigger and index expanders are added inside the table
        // expander
        _treeAPI.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander());        
        _treeAPI.addExpander(DatabaseObjectType.TABLE, new TableExpander());
        
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerSourceTab("The source of the trigger"));
        
    }
    
    private boolean isInformix(ISession session)
    {
        final String INFORMIX = "informix";
        String dbms = null;
        try
        {
            dbms = session.getSQLConnection().getSQLMetaData().getDatabaseProductName();
        }
        catch (SQLException ex)
        {
				s_log.error("Unexpected exception from getDatabaseProductName()", ex);
        }
        return dbms != null && dbms.toLowerCase().contains(INFORMIX);
    }

}
