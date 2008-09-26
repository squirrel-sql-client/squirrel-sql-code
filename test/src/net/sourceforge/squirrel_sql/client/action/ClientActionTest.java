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
package net.sourceforge.squirrel_sql.client.action;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class ClientActionTest extends BaseSQuirreLJUnit4TestCase
{

	private ClientAction classUnderTest = null;
	
	private EasyMockHelper mockHelper = new EasyMockHelper();
	
	private IApplication mockApplication = mockHelper.createMock(IApplication.class);
	private SquirrelResources mockResources = mockHelper.createMock(SquirrelResources.class);
	private SquirrelPreferences mockPreferences = mockHelper.createMock(SquirrelPreferences.class);
	
	@Before
	public void setUp() throws Exception
	{
		expect(mockApplication.getResources()).andStubReturn(mockResources);
		expect(mockApplication.getSquirrelPreferences()).andStubReturn(mockPreferences);
		expect(mockPreferences.getShowColoriconsInToolbar()).andStubReturn(false);
		mockResources.setupAction(isA(Action.class), eq(false));
		expectLastCall().anyTimes();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testClientActionIApplication()
	{
		mockHelper.replayAll();
		
		classUnderTest = new ClientAction(mockApplication) {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {}
		};		
		assertEquals(mockApplication, classUnderTest.getApplication());
	}

	@Test
	public void testClientActionIApplicationResources()
	{
		mockHelper.replayAll();
		
		classUnderTest = new ClientAction(mockApplication, mockResources) {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {}
		};		
		assertEquals(mockApplication, classUnderTest.getApplication());
	}

}
