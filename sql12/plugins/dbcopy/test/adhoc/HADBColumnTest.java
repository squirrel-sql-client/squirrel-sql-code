package adhoc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HADBColumnTest {

    String jdbcUrl = "jdbc:sun:hadb:system@192.168.1.129:15129,192.168.1.129:15109";
    
    String user = "system";
    
    String pass = "password";
    
    Connection con = null;
    
    String dropSQL = 
        "drop table bigint_type_table ";
    
    String createTableSQL = 
        "CREATE TABLE bigint_type_table " +
        "( " +
        "   bigint_column integer , pkcol integer PRIMARY KEY not null " +
        ") ";

    String insertSQL = 
        "insert into bigint_type_table (bigint_column, pkcol) " +
        "values (12345, 1) ";    
    
    
    public HADBColumnTest() throws Exception {
        init();
    }
    
    public void init() throws Exception {
        Class.forName("com.sun.hadb.jdbc.Driver");
        con = DriverManager.getConnection(jdbcUrl,user, pass);
    }
    

    /**
     * This fails with a Connection failure - java.io.EOFException
     * @throws SQLException
     */
    public void doTest() throws SQLException {
        Statement stmt = con.createStatement();
        try {
            stmt.execute(dropSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        stmt.execute(createTableSQL);
        stmt.executeUpdate(insertSQL);
        DatabaseMetaData md = con.getMetaData();
        ResultSet rs = 
            md.getColumns(null, null, "bigint_type_table", "bigint_column");
        while (rs.next()) {
            String columnName = rs.getString(4); // COLUMN_NAME
            System.out.println("columnName: "+columnName);
        }
        
    }

    
    public void shutdown() throws SQLException {
        con.close();
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        HADBColumnTest test = new HADBColumnTest();
        
        test.doTest();
        
        test.shutdown();
    }

}
