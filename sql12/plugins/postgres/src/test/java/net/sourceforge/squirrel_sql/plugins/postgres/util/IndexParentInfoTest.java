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
package net.sourceforge.squirrel_sql.plugins.postgres.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IndexParentInfoTest extends BaseSQuirreLJUnit4TestCase
{

	private IndexParentInfo classUnderTest = null;
	
	private ITableInfo mockTableInfo = mockHelper.createMock(ITableInfo.class);
	
	private SQLDatabaseMetaData mockSQLDatabaseMetaData = mockHelper.createMock(SQLDatabaseMetaData.class);
	
	@Before
	public void setUp() throws Exception
	{		
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public final void testGetTableInfo() throws SQLException
	{
		setupDboExpectations(mockTableInfo);
		setupSqlDatabaseMetaDataExpectations(mockSQLDatabaseMetaData);
		mockHelper.replayAll();
		classUnderTest = new IndexParentInfo(mockTableInfo, mockSQLDatabaseMetaData);
		assertNotNull(classUnderTest.getTableInfo());
		assertEquals(classUnderTest.getTableInfo(), mockTableInfo);
		mockHelper.verifyAll();
	}

}
