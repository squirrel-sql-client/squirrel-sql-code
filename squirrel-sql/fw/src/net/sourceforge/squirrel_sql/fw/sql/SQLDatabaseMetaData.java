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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sourceforge.squirrel_sql.fw.datasetviewer.LargeResultSetObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This class represents the metadata for a database. It is essentially
 * a wrapper around <TT>java.sql.DatabaseMetaData</TT>.
 * 
 * <P>From the JavaDoc for <TT>java.sql.DatabaseMetaData</TT>. &quot;Some
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

	/** Defines how to handel result sets. */
	private LargeResultSetObjectInfo _lrsi;

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

		_lrsi = new LargeResultSetObjectInfo();
		_lrsi.setReadBinary(true);
		_lrsi.setReadLongVarBinary(true);
		_lrsi.setReadVarBinary(true);
		_lrsi.setReadAllOther(true);
		_lrsi.setReadSQLOther(true);
	}

	/**
	 * Return the name of the current user.
	 * 
	 * @return	the current user name.
	 */
	public String getUserName() throws SQLException
	{
		return getJDBCMetaData().getUserName();
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
		//TODO: When min version supported is 1.4 then remove reflection.
		final String key = "getJDBCVersion";
		Integer value = (Integer)_common.get(key);
		if (value == null)
		{
			DatabaseMetaData md = getJDBCMetaData();
			try
			{
				final Method getMajorVersion = md.getClass().getMethod("getJDBCMajorVersion", null);
				final Method getMinorVersion = md.getClass().getMethod("getJDBCMinorVersion", null);
				final Integer major = (Integer)getMajorVersion.invoke(md, null);
				final Integer minor = (Integer)getMinorVersion.invoke(md, null);
				int vers = (major.intValue() * 100) + minor.intValue();
				value = new Integer(vers);
				_common.put(key, value);
			}
			catch (Throwable th)
			{
				throw new SQLException("Unsupported");
			}
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
		boolean hasGuest = false;
		final String dbProductName = getDatabaseProductName();
		final boolean isMSSQLorSYBASE = dbProductName.equals("Microsoft SQL Server") ||
											dbProductName.equals("Sybase SQL Server") ||
											dbProductName.equals("SQL Server"); // Old Sybase
		final ArrayList list = new ArrayList();
		final ResultSetReader rdr = new ResultSetReader(getJDBCMetaData().getSchemas(), _lrsi);
		Object[] row = null;
		while ((row = rdr.readRow()) != null)
		{
			if (isMSSQLorSYBASE && row[0].equals("guest"))
			{
				hasGuest = true;
			}
			list.add(row[0]);
		}

		// Some drivers for both MS SQL and Sybase don't return guest as
		// a schema name.
		if (isMSSQLorSYBASE && !hasGuest)
		{
			list.add("guest");
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
		final ArrayList list = new ArrayList();
		final ResultSetReader rdr = new ResultSetReader(getJDBCMetaData().getCatalogs(), _lrsi);
		Object[] row = null;
		while ((row = rdr.readRow()) != null)
		{
			list.add(row[0]);
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
	public DatabaseMetaData getJDBCMetaData() throws SQLException
	{
		return _conn.getConnection().getMetaData();
	}

	/**
	 * Retrieve information about the stored procedures in the system
	 * 
	 * @param	catalog		The name of the catalog to retrieve procedures
	 *						for. An empty string will return those without a
	 * 						catalog. <TT>null</TT> means that the catalog
	 * 						will not be used to narrow the search.
	 * @param	schemaPattern	The name of the schema to retrieve procedures
	 *						for. An empty string will return those without a
	 * 						schema. <TT>null</TT> means that the schema
	 * 						will not be used to narrow the search.
	 * @param	procedureNamepattern	A procedure name pattern; must match the
	 *									procedure name as it is stored in the
	 *									database.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public IProcedureInfo[] getProcedures(String catalog,
				String schemaPattern, String procedureNamePattern)
		throws SQLException
	{
		DatabaseMetaData md = getJDBCMetaData();
		ArrayList list = new ArrayList();
		ResultSet rs = md.getProcedures(catalog, schemaPattern, procedureNamePattern);
		final int[] cols = new int[] {1, 2, 3, 7, 8};
		final ResultSetReader rdr = new ResultSetReader(rs, cols);
		Object[] row = null;
		while ((row = rdr.readRow()) != null)
		{
			final int type = ((Number)row[4]).intValue();
			list.add(new ProcedureInfo((String)row[0], (String)row[1],
									(String)row[2], (String)row[3], type, this));
		}
		return (IProcedureInfo[])list.toArray(new IProcedureInfo[list.size()]);
	}

	/**
	 * Return a string array containing the different types of tables in this
	 * database. E.G. <TT>"TABLE", "VIEW", "SYSTEM TABLE"</TT>.
	 * 
	 * @return	table type names.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public String[] getTableTypes() throws SQLException
	{
		final DatabaseMetaData md = getJDBCMetaData();

		// Use a set rather than a list as some combinations of MS SQL and the
		// JDBC/ODBC return multiple copies of each table type.
		final Set tableTypes = new TreeSet();
		final ResultSet rs = md.getTableTypes();
		if (rs != null)
		{
			while (rs.next())
			{
				tableTypes.add(rs.getString(1).trim());
			}
		}

		final String dbProductName = getDatabaseProductName();
		final int nbrTableTypes = tableTypes.size();

		// InstantDB (at least version 3.13) only returns "TABLES"
		// for getTableTypes(). If you try to use this in a call to
		// DatabaseMetaData.getTables() no tables will be found. For the
		// moment hard code the types for InstantDB.
		if (nbrTableTypes == 1 && dbProductName.equals("InstantDB"))
		{
			tableTypes.clear();
			tableTypes.add("TABLE");
			tableTypes.add("SYSTEM TABLE");
		}

		// At least one version of PostgreSQL through the ODBC/JDBC
		// bridge returns an empty result set for the list of table
		// types. Another version of PostgreSQL returns 6 entries
		// of "SYSTEM TABLE" (which we have already filtered back to one).
		else if (dbProductName.equals("PostgreSQL"))
		{
			if (nbrTableTypes == 0 || nbrTableTypes == 1)
			{
				tableTypes.clear();
				tableTypes.add("TABLE");
				tableTypes.add("SYSTEM TABLE");
				tableTypes.add("VIEW");
				tableTypes.add("INDEX");
				tableTypes.add("SYSTEM INDEX");
				tableTypes.add("SEQUENCE");
			}
		}

		return (String[]) tableTypes.toArray(new String[tableTypes.size()]);
	}

	/**
	 * Retrieve information about the tables in the system.
	 * 
	 * @param	catalog		The name of the catalog to retrieve tables
	 *						for. An empty string will return those without a
	 * 						catalog. <TT>null</TT> means that the catalog
	 * 						will not be used to narrow the search.
	 * @param	schemaPattern	The name of the schema to retrieve tables
	 *						for. An empty string will return those without a
	 * 						schema. <TT>null</TT> means that the schema
	 * 						will not be used to narrow the search.
	 * @param	tableNamepattern	A table name pattern; must match the
	 *								table name as it is stored in the
	 *								database.
	 * @param	types		List of table types to include; null returns all types.
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public ITableInfo[] getTables(String catalog, String schemaPattern,
									String tableNamePattern, String[] types)
		throws SQLException
	{
		final DatabaseMetaData md = getJDBCMetaData();
		final String dbDriverName = getDriverName();
		Set list = new TreeSet();

		if (dbDriverName.equals(DriverNames.FREE_TDS) && schemaPattern == null)
		{
			schemaPattern = "dbo";
		}

		ResultSet tabResult = md.getTables(catalog, schemaPattern,
											tableNamePattern, types);
		ResultSet superTabResult = null;
		Map nameMap = null;
		try
		{
			//TODO: remove reflection once we only support JDK1.4
			//superTabResult = md.getSuperTables(catalog, schemaPattern,
			//									   tableNamePattern);
			Class clazz = md.getClass();
			Class[] p1 = new Class[] {String.class, String.class, String.class};
			Method method = clazz.getMethod("getSuperTables", p1);
			if (method != null)
			{
				Object[] p2 = new Object[] {catalog, schemaPattern, tableNamePattern};
				try
				{
					superTabResult = (ResultSet)method.invoke(md, p2);
				}
				catch (InvocationTargetException ignore)
				{
					// unsupported by this driver.
				}
			}
			// create a mapping of names if we have supertable info, since
			// we need to find the ITableInfo again for re-ordering.
			if (superTabResult != null && superTabResult.next())
			{
				nameMap = new HashMap();
			}
		}
		catch (Throwable th)
		{
			s_log.debug("DBMS/Driver doesn't support getSupertables()", th);
		}

		// store all plain table info we have.
		while (tabResult.next())
		{
			ITableInfo tabInfo = new TableInfo(tabResult.getString(1),
								tabResult.getString(2), tabResult.getString(3),
								tabResult.getString(4), tabResult.getString(5),
								this);
			if (nameMap != null)
			{
				nameMap.put(tabInfo.getSimpleName(), tabInfo);
			}
			list.add(tabInfo);
		}

		// re-order nodes if the tables are stored hierachically
		if (nameMap != null)
		{
			do
			{
				String tabName = superTabResult.getString(3);
				TableInfo tabInfo = (TableInfo) nameMap.get(tabName);
				if (tabInfo == null)
					continue;
				String superTabName = superTabResult.getString(4);
				if (superTabName == null)
					continue;
				TableInfo superInfo = (TableInfo) nameMap.get(superTabName);
				if (superInfo == null)
					continue;
				superInfo.addChild(tabInfo);
				list.remove(tabInfo); // remove from toplevel.
			}
			while (superTabResult.next());
		}
		return (ITableInfo[])list.toArray(new ITableInfo[list.size()]);
	}

	/**
	 * Retrieve information about the UDTs in the system.
	 * 
	 * @param	catalog		The name of the catalog to retrieve UDTs
	 *						for. An empty string will return those without a
	 * 						catalog. <TT>null</TT> means that the catalog
	 * 						will not be used to narrow the search.
	 * @param	schemaPattern	The name of the schema to retrieve UDTs
	 *						for. An empty string will return those without a
	 * 						schema. <TT>null</TT> means that the schema
	 * 						will not be used to narrow the search.
	 * @param	typeNamepattern		A type name pattern; must match the
	 *								type name as it is stored in the
	 *								database.
	 * @param	types		List of user-defined types (JAVA_OBJECT, STRUCT, or
	 *						DISTINCT) to include; null returns all types
	 * 
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public IUDTInfo[] getUDTs(String catalog, String schemaPattern,
								String typeNamePattern, int[] types)
		throws SQLException
	{
		DatabaseMetaData md = getJDBCMetaData();
		ArrayList list = new ArrayList();
		ResultSet rs = md.getUDTs(catalog, schemaPattern, typeNamePattern, types);
		final int[] cols = new int[] {1, 2, 3, 4, 5, 6};
		final ResultSetReader rdr = new ResultSetReader(rs, cols);
		Object[] row = null;
		while ((row = rdr.readRow()) != null)
		{
			list.add(new UDTInfo((String)row[0], (String)row[1], (String)row[2],
								(String)row[3], (String)row[4], (String)row[5],
								this));
		}
		return (IUDTInfo[])list.toArray(new IUDTInfo[list.size()]);
	}

	/**
	 * Retrieve the names of the Numeric Functions that this DBMS supports.
	 * 
	 * @return	String[] of function names.
	 */
	public String[] getNumericFunctions() throws SQLException
	{
		return makeArray(getJDBCMetaData().getNumericFunctions());
	}

	/**
	 * Retrieve the names of the String Functions that this DBMS supports.
	 * 
	 * @return	String[] of function names.
	 */
	public String[] getStringFunctions() throws SQLException
	{
		return makeArray(getJDBCMetaData().getStringFunctions());
	}

	/**
	 * Retrieve the names of the System Functions that this DBMS supports.
	 * 
	 * @return	String[] of function names.
	 */
	public String[] getSystemFunctions() throws SQLException
	{
		return makeArray(getJDBCMetaData().getSystemFunctions());
	}

	/**
	 * Retrieve the names of the Date/Time Functions that this DBMS supports.
	 * 
	 * @return	String[] of function names.
	 */
	public String[] getTimeDateFunctions() throws SQLException
	{
		return makeArray(getJDBCMetaData().getTimeDateFunctions());
	}

	/**
	 * Retrieve the names of the non-standard keywords that this DBMS supports.
	 * 
	 * @return	String[] of keywords.
	 */
	public String[] getSQLKeywords() throws SQLException
	{
		return makeArray(getJDBCMetaData().getSQLKeywords());
	}

	// TODO: Write a version that returns an array of RowIdentifier objects.
	public ResultSet getBestRowIdentifier(ITableInfo ti)
		throws SQLException
	{
		return getJDBCMetaData().getBestRowIdentifier(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName(), DatabaseMetaData.bestRowTransaction,
			true);
	}

	// TODO: Write a version that returns an array of ColumnPrivilige objects.
	public ResultSet getColumnPrivileges(ITableInfo ti)
		throws SQLException
	{
		// MM-MYSQL driver doesnt support null for column name.
		final String dbProdName = getDatabaseProductName();
		final String columns = dbProdName.equalsIgnoreCase("mysql") ? "%" : null;
		final String dbProductName = getDatabaseProductName();
		return getJDBCMetaData().getColumnPrivileges(ti.getCatalogName(),
													ti.getSchemaName(),
													ti.getSimpleName(),
													columns);
	}

	// TODO: Write a version that returns an array of data objects.
	public ResultSet getExportedKeys(ITableInfo ti)
		throws SQLException
	{
		return getJDBCMetaData().getExportedKeys(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName());
	}

	public ResultSet getImportedKeys(ITableInfo ti)
		throws SQLException
	{
		return getJDBCMetaData().getImportedKeys(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName());
	}

	public ForeignKeyInfo[] getImportedKeysInfo(ITableInfo ti)
		throws SQLException
	{
		return getForeignKeyInfo(getJDBCMetaData().getImportedKeys(ti.getCatalogName(),
								ti.getSchemaName(), ti.getSimpleName()));
	}

	public ForeignKeyInfo[] getExportedKeysInfo(ITableInfo ti)
		throws SQLException
	{
		return getForeignKeyInfo(getJDBCMetaData().getExportedKeys(ti.getCatalogName(),
								ti.getSchemaName(), ti.getSimpleName()));
	}

	public ForeignKeyInfo[] getForeignKeyInfo(ResultSet rs)
		throws SQLException
	{
		final ResultSetColumnReader rdr = new ResultSetColumnReader(rs);
		final Map keys = new HashMap();
		final Map columns = new HashMap();
		while (rdr.next())
		{
			final ForeignKeyInfo fki = new ForeignKeyInfo(rdr.getString(1),
						rdr.getString(2), rdr.getString(3), rdr.getString(5),
						rdr.getString(6), rdr.getString(7),
						rdr.getLong(10).intValue(), rdr.getLong(11).intValue(),
						rdr.getString(12), rdr.getString(13),
						rdr.getLong(14).intValue(), null, this);
			final String key = createForeignKeyInfoKey(fki);
			if (!keys.containsKey(key))
			{
				keys.put(key, fki);
				columns.put(key, new ArrayList());
			}

			ForeignKeyColumnInfo fkiCol = new ForeignKeyColumnInfo(rdr.getString(8),
													rdr.getString(8),
													rdr.getLong(9).intValue());
			((List)columns.get(key)).add(fkiCol);
		}

		final ForeignKeyInfo[] results = new ForeignKeyInfo[keys.size()];
		Iterator it = keys.values().iterator();
		int idx = 0;
		while (it.hasNext())
		{
			final ForeignKeyInfo fki = (ForeignKeyInfo)it.next();
			final String key = createForeignKeyInfoKey(fki);
			final List colsList = (List)columns.get(key);
			final ForeignKeyColumnInfo[] fkiCol = (ForeignKeyColumnInfo[])colsList.toArray(new ForeignKeyColumnInfo[colsList.size()]);
			fki.setForeignKeyColumnInfo(fkiCol);
			results[idx++] = fki;
		}
		
		return results;
	}
	
	private String createForeignKeyInfoKey(ForeignKeyInfo fki)
	{
		StringBuffer buf = new StringBuffer();
		buf.append(fki.getForeignKeyCatalogName())
			.append(fki.getForeignKeySchemaName())
			.append(fki.getForeignKeyTableName())
			.append(fki.getForeignKeyName())
			.append(fki.getPrimaryKeyCatalogName())
			.append(fki.getPrimaryKeySchemaName())
			.append(fki.getPrimaryKeyTableName())
			.append(fki.getPrimaryKeyName());
		return buf.toString();
	}

	// TODO: Write a version that returns an array of data objects.
	public ResultSet getIndexInfo(ITableInfo ti)
		throws SQLException
	{
		return getJDBCMetaData().getIndexInfo(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName(), false, true);
	}

	// TODO: Write a version that returns an array of data objects.
	public ResultSet getPrimaryKeys(ITableInfo ti)
		throws SQLException
	{
		return getJDBCMetaData().getPrimaryKeys(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName());
	}

	// TODO: Write a version that returns an array of data objects.
	public ResultSet getProcedureColumns(IProcedureInfo ti)
		throws SQLException
	{
		return getJDBCMetaData().getProcedureColumns(ti.getCatalogName(),
													ti.getSchemaName(),
													ti.getSimpleName(),
													"%");
	}

	// TODO: Write a version that returns an array of data objects.
	public ResultSet getTablePrivileges(ITableInfo ti)
		throws SQLException
	{
		return getJDBCMetaData().getTablePrivileges(ti.getCatalogName(),
													ti.getSchemaName(),
													ti.getSimpleName());
	}


	// TODO: Write a version that returns an array of data objects.
	public ResultSet getTypeInfo() throws SQLException
	{
		return getJDBCMetaData().getTypeInfo();
	}

	// TODO: Write a version that returns an array of data objects.
	public ResultSet getVersionColumns(ITableInfo ti)
		throws SQLException
	{
		return getJDBCMetaData().getVersionColumns(ti.getCatalogName(),
												ti.getSchemaName(),
												ti.getSimpleName());
	}

	public ResultSet getColumns(ITableInfo ti)
		throws SQLException
	{
		return getJDBCMetaData().getColumns(ti.getCatalogName(),
											ti.getSchemaName(),
											ti.getSimpleName(), "%");
	}

	public TableColumnInfo[] getColumnInfo(ITableInfo ti)
		throws SQLException
	{
		final ResultSetColumnReader rdr = new ResultSetColumnReader(getColumns(ti));
		final Map columns = new TreeMap();
		while (rdr.next())
		{
			final TableColumnInfo tci = new TableColumnInfo(rdr.getString(1),
						rdr.getString(2), rdr.getString(3), rdr.getString(4),
						rdr.getLong(5).intValue(), rdr.getString(6),
						rdr.getLong(7).intValue(), rdr.getLong(9).intValue(),
						rdr.getLong(10).intValue(), rdr.getLong(11).intValue(),
						rdr.getString(12), rdr.getString(13),
						rdr.getLong(16).intValue(), rdr.getLong(17).intValue(),
						rdr.getString(18), this);
			columns.put(new Integer(tci.getOrdinalPosition()), tci);
		}

		return (TableColumnInfo[])columns.values().toArray(
									new TableColumnInfo[columns.size()]);
	}

	private static String[] makeArray(String data)
	{
		List list = new ArrayList();
		StringTokenizer st = new StringTokenizer(data, ",");
		while (st.hasMoreTokens())
		{
			list.add(st.nextToken());
		}
		Collections.sort(list);

		return (String[])list.toArray(new String[list.size()]);
	}

}
