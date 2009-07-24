/*
 * Copyright (C) 2009 Rob Manning
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
package net.sourceforge.squirrel_sql.client;


import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;

import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.Action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.IAllowedSchemaChecker;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.action.DeleteSelectedTablesAction;
import net.sourceforge.squirrel_sql.client.session.action.EditWhereColsAction;
import net.sourceforge.squirrel_sql.client.session.action.FilterObjectsAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshObjectTreeItemAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshSchemaInfoAction;
import net.sourceforge.squirrel_sql.client.session.action.SQLFilterAction;
import net.sourceforge.squirrel_sql.client.session.event.ISQLResultExecuterTabListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecuter;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.FwTestUtil;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;

import org.easymock.classextension.EasyMock;

/**
 * This is intended to provide helper methods to build EasyMock mocks for classes and interfaces located
 * in the App module.  Fw mocks should be located in the corresponds FwTestUtil class, where as plugins 
 * mocks should be relocated to individual plugin TestUtil helpers.
 */
public class AppTestUtil
{

	/**
	 * Calls replay by default.
	 * 
	 * @param dbName
	 * @return
	 * @throws SQLException
	 */
	public static ISession getEasyMockSession(String dbName) throws SQLException
	{
		return AppTestUtil.getEasyMockSession(dbName, true);
	}

	public static ISession getEasyMockSession(String dbName, boolean replay) throws SQLException
	{
		ISQLDatabaseMetaData md = FwTestUtil.getEasyMockSQLMetaData(dbName, "jdbc:oracle");
		ISession session = AppTestUtil.getEasyMockSession(md, replay);
		return session;
	}

