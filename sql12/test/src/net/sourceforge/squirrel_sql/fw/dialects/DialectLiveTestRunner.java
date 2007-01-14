package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.db.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.MockSession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;

/**
 * The purpose of this class is to hookup to the database(s) specified in 
 * dialectLiveTest.properties and test SQL generation parts of the dialect 
 * syntatically using the database' native parser.  This is not a JUnit test, 
 * as it requires a running database to complete.
 * 
 * @author manningr
 */
public class DialectLiveTestRunner {

    ArrayList sessions = new ArrayList();
    ResourceBundle bundle = null;
    
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
    
    private static final String DB2_PK_COLNAME = "db2pkCol";
    
    public DialectLiveTestRunner() throws Exception {
        ApplicationArguments.initialize(new String[] {});
        bundle = ResourceBundle.getBundle("net.sourceforge.squirrel_sql.fw.dialects.dialectLiveTest");
        initSessions();
    }
    
    private void initSessions() throws Exception {
        String dbsToTest = bundle.getString("dbsToTest");
        StringTokenizer st = new StringTokenizer(dbsToTest, ",");
        ArrayList dbs = new ArrayList();
        while (st.hasMoreTokens()) {
            String db = st.nextToken().trim();
            dbs.add(db);
        }
        for (Iterator iter = dbs.iterator(); iter.hasNext();) {
            String db = (String) iter.next();
            String url = bundle.getString(db+"_jdbcUrl");
            String user = bundle.getString(db+"_jdbcUser");
            String pass = bundle.getString(db+"_jdbcPass");
            String driver = bundle.getString(db+"_jdbcDriver");
            String catalog = bundle.getString(db+"_catalog");
            String schema = bundle.getString(db+"_schema");
            MockSession session = new MockSession(driver, url, user, pass);
            session.setDefaultCatalog(catalog);
            session.setDefaultSchema(schema);
            sessions.add(session);            
        }
    }
    
    private void init(ISession session) throws Exception {
        createTestTables(session);
        firstCol = getIntegerColumn("nullint", fixTableName(session, "test1"), true, "0", "An int comment");
        secondCol = getIntegerColumn("notnullint", fixTableName(session,"test2"), false, "0", "An int comment");
        thirdCol = getVarcharColumn("nullvc", fixTableName(session,"test3"), true, "defVal", "A varchar comment");
        fourthCol = getVarcharColumn("notnullvc", fixTableName(session,"test4"), false, "defVal", "A varchar comment");
        noDefaultValueVarcharCol = 
            getVarcharColumn("noDefaultVarcharCol", fixTableName(session,"test"), true, null, "A varchar column with no default value"); 
        dropCol = getVarcharColumn("dropCol", fixTableName(session,"test5"), true, null, "A varchar comment");        
        noDefaultValueIntegerCol = 
            getIntegerColumn("noDefaultIntgerCol", fixTableName(session,"test5"), true, null, "An integer column with no default value");
        renameCol = getVarcharColumn("renameCol", fixTableName(session,"test"), true, null, "A column to be renamed");
        pkCol = getIntegerColumn("pkCol", fixTableName(session,"test"), false, "0", "primary key column");
        notNullIntegerCol = getIntegerColumn("notNullIntegerCol", fixTableName(session,"test5"), false, "0", "potential pk column");
        db2pkCol = getIntegerColumn(DB2_PK_COLNAME, fixTableName(session,"test"), false, "0", "A DB2 Primary Key column");
        
        // These two columns will be the only ones in the pktest table.  They will 
        // start out being nullable, and we will test that the dialect correctly
        // converts them to non-null then applies the PK constraint to them.
        // This test shall not be run against any database dialect that claims not
        // to support changing the nullability of a column.
        doubleColumnPKOne = getIntegerColumn("pk_col_1", fixTableName(session,"pktest"), true, null, "an initially nullable field to be made part of a PK");
        doubleColumnPKTwo = getIntegerColumn("pk_col_2", fixTableName(session,"pktest"), true, null, "an initially nullable field to be made part of a PK");
    }
    
