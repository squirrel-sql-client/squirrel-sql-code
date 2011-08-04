package net.sourceforge.squirrel_sql.fw.gui.action;

/*
 * Copyright (C) 2010 Rob Manning
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JTable;


import net.sourceforge.squirrel_sql.fw.gui.action.exportData.DataExportCSVWriter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TableExportCsvCommandTest
{

	private final String TEST_DATA = "Super \"Luxurious\" Truck";
	
	// See http://en.wikipedia.org/wiki/Comma-separated_values#Basic_rules for proper quote handling definition
	private final String TEST_DATA_PROPERLY_QUOTED = "\"Super \"\"Luxurious\"\" Truck\"";  
	
	private AbstractExportCommand classUnderTest = null;
	
	@Mock
	private JTable mockJTable;
	
	@Before
	public void setup() {
		classUnderTest = new TableExportCsvCommand(mockJTable);
	}
	
	
	@Test
	public void testGetDataCSVProperQuoteHandling() throws Exception
	{
		String separatorChar = ",";
		String result = invokeMethod(separatorChar,TEST_DATA);
		assertEquals(TEST_DATA_PROPERLY_QUOTED, result);
	}
	
	@Test
	public void testGetDataCSVProperNewLineHandling() throws Exception
	{
		String separatorChar = ",";
		String result = invokeMethod(separatorChar,"a \n n");
		assertEquals("\"a \n n\"", result);
	}
	
	@Test
	public void testGetDataCSVProperCarrageReturnHandling() throws Exception
	{
		String separatorChar = ",";
		String result = invokeMethod(separatorChar,"a \r n");
		assertEquals("\"a \r n\"", result);
	}


	private String invokeMethod(String separatorChar, String value) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		return DataExportCSVWriter.getDataCSV(separatorChar, value);
	}

}
