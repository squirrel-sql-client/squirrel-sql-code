/*
 * Copyright (C) 2009 Rob Manning
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

import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.ADD_COLUMN_TYPE;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.COLUMN_DROP_TYPE;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.COLUMN_NULL_ALTER_TYPE;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.COLUMN_TYPE_ALTER_TYPE;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.CREATE_INDEX_TYPE;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.DROP_INDEX_TYPE;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.getUnsupportedMessage;

import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.antlr.stringtemplate.StringTemplate;
import org.hibernate.HibernateException;

/**
 * Dialect for the Netezza NPS database server.
 */
public class NetezzaDialextExt extends CommonHibernateDialect
{

	// Note that Netezza 4.6 doesn't have support for LOB data types. Under the hood, it appears to use
	// PostgreSQL, but with some limitations.
	private class NetezzaDialectHelper extends org.hibernate.dialect.PostgreSQLDialect
	{
		public NetezzaDialectHelper()
		{
			super();
			registerColumnType(Types.BIGINT, "bigint");
			registerColumnType(Types.BIT, "bool");
			registerColumnType(Types.BOOLEAN, "bool");
			registerColumnType(Types.CHAR, 64000, "char($l)");
			registerColumnType(Types.CHAR, "char(64000)");
			// Netezza 4.6 doesn't support text or any large character object data type
			// registerColumnType(Types.CLOB, "text");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p,2)");
			registerColumnType(Types.DOUBLE, "double precision");
			registerColumnType(Types.FLOAT, 15, "float($p)");
			registerColumnType(Types.FLOAT, "float(15)");
			registerColumnType(Types.INTEGER, "int");
			// Netezza 4.6 doesn't appear to support bytea or any binary type.
			// registerColumnType(Types.LONGVARBINARY, "bytea");
			registerColumnType(Types.LONGVARCHAR, "char(64000)");
			registerColumnType(Types.NUMERIC, "numeric($p,$s)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "byteint");
			// Netezza 4.6 doesn't appear to support bytea or any binary type.
			// registerColumnType(Types.VARBINARY, "bytea");
			registerColumnType(Types.VARCHAR, 64000, "varchar($l)");
			registerColumnType(Types.VARCHAR, "varchar(64000)");
		}
	}

	/** extended hibernate dialect used in this wrapper */
	private NetezzaDialectHelper _dialect = new NetezzaDialectHelper();

	private String cascadeClause;

	@Override
	public DialectType getDialectType()
	{
		return DialectType.NETEZZA;
	}

	@Override
	public String getDisplayName()
	{
		return "Netezza";
	}

	@Override
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// "DROP SEQUENCE $sequenceName$ $cascade$";
		StringTemplate st = new StringTemplate(ST_DROP_SEQUENCE_STYLE_ONE);

		HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_SEQUENCE_NAME_KEY, sequenceName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getMaxPrecision(int)
	 */
	@Override
	public int getMaxPrecision(int dataType)
	{
		return 38;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getMaxScale(int)
	 */
	@Override
	public int getMaxScale(int dataType)
	{
		// user guide says scale can be 0 to as large as the precision. It cannot be
		// greater than the precision.
		return 38;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getPrecisionDigits(int, int)
	 */
	@Override
	public int getPrecisionDigits(int columnSize, int dataType)
	{
		// Netezza correctly reports the size of the column in digits.
		return columnSize;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getTypeName(int, int, int, int)
	 */
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAccessMethods()
	 */
	@Override
	public boolean supportsAccessMethods()
	{
		// Access methods have to do with indexes. Since Netezza disallows users from creating or changing
		// indexes, this is unsupported
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAddColumn()
	 */
	@Override
	public boolean supportsAddColumn()
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAlterColumnDefault()
	 */
	@Override
	public boolean supportsAlterColumnDefault()
	{
		return true;
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAlterColumnType()
	 */
	@Override
	public boolean supportsAlterColumnType()
	{
		return false;
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsColumnComment()
	 */
	@Override
	public boolean supportsColumnComment()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsCorrelatedSubQuery()
	 */
	@Override
	public boolean supportsCorrelatedSubQuery()
	{
		// Although Netezza claims to support correlated sub-queries, the type of sub-query we want is not
		// supported:
		// update TOTABLE t set t.MYDESC =
		// (
		// select
		// f.MYDESC
		// from FROMTABLE f
		// where f.myid = t.myid
		// )
		// Furthermore, the only refactoring that currently uses this is MergeTable which also requires
		// add column support, which Netezza also doesn't seem to have.
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsCreateIndex()
	 */
	@Override
	public boolean supportsCreateIndex()
	{
		// Since Netezza disallows users from creating or changing indexes, this is unsupported
		return false;
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsCreateTable()
	 */
	@Override
	public boolean supportsCreateTable()
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsDropColumn()
	 */
	@Override
	public boolean supportsDropColumn()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsDropConstraint()
	 */
	@Override
	public boolean supportsDropConstraint()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsDropIndex()
	 */
	@Override
	public boolean supportsDropIndex()
	{
		// Since Netezza disallows users from creating or changing indexes, this is unsupported
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsDropSequence()
	 */
	@Override
	public boolean supportsDropSequence()
	{
		return true;
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
		// Since Netezza disallows users from creating or changing indexes, this is unsupported
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsInsertInto()
	 */
	@Override
	public boolean supportsInsertInto()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsMultipleRowInserts()
	 */
	@Override
	public boolean supportsMultipleRowInserts()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsProduct(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null) { return false; }
		if (databaseProductName.trim().startsWith("Netezza"))
		{
			// We don't yet have the need to discriminate by version.
			return true;
		}
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsRenameColumn()
	 */
	@Override
	public boolean supportsRenameColumn()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsRenameTable()
	 */
	@Override
	public boolean supportsRenameTable()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsRenameView()
	 */
	@Override
	public boolean supportsRenameView()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsSchemasInTableDefinition()
	 */
	@Override
	public boolean supportsSchemasInTableDefinition()
	{
		return true;
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsViewDefinition()
	 */
	@Override
	public boolean supportsViewDefinition()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getTableDropSQL(net.sourceforge.squirrel_sql.fw.sql.ITableInfo,
	 *      boolean, boolean, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public List<String> getTableDropSQL(ITableInfo tableInfo, boolean cascadeConstraints,
		boolean isMaterializedView, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final boolean supportsCascade = false;
		final boolean supportsMatViews = true;
		return DialectUtils.getTableDropSQL(tableInfo, supportsCascade, cascadeConstraints, supportsMatViews,
			cascadeClause, isMaterializedView, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnDropSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException(getUnsupportedMessage(this, COLUMN_DROP_TYPE));
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getCreateIndexSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String[], boolean, java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		throw new UnsupportedOperationException(getUnsupportedMessage(this, CREATE_INDEX_TYPE));
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDropIndexSQL(java.lang.String,
	 *      java.lang.String, boolean, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		throw new UnsupportedOperationException(getUnsupportedMessage(this, DROP_INDEX_TYPE));
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAddColumnSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		throw new UnsupportedOperationException(getUnsupportedMessage(this, ADD_COLUMN_TYPE));
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnNullableAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		throw new UnsupportedOperationException(getUnsupportedMessage(this, COLUMN_NULL_ALTER_TYPE));
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnTypeAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException(getUnsupportedMessage(this, COLUMN_TYPE_ALTER_TYPE));

	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnDefaultAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		boolean specifyType = false;
		String defaultClause = DialectUtils.SET_DEFAULT_CLAUSE;
		return DialectUtils.getColumnDefaultAlterSQL(this, info, alterClause, specifyType, defaultClause,
			qualifier, prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnNameAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.RENAME_COLUMN_CLAUSE;
		String renameToClause = DialectUtils.TO_CLAUSE;
		return DialectUtils.getColumnNameAlterSQL(from, to, alterClause, renameToClause, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getRenameTableSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getRenameTableSQL(oldTableName, newTableName, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getCreateViewSQL(java.lang.String,
	 *      java.lang.String, java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getCreateViewSQL(viewName, definition, checkOption, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getRenameViewSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		String commandPrefix = DialectUtils.ALTER_VIEW_CLAUSE;
		String renameClause = DialectUtils.RENAME_TO_CLAUSE;
		return new String[] { DialectUtils.getRenameViewSQL(commandPrefix, renameClause, oldViewName,
			newViewName, qualifier, prefs, this) };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getViewDefinitionSQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return "SELECT 'create or replace view ' || v.VIEWNAME || ' as ' || "
			+ "v.definition FROM _v_view v, _v_objs_owned o " + "where v.objid = o.objid "
			+ "and o.DATABASE = '" + qualifier.getCatalog() + "' " + "and v.VIEWNAME = '" + viewName + "'";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getCreateSequenceSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final String minimumClause = DialectUtils.MINVALUE_CLAUSE;
		final String maximumClause = DialectUtils.MAXVALUE_CLAUSE;

		String cycleClause = DialectUtils.CYCLE_CLAUSE;
		if (!cycle)
		{
			cycleClause = DialectUtils.NO_CYCLE_CLAUSE;
		}

		return DialectUtils.getCreateSequenceSQL(sequenceName, increment, minimumClause, minimum,
			maximumClause, maximum, start, cache, cycleClause, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAlterSequenceSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String cycleClause = DialectUtils.CYCLE_CLAUSE;
		if (!cycle)
		{
			cycleClause = DialectUtils.NO_CYCLE_CLAUSE;
		}

		return new String[] { DialectUtils.getAlterSequenceSQL(sequenceName, increment, minimum, maximum,
			restart, cache, cycleClause, qualifier, prefs, this) };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getSequencePropertyMutability()
	 */
	@Override
	public SequencePropertyMutability getSequencePropertyMutability()
	{
		SequencePropertyMutability result = new SequencePropertyMutability();
		result.setCache(false);
		return result;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAddForeignKeyConstraintSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.Boolean, java.lang.Boolean, java.lang.Boolean,
	 *      boolean, java.lang.String, java.util.Collection, java.lang.String, java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getAddForeignKeyConstraintSQL(localTableName, refTableName, constraintName,
			deferrable, initiallyDeferred, matchFull, autoFKIndex, fkIndexName, localRefColumns, onUpdateAction,
			onDeleteAction, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAddUniqueConstraintSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo[],
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return new String[] { DialectUtils.getAddUniqueConstraintSQL(tableName, constraintName, columns,
			qualifier, prefs, this) };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDropConstraintSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropConstraintSQL(tableName, constraintName, qualifier, prefs, this)
			+ " RESTRICT";
	}

}
