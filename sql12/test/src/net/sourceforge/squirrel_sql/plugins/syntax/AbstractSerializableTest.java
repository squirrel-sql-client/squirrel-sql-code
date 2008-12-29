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
package net.sourceforge.squirrel_sql.plugins.syntax;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.After;
import org.junit.Test;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

/**
 * Tests for classes that are Serializable should extend this class then implement a @Before method that 
 * initializes the protected serializableToTest to an instance of the classUnderTest.
 * 
 * @author manningr
 *
 */
public abstract class AbstractSerializableTest extends BaseSQuirreLJUnit4TestCase
{

	protected Serializable serializableToTest = null;
	
	public AbstractSerializableTest()
	{
		super();
	}

	@Test
	public void serializationTest() throws Exception
	{
		String tmpDir = System.getProperty("java.io.tmpdir", "/tmp");
		String filename = tmpDir + File.separator  + "classUnderTest.ser";
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		fos = new FileOutputStream(filename);
		out = new ObjectOutputStream(fos);
		out.writeObject(serializableToTest);
		out.close();
	}

	@After
	public void tearDown() throws Exception
	{
		serializableToTest = null;
	}
	
}