package net.sourceforge.squirrel_sql.plugins.dbdiff.util;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.plugins.dbdiff.util.AbstractDifference.DiffType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

public class TableDifferenceTest
{

	private static final String INTEGER_TYPE = "INTEGER";
	private static final String VARCHAR_TYPE = "VARCHAR";
	private static final String TEST_CATALOG_NAME = "testCatalogName";
	private static final String TEST_TABLE_NAME = "testTableName";
	private static final String TEST_SCHEMA_NAME = "testSchemaName";
	private TableDifference classUnderTest = null;
	
	@Before
	public void setUp() {
		classUnderTest = new TableDifference();
		classUnderTest.setDifferenceType(DiffType.COLUMN_TYPE);
		classUnderTest.setTableName1(TEST_TABLE_NAME);
		classUnderTest.setTableName2(TEST_TABLE_NAME);
		classUnderTest.setCatalog1(TEST_CATALOG_NAME);
		classUnderTest.setCatalog2(TEST_CATALOG_NAME);
		classUnderTest.setSchema1(TEST_SCHEMA_NAME);
		classUnderTest.setSchema2(TEST_SCHEMA_NAME);
		classUnderTest.setDifferenceVal1(VARCHAR_TYPE);
		classUnderTest.setDifferenceVal2(INTEGER_TYPE);
	}
	
	@After
	public void tearDown() {
		classUnderTest = null;
	}
	
	@Test
	public void testGetDifferenceType()
	{
		assertEquals(DiffType.COLUMN_TYPE, classUnderTest.getDifferenceType());
	}

	@Test
	public void testGetTableName1()
	{
		assertEquals(TEST_TABLE_NAME, classUnderTest.getTableName1());
	}

	@Test
	public void testGetCatalog1()
	{
		assertEquals(TEST_CATALOG_NAME, classUnderTest.getCatalog1());
	}

	@Test
	public void testGetCatalog2()
	{
		assertEquals(TEST_CATALOG_NAME, classUnderTest.getCatalog2());
	}

	@Test
	public void testGetSchema1()
	{
		assertEquals(TEST_SCHEMA_NAME, classUnderTest.getSchema1());
	}

	@Test
	public void testGetSchema2()
	{
		assertEquals(TEST_SCHEMA_NAME, classUnderTest.getSchema2());
	}

	@Test
	public void testGetTableName2()
	{
		assertEquals(TEST_TABLE_NAME, classUnderTest.getTableName2());
	}

	@Test
	public void testGetDifferenceVal1()
	{
		assertEquals(VARCHAR_TYPE, classUnderTest.getDifferenceVal1());
	}

	@Test
	public void testGetDifferenceVal2()
	{
		assertEquals(INTEGER_TYPE, classUnderTest.getDifferenceVal2());
	}

}
