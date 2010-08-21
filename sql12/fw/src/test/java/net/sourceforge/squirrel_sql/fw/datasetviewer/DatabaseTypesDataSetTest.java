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
package net.sourceforge.squirrel_sql.fw.datasetviewer;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class DatabaseTypesDataSetTest extends BaseSQuirreLJUnit4TestCase
{

	private DatabaseTypesDataSet classUnderTest = null;
	
	private EasyMockHelper mockHelper = new EasyMockHelper();
	
	private ResultSet resultSet = mockHelper.createMock(ResultSet.class);
	private ResultSetMetaData resultSetMetaData = mockHelper.createMock(ResultSetMetaData.class);
	
	@Before
	public void setUp() throws Exception
	{
		expect(resultSet.getMetaData()).andStubReturn(resultSetMetaData);
	}

	@After
	public void tearDown() throws Exception
	{
		mockHelper.resetAll();
	}

	/**
	 * Test to be sure that the new Nullable column i18n labels are properly defined
	 * @throws Exception
	 */
	@Test
	public void testDatabaseTypesDataSetResultSet_Nullable() throws Exception
	{
		int[] columnIndices = new int[] { 7 };
		expect(resultSetMetaData.getColumnDisplaySize(7)).andStubReturn(7);
		expect(resultSetMetaData.getColumnLabel(7)).andStubReturn("NULLABLE");
		resultSet.close();

		expect(resultSet.next()).andReturn(true).times(3);
		expect(resultSet.next()).andReturn(false);
		expect(resultSet.getShort(7)).andReturn(DatabaseMetaData.attributeNoNulls);
		expect(resultSet.getShort(7)).andReturn(DatabaseMetaData.attributeNullable);
		expect(resultSet.getShort(7)).andReturn(DatabaseMetaData.attributeNullableUnknown);
		
		mockHelper.replayAll();
		classUnderTest = new DatabaseTypesDataSet(resultSet, columnIndices);
		classUnderTest.next(null);
		assertNotNull(classUnderTest.get(0));
		mockHelper.verifyAll();
	}
	
	/**
	 * Test to be sure that the new Searchable column i18n labels are properly defined
	 * @throws Exception
	 */
	@Test
	public void testDatabaseTypesDataSetResultSet_Searchable() throws Exception
	{
		int[] columnIndices = new int[] { 9 };
		
		expect(resultSetMetaData.getColumnDisplaySize(9)).andStubReturn(9);
		expect(resultSetMetaData.getColumnLabel(9)).andStubReturn("SEARCHABLE");
		resultSet.close();
		expect(resultSet.next()).andReturn(true).times(4);
		expect(resultSet.next()).andReturn(false);
		expect(resultSet.getShort(9)).andReturn((short)DatabaseMetaData.typePredChar);
		expect(resultSet.getShort(9)).andReturn((short)DatabaseMetaData.typePredBasic);
		expect(resultSet.getShort(9)).andReturn((short)DatabaseMetaData.typePredNone);
		expect(resultSet.getShort(9)).andReturn((short)DatabaseMetaData.typeSearchable);
		
		mockHelper.replayAll();
		classUnderTest = new DatabaseTypesDataSet(resultSet, columnIndices);
		assertEquals(1, classUnderTest.getColumnCount());
		assertNotNull(classUnderTest.getDataSetDefinition());
		
		assertTrue(classUnderTest.next(null));
		assertTrue(classUnderTest.next(null));
		assertTrue(classUnderTest.next(null));
		assertTrue(classUnderTest.next(null));
		assertFalse(classUnderTest.next(null));
		
		mockHelper.verifyAll();
	}

}
