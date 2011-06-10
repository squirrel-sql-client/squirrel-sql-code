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
import static org.mockito.Mockito.when;

import java.awt.Component;
import java.sql.SQLException;

import javax.swing.Icon;

import junit.framework.Assert;
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

	private EasyMockHelper mockHelper = new EasyMockHelper();

	private IDialogUtils mockDialogUtils = mockHelper.createMock(IDialogUtils.class);

	private ISQLDatabaseMetaData mockSqlDatabaseMetaData = mockHelper.createMock(ISQLDatabaseMetaData.class);

	private IDialectFactory classUnderTest = null;

	// Actual values reported by vendor JDBC drivers

	private static final String AXION_PRODUCT_NAME = "AxionDB";
	
	private static final String AXION_PRODUCT_VERSION = "1.0M3-dev";
	
	private static final String DB2_PRODUCT_NAME = "DB2/LINUX";
	
	private static final String DB2_PRODUCT_VERSION = "SQL09050";

	private static final String DERBY_PRODUCT_NAME = "Apache Derby";
	
	private static final String DERBY_PRODUCT_VERSION = "10.6.2.1 - (999685)";

	private static final String FIREBIRD_PRODUCT_NAME = "Firebird 2.11LI-V2.1.3.18185 Firebird 2.1/tcp (dell-devpc)/P10";
	
	private static final String FIREBIRD_PRODUCT_VERSION = "4.2.9";
	
	private static final String FRONTBASE_PRODUCT_NAME = "FrontBase";
	
	private static final String FRONTBASE_PRODUCT_VERSION = "4.2.9";
	
	private static final String HSQL_PRODUCT_NAME = "HSQL Database Engine";
	
	private static final String HSQL_PRODUCT_VERSION = "1.8.0";
	
	private static final String H2_PRODUCT_NAME = "H2";
	
	private static final String H2_PRODUCT_VERSION = "1.0.65 (2008-01-18)";
	
	private static final String INFORMIX_PRODUCT_NAME = "Informix Dynamic Server";

	private static final String INFORMIX_PRODUCT_VERSION = "10.00.UC6E";
	
	private static final String INGRES_PRODUCT_NAME = "INGRES";

	private static final String INGRES_PRODUCT_VERSION = "II 9.1.1 (int.rpl/103)";

	private static final String MSSQL_PRODUCT_NAME = "Microsoft SQL Server";

	private static final String MSSQL_PRODUCT_VERSION = "9.00.3077";	

	private static final String MYSQL_PRODUCT_NAME = "MySQL";

	private static final String MYSQL_PRODUCT_VERSION = "4.1.22-standard";		

	private static final String MYSQL5_PRODUCT_NAME = "MySQL";

	private static final String MYSQL5_PRODUCT_VERSION = "5.1.41-3ubuntu12.10";		

	private static final String ORACLE_PRODUCT_NAME = "Oracle";
	
	private static final String ORACLE_PRODUCT_VERSION = 
		"Oracle Database 11g Enterprise Edition Release 11.1.0.7.0 - " +
		"Productio With the Partitioning, OLAP, Data Mining and Real Application Testing options";
	
	private static final String POINTBASE_PRODUCT_NAME = "PointBase";

	private static final String POINTBASE_PRODUCT_VERSION = "5.1 ECF build 300";
	
	private static final String POSTGRESQL_PRODUCT_NAME = "PostgreSQL";

	private static final String POSTGRESQL_PRODUCT_VERSION = "8.3.1";

	private static final String PROGRESS_PRODUCT_NAME = "OpenEdge RDBMS";

	private static final String PROGRESS_PRODUCT_VERSION = "10.1C";
	
	private static final String SYBASE_PRODUCT_NAME = "Adaptive Server Enterprise";
	
	private static final String SYBASE_PRODUCT_VERSION = 
		"Adaptive Server Enterprise/15.0.2/EBF 15654 " +
		"ESD#4/P/Linux Intel/Linux 2.4.21-47.ELsmp i686/ase1502/2528/32-bit/FBO/Sat Apr  5 05:18:42 2008";
	
	@Before
	public void setUp() throws Exception
	{
		DialectFactory.setDialogUtils(mockDialogUtils);

		// Not a real database, but no driver should match this.
		expect(mockSqlDatabaseMetaData.getDatabaseProductName()).andStubReturn("FooBar Database");
		expect(mockSqlDatabaseMetaData.getDatabaseProductVersion()).andStubReturn("FooBar-v1.0.0");

		classUnderTest = new DialectFactoryImpl();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
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
	public void testGetDialectForAxion() throws SQLException
	{
		final String productName = AXION_PRODUCT_NAME;
		final String productVersion = AXION_PRODUCT_VERSION;
		final String expectedDialectClassname = AxionDialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
	}			
	
	@Test
	public void testGetDialectForDb2() throws SQLException
	{
		final String productName = DB2_PRODUCT_NAME;
		final String productVersion = DB2_PRODUCT_VERSION;
		final String expectedDialectClassname = DB2DialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
	}		

	@Test
	public void testGetDialectForDerby() throws SQLException
	{
		final String productName = DERBY_PRODUCT_NAME;
		final String productVersion = DERBY_PRODUCT_VERSION;
		final String expectedDialectClassname = DerbyDialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
	}		

	@Test
	public void testGetDialectForFirebird() throws SQLException
	{
		final String productName = FIREBIRD_PRODUCT_NAME;
		final String productVersion = FIREBIRD_PRODUCT_VERSION;
		final String expectedDialectClassname = FirebirdDialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
	}		
	
	
	@Test
	public void testGetDialectForFrontbase() throws SQLException
	{
		final String productName = FRONTBASE_PRODUCT_NAME;
		final String productVersion = FRONTBASE_PRODUCT_VERSION;
		final String expectedDialectClassname = FrontBaseDialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
	}		
	
	@Test
	public void testGetDialectForHsql() throws SQLException
	{
		final String productName = HSQL_PRODUCT_NAME;
		final String productVersion = HSQL_PRODUCT_VERSION;
		final String expectedDialectClassname = HSQLDialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
	}		

	@Test
	public void testGetDialectForH2() throws SQLException
	{
		final String productName = H2_PRODUCT_NAME;
		final String productVersion = H2_PRODUCT_VERSION;
		final String expectedDialectClassname = H2DialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
	}		
	
	@Test
	public void testGetDialectForIngres() throws SQLException
	{
		final String productName = INGRES_PRODUCT_NAME;
		final String productVersion = INGRES_PRODUCT_VERSION;
		final String expectedDialectClassname = IngresDialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
	}	

	@Test
	public void testGetDialectForInformix() throws SQLException
	{
		final String productName = INFORMIX_PRODUCT_NAME;
		final String productVersion = INFORMIX_PRODUCT_VERSION;
		final String expectedDialectClassname = InformixDialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
	}	
	
	@Test
	public void testGetDialectForMssql() throws SQLException
	{
		final String productName = MSSQL_PRODUCT_NAME;
		final String productVersion = MSSQL_PRODUCT_VERSION;
		final String expectedDialectClassname = SQLServerDialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
	}	

	@Test
	public void testGetDialectForMysql() throws SQLException
	{
		final String productName = MYSQL_PRODUCT_NAME;
		final String productVersion = MYSQL_PRODUCT_VERSION;
		final String expectedDialectClassname = MySQLDialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
	}	
	
	@Test
	public void testGetDialectForMysql5() throws SQLException
	{
		final String productName = MYSQL5_PRODUCT_NAME;
		final String productVersion = MYSQL5_PRODUCT_VERSION;
		final String expectedDialectClassname = MySQL5DialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
	}	

	@Test
	public void testGetDialectForOracle() throws SQLException
	{
		final String productName = ORACLE_PRODUCT_NAME;
		final String productVersion = ORACLE_PRODUCT_VERSION;
		final String expectedDialectClassname = OracleDialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
	}	

	@Test
	public void testGetDialectForPointbase() throws SQLException
	{
		final String productName = POINTBASE_PRODUCT_NAME;
		final String productVersion = POINTBASE_PRODUCT_VERSION;
		final String expectedDialectClassname = PointbaseDialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
	}
	
	@Test
	public void testGetDialectForPostgreSQL() throws SQLException
	{
		final String productName = POSTGRESQL_PRODUCT_NAME;
		final String productVersion = POSTGRESQL_PRODUCT_VERSION;
		final String expectedDialectClassname = PostgreSQLDialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
	}

	@Test
	public void testGetDialectForProgress() throws SQLException
	{
		final String productName = PROGRESS_PRODUCT_NAME;
		final String productVersion = PROGRESS_PRODUCT_VERSION;
		final String expectedDialectClassname = ProgressDialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
	}

	@Test
	public void testGetDialectForSybase() throws SQLException
	{
		final String productName = SYBASE_PRODUCT_NAME;
		final String productVersion = SYBASE_PRODUCT_VERSION;
		final String expectedDialectClassname = SybaseDialectExt.class.getName();
		testGetDialectForDatabase(productName, productVersion, expectedDialectClassname);
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

	private void testGetDialectForDatabase(final String productName, final String productVersion,
		String expectedDialectClassname) throws SQLException
	{
		final ISQLDatabaseMetaData databaseMetaData = org.mockito.Mockito.mock(ISQLDatabaseMetaData.class);
		when(databaseMetaData.getDatabaseProductName()).thenReturn(productName);
		when(databaseMetaData.getDatabaseProductVersion()).thenReturn(productVersion);
		final HibernateDialect dialect = classUnderTest.getDialect(databaseMetaData);
		final String actualDialectClassname = dialect.getClass().getName();

		Assert.assertEquals(
			getUnexpectedDialectMessage(productName, productVersion, expectedDialectClassname,
				actualDialectClassname), expectedDialectClassname, actualDialectClassname);
	}	
	
	private String getUnexpectedDialectMessage(final String productName, final String productVersion,
		final String expectedClassName, final String actualClassName)
	{

		StringBuilder logMessage = new StringBuilder();
		logMessage.append("For product name (");
		logMessage.append(POSTGRESQL_PRODUCT_NAME);
		logMessage.append(") and version (");
		logMessage.append(POSTGRESQL_PRODUCT_VERSION);
		logMessage.append("), this test expected \nto get Class: ");
		logMessage.append(expectedClassName);
		logMessage.append(" for getDialect(ISQLDatabaseMetaData).");
		logMessage.append("\nInstead, it received Class: ");
		logMessage.append(actualClassName);
		return logMessage.toString();
	}
	
}
