package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.sql.Date;
import java.text.DateFormat;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import org.junit.Before;
import org.junit.Test;

/*
 * Copyright (C) 2006 Rob Manning
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

/**
 * JUnit test for DataTypeDate class.
 * 
 * @author manningr
 */
public class DataTypeDateTest extends AbstractDataTypeComponentTest
{
	private static int userDefinedDateFormat = DateFormat.MEDIUM;

	@Before
	public void setUp() throws Exception
	{
		ColumnDisplayDefinition columnDisplayDefinition = getMockColumnDisplayDefinition();
		mockHelper.replayAll();
		classUnderTest = new DataTypeDate(null, columnDisplayDefinition);
		mockHelper.resetAll();
		super.setUp();
	}

	// 1757076 (DATE column seen as TIMESTAMP, update in editable mode fails)
	// We should always return false for this, when the user hasn't specified
	@Test
	public void testGetReadDateAsTimestamp()
	{
		DTProperties.put(DataTypeDate.class.getName(), "readDateAsTimestamp", "false");
		assertFalse("Expected default value to be false for read date as timestamp",
			DataTypeDate.getReadDateAsTimestamp());

		DTProperties.put(DataTypeDate.class.getName(), "readDateAsTimestamp", "true");
		assertTrue("Expected the user specified value", DataTypeDate.getReadDateAsTimestamp());
	}

	/**
	 * Ensure, that the Bug 3086444 is solved. If the user didn't choose a custom DateFormat, then we must use
	 * the default.
	 */
	@Test
	public void testUseDefaultDateFormatAfterLoadingProperties() throws Exception
	{
		resetPropertiesLoadedFlag();
		DTProperties.put(DataTypeDate.class.getName(), "useJavaDefaultFormat", "true");
		classUnderTest = new DataTypeDate(null, getMockColumnDisplayDefinition());
		Date currentDate = Date.valueOf("2010-10-15");
		String renderedDate = classUnderTest.renderObject(currentDate);
		assertEquals("Must use the default format", "2010-10-15", renderedDate);
	}

	@Test
	public void testUseCustomDateFormatAfterLoadingProperties() throws Exception
	{
		resetPropertiesLoadedFlag();
		DTProperties.put(DataTypeDate.class.getName(), "useJavaDefaultFormat", "false");
		DTProperties.put(DataTypeDate.class.getName(), "localeFormat", "" + DateFormat.MEDIUM);
		DTProperties.put(DataTypeDate.class.getName(), "lenient", "false");
		classUnderTest = new DataTypeDate(null, getMockColumnDisplayDefinition());
		
		Date currentDate = Date.valueOf("2010-10-15");
		String expectedDate = DateFormat.getDateInstance(userDefinedDateFormat).format(currentDate);
		String renderedDate = classUnderTest.renderObject(currentDate);
		assertEquals("Must use the user defined format", expectedDate, renderedDate);
	}

	@Override
	protected Object getEqualsTestObject()
	{
		return new Date(System.currentTimeMillis());
	}
	
	private void resetPropertiesLoadedFlag() throws Exception {
		Field field = DataTypeDate.class.getDeclaredField("propertiesAlreadyLoaded");
		field.setAccessible(true);
		field.set(null, false);
	}

}
