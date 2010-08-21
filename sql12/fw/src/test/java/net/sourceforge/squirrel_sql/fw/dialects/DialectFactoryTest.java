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
import static org.junit.Assert.assertFalse;
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

		// Not a real database, but no driver should match this.
		expect(mockSqlDatabaseMetaData.getDatabaseProductName()).andStubReturn("FooBar Database");
		expect(mockSqlDatabaseMetaData.getDatabaseProductVersion()).andStubReturn("FooBar-v1.0.0");
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	@Ignore
	public void testSetDialogUtils()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testIsAxion()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isAxion(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();
	}

	@Test
	public void testIsDaffodil()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isDaffodil(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();
	}

	@Test
	public void testIsDB2()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isDB2(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsDerby()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isDerby(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsFirebird()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isFirebird(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsFrontBase()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isFrontBase(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsHADB()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isHADB(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsH2()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isH2(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsHSQL()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isHSQL(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsInformix()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isInformix(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsIngres()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isIngres(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsInterbase()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isInterbase(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsMaxDB()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isMaxDB(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsMcKoi()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isMcKoi(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsMSSQLServer()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isMSSQLServer(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsMySQL()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isMySQL(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsMySQL5()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isMySQL5(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsOracle()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isOracle(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsPointbase()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isPointbase(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsPostgreSQL()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isPostgreSQL(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsProgress()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isProgress(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsSyBase()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isSyBase(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsTimesTen()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isTimesTen(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	public void testIsIntersystemsCacheDialectExt()
	{
		mockHelper.replayAll();
		assertFalse(DialectFactory.isIntersystemsCacheDialectExt(mockSqlDatabaseMetaData));
		mockHelper.verifyAll();

	}

	@Test
	@Ignore
	public void testGetDialectType()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public void testGetDialectString()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public void testGetDialectIgnoreCase()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public void testGetDialectISQLDatabaseMetaData()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test(expected = UserCancelledOperationException.class)
	public void testGetDialect_ShowDialog_UserCancelled() throws UserCancelledOperationException
	{

		DialectFactory.isPromptForDialect = true;

		expect(mockDialogUtils.showInputDialog((Component) anyObject(), isA(String.class), isA(String.class),
			anyInt(), (Icon) anyObject(), (Object[]) anyObject(), anyObject()));
		expectLastCall().andReturn("");

		mockHelper.replayAll();
		DialectFactory.getDialect(DialectFactory.DEST_TYPE, null, mockSqlDatabaseMetaData);
		mockHelper.verifyAll();
	}

	@Test
	@Ignore
	public void testGetDbNames()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public void testGetSupportedDialects()
	{
		fail("Not yet implemented"); // TODO
	}

}
