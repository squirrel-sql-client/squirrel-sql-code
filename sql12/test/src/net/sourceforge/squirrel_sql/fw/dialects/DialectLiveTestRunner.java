package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

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

    ISession[] sessions = null;
    ResourceBundle bundle = null;
    
    
    
    public DialectLiveTestRunner() throws Exception {
        ApplicationArguments.initialize(new String[] {});
        bundle = ResourceBundle.getBundle("net.sourceforge.squirrel_sql.fw.dialects.dialectLiveTest");
        initSessions();
    }
    
    private void initSessions() throws Exception {
        String jdbcPropCount = bundle.getString("jdbcPropCount");
        int count = Integer.parseInt(jdbcPropCount);
        sessions = new ISession[count];
        for (int i=0; i < count; i++) {
            String url = bundle.getString("jdbcUrl_"+i);
            String user = bundle.getString("jdbcUser_"+i);
            String pass = bundle.getString("jdbcPass_"+i);
            String driver = bundle.getString("jdbcDriver_"+i);
            sessions[i] = new MockSession(driver, url, user, pass);
        }        
    }
    
    private void runTests() throws Exception {
        for (int i = 0; i < sessions.length; i++) {
            ISession session = sessions[i];
            HibernateDialect dialect = getDialect(session);
            createTestTable(session);
            TableColumnInfo firstCol = 
                getIntegerColumn("nullint", true, "0", "An int comment");
            addColumn(session, firstCol);
            addColumn(session, getIntegerColumn("notnullint", false, "0", "An int comment"));
            addColumn(session, getVarcharColumn("nullvc", true, "defVal", "A varchar comment"));
            addColumn(session, getVarcharColumn("notnullvc", false, "defVal", "A varchar comment"));
            if (dialect.supportsDropColumn()) {
                dropColumn(session, firstCol);
            }
        }
    }
    
    private void createTestTable(ISession session) throws Exception {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);
        try {
            runSQL(session, dialect.getTableDropSQL("test", true));
        } catch (SQLException e) {
            // Don't care if table doesn't exist
        }
        runSQL(session, "create table test ( mychar char(10))");
    }
    
    private void addColumn(ISession session,    
                           TableColumnInfo info) 
        throws Exception 
    {
        HibernateDialect dialect = 
            DialectFactory.getDialect(session, DialectFactory.DEST_TYPE);
       
        String[] sqls = dialect.getColumnAddSQL("test", info);
        for (int i = 0; i < sqls.length; i++) {
            String sql = sqls[i];
            runSQL(session, sql);
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
