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

import static net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType.TABLE_TYPE_DBO;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RelatedObjectInfoTest extends BaseSQuirreLJUnit4TestCase
{

	private RelatedObjectInfo classUnderTest = null;

	private IDatabaseObjectInfo mockDatabaseObjectInfo = mockHelper.createMock(IDatabaseObjectInfo.class);

	private SQLDatabaseMetaData mockMetaData = mockHelper.createMock(SQLDatabaseMetaData.class);

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testContstructor() throws SQLException 
	{
		super.setupDboExpectations(mockDatabaseObjectInfo);
		super.setupSqlDatabaseMetaDataExpectations(mockMetaData);
		mockHelper.replayAll();
		classUnderTest = new RelatedObjectInfo(
		   mockDatabaseObjectInfo, TEST_SIMPLE_NAME, TABLE_TYPE_DBO, mockMetaData);
		mockHelper.verifyAll();
		
		assertEquals(mockDatabaseObjectInfo, classUnderTest.getRelatedObjectInfo());
	}
}
