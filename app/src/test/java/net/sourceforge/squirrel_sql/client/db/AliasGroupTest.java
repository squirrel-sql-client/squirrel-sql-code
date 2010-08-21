/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.client.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sourceforge.squirrel_sql.AbstractSerializableTest;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class AliasGroupTest extends AbstractSerializableTest
{

	private AliasGroup classUnderTest = null;

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new AliasGroup();
		super.serializableToTest = new AliasGroup();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
		super.serializableToTest = null;
	}

	@Test
	public void testEqualsObject() throws Exception
	{
		IIdentifier id1 = new IntegerIdentifier(1);
		IIdentifier id2 = new IntegerIdentifier(2);
		String name1 = "NameTest";
		String name2 = "NameTest2";

		AliasGroup ag1 = new AliasGroup();
		ag1.setIdentifier(id1);
		ag1.setName(name1);

		AliasGroup ag2 = new AliasGroup();
		ag2.setIdentifier(id1);
		ag2.setName(name1);

		AliasGroup ag3 = new AliasGroup();
		ag3.setIdentifier(id2);
		ag3.setName(name2);

		AliasGroup ag4 = new AliasGroup()
		{
			private static final long serialVersionUID = 1L;
		};
		ag4.setIdentifier(id1);
		ag4.setName(name1);

		new EqualsTester(ag1, ag2, ag3, ag4);

	}
	
	@Test (expected = ValidationException.class) 
	public void testSetName_invalid_name() throws ValidationException {
		classUnderTest.setName("");
	}

	@Test (expected = ValidationException.class) 
	public void testSetName_null_name() throws ValidationException {
		classUnderTest.setName(null);
	}
	
	@Test 
	public void testGetName() throws ValidationException {
		classUnderTest.setName("aTestName");
		assertEquals("aTestName", classUnderTest.getName());
	}
	
	@Test
	public void testIsValid() throws ValidationException {	
		assertFalse(classUnderTest.isValid());
		classUnderTest.setName("aTestName");
		assertTrue(classUnderTest.isValid());
	}
}
