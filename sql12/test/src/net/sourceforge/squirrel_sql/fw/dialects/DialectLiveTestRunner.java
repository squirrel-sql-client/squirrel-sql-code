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
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

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
            sessions.add(new MockSession(driver, url, user, pass));            
        }
    }
    
    private void runTests() throws Exception {

        for (Iterator iter = sessions.iterator(); iter.hasNext();) {
            ISession session = (ISession) iter.next();
            HibernateDialect dialect = getDialect(session);
            createTestTable(session);
            TableColumnInfo firstCol = 
                getIntegerColumn("nullint", true, "0", "An int comment");
            TableColumnInfo secondCol =
                getIntegerColumn("notnullint", false, "0", "An int comment");
            TableColumnInfo thirdCol =
                getVarcharColumn("nullvc", true, "defVal", "A varchar comment");
            TableColumnInfo fourthCol =
                getVarcharColumn("notnullvc", false, "defVal", "A varchar comment");
            TableColumnInfo dropCol =
                getVarcharColumn("dropCol", true, null, "A varchar comment");
            
            addColumn(session, firstCol);
            addColumn(session, secondCol);
            addColumn(session, thirdCol);
            addColumn(session, fourthCol);
            addColumn(session, dropCol);
            if (dialect.supportsColumnComment()) {
                alterColumnComment(session, firstCol);
                alterColumnComment(session, secondCol);
                alterColumnComment(session, thirdCol);
                alterColumnComment(session, fourthCol);
            }
            // Convert the thirdCol to not null 
            TableColumnInfo notNullThirdCol = 
                getVarcharColumn("nullvc", false, "defVal", "A varchar comment");
            String notNullSQL = 
                dialect.getColumnNullableAlterSQL(notNullThirdCol);
            runSQL(session, notNullSQL);
            
            // then make it the PK
            addPrimaryKey(session, new TableColumnInfo[] {thirdCol});
            
            if (dialect.supportsDropColumn()) {
                dropColumn(session, dropCol);
            }
            
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
            
            // convert thirdCol to varchar(1000) from varchar(100)
            TableColumnInfo thirdColLonger = 
                getVarcharColumn("nullvc", true, "defVal", "A varchar comment", 1000);
            String alterColLengthSQL = dialect.getColumnTypeAlterSQL(thirdCol, thirdColLonger);
            runSQL(session, alterColLengthSQL);
        }
    }
    
    private void createTestTable(ISession session) throws Exception {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);
        try {
            runSQL(session, dialect.getTableDropSQL("test", true));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (DialectFactory.isIngresSession(session)) {
            // alterations fail for some reason unless you do this...
            runSQL(session, "create table test ( mychar char(10)) with page_size=4096");
        } else {
            runSQL(session, "create table test ( mychar char(10))");
        }
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
    
    private void alterColumnNullable(ISession session,    
                                     TableColumnInfo info) 
        throws Exception    
    {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);
        String nullSQL = dialect.getColumnCommentAlterSQL(info);
        if (nullSQL != null && !nullSQL.equals("")) {
            runSQL(session, nullSQL);
        }
    }
    
    private void addPrimaryKey(ISession session,
                               TableColumnInfo[] colInfos) 
        throws Exception 
    {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);

        String tableName = colInfos[0].getTableName();
        
        String[] pkSQLs = 
            dialect.getAddPrimaryKeySQL(tableName.toUpperCase()+"_PK", colInfos);
        
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

        String sql = dialect.getColumnDropSQL("test", info.getColumnName());
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
                                             boolean nullable, 
                                             String defaultVal,
                                             String comment) 
    {
        return getColumn(java.sql.Types.INTEGER, 
                         "INTEGER", 
                         name, 
                         nullable, 
                         defaultVal, 
                         comment, 
                         10, 
                         0);
    }

    private TableColumnInfo getVarcharColumn(String name,
                                             boolean nullable, 
                                             String defaultVal,
                                             String comment,
                                             int size)
    {
        return getColumn(java.sql.Types.VARCHAR,
                "VARCHAR",
                name,
                nullable, 
                defaultVal, 
                comment, 
                size, 
                0);
    }
    
    
    private TableColumnInfo getVarcharColumn(String name,
                                             boolean nullable, 
                                             String defaultVal,
                                             String comment)
    {
        return getColumn(java.sql.Types.VARCHAR,
                         "VARCHAR",
                         name,
                         nullable, 
                         defaultVal, 
                         comment, 
                         100, 
                         0);
    }
    
    private TableColumnInfo getColumn(int dataType,
                                      String dataTypeName,
                                      String name,
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
                                "test",                 // tableName
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
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        DialectLiveTestRunner runner = new DialectLiveTestRunner();
        runner.runTests();
    }
}
