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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.Action;
import javax.swing.JComponent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionTabWidget;
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
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public class MockSession implements ISession
{

	ISQLAliasExt sqlAlias = null;

	ISQLDriver sqlDriver = mock(ISQLDriver.class);

	SQLConnection con = null;

	SessionProperties props = null;

	SchemaInfo schemaInfo = null;

	SessionPanel sessionPanel = null;

	SquirrelPreferences prefs = null;

	UidIdentifier id = null;

	boolean closed;

	// These tell the Dialect test runner where tables that it creates can be found.
	private String defaultCatalog = "";

	private String defaultSchema = "";

	private ISQLPanelAPI mockPanelApi = mock(ISQLPanelAPI.class);
	
	private IApplication mockApplication = mock(IApplication.class);
	
	private SessionManager mockSessionManager = mock(SessionManager.class);

	private IMessageHandler mockMessageHandler = mock(IMessageHandler.class);	
	
	private ISQLEntryPanel mockSqlEntryPanel = mock(ISQLEntryPanel.class);
	
	public MockSession(String className, String jdbcUrl, String u, String p) throws Exception
	{
		System.out.println("Attempting to load class=" + className);
		Class.forName(className);
		System.out.println("Getting connection for url=" + jdbcUrl);
		Connection c = DriverManager.getConnection(jdbcUrl, u, p);
		initMockDriver(className, jdbcUrl);
		con = new SQLConnection(c, null, sqlDriver);
		init();
		sqlAlias.setUrl(jdbcUrl);
		sqlAlias.setUserName(u);
		sqlAlias.setPassword(p);
		sqlDriver.setDriverClassName(className);
	}

	private void initMockDriver(String className, String jdbcUrl)
	{
		when(sqlDriver.getDriverClassName()).thenReturn(className);
		when(sqlDriver.getUrl()).thenReturn(jdbcUrl);
		when(sqlDriver.getName()).thenReturn("MockitoSQLDriver");
	}

	private void init() throws SQLException
	{
		id = new UidIdentifier();
		
		props = new SessionProperties();
		props.setLoadSchemasCatalogs(false);
		
		when(mockApplication.getSessionManager()).thenReturn(mockSessionManager);
		
		when(mockSessionManager.getActiveSession()).thenReturn(this);
		when(mockSessionManager.getSession(id)).thenReturn(this);
		when(mockSessionManager.getNextSession(this)).thenReturn(this);
		when(mockSessionManager.getPreviousSession(this)).thenReturn(this);
		when(mockSessionManager.getAllowedSchemas(this)).thenReturn(con.getSQLMetaData().getSchemas());
		
		when(mockPanelApi.getSQLEntryPanel()).thenReturn(mockSqlEntryPanel);
		when(mockSqlEntryPanel.getBoundsOfSQLToBeExecuted()).thenReturn(new int[] {0,1});
		
		sqlAlias = new SQLAlias(new UidIdentifier());
		schemaInfo = new SchemaInfo(mockApplication);
		schemaInfo.initialLoad(this);
		prefs = mockApplication.getSquirrelPreferences();
		try
		{
			UIFactory.initialize(prefs, mockApplication);
		}
		catch (Throwable e)
		{

		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISession#getExceptionFormatter()
	 */
	public ExceptionFormatter getExceptionFormatter()
	{

		System.err.println("MockSession.getExceptionFormatter: stub not yet implemented");
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISession#setExceptionFormatter(net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter)
	 */
	public void setExceptionFormatter(ExceptionFormatter formatter)
	{

		System.err.println("MockSession.setExceptionFormatter: stub not yet implemented");
	}

	public boolean isClosed()
	{
		return closed;
	}

	public IApplication getApplication()
	{
		return mockApplication;
	}

	public ISQLConnection getSQLConnection()
	{
		return con;
	}

	public ISQLDriver getDriver()
	{
		return sqlDriver;
	}

	public ISQLAliasExt getAlias()
	{
		return sqlAlias;
	}

	public SessionProperties getProperties()
	{
		return props;
	}

	public void commit()
	{
		try
		{
			con.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void rollback()
	{
		try
		{
			con.rollback();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void close() throws SQLException
	{
		if (!closed)
		{
			con.close();
		}
	}

	public void closeSQLConnection() throws SQLException
	{
		con.close();
	}

	public void setSessionInternalFrame(SessionInternalFrame sif)
	{

		System.err.println("MockSession.setSessionInternalFrame: stub not yet implemented");
	}

	public void reconnect()
	{

		System.err.println("MockSession.reconnect: stub not yet implemented");
	}

	public Object getPluginObject(IPlugin plugin, String key)
	{

		System.err.println("MockSession.getPluginObject: stub not yet implemented");
		return null;
	}

	public Object putPluginObject(IPlugin plugin, String key, Object obj)
	{

		System.err.println("MockSession.putPluginObject: stub not yet implemented");
		return null;
	}

	public void removePluginObject(IPlugin plugin, String key)
	{

		System.err.println("MockSession.removePluginObject: stub not yet implemented");
	}

	public void setMessageHandler(IMessageHandler handler)
	{
		mockMessageHandler = handler;
	}

	public IMessageHandler getMessageHandler()
	{
		return mockMessageHandler;
	}

	public SessionPanel getSessionSheet()
	{
		return sessionPanel;
	}

	public SessionInternalFrame getSessionInternalFrame()
	{

		System.err.println("MockSession.getSessionInternalFrame: stub not yet implemented");
		return null;
	}

	public SchemaInfo getSchemaInfo()
	{
		return schemaInfo;
	}

	public void selectMainTab(int tabIndex) throws IllegalArgumentException
	{

		System.err.println("MockSession.selectMainTab: stub not yet implemented");
	}

	public int addMainTab(IMainPanelTab tab)
	{

		System.err.println("MockSession.addMainTab: stub not yet implemented");
		return 0;
	}

	public void addToStatusBar(JComponent comp)
	{

		System.err.println("MockSession.addToStatusBar: stub not yet implemented");
	}

	public void removeFromStatusBar(JComponent comp)
	{

		System.err.println("MockSession.removeFromStatusBar: stub not yet implemented");
	}

	public String getTitle()
	{

		System.err.println("MockSession.getTitle: stub not yet implemented");
		return null;
	}

	public String getDatabaseProductName()
	{
		String result = null;
		try
		{
			result = con.getSQLMetaData().getDatabaseProductName();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public void addToToolbar(Action action)
	{

		System.err.println("MockSession.addToToolbar: stub not yet implemented");
	}

	public void addSeparatorToToolbar()
	{

		System.err.println("MockSession.addSeparatorToToolbar: stub not yet implemented");
	}

	public IParserEventsProcessor getParserEventsProcessor(IIdentifier sqlEntryPanelIdentifier)
	{

		System.err.println("MockSession.getParserEventsProcessor: stub not yet implemented");
		return null;
	}

	public void setActiveSessionWindow(SessionTabWidget activeActiveSessionWindow)
	{

		System.err.println("MockSession.setActiveSessionWindow: stub not yet implemented");
	}

	public SessionTabWidget getActiveSessionWindow()
	{

		System.err.println("MockSession.getActiveSessionWindow: stub not yet implemented");
		return null;
	}

	public ISQLPanelAPI getSQLPanelAPIOfActiveSessionWindow()
	{
		return mockPanelApi;
	}

	public IObjectTreeAPI getObjectTreeAPIOfActiveSessionWindow()
	{

		System.err.println("MockSession.getObjectTreeAPIOfActiveSessionWindow: stub not yet implemented");
		return null;
	}

	public boolean isfinishedLoading()
	{

		System.err.println("MockSession.isfinishedLoading: stub not yet implemented");
		return true;
	}

	public void setPluginsfinishedLoading(boolean _finishedLoading)
	{

		System.err.println("MockSession.setPluginsfinishedLoading: stub not yet implemented");
	}

	public boolean confirmClose()
	{

		System.err.println("MockSession.confirmClose: stub not yet implemented");
		return false;
	}

	public IIdentifier getIdentifier()
	{
		return id;
	}

	/**
	 * @param defaultCatalog
	 *           the defaultCatalog to set
	 */
	public void setDefaultCatalog(String defaultCatalog)
	{
		this.defaultCatalog = defaultCatalog;
	}

	/**
	 * @return the defaultCatalog
	 */
	public String getDefaultCatalog()
	{
		return defaultCatalog;
	}

	/**
	 * @param defaultSchema
	 *           the defaultSchema to set
	 */
	public void setDefaultSchema(String defaultSchema)
	{
		this.defaultSchema = defaultSchema;
	}

	/**
	 * @return the defaultSchema
	 */
	public String getDefaultSchema()
	{
		return defaultSchema;
	}

	public IQueryTokenizer getQueryTokenizer()
	{
		return new QueryTokenizer(";", "--", true);
	}

	public void setQueryTokenizer(IQueryTokenizer tokenizer)
	{

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.session.ISession#getMetaData()
	 */
	public ISQLDatabaseMetaData getMetaData()
	{
		return con.getSQLMetaData();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISession#showErrorMessage(java.lang.String)
	 */
	public void showErrorMessage(String msg)
	{

		System.err.println("MockSession.showErrorMessage: stub not yet implemented");
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISession#showErrorMessage(java.lang.Throwable)
	 */
	public void showErrorMessage(Throwable th)
	{

		System.err.println("MockSession.showErrorMessage: stub not yet implemented");
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISession#showMessage(java.lang.String)
	 */
	public void showMessage(String msg)
	{

		System.err.println("MockSession.showMessage: stub not yet implemented");
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISession#showMessage(java.lang.Throwable)
	 */
	public void showMessage(Throwable th)
	{

		System.err.println("MockSession.showMessage: stub not yet implemented");
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISession#showWarningMessage(java.lang.String)
	 */
	public void showWarningMessage(String msg)
	{

		System.err.println("MockSession.showWarningMessage: stub not yet implemented");
	}

	public String formatException(Throwable th)
	{

		System.err.println("MockSession.format: stub not yet implemented");
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISession#getSelectedMainTabIndex()
	 */
	public int getSelectedMainTabIndex()
	{
		System.err.println("MockSession.getSelectedMainTabIndex: stub not yet implemented");
		return 0;
	}

	public void setActiveSessionWindow(ISessionWidget activeActiveSessionWindow)
	{
		System.err.println("MockSession.setActiveSessionWindow: stub not yet implemented");
	}

	@Override
	public SQLConnection createUnmanagedConnection()
	{
		System.err.println("MockSession.createUnmanagedConnection: stub not yet implemented");
		return null;
	}

	@Override
	public IMainPanelTab getSelectedMainTab() 
	{	
		System.err.println("MockSession.getSelectedMainTab: stub not yet implemented");
		return null;
	}

	@Override
	public boolean isSessionWidgetActive() 
	{
		System.err.println("MockSession.isSessionWidgetActive: stub not yet implemented");
		return false;
	}

	@Override
	public JdbcConnectionData getJdbcData()
	{
		System.err.println("MockSession.getJdbcData: stub not yet implemented");
		return null;
	}

	@Override
	public void setTitle(String newTitle)
	{
		System.err.println("MockSession.setTitle: stub not yet implemented");		
	}
}
