package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SQLDriverManager
{
	private static final ILogger s_log =
		LoggerController.createLogger(SQLDriverManager.class);

	/**
	 * Collection of instances of <TT>java.sql.Driver</TT> objects keyed
	 * by the <TT>SQLDriver.getIdentifier()</TT>.
	 */
	private Map _driverInfo = new HashMap();

	/**
	 * Collection of the <TT>SQLDriverClassLoader</TT> class loaders used for
	 * each driver. keyed by <TT>SQLDriver.getIdentifier()</TT>.
	 */
	private Map _classLoaders = new HashMap();

	private MyDriverListener _myDriverListener = new MyDriverListener();

	public synchronized void registerSQLDriver(ISQLDriver sqlDriver)
		throws IllegalAccessException, InstantiationException,
					ClassNotFoundException, MalformedURLException
	{
		unregisterSQLDriver(sqlDriver);
		sqlDriver.addPropertyChangeListener(_myDriverListener);
		ClassLoader loader = new SQLDriverClassLoader(sqlDriver);
		Class driverClass = loader.loadClass(sqlDriver.getDriverClassName());
		_driverInfo.put(sqlDriver.getIdentifier(), driverClass.newInstance());
		_classLoaders.put(sqlDriver.getIdentifier(), loader);
		sqlDriver.setJDBCDriverClassLoaded(true);
	}

	public synchronized void unregisterSQLDriver(ISQLDriver sqlDriver)
	{
		sqlDriver.setJDBCDriverClassLoaded(false);
		sqlDriver.removePropertyChangeListener(_myDriverListener);
		_driverInfo.remove(sqlDriver.getIdentifier());
		_classLoaders.remove(sqlDriver.getIdentifier());
	}

	public synchronized SQLConnection getConnection(ISQLDriver sqlDriver,
											ISQLAlias alias, String user,
											String pw)
		throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, MalformedURLException, SQLException
	{
		Properties props = new Properties();
		if (user != null)
		{
			props.put("user", user);
		}
		if (pw != null)
		{
			props.put("password", pw);
		}

		Driver driver = (Driver)_driverInfo.get(sqlDriver.getIdentifier());
		if (driver == null)
		{
			s_log.debug("Loading driver that wasn't registered: " +
							sqlDriver.getDriverClassName());
			ClassLoader loader = new SQLDriverClassLoader(sqlDriver);
			Class driverCls = loader.loadClass(sqlDriver.getDriverClassName());
			driver = (Driver)driverCls.newInstance();
		}
		Connection jdbcConn = driver.connect(alias.getUrl(), props);
		if (jdbcConn == null)
		{
			throw new SQLException("Unable to create connection. Check your URL.");
		}
		return new SQLConnection(jdbcConn);
	}

	/**
	 * Return the <TT>java.sql.Driver</TT> being used for the passed
	 * <TT>ISQLDriver.getIdentifier()</TT> or <TT>null</TT> if none found.
	 * 
	 * @return	the <TT>java.sql.Driver</TT> being used for the passed
	 * 			<TT>ISQLDriver.getIdentifier()</TT> or <TT>null if none found.
	 * 
	 * @throws	IllegalArgumentException
	 *			Thrown if <TT>null</TT> IIdentifier</TT> passed.
	 */
	public Driver getJDBCDriver(IIdentifier id)
	{
		if (id == null)
		{
			throw new IllegalArgumentException("IIdentifier == null");
		}

		return (Driver)_driverInfo.get(id);
	}

	/**
	 * Return the <TT>SQLDriverClassLoader</TT> used for the passed driver.
	 * 
	 * @param	sqlDriver	Driver to find class loader for.
	 * 
	 * @throws	IllegalArgumentException
	 *			Thrown if <TT>null</TT> <TT>SQLDriverClassLoader</TT> passed.
	 * 
	 * @return	ClassLoader or null.
	 */
	public SQLDriverClassLoader getSQLDriverClassLoader(ISQLDriver driver)
	{
		if (driver == null)
		{
			throw new IllegalArgumentException("SQLDriverClassLoader == null");
		}

		return (SQLDriverClassLoader)_classLoaders.get(driver.getIdentifier());
	}

	private final class MyDriverListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			final String propName = evt.getPropertyName();
			if (propName == null
				|| propName.equals(ISQLDriver.IPropertyNames.DRIVER_CLASS)
				|| propName.equals(ISQLDriver.IPropertyNames.JARFILE_NAMES))
			{
				Object obj = evt.getSource();
				if (obj instanceof ISQLDriver)
				{
					ISQLDriver driver = (ISQLDriver) obj;
					SQLDriverManager.this.unregisterSQLDriver(driver);
					try
					{
						SQLDriverManager.this.registerSQLDriver(driver);
					}
					catch (IllegalAccessException ex)
					{
						s_log.error("Unable to create instance of Class "
										+ driver.getDriverClassName()
										+ " for JDCB driver "
										+ driver.getName(), ex);
					}
					catch (InstantiationException ex)
					{
						s_log.error("Unable to create instance of Class "
								+ driver.getDriverClassName()
								+ " for JDCB driver "
								+ driver.getName(), ex);
					}
					catch (MalformedURLException ex)
					{
						s_log.error("Unable to create instance of Class "
								+ driver.getDriverClassName()
								+ " for JDCB driver "
								+ driver.getName(), ex);
					}
					catch (ClassNotFoundException ex)
					{
						s_log.error("Unable to find Driver Class "
								+ driver.getDriverClassName()
								+ " for JDCB driver "
								+ driver.getName());
					}
				}
				else
				{
					s_log.error(
						"SqlDriverManager.MyDriverListener is listening to a non-ISQLDriver");
				}
			}
		}
	}
}