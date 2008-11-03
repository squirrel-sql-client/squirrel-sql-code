package net.sourceforge.squirrel_sql.plugins.sqlscript.prefs;

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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *   Test class for SQLScriptPreferenceBean
 */
public class SQLScriptPreferenceBeanTest extends BaseSQuirreLJUnit4TestCase {

	SQLScriptPreferenceBean classUnderTest = new SQLScriptPreferenceBean();

	@Test
	public void testGetClientName() throws Exception
	{
		classUnderTest.setClientName("aTestString");
		assertEquals("aTestString", classUnderTest.getClientName());
	}

	@Test
	public void testGetClientVersion() throws Exception
	{
		classUnderTest.setClientVersion("aTestString");
		assertEquals("aTestString", classUnderTest.getClientVersion());
	}

	@Test
	public void testIsQualifyTableNames() throws Exception
	{
		classUnderTest.setQualifyTableNames(true);
		assertEquals(true, classUnderTest.isQualifyTableNames());
	}

	@Test
	public void testIsDeleteRefAction() throws Exception
	{
		classUnderTest.setDeleteRefAction(true);
		assertEquals(true, classUnderTest.isDeleteRefAction());
	}

	@Test
	public void testGetDeleteAction() throws Exception
	{
		classUnderTest.setDeleteAction(10);
		assertEquals(10, classUnderTest.getDeleteAction());
	}

	@Test
	public void testGetUpdateAction() throws Exception
	{
		classUnderTest.setUpdateAction(10);
		assertEquals(10, classUnderTest.getUpdateAction());
	}

	@Test
	public void testIsUpdateRefAction() throws Exception
	{
		classUnderTest.setUpdateRefAction(true);
		assertEquals(true, classUnderTest.isUpdateRefAction());
	}

}
