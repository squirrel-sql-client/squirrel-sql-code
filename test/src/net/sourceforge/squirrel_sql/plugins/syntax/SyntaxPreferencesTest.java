package net.sourceforge.squirrel_sql.plugins.syntax;

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



import static org.junit.Assert.assertEquals;

import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *   Test class for SyntaxPreferences
 */
public class SyntaxPreferencesTest extends AbstractSerializableTest {

	SyntaxPreferences classUnderTest = new SyntaxPreferences();

	SyntaxStyle testStyle = new SyntaxStyle();
	
	@Before
	public void setUp() {
		super.serializableToTest = new SyntaxPreferences();
	}
	
	@After
	public void tearDown() {
		super.serializableToTest = null;
	}

	@Test
	public void testGetUseOsterTextControl() throws Exception
	{
		classUnderTest.setUseOsterTextControl(true);
		assertEquals(true, classUnderTest.getUseOsterTextControl());
	}

	@Test
	public void testGetUseNetbeansTextControl() throws Exception
	{
		classUnderTest.setUseNetbeansTextControl(true);
		assertEquals(true, classUnderTest.getUseNetbeansTextControl());
	}

	@Test
	public void testGetUsePlainTextControl() throws Exception
	{
		classUnderTest.setUsePlainTextControl(true);
		assertEquals(true, classUnderTest.getUsePlainTextControl());
	}

	@Test
	public void testIsTextLimitLineVisible() throws Exception
	{
		classUnderTest.setTextLimitLineVisible(true);
		assertEquals(true, classUnderTest.isTextLimitLineVisible());
	}

	@Test
	public void testGetTextLimitLineWidth() throws Exception
	{
		classUnderTest.setTextLimitLineWidth(10);
		assertEquals(10, classUnderTest.getTextLimitLineWidth());
	}

	@Test
	public void testGetCommentStyle() throws Exception
	{
		classUnderTest.setCommentStyle(testStyle);
		assertEquals(testStyle, classUnderTest.getCommentStyle());
	}

	@Test
	public void testGetDataTypeStyle() throws Exception
	{
		classUnderTest.setDataTypeStyle(testStyle);
		assertEquals(testStyle, classUnderTest.getDataTypeStyle());
	}

	@Test
	public void testGetErrorStyle() throws Exception
	{
		classUnderTest.setErrorStyle(testStyle);
		assertEquals(testStyle, classUnderTest.getErrorStyle());
	}

	@Test
	public void testGetFunctionStyle() throws Exception
	{
		classUnderTest.setFunctionStyle(testStyle);
		assertEquals(testStyle, classUnderTest.getFunctionStyle());
	}

	@Test
	public void testGetIdentifierStyle() throws Exception
	{
		classUnderTest.setIdentifierStyle(testStyle);
		assertEquals(testStyle, classUnderTest.getIdentifierStyle());
	}

	@Test
	public void testGetLiteralStyle() throws Exception
	{
		classUnderTest.setLiteralStyle(testStyle);
		assertEquals(testStyle, classUnderTest.getLiteralStyle());
	}

	@Test
	public void testGetTableStyle() throws Exception
	{
		classUnderTest.setTableStyle(testStyle);
		assertEquals(testStyle, classUnderTest.getTableStyle());
	}

	@Test
	public void testGetColumnStyle() throws Exception
	{
		classUnderTest.setColumnStyle(testStyle);
		assertEquals(testStyle, classUnderTest.getColumnStyle());
	}

	@Test
	public void testGetOperatorStyle() throws Exception
	{
		classUnderTest.setOperatorStyle(testStyle);
		assertEquals(testStyle, classUnderTest.getOperatorStyle());
	}

	@Test
	public void testGetReservedWordStyle() throws Exception
	{
		classUnderTest.setReservedWordStyle(testStyle);
		assertEquals(testStyle, classUnderTest.getReservedWordStyle());
	}

	@Test
	public void testGetSeparatorStyle() throws Exception
	{
		classUnderTest.setSeparatorStyle(testStyle);
		assertEquals(testStyle, classUnderTest.getSeparatorStyle());
	}

	@Test
	public void testGetWhiteSpaceStyle() throws Exception
	{
		classUnderTest.setWhiteSpaceStyle(testStyle);
		assertEquals(testStyle, classUnderTest.getWhiteSpaceStyle());
	}
}
