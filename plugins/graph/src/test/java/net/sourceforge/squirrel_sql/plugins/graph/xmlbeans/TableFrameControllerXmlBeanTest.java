package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

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
import static org.junit.Assert.assertNull;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;

/**
 *   Test class for TableFrameControllerXmlBean
 */
public class TableFrameControllerXmlBeanTest extends BaseSQuirreLJUnit4TestCase {

	TableFrameControllerXmlBean classUnderTest = new TableFrameControllerXmlBean();

	@Test
	public void testGetSchema() throws Exception
	{
		classUnderTest.setSchema("aTestString");
		assertEquals("aTestString", classUnderTest.getSchema());
	}

	@Test
	public void testGetCatalog() throws Exception
	{
		classUnderTest.setCatalog("aTestString");
		assertEquals("aTestString", classUnderTest.getCatalog());
	}

	@Test
	public void testGetTablename() throws Exception
	{
		classUnderTest.setTablename("aTestString");
		assertEquals("aTestString", classUnderTest.getTablename());
	}

	@Test
	public void testGetTableFrameXmlBean() throws Exception
	{
		classUnderTest.setTableFrameXmlBean(null);
		assertEquals(null, classUnderTest.getTableFrameXmlBean());
	}

	@Test
	public void testGetColumnIfoXmlBeans() throws Exception
	{
		classUnderTest.setColumnIfoXmlBeans(null);
		assertNull(classUnderTest.getColumnIfoXmlBeans());
	}

	@Test
	public void testGetTablesExportedTo() throws Exception
	{
		classUnderTest.setTablesExportedTo(null);
		assertNull(classUnderTest.getTablesExportedTo());
	}

	@Test
	public void testGetConstraintViewXmlBeans() throws Exception
	{
		classUnderTest.setConstraintViewXmlBeans(null);
		assertNull(classUnderTest.getConstraintViewXmlBeans());
	}

	@Test
	public void testGetColumOrder() throws Exception
	{
		classUnderTest.setColumOrder(10);
		assertEquals(10, classUnderTest.getColumOrder());
	}

}
