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
package net.sourceforge.squirrel_sql.client.gui.laf;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AllBluesBoldMetalThemeTest extends BaseSQuirreLJUnit4TestCase
{
	private AllBluesBoldMetalTheme classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new AllBluesBoldMetalTheme();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public final void testGetName()
	{
		assertNotNull(classUnderTest.getName());
	}

	@Test
	public final void testGetMenuItemSelectedBackground()
	{
		assertNotNull(classUnderTest.getMenuItemSelectedBackground());
	}

	@Test
	public final void testGetMenuItemSelectedForeground()
	{
		assertNotNull(classUnderTest.getMenuItemSelectedForeground());
	}

	@Test
	public final void testGetMenuSelectedBackground()
	{
		assertNotNull(classUnderTest.getMenuSelectedBackground());	
	}

	@Test
	public final void testGetPrimary1()
	{
		assertNotNull(classUnderTest.getPrimary1());	
	}

	@Test
	public final void testGetPrimary2()
	{
		assertNotNull(classUnderTest.getPrimary2());	
	}

	@Test
	public final void testGetPrimary3()
	{
		assertNotNull(classUnderTest.getPrimary3());
	}

	@Test
	public final void testGetSecondary1()
	{
		assertNotNull(classUnderTest.getSecondary1());
	}

	@Test
	public final void testGetSecondary2()
	{
		assertNotNull(classUnderTest.getSecondary2());
	}

	@Test
	public final void testGetSecondary3()
	{
		assertNotNull(classUnderTest.getSecondary3());
	}

}
