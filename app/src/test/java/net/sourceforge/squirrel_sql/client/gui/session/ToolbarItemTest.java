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
package net.sourceforge.squirrel_sql.client.gui.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.Action;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Test;

public class ToolbarItemTest extends BaseSQuirreLJUnit4TestCase
{
	ToolbarItem classUnderTest = null;

	private Action mockAction = mockHelper.createMock(Action.class);

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testActionItem()
	{
		classUnderTest = new ToolbarItem(mockAction);
		assertEquals(mockAction, classUnderTest.getAction());
		assertFalse(classUnderTest.isSeparator());
	}

	@Test
	public void testSeparatorItem()
	{
		classUnderTest = new ToolbarItem();
		assertTrue(classUnderTest.isSeparator());
	}

}
