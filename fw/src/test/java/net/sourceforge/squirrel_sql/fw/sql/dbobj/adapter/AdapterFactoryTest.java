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
package net.sourceforge.squirrel_sql.fw.sql.dbobj.adapter;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.sql.dbobj.BestRowIdentifier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class AdapterFactoryTest extends BaseSQuirreLJUnit4TestCase
{
	
	EasyMockHelper mockHelper = new EasyMockHelper();
	
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testGetInstance()
	{
		assertNotNull(AdapterFactory.getInstance());
	}

	@Test
	public void testCreateBestRowIdentifierAdapter()
	{
		BestRowIdentifier[] beans = new BestRowIdentifier[1];
		beans[0] = mockHelper.createMock(BestRowIdentifier.class);
		assertNotNull(AdapterFactory.getInstance().createBestRowIdentifierAdapter(beans));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testCreateBestRowIdentifierAdapter_NullArg() {
		AdapterFactory.getInstance().createBestRowIdentifierAdapter(null);
	}
	
}
