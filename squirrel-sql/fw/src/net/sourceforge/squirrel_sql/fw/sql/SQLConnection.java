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
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SQLConnection
{
	private interface DriverNames
	{
		String FREE_TDS = "InternetCDS Type 4 JDBC driver for MS SQLServer";
		String JCONNECT = "jConnect (TM) for JDBC (TM)";
	}

	private ILogger s_log = LoggerController.createLogger(SQLConnection.class);

	private String _url;
	private Connection _conn;
	private DatabaseMetaData _md;

	/** MetaData for this connection. */
	private SQLDatabaseMetaData _metaData;

	private String _dbProductName;
	private String _dbDriverName;

	private boolean _autoCommitOnClose = false;

	public SQLConnection(String url) throws SQLException
	{
		this(null, url);
	}

	public SQLConnection(String className, String url) throws SQLException
	{
		super();
		_url = url;
		if (className != null)
		{
			try
			{
				Class.forName(className);
			}
			catch (ClassNotFoundException ex)
			{
				throw new SQLException("JDBC Driver class not found: " + className);
			}
		}
	}

	public SQLConnection(Connection conn) throws SQLException
	{
		super();
		_conn = conn;
		loadMetaData();
		_url = _md.getURL();
	}

	public void close() throws SQLException
	{
		SQLException savedEx = null;
		if (_conn != null)
		{
			s_log.debug("Closing connection");
			try
			{
				if (!_conn.getAutoCommit())
				{
					if (_autoCommitOnClose)
					{
						_conn.commit();
					}
					else
					{
						_conn.rollback();
					}
				}
			}
			catch (SQLException ex)
			{
				savedEx = ex;
			}
			_conn.close();
			_conn = null;
			_md = null;

			if (savedEx != null)
			{
				s_log.debug("Connection close failed", savedEx);
				throw savedEx;
			}
			s_log.debug("Connection closed successfully");
		}
	}

	public boolean isConnected()
	{
		return _conn != null;
	}

	public void commit() throws SQLException
	{
		validateConnection();
		_conn.commit();
	}

	public void rollback() throws SQLException
	{
		validateConnection();
		_conn.rollback();
	}

	public boolean getAutoCommit() throws SQLException
	{
		validateConnection();
		return _conn.getAutoCommit();
	}

	public void setAutoCommit(boolean value) throws SQLException
	{
		validateConnection();
		_conn.setAutoCommit(value);
	}

	public boolean getCommitOnClose()
	{
		return _autoCommitOnClose;
	}

	public void setCommitOnClose(boolean value)
	{
		_autoCommitOnClose = value;
	}

	public Statement createStatement() throws SQLException
	{
		validateConnection();
		return _conn.createStatement();
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException
	{
		validateConnection();
		return _conn.prepareStatement(sql);
	}

	/**
	 * Retrieve the metadata for this connection.
	 * 
	 * @return	The <TT>SQLMetaData</TT> object.
	 */
	public synchronized SQLDatabaseMetaData getSQLMetaData()
	{
		if (_metaData == null)
		{
			_metaData = new SQLDatabaseMetaData(this);
		}
		return _metaData;
	}

	public ResultSet getBestRowIdentifier(ITableInfo ti)
		throws SQLException
	{
		return getMetaData().getBestRowIdentifier(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName(), DatabaseMetaData.bestRowSession,
			true);
	}

	public String getCatalog() throws SQLException
	{
		validateConnection();
		return getConnection().getCatalog();
	}

	public ResultSet getColumnPrivileges(ITableInfo ti)
		throws SQLException
	{
		return getMetaData().getColumnPrivileges(ti.getCatalogName(),
													ti.getSchemaName(),
													ti.getSimpleName(),
													null);
	}

	public ResultSet getColumns(ITableInfo ti)
		throws SQLException
	{
		return getMetaData().getColumns(ti.getCatalogName(),
											ti.getSchemaName(),
											ti.getSimpleName(), "%");
	}

	public ResultSet getExportedKeys(ITableInfo ti)
		throws SQLException
	{
		return getMetaData().getExportedKeys(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName());
	}

	public String getIdentifierQuoteString()
		throws SQLException
	{
		if (_dbDriverName.equals(DriverNames.FREE_TDS)
			|| _dbDriverName.equals(DriverNames.JCONNECT))
		{
			return "";
		}
		return getMetaData().getIdentifierQuoteString();
	}

	public ResultSet getImportedKeys(ITableInfo ti)
		throws SQLException
	{
		return getMetaData().getImportedKeys(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName());
	}

	public ResultSet getIndexInfo(ITableInfo ti)
		throws SQLException
	{
		return getMetaData().getIndexInfo(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName(), false, true);
	}

	public ResultSet getPrimaryKeys(ITableInfo ti)
		throws SQLException
	{
		return getMetaData().getPrimaryKeys(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName());
	}

	public IProcedureInfo[] getProcedures(String catalog,
				String schemaPattern, String procedureNamePattern)
		throws SQLException
	{
		DatabaseMetaData md = getMetaData();
		ArrayList list = new ArrayList();
		ResultSet rs = md.getProcedures(catalog, schemaPattern, procedureNamePattern);
		while (rs.next())
		{
			list.add(new ProcedureInfo(rs, this));
		}
		return (IProcedureInfo[]) list.toArray(new IProcedureInfo[list.size()]);
	}

	public ResultSet getProcedureColumns(IProcedureInfo ti)
		throws SQLException
	{
		return getMetaData().getProcedureColumns(ti.getCatalogName(),
													ti.getSchemaName(),
													ti.getSimpleName(),
													"%");
	}

	public String[] getSchemas() throws SQLException
	{
		DatabaseMetaData md = getMetaData();
		ArrayList list = new ArrayList();
		ResultSet rs = md.getSchemas();
		while (rs.next())
		{
			list.add(rs.getString(1));
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	public ResultSet getTablePrivileges(ITableInfo ti)
		throws SQLException
	{
		return getMetaData().getTablePrivileges(ti.getCatalogName(),
													ti.getSchemaName(),
													ti.getSimpleName());
	}

	public ITableInfo[] getTables(String catalog, String schemaPattern,
									String tableNamePattern, String[] types)
		throws SQLException
	{
		DatabaseMetaData md = getMetaData();
		Set list = new TreeSet();
		if (_dbDriverName.equals(DriverNames.FREE_TDS) && schemaPattern == null)
		{
			schemaPattern = "dbo";
		}

		ResultSet tabResult = md.getTables(catalog, schemaPattern,
											tableNamePattern, types);
		ResultSet superTabResult = null;
		Map nameMap = null;
		try
		{
			//				superTabResult = md.getSuperTables(catalog, schemaPattern,
			//												   tableNamePattern);
			Class clazz = md.getClass();
			Class[] p1 = new Class[] {String.class, String.class, String.class};
			Method method = clazz.getMethod("getSuperTables", p1);
			if (method != null)
			{
				Object[] p2 = new Object[] {catalog, schemaPattern, tableNamePattern};
				superTabResult = (ResultSet)method.invoke(md, p2);
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
			ITableInfo tabInfo = new TableInfo(tabResult, this);
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
		return (ITableInfo[]) list.toArray(new ITableInfo[list.size()]);
	}

	public String[] getTableTypes() throws SQLException
	{
		DatabaseMetaData md = getMetaData();

		// Use a set rather than a list as some combinations of MS SQL and the
		// JDBC/ODBC return multiple copies of each table type.
		Set tableTypes = new TreeSet();
		ResultSet rs = md.getTableTypes();
		while (rs.next())
		{
			tableTypes.add(rs.getString(1).trim());
		}

		final int nbrTableTypes = tableTypes.size();

		// InstantDB (at least version 3.13) only returns "TABLES"
		// for getTableTypes(). If you try to use this in a call to
		// DatabaseMetaData.getTables() no tables will be found. For the
		// moment hard code the types for InstantDB.
		if (nbrTableTypes == 1 && _dbProductName.equals("InstantDB"))
		{
			tableTypes.clear();
			tableTypes.add("TABLE");
			tableTypes.add("SYSTEM TABLE");
		}

		// At least one version of PostgreSQL through the ODBC/JDBC
		// bridge returns an empty result set for the list of table
		// types. Another version of PostgreSQL returns 6 entries
		// of "SYSTEM TABLE (which we have already filtered back to one).
		else if (_dbProductName.equals("PostgreSQL"))
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

	public ResultSet getTypeInfo() throws SQLException
	{
		return getMetaData().getTypeInfo();
	}

	public IUDTInfo[] getUDTs(String catalog, String schemaPattern,
								String typeNamePattern, int[] types)
		throws SQLException
	{
		DatabaseMetaData md = getMetaData();
		ArrayList list = new ArrayList();
		ResultSet rs = md.getUDTs(catalog, schemaPattern, typeNamePattern, types);
		while (rs.next())
		{
			list.add(new UDTInfo(rs, this));
		}
		return (IUDTInfo[]) list.toArray(new IUDTInfo[list.size()]);
	}

	public String getUserName() throws SQLException
	{
		return getMetaData().getUserName();
	}

	public ResultSet getVersionColumns(ITableInfo ti)
		throws SQLException
	{
		return getMetaData().getVersionColumns(ti.getCatalogName(),
												ti.getSchemaName(),
												ti.getSimpleName());
	}

	public void setCatalog(String catalogName)
		throws SQLException
	{
		validateConnection();
		getConnection().setCatalog(catalogName);
	}

	public boolean supportsSchemas() throws SQLException
	{
		return supportsSchemasInDataManipulation()
			|| supportsSchemasInTableDefinitions();
	}

	public boolean supportsSchemasInDataManipulation()
		throws SQLException
	{
		try
		{
			return getMetaData().supportsSchemasInDataManipulation();
		}
		catch (SQLException ex)
		{
			if (_dbDriverName.equals(DriverNames.FREE_TDS))
			{
				return true;
			}
			throw ex;
		}
	}

	public boolean supportsSchemasInTableDefinitions()
		throws SQLException
	{
		try
		{
			return getMetaData().supportsSchemasInTableDefinitions();
		}
		catch (SQLException ex)
		{
			if (_dbDriverName.equals(DriverNames.FREE_TDS))
			{
				return true;
			}
			throw ex;
		}
	}

	public boolean supportsStoredProcedures()
		throws SQLException
	{
		return getMetaData().supportsStoredProcedures();
	}

	public SQLWarning getWarnings() throws SQLException
	{
		validateConnection();
		return _conn.getWarnings();
	}

	public MetaDataDataSet createMetaDataDataSet(IMessageHandler msgHandler)
		throws SQLException, DataSetException
	{
		return new MetaDataDataSet(getMetaData(), msgHandler);
	}

	public MetaDataListDataSet createNumericFunctionsDataSet(IMessageHandler msgHandler)
		throws SQLException, DataSetException
	{
		DatabaseMetaData md = getMetaData();
		String functionList = null;
		if (md != null)
		{
			functionList = md.getNumericFunctions();
		}
		return new MetaDataListDataSet(functionList, msgHandler);
	}

	public MetaDataListDataSet createStringFunctionsDataSet(IMessageHandler msgHandler)
		throws SQLException, DataSetException
	{
		DatabaseMetaData md = getMetaData();
		String functionList = null;
		if (md != null)
		{
			functionList = md.getStringFunctions();
		}
		return new MetaDataListDataSet(functionList, msgHandler);
	}

	public MetaDataListDataSet createSystemFunctionsDataSet(IMessageHandler msgHandler)
		throws SQLException, DataSetException
	{
		DatabaseMetaData md = getMetaData();
		String functionList = null;
		if (md != null)
		{
			functionList = md.getSystemFunctions();
		}
		return new MetaDataListDataSet(functionList, msgHandler);
	}

	public MetaDataListDataSet createDateTimeFunctionsDataSet(IMessageHandler msgHandler)
		throws SQLException, DataSetException
	{
		DatabaseMetaData md = getMetaData();
		String functionList = null;
		if (md != null)
		{
			functionList = md.getTimeDateFunctions();
		}
		return new MetaDataListDataSet(functionList, msgHandler);
	}

	public MetaDataListDataSet createSQLKeywordsDataSet(IMessageHandler msgHandler)
		throws SQLException, DataSetException
	{
		DatabaseMetaData md = getMetaData();
		String keywordList = null;
		if (md != null)
		{
			keywordList = md.getSQLKeywords();
		}
		return new MetaDataListDataSet(keywordList, msgHandler);
	}

	public DatabaseMetaData getMetaData() throws SQLException
	{
		validateConnection();
		return _md;
	}

	public Connection getConnection()
	{
		return _conn;
	}

	protected void validateConnection() throws SQLException
	{
		if (_conn == null)
		{
			throw new SQLException("No connection");
		}
	}

	private void loadMetaData() throws SQLException
	{
		_md = getConnection().getMetaData();
		try
		{
			_dbProductName = _md.getDatabaseProductName();
		}
		catch (SQLException ignore)
		{
			_dbProductName = "";
		}
		try
		{
			_dbDriverName = _md.getDriverName().trim();
		}
		catch (SQLException ignore)
		{
			_dbDriverName = "";
		}
	}
}