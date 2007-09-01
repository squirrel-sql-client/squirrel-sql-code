package net.sourceforge.squirrel_sql.plugins.db2;
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
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableIndexExtractor;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableWithChildNodesExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.db2.exp.DB2TableIndexExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.db2.exp.DB2TableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.db2.exp.SchemaExpander;
import net.sourceforge.squirrel_sql.plugins.db2.tab.IndexDetailsTab;
import net.sourceforge.squirrel_sql.plugins.db2.tab.TableSourceTab;
import net.sourceforge.squirrel_sql.plugins.db2.tab.ProcedureSourceTab;
import net.sourceforge.squirrel_sql.plugins.db2.tab.SequenceDetailsTab;
import net.sourceforge.squirrel_sql.plugins.db2.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.db2.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.db2.tab.UDFDetailsTab;
import net.sourceforge.squirrel_sql.plugins.db2.tab.UDFSourceTab;
import net.sourceforge.squirrel_sql.plugins.db2.tab.ViewSourceTab;


/**
 * The main controller class for the DB2 plugin.
 * 
 * @author manningr
 */
public class DB2Plugin extends DefaultSessionPlugin {
    
    private static final String JCC_DRIVER_NAME = "IBM DB2 JDBC Universal Driver Architecture";
    
    /** The product name that indicates we need to use os/400 queries */ 
    private static final String OS_400_PRODUCT_NAME = "DB2 UDB for AS/400";
    
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DB2Plugin.class);

    /** Logger for this class. */
    private final static ILogger s_log = 
        LoggerController.createLogger(DB2Plugin.class);

    /** API for the Object Tree. */
    private IObjectTreeAPI _treeAPI;

    static interface i18n {
        //i18n[DB2Plugin.showUdfSource=Show UDF source]
        String SHOW_UDF_SOURCE = 
            s_stringMgr.getString("DB2Plugin.showUdfSource");
        
        //i18n[DB2Plugin.showViewSource=Show view source]
        String SHOW_VIEW_SOURCE = 
            s_stringMgr.getString("DB2Plugin.showViewSource");
        
        //i18n[DB2Plugin.showProcedureSource=Show procedure source]
        String SHOW_PROCEDURE_SOURCE =
            s_stringMgr.getString("DB2Plugin.showProcedureSource");
        
        //i18n[DB2Plugin.showTriggerSource=Show trigger source]
        String SHOW_TRIGGER_SOURCE =
            s_stringMgr.getString("DB2Plugin.showTriggerSource");
        
    }
    
    /**
     * Return the internal name of this plugin.
     *
     * @return  the internal name of this plugin.
     */
    public String getInternalName()
    {
        return "db2";
    }

    /**
     * Return the descriptive name of this plugin.
     *
     * @return  the descriptive name of this plugin.
     */
    public String getDescriptiveName()
    {
        return "DB2 Plugin";
    }

    /**
     * Returns the current version of this plugin.
     *
     * @return  the current version of this plugin.
     */
    public String getVersion()
    {
        return "0.03";
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
        return "Christoph Schmitz, Tilmann Brenk";
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
       
       if (!isPluginSession(session)) {
           return null;
       }
       GUIUtils.processOnSwingEventThread(new Runnable() {
           public void run() {
               updateTreeApi(session);
           }
       });

       // Install DB2JCCExceptionFormatter iff we're using the JCC driver
       try {
           if (JCC_DRIVER_NAME.equals(session.getMetaData().getJDBCMetaData().getDriverName())) {
               session.setExceptionFormatter(new DB2JCCExceptionFormatter());
           }
       } catch (SQLException e) {
           s_log.error("Problem installing exception formatter: " + e.getMessage());
       }    
  
       return new PluginSessionCallback()
       {
          public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, 
                                             ISession sess)
          {
             // Supports Session main window only
          }

          public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, 
                                                    ISession sess)
          {
             // Supports Session main window only
          }
       };


    }

    @Override
    protected boolean isPluginSession(ISession session) {
        return DialectFactory.isDB2(session.getMetaData());
    }
    
    private void updateTreeApi(ISession session) {
        String stmtSep = session.getQueryTokenizer().getSQLStatementSeparator();
        boolean isOS400 = isOS400(session);
        
        _treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();
        _treeAPI.addDetailTab(DatabaseObjectType.PROCEDURE, 
                new ProcedureSourceTab(i18n.SHOW_PROCEDURE_SOURCE, isOS400, stmtSep));
        _treeAPI.addDetailTab(DatabaseObjectType.VIEW, 
                              new ViewSourceTab(i18n.SHOW_VIEW_SOURCE, stmtSep, isOS400));
        
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.INDEX, new IndexDetailsTab(isOS400));


        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER_TYPE_DBO, new DatabaseObjectInfoTab());

        _treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.SEQUENCE, new SequenceDetailsTab(isOS400));        

        _treeAPI.addDetailTab(DatabaseObjectType.UDF, new DatabaseObjectInfoTab());
        _treeAPI.addDetailTab(DatabaseObjectType.UDF, 
                new UDFSourceTab(i18n.SHOW_UDF_SOURCE, stmtSep, isOS400));
        _treeAPI.addDetailTab(DatabaseObjectType.UDF, new UDFDetailsTab(isOS400));  
        
        _treeAPI.addDetailTab(DatabaseObjectType.TABLE, 
                new TableSourceTab("Show MQT Source", stmtSep, isOS400));
        
        
        // Expanders - trigger and index expanders are added inside the table
        // expander
        _treeAPI.addExpander(DatabaseObjectType.SCHEMA, new SchemaExpander(isOS400));        
        
        // Expanders - trigger and index expanders are added inside the table
        // expander        
        TableWithChildNodesExpander tableExpander = 
            new TableWithChildNodesExpander(); 
        
        //tableExpander.setTableIndexExtractor(extractor);
        ITableIndexExtractor indexExtractor = 
            new DB2TableIndexExtractorImpl(isOS400);
        tableExpander.setTableIndexExtractor(indexExtractor);
        
        ITableTriggerExtractor triggerExtractor = 
            new DB2TableTriggerExtractorImpl(isOS400);
        tableExpander.setTableTriggerExtractor(triggerExtractor);
        
        _treeAPI.addExpander(DatabaseObjectType.TABLE, tableExpander);
        
        
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
        _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, 
                              new TriggerSourceTab(i18n.SHOW_TRIGGER_SOURCE, isOS400, stmtSep));
        
    }
    
    /**
     * Determines whether or not we've connected to DB2 on OS/400. 
     * 
     * @param session
     * @return
     */
    private boolean isOS400(ISession session) {
        boolean result = false;
        try {
            String prodName = session.getMetaData().getDatabaseProductName();
            if (prodName == null || prodName.equals("")) {
                s_log.info("isOS400: product name is null or empty.  " +
                		   "Assuming not an OS/400 DB2 session.");
            } else if (prodName.equals(OS_400_PRODUCT_NAME)) {
                s_log.info("isOS400: session appears to be an OS/400 DB2");
                result = true;
            } else {
                s_log.info("isOS400: session doesn't appear to be an OS/400 DB2");
            }
        } catch (SQLException e) {
            s_log.error("isOS400: unable to determine the product name: "+
                        e.getMessage(), e);
        }
        return result;
    }
}
