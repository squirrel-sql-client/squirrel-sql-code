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

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.HibernateException;

/**
 * An interface for methods implemented by database dialects for the purpose of
 * handling standard and non-standard SQL and database types.
 */
public interface HibernateDialect extends StringTemplateConstants {

	/**
	 * Get the name of the database type associated with the given
	 * <tt>java.sql.Types</tt> typecode.
	 * 
	 * @param code
	 *            <tt>java.sql.Types</tt> typecode
	 * @param length
	 *            the length or precision of the column
	 * @param precision
	 *            the precision of the column
	 * @param scale
	 *            the scale of the column
	 * @return the database type name
	 * @throws HibernateException
	 */
	String getTypeName(int code, int length, int precision, int scale)
			throws HibernateException;

	/**
	 * Get the name of the database type associated with the given
	 * {@link java.sql.Types} typecode.
	 * 
	 * @param code
	 *            The {@link java.sql.Types} typecode
	 * @return the database type name
	 * @throws HibernateException
	 *             If no mapping was specified for that type.
	 */
	public String getTypeName(int code) throws HibernateException;

	/**
	 * Returns a boolean indicating whether or not the specified database object
	 * can be pasted into for this database dialect. Some databases support the
	 * notion of schemas where tables live, and in those cases pasting to a
	 * database object in the object tree is not really appropriate. However,
	 * other databases don't support schemas (like Axion, Firebird)
	 * 
	 * @param info
	 * @return
	 */
	boolean canPasteTo(IDatabaseObjectInfo info);

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports table creation statements where the table name is prefixed by
	 * the schema.
	 * 
	 * @return
	 */
	boolean supportsSchemasInTableDefinition();

	/**
	 * Returns the string that should be appended to a column declaration when
	 * nulls are allowed in the column.
	 * 
	 * @return the "nulls are allowed in this column" string for a table
	 *         declaration
	 */
	String getNullColumnString();

	/**
	 * Returns the name of the aggregate function that determines the max value
	 * of an expression.
	 * 
	 * @return the name of the function to be applied to a set to determine the
	 *         element with the highest numeric value.
	 */
	String getMaxFunction();

	/**
	 * Returns the name of the function that measures the length of a character
	 * string.
	 * 
	 * @param dataType
	 *            the java.sql.Types data type. Some databases have different
	 *            length functions for different data types.
	 * @return the name of the function to be applied to a column to determine
	 *         the length of that column in a particular record.
	 */
	String getLengthFunction(int dataType);

	/**
	 * Returns the maximum precision allowed by the database for number type
	 * fields that specify the length of the number to the left of the decimal
	 * point in digits. If the HibernateDialect implementation doesn't ever use
	 * $p in any call to registerColumnType(), then this maximum precsision will
	 * not be used.
	 * 
	 * @param dataType
	 *            the java.sql.Types data type.
	 * @return the maximum number that can be used in a column declaration for
	 *         precision for the specified type.
	 */
	int getMaxPrecision(int dataType);

	/**
	 * Returns the maximum scale allowed by the database for number type fields
	 * that specify the length of the number to the right of the decimal point
	 * in digits. If the HibernateDialect implementation doesn't ever use $s in
	 * any call to registerColumnType(), then this maximum scale will not be
	 * used.
	 * 
	 * @param dataType
	 *            the java.sql.Types data type.
	 * @return the maximum number that can be used in a column declaration for
	 *         scale for the specified type.
	 */
	int getMaxScale(int dataType);

	/**
	 * Returns the number of digits of precision is represented by the specifed
	 * columnSize for the specified dataType. Some DBs represent precision as
	 * the total number of digits on the right or left of the decimal. That is
	 * what we want. Others (like PostgreSQL) give the number of bytes of
	 * storage a column can use - less than useful, since the SQL-92 says
	 * "number of digits" and this is what most other DBs use.
	 * 
	 * @param columnSize
	 *            the size of the column as reported by the driver.
	 * @param dataType
	 *            the java.sql.Types data type.
	 * @return a number indicating the total number of digits (includes both
	 *         sides of the decimal point) the column can represent.
	 */
	int getPrecisionDigits(int columnSize, int dataType);

