package net.sourceforge.squirrel_sql.plugins.derby.prefs;

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
 * Test class for DerbyPreferenceBean
 */
public class DerbyPreferenceBeanTest extends BaseSQuirreLJUnit4TestCase
{

	DerbyPreferenceBean classUnderTest = new DerbyPreferenceBean();

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
	public void testGetStatementSeparator() throws Exception
	{
		classUnderTest.setStatementSeparator("aTestString");
		assertEquals("aTestString", classUnderTest.getStatementSeparator());
	}

	@Test
	public void testGetProcedureSeparator() throws Exception
	{
		classUnderTest.setProcedureSeparator("aTestString");
		assertEquals("aTestString", classUnderTest.getProcedureSeparator());
	}

	@Test
	public void testGetLineComment() throws Exception
	{
		classUnderTest.setLineComment("aTestString");
		assertEquals("aTestString", classUnderTest.getLineComment());
	}

	@Test
	public void testIsRemoveMultiLineComments() throws Exception
	{
		classUnderTest.setRemoveMultiLineComments(true);
		assertEquals(true, classUnderTest.isRemoveMultiLineComments());
	}

	@Test
	public void testIsReadClobsFully() throws Exception
	{
		classUnderTest.setReadClobsFully(true);
		assertEquals(true, classUnderTest.isReadClobsFully());
	}

	@Test
	public void testIsInstallCustomQueryTokenizer() throws Exception
	{
		classUnderTest.setInstallCustomQueryTokenizer(true);
		assertEquals(true, classUnderTest.isInstallCustomQueryTokenizer());
	}

	@Test
	public void testClone()
	{
		// Create a test bean to clone
		DerbyPreferenceBean bean1 = new DerbyPreferenceBean();
		bean1.setClientName("bean1");
		bean1.setClientVersion("bean1");
		bean1.setInstallCustomQueryTokenizer(true);
		bean1.setLineComment("bean1");
		bean1.setProcedureSeparator("bean1");
		bean1.setReadClobsFully(true);
		bean1.setRemoveMultiLineComments(true);
		bean1.setStatementSeparator("bean1");

		// Clone the test bean and change every bean property
		DerbyPreferenceBean bean2 = bean1.clone();
		bean2.setClientName("bean2");
		bean2.setClientVersion("bean2");
		bean2.setInstallCustomQueryTokenizer(false);
		bean2.setLineComment("bean2");
		bean2.setProcedureSeparator("bean2");
		bean2.setReadClobsFully(false);
		bean2.setRemoveMultiLineComments(false);
		bean2.setStatementSeparator("bean2");

		// verify that changing the clone didn't affect the original
		assertEquals("bean1", bean1.getClientName());
		assertEquals("bean1", bean1.getClientVersion());
		assertEquals(true, bean1.isInstallCustomQueryTokenizer());
		assertEquals("bean1", bean1.getLineComment());
		assertEquals("bean1", bean1.getProcedureSeparator());
		assertEquals(true, bean1.isReadClobsFully());
		assertEquals(true, bean1.isRemoveMultiLineComments());
		assertEquals("bean1", bean1.getStatementSeparator());
	}

}
