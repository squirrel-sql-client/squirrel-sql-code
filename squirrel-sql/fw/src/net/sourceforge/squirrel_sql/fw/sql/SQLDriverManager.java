package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.squirrel_sql.fw.util.Logger;
public class SQLDriverManager {
    private Logger _logger;
    private HashMap _driverInfo = new HashMap();

    private MyDriverListener _myDriverListener = new MyDriverListener();

    public SQLDriverManager(Logger logger) {
        super();
        _logger = logger;
    }
    public synchronized void registerSQLDriver(ISQLDriver sqlDriver)
            throws IllegalAccessException, InstantiationException,
                    ClassNotFoundException {
        unregisterSQLDriver(sqlDriver);
        sqlDriver.addPropertyChangeListener(_myDriverListener);
        Class driverClass = null;
        if (sqlDriver.getUsesClassPath()) {
            driverClass = Class.forName(sqlDriver.getDriverClassName());
        } else {
            driverClass = new SQLDriverClassLoader(sqlDriver).loadClass(sqlDriver.getDriverClassName());
        }
        _driverInfo.put(sqlDriver.getIdentifier(), driverClass.newInstance());
    }
    public synchronized void unregisterSQLDriver(ISQLDriver sqlDriver) {
        sqlDriver.removePropertyChangeListener(_myDriverListener);
        _driverInfo.remove(sqlDriver.getIdentifier());
    }

    public synchronized SQLConnection getConnection(ISQLDriver sqlDriver,
                                                    ISQLAlias alias,
                                                    String user, String pw)
            throws ClassNotFoundException, IllegalAccessException,
                    InstantiationException, BaseSQLException {
        Properties props = new Properties();
        if (user != null) {
            props.put("user", user);
        }
        if (pw != null) {
            props.put("password", pw);
        }

        try {
            if (!sqlDriver.getUsesClassPath()) {
                try {
                    Class driverCls = new SQLDriverClassLoader(sqlDriver).loadClass(sqlDriver.getDriverClassName());
                    Driver driver = (Driver)driverCls.newInstance();
                    return new SQLConnection(driver.connect(alias.getUrl(), props));
                } catch (SQLException ex) {
                    throw new BaseSQLException(ex);
                }
            }
            return new SQLConnection(DriverManager.getConnection(alias.getUrl(), user, pw));
        } catch (SQLException ex) {
            throw new BaseSQLException(ex);
        }
    }

    private final class MyDriverListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            final String propName = evt.getPropertyName();
            if (propName != null && propName.equals(ISQLDriver.IPropertyNames.DRIVER_CLASS)) {
                Object obj = evt.getSource();
                if (obj instanceof ISQLDriver) {
                    ISQLDriver driver = (ISQLDriver)obj;
                    SQLDriverManager.this.unregisterSQLDriver(driver);
                    try {
                        SQLDriverManager.this.registerSQLDriver(driver);
                    } catch (IllegalAccessException ex) {
                        _logger.showMessage(Logger.ILogTypes.ERROR, "Unable to create instance of Class " + driver.getDriverClassName() + " for JDCB driver " + driver.getName());
                    } catch (InstantiationException ex) {
                        _logger.showMessage(Logger.ILogTypes.ERROR, "Unable to create instance of Class " + driver.getDriverClassName() + " for JDCB driver " + driver.getName());
                    } catch (ClassNotFoundException ex) {
                        _logger.showMessage(Logger.ILogTypes.ERROR, "Unable to find Driver Class " + driver.getDriverClassName() + " for JDBC Driver" +  driver.getName());
                    }
                } else {
                    _logger.showMessage(Logger.ILogTypes.ERROR, "SqlDriverManager.MyDriverListener is listening to a non-ISQLDriver");
                }
            }
        }
    }
}
