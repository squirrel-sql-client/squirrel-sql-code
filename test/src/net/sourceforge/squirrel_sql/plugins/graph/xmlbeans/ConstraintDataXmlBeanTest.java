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

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *   Test class for ConstraintDataXmlBean
 */
public class ConstraintDataXmlBeanTest extends BaseSQuirreLJUnit4TestCase {

	ConstraintDataXmlBean classUnderTest = new ConstraintDataXmlBean();

	@Test
	public void testGetPkTableName() throws Exception
	{
		classUnderTest.setPkTableName("aTestString");
		assertEquals("aTestString", classUnderTest.getPkTableName());
	}

	@Test
	public void testGetFkTableName() throws Exception
	{
		classUnderTest.setFkTableName("aTestString");
		assertEquals("aTestString", classUnderTest.getFkTableName());
	}

	@Test
	public void testGetConstraintName() throws Exception
	{
		classUnderTest.setConstraintName("aTestString");
		assertEquals("aTestString", classUnderTest.getConstraintName());
	}

	@Test
	public void testGetColumnInfoXmlBeans() throws Exception
	{
		classUnderTest.setColumnInfoXmlBeans(null);
		assertEquals(null, classUnderTest.getColumnInfoXmlBeans());
	}

}
