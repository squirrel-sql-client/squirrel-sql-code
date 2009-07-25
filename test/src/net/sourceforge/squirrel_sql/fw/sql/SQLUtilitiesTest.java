package net.sourceforge.squirrel_sql.fw.sql;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SQLUtilitiesTest extends BaseSQuirreLJUnit4TestCase
{

	private ITableInfo mockTable1 = mockHelper.createMock("mockTable1", ITableInfo.class);

	private ITableInfo mockTable2 = mockHelper.createMock("mockTable2", ITableInfo.class);

	private ITableInfo mockTable3 = mockHelper.createMock("mockTable3", ITableInfo.class);

	private SQLDatabaseMetaData mockSQLDatabaseMetaData =
		mockHelper.createMock("mockSQLDatabaseMetaData", SQLDatabaseMetaData.class);

	@Before
	public void setUp() throws Exception
	{

	}

	@After
	public void tearDown() throws Exception
	{

	}

	@Test
	public void testGetInsertionOrder() throws SQLException
	{

		ArrayList<ITableInfo> tables = new ArrayList<ITableInfo>();

		expect(mockTable1.getSimpleName()).andStubReturn("mockTable1");
		expect(mockTable1.getImportedKeys()).andStubReturn(null);
		expect(mockTable1.getExportedKeys()).andStubReturn(null);
		
		expect(mockTable2.getSimpleName()).andStubReturn("mockTable2");
		expect(mockTable2.getImportedKeys()).andStubReturn(null);
		expect(mockTable2.getExportedKeys()).andStubReturn(null);

		
		expect(mockTable3.getSimpleName()).andStubReturn("mockTable3");
		expect(mockTable3.getImportedKeys()).andStubReturn(null);
		expect(mockTable3.getExportedKeys()).andStubReturn(null);
		
		tables.add(mockTable1);
		tables.add(mockTable2);
		tables.add(mockTable3);

		expect(mockSQLDatabaseMetaData.getImportedKeysInfo(mockTable1)).andStubReturn(null);
		mockTable1.setImportedKeys(null);
		expect(mockSQLDatabaseMetaData.getExportedKeysInfo(mockTable1)).andStubReturn(null);
		mockTable1.setExportedKeys(null);
		
		expect(mockSQLDatabaseMetaData.getImportedKeysInfo(mockTable2)).andStubReturn(null);
		mockTable2.setImportedKeys(null);
		expect(mockSQLDatabaseMetaData.getExportedKeysInfo(mockTable2)).andStubReturn(null);
		mockTable2.setExportedKeys(null);

		expect(mockSQLDatabaseMetaData.getImportedKeysInfo(mockTable3)).andStubReturn(null);
		mockTable3.setImportedKeys(null);
		expect(mockSQLDatabaseMetaData.getExportedKeysInfo(mockTable3)).andStubReturn(null);
		mockTable3.setExportedKeys(null);
		
		mockHelper.replayAll();

		try
		{
			List<ITableInfo> result =
				SQLUtilities.getInsertionOrder(tables, mockSQLDatabaseMetaData, new MyCallback());
			Assert.assertEquals(tables.size(), result.size());
		}
		catch (Exception e)
		{
			fail("Unexpected exception: " + e.getMessage());
		}

		mockHelper.verifyAll();
	}

	@Test
	public void testNullQuoteIdentifier()
	{
		Assert.assertNull(SQLUtilities.quoteIdentifier(null));
	}

	@Test
	public void testQuoteIdentifierWithEmbeddedQuotes()
	{
		String tableNameWithAnEmbeddedQuote = "foo\"bar";
		String newTableName = SQLUtilities.quoteIdentifier(tableNameWithAnEmbeddedQuote);
		assertEquals("foo\"\"bar", newTableName);

		tableNameWithAnEmbeddedQuote = "\"foo\"bar\"";
		newTableName = SQLUtilities.quoteIdentifier(tableNameWithAnEmbeddedQuote);
		assertEquals("\"foo\"\"bar\"", newTableName);

		String tableName = "MyTable";
		String quotedTableName = SQLUtilities.quoteIdentifier(tableName);
		assertEquals(tableName, quotedTableName);
	}

	private static class MyCallback implements ProgressCallBack
	{

		public void currentlyLoading(String simpleName)
		{
			// Do Nothing
		}

	}
}
