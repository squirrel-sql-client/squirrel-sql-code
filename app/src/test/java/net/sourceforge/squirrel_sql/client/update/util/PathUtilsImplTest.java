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
package net.sourceforge.squirrel_sql.client.update.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PathUtilsImplTest
{

	private PathUtils classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new PathUtilsImpl();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testBuildPath()
	{
		String result = classUnderTest.buildPath(true, new String[] { "a", "path", "to", "a", "file" });
		assertEquals("/a/path/to/a/file", result);
	}

	@Test
	public void testGetFileFromPath()
	{
		String result = classUnderTest.getFileFromPath("/a/path/to/a/file");
		assertEquals("file", result);
		
		result = classUnderTest.getFileFromPath("file");
		assertEquals("file", result);
	}

}