	public static ISession getEasyMockSession(ISQLDatabaseMetaData md, boolean replay)
	{
		ISession session = null;
		try
		{
			ISQLConnection con = FwTestUtil.getEasyMockSQLConnection();
			session = getEasyMockSession(md, con, false);
			if (replay)
			{
				replay(session);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return session;
	}

	public static ISession getEasyMockSession(ISQLDatabaseMetaData md, ISQLConnection con, boolean replay)
	{
		ISession session = createMock(ISession.class);
		IQueryTokenizer tokenizer = FwTestUtil.getEasyMockQueryTokenizer();
		// IMessageHandler messageHandler = getEasyMockMessageHandler();

		expect(session.getMetaData()).andReturn(md).anyTimes();
		expect(session.getApplication()).andReturn(AppTestUtil.getEasyMockApplication()).anyTimes();
		expect(session.getQueryTokenizer()).andReturn(tokenizer).anyTimes();
		session.setQueryTokenizer(isA(QueryTokenizer.class));
		ISQLPanelAPI api = AppTestUtil.getEasyMockSqlPanelApi();
		expect(session.getSQLPanelAPIOfActiveSessionWindow()).andReturn(api).anyTimes();
		// expect(session.getMessageHandler()).andReturn(messageHandler).anyTimes();
		expect(session.getAlias()).andReturn(AppTestUtil.getEasyMockSqlAliasExt());
		expect(session.getIdentifier()).andReturn(FwTestUtil.getEasyMockIdentifier()).anyTimes();
		expect(session.getSQLConnection()).andReturn(con).anyTimes();
		session.setExceptionFormatter(isA(ExceptionFormatter.class));
		expectLastCall().anyTimes();
		session.addSeparatorToToolbar();
		expectLastCall().anyTimes();
		SessionPanel panel = AppTestUtil.getEasyMockSessionPanel();
		expect(session.getSessionSheet()).andReturn(panel).anyTimes();
		session.addToToolbar(isA(Action.class));
		expectLastCall().anyTimes();
		SessionInternalFrame frame = AppTestUtil.getEasyMockSessionInternalFrame();
		expect(session.getSessionInternalFrame()).andReturn(frame).anyTimes();

		if (replay)
		{
			replay(session);
		}
		return session;
	}

	public static SessionPanel getEasyMockSessionPanel()
	{
		SessionPanel result = createMock(SessionPanel.class);
		ISQLPanelAPI api = AppTestUtil.getEasyMockSqlPanelApi();
		expect(result.getSQLPaneAPI()).andReturn(api);
		ObjectTreePanel mockObjTreePanel = createMock(ObjectTreePanel.class);
		expect(result.getObjectTreePanel()).andStubReturn(mockObjTreePanel);
		mockObjTreePanel.addExpander(isA(DatabaseObjectType.class), isA(INodeExpander.class));
		expectLastCall().anyTimes();
		mockObjTreePanel.addDetailTab(isA(DatabaseObjectType.class), isA(IObjectTab.class));
		expectLastCall().anyTimes();
		replay(mockObjTreePanel);
		replay(result);
		return result;
	}

	public static ISQLPanelAPI getEasyMockSqlPanelApi()
	{
		ISQLPanelAPI result = createMock(ISQLPanelAPI.class);
		ISQLEntryPanel panel = AppTestUtil.getEasyMockSqlEntryPanel();
		expect(result.getSQLEntryPanel()).andReturn(panel).anyTimes();
		result.addExecuterTabListener(isA(ISQLResultExecuterTabListener.class));
		expectLastCall().anyTimes();
		result.addExecutor(isA(ISQLResultExecuter.class));
		expectLastCall().anyTimes();
		replay(result);
		return result;
	}

	public static ISQLEntryPanel getEasyMockSqlEntryPanel()
	{
		ISQLEntryPanel result = createMock(ISQLEntryPanel.class);
		expect(result.getBoundsOfSQLToBeExecuted()).andReturn(new int[] { 10, 20 }).anyTimes();
		result.setCaretPosition(org.easymock.EasyMock.anyInt());
		expectLastCall().anyTimes();
		replay(result);
		return result;
	}

	/**
	 * Calls replay by default.
	 * 
	 * @param md
	 * @return
	 */
	public static ISession getEasyMockSession(ISQLDatabaseMetaData md) {
	   return getEasyMockSession(md, true);
	}

	public static ISession getEasyMockSession(ISQLDatabaseMetaData md,
	      ResultSet rs) throws SQLException {
	   ISQLConnection con = FwTestUtil.getEasyMockSQLConnection(rs);
	   ISession session = getEasyMockSession(md, con, false);
	   replay(session);
	   return session;
	}

	public static SessionManager getEasyMockSessionManager() {
	   SessionManager result = createMock(SessionManager.class);
	   result.addAllowedSchemaChecker(isA(IAllowedSchemaChecker.class));
	   expectLastCall().anyTimes();
	   replay(result);
	   return result;
	}

	public static ActionCollection getEasyMockActionCollection() {
	   ActionCollection result = createMock(ActionCollection.class);
	   result.add(isA(Action.class));
	   expectLastCall().anyTimes();
	   AppTestUtil.expectActionCollectionGet("refreshSchema",
	                             RefreshSchemaInfoAction.class,
	                             result);
	   AppTestUtil.expectActionCollectionGet("refreshObjectTree",
	                             RefreshObjectTreeAction.class,
	                             result);
	   AppTestUtil.expectActionCollectionGet("refreshObjectItemTree",
	                             RefreshObjectTreeItemAction.class,
	                             result);
	   AppTestUtil.expectActionCollectionGet("editWhereColsAction",
	                             EditWhereColsAction.class,
	                             result);
	   AppTestUtil.expectActionCollectionGet("SQLFilterAction",
	                             SQLFilterAction.class,
	                             result);
	   AppTestUtil.expectActionCollectionGet("DeleteSelectedTablesAction",
	                             DeleteSelectedTablesAction.class,
	                             result);
	   AppTestUtil.expectActionCollectionGet("FilterObjectsAction",
	                             FilterObjectsAction.class,
	                             result);
	   replay(result);
	   return result;
	}

	public static void expectActionCollectionGet(String actionName,
	      Class<? extends Action> actionClass, ActionCollection col) {
	   SquirrelAction action = AppTestUtil.getEasyMockSquirrelAction(actionName);
	   expect(col.get(actionClass)).andReturn(action).anyTimes();
	}

	public static SquirrelAction getEasyMockSquirrelAction(String name) {
	   SquirrelAction result = createMock(SquirrelAction.class);
	   expect(result.getValue(Action.NAME)).andReturn(name).anyTimes();
	   expect(result.getValue(Action.SMALL_ICON)).andReturn(null).anyTimes();
	   expect(result.getValue(Action.MNEMONIC_KEY)).andReturn(null).anyTimes();
	   expect(result.getValue(Action.SHORT_DESCRIPTION)).andReturn(null)
	                                                    .anyTimes();
	   expect(result.getValue(Action.ACTION_COMMAND_KEY)).andReturn(null)
	                                                     .anyTimes();
	   expect(result.getValue(Action.ACCELERATOR_KEY)).andReturn(null)
	                                                  .anyTimes();
	   expect(result.isEnabled()).andReturn(true).anyTimes();
	   expect(result.getKeyStroke()).andReturn(null).anyTimes();
	   expect(result.getValue(isA(String.class))).andStubReturn(null);
	   result.addPropertyChangeListener(isA(PropertyChangeListener.class));
	   expectLastCall().anyTimes();
	   replay(result);
	   return result;
	}

	public static ISQLAliasExt getEasyMockSqlAliasExt() {
	   ISQLAliasExt result = createMock(ISQLAliasExt.class);
	   expect(result.getName()).andReturn("TestAlias").anyTimes();
	   IIdentifier id = FwTestUtil.getEasyMockIdentifier();
	   expect(result.getDriverIdentifier()).andReturn(id).anyTimes();
	   replay(result);
	   return result;
	}

	public static ActionCollection getEasyMockActionCollection(boolean replay) {
	   ActionCollection result = createMock(ActionCollection.class);
	   if (replay) {
	      replay(result);
	   }
	   return result;
	}

	public static IApplication getEasyMockApplication() {
	   return AppTestUtil.getEasyMockApplication(true, true, null);
	}

	public static IApplication getEasyMockApplication(ActionCollection col) {
	   IApplication result = AppTestUtil.getEasyMockApplication(false, false, col);
	   replay(result);
	   return result;
	}

	public static SquirrelResources getEasyMockSquirrelResources() {
	   SquirrelResources resources = EasyMock.createMock(SquirrelResources.class);
	   resources.setupAction(isA(Action.class), EasyMock.anyBoolean());
	   EasyMock.expectLastCall().times(1, 10000);
	   replay(resources);
	   return resources;
	}

	public static SessionInternalFrame getEasyMockSessionInternalFrame() {
	   SessionInternalFrame result = createMock(SessionInternalFrame.class);
	   result.addToToolsPopUp(isA(String.class), isA(SquirrelAction.class));
	   expectLastCall().anyTimes();
	   return result;
	}

	public static SessionProperties getEasyMockSessionProperties(String sep,
	      String solComment, boolean removeMultLineComments) {
	   SessionProperties result = createMock(SessionProperties.class);
	   expect(result.getSQLStatementSeparator()).andReturn(sep).anyTimes();
	   expect(result.getStartOfLineComment()).andReturn(solComment).anyTimes();
	   expect(result.getRemoveMultiLineComment()).andReturn(removeMultLineComments)
	                                             .anyTimes();
	   expect(result.clone()).andReturn(result).anyTimes();
	   replay(result);
	   return result;
	}

	public static SquirrelPreferences getEasyMockSquirrelPreferences(
	      SessionProperties props) {
	   SquirrelPreferences prefs = createMock(SquirrelPreferences.class);
	   expect(prefs.getShowColoriconsInToolbar()).andReturn(true).anyTimes();
	   expect(prefs.getSessionProperties()).andReturn(props).anyTimes();
	   expect(prefs.getWarnJreJdbcMismatch()).andReturn(false).anyTimes();
	   replay(prefs);
	   return prefs;
	}

	public static SQLAlias getEasyMockSQLAlias(IIdentifier SqlAliasId,
	      IIdentifier SqlDriverId) {
	   SQLAlias mockSqlAlias = createMock(SQLAlias.class);
	   SQLDriverPropertyCollection mockSqlDriverPropCol = createMock(SQLDriverPropertyCollection.class);
	   expect(mockSqlAlias.getIdentifier()).andReturn(SqlAliasId).anyTimes();
	   expect(mockSqlAlias.getName()).andReturn("TestAliasName").anyTimes();
	   expect(mockSqlAlias.getDriverIdentifier()).andReturn(SqlDriverId)
	                                             .anyTimes();
	   expect(mockSqlAlias.getUrl()).andReturn("TestUrl").anyTimes();
	   expect(mockSqlAlias.getUserName()).andReturn("TestUserName").anyTimes();
	   expect(mockSqlAlias.getPassword()).andReturn("TestPassword").anyTimes();
	   expect(mockSqlAlias.isAutoLogon()).andReturn(true).anyTimes();
	   expect(mockSqlAlias.getUseDriverProperties()).andReturn(true).anyTimes();
	   expect(mockSqlAlias.getDriverPropertiesClone()).andReturn(mockSqlDriverPropCol)
	                                                  .anyTimes();
	   replay(mockSqlAlias);
	   return mockSqlAlias;
	}

	public static IApplication getEasyMockApplication(boolean nice,
	      boolean replay, ActionCollection col) {
	   IApplication result = null;
	   if (nice) {
	      result = createNiceMock(IApplication.class);
	   } else {
	      result = createMock(IApplication.class);
	   }
	   SquirrelResources resoures = getEasyMockSquirrelResources();
	   SessionProperties props = getEasyMockSessionProperties(";", "--", true);
	   SquirrelPreferences prefs = getEasyMockSquirrelPreferences(props);
	   expect(result.getMainFrame()).andReturn(null).anyTimes();
	   expect(result.getResources()).andReturn(resoures).anyTimes();
	   expect(result.getSquirrelPreferences()).andReturn(prefs).anyTimes();
	   TaskThreadPool mockThreadPool = FwTestUtil.getEasyMockTaskThreadPool();
	   expect(result.getThreadPool()).andReturn(mockThreadPool).anyTimes();
	   ActionCollection mockActColl = col;
	   if (col == null) {
	      mockActColl = getEasyMockActionCollection();
	   }
	   expect(result.getActionCollection()).andReturn(mockActColl).anyTimes();
	   SQLDriverManager driverManager = FwTestUtil.getEasyMockSQLDriverManager();
	   expect(result.getSQLDriverManager()).andReturn(driverManager).anyTimes();
	   SessionManager mockSessionManager = getEasyMockSessionManager();
	   expect(result.getSessionManager()).andReturn(mockSessionManager)
	                                     .anyTimes();
	   if (replay) {
	      replay(result);
	   }
	   return result;
	}

}
