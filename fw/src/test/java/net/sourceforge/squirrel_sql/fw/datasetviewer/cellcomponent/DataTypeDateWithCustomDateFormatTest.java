package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import static org.junit.Assert.*;

import java.sql.Date;
import java.text.DateFormat;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import org.junit.Before;
import org.junit.Test;

/*
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
 * JUnit test for DataTypeDate class when using custom properties.
 * 
 * @author Stefan Willinger
 */
public class DataTypeDateWithCustomDateFormatTest extends AbstractDataTypeComponentTest
{
	
	private static int userDefinedDateFormat = DateFormat.MEDIUM;

	/**
	 * Setup the test case with user defined properties
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.AbstractDataTypeComponentTest#setUp()
	 */
	@Before
	public void setUp() throws Exception
	{
		DTProperties.put(DataTypeDate.class.getName(), "useJavaDefaultFormat", "false");
		DTProperties.put(DataTypeDate.class.getName(), "localeFormat", ""+DateFormat.MEDIUM);
		DTProperties.put(DataTypeDate.class.getName(), "lenient", "false");
		DTProperties.put(DataTypeDate.class.getName(), "readDateAsTimestamp", "true");
		
		
		ColumnDisplayDefinition columnDisplayDefinition = getMockColumnDisplayDefinition();
		mockHelper.replayAll();
		classUnderTest = new DataTypeDate(null, columnDisplayDefinition);
		mockHelper.resetAll();
		super.setUp();
	}

	/**
	 *1757076 (DATE column seen as TIMESTAMP, update in editable mode fails)
	 * Ensure that we use the user specified value
	 * */
	@Test
	public void testGetReadDateAsTimestamp()
	{
		assertTrue("Expected the user specified value",
			DataTypeDate.getReadDateAsTimestamp());
	}
	
	
	/**
	 * Ensure, that the Bug 3086444 is solved.
	 * If the user defines a custom DateFormat, then we must use this after reading the
	 * properties at startup.
	 */
	@Test
	public void testUseCustomDateFormatAfterLoadingProperties()
	{
		
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

}
