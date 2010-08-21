package net.sourceforge.squirrel_sql.plugins.refactoring.prefs;

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
 *   Test class for RefactoringPreferenceBean
 */
public class RefactoringPreferenceBeanTest extends BaseSQuirreLJUnit4TestCase {

	RefactoringPreferenceBean classUnderTest = new RefactoringPreferenceBean();

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
	public void testIsQuoteIdentifiers() throws Exception
	{
		classUnderTest.setQuoteIdentifiers(true);
		assertEquals(true, classUnderTest.isQuoteIdentifiers());
	}

}
