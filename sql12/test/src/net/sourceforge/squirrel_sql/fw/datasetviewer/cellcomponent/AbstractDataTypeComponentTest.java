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
package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractDataTypeComponentTest extends BaseSQuirreLJUnit4TestCase
{

	protected IDataTypeComponent classUnderTest = null;

	protected ColumnDisplayDefinition mockColumnDisplayDefinition =
		mockHelper.createMock(ColumnDisplayDefinition.class);

	protected ISQLDatabaseMetaData mockMetaData = mockHelper.createMock(ISQLDatabaseMetaData.class);

	protected IToolkitBeepHelper mockBeepHelper = mockHelper.createMock(IToolkitBeepHelper.class);

	protected boolean defaultValueIsNull = false;

	protected boolean canDoFileIO = true;

	protected boolean isEditableInCell = true;

	protected boolean isEditableInPopup = true;

	/**
	 * Some DataTypeComponents require a ColumnDisplayDefinition in their constructor. This returns a mock
	 * which can be passed into the constructor.
	 * 
	 * @return
	 */
	protected ColumnDisplayDefinition getMockColumnDisplayDefinition()
	{
		ColumnDisplayDefinition columnDisplayDefinition = mockHelper.createMock(ColumnDisplayDefinition.class);
		expect(columnDisplayDefinition.isNullable()).andStubReturn(false);
		expect(columnDisplayDefinition.isSigned()).andStubReturn(false);
		expect(columnDisplayDefinition.getPrecision()).andStubReturn(10);
		expect(columnDisplayDefinition.getScale()).andStubReturn(3);
		expect(columnDisplayDefinition.getColumnSize()).andStubReturn(10);
		return columnDisplayDefinition;
	}

	@Before
	public void setUp() throws Exception
	{
		classUnderTest.setColumnDisplayDefinition(mockColumnDisplayDefinition);
		classUnderTest.setBeepHelper(mockBeepHelper);
		expect(mockColumnDisplayDefinition.getLabel()).andStubReturn("testLabel");
		expect(mockMetaData.getDatabaseProductName()).andStubReturn("testDatabaseProductName");
		expect(mockMetaData.getDatabaseProductVersion()).andStubReturn("testDatabaseProductVersion");
		mockBeepHelper.beep(isA(Component.class));
		expectLastCall().anyTimes();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testGetClassName() throws Exception
	{
		assertNotNull(classUnderTest.getClassName());
		Class.forName(classUnderTest.getClassName());
	}

	@Test
	public void testCanDoFileIO()
	{
		mockHelper.replayAll();
		if (canDoFileIO)
		{
			assertTrue(classUnderTest.canDoFileIO());
		}
		else
		{
			assertFalse(classUnderTest.canDoFileIO());
		}
		mockHelper.verifyAll();
	}

	@Test
	public void testGetDefaultValue()
	{
		mockHelper.replayAll();
		if (defaultValueIsNull)
		{
			assertNull(classUnderTest.getDefaultValue(null));
		}
		else
		{
			assertNotNull(classUnderTest.getDefaultValue(null));
		}
		mockHelper.verifyAll();
	}

	@Test
	public void testIsEditableInCell()
	{
		Object testObject = getEqualsTestObject();
		;
		mockHelper.replayAll();
		if (isEditableInCell)
		{
			assertTrue(classUnderTest.isEditableInCell(testObject));
			assertTrue(classUnderTest.isEditableInCell(null));
		}
		else
		{
			assertFalse(classUnderTest.isEditableInCell(testObject));
			assertFalse(classUnderTest.isEditableInCell(null));

		}
		mockHelper.verifyAll();
	}

	@Test
	public void testIsEditableInPopup()
	{
		Object testObject = getEqualsTestObject();
		mockHelper.replayAll();
		if (isEditableInPopup)
		{
			assertTrue(classUnderTest.isEditableInPopup(testObject));
			assertTrue(classUnderTest.isEditableInPopup(null));
		}
		else
		{
			assertFalse(classUnderTest.isEditableInPopup(testObject));
			assertFalse(classUnderTest.isEditableInPopup(null));
		}
		mockHelper.verifyAll();
	}

	@Test
	public void testNeedToReRead()
	{
		mockHelper.replayAll();
		// not necessarily the case - I'm just curious if we have any components that need to be re-read
		assertFalse(classUnderTest.needToReRead(null));
		mockHelper.verifyAll();
	}

	@Test
	public void testUseBinaryEditingPanel()
	{
		mockHelper.replayAll();
		classUnderTest.useBinaryEditingPanel();
		mockHelper.verifyAll();
	}

	@Test
	public void testAreEqual()
	{
		Object testObject = getEqualsTestObject();
		mockHelper.replayAll();
		assertFalse(classUnderTest.areEqual(testObject, null));
		mockHelper.verifyAll();

	}

	protected abstract Object getEqualsTestObject();

	@Test
	public void testTextComponents()
	{
		JTextField tf = classUnderTest.getJTextField();
		tf.setText("111111111111");
		testKeyListener(tf);
		JTextArea ta = classUnderTest.getJTextArea(null);
		ta.setText("111111111111");
		testKeyListener(ta);
	}

	public void testKeyListener(Component c)
	{
		KeyListener[] listeners = c.getKeyListeners();
		if (listeners.length > 0)
		{
			KeyListener listener = listeners[0];
			KeyEvent e = new KeyEvent(c, -1, 1111111111l, -1, -1, (char) KeyEvent.VK_ENTER);
			// Test for bug 1541154 (ArrayIndexOutOfBoundsException in DataTypeDouble)
			listener.keyTyped(e);
		}
	}

}