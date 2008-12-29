package net.sourceforge.squirrel_sql.fw.id;

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
import junit.framework.Assert;
import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class IntegerIdentifierTest extends AbstractSerializableTest
{

	private IntegerIdentifier classUnderTest = null;
	
	private int value = 10;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new IntegerIdentifier(value);
		super.serializableToTest = new IntegerIdentifier(value); 
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
		super.serializableToTest = null;
	}

	@Test
	public void testEquals()
	{
		IntegerIdentifier uid1 = new IntegerIdentifier(1);

		IntegerIdentifier uid2 = new IntegerIdentifier(1);

		IntegerIdentifier uid3 = new IntegerIdentifier(2);

		IntegerIdentifier uid4 = new IntegerIdentifier(1)
		{
			private static final long serialVersionUID = 1L;
		};

		new EqualsTester(uid1, uid2, uid3, uid4);
	}

	@Test
	public void testSetString()
	{
		Assert.assertEquals(value, classUnderTest.hashCode());
		classUnderTest.setString("5");
		Assert.assertEquals(5, classUnderTest.hashCode());
	}
}
