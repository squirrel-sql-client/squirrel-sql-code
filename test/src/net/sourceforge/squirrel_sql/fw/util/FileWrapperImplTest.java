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
package net.sourceforge.squirrel_sql.fw.util;


import static org.junit.Assert.assertEquals;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class FileWrapperImplTest extends BaseSQuirreLJUnit4TestCase
{

	FileWrapperImpl classUnderTest = null;
	
	private final static String tmpDir = System.getProperty("java.io.tmpdir");
	private final static String userHome = System.getProperty("user.home");
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new FileWrapperImpl(tmpDir);
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}
	
	@Test
	public void testGetAbsolutePath() {
		// Don't fail if the only difference is a slash on the end of the path
		if (!classUnderTest.getAbsolutePath().endsWith("\\") && tmpDir.endsWith("\\"))
		{
			assertEquals(tmpDir, classUnderTest.getAbsolutePath()+"\\");
		} else {
			assertEquals(tmpDir, classUnderTest.getAbsolutePath());
		}
	}
	
	@Test
	public void testEqualsAndHashCode() {
		FileWrapperImpl a = new FileWrapperImpl(tmpDir);
		FileWrapperImpl b = new FileWrapperImpl(tmpDir);
		FileWrapperImpl c = new FileWrapperImpl(userHome);
		FileWrapperImpl d = new FileWrapperImpl(tmpDir) {
			private static final long serialVersionUID = 1L;			
		};
		new EqualsTester(a, b, c, d);		
	}

}
