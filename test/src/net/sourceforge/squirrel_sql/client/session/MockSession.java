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

import java.sql.SQLException;

import javax.swing.Action;
import javax.swing.JComponent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.MockApplication;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.MockSQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.MockMessageHandler;
import net.sourceforge.squirrel_sql.mo.sql.MockDatabaseMetaData;

import com.mockobjects.sql.MockConnection2;

public class MockSession implements ISession {

    ISQLAliasExt sqlAlias = null;
    ISQLDriver sqlDriver = null;
    SQLConnection con = null;
    MockConnection2 mcon = null;
    MockDatabaseMetaData mdata = null;
    MockApplication app = null;
    SessionProperties props = null;
    IMessageHandler messageHandler = null;
    
    
    public MockSession() {
        sqlAlias = new MockSQLAlias();
        sqlDriver = new SQLDriver();
        app = new MockApplication();
        mcon = new MockConnection2();
        mdata = new MockDatabaseMetaData();
        mdata.setupDriverName("junit");
        mcon.setupMetaData(mdata);
        con = new SQLConnection(mcon, null);
        props = new SessionProperties();
        messageHandler = new MockMessageHandler();
    }
    
    public boolean isClosed() {
        // TODO Auto-generated method stub
        return false;
    }

    public IApplication getApplication() {
        return app;
    }

    public SQLConnection getSQLConnection() {
        return con;
    }

    public ISQLDriver getDriver() {
        // TODO Auto-generated method stub
        return null;
    }

    public ISQLAliasExt getAlias() {
        return sqlAlias;
    }

    public SessionProperties getProperties() {
        return props;
    }

    public void commit() {
        // TODO Auto-generated method stub

    }

    public void rollback() {
        // TODO Auto-generated method stub

    }

    public void close() throws SQLException {
        // TODO Auto-generated method stub

    }

    public void closeSQLConnection() throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setSessionInternalFrame(SessionInternalFrame sif) {
        // TODO Auto-generated method stub

    }

    public void reconnect() {
        // TODO Auto-generated method stub

    }

    public Object getPluginObject(IPlugin plugin, String key) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object putPluginObject(IPlugin plugin, String key, Object obj) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return null;
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
