/*
 * Copyright (C) 2009 Rob Manning
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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.PreparedStatement;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ISequenceParentExtractor;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

public class AbstractSequenceParentExtractorTest extends BaseSQuirreLJUnit4TestCase
{
	protected ISequenceParentExtractor classUnderTest = null;
	
	protected static final String TEST_LIKE_MATCH_STRING = "test like match string";
	
	// Mock Objects
	
	PreparedStatement mockPreparedStatement = mockHelper.createMock(PreparedStatement.class);
	IDatabaseObjectInfo mockDatabaseObjectInfo = mockHelper.createMock(IDatabaseObjectInfo.class);
	ObjFilterMatcher mockObjFilterMatcher = mockHelper.createMock(ObjFilterMatcher.class);

	public AbstractSequenceParentExtractorTest()
	{
		super();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testGetSequenceParentQuery()
	{
		mockHelper.replayAll();
		String sql = classUnderTest.getSequenceParentQuery();
		assertNotNull(sql);
		assertTrue(sql.length() > 0);
		mockHelper.verifyAll();
	}

	@Test
	public void testBindParameters() throws Exception
	{
		expect(mockDatabaseObjectInfo.getSchemaName()).andStubReturn(TEST_SCHEMA_NAME);
		// We could make this expectation more restrictive currently. However, we might have impls that have 
		// wildly different queries in the future.  We don't want to sacrifice flexibility to use this test
		// for a "perceived" benefit of mirroring the current implementations.
		mockPreparedStatement.setString(EasyMock.anyInt(), isA(String.class));
		EasyMock.expectLastCall().anyTimes();
		// We don't stub return because we want to verify that the object filter was actually used.
		expect(mockObjFilterMatcher.getSqlLikeMatchString()).andReturn(TEST_LIKE_MATCH_STRING);
	
		mockHelper.replayAll();
		classUnderTest.bindParameters(mockPreparedStatement, mockDatabaseObjectInfo, mockObjFilterMatcher);
		mockHelper.verifyAll();
	}

}