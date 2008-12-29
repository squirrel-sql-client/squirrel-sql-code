package net.sourceforge.squirrel_sql.client.gui.db;

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

import com.gargoylesoftware.base.testing.EqualsTester;

/**
 *   Test class for SQLAliasSchemaDetailProperties
 */
public class SQLAliasSchemaDetailPropertiesTest extends AbstractSerializableTest {

	SQLAliasSchemaDetailProperties classUnderTest = new SQLAliasSchemaDetailProperties();

	@Before
	public void setUp() {
		super.serializableToTest = new SQLAliasSchemaDetailProperties();
	}
	
	@After
	public void tearDown() {
		super.serializableToTest = null;
	}
	
	@Test
	public void testGetSchemaName() throws Exception
	{
		classUnderTest.setSchemaName("aTestString");
		assertEquals("aTestString", classUnderTest.getSchemaName());
	}

	@Test
	public void testGetTable() throws Exception
	{
		classUnderTest.setTable(10);
		assertEquals(10, classUnderTest.getTable());
	}

	@Test
	public void testGetView() throws Exception
	{
		classUnderTest.setView(10);
		assertEquals(10, classUnderTest.getView());
	}

	@Test
	public void testGetProcedure() throws Exception
	{
		classUnderTest.setProcedure(10);
		assertEquals(10, classUnderTest.getProcedure());
	}

	@Test
	public void testHashcodeAndEquals() {
		SQLAliasSchemaDetailProperties a = new SQLAliasSchemaDetailProperties();
		a.setSchemaName("Schema1");
		SQLAliasSchemaDetailProperties b = new SQLAliasSchemaDetailProperties();
		b.setSchemaName("Schema1");
		SQLAliasSchemaDetailProperties c = new SQLAliasSchemaDetailProperties();
		c.setSchemaName("Schema2");
		SQLAliasSchemaDetailProperties d = new SQLAliasSchemaDetailProperties(){
			private static final long serialVersionUID = 1L;
		};
		d.setSchemaName("Schema1");
		
		new EqualsTester(a, b, c, d);
		
		a.setSchemaName(null);
		b.setSchemaName(null);
		d.setSchemaName(null);
		
		new EqualsTester(a, b, c, d);
	}
	
	@Test
	public void testCompareTo() {
		SQLAliasSchemaDetailProperties a = new SQLAliasSchemaDetailProperties();
		a.setSchemaName("Schema1");
		SQLAliasSchemaDetailProperties b = new SQLAliasSchemaDetailProperties();
		b.setSchemaName("Schema1");
		SQLAliasSchemaDetailProperties c = new SQLAliasSchemaDetailProperties();
		c.setSchemaName("Schema2");
		assertEquals("Schema1".compareTo("Schema1"), a.compareTo(b));
		assertEquals("Schema1".compareTo("Schema2"), a.compareTo(c));
	}
}