	/**
	 * Some jdbc drivers are hopelessly broken with regard to reporting the
	 * COLUMN_SIZE. For example, MaxDB has a "long byte" data type which can
	 * store up to 2G of data, yet the driver reports that the column size is
	 * "8" - real helpful. So for drivers that have this problem, return the
	 * "proper" maximum column length for the specified dataType. If the driver
	 * doesn't have this problem, just return the columnSize.
	 * 
	 * @param columnSize
	 *            the size of the column as reported by the jdbc driver
	 * @param dataType
	 *            the type of the column.
	 * @return the specified columnSize if the jdbc driver isn't broken;
	 *         otherwise, the maximum column size for the specified dataType if
	 *         the driver is broken.
	 */
	int getColumnLength(int columnSize, int dataType);

	/**
	 * Returns boolean value indicating whether or not this dialect supports the
	 * specified database product/version.
	 * 
	 * @param databaseProductName
	 *            the name of the database as reported by
	 *            DatabaseMetaData.getDatabaseProductName()
	 * @param databaseProductVersion
	 *            the version of the database as reported by
	 *            DatabaseMetaData.getDatabaseProductVersion()
	 * @return true if this dialect can be used for the specified product name
	 *         and version; false otherwise.
	 */
	boolean supportsProduct(String databaseProductName,
			String databaseProductVersion);

	/**
	 * The string which identifies this dialect in the dialect chooser.
	 * 
	 * @return a descriptive name that tells the user what database this dialect
	 *         is design to work with.
	 */
	String getDisplayName();

	/**
	 * Returns a boolean value indicating whether or not this dialect supports
	 * adding comments to columns.
	 * 
	 * @return true if column comments are supported; false otherwise.
	 */
	boolean supportsColumnComment();

