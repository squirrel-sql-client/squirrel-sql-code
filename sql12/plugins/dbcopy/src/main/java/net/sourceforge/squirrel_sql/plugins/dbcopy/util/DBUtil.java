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

package net.sourceforge.squirrel_sql.plugins.dbcopy.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbcopy.ColTypeMapper;
import net.sourceforge.squirrel_sql.plugins.dbcopy.I18NBaseObject;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.DBCopyPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.PreferencesManager;

import org.hibernate.MappingException;

/**
 * A utility class for interacting with the database.
 */
public class DBUtil extends I18NBaseObject
{

	/** Logger for this class. */
	private final static ILogger log = LoggerController.createLogger(DBUtil.class);

	/** Plugin settings. The configuration panel uses this */
	private static DBCopyPreferenceBean _prefs = PreferencesManager.getPreferences();

	/** Internationalized strings for this class */
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DBUtil.class);

	/** The name of the table to create when testing column names in dest db */
	private static final String TEST_TABLE_NAME = "dbcopytest";

	/** The last statement executed that we'll show to the user if error */
	private static String lastStatement = null;

	private static String lastStatementValues = null;

	public static void setPreferences(DBCopyPreferenceBean bean)
	{
		_prefs = bean;
	}

	/**
	 * Returns a string that looks like:
	 * 
	 * (PK_COL1, PK_COL2, PK_COL3, ...)
	 * 
	 * or null if there is no primary key for the specified table.
	 * 
	 * @param sourceConn
	 * @param ti
	 * @return
	 * @throws SQLException
	 */
	public static String getPKColumnString(ISQLConnection sourceConn, ITableInfo ti) throws SQLException
	{
		List<String> pkColumns = getPKColumnList(sourceConn, ti);
		if (pkColumns == null || pkColumns.size() == 0)
		{
			return null;
		}
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
		} else if (md.supportsSchemasInTableDefinitions())
		{
			rs = md.getPrimaryKeys(null, ti.getSchemaName(), ti.getSimpleName());
		} else
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
		if (pkColumns.size() == 0)
		{
			return null;
		}
		return pkColumns;
	}

	/**
	 * Returns a List of SQL statements that add foreign key(s) to the table described in the specified
	 * ITableInfo.
	 * 
	 * @param prov
	 *        used to see if the destination session connection FKs in the source session exist already
	 * @param ti
	 *        the table to get FK information on
	 * @return Set a set of SQL statements that can be used to create foreign key constraints.
	 * @throws SQLException
	 */
	public static Set<String> getForeignKeySQL(SessionInfoProvider prov, ITableInfo ti,
	      ArrayList<ITableInfo> selectedTableInfos) throws SQLException, UserCancelledOperationException
	{
		HashSet<String> result = new HashSet<String>();
		ForeignKeyInfo[] keys = ti.getImportedKeys();
		if (keys == null)
		{
			return result;
		}
		for (ForeignKeyInfo fkInfo : keys)
		{
			String pkTableName = fkInfo.getPrimaryKeyTableName();
			String pkTableCol = fkInfo.getPrimaryKeyColumnName();
			String fkTableName = fkInfo.getForeignKeyTableName();
			String fkTableCol = fkInfo.getForeignKeyColumnName();
			// TODO: Is giving a FK constraint a name universally supported
			// and done the same way on every database?
			String fkName = fkInfo.getForeignKeyName();

			// alter table ti.getSimpleName()
			// add foreign key (fkTableCol)
			// references pkTableName(pkTableCol);
			if (!containsTable(selectedTableInfos, pkTableName))
			{
				// TODO: Maybe someday we could inform the user that the imported
				// key can't be created because the list of tables they've
				// selected, doesn't include the table that this foreign key
				// depends upon. For now, just log a warning and skip it.
				if (log.isDebugEnabled())
				{
					// i18n[DBUtil.error.missingtable=getForeignKeySQL: table
					// '{0}' has a column '{1}' that references table '{2}'
					// column '{3}'. However, that table is not being copied.
					// Skipping this foreign key.]
					String msg = s_stringMgr.getString("DBUtil.error.missingtable", new String[]
						{ fkTableName, fkTableCol, pkTableName, pkTableCol });

					log.debug(msg);
				}
				continue;
			}

			ISession destSession = prov.getDestSession();
			String destSchema = prov.getDestDatabaseObject().getSimpleName();
			String destCatalog = prov.getDestDatabaseObject().getCatalogName();
			if (tableHasForeignKey(destCatalog, destSchema, ti.getSimpleName(), fkInfo, prov))
			{
				if (log.isInfoEnabled())
				{
					log.info("Skipping FK (" + fkName + ") - table " + ti.getSimpleName()
					      + " seems to already have it defined.");
				}
				continue;
			}

			String fkTable = getQualifiedObjectName(
			   destSession, destCatalog, destSchema, ti.getSimpleName(), DialectFactory.DEST_TYPE);
			String pkTable = getQualifiedObjectName(
			   destSession, destCatalog, destSchema, pkTableName, DialectFactory.DEST_TYPE);
			StringBuilder tmp = new StringBuilder();
			tmp.append("ALTER TABLE ");
			tmp.append(fkTable);
			tmp.append(" ADD FOREIGN KEY (");
			tmp.append(fkTableCol);
			tmp.append(") REFERENCES ");
			tmp.append(pkTable);
			tmp.append("(");
			tmp.append(pkTableCol);
			tmp.append(")");
			result.add(tmp.toString());
		}
		return result;
	}

	public static boolean tableHasForeignKey(String destCatalog, String destSchema, String destTableName,
	      ForeignKeyInfo fkInfo, SessionInfoProvider prov)
	{
		boolean result = false;
		try
		{
			SQLDatabaseMetaData md = prov.getDestSession().getSQLConnection().getSQLMetaData();

			ITableInfo[] tables = md.getTables(destCatalog, destSchema, destTableName, new String[]
				{ "TABLE" }, null);
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
			} else
			{
				log.error("Couldn't find an exact match for destination table " + destTableName + " in schema "
				      + destSchema + " and catalog " + destCatalog + ". Skipping FK constraint");
			}
		} catch (SQLException e)
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

		if (!fk1PKColumn.equals(fk2PKColumn))
		{
			return false;
		}
		if (!fk1FKColumn.equals(fk2FKColumn))
		{
			return false;
		}
		if (!fk1PKTable.equals(fk2PKTable))
		{
			return false;
		}
		if (!fk1FKTable.equals(fk2FKTable))
		{
			return false;
		}
		return true;
	}

	private static boolean containsTable(List<ITableInfo> tableInfos, String table)
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
	 * Executes the given SQL using the specified SQLConnection.
	 * 
	 * @param con
	 *        the SQLConnection to execute the update on.
	 * @param SQL
	 *        the statement to execute.
	 * @return either the row count for INSERT, UPDATE or DELETE statements, or 0 for SQL statements that
	 *         return nothing
	 * @throws SQLException
	 *         if a database access error occurs or the given SQL statement produces a ResultSet object
	 */
	public static int executeUpdate(ISQLConnection con, String SQL, boolean writeSQL) throws SQLException
	{
		Statement stmt = null;
		int result = 0;
		try
		{
			stmt = con.createStatement();
			if (writeSQL)
			{
				ScriptWriter.write(SQL);
			}
			if (log.isDebugEnabled())
			{
				// i18n[DBUtil.info.executeupdate=executeupdate: Running SQL:\n '{0}']
				String msg = s_stringMgr.getString("DBUtil.info.executeupdate", SQL);
				log.debug(msg);
			}
			lastStatement = SQL;
			result = stmt.executeUpdate(SQL);
		} finally
		{
			SQLUtilities.closeStatement(stmt);
		}
		return result;
	}

	/**
	 * Executes the specified sql statement on the specified connection and returns the ResultSet.
	 * 
	 * @param con
	 * @param sql
	 * @param mysqlBigResultFix
	 *        if true, provides a work-around which is useful in the case that the connection is to a MySQL
	 *        database. If the number of rows is large this will prevent the driver from reading them all into
	 *        client memory. MySQL's normal practice is to do such a thing for performance reasons.
	 * @return
	 * @throws Exception
	 */
	public static ResultSet executeQuery(ISession session, String sql) throws SQLException
	{
		ISQLConnection sqlcon = session.getSQLConnection();
		if (sqlcon == null || sql == null)
		{
			return null;
		}
		Statement stmt = null;
		ResultSet rs = null;

		Connection con = sqlcon.getConnection();
		try
		{
			if (DialectFactory.isMySQL(session.getMetaData()))
			{
				stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

				stmt.setFetchSize(Integer.MIN_VALUE);
			} else if (DialectFactory.isTimesTen(session.getMetaData()))
			{
				stmt = con.createStatement();
				int fetchSize = _prefs.getSelectFetchSize();
				// TimesTen allows a maximum fetch size of 128.
				if (fetchSize > 128)
				{
					log.info("executeQuery: TimesTen allows a maximum fetch size of "
					      + "128.  Altering preferred fetch size from " + fetchSize + " to 128.");
					fetchSize = 128;
				}
				stmt.setFetchSize(fetchSize);
			} else
			{
				stmt = con.createStatement();
				// Allow the user to set "0" for the fetch size to indicate that
				// this should not be called. JDBC-ODBC bridge driver fails to
				// execute SQL once you have set the fetch size to *any* value.
				if (_prefs.getSelectFetchSize() > 0)
				{
					stmt.setFetchSize(_prefs.getSelectFetchSize());
				}
			}
		} catch (SQLException e)
		{
			// Only close the statement if SQLException - otherwise it has to
			// remain open until the ResultSet is read through by the caller.
			SQLUtilities.closeResultSet(rs);
			SQLUtilities.closeStatement(stmt);
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
		} catch (SQLException e)
		{
			// Only close the statement if SQLException - otherwise it has to
			// remain open until the ResultSet is read through by the caller.
			SQLUtilities.closeStatement(stmt);
			throw e;
		}

		return rs;
	}

	/**
	 * Returns a count of the records in the specified table.
	 * 
	 * @param con
	 *        the SQLConnection to use to execute the count query.
	 * @param tableName
	 *        the name of the table. This name should already be qualified by the schema.
	 * 
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
		} catch (Exception e)
		{
			/* Do Nothing - this can happen when the table doesn't exist */
		} finally
		{
			SQLUtilities.closeResultSet(rs, true);
		}
		return result;
	}

	/**
	 * Returns a count of the records in the specified table.
	 * 
	 * @param con
	 *        the SQLConnection to use to execute the count query.
	 * @param tableName
	 *        the name of the table
	 * 
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
		ITableInfo[] tis = getTables(session, catalog, schema, tableName);
		if (tis == null || tis.length == 0)
		{
			if (Character.isUpperCase(tableName.charAt(0)))
			{
				tableName = tableName.toLowerCase();
			} else
			{
				tableName = tableName.toUpperCase();
			}
			tis = getTables(session, null, schema, tableName);
			if (tis.length == 0)
			{
				if (Character.isUpperCase(tableName.charAt(0)))
				{
					tableName = tableName.toLowerCase();
				} else
				{
					tableName = tableName.toUpperCase();
				}
				tis = getTables(session, null, schema, tableName);
			}
		}
		if (tis.length == 0)
		{
			// i18n[DBUtil.error.tablenotfound=Couldn't locate table '{0}' in
			// schema '(1)']
			String msg = s_stringMgr.getString("DBUtil.error.tablenotfound", new String[]
				{ tableName, schema });
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

	public static ITableInfo[] getTables(ISession session, String catalog, String schema, String tableName)
	{
		ITableInfo[] result = new ITableInfo[0];

		try
		{
			SchemaInfo schemaInfo = session.getSchemaInfo();
			result = schemaInfo.getITableInfos(catalog, schema, tableName);
		} catch (Exception e)
		{
			log.error("Encountered unexpected exception when attempting to "
			      + "call schemaInfo.getTables with catalog = " + catalog + " schema = " + schema
			      + " tableName = " + tableName);

		}

		if (result == null || result.length == 0)
		{
			// Fallback to the old method, going directly to the database
			// instead
			// of using SchemaInfo, since SchemaInfo didn't have it.
			SQLDatabaseMetaData d = session.getSQLConnection().getSQLMetaData();
			result = getTables(d, catalog, schema, tableName);
		}

		return result;
	}

	private static ITableInfo[] getTables(SQLDatabaseMetaData data, String catalog, String schema,
	      String tableName)
	{

		ITableInfo[] result = new ITableInfo[0];

		try
		{
			result = data.getTables(catalog, schema, tableName, null, null);
		} catch (Exception e)
		{
			log.error("Encountered unexpected exception when attempting to "
			      + "call SQLDatabaseMetaData.getTables with catalog = " + catalog + " schema = " + schema
			      + " tableName = " + tableName);

		}
		return result;
	}

	/**
	 * Decides whether or not the specified column types (java.sql.Type constants) use the same java type to
	 * read from the source database as the one used to write to the destination database. For example,
	 * Types.DECIMAL and Types.NUMERIC both use BigDecimal java type to store the value in between reading and
	 * writing it. Therefore, even though these types are not equal, they are equivalent. This method has not
	 * yet been fully implemented with equivalences from the bindVariable method.
	 * 
	 * @param sourceType
	 *        the column type as identified by the source database jdbc driver.
	 * @param destType
	 *        the column type as identified by the destination database jdbc driver.
	 * @return true if equivalent, false if not.
	 */
	public static boolean typesAreEquivalent(int sourceType, int destType)
	{
		boolean result = false;
		if (sourceType == destType)
		{
			result = true;
		}
		if (sourceType == Types.DECIMAL && destType == Types.NUMERIC)
		{
			result = true;
		}
		if (sourceType == Types.NUMERIC && destType == Types.DECIMAL)
		{
			result = true;
		}
		if (sourceType == Types.BOOLEAN && destType == Types.BIT)
		{
			result = true;
		}
		if (sourceType == Types.BIT && destType == Types.BOOLEAN)
		{
			result = true;
		}
		return result;
	}

	/**
	 * Check to see if the last column retrieved at the specified index was null. If so, bind the specified
	 * PreparedStatement column at the specified index to null and return true.
	 * 
	 * @param rs
	 *        the ResultSet that was used to read the last row.
	 * @param ps
	 *        the PreparedStatement that will be used to insetrt a row into the destination database.
	 * @param index
	 *        the column in the row that was last read, whose value we mean to inspect.
	 * @param type
	 *        the type of the column.
	 * @return true if last column was null; false otherwise.
	 * @throws SQLException
	 */
	private static boolean handleNull(ResultSet rs, PreparedStatement ps, int index, int type)
	      throws SQLException
	{
		boolean result = false;
		if (rs.wasNull())
		{
			ps.setNull(index, type);
			result = true;
		}
		return result;
	}

	/**
	 * Takes the specified colInfo, gets the data type to see if it is 1111(OTHER). If so then get the type
	 * name and try to match a jdbc type with the same name to get it's type code.
	 * 
	 * @param colInfo
	 *        information about the column
	 * @param session
	 *        the session that the specified column info came from.
	 * @return the data type code
	 * @throws MappingException
	 */
	public static int replaceOtherDataType(TableColumnInfo colInfo, ISession session) throws MappingException
	{
		int colJdbcType = colInfo.getDataType();
		if (colJdbcType == java.sql.Types.OTHER)
		{
			try
			{
				HibernateDialect dialect = DialectFactory.getDialect(session.getMetaData());
				String typeName = colInfo.getTypeName().toUpperCase();
				int parenIndex = typeName.indexOf("(");
				if (parenIndex != -1)
				{
					typeName = typeName.substring(0, parenIndex);
				}
				colJdbcType = dialect.getJavaTypeForNativeType(colInfo.getTypeName());
			} catch (Exception e)
			{
				log.error("replaceOtherDataType: unexpected exception - " + e.getMessage());
			}
		}
		return colJdbcType;
	}

	/**
	 * This is postgresql specific. If the session is pg, and the colInfo has a DISTINCT type (Java SQl Type
	 * 2001)then this will query the information_schema, looking for the native type name of the column which
	 * backs the DISINCT type. A distinct type is like a type alias - it is defined in SQL-99 as a UDT.
	 * 
	 * @param colInfo
	 *        the TableColumnInfo representing the column.
	 * @param session
	 *        the session to the database that the column is defined in.
	 * @return the type code of the matching type, or if not found, the type code is taken from the specified
	 *         colInfo
	 */
	public static int replaceDistinctDataType(int colJdbcType, TableColumnInfo colInfo, ISession session)
	{
		
		if (colJdbcType == java.sql.Types.DISTINCT && DialectFactory.isPostgreSQL(session.getMetaData()))
		{
			Connection con = session.getSQLConnection().getConnection();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				String sql = "SELECT data_type " + "FROM information_schema.columns " + "where column_name = ? ";
				if (colInfo.getSchemaName() != null)
				{
					sql += " and table_schema = ? ";
				}
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, colInfo.getColumnName());
				if (colInfo.getSchemaName() != null)
				{
					pstmt.setString(2, colInfo.getSchemaName());
				}
				rs = pstmt.executeQuery();
				if (rs.next())
				{
					String nativeTypeName = rs.getString(1);
					colJdbcType = JDBCTypeMapper.getJdbcType(nativeTypeName.toUpperCase(), colJdbcType);
				}
			} catch (SQLException e)
			{
				log.error("replaceDistinctDataType: Unexpected exception - " + e, e);
			} finally
			{
				SQLUtilities.closeStatement(pstmt);
			}
		}
		return colJdbcType;
	}

	/**
	 * Reads the value from the specified ResultSet at column index index, and based on the type, calls the
	 * appropriate setXXX method on ps with the value obtained.
	 * 
	 * @param ps
	 * @param sourceColType
	 * @param destColType
	 * @param index
	 * @param rs
	 * @return a string representation of the value that was bound.
	 * @throws SQLException
	 */
	public static String bindVariable(PreparedStatement ps, int sourceColType, int destColType, int index,
	      ResultSet rs) throws SQLException
	{
		String result = "null";
		switch (sourceColType)
		{
		case Types.ARRAY:
			Array arrayVal = rs.getArray(index);
			result = getValue(arrayVal);
			ps.setArray(index, arrayVal);
			break;
		case Types.BIGINT:
			long bigintVal = rs.getLong(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = Long.toString(bigintVal);
				ps.setLong(index, bigintVal);
			}
			break;
		case Types.BINARY:
			result = bindBlobVar(ps, index, rs, destColType);
			break;
		case Types.BIT:
			// JDBC spec says that BIT refers to a boolean column - i.e. a
			// single binary digit with value either "0" or "1". Also
			// the same spec encourages use of getBoolean/setBoolean.
			// However, the SQL-92 standard clearly states that the BIT type
			// is a bit string with length >= 0. So for SQL-92 compliant
			// databases (like PostgreSQL) the JDBC spec's support for BIT
			// is at best broken and unusable. Still, we do what the JDBC
			// spec suggests as that is all that we can do.

			// TODO: just noticed that MySQL 5.0 supports a multi-bit BIT
			// column by using the getObject/setObject methods with a byte[].
			// So it would be valuable at some point to make this code a bit
			// more dbms-specific
			boolean bitValue = rs.getBoolean(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = Boolean.toString(bitValue);
				ps.setBoolean(index, bitValue);
			}
			break;
		case Types.BLOB:
			result = bindBlobVar(ps, index, rs, destColType);
			break;
		case Types.BOOLEAN:
			boolean booleanValue = rs.getBoolean(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = Boolean.toString(booleanValue);
				// HACK: some dbs (like Frontbase) don't support boolean
				// types. I've tried tinyint, bit and boolean as the column
				// type, and setBoolean fails for all three. It's a mystery
				// at this point what column the getBoolean/setBoolean methods
				// actually work on iin FrontBase.
				switch (destColType)
				{
				case Types.TINYINT:
				case Types.SMALLINT:
				case Types.BIGINT:
				case Types.INTEGER:
					ps.setInt(index, booleanValue ? 1 : 0);
					break;
				case Types.FLOAT:
					ps.setFloat(index, booleanValue ? 1 : 0);
					break;
				case Types.DOUBLE:
					ps.setDouble(index, booleanValue ? 1 : 0);
					break;
				case Types.VARCHAR:
				case Types.CHAR:
					ps.setString(index, booleanValue ? "1" : "0");
					break;
				default:
					ps.setBoolean(index, booleanValue);
					break;
				}
			}
			break;
		case Types.CHAR:
			String charValue = rs.getString(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = charValue;
				ps.setString(index, charValue);
			}
			break;
		case Types.CLOB:
			bindClobVar(ps, index, rs, destColType);
			break;
		case Types.DATALINK:
			// TODO: is this right???
			Object datalinkValue = rs.getObject(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = getValue(datalinkValue);
				ps.setObject(index, datalinkValue);
			}
			break;
		case Types.DATE:
			Date dateValue = rs.getDate(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				// TODO: use the destination database type to derive a
				// format that is acceptable.
				result = getValue(dateValue);
				ps.setDate(index, dateValue);
			}
			break;
		case Types.DECIMAL:
			BigDecimal decimalValue = rs.getBigDecimal(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = getValue(decimalValue);
				ps.setBigDecimal(index, decimalValue);
			}
			break;
		case Types.DISTINCT:
			// TODO: is this right???
			Object distinctValue = rs.getObject(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = getValue(distinctValue);
				ps.setObject(index, distinctValue);
			}
			break;
		case Types.DOUBLE:
			double doubleValue = rs.getDouble(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = Double.toString(doubleValue);
				ps.setDouble(index, doubleValue);
			}
			break;
		case Types.FLOAT:
			// SQL FLOAT requires support for 15 digits of mantissa.
			double floatValue = rs.getDouble(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = Double.toString(floatValue);
				ps.setDouble(index, floatValue);
			}
			break;
		case Types.INTEGER:
			int integerValue = rs.getInt(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = Integer.toString(integerValue);
				ps.setInt(index, integerValue);
			}
			break;
		case Types.JAVA_OBJECT:
			Object objectValue = rs.getObject(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = getValue(objectValue);
				ps.setObject(index, objectValue);
			}
			break;
		case Types.LONGVARBINARY:
			result = bindBlobVar(ps, index, rs, destColType);
			break;
		case Types.LONGVARCHAR:
			String longvarcharValue = rs.getString(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = longvarcharValue;
				ps.setString(index, longvarcharValue);
			}
			break;
		case Types.NULL:
			// TODO: is this right???
			ps.setNull(index, Types.NULL);
			break;
		case Types.NUMERIC:
			BigDecimal numericValue = rs.getBigDecimal(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = getValue(numericValue);
				ps.setBigDecimal(index, numericValue);
			}
			break;
		case Types.OTHER:
			// TODO: figure out a more reliable way to handle OTHER type
			// which indicates a database-specific type.
			String testValue = rs.getString(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				try
				{
					Double.parseDouble(testValue);
					double numberValue = rs.getDouble(index);
					ps.setDouble(index, numberValue);
				} catch (SQLException e)
				{
					byte[] otherValue = rs.getBytes(index);
					result = getValue(otherValue);
					ps.setBytes(index, otherValue);
				}
			}
			break;
		case Types.REAL:
			float realValue = rs.getFloat(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = Float.toString(realValue);
				ps.setFloat(index, realValue);
			}
			break;
		case Types.REF:
			Ref refValue = rs.getRef(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = getValue(refValue);
				ps.setRef(index, refValue);
			}
			break;
		case Types.SMALLINT:
			short smallintValue = rs.getShort(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = Short.toString(smallintValue);
				ps.setShort(index, smallintValue);
			}
			break;
		case Types.STRUCT:
			Object structValue = rs.getObject(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = getValue(structValue);
				ps.setObject(index, structValue);
			}
			break;
		case Types.TIME:
			Time timeValue = rs.getTime(index);
			// TODO: use the destination database type to derive a format
			// that is acceptable.
			if (!handleNull(rs, ps, index, destColType))
			{
				result = getValue(timeValue);
				ps.setTime(index, timeValue);
			}
			break;
		case Types.TIMESTAMP:
			Timestamp timestampValue = rs.getTimestamp(index);
			// TODO: use the destination database type to derive a format
			// that is acceptable.
			if (!handleNull(rs, ps, index, destColType))
			{
				result = getValue(timestampValue);
				ps.setTimestamp(index, timestampValue);
			}
			break;
		case Types.TINYINT:
			byte tinyintValue = rs.getByte(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = Byte.toString(tinyintValue);
				ps.setByte(index, tinyintValue);
			}
			break;
		case Types.VARBINARY:
			result = bindBlobVar(ps, index, rs, destColType);
			break;
		case Types.VARCHAR:
			String varcharValue = rs.getString(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = varcharValue;
				ps.setString(index, varcharValue);
			}
			break;
		default:
			// i18n[DBUtil.error.unknowntype=Unknown Java SQL column type: '{0}']
			String msg = s_stringMgr.getString("DBUtil.error.unknowntype", Integer.valueOf(sourceColType));
			log.error(msg);
			// We still have to bind a value, or else the PS will throw
			// an exception.
			String value = rs.getString(index);
			if (!handleNull(rs, ps, index, destColType))
			{
				result = value;
				ps.setString(index, value);
			}
			break;
		}
		return result;
	}

	private static String bindClobVar(PreparedStatement ps, int index, ResultSet rs, int type)
	      throws SQLException
	{
		String result = "null";
		if (_prefs.isUseFileCaching())
		{
			try
			{
				bindClobVarInFile(ps, index, rs, type);
			} catch (Exception e)
			{
				// i18n[DBUtil.error.bindclobfailure=bindBlobVar: failed to
				// bind blob using filesystem - attempting to bind blob using
				// memory]
				String msg = s_stringMgr.getString("DBUtil.error.bindclobfailure");
				log.error(msg, e);
				// if we failed to bind the blob in a file, try memory.
				result = bindClobVarInMemory(ps, index, rs, type);
			}
		} else
		{
			result = bindClobVarInMemory(ps, index, rs, type);
		}
		return result;
	}

	private static String bindBlobVar(PreparedStatement ps, int index, ResultSet rs, int type)
	      throws SQLException
	{
		String result = "null";
		if (_prefs.isUseFileCaching())
		{
			try
			{
				bindBlobVarInFile(ps, index, rs, type);
			} catch (Exception e)
			{
				// i18n[DBUtil.error.bindblobfailure=bindBlobVar: failed to
				// bind blob using filesystem - attempting to bind blob using
				// memory]
				String msg = s_stringMgr.getString("DBUtil.error.bindblobfailure");
				log.error(msg, e);
				// if we failed to bind the blob in a file, try memory.
				result = bindBlobVarInMemory(ps, index, rs, type);
			}
		} else
		{
			result = bindBlobVarInMemory(ps, index, rs, type);
		}
		return result;
	}

	private static String bindClobVarInMemory(PreparedStatement ps, int index, ResultSet rs, int type)
	      throws SQLException
	{
		String clobValue = rs.getString(index);
		if (rs.wasNull())
		{
			ps.setNull(index, type);
			return "null";
		}
		String result = getValue(clobValue);
		if (log.isDebugEnabled() && clobValue != null)
		{
			// i18n[DBUtil.info.bindclobmem=bindClobVarInMemory: binding '{0}' bytes]
			String msg = s_stringMgr.getString("DBUtil.info.bindclobmem", Integer.valueOf(clobValue.length()));
			log.debug(msg);
		}
		ps.setString(index, clobValue);
		return result;
	}

	private static String bindBlobVarInMemory(PreparedStatement ps, int index, ResultSet rs, int type)
	      throws SQLException
	{
		byte[] blobValue = rs.getBytes(index);
		if (rs.wasNull())
		{
			ps.setNull(index, type);
			return "null";
		}
		String result = getValue(blobValue);
		if (log.isDebugEnabled() && blobValue != null)
		{
			// i18n[DBUtil.info.bindblobmem=bindBlobVarInMemory: binding '{0}' bytes]
			String msg = s_stringMgr.getString("DBUtil.info.bindblobmem", Integer.valueOf(blobValue.length));
			log.debug(msg);
		}
		ps.setBytes(index, blobValue);
		return result;
	}

	private static void bindClobVarInFile(PreparedStatement ps, int index, ResultSet rs, int type)
	      throws IOException, SQLException
	{
		// get ascii stream from rs
		InputStream is = rs.getAsciiStream(index);
		if (rs.wasNull())
		{
			ps.setNull(index, type);
			return;
		}

		// Open file output stream
		long millis = System.currentTimeMillis();
		File f = File.createTempFile("clob", "" + millis);
		f.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(f);
		if (log.isDebugEnabled())
		{
			// i18n[DBUtil.info.bindclobfile=bindClobVarInFile: Opening temp file '{0}']
			String msg = s_stringMgr.getString("DBUtil.info.bindclobfile", f.getAbsolutePath());
			log.debug(msg);
		}

		// read rs input stream write to file output stream
		byte[] buf = new byte[_prefs.getFileCacheBufferSize()];
		int length = 0;
		int total = 0;
		while ((length = is.read(buf)) >= 0)
		{
			if (log.isDebugEnabled())
			{
				// i18n[DBUtil.info.bindcloblength=bindClobVarInFile: writing '{0}' bytes.]
				String msg = s_stringMgr.getString("DBUtil.info.bindcloblength", Integer.valueOf(length));
				log.debug(msg);
			}
			fos.write(buf, 0, length);
			total += length;
		}
		fos.close();

		// set the ps to read from the file we just created.
		FileInputStream fis = new FileInputStream(f);
		BufferedInputStream bis = new BufferedInputStream(fis);
		ps.setAsciiStream(index, bis, total);
	}

	private static void bindBlobVarInFile(PreparedStatement ps, int index, ResultSet rs, int type)
	      throws IOException, SQLException
	{
		// get binary stream from rs
		InputStream is = rs.getBinaryStream(index);
		if (rs.wasNull())
		{
			ps.setNull(index, type);
			return;
		}
		// Open file output stream
		long millis = System.currentTimeMillis();
		File f = File.createTempFile("blob", "" + millis);
		f.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(f);
		if (log.isDebugEnabled())
		{
			// i18n[DBUtil.info.bindblobfile=bindBlobVarInFile: Opening temp file '{0}']
			String msg = s_stringMgr.getString("DBUtil.info.bindblobfile", f.getAbsolutePath());
			log.debug(msg);
		}

		// read rs input stream write to file output stream
		byte[] buf = new byte[_prefs.getFileCacheBufferSize()];
		int length = 0;
		int total = 0;
		while ((length = is.read(buf)) >= 0)
		{
			if (log.isDebugEnabled())
			{
				// i18n[DBUtil.info.bindbloblength=bindBlobVarInFile: writing '{0}' bytes.]
				String msg = s_stringMgr.getString("DBUtil.info.bindbloblength", Integer.valueOf(length));
				log.debug(msg);
			}
			fos.write(buf, 0, length);
			total += length;
		}
		fos.close();

		// set the ps to read from the file we just created.
		FileInputStream fis = new FileInputStream(f);
		BufferedInputStream bis = new BufferedInputStream(fis);
		ps.setBinaryStream(index, bis, total);
	}

	/**
	 * Returns the string representation of the specified object, or "null" if the specified object is null.
	 * 
	 * @param o
	 * @return
	 */
	private static String getValue(Object o)
	{
		if (o != null)
		{
			return o.toString();
		}
		return "null";
	}

	/**
	 * 
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
		} finally
		{
			SQLUtilities.closeResultSet(rs);
		}
		return result;
	}

	/**
	 * Check the specified session to determine if the specified data is a keyword.
	 * 
	 * @param session
	 * @param data
	 * @return
	 */
	public static boolean isKeyword(ISession session, String data)
	{
		return session.getSchemaInfo().isKeyword(data);
	}

	/**
	 * Deletes existing data from the destination connection specified in the specified table. This will use
	 * preferences to determine if truncate command is preferred. If truncate is preferred and fails, then
	 * delete will be attempted.
	 * 
	 * @param con
	 * @param tablename
	 * @throws SQLException
	 */
	public static void deleteDataInExistingTable(ISession session, String catalogName, String schemaName,
	      String tableName) throws SQLException, UserCancelledOperationException
	{
		ISQLConnection con = session.getSQLConnection();
		boolean useTrunc = PreferencesManager.getPreferences().isUseTruncate();
		String fullTableName = getQualifiedObjectName(
		   session, catalogName, schemaName, tableName, DialectFactory.DEST_TYPE);
		String truncSQL = "TRUNCATE TABLE " + fullTableName;
		String deleteSQL = "DELETE FROM " + fullTableName;
		try
		{
			if (useTrunc)
			{
				DBUtil.executeUpdate(con, truncSQL, true);
			} else
			{
				DBUtil.executeUpdate(con, deleteSQL, true);
			}
		} catch (SQLException e)
		{
			// If truncate was attempted and not supported, then try delete.
			// If on the other hand delete was attempted, just throw the
			// SQLException that resulted from the delete.
			if (useTrunc)
			{
				DBUtil.executeUpdate(con, deleteSQL, true);
			} else
			{
				throw e;
			}
		}
	}

	/**
	 * This will take into account any special needs that the destination session has with regard to user
	 * preferences, and throw a MappingException if any user preference isn't valid for the specified
	 * destination session.
	 * 
	 * @param destSession
	 */
	public static void sanityCheckPreferences(ISession destSession) throws MappingException
	{

		if (DialectFactory.isFirebird(destSession.getMetaData()))
		{
			if (!PreferencesManager.getPreferences().isCommitAfterTableDefs())
			{
				// TODO: maybe instead of throwing an exception, we could ask
				// the user if they would like us to adjust their preference for
				// them.

				// i18n[DBUtil.error.firebirdcommit=Firebird requires commit
				// table create before inserting records. Please adjust your
				// preferences.]
				String msg = s_stringMgr.getString("DBUtil.error.firebirdcommit");
				throw new MappingException(msg);
			}
		}
	}

	public static String getCreateTableSql(SessionInfoProvider prov, ITableInfo ti, String destTableName, String destSchema, String destCatalog) throws SQLException,
	      MappingException, UserCancelledOperationException
	{

		ISession sourceSession = prov.getSourceSession();
		String sourceSchema = prov.getSourceDatabaseObjects().get(0).getSchemaName();
		String sourceCatalog = prov.getSourceDatabaseObjects().get(0).getCatalogName();
		String sourceTableName = getQualifiedObjectName(
		   sourceSession, sourceCatalog, sourceSchema, ti.getSimpleName(), DialectFactory.SOURCE_TYPE);
		ISession destSession = prov.getDestSession();
//		String destSchema = getSchemaNameFromDbObject(prov.getDestDatabaseObject());
//		String destCatalog = prov.getDestDatabaseObject().getCatalogName();
		String destinationTableName = getQualifiedObjectName(
		   destSession, destCatalog, destSchema, destTableName, DialectFactory.DEST_TYPE);
		StringBuilder result = new StringBuilder("CREATE TABLE ");
		result.append(destinationTableName);
		result.append(" ( ");
		result.append("\n");
		TableColumnInfo colInfo = null;
		try
		{
			ISQLConnection sourceCon = prov.getSourceSession().getSQLConnection();
			TableColumnInfo[] colInfoArr = sourceCon.getSQLMetaData().getColumnInfo(ti);
			if (colInfoArr.length == 0)
			{
				// i18n[DBUtil.error.nocolumns=Table '{0}' in schema '{1}' has
				// no columns to copy]
				String msg = s_stringMgr.getString("DBUtil.error.nocolumns", new String[]
					{ ti.getSimpleName(), ti.getSchemaName() });
				throw new MappingException(msg);
			}
			for (int i = 0; i < colInfoArr.length; i++)
			{
				colInfo = colInfoArr[i];
				result.append("\t");
				String columnSql = DBUtil.getColumnSql(prov, colInfo, sourceTableName, destinationTableName);
				result.append(columnSql);
				if (i < colInfoArr.length - 1)
				{
					result.append(",\n");
				}
			}

			// If the user wants the primary key copied and the source session
			// isn't Axion (Axion throws SQLException for getPrimaryKeys())

			// TODO: Perhaps we can tell the user when they click "Copy Table"
			// if the source session is Axion and they want primary keys that
			// it's not possible.
			if (_prefs.isCopyPrimaryKeys() && !DialectFactory.isAxion(sourceSession.getMetaData()))
			{
				String pkString = DBUtil.getPKColumnString(sourceCon, ti);
				if (pkString != null)
				{
					result.append(",\n\tPRIMARY KEY ");
					result.append(pkString);
				}
			}
			result.append(")");
		} catch (MappingException e)
		{
			if (colInfo != null)
			{
				// i18n[DBUtil.error.maptype=Couldn't map type for table='{0}'
				// column='{1}']
				String msg = s_stringMgr.getString("DBUtil.error.maptype", new String[]
					{ destinationTableName, colInfo.getColumnName() });
				log.error(msg, e);
			}
			throw e;
		}

		return result.toString();
	}

	/**
	 * 
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
	 * Uses the column type mapper to get the column type and appends that to the name with an optional not
	 * null modifier.
	 * 
	 *
    * @param colInfo
    * @throws UserCancelledOperationException
	 * @throws MappingException
	 */
	public static String getColumnSql(SessionInfoProvider prov, TableColumnInfo colInfo,
                                     String sourceTableName, String destTableName) throws UserCancelledOperationException,
	      MappingException
	{
		String columnName = colInfo.getColumnName();
		if (_prefs.isCheckKeywords())
		{
			checkKeyword(prov.getDestSession(), destTableName, columnName);
		}
		StringBuilder result = new StringBuilder(columnName);
		boolean notNullable = colInfo.isNullable().equalsIgnoreCase("NO");
		String typeName = ColTypeMapper.mapColType(
		   prov.getSourceSession(), prov.getDestSession(), colInfo, sourceTableName, destTableName);
		result.append(" ");
		result.append(typeName);
		if (notNullable)
		{
			result.append(" NOT NULL");
		} else
		{
			ISession destSession = prov.getDestSession();
			HibernateDialect d = DialectFactory.getDialect(
			   DialectFactory.DEST_TYPE, destSession.getApplication().getMainFrame(), destSession.getMetaData());
			String nullString = d.getNullColumnString().toUpperCase();
			result.append(nullString);
		}
		return result.toString();
	}

	/**
	 * Checks the specified column is not a keyword in the specified session.
	 * 
	 * @param session
	 *        the session whose keywords to check against
	 * @param table
	 *        the name of the table to use in the error message
	 * @param column
	 *        the name of the column to check
	 * 
	 * @throws MappingException
	 *         if the specified column is a keyword in the specified session
	 */
	public static void checkKeyword(ISession session, String table, String column) throws MappingException
	{
		if (isKeyword(session, column))
		{
			String message = getMessage("DBUtil.mappingErrorKeyword", new String[]
				{ table, column });
			throw new MappingException(message);
		}
	}

	/**
	 * 
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
	 * 
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
	 * 
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
		ISession sourceSession = prov.getSourceSession();

		String tableName = getQualifiedObjectName(
		   sourceSession, ti.getCatalogName(), ti.getSchemaName(), ti.getSimpleName(),
		   DialectFactory.SOURCE_TYPE);
		result.append(tableName);
		return result.toString();
	}

	/**
	 * 
	 * @param sourceConn
	 * @param columnList
	 * @param ti
	 * @return
	 * @throws SQLException
	 */
	public static String getInsertSQL(SessionInfoProvider prov, String columnList, ITableInfo ti,
	      int columnCount) throws SQLException, UserCancelledOperationException
	{
		StringBuilder result = new StringBuilder();
		result.append("insert into ");
		String destSchema = DBUtil.getSchemaNameFromDbObject(prov.getDestDatabaseObject());
		String destCatalog = prov.getDestDatabaseObject().getCatalogName();
		ISession destSession = prov.getDestSession();
		result.append(getQualifiedObjectName(
		   destSession, destCatalog, destSchema, ti.getSimpleName(), DialectFactory.DEST_TYPE));
		result.append(" ( ");
		result.append(columnList);
		result.append(" ) values ( ");
		result.append(getQuestionMarks(columnCount));
		result.append(" )");
		return result.toString();
	}

	/**
	 * Returns a boolean value indicating whether or not the specified TableColumnInfo represents a database
	 * column that holds binary type data.
	 * 
	 * @param columnInfo
	 *        the TableColumnInfo to examine
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
	      String objectName, int sessionType)
	{
		String catalog = catalogName;
		String schema = schemaName;
		String object = objectName;

		// Bug #1714476 (DB copy uses wrong case for table names): When the
		// catalog/schema/object names come from the source session, don't mess
		// with the case, as the case is provided by the driver for the existing
		// table, and doesn't need to be fixed.
		if (sessionType == DialectFactory.DEST_TYPE)
		{
			catalog = fixCase(session, catalogName);
			schema = fixCase(session, schemaName);
			object = fixCase(session, objectName);
		}
		ISQLDatabaseMetaData md = session.getMetaData();
		boolean useSchema = true;
		boolean useCatalog = true;
		try
		{
			useCatalog = md.supportsCatalogsInTableDefinitions();
		} catch (SQLException e)
		{
			log.info("Encountered unexpected exception while attempting to "
			      + "determine if catalogs are used in table definitions");
		}
		try
		{
			useSchema = md.supportsSchemasInTableDefinitions();
		} catch (SQLException e)
		{
			log.info("Encountered unexpected exception while attempting to "
			      + "determine if schemas are used in table definitions");
		}
		if (!useCatalog && !useSchema)
		{
			return object;
		}
		if ((catalog == null || catalog.equals("")) && (schema == null || schema.equals("")))
		{
			return object;
		}
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
			ISQLDatabaseMetaData md = session.getMetaData();
			catsep = md.getCatalogSeparator();
		} catch (SQLException e)
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
	 *        the session whose disposition on case we care about.
	 * @param identifier
	 * @return
	 */
	public static String fixCase(ISession session, String identifier)
	{
		if (identifier == null || identifier.equals(""))
		{
			return identifier;
		}
		try
		{
			DatabaseMetaData md = session.getSQLConnection().getConnection().getMetaData();

			// Don't change the case of the identifier if database allows mixed
			// case.
			if (md.storesMixedCaseIdentifiers())
			{
				return identifier;
			}
			// Fix the case according to what the database tells us.
			if (md.storesUpperCaseIdentifiers())
			{
				return identifier.toUpperCase();
			} else
			{
				return identifier.toLowerCase();
			}
		} catch (SQLException e)
		{
			if (log.isDebugEnabled())
			{
				log.debug("fixCase: unexpected exception: " + e.getMessage());
			}
			return identifier;
		}
	}

	/**
	 * Generates a string of question marks which are used for creating PreparedStatements. The question marks
	 * are delimited by commas.
	 * 
	 * @param count
	 *        the number of question marks (representing PS bind variables).
	 * @return
	 */
	private static String getQuestionMarks(int count)
	{
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < count; i++)
		{
			result.append("?");
			if (i < count - 1)
			{
				result.append(", ");
			}
		}
		return result.toString();
	}

	/**
	 * 
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
	 * 
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

	public static void validateColumnNames(ITableInfo ti, SessionInfoProvider prov) throws MappingException,
	      UserCancelledOperationException
	{
		if (prov == null)
		{
			return;
		}
		ISession sourceSession = prov.getSourceSession();
		ISession destSession = prov.getDestSession();
		if (sourceSession == null || destSession == null)
		{
			return;
		}
		ISQLConnection sourceCon = sourceSession.getSQLConnection();
		ISQLConnection con = destSession.getSQLConnection();
		TableColumnInfo[] colInfoArr = null;
		try
		{
			colInfoArr = sourceCon.getSQLMetaData().getColumnInfo(ti);
		} catch (SQLException e)
		{
			// ignore any SQLExceptions. This would only if we could not get
			// column info from the SQL database meta data.
			return;
		}
		for (int colIdx = 0; colIdx < colInfoArr.length; colIdx++)
		{
			TableColumnInfo colInfo = colInfoArr[colIdx];
			IDatabaseObjectInfo selectedDestObj = prov.getDestDatabaseObject();
			String schema = selectedDestObj.getSimpleName();
			String catalog = selectedDestObj.getCatalogName();
			String tableName = getQualifiedObjectName(
			   destSession, catalog, schema, TEST_TABLE_NAME, DialectFactory.DEST_TYPE);

			StringBuilder sql = new StringBuilder("CREATE TABLE ");
			sql.append(tableName);
			sql.append(" ( ");
			sql.append(colInfo.getColumnName());
			sql.append(" CHAR(10) )");
			boolean cascade = DialectFactory.isFrontBase(destSession.getMetaData());
			try
			{
				dropTable(TEST_TABLE_NAME, schema, catalog, destSession, cascade, DialectFactory.DEST_TYPE);
				DBUtil.executeUpdate(con, sql.toString(), false);
			} catch (SQLException e)
			{
				String message = getMessage("DBUtil.mappingErrorKeyword", new String[]
					{ ti.getSimpleName(), colInfo.getColumnName() });
				log.error(message, e);
				throw new MappingException(message);
			} finally
			{
				dropTable(tableName, schema, catalog, destSession, cascade, DialectFactory.DEST_TYPE);
			}

		}
	}

	public static boolean dropTable(String tableName, String schemaName, String catalogName, ISession session,
	      boolean cascade, int sessionType) throws UserCancelledOperationException
	{
		boolean result = false;
		ISQLConnection con = session.getSQLConnection();
		String table = getQualifiedObjectName(session, catalogName, schemaName, tableName, sessionType);
		String dropsql = "DROP TABLE " + table;
		if (cascade)
		{
			dropsql += " CASCADE";
		}
		try
		{
			DBUtil.executeUpdate(con, dropsql, false);
			result = true;
		} catch (SQLException e)
		{
			/* Do nothing */
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
	 *        TODO
	 * @return
	 */
	public static String getMaxColumnLengthSQL(ISession sourceSession, TableColumnInfo colInfo,
	      String tableName, boolean tableNameIsQualified) throws UserCancelledOperationException
	{
		StringBuilder result = new StringBuilder();
		HibernateDialect dialect = DialectFactory.getDialect(
		   DialectFactory.SOURCE_TYPE, sourceSession.getApplication().getMainFrame(),
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
			table = getQualifiedObjectName(
			   sourceSession, colInfo.getCatalogName(), colInfo.getSchemaName(), tableName,
			   DialectFactory.SOURCE_TYPE);
		}
		result.append(table);
		return result.toString();
	}

	/**
	 * @param lastStatement
	 *        the lastStatement to set
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
	
	public static List<ITableInfo> convertObjectToTableList(List<IDatabaseObjectInfo> objectInfoList) 
	{
		List<ITableInfo> result = new ArrayList<ITableInfo>();
		for (IDatabaseObjectInfo info : objectInfoList) {
			result.add((ITableInfo)info);
		}
		return result;
	}

	public static List<ITableInfo> convertObjectArrayToTableList(IDatabaseObjectInfo[] objectInfoArr) 
	{
		List<ITableInfo> selectedTables = new ArrayList<ITableInfo>();
		for (int i = 0; i < objectInfoArr.length; i++)
		{
			selectedTables.add((ITableInfo) objectInfoArr[i]);
		}
		return selectedTables;		
	}

	public static List<IDatabaseObjectInfo> convertTableToObjectList(List<ITableInfo> tableInfoList) 
	{
		List<IDatabaseObjectInfo> result = new ArrayList<IDatabaseObjectInfo>();
		for (IDatabaseObjectInfo info : tableInfoList) {
			result.add(info);
		}
		return result;
	}

   public static String getSchemaNameFromDbObject(IDatabaseObjectInfo dbObject)
   {
      String destSchema;

      if (dbObject.getDatabaseObjectType().equals(DatabaseObjectType.SCHEMA))
      {
         destSchema = dbObject.getSimpleName();
      }
      else
      {
         destSchema = dbObject.getSchemaName();
      }
      return destSchema;
   }

   public static IDatabaseObjectInfo getSchemaFromDbObject(IDatabaseObjectInfo dbObject, SchemaInfo schemaInfo)
   {
      if (dbObject.getDatabaseObjectType().equals(DatabaseObjectType.SCHEMA))
      {
         return dbObject;
      }
      else
      {
         return new DatabaseObjectInfo(dbObject.getCatalogName(),
               dbObject.getSchemaName(),
               dbObject.getSimpleName(),
               DatabaseObjectType.SCHEMA,
               schemaInfo.getSQLDatabaseMetaData());

      }
   }
}
