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
package net.sourceforge.squirrel_sql.client.session;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.event.ISessionListener;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;
import net.sourceforge.squirrel_sql.test.AppTestUtil;
import net.sourceforge.squirrel_sql.test.TestUtil;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SessionTest extends BaseSQuirreLJUnit4TestCase
{

	Session sessionUnderTest = null;

	// Mock objects
	IApplication mockApplication = createMock(IApplication.class);

	SessionManager mockSessionManager = createMock(SessionManager.class);

	ISQLDriver mockSqlDriver = createMock(ISQLDriver.class);

	SQLAlias mockSqlAlias = null;

	SQLConnection mockSqlConnection = null;

	SquirrelPreferences mockSquirrePrefs = null;

	IIdentifier mockIidentifier = createMock(IIdentifier.class);

	IIdentifier mockSqlAliasId = createMock(IIdentifier.class);

	IIdentifier mockSqlDriverId = createMock(IIdentifier.class);

	TaskThreadPool mockTaskThreadPool = createMock(TaskThreadPool.class);

	SessionProperties props = null;

	static final String FIRST_STMT_SEP = ";";

	static final String SECOND_STMT_SEP = "FOO";

	static final String CUSTOM_STMT_SEP = "CustomSeparator";

	@Before
	public void setUp() throws Exception
	{
		// Simulate the user changing the session properties
		props = getEasyMockSessionProperties();

		mockSquirrePrefs = AppTestUtil.getEasyMockSquirrelPreferences(props);
		mockSqlAlias = AppTestUtil.getEasyMockSQLAlias(mockSqlAliasId, mockSqlDriverId);
		mockSqlConnection = TestUtil.getEasyMockSQLConnection();

		mockSessionManager.addSessionListener(isA(ISessionListener.class));
		mockSessionManager.addSessionListener(isA(ISessionListener.class));

		replay(mockSessionManager);

		expect(mockApplication.getSessionManager()).andReturn(mockSessionManager).anyTimes();
		expect(mockApplication.getSquirrelPreferences()).andReturn(mockSquirrePrefs).anyTimes();
		expect(mockApplication.getThreadPool()).andReturn(mockTaskThreadPool).anyTimes();

		replay(mockApplication);
		replay(mockSqlDriver);
		replay(mockSqlConnection);

		sessionUnderTest =
			new Session(mockApplication, mockSqlDriver, mockSqlAlias, mockSqlConnection, "user", "password",
				mockIidentifier);
	}

	@After
	public void tearDown() throws Exception
	{
		sessionUnderTest = null;
	}

	// Null tests
	@Test(expected = IllegalArgumentException.class)
	public final void testSessionNullApp()
	{
		new Session(null, mockSqlDriver, mockSqlAlias, mockSqlConnection, "user", "password", mockIidentifier);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testSessionNullDriver()
	{
		new Session(mockApplication, null, mockSqlAlias, mockSqlConnection, "user", "password", mockIidentifier);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testSessionNullAlias()
	{
		new Session(mockApplication, mockSqlDriver, null, mockSqlConnection, "user", "password",
			mockIidentifier);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testSessionNullConnection()
	{
		new Session(mockApplication, mockSqlDriver, mockSqlAlias, null, "user", "password", mockIidentifier);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testSessionNullSessionId()
	{
		new Session(mockApplication, mockSqlDriver, mockSqlAlias, mockSqlConnection, "user", "password", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testSessionNullQueryTokenizer()
	{
		sessionUnderTest.setQueryTokenizer(null);
	}

	// QueryTokenizer tests

	@Test
	public final void testGetQueryTokenizer_Default()
	{
		// These should be different since we are providing two different
		// statement separators in SessionProperties
		IQueryTokenizer qt1 = sessionUnderTest.getQueryTokenizer();

		// TODO: Temporary until we sort out a better way to do guido's fix
		// IQueryTokenizer qt2 = sessionUnderTest.getQueryTokenizer();

		assertEquals(FIRST_STMT_SEP, qt1.getSQLStatementSeparator());

		// TODO: Temporary until we sort out a better way to do guido's fix
		// assertEquals(SECOND_STMT_SEP, qt2.getSQLStatementSeparator());
	}

	@Test
	public final void testGetQueryTokenizer_Custom()
	{
		IQueryTokenizer customTokenizer = TestUtil.getEasyMockQueryTokenizer(CUSTOM_STMT_SEP, "--", true, 0);

		sessionUnderTest.setQueryTokenizer(customTokenizer);

		IQueryTokenizer retrievedTokenizer = sessionUnderTest.getQueryTokenizer();

		assertEquals(CUSTOM_STMT_SEP, retrievedTokenizer.getSQLStatementSeparator());
	}

	@Test
	public final void testGetQueryTokenizer_CustomAfterGet()
	{
		// This should be a default tokenizer which uses ";" as statement sep
		IQueryTokenizer initialTokenizer = sessionUnderTest.getQueryTokenizer();
		assertEquals(FIRST_STMT_SEP, initialTokenizer.getSQLStatementSeparator());

		IQueryTokenizer customTokenizer = TestUtil.getEasyMockQueryTokenizer(CUSTOM_STMT_SEP, "--", true, 0);

		// This should override the default tokenizer
		sessionUnderTest.setQueryTokenizer(customTokenizer);

		IQueryTokenizer retrievedTokenizer = sessionUnderTest.getQueryTokenizer();

		assertEquals(CUSTOM_STMT_SEP, retrievedTokenizer.getSQLStatementSeparator());

		// Check to ensure that the tokenizer received is not the default one that
		// should have been overridden.
		assertNotSame(initialTokenizer, retrievedTokenizer);
	}

	@Test(expected = IllegalStateException.class)
	public final void testSetQueryTokenizer()
	{
		IQueryTokenizer customTokenizer1 = TestUtil.getEasyMockQueryTokenizer(FIRST_STMT_SEP, "--", true, 0);

		IQueryTokenizer customTokenizer2 = TestUtil.getEasyMockQueryTokenizer(SECOND_STMT_SEP, "--", true, 0);

		sessionUnderTest.setQueryTokenizer(customTokenizer1);

		// this should throw an exception - should not allow multiple custom
		// tokenizers to be installed for a single session.
		sessionUnderTest.setQueryTokenizer(customTokenizer2);
	}

   private SessionProperties getEasyMockSessionProperties() {
      // Simulate the user switching the statement separator for the session
      SessionProperties result = EasyMock.createMock(SessionProperties.class);
      expect(result.getSQLStatementSeparator()).andReturn(";").once();
      expect(result.getSQLStatementSeparator()).andReturn("FOO").once();
      expect(result.getStartOfLineComment()).andReturn("--").anyTimes();
      expect(result.clone()).andReturn(result);
      expect(result.getRemoveMultiLineComment()).andReturn(true).anyTimes();
      result.setSQLStatementSeparator(isA(String.class));
      expectLastCall().anyTimes();
      result.setStartOfLineComment(isA(String.class));
      expectLastCall().anyTimes();
      result.setRemoveMultiLineComment(EasyMock.anyBoolean());
      expectLastCall().anyTimes();
      replay(result);
      return result;
  }
}
