package net.sourceforge.squirrel_sql.fw.dialects;

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
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.MockSession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IndexInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.plugins.db2.DB2JCCExceptionFormatter;
import net.sourceforge.squirrel_sql.plugins.informix.exception.InformixExceptionFormatter;
import net.sourceforge.squirrel_sql.plugins.refactoring.commands.MergeTableCommand;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.IMergeTableDialog;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.IMergeTableDialogFactory;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;
import net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.CreateDataScriptCommand;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

/**
 * The purpose of this class is to hookup to the database(s) specified in dialectLiveTest.properties and test
 * SQL generation parts of the dialect syntatically using the database' native parser. This is not a JUnit
 * test, as it requires a running database to complete.
 * 
 * @author manningr
 */
public class DialectExternalTest extends BaseSQuirreLJUnit4TestCase
{

	/**
	 * These sessions are the ones that are being tested. This is populated from the dbsToTest list in the
	 * properties file.
	 */
	ArrayList<ISession> sessions = new ArrayList<ISession>();

	/**
	 * These sessions will only be used to produce SQL, in case the session that is being tested is failing to
	 * produce valid SQL. This is populated from the dbsToReference list in the properties file.
	 */
	ArrayList<HibernateDialect> referenceDialects = new ArrayList<HibernateDialect>();

	Properties props = new Properties();

	TableColumnInfo firstCol = null;

	TableColumnInfo secondCol = null;

	TableColumnInfo thirdCol = null;

	TableColumnInfo fourthCol = null;

	TableColumnInfo dropCol = null;

	TableColumnInfo noDefaultValueVarcharCol = null;

	TableColumnInfo noDefaultValueIntegerCol = null;

	TableColumnInfo renameCol = null;

	TableColumnInfo pkCol = null;

	// This column is created in the create script abd unused unless testing DB2
	TableColumnInfo db2pkCol = null;

	TableColumnInfo notNullIntegerCol = null;

	// two columns to represent a Primary key in the pktest table
	TableColumnInfo doubleColumnPKOne = null;

	TableColumnInfo doubleColumnPKTwo = null;

	TableColumnInfo autoIncrementColumn = null;

	TableColumnInfo addColumn = null;

	TableColumnInfo uniqueConstraintTableColumnInfo = null;

	private TableColumnInfo dropConstraintColumn = null;

	private static final String DB2_PK_COLNAME = "DB2PKCOL";

	private DatabaseObjectQualifier qualifier = null;

	private final SqlGenerationPreferences prefs = new SqlGenerationPreferences();

	private static String fkParentTableName = "fkTestParentTable";

	private static String fkChildTableName = "fkTestChildTable";

	private static String fkParentColumnName = "parentid";

	private static String fkChildColumnName = "fkchildid";

	private static String fkChildPrimaryKeyColumnName = "childpkid";

	private static String testUniqueConstraintTableName = "testUniqueConstraintTable";

	private static String uniqueConstraintName = "uniq_constraint";

	private static String uniqueConstraintColName = "uidc";

	// this is the constraint that is dropped in drop constraint test
	private static String secondUniqueConstraintName = "uniq_constraint2";

	// this is the column on which the constraint to be dropped is defined
	private static String secondUniqueColumnName = "secondUniqueColumn";

	private static String autoIncrementTableName = "testAutoIncrementTable";

	private static String autoIncrementColumnName = "myid";

	private static String autoIncrementSequenceName = "auto_increment_seq";

	private static String integerDataTableName = "integerDataTable";

	private static String primaryKeyTestColumnOne = "pk_col_1";

	private static String primaryKeyTestColumnTwo = "pk_col_2";

	private static String nullVarcharColumnName = "nullvc";

	private static String indexTestColumnName = "idxTestColumnName";

	private static String uniqueIndexTestColumnName = "uniqidxTestColumnName";

	private static String columnAddedColumnName = "columnAdded";

	private static String testSequenceName = "testSequence";

	private static String testSequenceName2 = "testSequence2";

	private static String testCreateTable = "testCreateTable";

	private static String testInsertIntoTable = "testInsertIntoTable";

	private static String testCreateViewTable = "createviewtest";

	private static String testViewName = "testview";

	private static String testNewViewName = "newTestView";

	private static String testView2Name = "testview2";

	private static String testViewToBeDropped = "testViewToDrop";

	private static String testRenameTableBefore = "tableRenameTest";

	private static String testRenameTableAfter = "tableWasRenamed";

	private static String testCreateIndexTable = "createIndexTest";

	private static String testFirstMergeTable = "firstTableToBeMerged";

	private static String testSecondMergeTable = "secondTableToBeMerged";

	private static String testTableForDropView = "testTableForDropView";

	private static String testTimestampTable = "timestamptest";

	private static String testNewTimestampTable = "timestampttest2";

	private static String testTableForGetViewDefinition = "testTableForGetViewDef";

	/**
	 * this is set to true to try to derive SQL for the dialect being tested automatically, using other
	 * dialects
	 */
	private boolean dialectDiscoveryMode = false;

	private final EasyMockHelper mockHelper = new EasyMockHelper();

	private static final String[] TABLE_TYPES = new String[] { "TABLE" };

	// Mocks for generic dialect test
	private final ISession mockGenericDialectSession = mockHelper.createMock(ISession.class);

	private final SQLDatabaseMetaData mockGenericSQLDialectMetaData =
		mockHelper.createMock(SQLDatabaseMetaData.class);

	private final ISQLConnection mockGenericDialectSQLConnection = mockHelper.createMock(ISQLConnection.class);

	private final ITableInfo mockGenericDialectTableInfo =
		mockHelper.createMock("mockGenericDialectTableInfo", ITableInfo.class);

	private final TableColumnInfo mockTableColumnInfo = mockHelper.createMock(TableColumnInfo.class);

	private final IQueryTokenizer mockQueryTokenizer = mockHelper.createMock(IQueryTokenizer.class);

	private final IApplication mockApplication = mockHelper.createMock(IApplication.class);

	// private DatabaseMetaData mockGenericDialectDBMetaData = mockHelper.createMock(DatabaseMetaData.class);

	private final Connection mockGenericDialectConnection = mockHelper.createMock(Connection.class);

	private final Statement mockGenericDialectStatement = mockHelper.createMock(Statement.class);

	private final ResultSet mockResultSet = mockHelper.createMock(ResultSet.class);

	@Before
	public void setup() throws Exception
	{
		expect(mockGenericDialectSession.getMetaData()).andStubReturn(mockGenericSQLDialectMetaData);
		expect(mockGenericSQLDialectMetaData.getDatabaseProductName()).andStubReturn(
			"Generic Database Product Name");
		expect(mockGenericSQLDialectMetaData.getDatabaseProductVersion()).andStubReturn(
			"Generic Database Product Version");
		expect(mockGenericDialectSession.getSQLConnection()).andStubReturn(mockGenericDialectSQLConnection);
		expect(mockGenericDialectSQLConnection.getSQLMetaData()).andStubReturn(mockGenericSQLDialectMetaData);
		expect(mockGenericSQLDialectMetaData.storesUpperCaseIdentifiers()).andStubReturn(true);
		expect(mockGenericDialectSQLConnection.getConnection()).andStubReturn(mockGenericDialectConnection);
		expect(mockGenericDialectConnection.createStatement()).andStubReturn(mockGenericDialectStatement);
		expect(mockGenericDialectStatement.execute(EasyMock.isA(String.class))).andStubReturn(true);
		expect(
			mockGenericSQLDialectMetaData.getTables(isA(String.class), isA(String.class), isA(String.class),
				eq(TABLE_TYPES), (ProgressCallBack) isNull())).andStubReturn(
			new ITableInfo[] { mockGenericDialectTableInfo });
		expect(mockGenericSQLDialectMetaData.supportsSchemasInDataManipulation()).andStubReturn(true);
		expect(mockGenericSQLDialectMetaData.supportsCatalogsInDataManipulation()).andStubReturn(false);
		expect(mockGenericSQLDialectMetaData.supportsSchemasInTableDefinitions()).andStubReturn(true);
		expect(mockGenericSQLDialectMetaData.supportsCatalogsInTableDefinitions()).andStubReturn(false);
		expect(mockGenericSQLDialectMetaData.getCatalogSeparator()).andStubReturn("");
		expect(mockGenericSQLDialectMetaData.getIdentifierQuoteString()).andStubReturn("'");
		expect(mockGenericDialectTableInfo.getSimpleName()).andStubReturn("GenericTable");
		expect(mockGenericDialectTableInfo.getCatalogName()).andStubReturn("DefaultCatalog");
		expect(mockGenericDialectTableInfo.getSchemaName()).andStubReturn("DefaultSchema");
		expect(mockGenericDialectTableInfo.getDatabaseObjectType()).andStubReturn(DatabaseObjectType.TABLE);

		expect(mockGenericSQLDialectMetaData.getPrimaryKey(mockGenericDialectTableInfo)).andStubReturn(
			new PrimaryKeyInfo[] {});
		expect(mockGenericSQLDialectMetaData.getColumnInfo(mockGenericDialectTableInfo)).andStubReturn(
			new TableColumnInfo[] { mockTableColumnInfo });
		expect(
			mockGenericSQLDialectMetaData.getColumnInfo(isA(String.class), isA(String.class), isA(String.class))).andStubReturn(
			new TableColumnInfo[] { mockTableColumnInfo });

		expect(mockTableColumnInfo.getColumnName()).andStubReturn("GenericColumn");
		expect(mockTableColumnInfo.getDefaultValue()).andStubReturn("GenericDefaultValue");
		expect(mockTableColumnInfo.getColumnSize()).andStubReturn(10);
		expect(mockTableColumnInfo.getDataType()).andStubReturn(Types.VARCHAR);
		expect(mockTableColumnInfo.getDecimalDigits()).andStubReturn(0);
		expect(mockTableColumnInfo.isNullable()).andStubReturn("YES");

		expect(mockGenericSQLDialectMetaData.getImportedKeysInfo(mockGenericDialectTableInfo)).andStubReturn(
			new ForeignKeyInfo[] {});
		expect(mockGenericSQLDialectMetaData.getIndexInfo(mockGenericDialectTableInfo)).andStubReturn(
			new ArrayList<IndexInfo>());

		expect(mockGenericDialectSession.getQueryTokenizer()).andStubReturn(mockQueryTokenizer);
		expect(mockQueryTokenizer.getSQLStatementSeparator()).andStubReturn(";");

		expect(mockGenericDialectSession.getApplication()).andStubReturn(mockApplication);
		expect(mockApplication.getMainFrame()).andStubReturn(null);

		expect(mockGenericDialectStatement.executeQuery(isA(String.class))).andStubReturn(mockResultSet);
		expect(mockResultSet.getStatement()).andStubReturn(mockGenericDialectStatement);
		expect(mockResultSet.next()).andStubReturn(false);
		mockGenericDialectStatement.close();
		EasyMock.expectLastCall().atLeastOnce();
		mockResultSet.close();
		EasyMock.expectLastCall().atLeastOnce();
		mockGenericDialectSession.close();
		mockHelper.replayAll();
	}

	@After
	public void teardown()
	{
		mockHelper.resetAll();
	}

	@Test
	public void testDialectsQuotedQualified() throws Exception
	{
		testDialects(true, true);
	}

	@Test
	public void testDialectsNotQuotedNotQualified() throws Exception
	{
		testDialects(false, false);
	}

	private void testDialects(boolean quoteIdentifiers, boolean qualifyTableNames) throws Exception
	{

		prefs.setQualifyTableNames(qualifyTableNames);
		prefs.setQuoteIdentifiers(quoteIdentifiers);

		final String propertyFile = System.getProperty("dialectExternalTestPropertyFile");
		if (propertyFile == null || "".equals(propertyFile))
		{
			fail("Must specify the location of the properties file as a system property (dialectExternalTestPropertyFile)");
		}

		props.load(new FileReader(propertyFile));
		initSessions(sessions, "dbsToTest");
		initReferenceDialects(referenceDialects);
		runTests(quoteIdentifiers, qualifyTableNames);
	}

	/**
	 * This sets up the dialects that will be used as references to consult for sql variants that can be tried
	 * to see discover whether an undocumented variant might be allowed.
	 * 
	 * @param dialects
	 *           a data structure to hold the dialects in.
	 */
	private void initReferenceDialects(ArrayList<HibernateDialect> dialects)
	{
		final List<String> dbs = getPropertyListValue("dbsToReference");

		for (String dbName : dbs)
		{
			if (dbName.equals("mssql"))
			{
				dbName = "MS SQLServer";
			}
			final HibernateDialect dialectToRef = DialectFactory.getDialectIgnoreCase(dbName);
			if (dialectToRef == null)
			{
				fail("Failed to find dialect for : " + dbName);
			}
			dialects.add(dialectToRef);
		}
	}

