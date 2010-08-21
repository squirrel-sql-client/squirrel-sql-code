package net.sourceforge.squirrel_sql.plugins.mysql.util;

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

import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;

/**
 *   Test class for FieldDetails
 */
public class FieldDetailsTest extends BaseSQuirreLJUnit4TestCase {

	FieldDetails classUnderTest = new FieldDetails();

	@Test
	public void testGetDefault() throws Exception
	{
		classUnderTest.setDefault("aTestString");
		assertEquals("aTestString", classUnderTest.getDefault());
	}

	@Test
	public void testGetFieldName() throws Exception
	{
		classUnderTest.setFieldName("aTestString");
		assertEquals("aTestString", classUnderTest.getFieldName());
	}

	@Test
	public void testGetFieldType() throws Exception
	{
		classUnderTest.setFieldType("aTestString");
		assertEquals("aTestString", classUnderTest.getFieldType());
	}

	@Test
	public void testGetFieldLength() throws Exception
	{
		classUnderTest.setFieldLength("aTestString");
		assertEquals("aTestString", classUnderTest.getFieldLength());
	}

	@Test
	public void testIsUnique() throws Exception
	{
		classUnderTest.setUnique(true);
		assertEquals(true, classUnderTest.IsUnique());
	}

	@Test
	public void testIsIndex() throws Exception
	{
		classUnderTest.setIndex(true);
		assertEquals(true, classUnderTest.IsIndex());
	}

	@Test
	public void testIsPrimary() throws Exception
	{
		classUnderTest.setPrimary(true);
		assertEquals(true, classUnderTest.IsPrimary());
	}

	@Test
	public void testIsBinary() throws Exception
	{
		classUnderTest.setBinary(true);
		assertEquals(true, classUnderTest.IsBinary());
	}

	@Test
	public void testIsNotNull() throws Exception
	{
		classUnderTest.setNotNull(true);
		assertEquals(true, classUnderTest.IsNotNull());
	}

	@Test
	public void testIsUnisigned() throws Exception
	{
		classUnderTest.setUnisigned(true);
		assertEquals(true, classUnderTest.IsUnisigned());
	}

	@Test
	public void testIsAutoIncrement() throws Exception
	{
		classUnderTest.setAutoIncrement(true);
		assertEquals(true, classUnderTest.IsAutoIncrement());
	}

	@Test
	public void testIsZeroFill() throws Exception
	{
		classUnderTest.setZeroFill(true);
		assertEquals(true, classUnderTest.IsZeroFill());
	}

}
