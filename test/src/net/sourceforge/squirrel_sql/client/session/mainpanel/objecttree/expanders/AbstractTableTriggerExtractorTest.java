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
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertNotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.easymock.EasyMock;
import org.junit.Test;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ITableTriggerExtractor;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

public class AbstractTableTriggerExtractorTest extends BaseSQuirreLJUnit4TestCase
{

	protected ITableTriggerExtractor classUnderTest = null;
	private PreparedStatement mockPreparedStatement = mockHelper.createMock(PreparedStatement.class);
	private IDatabaseObjectInfo mockDatabaseObjectInfo = mockHelper.createMock(IDatabaseObjectInfo.class);

	public AbstractTableTriggerExtractorTest()
	{
		super();
	}

	@Test
	public void testBindParamters() throws SQLException
	{
		expect(mockDatabaseObjectInfo.getSchemaName()).andStubReturn(TEST_SCHEMA_NAME);
		expect(mockDatabaseObjectInfo.getCatalogName()).andStubReturn(TEST_CATALOG_NAME);
		expect(mockDatabaseObjectInfo.getSimpleName()).andStubReturn(TEST_SIMPLE_NAME);
		expect(mockDatabaseObjectInfo.getQualifiedName()).andStubReturn(TEST_QUALIFIED_NAME);
		
		mockPreparedStatement.setString(anyInt(), EasyMock.isA(String.class));
		// Should at least bind the tablename to a bind var in the SQL
		expectLastCall().atLeastOnce();
		
		mockHelper.replayAll();
		classUnderTest.bindParamters(mockPreparedStatement, mockDatabaseObjectInfo);
		mockHelper.verifyAll();
	}

	@Test
	public void testGetTableTriggerQuery()
	{
		assertNotNull(classUnderTest.getTableTriggerQuery());
	}

}