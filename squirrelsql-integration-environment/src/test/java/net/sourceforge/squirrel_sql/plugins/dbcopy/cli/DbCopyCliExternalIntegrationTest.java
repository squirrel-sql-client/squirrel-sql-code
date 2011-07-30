package net.sourceforge.squirrel_sql.plugins.dbcopy.cli;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests copying tables from one type of database to another.  The ALIAS_NAME constants below must be defined
 * in ~/.squirrel-sql/SQLAliases23.xml for this test to work properly.
 */
public class DbCopyCliExternalIntegrationTest
{
	private static String DB2_DEST_ALIAS_NAME = "DB2 (DBCPDST)";
	
	private static String DB2_SOURCE_ALIAS_NAME = "DB2 (DBCPSRC)";
	
	private static String DERBY_DEST_ALIAS_NAME = "Derby (dbcopydest)";
	
	private static String DERBY_SOURCE_ALIAS_NAME = "Derby (dbcopysrc)";
	
	private static String FIREBIRD_DEST_ALIAS_NAME = "Firebird (dbcopydest)";
	
	private static String FRONTBASE_DEST_ALIAS_NAME = "Frontbase (dbcopydest)";
	
	private static String H2_SOURCE_ALIAS_NAME = "H2 (dbcopysrc)";
	
	private static String H2_DEST_ALIAS_NAME = "H2 (dbcopydest)";
	
	private static String INFORMIX_DEST_ALIAS_NAME = "Informix (demo_on)";
	
	private static String HSQLDB_DEST_ALIAS_NAME = "HSQLDB (dbcopydest)";
	
	private static String MYSQL4_DEST_ALIAS_NAME = "MySQL 4 (dbcopydest)";
	
	private static String MYSQL4_SOURCE_ALIAS_NAME = "MySQL 4 (dbcopysrc)";
	
	private static String MYSQL5_DEST_ALIAS_NAME = "MySQL 5 (dbcopydest)";
	
	private static String MYSQL5_SOURCE_ALIAS_NAME = "MySQL 5 (dbcopysrc)";
	
	private static String ORACLE_SOURCE_ALIAS_NAME = "Oracle (dbcopysrc)";
	
	private static String ORACLE_DEST_ALIAS_NAME = "Oracle (dbcopydest)";

	private static String POSTGRES_DEST_ALIAS_NAME = "PostgreSQL";

	private static String TABLE_LIST_UPPER = 
		"BIGINT_TYPE_TABLE,BINARY_TYPE_TABLE,BIT_TYPE_TABLE,BLOB_TYPE_TABLE,BOOLEAN_TYPE_TABLE,"+
		"CHAR_TYPE_TABLE,CLOB_TYPE_TABLE,DATE_TYPE_TABLE,DECIMAL_TYPE_TABLE,DOUBLE_TYPE_TABLE," +
		"FLOAT_TYPE_TABLE,INTEGER_TYPE_TABLE,LONGVARBINARY_TYPE_TABLE,LONGVARCHAR_TYPE_TABLE," +
		"NUMERIC_TYPE_TABLE,REAL_TYPE_TABLE,SMALLINT_TYPE_TABLE,TIME_TYPE_TABLE,TIMESTAMP_TYPE_TABLE," +
		"TINYINT_TYPE_TABLE,VARBINARY_TYPE_TABLE,VARCHAR_TYPE_TABLE";
		
	private static String TABLE_LIST_LOWER = TABLE_LIST_UPPER.toLowerCase();

	
	@Test
	public void testH2ToDb2() throws Exception
	{
		String[] args =
			new String[] { 
				"--source-alias", H2_SOURCE_ALIAS_NAME, 
				"--dest-alias", DB2_DEST_ALIAS_NAME,
				"--table-list", TABLE_LIST_UPPER, 
				"--dest-schema", "DBCPDST", 
				"--source-schema", "PUBLIC" };
		DBCopyCLI.main(args);
	}
	
	@Test
	public void testOracleToDb2() throws Exception
	{
		String[] args =
			new String[] { 
				"--source-alias", ORACLE_SOURCE_ALIAS_NAME, 
				"--dest-alias", DB2_DEST_ALIAS_NAME,
				"--table-list", TABLE_LIST_UPPER, 
				"--dest-schema", "DBCPDST", 
				"--source-schema", "DBCOPYSRC" };
		DBCopyCLI.main(args);
	}

