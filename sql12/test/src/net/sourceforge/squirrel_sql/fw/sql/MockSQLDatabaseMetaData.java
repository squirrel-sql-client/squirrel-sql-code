package net.sourceforge.squirrel_sql.fw.sql;

import java.sql.SQLException;

import com.mockobjects.sql.MockConnection2;

public class MockSQLDatabaseMetaData extends SQLDatabaseMetaData {

    static MockConnection2 conn = new MockConnection2();
    static SQLConnection sqlConn = new SQLConnection(conn, null, null);
    
    
    public MockSQLDatabaseMetaData() {
        super(sqlConn);
    }


    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData#getExportedKeysInfo(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
     */
    @Override
    public synchronized ForeignKeyInfo[] getExportedKeysInfo(ITableInfo ti) throws SQLException {
        throw new SQLException("Simulated Unsupported API Method");
    }


    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData#getImportedKeysInfo(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
     */
    @Override
    public synchronized ForeignKeyInfo[] getImportedKeysInfo(ITableInfo ti) throws SQLException {
        throw new SQLException("Simulated Unsupported API Method");
    }
    
    
    
}
