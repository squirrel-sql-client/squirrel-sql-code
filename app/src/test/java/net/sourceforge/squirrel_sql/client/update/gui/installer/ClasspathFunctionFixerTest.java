package net.sourceforge.squirrel_sql.client.update.gui.installer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import net.sourceforge.squirrel_sql.fw.util.IOUtilities;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/*
 * Copyright (C) 2010 Rob Manning
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

public class ClasspathFunctionFixerTest
{

	private ClasspathFunctionFixer classUnderTest = null;
	
	private static final String WINDOWS_BATCH_FILE_NAME = "squirrel-sql.bat";
	private static final String SHELL_FILE_NAME = "squirrel-sql.sh";
	
	private static final String[] SCRIPT_THAT_CONTAINS_BAD_FUNCTION = new String[] {
		"#! /bin/sh",
		"",
		"buildCPFromDir()",
		"{",
		"if [ -d \"$1\"/lib ]; then"
	};

	private static final String[] SCRIPT_THAT_CONTAINS_GOOD_FUNCTION = new String[] {
		"#! /bin/sh",
		"",
		"buildCPFromDir()",
		"{",
		"CP=\"\"",
		"if [ -d \"$1\"/lib ]; then"
	};

	@Before
	public void setUp() {
		classUnderTest = new ClasspathFunctionFixer();
	}
	
	@After
	public void tearDown() {
		classUnderTest = null;
	}
	
	@Test
	public void testFixLineOnBatchFileLine()
	{
		// Expect no changes for squirrel-sql.bat
		String[] fixedScript = runFixerOnScriptLines(WINDOWS_BATCH_FILE_NAME, SCRIPT_THAT_CONTAINS_BAD_FUNCTION);
		Assert.assertArrayEquals(SCRIPT_THAT_CONTAINS_BAD_FUNCTION, fixedScript);
		
		// Expect no changes for squirrel-sql.bat
		fixedScript = runFixerOnScriptLines(WINDOWS_BATCH_FILE_NAME, SCRIPT_THAT_CONTAINS_GOOD_FUNCTION);
		Assert.assertArrayEquals(SCRIPT_THAT_CONTAINS_GOOD_FUNCTION, fixedScript);
		
	}

	@Test
	public void testFileLineOnBadShellScript() {
		String[] fixedScript = runFixerOnScriptLines(SHELL_FILE_NAME, SCRIPT_THAT_CONTAINS_BAD_FUNCTION);
		assertEquals(SCRIPT_THAT_CONTAINS_BAD_FUNCTION.length+1, fixedScript.length);
	}

	@Test
	public void testFileLineOnGoofShellScript() {
		String[] fixedScript = runFixerOnScriptLines(SHELL_FILE_NAME, SCRIPT_THAT_CONTAINS_GOOD_FUNCTION);
		assertArrayEquals(SCRIPT_THAT_CONTAINS_GOOD_FUNCTION, fixedScript);
	}
	
	private String[] runFixerOnScriptLines(String scriptFileName, String[] lines) {
		ArrayList<String> result = new ArrayList<String>();		
		for (String line : lines) {
			String fixedLine = classUnderTest.fixLine(scriptFileName, line);
			if (fixedLine.contains(IOUtilities.NEW_LINE)) {
				String[] parts = fixedLine.split(IOUtilities.NEW_LINE);
				for (String part : parts) {
					result.add(part);
				}
			} else {
				result.add(fixedLine);
			}
		}
		return result.toArray(new String[result.size()]);
	}
}
