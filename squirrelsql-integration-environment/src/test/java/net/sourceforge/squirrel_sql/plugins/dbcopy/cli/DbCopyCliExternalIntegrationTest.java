/*
 * Copyright (C) 2011 Rob Manning
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

package net.sourceforge.squirrel_sql.plugins.dbcopy.cli;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AliasNames;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.IOUtilitiesImpl;
import net.sourceforge.squirrel_sql.plugins.db2.DB2JCCExceptionFormatter;

import org.junit.Test;

import pl.kernelpanic.dbmonster.DBMonster;
import pl.kernelpanic.dbmonster.ProgressMonitorAdapter;
import pl.kernelpanic.dbmonster.schema.Schema;
import pl.kernelpanic.dbmonster.schema.SchemaUtil;

/**
 * Tests copying tables from one type of database to another. The ALIAS_NAME constants below must be defined
 * in ~/.squirrel-sql/SQLAliases23.xml for this test to work properly.
 */
public class DbCopyCliExternalIntegrationTest
{

	private static String TABLE_LIST_UPPER =
		"BIGINT_TYPE_TABLE,BINARY_TYPE_TABLE,BIT_TYPE_TABLE,BLOB_TYPE_TABLE,BOOLEAN_TYPE_TABLE,"
			+ "CHAR_TYPE_TABLE,CLOB_TYPE_TABLE,DATE_TYPE_TABLE,DECIMAL_TYPE_TABLE,DOUBLE_TYPE_TABLE,"
			+ "FLOAT_TYPE_TABLE,INTEGER_TYPE_TABLE,LONGVARBINARY_TYPE_TABLE,LONGVARCHAR_TYPE_TABLE,"
			+ "NUMERIC_TYPE_TABLE,REAL_TYPE_TABLE,SMALLINT_TYPE_TABLE,TIME_TYPE_TABLE,TIMESTAMP_TYPE_TABLE,"
			+ "TINYINT_TYPE_TABLE,VARBINARY_TYPE_TABLE,VARCHAR_TYPE_TABLE";

	private static String TABLE_LIST_LOWER = TABLE_LIST_UPPER.toLowerCase();

	private SessionUtil sessionUtil = new SessionUtil();

	private DBCopyRunner runner = new DBCopyRunner();
	
	private IOUtilities ioutils = new IOUtilitiesImpl();

