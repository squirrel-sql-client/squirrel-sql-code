package net.sourceforge.squirrel_sql.client.gui.db;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SQLAliasSchemaProperties
 */
public class SQLAliasSchemaPropertiesTest extends AbstractSerializableTest
{

	SQLAliasSchemaProperties classUnderTest = new SQLAliasSchemaProperties();

	@Before
	public void setUp() {
		super.serializableToTest = new SQLAliasSchemaProperties();
	}
	
	@After
	public void tearDown() {
		super.serializableToTest = null;
	}
	
	@Test
	public void testGetSchemaDetails() throws Exception
	{
		classUnderTest.setSchemaDetails(null);
		assertNull(classUnderTest.getSchemaDetails());
	}

	@Test
	public void testGetGlobalState() throws Exception
	{
		classUnderTest.setGlobalState(10);
		assertEquals(10, classUnderTest.getGlobalState());
	}

	@Test
	public void testIsCacheSchemaIndependentMetaData() throws Exception
	{
		classUnderTest.setCacheSchemaIndependentMetaData(true);
		assertEquals(true, classUnderTest.isCacheSchemaIndependentMetaData());
	}

	@Test
	public void testloadSchemaIndependentMetaData() throws Exception
	{
	}

	@Test
	public void testGetAllSchemaProceduresNotToBeCached() throws Exception
	{
		assertTrue(classUnderTest.getAllSchemaProceduresNotToBeCached().length == 0);
	}

	@Test
	public void testGetExpectsSomeCachedData() throws Exception
	{
		assertEquals(false, classUnderTest.getExpectsSomeCachedData());
	}

}
