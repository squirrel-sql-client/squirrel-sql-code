package net.sourceforge.squirrel_sql.test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

/**
 * A utility class for building test objects.
 * 
 * @author manningr
 */
public class TestUtil {

    public static ISession getEasyMockSession(String dbName) 
        throws SQLException 
    {
        ISQLDatabaseMetaData md = getEasyMockSQLMetaData(dbName);
        ISession session = getEasyMockSession(md);        
        return session;
    }
    
    public static ISession getEasyMockSession(ISQLDatabaseMetaData md) {
        ISession session =
            createMock(ISession.class);
        expect(session.getMetaData()).andReturn(md).anyTimes();
        expect(session.getApplication()).andReturn(getEasyMockApplication()).anyTimes();
        replay(session);
        return session;
    }
    
    public static ISession getEasyMockSession(ISQLDatabaseMetaData md, ResultSet rs) 
        throws SQLException 
    {
        ISQLConnection con = getEasyMockSQLConnection(rs);
        ISession session =
            createMock(ISession.class);
        expect(session.getMetaData()).andReturn(md).anyTimes();
        expect(session.getApplication()).andReturn(getEasyMockApplication()).anyTimes();
        expect(session.getSQLConnection()).andReturn(con).anyTimes();
        replay(session);
        return session;
    }
    
    
    
    public static ISQLConnection getEasyMockSQLConnection(ResultSet rs) 
        throws SQLException 
    {
        Statement stmt = createNiceMock(Statement.class);
        expect(stmt.executeQuery("")).andReturn(rs);
        replay(stmt);
        
        Connection con = createNiceMock(Connection.class);
        expect(con.createStatement()).andReturn(stmt);
        expect(con.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                   ResultSet.CONCUR_READ_ONLY)).andReturn(stmt);
        replay(con);
        
        
        ISQLConnection sqlCon = createNiceMock(ISQLConnection.class);
        expect(sqlCon.getConnection()).andReturn(con);
        replay(sqlCon);
        
        return sqlCon;
    }
    
    public static ISQLDatabaseMetaData getEasyMockSQLMetaData(String dbName) 
        throws SQLException 
    {
        ISQLDatabaseMetaData md = 
            createNiceMock(ISQLDatabaseMetaData.class);
        expect(md.getDatabaseProductName()).andReturn(dbName).anyTimes();
        expect(md.getDatabaseProductVersion()).andReturn("1.0").anyTimes();
        replay(md);
        return md;
    }
    
    public static IApplication getEasyMockApplication() {
        IApplication result = createNiceMock(IApplication.class);
        expect(result.getMainFrame()).andReturn(null);
        replay(result);
        return result;
    }

    public static TableColumnInfo getBigintColumnInfo(ISQLDatabaseMetaData md,
            boolean nullable) 
    {
        return getTableColumnInfo(md, 
                                  java.sql.Types.BIGINT, 
                                  "Bigint", 
                                  20, 
                                  10, 
                                  nullable);        
    }
    
    public static TableColumnInfo getBinaryColumnInfo(ISQLDatabaseMetaData md,
                                                      boolean nullable) 
    {
        return getTableColumnInfo(md, 
                                  java.sql.Types.BINARY, 
                                  "Binary", 
                                  -1, 
                                  0, 
                                  nullable);        
    }    
    
    public static TableColumnInfo getBlobColumnInfo(ISQLDatabaseMetaData md,
                                                    boolean nullable) 
    {
        return getTableColumnInfo(md, 
                                  java.sql.Types.BLOB, 
                                  "Binary LOB", 
                                  Integer.MAX_VALUE, 
                                  0, 
                                  nullable);        
    }

    public static TableColumnInfo getClobColumnInfo(ISQLDatabaseMetaData md,
            boolean nullable) 
    {
        return getTableColumnInfo(md, 
                                  java.sql.Types.CLOB, 
                                  "Character LOB", 
                                  Integer.MAX_VALUE, 
                                  0, 
                                  nullable);        
    }
    
    public static TableColumnInfo getIntegerColumnInfo(ISQLDatabaseMetaData md,
                                                       boolean nullable) 
    {
        return getTableColumnInfo(md, 
                                  java.sql.Types.INTEGER, 
                                  "Integer", 
                                  10, 
                                  0, 
                                  nullable);        
    }
    
    public static TableColumnInfo getDateColumnInfo(ISQLDatabaseMetaData md,
                                                    boolean nullable) 
    {
        return getTableColumnInfo(md, 
                                  java.sql.Types.DATE, 
                                  "Date", 
                                  0, 
                                  0, 
                                  nullable);                
    }

    public static TableColumnInfo getLongVarcharColumnInfo(ISQLDatabaseMetaData md,
                                                           boolean nullable,
                                                           int length) 
    {
        return getTableColumnInfo(md, 
                java.sql.Types.LONGVARCHAR, 
                "LongVarchar", 
                length, 
                0, 
                nullable);                
    }
    
    
    public static TableColumnInfo getVarcharColumnInfo(ISQLDatabaseMetaData md,
                                                       boolean nullable,
                                                       int length) 
    {
        return getTableColumnInfo(md, 
                                  java.sql.Types.VARCHAR, 
                                  "Varchar", 
                                  length, 
                                  0, 
                                  nullable);                
    }
    
    public static TableColumnInfo getTableColumnInfo(ISQLDatabaseMetaData md,
                                                     int type,
                                                     String typeName,
                                                     int columnSize,
                                                     int decimalDigits,
                                                     boolean nullable) 
    {
        int isNullableInt = 0;
        String isNullableStr = "no";
            
        if (nullable) {
            isNullableInt = 1;
            isNullableStr = "yes";            
        }
        TableColumnInfo info = new TableColumnInfo("TestCatalog",
                "TestSchema",
                "TestTable",
                "TestColumn",
                type,
                typeName,      // typeName
                columnSize,    // columnSize
                decimalDigits, // decimalDigits
                0,             // radix
                isNullableInt, // isNullAllowable
                "TestRemark",
                "0",           // defaultValue
                0,             // octetLength
                0,             // ordinalPosition
                isNullableStr, // isNullable
                md);
        return info;
    }
    
}