	private List<String> getPropertyListValue(String propertyKey)
	{
		final String dbsToTest = props.getProperty(propertyKey);
		final StringTokenizer st = new StringTokenizer(dbsToTest, ",");
		final ArrayList<String> dbs = new ArrayList<String>();
		while (st.hasMoreTokens())
		{
			final String db = st.nextToken().trim();
			dbs.add(db);
		}
		return dbs;
	}

	private void initSessions(ArrayList<ISession> sessionsList, String sessionListPropKey) throws Exception
	{
		prefs.setSqlStatementSeparator(";");

		dialectDiscoveryMode = Boolean.parseBoolean(props.getProperty("dialectDiscoveryMode"));
		if (dialectDiscoveryMode)
		{
			System.out.println("In discovery mode - assuming that the dialect being tested isn't yet implemented");
		}

		final List<String> dbs = getPropertyListValue(sessionListPropKey);
		for (final String db : dbs)
		{
			final String url = props.getProperty(db + "_jdbcUrl");
			final String user = props.getProperty(db + "_jdbcUser");
			final String pass = props.getProperty(db + "_jdbcPass");
			final String driver = props.getProperty(db + "_jdbcDriver");
			final String catalog = props.getProperty(db + "_catalog");
			final String schema = props.getProperty(db + "_schema");

			final MockSession session = new MockSession(driver, url, user, pass);
			session.setDefaultCatalog(catalog);
			session.setDefaultSchema(schema);
			sessionsList.add(session);
		}

		sessionsList.add(mockGenericDialectSession);
	}

	private void init(ISession session) throws Exception
	{
		final String defaultCatalog = getDefaultCatalog(session);
		final String defaultSchema = getDefaultSchema(session);
		qualifier = new DatabaseObjectQualifier(defaultCatalog, defaultSchema);

		uniqueConstraintColName = fixIdentifierCase(session, uniqueConstraintColName);
		autoIncrementSequenceName = fixIdentifierCase(session, autoIncrementSequenceName);
		primaryKeyTestColumnOne = fixIdentifierCase(session, primaryKeyTestColumnOne);
		primaryKeyTestColumnTwo = fixIdentifierCase(session, primaryKeyTestColumnTwo);
		nullVarcharColumnName = fixIdentifierCase(session, nullVarcharColumnName);
		columnAddedColumnName = fixIdentifierCase(session, columnAddedColumnName);

		createTestTables(session);
		firstCol =
			getIntegerColumn("nullint", fixIdentifierCase(session, "test1"), true, "0", "An int comment");
		secondCol =
			getIntegerColumn("notnullint", fixIdentifierCase(session, "test2"), false, "0", "An int comment");
		thirdCol =
			getVarcharColumn(nullVarcharColumnName, fixIdentifierCase(session, "test3"), true, "defVal",
				"A varchar comment");
		fourthCol =
			getVarcharColumn("notnullvc", fixIdentifierCase(session, "test4"), false, "defVal",
				"A varchar comment");
		noDefaultValueVarcharCol =
			getVarcharColumn("noDefaultVarcharCol", fixIdentifierCase(session, "test"), true, null,
				"A varchar column with no default value");
		dropCol =
			getVarcharColumn("dropCol", fixIdentifierCase(session, "test5"), true, null, "A varchar comment");
		noDefaultValueIntegerCol =
			getIntegerColumn("noDefaultIntgerCol", fixIdentifierCase(session, "test5"), true, null,
				"An integer column with no default value");
		renameCol =
			getVarcharColumn("renameCol", fixIdentifierCase(session, "test"), true, null,
				"A column to be renamed");
		pkCol = getIntegerColumn("pkCol", fixIdentifierCase(session, "test"), false, "0", "primary key column");
		notNullIntegerCol =
			getIntegerColumn("notNullIntegerCol", fixIdentifierCase(session, "test5"), false, "0",
				"potential pk column");
		db2pkCol =
			getIntegerColumn(DB2_PK_COLNAME, fixIdentifierCase(session, "test"), false, "0",
				"A DB2 Primary Key column");

		// These two columns will be the only ones in the pktest table. They will
		// start out being nullable, and we will test that the dialect correctly
		// converts them to non-null then applies the PK constraint to them.
		// This test shall not be run against any database dialect that claims not
		// to support changing the nullability of a column.
		doubleColumnPKOne =
			getIntegerColumn(primaryKeyTestColumnOne, fixIdentifierCase(session, "pktest"), true, null,
				"an initially nullable field to be made part of a PK");
		doubleColumnPKTwo =
			getIntegerColumn(primaryKeyTestColumnTwo, fixIdentifierCase(session, "pktest"), true, null,
				"an initially nullable field to be made part of a PK");

		autoIncrementColumn =
			getIntegerColumn(autoIncrementColumnName, fixIdentifierCase(session, autoIncrementTableName), false,
				null, "Column that will be auto incrementing");

		addColumn =
			getIntegerColumn(columnAddedColumnName, fixIdentifierCase(session, testUniqueConstraintTableName),
				true, null, null);

		uniqueConstraintTableColumnInfo =
			getCharColumn(uniqueConstraintColName, fixIdentifierCase(session, testUniqueConstraintTableName),
				true, null, null);

		dropConstraintColumn =
			getCharColumn(secondUniqueColumnName, fixIdentifierCase(session, testUniqueConstraintTableName),
				true, null, null);
	}

	private String getDefaultCatalog(ISession session)
	{
		if (session instanceof MockSession) { return ((MockSession) session).getDefaultCatalog(); }
		return "defaultCatalog";
	}

	private String getDefaultSchema(ISession session)
	{
		if (session instanceof MockSession) { return ((MockSession) session).getDefaultSchema(); }
		return "defaultSchema";
	}

	private ITableInfo getTableInfo(ISession session, String tableName) throws Exception
	{
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final String catalog = getDefaultCatalog(session);
		final String schema = getDefaultSchema(session);
		System.out.println("Looking for table with catalog=" + catalog + " schema=" + schema + " tableName="
			+ tableName);
		ITableInfo[] infos = md.getTables(catalog, schema, tableName, TABLE_TYPES, null);
		if (infos.length == 0)
		{
			if (md.storesUpperCaseIdentifiers())
			{
				tableName = tableName.toUpperCase();
			}
			else
			{
				tableName = tableName.toLowerCase();
			}
			infos = md.getTables(catalog, schema, tableName, TABLE_TYPES, null);
			if (infos.length == 0) { return null; }
		}
		if (infos.length > 1) { throw new IllegalStateException("Found more than one table matching name="
			+ tableName);

		}

		return infos[0];

	}

	private void dropTable(ISession session, String tableName) throws Exception
	{
		// For some reason, Frontbase doesn't find tables that have been previously created.
		if (DialectFactory.isFrontBase(session.getMetaData())
			|| DialectFactory.isMSSQLServer(session.getMetaData()))
		{
			try
			{
				String dropSql = "drop table " + tableName;
				if (DialectFactory.isFrontBase(session.getMetaData()))
				{
					dropSql += " cascade";
				}
				runSQL(session, dropSql);
			}
			catch (final Exception e)
			{
				// quiet
			}
			return;
		}
		try
		{
			final ITableInfo ti = getTableInfo(session, tableName);
			if (ti == null)
			{
				System.out.println("Table " + tableName + " couldn't be dropped - doesn't exist");
				return;
			}
			dropTable(session, ti);
		}
		catch (final SQLException e)
		{
		}
	}

