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
package net.sourceforge.squirrel_sql.fw.dialects;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.fail;

import java.awt.Component;

import javax.swing.Icon;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.gui.IDialogUtils;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import utils.EasyMockHelper;

public class DialectFactoryTest extends BaseSQuirreLJUnit4TestCase
{
	
	EasyMockHelper mockHelper = new EasyMockHelper();
	
	IDialogUtils mockDialogUtils = mockHelper.createMock(IDialogUtils.class);
	ISQLDatabaseMetaData mockSqlDatabaseMetaData = mockHelper.createMock(ISQLDatabaseMetaData.class);
	
	@Before
	public void setUp() throws Exception
	{
		DialectFactory.setDialogUtils(mockDialogUtils);
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test @Ignore 
	public void testSetDialogUtils()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsAxion()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsDaffodil()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsDB2()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsDerby()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsFirebird()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsFrontBase()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsHADB()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsH2()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsHSQL()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsInformix()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsIngres()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsInterbase()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsMaxDB()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsMcKoi()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsMSSQLServer()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsMySQL()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsMySQL5()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsOracle()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsPointbase()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsPostgreSQL()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsProgress()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsSyBase()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsTimesTen()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testIsIntersystemsCacheDialectExt()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testGetDialectType()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testGetDialectString()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testGetDialectIgnoreCase()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testGetDialectISQLDatabaseMetaData()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test (expected = UserCancelledOperationException.class)
	public void testGetDialect_ShowDialog_UserCancelled() throws UserCancelledOperationException
	{
		
		DialectFactory.isPromptForDialect = true;
		
		expect(mockDialogUtils.showInputDialog((Component)anyObject(), isA(String.class), isA(String.class), 
			anyInt(), (Icon)anyObject(), (Object[]) anyObject(), anyObject()));
		expectLastCall().andReturn("");
		
		mockHelper.replayAll();
		DialectFactory.getDialect(DialectFactory.DEST_TYPE, null, mockSqlDatabaseMetaData);
		mockHelper.verifyAll();
	}

	@Test @Ignore
	public void testGetDbNames()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test @Ignore
	public void testGetSupportedDialects()
	{
		fail("Not yet implemented"); // TODO
	}

}
