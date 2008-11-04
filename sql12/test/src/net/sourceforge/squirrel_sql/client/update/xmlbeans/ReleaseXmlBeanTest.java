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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

/**
 *   Test class for ReleaseXmlBean
 */
public class ReleaseXmlBeanTest extends BaseSQuirreLJUnit4TestCase {

	ReleaseXmlBean classUnderTest = new ReleaseXmlBean();

	@Test
	public void testGetName() throws Exception
	{
		classUnderTest.setName("aTestString");
		assertEquals("aTestString", classUnderTest.getName());
	}

	@Test
	public void testGetLastModifiedTime() throws Exception
	{
		Date testDate = new Date();
		classUnderTest.setLastModifiedTime(testDate);
		String testDateString = classUnderTest.getLastModifiedTime();
		assertEquals(testDateString, classUnderTest.getLastModifiedTime());
		classUnderTest.setLastModifiedTime(null);
		assertNotNull(classUnderTest.getLastModifiedTime());
		assertEquals(testDateString, classUnderTest.getLastModifiedTime());
	}

	@Test
	public void testGetVersion() throws Exception
	{
		classUnderTest.setVersion("aTestString");
		assertEquals("aTestString", classUnderTest.getVersion());
	}

	@Test
	public void testGetModules() throws Exception
	{
		classUnderTest.setModules(null);
		assertNull(classUnderTest.getModules());
	}

	@Test
	public void testaddmodule() throws Exception
	{
		classUnderTest.addmodule(null);
		assertEquals(0, classUnderTest.getModules().size());
		ModuleXmlBean testModuleBean = new ModuleXmlBean();
		classUnderTest.addmodule(testModuleBean);
		assertEquals(1, classUnderTest.getModules().size());
		assertTrue(classUnderTest.getModules().contains(testModuleBean));
	}

	@Test
	public void testGetCreateTime() throws Exception
	{
		classUnderTest.setCreateTime(new Date());
		String testDateString = classUnderTest.getCreateTime();
		assertEquals(testDateString, classUnderTest.getCreateTime());
		classUnderTest.setCreateTime(null);
		assertEquals(testDateString, classUnderTest.getCreateTime());
	}

	@Test
	public void testReleaseXmlBean_String_String() throws Exception {
		String name = "aReleaseName";
		String version = "aReleaseVersion";
		classUnderTest = new ReleaseXmlBean(name, version);
		assertEquals(name, classUnderTest.getName());
		assertEquals(version, classUnderTest.getVersion());
	}
	
	@SuppressWarnings("serial")
	@Test
	public void testEqualsAndHashcode() throws Exception {
		String name = "aReleaseName";
		String version = "aReleaseVersion";
		String name2 = "aReleaseName2";
		String version2 = "aReleaseVersion2";

		
		ReleaseXmlBean a = new ReleaseXmlBean(name, version);
		
		ReleaseXmlBean b = new ReleaseXmlBean(name, version);
		ReleaseXmlBean c1 = new ReleaseXmlBean(name2, version);
		ReleaseXmlBean c2 = new ReleaseXmlBean(name, version2);
		ReleaseXmlBean c3 = new ReleaseXmlBean(null, version2);
		ReleaseXmlBean c4 = new ReleaseXmlBean(name, null);
		
		ReleaseXmlBean d = new ReleaseXmlBean() {};
			
		
		new EqualsTester(a,b,c1,d);
		new EqualsTester(a,b,c2,d);
		new EqualsTester(a,b,c3,d);
		new EqualsTester(a,b,c4,d);
		
		ReleaseXmlBean a2 = new ReleaseXmlBean(null, version);
		ReleaseXmlBean b2 = new ReleaseXmlBean(null, version);
		
		new EqualsTester(a2,b2,c1,d);
		
		a2 = new ReleaseXmlBean(name, null);
		b2 = new ReleaseXmlBean(name, null);
		
		new EqualsTester(a2,b2,c1,d);
	}
	
	@Test public void testToString() {
		assertNotNull(classUnderTest.toString());
	}
}
