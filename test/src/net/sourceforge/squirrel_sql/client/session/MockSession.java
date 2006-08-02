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
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.MockSQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.MockSQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.MockMessageHandler;
import net.sourceforge.squirrel_sql.mo.sql.MockDatabaseMetaData;

import com.mockobjects.sql.MockConnection2;

public class MockSession implements ISession {

    ISQLAliasExt sqlAlias = null;
    ISQLDriver sqlDriver = null;
    SQLConnection con = null;
    MockConnection2 mcon2 = null;
    MockDatabaseMetaData mdata = null;
    MockApplication app = null;
    SessionProperties props = null;
    IMessageHandler messageHandler = null;
    SchemaInfo schemaInfo = null;
    private ISQLDriver driver = null;
    boolean closed;
        
    public MockSession() {
    	mcon2 = new MockConnection2();    	
        sqlAlias = new MockSQLAlias();
        sqlDriver = new SQLDriver();
        app = new MockApplication();
        mdata = new MockDatabaseMetaData();
        mdata.setupDriverName("junit");
        mcon2.setupMetaData(mdata);
        con = new SQLConnection(mcon2, null);
        props = new SessionProperties();
        messageHandler = new MockMessageHandler();
    }
    
    public MockSession(String className, 
    				   String jdbcUrl, 
    				   String u, 
    				   String p) 
    	throws Exception 
    {
    	System.out.println("Attempting to load class="+className);
    	Class.forName(className);
    	Connection c = DriverManager.getConnection(jdbcUrl, u, p);
    	SQLDriverPropertyCollection col = null;
    	driver = new MockSQLDriver(className, jdbcUrl);
    	app = new MockApplication();
    	con = new SQLConnection(c, col);
    	props = new SessionProperties();
    	schemaInfo = new SchemaInfo(null);
    	schemaInfo.initialLoad(this);
    	sqlAlias = new SQLAlias(new UidIdentifier());
    }
    
    
    public boolean isClosed() {
    	return closed;
    }

    public IApplication getApplication() {
        return app;
    }

    public SQLConnection getSQLConnection() {
    	return con;
    }

    public ISQLDriver getDriver() {
    	return driver;
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

    }

    public void setMessageHandler(IMessageHandler handler) {
        messageHandler = handler;
    }

    public IMessageHandler getMessageHandler() {
        return messageHandler;
    }

    public SessionPanel getSessionSheet() {
        // TODO Auto-generated method stub
        return null;
    }

    public SessionInternalFrame getSessionInternalFrame() {
        // TODO Auto-generated method stub
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

    }

    public void addMainTab(IMainPanelTab tab) {
        // TODO Auto-generated method stub

    }

    public void addToStatusBar(JComponent comp) {
        // TODO Auto-generated method stub

    }

    public void removeFromStatusBar(JComponent comp) {
        // TODO Auto-generated method stub

    }

    public String getTitle() {
        // TODO Auto-generated method stub
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

    }

    public void addSeparatorToToolbar() {
        // TODO Auto-generated method stub

    }

    public IParserEventsProcessor getParserEventsProcessor(
            IIdentifier sqlEntryPanelIdentifier) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setActiveSessionWindow(
            BaseSessionInternalFrame activeActiveSessionWindow) {
        // TODO Auto-generated method stub

    }

    public BaseSessionInternalFrame getActiveSessionWindow() {
        // TODO Auto-generated method stub
        return null;
    }

    public ISQLPanelAPI getSQLPanelAPIOfActiveSessionWindow() {
        // TODO Auto-generated method stub
        return null;
    }

    public IObjectTreeAPI getObjectTreeAPIOfActiveSessionWindow() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isfinishedLoading() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setPluginsfinishedLoading(boolean _finishedLoading) {
        // TODO Auto-generated method stub

    }

    public boolean confirmClose() {
        // TODO Auto-generated method stub
        return false;
    }

    public IIdentifier getIdentifier() {
        // TODO Auto-generated method stub
        return null;
    }

}
