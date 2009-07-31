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
package net.sourceforge.squirrel_sql.client.gui;

import java.awt.Component;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ScrollableDesktopPane;

import org.junit.Before;
import org.junit.Test;

public class ScrollableDesktopPaneTest extends BaseSQuirreLJUnit4TestCase
{

	ScrollableDesktopPane classUnderTest = null;
	
	// Mocks
	IApplication mockApplication = mockHelper.createMock("mockApplication", IApplication.class);
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new ScrollableDesktopPane(mockApplication);
	}

	@Test
	public void testRemoveComponent_nullarg()
	{
		mockHelper.replayAll();
		classUnderTest.remove((Component)null);
		mockHelper.verifyAll();
	}

	@Test
	public void testAddImplComponentObjectInt_nullargs()
	{
		mockHelper.replayAll();
		classUnderTest.addWidget((DialogWidget)null);
		mockHelper.verifyAll();
	}

}
