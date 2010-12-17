package net.sourceforge.squirrel_sql.client.session.action;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.IProgressCallBackFactory;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLExecuterHandler;
//import net.sourceforge.squirrel_sql.client.session.ISQLExecuterTask;
//import net.sourceforge.squirrel_sql.client.session.ISQLExecuterTaskFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
//import net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
//import net.sourceforge.squirrel_sql.fw.sql.ISQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/*
 * Copyright (C) 2010 Rob Manning
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

@RunWith(MockitoJUnitRunner.class)
public class DeleteTablesCommandTest
{

	private static final String TEST_TABLE_NAME = "TestTable1";

	private DeleteTablesCommand classUnderTest = null;

	@Mock
	private IObjectTreeAPI mockObjectTreeApi;

	@Mock
	private IProgressCallBackFactory mockProgressCallBackFactory;

//	@Mock
//	private ISQLExecuterTaskFactory mockSqlExecuterTaskFactory;

//	@Mock
//	private ProgressCallBack mockProgressCallBack;

	@Mock
	private ISession mockSession;

	@Mock
	private IApplication mockApplication;

	@Mock
	private MainFrame mockMainFrame;

	@Mock
	private TaskThreadPool mockTaskThreadPool;

//	@Mock
//	private ISQLUtilities mockSqlUtilities;

	@Mock
	private ISQLConnection mockSqlConnection;

	@Mock
	private SQLDatabaseMetaData mockSqlDatabaseMetaData;

	@Mock
	private IQueryTokenizer mockQueryTokenizer;

	@Mock
	private ITableInfo mockTableInfo;

	@Mock
	private ITableInfo mockMatViewTableInfo;

//	@Mock
//	private ISQLExecuterTask mockSqlExecuterTask;

	@Mock
	private PreparedStatement mockPreparedStatement;

	@Mock
	private ResultSet mockResultSet;

//	@Mock
//	private IDialectFactory mockDialectFactory;

	private List<ITableInfo> selectedTables = new ArrayList<ITableInfo>();

	private ArgumentCaptor<Runnable> runnableArgument = ArgumentCaptor.forClass(Runnable.class);

	private ArgumentCaptor<String> sqlArgument = ArgumentCaptor.forClass(String.class);

	@Before
	public void setUp() throws SQLException
	{
		when(mockObjectTreeApi.getSession()).thenReturn(mockSession);
		when(mockSession.getApplication()).thenReturn(mockApplication);
		when(mockSession.getSQLConnection()).thenReturn(mockSqlConnection);
		when(mockSession.getQueryTokenizer()).thenReturn(mockQueryTokenizer);
		when(mockSession.getMetaData()).thenReturn(mockSqlDatabaseMetaData);
		when(mockApplication.getMainFrame()).thenReturn(mockMainFrame);
		when(mockApplication.getThreadPool()).thenReturn(mockTaskThreadPool);
		when(mockSqlConnection.getSQLMetaData()).thenReturn(mockSqlDatabaseMetaData);
		when(mockSqlDatabaseMetaData.getDatabaseProductName()).thenReturn("testdb");
		when(mockTableInfo.getQualifiedName()).thenReturn(TEST_TABLE_NAME);

		selectedTables.add(mockTableInfo);
	}

	@After
	public void tearDown()
	{
		selectedTables.clear();
	}

	@Test
	public void testExecute() throws SQLException
	{
//		when(mockProgressCallBackFactory.create(isA(MainFrame.class), anyString(), eq(selectedTables.size()))).thenReturn(
//			mockProgressCallBack);
//		when(mockSqlUtilities.getDeletionOrder(selectedTables, mockSqlDatabaseMetaData, mockProgressCallBack)).thenReturn(
//			selectedTables);
//		when(
//			mockSqlExecuterTaskFactory.createSQLExecuterTask(eq(mockSession), anyString(),
//				(ISQLExecuterHandler) Mockito.isNull())).thenReturn(mockSqlExecuterTask);
//		when(mockSqlDatabaseMetaData.getCascadeClause()).thenReturn("CASCADE");
//
//		classUnderTest = new DeleteTablesCommand(mockObjectTreeApi, selectedTables);
//		classUnderTest.setProgressCallBackFactory(mockProgressCallBackFactory);
//		classUnderTest.setSqlExecuterTaskFactory(mockSqlExecuterTaskFactory);
//		classUnderTest.setSqlUtilities(mockSqlUtilities);
//		classUnderTest.setDialectFactory(mockDialectFactory);
//		classUnderTest.execute();
//
//		// Capture the Runnable that was given to the thread pool and call it's run method.
//		verify(mockTaskThreadPool).addTask(runnableArgument.capture());
//		Runnable deleteExecuter = runnableArgument.getValue();
//		deleteExecuter.run();
	}

