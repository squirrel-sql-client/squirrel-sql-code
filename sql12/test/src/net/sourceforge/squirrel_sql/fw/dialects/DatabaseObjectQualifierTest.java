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
package net.sourceforge.squirrel_sql.fw.dialects;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatabaseObjectQualifierTest extends BaseSQuirreLJUnit4TestCase
{

	DatabaseObjectQualifier classUnderTest = null;
	
	String catalog = "aCatalog";
	String schema = "aSchema";

	
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testDatabaseObjectQualifier()
	{
		classUnderTest = new DatabaseObjectQualifier();
		Assert.assertNull(classUnderTest.getCatalog());
		Assert.assertNull(classUnderTest.getSchema());
	}

	@Test
	public void testDatabaseObjectQualifierStringString()
	{
		classUnderTest = new DatabaseObjectQualifier(catalog, schema);
		assertEquals(catalog, classUnderTest.getCatalog());
		assertEquals(schema, classUnderTest.getSchema());
	}

	@Test
	public void testSetGetCatalog()
	{
		classUnderTest = new DatabaseObjectQualifier();
		classUnderTest.setCatalog(catalog);
		assertEquals(catalog, classUnderTest.getCatalog());
	}


	@Test
	public void testSetGetSchema()
	{
		classUnderTest = new DatabaseObjectQualifier();
		classUnderTest.setSchema(catalog);
		assertEquals(catalog, classUnderTest.getSchema());
	}

}
