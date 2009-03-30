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
package net.sourceforge.squirrel_sql.client.action;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;

/**
 *   Test class for ActionKeys
 */
public class ActionKeysTest extends BaseSQuirreLJUnit4TestCase {

	private static final int TEST_MNEMONIC = 10;
	private static final String TEST_ACCELERATOR = "testAccelerator";
	private static final String TEST_ACTION_CLASS_NAME = "testActionClassName";
	ActionKeys classUnderTest = new ActionKeys();
	ActionKeys classUnderTest2 = new ActionKeys(TEST_ACTION_CLASS_NAME, TEST_ACCELERATOR, TEST_MNEMONIC);

	@Test
	public void testGetActionClassName() throws Exception
	{
		classUnderTest.setActionClassName("aTestString");
		assertEquals("aTestString", classUnderTest.getActionClassName());
		assertEquals(TEST_ACTION_CLASS_NAME, classUnderTest2.getActionClassName());
	}

	@Test
	public void testGetMnemonic() throws Exception
	{
		classUnderTest.setMnemonic(10);
		assertEquals(10, classUnderTest.getMnemonic());
		assertEquals(TEST_MNEMONIC, classUnderTest2.getMnemonic());
	}

	@Test
	public void testGetAccelerator() throws Exception
	{
		classUnderTest.setAccelerator("aTestString");
		assertEquals("aTestString", classUnderTest.getAccelerator());
		assertEquals(TEST_ACCELERATOR, classUnderTest2.getAccelerator());
	}

	@Test (expected = IllegalArgumentException.class)
	public void testNullActionClassName() {
		classUnderTest.setActionClassName(null);
	}
	
	@Test
	public void testNullAccelerator() {
		classUnderTest.setAccelerator(null);
		assertNotNull(classUnderTest.getAccelerator());
	}
}
