package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This class represents the metadata for a database.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLDatabaseMetaData
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(SQLDatabaseMetaData.class);

	private interface DriverNames
	{
		String FREE_TDS = "InternetCDS Type 4 JDBC driver for MS SQLServer";
		String JCONNECT = "jConnect (TM) for JDBC (TM)";
	}

	/** Connection to database this class is supplying information for. */
	private SQLConnection _conn;

	/** Collection of commonly accessed metadata properties. */
	private Map _common = new HashMap();

	/**
	 * ctor specifying the connection that we are retrieving metadata for.
	 * 
	 * @param	conn	Connection to database.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if null SQLConnection passed.
	 */
	SQLDatabaseMetaData(SQLConnection conn)
	{
		super();
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLDatabaseMetaData == null");
		}
		_conn = conn;
	}

	/**
	 * Return the database product name for this connection.
	 * 
	 * @return	the database product name for this connection.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public synchronized String getDatabaseProductName()
		throws SQLException
	{
		final String key = "getDatabaseProductName";
		String value = (String)_common.get(key);
		if (value == null)
		{
			value = getJDBCMetaData().getDatabaseProductName();
			_common.put(key, value);
		}
		return value;
	}

	/**
	 * Return the database product version for this connection.
	 * 
	 * @return	database product version
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public synchronized String getDatabaseProductVersion()
		throws SQLException
	{
		final String key = "getDatabaseProductVersion";
		String value = (String)_common.get(key);
		if (value == null)
		{
			value = getJDBCMetaData().getDatabaseProductVersion();
			_common.put(key, value);
		}
		return value;
	}

	/**
	 * Return the JDBC driver name for this connection.
	 * 
	 * @return	the JDBC driver name for this connection.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public synchronized String getDriverName() throws SQLException
	{
		final String key = "getDriverName";
		String value = (String)_common.get(key);
		if (value == null)
		{
			value = getJDBCMetaData().getDriverName();
			_common.put(key, value);
		}
		return value;
	}

	/**
	 * Return the JDBC version of this driver.
	 * 
	 * @return	the JDBC version of the driver.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public synchronized int getJDBCVersion() throws SQLException
	{
		final String key = "getJDBCVersion";
		Integer value = (Integer)_common.get(key);
		if (value == null)
		{
			DatabaseMetaData md = getJDBCMetaData();
			int vers = (md.getJDBCMajorVersion() * 100) + md.getJDBCMinorVersion();
			_common.put(key, new Integer(vers));
		}
		return value.intValue();
	}
	/**
	 * Return a string array containing the names of all the catalogs in the
	 * database.
	 * 
	 * @return	String[] of the names of the catalogs in the database.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public String[] getCatalogs() throws SQLException
	{
		DatabaseMetaData md = getJDBCMetaData();
		ArrayList list = new ArrayList();
		ResultSet rs = md.getCatalogs();
		while (rs.next())
		{
			list.add(rs.getString(1));
		}
		return (String[])list.toArray(new String[list.size()]);
	}

	/**
	 * Retrieves the String that this database uses as the separator between a
	 * catalog and table name.
	 * 
	 * @return	The separator character.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public String getCatalogSeparator() throws SQLException
	{
		return getJDBCMetaData().getCatalogSeparator();
	}

	/**
	 * Retrieves whether this database supports catalogs at all.
	 * 
	 * @return	<TT>true</TT> fi database supports catalogs.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public boolean supportsCatalogs() throws SQLException
	{
		return supportsCatalogsInTableDefinitions()
			|| supportsCatalogsInDataManipulation()
			|| supportsCatalogsInProcedureCalls();
	}

	/**
	 * Retrieves whether a catalog name can be used in a table definition
	 * statement.
	 * 
	 * @return	<TT>true</TT> if a catalog name can be used in a table
	 *			definition statement.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public boolean supportsCatalogsInTableDefinitions() throws SQLException
	{
		try
		{
			return getJDBCMetaData().supportsCatalogsInTableDefinitions();
		}
		catch (SQLException ex)
		{
			if (getDriverName().equals(DriverNames.FREE_TDS))
			{
				return true;
			}
			throw ex;
		}
	}

	/**
	 * Retrieves whether a catalog name can be used in a data manipulation
	 * statement.
	 * 
	 * @return	<TT>true</TT> if a catalog name can be used in a data
	 *			manipulation statement.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public boolean supportsCatalogsInDataManipulation() throws SQLException
	{
		try
		{
			return getJDBCMetaData().supportsCatalogsInDataManipulation();
		}
		catch (SQLException ex)
		{
			if (getDriverName().equals(DriverNames.FREE_TDS))
			{
				return true;
			}
			throw ex;
		}
	}

	/**
	 * Retrieves whether a catalog name can be used in a procedure call.
	 * 
	 * @return	<TT>true</TT> if a catalog name can be used in a procedure
	 *			call.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public boolean supportsCatalogsInProcedureCalls() throws SQLException
	{
		try
		{
			return getJDBCMetaData().supportsCatalogsInProcedureCalls();
		}
		catch (SQLException ex)
		{
			if (getDriverName().equals(DriverNames.FREE_TDS))
			{
				return true;
			}
			throw ex;
		}
	}

	/**
	 * Return the <TT>DatabaseMetaData</TT> object for this ocnnection.
	 * 
	 * @return	The <TT>DatabaseMetaData</TT> object for this connection.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	private DatabaseMetaData getJDBCMetaData() throws SQLException
	{
		return _conn.getConnection().getMetaData();
	}
}
