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
import static org.junit.Assert.assertNotNull;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;

/**
 *   Test class for DatabaseFileInfo
 */
public class DatabaseFileInfoTest extends BaseSQuirreLJUnit4TestCase {

	DatabaseFileInfo classUnderTest = new DatabaseFileInfo();

	@Test
	public void testGetOwner() throws Exception
	{
		classUnderTest.setOwner("aTestString");
		assertEquals("aTestString", classUnderTest.getOwner());
	}

	@Test
	public void testGetDatabaseSize() throws Exception
	{
		classUnderTest.setDatabaseSize("aTestString");
		assertEquals("aTestString", classUnderTest.getDatabaseSize());
	}

	@Test
	public void testGetCompatibilityLevel() throws Exception
	{
		classUnderTest.setCompatibilityLevel((short)2);
		assertEquals(2, classUnderTest.getCompatibilityLevel());
	}

	@Test
	public void testGetCreatedDate() throws Exception
	{
		classUnderTest.setCreatedDate("aTestString");
		assertEquals("aTestString", classUnderTest.getCreatedDate());
	}

	@Test
	public void testGetOption() throws Exception
	{
		classUnderTest.setOption("aTestOption", "aTestValue");
		assertEquals("aTestValue", classUnderTest.getOption("aTestOption"));
	}

	@Test
	public void testGetDatabaseName() throws Exception
	{
		classUnderTest.setDatabaseName("aTestString");
		assertEquals("aTestString", classUnderTest.getDatabaseName());
	}

	@Test
	public void testGetLogFiles() throws Exception
	{
		assertNotNull(classUnderTest.getLogFiles());
		assertEquals(0, classUnderTest.getLogFiles().length);
	}

	@Test
	public void testGetDataFiles() throws Exception
	{
		assertNotNull(classUnderTest.getDataFiles());
		assertEquals(0, classUnderTest.getDataFiles().length);

	}


}
