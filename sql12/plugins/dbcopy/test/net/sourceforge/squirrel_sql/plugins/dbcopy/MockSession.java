/*
 * Copyright (C) 2005 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.Action;
import javax.swing.JComponent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SchemaInfo;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public class MockSession implements ISession {

    private SQLConnection con = null;
    
    private ISQLDriver driver = null;
    
    private SchemaInfo schemaInfo = null;
    
    private SessionProperties _props = null;
    
    public MockSession(String className, 
                       String jdbcUrl, 
                       String u, 
                       String p) throws Exception 
    {
        System.out.println("Attempting to load class="+className);
        Class.forName(className);
        Connection c = DriverManager.getConnection(jdbcUrl, u, p);
        SQLDriverPropertyCollection col = null;
        driver = new MockSQLDriver(className, jdbcUrl);
        con = new SQLConnection(c, col);
        _props = new SessionProperties();
        schemaInfo = new SchemaInfo(null);
        schemaInfo.load(this);
    }
    
    public boolean isClosed() {
        // TODO Auto-generated method stub
        System.err.println("MockSession.isClosed: stub not yet implemented");
        return false;
    }

    public IApplication getApplication() {
        // TODO Auto-generated method stub
        System.err.println("MockSession.getApplication: stub not yet implemented");
        return null;
    }

    public SQLConnection getSQLConnection() {
        return con;
    }

    public ISQLDriver getDriver() {
        return driver;
    }

    public ISQLAlias getAlias() {
        // TODO Auto-generated method stub
        System.err.println("MockSession.getAlias: stub not yet implemented");
        return null;
    }

    public SessionProperties getProperties() {
        return _props;
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
        con.close();
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
        // TODO Auto-generated method stub

    }

    public IMessageHandler getMessageHandler() {
        // TODO Auto-generated method stub
        return null;
    }

    public SessionPanel getSessionSheet() {
        // TODO Auto-generated method stub
        return null;
    }

    public SessionInternalFrame getSessionInternalFrame() {
        // TODO Auto-generated method stub
        return null;
    }

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

    public IIdentifier getIdentifier() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.session.ISession#getDatabaseProductName()
     */
    public String getDatabaseProductName() {
        String result = null;
        try {
        result = con.getSQLMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.session.ISession#isfinishedLoading()
     */
    public boolean isfinishedLoading() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.session.ISession#setPluginsfinishedLoading(boolean)
     */
    public void setPluginsfinishedLoading(boolean _finishedLoading) {
        // TODO Auto-generated method stub
        
    }

}
