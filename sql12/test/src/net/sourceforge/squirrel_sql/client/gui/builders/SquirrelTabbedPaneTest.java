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
package net.sourceforge.squirrel_sql.client.gui.builders;

import static org.easymock.EasyMock.expect;

import java.beans.PropertyChangeListener;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class SquirrelTabbedPaneTest extends BaseSQuirreLJUnit4TestCase
{

	SquirrelTabbedPane classUnderTest = null;
	
	EasyMockHelper mockHelper = new EasyMockHelper();
	
	SquirrelPreferences mockSquirrelPreferences = mockHelper.createMock(SquirrelPreferences.class);
	IApplication mockApplication = mockHelper.createMock(IApplication.class);
	
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testAll_useScrollableTabs()
	{
		expect(mockSquirrelPreferences.getUseScrollableTabbedPanes()).andStubReturn(true);
		mockSquirrelPreferences.addPropertyChangeListener(EasyMock.isA(PropertyChangeListener.class));
		mockSquirrelPreferences.removePropertyChangeListener(EasyMock.isA(PropertyChangeListener.class));
		
		mockHelper.replayAll();
		classUnderTest = new SquirrelTabbedPane(mockSquirrelPreferences, mockApplication);
		classUnderTest.addNotify();
		classUnderTest.removeNotify();
		mockHelper.verifyAll();
	}

	@Test
	public void testAll_noUseScrollableTabs()
	{
		expect(mockSquirrelPreferences.getUseScrollableTabbedPanes()).andStubReturn(false);
		mockSquirrelPreferences.addPropertyChangeListener(EasyMock.isA(PropertyChangeListener.class));
		mockSquirrelPreferences.removePropertyChangeListener(EasyMock.isA(PropertyChangeListener.class));
		
		mockHelper.replayAll();
		classUnderTest = new SquirrelTabbedPane(mockSquirrelPreferences, mockApplication);
		classUnderTest.addNotify();
		classUnderTest.removeNotify();
		mockHelper.verifyAll();
	}

	@Test (expected = IllegalArgumentException.class)
	public void testNullPrefs() {
		mockHelper.replayAll();
		classUnderTest = new SquirrelTabbedPane(null, mockApplication);
		mockHelper.verifyAll();
	}
}
