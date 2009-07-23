package net.sourceforge.squirrel_sql;

import static org.easymock.EasyMock.expect;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import org.apache.log4j.Level;

import utils.EasyMockHelper;

public class BaseSQuirreLJUnit4TestCase
{

	public static final String TEST_QUALIFIED_NAME = "testQualifiedName";
	public static final String TEST_SIMPLE_NAME = "testSimpleName";
	public static final String TEST_CATALOG_NAME = "testCatalogName";
	public static final String TEST_SCHEMA_NAME = "testSchemaName";
	public static final String TEST_DATABASE_PRODUCT_NAME = "testDatabaseProductName";
	public static final String TEST_DATABASE_PRODUCT_VERSION = "testDatabaseProductVersion";
	
	protected EasyMockHelper mockHelper = new EasyMockHelper();
	
	public BaseSQuirreLJUnit4TestCase()
	{
		StringManager.setTestMode(true);
	}

	@SuppressWarnings("unchecked")
	protected static void disableLogging(Class c)
	{
		ILogger s_log = LoggerController.createLogger(c);
		s_log.setLevel(Level.OFF);
	}

	@SuppressWarnings("unchecked")
	protected static void debugLogging(Class c)
	{
		ILogger s_log = LoggerController.createLogger(c);
		s_log.setLevel(Level.DEBUG);
	}

	protected void setupDboExpectations(IDatabaseObjectInfo info) {
		expect(info.getCatalogName()).andStubReturn(TEST_CATALOG_NAME);
		expect(info.getSchemaName()).andStubReturn(TEST_SCHEMA_NAME);
		expect(info.getSimpleName()).andStubReturn(TEST_SIMPLE_NAME);
		expect(info.getQualifiedName()).andStubReturn(TEST_QUALIFIED_NAME);
	}
	
	protected void setupSqlDatabaseMetaDataExpectations(ISQLDatabaseMetaData md) 
	throws SQLException {
		expect(md.getDatabaseProductName()).andStubReturn(TEST_DATABASE_PRODUCT_NAME);
		expect(md.getDatabaseProductVersion()).andStubReturn(TEST_DATABASE_PRODUCT_VERSION);
		expect(md.supportsSchemasInDataManipulation()).andStubReturn(true);
		expect(md.supportsCatalogsInDataManipulation()).andStubReturn(true);
		expect(md.supportsSchemasInTableDefinitions()).andStubReturn(true);
		expect(md.getCatalogSeparator()).andStubReturn(".");
		expect(md.getIdentifierQuoteString()).andStubReturn("\"");
	}
}
