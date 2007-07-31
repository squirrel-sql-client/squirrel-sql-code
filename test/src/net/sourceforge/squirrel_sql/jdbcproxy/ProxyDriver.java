package net.sourceforge.squirrel_sql.jdbcproxy;
/*
 * Copyright (C) 2006 Rob Manning
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

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class ProxyDriver implements Driver {

    Driver _driver = null;
        
    public static final String DRIVER_URL_PROP_KEY = "driverUrl";
    public static final String DRIVER_CLASS_PROP_KEY = "driverClass";
    public static final String DRIVER_USER_PROP_KEY = "driverUser";
    public static final String DRIVER_PASS_PROP_KEY = "driverPass";
    public static final String TRACK_METHOD_CALLS_KEY = "trackMethodCalls";
    
    public ProxyDriver() {
    }
    
    public int getMajorVersion() {
        return _driver.getMajorVersion();
    }

    public int getMinorVersion() {
        return _driver.getMinorVersion();
    }

    public boolean jdbcCompliant() {
        return _driver.jdbcCompliant();
    }

    public boolean acceptsURL(String url) throws SQLException {
        System.out.println("acceptsUrl: url="+url);
        if (url.startsWith("jdbc:proxydriver")) {
            System.out.println("true");
            return true;
        } else {
            System.out.println("false");
            return false;
        }
    }

    public Connection connect(String proxyUrl, Properties props) throws SQLException {
        if (null == props) {
            throw new IllegalArgumentException("Properties arg was null");
        }
        ProxyMethodManager.setDriverProperties(props);
        System.out.println("Connecting with proxyUrl="+proxyUrl);
        String propsUrl = props.getProperty(DRIVER_URL_PROP_KEY);
        String propsClass = props.getProperty(DRIVER_CLASS_PROP_KEY);
        String propsUser = props.getProperty(DRIVER_USER_PROP_KEY);
        String propsPass = props.getProperty(DRIVER_PASS_PROP_KEY);
        try {
            _driver = (Driver) Class.forName(propsClass).newInstance();

            Properties driverProps = new Properties();
            driverProps.put("user", propsUser);
            driverProps.put("password", propsPass);
            
            Connection tmp = _driver.connect(propsUrl, driverProps);
            if (tmp == null) {
                throw new RuntimeException("Connect failed");
            }
            return new ProxyConnection(tmp);
        } catch (SQLException e) { 
            throw e;
        } catch (ClassNotFoundException e) {
            System.err.println("Unable to locate class "+propsClass);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
        throws SQLException 
    {
        List<DriverPropertyInfo> list = new ArrayList<DriverPropertyInfo>();

        DriverPropertyInfo classInfo = new SortableDriverProperty(DRIVER_CLASS_PROP_KEY, null);
        classInfo.required = true;
        list.add(classInfo);
        DriverPropertyInfo urlInfo = new SortableDriverProperty(DRIVER_URL_PROP_KEY, null);
        urlInfo.required = true;
        list.add(urlInfo);
        DriverPropertyInfo userInfo = new SortableDriverProperty(DRIVER_USER_PROP_KEY, null);
        userInfo.required = true;
        list.add(userInfo);
        DriverPropertyInfo passInfo = new SortableDriverProperty(DRIVER_PASS_PROP_KEY, null);
        passInfo.required = true;
        list.add(passInfo);
        DriverPropertyInfo trackCalls = new SortableDriverProperty(TRACK_METHOD_CALLS_KEY, null);
        trackCalls.required = false;
        list.add(trackCalls);
        List<SortableDriverProperty> connMethods = 
            getMethods("java.sql.Connection", "ProxyConnection");
        Collections.sort(connMethods);
        list.addAll(connMethods);
        List<SortableDriverProperty> mdMethods = 
            getMethods("java.sql.DatabaseMetaData", "ProxyDatabaseMetaData");
        Collections.sort(mdMethods);
        list.addAll(mdMethods);
        List<SortableDriverProperty> psMethods = 
            getMethods("java.sql.PreparedStatement", "ProxyPreparedStatement");
        Collections.sort(psMethods);
        list.addAll(psMethods);        
        List<SortableDriverProperty> rsMethods = getMethods("java.sql.ResultSet", "ProxyResultSet");
        Collections.sort(rsMethods);
        list.addAll(rsMethods);        
        List<SortableDriverProperty> rsmdMethods = getMethods("java.sql.ResultSetMetaData", "ProxyResultSetMetaData");
        Collections.sort(rsmdMethods);
        list.addAll(rsmdMethods);        
        List<SortableDriverProperty> stmtMethods = getMethods("java.sql.Statement", "ProxyStatement");
        Collections.sort(stmtMethods);
        list.addAll(stmtMethods);        
        DriverPropertyInfo[] ar = new DriverPropertyInfo[list.size()];
        return list.toArray(ar);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws SQLException {
        ProxyDriver driver = new ProxyDriver();
        Properties props = new Properties();
        props.put(DRIVER_URL_PROP_KEY, "jdbc:db2://localhost:50000/sample");
        props.put(DRIVER_CLASS_PROP_KEY, "com.ibm.db2.jcc.DB2Driver");
        props.put(DRIVER_USER_PROP_KEY, "dbcopy");
        props.put(DRIVER_PASS_PROP_KEY, "password");
        Connection con = driver.connect("jdbc:proxydriver", props);
        if (con == null) {
            System.out.println("Connection failed");
        } else {
            System.out.println("Connection succeeded");
        }

    }
    
    private List<SortableDriverProperty> getMethods(String className, String proxyClassName) {
        ArrayList<SortableDriverProperty> result = new ArrayList<SortableDriverProperty>();
        try {
            Class<?> c = Class.forName(className);
            Method[] methods = c.getDeclaredMethods();
            
            if (methods.length > 0) {
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    SortableDriverProperty info = 
                        new SortableDriverProperty(proxyClassName + "." + method.getName(), null);
                    info.required = false;
                    result.add(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private class SortableDriverProperty extends DriverPropertyInfo
                                         implements Comparable<SortableDriverProperty> {        
        public SortableDriverProperty(String name, String value) {
            super(name, value);
        }

        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(SortableDriverProperty o) {
            return name.compareTo(o.name); 
        }
    }
}
