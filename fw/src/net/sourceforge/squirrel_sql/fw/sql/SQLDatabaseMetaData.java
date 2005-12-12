package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2002-2003 Colin Bell
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

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DatabaseTypesDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.sql.dbobj.BestRowIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This class represents the metadata for a database. It is essentially
 * a wrapper around <TT>java.sql.DatabaseMetaData</TT>.
 *
 * <P>Some data can be cached on the first retrieval in order to speed up
 * subsequent retrievals. To clear this cache call <TT>clearCache()</TT>.
 *
 * <P>From the JavaDoc for <TT>java.sql.DatabaseMetaData</TT>. &quot;Some
 * methods take arguments that are String patterns. These arguments all
 * have names such as fooPattern. Within a pattern String, "%" means match any
 * substring of 0 or more characters, and "_" means match any one character. Only
 * metadata entries matching the search pattern are returned. If a search pattern
 * argument is set to null, that argument's criterion will be dropped from the
 * search.&quot;
 *
 * <P>Additionally, it should be noted that some JDBC drivers (like Oracle) do
 * not handle multi-threaded access to methods that return ResultSets very well.
 * It is therefore highly recommended that methods in this class that return 
 * a ResultSet, should not be called outside of this class where this class' 
 * monitor has no jurisdiction.  Furthermore, methods that are meant to be 
 * called externally that create a ResultSet should package the data in some 
 * container object structure for use by the caller, and should always be 
 * synchronized on this class' monitor. 
 * 
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLDatabaseMetaData
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SQLDatabaseMetaData.class);

	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(SQLDatabaseMetaData.class);

	/**
	 * Full or partial names of various JDBC driivers that can be matched
	 * to <tt>getDriverName()</tt>.
	 */
	private interface IDriverNames
	{
		String FREE_TDS = "InternetCDS Type 4 JDBC driver for MS SQLServer";
		String JCONNECT = "jConnect (TM) for JDBC (TM)";
		String OPTA2000 = "i-net OPTA 2000";
		String JTDS = "jTDS Type 4 JDBC Driver for MS SQL Server and Sybase";
	}

	/**
	 * Full or partial names of various DBMS poducts that can be matched
	 * to <tt>getDatabaseProductName()</tt>.
	 */
	private interface IDBMSProductNames
	{
		String DB2 = "DB2";
		String MYSQL = "mysql";
		String MICROSOFT_SQL = "Microsoft SQL Server";
		String POSTGRESQL = "PostgreSQL";
		String SYBASE = "Sybase SQL Server";
		String SYBASE_OLD = "SQL Server";
	}

	/** Connection to database this class is supplying information for. */
	private SQLConnection _conn;

	/**
	 * Cache of commonly accessed metadata properties keyed by the method
	 * name that attempts to retrieve them.
	 */
	private Map _cache = Collections.synchronizedMap(new HashMap());

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
     * Return the name of the current user. Cached on first call.
     *
     * @return  the current user name.
     */
    public synchronized String getUserName() throws SQLException
	{
		final String key = "getUserName";
		String value = (String)_cache.get(key);
		if (value == null)
		{
			value = privateGetJDBCMetaData().getUserName();
			_cache.put(key, value);
		}
		return value;
	}

    /**
     * Return the database product name for this connection. Cached on first
     * call.
     *
     * @return  the database product name for this connection.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
    public synchronized String getDatabaseProductName()
		throws SQLException
	{
		final String key = "getDatabaseProductName";
		String value = (String)_cache.get(key);
		if (value == null)
		{
			value = privateGetJDBCMetaData().getDatabaseProductName();
            _cache.put(key, value);
		}
		return value;
	}

    /**
     * Return the database product version for this connection. Cached on first
     * call.
     *
     * @return  database product version
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
	public synchronized String getDatabaseProductVersion()
		throws SQLException
	{
		final String key = "getDatabaseProductVersion";
		String value = (String)_cache.get(key);
		if (value == null)
		{
			value = privateGetJDBCMetaData().getDatabaseProductVersion();
			_cache.put(key, value);
		}
		return value;
	}

    /**
     * Return the JDBC driver name for this connection. Cached on first call.
     *
     * @return  the JDBC driver name for this connection.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
	public synchronized String getDriverName() throws SQLException
	{
		final String key = "getDriverName";
		String value = (String)_cache.get(key);
		if (value == null)
		{
			value = privateGetJDBCMetaData().getDriverName();
			_cache.put(key, value);
		}
		return value;
	}

    /**
     * Return the JDBC version of this driver. Cached on first call.
     *
     * @return  the JDBC version of the driver.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
    public int getJDBCVersion() throws SQLException
	{
		final String key = "getJDBCVersion";
		Integer value = (Integer)_cache.get(key);
		if (value == null)
		{
			DatabaseMetaData md = privateGetJDBCMetaData();
            int major = md.getJDBCMajorVersion();
            int minor = md.getJDBCMinorVersion();
            int vers = (major * 100) + minor;
            value = new Integer(vers);
            _cache.put(key, value);
		}
		return value.intValue();
	}

    /**
     * Return the string used to quote characters in this DBMS. Cached on first
     * call.
     *
     * @return  quote string.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
	public synchronized String getIdentifierQuoteString() throws SQLException
	{
		final String key = "getIdentifierQuoteString";
		String value = (String)_cache.get(key);
		if (value == null)
		{
			final String driverName = getDriverName();
			if (driverName.equals(IDriverNames.FREE_TDS)
				|| driverName.equals(IDriverNames.JCONNECT)
				|| driverName.equals(IDriverNames.JTDS))
			{
				value = "";
			}
			else
			{
				value = privateGetJDBCMetaData().getIdentifierQuoteString();
			}
			_cache.put(key, value);
		}
		return value;
	}

    /**
     * Return a string array containing the names of all the schemas in the
     * database. Cached on first call.
     *
     * @return  String[] of the names of the schemas in the database.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
	public synchronized String[] getSchemas() throws SQLException
	{
		final String key = "getSchemas";
		String[] value = (String[])_cache.get(key);
		if (value != null)
		{
			return value;
		}

		boolean hasGuest = false;
		boolean hasSysFun = false;

		final String dbProductName = getDatabaseProductName();
		final boolean isMSSQLorSYBASE = dbProductName.equals(IDBMSProductNames.MICROSOFT_SQL)
								|| dbProductName.equals(IDBMSProductNames.SYBASE)
								|| dbProductName.equals(IDBMSProductNames.SYBASE_OLD);
		final boolean isDB2 = dbProductName.startsWith(IDBMSProductNames.DB2);

		final ArrayList list = new ArrayList();
		ResultSet rs = privateGetJDBCMetaData().getSchemas();
		try
		{
            if (rs != null) {
    			final ResultSetReader rdr = new ResultSetReader(rs);
    			Object[] row = null;
    			while ((row = rdr.readRow()) != null)
    			{
    				if (isMSSQLorSYBASE && row[0].equals("guest"))
    				{
    					hasGuest = true;
    				}
    				if (isDB2 && row[0].equals("SYSFUN"))
    				{
    					hasSysFun = true;
    				}
    				list.add(row[0]);
    			}
            }
		}
		finally
		{
            if (rs != null) {
                rs.close();
            }
		}

		// Some drivers for both MS SQL and Sybase don't return guest as
		// a schema name.
		if (isMSSQLorSYBASE && !hasGuest)
		{
			list.add("guest");
		}

		// Some drivers for DB2 don't return SYSFUN as a schema name. A
		// number of system stored procs are kept in this schema.
		if (isDB2 && !hasSysFun)
		{
			list.add("SYSFUN");
		}

		value = (String[])list.toArray(new String[list.size()]);
		_cache.put(key, value);

		return value;
	}

    /**
     * Retrieves whether this database supports schemas at all.
     *
     * @return  <TT>true</TT> if database supports schemas.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
    public boolean supportsSchemas() throws SQLException
	{
		return supportsSchemasInDataManipulation()
				|| supportsSchemasInTableDefinitions();
	}

    /**
     * Retrieves whether a schema name can be used in a data manipulation
     * statement. Cached on first call.
     *
     * @return  <TT>true</TT> if a schema name can be used in a data
     *          manipulation statement.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
    public synchronized boolean supportsSchemasInDataManipulation()
		throws SQLException
	{
		final String key = "supportsSchemasInDataManipulation";
		Boolean value = (Boolean)_cache.get(key);
		if (value != null)
		{
			return value.booleanValue();
		}

		try
		{
			value = new Boolean(privateGetJDBCMetaData().supportsSchemasInDataManipulation());
		}
		catch (SQLException ex)
		{
			if (getDriverName().equals(IDriverNames.FREE_TDS))
			{
				value = Boolean.TRUE;
			}
			throw ex;
		}

		_cache.put(key, value);

		return value.booleanValue();
	}

    /**
     * Retrieves whether a schema name can be used in a table definition
     * statement. Cached on first call.
     *
     * @return  <TT>true</TT> if a schema name can be used in a table
     *          definition statement.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
	public synchronized boolean supportsSchemasInTableDefinitions()
		throws SQLException
	{
		final String key = "supportsSchemasInTableDefinitions";
		Boolean value = (Boolean)_cache.get(key);
		if (value != null)
		{
			return value.booleanValue();
		}

		try
		{
			value = new Boolean(privateGetJDBCMetaData().supportsSchemasInTableDefinitions());
		}
		catch (SQLException ex)
		{
			if (getDriverName().equals(IDriverNames.FREE_TDS))
			{
				value = Boolean.TRUE;
			}
			throw ex;
		}

		_cache.put(key, value);

		return value.booleanValue();
	}

    /**
     * Retrieves whether this DBMS supports stored procedures. Cached on first
     * call.
     *
     * @return  <TT>true</TT> if DBMS supports stored procedures.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
    public synchronized boolean supportsStoredProcedures() throws SQLException
	{
		final String key = "supportsStoredProcedures";
		Boolean value = (Boolean)_cache.get(key);
		if (value != null)
		{
			return value.booleanValue();
		}

		// PostgreSQL (at least 7.3.2) returns false for
		// supportsStoredProcedures() even though it does support them.
		if (getDatabaseProductName().equals(IDBMSProductNames.POSTGRESQL))
		{
			value = Boolean.TRUE;
		}
		else
		{
			value = new Boolean(privateGetJDBCMetaData().supportsStoredProcedures());
		}
		_cache.put(key, value);

		return value.booleanValue();
	}

    /**
     * Retrieves whether this database supports savepoints.
     * 
     * @return true if savepoints are supported; false otherwise
     * 
     * @throws SQLException if a database access error occurs
     */
    public boolean supportsSavepoints() throws SQLException {
        
        final String key = "supportsSavepoints";
        Boolean value = (Boolean)_cache.get(key);
        if (value != null)
        {
            return value.booleanValue();
        }
        value = new Boolean(privateGetJDBCMetaData().supportsSavepoints());

        _cache.put(key, value);

        return value.booleanValue();        
    }
    
    /**
     * Return a string array containing the names of all the catalogs in the
     * database. Cached on first call.
     *
     * @return  String[] of the names of the catalogs in the database.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
    public synchronized String[] getCatalogs() throws SQLException
	{
		final String key = "getCatalogs";
		String[] value = (String[])_cache.get(key);
		if (value != null)
		{
			return value;
		}

		final ArrayList list = new ArrayList();
		ResultSet rs = privateGetJDBCMetaData().getCatalogs();
		try
		{
            if (rs != null) {
    			final ResultSetReader rdr = new ResultSetReader(rs);
    			Object[] row = null;
    			while ((row = rdr.readRow()) != null)
    			{
    				list.add(row[0]);
    			}
            }
		}
		finally
		{
            if (rs != null) {
                rs.close();
            }
		}

		value = (String[])list.toArray(new String[list.size()]);
		_cache.put(key, value);

		return value;
	}

    /**
     * Retrieves the URL for this DBMS.
     * 
     * @return  the URL for this DBMS or null if it cannot be generated
     * 
     * @throws SQLException if a database access error occurs
     */
    public synchronized String getURL() throws SQLException {
        final String key = "getURL";
        String value = (String)_cache.get(key);
        if (value != null) {
            return value;
        }
        
        value = privateGetJDBCMetaData().getURL();
        _cache.put(key, value);
        
        return value;
    }    
    
    /**
     * Retrieves the database vendor's preferred term for "catalog".
     * 
     * @return the vendor term for "catalog"
     * 
     * @throws SQLException if a database access error occurs
     */
    public synchronized String getCatalogTerm() throws SQLException {
        final String key = "getCatalogTerm";
        String value = (String)_cache.get(key);
        if (value != null) {
            return value;
        }
        
        value = privateGetJDBCMetaData().getCatalogTerm();
        _cache.put(key, value);
        
        return value;
    }
    
    /**
     * Retrieves the database vendor's preferred term for "schema".
     * 
     * @return  the vendor term for "schema"
     * 
     * @throws SQLException if a database access error occurs
     */
    public synchronized String getSchemaTerm() throws SQLException {
        final String key = "getSchemaTerm";
        String value = (String)_cache.get(key);
        if (value != null) {
            return value;
        }
        
        value = privateGetJDBCMetaData().getSchemaTerm();
        _cache.put(key, value);
        
        return value;        
    }

    /**
     * Retrieves the database vendor's preferred term for "procedure".
     * 
     * @return the vendor term for "procedure"
     * 
     * @throws SQLException if a database access error occurs
     */
    public synchronized String getProcedureTerm() throws SQLException {
        final String key = "getProcedureTerm";
        String value = (String)_cache.get(key);
        if (value != null) {
            return value;
        }
        
        value = privateGetJDBCMetaData().getProcedureTerm();
        _cache.put(key, value);
        
        return value;        
    }
    
    
    /**
     * Retrieves the String that this database uses as the separator between a
     * catalog and table name. Cached on first call.
     *
     * @return  The separator character.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
    public synchronized String getCatalogSeparator() throws SQLException
	{
		final String key = "getCatalogSeparator";
		String value = (String)_cache.get(key);
		if (value != null)
		{
			return value;
		}

		value = privateGetJDBCMetaData().getCatalogSeparator();
		_cache.put(key, value);

		return value;
	}

    /**
     * Retrieves whether this database supports catalogs at all.
     *
     * @return  <TT>true</TT> fi database supports catalogs.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
    public boolean supportsCatalogs() throws SQLException
	{
		return supportsCatalogsInTableDefinitions()
			|| supportsCatalogsInDataManipulation()
			|| supportsCatalogsInProcedureCalls();
	}

    /**
     * Retrieves whether a catalog name can be used in a table definition
     * statement. Cached on first call.
     *
     * @return  <TT>true</TT> if a catalog name can be used in a table
     *          definition statement.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
    public synchronized boolean supportsCatalogsInTableDefinitions() throws SQLException
	{
		final String key = "supportsCatalogsInTableDefinitions";
		Boolean value = (Boolean)_cache.get(key);
		if (value != null)
		{
			return value.booleanValue();
		}

		try
		{
			value = new Boolean(privateGetJDBCMetaData().supportsCatalogsInTableDefinitions());
		}
		catch (SQLException ex)
		{
			if (getDriverName().equals(IDriverNames.FREE_TDS))
			{
				value = Boolean.TRUE;
			}
			throw ex;
		}

		_cache.put(key, value);

		return value.booleanValue();
	}

    /**
     * Retrieves whether a catalog name can be used in a data manipulation
     * statement. Cached on first call.
     *
     * @return  <TT>true</TT> if a catalog name can be used in a data
     *          manipulation statement.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
    public synchronized boolean supportsCatalogsInDataManipulation() throws SQLException
	{
		final String key = "supportsCatalogsInDataManipulation";
		Boolean value = (Boolean)_cache.get(key);
		if (value != null)
		{
			return value.booleanValue();
		}

		try
		{
			value = new Boolean(privateGetJDBCMetaData().supportsCatalogsInDataManipulation());
		}
		catch (SQLException ex)
		{
			if (getDriverName().equals(IDriverNames.FREE_TDS))
			{
				value = Boolean.TRUE;
			}
			throw ex;
		}
		_cache.put(key, value);

		return value.booleanValue();
	}

    /**
     * Retrieves whether a catalog name can be used in a procedure call. Cached
     * on first call.
     *
     * @return  <TT>true</TT> if a catalog name can be used in a procedure
     *          call.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
	public synchronized boolean supportsCatalogsInProcedureCalls() throws SQLException
	{
		final String key = "supportsCatalogsInProcedureCalls";
		Boolean value = (Boolean)_cache.get(key);
		if (value != null)
		{
			return value.booleanValue();
		}

		try
		{
			value = new Boolean(privateGetJDBCMetaData().supportsCatalogsInProcedureCalls());
		}
		catch (SQLException ex)
		{
			if (getDriverName().equals(IDriverNames.FREE_TDS))
			{
				value = Boolean.TRUE;
			}
			throw ex;
		}
		_cache.put(key, value);

		return value.booleanValue();
	}

    /**
     * Return the <TT>DatabaseMetaData</TT> object for this connection.
     *
     * @return  The <TT>DatabaseMetaData</TT> object for this connection.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
	public synchronized DatabaseMetaData getJDBCMetaData() throws SQLException
	{
		return privateGetJDBCMetaData();
	}

	/**
     * 
     * @return
     * @throws SQLException
	 */
    public synchronized IDataSet getMetaDataSet() throws SQLException {
        return new MetaDataDataSet(privateGetJDBCMetaData());
    }
    
    /**
     * @deprecated  Replaced by getDataTypes
     */
    public ResultSet getTypeInfo() throws SQLException
	{
		return privateGetJDBCMetaData().getTypeInfo();
	}

    /**
     * 
     * @return
     * @throws DataSetException
     */
    public synchronized IDataSet getTypesDataSet() throws DataSetException {
        ResultSet rs = null;
        try {
            rs = privateGetJDBCMetaData().getTypeInfo();
            return (new DatabaseTypesDataSet(rs));
        } catch (SQLException e) {
            throw new DataSetException(e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
        }
    }
    
    /**
     * Retrieve information about the data types in the database.
     *
     * TODO: Any reason this is not cached?
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
    public synchronized DataTypeInfo[] getDataTypes()
		throws SQLException
	{
		final DatabaseMetaData md = privateGetJDBCMetaData();
		final ArrayList list = new ArrayList();
		final ResultSet rs = md.getTypeInfo();
		try
		{
			ResultSetColumnReader rdr = new ResultSetColumnReader(rs);
			while (rdr.next())
			{
				final String typeName = rdr.getString(1);
				final int dataType = rdr.getLong(2).intValue();
				final int precis = rdr.getLong(3).intValue();
				final String literalPrefix = rdr.getString(4);
				final String literalSuffix = rdr.getString(5);
				final String createParams = rdr.getString(6);
				final int nullable = rdr.getLong(7).intValue();
				final boolean caseSens = rdr.getBoolean(8).booleanValue();
				final int searchable = rdr.getLong(9).intValue();
				final boolean unsigned = rdr.getBoolean(10).booleanValue();
				final boolean canBeMoney = rdr.getBoolean(11).booleanValue();
				final boolean canBeAutoInc = rdr.getBoolean(12).booleanValue();
				final String localTypeName = rdr.getString(13);
				final int min = rdr.getLong(14).intValue();
				final int max = rdr.getLong(15).intValue();
				final int radix = rdr.getLong(18).intValue();
				list.add(new DataTypeInfo(typeName, dataType, precis,
										literalPrefix, literalSuffix,
										createParams, nullable, caseSens,
										searchable, unsigned, canBeMoney,
										canBeAutoInc, localTypeName,
										min, max, radix,
										this));
			}
		}
		finally
		{
            if (rs != null) {
                rs.close();
            }
		}
		return (DataTypeInfo[])list.toArray(new DataTypeInfo[list.size()]);
	}

    /**
     * Retrieve information about the stored procedures in the system
     *
     * @param   catalog     The name of the catalog to retrieve procedures
     *                      for. An empty string will return those without a
     *                      catalog. <TT>null</TT> means that the catalog
     *                      will not be used to narrow the search.
     * @param   schemaPattern   The name of the schema to retrieve procedures
     *                      for. An empty string will return those without a
     *                      schema. <TT>null</TT> means that the schema
     *                      will not be used to narrow the search.
     * @param   procedureNamepattern    A procedure name pattern; must match the
     *                                  procedure name as it is stored in the
     *                                  database.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
    public synchronized IProcedureInfo[] getProcedures(String catalog,
				String schemaPattern, String procedureNamePattern)
		throws SQLException
	{
		DatabaseMetaData md = privateGetJDBCMetaData();
		ArrayList list = new ArrayList();
		ResultSet rs = md.getProcedures(catalog, schemaPattern, procedureNamePattern);
		try
		{
			final int[] cols = new int[] {1, 2, 3, 7, 8};
			final ResultSetReader rdr = new ResultSetReader(rs, cols);
			Object[] row = null;
			while ((row = rdr.readRow()) != null)
			{
				final int type = ((Number)row[4]).intValue();
				list.add(new ProcedureInfo(getAsString(row[0]), getAsString(row[1]),
										getAsString(row[2]), getAsString(row[3]), type, this));
			}
		}
		finally
		{
            if (rs != null) {
                rs.close();
            }
			
		}
		return (IProcedureInfo[])list.toArray(new IProcedureInfo[list.size()]);
	}

    /**
     * Return a string array containing the different types of tables in this
     * database. E.G. <TT>"TABLE", "VIEW", "SYSTEM TABLE"</TT>. Cached on first
     * call.
     *
     * @return  table type names.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
    public synchronized String[] getTableTypes() throws SQLException
	{
		final String key = "getTableTypes";
		String[] value = (String[])_cache.get(key);
		if (value != null)
		{
			return value;
		}

		final DatabaseMetaData md = privateGetJDBCMetaData();
        
		// Use a set rather than a list as some combinations of MS SQL and the
		// JDBC/ODBC return multiple copies of each table type.
		final Set tableTypes = new TreeSet();
		final ResultSet rs = md.getTableTypes();
		if (rs != null)
		{
			try
			{
				while (rs.next())
				{
					tableTypes.add(rs.getString(1).trim());
				}
			}
			finally
			{
                if (rs != null) {
                    rs.close();
                }
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

		// At least one version of PostgreSQL through the JDBC/ODBC
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

		value = (String[])tableTypes.toArray(new String[tableTypes.size()]);
		_cache.put(key, value);
		return value;
	}

	public synchronized ITableInfo[] getAllTables()
			throws SQLException
	{
		final String key = "getTables";
		ITableInfo[] value = (ITableInfo[])_cache.get(key);
		if (value == null)
		{
			value = getTables(null, null, "%", null);
			_cache.put(key, value);
		}
		return value;
	}
    
    /**
     * Retrieve information about the tables in the system.
     *
     * @param   catalog     The name of the catalog to retrieve tables
     *                      for. An empty string will return those without a
     *                      catalog. <TT>null</TT> means that the catalog
     *                      will not be used to narrow the search.
     * @param   schemaPattern   The name of the schema to retrieve tables
     *                      for. An empty string will return those without a
     *                      schema. <TT>null</TT> means that the schema
     *                      will not be used to narrow the search.
     * @param   tableNamepattern    A table name pattern; must match the
     *                              table name as it is stored in the
     *                              database.
     * @param   types       List of table types to include; null returns all types.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
    public synchronized ITableInfo[] getTables(String catalog, 
                                               String schemaPattern,
                                               String tableNamePattern, 
                                               String[] types)
		throws SQLException
	{
		final DatabaseMetaData md = privateGetJDBCMetaData();
		final String dbDriverName = getDriverName();
		Set list = new TreeSet();

		if (dbDriverName.equals(IDriverNames.FREE_TDS) && schemaPattern == null)
		{
			schemaPattern = "dbo";
		}

		Map nameMap = null;
		ResultSet superTabResult = null;
		ResultSet tabResult = null;
		try
		{
			try
			{
				//TODO: remove reflection once we only support JDK1.4
				//superTabResult = md.getSuperTables(catalog, schemaPattern,
				//									tableNamePattern);
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
			tabResult = md.getTables(catalog, schemaPattern, tableNamePattern, types);
			while (tabResult != null && tabResult.next())
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
		}
		finally
		{
			if(tabResult != null)
			{
				tabResult.close();
			}
			if (superTabResult != null)
			{
				superTabResult.close();
			}
		}

		return (ITableInfo[])list.toArray(new ITableInfo[list.size()]);
	}

    /**
     * Retrieve information about the UDTs in the system.
     *
     * @param   catalog     The name of the catalog to retrieve UDTs
     *                      for. An empty string will return those without a
     *                      catalog. <TT>null</TT> means that the catalog
     *                      will not be used to narrow the search.
     * @param   schemaPattern   The name of the schema to retrieve UDTs
     *                      for. An empty string will return those without a
     *                      schema. <TT>null</TT> means that the schema
     *                      will not be used to narrow the search.
     * @param   typeNamepattern     A type name pattern; must match the
     *                              type name as it is stored in the
     *                              database.
     * @param   types       List of user-defined types (JAVA_OBJECT, STRUCT, or
     *                      DISTINCT) to include; null returns all types
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
    public synchronized IUDTInfo[] getUDTs(String catalog, String schemaPattern,
								           String typeNamePattern, int[] types)
		throws SQLException
	{
		DatabaseMetaData md = privateGetJDBCMetaData();
		ArrayList list = new ArrayList();
		ResultSet rs = md.getUDTs(catalog, schemaPattern, typeNamePattern, types);
		try
		{
			final int[] cols = new int[] {1, 2, 3, 4, 5, 6};
			final ResultSetReader rdr = new ResultSetReader(rs, cols);
			Object[] row = null;
			while ((row = rdr.readRow()) != null)
			{
				list.add(new UDTInfo(getAsString(row[0]), getAsString(row[1]), getAsString(row[2]),
									getAsString(row[3]), getAsString(row[4]), getAsString(row[5]),
									this));
			}
		}
		finally
		{
            if (rs != null) {
                rs.close();
            }
		}

		return (IUDTInfo[])list.toArray(new IUDTInfo[list.size()]);
	}

   private String getAsString(Object val)
   {
      if(null == val)
      {
         return null;
      }
      else
      {
         if (val instanceof String) {
             return (String)val;
         } else {
             return "" + val;
         }
      }

   }

   /**
     * Retrieve the names of the Numeric Functions that this DBMS supports.
     * Cached on first call.
     *
     * @return  String[] of function names.
     */
   public synchronized String[] getNumericFunctions() throws SQLException
	{
		final String key = "getNumericFunctions";
		String[] value = (String[])_cache.get(key);
		if (value != null)
		{
			return value;
		}

		value = makeArray(privateGetJDBCMetaData().getNumericFunctions());
		_cache.put(key, value);
		return value;
	}

    /**
     * Retrieve the names of the String Functions that this DBMS supports.
     * Cached on first call.
     *
     * @return  String[] of function names.
     */
	public synchronized String[] getStringFunctions() throws SQLException
	{
		final String key = "getStringFunctions";
		String[] value = (String[])_cache.get(key);
		if (value != null)
		{
			return value;
		}

		value = makeArray(privateGetJDBCMetaData().getStringFunctions());
		_cache.put(key, value);
		return value;
	}

    /**
     * Retrieve the names of the System Functions that this DBMS supports.
     * Cached on first call.
     *
     * @return  String[] of function names.
     */
	public synchronized String[] getSystemFunctions() throws SQLException
	{
		final String key = "getSystemFunctions";
		String[] value = (String[])_cache.get(key);
		if (value != null)
		{
			return value;
		}

		value = makeArray(privateGetJDBCMetaData().getSystemFunctions());
		_cache.put(key, value);
		return value;
	}

    /**
     * Retrieve the names of the Date/Time Functions that this DBMS supports.
     * Cached on first call.
     *
     * @return  String[] of function names.
     */
	public synchronized String[] getTimeDateFunctions() throws SQLException
	{
		final String key = "getTimeDateFunctions";
		String[] value = (String[])_cache.get(key);
		if (value != null)
		{
			return value;
		}

		value = makeArray(privateGetJDBCMetaData().getTimeDateFunctions());
		_cache.put(key, value);
		return value;
	}

    /**
     * Retrieve the names of the non-standard keywords that this DBMS supports.
     * Cached on first call.
     *
     * @return  String[] of keywords.
     */
	public synchronized String[] getSQLKeywords() throws SQLException
	{
		final String key = "getSQLKeywords";
		String[] value = (String[])_cache.get(key);
		if (value != null)
		{
			return value;
		}

		value = makeArray(privateGetJDBCMetaData().getSQLKeywords());
		_cache.put(key, value);
		return value;
	}

	public synchronized BestRowIdentifier[] getBestRowIdentifier(ITableInfo ti)
		throws SQLException
	{
		final List results = new ArrayList();

        ResultSet rs = null;
		try
		{
            
            rs = privateGetJDBCMetaData().getBestRowIdentifier(
                    ti.getCatalogName(), ti.getSchemaName(),
                    ti.getSimpleName(),
                    DatabaseMetaData.bestRowTransaction, true);
            
			final String catalog = ti.getCatalogName();
			final String schema = ti.getSchemaName();
			final String table = ti.getSimpleName();

			final ResultSetColumnReader rdr = new ResultSetColumnReader(rs);
			while (rdr.next())
			{
				final BestRowIdentifier rid = new BestRowIdentifier(catalog,
							schema, table, rdr.getLong(1).intValue(),
							rdr.getString(2), rdr.getLong(3).shortValue(),
							rdr.getString(4), rdr.getLong(5).intValue(),
							rdr.getLong(7).shortValue(),
							rdr.getLong(8).shortValue(), this);
				results.add(rid);
			}
		}
		finally
		{
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
		}

		final BestRowIdentifier[] ar = new BestRowIdentifier[results.size()];
		return (BestRowIdentifier[])results.toArray(ar);
	}

	/**
     * @deprecated use getColumnPrivilegesDataSet instead. 
	 */
    public ResultSet getColumnPrivileges(ITableInfo ti)
		throws SQLException
	{
		// MM-MYSQL driver doesnt support null for column name.
		final String dbProdName = getDatabaseProductName();
		final String columns = dbProdName.equalsIgnoreCase(IDBMSProductNames.MYSQL) ? "%" : null;
		return privateGetJDBCMetaData().getColumnPrivileges(ti.getCatalogName(),
													ti.getSchemaName(),
													ti.getSimpleName(),
													columns);
	}

    /**
     * 
     * @param ti
     * @param columnIndices
     * @param computeWidths
     * @return
     * @throws DataSetException
     */
    public synchronized IDataSet getColumnPrivilegesDataSet(ITableInfo ti,
                                                            int[] columnIndices,
                                                            boolean computeWidths) 
        throws DataSetException 
    {
        ResultSet rs = null;
        try {
            DatabaseMetaData md = privateGetJDBCMetaData();
            final String dbProdName = getDatabaseProductName();
            final String columns = 
                dbProdName.equalsIgnoreCase(IDBMSProductNames.MYSQL) ? "%" : null;
            
            rs = md.getColumnPrivileges(ti.getCatalogName(),
                    ti.getSchemaName(),
                    ti.getSimpleName(),
                    columns);
            ResultSetDataSet rsds = new ResultSetDataSet();
                rsds.setResultSet(rs, columnIndices, computeWidths);
            return rsds;
        } catch (SQLException e) {
            throw new DataSetException(e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
        }
    }
    
    /**
     *  @deprecated. Replaced by getExportedKeysInfo 
     */
    public ResultSet getExportedKeys(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getExportedKeys(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName());
	}

    /**
     * 
     * @param ti
     * @return
     * @throws DataSetException
     */
    public synchronized IDataSet getExportedKeysDataSet(ITableInfo ti) 
        throws DataSetException  
    {
        ResultSet rs = null;
        try {
            rs = privateGetJDBCMetaData().getExportedKeys(ti.getCatalogName(), 
                                                          ti.getSchemaName(),
                                                          ti.getSimpleName());
            ResultSetDataSet rsds = new ResultSetDataSet();
            rsds.setResultSet(rs, null, true);
            return rsds;            
        } catch (SQLException e) { 
            throw new DataSetException(e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
        }
    }
    
    /**
     * @deprecated. Replaced by getImportedKeysInfo
     */
	public ResultSet getImportedKeys(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getImportedKeys(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName());
	}

	public synchronized ForeignKeyInfo[] getImportedKeysInfo(String catalog, 
                                                             String schema, 
                                                             String tableName) 
        throws SQLException 
    {
        ResultSet rs = 
            privateGetJDBCMetaData().getImportedKeys(catalog, schema, tableName);
        return getForeignKeyInfo(rs);
    }
    
    public synchronized ForeignKeyInfo[] getImportedKeysInfo(ITableInfo ti)
		throws SQLException
	{
		return getForeignKeyInfo(privateGetJDBCMetaData().getImportedKeys(ti.getCatalogName(),
								ti.getSchemaName(), ti.getSimpleName()));
	}

    public synchronized IDataSet getImportedKeysDataSet(ITableInfo ti) 
        throws DataSetException  
    {
        ResultSet rs = null;
        try {
            rs = privateGetJDBCMetaData().getImportedKeys(ti.getCatalogName(), 
                                                          ti.getSchemaName(),
                                                          ti.getSimpleName());
            ResultSetDataSet rsds = new ResultSetDataSet();
            rsds.setResultSet(rs, null, true);
            return rsds;            
        } catch (SQLException e) { 
            throw new DataSetException(e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
        }
    }    
    
    public synchronized ForeignKeyInfo[] getExportedKeysInfo(String catalog, String schema, String tableName)
        throws SQLException
    {
        ResultSet rs = 
            privateGetJDBCMetaData().getExportedKeys(catalog, schema, tableName);
        return getForeignKeyInfo(rs);
    } 
    
	public synchronized ForeignKeyInfo[] getExportedKeysInfo(ITableInfo ti)
		throws SQLException
	{
		return getForeignKeyInfo(privateGetJDBCMetaData().getExportedKeys(ti.getCatalogName(),
								ti.getSchemaName(), ti.getSimpleName()));
	}
        
	private ForeignKeyInfo[] getForeignKeyInfo(ResultSet rs)
		throws SQLException
	{
		final Map keys = new HashMap();
		final Map columns = new HashMap();

		try
		{
			final ResultSetColumnReader rdr = new ResultSetColumnReader(rs);
			while (rdr.next())
			{
				final ForeignKeyInfo fki = new ForeignKeyInfo(rdr.getString(1),
							rdr.getString(2), rdr.getString(3), rdr.getString(4),
                            rdr.getString(5),rdr.getString(6), rdr.getString(7),
                            rdr.getString(8),
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
		}
		finally
		{
            if (rs != null) {
                rs.close();
            }
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

    /**
     * @deprecated use getIndexInfo instead.
     */
    public ResultSet getIndexInfo(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getIndexInfo(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName(), false, true);
	}

    /**
     * 
     * @param ti
     * @param columnIndices
     * @param computeWidths
     * @return
     * @throws DataSetException
     */
    public synchronized ResultSetDataSet getIndexInfo(ITableInfo ti, 
                                                      int[] columnIndices,
                                                      boolean computeWidths) 
        throws DataSetException 
    {
        ResultSet rs = null;
        try {
            rs = privateGetJDBCMetaData().getIndexInfo(
                    ti.getCatalogName(), ti.getSchemaName(),
                    ti.getSimpleName(), false, true);
            ResultSetDataSet rsds = new ResultSetDataSet();
            rsds.setResultSet(rs, columnIndices, computeWidths);
            return rsds;
        } catch (SQLException e) {
            throw new DataSetException(e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
        }
    }
    
    /**
     * @deprecated use getPrimaryKey instead
     */
	public ResultSet getPrimaryKeys(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getPrimaryKeys(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName());
	}
    
	/**
     * 
     * @param ti
     * @param columnIndices
     * @param computeWidths
     * @return
     * @throws DataSetException
	 */
    public synchronized IDataSet getPrimaryKey(ITableInfo ti, 
                                               int[] columnIndices,
                                               boolean computeWidths)
        throws DataSetException
    {
        ResultSet rs = null;
        try {
            rs = privateGetJDBCMetaData().getPrimaryKeys(
                    ti.getCatalogName(), ti.getSchemaName(),
                    ti.getSimpleName());
            ResultSetDataSet rsds = new ResultSetDataSet();
                rsds.setResultSet(rs, columnIndices , computeWidths);
            return rsds;  
        } catch (SQLException e) { 
            throw new DataSetException(e);
        }finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
        }
    }

    /**
     * 
     * @param ti
     * @return
     * @throws SQLException
     */
    public synchronized PrimaryKeyInfo[] getPrimaryKey(ITableInfo ti) 
        throws SQLException
    {
        final List results = new ArrayList();
        ResultSet rs = null;
        try {
            rs = privateGetJDBCMetaData().getPrimaryKeys(
                    ti.getCatalogName(), ti.getSchemaName(),
                    ti.getSimpleName());
            while (rs.next()) {
                PrimaryKeyInfo pkInfo = 
                    new PrimaryKeyInfo(rs.getString(1),  // catalog
                                       rs.getString(2),  // schema
                                       rs.getString(4),  // columnName
                                       rs.getShort(5),   // keySequence
                                       rs.getString(6),  // pkName
                                       this);
                results.add(pkInfo);
            }
        }finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
        }
        
        final PrimaryKeyInfo[] ar = new PrimaryKeyInfo[results.size()];
        return (PrimaryKeyInfo[])results.toArray(ar);
    }
    
    /**
     * @deprecated use getProcedureColumnsDataSet instead
     */
    public ResultSet getProcedureColumns(IProcedureInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getProcedureColumns(ti.getCatalogName(),
													        ti.getSchemaName(),
													        ti.getSimpleName(),
													        "%");
	}

    /**
     * 
     * @param ti
     * @return
     * @throws DataSetException
     */
    public synchronized IDataSet getProcedureColumnsDataSet(IProcedureInfo ti)
        throws DataSetException
    {
        ResultSet rs = null;
        try {
            DatabaseMetaData md = privateGetJDBCMetaData();
            rs = md.getProcedureColumns(ti.getCatalogName(),
                                        ti.getSchemaName(),
                                        ti.getSimpleName(),
                                        "%");
            ResultSetDataSet rsds = new ResultSetDataSet();
            rsds.setResultSet(rs);
            return rsds;
        } catch (SQLException e) {
            throw new DataSetException(e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
        }
    }
    
    /**
     * @deprecated use getTablePrivilegesDataSet instead
     */ 
	public ResultSet getTablePrivileges(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getTablePrivileges(ti.getCatalogName(),
													ti.getSchemaName(),
													ti.getSimpleName());
	}

	/**
     * 
     * @param ti
     * @param columnIndices
     * @param computeWidths
     * @return
     * @throws DataSetException
	 */
    public synchronized IDataSet getTablePrivilegesDataSet(ITableInfo ti,
                                                           int[] columnIndices,
                                                           boolean computeWidths) 
        throws DataSetException 
    {
        ResultSet rs = null;
        try {
            DatabaseMetaData md = privateGetJDBCMetaData();
            rs = md.getTablePrivileges(ti.getCatalogName(),
                                       ti.getSchemaName(),
                                       ti.getSimpleName());
            ResultSetDataSet rsds = new ResultSetDataSet();
            rsds.setResultSet(rs, columnIndices, computeWidths);
            return rsds;
        } catch (SQLException e) {
            throw new DataSetException(e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
        }
    }
    
    
	/**
     *  @deprecated use getVersionColumnsDataSet instead
	 */
	public ResultSet getVersionColumns(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getVersionColumns(ti.getCatalogName(),
												          ti.getSchemaName(),
												          ti.getSimpleName());
	}

	/**
     * 
     * @param ti
     * @return
     * @throws DataSetException
	 */
    public synchronized IDataSet getVersionColumnsDataSet(ITableInfo ti)
        throws DataSetException
    {
        ResultSet rs = null;
        try {
            DatabaseMetaData md = privateGetJDBCMetaData();
            rs = md.getVersionColumns(ti.getCatalogName(),
                                      ti.getSchemaName(),
                                      ti.getSimpleName());
            ResultSetDataSet rsds = new ResultSetDataSet();
            rsds.setResultSet(rs);
            return rsds;
        } catch (SQLException e) {
            throw new DataSetException(e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
        }
    }
    
    
    /**
     * @deprecated use getColumns that returns an IDataSet or a 
     *             TableColumnInfo[] instead.
     */
	public ResultSet getColumns(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getColumns(ti.getCatalogName(),
											ti.getSchemaName(),
											ti.getSimpleName(), "%");
	}
    
	/**
     * 
     * @param ti
     * @param columnIndices
     * @param computeWidths
     * @return
     * @throws DataSetException
	 */
    public synchronized IDataSet getColumns(ITableInfo ti, 
                                            int[] columnIndices,
                                            boolean computeWidths)
        throws DataSetException
    {
        IDataSet result = null;
        ResultSet rs = null;
        try {
            rs = getColumns(ti);
            ResultSetDataSet rsds = new ResultSetDataSet();
            rsds.setResultSet(rs, columnIndices, computeWidths);
            result = rsds;
        } catch (SQLException e) { 
            throw new DataSetException(e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
        }
        return result;
    }
    
    /**
     * 
     * @param catalog
     * @param schema
     * @param table
     * @return
     * @throws SQLException
     */
    public synchronized TableColumnInfo[] getColumnInfo(String catalog, 
                                                        String schema, 
                                                        String table) 
        throws SQLException 
    {
        final Map columns = new TreeMap();
        ResultSet rs = null;
        try {
            DatabaseMetaData md = privateGetJDBCMetaData();
            rs = md.getColumns(catalog, schema, table, "%");
            final ResultSetColumnReader rdr = new ResultSetColumnReader(rs);
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
            
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
        }
        return (TableColumnInfo[])columns.values().toArray(
                new TableColumnInfo[columns.size()]);        
    }
    
    /**
     * 
     * @param ti
     * @return
     * @throws SQLException
     */
    public synchronized TableColumnInfo[] getColumnInfo(ITableInfo ti)
		throws SQLException
	{
	    return getColumnInfo(ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName());
    }

    /**
     * Retrieve whether this driver correctly handles Statement.setMaxRows(int).
     * Some drivers such as version 5.02 of the Opta2000 driver use setMaxRows
     * for UPDATEs, DELETEs etc. instead of just SELECTs. If this method returns
     * <TT>false</TT> then setMaxRows should only be applied to statements
     * that are running SELECTs.
     *
     * @return  <TT>true</TT> if this driver correctly implements setMaxRows().
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
    public boolean correctlySupportsSetMaxRows() throws SQLException
	{
		return !IDriverNames.OPTA2000.equals(getDriverName());
	}

    /**
     * Retrieve whether this driver supports multiple result sets. Cached on
     * first call.
     *
     * @return  <tt>true</tt> if driver supports multiple result sets
     *          else <tt>false</tt>.
     *
     * @throws  SQLException    Thrown if an SQL error occurs.
     */
	public synchronized boolean supportsMultipleResultSets()
			throws SQLException
	{
		final String key = "supportsMultipleResultSets";
		Boolean value = (Boolean)_cache.get(key);
		if (value != null)
		{
			return value.booleanValue();
		}

		value = new Boolean(privateGetJDBCMetaData().supportsMultipleResultSets());
		_cache.put(key, value);

		return value.booleanValue();
	}

    /**
     * Clear cache of commonly accessed metadata properties.
     */
	public void clearCache()
	{
		_cache.clear();
	}

	/**
	 * Make a String array of the passed string. Commas separate the elements
	 * in the input string. The array is sorted.
	 *
	 * @param	data	Data to be split into the array.
	 *
	 * @return	data as an array.
	 */
	private static String[] makeArray(String data)
	{
		if (data == null)
		{
			data = "";
		}

		final List list = new ArrayList();
		final StringTokenizer st = new StringTokenizer(data, ",");
		while (st.hasMoreTokens())
		{
			list.add(st.nextToken());
		}
		Collections.sort(list);

		return (String[])list.toArray(new String[list.size()]);
	}

	/**
	 * Return the <TT>DatabaseMetaData</TT> object for this connection.
	 *
	 * @return	The <TT>DatabaseMetaData</TT> object for this connection.
	 *
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	private DatabaseMetaData privateGetJDBCMetaData() throws SQLException
	{
        checkThread();
		return _conn.getConnection().getMetaData();
	}

    /**
     * 
     * @param fki
     * @return
     */
	private String createForeignKeyInfoKey(ForeignKeyInfo fki)
	{
		final StringBuffer buf = new StringBuffer();
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
    
    /**
     * Check the thread of the caller to see if it is the event dispatch thread
     * and if we are debugging print a debug log message with the call trace.
     */
    private void checkThread() {
        if (s_log.isDebugEnabled() && SwingUtilities.isEventDispatchThread()) {
            try {
                throw new Exception("GUI Thread is doing database work");
            } catch (Exception e) {
                s_log.debug(e.getMessage(), e);
            }
        }
    }
}

