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
package net.sourceforge.squirrel_sql.fw.completion.util;

import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CompletionUtilsTest extends BaseSQuirreLJUnit4TestCase
{
	
	private static final String SELECT_ID_FROM_TABLE_S_WHERE_S_ID_1 = 
		"select id\n from table s \nwhere s.id = 1";
	
	private static final String SELECT_ID_FROM_MYDB_TABLE = 
		"select id from mydb.table";

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testGetStringToParse_incomplete()
	{
		String result = CompletionUtils.getStringToParse("select");
		assertEquals("select", result);
	}
	
	
	@Test
	public void testGetStringToParse()
	{
		String result = CompletionUtils.getStringToParse(SELECT_ID_FROM_MYDB_TABLE);
		assertEquals("mydb.table", result);
		
		result = CompletionUtils.getStringToParse(SELECT_ID_FROM_TABLE_S_WHERE_S_ID_1);
		assertEquals("1", result);
	}

	@Test
	public void testGetLastSeparatorIndex()
	{
		assertEquals(14, CompletionUtils.getLastSeparatorIndex(SELECT_ID_FROM_MYDB_TABLE));
		assertEquals(37, CompletionUtils.getLastSeparatorIndex(SELECT_ID_FROM_TABLE_S_WHERE_S_ID_1));
	}

	@Test
	public void testGetStringToParsePosition()
	{
		assertEquals(14, CompletionUtils.getStringToParsePosition(SELECT_ID_FROM_MYDB_TABLE));
		assertEquals(38, CompletionUtils.getStringToParsePosition(SELECT_ID_FROM_TABLE_S_WHERE_S_ID_1));	
	}

}
