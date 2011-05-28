/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.gui.action;


import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import javax.swing.JTable;

import org.fest.assertions.AssertExtension;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests the selection of a full row(s). 
 * @author Stefan Willinger
 *
 */
public class TableSelectEntireRowsCommandTest {

	private JTable table;
	
	private TableSelectEntireRowsCommand classUnderTest = null;
	
	@Before
	public void setup() {
		String[][] rowData = new String[][]{
				{"Austria", "Vienna"},
				{"Italy", "Rome"},
				{"Germany", "Berlin"}
		};
		
		String[] columnNames = new String[]{"Country", "Capital"};
		
		table = new JTable(rowData, columnNames);
		classUnderTest = new TableSelectEntireRowsCommand(table);
	}
	
	@Test
	public void testSelectSingleRow() throws Exception {
		table.setRowSelectionInterval(0, 0);
		table.setColumnSelectionInterval(0, 0);
		
		classUnderTest.execute();
		
		assertEquals(0, table.getSelectedRows()[0]);
		assertEquals(2, table.getSelectedColumnCount());
		assertEquals(1, table.getSelectedRowCount());
	}
	
	@Test
	public void testSelectMultibleRows() throws Exception {
		table.setRowSelectionInterval(1, 2);
		table.setColumnSelectionInterval(1, 1);
		
		classUnderTest.execute();
		
		assertEquals(1, table.getSelectedRows()[0]);
		assertEquals(2, table.getSelectedRows()[1]);
		assertEquals(2, table.getSelectedColumnCount());
		assertEquals(2, table.getSelectedRowCount());
	}
}
