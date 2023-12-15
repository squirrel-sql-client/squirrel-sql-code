package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001-2003 Colin Bell
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

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.session.action.reconnect.ReconnectInfo;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;

/**
 * This class replaces the standard Java class <TT>java.ql.DriverManager</TT>.
 * The main reason for replacing it is that <TT>java.ql.DriverManager</TT>
 * won't handle JDBC driver classes that were loaded by a custom classloader.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLDriverManager
{
	private static final ILogger s_log = LoggerController.createLogger(SQLDriverManager.class);

	/**
	 * Collection of instances of <TT>java.sql.Driver</TT> objects keyed
	 * by the <TT>SQLDriver.getIdentifier()</TT>.
	 */
	private Map<IIdentifier, Driver> _driverInfo = new HashMap<>();

	/**
	 * Collection of the <TT>SQLDriverClassLoader</TT> class loaders used for
	 * each driver. keyed by <TT>SQLDriver.getIdentifier()</TT>.
	 */
	private Map<IIdentifier, SQLDriverClassLoader> _classLoaders = new HashMap<>();

	private MyDriverListener _myDriverListener = new MyDriverListener();

	public synchronized void registerSQLDriver(ISQLDriver sqlDriver) throws ClassNotFoundException
	{
		try
		{
			unregisterSQLDriver(sqlDriver);
			sqlDriver.addPropertyChangeListener(_myDriverListener);
			SQLDriverClassLoader loader = new SQLDriverClassLoader(sqlDriver);

			Driver driver = (Driver) (Class.forName(sqlDriver.getDriverClassName(), false, loader).getDeclaredConstructor().newInstance());

			_driverInfo.put(sqlDriver.getIdentifier(), driver);
			_classLoaders.put(sqlDriver.getIdentifier(), loader);
			sqlDriver.setJDBCDriverClassLoaded(true);
		}
		catch (ClassNotFoundException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw Utilities.wrapRuntime(e);
		}
	}

	public synchronized void unregisterSQLDriver(ISQLDriver sqlDriver)
	{
		sqlDriver.setJDBCDriverClassLoaded(false);
		sqlDriver.removePropertyChangeListener(_myDriverListener);
		_driverInfo.remove(sqlDriver.getIdentifier());
		_classLoaders.remove(sqlDriver.getIdentifier());
	}

	public ISQLConnection getConnection(ISQLDriver sqlDriver, SQLAlias alias, String user, String pw)
	{
		return getConnection(sqlDriver, alias, user, pw, null);
	}

	public SQLConnection getConnection(ISQLDriver sqlDriver, SQLAlias alias, String user, String pw, SQLDriverPropertyCollection props)
	{
		return getConnection(sqlDriver, alias, user, pw, props, null);
	}


	public SQLConnection getConnection(ISQLDriver sqlDriver, SQLAlias alias, String user, String pw, SQLDriverPropertyCollection props, ReconnectInfo reconnectInfo)
	{
		Driver driver;
		synchronized (this)
		{
			driver = _driverInfo.get(sqlDriver.getIdentifier());
		}

		return SQLConnector.getSqlConnection(sqlDriver, alias, user, pw, props, reconnectInfo, driver);
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

		return _driverInfo.get(id);
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

		return _classLoaders.get(driver.getIdentifier());
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
					catch (ClassNotFoundException ex)
					{
                        String[] jars = driver.getJarFileNames();
                        String jarFileList = "<empty list>"; 
                        if (jars != null) {
                            jarFileList = 
                                "[ " + StringUtilities.join(jars, ", ") + " ]";
                        }
                       
						s_log.error("Unable to find Driver Class "
								+ driver.getDriverClassName()
								+ " for JDBC driver "
								+ driver.getName()
                                + "; jar filenames = "+jarFileList);
					}
					catch (Exception ex)
					{
						s_log.error("Unable to create instance of Class "
												+ driver.getDriverClassName()
												+ " for JDBC driver "
												+ driver.getName(), ex);
					}

				}
				else
				{
					s_log.error("SqlDriverManager.MyDriverListener is listening to a non-ISQLDriver");
				}
			}
		}
	}
}
