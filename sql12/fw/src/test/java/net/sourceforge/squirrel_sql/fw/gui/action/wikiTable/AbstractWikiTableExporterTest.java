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
package net.sourceforge.squirrel_sql.fw.gui.action.wikiTable;

import javax.swing.JTable;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Stefan Willinger
 *
 */
public abstract class AbstractWikiTableExporterTest<T extends IWikiTableConfiguration> extends BaseSQuirreLJUnit4TestCase{
	protected T classUnderTest;
	protected JTable table;
	
	@Before
	public void setUp(){

		assertNotNull(classUnderTest);
		String[][] rowData = new String[][]{
				{"Austria", "Vienna"},
				{"Italy", "Rome"}
		};
		
		String[] columnNames = new String[]{"Country", "Capital"};
		
		table = new JTable(rowData, columnNames);
	}
	

	@Test
	public void testTransform_NoSelection() {
		IWikiTableTransformer transformer = classUnderTest.createTransformer();
		assertEquals(GenericWikiTableTransformer.class, transformer.getClass());
		String text = transformer.transform(table);
		assertNull(text);
	
	}
	
	/**
	 * Test method for {@link net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.GenericWikiTableConfigurationBean#transform(javax.swing.JTable)}.
	 */
	@Test
	public void testTransform_fullTableSelected() {
		IWikiTableTransformer transformer = classUnderTest.createTransformer();
		assertEquals(GenericWikiTableTransformer.class, transformer.getClass());
		
		table.changeSelection(0, 0, false, false);
		table.changeSelection(1, 1, true, true);
		
		String text = transformer.transform(table);

		String expected = createExpectedTextForFullSelection();
			
		assertEquals(expected, text);
	}


	@Test(expected=IllegalArgumentException.class)
	public void testHeaderCellMissingValueVariable() throws Exception {
		classUnderTest.setHeaderCell("%");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDataCellMissingValueVariable() throws Exception {
		classUnderTest.setDataCell("%");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testEscapeSequenceMissingValueVariable() throws Exception {
		classUnderTest.setNoWikiTag("%");
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testNameIsNull() throws Exception {
		classUnderTest.setName(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNameIsEmpty() throws Exception {
		classUnderTest.setName(" ");
	}
	
	public void testCopyAsUserSpecific() throws Exception {
		IWikiTableConfiguration copy = classUnderTest.copyAsUserSpecific();
		assertNotNull(copy);
		assertFalse(copy.isReadOnly());
		assertEquals(GenericWikiTableConfigurationBean.class, copy.getClass());
	}
	
	
	/**
	 * If a configuration is disabled, also the clon must be disabled.
	 */
	public void testDisabledClone() throws Exception {
		classUnderTest.setEnabled(false);
		IWikiTableConfiguration copy = classUnderTest.clone();
		assertFalse(copy.isEnabled());
	}
	
	/**
	 * Create the expected Wiki text, which represents the table, if all cells are selected.
	 * @return
	 */
	protected abstract String createExpectedTextForFullSelection();


	
}
