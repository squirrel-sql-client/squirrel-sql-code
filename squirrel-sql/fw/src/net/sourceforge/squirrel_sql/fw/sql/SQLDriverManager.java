package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SQLDriverManager {
	private static ILogger s_log = LoggerController.createLogger(SQLDriverManager.class);
	private HashMap _driverInfo = new HashMap();

	private MyDriverListener _myDriverListener = new MyDriverListener();

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
		sqlDriver.setJDBCDriverClassLoaded(true);
	}

	public synchronized void unregisterSQLDriver(ISQLDriver sqlDriver) {
		sqlDriver.setJDBCDriverClassLoaded(false);
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
					Connection jdbcConn = driver.connect(alias.getUrl(), props);
					if (jdbcConn == null) {
						throw new BaseSQLException("Unable to create connection. Check your URL.");
					}
					return new SQLConnection(jdbcConn);
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
//			if (propName != null && propName.equals(ISQLDriver.IPropertyNames.DRIVER_CLASS)) {
			if (propName == null ||
					propName.equals(ISQLDriver.IPropertyNames.DRIVER_CLASS) ||
					propName.equals(ISQLDriver.IPropertyNames.JARFILE_NAME) ||
					propName.equals(ISQLDriver.IPropertyNames.USES_CLASSPATH)) {
				Object obj = evt.getSource();
				if (obj instanceof ISQLDriver) {
					ISQLDriver driver = (ISQLDriver)obj;
					SQLDriverManager.this.unregisterSQLDriver(driver);
					try {
						SQLDriverManager.this.registerSQLDriver(driver);
					} catch (IllegalAccessException ex) {
						s_log.error("Unable to create instance of Class " + driver.getDriverClassName() +
										" for JDCB driver " + driver.getName());
					} catch (InstantiationException ex) {
						s_log.error("Unable to create instance of Class " + driver.getDriverClassName() +
										" for JDCB driver " + driver.getName());
					} catch (ClassNotFoundException ex) {
						s_log.error("Unable to find Driver Class " + driver.getDriverClassName() +
										" for JDCB driver " + driver.getName());
					}
				} else {
					s_log.error("SqlDriverManager.MyDriverListener is listening to a non-ISQLDriver");
				}
			}
		}
	}
}
