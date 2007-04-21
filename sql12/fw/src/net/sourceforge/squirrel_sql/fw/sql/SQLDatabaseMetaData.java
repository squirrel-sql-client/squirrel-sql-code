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

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DatabaseTypesDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
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
public class SQLDatabaseMetaData implements ISQLDatabaseMetaData
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
        String AS400 = "AS/400 Toolbox for Java JDBC Driver";
        String OPTA2000 = "i-net OPTA 2000";
	}

   public static class DriverMatch
   {
      private static final String COM_HTTX_DRIVER_PREFIX = "com.hxtt.sql.";

      public static boolean isComHttxDriver(SQLConnection con)
      {
         if(null == con)
         {
            return false;
         }
         return con.getSQLDriver().getDriverClassName().startsWith(COM_HTTX_DRIVER_PREFIX);
      }
   }

	/** Connection to database this class is supplying information for. */
	private SQLConnection _conn;

	/**
	 * Cache of commonly accessed metadata properties keyed by the method
	 * name that attempts to retrieve them.
    * Note, this cache should only be used for metadata that are not
    * likely to be changed during an open Session.
    * Meta data that is likely to be changed should be kept in SchemaInfo.
	 */
	private Map _cache = Collections.synchronizedMap(new HashMap());

    /**
     * If previous attempts to getSuperTables fail, then this will be set to 
     * false, and prevent further attempts.
     */
    private boolean supportsSuperTables = true;
    
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getUserName()
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getDatabaseProductName()
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getDatabaseProductVersion()
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getDriverName()
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getJDBCVersion()
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getIdentifierQuoteString()
     */
	public synchronized String getIdentifierQuoteString() throws SQLException
	{
		final String key = "getIdentifierQuoteString";
		String value = (String)_cache.get(key);
		if (value == null)
		{
            boolean isSQLServer = 
                DialectFactory.isSyBase(this) || DialectFactory.isMSSQLServer(this);
            
			final String driverName = getDriverName();
            
			if (isSQLServer)
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getCascadeClause()
     */
    public synchronized String getCascadeClause() throws SQLException {
        final String key = "getCascadeClause";
        String value = (String)_cache.get(key);
        if (value == null)
        {
            final String driverName = getDriverName();
            if (DialectFactory.isDB2(this) || DialectFactory.isOracle(this))
            {
                value = "CASCADE";
            }
            else
            {
                value = "";
            }
            _cache.put(key, value);
        }
        return value;
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getSchemas()
     */
	public synchronized String[] getSchemas() throws SQLException
	{
//		final String key = "getSchemas";
//		String[] value = (String[])_cache.get(key);
//		if (value != null)
//		{
//			return value;
//		}

		boolean hasGuest = false;
		boolean hasSysFun = false;
        
        final boolean isMSSQLorSYBASE = 
              DialectFactory.isSyBase(this) || DialectFactory.isMSSQLServer(this);

		final boolean isDB2 = DialectFactory.isDB2(this);

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
         close(rs);
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

//		value = (String[])list.toArray(new String[list.size()]);
//		_cache.put(key, value);

		return (String[])list.toArray(new String[list.size()]);
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#supportsSchemas()
     */
    public boolean supportsSchemas() throws SQLException
	{
		return supportsSchemasInDataManipulation()
				|| supportsSchemasInTableDefinitions();
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#supportsSchemasInDataManipulation()
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
            boolean isSQLServer = 
                DialectFactory.isSyBase(this) || DialectFactory.isMSSQLServer(this);
            
			if (isSQLServer)
			{
				value = Boolean.TRUE;
			}
			throw ex;
		}

		_cache.put(key, value);

		return value.booleanValue();
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#supportsSchemasInTableDefinitions()
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
            boolean isSQLServer = 
                DialectFactory.isSyBase(this) || DialectFactory.isMSSQLServer(this);            
			if (isSQLServer)
			{
				value = Boolean.TRUE;
			}
			throw ex;
		}

		_cache.put(key, value);

		return value.booleanValue();
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#supportsStoredProcedures()
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
		if (DialectFactory.isPostgreSQL(this))
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#supportsSavepoints()
     */
    public synchronized boolean supportsSavepoints() throws SQLException {
        
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
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#supportsResultSetType(int)
     */
    public synchronized boolean supportsResultSetType(int type) 
        throws SQLException
    {
        final String key = "supportsResultSetType";
        Boolean value = (Boolean)_cache.get(key);
        if (value != null)
        {
            return value.booleanValue();
        }
        value = new Boolean(privateGetJDBCMetaData().supportsResultSetType(type));

        _cache.put(key, value);

        return value.booleanValue();                
    }
        
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getCatalogs()
     */
    public synchronized String[] getCatalogs() throws SQLException
	{
//		final String key = "getCatalogs";
//		String[] value = (String[])_cache.get(key);
//		if (value != null)
//		{
//			return value;
//		}

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
         close(rs);
		}

//		value = (String[])list.toArray(new String[list.size()]);
//		_cache.put(key, value);

		return (String[])list.toArray(new String[list.size()]);
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getURL()
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
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getCatalogTerm()
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
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getSchemaTerm()
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getProcedureTerm()
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
    
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getCatalogSeparator()
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#supportsCatalogs()
     */
    public boolean supportsCatalogs() throws SQLException
	{
		return supportsCatalogsInTableDefinitions()
			|| supportsCatalogsInDataManipulation()
			|| supportsCatalogsInProcedureCalls();
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#supportsCatalogsInTableDefinitions()
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
            boolean isSQLServer = 
                DialectFactory.isSyBase(this) || DialectFactory.isMSSQLServer(this);
            
			if (isSQLServer)
			{
				value = Boolean.TRUE;
			}
			throw ex;
		}

		_cache.put(key, value);

		return value.booleanValue();
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#supportsCatalogsInDataManipulation()
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
		    boolean isSQLServer = 
                DialectFactory.isSyBase(this) || DialectFactory.isMSSQLServer(this);

			if (isSQLServer)
			{
				value = Boolean.TRUE;
			}
			throw ex;
		}
		_cache.put(key, value);

		return value.booleanValue();
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#supportsCatalogsInProcedureCalls()
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
            boolean isSQLServer = 
                DialectFactory.isSyBase(this) || DialectFactory.isMSSQLServer(this);
            
			if (isSQLServer)
			{
				value = Boolean.TRUE;
			}
			throw ex;
		}
		_cache.put(key, value);

		return value.booleanValue();
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getJDBCMetaData()
     */
	public synchronized DatabaseMetaData getJDBCMetaData() throws SQLException
	{
		return privateGetJDBCMetaData();
	}

	/* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getMetaDataSet()
     */
    public synchronized IDataSet getMetaDataSet() throws SQLException {
        return new MetaDataDataSet(privateGetJDBCMetaData());
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getTypeInfo()
     * 
     * @deprecated  Replaced by getDataTypes
     */
    public ResultSet getTypeInfo() throws SQLException
	{
		return privateGetJDBCMetaData().getTypeInfo();
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getTypesDataSet()
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
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getDataTypes()
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
         close(rs);
      }
		return (DataTypeInfo[])list.toArray(new DataTypeInfo[list.size()]);
	}

   private void close(ResultSet rs)
   {
      try
      {
         if (rs != null)
         {
            rs.close();
         }
      }
      catch (Exception e)
      {
         s_log.info("closing resultset failed", e);
      }
   }

   /* (non-Javadoc)
 * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getProcedures(java.lang.String, java.lang.String, java.lang.String, net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack)
 */
   public synchronized IProcedureInfo[] getProcedures(String catalog,
                                                      String schemaPattern,
                                                      String procedureNamePattern,
                                                      ProgressCallBack progressCallBack)
     throws SQLException
  {
     DatabaseMetaData md = privateGetJDBCMetaData();
     ArrayList list = new ArrayList();
     ResultSet rs = md.getProcedures(catalog, schemaPattern, procedureNamePattern);
     int count = 0;
     try
     {
        final int[] cols = new int[]{1, 2, 3, 7, 8};
        final ResultSetReader rdr = new ResultSetReader(rs, cols);
        Object[] row = null;
        while ((row = rdr.readRow()) != null)
        {
           final int type = ((Number) row[4]).intValue();
           ProcedureInfo pi = new ProcedureInfo(getAsString(row[0]), getAsString(row[1]),
              getAsString(row[2]), getAsString(row[3]), type, this);

           list.add(pi);

           if (null != progressCallBack)
           {
              if(0 == count++ % 200 )
              {
                 progressCallBack.currentlyLoading(pi.getSimpleName());
              }
           }
        }
     }
     finally
     {
        close(rs);

     }
     return (IProcedureInfo[]) list.toArray(new IProcedureInfo[list.size()]);
  }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getTableTypes()
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
            close(rs);
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
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Detected PostgreSQL and "+nbrTableTypes+
                                " table types - overriding to 4 table types");
                }
				tableTypes.clear();
				tableTypes.add("TABLE");
				tableTypes.add("SYSTEM TABLE");
				tableTypes.add("VIEW");
                tableTypes.add("SYSTEM VIEW");
			}
            // Treating indexes as tables interferes with the operation of the
            // PostgreSQL plugin
            if (tableTypes.contains("INDEX")) {
                tableTypes.remove("INDEX");
            }
            // Treating sequences as tables interferes with the operation of the
            // PostgreSQL plugin            
            if (tableTypes.contains("SEQUENCE")) {
                tableTypes.remove("SEQUENCE");
            }
		}

		value = (String[])tableTypes.toArray(new String[tableTypes.size()]);
		_cache.put(key, value);
		return value;
	}


   /* (non-Javadoc)
 * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getTables(java.lang.String, java.lang.String, java.lang.String, java.lang.String[], net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack)
 */
   public synchronized ITableInfo[] getTables(String catalog,
                                              String schemaPattern,
                                              String tableNamePattern,
                                              String[] types,
                                              ProgressCallBack progressCallBack)
      throws SQLException
   {


      final DatabaseMetaData md = privateGetJDBCMetaData();
      final String dbDriverName = getDriverName();
      Set list = new TreeSet();
      boolean isSQLServer = 
          DialectFactory.isSyBase(this) || DialectFactory.isMSSQLServer(this);
      if (isSQLServer && schemaPattern == null)
      {
         schemaPattern = "dbo";
      }
      if (dbDriverName.equals(IDriverNames.AS400) && schemaPattern == null)
      {
          schemaPattern = "*ALLUSR";
      }

      //Add begin
      if (catalog == null && DriverMatch.isComHttxDriver(_conn))
      {
          String[] catalogs=getCatalogs();
          if (catalogs != null) {
            for (int i = 0; i < catalogs.length; i++) {
              ITableInfo[] tables = getTables(catalogs[i],
                                              schemaPattern,
                                              tableNamePattern,
                                              types,
                                              progressCallBack);
              for(int j=0;j<tables.length;j++){
                list.add(tables[j]);
              }
            }
            return (ITableInfo[]) list.toArray(new ITableInfo[list.size()]);
          }
      }
      //Add end


      Map nameMap = null;
      ResultSet superTabResult = null;
      ResultSet tabResult = null;
      try
      {
         if (supportsSuperTables) {
             try
             {
                superTabResult = md.getSuperTables(catalog, 
                                                   schemaPattern,
                								   tableNamePattern);
                // create a mapping of names if we have supertable info, since
                // we need to find the ITableInfo again for re-ordering.
                if (superTabResult != null && superTabResult.next())
                {
                   nameMap = new HashMap();
                }
             }
             catch (Throwable th)
             {
                s_log.debug("DBMS/Driver doesn't support getSupertables(): "+
                            th.getMessage());
                supportsSuperTables = false;
             }
         }
         // store all plain table info we have.
         tabResult = md.getTables(catalog, schemaPattern, tableNamePattern, types);
         int count = 0;
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

            if (null != progressCallBack)
            {
               if (0 == count++ % 100)
               {
                  progressCallBack.currentlyLoading(tabInfo.getSimpleName());
               }
            }
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

               if (null != progressCallBack)
               {
                  if (0 == count++ % 20)
                  {
                     progressCallBack.currentlyLoading(tabInfo.getSimpleName());
                  }
               }
            }
            while (superTabResult.next());
         }
      }
      finally
      {
         close(tabResult);
         close(superTabResult);
      }

      return (ITableInfo[]) list.toArray(new ITableInfo[list.size()]);
   }
   
   /* (non-Javadoc)
 * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getUDTs(java.lang.String, java.lang.String, java.lang.String, int[])
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
         close(rs);
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

   /* (non-Javadoc)
 * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getNumericFunctions()
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getStringFunctions()
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getSystemFunctions()
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getTimeDateFunctions()
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getSQLKeywords()
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

	/* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getBestRowIdentifier(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
     */
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

	/* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getColumnPrivileges(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
     * 
     * @deprecated use getColumnPrivilegesDataSet instead. 
     */
    public ResultSet getColumnPrivileges(ITableInfo ti)
		throws SQLException
	{
		// MM-MYSQL driver doesnt support null for column name.
        final String columns = DialectFactory.isMySQL(this) ? "%" : null;
		return privateGetJDBCMetaData().getColumnPrivileges(ti.getCatalogName(),
													ti.getSchemaName(),
													ti.getSimpleName(),
													columns);
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getColumnPrivilegesDataSet(net.sourceforge.squirrel_sql.fw.sql.ITableInfo, int[], boolean)
     */
    public synchronized IDataSet getColumnPrivilegesDataSet(ITableInfo ti,
                                                            int[] columnIndices,
                                                            boolean computeWidths) 
        throws DataSetException 
    {
        ResultSet rs = null;
        try {
            DatabaseMetaData md = privateGetJDBCMetaData();
            final String columns = DialectFactory.isMySQL(this) ? "%" : null;
            
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
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getExportedKeys(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
     * 
     * @deprecated. Replaced by getExportedKeysInfo 
     */
    public ResultSet getExportedKeys(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getExportedKeys(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName());
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getExportedKeysDataSet(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
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
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getImportedKeys(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
     * 
     * @deprecated. Replaced by getImportedKeysInfo
     */
	public ResultSet getImportedKeys(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getImportedKeys(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName());
	}

	/* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getImportedKeysInfo(java.lang.String, java.lang.String, java.lang.String)
     */
	public synchronized ForeignKeyInfo[] getImportedKeysInfo(String catalog, 
                                                             String schema, 
                                                             String tableName) 
        throws SQLException 
    {
        ResultSet rs = 
            privateGetJDBCMetaData().getImportedKeys(catalog, schema, tableName);
        return getForeignKeyInfo(rs);
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getImportedKeysInfo(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
     */
    public synchronized ForeignKeyInfo[] getImportedKeysInfo(ITableInfo ti)
		throws SQLException
	{
		return getForeignKeyInfo(privateGetJDBCMetaData().getImportedKeys(ti.getCatalogName(),
								ti.getSchemaName(), ti.getSimpleName()));
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getImportedKeysDataSet(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
     */
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
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getExportedKeysInfo(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized ForeignKeyInfo[] getExportedKeysInfo(String catalog, String schema, String tableName)
        throws SQLException
    {
        ResultSet rs = 
            privateGetJDBCMetaData().getExportedKeys(catalog, schema, tableName);
        return getForeignKeyInfo(rs);
    } 
    
	/* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getExportedKeysInfo(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
     */
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
         close(rs);
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getIndexInfo(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
     * 
     * @deprecated use getIndexInfo instead.
     */
    public ResultSet getIndexInfo(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getIndexInfo(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName(), false, true);
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getIndexInfo(net.sourceforge.squirrel_sql.fw.sql.ITableInfo, int[], boolean)
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
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getPrimaryKeys(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
     * 
     * @deprecated use getPrimaryKey instead
     */
	public ResultSet getPrimaryKeys(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getPrimaryKeys(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName());
	}
    
	/* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getPrimaryKey(net.sourceforge.squirrel_sql.fw.sql.ITableInfo, int[], boolean)
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getPrimaryKey(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
     */
    public synchronized PrimaryKeyInfo[] getPrimaryKey(ITableInfo ti) 
        throws SQLException
    {
        return getPrimaryKey(ti.getCatalogName(), 
                             ti.getSchemaName(), 
                             ti.getSimpleName());
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getPrimaryKey(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized PrimaryKeyInfo[] getPrimaryKey(String catalog, 
                                                       String schema, 
                                                       String table) 
        throws SQLException
    {
        final List<PrimaryKeyInfo> results = new ArrayList<PrimaryKeyInfo>();
        ResultSet rs = null;
        try {
            rs = privateGetJDBCMetaData().getPrimaryKeys(catalog, schema,table);
            while (rs.next()) {
                PrimaryKeyInfo pkInfo = 
                    new PrimaryKeyInfo(rs.getString(1),  // catalog
                                       rs.getString(2),  // schema
                                       rs.getString(3),  // tableName
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
        return results.toArray(ar);
    }
    
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getProcedureColumns(net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo)
     * 
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getProcedureColumnsDataSet(net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo)
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
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getTablePrivileges(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
     * 
     * @deprecated use getTablePrivilegesDataSet instead
     */ 
	public ResultSet getTablePrivileges(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getTablePrivileges(ti.getCatalogName(),
													ti.getSchemaName(),
													ti.getSimpleName());
	}

	/* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getTablePrivilegesDataSet(net.sourceforge.squirrel_sql.fw.sql.ITableInfo, int[], boolean)
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
    
    
	/* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getVersionColumns(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
     * 
     * @deprecated use getVersionColumnsDataSet instead
     */
	public ResultSet getVersionColumns(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getVersionColumns(ti.getCatalogName(),
												          ti.getSchemaName(),
												          ti.getSimpleName());
	}

	/* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getVersionColumnsDataSet(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
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
    
    
	private ResultSet getColumns(ITableInfo ti)
		throws SQLException
	{
		return privateGetJDBCMetaData().getColumns(ti.getCatalogName(),
											ti.getSchemaName(),
											ti.getSimpleName(), "%");
	}
    
	/* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getColumns(net.sourceforge.squirrel_sql.fw.sql.ITableInfo, int[], boolean)
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
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getColumnInfo(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized TableColumnInfo[] getColumnInfo(String catalog, 
                                                        String schema, 
                                                        String table) 
        throws SQLException 
    {
       ResultSet rs = null;
       try
       {
          final Map columns = new TreeMap();
          DatabaseMetaData md = privateGetJDBCMetaData();
          rs = md.getColumns(catalog, schema, table, "%");
          final ResultSetColumnReader rdr = new ResultSetColumnReader(rs);

          int index = 0;
          while (rdr.next())
          {
             final TableColumnInfo tci = 
                 new TableColumnInfo(rdr.getString(1),           // TABLE_CAT
                                     rdr.getString(2),           // TABLE_SCHEM
                                     rdr.getString(3),           // TABLE_NAME
                                     rdr.getString(4),           // COLUMN_NAME
                                     rdr.getLong(5).intValue(),  // DATA_TYPE
                                     rdr.getString(6),           // TYPE_NAME
                                     rdr.getLong(7).intValue(),  // COLUMN_SIZE
                                     rdr.getLong(9).intValue(),  // DECIMAL_DIGITS
                                     rdr.getLong(10).intValue(), // NUM_PREC_RADIX
                                     rdr.getLong(11).intValue(), // NULLABLE
                                     rdr.getString(12),          // REMARKS
                                     rdr.getString(13),          // COLUMN_DEF
                                     rdr.getLong(16).intValue(), // CHAR_OCTET_LENGTH
                                     rdr.getLong(17).intValue(), // ORDINAL_POSITION
                                     rdr.getString(18),          // IS_NULLABLE
                                     this);
             ////////////////////////////////////////////////////////////////////////////////////////////
             // The index is needed in case this method is called with schema = null, catalog = null
             // and two tables with the same name in different schemas/catalogs.
             // Without the index the same ordinal position could only occur once.
             ++index;
             //
             ////////////////////////////////////////////////////////////////////////////////////////////
             columns.put(new Integer(10000 * tci.getOrdinalPosition()  + index), tci);
          }

          return (TableColumnInfo[]) columns.values().toArray(new TableColumnInfo[columns.size()]);

       }
       finally
       {
          if (rs != null) try
          {
             rs.close();
          }
          catch (SQLException e){}
       }
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#getColumnInfo(net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
     */
    public synchronized TableColumnInfo[] getColumnInfo(ITableInfo ti)
		throws SQLException
	{
	    return getColumnInfo(ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName());
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#correctlySupportsSetMaxRows()
     */
    public boolean correctlySupportsSetMaxRows() throws SQLException
	{
		return !IDriverNames.OPTA2000.equals(getDriverName());
	}

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#supportsMultipleResultSets()
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

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#storesUpperCaseIdentifiers()
     */
	public synchronized boolean storesUpperCaseIdentifiers()
		throws SQLException
	{
		final String key = "storesUpperCaseIdentifiers";
		Boolean value = (Boolean)_cache.get(key);
		if (value != null)
		{
			return value.booleanValue();
		}

		value = new Boolean(privateGetJDBCMetaData().storesUpperCaseIdentifiers());
		_cache.put(key, value);

		return value.booleanValue();
	}


    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData2#clearCache()
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
        /* This is extremely useful when trying to track down Swing UI freezing.
         * However, it currently fills the log which obscures other debug 
         * messages even though UI performance is acceptable, so it is commented 
         * out until it is needed later. 
        if (s_log.isDebugEnabled() && SwingUtilities.isEventDispatchThread()) {
            try {
                throw new Exception("GUI Thread is doing database work");
            } catch (Exception e) {
                s_log.debug(e.getMessage(), e);
            }
        }
        */
    }
}

