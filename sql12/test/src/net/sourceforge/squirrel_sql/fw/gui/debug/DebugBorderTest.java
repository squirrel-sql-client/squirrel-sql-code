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
package net.sourceforge.squirrel_sql.fw.gui.debug;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DebugBorderTest extends BaseSQuirreLJUnit4TestCase
{

	private DebugBorder classUnderTest = null;
	
	private Border mockBorder = mockHelper.createMock(Border.class);
	private Component mockComponent = mockHelper.createMock(Component.class);
	
	// Data 
	
	private Insets insets = new Insets(1,1,1,1);
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new DebugBorder(mockBorder);
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
		mockHelper.resetAll();
	}

	@Test
	public void testPaintBorder()
	{
		Graphics g = mockHelper.createMock(Graphics.class);
		int x = 0;
		int y = 0;
		int width = 10;
		int height = 10;
		

		mockBorder.paintBorder(mockComponent, g, x, y, width, height);
		expect(mockBorder.getBorderInsets(mockComponent)).andStubReturn(insets);
		g.setColor(isA(Color.class));
		g.fillRect(anyInt(), anyInt(), anyInt(), anyInt());
		expectLastCall().anyTimes();
		
		mockHelper.replayAll();		
		classUnderTest.paintBorder(mockComponent, g, x, y, width, height);
		mockHelper.verifyAll();
	}

	@Test
	public void testGetBorderInsets()
	{

		expect(mockBorder.getBorderInsets(mockComponent)).andReturn(insets);
		
		mockHelper.replayAll();		
		assertEquals(insets, classUnderTest.getBorderInsets(mockComponent));
		mockHelper.verifyAll();
		
	}

	@Test
	public void testIsBorderOpaque()
	{
		expect(mockBorder.isBorderOpaque()).andStubReturn(true);
		
		mockHelper.replayAll();		
		assertTrue(classUnderTest.isBorderOpaque());
		mockHelper.verifyAll();
	}

	@Test
	public void testGetDelegate()
	{
		mockHelper.replayAll();		
		assertEquals(mockBorder, classUnderTest.getDelegate());
		mockHelper.verifyAll();
	}

}
