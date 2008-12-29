package net.sourceforge.squirrel_sql.client.update.xmlbeans;

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
import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *   Test class for ModuleXmlBean
 */
public class ModuleXmlBeanTest extends AbstractSerializableTest {

	ModuleXmlBean classUnderTest = null;

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new ModuleXmlBean();
		super.serializableToTest = new ModuleXmlBean();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
		super.serializableToTest = null;
	}
	
	
	@Test
	public void testGetName() throws Exception
	{
		classUnderTest.setName("aTestString");
		assertEquals("aTestString", classUnderTest.getName());
	}

	@Test
	public void testGetArtifacts() throws Exception
	{
		classUnderTest.setArtifacts(null);
		assertNull(classUnderTest.getArtifacts());
	}

	@Test
	public void testaddArtifact() throws Exception
	{
	}

}
