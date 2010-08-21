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
package net.sourceforge.squirrel_sql.plugins.dbdiff.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.sourceforge.squirrel_sql.plugins.dbdiff.util.AbstractDifference.DiffType;

import org.junit.Test;

public class AbstractDifferenceTest
{

	AbstractDifference classUnderTest = new AbstractDifference();

	@Test
	public void testGetDifferenceType() throws Exception
	{
		classUnderTest.setDifferenceType(null);
		assertNull(classUnderTest.getDifferenceType());
		for (DiffType type :AbstractDifference.DiffType.values()) {
			classUnderTest.setDifferenceType(type);
			assertEquals(type, classUnderTest.getDifferenceType());
		}
	}

	@Test
	public void testGetTableName1() throws Exception
	{
		classUnderTest.setTableName1("aTestString");
		assertEquals("aTestString", classUnderTest.getTableName1());
	}

	@Test
	public void testGetSchema1() throws Exception
	{
		classUnderTest.setSchema1("aTestString");
		assertEquals("aTestString", classUnderTest.getSchema1());
	}

	@Test
	public void testGetCatalog1() throws Exception
	{
		classUnderTest.setCatalog1("aTestString");
		assertEquals("aTestString", classUnderTest.getCatalog1());
	}

	@Test
	public void testGetCatalog2() throws Exception
	{
		classUnderTest.setCatalog2("aTestString");
		assertEquals("aTestString", classUnderTest.getCatalog2());
	}

	@Test
	public void testGetSchema2() throws Exception
	{
		classUnderTest.setSchema2("aTestString");
		assertEquals("aTestString", classUnderTest.getSchema2());
	}

	@Test
	public void testGetTableName2() throws Exception
	{
		classUnderTest.setTableName2("aTestString");
		assertEquals("aTestString", classUnderTest.getTableName2());
	}

	@Test
	public void testGetDifferenceVal1() throws Exception
	{
		classUnderTest.setDifferenceVal1(null);
		assertNull(classUnderTest.getDifferenceVal1());
	}

	@Test
	public void testGetDifferenceVal2() throws Exception
	{
		classUnderTest.setDifferenceVal2(null);
		assertNull(classUnderTest.getDifferenceVal2());
	}

}
