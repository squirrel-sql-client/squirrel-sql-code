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
package net.sourceforge.squirrel_sql.client.session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.Action;
import javax.swing.JComponent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.MockApplication;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.MockSQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.MockMessageHandler;
import net.sourceforge.squirrel_sql.mo.sql.MockDatabaseMetaData;
import net.sourceforge.squirrel_sql.test.TestUtil;

import com.mockobjects.sql.MockConnection2;

public class MockSession implements ISession {

   ISQLAliasExt sqlAlias = null;
    ISQLDriver sqlDriver = null;
    SQLConnection con = null;
    MockDatabaseMetaData mdata = null;
    MockApplication app = null;
    SessionProperties props = null;
    IMessageHandler messageHandler = null;
    SchemaInfo schemaInfo = null;
    SessionPanel sessionPanel = null;
    SquirrelPreferences prefs = null;
    UidIdentifier id = null;
    boolean closed;
    
    ISQLPanelAPI panelApi = TestUtil.getEasyMockSqlPanelApi();
        
    // These tell the Dialect test runner where tables that is creates can be 
    // found.
    private String defaultCatalog = "";
    private String defaultSchema = "";
    
    public MockSession() {
    	init(true);
    }
    
    public MockSession(String className, 
    				   String jdbcUrl, 
    				   String u, 
    				   String p) 
    	throws Exception 
    {
    	System.out.println("Attempting to load class="+className);
    	Class.forName(className);
        System.out.println("Getting connection for url="+jdbcUrl);
    	Connection c = DriverManager.getConnection(jdbcUrl, u, p);
    	sqlDriver = new MockSQLDriver(className, jdbcUrl);
    	con = new SQLConnection(c, null, sqlDriver);
    	init(false);
        sqlAlias.setUrl(jdbcUrl);
        sqlAlias.setUserName(u);
        sqlAlias.setPassword(p);
        sqlDriver.setDriverClassName(className);
    }
    
    private void init(boolean initConnection) {
    	if (initConnection) {
            //MockConnection2 mockCon = getMockConnection();
    		con = new SQLConnection(getMockConnection(), null, sqlDriver);
    	}
    	id = new UidIdentifier();
    	messageHandler = new MockMessageHandler();
    	props = new SessionProperties();
    	props.setLoadSchemasCatalogs(false);
    	app = new MockApplication();
    	app.getMockSessionManager().setSession(this);
    	sqlAlias = new SQLAlias(new UidIdentifier());
    	schemaInfo = new SchemaInfo(app);
    	schemaInfo.initialLoad(this);
    	prefs = app.getSquirrelPreferences();
    	try {
    		UIFactory.initialize(prefs, app);
    	} catch (Throwable e) {
    		
    	}
        // If we are connecting to a database, then this is fine.  However, when
        // using MockObjects, this is problematic since there are many 
        // unimplemented methods in the MockObjects implementation that are 
        // required by this.
        if (!initConnection) {
            sessionPanel = new SessionPanel(this);
        }
    }
    