	@Test
	public void testOracleToDerby() throws Exception
	{
		String[] args =
			new String[] { 
				"--source-alias", ORACLE_SOURCE_ALIAS_NAME, 
				"--dest-alias", DERBY_DEST_ALIAS_NAME,
				"--table-list", TABLE_LIST_UPPER, 
				"--dest-schema", "APP", 
				"--source-schema", "DBCOPYSRC" };
		DBCopyCLI.main(args);
	}

	@Test
	public void testOracleToFirebird() throws Exception
	{
		String[] args =
			new String[] { 
				"--source-alias", ORACLE_SOURCE_ALIAS_NAME, 
				"--dest-alias", FIREBIRD_DEST_ALIAS_NAME,
				"--table-list", TABLE_LIST_UPPER, 
				"--dest-schema", "", 
				"--source-schema", "DBCOPYSRC" };
		DBCopyCLI.main(args);
	}
	
	@Test
	public void testOracleToFrontbase() throws Exception
	{
		String[] args =
			new String[] { 
				"--source-alias", ORACLE_SOURCE_ALIAS_NAME, 
				"--dest-alias", FRONTBASE_DEST_ALIAS_NAME,
				"--table-list", TABLE_LIST_UPPER, 
				"--dest-schema", "DBCOPY", 
				"--source-schema", "DBCOPYSRC" };
		DBCopyCLI.main(args);
	}

	@Test
	public void testOracleToH2() throws Exception
	{
		String[] args =
			new String[] { 
				"--source-alias", ORACLE_SOURCE_ALIAS_NAME, 
				"--dest-alias", H2_DEST_ALIAS_NAME,
				"--table-list", TABLE_LIST_UPPER, 
				"--dest-schema", "PUBLIC", 
				"--source-schema", "DBCOPYSRC" };
		DBCopyCLI.main(args);
	}

	@Test
	public void testOracleToHSQLDB() throws Exception
	{
		String[] args =
			new String[] { 
				"--source-alias", ORACLE_SOURCE_ALIAS_NAME, 
				"--dest-alias", HSQLDB_DEST_ALIAS_NAME,
				"--table-list", TABLE_LIST_UPPER, 
				"--dest-schema", "PUBLIC", 
				"--source-schema", "DBCOPYSRC" };
		DBCopyCLI.main(args);
	}

	@Test
	public void testOracleToInformix() throws Exception
	{
		String[] args =
			new String[] { 
				"--source-alias", ORACLE_SOURCE_ALIAS_NAME, 
				"--dest-alias", INFORMIX_DEST_ALIAS_NAME,
				"--table-list", TABLE_LIST_UPPER, 
				"--dest-schema", "informix", 
				"--source-schema", "DBCOPYSRC" };
		DBCopyCLI.main(args);
	}
	
	@Test
	public void testOracleToMySQL4() throws Exception
	{
		String[] args =
			new String[] { 
				"--source-alias", ORACLE_SOURCE_ALIAS_NAME, 
				"--dest-alias", MYSQL4_DEST_ALIAS_NAME,
				"--table-list", TABLE_LIST_UPPER, 
				"--dest-catalog", "dbcopydest", 
				"--source-schema", "DBCOPYSRC" };
		DBCopyCLI.main(args);		
	}

	@Test
	public void testOracleToMySQL5() throws Exception
	{
		String[] args =
			new String[] { 
				"--source-alias", ORACLE_SOURCE_ALIAS_NAME, 
				"--dest-alias", MYSQL5_DEST_ALIAS_NAME,
				"--table-list", TABLE_LIST_UPPER, 
				"--dest-catalog", "dbcopydest", 
				"--source-schema", "DBCOPYSRC" };
		DBCopyCLI.main(args);		
	}
	
	@Test
	public void testOracleToOracle() throws Exception
	{
		String[] args =
			new String[] { 
				"--source-alias", ORACLE_SOURCE_ALIAS_NAME, 
				"--dest-alias", ORACLE_DEST_ALIAS_NAME,
				"--table-list", TABLE_LIST_UPPER, 
				"--dest-schema", "DBCOPYDEST", 
				"--source-schema", "DBCOPYSRC" };
		DBCopyCLI.main(args);
	}	
	
	@Test
	public void testOracleToPostgreSQL() throws Exception
	{
		String[] args =
			new String[] { 
				"--source-alias", ORACLE_SOURCE_ALIAS_NAME, 
				"--dest-alias", POSTGRES_DEST_ALIAS_NAME,
				"--table-list", TABLE_LIST_UPPER, 
				"--dest-schema", "public", 
				"--source-schema", "DBCOPYSRC" };
		DBCopyCLI.main(args);
	}

	
}
