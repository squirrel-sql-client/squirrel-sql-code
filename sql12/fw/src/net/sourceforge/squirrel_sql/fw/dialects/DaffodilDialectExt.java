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

import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.antlr.stringtemplate.StringTemplate;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;

/**
 * A dialect delegate for the Daffodil database.
 * 
 * @author manningr
 */
public class DaffodilDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class DaffodilDialectHelper extends Dialect
	{
		public DaffodilDialectHelper()
		{
			super();
			registerColumnType(Types.BIGINT, "bigint");
			registerColumnType(Types.BINARY, 4192, "binary($l)");
			registerColumnType(Types.BINARY, 1073741823, "long varbinary($l)");
			registerColumnType(Types.BINARY, "long varbinary(1073741823)");
			registerColumnType(Types.BIT, "bit");
			registerColumnType(Types.BLOB, 1073741823, "blob($l)");
			registerColumnType(Types.BLOB, "blob(1073741823)");
			registerColumnType(Types.BOOLEAN, "boolean");
			registerColumnType(Types.CHAR, 4192, "char($l)");
			registerColumnType(Types.CHAR, 1073741823, "clob($l)");
			registerColumnType(Types.CHAR, "clob(1073741823)");
			registerColumnType(Types.CLOB, 1073741823, "clob($l)");
			registerColumnType(Types.CLOB, "clob(1073741823)");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p,$s)");
			registerColumnType(Types.DOUBLE, "double precision");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "integer");
			registerColumnType(Types.LONGVARBINARY, 1073741823, "long varbinary($l)");
			registerColumnType(Types.LONGVARBINARY, "long varbinary(1073741823)");
			registerColumnType(Types.LONGVARCHAR, 1073741823, "long varchar($l)");
			registerColumnType(Types.LONGVARCHAR, "long varchar(1073741823)");
			registerColumnType(Types.NUMERIC, "numeric($p,$s)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "tinyint");
			registerColumnType(Types.VARBINARY, 4192, "varbinary($l)");
			registerColumnType(Types.VARBINARY, 1073741823, "long varbinary($l)");
			registerColumnType(Types.VARBINARY, "long varbinary(1073741823)");
			registerColumnType(Types.VARCHAR, 4192, "varchar($l)");
			registerColumnType(Types.VARCHAR, 1073741823, "clob($l)");
			registerColumnType(Types.VARCHAR, "clob(1073741823)");

			// The registrations below are made in support for new types introduced in Java6

			// Replace "-8" with Types.ROWID when Java6 is the minimum supported version
			registerColumnType(-8, "integer");
			// Replace "-9" with Types.NVARCHAR when Java6 is the minimum supported version
			registerColumnType(-9, 4192, "varchar($l)");
			registerColumnType(-9, 1073741823, "clob($l)");
			registerColumnType(-9, "clob(1073741823)");
			// Replace "-15" with Types.NCHAR when Java6 is the minimum supported version
			registerColumnType(-15, 4192, "char($l)");
			registerColumnType(-15, 1073741823, "clob($l)");
			registerColumnType(-15, "clob(1073741823)");
			// Replace "-16" with Types.LONGNVARCHAR when Java6 is the minimum supported version
			registerColumnType(-16, 1073741823, "long varchar($l)");
			registerColumnType(-16, "long varchar(1073741823)");
			// Replace "2009" with Types.SQLXML when Java6 is the minimum supported version
			registerColumnType(2009, 1073741823, "clob($l)");
			registerColumnType(2009, "clob(1073741823)");
			// Replace "2011" with Types.NCLOB when Java6 is the minimum supported version
			registerColumnType(2011, 1073741823, "clob($l)");
			registerColumnType(2011, "clob(1073741823)");

		}
	}

	/** extended hibernate dialect used in this wrapper */
	private final DaffodilDialectHelper _dialect = new DaffodilDialectHelper();

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getTypeName(int, int, int, int)
	 */
	@Override
	public String getTypeName(final int code, final int length, final int precision, final int scale)
		throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#canPasteTo(net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo)
	 */
	@Override
	public boolean canPasteTo(final IDatabaseObjectInfo info)
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsSchemasInTableDefinition()
	 */
	@Override
	public boolean supportsSchemasInTableDefinition()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getMaxPrecision(int)
	 */
	@Override
	public int getMaxPrecision(final int dataType)
	{
		if (dataType == Types.FLOAT) { return 15; }
		if (dataType == Types.NUMERIC || dataType == Types.DECIMAL) { return 38; }
		return 0;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDisplayName()
	 */
	@Override
	public String getDisplayName()
	{
		return "Daffodil";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsProduct(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean supportsProduct(final String databaseProductName, final String databaseProductVersion)
	{
		if (databaseProductName == null) { return false; }
		if (databaseProductName.trim().startsWith("Daffodil"))
		{
			// We don't yet have the need to discriminate by version.
			return true;
		}
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAddColumnString()
	 */
	@Override
	public String getAddColumnString()
	{
		return "ADD COLUMN";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsColumnComment()
	 */
	@Override
	public boolean supportsColumnComment()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsDropColumn()
	 */
	@Override
	public boolean supportsDropColumn()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnDropSQL(java.lang.String,
	 *      java.lang.String, DatabaseObjectQualifier, SqlGenerationPreferences)
	 */
	@Override
	public String getColumnDropSQL(final String tableName, final String columnName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		final StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(tableName);
		result.append(" DROP COLUMN ");
		result.append(columnName);
		result.append(" CASCADE");
		return result.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getTableDropSQL(net.sourceforge.squirrel_sql.fw.sql.ITableInfo,
	 *      boolean, boolean, DatabaseObjectQualifier, SqlGenerationPreferences)
	 */
	@Override
	public List<String> getTableDropSQL(final ITableInfo iTableInfo, final boolean cascadeConstraints,
		final boolean isMaterializedView, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getTableDropSQL(iTableInfo, true, cascadeConstraints, false,
			DialectUtils.CASCADE_CLAUSE, false, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAddPrimaryKeySQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo[],
	 *      net.sourceforge.squirrel_sql.fw.sql.ITableInfo, DatabaseObjectQualifier, SqlGenerationPreferences)
	 */
	@Override
	public String[] getAddPrimaryKeySQL(final String pkName, final TableColumnInfo[] columns,
		final ITableInfo ti, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return new String[] { DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, false, qualifier, prefs,
			this) };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnCommentAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getColumnCommentAlterSQL(final TableColumnInfo info,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
		throws UnsupportedOperationException
	{
		final int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAlterColumnNull()
	 */
	@Override
	public boolean supportsAlterColumnNull()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnNullableAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getColumnNullableAlterSQL(final TableColumnInfo info,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.COLUMN_NULL_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsRenameColumn()
	 */
	@Override
	public boolean supportsRenameColumn()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnNameAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo, DatabaseObjectQualifier,
	 *      SqlGenerationPreferences)
	 */
	@Override
	public String getColumnNameAlterSQL(final TableColumnInfo from, final TableColumnInfo to,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.COLUMN_NAME_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAlterColumnType()
	 */
	@Override
	public boolean supportsAlterColumnType()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnTypeAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public List<String> getColumnTypeAlterSQL(final TableColumnInfo from, final TableColumnInfo to,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
		throws UnsupportedOperationException
	{
		final int featureId = DialectUtils.COLUMN_TYPE_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAlterColumnDefault()
	 */
	@Override
	public boolean supportsAlterColumnDefault()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnDefaultAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      DatabaseObjectQualifier, SqlGenerationPreferences)
	 */
	@Override
	public String getColumnDefaultAlterSQL(final TableColumnInfo info,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		final String defaultClause = DialectUtils.SET_DEFAULT_CLAUSE;
		return DialectUtils.getColumnDefaultAlterSQL(this, info, alterClause, false, defaultClause, qualifier,
			prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDropPrimaryKeySQL(java.lang.String,
	 *      java.lang.String, DatabaseObjectQualifier, SqlGenerationPreferences)
	 */
	@Override
	public String getDropPrimaryKeySQL(final String pkName, final String tableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, true, true, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDropForeignKeySQL(java.lang.String,
	 *      java.lang.String, DatabaseObjectQualifier, SqlGenerationPreferences)
	 */
	@Override
	public String getDropForeignKeySQL(final String fkName, final String tableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropForeignKeySQL(fkName, tableName, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getCreateTableSQL(java.util.List,
	 *      net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData,
	 *      net.sourceforge.squirrel_sql.fw.dialects.CreateScriptPreferences, boolean)
	 */
	@Override
	public List<String> getCreateTableSQL(final List<ITableInfo> tables, final ISQLDatabaseMetaData md,
		final CreateScriptPreferences prefs, final boolean isJdbcOdbc) throws SQLException
	{
		return DialectUtils.getCreateTableSQL(tables, md, this, prefs, isJdbcOdbc);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDialectType()
	 */
	@Override
	public DialectType getDialectType()
	{
		return DialectType.DAFFODIL;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getIndexAccessMethodsTypes()
	 */
	@Override
	public String[] getIndexAccessMethodsTypes()
	{

		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getIndexStorageOptions()
	 */
	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getIndexStorageOptions()
	 */
	@Override
	public String[] getIndexStorageOptions()
	{

		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAddAutoIncrementSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getAddAutoIncrementSQL(final TableColumnInfo column,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.ADD_AUTO_INCREMENT_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAddForeignKeyConstraintSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.Boolean, java.lang.Boolean, java.lang.Boolean,
	 *      boolean, java.lang.String, java.util.Collection, java.lang.String, java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getAddForeignKeyConstraintSQL(final String localTableName, final String refTableName,
		final String constraintName, final Boolean deferrable, final Boolean initiallyDeferred,
		final Boolean matchFull, final boolean autoFKIndex, final String fkIndexName,
		final Collection<String[]> localRefColumns, final String onUpdateAction, final String onDeleteAction,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final Boolean deferrableNotSupported = null;
		final Boolean initiallyDeferredNotSupported = null;
		final Boolean matchFullNotSupported = null;

		return DialectUtils.getAddForeignKeyConstraintSQL(localTableName, refTableName, constraintName,
			deferrableNotSupported, initiallyDeferredNotSupported, matchFullNotSupported, autoFKIndex,
			fkIndexName, localRefColumns, onUpdateAction, onDeleteAction, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAddUniqueConstraintSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo[],
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getAddUniqueConstraintSQL(final String tableName, final String constraintName,
		final TableColumnInfo[] columns, final DatabaseObjectQualifier qualifier,
		final SqlGenerationPreferences prefs)
	{
		// alter table FKTESTCHILDTABLE add CONSTRAINT foo_const UNIQUE (MYID)

		// "ALTER TABLE $tableName$ " +
		// "ADD CONSTRAINT $constraintName$ UNIQUE ($columnName; separator=\",\"$)";

		final String templateStr = ST_ADD_UNIQUE_CONSTRAINT_STYLE_TWO;

		final StringTemplate st = new StringTemplate(templateStr);

		final HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, tableName, ST_CONSTRAINT_NAME_KEY, constraintName);

		return new String[] { DialectUtils.getAddUniqueConstraintSQL(st, valuesMap, columns, qualifier, prefs,
			this) };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAlterSequenceSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getAlterSequenceSQL(final String sequenceName, final String increment,
		final String minimum, final String maximum, final String restart, final String cache,
		final boolean cycle, final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		return DialectUtils.getSimulatedAlterSequenceSQL(sequenceName, increment, minimum, maximum, minimum,
			cache, cycle, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getCreateIndexSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String[], boolean, java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getCreateIndexSQL(final String indexName, final String tableName, final String accessMethod,
		final String[] columns, final boolean unique, final String tablespace, final String constraints,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final StringBuilder result = new StringBuilder();
		result.append("CREATE ");

		// TODO: Are there unique indexes in daffodil ?
		// if (unique)
		// {
		// result.append("UNIQUE ");
		// }
		result.append(" INDEX ");
		result.append(DialectUtils.shapeQualifiableIdentifier(indexName, qualifier, prefs, this));
		result.append(" ON ");
		result.append(DialectUtils.shapeQualifiableIdentifier(tableName, qualifier, prefs, this));
		result.append("(");
		for (final String column : columns)
		{
			result.append(column);
			result.append(",");
		}
		result.setLength(result.length() - 1);
		result.append(")");
		return result.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getCreateSequenceSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getCreateSequenceSQL(final String sequenceName, final String increment,
		final String minimum, final String maximum, final String start, final String cache,
		final boolean cycle, final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		return DialectUtils.getCreateSequenceSQL(sequenceName, increment, minimum, maximum, start, cache, null,
			qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAddColumnSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getAddColumnSQL(final TableColumnInfo column, final DatabaseObjectQualifier qualifier,
		final SqlGenerationPreferences prefs)
	{
		final boolean addDefaultClause = true;
		final boolean supportsNullQualifier = false;
		final boolean addNullClause = true;

		return new String[] { DialectUtils.getAddColumSQL(column, this, addDefaultClause,
			supportsNullQualifier, addNullClause, qualifier, prefs) };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getCreateViewSQL(java.lang.String,
	 *      java.lang.String, java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getCreateViewSQL(final String viewName, final String definition, final String checkOption,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		return DialectUtils.getCreateViewSQL(viewName, definition, checkOption, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDropConstraintSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getDropConstraintSQL(final String tableName, final String constraintName,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.DROP_CONSTRAINT_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDropIndexSQL(java.lang.String,
	 *      java.lang.String, boolean, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getDropIndexSQL(final String tableName, final String indexName, final boolean cascade,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.DROP_INDEX_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getRenameTableSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getRenameTableSQL(final String oldTableName, final String newTableName,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.RENAME_TABLE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getSequenceInformationSQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getSequenceInformationSQL(final String sequenceName,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.SEQUENCE_INFORMATION_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAccessMethods()
	 */
	@Override
	public boolean supportsAccessMethods()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAddForeignKeyConstraint()
	 */
	@Override
	public boolean supportsAddForeignKeyConstraint()
	{

		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAddUniqueConstraint()
	 */
	@Override
	public boolean supportsAddUniqueConstraint()
	{

		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAlterSequence()
	 */
	@Override
	public boolean supportsAlterSequence()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAutoIncrement()
	 */
	@Override
	public boolean supportsAutoIncrement()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsCheckOptionsForViews()
	 */
	@Override
	public boolean supportsCheckOptionsForViews()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsCreateIndex()
	 */
	@Override
	public boolean supportsCreateIndex()
	{

		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsCreateSequence()
	 */
	@Override
	public boolean supportsCreateSequence()
	{

		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsCreateView()
	 */
	@Override
	public boolean supportsCreateView()
	{

		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsDropConstraint()
	 */
	@Override
	public boolean supportsDropConstraint()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsDropIndex()
	 */
	@Override
	public boolean supportsDropIndex()
	{
		// TODO: Bizarre that I couldn't find a valid SQL to drop an index.
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsDropView()
	 */
	@Override
	public boolean supportsDropView()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsEmptyTables()
	 */
	@Override
	public boolean supportsEmptyTables()
	{

		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsIndexes()
	 */
	@Override
	public boolean supportsIndexes()
	{

		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsMultipleRowInserts()
	 */
	@Override
	public boolean supportsMultipleRowInserts()
	{

		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsRenameTable()
	 */
	@Override
	public boolean supportsRenameTable()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsSequence()
	 */
	@Override
	public boolean supportsSequence()
	{

		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsSequenceInformation()
	 */
	@Override
	public boolean supportsSequenceInformation()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsTablespace()
	 */
	@Override
	public boolean supportsTablespace()
	{

		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsUpdate()
	 */
	@Override
	public boolean supportsUpdate()
	{

		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAddColumn()
	 */
	@Override
	public boolean supportsAddColumn()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getQualifiedIdentifier(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getQualifiedIdentifier(final String identifier, final DatabaseObjectQualifier qualifier,
		final SqlGenerationPreferences prefs)
	{
		final String schema = qualifier.getSchema();
		final String catalog = qualifier.getCatalog();
		final StringBuilder result = new StringBuilder();
		if (catalog != null)
		{
			result.append(DialectUtils.shapeIdentifier(catalog, prefs, this));
			result.append(".");
		}
		if (schema != null)
		{
			result.append(DialectUtils.shapeIdentifier(schema, prefs, this));
			result.append(".");
		}
		result.append(DialectUtils.shapeIdentifier(identifier, prefs, this));
		return result.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsCorrelatedSubQuery()
	 */
	@Override
	public boolean supportsCorrelatedSubQuery()
	{

		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsSubSecondTimestamps()
	 */
	@Override
	public boolean supportsSubSecondTimestamps()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDropViewSQL(java.lang.String,
	 *      boolean, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getDropViewSQL(final String viewName, final boolean cascade,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropViewSQL(viewName, cascade, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDropSequenceSQL(java.lang.String,
	 *      boolean, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getDropSequenceSQL(final String sequenceName, final boolean cascade,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		// "DROP SEQUENCE $sequenceName$ $cascade$";
		final StringTemplate st = new StringTemplate(ST_DROP_SEQUENCE_STYLE_ONE);

		final HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_SEQUENCE_NAME_KEY, sequenceName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsDropSequence()
	 */
	@Override
	public boolean supportsDropSequence()
	{
		return true;
	}

}
