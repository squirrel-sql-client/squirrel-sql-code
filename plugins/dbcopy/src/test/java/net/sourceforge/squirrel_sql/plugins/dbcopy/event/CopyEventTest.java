package net.sourceforge.squirrel_sql.plugins.dbcopy.event;

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

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

/**
 *   Test class for CopyEvent
 */
public class CopyEventTest extends BaseSQuirreLJUnit4TestCase {

	CopyEvent classUnderTest = null;

	EasyMockHelper mockHelper = new EasyMockHelper();
	
	SessionInfoProvider mockSessionInfoProvider =  mockHelper.createMock(SessionInfoProvider.class);
	
	@Before
	public void setUp() {
		classUnderTest = new CopyEvent(mockSessionInfoProvider);
	}
	
	@After
	public void tearDown() {
		classUnderTest = null;
	}
	
	@Test
	public void testGetTableCounts() throws Exception
	{
		classUnderTest.setTableCounts(null);
		assertNull(classUnderTest.getTableCounts());
		int[] tableCounts = new int[] { 0, 1, 1 };
		classUnderTest.setTableCounts(tableCounts);
		assertEquals(tableCounts, classUnderTest.getTableCounts());
	}

	@Test
	public void testGetSessionInfoProvider() throws Exception
	{
		Assert.assertEquals(mockSessionInfoProvider, classUnderTest.getSessionInfoProvider());
	}

}
