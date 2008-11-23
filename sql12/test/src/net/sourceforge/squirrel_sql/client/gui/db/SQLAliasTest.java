package net.sourceforge.squirrel_sql.client.gui.db;

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
import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;
import net.sourceforge.squirrel_sql.plugins.syntax.AbstractSerializableTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class SQLAliasTest extends AbstractSerializableTest
{

	@Before
	public void setUp() throws Exception
	{
		super.serializableToTest = new SQLAlias();
	}

	@After
	public void tearDown() throws Exception
	{
		super.serializableToTest = null;
	}

	@Test
	public void testEqualsObject()
	{
		IIdentifierFactory factory = IdentifierFactory.getInstance();
		IIdentifier id1 = factory.createIdentifier();
		IIdentifier id2 = factory.createIdentifier();
		SQLAlias alias1 = new SQLAlias(id1);
		SQLAlias alias2 = new SQLAlias(id1);
		SQLAlias alias3 = new SQLAlias(id2);
		SQLAlias alias4 = new SQLAlias(id1)
		{
			private static final long serialVersionUID = 1L;
		};
		new EqualsTester(alias1, alias2, alias3, alias4);
	}

	@Test
	public void testIsValid()
	{
		SQLAlias uninitializedAlias = new SQLAlias();
		assertEquals(false, uninitializedAlias.isValid());
	}

}
