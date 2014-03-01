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
import java.util.ArrayList;
import java.util.Arrays;
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

/**
 * An extension to the standard Hibernate Ingres dialect
 */
public class IngresDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class IngresDialectHelper extends org.hibernate.dialect.IngresDialect
	{
		public IngresDialectHelper()
		{
			registerColumnType(Types.BIGINT, "bigint");
			// SQL Reference Guide says 32k, but I get:
			//
			// The specified row size exceeded the maximum allowable row width.,
			// SQL State: 50002, Error Code: 2045
			//
			// when I go above 8000.
			registerColumnType(Types.BINARY, 8000, "byte($l)");
			registerColumnType(Types.BINARY, "long byte");
			registerColumnType(Types.BIT, "tinyint");
			registerColumnType(Types.BLOB, "long byte");
			registerColumnType(Types.BOOLEAN, "tinyint");
			registerColumnType(Types.CHAR, 2000, "char($l)");
			registerColumnType(Types.CHAR, "long varchar");
			registerColumnType(Types.CLOB, "long varchar");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p, $s)");
			registerColumnType(Types.DOUBLE, "double precision");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "integer");
			registerColumnType(Types.LONGVARBINARY, "long byte");
			registerColumnType(Types.LONGVARCHAR, "long varchar");
			registerColumnType(Types.NUMERIC, "numeric($p, $s)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "date");
			registerColumnType(Types.TIMESTAMP, "date");
			registerColumnType(Types.TINYINT, "tinyint");
			// I tried the following for values under 8000 but I get
			// Encountered unexpected exception - line 1, You cannot assign a
			// value of type 'long byte' to a column of type 'byte varying'.
			// Explicitly convert the value to the required type.
			// registerColumnType(Types.VARBINARY, 8000, "byte varying($l)");
			registerColumnType(Types.VARBINARY, "long byte");
			// I tried 8000 for the max length of VARCHAR and ingres gives an exception
			// (cannot assign a value of type long varchar to a varchar field). So
			// I limit this field to 4000 for now - the Ingres product documentation
			// indicated that 32k was acceptable. I've tested 4k and it seems to
			// work fine.
			registerColumnType(Types.VARCHAR, 4000, "varchar($l)");
			registerColumnType(Types.VARCHAR, "long varchar");
		}
	}

	/** extended hibernate dialect used in this wrapper */
	private IngresDialectHelper _dialect = new IngresDialectHelper();

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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsSchemasInTableDefinition()
	 */
	public boolean supportsSchemasInTableDefinition()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getMaxPrecision(int)
	 */
	public int getMaxPrecision(int dataType)
	{
		// float(54) produces an exception:
		//
		// invalid column format 'float' on column 'float_column'.,
		// SQL State: 42000, Error Code: 2014
		if (dataType == Types.FLOAT)
		{
			return 53;
		} else
		{
			return 31;
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDisplayName()
	 */
	public String getDisplayName()
	{
		return "Ingres";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsProduct(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		if (databaseProductName.trim().toLowerCase().startsWith("ingres"))
		{
			// We don't yet have the need to discriminate by version.
			return true;
		}
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnDropSQL(java.lang.String,
	 *      java.lang.String, DatabaseObjectQualifier, SqlGenerationPreferences)
	 */
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String dropClause = DialectUtils.DROP_COLUMN_CLAUSE;
		// TODO: Need to allow user to specify this
		String constraintClause = "CASCADE";

		return DialectUtils.getColumnDropSQL(tableName,
			columnName,
			dropClause,
			true,
			constraintClause,
			qualifier,
			prefs,
			this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getTableDropSQL(net.sourceforge.squirrel_sql.fw.sql.ITableInfo,
	 *      boolean, boolean, DatabaseObjectQualifier, SqlGenerationPreferences)
	 */
	public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints,
		boolean isMaterializedView, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getTableDropSQL(iTableInfo,
			false,
			cascadeConstraints,
			false,
			DialectUtils.CASCADE_CLAUSE,
			false, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAddPrimaryKeySQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo[],
	 *      net.sourceforge.squirrel_sql.fw.sql.ITableInfo, DatabaseObjectQualifier, SqlGenerationPreferences)
	 */
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		for (int i = 0; i < columns.length; i++)
		{
			TableColumnInfo info = columns[i];
			String notNullSQL = DialectUtils.getColumnNullableAlterSQL(info, false, this, alterClause, true, qualifier, prefs);
			result.add(notNullSQL);
		}
		result.add(DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, false, qualifier, prefs, this));
		return result.toArray(new String[result.size()]);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsColumnComment()
	 */
	public boolean supportsColumnComment()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnCommentAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		return DialectUtils.getColumnCommentAlterSQL(info, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAlterColumnNull()
	 */
	public boolean supportsAlterColumnNull()
	{
		// I see this on Ingres 2006 R3 when altering a column to be not null
		// com.ingres.gcf.util.SqlEx: ALTER TABLE: invalid change of attributes on an ALTER COLUMN
		//   at com.ingres.gcf.jdbc.DrvObj.readError(DrvObj.java:844)
		//   at com.ingres.gcf.jdbc.JdbcStmt.readError(JdbcStmt.java:2412)
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnNullableAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.COLUMN_NULL_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsRenameColumn()
	 */
	public boolean supportsRenameColumn()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnNameAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo, DatabaseObjectQualifier,
	 *      SqlGenerationPreferences)
	 */
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.COLUMN_NAME_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAlterColumnType()
	 */
	public boolean supportsAlterColumnType()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnTypeAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String setClause = "";
		return DialectUtils.getColumnTypeAlterSQL(this, alterClause, setClause, false, from, to, qualifier, prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAlterColumnDefault()
	 */
	public boolean supportsAlterColumnDefault()
	{
		return true;
	}

	/**
	 * Returns the SQL command to change the specified column's default value alter table test2 alter column
	 * mycol char(10) default 'foo' Note: This worked in a previous release of Ingres, however, it no longer
	 * works and fails with: Exception in thread "main" com.ingres.gcf.util.SqlEx: ALTER TABLE: invalid change
	 * of attributes on an ALTER COLUMN at com.ingres.gcf.jdbc.DrvObj.readError(DrvObj.java:844) I would file a
	 * bug if I knew where the bug database was. Too bad for them.
	 * 
	 * @param info
	 *           the column to modify and it's default value.
	 * @return SQL to make the change
	 */
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String defaultClause = DialectUtils.DEFAULT_CLAUSE;
		return DialectUtils.getColumnDefaultAlterSQL(this, info, alterClause, true, defaultClause, qualifier, prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDropPrimaryKeySQL(java.lang.String,
	 *      java.lang.String, DatabaseObjectQualifier, SqlGenerationPreferences)
	 */
	public String getDropPrimaryKeySQL(String pkName, String tableName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, true, true, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDropForeignKeySQL(java.lang.String,
	 *      java.lang.String, DatabaseObjectQualifier, SqlGenerationPreferences)
	 */
	public String getDropForeignKeySQL(String fkName, String tableName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropForeignKeySQL(fkName, tableName, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getCreateTableSQL(java.util.List,
	 *      net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData,
	 *      net.sourceforge.squirrel_sql.fw.dialects.CreateScriptPreferences, boolean)
	 */
	public List<String> getCreateTableSQL(List<ITableInfo> tables, ISQLDatabaseMetaData md,
		CreateScriptPreferences prefs, boolean isJdbcOdbc) throws SQLException
	{
		return DialectUtils.getCreateTableSQL(tables, md, this, prefs, isJdbcOdbc);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getCreateTableSQL(java.lang.String,
	 *      java.util.List, java.util.List, net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier)
	 */
	public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{
		return DialectUtils.getCreateTableSQL(tableName, columns, primaryKeys, prefs, qualifier, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDialectType()
	 */
	public DialectType getDialectType()
	{
		return DialectType.INGRES;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getIndexAccessMethodsTypes()
	 */
	public String[] getIndexAccessMethodsTypes()
	{
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getIndexStorageOptions()
	 */
	public String[] getIndexStorageOptions()
	{
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAddAutoIncrementSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final String msg = DialectUtils.getUnsupportedMessage(this, DialectUtils.ADD_AUTO_INCREMENT_TYPE);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAddColumnSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();
		boolean addDefaultClause = false;
		boolean supportsNullQualifier = false;
		boolean addNullClause = false;

		// In Ingres2006, these work:
		// ALTER TABLE test1 ADD "nullint" integer 
		// ALTER TABLE test1 ADD "nullint" integer not null with default
		// 
		// This doesn't:
		// ALTER TABLE test1 ADD "nullint" integer with default
		
		
		String sql =
			DialectUtils.getAddColumSQL(column,
				this,
				addDefaultClause,
				supportsNullQualifier,
				addNullClause,
				qualifier,
				prefs);

		result.add(sql);

		StringTemplate st = new StringTemplate(ST_MODIFY_TABLE_TO_RECONSTRUCT);

		result.add(DialectUtils.bindTemplateAttributes(this, st, DialectUtils.getValuesMap(ST_TABLE_NAME_KEY,
			column.getTableName()), qualifier, prefs));

		// Ingres requires that columns that are to become not null must have a
		// default value.
		if (column.isNullable().equals("NO"))
		{
			result.add(getColumnDefaultAlterSQL(column, qualifier, prefs));
			result.addAll(Arrays.asList(getColumnNullableAlterSQL(column, qualifier, prefs)));
		}
		if (column.getRemarks() != null && !"".equals(column.getRemarks()))
		{
			result.add(getColumnCommentAlterSQL(column, qualifier, prefs));
		}
		return result.toArray(new String[result.size()]);
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
		// Alter table <table> add constraint <constraint>
		// foreign key(<column>) references <table>(<column>)
		// on delete set null

		// "ALTER TABLE $childTableName$ " +
		// "ADD $constraint$ $constraintName$ FOREIGN KEY ( $childColumn; separator=\",\"$ ) " +
		// "REFERENCES $parentTableName$ ( $parentColumn; separator=\",\"$ )";

		StringTemplate fkST = new StringTemplate(ST_ADD_FOREIGN_KEY_CONSTRAINT_STYLE_ONE);
		HashMap<String, String> fkValuesMap = DialectUtils.getValuesMap(ST_CHILD_TABLE_KEY, localTableName);
		fkValuesMap.put(ST_CONSTRAINT_KEY, "CONSTRAINT");
		fkValuesMap.put(ST_CONSTRAINT_NAME_KEY, constraintName);
		fkValuesMap.put(ST_PARENT_TABLE_KEY, refTableName);

		StringTemplate childIndexST = null;
		HashMap<String, String> ckIndexValuesMap = null;

		if (autoFKIndex)
		{
			// "CREATE $unique$ $storageOption$ INDEX $indexName$ " +
			// "ON $tableName$ ( $columnName; separator=\",\"$ )";

			childIndexST = new StringTemplate(ST_CREATE_INDEX_STYLE_TWO);
			ckIndexValuesMap = new HashMap<String, String>();
			ckIndexValuesMap.put(ST_INDEX_NAME_KEY, "fk_child_idx");
		}

		return DialectUtils.getAddForeignKeyConstraintSQL(fkST,
			fkValuesMap,
			childIndexST,
			ckIndexValuesMap,
			localRefColumns,
			qualifier,
			prefs,
			this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAddUniqueConstraintSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo[],
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// alter table <table> add constraint <constraint>

		// alter table foo add constraint foocon UNIQUE (myid)

		// "ALTER TABLE $tableName$ " +
		// "ADD CONSTRAINT $constraintName$ UNIQUE ($columnName; separator=\",\"$)";

		StringTemplate st = new StringTemplate(ST_ADD_UNIQUE_CONSTRAINT_STYLE_TWO);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, tableName, ST_CONSTRAINT_NAME_KEY, constraintName);

		return new String[] { DialectUtils.getAddUniqueConstraintSQL(st,
			valuesMap,
			columns,
			qualifier,
			prefs,
			this) };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAlterSequenceSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// "ALTER SEQUENCE $sequenceName$ $startWith$ $increment$ $minimum$ $maximum$ $cache$ $cycle$";

		StringTemplate st = new StringTemplate(ST_ALTER_SEQUENCE_STYLE_TWO);

		OptionalSqlClause incClause = new OptionalSqlClause(DialectUtils.INCREMENT_BY_CLAUSE, increment);
		OptionalSqlClause minClause = new OptionalSqlClause(DialectUtils.MINVALUE_CLAUSE, minimum);
		OptionalSqlClause maxClause = new OptionalSqlClause(DialectUtils.MAXVALUE_CLAUSE, maximum);
		OptionalSqlClause cacheClause = new OptionalSqlClause(DialectUtils.CACHE_CLAUSE, cache);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_SEQUENCE_NAME_KEY,
				sequenceName,
				ST_INCREMENT_KEY,
				incClause,
				ST_MINIMUM_KEY,
				minClause,
				ST_MAXIMUM_KEY,
				maxClause,
				ST_CACHE_KEY,
				cacheClause);
		if (cycle)
		{
			valuesMap.put(ST_CYCLE_KEY, "CYCLE");
		}

		return new String[] { DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs) };

	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getCreateIndexSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String[], boolean, java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getCreateSequenceSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// "CREATE SEQUENCE $sequenceName$ $startWith$ $increment$ $minimum$ $maximum$ $cache$ $cycle$";

		StringTemplate st = new StringTemplate(ST_CREATE_SEQUENCE_STYLE_TWO);

		OptionalSqlClause incClause = new OptionalSqlClause(DialectUtils.INCREMENT_BY_CLAUSE, increment);
		OptionalSqlClause minClause = new OptionalSqlClause(DialectUtils.MINVALUE_CLAUSE, minimum);
		OptionalSqlClause maxClause = new OptionalSqlClause(DialectUtils.MAXVALUE_CLAUSE, maximum);
		OptionalSqlClause cacheClause = new OptionalSqlClause(DialectUtils.CACHE_CLAUSE, cache);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_SEQUENCE_NAME_KEY,
				sequenceName,
				ST_INCREMENT_KEY,
				incClause,
				ST_MINIMUM_KEY,
				minClause,
				ST_MAXIMUM_KEY,
				maxClause,
				ST_CACHE_KEY,
				cacheClause);
		if (cycle)
		{
			valuesMap.put(ST_CYCLE_KEY, "CYCLE");
		}

		return DialectUtils.getCreateSequenceSQL(st, valuesMap, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getCreateViewSQL(java.lang.String,
	 *      java.lang.String, java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// [EXEC SQL] CREATE VIEW view_name
		// [(column_name {, column_name})]
		// AS select_stmt
		// [WITH CHECK OPTION]

		// "CREATE VIEW $viewName$ " +
		// "AS $selectStatement$ $with$ $checkOptionType$ $checkOption$";

		StringTemplate st = new StringTemplate(ST_CREATE_VIEW_STYLE_ONE);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_VIEW_NAME_KEY, viewName, ST_SELECT_STATEMENT_KEY, definition);

		if (checkOption != null)
		{
			valuesMap.put(ST_WITH_CHECK_OPTION_KEY, DialectUtils.WITH_CHECK_OPTION_CLAUSE);
		}

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDropConstraintSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDropIndexSQL(java.lang.String,
	 *      java.lang.String, boolean, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDropSequenceSQL(java.lang.String,
	 *      boolean, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// "DROP SEQUENCE $sequenceName$ $cascade$";
		StringTemplate st = new StringTemplate(ST_DROP_SEQUENCE_STYLE_ONE);

		HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_SEQUENCE_NAME_KEY, sequenceName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDropViewSQL(java.lang.String,
	 *      boolean, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// "DROP VIEW $viewName$";
		StringTemplate st = new StringTemplate(ST_DROP_VIEW_STYLE_ONE);

		HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_VIEW_NAME_KEY, viewName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getInsertIntoSQL(java.lang.String,
	 *      java.util.List, java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getInsertIntoSQL(tableName, columns, valuesPart, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getRenameTableSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.RENAME_TABLE_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getRenameViewSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		int featureId = DialectUtils.RENAME_VIEW_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getSequenceInformationSQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String result =
			"SELECT next_value-1, max_value, min_value, cache_size, " + "increment_value, " + "case "
				+ "    when cycle_flag = 'Y' " + "    then 1 " + "    else 0 " + "end " + "FROM iisequences "
				+ "where seq_name = ?";

		return result;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getUpdateSQL(java.lang.String,
	 *      java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String[],
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getUpdateSQL(String tableName, String[] setColumns, String[] setValues,
		String[] fromTables, String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		StringTemplate st = new StringTemplate(ST_UPDATE_CORRELATED_QUERY_STYLE_TWO);

		return DialectUtils.getUpdateSQL(st,
			tableName,
			setColumns,
			setValues,
			fromTables,
			whereColumns,
			whereValues,
			qualifier,
			prefs,
			this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAccessMethods()
	 */
	public boolean supportsAccessMethods()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAddForeignKeyConstraint()
	 */
	public boolean supportsAddForeignKeyConstraint()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAddUniqueConstraint()
	 */
	public boolean supportsAddUniqueConstraint()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAlterSequence()
	 */
	public boolean supportsAlterSequence()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAutoIncrement()
	 */
	public boolean supportsAutoIncrement()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsCheckOptionsForViews()
	 */
	public boolean supportsCheckOptionsForViews()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsCreateIndex()
	 */
	public boolean supportsCreateIndex()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsCreateSequence()
	 */
	public boolean supportsCreateSequence()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsCreateTable()
	 */
	public boolean supportsCreateTable()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsCreateView()
	 */
	public boolean supportsCreateView()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsDropConstraint()
	 */
	public boolean supportsDropConstraint()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsDropIndex()
	 */
	public boolean supportsDropIndex()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsDropSequence()
	 */
	public boolean supportsDropSequence()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsDropView()
	 */
	public boolean supportsDropView()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsEmptyTables()
	 */
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsInsertInto()
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsRenameTable()
	 */
	public boolean supportsRenameTable()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsRenameView()
	 */
	public boolean supportsRenameView()
	{
		return false;
	}

	public boolean supportsSequence()
	{
		return true;
	}

	public boolean supportsSequenceInformation()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsTablespace()
	 */
	public boolean supportsTablespace()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsUpdate()
	 */
	public boolean supportsUpdate()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsAddColumn()
	 */
	public boolean supportsAddColumn()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsViewDefinition()
	 */
	public boolean supportsViewDefinition()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getViewDefinitionSQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// SELECT TEXT_SEGMENT FROM IIVIEWS
		// WHERE TABLE_NAME = <VIEWNAME> AND TABLE_OWNER = <SCHEMANAME>
		StringBuilder result = new StringBuilder();
		result.append("SELECT TEXT_SEGMENT FROM IIVIEWS ");
		result.append("WHERE TABLE_NAME = '");
		result.append(viewName);
		result.append("' AND TABLE_OWNER = '");
		result.append(qualifier.getSchema());
		result.append("'");

		return result.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getQualifiedIdentifier(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getQualifiedIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return identifier;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsCorrelatedSubQuery()
	 */
	public boolean supportsCorrelatedSubQuery()
	{
		return true;
	}

}
