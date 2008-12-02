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
package net.sourceforge.squirrel_sql.fw.sql;

import static org.junit.Assert.assertEquals;

import java.sql.DriverPropertyInfo;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Before;
import org.junit.Test;

public class SQLDriverPropertyTest extends BaseSQuirreLJUnit4TestCase
{

	SQLDriverProperty classUnderTest = null;
	
	// Mocks
	
	DriverPropertyInfo mockDriverPropertyInfo = new DriverPropertyInfo(PROP_NAME, PROP_VALUE);

	// Data
	
	private static final String PROP_VALUE = "aPropValue";

	private static final String PROP_NAME = "aPropName";
	
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SQLDriverProperty();
	}

	@Test (expected = IllegalArgumentException.class)
	public void testSetDriverPropertyInfo()
	{
		classUnderTest.setDriverPropertyInfo(null);
	}

	@Test
	public void testGetName() throws Exception
	{
		classUnderTest.setName("aTestString");
		assertEquals("aTestString", classUnderTest.getName());
	}

	@Test
	public void testGetValue() throws Exception
	{
		classUnderTest.setValue("aTestString");
		assertEquals("aTestString", classUnderTest.getValue());
	}

	@Test
	public void testIsSpecified() throws Exception
	{
		assertEquals(false, classUnderTest.isSpecified());
	}

	@Test
	public void testGetDriverPropertyInfo() throws Exception
	{
		mockHelper.replayAll();
		classUnderTest.setName(PROP_NAME);
		classUnderTest.setDriverPropertyInfo(mockDriverPropertyInfo);
		assertEquals(mockDriverPropertyInfo, classUnderTest.getDriverPropertyInfo());
		mockHelper.verifyAll();
	}
	
}