    private MockConnection2 getMockConnection() {
    	MockConnection2 result = new MockConnection2();
        sqlDriver = new MockSQLDriver("JUnitTestClassName", "JUnitJDBCURL");
        mdata = new MockDatabaseMetaData();
        mdata.setupDriverName("junit");
        result.setupMetaData(mdata);    
        return result;
    }
    
    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#getExceptionFormatter()
     */
    public ExceptionFormatter getExceptionFormatter() {
        // TODO Auto-generated method stub
        System.err.println("MockSession.getExceptionFormatter: stub not yet implemented");
        return null;
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#setExceptionFormatter(net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter)
     */
    public void setExceptionFormatter(ExceptionFormatter formatter) {
        // TODO Auto-generated method stub
        System.err.println("MockSession.setExceptionFormatter: stub not yet implemented");
    }

    public boolean isClosed() {
    	return closed;
    }

    public IApplication getApplication() {
        return app;
    }

    public ISQLConnection getSQLConnection() {
    	return con;
    }

    public ISQLDriver getDriver() {
    	return sqlDriver;
    }

    public ISQLAliasExt getAlias() {
        return sqlAlias;
    }

    public SessionProperties getProperties() {
        return props;
    }

    public void commit() {
        try {
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rollback() {
        try {
            con.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() throws SQLException {
    	if (!closed) {
    		con.close();
    	}
    }

    public void closeSQLConnection() throws SQLException {
    	con.close();
    }

    public void setSessionInternalFrame(SessionInternalFrame sif) {
        // TODO Auto-generated method stub
        System.err.println("MockSession.setSessionInternalFrame: stub not yet implemented");
    }

    public void reconnect() {
        // TODO Auto-generated method stub
        System.err.println("MockSession.reconnect: stub not yet implemented");
    }

    public Object getPluginObject(IPlugin plugin, String key) {
        // TODO Auto-generated method stub
        System.err.println("MockSession.getPluginObject: stub not yet implemented");
        return null;
    }

    public Object putPluginObject(IPlugin plugin, String key, Object obj) {
        // TODO Auto-generated method stub
        System.err.println("MockSession.putPluginObject: stub not yet implemented");
        return null;
    }

    public void removePluginObject(IPlugin plugin, String key) {
        // TODO Auto-generated method stub
    	System.err.println("MockSession.removePluginObject: stub not yet implemented");
    }

    public void setMessageHandler(IMessageHandler handler) {
        messageHandler = handler;
    }

    public IMessageHandler getMessageHandler() {
        return messageHandler;
    }

    public SessionPanel getSessionSheet() {
        return sessionPanel;
    }

    public SessionInternalFrame getSessionInternalFrame() {
        // TODO Auto-generated method stub
    	System.err.println("MockSession.getSessionInternalFrame: stub not yet implemented");    	
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.session.ISession#getSchemaInfo()
     */
    public SchemaInfo getSchemaInfo() {
    	return schemaInfo;
    }

    public void selectMainTab(int tabIndex) throws IllegalArgumentException {
        // TODO Auto-generated method stub
    	System.err.println("MockSession.selectMainTab: stub not yet implemented");
    }

    public int addMainTab(IMainPanelTab tab) {
        // TODO Auto-generated method stub
    	System.err.println("MockSession.addMainTab: stub not yet implemented");
        return 0;
    }

    public void addToStatusBar(JComponent comp) {
        // TODO Auto-generated method stub
    	System.err.println("MockSession.addToStatusBar: stub not yet implemented");
    }

    public void removeFromStatusBar(JComponent comp) {
        // TODO Auto-generated method stub
    	System.err.println("MockSession.removeFromStatusBar: stub not yet implemented");
    }

    public String getTitle() {
        // TODO Auto-generated method stub
    	System.err.println("MockSession.getTitle: stub not yet implemented");
        return null;
    }

    public String getDatabaseProductName() {
        String result = null;
        try {
        	result = con.getSQLMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void addToToolbar(Action action) {
        // TODO Auto-generated method stub
    	System.err.println("MockSession.addToToolbar: stub not yet implemented");
    }

    public void addSeparatorToToolbar() {
        // TODO Auto-generated method stub
    	System.err.println("MockSession.addSeparatorToToolbar: stub not yet implemented");
    }

    public IParserEventsProcessor getParserEventsProcessor(
            							    IIdentifier sqlEntryPanelIdentifier) 
    {
        // TODO Auto-generated method stub
    	System.err.println("MockSession.getParserEventsProcessor: stub not yet implemented");
        return null;
    }

    public void setActiveSessionWindow(
            BaseSessionInternalFrame activeActiveSessionWindow) {
        // TODO Auto-generated method stub
    	System.err.println("MockSession.setActiveSessionWindow: stub not yet implemented");
    }

    public BaseSessionInternalFrame getActiveSessionWindow() {
        // TODO Auto-generated method stub
    	System.err.println("MockSession.getActiveSessionWindow: stub not yet implemented");
        return null;
    }

    public ISQLPanelAPI getSQLPanelAPIOfActiveSessionWindow() {
   	 return panelApi;
    }

    public IObjectTreeAPI getObjectTreeAPIOfActiveSessionWindow() {
        // TODO Auto-generated method stub
    	System.err.println("MockSession.getObjectTreeAPIOfActiveSessionWindow: stub not yet implemented");
        return null;
    }

    public boolean isfinishedLoading() {
        // TODO Auto-generated method stub
    	System.err.println("MockSession.isfinishedLoading: stub not yet implemented");
        return true;
    }

    public void setPluginsfinishedLoading(boolean _finishedLoading) {
        // TODO Auto-generated method stub
    	System.err.println("MockSession.setPluginsfinishedLoading: stub not yet implemented");
    }

    public boolean confirmClose() {
        // TODO Auto-generated method stub
    	System.err.println("MockSession.confirmClose: stub not yet implemented");
        return false;
    }

    public IIdentifier getIdentifier() {
        return id;
    }

    public MockDatabaseMetaData getMockDatabaseMetaData() {
    	return mdata;
    }

    /**
     * @param defaultCatalog the defaultCatalog to set
     */
    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }

    /**
     * @return the defaultCatalog
     */
    public String getDefaultCatalog() {
        return defaultCatalog;
    }

    /**
     * @param defaultSchema the defaultSchema to set
     */
    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }

    /**
     * @return the defaultSchema
     */
    public String getDefaultSchema() {
        return defaultSchema;
    }
    
    public IQueryTokenizer getQueryTokenizer() {
        return new QueryTokenizer(";", "--", true);
    }

    public void setQueryTokenizer(IQueryTokenizer tokenizer) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.session.ISession#getMetaData()
     */
    public ISQLDatabaseMetaData getMetaData() {
        return con.getSQLMetaData();
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#showErrorMessage(java.lang.String)
     */
    public void showErrorMessage(String msg) {
        // TODO Auto-generated method stub
        System.err.println("MockSession.showErrorMessage: stub not yet implemented");
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#showErrorMessage(java.lang.Throwable)
     */
    public void showErrorMessage(Throwable th) {
        // TODO Auto-generated method stub
        System.err.println("MockSession.showErrorMessage: stub not yet implemented");
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#showMessage(java.lang.String)
     */
    public void showMessage(String msg) {
        // TODO Auto-generated method stub
        System.err.println("MockSession.showMessage: stub not yet implemented");
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#showMessage(java.lang.Throwable)
     */
    public void showMessage(Throwable th) {
        // TODO Auto-generated method stub
        System.err.println("MockSession.showMessage: stub not yet implemented");
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#showWarningMessage(java.lang.String)
     */
    public void showWarningMessage(String msg) {
        // TODO Auto-generated method stub
        System.err.println("MockSession.showWarningMessage: stub not yet implemented");
    }    
    
    public String formatException(Throwable th) {
        // TODO Auto-generated method stub
        System.err.println("MockSession.format: stub not yet implemented");
        return null;        
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISession#getSelectedMainTabIndex()
     */
    public int getSelectedMainTabIndex() {
       System.err.println("MockSession.getSelectedMainTabIndex: stub not yet implemented");
       return 0;
    }

}
