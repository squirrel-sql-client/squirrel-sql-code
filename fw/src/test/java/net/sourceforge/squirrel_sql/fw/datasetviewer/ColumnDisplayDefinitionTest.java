/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
import static org.junit.Assert.*;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

/**
 * Some tests for a {@link ColumnDisplayDefinition}
 * @author Stefan Willinger
 * 
 */
public class ColumnDisplayDefinitionTest extends BaseSQuirreLJUnit4TestCase {

	private ColumnDisplayDefinition classUnderTest = null;

	private EasyMockHelper mockHelper = new EasyMockHelper();

	private ResultSet resultSet = mockHelper.createMock(ResultSet.class);
	private ResultSetMetaData resultSetMetaData = mockHelper.createMock(ResultSetMetaData.class);

	@Before
	public void setUp() throws Exception {
		expect(resultSet.getMetaData()).andStubReturn(resultSetMetaData);
	}

	@After
	public void tearDown() throws Exception {
		mockHelper.resetAll();
	}

	/**
	 * Test to be sure that a {@link ColumnDisplayDefinition} can handle a
	 * {@link ResultSetMetaData} with <code>null</code> as a table name.
	 */
	@Test
	public void testNullAsTableName() throws Exception {
		expect(resultSetMetaData.getTableName(0)).andStubReturn(null);
		expect(resultSetMetaData.getColumnTypeName(0)).andStubReturn("typeName");
		expect(resultSetMetaData.getColumnLabel(0)).andStubReturn("label");
		expect(resultSetMetaData.getColumnName(0)).andStubReturn("name");
		expect(resultSetMetaData.getColumnType(0)).andStubReturn(0);
		expect(resultSetMetaData.getColumnDisplaySize(0)).andStubReturn(0);

		expect(resultSetMetaData.getPrecision(0)).andStubReturn(0);
		expect(resultSetMetaData.getScale(0)).andStubReturn(0);
		expect(resultSetMetaData.isSigned(0)).andStubReturn(false);
		expect(resultSetMetaData.isCurrency(0)).andStubReturn(false);
		expect(resultSetMetaData.isAutoIncrement(0)).andStubReturn(false);
		expect(resultSetMetaData.isNullable(0)).andStubReturn(0);

		mockHelper.replayAll();
		classUnderTest = new ColumnDisplayDefinition(resultSet, 0, DialectType.ORACLE);

		assertEquals("null:name", classUnderTest.getFullTableColumnName());
		mockHelper.verifyAll();
	}

}
