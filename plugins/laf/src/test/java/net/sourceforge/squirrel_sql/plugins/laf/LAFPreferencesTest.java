package net.sourceforge.squirrel_sql.plugins.laf;

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
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

import utils.EasyMockHelper;

/**
 *   Test class for LAFPreferences
 */
public class LAFPreferencesTest extends BaseSQuirreLJUnit4TestCase {

	LAFPreferences classUnderTest = new LAFPreferences();

	EasyMockHelper mockHelper = new EasyMockHelper();
	
	@Test
	public void testGetId() throws Exception
	{
		classUnderTest.setId(null);
		assertNull(classUnderTest.getId());
	}

	@Test
	public void testGetLookAndFeelClassName() throws Exception
	{
		classUnderTest.setLookAndFeelClassName("aTestString");
		assertEquals("aTestString", classUnderTest.getLookAndFeelClassName());
	}

	@Test
	public void testGetMenuFontInfo() throws Exception
	{
		classUnderTest.setMenuFontInfo(null);
		assertNull(classUnderTest.getMenuFontInfo());
	}

	@Test
	public void testGetStaticFontInfo() throws Exception
	{
		classUnderTest.setStaticFontInfo(null);
		assertNull(classUnderTest.getStaticFontInfo());
	}

	@Test
	public void testGetStatusBarFontInfo() throws Exception
	{
		classUnderTest.setStatusBarFontInfo(null);
		assertNull(classUnderTest.getStatusBarFontInfo());
	}

	@Test
	public void testGetOtherFontInfo() throws Exception
	{
		classUnderTest.setOtherFontInfo(null);
		assertNull(classUnderTest.getOtherFontInfo());
	}

	@Test
	public void testIsMenuFontEnabled() throws Exception
	{
		classUnderTest.setMenuFontEnabled(true);
		assertEquals(true, classUnderTest.isMenuFontEnabled());
	}

	@Test
	public void testIsStaticFontEnabled() throws Exception
	{
		classUnderTest.setStaticFontEnabled(true);
		assertEquals(true, classUnderTest.isStaticFontEnabled());
	}

	@Test
	public void testIsStatusBarFontEnabled() throws Exception
	{
		classUnderTest.setStatusBarFontEnabled(true);
		assertEquals(true, classUnderTest.isStatusBarFontEnabled());
	}

	@Test
	public void testIsOtherFontEnabled() throws Exception
	{
		classUnderTest.setOtherFontEnabled(true);
		assertEquals(true, classUnderTest.isOtherFontEnabled());
	}

	@Test
	public void testGetIdentifier() throws Exception
	{
		assertNull(classUnderTest.getIdentifier());
	}

	@Test
	public void testGetCanLAFSetBorder() throws Exception
	{
		classUnderTest.setCanLAFSetBorder(true);
		assertEquals(true, classUnderTest.getCanLAFSetBorder());
	}

	@Test
	public void testEqualsAndHashcode() {
		IIdentifier id = mockHelper.createMock(IIdentifier.class);
		IIdentifier id2 = mockHelper.createMock(IIdentifier.class);
		
		LAFPreferences a = new LAFPreferences(id);
		LAFPreferences b = new LAFPreferences(id);
		LAFPreferences c = new LAFPreferences(id2);
		LAFPreferences d = new LAFPreferences(id) {
			private static final long serialVersionUID = 1L;
		};
		
		new EqualsTester(a, b, c, d);
	}
}
