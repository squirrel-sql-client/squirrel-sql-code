package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
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
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SchemaInfo
{
	private boolean _loading = false;
	private boolean _loaded = false;

	private DatabaseMetaData _dmd;
	private final HashMap _keywords = new HashMap();
	private final HashMap _dataTypes = new HashMap();
	private final List _functions = new ArrayList();
	private final HashMap _tables = new HashMap();
	private final HashMap _columns = new HashMap();
	private Hashtable _extendedColumnInfosByTableName = new Hashtable();
	private HashMap _tablesLoadingColsInBackground = new HashMap();
	private final List _catalogs = new ArrayList();
	private final List _schemas = new ArrayList();
	private final List _extendedtableInfos = new ArrayList();
	private ISession _session = null;
	private IProcedureInfo[] _procInfos = new IProcedureInfo[0];


	/** Logger for this class. */
	private static final ILogger s_log =
				LoggerController.createLogger(SchemaInfo.class);

	public SchemaInfo(ISession session)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("Session == null");
		}
		_session=session;
	}

	public SchemaInfo(SQLConnection conn, ISession session)
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}
		if (session == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}
		_session=session;
		load(conn);
	}

	public void load(SQLConnection conn)
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}

		_loading = true;
		try
		{
			final SQLDatabaseMetaData sqlDmd = conn.getSQLMetaData();

			_dmd = null;
			try
			{
				_dmd = sqlDmd.getJDBCMetaData();
			}
			catch (Exception ex)
			{
				s_log.error("Error retrieving metadata", ex);
			}

			try
			{
				s_log.debug("Loading keywords");
				loadKeywords(_dmd);
				s_log.debug("Keywords loaded");
			}
			catch (Exception ex)
			{
				s_log.error("Error loading keywords", ex);
			}

			try
			{
					 s_log.debug("Loading data types");
				loadDataTypes(_dmd);
					 s_log.debug("Data types loaded");
			}
			catch (Exception ex)
			{
				s_log.error("Error loading data types", ex);
			}

			try
			{
				s_log.debug("Loading functions");
				loadFunctions(_dmd);
				s_log.debug("Functions loaded");
			}
			catch (Exception ex)
			{
				s_log.error("Error loading functions", ex);
			}

			try
			{
				s_log.debug("Loading catalogs");
				loadCatalogs(_dmd);
				s_log.debug("Catalogs loaded");
			}
			catch (Exception ex)
			{
				s_log.error("Error loading catalogs", ex);
			}

			try
			{
				s_log.debug("Loading schemas");
				loadSchemas(_dmd);
				s_log.debug("Schemas loaded");
			}
			catch (Exception ex)
			{
				s_log.error("Error loading schemas", ex);
			}


			try
			{
				s_log.debug("Loading tables");
				loadTables(_dmd);
				s_log.debug("Tables loaded");
			}
			catch (Exception ex)
			{
				s_log.error("Error loading tables", ex);
			}

			try
			{
				s_log.debug("Loading stored procedures");
				loadStoredProcedures(sqlDmd);
				s_log.debug("stored procedures loaded");
			}
			catch (Exception ex)
			{
				s_log.error("Error loading stored procedures", ex);
			}
		}
		finally
		{
			_loading = false;
			_loaded = true;
		}
	}

	private void loadStoredProcedures(SQLDatabaseMetaData dmd)
	{

		final String objFilter = _session.getProperties().getObjectFilter();
		try
		{
			s_log.debug("Loading stored procedures with filter "+objFilter);
			_procInfos = dmd.getProcedures(null, null,objFilter != null && objFilter.length() > 0 ? objFilter :"%");
		}
		catch (Throwable th)
		{
			s_log.error("Failed to load stored procedures", th);
		}

	}

	private void loadCatalogs(DatabaseMetaData dmd)
	{
		try
		{
			final ResultSet rs = dmd.getCatalogs();
			try
			{
				while(rs.next())
				{
					_catalogs.add(rs.getString("TABLE_CAT"));
				}
			}
			finally
			{
				rs.close();
			}
		}
		catch (Throwable th)
		{
			s_log.error("failed to load catalog names", th);
		}
	}

	private void loadSchemas(DatabaseMetaData dmd)
	{
		try
		{
			final ResultSet rs = dmd.getSchemas();
			try
			{
				while(rs.next())
				{
					_schemas.add(rs.getString("TABLE_SCHEM"));
				}
			}
			finally
			{
				rs.close();
			}
		}
		catch (Throwable th)
		{
			s_log.error("failed to load schema names", th);
		}
	}

	/**
	 * Retrieve whether the passed string is a keyword.
	 *
	 * @param	keyword		String to check.
	 *
	 * @return	<TT>true</TT> if a keyword.
	 */
	public boolean isKeyword(String data)
	{
		if (!_loading && data != null)
		{
			return _keywords.containsKey(data.toUpperCase());
		}
		return false;
	}

	/**
	 * Retrieve whether the passed string is a data type.
	 *
	 * @param	keyword		String to check.
	 *
	 * @return	<TT>true</TT> if a data type.
	 */
	public boolean isDataType(String data)
	{
		if (!_loading && data != null)
		{
			return _dataTypes.containsKey(data.toUpperCase());
		}
		return false;
	}

	/**
	 * Retrieve whether the passed string is a function.
	 *
	 * @param	keyword		String to check.
	 *
	 * @return	<TT>true</TT> if a function.
	 */
	public boolean isFunction(String data)
	{
		if (!_loading && data != null)
		{
			return _functions.contains(data.toUpperCase());
		}
		return false;
	}

	/**
	 * Retrieve whether the passed string is a table.
	 *
	 * @param	keyword		String to check.
	 *
	 * @return	<TT>true</TT> if a table.
	 */
	public boolean isTable(String data)
	{
		if (!_loading && data != null)
		{
			String ucData = data.toUpperCase();
			if(_tables.containsKey(ucData))
			{
				loadColumns(ucData);
				return true;
			}
		}
		return false;
	}

	/**
	 * This method returns the case sensitive name of a table as it is stored
	 * in the database.
	 * The case sensitive name is needed for example if you want to retrieve
	 * a table's meta data. Quote from the API doc of DataBaseMetaData.getTables():
	 * Parameters:
	 * ...
	 * tableNamePattern - a table name pattern; must match the table name as it is stored in the database
	 *
	 *
	 * @param data The tables name in arbitrary case.
	 * @return the table name as it is stored in the database
	 */
	public String getCaseSensitiveTableName(String data)
	{
		if (!_loading && data != null)
		{
			String ucData = data.toUpperCase();
			return (String) _tables.get(ucData);
		}
		return null;
	}


	/**
	 * Retrieve whether the passed string is a column.
	 *
	 * @param	keyword		String to check.
	 *
	 * @return	<TT>true</TT> if a column.
	 */
	public boolean isColumn(String data)
	{
		if (!_loading && data != null)
		{
			return _columns.containsKey(data.toUpperCase());
		}
		return false;
	}

	private void loadKeywords(DatabaseMetaData dmd)
	{
		try
		{
			_keywords.put("ABSOLUTE", "DUMMY");
			_keywords.put("ACTION", "DUMMY");
			_keywords.put("ADD", "DUMMY");
			_keywords.put("ALL", "DUMMY");
			_keywords.put("ALTER", "DUMMY");
			_keywords.put("AND", "DUMMY");
			_keywords.put("AS", "DUMMY");
			_keywords.put("ASC", "DUMMY");
			_keywords.put("ASSERTION", "DUMMY");
			_keywords.put("AUTHORIZATION", "DUMMY");
			_keywords.put("AVG", "DUMMY");
			_keywords.put("BETWEEN", "DUMMY");
			_keywords.put("BY", "DUMMY");
			_keywords.put("CASCADE", "DUMMY");
			_keywords.put("CASCADED", "DUMMY");
			_keywords.put("CATALOG", "DUMMY");
			_keywords.put("CHARACTER", "DUMMY");
			_keywords.put("CHECK", "DUMMY");
			_keywords.put("COLLATE", "DUMMY");
			_keywords.put("COLLATION", "DUMMY");
			_keywords.put("COLUMN", "DUMMY");
			_keywords.put("COMMIT", "DUMMY");
			_keywords.put("COMMITTED", "DUMMY");
			_keywords.put("CONNECT", "DUMMY");
			_keywords.put("CONNECTION", "DUMMY");
			_keywords.put("CONSTRAINT", "DUMMY");
			_keywords.put("COUNT", "DUMMY");
			_keywords.put("CORRESPONDING", "DUMMY");
			_keywords.put("CREATE", "DUMMY");
			_keywords.put("CROSS", "DUMMY");
			_keywords.put("CURRENT", "DUMMY");
			_keywords.put("CURSOR", "DUMMY");
			_keywords.put("DECLARE", "DUMMY");
			_keywords.put("DEFAULT", "DUMMY");
			_keywords.put("DEFERRABLE", "DUMMY");
			_keywords.put("DEFERRED", "DUMMY");
			_keywords.put("DELETE", "DUMMY");
			_keywords.put("DESC", "DUMMY");
			_keywords.put("DIAGNOSTICS", "DUMMY");
			_keywords.put("DISCONNECT", "DUMMY");
			_keywords.put("DISTINCT", "DUMMY");
			_keywords.put("DOMAIN", "DUMMY");
			_keywords.put("DROP", "DUMMY");
			_keywords.put("ESCAPE", "DUMMY");
			_keywords.put("EXCEPT", "DUMMY");
			_keywords.put("EXISTS", "DUMMY");
			_keywords.put("EXTERNAL", "DUMMY");
			_keywords.put("FALSE", "DUMMY");
			_keywords.put("FETCH", "DUMMY");
			_keywords.put("FIRST", "DUMMY");
			_keywords.put("FOREIGN", "DUMMY");
			_keywords.put("FROM", "DUMMY");
			_keywords.put("FULL", "DUMMY");
			_keywords.put("GET", "DUMMY");
			_keywords.put("GLOBAL", "DUMMY");
			_keywords.put("GRANT", "DUMMY");
			_keywords.put("GROUP", "DUMMY");
			_keywords.put("HAVING", "DUMMY");
			_keywords.put("IDENTITY", "DUMMY");
			_keywords.put("IMMEDIATE", "DUMMY");
			_keywords.put("IN", "DUMMY");
			_keywords.put("INITIALLY", "DUMMY");
			_keywords.put("INNER", "DUMMY");
			_keywords.put("INSENSITIVE", "DUMMY");
			_keywords.put("INSERT", "DUMMY");
			_keywords.put("INTERSECT", "DUMMY");
			_keywords.put("INTO", "DUMMY");
			_keywords.put("IS", "DUMMY");
			_keywords.put("ISOLATION", "DUMMY");
			_keywords.put("JOIN", "DUMMY");
			_keywords.put("KEY", "DUMMY");
			_keywords.put("LAST", "DUMMY");
			_keywords.put("LEFT", "DUMMY");
			_keywords.put("LEVEL", "DUMMY");
			_keywords.put("LIKE", "DUMMY");
			_keywords.put("LOCAL", "DUMMY");
			_keywords.put("MATCH", "DUMMY");
			_keywords.put("MAX", "DUMMY");
			_keywords.put("MIN", "DUMMY");
			_keywords.put("NAMES", "DUMMY");
			_keywords.put("NEXT", "DUMMY");
			_keywords.put("NO", "DUMMY");
			_keywords.put("NOT", "DUMMY");
			_keywords.put("NULL", "DUMMY");
			_keywords.put("OF", "DUMMY");
			_keywords.put("ON", "DUMMY");
			_keywords.put("ONLY", "DUMMY");
			_keywords.put("OPEN", "DUMMY");
			_keywords.put("OPTION", "DUMMY");
			_keywords.put("OR", "DUMMY");
			_keywords.put("ORDER", "DUMMY");
			_keywords.put("OUTER", "DUMMY");
			_keywords.put("OVERLAPS", "DUMMY");
			_keywords.put("PARTIAL", "DUMMY");
			_keywords.put("PRESERVE", "DUMMY");
			_keywords.put("PRIMARY", "DUMMY");
			_keywords.put("PRIOR", "DUMMY");
			_keywords.put("PRIVILIGES", "DUMMY");
			_keywords.put("PUBLIC", "DUMMY");
			_keywords.put("READ", "DUMMY");
			_keywords.put("REFERENCES", "DUMMY");
			_keywords.put("RELATIVE", "DUMMY");
			_keywords.put("REPEATABLE", "DUMMY");
			_keywords.put("RESTRICT", "DUMMY");
			_keywords.put("REVOKE", "DUMMY");
			_keywords.put("RIGHT", "DUMMY");
			_keywords.put("ROLLBACK", "DUMMY");
			_keywords.put("ROWS", "DUMMY");
			_keywords.put("SCHEMA", "DUMMY");
			_keywords.put("SCROLL", "DUMMY");
			_keywords.put("SELECT", "DUMMY");
			_keywords.put("SERIALIZABLE", "DUMMY");
			_keywords.put("SESSION", "DUMMY");
			_keywords.put("SET", "DUMMY");
			_keywords.put("SIZE", "DUMMY");
			_keywords.put("SOME", "DUMMY");
			_keywords.put("SUM", "DUMMY");
			_keywords.put("TABLE", "DUMMY");
			_keywords.put("TEMPORARY", "DUMMY");
			_keywords.put("THEN", "DUMMY");
			_keywords.put("TIME", "DUMMY");
			_keywords.put("TO", "DUMMY");
			_keywords.put("TRANSACTION", "DUMMY");
			_keywords.put("TRIGGER", "DUMMY");
			_keywords.put("TRUE", "DUMMY");
			_keywords.put("UNCOMMITTED", "DUMMY");
			_keywords.put("UNION", "DUMMY");
			_keywords.put("UNIQUE", "DUMMY");
			_keywords.put("UNKNOWN", "DUMMY");
			_keywords.put("UPDATE", "DUMMY");
			_keywords.put("USAGE", "DUMMY");
			_keywords.put("USER", "DUMMY");
			_keywords.put("USING", "DUMMY");
			_keywords.put("VALUES", "DUMMY");
			_keywords.put("VIEW", "DUMMY");
			_keywords.put("WHERE", "DUMMY");
			_keywords.put("WITH", "DUMMY");
			_keywords.put("WORK", "DUMMY");
			_keywords.put("WRITE", "DUMMY");
			_keywords.put("ZONE", "DUMMY");

			// Not actually in the std.
			_keywords.put("INDEX", "DUMMY");

			// Extra _keywords that this DBMS supports.
			if (dmd != null)
			{
				StringBuffer buf = new StringBuffer(1024);

				try
				{
					buf.append(dmd.getSQLKeywords());
				}
				catch (Throwable ex)
				{
					s_log.error("Error retrieving DBMS _keywords", ex);
				}

				StringTokenizer strtok = new StringTokenizer(buf.toString(), ",");

				while (strtok.hasMoreTokens())
				{
					_keywords.put(strtok.nextToken().trim(), "DUMMY");
				}

				try
				{
					addSingleKeyword(dmd.getCatalogTerm());
				}
				catch (Throwable ex)
				{
					s_log.error("Error", ex);
				}

				try
				{
					addSingleKeyword(dmd.getSchemaTerm());
				}
				catch (Throwable ex)
				{
					s_log.error("Error", ex);
				}

				try
				{
					addSingleKeyword(dmd.getProcedureTerm());
				}
				catch (Throwable ex)
				{
					s_log.error("Error", ex);
				}
			}
		}
		catch (Throwable ex)
		{
			s_log.error("Error occured creating keyword collection", ex);
		}
	}

	private void loadDataTypes(DatabaseMetaData dmd)
	{
		try
		{
			final ResultSet rs = dmd.getTypeInfo();
			try
			{
				while (rs.next())
				{
					String typeName = rs.getString(1).trim();
					_dataTypes.put(typeName.toUpperCase(), typeName);
				}
			}
			finally
			{
				rs.close();
			}
		}
		catch (Throwable ex)
		{
			s_log.error("Error occured creating data types collection", ex);
		}
	}

	private void loadFunctions(DatabaseMetaData dmd)
	{
		StringBuffer buf = new StringBuffer(1024);

		try
		{
			buf.append(dmd.getNumericFunctions());
		}
		catch (Throwable ex)
		{
			s_log.error("Error", ex);
		}

		buf.append(",");

		try
		{
			buf.append(dmd.getStringFunctions());
		}
		catch (Throwable ex)
		{
			s_log.error("Error", ex);
		}

		buf.append(",");

		try
		{
			buf.append(dmd.getTimeDateFunctions());
		}
		catch (Throwable ex)
		{
			s_log.error("Error", ex);
		}

		StringTokenizer strtok = new StringTokenizer(buf.toString(), ",");

		while (strtok.hasMoreTokens())
		{
			final String func = strtok.nextToken().trim();
			if (func.length() > 0)
			{
				_functions.add(func.toUpperCase());
			}
		}
	}


	private void addSingleKeyword(String keyword)
	{
		if (keyword != null)
		{
			keyword = keyword.trim();

			if (keyword.length() > 0)
			{
				_keywords.put(keyword.toUpperCase(), "DUMMY");
			}
		}
	}

	public String[] getKeywords()
	{
		return (String[]) _keywords.keySet().toArray(new String[_keywords.size()]);
	}

	public String[] getDataTypes()
	{
		return (String[]) _dataTypes.values().toArray(new String[_dataTypes.size()]);
	}

	public String[] getFunctions()
	{
		return (String[]) _functions.toArray(new String[_functions.size()]);
	}

	public String[] getTables()
	{
		return (String[]) _tables.values().toArray(new String[_tables.size()]);
	}

	public String[] getCatalogs()
	{
		return (String[]) _catalogs.toArray(new String[_catalogs.size()]);
	}

	public String[] getSchemas()
	{
		return (String[]) _schemas.toArray(new String[_schemas.size()]);
	}

	public ExtendedTableInfo[] getExtendedTableInfos()
	{
		return getExtendedTableInfos(null, null);
	}

	public ExtendedTableInfo[] getExtendedTableInfos(String catalog, String schema)
	{
		if(null == catalog && null == schema)
		{
			return (ExtendedTableInfo[]) _extendedtableInfos.toArray(new ExtendedTableInfo[_extendedtableInfos.size()]);
		}
		else
		{
			ArrayList ret = new ArrayList();

			for (int i = 0; i < _extendedtableInfos.size(); i++)
			{
				ExtendedTableInfo extendedTableInfo = (ExtendedTableInfo) _extendedtableInfos.get(i);
				boolean toAdd = true;
				if(null != catalog && false == catalog.equalsIgnoreCase(extendedTableInfo.getCatalog()) )
				{
					toAdd = false;
				}

				if(null != schema && false == schema.equalsIgnoreCase(extendedTableInfo.getSchema()) )
				{
					toAdd = false;
				}

				if(toAdd)
				{
					ret.add(extendedTableInfo);
				}
			}

			return (ExtendedTableInfo[]) ret.toArray(new ExtendedTableInfo[ret.size()]);
		}
	}


	public IProcedureInfo[] getStoredProceduresInfos()
	{
		return getStoredProceduresInfos(null, null);
	}

	public IProcedureInfo[] getStoredProceduresInfos(String catalog, String schema)
	{
		if(null == catalog && null == schema)
		{
			return _procInfos;
		}
		else
		{
			ArrayList ret = new ArrayList();

			for (int i = 0; i < _procInfos.length; i++)
			{
				boolean toAdd = true;
				if(null != catalog && false == catalog.equalsIgnoreCase(_procInfos[i].getCatalogName()) )
				{
					toAdd = false;
				}

				if(null != schema && false == schema.equalsIgnoreCase(_procInfos[i].getSchemaName()) )
				{
					toAdd = false;
				}

				if(toAdd)
				{
					ret.add(_procInfos[i]);
				}
			}

			return (IProcedureInfo[]) ret.toArray(new IProcedureInfo[ret.size()]);
		}
	}


	public boolean isLoaded()
	{
		return _loaded;
	}

	private void loadTables(DatabaseMetaData dmd)
	{
		try
		{
			// TODO: Use table types from meta data?
			final String[] tabTypes = new String[] { "TABLE", "VIEW" };
			final ResultSet rs = dmd.getTables(null, null, null, tabTypes);
			try
			{
				while (rs.next())
				{
					String tableName = rs.getString("TABLE_NAME");

					_tables.put(tableName.toUpperCase(), tableName);

					String tableType = rs.getString("TABLE_TYPE");

					String cat = rs.getString("TABLE_CAT");
					String schem = rs.getString("TABLE_SCHEM");

					_extendedtableInfos.add(new ExtendedTableInfo(tableName, tableType, cat, schem));
				}
			}
			finally
			{
				rs.close();
			}
		}
		catch (Throwable th)
		{
			s_log.error("failed to load table names", th);
		}
	}

	private void loadColumns(final String tableName)
	{
		try
		{
			if(_extendedColumnInfosByTableName.containsKey(tableName))
			{
				return;
			}


			if (_session.getProperties().getLoadColumnsInBackground())
			{
				if(_tablesLoadingColsInBackground.containsKey(tableName))
				{
					return;
				}

				_tablesLoadingColsInBackground.put(tableName, tableName);
				_session.getApplication().getThreadPool().addTask(new Runnable()
				{
					public void run()
					{
						try
						{
							accessDbToLoadColumns(tableName);
							_tablesLoadingColsInBackground.remove(tableName);
						}
						catch (SQLException e)
						{
							throw new RuntimeException(e);
						}
					}
				});
			}
			else
			{
				accessDbToLoadColumns(tableName);
			}
		}
		catch (Throwable th)
		{
			s_log.error("failed to load table names", th);
		}
	}

	private void accessDbToLoadColumns(String tableName)
		throws SQLException
	{
		ResultSet rs = _dmd.getColumns(null, null, getCaseSensitiveTableName(tableName), null);
		try
		{
			ArrayList infos = new ArrayList();

			while (rs.next())
			{
				String columnName = rs.getString("COLUMN_NAME");
				String columnType = rs.getString("TYPE_NAME");
				int columnSize = rs.getInt("COLUMN_SIZE");
				int decimalDigits = rs.getInt("DECIMAL_DIGITS");
				boolean nullable = "YES".equals(rs.getString("IS_NULLABLE"));
				String cat = rs.getString("TABLE_CAT");
				String schem = rs.getString("TABLE_SCHEM");
				ExtendedColumnInfo buf = new ExtendedColumnInfo(columnName, columnType, columnSize, decimalDigits, nullable, cat, schem);
				infos.add(buf);

				_columns.put(columnName.toUpperCase(), columnName);
			}
			_extendedColumnInfosByTableName.put(tableName, infos);

		}
		finally
		{
			rs.close();
		}
	}

	public ExtendedColumnInfo[] getExtendedColumnInfos(String tableName)
	{
		return getExtendedColumnInfos(null, null, tableName);
	}

	public ExtendedColumnInfo[] getExtendedColumnInfos(String catalog, String schema, String tableName)
	{
		String upperCaseTableName = tableName.toUpperCase();
		loadColumns(upperCaseTableName);
		ArrayList extColInfo = (ArrayList) _extendedColumnInfosByTableName.get(upperCaseTableName);

		if (null == extColInfo)
		{
			return new ExtendedColumnInfo[0];
		}

		if (null == catalog && null == schema)
		{
			return (ExtendedColumnInfo[]) extColInfo.toArray(new ExtendedColumnInfo[extColInfo.size()]);
		}
		else
		{
			ArrayList ret = new ArrayList();

			for (int i = 0; i < extColInfo.size(); i++)
			{
				ExtendedColumnInfo extendedColumnInfo = (ExtendedColumnInfo) extColInfo.get(i);
				boolean toAdd = true;
				if (null != catalog && false == catalog.equalsIgnoreCase(extendedColumnInfo.getCatalog()))
				{
					toAdd = false;
				}

				if (null != schema && false == schema.equalsIgnoreCase(extendedColumnInfo.getSchema()))
				{
					toAdd = false;
				}

				if (toAdd)
				{
					ret.add(extendedColumnInfo);
				}
			}

			return (ExtendedColumnInfo[]) ret.toArray(new ExtendedColumnInfo[ret.size()]);
		}
	}
}
