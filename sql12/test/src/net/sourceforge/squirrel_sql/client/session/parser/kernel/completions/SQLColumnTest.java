package net.sourceforge.squirrel_sql.client.session.parser.kernel.completions;

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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLCompletion;

import org.junit.Before;
import org.junit.Test;

/**
 *   Test class for SQLColumn
 */
public class SQLColumnTest extends BaseSQuirreLJUnit4TestCase {

	
	
	SQLColumn classUnderTest = null;

	// Common mocks
	
	SQLStatementContext mockStatementContext = mockHelper.createMock("mockStatementContext", SQLStatementContext.class);
	SQLStatement mockSQLStatement = mockHelper.createMock("mockSQLStatement", SQLStatement.class);
	
	@Before
	public void setup() {
		expect(mockStatementContext.getStatement()).andStubReturn(mockSQLStatement);
		expect(mockSQLStatement.getTable()).andStubReturn(null);
		classUnderTest = new SQLColumn(mockStatementContext);
	}
	
	@Test
	public void testGetName() throws Exception
	{
		mockHelper.replayAll();
		classUnderTest.setName("aTestString");
		assertEquals("aTestString", classUnderTest.getName());
	}

	@Test
	public void testIsRepeatable() throws Exception
	{
		mockHelper.replayAll();
		classUnderTest.setRepeatable(true);
		assertEquals(true, classUnderTest.isRepeatable());
	}

	@Test
	public void testGetQualifier() throws Exception
	{
		mockHelper.replayAll();
		classUnderTest.setQualifier("aTestString");
		assertEquals("aTestString", classUnderTest.getQualifier());
	}

	@Test
	public void testGetStatement() throws Exception
	{	
		mockHelper.replayAll();
		assertEquals(mockSQLStatement, classUnderTest.getStatement());
		mockHelper.verifyAll();
	}


	@Test
	public void testGetText() throws Exception
	{
		mockHelper.replayAll();
		classUnderTest.setName("aTestString");
		assertEquals("aTestString", classUnderTest.getText());
		mockHelper.verifyAll();
	}


	@Test 
	public void testGetTextInt() {
		mockHelper.replayAll();
		classUnderTest.setName(TEST_SIMPLE_NAME);
		assertNotNull(classUnderTest.getText(1));
		assertEquals(TEST_SIMPLE_NAME,classUnderTest.getText(1)); 
		mockHelper.verifyAll();
	}
	
	@Test
	public void testGetCompletions() throws Exception
	{
		mockHelper.replayAll();
		assertNotNull(classUnderTest.getCompletions(10));
		mockHelper.verifyAll();
	}

	@Test
	public void testGetLength() throws Exception
	{
		mockHelper.replayAll();
		classUnderTest.setEndPosition(10);
		
		assertEquals(12, classUnderTest.getLength());
		mockHelper.verifyAll();
	}

	@Test
	public void testhasTextPosition() throws Exception
	{
		mockHelper.replayAll();
		mockHelper.verifyAll();
	}

	@Test
	public void testGetStart() throws Exception
	{
		mockHelper.replayAll();
		assertEquals(SQLCompletion.NO_POSITION, classUnderTest.getStart());
		mockHelper.verifyAll();
	}

	@Test
	public void testIsConcrete() {
		mockHelper.replayAll();
		assertFalse(classUnderTest.isConcrete());
		classUnderTest.setName(TEST_SIMPLE_NAME);
		assertTrue(classUnderTest.isConcrete());
		mockHelper.verifyAll();
	}
}
