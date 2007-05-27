package test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

public class SQuirreLConnectTest {

    public static void main(String[] args) 
        throws Exception 
    {
        if (args.length < 4) {
            printUsage();
        }
        String driver = args[0];
        String url = args[1];
        String user = args[2];
        String pass = args[3];
        
        Connection connection = getConnection(driver, url, user, pass, null);

        System.out.println("Connected to: " + connection.getMetaData().getURL());
    }

    private static void printUsage() {
        System.out.println("SQuirreLConnectTest: <driver> <url> <user> <pass>");
        System.exit(-1);
    }
    
    /**
     * @param props
     *            may be null
     */
    public static Connection getConnection(String driver, String url,
            String user, String pw, Properties props)
            throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, SQLException {
        if (null == props) {
            props = new Properties();
        }

        props.put("user", user);
        props.put("password", pw);

        Driver driverInst = (Driver) Class.forName(driver).newInstance();

        Connection jdbcConn = driverInst.connect(url, props);
        if (jdbcConn == null) {
            throw new RuntimeException("Connect failed");
        }

        return jdbcConn;
    }
}
