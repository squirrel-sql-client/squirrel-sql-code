/*
 * Copyright (C) 2011 Rob Manning
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

package net.sourceforge.squirrel_sql.plugins.dbdiff.gui;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.CreateScriptPreferences;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.plugins.dbdiff.IScriptFileManager;
import net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AbstractSideBySideDiffPresentationTest
{

	private AbstractSideBySideDiffPresentation classUnderTest;

	@Mock
	private IOUtilities mockIoUtilities;

	@Mock
	private FileWrapperFactory mockFileWrapperFactory;

	@Mock
	private SessionInfoProvider mockSessionInfoProvider;

	@Mock
	private IDialectFactory mockDialectFactory;

	@Mock
	private ISession mockSourceSession;

	@Mock
	private ISession mockDestSession;

	@Mock
	private HibernateDialect mockHibernateDialect;

	@Mock
	private ISQLDatabaseMetaData mockSourceDatabaseMetaData;

	@Mock
	private ISQLDatabaseMetaData mockDestDatabaseMetaData;

	@Mock
	private IScriptFileManager mockScriptFileManager;

	private ITableInfo testTableInfo1;

	private ITableInfo testTableInfo2;

	private IDatabaseObjectInfo[] mockSourceSelectedObjects = null;

	private IDatabaseObjectInfo[] mockDestSelectedObjects = null;

	@Before
	public void setUp()
	{
		classUnderTest = new AbstractSideBySideDiffPresentation()
		{
			@Override
			protected void executeDiff(String script1Filename, String script2Filename) throws Exception
			{
				// Do nothing
			}
		};

		testTableInfo1 =
			new TableInfo("testCatalog1", "testSchema1", "table1", "TABLE", "", mockSourceDatabaseMetaData);
		testTableInfo2 =
			new TableInfo("testCatalog2", "testSchema2", "table2", "TABLE", "", mockDestDatabaseMetaData);

		mockSourceSelectedObjects = new IDatabaseObjectInfo[] { testTableInfo1 };
		mockDestSelectedObjects = new IDatabaseObjectInfo[] { testTableInfo2 };

	}

	@After
	public void tearDown()
	{
		classUnderTest = null;
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testExecute() throws SQLException
	{
		classUnderTest.setFileWrapperFactory(mockFileWrapperFactory);
		classUnderTest.setIoutils(mockIoUtilities);
		classUnderTest.setSessionInfoProvider(mockSessionInfoProvider);
		classUnderTest.setDialectFactory(mockDialectFactory);

		when(mockSessionInfoProvider.getSourceSession()).thenReturn(mockSourceSession);
		when(mockSessionInfoProvider.getDestSession()).thenReturn(mockDestSession);
		when(mockSessionInfoProvider.getSourceSelectedDatabaseObjects()).thenReturn(mockSourceSelectedObjects);
		when(mockSessionInfoProvider.getDestSelectedDatabaseObjects()).thenReturn(mockDestSelectedObjects);
		when(mockSessionInfoProvider.getScriptFileManager()).thenReturn(mockScriptFileManager);

		when(mockSourceSession.getMetaData()).thenReturn(mockSourceDatabaseMetaData);
		when(mockDestSession.getMetaData()).thenReturn(mockDestDatabaseMetaData);

		when(mockDialectFactory.getDialect(mockSourceDatabaseMetaData)).thenReturn(mockHibernateDialect);
		when(mockDialectFactory.getDialect(mockDestDatabaseMetaData)).thenReturn(mockHibernateDialect);

		final List<String> createScript = new ArrayList<String>();
		createScript.add("create table test ( myid integere, mydesc varchar(100));");

		when(
			mockHibernateDialect.getCreateTableSQL(anyList(), eq(mockSourceDatabaseMetaData),
				(CreateScriptPreferences) any(), eq(false))).thenReturn(createScript);

		when(
			mockHibernateDialect.getCreateTableSQL(anyList(), eq(mockDestDatabaseMetaData),
				(CreateScriptPreferences) any(), eq(false))).thenReturn(createScript);

		classUnderTest.execute();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetFileWrapperFactory()
	{
		classUnderTest.setFileWrapperFactory(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetIoutils()
	{
		classUnderTest.setIoutils(null);
	}

}