	@Test
	public void testDerbyToDb2() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.DERBY_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.DB2_DEST_ALIAS_NAME);

		testCopy(sourceSession, null, null, destSession, null, "DBCPDST", TABLE_LIST_UPPER);		
	}

	@Test
	public void testDerbyToDerby() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.DERBY_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.DERBY_DEST_ALIAS_NAME);

		testCopy(sourceSession, null, null, destSession, null, "APP", TABLE_LIST_UPPER);		
	}

	@Test
	public void testDerbyToFirebird() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.DERBY_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.FIREBIRD_DEST_ALIAS_NAME);

		testCopy(sourceSession, null, null, destSession, null, "APP", TABLE_LIST_UPPER);		
	}
	
	@Test
	public void testDerbyToFrontbase() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.DERBY_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.FRONTBASE_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, null, destSession, null, "DBCOPY", TABLE_LIST_UPPER);
	}
	
	@Test
	public void testDerbyToH2() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.DERBY_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.H2_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, null, destSession, null, "PUBLIC", TABLE_LIST_UPPER);
	}

	@Test
	public void testDerbyToInformix() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.DERBY_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.INFORMIX_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, null, destSession, "ansidb", "informix", TABLE_LIST_UPPER);
	}
	
	@Test
	public void testDerbyToMysql4() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.DERBY_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.MYSQL4_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, null, destSession, null, "dbcopydest", TABLE_LIST_UPPER);
	}

	@Test
	public void testDerbyToMysql5() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.DERBY_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.MYSQL5_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, null, destSession, null, "dbcopydest", TABLE_LIST_UPPER);
	}
	
	@Test
	public void testDerbyToOracle() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.DERBY_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.ORACLE_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, null, destSession, null, "DBCOPYDEST", TABLE_LIST_UPPER);
	}

	@Test
	public void testDerbyToPointbase() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.DERBY_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.POINTBASE_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, null, destSession, null, "DBCOPY", TABLE_LIST_UPPER);
	}
	
	@Test
	public void testDerbyToPostgreSQL() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.DERBY_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.POSTGRES_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, null, destSession, null, "public", TABLE_LIST_UPPER);
	}
		
	@Test
	public void testMysql4ToDb2() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.MYSQL4_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.DB2_DEST_ALIAS_NAME);

		testCopy(sourceSession, "dbcopysrc", null, destSession, null, "DBCPDST", TABLE_LIST_LOWER);		
	}

	
	// This test is currently broken because DB2 doesn't allow null clob column values for 
	// table with just one column.
	@Test
	public void testHsqldbToDb2() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.HSQLDB_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.DB2_DEST_ALIAS_NAME);

		testCopy(sourceSession, "PUBLIC", "PUBLIC", destSession, null, "DBCPDST", TABLE_LIST_UPPER);		
	}
	
	
	@Test
	public void testH2ToDb2() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.H2_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.DB2_DEST_ALIAS_NAME);

		testCopy(sourceSession, null, "PUBLIC", destSession, null, "DBCPDST", TABLE_LIST_UPPER);
	}

	@Test
	public void testH2ToDerby() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.H2_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.DERBY_DEST_ALIAS_NAME);

		testCopy(sourceSession, null, "PUBLIC", destSession, null, "APP", TABLE_LIST_UPPER);
	}

	@Test
	public void testH2ToFirebird() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.H2_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.FIREBIRD_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "PUBLIC", destSession, null, "", TABLE_LIST_UPPER);
	}

	@Test
	public void testH2ToFrontbase() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.H2_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.FRONTBASE_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "PUBLIC", destSession, null, "DBCOPY", TABLE_LIST_UPPER);
	}
	
	@Test
	public void testH2ToH2() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.H2_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.H2_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "PUBLIC", destSession, null, "PUBLIC", TABLE_LIST_UPPER);
	}

	@Test
	public void testH2ToInformix() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.H2_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.INFORMIX_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "PUBLIC", destSession, "ansidb", "informix", TABLE_LIST_UPPER);
	}
	
	@Test
	public void testH2ToMysql4() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.H2_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.MYSQL4_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "PUBLIC", destSession, null, "dbcopydest", TABLE_LIST_UPPER);
	}

	@Test
	public void testH2ToMysql5() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.H2_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.MYSQL5_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "PUBLIC", destSession, null, "dbcopydest", TABLE_LIST_UPPER);
	}
	
	@Test
	public void testH2ToOracle() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.H2_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.ORACLE_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "PUBLIC", destSession, null, "DBCOPYDEST", TABLE_LIST_UPPER);
	}

	@Test
	public void testH2ToPointbase() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.H2_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.POINTBASE_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "PUBLIC", destSession, null, "DBCOPY", TABLE_LIST_UPPER);
	}
	
	@Test
	public void testH2ToPostgreSQL() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.H2_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.POSTGRES_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "PUBLIC", destSession, null, "public", TABLE_LIST_UPPER);
	}
		
	@Test
	public void testOracleToDb2() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.ORACLE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.DB2_DEST_ALIAS_NAME);

		testCopy(sourceSession, null, "DBCOPYSRC", destSession, null, "DBCPDST", TABLE_LIST_UPPER);
	}

	@Test
	public void testOracleToDerby() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.ORACLE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.DERBY_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPYSRC", destSession, null, "APP", TABLE_LIST_UPPER);
	}

	@Test
	public void testOracleToFirebird() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.ORACLE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.FIREBIRD_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPYSRC", destSession, null, "", TABLE_LIST_UPPER);
	}

	@Test
	public void testOracleToFrontbase() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.ORACLE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.FRONTBASE_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPYSRC", destSession, null, "DBCOPY", TABLE_LIST_UPPER);
	}

	@Test
	public void testOracleToH2() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.ORACLE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.H2_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPYSRC", destSession, null, "PUBLIC", TABLE_LIST_UPPER);
	}

	@Test
	public void testOracleToHSQLDB() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.ORACLE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.HSQLDB_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPYSRC", destSession, null, "PUBLIC", TABLE_LIST_UPPER);
	}

	@Test
	public void testOracleToInformix() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.ORACLE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.INFORMIX_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPYSRC", destSession, null, "informix", TABLE_LIST_UPPER);
	}

	@Test
	public void testOracleToMySQL4() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.ORACLE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.INFORMIX_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPYSRC", destSession, "dbcopydest", null, TABLE_LIST_UPPER);
	}

	@Test
	public void testOracleToMySQL5() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.ORACLE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.MYSQL5_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPYSRC", destSession, "dbcopydest", null, TABLE_LIST_UPPER);
	}

	@Test
	public void testOracleToOracle() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.ORACLE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.ORACLE_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPYSRC", destSession, null, "DBCOPYDEST", TABLE_LIST_UPPER);
	}

	@Test
	public void testOracleToPointbase() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.ORACLE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.POINTBASE_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPYSRC", destSession, null, "DBCOPY", TABLE_LIST_UPPER);
	}
	
	@Test
	public void testOracleToPostgreSQL() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.ORACLE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.POSTGRES_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPYSRC", destSession, null, "public", TABLE_LIST_UPPER);
	}

	@Test
	public void testPointbaseToDerby() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.POINTBASE_DEST_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.DERBY_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPY", destSession, null, "APP", TABLE_LIST_UPPER);
	}

	@Test
	public void testPointbaseToDB2() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.POINTBASE_DEST_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.DB2_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPY", destSession, null, "DBCPDST", TABLE_LIST_UPPER);
	}
	
	@Test
	public void testPointbaseToFirebird() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.POINTBASE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.FIREBIRD_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPY", destSession, null, "", TABLE_LIST_UPPER);
	}

	@Test
	public void testPointbaseToFrontbase() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.POINTBASE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.FRONTBASE_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPY", destSession, null, "DBCOPY", TABLE_LIST_UPPER);
	}
	
	@Test
	public void testPointbaseToH2() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.POINTBASE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.H2_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPY", destSession, null, "PUBLIC", TABLE_LIST_UPPER);
	}	
		
	@Test
	public void testPointbaseToHSQLDB() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.POINTBASE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.HSQLDB_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPY", destSession, null, "PUBLIC", TABLE_LIST_UPPER);
	}

	@Test
	public void testPointbaseToInformix() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.POINTBASE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.INFORMIX_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPY", destSession, null, "informix", TABLE_LIST_UPPER);
	}

	@Test
	public void testPointbaseToMySQL4() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.POINTBASE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.INFORMIX_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPY", destSession, "dbcopydest", null, TABLE_LIST_UPPER);
	}

	@Test
	public void testPointbaseToMySQL5() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.POINTBASE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.MYSQL5_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPY", destSession, "dbcopydest", null, TABLE_LIST_UPPER);
	}

	@Test
	public void testPointbaseToOracle() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.POINTBASE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.ORACLE_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPY", destSession, null, "DBCOPYDEST", TABLE_LIST_UPPER);
	}

	@Test
	public void testPointbaseToPointbase() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.POINTBASE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.POINTBASE_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPY", destSession, null, "DBCOPY", TABLE_LIST_UPPER);
	}
	
	@Test
	public void testPointbaseToPostgreSQL() throws Exception
	{
		ISession sourceSession = sessionUtil.getSessionForAlias(AliasNames.POINTBASE_SOURCE_ALIAS_NAME);
		ISession destSession = sessionUtil.getSessionForAlias(AliasNames.POSTGRES_DEST_ALIAS_NAME);
		
		testCopy(sourceSession, null, "DBCOPY", destSession, null, "public", TABLE_LIST_UPPER);
	}
	
	
	
	private String concat(List<String> lines) {
		StringBuilder result = new StringBuilder();
		for (String line : lines) {
			result.append(line);
			result.append("\n");
		}
		return result.toString();
	}
	
	private void executeScript(ISession session, String scriptFilename) throws Exception {
		IQueryTokenizer tokenizer = session.getQueryTokenizer();
		List<String> lines = ioutils.getLinesFromFile(scriptFilename, null);
		tokenizer.setScriptToTokenize(concat(lines));
		while (tokenizer.hasQuery()) {
			String sql = tokenizer.nextQuery();
			System.out.println("Running SQL ("+scriptFilename+"): "+sql);
			
			Statement stmt = null;
			try {
				stmt = session.getSQLConnection().createStatement();
				stmt.execute(sql);
			} catch (SQLException e) {
				System.err.println("Exception: "+e.getMessage());
			} finally {
				SQLUtilities.closeStatement(stmt);
			}
		}
		
	}
	
	private void runDBMonsterOnSourceSession(ISession sourceSession, String dbschema, String schemaXmlFile) throws Exception {
		SessionConnectionProvider connProvider = new SessionConnectionProvider(sourceSession);
		DBMonster dbm = new DBMonster();
		Properties props = new Properties();
		if (dbschema != null) {
			props.setProperty("dbmonster.jdbc.schema", dbschema);
		}
		dbm.setProperties(props);
		dbm.setProgressMonitor(new ProgressMonitorAdapter());
		dbm.setConnectionProvider(connProvider);
		Schema schema = SchemaUtil.loadSchema(schemaXmlFile, dbm.getLogger());
		dbm.addSchema(schema);
		System.out.println("Running DBMonster on source session using schema file: "+schemaXmlFile);
		dbm.doTheJob();		
	}
	
	private void testCopy(ISession sourceSession, String sourceCatalog, String sourceSchema, ISession destSession, String destCatalog, String destSchema, String tableListStr) throws Exception {
		String sourceTestDataFolder = 
			DialectFactory.getDialect(sourceSession.getMetaData()).getDisplayName().toLowerCase();
		String destTestDataFolder = 
			DialectFactory.getDialect(destSession.getMetaData()).getDisplayName().toLowerCase();

		
		// run drop script on both the source and destination databases.  If the testDataFolder associated with
		// the session has a drop.sql script, use that one instead of the generic one.
		if ((new File("src/test/resources/sql/"+sourceTestDataFolder+"/drop.sql")).exists()) {
			 executeScript(sourceSession, "src/test/resources/sql/"+sourceTestDataFolder+"/drop.sql");
		} else {
		    executeScript(sourceSession, "src/test/resources/sql/generic/drop.sql");
		}
		
		if ((new File("src/test/resources/sql/"+destTestDataFolder+"/drop.sql")).exists()) {
			executeScript(destSession, "src/test/resources/sql/"+destTestDataFolder+"/drop.sql");
		} else {
			executeScript(destSession, "src/test/resources/sql/generic/drop.sql");
		}
		
		// run the all types sql script on sourceSession
		executeScript(sourceSession, "src/test/resources/sql/"+sourceTestDataFolder+"/all_types.sql");
				
		// run dbmonster using oracle session as a ConnectionProvider.
		runDBMonsterOnSourceSession(sourceSession, sourceSchema, "src/test/resources/sql/"+sourceTestDataFolder+"/test.xml");

		runner.setSourceSession(sourceSession);
		runner.setSourceCatalogName(sourceCatalog);
		runner.setSourceSchemaName(sourceSchema);
		runner.setDestCatalogName(destCatalog);
		runner.setDestSession(destSession);
		runner.setDestSchemaName(destSchema);
		runner.setTableList(tableListStr);
		try {
			runner.run();
		} catch (Exception e) {
			System.err.println((new DB2JCCExceptionFormatter()).format(e));
			throw e;
		}
	}
	
}
