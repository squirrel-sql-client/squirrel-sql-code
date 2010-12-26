package net.sourceforge.squirrel_sql.plugins.example;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

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

/**
 * A simple test that uses Mockito to create a mock (IMessageHandler) that is required to construct the 
 * class that is being tested (ExampleSqlExecutionListener).  
 */
@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class ExampleSqlExecutionListenerTest
{

	/** 
	 * Class that is being tested.  This should always be called classUnderTest to distinguish it as the 
	 * thing being tested.
	 */
	private ExampleSqlExecutionListener classUnderTest = null;
	
	/**
	 * The @Mock annotation in conjunction with the MockitoJUnitRunner is pure magic.  This creates the mock
	 * automatically and injects it into this test class.  To the naked eye, mockMessageHandler appears to be
	 * uninitialized here.  However, because it's annotated with @Mock, it actually is set to a Mockito 
	 * mock implementation of IMessageHandler.
	 */
	@Mock
	private IMessageHandler mockMessageHandler;
	
	@Before
	public void setUp() {
		classUnderTest = new ExampleSqlExecutionListener(mockMessageHandler);
	}
	
	@Test
	public void testStatementExecuted()
	{
		final String sqlToTest = "select * from testtable";
		classUnderTest.statementExecuted(sqlToTest);
		
		// We just want to verify that the message handler's showMessage method was called.
		Mockito.verify(mockMessageHandler).showMessage("statementExecuted: "+sqlToTest);
	}

	@Test
	public void testStatementExecuting()
	{
		final String sqlToTest = "select * from testtable";
		String result = classUnderTest.statementExecuting(sqlToTest);
		assertEquals(sqlToTest, result);
	}

}