	private void dropView(ISession session, String viewName) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		if (dialect.supportsDropView())
		{
			try
			{
				runSQL(session, dialect.getDropViewSQL(viewName, false, qualifier, prefs));
			}
			catch (final SQLException e)
			{
				// Do Nothing
			}
		}
		else
		{
			System.err.println("Dialect doesn't appear to support dropping views");
		}
	}

	private void dropSequence(ISession session, String sequenceName) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		if (dialect.supportsDropSequence())
		{
			try
			{
				final String sql = dialect.getDropSequenceSQL(sequenceName, false, qualifier, prefs);

				runSQL(session, sql);
			}
			catch (final Exception e)
			{
				// Do nothing
			}
		}
	}

	private void dropTable(ISession session, ITableInfo ti) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		try
		{
			runSQL(session, dialect.getTableDropSQL(ti, true, false, qualifier, prefs));
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Setup the test tables. This used to be only one table but it grew due primarily to Ingres' inability to
	 * have a table with more than 5-10 columns.
	 * 
	 * @param session
	 * @throws Exception
	 */
	private void createTestTables(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);

		// views will depend on tables, so drop them first
		dropView(session, fixIdentifierCase(session, testViewName));
		dropView(session, fixIdentifierCase(session, testView2Name));
		dropView(session, fixIdentifierCase(session, testNewViewName));
		dropView(session, fixIdentifierCase(session, testViewToBeDropped));

		// Tables might have triggers that depend on sequences, so drop tables next.
		dropTable(session, fixIdentifierCase(session, "test"));
		dropTable(session, fixIdentifierCase(session, "test1"));
		dropTable(session, fixIdentifierCase(session, "test2"));
		dropTable(session, fixIdentifierCase(session, "test3"));
		dropTable(session, fixIdentifierCase(session, "test4"));
		dropTable(session, fixIdentifierCase(session, "test5"));
		dropTable(session, fixIdentifierCase(session, "pktest"));
		dropTable(session, fixIdentifierCase(session, testRenameTableBefore));
		dropTable(session, fixIdentifierCase(session, testRenameTableAfter));
		dropTable(session, fixIdentifierCase(session, testCreateViewTable));
		dropTable(session, fixIdentifierCase(session, testCreateIndexTable));
		dropTable(session, fixIdentifierCase(session, testTimestampTable));
		dropTable(session, fixIdentifierCase(session, testNewTimestampTable));
		dropTable(session, fixIdentifierCase(session, fkChildTableName));
		dropTable(session, fixIdentifierCase(session, fkParentTableName));
		dropTable(session, fixIdentifierCase(session, testUniqueConstraintTableName));
		dropTable(session, fixIdentifierCase(session, autoIncrementTableName));
		dropTable(session, fixIdentifierCase(session, integerDataTableName));
		dropTable(session, fixIdentifierCase(session, "a"));
		dropTable(session, fixIdentifierCase(session, testCreateTable));
		dropTable(session, fixIdentifierCase(session, testInsertIntoTable));
		dropTable(session, fixIdentifierCase(session, testFirstMergeTable));
		dropTable(session, fixIdentifierCase(session, testSecondMergeTable));
		dropTable(session, fixIdentifierCase(session, testTableForDropView));
		dropTable(session, fixIdentifierCase(session, testTableForGetViewDefinition));

		// Now sequences should go.
		dropSequence(session, testSequenceName);
		dropSequence(session, testSequenceName2);
		dropSequence(session, autoIncrementTableName + "_MYID_SEQ");
		dropSequence(session, autoIncrementColumnName + "_AUTOINC_SEQ");
		dropSequence(session, autoIncrementSequenceName);

		if (DialectFactory.isOracle(session.getMetaData()))
		{
			dropTable(session, fixIdentifierCase(session, "matview"));
		}

		String pageSizeClause = "";

		if (DialectFactory.isIngres(session.getMetaData()))
		{
			// alterations fail for some reason unless you do this...
			pageSizeClause = " with page_size=4096";
		}

		if (DialectFactory.isDB2(session.getMetaData()))
		{
			// db2pkCol is used to create a PK when using DB2. DB2 doesn't allow
			// you to add a PK to a table after it has been constructed unless the
			// column(s) that comprise the PK were originally there when created
			// *and* created not null.
			runSQL(session, "create table " + fixIdentifierCase(session, "test") + " ( mychar char(10), "
				+ DB2_PK_COLNAME + " integer not null)");
		}
		else
		{
			runSQL(session, "create table " + fixIdentifierCase(session, "test") + " ( mychar char(10))"
				+ pageSizeClause);
		}
		fkParentColumnName = fixIdentifierCase(session, fkParentColumnName);
		fkChildColumnName = fixIdentifierCase(session, fkChildColumnName);
		uniqueConstraintColName = fixIdentifierCase(session, uniqueConstraintColName);
		secondUniqueColumnName = fixIdentifierCase(session, secondUniqueColumnName);
		indexTestColumnName = fixIdentifierCase(session, indexTestColumnName);
		uniqueIndexTestColumnName = fixIdentifierCase(session, uniqueIndexTestColumnName);
		autoIncrementColumnName = fixIdentifierCase(session, autoIncrementColumnName);
		fkChildPrimaryKeyColumnName = fixIdentifierCase(session, fkChildPrimaryKeyColumnName);

		runSQL(session, "create table " + fixIdentifierCase(session, "test1") + " ( mychar char(10))"
			+ pageSizeClause);
		runSQL(session, "create table " + fixIdentifierCase(session, "test2") + " ( mychar char(10))"
			+ pageSizeClause);
		runSQL(session, "create table " + fixIdentifierCase(session, "test3") + " ( mychar char(10))"
			+ pageSizeClause);
		runSQL(session, "create table " + fixIdentifierCase(session, "test4") + " ( mychar char(10))"
			+ pageSizeClause);
		runSQL(session, "create table " + fixIdentifierCase(session, "test5") + " ( mychar char(10))"
			+ pageSizeClause);
		runSQL(session, "create table " + fixIdentifierCase(session, testRenameTableBefore)
			+ " ( mychar char(10))" + pageSizeClause);
		runSQL(session, "create table " + fixIdentifierCase(session, testCreateViewTable)
			+ " ( mychar char(10))" + pageSizeClause);
		// MySQL spatial index requires a not null column
		runSQL(session, "create table " + fixIdentifierCase(session, testCreateIndexTable) + " ( "
			+ indexTestColumnName + " varchar(10) not null, " + uniqueIndexTestColumnName + " varchar(10))"
			+ pageSizeClause);
		/* DB2 requires primary keys to also be declared "not null" */
		runSQL(session, "create table " + fixIdentifierCase(session, fkParentTableName) + " ( "
			+ fkParentColumnName + " integer not null primary key, mychar char(10))" + pageSizeClause);
		runSQL(session, "create table " + fixIdentifierCase(session, fkChildTableName) + " ( "
			+ fkChildPrimaryKeyColumnName + " integer, " + fkChildColumnName + " integer)" + pageSizeClause);

		runSQL(session, "create table " + fixIdentifierCase(session, testUniqueConstraintTableName) + " ( "
			+ uniqueConstraintColName + " char(10), " + secondUniqueColumnName + " char(10))" + pageSizeClause);

		runSQL(session, "create table " + fixIdentifierCase(session, autoIncrementTableName) + " ( "
			+ autoIncrementColumnName + " integer)" + pageSizeClause);

		runSQL(session, "create table " + fixIdentifierCase(session, integerDataTableName) + " ( myid integer)"
			+ pageSizeClause);

		int count = 0;
		while (count++ < 11)
		{
			runSQL(session, "insert into " + fixIdentifierCase(session, integerDataTableName) + " values ("
				+ count + ")");
		}

		runSQL(session, "CREATE TABLE " + fixIdentifierCase(session, "a") + "( "
			+ "   acol int NOT NULL PRIMARY KEY, " + "   adesc varchar(10), " + "   bdesc varchar(10),"
			+ "   joined varchar(20) " + ") ");

		runSQL(session, "INSERT INTO " + fixIdentifierCase(session, "a")
			+ " (acol,adesc,bdesc,joined) VALUES (1,'a1','b1','a1b1') ");

		runSQL(session, "INSERT INTO " + fixIdentifierCase(session, "a")
			+ " (acol,adesc,bdesc,joined) VALUES (2,'a2','b2','a2b2') ");

		runSQL(session, "INSERT INTO " + fixIdentifierCase(session, "a")
			+ " (acol,adesc,bdesc,joined) VALUES (3,'a3','b3','a3b3') ");

		if (dialect.supportsAlterColumnNull())
		{
			runSQL(session, "create table " + fixIdentifierCase(session, "pktest") + " ( "
				+ primaryKeyTestColumnOne + " integer, " + primaryKeyTestColumnTwo + " integer )"
				+ pageSizeClause);
		}

		runSQL(session, "create table " + fixIdentifierCase(session, testInsertIntoTable) + " ( myid integer)"
			+ pageSizeClause);

		runSQL(session, "create table " + fixIdentifierCase(session, testFirstMergeTable)
			+ " ( myid integer, desc_t1 varchar(20))" + pageSizeClause);

		runSQL(session, "INSERT INTO " + fixIdentifierCase(session, testFirstMergeTable)
			+ " (myid, desc_t1) VALUES (1,'table1-row1') ");
		runSQL(session, "INSERT INTO " + fixIdentifierCase(session, testFirstMergeTable)
			+ " (myid, desc_t1) VALUES (2,'table1-row2') ");
		runSQL(session, "INSERT INTO " + fixIdentifierCase(session, testFirstMergeTable)
			+ " (myid, desc_t1) VALUES (3,'table1-row3') ");

		runSQL(session, "create table " + fixIdentifierCase(session, testSecondMergeTable)
			+ " ( myid integer, desc_t2 varchar(20))" + pageSizeClause);

		runSQL(session, "INSERT INTO " + fixIdentifierCase(session, testSecondMergeTable)
			+ " (myid, desc_t2) VALUES (1,'table2-row1') ");
		runSQL(session, "INSERT INTO " + fixIdentifierCase(session, testSecondMergeTable)
			+ " (myid, desc_t2) VALUES (2,'table2-row2') ");
		runSQL(session, "INSERT INTO " + fixIdentifierCase(session, testSecondMergeTable)
			+ " (myid, desc_t2) VALUES (3,'table2-row3') ");

		runSQL(session, "create table " + fixIdentifierCase(session, testTableForDropView)
			+ " ( myid integer, mydesc varchar(20))" + pageSizeClause);

		runSQL(session, "create table " + fixIdentifierCase(session, testTableForGetViewDefinition)
			+ " ( myid integer, mydesc varchar(20))" + pageSizeClause);

	}

	private void runTests(boolean quoteIdentifiers, boolean qualifyTableNames) throws Exception
	{
		for (final ISession session : sessions)
		{
			prefs.setQualifyTableNames(qualifyTableNames);
			prefs.setQuoteIdentifiers(quoteIdentifiers);
			prefs.setQuoteConstraintNames(true);

			final HibernateDialect dialect = getDialect(session);
			final DialectType dialectType = DialectFactory.getDialectType(session.getMetaData());
			if (dialectType == DialectType.SYBASEASE || dialectType == DialectType.MSSQL)
			{
				prefs.setSqlStatementSeparator("GO");
			}
			init(session);
			testAddColumn(session);
			testDropColumn(session);
			testAlterDefaultValue(session);
			testAlterColumnComment(session);
			testAlterNull(session);
			testAlterColumnName(session);
			testAlterColumnlength(session);
			// DB2 cannot alter a column's null attribute directly (only
			// through constraints). Not only that, but it's apparently not a
			// valid thing to do to create a primary key using a column that has
			// been made "not null" via a check constraint. Therefore, the only
			// columns that qualify to be made PKs are those that were declared
			// not null at the time of table creation.
			if (DialectFactory.isDB2(session.getMetaData()))
			{
				testAddPrimaryKey(session, new TableColumnInfo[] { db2pkCol });
				testDropPrimaryKey(session, db2pkCol.getTableName());
			}
			else
			{
				try
				{
					testAddPrimaryKey(session, new TableColumnInfo[] { notNullIntegerCol });
				}
				catch (final UnsupportedOperationException e)
				{
					System.err.println("doesn't support adding primary keys");
				}
				try
				{
					testDropPrimaryKey(session, notNullIntegerCol.getTableName());
				}
				catch (final UnsupportedOperationException e)
				{
					System.err.println("doesn't support dropping primary keys");
				}

				// Test whether or not the dialect correctly converts nullable
				// columns to not-null before applying the primary key - if
				// necessary
				if (dialect.supportsAlterColumnNull())
				{
					try
					{
						final TableColumnInfo[] infos =
							new TableColumnInfo[] { doubleColumnPKOne, doubleColumnPKTwo };
						testAddPrimaryKey(session, infos);
					}
					catch (final UnsupportedOperationException e)
					{
						System.err.println("doesn't support adding primary keys");
					}
				}
			}
			testSupportsIndexes(session);
			testGetMaxPrecision(session);
			testDropMatView(session);
			testCreateTableWithDefault(session);
			testCreateTableSql(session);
			testRenameTable(session);
			testCreateView(session);
			testRenameView(session);
			testDropView(session);
			testGetViewDefinition(session);
			testCreateAndDropIndex(session);
			testCreateSequence(session);
			testGetSequenceInfo(session);
			testAlterSequence(session);
			testDropSequence(session);
			testAddForeignKeyConstraint(session);
			testAddUniqueConstraint(session);
			testAddAutoIncrement(session);
			testDropConstraint(session);
			testInsertIntoSQL(session);
			testAddColumnSQL(session);
			testUpdateSQL(session);
			testMergeTable(session);
			testDataScript(session);
			System.out.println("Completed tests for " + dialect.getDisplayName());
			session.close();
		}
	}

	private void testSupportsIndexes(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		if (dialect.supportsCreateIndex() || dialect.supportsDropIndex())
		{
			assertTrue(dialect.getDisplayName()
				+ " dialect claims not to support indexes; yet it can create or drop them",
				dialect.supportsIndexes());
		}
		else
		{
			assertFalse(" Dialect (" + dialect.getDisplayName() + ")claims to support indexes; "
				+ "yet it can't create them nor can it drop them", dialect.supportsIndexes());
		}
	}

	private void testGetMaxPrecision(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		for (final String jdbcTypeName : JDBCTypeMapper.getJdbcTypeList())
		{
			assertTrue(dialect.getMaxPrecision(JDBCTypeMapper.getJdbcType(jdbcTypeName)) > 0);
		}
	}

	/**
	 * CREATE MATERIALIZED VIEW matview2 REFRESH COMPLETE NEXT SYSDATE + 1 WITH PRIMARY KEY AS SELECT * FROM
	 * TEST;
	 * 
	 * @param session
	 * @throws Exception
	 */
	private void testDropMatView(ISession session) throws Exception
	{
		if (!DialectFactory.isOracle(session.getMetaData())) { return; }
		final HibernateDialect dialect = getDialect(session);

		final String dropMatViewSql = "DROP MATERIALIZED VIEW MATVIEW ";

		try
		{
			runSQL(session, dropMatViewSql);
		}
		catch (final Exception e)
		{
			// Don't care if it's not there
		}

		testAddPrimaryKey(session, new TableColumnInfo[] { pkCol });
		final String createMatViewSQL =
			"CREATE MATERIALIZED VIEW MATVIEW " + "       REFRESH COMPLETE " + "   NEXT  SYSDATE + 1 "
				+ "   WITH PRIMARY KEY " + "   AS SELECT * FROM TEST ";
		runSQL(session, createMatViewSQL);
		final MockSession msession = (MockSession) session;
		final String cat = msession.getDefaultCatalog();
		final String schema = msession.getDefaultSchema();
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final ITableInfo info = new TableInfo(cat, schema, "MATVIEW", "TABLE", "", md);
		final List<String> dropSQL = dialect.getTableDropSQL(info, true, true, qualifier, prefs);
		runSQL(session, dropSQL);
	}

	private void testAlterColumnName(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);

		final TableColumnInfo newNameCol =
			getVarcharColumn("newNameCol", "test", true, null, "A column to be renamed");

		final AlterColumnNameSqlExtractor extractor = new AlterColumnNameSqlExtractor(renameCol, newNameCol);
		if (dialectDiscoveryMode)
		{
			findSQL(session, extractor);
			return;
		}

		if (dialect.supportsRenameColumn())
		{
			final String sql = dialect.getColumnNameAlterSQL(renameCol, newNameCol, qualifier, prefs);

			runSQL(session, new String[] { sql }, extractor);
		}
		else
		{
			try
			{
				dialect.getColumnNameAlterSQL(renameCol, newNameCol, qualifier, prefs);
				failDialect(dialect, "renaming a column");
			}
			catch (final UnsupportedOperationException e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void testDropColumn(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);

		if (dialect.supportsDropColumn())
		{
			dropColumn(session, dropCol);
		}
		else
		{
			try
			{
				dropColumn(session, dropCol);
				failDialect(dialect, "dropping a column");
			}
			catch (final UnsupportedOperationException e)
			{
				// This is what we expect
				System.err.println(e.getMessage());
			}
		}
	}

	private void testAlterColumnlength(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);

		// convert nullint into a varchar(100)
		/*
		 * This won't work on Derby where non-varchar columns cannot be altered
		 * among other restrictions.
		 * 
		 * TableColumnInfo nullintVC = getVarcharColumn("nullint", true, "defVal",
		 * "A varchar comment"); String alterColTypeSQL =
		 * dialect.getColumnTypeAlterSQL(firstCol, nullintVC); runSQL(session,
		 * alterColTypeSQL);
		 */
		final TableColumnInfo thirdColLonger =
			getVarcharColumn(nullVarcharColumnName, fixIdentifierCase(session, "test3"), true, "defVal",
				"A varchar comment", 30);

		final AlterColumnTypeSqlExtractor extractor = new AlterColumnTypeSqlExtractor(thirdCol, thirdColLonger);
		if (dialectDiscoveryMode)
		{
			findSQL(session, extractor);
			return;
		}

		if (dialect.supportsAlterColumnType())
		{
			final List<String> alterColLengthSQL =
				dialect.getColumnTypeAlterSQL(thirdCol, thirdColLonger, qualifier, prefs);
			runSQL(session, alterColLengthSQL.toArray(new String[alterColLengthSQL.size()]), extractor);
		}
		else
		{
			try
			{
				dialect.getColumnTypeAlterSQL(thirdCol, thirdColLonger, qualifier, prefs);
				failDialect(dialect, "altering column type");
			}
			catch (final UnsupportedOperationException e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void testAlterDefaultValue(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		final TableColumnInfo varcharColWithDefaultValue =
			getVarcharColumn("noDefaultVarcharCol", noDefaultValueVarcharCol.getTableName(), true,
				"Default Value", "A column with a default value");

		final TableColumnInfo integerColWithDefaultVal =
			getIntegerColumn("noDefaultIntgerCol", noDefaultValueIntegerCol.getTableName(), true, "0",
				"An integer column with a default value");

		final AlterDefaultValueSqlExtractor extractor =
			new AlterDefaultValueSqlExtractor(varcharColWithDefaultValue);
		if (dialectDiscoveryMode)
		{
			findSQL(session, extractor);
			return;
		}

		if (dialect.supportsAlterColumnDefault())
		{
			String defaultValSQL =
				dialect.getColumnDefaultAlterSQL(varcharColWithDefaultValue, qualifier, prefs);
			runSQL(session, new String[] { defaultValSQL }, extractor);

			defaultValSQL = dialect.getColumnDefaultAlterSQL(integerColWithDefaultVal, qualifier, prefs);
			runSQL(session, new String[] { defaultValSQL }, extractor);

		}
		else
		{
			try
			{
				dialect.getColumnDefaultAlterSQL(noDefaultValueVarcharCol, qualifier, prefs);
				failDialect(dialect, "column default alter");
			}
			catch (final UnsupportedOperationException e)
			{
				// This is what we expect.
				System.err.println(e.getMessage());
			}
		}
	}

	private void testAlterNull(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		final String tableName = fixIdentifierCase(session, "test3");
		final TableColumnInfo notNullThirdCol =
			getVarcharColumn(fixIdentifierCase(session, "nullvc"), tableName, false, "defVal",
				"A varchar comment");

		final AlterColumnNullSqlExtractor extractor = new AlterColumnNullSqlExtractor(notNullThirdCol);
		if (dialectDiscoveryMode)
		{
			findSQL(session, extractor);
			return;
		}

		if (dialect.supportsAlterColumnNull())
		{
			final String[] notNullSQL = dialect.getColumnNullableAlterSQL(notNullThirdCol, qualifier, prefs);
			runSQL(session, notNullSQL, extractor);
		}
		else
		{
			try
			{
				dialect.getColumnNullableAlterSQL(notNullThirdCol, qualifier, prefs);
				failDialect(dialect, "column nullable alter");
			}
			catch (final UnsupportedOperationException e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void testAddColumn(ISession session) throws Exception
	{
		addColumn(session, firstCol);
		addColumn(session, secondCol);
		addColumn(session, thirdCol);
		addColumn(session, fourthCol);
		addColumn(session, dropCol);
		addColumn(session, noDefaultValueVarcharCol);
		addColumn(session, noDefaultValueIntegerCol);
		addColumn(session, renameCol);
		addColumn(session, pkCol);
		addColumn(session, notNullIntegerCol);
	}

	private void addColumn(ISession session, TableColumnInfo info) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		final String[] sqls = dialect.getAddColumnSQL(info, qualifier, prefs);
		for (final String sql : sqls)
		{
			runSQL(session, sql);
		}

	}

	private void testAlterColumnComment(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);

		final AlterColumnCommentSqlExtractor extractor = new AlterColumnCommentSqlExtractor(firstCol);
		if (dialectDiscoveryMode)
		{
			findSQL(session, extractor);
			return;
		}

		if (dialect.supportsColumnComment())
		{
			alterColumnComment(session, firstCol);
			alterColumnComment(session, secondCol);
			alterColumnComment(session, thirdCol);
			alterColumnComment(session, fourthCol);
		}
		else
		{
			try
			{
				alterColumnComment(session, firstCol);
				failDialect(dialect, "column comment alter");
			}
			catch (final UnsupportedOperationException e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void alterColumnComment(ISession session, TableColumnInfo info) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		final String catalog = getDefaultCatalog(session);
		final String schema = getDefaultSchema(session);
		final DatabaseObjectQualifier qual = new DatabaseObjectQualifier(catalog, schema);

		final AlterColumnCommentSqlExtractor extractor = new AlterColumnCommentSqlExtractor(info);
		final String commentSQL = dialect.getColumnCommentAlterSQL(info, qual, prefs);
		runSQL(session, new String[] { commentSQL }, extractor);
	}

	private String getPKName(String tableName)
	{
		return tableName.toUpperCase() + "_PK";
	}

	private void testDropPrimaryKey(ISession session, String tableName) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		final String pkName = getPKName(tableName);

		final DropPrimaryKeySqlExtractor dropPrimaryKeySqlExtractor =
			new DropPrimaryKeySqlExtractor(tableName, pkName);

		if (dialectDiscoveryMode)
		{
			findSQL(session, dropPrimaryKeySqlExtractor);
			return;
		}

		if (dialect.supportsDropPrimaryKey())
		{
			final String sql = dialect.getDropPrimaryKeySQL(pkName, tableName, qualifier, prefs);

			runSQL(session, new String[] { sql }, dropPrimaryKeySqlExtractor);
		}
		else
		{
			try
			{
				dialect.getDropPrimaryKeySQL(pkName, tableName, qualifier, prefs);
				failDialect(dialect, "dropping a primary key");
			}
			catch (final UnsupportedOperationException e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}

	}

	private void testAddPrimaryKey(ISession session, TableColumnInfo[] colInfos) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);

		String tableName = colInfos[0].getTableName();

		if (session.getSQLConnection().getSQLMetaData().storesUpperCaseIdentifiers())
		{
			tableName = tableName.toUpperCase();
		}

		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		final String catalog = getDefaultCatalog(session);
		final String schema = getDefaultSchema(session);

		ITableInfo[] infos = null;
		try
		{
			infos = md.getTables(catalog, schema, tableName, TABLE_TYPES, null);
		}
		catch (final SQLException e)
		{
			// Do nothing
		}

		ITableInfo ti = null;
		if (infos != null && infos.length > 0)
		{
			ti = infos[0];
		}
		else
		{
			// Couldn't locate the table - just try to fake it.
			ti = new TableInfo(catalog, schema, tableName, "TABLE", "", md);
		}

		final String pkName = getPKName(tableName);
		final AddPrimaryKeySqlExtractor extractor = new AddPrimaryKeySqlExtractor(ti, colInfos, pkName);
		if (dialectDiscoveryMode)
		{
			findSQL(session, extractor);
			return;
		}

		if (dialect.supportsAddPrimaryKey())
		{
			final String[] pkSQLs = dialect.getAddPrimaryKeySQL(pkName, colInfos, ti, qualifier, prefs);

			for (final String pkSQL : pkSQLs)
			{
				runSQL(session, new String[] { pkSQL }, extractor);
			}

		}
		else
		{
			try
			{
				dialect.getAddPrimaryKeySQL(pkName, colInfos, ti, qualifier, prefs);
				failDialect(dialect, "adding a primary key");
			}
			catch (final Exception e)
			{
				// This is expected
				System.err.println(e.getMessage());
			}

		}
	}

	private void testCreateTableWithDefault(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);

		if (!dialect.supportsCreateTable()) { return; }

		final String testTableName = "TESTDEFAULT";

		dropTable(session, testTableName);

		/* create a table with a default value to use as the model */
		final String createSql = "create table " + testTableName + " ( mycol integer default 0 )";

		runSQL(session, createSql);

		/* Build the sql statement(s) to create the table */
		ITableInfo tableInfo = getTableInfo(session, testTableName);
		if (tableInfo == null)
		{
			tableInfo = getTableInfo(session, testTableName.toLowerCase());
		}
		if (tableInfo == null)
		{
			tableInfo = getTableInfo(session, testTableName.toUpperCase());
		}

		assertNotNull("Couldn't locate table (" + testTableName + ") that was just created with statement ("
			+ createSql + ").  Dialect=" + dialect.getDisplayName(), tableInfo);

		final CreateScriptPreferences prefs = new CreateScriptPreferences();
		final List<String> sqls =
			dialect.getCreateTableSQL(Arrays.asList(tableInfo), session.getMetaData(), prefs, false);

		/* drop the table so that we can test our create statement */
		dropTable(session, tableInfo);

		/* Test the create statement */
		for (final String sql : sqls)
		{
			runSQL(session, sql);
		}
	}

	private void testCreateTableSql(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);

		final List<TableColumnInfo> columns = new ArrayList<TableColumnInfo>();
		final String catalog = getDefaultCatalog(session);
		final String schema = getDefaultSchema(session);
		final String columnName = "firstCol";
		final String tableName = fixIdentifierCase(session, testCreateTable);

		final TableColumnInfo firstCol =
			new TableColumnInfo(catalog, schema, testCreateTable, columnName, Types.BIGINT, "BIGINT", 10, 0, 0,
				1, null, null, 0, 0, "YES");
		columns.add(firstCol);
		final List<TableColumnInfo> pkColumns = null;
		final CreateTableSqlExtractor extractor = new CreateTableSqlExtractor(tableName, columns, pkColumns);

		if (dialectDiscoveryMode)
		{
			findSQL(session, extractor);
			return;
		}

		if (dialect.supportsCreateTable())
		{
			final String sql = dialect.getCreateTableSQL(tableName, columns, pkColumns, prefs, qualifier);
			runSQL(session, new String[] { sql }, extractor);
		}
		else
		{
			try
			{
				dialect.getCreateTableSQL(tableName, columns, pkColumns, prefs, qualifier);
				failDialect(dialect, "creating a table");
			}
			catch (final Exception e)
			{
				// This is expected
				System.err.println(e.getMessage());
			}
		}

	}

	private void testRenameTable(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		final String oldTableName = fixIdentifierCase(session, testRenameTableBefore);
		final String newTableName = fixIdentifierCase(session, testRenameTableAfter);

		final RenameTableSqlExtractor renameTableSqlExtractor =
			new RenameTableSqlExtractor(oldTableName, newTableName);

		if (dialectDiscoveryMode)
		{
			findSQL(session, renameTableSqlExtractor);
			return;
		}

		if (dialect.supportsRenameTable())
		{
			final String sql = dialect.getRenameTableSQL(oldTableName, newTableName, qualifier, prefs);

			runSQL(session, new String[] { sql }, renameTableSqlExtractor);
		}
		else
		{
			try
			{
				dialect.getRenameTableSQL(oldTableName, newTableName, qualifier, prefs);
				failDialect(dialect, "renaming a table");
			}
			catch (final Exception e)
			{
				// This is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void testCreateView(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);

		String checkOption = "";
		final String tableToView = fixIdentifierCase(session, testCreateViewTable);
		final String firstViewName = fixIdentifierCase(session, testViewName);
		final String secondViewName = fixIdentifierCase(session, testView2Name);
		final String definition = "select * from " + tableToView;
		final CreateViewSqlExtractor extractor =
			new CreateViewSqlExtractor(firstViewName, definition, checkOption);
		if (dialectDiscoveryMode)
		{
			findSQL(session, extractor);
			return;
		}

		if (dialect.supportsCreateView())
		{

			String sql = dialect.getCreateViewSQL(firstViewName, definition, checkOption, qualifier, prefs);
			runSQL(session, new String[] { sql }, extractor);

			if (dialect.supportsCheckOptionsForViews())
			{
				checkOption = "some_check";
				sql = dialect.getCreateViewSQL(secondViewName, definition, checkOption, qualifier, prefs);
				runSQL(session, new String[] { sql }, new CreateViewSqlExtractor(secondViewName, definition,
					checkOption));
			}

		}
		else
		{
			try
			{
				dialect.getCreateViewSQL(testViewName, "select * from " + testCreateViewTable, checkOption,
					qualifier, prefs);
				failDialect(dialect, "creating a view");
			}
			catch (final Exception e)
			{
				// This is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void testRenameView(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		final String oldViewName = fixIdentifierCase(session, testViewName);
		final String newViewName = fixIdentifierCase(session, testNewViewName);
		final String catalog = getDefaultCatalog(session);
		final String schema = getDefaultSchema(session);
		final DatabaseObjectQualifier qual = new DatabaseObjectQualifier(catalog, schema);

		final RenameViewSqlExtractor renameViewSqlExtractor =
			new RenameViewSqlExtractor(oldViewName, newViewName);

		if (dialectDiscoveryMode)
		{
			findSQL(session, renameViewSqlExtractor);
			return;
		}

		if (dialect.supportsRenameView())
		{
			final String[] sql = dialect.getRenameViewSQL(oldViewName, newViewName, qual, prefs);

			runSQL(session, sql, renameViewSqlExtractor);
		}
		else if (dialect.supportsViewDefinition() && dialect.supportsCreateView())
		{

			final String viewDefSql = dialect.getViewDefinitionSQL(oldViewName, qual, prefs);
			final ResultSet rs = this.runQuery(session, viewDefSql);
			final StringBuilder tmp = new StringBuilder();
			while (rs.next())
			{
				tmp.append(rs.getString(1));
			}
			String viewDefinition = tmp.toString();

			if (viewDefinition == null || "".equals(viewDefinition)) { throw new IllegalStateException(
				"View source was not retrieved by this query: " + viewDefSql); }
			final int asIndex = viewDefinition.toUpperCase().indexOf("AS");
			if (asIndex != -1)
			{
				viewDefinition = "create view " + newViewName + " as " + viewDefinition.substring(asIndex + 2);
			}

			final String dropOldViewSql = dialect.getDropViewSQL(oldViewName, false, qual, prefs);
			runSQL(session, new String[] { viewDefinition, dropOldViewSql });
		}
		else
		{
			try
			{
				dialect.getRenameViewSQL(oldViewName, newViewName, qual, prefs);
				failDialect(dialect, "renaming a view");
			}
			catch (final Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void testDropView(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		final String tableName = fixIdentifierCase(session, testTableForDropView);
		final String viewName = fixIdentifierCase(session, testViewToBeDropped);

		final DropViewSqlExtractor dropViewSqlExtractor = new DropViewSqlExtractor(viewName, false);
		if (dialectDiscoveryMode)
		{
			findSQL(session, dropViewSqlExtractor);
			return;
		}

		if (dialect.supportsCreateView() && dialect.supportsDropView())
		{

			String sql =
				dialect.getCreateViewSQL(viewName, "select * from " + tableName, null, qualifier, prefs);
			runSQL(session, sql);

			sql = dialect.getDropViewSQL(viewName, false, qualifier, prefs);

			runSQL(session, new String[] { sql }, dropViewSqlExtractor);
		}
		else
		{
			if (!dialect.supportsDropView())
			{
				try
				{
					dialect.getDropViewSQL(viewName, false, qualifier, prefs);
					failDialect(dialect, "dropping a view");
				}
				catch (final Exception e)
				{
					// this is expected
					System.err.println(e.getMessage());
				}
			}
		}
	}

	private void testGetViewDefinition(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		final String tableName = fixIdentifierCase(session, testTableForGetViewDefinition);
		final String viewName = fixIdentifierCase(session, testViewToBeDropped);

		final GetViewdefinitionSqlExtractor extractor = new GetViewdefinitionSqlExtractor(viewName);
		if (dialectDiscoveryMode)
		{
			findSQL(session, extractor);
			return;
		}
		if (dialect.supportsCreateView() && dialect.supportsDropView() && dialect.supportsViewDefinition())
		{

			/* create a test view that we will get view def for then drop and re-create */
			final String createViewSql =
				dialect.getCreateViewSQL(viewName, "select * from " + tableName, null, qualifier, prefs);
			runSQL(session, createViewSql);

			/* 
			 * viewDefSql is the SQL that can be used to query the data dictionary for the body of a view. 
			 * This should exclude the "CREATE VIEW <viewname> AS" prefix and just return the query.
			 */
			final String viewDefSql = dialect.getViewDefinitionSQL(viewName, qualifier, prefs);
			final ResultSet rs = runQuery(session, viewDefSql);
			String viewBody = "";
			if (rs.next())
			{
				viewBody = rs.getString(1);
			}
			else
			{
				fail("viewDefSql () returned no view body for dialect: " + dialect.getDisplayName());
			}
			assertNotNull(viewBody);
			assertTrue("Expected view body to have larger than zero length", viewBody.length() > 0);

			/*
			 * Now that we have run the view definition query, and retrieved the SQL that is the body of the view
			 * (that is, it's query part minus the "CREATE VIEW AS" prefix), we can drop the view. 
			 */
			final String dropViewSql = dialect.getDropViewSQL(viewName, false, qualifier, prefs);
			runSQL(session, new String[] { dropViewSql }, extractor);

		}
		else
		{
			try
			{
				dialect.getViewDefinitionSQL(viewName, qualifier, prefs);
				failDialect(dialect, "getting the view definition");
			}
			catch (final Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}

	}

	private void testCreateAndDropIndex(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		String[] columns = new String[] { indexTestColumnName };
		final String tablespace = null;
		final String constraints = null;

		final String indexName1 = "testIndex";
		final String indexName2 = "testUniqueIndex";
		final String tableName = fixIdentifierCase(session, testCreateIndexTable);

		if (dialect.supportsCreateIndex() && dialect.supportsDropIndex())
		{

			String[] accessMethods = null;
			if (dialect.supportsAccessMethods())
			{
				accessMethods = dialect.getIndexAccessMethodsTypes();
			}
			else
			{
				accessMethods = new String[] { "" };
			}

			for (final String accessMethod : accessMethods)
			{
				// Postgres has some special indexes that only work on certain columns. Skip tests for those
				if (accessMethod.equalsIgnoreCase("gin") || accessMethod.equalsIgnoreCase("gist"))
				{
					continue;
				}
				// MySQL requires MyISAM storage engine for spatial and fulltext indexes. Don't test them
				// for now. See the TODO in the MySQLDialect for getCreateIndexSQL.
				if (DialectFactory.isMySQL5(session.getMetaData())
					|| DialectFactory.isMySQL(session.getMetaData()))
				{
					if (accessMethod.equalsIgnoreCase("spatial") || accessMethod.equalsIgnoreCase("fulltext"))
					{
						continue;
					}
				}
				// create a non-unique index on mychar
				columns = new String[] { indexTestColumnName };
				String sql =
					dialect.getCreateIndexSQL(indexName1, tableName, accessMethod, columns, false, tablespace,
						constraints, qualifier, prefs);

				final CreateIndexSqlExtractor extractor =
					new CreateIndexSqlExtractor(indexName1, tableName, accessMethod, columns, false, tablespace,
						constraints);

				// create it.
				runSQL(session, new String[] { sql }, extractor);

				// create a unique index on myuniquechar
				columns = new String[] { uniqueIndexTestColumnName };
				sql =
					dialect.getCreateIndexSQL(indexName2, tableName, accessMethod, columns, true, tablespace,
						constraints, qualifier, prefs);

				final CreateIndexSqlExtractor extractor2 =
					new CreateIndexSqlExtractor(indexName2, tableName, accessMethod, columns, true, tablespace,
						constraints);

				// create it.
				runSQL(session, new String[] { sql }, extractor2);

				// now drop the second.
				String dropIndexSQL = dialect.getDropIndexSQL(tableName, indexName2, true, qualifier, prefs);

				// TODO: Remove this when Frontbase bug gets fixed.
				// For some reason, Frontbase renames the unique indexes to a generated value. Since we don't
				// know what that new name for the index is, skip dropping it for now.
				if (!DialectFactory.isFrontBase(session.getMetaData()))
				{
					runSQL(session, new String[] { dropIndexSQL }, new DropIndexSqlExtractor(tableName,
						indexName2, true));
				}

				// now drop the first
				dropIndexSQL = dialect.getDropIndexSQL(tableName, indexName1, true, qualifier, prefs);
				try
				{
					runSQL(session, dropIndexSQL);
				}
				catch (final Exception e)
				{
					// Progress throws an exception if you try to drop the first index that was created
				}

			}
		}
		else
		{
			if (!dialect.supportsCreateIndex())
			{
				try
				{
					dialect.getCreateIndexSQL(indexName1, tableName, null, columns, false, tablespace,
						constraints, qualifier, prefs);
					failDialect(dialect, "for creating an index");
				}
				catch (final Exception e)
				{
					// this is expected
					System.err.println(e.getMessage());
				}
			}
			if (!dialect.supportsDropIndex())
			{
				try
				{
					dialect.getDropIndexSQL(tableName, indexName1, false, qualifier, prefs);
					failDialect(dialect, "for dropping an index");
				}
				catch (final Exception e)
				{
					// this is expected
					System.err.println(e.getMessage());
				}
			}
		}
	}

	private void testCreateSequence(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		final String cache = null;
		boolean cycle = true;
		final CreateSequenceSqlExtractor extractor =
			new CreateSequenceSqlExtractor(testSequenceName, "1", "1", "100", "1", cache, cycle);

		if (dialectDiscoveryMode)
		{
			findSQL(session, extractor);
			return;
		}

		if (dialect.supportsCreateSequence())
		{

			String sql =
				dialect.getCreateSequenceSQL(testSequenceName, "1", "1", "100", "1", cache, cycle, qualifier,
					prefs);
			runSQL(session, new String[] { sql }, extractor);

			cycle = false;
			sql =
				dialect.getCreateSequenceSQL(testSequenceName2, "1", "1", "100", "1", cache, cycle, qualifier,
					prefs);
			runSQL(session, new String[] { sql }, new CreateSequenceSqlExtractor(testSequenceName2, "1", "1",
				"100", "1", cache, cycle));
		}
		else
		{
			try
			{
				dialect.getCreateSequenceSQL(testSequenceName, "1", "1", "100", "1", cache, cycle, qualifier,
					prefs);
				failDialect(dialect, "for creating a sequence");
			}
			catch (final Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void testGetSequenceInfo(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);

		final SequenceInfoSqlExtractor sequenceInfoSqlExtractor =
			new SequenceInfoSqlExtractor(testSequenceName);
		if (dialectDiscoveryMode)
		{
			findSQL(session, sequenceInfoSqlExtractor);
			return;
		}

		if (dialect.supportsSequence() && dialect.supportsSequenceInformation())
		{
			final String sql = dialect.getSequenceInformationSQL(testSequenceName, qualifier, prefs);
			if (sql.endsWith("?") || sql.endsWith("?)"))
			{
				final ResultSet rs = runPreparedQuery(session, sql, testSequenceName);
				if (!rs.next()) { throw new IllegalStateException("Expected a result from sequence info query"); }
			}
			else
			{
				runSQL(session, new String[] { sql }, sequenceInfoSqlExtractor);
			}

		}
		else
		{
			try
			{
				dialect.getSequenceInformationSQL(testSequenceName, qualifier, prefs);
				failDialect(dialect, "getting sequence info");
			}
			catch (final Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void testAlterSequence(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		String cache = null;
		boolean cycle = true;
		String restart = "5";
		final SequencePropertyMutability mutability = dialect.getSequencePropertyMutability();
		final AlterSequenceSqlExtractor restartExtractor =
			new AlterSequenceSqlExtractor(testSequenceName, "1", "1", "1000", restart, cache, cycle);

		if (dialectDiscoveryMode)
		{
			findSQL(session, restartExtractor);
			return;
		}

		if (dialect.supportsAlterSequence())
		{

			if (mutability.isRestart())
			{
				final String[] sql =
					dialect.getAlterSequenceSQL(testSequenceName, "1", "1", "1000", restart, cache, cycle,
						qualifier, prefs);
				runSQL(session, sql, restartExtractor);
			}
			if (mutability.isCache() && mutability.isCycle())
			{
				cache = "10";
				cycle = false;
				restart = null;
				final String[] sql =
					dialect.getAlterSequenceSQL(testSequenceName, "1", "1", "1000", restart, cache, cycle,
						qualifier, prefs);
				runSQL(session, sql);
			}
		}
		else
		{
			try
			{
				dialect.getAlterSequenceSQL(testSequenceName, "1", "1", "1000", "1", cache, cycle, qualifier,
					prefs);
				failDialect(dialect, "for altering a sequence");
			}
			catch (final Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void testDropSequence(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);

		final DropSequenceSqlExtractor dropSequenceSqlExtractor =
			new DropSequenceSqlExtractor(testSequenceName, false);

		if (dialectDiscoveryMode)
		{
			findSQL(session, dropSequenceSqlExtractor);
			return;
		}

		if (dialect.supportsDropSequence())
		{
			final String sql = dialect.getDropSequenceSQL(testSequenceName, false, qualifier, prefs);

			runSQL(session, new String[] { sql }, dropSequenceSqlExtractor);
		}
		else
		{
			try
			{
				dialect.getDropSequenceSQL(testSequenceName, false, qualifier, prefs);
				failDialect(dialect, "for dropping a sequence");
			}
			catch (final Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void testAddForeignKeyConstraint(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		final boolean deferrable = true;
		final boolean initiallyDeferred = true;
		final boolean matchFull = true;
		final boolean autoFKIndex = false;
		final String fkIndexName = "fk_idx";
		final ArrayList<String[]> localRefColumns = new ArrayList<String[]>();
		localRefColumns.add(new String[] { fkChildColumnName, fkParentColumnName });
		final String onUpdateAction = null;
		final String onDeleteAction = null;
		final String childTable = fixIdentifierCase(session, fkChildTableName);
		final String parentTable = fixIdentifierCase(session, fkParentTableName);

		final CreateForeignKeyConstraintSqlExtractor extractor =
			new CreateForeignKeyConstraintSqlExtractor("fk_child_refs_parent", deferrable, initiallyDeferred,
				matchFull, autoFKIndex, fkIndexName, localRefColumns, onUpdateAction, onDeleteAction, childTable,
				parentTable);

		if (dialectDiscoveryMode)
		{
			findSQL(session, extractor);
			return;
		}

		if (dialect.supportsAddForeignKeyConstraint())
		{
			final String[] sql =
				dialect.getAddForeignKeyConstraintSQL(childTable, parentTable, "fk_child_refs_parent",
					deferrable, initiallyDeferred, matchFull, autoFKIndex, fkIndexName, localRefColumns,
					onUpdateAction, onDeleteAction, qualifier, prefs);

			if (DialectFactory.isFirebird(session.getMetaData()))
			{
				// Firebird gives weird exception: unsuccessful metadata update
				// object FKTESTPARENTTABLE is in use; squelch it and continue.
				try
				{
					runSQL(session, sql);
				}
				catch (final Exception e)
				{
				}
			}
			else
			{
				runSQL(session, sql, extractor);
			}
		}
		else
		{
			try
			{
				dialect.getAddForeignKeyConstraintSQL(childTable, parentTable, "fk_child_refs_parent",
					deferrable, initiallyDeferred, matchFull, autoFKIndex, fkIndexName, localRefColumns,
					onUpdateAction, onDeleteAction, qualifier, prefs);
				failDialect(dialect, "for adding a FK constraint");
			}
			catch (final Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void testAddUniqueConstraint(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		TableColumnInfo[] columns = new TableColumnInfo[] { uniqueConstraintTableColumnInfo };
		final String tableName = fixIdentifierCase(session, testUniqueConstraintTableName);

		final AddUniqueConstraintSqlExtractor extractor =
			new AddUniqueConstraintSqlExtractor(tableName, secondUniqueConstraintName, columns);

		if (dialectDiscoveryMode)
		{
			findSQL(session, extractor);
			return;
		}

		if (dialect.supportsAddUniqueConstraint())
		{
			String[] sql =
				dialect.getAddUniqueConstraintSQL(tableName, uniqueConstraintName, columns, qualifier, prefs);
			runSQL(session, sql, extractor);

			// We need to add a second column to have a unique constraint so that we can drop that one. Progress
			// doesn't allow the very first index to ever be dropped.
			columns = new TableColumnInfo[] { dropConstraintColumn };
			sql =
				dialect.getAddUniqueConstraintSQL(tableName, secondUniqueConstraintName, columns, qualifier,
					prefs);
			runSQL(session, sql, extractor);

		}
		else
		{
			try
			{
				dialect.getAddUniqueConstraintSQL(tableName, uniqueConstraintName, columns, qualifier, prefs);
				failDialect(dialect, "for adding a unique constraint");
			}
			catch (final Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void testAddAutoIncrement(ISession session) throws Exception
	{
		if (dialectDiscoveryMode)
		{
			findSQL(session, new AddAutoIncrementSqlExtractor(autoIncrementColumn));
			return;
		}
		final HibernateDialect dialect = getDialect(session);
		if (dialect.supportsAutoIncrement())
		{
			final String[] sql =
				dialect.getAddAutoIncrementSQL(autoIncrementColumn, autoIncrementSequenceName, qualifier, prefs);
			runSQL(session, sql, new AddAutoIncrementSqlExtractor(autoIncrementColumn));
		}
		else
		{
			try
			{
				dialect.getAddAutoIncrementSQL(autoIncrementColumn, qualifier, prefs);
				failDialect(dialect, "for adding an auto increment column");
			}
			catch (final Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void testDropConstraint(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		final String tableName = fixIdentifierCase(session, testUniqueConstraintTableName);

		final DropConstraintSqlExtractor dropConstraintSqlExtractor =
			new DropConstraintSqlExtractor(tableName, secondUniqueConstraintName);

		if (dialectDiscoveryMode)
		{
			findSQL(session, dropConstraintSqlExtractor);
			return;
		}

		if (dialect.supportsDropConstraint())
		{
			final String[] sql =
				new String[] { dialect.getDropConstraintSQL(tableName, secondUniqueConstraintName, qualifier,
					prefs) };

			runSQL(session, sql, dropConstraintSqlExtractor);
		}
		else
		{
			try
			{
				dialect.getDropConstraintSQL(tableName, uniqueConstraintName, qualifier, prefs);
				failDialect(dialect, "for dropping a constraint");
			}
			catch (final Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void testInsertIntoSQL(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		String valuesPart = "select distinct myid from " + fixIdentifierCase(session, integerDataTableName);
		final ArrayList<String> columns = new ArrayList<String>();
		final String tableName = fixIdentifierCase(session, testInsertIntoTable);

		final InsertIntoSqlExtractor insertIntoSqlExtractor =
			new InsertIntoSqlExtractor(tableName, columns, valuesPart);
		if (dialectDiscoveryMode)
		{
			findSQL(session, insertIntoSqlExtractor);
			return;
		}

		if (dialect.supportsInsertInto())
		{
			String sql = dialect.getInsertIntoSQL(tableName, columns, valuesPart, qualifier, prefs);

			runSQL(session, new String[] { sql }, insertIntoSqlExtractor);

			valuesPart = " values ( 20 )";
			sql = dialect.getInsertIntoSQL(tableName, columns, valuesPart, qualifier, prefs);
			runSQL(session, sql);
		}
		else
		{
			try
			{
				dialect.getInsertIntoSQL(testInsertIntoTable, columns, valuesPart, qualifier, prefs);
				failDialect(dialect, "for insert into statements");
			}
			catch (final Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void testAddColumnSQL(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);

		if (dialect.supportsAddColumn())
		{
			final String[] sql = dialect.getAddColumnSQL(addColumn, qualifier, prefs);
			runSQL(session, Arrays.asList(sql));
		}
		else
		{
			try
			{
				dialect.getAddColumnSQL(addColumn, qualifier, prefs);
				failDialect(dialect, "for adding a column");
			}
			catch (final Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}

	/**
	 * // UPDATE tableName SET setColumn1 = setValue1, setColumn2 = setValue2 // FROM fromTable1, fromTable2 //
	 * WHERE whereColumn1 = whereValue1 AND whereColumn2 = whereValue2;
	 * 
	 * @param session
	 * @throws Exception
	 */
	private void testUpdateSQL(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		final String tableName = "a";
		final String[] setColumns = new String[] { "joined" };
		final String[] setValues = new String[] { "'a1b1'" };
		final String[] fromTables = null;
		final String[] whereColumns = new String[] { "adesc", "bdesc" };
		final String[] whereValues = new String[] { "'a1'", "'b1'" };

		final UpdateSqlExtractor extractor =
			new UpdateSqlExtractor(tableName, setColumns, setValues, fromTables, whereColumns, whereValues);

		if (dialectDiscoveryMode)
		{
			findSQL(session, extractor);
			return;
		}

		if (dialect.supportsUpdate())
		{
			final String[] sql =
				dialect.getUpdateSQL(tableName, setColumns, setValues, fromTables, whereColumns, whereValues,
					qualifier, prefs);
			runSQL(session, sql, extractor);
		}
		else
		{
			try
			{
				dialect.getUpdateSQL(testUniqueConstraintTableName, setColumns, setValues, fromTables,
					whereColumns, whereValues, qualifier, prefs);
				failDialect(dialect, "for update SQL");
			}
			catch (final Exception e)
			{
				// this is expected
				System.err.println(e.getMessage());
			}
		}
	}

	private void testMergeTable(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		if (dialect.supportsUpdate() && dialect.supportsAddColumn())
		{

			final IDatabaseObjectInfo[] selectedTables = new IDatabaseObjectInfo[1];
			final String catalog = getDefaultCatalog(session);
			final String schema = getDefaultSchema(session);

			selectedTables[0] =
				new DatabaseObjectInfoHelper(catalog, schema, fixIdentifierCase(session, testSecondMergeTable),
					fixIdentifierCase(session, testSecondMergeTable), DatabaseObjectType.TABLE);

			final MergeTableCommandHelper command = new MergeTableCommandHelper(session, selectedTables, null);

			final String[] sqls = command.generateSQLStatements();

			runSQL(session, sqls);
		}
	}

	private void testDataScript(ISession session) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);

		// Axion cannot handle ts escape sequence - so skip this test for now.
		if (DialectFactory.isAxion(session.getMetaData())) { return; }

		final String tableName = fixIdentifierCase(session, testTimestampTable);
		final String timestampTypeName = dialect.getTypeName(Types.TIMESTAMP, 5, 5, 5);

		final CreateDataScriptHelper command = new CreateDataScriptHelper(session, null, false);

		dropTable(session, tableName);
		runSQL(session, "create table " + tableName + " ( mytime " + timestampTypeName + " )");
		if (dialect.supportsSubSecondTimestamps())
		{
			runSQL(session, "insert into " + tableName + " values ({ts '2008-02-21 21:26:23.966123'})");
		}
		else
		{
			// MS SQLServer yields "Conversion failed when converting datetime from character string"
			// for {ts '2008-02-21 21:26:23.966123'}
			runSQL(session, "insert into " + tableName + " values ({ts '2008-02-21 21:26:23.966'})");
		}

		final StringBuffer sb = command.getSQL(tableName);

		final String newTableName = fixIdentifierCase(session, testNewTimestampTable);
		dropTable(session, newTableName);
		runSQL(session, "create table " + newTableName + " ( mytime " + timestampTypeName + " )");
		runSQL(session, sb.toString());

		/* Verify insert worked only if the dialect supports sub-second timestamp values */
		if (dialect.supportsSubSecondTimestamps())
		{
			final ResultSet rs = runQuery(session, "select mytime from " + newTableName);
			if (rs.next())
			{
				final Timestamp ts = rs.getTimestamp(1);
				final String nanos = ("" + ts.getNanos() / 1000).substring(3);
				if (!"123".equals(nanos))
				{
					System.err.println("Expected nanos to be 123, but was instead: " + nanos);
				}
			}
			final Statement stmt = rs.getStatement();
			SQLUtilities.closeResultSet(rs);
			SQLUtilities.closeStatement(stmt);
		}
	}

	// Utility methods

	private void failDialect(HibernateDialect dialect, String refactoringType)
	{
		fail("Expected dialect (" + dialect.getDisplayName() + ") to fail to provide SQL for "
			+ refactoringType);
	}

	private void dropColumn(ISession session, TableColumnInfo info) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		final String sql =
			dialect.getColumnDropSQL(info.getTableName(), info.getColumnName(), qualifier, prefs);
		runSQL(session, sql);
	}

	private HibernateDialect getDialect(ISession session) throws Exception
	{
		return DialectFactory.getDialect(DialectFactory.DEST_TYPE, null, session.getMetaData());
	}

	private void runSQL(ISession session, List<String> sql) throws Exception
	{
		for (final String stmt : sql)
		{
			runSQL(session, stmt);
		}
	}

	private void runSQL(ISession session, String[] sql) throws Exception
	{
		if (sql == null) { throw new IllegalStateException("sql argument was null"); }
		for (final String stmt : sql)
		{
			runSQL(session, stmt);
		}
	}

	private void runSQL(ISession session, String sqlIn) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		if (sqlIn == null) { throw new IllegalStateException("sqlIn argument was null"); }
		if (!sqlIn.startsWith("--"))
		{
			String sql = sqlIn.trim();
			if (sql.endsWith(";"))
			{
				sql = sql.substring(0, sql.length() - 1);
			}
			final Connection con = session.getSQLConnection().getConnection();
			final Statement stmt = con.createStatement();
			if (!dialectDiscoveryMode)
			{
				System.out.println("Running SQL  (" + dialect.getDisplayName() + "): " + sql);
			}
			try
			{
				stmt.execute(sql);
			}
			catch (final Exception e)
			{
				if (DialectFactory.isDB2(session.getMetaData()))
				{
					final DB2JCCExceptionFormatter formatter = new DB2JCCExceptionFormatter();
					System.err.println("Formatted message: " + formatter.format(e));
				}
				if (DialectFactory.isInformix(session.getMetaData()))
				{
					final InformixExceptionFormatter formatter = new InformixExceptionFormatter(session);
					System.err.println("Formatted message: " + formatter.format(e));
				}
				throw e;
			}
			finally
			{
				SQLUtilities.closeStatement(stmt);
			}
		}
		else
		{
			System.out.println("Skip Comment (" + dialect.getDisplayName() + "): " + sqlIn);
		}
	}

	/**
	 * This is only useful in discovery mode. Since there is not SQL to try, the assumption here is that the
	 * dialect's SQL isn't correctly implemented.
	 * 
	 * @param session
	 * @param extractor
	 */
	private void findSQL(ISession session, IDialectSqlExtractor extractor)
	{
		boolean success = false;
		HibernateDialect lastDialect = null;
		for (final HibernateDialect referenceDialect : referenceDialects)
		{
			if (success)
			{
				break;
			}
			if (!extractor.supportsOperation(referenceDialect))
			{
				continue;
			}

			lastDialect = referenceDialect;
			final String[] sql = extractor.getSql(referenceDialect);

			if (sql == null || "".equals(sql))
			{
				continue;
			}
			try
			{
				runSQL(session, sql);
				// System.err.println("("+extractor.getClass().getSimpleName()+"):The SQL generated by " +
				// referenceDialect.getDisplayName() + " works !!!");
				success = true;
			}
			catch (final Exception e2)
			{
				// System.err.println("Attempt to use dialect sql from " + referenceDialect.getDisplayName()
				// + " failed: " + e2.getMessage());
			}
		}
		if (success)
		{
			System.out.println("Dialect " + lastDialect.getDisplayName()
				+ " produced valid SQL using extractor: " + extractor.getClass().getSimpleName());
		}
		else
		{
			System.err.println("No reference dialect was able to generate valid SQL for extractor: "
				+ extractor.getClass().getSimpleName());
		}

	}

	/**
	 * Uses the specified extractor to look up the SQL in all other dialects and try them to see if they will
	 * work, if the specified session's dialect isn't up to the task.
	 * 
	 * @param session
	 * @param sql
	 * @param extractor
	 */
	private void runSQL(ISession session, String[] sql, IDialectSqlExtractor extractor)
	{
		try
		{
			runSQL(session, sql);
			return;
		}
		catch (final Exception e)
		{
			System.err.println("Dialect failed to produce valid sql: " + e.getMessage());
			e.printStackTrace();
			if (!dialectDiscoveryMode)
			{
				String displayName = "unknown";
				try
				{
					displayName = getDialect(session).getDisplayName();
				}
				catch (final Exception e1)
				{
					e1.printStackTrace();
				}
				fail("Dialect (" + displayName + ") failed to produce valid sql: " + e.getMessage());
			}
		}
		boolean success = false;
		HibernateDialect lastDialect = null;
		for (final HibernateDialect referenceDialect : referenceDialects)
		{
			if (success)
			{
				break;
			}
			if (!extractor.supportsOperation(referenceDialect))
			{
				continue;
			}

			lastDialect = referenceDialect;
			sql = extractor.getSql(referenceDialect);

			if (sql == null || "".equals(sql))
			{
				continue;
			}
			try
			{
				System.err.println("Trying SQL generated by " + referenceDialect.getDisplayName());
				runSQL(session, sql);
				success = true;
			}
			catch (final Exception e2)
			{
				System.err.println("Attempt to use dialect sql from " + referenceDialect.getDisplayName()
					+ " failed: " + e2.getMessage());
			}
		}
		if (success)
		{
			System.out.println("Dialect " + lastDialect.getDisplayName() + " produced valid SQL: "
				+ Arrays.toString(sql));
		}
		else
		{
			System.err.println("No reference dialect was able to generate valid SQL for this operation.");
		}
	}

	private ResultSet runQuery(ISession session, String sql) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		final Connection con = session.getSQLConnection().getConnection();
		final Statement stmt = con.createStatement();

		System.out.println("Running SQL (" + dialect.getDisplayName() + "): " + sql);
		return stmt.executeQuery(sql);
	}

	private ResultSet runPreparedQuery(ISession session, String sql, String value) throws Exception
	{
		final HibernateDialect dialect = getDialect(session);
		final Connection con = session.getSQLConnection().getConnection();
		final PreparedStatement stmt = con.prepareStatement(sql);
		System.out.println("Running SQL (" + dialect.getDisplayName() + "): " + sql + " with bind variable : "
			+ value);
		stmt.setString(1, value);
		return stmt.executeQuery();
	}

	private TableColumnInfo getIntegerColumn(String name, String tableName, boolean nullable,
		String defaultVal, String comment)
	{
		return getColumn(java.sql.Types.INTEGER, "INTEGER", name, tableName, nullable, defaultVal, comment, 10,
			0);
	}

	private TableColumnInfo getCharColumn(String name, String tableName, boolean nullable, String defaultVal,
		String comment)
	{
		return getColumn(java.sql.Types.CHAR, "CHAR", name, tableName, nullable, defaultVal, comment, 10, 0);
	}

	private TableColumnInfo getVarcharColumn(String name, String tableName, boolean nullable,
		String defaultVal, String comment, int size)
	{
		return getColumn(java.sql.Types.VARCHAR, "VARCHAR", name, tableName, nullable, defaultVal, comment,
			size, 0);
	}

	private TableColumnInfo getVarcharColumn(String name, String tableName, boolean nullable,
		String defaultVal, String comment)
	{
		return getColumn(java.sql.Types.VARCHAR, "VARCHAR", name, tableName, nullable, defaultVal, comment, 20,
			0);
	}

	private TableColumnInfo getColumn(int dataType, String dataTypeName, String name, String tableName,
		boolean nullable, String defaultVal, String comment, int columnSize, int scale)
	{
		String isNullable = "YES";
		int isNullAllowed = DatabaseMetaData.columnNullable;
		if (!nullable)
		{
			isNullable = "NO";
			isNullAllowed = DatabaseMetaData.columnNoNulls;
		}
		final TableColumnInfo result = new TableColumnInfo("testCatalog", // catalog
			"testSchema", // schema
			tableName, // tableName
			name, // columnName
			dataType, // dataType
			dataTypeName, // typeName
			columnSize, // columnSize
			scale, // decimalDigits
			10, // radix
			isNullAllowed, // isNullAllowed
			comment, // remarks
			defaultVal, // defaultValue
			0, // octet length
			0, // ordinal position
			isNullable); // isNullable
		return result;
	}

	/**
	 * Some databases that I have are particular about the case of table/view names. This method (hopefully)
	 * normalizes the names such that they can be found later after they are created.
	 * 
	 * @param session
	 * @param identifier
	 * @return
	 * @throws Exception
	 */
	private String fixIdentifierCase(ISession session, String identifier) throws Exception
	{
		String result = null;
		final SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		if (md.storesUpperCaseIdentifiers())
		{
			result = identifier.toUpperCase();
		}
		else
		{
			result = identifier.toLowerCase();
		}
		return result;
	}

	/**
	 * @param session
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	private TableColumnInfo[] getLiveColumnInfo(ISession session, String tableName) throws SQLException
	{
		final ISQLDatabaseMetaData md = session.getMetaData();
		final String catalog = getDefaultCatalog(session);
		final String schema = getDefaultSchema(session);
		return md.getColumnInfo(catalog, schema, tableName);
	}

	/**
	 * A helper "dialog" implementation that simulates getting user input for automating the test. The merge
	 * table command takes substantial input, and based on that generates a substantial amount of SQL.
	 * 
	 * @author manningr
	 */
	private class MergeTableDialogHelper implements IMergeTableDialog
	{

		private Vector<String> _mergeColumns = null;

		private String _referencedTable = null;

		private Vector<String[]> _whereDataColumns = null;

		private boolean _isMergeData = false;

		public MergeTableDialogHelper(Vector<String> mergeColumns, String referencedTable,
			Vector<String[]> whereDataColumns, boolean isMergeData)
		{
			_mergeColumns = mergeColumns;
			_referencedTable = referencedTable;
			_whereDataColumns = whereDataColumns;
			_isMergeData = isMergeData;
		}

		// Unused methods - this is not a real dialog, just a fake.
		public void addEditSQLListener(ActionListener listener)
		{
		}

		public void addExecuteListener(ActionListener listener)
		{
		}

		public void addShowSQLListener(ActionListener listener)
		{
		}

		public void setLocationRelativeTo(Component c)
		{
		}

		public void setVisible(boolean val)
		{
		}

		public void dispose()
		{
		}

		public Vector<String> getMergeColumns()
		{
			return _mergeColumns;
		}

		public String getReferencedTable()
		{
			return _referencedTable;
		}

		public Vector<String[]> getWhereDataColumns()
		{
			return _whereDataColumns;
		}

		public boolean isMergeData()
		{
			return _isMergeData;
		}
	}

	/**
	 * runSQL(session, "create table " + fixTableName(session, testFirstMergeTable) +
	 * " ( myid integer, desc_t1 varchar(20))" + pageSizeClause); runSQL(session, "create table " +
	 * fixTableName(session, testSecondMergeTable) + " ( myid integer, desc_t2 varchar(20))" + pageSizeClause);
	 */
	private class MergeTableCommandHelper extends MergeTableCommand
	{
		public MergeTableCommandHelper(ISession session, IDatabaseObjectInfo[] info,
			IMergeTableDialogFactory dialogFactory) throws Exception
		{
			super(session, info, dialogFactory);
			final Vector<String> mergeColumns = new Vector<String>();
			mergeColumns.add("desc_t1");
			final String referencedTable = fixIdentifierCase(session, testFirstMergeTable);
			final Vector<String[]> whereDataColumns = new Vector<String[]>();
			whereDataColumns.add(new String[] { "myid", "myid" });
			final boolean _isMergeData = true;
			super.customDialog =
				new MergeTableDialogHelper(mergeColumns, referencedTable, whereDataColumns, _isMergeData);
			_allTables = new HashMap<String, TableColumnInfo[]>();
			_allTables.put(fixIdentifierCase(session, testFirstMergeTable), getLiveColumnInfo(session,
				fixIdentifierCase(session, testFirstMergeTable)));
			_allTables.put(fixIdentifierCase(session, testSecondMergeTable), getLiveColumnInfo(session,
				fixIdentifierCase(session, testSecondMergeTable)));
			super._dialect = DialectFactory.getDialect(session.getMetaData());
		}

		public String[] generateSQLStatements() throws UserCancelledOperationException, SQLException
		{
			return super.generateSQLStatements();
		}
	}

	private class CreateDataScriptHelper extends CreateDataScriptCommand
	{

		public CreateDataScriptHelper(ISession session, SQLScriptPlugin plugin, boolean templateScriptOnly)
		{
			super(session, plugin, templateScriptOnly);
		}

		public StringBuffer getSQL(String tableName) throws SQLException
		{
			final StringBuffer result = new StringBuffer();
			if (_session instanceof MockSession)
			{
				final Statement st = _session.getSQLConnection().getConnection().createStatement();
				final ResultSet rs = st.executeQuery("select * from " + tableName);
				genInserts(rs, tableName, result, false);
			}
			return result;
		}

		/**
		 * @see net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.CreateDataScriptCommand#genInserts(java.sql.ResultSet,
		 *      java.lang.String, java.lang.StringBuffer, boolean)
		 */
		@Override
		protected void genInserts(ResultSet srcResult, String table, StringBuffer sbRows, boolean headerOnly)
			throws SQLException
		{
			// TODO Auto-generated method stub
			super.genInserts(srcResult, table, sbRows, headerOnly);
		}

	}

	private class DatabaseObjectInfoHelper implements IDatabaseObjectInfo
	{

		private String _catalog = null;

		private String _schema = null;

		private String _simpleName = null;

		private String _qualName = null;

		private DatabaseObjectType _type = null;

		public DatabaseObjectInfoHelper(String catalog, String schema, String simpleName, String qualName,
			DatabaseObjectType type)
		{
			_catalog = catalog;
			_schema = schema;
			_simpleName = simpleName;
			_qualName = qualName;
			_type = type;
		}

		public String getCatalogName()
		{
			return _catalog;
		}

		public DatabaseObjectType getDatabaseObjectType()
		{
			return _type;
		}

		public String getQualifiedName()
		{
			return _qualName;
		}

		public String getSchemaName()
		{
			return _schema;
		}

		public String getSimpleName()
		{
			return _simpleName;
		}

		public int compareTo(IDatabaseObjectInfo o)
		{
			return 0;
		}

	}

	private class AlterColumnNameSqlExtractor implements IDialectSqlExtractor
	{

		TableColumnInfo from;

		TableColumnInfo to;

		public AlterColumnNameSqlExtractor(TableColumnInfo from, TableColumnInfo to)
		{
			this.from = from;
			this.to = to;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return new String[] { dialect.getColumnNameAlterSQL(from, to, qualifier, prefs) };
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsRenameColumn();
		}

	}

	private class AlterColumnCommentSqlExtractor implements IDialectSqlExtractor
	{

		TableColumnInfo info = null;

		public AlterColumnCommentSqlExtractor(TableColumnInfo info)
		{
			this.info = info;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return new String[] { dialect.getColumnCommentAlterSQL(info, qualifier, prefs) };
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsColumnComment();
		}
	}

	private class AlterColumnNullSqlExtractor implements IDialectSqlExtractor
	{

		TableColumnInfo info;

		public AlterColumnNullSqlExtractor(TableColumnInfo info)
		{
			this.info = info;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return dialect.getColumnNullableAlterSQL(info, qualifier, prefs);
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsAlterColumnNull();
		}

	}

	private class AlterColumnTypeSqlExtractor implements IDialectSqlExtractor
	{
		TableColumnInfo from;

		TableColumnInfo to;

		public AlterColumnTypeSqlExtractor(TableColumnInfo from, TableColumnInfo to)
		{
			super();
			this.from = from;
			this.to = to;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			final List<String> result = dialect.getColumnTypeAlterSQL(from, to, qualifier, prefs);
			return result.toArray(new String[result.size()]);
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsAlterColumnType();
		}
	}

	private class AddPrimaryKeySqlExtractor implements IDialectSqlExtractor
	{
		ITableInfo ti;

		TableColumnInfo[] columns;

		String pkName;

		public AddPrimaryKeySqlExtractor(ITableInfo ti, TableColumnInfo[] columns, String pkName)
		{
			super();
			this.ti = ti;
			this.columns = columns;
			this.pkName = pkName;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return dialect.getAddPrimaryKeySQL(pkName, columns, ti, qualifier, prefs);
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return true;
		}
	}

	private class DropPrimaryKeySqlExtractor implements IDialectSqlExtractor
	{

		String tableName;

		String pkName;

		public DropPrimaryKeySqlExtractor(String tableName, String pkName)
		{
			super();
			this.tableName = tableName;
			this.pkName = pkName;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return new String[] { dialect.getDropPrimaryKeySQL(pkName, tableName, qualifier, prefs) };
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return true;
		}

	}

	private class DropSequenceSqlExtractor implements IDialectSqlExtractor
	{

		String sequenceName;

		boolean cascade;

		public DropSequenceSqlExtractor(String sequenceName, boolean cascade)
		{
			super();
			this.sequenceName = sequenceName;
			this.cascade = cascade;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return new String[] { dialect.getDropSequenceSQL(sequenceName, cascade, qualifier, prefs) };
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsDropSequence();
		}

	}

	private class RenameTableSqlExtractor implements IDialectSqlExtractor
	{

		private final String oldTableName;

		private final String newTableName;

		public RenameTableSqlExtractor(String oldTableName, String newTableName)
		{
			super();
			this.oldTableName = oldTableName;
			this.newTableName = newTableName;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return new String[] { dialect.getRenameTableSQL(oldTableName, newTableName, qualifier, prefs) };
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsRenameTable();
		}

	}

	private class CreateViewSqlExtractor implements IDialectSqlExtractor
	{

		private final String viewName;

		private final String definition;

		private final String checkOption;

		public CreateViewSqlExtractor(String viewName, String definition, String checkOption)
		{
			super();
			this.viewName = viewName;
			this.definition = definition;
			this.checkOption = checkOption;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return new String[] { dialect.getCreateViewSQL(viewName, definition, checkOption, qualifier, prefs) };
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsCreateView();
		}

	}

	private class CreateIndexSqlExtractor implements IDialectSqlExtractor
	{

		private final String indexName;

		private final String tableName;

		private final String accessMethod;

		private final String[] columns;

		private final boolean unique;

		private final String tablespace;

		private final String constraints;

		public CreateIndexSqlExtractor(String indexName, String tableName, String accessMethod,
			String[] columns, boolean unique, String tablespace, String constraints)
		{
			super();
			this.indexName = indexName;
			this.tableName = tableName;
			this.accessMethod = accessMethod;
			this.columns = columns;
			this.unique = unique;
			this.tablespace = tablespace;
			this.constraints = constraints;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return new String[] { dialect.getCreateIndexSQL(indexName, tableName, accessMethod, columns, unique,
				tablespace, constraints, qualifier, prefs) };
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsCreateIndex();
		}

	}

	private class DropIndexSqlExtractor implements IDialectSqlExtractor
	{

		private final String tableName;

		private final String indexName;

		private final boolean cascade;

		public DropIndexSqlExtractor(String tableName, String indexName, boolean cascade)
		{
			super();
			this.tableName = tableName;
			this.indexName = indexName;
			this.cascade = cascade;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return new String[] { dialect.getDropIndexSQL(tableName, indexName, cascade, qualifier, prefs) };
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsDropIndex();
		}

	}

	private class CreateSequenceSqlExtractor implements IDialectSqlExtractor
	{

		private final String sequenceName;

		private final String increment;

		private final String minimum;

		private final String maximum;

		private final String start;

		private final String cache;

		private final boolean cycle;

		public CreateSequenceSqlExtractor(String sequenceName, String increment, String minimum,
			String maximum, String start, String cache, boolean cycle)
		{
			super();
			this.sequenceName = sequenceName;
			this.increment = increment;
			this.minimum = minimum;
			this.maximum = maximum;
			this.start = start;
			this.cache = cache;
			this.cycle = cycle;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return new String[] { dialect.getCreateSequenceSQL(sequenceName, increment, minimum, maximum, start,
				cache, cycle, qualifier, prefs) };
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsCreateSequence();
		}

	}

	private class CreateForeignKeyConstraintSqlExtractor implements IDialectSqlExtractor
	{

		private final String constraintName;

		private final Boolean deferrable;

		private final Boolean initiallyDeferred;

		private final Boolean matchFull;

		private final boolean autoFKIndex;

		private final String fkIndexName;

		private final Collection<String[]> localRefColumns;

		private final String onUpdateAction;

		private final String onDeleteAction;

		private final String localTableName;

		private final String refTableName;

		public CreateForeignKeyConstraintSqlExtractor(String constraintName, Boolean deferrable,
			Boolean initiallyDeferred, Boolean matchFull, boolean autoFKIndex, String fkIndexName,
			Collection<String[]> localRefColumns, String onUpdateAction, String onDeleteAction,
			String localTableName, String refTableName)
		{
			super();
			this.constraintName = constraintName;
			this.deferrable = deferrable;
			this.initiallyDeferred = initiallyDeferred;
			this.matchFull = matchFull;
			this.autoFKIndex = autoFKIndex;
			this.fkIndexName = fkIndexName;
			this.localRefColumns = localRefColumns;
			this.onUpdateAction = onUpdateAction;
			this.onDeleteAction = onDeleteAction;
			this.localTableName = localTableName;
			this.refTableName = refTableName;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return dialect.getAddForeignKeyConstraintSQL(localTableName, refTableName, constraintName,
				deferrable, initiallyDeferred, matchFull, autoFKIndex, fkIndexName, localRefColumns,
				onUpdateAction, onDeleteAction, qualifier, prefs);
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsAddForeignKeyConstraint();
		}

	}

	private class UpdateSqlExtractor implements IDialectSqlExtractor
	{

		private final String tableName;

		private final String[] setColumns;

		private final String[] setValues;

		private final String[] fromTables;

		private final String[] whereColumns;

		private final String[] whereValues;

		public UpdateSqlExtractor(String tableName, String[] setColumns, String[] setValues,
			String[] fromTables, String[] whereColumns, String[] whereValues)
		{
			super();
			this.tableName = tableName;
			this.setColumns = setColumns;
			this.setValues = setValues;
			this.fromTables = fromTables;
			this.whereColumns = whereColumns;
			this.whereValues = whereValues;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return dialect.getUpdateSQL(tableName, setColumns, setValues, fromTables, whereColumns, whereValues,
				qualifier, prefs);
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsUpdate();
		}

	}

	private class AddUniqueConstraintSqlExtractor implements IDialectSqlExtractor
	{

		private final String tableName;

		private final String constraintName;

		private final TableColumnInfo[] columns;

		public AddUniqueConstraintSqlExtractor(String tableName, String constraintName,
			TableColumnInfo[] columns)
		{
			super();
			this.tableName = tableName;
			this.constraintName = constraintName;
			this.columns = columns;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return dialect.getAddUniqueConstraintSQL(tableName, constraintName, columns, qualifier, prefs);
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsAddUniqueConstraint();
		}

	}

	private class DropConstraintSqlExtractor implements IDialectSqlExtractor
	{

		private final String tableName;

		private final String constraintName;

		public DropConstraintSqlExtractor(String tableName, String constraintName)
		{
			super();
			this.tableName = tableName;
			this.constraintName = constraintName;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return new String[] { dialect.getDropConstraintSQL(tableName, constraintName, qualifier, prefs) };
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsDropConstraint();
		}

	}

	private class CreateTableSqlExtractor implements IDialectSqlExtractor
	{

		private final String tableName;

		private final List<TableColumnInfo> columns;

		private final List<TableColumnInfo> primaryKeys;

		public CreateTableSqlExtractor(String tableName, List<TableColumnInfo> columns,
			List<TableColumnInfo> primaryKeys)
		{
			super();
			this.tableName = tableName;
			this.columns = columns;
			this.primaryKeys = primaryKeys;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			// tableName, columns, pkColumns, prefs, qualifier
			return new String[] { dialect.getCreateTableSQL(tableName, columns, primaryKeys, prefs, qualifier) };
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsCreateTable();
		}

	}

	private class RenameViewSqlExtractor implements IDialectSqlExtractor
	{

		private final String oldViewName;

		private final String newViewName;

		public RenameViewSqlExtractor(String oldViewName, String newViewName)
		{
			super();
			this.oldViewName = oldViewName;
			this.newViewName = newViewName;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return dialect.getRenameViewSQL(oldViewName, newViewName, qualifier, prefs);
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsRenameView();
		}

	}

	private class SequenceInfoSqlExtractor implements IDialectSqlExtractor
	{

		private final String sequenceName;

		public SequenceInfoSqlExtractor(String sequenceName)
		{
			super();
			this.sequenceName = sequenceName;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return new String[] { dialect.getSequenceInformationSQL(sequenceName, qualifier, prefs) };
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsSequenceInformation();
		}

	}

	private class AlterSequenceSqlExtractor implements IDialectSqlExtractor
	{

		private final String sequenceName;

		private final String increment;

		private final String minimum;

		private final String maximum;

		private final String restart;

		private final String cache;

		private final boolean cycle;

		public AlterSequenceSqlExtractor(String sequenceName, String increment, String minimum, String maximum,
			String restart, String cache, boolean cycle)
		{
			super();
			this.sequenceName = sequenceName;
			this.increment = increment;
			this.minimum = minimum;
			this.maximum = maximum;
			this.restart = restart;
			this.cache = cache;
			this.cycle = cycle;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return dialect.getAlterSequenceSQL(sequenceName, increment, minimum, maximum, restart, cache, cycle,
				qualifier, prefs);
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsAlterSequence();
		}

	}

	private class AddAutoIncrementSqlExtractor implements IDialectSqlExtractor
	{

		private final TableColumnInfo column;

		public AddAutoIncrementSqlExtractor(TableColumnInfo column)
		{
			super();
			this.column = column;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return dialect.getAddAutoIncrementSQL(column, qualifier, prefs);
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsAutoIncrement();
		}

	}

	private class InsertIntoSqlExtractor implements IDialectSqlExtractor
	{

		private final String tableName;

		private final List<String> columns;

		private final String valuesPart;

		public InsertIntoSqlExtractor(String tableName, List<String> columns, String valuesPart)
		{
			super();
			this.tableName = tableName;
			this.columns = columns;
			this.valuesPart = valuesPart;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return new String[] { dialect.getInsertIntoSQL(tableName, columns, valuesPart, qualifier, prefs) };
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsInsertInto();
		}

	}

	private class AlterDefaultValueSqlExtractor implements IDialectSqlExtractor
	{

		private final TableColumnInfo info;

		public AlterDefaultValueSqlExtractor(TableColumnInfo info)
		{
			super();
			this.info = info;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return new String[] { dialect.getColumnDefaultAlterSQL(info, qualifier, prefs) };
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsAlterColumnDefault();
		}

	}

	private class DropViewSqlExtractor implements IDialectSqlExtractor
	{

		private final String viewName;

		private final boolean cascade;

		public DropViewSqlExtractor(String viewName, boolean cascade)
		{
			super();
			this.viewName = viewName;
			this.cascade = cascade;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return new String[] { dialect.getDropViewSQL(viewName, cascade, qualifier, prefs) };
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsDropView();
		}
	}

	private class GetViewdefinitionSqlExtractor implements IDialectSqlExtractor
	{

		private final String viewName;

		public GetViewdefinitionSqlExtractor(String viewName)
		{
			super();
			this.viewName = viewName;
		}

		public String[] getSql(HibernateDialect dialect)
		{
			return new String[] { dialect.getViewDefinitionSQL(viewName, qualifier, prefs) };
		}

		public boolean supportsOperation(HibernateDialect dialect)
		{
			return dialect.supportsViewDefinition();
		}
	}

}