	/**
	 * Returns the SQL statement to use to add a comment to the specified column
	 * of the specified table.
	 * 
	 * @param info
	 *            information about the column such as type, name, etc.
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return
	 * @throws UnsupportedOperationException
	 *             if the database doesn't support annotating columns with a
	 *             comment.
	 */
	public String getColumnCommentAlterSQL(TableColumnInfo info,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
			throws UnsupportedOperationException;

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports dropping columns from tables.
	 * 
	 * @return true if the database supports dropping columns; false otherwise.
	 */
	boolean supportsDropColumn();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports changing a column from null to not-null and vice versa.
	 * 
	 * @return true if the database supports dropping columns; false otherwise.
	 */
	boolean supportsAlterColumnNull();

	/**
	 * Returns the SQL that forms the command to drop the specified colum in the
	 * specified table.
	 * 
	 * @param tableName
	 *            the name of the table that has the column
	 * @param columnName
	 *            the name of the column to drop.
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql for dropping the specified column
	 * @throw UnsupportedOperationException if the database doesn't support
	 *        dropping columns.
	 */
	String getColumnDropSQL(String tableName, String columnName,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
			throws UnsupportedOperationException;

	/**
	 * Returns the SQL that forms the command to drop the specified table. If
	 * cascade contraints is supported by the dialect and cascadeConstraints is
	 * true, then a drop statement with cascade constraints clause will be
	 * formed.
	 * 
	 * @param iTableInfo
	 *            the table to drop
	 * @param cascadeConstraints
	 *            whether or not to drop any FKs that may reference the
	 *            specified table.
	 * @param isMaterializedView
	 *            whether or not the specified table info is actually a
	 *            materialized view
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the drop SQL command.
	 */
	List<String> getTableDropSQL(ITableInfo iTableInfo,
			boolean cascadeConstraints, boolean isMaterializedView,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Returns the SQL that forms the command to add a primary key to the
	 * specified table composed of the given column names.
	 * 
	 * @param pkName
	 *            the name of the constraint
	 * @param ti
	 *            the table to add a primary key to
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @param columnNames
	 *            the columns that form the key
	 * @return
	 */
	String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] colInfos,
			ITableInfo ti, DatabaseObjectQualifier qualifier,
			SqlGenerationPreferences prefs);

	/**
	 * Returns the SQL fragment for adding a column in an alter table statement.
	 * 
	 * @return
	 */
	String getAddColumnString();

	/**
	 * Returns the SQL used to alter the nullability of the specified column
	 * 
	 * @param info
	 *            the column to modify
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the SQL to execute
	 */
	String[] getColumnNullableAlterSQL(TableColumnInfo info,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports renaming columns.
	 * 
	 * @return true if the database supports changing the name of columns; false
	 *         otherwise.
	 */
	boolean supportsRenameColumn();

	/**
	 * Returns the SQL that is used to change the column name.
	 * 
	 * @param from
	 *            the TableColumnInfo as it is
	 * @param to
	 *            the TableColumnInfo as it wants to be
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the SQL to make the change
	 */
	String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Returns a boolean value indicating whether or not this dialect supports
	 * modifying a columns type.
	 * 
	 * @return true if supported; false otherwise
	 */
	boolean supportsAlterColumnType();

	/**
	 * Returns the SQL that is used to change the column type.
	 * 
	 * @param from
	 *            the TableColumnInfo as it is
	 * @param to
	 *            the TableColumnInfo as it wants to be
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the SQL to make the change
	 * @throw UnsupportedOperationException if the database doesn't support
	 *        modifying column types.
	 */
	List<String> getColumnTypeAlterSQL(TableColumnInfo from,
			TableColumnInfo to, DatabaseObjectQualifier qualifier,
			SqlGenerationPreferences prefs)
			throws UnsupportedOperationException;

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports changing a column's default value.
	 * 
	 * @return true if the database supports modifying column defaults; false
	 *         otherwise
	 */
	boolean supportsAlterColumnDefault();

	/**
	 * Returns the SQL command to change the specified column's default value
	 * 
	 * @param info
	 *            the column to modify and it's default value.
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return SQL to make the change
	 */
	String getColumnDefaultAlterSQL(TableColumnInfo info,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Returns the SQL command to drop the specified table's primary key.
	 * 
	 * @param pkName
	 *            the name of the primary key that should be dropped
	 * @param tableName
	 *            the name of the table whose primary key should be dropped
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return
	 */
	String getDropPrimaryKeySQL(String pkName, String tableName,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Returns the SQL command to drop the specified table's foreign key
	 * constraint.
	 * 
	 * @param fkName
	 *            the name of the foreign key that should be dropped
	 * @param tableName
	 *            the name of the table whose foreign key should be dropped
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return
	 */
	String getDropForeignKeySQL(String fkName, String tableName,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Returns the SQL command to create the specified table (columns appear in
	 * the same order as they are stored in the database).
	 * 
	 * @param tables
	 *            the tables to get create statements for
	 * @param md
	 *            the metadata from the ISession
	 * @param prefs
	 *            preferences about how the resultant SQL commands should be
	 *            formed.
	 * @param isJdbcOdbc
	 *            whether or not the connection is via JDBC-ODBC bridge.
	 * @return the SQL that is used to create the specified table
	 */
	List<String> getCreateTableSQL(List<ITableInfo> tables,
			ISQLDatabaseMetaData md, CreateScriptPreferences prefs,
			boolean isJdbcOdbc) throws SQLException;

	/**
	 * Returns the SQL command to create the specified table. Optionally, if
	 * sortColumns is true, colums will be sorted by column name in ascending
	 * order.
	 * 
	 * @param tables
	 *            the tables to get create statements for
	 * @param md
	 *            the metadata from the ISession
	 * @param prefs
	 *            preferences about how the resultant SQL commands should be
	 *            formed.
	 * @param isJdbcOdbc
	 *            whether or not the connection is via JDBC-ODBC bridge.
	 * @param sortColumns
	 *            whether or not to sort columns according to their column name.
	 * 
	 * @return the SQL that is used to create the specified table
	 */
	List<String> getCreateTableSQL(List<ITableInfo> tables,
			ISQLDatabaseMetaData md, CreateScriptPreferences prefs,
			boolean isJdbcOdbc, boolean sortColumns) throws SQLException;

	/**
	 * Returns the DialectType enum value associated with this dialect.
	 * 
	 * @return the DialectType
	 */
	DialectType getDialectType();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports sequences.
	 * 
	 * @return true if the database supports sequence; false otherwise.
	 */
	public boolean supportsSequence();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * suports tablespaces. Specifically, when creating an index, some databases
	 * allow a tablespace to be specified to create the index in, recognizing
	 * that indexes can be quite large.
	 * 
	 * @return true if the database supports tablespaces; false otherwise.
	 */
	public boolean supportsTablespace();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports indexes.
	 * 
	 * @return true if the database supports indexes; false otherwise.
	 */
	public boolean supportsIndexes();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports access methods.
	 * 
	 * @return true if the database supports access methods; false otherwise.
	 */
	public boolean supportsAccessMethods();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports auto-increment on columns.
	 * 
	 * @return true if the database supports auto-increment; false otherwise.
	 */
	public boolean supportsAutoIncrement();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports check options for views.
	 * 
	 * @return true if the database supports check options for views; false
	 *         otherwise.
	 */
	public boolean supportsCheckOptionsForViews();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports empty tables.
	 * 
	 * @return true if the database supports empty tables; false otherwise.
	 */
	public boolean supportsEmptyTables();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports the optional SQL feature "row value constructors" (F641) that
	 * allows to insert multiple rows in a single insert statement.
	 * 
	 * @return true if the database supports multiple row inserts; false
	 *         otherwise.
	 */
	public boolean supportsMultipleRowInserts();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports adding columns to existing tables
	 * 
	 * @return true if the database supports adding columns; false otherwise.
	 */
	public boolean supportsAddColumn();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports adding foreign key constraints.
	 * 
	 * @return true if the database supports adding foreign key constraints;
	 *         false otherwise.
	 */
	public boolean supportsAddForeignKeyConstraint();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports adding unique constraints.
	 * 
	 * @return true if the database supports adding unique constraints; false
	 *         otherwise.
	 */
	public boolean supportsAddUniqueConstraint();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports altering sequences.
	 * 
	 * @return true if the database supports altering sequences; false
	 *         otherwise.
	 */
	public boolean supportsAlterSequence();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports creating indexes.
	 * 
	 * @return true if the database supports creating indexes; false otherwise.
	 */
	public boolean supportsCreateIndex();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports creating sequences.
	 * 
	 * @return true if the database supports creating sequences; false
	 *         otherwise.
	 */
	public boolean supportsCreateSequence();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports creating tables.
	 * 
	 * @return true if the database supports creating tables; false otherwise.
	 */
	public boolean supportsCreateTable();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports creating views.
	 * 
	 * @return true if the database supports creating views; false otherwise.
	 */
	public boolean supportsCreateView();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports dropping constraints.
	 * 
	 * @return true if the database supports dropping constraints; false
	 *         otherwise.
	 */
	public boolean supportsDropConstraint();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports dropping indexes.
	 * 
	 * @return true if the database supports dropping indexes; false otherwise.
	 */
	public boolean supportsDropIndex();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports dropping sequences.
	 * 
	 * @return true if the database supports dropping sequences; false
	 *         otherwise.
	 */
	public boolean supportsDropSequence();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports dropping views.
	 * 
	 * @return true if the database supports dropping views; false otherwise.
	 */
	public boolean supportsDropView();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports inserting rows.
	 * 
	 * @return true if the database supports inserting rows; false otherwise.
	 */
	public boolean supportsInsertInto();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports renaming tables.
	 * 
	 * @return true if the database supports renaming tables; false otherwise.
	 */
	public boolean supportsRenameTable();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports renaming views.
	 * 
	 * @return true if the database supports renaming views; false otherwise.
	 */
	public boolean supportsRenameView();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports gathering information about sequences.
	 * 
	 * @return true if the database supports gathering information about
	 *         sequences; false otherwise.
	 */
	public boolean supportsSequenceInformation();

	/**
	 * Returns a boolean value indicating whether or not this database dialect
	 * supports updating rows.
	 * 
	 * @return true if the database supports updating rows; false otherwise.
	 */
	public boolean supportsUpdate();

	/**
	 * Gets the index access methods that this dialect supports used when
	 * creating indexes.
	 * 
	 * @return all the access methods supported by this dialect.
	 */
	public String[] getIndexAccessMethodsTypes();

	/**
	 * Gets the index storage options that this dialect supports used when
	 * creating indexes.
	 * 
	 * @return all the access methods supported by this dialect.
	 */
	public String[] getIndexStorageOptions();

	/**
	 * Gets the SQL command to create a new table.
	 * 
	 * @param tableName
	 *            simple name of the table
	 * @param columns
	 *            columns of the table
	 * @param primaryKeys
	 *            primary keys of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @param qualifier
	 *            qualifier of the table
	 * @return the sql command to create a table.
	 */
	public String getCreateTableSQL(String tableName,
			List<TableColumnInfo> columns, List<TableColumnInfo> primaryKeys,
			SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier);

	/**
	 * Gets the SQL command to rename a table.
	 * 
	 * @param oldTableName
	 *            old name of the table
	 * @param newTableName
	 *            new name of the table
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command to rename a table.
	 */
	public String getRenameTableSQL(String oldTableName, String newTableName,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL command to create a view.
	 * 
	 * @param viewName
	 *            name of the view
	 * @param definition
	 *            old definition of the view.
	 * @param checkOption
	 *            CHECK OPTION. CASCADE, LOCAL or null for no check option.
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command to create a view.
	 */
	public String getCreateViewSQL(String viewName, String definition,
			String checkOption, DatabaseObjectQualifier qualifier,
			SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL command to rename a view.
	 * 
	 * @param oldViewName
	 *            old name of the view
	 * @param newViewName
	 *            new name of the view
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command
	 */
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL command to drop a view.
	 * 
	 * @param viewName
	 *            name of the view
	 * @param cascade
	 *            cascade true if automatically drop object that depend on the
	 *            view (such as other views).
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the SQL command to drop a view.
	 */
	public String getDropViewSQL(String viewName, boolean cascade,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL command to create an index.
	 * 
	 * @param indexName
	 *            name of the index to be created
	 * @param tableName
	 *            name of the table
	 * @param accessMethod
	 *            the index access method to use (for example, b-tree, r-tree,
	 *            hash, etc.)
	 * @param columns
	 *            columns where the index should be stored for
	 * @param unique
	 *            true if the index should be unique
	 * @param tablespace
	 *            tablespace for the index (leave empty for no tablespace)
	 * @param constraints
	 *            constraints for the index (leave empty for no constraints)
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command to create an index.
	 */
	public String getCreateIndexSQL(String indexName, String tableName,
			String accessMethod, String[] columns, boolean unique,
			String tablespace, String constraints,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL command to drop an index.
	 * 
	 * @param tableName
	 *            name of the table that the index indexes. This can be null.
	 * @param indexName
	 *            name of the index
	 * @param cascade
	 *            true if automatically drop object that depend on the view
	 *            (such as other views).
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command to drop an index.
	 */
	public String getDropIndexSQL(String tableName, String indexName,
			boolean cascade, DatabaseObjectQualifier qualifier,
			SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL command to create a sequence.
	 * 
	 * @param sequenceName
	 *            name of the sequence
	 * @param increment
	 *            increment value
	 * @param minimum
	 *            minimum value (leave empty for NO MINVALUE)
	 * @param maximum
	 *            maximum value (leave empty for NO MINVALUE)
	 * @param start
	 *            start value (leave empty for default)
	 * @param cache
	 *            cache value, how many sequences should be preallocated (leave
	 *            empty for default)
	 * @param cycle
	 *            true if the sequence should wrap around when the max-/minvalue
	 *            has been reached (leave empty for NO CYCLE)
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command to create a sequence.
	 */
	public String getCreateSequenceSQL(String sequenceName, String increment,
			String minimum, String maximum, String start, String cache,
			boolean cycle, DatabaseObjectQualifier qualifier,
			SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL command to alter a sequence.
	 * 
	 * @param sequenceName
	 *            name of the sequence.
	 * @param increment
	 *            increment value.
	 * @param minimum
	 *            minimum value.
	 * @param maximum
	 *            maximum value.
	 * @param restart
	 *            start value.
	 * @param cache
	 *            cache value, how many sequences should be preallocated.
	 * @param cycle
	 *            true if the sequence should wrap around when the max-/minvalue
	 *            has been reached.
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command statements
	 */
	public String[] getAlterSequenceSQL(String sequenceName, String increment,
			String minimum, String maximum, String restart, String cache,
			boolean cycle, DatabaseObjectQualifier qualifier,
			SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL command to query the specific database to find out the
	 * information about the sequence. The query should return the following
	 * fields: last_value, max_value, min_value, cache_value, increment_by,
	 * is_cycled
	 * 
	 * @param sequenceName
	 *            the name of the sequence.
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command to query the database.
	 */
	public String getSequenceInformationSQL(String sequenceName,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL command to drop a sequence.
	 * 
	 * @param sequenceName
	 *            name of the sequence
	 * @param cascade
	 *            true if automatically drop object that depend on the view
	 *            (such as other views).
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command to drop a sequence.
	 */
	public String getDropSequenceSQL(String sequenceName, boolean cascade,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL command to add a foreign key constraint to a table.
	 * 
	 * @param localTableName
	 *            name of the table where the foreign key should be stored.
	 * @param refTableName
	 *            name of the table where the foreign key should reference to.
	 * @param constraintName
	 *            name of the constraint. Leave it empty and it won't create a
	 *            CONSTRAINT name.
	 * @param deferrable
	 *            true if the constraint is deferrable, false if not.
	 * @param initiallyDeferred
	 *            true if the constraint is deferrable and initially deferred,
	 *            false if not.
	 * @param matchFull
	 *            true if the referenced columns using MATCH FULL.
	 * @param autoFKIndex
	 *            true to create an additional INDEX with the given fkIndexName
	 *            Name.
	 * @param fkIndexName
	 *            name of the foreign key index name.
	 * @param localRefColumns
	 *            local and referenced column collection. In the first Element
	 *            of the String Array should be the local column name and in the
	 *            second Element the referenced Table column name.
	 * @param onUpdateAction
	 *            update action. For example "RESTRICT".
	 * @param onDeleteAction
	 *            delete action. For exampel "NO ACTION".
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command to add a foreign key constraint.
	 */
	public String[] getAddForeignKeyConstraintSQL(String localTableName,
			String refTableName, String constraintName, Boolean deferrable,
			Boolean initiallyDeferred, Boolean matchFull, boolean autoFKIndex,
			String fkIndexName, Collection<String[]> localRefColumns,
			String onUpdateAction, String onDeleteAction,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL command to add a unique constraint to a table.
	 * 
	 * @param tableName
	 *            name of the table where the unique constraint should be added
	 *            to.
	 * @param constraintName
	 *            name of the constraint.
	 * @param columns
	 *            the unique columns.
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command to add a unique constraint.
	 */
	public String[] getAddUniqueConstraintSQL(String tableName,
			String constraintName, TableColumnInfo[] columns,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL to add an auto-increment to a column.
	 * 
	 * @param column
	 *            column to where the auto-increment should be added to.
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command to add auto-increment.
	 */
	public String[] getAddAutoIncrementSQL(TableColumnInfo column,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL to add an auto-increment to a column.
	 * 
	 * @param column
	 *            column to where the auto-increment should be added to.
	 * @param sequenceName
	 *            if a sequence is created for the purpose of generating the
	 *            next auto-incremented value, then this is the name of the
	 *            sequence.
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command to add auto-increment.
	 */
	public String[] getAddAutoIncrementSQL(TableColumnInfo column,
			String sequenceName, DatabaseObjectQualifier qualifier,
			SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL command to drop a constraint from a table.
	 * 
	 * @param tableName
	 *            name of the table where the constraint should be dropped from.
	 * @param constraintName
	 *            name of the constraint.
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command to drop a constraint.
	 */
	public String getDropConstraintSQL(String tableName, String constraintName,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL command to insert data into a table.
	 * <p/>
	 * If the list of columns is empty or null the SQL will look like: INSERT
	 * INTO tablename valuesPart;
	 * <p/>
	 * instead of: INSERT INTO tablename ( column1, column2, ... ) valuesPart;
	 * 
	 * @param tableName
	 *            simple name of the table
	 * @param columns
	 *            columns of the table
	 * @param valuesPart
	 *            either a query or a VALUES( ... ) string that defines the data
	 *            to insert
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command to insert data.
	 */
	public String getInsertIntoSQL(String tableName, List<String> columns,
			String valuesPart, DatabaseObjectQualifier qualifier,
			SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL command to update the specified columns with the specified
	 * values.
	 * 
	 * @param tableName
	 *            simple name of the table
	 * @param setColumns
	 *            columns to be set
	 * @param setValues
	 *            values the columns should be set with
	 * @param fromTables
	 *            simple names of the tables in the FROM clause
	 * @param whereColumns
	 *            columns in the WHERE clause
	 * @param whereValues
	 *            values of the columns in the WHERE clause
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command to update data.
	 */
	public String[] getUpdateSQL(String tableName, String[] setColumns,
			String[] setValues, String[] fromTables, String[] whereColumns,
			String[] whereValues, DatabaseObjectQualifier qualifier,
			SqlGenerationPreferences prefs);

	/**
	 * Gets the SQL command to add a column to the specified table.
	 * 
	 * @param column
	 *            information about the column
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return the sql command to add a column
	 * @throws UnsupportedOperationException
	 *             if the database doesn't support adding columns after a table
	 *             has already been created. Use supportsAddColumn before
	 *             calling this to avoid that.
	 */
	public String[] getAddColumnSQL(TableColumnInfo column,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * The character specific to this dialect used to close a quoted identifier.
	 * 
	 * @return The dialect's specific close quote character.
	 */
	public char closeQuote();

	/**
	 * The character specific to this dialect used to begin a quoted identifier.
	 * 
	 * @return The dialect's specific open quote character.
	 */
	public char openQuote();

	/**
	 * Whether or not the definition of a view can be determined via a query
	 * that is returned from getViewDefinitionSQL method
	 * 
	 * @return true if getViewDefinitionSQL yields a non-null value; false
	 *         otherwise.
	 */
	public boolean supportsViewDefinition();

	/**
	 * Returns the SQL that can be used to query the data dictionary for the
	 * body of a view. This should exclude the "CREATE VIEW <viewname> AS"
	 * prefix and just return the query. This can return null if the database
	 * doesn't provide access to this definition.
	 * 
	 * @param viewName
	 *            the name of the view to get the definition for.
	 * @param qualifier
	 *            qualifier of the table
	 * @param prefs
	 *            preferences for generated sql scripts
	 * @return
	 */
	public String getViewDefinitionSQL(String viewName,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Returns the qualified identifier based on the specified qualifier and
	 * user preferences.
	 * 
	 * @param identifier
	 * @param qualifier
	 * @param prefs
	 * @return
	 */
	public String getQualifiedIdentifier(String identifier,
			DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs);

	/**
	 * Returns a boolean indicating whether or not this dialect supports
	 * correlated sub-queries.
	 * 
	 * @return true if support for correlated sub-queries and false otherwise.
	 */
	public boolean supportsCorrelatedSubQuery();

	/**
	 * Gets the sequence property mutability, which describes which properties
	 * of a sequence can be changed, and which cannot.
	 * 
	 * @return the SequencePropertyMutability
	 */
	SequencePropertyMutability getSequencePropertyMutability();

	/**
	 * Returns a boolean indicating whether or not this dialect supports
	 * timestamps with fractional second precision. For dialects that do,
	 * Timestamp.getNanos returns the sub-second fractional value.
	 * 
	 * @return true if support for sub-second precision, or false if timestamps
	 *         are granular only to the second.
	 */
	boolean supportsSubSecondTimestamps();

	/**
	 * Returns a boolean indicating whether or not this dialect supports
	 * generating SQL for adding primary keys to existing tables.
	 * 
	 * @return true if adding primary keys is supported; false otherwise.
	 */
	boolean supportsAddPrimaryKey();

	/**
	 * Returns a boolean indicating whether or not this dialect supports
	 * generating SQL for dropping primary keys from existing tables.
	 * 
	 * @return true if dropping primary keys is supported; false otherwise.
	 */
	boolean supportsDropPrimaryKey();

	/**
	 * For the given columnInfo, this provides the type name that is used to
	 * re-create this column in a new table. This method also delegates to
	 * getJavaTypeForNativeType for type OTHER (1111).
	 * 
	 * @param tcInfo
	 *            the TableColumnInfo describing the column
	 * @return the native column type name
	 */
	String getTypeName(TableColumnInfo tcInfo);

	/**
	 * This will return the java.sql.Types constant that the specified native
	 * type name can be stored in.
	 * 
	 * @param nativeColumnTypeName
	 *            the native column type name
	 * @return a java.sql.Types constant representing the java column type this
	 *         native type is compatible with.
	 */
	int getJavaTypeForNativeType(String nativeColumnTypeName);

	int getTimestampMaximumFractionalDigits();

	/**
	 * Returns a literal string which is acceptable for SQL that update,
	 * insert or delete statements on a row which contains a column whose data type accepts
	 * binary data.
	 * 
	 * @param binaryData
	 *            the actual bytes that form the binary data value
	 * @return a literal string that is acceptable to be used in an insert,
	 *         update or delete statement.
	 */
	String getBinaryLiteralString(byte[] binaryData);
}
