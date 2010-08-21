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
package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

import com.gargoylesoftware.base.testing.EqualsTester;

public class I18nBundleTest extends BaseSQuirreLJUnit4TestCase
{
	
	EasyMockHelper mockHelepr = new EasyMockHelper();

	I18nProps mockProps = mockHelepr.createMock(I18nProps.class);
	I18nProps mockProps2 = mockHelepr.createMock(I18nProps.class);
	
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
		mockHelepr.resetAll();
	}

	@Test
	public void testEqualsObject()
	{
		EasyMock.expect(mockProps.getName()).andStubReturn("NameOne");
		
		EasyMock.expect(mockProps2.getName()).andStubReturn("NameOne2");
		
		mockHelepr.replayAll();
		
		I18nBundle a = new I18nBundle(mockProps, null, null, null);
		I18nBundle b = new I18nBundle(mockProps, null, null, null);
		I18nBundle c = new I18nBundle(mockProps2, null, null, null);
		I18nBundle d = new I18nBundle(mockProps, null, null, null) {};
	
		new EqualsTester(a, b, c, d);
		
		mockHelepr.verifyAll();
	}

}
