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
 * This class represents the metadata for a database. It is essentially
 * a wrapper around <TT>java.sql.DatabaseMetaData</TT>.
 * 
 * <P>. From the JavaDoc for <TT>java.sql.DatabaseMetaData</TT>. &quot;Some
 * methods take arguments that are String patterns. These arguments all
 * have names such as fooPattern. Within a pattern String, "%" means match any
 * substring of 0 or more characters, and "_" means match any one character. Only
 * metadata entries matching the search pattern are returned. If a search pattern
 * argument is set to null, that argument's criterion will be dropped from the
 * search.&quot;
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
			value = new Integer(vers);
			_common.put(key, value);
		}
		return value.intValue();
	}

	/**
	 * Return the string used to quote characters in thuis DBMS.
	 * 
	 * @return	quote string.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public synchronized String getIdentifierQuoteString() throws SQLException
	{
		final String key = "getIdentifierQuoteString";
		String value = (String)_common.get(key);
		if (value == null)
		{
			final String driverName = getDriverName();
			if (driverName.equals(DriverNames.FREE_TDS)
				|| driverName.equals(DriverNames.JCONNECT))
			{
				value = "";
			}
			else
			{
				value = getJDBCMetaData().getIdentifierQuoteString();
			}
			_common.put(key, value);
		}
		return value;
	}

	/**
	 * Return a string array containing the names of all the schemas in the
	 * database.
	 * 
	 * @return	String[] of the names of the schemas in the database.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public String[] getSchemas() throws SQLException
	{
		DatabaseMetaData md = getJDBCMetaData();
		ArrayList list = new ArrayList();
		ResultSet rs = md.getSchemas();
		while (rs.next())
		{
			list.add(rs.getString(1));
		}
		return (String[])list.toArray(new String[list.size()]);
	}

	/**
	 * Retrieves whether this database supports schemas at all.
	 * 
	 * @return	<TT>true</TT> if database supports schemas.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public boolean supportsSchemas() throws SQLException
	{
		return supportsSchemasInDataManipulation()
			|| supportsSchemasInTableDefinitions();
	}

	/**
	 * Retrieves whether a schema name can be used in a data manipulation
	 * statement.
	 * 
	 * @return	<TT>true</TT> if a schema name can be used in a data
	 *			manipulation statement.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public boolean supportsSchemasInDataManipulation()
		throws SQLException
	{
		try
		{
			return getJDBCMetaData().supportsSchemasInDataManipulation();
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
	 * Retrieves whether a schema name can be used in a table definition
	 * statement.
	 * 
	 * @return	<TT>true</TT> if a schema name can be used in a table
	 *			definition statement.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public boolean supportsSchemasInTableDefinitions()
		throws SQLException
	{
		try
		{
			return getJDBCMetaData().supportsSchemasInTableDefinitions();
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
	 * Retrieves whether this DBMS supports stored procedures.
	 * 
	 * @return	<TT>true</TT> if DBMS supports stored procedures.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public boolean supportsStoredProcedures() throws SQLException
	{
		return getJDBCMetaData().supportsStoredProcedures();
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
	 * Return the <TT>DatabaseMetaData</TT> object for this connection.
	 * 
	 * @return	The <TT>DatabaseMetaData</TT> object for this connection.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	private DatabaseMetaData getJDBCMetaData() throws SQLException
	{
		return _conn.getConnection().getMetaData();
	}

	/**
	 * Retrive information about the storeed procedures in the system
	 * 
	 * @param	catalog		The name of the catalog to retrieve procedures
	 *						for. An empty string will return those without a
	 * 						catalog. <TT>null</TT> means that the catalog
	 * 						will not be used to narrow the search.
	 * @param	schemaPattern	The name of the schema to retrieve procedures
	 *						for. An empty string will return those without a
	 * 						schema. <TT>null</TT> means that the schema
	 * 						will not be used to narrow the search.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 * 
	 */
	public IProcedureInfo[] getProcedures(String catalog,
				String schemaPattern, String procedureNamePattern)
		throws SQLException
	{
		DatabaseMetaData md = getJDBCMetaData();
		ArrayList list = new ArrayList();
		ResultSet rs = md.getProcedures(catalog, schemaPattern, procedureNamePattern);
		while (rs.next())
		{
			list.add(new ProcedureInfo(rs.getString(1), rs.getString(2),
										rs.getString(3), rs.getString(7),
										rs.getInt(8), this));
		}
		return (IProcedureInfo[])list.toArray(new IProcedureInfo[list.size()]);
	}

}