    private ITableInfo getTableInfo(ISession session, String tableName) 
        throws Exception 
    {
        SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData(); 
        String catalog = ((MockSession)session).getDefaultCatalog();
        String schema = ((MockSession)session).getDefaultSchema();
        if (md.storesUpperCaseIdentifiers()) {
            tableName = tableName.toUpperCase();
        } else {
            tableName = tableName.toLowerCase();
        }
        //System.out.println("Looking for table with catalog="+catalog+" schema="+schema+" tableName="+tableName);
        ITableInfo[] infos = 
            md.getTables(catalog, schema, tableName, new String[] { "TABLE" }, null);
        if (infos.length > 1) {
            throw new IllegalStateException("Found more than one table matching name="+tableName);
            
        } 
        if (infos.length == 0) {    
            return null;
        }
        
        return infos[0];

    }
    
    private void dropTable(ISession session, String tableName) throws Exception {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);        
        try {
            ITableInfo ti = getTableInfo(session, tableName);
            if (ti == null) {    
                System.out.println("Table "+tableName+" couldn't be dropped - doesn't exist");
                return;
            }
            runSQL(session, dialect.getTableDropSQL(ti, true, false));
        } catch (SQLException e) {
            // Do Nothing
        }
    }
    
    /**
     * Setup the test tables.  This used to be only one table but it grew due
     * primarily to Ingres' inability to have a table with more than 5-10 
     * columns.
     * 
     * @param session
     * @throws Exception
     */
    private void createTestTables(ISession session) throws Exception {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);  

        dropTable(session, fixTableName(session,"test"));
        dropTable(session, fixTableName(session,"test1"));
        dropTable(session, fixTableName(session,"test2"));
        dropTable(session, fixTableName(session,"test3"));
        dropTable(session, fixTableName(session,"test4"));
        dropTable(session, fixTableName(session,"test5"));
        dropTable(session, fixTableName(session,"pktest"));
        if (DialectFactory.isOracleSession(session)) {
            dropTable(session, fixTableName(session,"matview"));
        }

        String pageSizeClause = "";
        
        if (DialectFactory.isIngresSession(session)) {
            // alterations fail for some reason unless you do this...
            pageSizeClause = " with page_size=4096";
        } 
        
        if (DialectFactory.isDB2Session(session)) {
            // db2pkCol is used to create a PK when using DB2.  DB2 doesn't allow
            // you to add a PK to a table after it has been constructed unless the
            // column(s) that comprise the PK were originally there when created
            // *and* created not null.
            runSQL(session, "create table "+fixTableName(session,"test")+" ( mychar char(10), "+DB2_PK_COLNAME+" integer not null)");
        } else {
            runSQL(session, "create table "+fixTableName(session,"test")+" ( mychar char(10))"+pageSizeClause);
        }
        
        runSQL(session, "create table "+fixTableName(session,"test1")+" ( mychar char(10))"+pageSizeClause);
        runSQL(session, "create table "+fixTableName(session,"test2")+" ( mychar char(10))"+pageSizeClause);
        runSQL(session, "create table "+fixTableName(session,"test3")+" ( mychar char(10))"+pageSizeClause);
        runSQL(session, "create table "+fixTableName(session,"test4")+" ( mychar char(10))"+pageSizeClause);
        runSQL(session, "create table "+fixTableName(session,"test5")+" ( mychar char(10))"+pageSizeClause);
        
        if (dialect.supportsAlterColumnNull()) {
            runSQL(session, "create table "+fixTableName(session,"pktest")+" ( pk_col_1 integer, pk_col_2 integer )"+pageSizeClause);
        }
    }    
    
    private void runTests() throws Exception {        
        for (Iterator iter = sessions.iterator(); iter.hasNext();) {            
            ISession session = (ISession) iter.next();
            HibernateDialect dialect = 
                DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);  

            init(session);
            testAddColumn(session);
            testDropColumn(session);
            testAlterDefaultValue(session);
            testColumnComment(session);
            testAlterNull(session);
            testAlterName(session);
            testAlterColumnlength(session);
            // DB2 cannot alter a column's null attribute directly (only 
            // through constraints).  Not only that, but it's apparently not a
            // valid thing to do to create a primary key using a column that has
            // been made "not null" via a check constraint.  Therefore, the only
            // columns that qualify to be made PKs are those that were declared
            // not null at the time of table creation.
            if (DialectFactory.isDB2Session(session)) {
                testAddPrimaryKey(session, new TableColumnInfo[] {db2pkCol});
                testDropPrimaryKey(session, db2pkCol.getTableName());
            } else {
                try {
                    testAddPrimaryKey(session, new TableColumnInfo[] {notNullIntegerCol});
                } catch (UnsupportedOperationException e) {
                    System.err.println("doesn't support adding primary keys");
                }
                try {
                    testDropPrimaryKey(session, notNullIntegerCol.getTableName());
                } catch (UnsupportedOperationException e) {
                    System.err.println("doesn't support dropping primary keys");
                }
                
                // Test whether or not the dialect correctly converts nullable
                // columns to not-null before applying the primary key - if 
                // necessary
                if (dialect.supportsAlterColumnNull()) {
                    try {
                        TableColumnInfo[] infos = new TableColumnInfo[] {doubleColumnPKOne, doubleColumnPKTwo}; 
                        testAddPrimaryKey(session, infos);
                    } catch (UnsupportedOperationException e) {
                        System.err.println("doesn't support adding primary keys");
                    } 
                }
            }
            testDropMatView(session);
        }
    }

    /**
        CREATE MATERIALIZED VIEW matview2
            REFRESH COMPLETE
            NEXT  SYSDATE + 1
            WITH PRIMARY KEY 
            AS SELECT * FROM TEST;

     * @param session
     * @throws Exception
     */
    private void testDropMatView(ISession session) throws Exception {
        if (!DialectFactory.isOracleSession(session)) return;
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);          
        
        testAddPrimaryKey(session, new TableColumnInfo[] { pkCol } );
        String createMatViewSQL = 
            "CREATE MATERIALIZED VIEW MATVIEW " +
            "       REFRESH COMPLETE " +
            "   NEXT  SYSDATE + 1 " +
            "   WITH PRIMARY KEY " +
            "   AS SELECT * FROM TEST ";
        runSQL(session, createMatViewSQL);
        MockSession msession = (MockSession)session;
        String cat = msession.getDefaultCatalog();
        String schema = msession.getDefaultSchema();
        SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
        ITableInfo info = new TableInfo(cat, schema, "MATVIEW", "TABLE", "", md);
        String dropSQL = dialect.getTableDropSQL(info, true, false);
        runSQL(session, dropSQL);
    }
    
    
    
    private void testAlterName(ISession session) throws Exception {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);  

        TableColumnInfo newNameCol = 
            getVarcharColumn("newNameCol", "test", true, null, "A column to be renamed");
        if (dialect.supportsRenameColumn()) {
            String sql = dialect.getColumnNameAlterSQL(renameCol, newNameCol);
            runSQL(session, sql);
        } else {
            try {
                dialect.getColumnNameAlterSQL(renameCol, newNameCol);
            } catch (UnsupportedOperationException e) {
                // this is expected
                System.err.println(e.getMessage());
            }
        }
    }
    
    private void testDropColumn(ISession session) throws Exception {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);  
        
        if (dialect.supportsDropColumn()) {
            dropColumn(session, dropCol);
        } else {
            try {
                dropColumn(session, dropCol);
                throw new IllegalStateException(
                        "Expected dialect to fail to provide SQL for dropping a column");
            } catch (UnsupportedOperationException e) {
                // This is what we expect
                System.err.println(e.getMessage());
            }
        }        
    }
    
    private void testAlterColumnlength(ISession session) throws Exception {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);  
        
        
        //convert nullint into a varchar(100)
        /*
         * This won't work on Derby where non-varchar columns cannot be 
         * altered among other restrictions.
         * 
        TableColumnInfo nullintVC = 
            getVarcharColumn("nullint", true, "defVal", "A varchar comment");
        String alterColTypeSQL = dialect.getColumnTypeAlterSQL(firstCol, nullintVC);
        runSQL(session, alterColTypeSQL);
        */
        TableColumnInfo thirdColLonger = 
            getVarcharColumn("nullvc", "test3", true, "defVal", "A varchar comment", 30);
        if (dialect.supportsAlterColumnType()) {
            String alterColLengthSQL = 
                dialect.getColumnTypeAlterSQL(thirdCol, thirdColLonger);
            runSQL(session, alterColLengthSQL);     
        } else {
            try {
                dialect.getColumnTypeAlterSQL(thirdCol, thirdColLonger);
                throw new IllegalStateException(
                    "Expected dialect to fail to provide SQL for altering column type");
            } catch (UnsupportedOperationException e) {
                // this is expected
            }
        }
    }
    
    private void testAlterDefaultValue(ISession session) throws Exception {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);  
        
        TableColumnInfo varcharColWithDefaultValue = 
            getVarcharColumn("noDefaultVarcharCol",
                             noDefaultValueVarcharCol.getTableName(),
                             true, 
                             "Default Value", 
                             "A column with a default value");
        
        TableColumnInfo integerColWithDefaultVal = 
            getIntegerColumn("noDefaultIntgerCol",
                             noDefaultValueIntegerCol.getTableName(),
                             true, 
                             "0", 
                             "An integer column with a default value");
        
        if (dialect.supportsAlterColumnDefault()) {
            String defaultValSQL = 
                dialect.getColumnDefaultAlterSQL(varcharColWithDefaultValue);
            runSQL(session, defaultValSQL);
            
            defaultValSQL = 
                dialect.getColumnDefaultAlterSQL(integerColWithDefaultVal);
            runSQL(session, defaultValSQL);
        } else {
            try {
                dialect.getColumnDefaultAlterSQL(noDefaultValueVarcharCol);
                throw new IllegalStateException(
                        "Expected dialect to fail to provide SQL for column default alter");
            } catch (UnsupportedOperationException e) {
                // This is what we expect.
                System.err.println(e.getMessage());
            }
        }        
    }
    
    private void testAlterNull(ISession session) throws Exception {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);  
        TableColumnInfo notNullThirdCol = 
            getVarcharColumn("nullvc", "test3", false, "defVal", "A varchar comment");        
        if (dialect.supportsAlterColumnNull()) {
            String notNullSQL = 
                dialect.getColumnNullableAlterSQL(notNullThirdCol);
            runSQL(session, notNullSQL);
        } else {
            try {
                dialect.getColumnNullableAlterSQL(notNullThirdCol);     
                throw new IllegalStateException(
                        "Expected dialect to fail to provide SQL for column nullable alter");
            } catch (UnsupportedOperationException e) {
                // this is expected
                System.err.println(e.getMessage());
            }
        }
    }    
    
    private void testAddColumn(ISession session) 
        throws Exception 
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
    
    private void addColumn(ISession session,    
                           TableColumnInfo info) 
        throws Exception 
    {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);
       
        String[] sqls = dialect.getColumnAddSQL(info);
        for (int i = 0; i < sqls.length; i++) {
            String sql = sqls[i];
            runSQL(session, sql);
        }
        
    }

    private void testColumnComment(ISession session) throws Exception {
        HibernateDialect dialect = getDialect(session);
        if (dialect.supportsColumnComment()) {
            alterColumnComment(session, firstCol);
            alterColumnComment(session, secondCol);
            alterColumnComment(session, thirdCol);
            alterColumnComment(session, fourthCol);
        } else {
            try {
                alterColumnComment(session, firstCol);    
            } catch (UnsupportedOperationException e) {
                // This is expected
                System.err.println(e.getMessage());
            }
        }
    }
    
    private void alterColumnComment(ISession session,    
                                    TableColumnInfo info) 
        throws Exception    
    {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);
        String commentSQL = dialect.getColumnCommentAlterSQL(info);
        if (commentSQL != null && !commentSQL.equals("")) {
            runSQL(session, commentSQL);
        }
    }
     
    private String getPKName(String tableName) {
        return tableName.toUpperCase()+"_PK";
    }
   
    
    private void testDropPrimaryKey(ISession session, String tableName) 
        throws Exception 
    {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);
        String pkName = getPKName(tableName);
        String sql = dialect.getDropPrimaryKeySQL(pkName, tableName);
        runSQL(session, sql);
    }
    
    private void testAddPrimaryKey(ISession session,
                               TableColumnInfo[] colInfos) 
        throws Exception 
    {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);

        String tableName = colInfos[0].getTableName();
        
        if (session.getSQLConnection().getSQLMetaData().storesUpperCaseIdentifiers()) {
            tableName = tableName.toUpperCase();
        }
        
        SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
        String catalog = ((MockSession)session).getDefaultCatalog();
        String schema = ((MockSession)session).getDefaultSchema();
        
        ITableInfo[] infos = null;
        try {
            md.getTables(catalog, schema, tableName, new String[] {"TABLE"}, null);
        } catch (SQLException e) {
            // Do nothing
        }
        
        ITableInfo ti = null;
        if (infos != null && infos.length > 0) {
            ti = infos[0];
        } else {
            // Couldn't locate the table - just try to fake it.
            ti = new TableInfo(catalog, schema, tableName, "TABLE", "", md);
        }
        
        String[] pkSQLs = 
            dialect.getAddPrimaryKeySQL(getPKName(tableName), colInfos, ti);
        
        for (int i = 0; i < pkSQLs.length; i++) {
            String pkSQL = pkSQLs[i];
            runSQL(session, pkSQL);
        }
    }
    
    private void dropColumn(ISession session,    
                            TableColumnInfo info) 
    throws Exception 
    {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);

        String sql = dialect.getColumnDropSQL(info.getTableName(), info.getColumnName());
        runSQL(session, sql);
    }
    
    private HibernateDialect getDialect(ISession session) throws Exception  {
        return DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);
    }
    
    private void runSQL(ISession session, String sql) throws Exception {
        HibernateDialect dialect = getDialect(session);        
        Connection con = session.getSQLConnection().getConnection();
        Statement stmt = con.createStatement();
        System.out.println("Running SQL ("+dialect.getDisplayName()+"): "+sql);
        stmt.execute(sql);
    }
    
    private TableColumnInfo getIntegerColumn(String name,
                                             String tableName,
                                             boolean nullable, 
                                             String defaultVal,
                                             String comment) 
    {
        return getColumn(java.sql.Types.INTEGER, 
                         "INTEGER", 
                         name, 
                         tableName,
                         nullable, 
                         defaultVal, 
                         comment, 
                         10, 
                         0);
    }

    private TableColumnInfo getVarcharColumn(String name,
                                             String tableName,
                                             boolean nullable, 
                                             String defaultVal,
                                             String comment,
                                             int size)
    {
        return getColumn(java.sql.Types.VARCHAR,
                "VARCHAR",
                name,
                tableName,
                nullable, 
                defaultVal, 
                comment, 
                size, 
                0);
    }
    
    
    private TableColumnInfo getVarcharColumn(String name,
                                             String tableName,
                                             boolean nullable, 
                                             String defaultVal,
                                             String comment)
    {
        return getColumn(java.sql.Types.VARCHAR,
                         "VARCHAR",
                         name,
                         tableName,
                         nullable, 
                         defaultVal, 
                         comment, 
                         20, 
                         0);
    }
    
    private TableColumnInfo getColumn(int dataType,
                                      String dataTypeName,
                                      String name,
                                      String tableName,
                                      boolean nullable, 
                                      String defaultVal,
                                      String comment,
                                      int columnSize,
                                      int scale) 
    {
        String isNullable = "YES";
        int isNullAllowed = DatabaseMetaData.columnNullable;
        if (!nullable) {
            isNullable = "NO";
            isNullAllowed = DatabaseMetaData.columnNoNulls;
        }        
        TableColumnInfo result = 
            new TableColumnInfo("testCatalog",          // catalog 
                                "testSchema",           // schema
                                tableName,              // tableName
                                name,                   // columnName
                                dataType,               // dataType
                                dataTypeName,           // typeName 
                                columnSize,             // columnSize
                                scale,                  // decimalDigits
                                10,                     // radix
                                isNullAllowed,          // isNullAllowed
                                comment,                // remarks
                                defaultVal,             // defaultValue
                                0,                      // octet length
                                0,                      // ordinal position
                                isNullable);            // isNullable 
        return result;
    }
    
    private String fixTableName(ISession session, String table) throws Exception {
        String result = null;
        SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
        if (md.storesUpperCaseIdentifiers()) {
            result = table.toUpperCase();
        } else {
            result = table.toLowerCase();
        }
        return result;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        DialectLiveTestRunner runner = new DialectLiveTestRunner();
        runner.runTests();
    }
}
