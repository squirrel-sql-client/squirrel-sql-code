package net.sourceforge.squirrel_sql.plugins.mssql.sql.dbfile;

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
 *   Test class for DatabaseFile
 */
public class DatabaseFileTest extends BaseSQuirreLJUnit4TestCase {

	DatabaseFile classUnderTest = new DatabaseFile();

	@Test
	public void testGetName() throws Exception
	{
		classUnderTest.setName("aTestString");
		assertEquals("aTestString", classUnderTest.getName());
	}

	@Test
	public void testGetId() throws Exception
	{
		classUnderTest.setId((short)2);
		assertEquals(2, classUnderTest.getId());
	}

	@Test
	public void testGetSize() throws Exception
	{
		classUnderTest.setSize("aTestString");
		assertEquals("aTestString", classUnderTest.getSize());
	}

	@Test
	public void testGetFilename() throws Exception
	{
		classUnderTest.setFilename("aTestString");
		assertEquals("aTestString", classUnderTest.getFilename());
	}

	@Test
	public void testGetFilegroup() throws Exception
	{
		classUnderTest.setFilegroup("aTestString");
		assertEquals("aTestString", classUnderTest.getFilegroup());
	}

	@Test
	public void testGetMaxSize() throws Exception
	{
		classUnderTest.setMaxSize("aTestString");
		assertEquals("aTestString", classUnderTest.getMaxSize());
	}

	@Test
	public void testGetGrowth() throws Exception
	{
		classUnderTest.setGrowth("aTestString");
		assertEquals("aTestString", classUnderTest.getGrowth());
	}

	@Test
	public void testGetUsage() throws Exception
	{
		classUnderTest.setUsage("aTestString");
		assertEquals("aTestString", classUnderTest.getUsage());
	}

}
