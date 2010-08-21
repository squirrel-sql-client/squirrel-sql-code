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
package net.sourceforge.squirrel_sql.fw.dialects;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SequencePropertyMutabilityTest
{

	SequencePropertyMutability classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SequencePropertyMutability();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testRestart()
	{
		classUnderTest.setRestart(true);
		assertTrue(classUnderTest.isRestart());
		
		classUnderTest.setRestart(false);
		assertFalse(classUnderTest.isRestart());
	}

	@Test
	public void testStartWith()
	{
		classUnderTest.setStartWith(true);
		assertTrue(classUnderTest.isStartWith());
		
		classUnderTest.setStartWith(false);
		assertFalse(classUnderTest.isStartWith());		
	}

	@Test
	public void testMinValue()
	{
		classUnderTest.setMinValue(true);
		assertTrue(classUnderTest.isMinValue());
		
		classUnderTest.setMinValue(false);
		assertFalse(classUnderTest.isMinValue());				
	}

	@Test
	public void testMaxValue()
	{
		classUnderTest.setMaxValue(true);
		assertTrue(classUnderTest.isMaxValue());
		
		classUnderTest.setMaxValue(false);
		assertFalse(classUnderTest.isMaxValue());		
	}

	@Test
	public void testCycle()
	{
		classUnderTest.setCycle(true);
		assertTrue(classUnderTest.isCycle());
		
		classUnderTest.setCycle(false);
		assertFalse(classUnderTest.isCycle());		
	}

	@Test
	public void testCache()
	{
		classUnderTest.setCache(true);
		assertTrue(classUnderTest.isCache());
		
		classUnderTest.setCache(false);
		assertFalse(classUnderTest.isCache());		
	}

}
