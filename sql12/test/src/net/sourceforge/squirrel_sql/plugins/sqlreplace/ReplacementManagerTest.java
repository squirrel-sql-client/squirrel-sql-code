/*
 * Copyright (C) 2008 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.sqlreplace;


import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;


import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.expect;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class ReplacementManagerTest extends BaseSQuirreLJUnit4TestCase
{

	private ReplacementManager classUnderTest = null;
	
	private EasyMockHelper mockHelper = new EasyMockHelper();
	
	private MockSQLReplacePluginStub mockPlugin = null;
	
	private IApplication mockApplication = mockHelper.createMock("mockApplication", IApplication.class);
	
	private IMessageHandler mockMessageHandler = mockHelper.createMock("mockMessageHandler", IMessageHandler.class);
	
	@Before
	public void setUp() throws Exception
	{
		mockPlugin = new MockSQLReplacePluginStub(mockApplication);
		expect(mockApplication.getMessageHandler()).andStubReturn(mockMessageHandler);
		mockMessageHandler.showMessage(EasyMock.isA(String.class));
		expectLastCall().anyTimes();
	}

	@After
	public void tearDown() throws Exception
	{
		File f = new File("sqlreplacement.xml");
		if (f.exists()) {
			f.delete();
		}
	}

	@Test
	public void testReplaceIllegalCharsInReplaceStr() throws Exception
	{
		mockHelper.replayAll();
		classUnderTest = new ReplacementManager(mockPlugin);
		
		classUnderTest.setContentFromEditor("$P{StartDate} = '2008-01-01'\n");
		
		StringBuffer testSql = new StringBuffer("select $P{StartDate} from dual");
		
		String result = classUnderTest.replace(testSql);
		
		assertEquals("select '2008-01-01' from dual", result);
		
		mockHelper.verifyAll();
	}
	
	
	@Test
	public void testReplaceTableName() throws Exception {
		mockHelper.replayAll();
		classUnderTest = new ReplacementManager(mockPlugin);
		
		classUnderTest.setContentFromEditor("bigint_type_tbl = bigint_type_table\n");
		
		StringBuffer testSql = new StringBuffer("SELECT * FROM bigint_type_tbl");
		
		String result = classUnderTest.replace(testSql);
		
		assertEquals("SELECT * FROM bigint_type_table", result);
		
		mockHelper.verifyAll();		
	}
	
	/**
	 * This class exists simply because I was having difficulty setting an expectation to return 
	 * mockApplication from the mock SQLReplacePlugin (which is a class - not an interface; a might be why 
	 * I was having trouble.)  Anyway, I gave up trying to use an EasyMock mock for this. 
	 */
	private class MockSQLReplacePluginStub extends SQLReplacePlugin {
		
		public MockSQLReplacePluginStub(IApplication app) throws PluginException {
			super.load(app);
		}

		/**
		 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getPluginAppSettingsFolder()
		 */
		@Override
		public synchronized File getPluginAppSettingsFolder() throws IllegalStateException, IOException
		{
			return new File(".");
		}

		/**
		 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#getPluginUserSettingsFolder()
		 */
		@Override
		public synchronized File getPluginUserSettingsFolder() throws IllegalStateException, IOException
		{
			return new File(".");
		}
		
		
	}
	
}
