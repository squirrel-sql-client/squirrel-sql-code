/*
 * Copyright (C) 2005 Rob Manning
 * manningr@users.sourceforge.net
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

package net.sourceforge.squirrel_sql.plugins.dbdiff.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbdiff.I18NBaseObject;
import net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider;

import org.hibernate.MappingException;

/**
 * A utility class for interacting with the database.
 */
public class DBUtil extends I18NBaseObject
{

	/** Logger for this class. */
	private final static ILogger log = LoggerController.createLogger(DBUtil.class);

	/** Internationalized strings for this class */
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DBUtil.class);

	/** The last statement executed that we'll show to the user if error */
	private static String lastStatement = null;

	private static String lastStatementValues = null;

	/**
	 * Returns a string that looks like: (PK_COL1, PK_COL2, PK_COL3, ...) or null if there is no primary key
	 * for the specified table.
	 * 
	 * @param sourceConn
	 * @param ti
	 * @return
	 * @throws SQLException
	 */
	public static String getPKColumnString(ISQLConnection sourceConn, ITableInfo ti) throws SQLException
	{
		List<String> pkColumns = getPKColumnList(sourceConn, ti);
		if (pkColumns == null || pkColumns.size() == 0) { return null; }
		StringBuilder sb = new StringBuilder("(");
		Iterator<String> i = pkColumns.iterator();
		while (i.hasNext())
		{
			String columnName = i.next();
			sb.append(columnName);
			if (i.hasNext())
			{
				sb.append(", ");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Returns a list of primary keys or null if there are no primary keys for the specified table.
	 * 
	 * @param sourceConn
	 * @param ti
	 * @return
	 * @throws SQLException
	 */
	private static List<String> getPKColumnList(ISQLConnection sourceConn, ITableInfo ti) throws SQLException
	{
		ArrayList<String> pkColumns = new ArrayList<String>();
		DatabaseMetaData md = sourceConn.getConnection().getMetaData();
		ResultSet rs = null;
		if (md.supportsCatalogsInTableDefinitions())
		{
			rs = md.getPrimaryKeys(ti.getCatalogName(), null, ti.getSimpleName());
		}
		else if (md.supportsSchemasInTableDefinitions())
		{
			rs = md.getPrimaryKeys(null, ti.getSchemaName(), ti.getSimpleName());
		}
		else
		{
			rs = md.getPrimaryKeys(null, null, ti.getSimpleName());
		}
		while (rs.next())
		{
			String keyColumn = rs.getString(4);
			if (keyColumn != null)
			{
				pkColumns.add(keyColumn);
			}
		}
		if (pkColumns.size() == 0) { return null; }
		return pkColumns;
	}

	public static boolean tableHasForeignKey(String destCatalog, String destSchema, String destTableName,
		ForeignKeyInfo fkInfo, SessionInfoProvider prov)
	{
		boolean result = false;
		try
		{
			SQLDatabaseMetaData md = prov.getDiffDestSession().getSQLConnection().getSQLMetaData();

			ITableInfo[] tables =
				md.getTables(destCatalog, destSchema, destTableName, new String[] { "TABLE" }, null);
			if (tables != null && tables.length == 1)
			{
				ForeignKeyInfo[] fks = SQLUtilities.getImportedKeys(tables[0], md);
				for (ForeignKeyInfo existingKey : fks)
				{
					if (areEqual(existingKey, fkInfo))
					{
						result = true;
						break;
					}
				}
			}
			else
			{
				log.error("Couldn't find an exact match for destination table " + destTableName + " in schema "
					+ destSchema + " and catalog " + destCatalog + ". Skipping FK constraint");
			}
		}
		catch (SQLException e)
		{
			log.error("Unexpected exception while attempting to determine if " + "a table (" + destTableName
				+ ") has a particular foreign " + "key");
		}
		return result;
	}

	private static boolean areEqual(ForeignKeyInfo fk1, ForeignKeyInfo fk2)
	{
		String fk1FKColumn = fk1.getForeignKeyColumnName();
		String fk2FKColumn = fk2.getForeignKeyColumnName();
		String fk1PKColumn = fk1.getPrimaryKeyColumnName();
		String fk2PKColumn = fk2.getPrimaryKeyColumnName();
		String fk1FKTable = fk1.getForeignKeyTableName();
		String fk2FKTable = fk2.getForeignKeyTableName();
		String fk1PKTable = fk1.getPrimaryKeyTableName();
		String fk2PKTable = fk2.getPrimaryKeyTableName();

		if (!fk1PKColumn.equals(fk2PKColumn)) { return false; }
		if (!fk1FKColumn.equals(fk2FKColumn)) { return false; }
		if (!fk1PKTable.equals(fk2PKTable)) { return false; }
		if (!fk1FKTable.equals(fk2FKTable)) { return false; }
		return true;
	}

	public static boolean containsTable(List<ITableInfo> tableInfos, String table)
	{
		boolean result = false;
		for (ITableInfo ti : tableInfos)
		{
			if (table.equalsIgnoreCase(ti.getSimpleName()))
			{
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * Executes the specified sql statement on the specified connection and returns the ResultSet.
	 * 
	 * @param con
	 * @param sql
	 * @param mysqlBigResultFix
	 *           if true, provides a work-around which is useful in the case that the connection is to a MySQL
	 *           database. If the number of rows is large this will prevent the driver from reading them all
	 *           into client memory. MySQL's normal practice is to do such a thing for performance reasons.
	 * @return
	 * @throws Exception
	 */
	public static ResultSet executeQuery(ISession session, String sql) throws SQLException
	{
		ISQLConnection sqlcon = session.getSQLConnection();
		if (sqlcon == null || sql == null) { return null; }
		Statement stmt = null;
		ResultSet rs = null;

		Connection con = sqlcon.getConnection();
		try
		{
			stmt = con.createStatement();
		}
		catch (SQLException e)
		{
			// Only close the statement if SQLException - otherwise it has to
			// remain open until the ResultSet is read through by the caller.
			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (SQLException ex)
				{ /* Do Nothing */
				}
			}
			throw e;
		}
		if (log.isDebugEnabled())
		{
			// i18n[DBUtil.info.executequery=executeQuery: Running SQL:\n '{0}']
			String msg = s_stringMgr.getString("DBUtil.info.executequery", sql);
			log.debug(msg);
		}
		try
		{
			lastStatement = sql;
			rs = stmt.executeQuery(sql);
		}
		catch (SQLException e)
		{
			// Only close the statement if SQLException - otherwise it has to
			// remain open until the ResultSet is read through by the caller.
			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (SQLException ex)
				{ /* Do Nothing */
				}
			}
			throw e;
		}

		return rs;
	}

	/**
	 * Returns a count of the records in the specified table.
	 * 
	 * @param con
	 *           the SQLConnection to use to execute the count query.
	 * @param tableName
	 *           the name of the table. This name should already be qualified by the schema.
	 * @return -1 if the table does not exist, otherwise the record count is returned.
	 */
	private static int getTableCount(ISession session, String tableName)
	{
		int result = -1;
		ResultSet rs = null;
		try
		{
			String sql = "select count(*) from " + tableName;
			rs = executeQuery(session, sql);
			if (rs.next())
			{
				result = rs.getInt(1);
			}
		}
		catch (Exception e)
		{
			/* Do Nothing - this can happen when the table doesn't exist */
		}
		finally
		{
			SQLUtilities.closeResultSet(rs);
		}
		return result;
	}

	/**
	 * Returns a count of the records in the specified table.
	 * 
	 * @param con
	 *           the SQLConnection to use to execute the count query.
	 * @param tableName
	 *           the name of the table
	 * @return -1 if the table does not exist, otherwise the record count is returned.
	 */
	public static int getTableCount(ISession session, String catalog, String schema, String tableName,
		int sessionType) throws UserCancelledOperationException
	{
		String table = getQualifiedObjectName(session, catalog, schema, tableName, sessionType);
		return getTableCount(session, table);
	}

	public static ITableInfo getTableInfo(ISession session, String schema, String tableName)
		throws SQLException, MappingException, UserCancelledOperationException
	{
		ISQLConnection con = session.getSQLConnection();
		SchemaInfo schemaInfo = session.getSchemaInfo();
		// Currently, as of milestone 3, Axion doesn't support "schemas" like
		// other databases. So, set the schema to emtpy string if we detect
		// an Axion session.
		if (con.getSQLMetaData().getDriverName().toLowerCase().startsWith("axion"))
		{
			schema = "";
		}
		String catalog = null;
		// MySQL uses catalogs and not schemas
		if (DialectFactory.isMySQL(session.getMetaData()))
		{
			catalog = schema;
			schema = null;
		}
		// trim the table name in case of HADB
		tableName = tableName.trim();
		ITableInfo[] tis = schemaInfo.getITableInfos(catalog, schema, tableName);

		if (tis == null || tis.length == 0)
		{
			if (Character.isUpperCase(tableName.charAt(0)))
			{
				tableName = tableName.toLowerCase();
			}
			else
			{
				tableName = tableName.toUpperCase();
			}
			tis = schemaInfo.getITableInfos(null, schema, tableName);
			if (tis.length == 0)
			{
				if (Character.isUpperCase(tableName.charAt(0)))
				{
					tableName = tableName.toLowerCase();
				}
				else
				{
					tableName = tableName.toUpperCase();
				}
				tis = schemaInfo.getITableInfos(null, schema, tableName);
			}
		}
		if (tis.length == 0)
		{
			// i18n[DBUtil.error.tablenotfound=Couldn't locate table '{0}' in
			// schema '(1)']
			String msg = s_stringMgr.getString("DBUtil.error.tablenotfound", new String[] { tableName, schema });
			throw new MappingException(msg);
		}
		if (tis.length > 1)
		{
			if (log.isDebugEnabled())
			{
				log.debug("DBUtil.getTableInfo: found " + tis.length + " that matched " + "catalog=" + catalog
					+ " schema=" + schema + " tableName=" + tableName);
			}
		}
		return tis[0];
	}

	/**
	 * Takes the specified colInfo, gets the data type to see if it is 1111(OTHER). If so then get the type
	 * name and try to match a jdbc type with the same name to get it's type code.
	 * 
	 * @param colInfo
	 * @return
	 * @throws MappingException
	 */
	public static int replaceOtherDataType(TableColumnInfo colInfo) throws MappingException
	{
		int colJdbcType = colInfo.getDataType();
		if (colJdbcType == java.sql.Types.OTHER)
		{
			String typeName = colInfo.getTypeName().toUpperCase();
			int parenIndex = typeName.indexOf("(");
			if (parenIndex != -1)
			{
				typeName = typeName.substring(0, parenIndex);
			}
			colJdbcType = JDBCTypeMapper.getJdbcType(typeName);
			if (colJdbcType == Types.NULL) { throw new MappingException(
				"Encoutered jdbc type OTHER (1111) and couldn't map " + "the database-specific type name ("
					+ typeName + ") to a jdbc type"); }
		}
		return colJdbcType;
	}

	/**
	 * @param con
	 * @param synonym
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	public static int getColumnType(ISQLConnection con, ITableInfo ti, String columnName) throws SQLException
	{
		int result = -1;
		if (ti != null)
		{
			TableColumnInfo[] tciArr = con.getSQLMetaData().getColumnInfo(ti);
			for (int i = 0; i < tciArr.length; i++)
			{
				if (tciArr[i].getColumnName().equalsIgnoreCase(columnName))
				{
					result = tciArr[i].getDataType();
					break;
				}
			}
		}
		return result;
	}

	public static int[] getColumnTypes(ISQLConnection con, ITableInfo ti, String[] colNames)
		throws SQLException
	{
		TableColumnInfo[] tciArr = con.getSQLMetaData().getColumnInfo(ti);
		int[] result = new int[tciArr.length];
		for (int i = 0; i < tciArr.length; i++)
		{
			boolean found = false;
			for (int j = 0; j < colNames.length && !found; j++)
			{
				String columnName = colNames[j];
				if (tciArr[i].getColumnName().equalsIgnoreCase(columnName))
				{
					result[i] = tciArr[i].getDataType();
					found = true;
				}
			}
		}
		return result;
	}

	public static boolean tableHasPrimaryKey(ISQLConnection con, ITableInfo ti) throws SQLException
	{
		boolean result = false;
		ResultSet rs = null;
		try
		{
			DatabaseMetaData md = con.getConnection().getMetaData();
			String cat = ti.getCatalogName();
			String schema = ti.getSchemaName();
			String tableName = ti.getSimpleName();
			rs = md.getPrimaryKeys(cat, schema, tableName);
			if (rs.next())
			{
				result = true;
			}
		}
		finally
		{
			SQLUtilities.closeResultSet(rs);
		}
		return result;
	}

	/**
	 * @param con
	 * @param ti
	 * @return
	 * @throws SQLException
	 */
	public static String getColumnList(TableColumnInfo[] colInfoArr) throws SQLException
	{
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < colInfoArr.length; i++)
		{
			TableColumnInfo colInfo = colInfoArr[i];
			String columnName = colInfo.getColumnName();
			result.append(columnName);
			if (i < colInfoArr.length - 1)
			{
				result.append(", ");
			}
		}
		return result.toString();
	}

	/**
	 * @param sourceConn
	 * @param ti
	 * @param column
	 * @return
	 * @throws SQLException
	 */
	public static String getColumnName(ISQLConnection sourceConn, ITableInfo ti, int column)
		throws SQLException
	{
		TableColumnInfo[] infoArr = sourceConn.getSQLMetaData().getColumnInfo(ti);
		TableColumnInfo colInfo = infoArr[column];
		return colInfo.getColumnName();
	}

	/**
	 * @param sourceConn
	 * @param ti
	 * @return
	 * @throws SQLException
	 */
	public static String[] getColumnNames(ISQLConnection sourceConn, ITableInfo ti) throws SQLException
	{
		TableColumnInfo[] infoArr = sourceConn.getSQLMetaData().getColumnInfo(ti);
		String[] result = new String[infoArr.length];
		for (int i = 0; i < result.length; i++)
		{
			TableColumnInfo colInfo = infoArr[i];
			result[i] = colInfo.getColumnName();
		}
		return result;
	}

	/**
	 * @param columnList
	 * @param ti
	 * @return
	 * @throws SQLException
	 */
	public static String getSelectQuery(SessionInfoProvider prov, String columnList, ITableInfo ti)
		throws SQLException, UserCancelledOperationException
	{
		StringBuilder result = new StringBuilder("select ");
		result.append(columnList);
		result.append(" from ");
		ISession sourceSession = prov.getDiffSourceSession();

		// String sourceSchema = null;
		// MySQL uses catalogs instead of schemas
		/*
		if (DialectFactory.isMySQLSession(sourceSession)) {
		    if (log.isDebugEnabled()) {
		        String catalog = 
		            prov.getSourceSelectedDatabaseObjects()[0].getCatalogName();
		        String schema =
		            prov.getSourceSelectedDatabaseObjects()[0].getSchemaName();
		        log.debug("Detected MySQL, using catalog ("+catalog+") " +
		                  "instead of schema ("+schema+")");
		    }
		    sourceSchema = 
		        prov.getSourceSelectedDatabaseObjects()[0].getCatalogName();
		} else {
		    sourceSchema = 
		        prov.getSourceSelectedDatabaseObjects()[0].getSchemaName();
		}
		*/
		String tableName =
			getQualifiedObjectName(sourceSession, ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName(),
				DialectFactory.SOURCE_TYPE);
		result.append(tableName);
		return result.toString();
	}

	/**
	 * Returns a boolean value indicating whether or not the specified TableColumnInfo represents a database
	 * column that holds binary type data.
	 * 
	 * @param columnInfo
	 *           the TableColumnInfo to examine
	 * @return true if binary; false otherwise.
	 */
	public static boolean isBinaryType(TableColumnInfo columnInfo)
	{
		boolean result = false;
		int type = columnInfo.getDataType();
		if (type == Types.BINARY || type == Types.BLOB || type == Types.LONGVARBINARY
			|| type == Types.VARBINARY)
		{
			result = true;
		}
		return result;
	}

	/**
	 * Decide whether or not the session specified needs fully qualified table names (schema.table). In most
	 * databases this is optional (Oracle). In others it is required (Progress). In still others it must not
	 * occur. (Axion, Hypersonic)
	 * 
	 * @param session
	 * @param catalogName
	 * @param schemaName
	 * @param objectName
	 * @return
	 * @throws UserCancelledOperationException
	 */
	public static String getQualifiedObjectName(ISession session, String catalogName, String schemaName,
		String objectName, int sessionType) throws UserCancelledOperationException
	{
		String catalog = fixCase(session, catalogName);
		String schema = fixCase(session, schemaName);
		String object = fixCase(session, objectName);
		SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
		boolean useSchema = true;
		boolean useCatalog = true;
		try
		{
			useCatalog = md.supportsCatalogsInTableDefinitions();
		}
		catch (SQLException e)
		{
			log.info("Encountered unexpected exception while attempting to "
				+ "determine if catalogs are used in table definitions");
		}
		try
		{
			useSchema = md.supportsSchemasInTableDefinitions();
		}
		catch (SQLException e)
		{
			log.info("Encountered unexpected exception while attempting to "
				+ "determine if schemas are used in table definitions");
		}
		if (!useCatalog && !useSchema) { return object; }
		if ((catalog == null || catalog.equals("")) && (schema == null || schema.equals(""))) { return object; }
		StringBuilder result = new StringBuilder();
		if (useCatalog && catalog != null && !catalog.equals(""))
		{
			result.append(catalog);
			result.append(getCatSep(session));
		}
		if (useSchema && schema != null && !schema.equals(""))
		{
			result.append(schema);
			result.append(".");
		}
		result.append(object);
		return result.toString();
	}

	public static String getCatSep(ISession session)
	{
		String catsep = ".";
		try
		{
			SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
			catsep = md.getCatalogSeparator();
		}
		catch (SQLException e)
		{
			log.error("getCatSep: Unexpected Exception - " + e.getMessage(), e);
		}
		return catsep;
	}

	/**
	 * Uppercase / Lowercase / Mixedcase identifiers are a big problem. Some databases support mixing case
	 * (like McKoi) others force identifier case to all uppercase or all lowercase. Some (like MySQL) can be
	 * configured to care or not care about case as well as depending on the platform the database is on. This
	 * method attempt to use the metadata from the driver to "fix" the case of the identifier to be acceptable
	 * for the specified session.
	 * 
	 * @param session
	 *           the session whose disposition on case we care about.
	 * @param identifier
	 * @return
	 */
	public static String fixCase(ISession session, String identifier)
	{
		if (identifier == null || identifier.equals("")) { return identifier; }
		try
		{
			DatabaseMetaData md = session.getSQLConnection().getConnection().getMetaData();

			// Don't change the case of the identifier if database allows mixed
			// case.
			if (md.storesMixedCaseIdentifiers()) { return identifier; }
			// Fix the case according to what the database tells us.
			if (md.storesUpperCaseIdentifiers())
			{
				return identifier.toUpperCase();
			}
			else
			{
				return identifier.toLowerCase();
			}
		}
		catch (SQLException e)
		{
			if (log.isDebugEnabled())
			{
				log.debug("fixCase: unexpected exception: " + e.getMessage());
			}
			return identifier;
		}
	}

	/**
	 * @param sourceConn
	 * @param ti
	 * @return
	 * @throws SQLException
	 */
	public static int getColumnCount(ISQLConnection sourceConn, ITableInfo ti) throws SQLException
	{
		return sourceConn.getSQLMetaData().getColumnInfo(ti).length;
	}

	/**
	 * @param con
	 * @param ti
	 * @param column
	 * @return
	 * @throws SQLException
	 */
	public static int getColumnType(ISQLConnection con, ITableInfo ti, int column) throws SQLException
	{
		TableColumnInfo[] infoArr = con.getSQLMetaData().getColumnInfo(ti);
		TableColumnInfo colInfo = infoArr[column];
		return colInfo.getDataType();
	}

	public static int[] getColumnTypes(ISQLConnection con, ITableInfo ti) throws SQLException
	{
		TableColumnInfo[] infoArr = con.getSQLMetaData().getColumnInfo(ti);
		int[] result = new int[infoArr.length];
		for (int i = 0; i < result.length; i++)
		{
			TableColumnInfo colInfo = infoArr[i];
			result[i] = colInfo.getDataType();
		}
		return result;
	}

	public static boolean sameDatabaseType(ISession session1, ISession session2)
	{
		boolean result = false;
		String driver1ClassName = session1.getDriver().getDriverClassName();
		String driver2ClassName = session2.getDriver().getDriverClassName();
		if (driver1ClassName.equals(driver2ClassName))
		{
			result = true;
		}
		return result;
	}

	/**
	 * Gets the SQL statement which can be used to select the maximum length of the current data found in
	 * tableName within the specified column.
	 * 
	 * @param sourceSession
	 * @param colInfo
	 * @param tableName
	 * @param tableNameIsQualified
	 *           whether or not the specified tableName is qualified.
	 * @return
	 */
	public static String getMaxColumnLengthSQL(ISession sourceSession, TableColumnInfo colInfo,
		String tableName, boolean tableNameIsQualified) throws UserCancelledOperationException
	{
		StringBuilder result = new StringBuilder();
		HibernateDialect dialect =
			DialectFactory.getDialect(DialectFactory.SOURCE_TYPE, sourceSession.getApplication().getMainFrame(),
				sourceSession.getMetaData());
		String lengthFunction = dialect.getLengthFunction(colInfo.getDataType());
		if (lengthFunction == null)
		{
			log.error("Length function is null for dialect=" + dialect.getClass().getName() + ". Using 'length'");
			lengthFunction = "length";
		}
		String maxFunction = dialect.getMaxFunction();
		if (maxFunction == null)
		{
			log.error("Max function is null for dialect=" + dialect.getClass().getName() + ". Using 'max'");
			maxFunction = "max";
		}
		result.append("select ");
		result.append(maxFunction);
		result.append("(");
		result.append(lengthFunction);
		result.append("(");
		result.append(colInfo.getColumnName());
		result.append(")) from ");
		String table = tableName;
		if (!tableNameIsQualified)
		{
			table =
				getQualifiedObjectName(sourceSession, colInfo.getCatalogName(), colInfo.getSchemaName(),
					tableName, DialectFactory.SOURCE_TYPE);
		}
		result.append(table);
		return result.toString();
	}

	/**
	 * @param lastStatement
	 *           the lastStatement to set
	 */
	public static void setLastStatement(String lastStatement)
	{
		DBUtil.lastStatement = lastStatement;
	}

	/**
	 * @return the lastStatement
	 */
	public static String getLastStatement()
	{
		return lastStatement;
	}

	public static void setLastStatementValues(String values)
	{
		lastStatementValues = values;
	}

	public static String getLastStatementValues()
	{
		return lastStatementValues;
	}
}
