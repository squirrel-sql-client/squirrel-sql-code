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
//	private interface DriverNames
//	{
//		String FREE_TDS = "InternetCDS Type 4 JDBC driver for MS SQLServer";
//		String JCONNECT = "jConnect (TM) for JDBC (TM)";
//	}

	private final ILogger s_log = LoggerController.createLogger(SQLConnection.class);

	private final String _url;
	private Connection _conn;
	private DatabaseMetaData _md;

	/** MetaData for this connection. */
	private SQLDatabaseMetaData _metaData;

//	private String _dbProductName;
//	private String _dbDriverName;

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

//todo: get rid of this method.
	public DatabaseMetaData getMetaData() throws SQLException
	{
		validateConnection();
		return _md;
	}

	public Connection getConnection()
	{
		return _conn;
	}

	public String getCatalog() throws SQLException
	{
		validateConnection();
		return getConnection().getCatalog();
	}

	public void setCatalog(String catalogName)
		throws SQLException
	{
		validateConnection();
		getConnection().setCatalog(catalogName);
	}

	public SQLWarning getWarnings() throws SQLException
	{
		validateConnection();
		return _conn.getWarnings();
	}

	protected void validateConnection() throws SQLException
	{
		if (_conn == null)
		{
			throw new SQLException("No connection");
		}
	}


/////////////////////////////////////////////////////////////////////
// TODO: to be moved to metadata class///////////////////////////////
/////////////////////////////////////////////////////////////////////
	public ResultSet getBestRowIdentifier(ITableInfo ti)
		throws SQLException
	{
		return getMetaData().getBestRowIdentifier(
			ti.getCatalogName(), ti.getSchemaName(),
			ti.getSimpleName(), DatabaseMetaData.bestRowSession,
			true);
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

	public ResultSet getProcedureColumns(IProcedureInfo ti)
		throws SQLException
	{
		return getMetaData().getProcedureColumns(ti.getCatalogName(),
													ti.getSchemaName(),
													ti.getSimpleName(),
													"%");
	}

	public ResultSet getTablePrivileges(ITableInfo ti)
		throws SQLException
	{
		return getMetaData().getTablePrivileges(ti.getCatalogName(),
													ti.getSchemaName(),
													ti.getSimpleName());
	}


	public ResultSet getTypeInfo() throws SQLException
	{
		return getMetaData().getTypeInfo();
	}

	public ResultSet getVersionColumns(ITableInfo ti)
		throws SQLException
	{
		return getMetaData().getVersionColumns(ti.getCatalogName(),
												ti.getSchemaName(),
												ti.getSimpleName());
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

	private void loadMetaData() throws SQLException
	{
		_md = getConnection().getMetaData();
//		try
//		{
//			_dbProductName = _md.getDatabaseProductName();
//		}
//		catch (SQLException ignore)
//		{
//			_dbProductName = "";
//		}
//		try
//		{
//			_dbDriverName = _md.getDriverName().trim();
//		}
//		catch (SQLException ignore)
//		{
//			_dbDriverName = "";
//		}
	}
}