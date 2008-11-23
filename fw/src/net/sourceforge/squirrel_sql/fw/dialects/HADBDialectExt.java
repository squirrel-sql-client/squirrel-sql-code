/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;

/**
 * A dialect delegate for the Sun Microsystems HADB (High-Availability) database.
 * TODO: This dialect is not yet complete.  Need to provide implementations wherever "Not yet implemented"
 * appears.
 * 
 * @author manningr
 */
public class HADBDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class HADBDialectHelper extends Dialect
	{
		public HADBDialectHelper()
		{
			registerColumnType(Types.BIGINT, "double integer");
			registerColumnType(Types.BINARY, 8000, "binary($l)");
			registerColumnType(Types.BINARY, "binary(8000)");
			registerColumnType(Types.BIT, "smallint");
			registerColumnType(Types.BOOLEAN, "smallint");
			registerColumnType(Types.BLOB, "blob");
			registerColumnType(Types.CHAR, 8000, "char($l)");
			registerColumnType(Types.CHAR, "char(8000)");
			registerColumnType(Types.CLOB, "clob");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p,$s)");
			registerColumnType(Types.DOUBLE, "double precision");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "integer");
			registerColumnType(Types.LONGVARBINARY, "blob");
			registerColumnType(Types.LONGVARCHAR, "clob");
			registerColumnType(Types.NUMERIC, "decimal($p,$s)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "smallint");
			registerColumnType(Types.VARBINARY, 8000, "varbinary($l)");
			registerColumnType(Types.VARBINARY, "varbinary(8000)");
			registerColumnType(Types.VARCHAR, 8000, "varchar($l)");
			registerColumnType(Types.VARCHAR, "varchar(8000)");
		}
	}

	/** extended hibernate dialect used in this wrapper */
	private HADBDialectHelper _dialect = new HADBDialectHelper();

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getTypeName(int, int, int, int)
	 */
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}

	/**
	 * Returns a boolean indicating whether or not the specified database object can be pasted into for this
	 * database dialect. Some databases support the notion of schemas where tables live, and in those cases
	 * pasting to a database object in the object tree is not really appropriate. However, other databases
	 * don't support schemas (like Axion, Firebird)
	 * 
	 * @param info
	 * @return
	 */
	public boolean canPasteTo(IDatabaseObjectInfo info)
	{
		return true;
	}

	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] colInfos, ITableInfo ti,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * Returns the SQL that forms the command to drop the specified colum in the specified table.
	 * 
	 * @param tableName
	 *           the name of the table that has the column
	 * @param columnName
	 *           the name of the column to drop.
	 * @return
	 * @throw UnsupportedOperationException if the database doesn't support dropping columns.
	 */
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * Some jdbc drivers are hopelessly broken with regard to reporting the COLUMN_SIZE. For example, MaxDB has
	 * a "long byte" data type which can store up to 2G of data, yet the driver reports that the column size is
	 * "8" - real helpful. So for drivers that have this problem, return the "proper" maximum column length for
	 * the specified dataType. If the driver doesn't have this problem, just return the columnSize.
	 * 
	 * @param columnSize
	 *           the size of the column as reported by the jdbc driver
	 * @param dataType
	 *           the type of the column.
	 * @return the specified columnSize if the jdbc driver isn't broken; otherwise, the maximum column size for
	 *         the specified dataType if the driver is broken.
	 */
	public int getColumnLength(int columnSize, int dataType)
	{
		// HADB reports "10" for column size of BLOB/CLOB
		if (dataType == Types.CLOB || dataType == Types.BLOB) { return Integer.MAX_VALUE; // 2GB (2^32)
		}
		return columnSize;
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
	 * Returns the SQL that is used to change the column name. alter table test rename column mycol mycol2
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
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(from.getTableName());
		result.append(" RENAME COLUMN ");
		result.append(from.getColumnName());
		result.append(" ");
		result.append(to.getColumnName());
		return result.toString();
	}

	/**
	 * Returns the SQL used to alter the nullability of the specified column
	 * 
	 * @param info
	 *           the column to modify
	 * @return the SQL to execute
	 */
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		throw new UnsupportedOperationException("Not yet implemented");
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
	 * Returns the SQL that is used to change the column type.
	 * 
	 * @param from
	 *           the TableColumnInfo as it is
	 * @param to
	 *           the TableColumnInfo as it wants to be
	 * @return the SQL to make the change
	 * @throw UnsupportedOperationException if the database doesn't support modifying column types.
	 */
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * The string which identifies this dialect in the dialect chooser.
	 * 
	 * @return a descriptive name that tells the user what database this dialect is design to work with.
	 */
	public String getDisplayName()
	{
		return "Sun HADB";
	}

	/**
	 * Returns the name of the function that measures the length of a character string.
	 * 
	 * @param dataType
	 *           the java.sql.Types data type. Some databases have different length functions for different
	 *           data types.
	 * @return the name of the function to be applied to a column to determine the length of that column in a
	 *         particular record.
	 */
	public String getLengthFunction(int dataType)
	{
		return "char_length";
	}

	/**
	 * Returns the name of the aggregate function that determines the max value of an expression.
	 * 
	 * @return the name of the function to be applied to a set to determine the element with the highest
	 *         numeric value.
	 */
	public String getMaxFunction()
	{
		return "max";
	}

	/**
	 * Returns the maximum precision allowed by the database for number type fields that specify the length of
	 * the number to the left of the decimal point in digits. If the HibernateDialect implementation doesn't
	 * ever use $p in any call to registerColumnType(), then this maximum precsision will not be used.
	 * 
	 * @param dataType
	 *           the java.sql.Types data type.
	 * @return the maximum number that can be used in a column declaration for precision for the specified
	 *         type.
	 */
	public int getMaxPrecision(int dataType)
	{
		if (dataType == Types.FLOAT) { return 52; }
		if (dataType == Types.DECIMAL || dataType == Types.NUMERIC) { return 31; }
		return 0;
	}

	/**
	 * Returns the maximum scale allowed by the database for number type fields that specify the length of the
	 * number to the right of the decimal point in digits. If the HibernateDialect implementation doesn't ever
	 * use $s in any call to registerColumnType(), then this maximum scale will not be used.
	 * 
	 * @param dataType
	 *           the java.sql.Types data type.
	 * @return the maximum number that can be used in a column declaration for scale for the specified type.
	 */
	public int getMaxScale(int dataType)
	{
		return getMaxPrecision(dataType);
	}

	/**
	 * Returns the number of digits of precision is represented by the specifed columnSize for the specified
	 * dataType. Some DBs represent precision as the total number of digits on the right or left of the
	 * decimal. That is what we want. Others (like PostgreSQL) give the number of bytes of storage a column can
	 * use - less than useful, since the SQL-92 says "number of digits" and this is what most other DBs use.
	 * 
	 * @param columnSize
	 *           the size of the column as reported by the driver.
	 * @param dataType
	 *           the java.sql.Types data type.
	 * @return a number indicating the total number of digits (includes both sides of the decimal point) the
	 *         column can represent.
	 */
	public int getPrecisionDigits(int columnSize, int dataType)
	{
		return columnSize;
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
		return DialectUtils.getTableDropSQL(iTableInfo, false, cascadeConstraints, false,
			DialectUtils.CASCADE_CLAUSE, false, qualifier, prefs, this);
	}

	/**
	 * Returns a boolean value indicating whether or not this dialect supports adding comments to columns.
	 * 
	 * @return true if column comments are supported; false otherwise.
	 */
	public boolean supportsColumnComment()
	{
		return false;
	}

	/**
	 * Returns a boolean value indicating whether or not this database dialect supports dropping columns from
	 * tables.
	 * 
	 * @return true if the database supports dropping columns; false otherwise.
	 */
	public boolean supportsDropColumn()
	{
		return false;
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
		if (databaseProductName == null) { return false; }
		String prodName = "sun java system high availability";
		if (databaseProductName.trim().toLowerCase().startsWith(prodName))
		{
			// We don't yet have the need to discriminate by version.
			return true;
		}
		return false;
	}

	public boolean supportsSchemasInTableDefinition()
	{

		return false;
	}

	/**
	 * Returns a boolean value indicating whether or not this database dialect supports changing a column from
	 * null to not-null and vice versa.
	 * 
	 * @return true if the database supports dropping columns; false otherwise.
	 */
	public boolean supportsAlterColumnNull()
	{

		return false;
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
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		throw new UnsupportedOperationException("Not yet implemented");
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
	public String getDropPrimaryKeySQL(String pkName, String tableName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false, qualifier, prefs, this);
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
	public String getDropForeignKeySQL(String fkName, String tableName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
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
		return DialectType.HADB;
	}

	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final String msg = DialectUtils.getUnsupportedMessage(this, DialectUtils.ADD_COLUMN_TYPE);
		throw new UnsupportedOperationException(msg);
	}

	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public String[] getUpdateSQL(String tableName, String[] setColumns, String[] setValues,
		String[] fromTables, String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		throw new UnsupportedOperationException("Not yet implemented");
	}

	public boolean supportsCreateTable()
	{

		return false;
	}

	public boolean supportsDropView()
	{

		return false;
	}

	public boolean supportsInsertInto()
	{

		return false;
	}

	public boolean supportsRenameTable()
	{

		return false;
	}

	public boolean supportsRenameView()
	{

		return false;
	}

	public boolean supportsUpdate()
	{

		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAddColumn()
	 */
	public boolean supportsAddColumn()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getViewDefinitionSQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getQualifiedIdentifier(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getQualifiedIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return identifier;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsCorrelatedSubQuery()
	 */
	public boolean supportsCorrelatedSubQuery()
	{

		return false;
	}

}