	@Test
	public void testGetCascadeClauseSqlException() throws SQLException
	{
//		when(mockProgressCallBackFactory.create(isA(MainFrame.class), anyString(), eq(selectedTables.size()))).thenReturn(
//			mockProgressCallBack);
//		when(mockSqlDatabaseMetaData.getCascadeClause()).thenThrow(new SQLException("Test Exception"));
//
//		classUnderTest = new DeleteTablesCommand(mockObjectTreeApi, selectedTables);
//		classUnderTest.setProgressCallBackFactory(mockProgressCallBackFactory);
//		classUnderTest.setSqlExecuterTaskFactory(mockSqlExecuterTaskFactory);
//		classUnderTest.setSqlUtilities(mockSqlUtilities);
//		classUnderTest.setDialectFactory(mockDialectFactory);
//		classUnderTest.execute();
//
//		// Capture the Runnable that was given to the thread pool and call it's run method.
//		verify(mockTaskThreadPool).addTask(runnableArgument.capture());
//		Runnable deleteExecuter = runnableArgument.getValue();
//		deleteExecuter.run();

	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullTreeArgument()
	{
		classUnderTest = new DeleteTablesCommand(null, selectedTables);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullTablesArgument()
	{
		classUnderTest = new DeleteTablesCommand(mockObjectTreeApi, null);
	}

	@Test
	public void testOracleSessionWithMaterializedView() throws SQLException
	{

//		final String MAT_VIEW_NAME = "matview";
//		
//		when(mockMatViewTableInfo.getSimpleName()).thenReturn(MAT_VIEW_NAME);
//		when(mockMatViewTableInfo.getQualifiedName()).thenReturn(MAT_VIEW_NAME);
//		selectedTables.add(mockMatViewTableInfo);
//
//		when(mockProgressCallBackFactory.create(isA(MainFrame.class), anyString(), eq(selectedTables.size()))).thenReturn(
//			mockProgressCallBack);
//
//		when(mockDialectFactory.isOracle(mockSqlDatabaseMetaData)).thenReturn(true);
//		when(mockSqlConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
//		when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
//		when(mockResultSet.next()).thenReturn(true).thenReturn(false);
//		when(mockResultSet.getString(1)).thenReturn(MAT_VIEW_NAME);
//
//		when(mockSqlUtilities.getDeletionOrder(selectedTables, mockSqlDatabaseMetaData, mockProgressCallBack)).thenReturn(
//			selectedTables);
//		when(
//			mockSqlExecuterTaskFactory.createSQLExecuterTask(eq(mockSession), anyString(),
//				(ISQLExecuterHandler) Mockito.isNull())).thenReturn(mockSqlExecuterTask);
//
//		classUnderTest = new DeleteTablesCommand(mockObjectTreeApi, selectedTables);
//		classUnderTest.setProgressCallBackFactory(mockProgressCallBackFactory);
//		classUnderTest.setSqlExecuterTaskFactory(mockSqlExecuterTaskFactory);
//		classUnderTest.setSqlUtilities(mockSqlUtilities);
//		classUnderTest.setDialectFactory(mockDialectFactory);
//		classUnderTest.execute();
//
//		// Capture the Runnable that was given to the thread pool and call it's run method.
//		verify(mockTaskThreadPool).addTask(runnableArgument.capture());
//		Runnable deleteExecuter = runnableArgument.getValue();
//		deleteExecuter.run();
//
//		// Capture the SQL delete statement that was generated and given to the sqlExecuterTaskFactory
//		verify(mockSqlExecuterTaskFactory).createSQLExecuterTask(eq(mockSession), sqlArgument.capture(),
//			(ISQLExecuterHandler) Mockito.isNull());
//		String deleteSql = sqlArgument.getValue();
//		Assert.assertTrue(deleteSql.contains(TEST_TABLE_NAME));
//		Assert.assertFalse(deleteSql.contains(MAT_VIEW_NAME));
//
//		verify(mockSqlUtilities).closeResultSet(mockResultSet, true);

	}

	@Test
	public void testGetDeletionOrderSqlException() throws SQLException
	{
//		when(mockProgressCallBackFactory.create(isA(MainFrame.class), anyString(), eq(selectedTables.size()))).thenReturn(
//			mockProgressCallBack);
//
//		when(
//			mockSqlExecuterTaskFactory.createSQLExecuterTask(eq(mockSession), anyString(),
//				(ISQLExecuterHandler) Mockito.isNull())).thenReturn(mockSqlExecuterTask);
//
//		when(mockSqlUtilities.getDeletionOrder(selectedTables, mockSqlDatabaseMetaData, mockProgressCallBack)).thenThrow(
//			new SQLException("Test Exception"));
//
//		classUnderTest = new DeleteTablesCommand(mockObjectTreeApi, selectedTables);
//		classUnderTest.setProgressCallBackFactory(mockProgressCallBackFactory);
//		classUnderTest.setSqlExecuterTaskFactory(mockSqlExecuterTaskFactory);
//		classUnderTest.setSqlUtilities(mockSqlUtilities);
//		classUnderTest.execute();
//
//		// Capture the Runnable that was given to the thread pool and call it's run method.
//		verify(mockTaskThreadPool).addTask(runnableArgument.capture());
//		Runnable deleteExecuter = runnableArgument.getValue();
//		deleteExecuter.run();

	}
}
