/*
 * Copyright (C) 2006 Rob Manning
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

import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.CYCLE_CLAUSE;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.NO_CYCLE_CLAUSE;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;

/**
 * An extension to the standard Hibernate DB2 dialect
 */
public class DB2DialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class DB2DialectHelper extends org.hibernate.dialect.DB2Dialect
	{
		public DB2DialectHelper()
		{
			super();
			registerColumnType(Types.BIGINT, "bigint");
			registerColumnType(Types.BINARY, 254, "char($l) for bit data");
			registerColumnType(Types.BINARY, "blob");
			registerColumnType(Types.BIT, "smallint");
			// DB2 spec says max=2147483647, but the driver throws an exception
			registerColumnType(Types.BLOB, 1073741823, "blob($l)");
			registerColumnType(Types.BLOB, "blob(1073741823)");
			registerColumnType(Types.BOOLEAN, "smallint");
			registerColumnType(Types.CHAR, 254, "char($l)");
			registerColumnType(Types.CHAR, 4000, "varchar($l)");
			registerColumnType(Types.CHAR, 32700, "long varchar");
			registerColumnType(Types.CHAR, 1073741823, "clob($l)");
			registerColumnType(Types.CHAR, "clob(1073741823)");
			// DB2 spec says max=2147483647, but the driver throws an exception
			registerColumnType(Types.CLOB, 1073741823, "clob($l)");
			registerColumnType(Types.CLOB, "clob(1073741823)");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p,$s)");
			registerColumnType(Types.DOUBLE, "float($p)");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "int");
			registerColumnType(Types.LONGVARBINARY, 32700, "long varchar for bit data");
			registerColumnType(Types.LONGVARBINARY, 1073741823, "blob($l)");
			registerColumnType(Types.LONGVARBINARY, "blob(1073741823)");
			registerColumnType(Types.LONGVARCHAR, 32700, "long varchar");
			// DB2 spec says max=2147483647, but the driver throws an exception
			registerColumnType(Types.LONGVARCHAR, 1073741823, "clob($l)");
			registerColumnType(Types.LONGVARCHAR, "clob(1073741823)");
			registerColumnType(Types.NUMERIC, "bigint");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "smallint");
			registerColumnType(Types.VARBINARY, 254, "varchar($l) for bit data");
			registerColumnType(Types.VARBINARY, "blob");
			// The driver throws an exception for varchar with length > 3924
			registerColumnType(Types.VARCHAR, 3924, "varchar($l)");
			registerColumnType(Types.VARCHAR, 32700, "long varchar");
			// DB2 spec says max=2147483647, but the driver throws an exception
			registerColumnType(Types.VARCHAR, 1073741823, "clob($l)");
			registerColumnType(Types.VARCHAR, "clob(1073741823)");

			// The registrations below are made in support for new types introduced in Java6

			// Replace "-8" with Types.ROWID when Java6 is the minimum supported version
			registerColumnType(-8, "int");
			// Replace "-9" with Types.NVARCHAR when Java6 is the minimum supported version
			registerColumnType(-9, 1073741823, "clob($l)");
			registerColumnType(-9, "clob(1073741823)");

			// Replace "-15" with Types.NCHAR when Java6 is the minimum supported version
			registerColumnType(-15, "char($l)");
			// Replace "-16" with Types.LONGNVARCHAR when Java6 is the minimum supported version
			registerColumnType(-16, "longvarchar");
			// Replace "2009" with Types.SQLXML when Java6 is the minimum supported version
			registerColumnType(2009, "clob");
			// Replace "2011" with Types.NCLOB when Java6 is the minimum supported version
			registerColumnType(2011, "clob");

		}
	}

	/** extended hibernate dialect used in this wrapper */
	private final DB2DialectHelper _dialect = new DB2DialectHelper();

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getTypeName(int, int, int, int)
	 */
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#canPasteTo(net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo)
	 */
	@Override
	public boolean canPasteTo(IDatabaseObjectInfo info)
	{
		boolean result = true;
		final DatabaseObjectType type = info.getDatabaseObjectType();
		if (type.getName().equalsIgnoreCase("database"))
		{
			result = false;
		}
		return result;
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
		}
		else
		{
			return 31;
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getMaxScale(int)
	 */
	public int getMaxScale(int dataType)
	{
		if (dataType == Types.DOUBLE || dataType == Types.FLOAT)
		{
			// double and float have no scale - that is DECIMAL_DIGITS is null.
			// Assume that is because it's variable - "floating" point.
			return 0;
		}
		else
		{
			return getMaxPrecision(dataType);
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getPrecisionDigits(int, int)
	 */
	public int getPrecisionDigits(int columnSize, int dataType)
	{
		return columnSize;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getColumnLength(int, int)
	 */
	public int getColumnLength(int columnSize, int dataType)
	{
		return columnSize;
	}

	/**
	 * The string which identifies this dialect in the dialect chooser.
	 * 
	 * @return a descriptive name that tells the user what database this dialect is design to work with.
	 */
	public String getDisplayName()
	{
		return "DB2";
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
		if (databaseProductName.trim().startsWith("DB2"))
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
	public String[] getAddColumnSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		final String qualifedTableName =
			DialectUtils.shapeQualifiableIdentifier(info.getTableName(), qualifier, prefs, this);
		final String shapedColumnName = DialectUtils.shapeIdentifier(info.getColumnName(), prefs, this);

		final ArrayList<String> result = new ArrayList<String>();

		final StringBuffer addColumn = new StringBuffer();
		addColumn.append("ALTER TABLE ");
		addColumn.append(qualifedTableName);
		addColumn.append(" ADD ");
		addColumn.append(shapedColumnName);
		addColumn.append(" ");
		addColumn.append(getTypeName(info.getDataType(), info.getColumnSize(), info.getColumnSize(),
			info.getDecimalDigits()));
		if (info.getDefaultValue() != null)
		{
			addColumn.append(" WITH DEFAULT ");
			if (JDBCTypeMapper.isNumberType(info.getDataType()))
			{
				addColumn.append(info.getDefaultValue());
			}
			else
			{
				addColumn.append("'");
				addColumn.append(info.getDefaultValue());
				addColumn.append("'");
			}
		}
		result.add(addColumn.toString());

		if (info.isNullable() == "NO")
		{
			// ALTER TABLE <TABLENAME> ADD CONSTRAINT NULL_FIELD CHECK (<FIELD> IS NOT
			// NULL)
			final StringBuffer notnull = new StringBuffer();
			notnull.append("ALTER TABLE ");
			notnull.append(qualifedTableName);
			notnull.append(" ADD CONSTRAINT ");
			// TODO: should the constraint name simply be the column name or something more like a constraint
			// name?
			notnull.append(shapedColumnName);
			notnull.append(" CHECK (");
			notnull.append(shapedColumnName);
			notnull.append(" IS NOT NULL)");
			result.add(notnull.toString());
		}

		if (info.getRemarks() != null && !"".equals(info.getRemarks()))
		{
			result.add(getColumnCommentAlterSQL(info, qualifier, prefs));
		}

		return result.toArray(new String[result.size()]);

	}

	/**
	 * Returns the SQL statement to use to add a comment to the specified column of the specified table.
	 * 
	 * @param tableName
	 *           the name of the table to create the SQL for.
	 * @param columnName
	 *           the name of the column to create the SQL for.
	 * @param comment
	 *           the comment to add.
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect for the target database
	 * @return
	 * @throws UnsupportedOperationException
	 *            if the database doesn't support annotating columns with a comment.
	 */
	public String getColumnCommentAlterSQL(String tableName, String columnName, String comment,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
		throws UnsupportedOperationException
	{
		return DialectUtils.getColumnCommentAlterSQL(tableName, columnName, comment, qualifier, prefs, dialect);
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
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// alter table <tablename> drop column <columnName>
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
		return DialectUtils.getTableDropSQL(iTableInfo, false, cascadeConstraints, false,
			DialectUtils.CASCADE_CLAUSE, false, qualifier, prefs, this);
	}

	/**
	 * Returns the SQL that forms the command to add a primary key to the specified table composed of the given
	 * column names. ALTER TABLE table_name ADD CONSTRAINT contraint_name PRIMARY KEY (column_name)
	 * 
	 * @param pkName
	 *           the name of the constraint
	 * @param columnNames
	 *           the columns that form the key
	 * @return
	 */
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return new String[] { DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, false, qualifier, prefs,
			this) };
	}

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
	 * Returns the SQL statement to use to add a comment to the specified column of the specified table.
	 * 
	 * @param info
	 *           information about the column such as type, name, etc.
	 * @return
	 * @throws UnsupportedOperationException
	 *            if the database doesn't support annotating columns with a comment.
	 */
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		return DialectUtils.getColumnCommentAlterSQL(info, qualifier, prefs, this);
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
	 * Update: DB2 version 9.5 appears to support altering column nullability just fine via: ALTER TABLE
	 * table_name ALTER COLUMN column_name SET NOT NULL So, I'll use that Returns the SQL used to alter the
	 * specified column to not allow null values This appears to work: ALTER TABLE table_name ADD CONSTRAINT
	 * constraint_name CHECK (column_name IS NOT NULL) However, the jdbc driver still reports the column as
	 * nullable - which means I can't reliably display the correct value for this attribute in the UI. I tried
	 * this alternate syntax and it fails with an exception: ALTER TABLE table_name ALTER COLUMN column_name
	 * SET NOT NULL Error: com.ibm.db2.jcc.b.SqlException: DB2 SQL error: SQLCODE: -104, SQLSTATE: 42601,
	 * SQLERRMC: NOT;ER COLUMN mychar SET;DEFAULT, SQL State: 42601, Error Code: -104 I don't see how I can
	 * practically support changing column nullability in DB2.
	 * 
	 * @param info
	 *           the column to modify
	 * @return the SQL to execute
	 */
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final ArrayList<String> result = new ArrayList<String>();

		final boolean nullable = info.isNullable().equalsIgnoreCase("yes");
		result.addAll(Arrays.asList(getColumnNullableAlterSQL(info, nullable, qualifier, prefs)));

		/* DB2 needs to reorg table after changing nullabolity */
		final StringBuilder reorgSql = new StringBuilder();
		reorgSql.append("CALL SYSPROC.ADMIN_CMD('REORG TABLE ");
		reorgSql.append(DialectUtils.shapeQualifiableIdentifier(info.getTableName(), qualifier, prefs, this));
		reorgSql.append("')");

		result.add(reorgSql.toString());
		return result.toArray(new String[result.size()]);
	}

	/**
	 * Returns an SQL statement that alters the specified column nullability.
	 * 
	 * @param info
	 *           the column to modify
	 * @param nullable
	 *           whether or not the column should allow nulls after being altered
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return
	 */
	private String[] getColumnNullableAlterSQL(TableColumnInfo info, boolean nullable,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final ArrayList<String> sql = new ArrayList<String>();

		final StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(DialectUtils.shapeQualifiableIdentifier(info.getTableName(), qualifier, prefs, this));
		result.append(" ");
		result.append(DialectUtils.ALTER_COLUMN_CLAUSE);
		result.append(" ");
		result.append(DialectUtils.shapeIdentifier(info.getColumnName(), prefs, this));
		result.append(" SET ");
		if (nullable)
		{
			result.append("NULL");
		}
		else
		{
			result.append("NOT NULL");
		}
		sql.add(result.toString());
		sql.add(getTableReorgSql(info.getTableName(), qualifier, prefs));
		return sql.toArray(new String[sql.size()]);
	}

	/**
	 * Returns a boolean value indicating whether or not this database dialect supports renaming columns.
	 * 
	 * @return true if the database supports changing the name of columns; false otherwise.
	 */
	public boolean supportsRenameColumn()
	{
		return false;
	}

	/**
	 * Returns the SQL that is used to change the column name.
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
		final int featureId = DialectUtils.COLUMN_NAME_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
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
	 * Returns the SQL that is used to change the column type. ALTER TABLE table_name ALTER COLUMN column_name
	 * SET DATA TYPE data_type
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

		// "ALTER TABLE $tableName$ " +
		// "ALTER $columnName$ SET DATA TYPE $dataType$";

		final String templateString = ST_ALTER_COLUMN_SET_DATA_TYPE_STYLE_ONE;
		final StringTemplate st = new StringTemplate(templateString);

		final HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, from.getTableName());
		valuesMap.put(ST_COLUMN_NAME_KEY, from.getColumnName());
		valuesMap.put(ST_DATA_TYPE_KEY, DialectUtils.getTypeName(to, this));

		final ArrayList<String> result = new ArrayList<String>();
		result.add(DialectUtils.bindAttributes(this, st, valuesMap, qualifier, prefs));
		return result;
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
	 * Returns the SQL command to change the specified column's default value ALTER TABLE EMPLOYEE ALTER COLUMN
	 * WORKDEPTSET SET DEFAULT '123'
	 * 
	 * @param info
	 *           the column to modify and it's default value.
	 * @return SQL to make the change
	 */
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		final String defaultClause = DialectUtils.SET_DEFAULT_CLAUSE;
		return DialectUtils.getColumnDefaultAlterSQL(this, info, alterClause, false, defaultClause, qualifier,
			prefs);
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
		return DialectType.DB2;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getIndexAccessMethodsTypes()
	 */
	public String[] getIndexAccessMethodsTypes()
	{
		return new String[] {};
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getIndexStorageOptions()
	 */
	public String[] getIndexStorageOptions()
	{
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAddAutoIncrementSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, String sequenceName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final ArrayList<String> result = new ArrayList<String>();
		/*
		 * DB2 doesn't support adding an auto-increment column once the table has already been created. So this
		 * can simulate one using trigger on the table to access a sequence. Found this idea at wikibooks:
		 * http://en.wikibooks.org/wiki/SQL_dialects_reference/Data_structure_definition/Auto-increment_column
		 * CREATE SEQUENCE sequence_name; CREATE TABLE table_name ( column_name INT ); CREATE TRIGGER
		 * insert_trigger NO CASCADE BEFORE INSERT ON table_name REFERENCING NEW AS n FOR EACH ROW SET
		 * n.column_name = NEXTVAL FOR sequence_name;
		 */
		final String tableName = column.getTableName();
		final String columnName = column.getColumnName();

		result.add(getCreateSequenceSQL(sequenceName, "1", "1", null, "1", null, false, qualifier, prefs));

		final StringBuilder triggerSql = new StringBuilder();
		triggerSql.append("CREATE TRIGGER ");
		triggerSql.append(columnName);
		triggerSql.append("_trigger \n");
		triggerSql.append("NO CASCADE BEFORE INSERT ON ");
		triggerSql.append(tableName);
		triggerSql.append(" REFERENCING NEW AS n \n");
		triggerSql.append("FOR EACH ROW \n");
		triggerSql.append("SET n.");
		triggerSql.append(columnName);
		triggerSql.append(" = NEXTVAL FOR ");
		triggerSql.append(sequenceName);

		result.add(triggerSql.toString());

		return result.toArray(new String[result.size()]);

	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddAutoIncrementSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      DatabaseObjectQualifier, net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 * @deprecated use the version that accepts the sequence name instead.
	 */
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final String tableName = column.getTableName();
		final String columnName = column.getColumnName();
		final StringBuilder sequenceName = new StringBuilder();
		sequenceName.append(tableName.toUpperCase()).append("_");
		sequenceName.append(columnName.toUpperCase()).append("_SEQ");

		return getAddAutoIncrementSQL(column, sequenceName.toString(), qualifier, prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAddForeignKeyConstraintSQL(java.lang.String,
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
		final Boolean deferrableNotSupported = null;
		final Boolean initiallyDeferredNotSupported = null;
		final Boolean matchFullNotSupported = null;

		/* DB2 doesn't support cascade, set default, or set null for FK constraint update action */
		if (onUpdateAction != null) {
			if (! (onUpdateAction.equalsIgnoreCase("no action") 
						|| onUpdateAction.equalsIgnoreCase("restrict"))) {
				onUpdateAction = "";
			}
		}

		/* DB2 doesn't support set default for FK constraint delete action */
		if (onDeleteAction != null && onDeleteAction.equalsIgnoreCase("set default")) {
			onDeleteAction = "";
		}
		
		return DialectUtils.getAddForeignKeyConstraintSQL(localTableName, refTableName, constraintName,
			deferrableNotSupported, initiallyDeferredNotSupported, matchFullNotSupported, autoFKIndex,
			fkIndexName, localRefColumns, onUpdateAction, onDeleteAction, qualifier, prefs, this);
	}

	private String getTableReorgSql(String tableName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		/* DB2 needs to reorg table after changing nullabolity */
		final StringBuilder reorgSql = new StringBuilder();
		reorgSql.append("CALL SYSPROC.ADMIN_CMD('REORG TABLE ");
		reorgSql.append(DialectUtils.shapeQualifiableIdentifier(tableName, qualifier, prefs, this));
		reorgSql.append("')");
		return reorgSql.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddUniqueConstraintSQL(java.lang.String,
	 *      java.lang.String, TableColumnInfo[],
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final ArrayList<String> result = new ArrayList<String>();

		// DB2 requires that columns be not-null before applying a unique constraint
		for (final TableColumnInfo column : columns)
		{
			if (column.isNullable().equalsIgnoreCase("YES"))
			{
				result.addAll(Arrays.asList(getColumnNullableAlterSQL(column, false, qualifier, prefs)));
			}
		}

		result.add(DialectUtils.getAddUniqueConstraintSQL(tableName, constraintName, columns, qualifier, prefs,
			this));

		return result.toArray(new String[result.size()]);
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
		String cycleClause = NO_CYCLE_CLAUSE;
		if (cycle == true)
		{
			cycleClause = CYCLE_CLAUSE;
		}
		return new String[] {

		DialectUtils.getAlterSequenceSQL(sequenceName, increment, minimum, maximum, restart, cache,
			cycleClause, qualifier, prefs, this) };
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
		return DialectUtils.getCreateIndexSQL(indexName, tableName, accessMethod, columns, unique, tablespace,
			constraints, qualifier, prefs, this);
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
		return DialectUtils.getCreateSequenceSQL(sequenceName, increment, minimum, maximum, start, cache, null,
			qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getCreateTableSQL(java.lang.String,
	 *      java.util.List, java.util.List, net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier)
	 */
	public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{
		return DialectUtils.getCreateTableSQL(tableName, columns, primaryKeys, prefs, qualifier, this);
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropIndexSQL(String, java.lang.String,
	 *      boolean, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final Boolean cascadeNotSupported = null;
		return DialectUtils.getDropIndexSQL(indexName, cascadeNotSupported, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropSequenceSQL(java.lang.String,
	 *      boolean, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropSequenceSQL(sequenceName, false, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropViewSQL(java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final Boolean cascadeNotSupported = null;

		return DialectUtils.getDropViewSQL(viewName, cascadeNotSupported, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getInsertIntoSQL(java.lang.String,
	 *      java.util.List, java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getInsertIntoSQL(tableName, columns, valuesPart, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getRenameTableSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// RENAME TABLE <tablename> TO <newtablename>;
		final StringBuilder sql = new StringBuilder();

		sql.append("RENAME TABLE ");
		sql.append(DialectUtils.shapeQualifiableIdentifier(oldTableName, qualifier, prefs, this));
		sql.append(" ");
		sql.append(" TO ");
		sql.append(DialectUtils.shapeIdentifier(newTableName, prefs, this));

		return sql.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getRenameViewSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.RENAME_VIEW_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsViewDefinition()
	 */
	public boolean supportsViewDefinition()
	{
		return true;
	}

	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		/*
		 * SELECT 'CREATE VIEW <newViewName> AS ' || SUBSTR(TEXT , LOCATE('as', TEXT)+2, LENGTH(TEXT)) FROM
		 * SYSCAT.VIEWS WHERE VIEWSCHEMA = '<schema>' AND VIEWNAME = '<oldViewName>';
		 */

		final StringBuilder createViewSql = new StringBuilder();
		createViewSql.append("SELECT TEXT ");
		createViewSql.append(" FROM SYSCAT.VIEWS ");
		createViewSql.append("WHERE VIEWSCHEMA = '");
		createViewSql.append(qualifier.getSchema());
		createViewSql.append("' AND UPPER(VIEWNAME) = '");
		createViewSql.append(viewName.toUpperCase());
		createViewSql.append("'");
		return createViewSql.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getSequenceInformationSQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// SELECT
		//SEQSCHEMA,SEQNAME,DEFINER,DEFINERTYPE,OWNER,OWNERTYPE,SEQID,SEQTYPE,INCREMENT,START,MAXVALUE,MINVALUE,
		//NEXTCACHEFIRSTVALUE,CYCLE,CACHE,ORDER,DATATYPEID,SOURCETYPEID,CREATE_TIME,ALTER_TIME,PRECISION,ORIGIN,
		// REMARKS
		// FROM SYSCAT.SEQUENCES
		// WHERE SEQNAME = ?
		// and SEQSCHEMA = <schema>

		final StringBuilder result = new StringBuilder();
		result.append("SELECT NEXTCACHEFIRSTVALUE, MAXVALUE, MINVALUE, CACHE, INCREMENT, CYCLE ");
		result.append("FROM SYSCAT.SEQUENCES ");
		result.append("WHERE ");
		if (qualifier.getSchema() != null)
		{
			result.append("SEQSCHEMA = upper('" + qualifier.getSchema() + "') AND ");
		}
		// TODO: figure out why bind variables aren't working
		result.append("SEQNAME = '");
		result.append(sequenceName);
		result.append("'");
		return result.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAccessMethods()
	 */
	public boolean supportsAccessMethods()
	{
		return false;
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAutoIncrement()
	 */
	public boolean supportsAutoIncrement()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsCheckOptionsForViews()
	 */
	public boolean supportsCheckOptionsForViews()
	{
		return false;
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsDropView()
	 */
	public boolean supportsDropView()
	{
		return true;
	}

	public boolean supportsEmptyTables()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsIndexes()
	 */
	public boolean supportsIndexes()
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsMultipleRowInserts()
	 */
	public boolean supportsMultipleRowInserts()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsRenameTable()
	 */
	public boolean supportsRenameTable()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsRenameView()
	 */
	public boolean supportsRenameView()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsSequence()
	 */
	public boolean supportsSequence()
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsTablespace()
	 */
	public boolean supportsTablespace()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsUpdate()
	 */
	public boolean supportsUpdate()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAddColumn()
	 */
	public boolean supportsAddColumn()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getQualifiedIdentifier(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getQualifiedIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final String schema = qualifier.getSchema();
		final String catalog = qualifier.getCatalog();
		final StringBuilder result = new StringBuilder();
		if (!StringUtils.isEmpty(catalog))
		{
			result.append(DialectUtils.shapeIdentifier(catalog, prefs, this));
			result.append(".");
		}
		if (!StringUtils.isEmpty(schema))
		{
			result.append(DialectUtils.shapeIdentifier(schema, prefs, this));
			result.append(".");
		}
		result.append(DialectUtils.shapeIdentifier(identifier, prefs, this));
		return result.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsCorrelatedSubQuery()
	 */
	public boolean supportsCorrelatedSubQuery()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getTimestampMaximumFractionalDigits()
	 */
	@Override
	public int getTimestampMaximumFractionalDigits()
	{
		return 6;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getBinaryLiteralString(byte[])
	 */
	@Override
	public String getBinaryLiteralString(byte[] binaryData)
	{
		return "BLOB(x'" + DialectUtils.toHexString(binaryData) + "')";
	}


}
