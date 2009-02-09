/*
 * Copyright (C) 2006 Rob Manning
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
package net.sourceforge.squirrel_sql.fw.dialects;

import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.CYCLE_CLAUSE;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.NO_CYCLE_CLAUSE;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.HibernateException;

/**
 * An extension to the standard Hibernate PostgreSQL dialect
 * 
 * @author manningr 
 */
public class PostgreSQLDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class PostgreSQLDialectHelper extends org.hibernate.dialect.PostgreSQLDialect {
		public PostgreSQLDialectHelper() {
			super();
			registerColumnType(Types.BIGINT, "bigint");
			registerColumnType(Types.BINARY, "bytea");
			// PostgreSQL follows the standard for SQL BIT. It's a string of BITs.
			// So bit(10) is a string of 10 bits. JDBC treats SQL BIT as if it
			// were only a single BIT. It specifies that BIT is equivalent to
			// BOOLEAN. It claims that the PreparedStatement set method that should
			// be used with BIT is setBoolean and getBoolean. This is not compliant
			// with the standard. So SQL BIT type support is broken in Java, there
			// is nothing we can do about that.
			// Best thing to do for now, is try to convert the BIT type to a
			// boolean like the JDBC spec says and hope for the best. Hope that the
			// source database isn't using the BIT column as a sequence of multiple
			// BITs.
			registerColumnType(Types.BIT, "bool");
			registerColumnType(Types.BLOB, "bytea");
			registerColumnType(Types.BOOLEAN, "bool");
			registerColumnType(Types.CHAR, 8000, "char($l)");
			registerColumnType(Types.CHAR, "text");
			registerColumnType(Types.CLOB, "text");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p,2)");
			registerColumnType(Types.DOUBLE, "float($p)");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "int");
			registerColumnType(Types.LONGVARBINARY, "bytea");
			registerColumnType(Types.LONGVARCHAR, "text");
			registerColumnType(Types.NUMERIC, "numeric($p)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "int");
			registerColumnType(Types.VARBINARY, "bytea");
			registerColumnType(Types.VARCHAR, 8000, "varchar($l)");
			registerColumnType(Types.VARCHAR, "text");			
		}
	}
	
	/** extended hibernate dialect used in this wrapper */
	private PostgreSQLDialectHelper _dialect = new PostgreSQLDialectHelper();

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getTypeName(int, int, int, int)
	 */
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#canPasteTo(net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType)
	 */
	public boolean canPasteTo(IDatabaseObjectInfo info)
	{
		boolean result = true;
		DatabaseObjectType type = info.getDatabaseObjectType();
		if (type.getName().equalsIgnoreCase("database"))
		{
			result = false;
		}
		return result;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#supportsSchemasInTableDefinition()
	 */
	public boolean supportsSchemasInTableDefinition()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getLengthFunction(int)
	 */
	public String getLengthFunction(int dataType)
	{
		return "length";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getMaxFunction()
	 */
	public String getMaxFunction()
	{
		return "max";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getMaxPrecision(int)
	 */
	public int getMaxPrecision(int dataType)
	{
		if (dataType == Types.DOUBLE || dataType == Types.FLOAT)
		{
			return 53;
		} else
		{
			return 38;
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getMaxScale(int)
	 */
	public int getMaxScale(int dataType)
	{
		return getMaxPrecision(dataType);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getPrecisionDigits(int, int)
	 */
	public int getPrecisionDigits(int columnSize, int dataType)
	{
		if (columnSize == 2)
		{
			return 5;
		}
		if (columnSize == 4)
		{
			return 10;
		}
		return 19;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getColumnLength(int, int)
	 */
	public int getColumnLength(int columnSize, int dataType)
	{
		if (dataType == Types.VARCHAR && columnSize == -1)
		{
			// PostgreSQL 8.0 reports length as -1 sometimes. Why??
			return 2000;
		}
		return columnSize;
	}

	/**
	 * The string which identifies this dialect in the dialect chooser.
	 * 
	 * @return a descriptive name that tells the user what database this dialect is design to work with.
	 */
	public String getDisplayName()
	{
		return "PostgreSQL";
	}

	/**
	 * Returns boolean value indicating whether or not this dialect supports the specified database
	 * product/version.
	 * 
	 * @param databaseProductName
	 *           the name of the database as reported by DatabaseMetaData.getDatabaseProductName()
	 * @param databaseProductVersion
	 *           the version of the database as reported by DatabaseMetaData.getDatabaseProductVersion()
	 * @return true if this dialect can be used for the specified product name and version; false otherwise.
	 */
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		if (databaseProductName.trim().toLowerCase().startsWith("postgresql"))
		{
			// We don't yet have the need to discriminate by version.
			return true;
		}
		return false;
	}

	/**
	 * Returns the SQL statement to use to add a column to the specified table using the information about the
	 * new column specified by info.
	 * 
	 * @param info
	 *           information about the new column such as type, name, etc.
	 * @return
	 * @throws UnsupportedOperationException
	 *            if the database doesn't support adding columns after a table has already been created.
	 */
//	public String[] getColumnAddSQL(TableColumnInfo info) throws UnsupportedOperationException
//	{
//		ArrayList<String> result = new ArrayList<String>();
//		result.add(DialectUtils.getColumnAddSQL(info, this, true, true, true));
//		if (info.getRemarks() != null && !"".equals(info.getRemarks()))
//		{
//			result.add(getColumnCommentAlterSQL(info));
//		}
//		return result.toArray(new String[result.size()]);
//	}

	/**
	 * Returns a boolean value indicating whether or not this dialect supports adding comments to columns.
	 * 
	 * @return true if column comments are supported; false otherwise.
	 */
	public boolean supportsColumnComment()
	{
		return true;
	}

	/**
	 * Returns a boolean value indicating whether or not this database dialect supports dropping columns from
	 * tables.
	 * 
	 * @return true if the database supports dropping columns; false otherwise.
	 */
	public boolean supportsDropColumn()
	{
		return true;
	}

	/**
	 * Returns the SQL that forms the command to drop the specified colum in the specified table.
	 * 
	 * @param tableName
	 *           the name of the table that has the column
	 * @param columnName
	 *           the name of the column to drop.
	 * @return
	 * @throws UnsupportedOperationException
	 *            if the database doesn't support dropping columns.
	 */
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getColumnDropSQL(tableName, columnName, qualifier, prefs, this);
	}

	/**
	 * Returns the SQL that forms the command to drop the specified table. If cascade contraints is supported
	 * by the dialect and cascadeConstraints is true, then a drop statement with cascade constraints clause
	 * will be formed.
	 * 
	 * @param iTableInfo
	 *           the table to drop
	 * @param cascadeConstraints
	 *           whether or not to drop any FKs that may reference the specified table.
	 * @return the drop SQL command.
	 */
	public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints,
		boolean isMaterializedView, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getTableDropSQL(iTableInfo,
			true,
			cascadeConstraints,
			false,
			DialectUtils.CASCADE_CLAUSE,
			false, qualifier, prefs, this);
	}

	/**
	 * Returns the SQL that forms the command to add a primary key to the specified table composed of the given
	 * column names.
	 * 
	 * @param pkName
	 *           the name of the constraint
	 * @param columnNames
	 *           the columns that form the key
	 * @return
	 */
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] colInfos, ITableInfo ti, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return new String[] { DialectUtils.getAddPrimaryKeySQL(ti, pkName, colInfos, false, qualifier, prefs, this) };
	}

	/**
	 * Returns the SQL statement to use to add a comment to the specified column of the specified table.
	 * 
	 * @param info
	 *           information about the column such as type, name, etc.
	 * @return
	 * @throws UnsupportedOperationException
	 *            if the database doesn't support annotating columns with a comment.
	 */
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		prefs.setQuoteColumnNames(false);
		return DialectUtils.getColumnCommentAlterSQL(info.getTableName(),
			info.getColumnName(),
			info.getRemarks(), qualifier, prefs, this);

	}

	/**
	 * Returns the SQL used to alter the specified column to not allow null values ALTER TABLE products ALTER
	 * COLUMN product_no SET NOT NULL ALTER TABLE products ALTER COLUMN product_no DROP NOT NULL
	 * 
	 * @param info
	 *           the column to modify
	 * @return the SQL to execute
	 */
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(info.getTableName());
		result.append(" ALTER COLUMN ");
		result.append(info.getColumnName());
		if (info.isNullable().equalsIgnoreCase("YES"))
		{
			result.append(" DROP NOT NULL");
		} else
		{
			result.append(" SET NOT NULL");
		}
		return new String[] { result.toString() };
	}

	/**
	 * Returns a boolean value indicating whether or not this database dialect supports renaming columns.
	 * 
	 * @return true if the database supports changing the name of columns; false otherwise.
	 */
	public boolean supportsRenameColumn()
	{
		return true;
	}

	/**
	 * Returns the SQL that is used to change the column name. ALTER TABLE a RENAME COLUMN x TO y
	 * 
	 * @param from
	 *           the TableColumnInfo as it is
	 * @param to
	 *           the TableColumnInfo as it wants to be
	 * @return the SQL to make the change
	 */
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.RENAME_COLUMN_CLAUSE;
		String toClause = DialectUtils.TO_CLAUSE;
		return DialectUtils.getColumnNameAlterSQL(from, to, alterClause, toClause, qualifier, prefs, this);
	}

	/**
	 * Returns a boolean value indicating whether or not this dialect supports modifying a columns type.
	 * 
	 * @return true if supported; false otherwise
	 */
	public boolean supportsAlterColumnType()
	{
		return true;
	}

	/**
	 * Returns the SQL that is used to change the column type. ALTER TABLE products ALTER COLUMN price TYPE
	 * numeric(10,2);
	 * 
	 * @param from
	 *           the TableColumnInfo as it is
	 * @param to
	 *           the TableColumnInfo as it wants to be
	 * @return the SQL to make the change
	 * @throw UnsupportedOperationException if the database doesn't support modifying column types.
	 */
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
		throws UnsupportedOperationException
	{
		ArrayList<String> list = new ArrayList<String>();
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(from.getTableName());
		result.append(" ALTER COLUMN ");
		result.append(to.getColumnName());
		result.append(" TYPE ");
		result.append(DialectUtils.getTypeName(to, this));
		list.add(result.toString());
		return list;
	}

	/**
	 * Returns a boolean value indicating whether or not this database dialect supports changing a column from
	 * null to not-null and vice versa.
	 * 
	 * @return true if the database supports dropping columns; false otherwise.
	 */
	public boolean supportsAlterColumnNull()
	{
		return true;
	}

	/**
	 * Returns a boolean value indicating whether or not this database dialect supports changing a column's
	 * default value.
	 * 
	 * @return true if the database supports modifying column defaults; false otherwise
	 */
	public boolean supportsAlterColumnDefault()
	{
		return true;
	}

	/**
	 * Returns the SQL command to change the specified column's default value
	 * 
	 * @param info
	 *           the column to modify and it's default value.
	 * @return SQL to make the change
	 */
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(DialectUtils.shapeQualifiableIdentifier(info.getTableName(), qualifier, prefs, this));
		result.append(" ALTER COLUMN ");
		result.append(info.getColumnName());
		String defVal = info.getDefaultValue();
		if (defVal == null || "".equals(defVal))
		{
			result.append(" DROP DEFAULT");
		} else
		{
			result.append(" SET DEFAULT ");
			if (JDBCTypeMapper.isNumberType(info.getDataType()))
			{
				result.append(defVal);
			} else
			{
				result.append("'");
				result.append(defVal);
				result.append("'");
			}
		}
		return result.toString();
	}

	/**
	 * Returns the SQL command to drop the specified table's primary key.
	 * 
	 * @param pkName
	 *           the name of the primary key that should be dropped
	 * @param tableName
	 *           the name of the table whose primary key should be dropped
	 * @return
	 */
	public String getDropPrimaryKeySQL(String pkName, String tableName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, true, false, qualifier, prefs, this);
	}

	/**
	 * Returns the SQL command to drop the specified table's foreign key constraint.
	 * 
	 * @param fkName
	 *           the name of the foreign key that should be dropped
	 * @param tableName
	 *           the name of the table whose foreign key should be dropped
	 * @return
	 */
	public String getDropForeignKeySQL(String fkName, String tableName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropForeignKeySQL(fkName, tableName, qualifier, prefs, this);
	}

	/**
	 * Returns the SQL command to create the specified table.
	 * 
	 * @param tables
	 *           the tables to get create statements for
	 * @param md
	 *           the metadata from the ISession
	 * @param prefs
	 *           preferences about how the resultant SQL commands should be formed.
	 * @param isJdbcOdbc
	 *           whether or not the connection is via JDBC-ODBC bridge.
	 * @return the SQL that is used to create the specified table
	 */
	public List<String> getCreateTableSQL(List<ITableInfo> tables, ISQLDatabaseMetaData md,
		CreateScriptPreferences prefs, boolean isJdbcOdbc) throws SQLException
	{
		return DialectUtils.getCreateTableSQL(tables, md, this, prefs, isJdbcOdbc);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDialectType()
	 */
	public DialectType getDialectType()
	{
		return DialectType.POSTGRES;
	}

	/**
	 * Access Methods Field. Values "btree", "hash", "gist", "gin".
	 */
	private static final String[] ACCESS_METHODS = { "btree", "hash", "gist", "gin" };

	/**
	 * Shapes the table name depending on the prefereneces. If isQualifyTableNames is true, the qualified name
	 * of the table is returned.
	 * 
	 * @param identifier
	 *           identifier to be shaped
	 * @param qualifier
	 *           qualifier of the identifier
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return the shaped table name
	 */
	private String shapeQualifiableIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.shapeQualifiableIdentifier(identifier, qualifier, prefs, this);
	}

	/**
	 * Shapes the identifier depending on the preferences. If isQuoteIdentifiers is true, the identifier is
	 * quoted with dialect-specific delimiters.
	 * 
	 * @param identifier
	 *           identifier to be shaped
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return the shaped identifier
	 */
	private String shapeIdentifier(String identifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.shapeIdentifier(identifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsSequence()
	 */
	public boolean supportsSequence()
	{
		return _dialect.supportsSequences();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsCheckOptionsForViews()
	 */
	public boolean supportsCheckOptionsForViews()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsIndexes()
	 */
	public boolean supportsIndexes()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsTablespace()
	 */
	public boolean supportsTablespace()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAccessMethods()
	 */
	public boolean supportsAccessMethods()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsDropView()
	 */
	public boolean supportsDropView()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsRenameView()
	 */
	public boolean supportsRenameView()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAutoIncrement()
	 */
	public boolean supportsAutoIncrement()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsEmptyTables()
	 */
	public boolean supportsEmptyTables()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsMultipleRowInserts()
	 */
	public boolean supportsMultipleRowInserts()
	{
		/* Can be done as follows in Postgres
			INSERT INTO mytable(id,name) 
			  SELECT 1, 'pizza'
			  UNION
			  SELECT 2, 'donuts'
			  UNION
			  SELECT 3, 'milk';
		 */
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAddForeignKeyConstraint()
	 */
	public boolean supportsAddForeignKeyConstraint()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAddUniqueConstraint()
	 */
	public boolean supportsAddUniqueConstraint()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAlterSequence()
	 */
	public boolean supportsAlterSequence()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsCreateIndex()
	 */
	public boolean supportsCreateIndex()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsCreateSequence()
	 */
	public boolean supportsCreateSequence()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsCreateTable()
	 */
	public boolean supportsCreateTable()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsCreateView()
	 */
	public boolean supportsCreateView()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsDropConstraint()
	 */
	public boolean supportsDropConstraint()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsDropIndex()
	 */
	public boolean supportsDropIndex()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsDropSequence()
	 */
	public boolean supportsDropSequence()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsInsertInto()
	 */
	public boolean supportsInsertInto()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsRenameTable()
	 */
	public boolean supportsRenameTable()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsSequenceInformation()
	 */
	public boolean supportsSequenceInformation()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsUpdate()
	 */
	public boolean supportsUpdate()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getIndexAccessMethodsTypes()
	 */
	public String[] getIndexAccessMethodsTypes()
	{
		return ACCESS_METHODS;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getIndexStorageOptions()
	 */
	public String[] getIndexStorageOptions()
	{
		return null;
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getCreateTableSQL(java.lang.String,
	 *      java.util.List, java.util.List, net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier)
	 */
	public String getCreateTableSQL(String simpleName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{
		return DialectUtils.getCreateTableSQL(simpleName, columns, primaryKeys, prefs, qualifier, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getRenameTableSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// ALTER TABLE oldTableName RENAME TO newTableName;
		StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.ALTER_TABLE_CLAUSE);
		sql.append(" ");
		sql.append(DialectUtils.shapeQualifiableIdentifier(oldTableName, qualifier, prefs, this));
		sql.append(" RENAME TO ");
		// This is a work-around for what looks like a bug to me - it is a syntax error to qualify with the 
		// schema the new table name, but not the old as in:
		//
		// ALTER TABLE "public"."tablerenametest" RENAME TO "public"."tablewasrenamed"
		//
		// The second "public". is apparently not (currently) a valid thing to do.
		
		//sql.append(DialectUtils.shapeQualifiableIdentifier(newTableName, qualifier, prefs, this));

		sql.append(DialectUtils.shapeIdentifier(newTableName, prefs, this));
		
		return sql.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getCreateViewSQL(java.lang.String,
	 *      java.lang.String, java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getCreateViewSQL(viewName, definition, checkOption, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getRenameViewSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getRenameViewSQL(String oldViewName, String newViewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// rename view has that same syntax as that of tables.
		return new String[] { 
			getRenameTableSQL(oldViewName, newViewName, qualifier, prefs) 
		};
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropViewSQL(java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropViewSQL(viewName, cascade, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getCreateIndexSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String[], boolean, java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// From PostgreSQL 8.2 documentation:
		// CREATE [ UNIQUE ] INDEX [ CONCURRENTLY ] name ON table [ USING method ]
		// 	( { column | ( expression ) } [ opclass ] [, ...] )
		// 	[ WITH ( storage_parameter = value [, ... ] ) ]
		// 	[ TABLESPACE tablespace ]
		// 	[ WHERE predicate ]
		
		String result = "";
		if (unique && accessMethod.equalsIgnoreCase("hash")) {
			String uniqueHashAccessMethodNotSupported = null;
			
			result =
				DialectUtils.getAddIndexSQL(this,
					indexName,
					tableName,
					uniqueHashAccessMethodNotSupported,
					columns,
					unique,
					tablespace,
					constraints,
					qualifier,
					prefs);			
		} else {
			result =
				DialectUtils.getAddIndexSQL(this,
					indexName,
					tableName,
					accessMethod,
					columns,
					unique,
					tablespace,
					constraints,
					qualifier,
					prefs);
		}
		return result;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropIndexSQL(String,
	 *      java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier, net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// DROP INDEX indexName CASCADE;
		return DialectUtils.getDropIndexSQL(indexName, cascade, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getCreateSequenceSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String cycleCause = cycle ? CYCLE_CLAUSE : NO_CYCLE_CLAUSE;
		return DialectUtils.getCreateSequenceSQL(sequenceName,
			increment,
			minimum,
			maximum,
			start,
			cache,
			cycleCause,
			qualifier,
			prefs,
			this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAlterSequenceSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String cycleClause = cycle ? CYCLE_CLAUSE : NO_CYCLE_CLAUSE;
		String sql =
			DialectUtils.getAlterSequenceSQL(sequenceName,
				increment,
				minimum,
				maximum,
				restart,
				cache,
				cycleClause,
				qualifier,
				prefs,
				this);
		return new String[] { sql };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getSequenceInformationSQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// SELECT last_value, max_value, min_value, cache_value, increment_by, is_cycled FROM sequenceName;
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT last_value, max_value, min_value, cache_value, increment_by, is_cycled FROM ");
		sql.append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs));

		return sql.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropSequenceSQL(java.lang.String,
	 *      boolean, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropSequenceSQL(sequenceName, cascade, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddForeignKeyConstraintSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.Boolean, java.lang.Boolean, java.lang.Boolean,
	 *      boolean, java.lang.String, java.util.Collection, java.lang.String, java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getAddForeignKeyConstraintSQL(localTableName,
			refTableName,
			constraintName,
			deferrable,
			initiallyDeferred,
			matchFull,
			autoFKIndex,
			fkIndexName,
			localRefColumns,
			onUpdateAction,
			onDeleteAction,
			qualifier,
			prefs,
			this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddUniqueConstraintSQL(java.lang.String,
	 *      java.lang.String, TableColumnInfo[],
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName, TableColumnInfo[] columns,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return new String[] { DialectUtils.getAddUniqueConstraintSQL(tableName,
			constraintName,
			columns,
			qualifier,
			prefs,
			this) };
	}

	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		ArrayList<String> result = new ArrayList<String>();

		// ALTER TABLE tableName
		// ALTER COLUMN columnName
		// SET DEFAULT nextval('tableName_columnName_seq');
		// ALTER SEQUENCE tableName_columnName_seq OWNED BY tableName.columnName;

		// In PostgreSQL we need to add a sequence to support auto-increment (name: tablename_colname_seq)
		String sequenceName = column.getTableName() + "_" + column.getColumnName() + "_seq";
		String sequenceSQL =
			getCreateSequenceSQL(sequenceName, null, null, null, null, null, false, qualifier, prefs);
		result.add(sequenceSQL);

		StringBuilder sql = new StringBuilder();
		sql.append(DialectUtils.ALTER_TABLE_CLAUSE);
		sql.append(" ");
		sql.append(shapeQualifiableIdentifier(column.getTableName(), qualifier, prefs)).append("\n");

		sql.append(" ");
		sql.append(DialectUtils.ALTER_COLUMN_CLAUSE);
		sql.append(" ");
		sql.append(shapeIdentifier(column.getColumnName(), prefs)).append("\n");
		sql.append(" ");
		sql.append(DialectUtils.SET_DEFAULT_CLAUSE + " nextval('");
		sql.append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs)).append("')");
		result.add(sql.toString());

		sql = new StringBuilder();
		sql.append(DialectUtils.ALTER_SEQUENCE_CLAUSE + " ");
		sql.append(" ");
		sql.append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs)).append("\n");
		sql.append(" OWNED BY ");
		sql.append(shapeQualifiableIdentifier(column.getTableName(), qualifier, prefs));
		sql.append(".");
		sql.append(shapeIdentifier(column.getColumnName(), prefs));
		result.add(sql.toString());

		return result.toArray(new String[result.size()]);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropConstraintSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropConstraintSQL(tableName, constraintName, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getInsertIntoSQL(java.lang.String,
	 *      java.util.List, java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getInsertIntoSQL(String tableName, List<String> columns, String query,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getInsertIntoSQL(tableName, columns, query, qualifier, prefs, this);
	}

	public String[] getUpdateSQL(String tableName, String[] setColumns, String[] setValues, String[] fromTables,
		String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		if ((setColumns == null && setValues == null)
			|| (setColumns != null && setValues != null && setColumns.length == 0 && setValues.length == 0))
		{
			return new String[] {};
		}
		if ((setColumns != null && setValues != null && setColumns.length != setValues.length)
			|| setColumns == null || setValues == null)
		{
			throw new IllegalArgumentException("The amount of SET columns and values must be the same!");
		}
		if ((whereColumns != null && whereValues != null && whereColumns.length != whereValues.length)
			|| (whereColumns == null && whereValues != null) || (whereColumns != null && whereValues == null))
		{
			throw new IllegalArgumentException("The amount of WHERE columns and values must be the same!");
		}
		// UPDATE tableName SET setColumn1 = setValue1, setColumn2 = setValue2
		// FROM fromTable1, fromTable2
		// WHERE whereColumn1 = whereValue1 AND whereColumn2 = whereValue2;
		StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.UPDATE_CLAUSE + " ");
		sql.append(shapeQualifiableIdentifier(tableName, qualifier, prefs));
		sql.append(" " + DialectUtils.SET_CLAUSE + " ");
		for (int i = 0; i < setColumns.length; i++)
		{
			sql.append(shapeIdentifier(setColumns[i], prefs));
			if (setValues[i] == null)
				sql.append(" = NULL");
			else
				sql.append(" = ").append(setValues[i]);
			sql.append(", ");
		}
		sql.setLength(sql.length() - 2);

		if (fromTables != null)
		{
			sql.append("\n " + DialectUtils.FROM_CLAUSE + " ");
			for (String from : fromTables)
			{
				sql.append(shapeQualifiableIdentifier(from, qualifier, prefs)).append(", ");
			}
			sql.setLength(sql.length() - 2);
		}

		if (whereColumns != null && whereColumns.length != 0)
		{
			sql.append("\n " + DialectUtils.WHERE_CLAUSE + " ");
			for (int i = 0; i < whereColumns.length; i++)
			{
				sql.append(shapeIdentifier(whereColumns[i], prefs));
				if (whereValues[i] == null)
					sql.append(" IS NULL");
				else
					sql.append(" = ").append(whereValues[i]);
				sql.append(" " + DialectUtils.AND_CLAUSE + " ");
			}
			sql.setLength(sql.length() - 5);
		}

		return new String[] { sql.toString() };
	}

	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();		
		boolean addDefaultClause = true;
		boolean supportsNullQualifier = true;
		boolean addNullClause = true;
		
		result.add(DialectUtils.getAddColumSQL(column,
			this,
			addDefaultClause,
			supportsNullQualifier,
			addNullClause,
			qualifier,
			prefs));
		

		if (column.getRemarks() != null && !"".equals(column.getRemarks()))
		{
			result.add(getColumnCommentAlterSQL(column, qualifier, prefs));
		}		
		return result.toArray(new String[result.size()]);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAddColumn()
	 */
	public boolean supportsAddColumn()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsViewDefinition()
	 */
	public boolean supportsViewDefinition() {
		return true;
	}	
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getViewDefinitionSQL(java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier, net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) {
		
      StringBuilder result = new StringBuilder();
      result.append("select view_definition from information_schema.views where table_schema = '");
      result.append(qualifier.getSchema());
      result.append("' and table_name =  '");
      result.append(viewName);
      result.append("'");
      return result.toString();
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getQualifiedIdentifier(java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier, net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getQualifiedIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		StringBuilder result = new StringBuilder();
		if (prefs.isQualifyTableNames()) {
			if (prefs.isQuoteIdentifiers()) {
				result.append(this.openQuote());
			}
			result.append(qualifier.getSchema());
			if (prefs.isQuoteIdentifiers()) {
				result.append(this.closeQuote());
			} 
			result.append(".");
		}
		if (prefs.isQuoteIdentifiers()) {
			result.append(this.openQuote());
		}
		result.append(identifier);
		if (prefs.isQuoteIdentifiers()) {
			result.append(this.closeQuote());
		}
		return result.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsCorrelatedSubQuery()
	 */
	public boolean supportsCorrelatedSubQuery()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getJavaTypeForNativeType(java.lang.String)
	 */
	@Override
	public int getJavaTypeForNativeType(String nativeColumnTypeName)
	{
		if ("character_data".equalsIgnoreCase(nativeColumnTypeName)) {
			return java.sql.Types.CHAR;
		}
		if ("cardinal_number".equalsIgnoreCase(nativeColumnTypeName)) {
			return java.sql.Types.INTEGER;
		}
		if ("xml".equalsIgnoreCase(nativeColumnTypeName)) {
			return java.sql.Types.VARCHAR;
		}
		return super.getJavaTypeForNativeType(nativeColumnTypeName);
	}
	
}
