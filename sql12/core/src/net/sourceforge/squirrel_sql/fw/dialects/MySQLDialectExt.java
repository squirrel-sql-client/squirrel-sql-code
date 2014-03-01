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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.antlr.stringtemplate.StringTemplate;
import org.hibernate.HibernateException;

/**
 * A dialect delegate for the MySQL database.
 */
public class MySQLDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	/**
	 * The hibernate extension that we delegate certain operations to. This is mostly just used for resolving
	 * the sql data type.
	 * 
	 * @author manningr
	 */
	class MySQLDialectHelper extends org.hibernate.dialect.MySQLDialect
	{
		public MySQLDialectHelper()
		{
			super();
			registerColumnType(Types.BIGINT, "bigint");
			registerColumnType(Types.BINARY, 255, "binary($l)");
			registerColumnType(Types.BINARY, 65532, "blob");
			registerColumnType(Types.BINARY, "longblob");
			registerColumnType(Types.BIT, "bit");
			registerColumnType(Types.BLOB, 65532, "blob");
			registerColumnType(Types.BLOB, "longblob");
			registerColumnType(Types.BOOLEAN, "bool");
			registerColumnType(Types.CHAR, 255, "char($l)");
			registerColumnType(Types.CHAR, 65532, "text");
			registerColumnType(Types.CHAR, "longtext");
			registerColumnType(Types.CLOB, "longtext");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p,$s)");
			registerColumnType(Types.DOUBLE, "double");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "int");
			registerColumnType(Types.LONGVARBINARY, "longblob");
			registerColumnType(Types.LONGVARCHAR, "longtext");
			registerColumnType(Types.NUMERIC, "numeric($p,$s)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "tinyint");
			registerColumnType(Types.VARBINARY, 255, "varbinary($l)");
			registerColumnType(Types.VARBINARY, "blob");
			registerColumnType(Types.VARCHAR, "text");
		}
	}

	/** extended hibernate dialect used in this wrapper */
	private final MySQLDialectHelper _dialect = new MySQLDialectHelper();

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getTypeName(int, int, int, int)
	 */
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getMaxPrecision(int)
	 */
	@Override
	public int getMaxPrecision(int dataType)
	{
		if (dataType == Types.FLOAT)
		{
			return 53;
		}
		else
		{
			return 38;
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDisplayName()
	 */
	@Override
	public String getDisplayName()
	{
		return "MySQL";
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
	@Override
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null) { return false; }
		if (databaseProductName.trim().toLowerCase().startsWith("mysql")
			&& !databaseProductVersion.startsWith("5")) { return true; }
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
	// public String[] getColumnAddSQL(TableColumnInfo info) throws UnsupportedOperationException
	// {
	// ArrayList<String> returnVal = new ArrayList<String>();
	// StringBuilder result = new StringBuilder();
	// result.append("ALTER TABLE ");
	// result.append(info.getTableName());
	// result.append(" ADD COLUMN ");
	// result.append(info.getColumnName());
	// result.append(" ");
	// result.append(DialectUtils.getTypeName(info, this));
	// result.append(" ");
	// DialectUtils.appendDefaultClause(info, result);
	// if (info.getRemarks() != null && !"".equals(info.getRemarks()))
	// {
	// result.append(" COMMENT ");
	// result.append("'");
	// result.append(info.getRemarks());
	// result.append("'");
	// }
	// returnVal.add(result.toString());
	// if (info.isNullable().equals("NO"))
	// {
	// String setNullSQL = getModifyColumnNullabilitySQL(info.getTableName(), info, false);
	// returnVal.add(setNullSQL);
	// }
	// // Sometimes, MySQL omits the change for COMMENT, so explicitly add
	// // it in a separate alter statement as well
	// if (info.getRemarks() != null && !"".equals(info.getRemarks()))
	// {
	// returnVal.add(getColumnCommentAlterSQL(info, null, null));
	// }
	// // Sometimes, MySQL omits the change for DEFAULT, so explicitly add
	// // it in a separate alter statement as well
	// // returnVal.add()
	// if (info.getDefaultValue() != null && !"".equals(info.getDefaultValue()))
	// {
	// returnVal.add(getColumnDefaultAlterSQL(info, qualifier, prefs));
	// }
	//
	// return returnVal.toArray(new String[returnVal.size()]);
	// }
	/**
	 * @param tableName
	 * @param info
	 * @param nullable
	 * @return
	 */
	public String getModifyColumnNullabilitySQL(String tableName, TableColumnInfo info, boolean nullable)
	{
		final StringBuilder result = new StringBuilder();
		result.append(" ALTER TABLE ");
		result.append(tableName);
		result.append(" MODIFY ");
		result.append(info.getColumnName());
		result.append(" ");
		result.append(DialectUtils.getTypeName(info, this));
		if (nullable)
		{
			result.append(" NULL ");
		}
		else
		{
			result.append(" NOT NULL ");
		}
		return result.toString();
	}

	/**
	 * Returns a boolean value indicating whether or not this database dialect supports changing a column's
	 * default value.
	 * 
	 * @return true if the database supports modifying column defaults; false otherwise
	 */
	@Override
	public boolean supportsAlterColumnDefault()
	{
		return true;
	}

	/**
	 * Returns SQL statement used to add the default value of the specified column.
	 * 
	 * @param info
	 * @return
	 * @throws UnsupportedOperationException
	 */
	@Override
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		final StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(info.getTableName());
		result.append(" MODIFY ");
		result.append(info.getColumnName());
		result.append(" ");
		result.append(DialectUtils.getTypeName(info, this));
		DialectUtils.appendDefaultClause(info, result);
		return result.toString();
	}

	/**
	 * Returns a boolean value indicating whether or not this dialect supports adding comments to columns.
	 * 
	 * @return true if column comments are supported; false otherwise.
	 */
	@Override
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
	@Override
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		final StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(info.getTableName());
		result.append(" MODIFY ");
		result.append(info.getColumnName());
		result.append(" ");
		result.append(DialectUtils.getTypeName(info, this));
		result.append(" COMMENT '");
		result.append(info.getRemarks());
		result.append("'");
		return result.toString();
	}

	/**
	 * Returns a boolean value indicating whether or not this database dialect supports dropping columns from
	 * tables.
	 * 
	 * @return true if the database supports dropping columns; false otherwise.
	 */
	@Override
	public boolean supportsDropColumn()
	{
		return true;
	}

	/**
	 * Returns a boolean value indicating whether or not this database dialect supports changing a column from
	 * null to not-null and vice versa.
	 * 
	 * @return true if the database supports dropping columns; false otherwise.
	 */
	@Override
	public boolean supportsAlterColumnNull()
	{
		return true;
	}

	/**
	 * Returns the SQL used to alter the specified column to not allow null values ALTER TABLE testdate MODIFY
	 * mydate date NOT NULL;
	 * 
	 * @param info
	 *           the column to modify
	 * @return the SQL to execute
	 */
	@Override
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final String alterClause = DialectUtils.MODIFY_COLUMN_CLAUSE;
		
		// MySQL disallows quoted column identifiers.
		prefs.setQuoteColumnNames(false);
		
		String columnNullableAlterSql =
			DialectUtils.getColumnNullableAlterSQL(info, this, alterClause, true, qualifier, prefs);
		
		return new String[] { columnNullableAlterSql };
	}

	/**
	 * Returns a boolean value indicating whether or not this database dialect supports renaming columns.
	 * 
	 * @return true if the database supports changing the name of columns; false otherwise.
	 */
	@Override
	public boolean supportsRenameColumn()
	{
		return true;
	}

	/**
	 * Returns the SQL that is used to change the column name. ALTER TABLE t1 CHANGE a b INTEGER;
	 * 
	 * @param from
	 *           the TableColumnInfo as it is
	 * @param to
	 *           the TableColumnInfo as it wants to be
	 * @return the SQL to make the change
	 */
	@Override
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(from.getTableName());
		result.append(" CHANGE ");
		result.append(from.getColumnName());
		result.append(" ");
		result.append(to.getColumnName());
		result.append(" ");
		result.append(DialectUtils.getTypeName(from, this));
		return result.toString();
	}

	/**
	 * Returns a boolean value indicating whether or not this dialect supports modifying a columns type.
	 * 
	 * @return true if supported; false otherwise
	 */
	@Override
	public boolean supportsAlterColumnType()
	{
		return true;
	}

	/**
	 * Returns the SQL that is used to change the column type. ALTER TABLE t1 CHANGE b b BIGINT NOT NULL;
	 * 
	 * @param from
	 *           the TableColumnInfo as it is
	 * @param to
	 *           the TableColumnInfo as it wants to be
	 * @return the SQL to make the change
	 * @throw UnsupportedOperationException if the database doesn't support modifying column types.
	 */
	@Override
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		final StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(from.getTableName());
		result.append(" CHANGE ");
		// Always use "to" column name since name changes happen first
		result.append(to.getColumnName());
		result.append(" ");
		result.append(to.getColumnName());
		result.append(" ");
		result.append(DialectUtils.getTypeName(to, this));
		final ArrayList<String> list = new ArrayList<String>();
		list.add(result.toString());
		return list;
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
	@Override
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
	@Override
	public String getDropForeignKeySQL(String fkName, String tableName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final StringBuilder tmp = new StringBuilder();
		tmp.append("ALTER TABLE ");
		tmp.append(tableName);
		tmp.append(" DROP FOREIGN KEY ");
		tmp.append(fkName);
		return tmp.toString();
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
	@Override
	public List<String> getCreateTableSQL(List<ITableInfo> tables, ISQLDatabaseMetaData md,
		CreateScriptPreferences prefs, boolean isJdbcOdbc) throws SQLException
	{
		return DialectUtils.getCreateTableSQL(tables, md, this, prefs, isJdbcOdbc);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDialectType()
	 */
	@Override
	public DialectType getDialectType()
	{
		return DialectType.MYSQL;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getIndexAccessMethodsTypes()
	 */
	@Override
	public String[] getIndexAccessMethodsTypes()
	{
		return new String[] { "UNIQUE", "FULLTEXT", "SPATIAL" };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getIndexStorageOptions()
	 */
	@Override
	public String[] getIndexStorageOptions()
	{
		return new String[] { "BTREE", "HASH" };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddAutoIncrementSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// ALTER TABLE <tableName> MODIFY <columnName> MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY
		final String templateStr = ST_ADD_AUTO_INCREMENT_STYLE_ONE;
		final StringTemplate st = new StringTemplate(templateStr);

		final HashMap<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put(ST_TABLE_NAME_KEY, column.getTableName());
		valuesMap.put(ST_COLUMN_NAME_KEY, column.getColumnName());

		String addAutoIncrementSql = DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
		addAutoIncrementSql = 
			DialectUtils.stripQuotesFromIdentifier(this, column.getColumnName(), addAutoIncrementSql);
		return new String[] { addAutoIncrementSql };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddColumnSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final boolean addDefaultClause = true;
		final boolean supportsNullQualifier = true;
		final boolean addNullClause = true;

		// MySQL disallows quoted column identifiers.
		prefs.setQuoteColumnNames(false);
		
		String addColumnSql =
			DialectUtils.getAddColumSQL(column, this, addDefaultClause, supportsNullQualifier, addNullClause,
				qualifier, prefs);

		return new String[] { addColumnSql };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddForeignKeyConstraintSQL(java.lang.String,
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
		// From MySQL 5.0 Reference:
		// ALTER TABLE tbl_name
		// ADD [CONSTRAINT symbol] FOREIGN KEY [id] (index_col_name, ...)
		// REFERENCES tbl_name (index_col_name, ...)
		// [ON DELETE {RESTRICT | CASCADE | SET NULL | NO ACTION}]
		// [ON UPDATE {RESTRICT | CASCADE | SET NULL | NO ACTION}]

		final String fkTemplateStr = ST_ADD_FOREIGN_KEY_CONSTRAINT_STYLE_ONE;

		final StringTemplate fkst = new StringTemplate(fkTemplateStr);
		final HashMap<String, String> fkValuesMap = new HashMap<String, String>();
		fkValuesMap.put(ST_CHILD_TABLE_KEY, localTableName);
		if (constraintName != null)
		{
			fkValuesMap.put(ST_CONSTRAINT_KEY, "CONSTRAINT");
			fkValuesMap.put(ST_CONSTRAINT_NAME_KEY, constraintName);
		}
		fkValuesMap.put(ST_PARENT_TABLE_KEY, refTableName);

		StringTemplate ckIndexSt = null;
		HashMap<String, String> ckIndexValuesMap = null;

		if (autoFKIndex)
		{
			// "CREATE $unique$ $storageOption$ INDEX $indexName$ " +
			// "ON $tableName$ ( $columnName; separator=\",\"$ )";

			ckIndexSt = new StringTemplate(ST_CREATE_INDEX_STYLE_TWO);
			ckIndexValuesMap = new HashMap<String, String>();
			ckIndexValuesMap.put(ST_INDEX_NAME_KEY, "fk_child_idx");
			ckIndexValuesMap.put(ST_TABLE_NAME_KEY, localTableName);
		}

		// MySQL disallows quoted column identifiers when creating an index on a column.
		prefs.setQuoteColumnNames(false);		
		
		return DialectUtils.getAddForeignKeyConstraintSQL(fkst, fkValuesMap, ckIndexSt, ckIndexValuesMap,
			localRefColumns, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddUniqueConstraintSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo[],
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// From MySQL 5.0 reference manual
		//
		// ALTER [IGNORE] TABLE tbl_name
		// alter_specification [, alter_specification] ...
		// 
		// alter_specification:
		// | ADD [CONSTRAINT [symbol]] UNIQUE [INDEX|KEY] [index_name] [index_type] (index_col_name,...)

		final String templateStr = ST_ADD_UNIQUE_CONSTRAINT_STYLE_ONE;

		final StringTemplate st = new StringTemplate(templateStr);
		st.setAttribute(ST_TABLE_NAME_KEY, tableName);
		if (constraintName != null)
		{
			st.setAttribute(ST_CONSTRAINT_KEY, "CONSTRAINT");
			st.setAttribute(ST_CONSTRAINT_NAME_KEY, constraintName);
		}

		// TODO: allow the user to choose the name of the index that is created.
		// if (indexName != null) {
		// st.setAttribute(ST_INDEX_KEY, indexName);
		// st.setAttribute(ST_INDEX_NAME_KEY, indexName);
		// }

		// TODO: allow the user to choose the index type that is created.
		// if (indexType != null) {
		// st.setAttribute(ST_INDEX_TYPE_KEY, indexType);
		// }

		for (final TableColumnInfo columnInfo : columns)
		{
			st.setAttribute(ST_COLUMN_NAME_KEY, columnInfo.getColumnName());
		}

		return new String[] { st.toString() };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAlterSequenceSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.ALTER_SEQUENCE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getCreateIndexSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String[], boolean, java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO: SPATIAL and FULLTEXT indexes require a MyISAM engine for the table. Is there a way
		// to tell what engine is being used for a table? It may not be necessary, since the following
		// doens't hurt if already a MyISAM engine:
		//
		// ALTER TABLE my_table ENGINE = MYISAM;
		//
		// Still, this is not the kind of thing we would want to do automatically, since MyISAM engine is
		// non-transactional. We will probably need to tell the user - somehow - that they need this
		// otherwise the create index statement will fail. Maybe a comment in the script and if they
		// happen to read it they could uncomment the conversion of the engine? Maybe a custom dialog?

		/*
		 * From MySQL 5.0 manual:
		 */
		// CREATE [UNIQUE|FULLTEXT|SPATIAL] INDEX index_name
		// [index_type]
		// ON tbl_name (index_col_name,...)
		//
		// index_col_name:
		// col_name [(length)] [ASC | DESC]
		//	
		// index_type:
		// USING {BTREE | HASH}
		// Note; indexType is unused at the moment because the index dialog doesn't accept this. See below.
		
		// MySQL disallows quoted column identifiers.

		final String templateStr = ST_CREATE_INDEX_STYLE_ONE;

		final StringTemplate st = new StringTemplate(templateStr);

		final HashMap<String, String> valuesMap = new HashMap<String, String>();

		if (accessMethod != null && !accessMethod.toLowerCase().equals("default"))
		{
			valuesMap.put(ST_ACCESS_METHOD_KEY, accessMethod);
		}
		valuesMap.put(ST_INDEX_NAME_KEY, indexName);
		// TODO: Need to enhance the index dialog to allow specifying storage option. For now just accept the
		// default for the index access method.
		// valuesMap.put("indexType", "USING BTREE");
		valuesMap.put(ST_TABLE_NAME_KEY, tableName);

		String addIndexSql = DialectUtils.getAddIndexSQL(this, st, valuesMap, columns, qualifier, prefs);
		for (String column : columns) {
			addIndexSql = DialectUtils.stripQuotesFromIdentifier(this, column, addIndexSql);
		}
		
		return addIndexSql;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getCreateSequenceSQL(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.CREATE_SEQUENCE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getCreateTableSQL(java.lang.String,
	 *      java.util.List, java.util.List, net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier)
	 */
	@Override
	public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{
		// MySQL disallows quoted column identifiers.
		prefs.setQuoteColumnNames(false);
		
		return  DialectUtils.getCreateTableSQL(tableName, columns, primaryKeys, prefs, qualifier, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getCreateViewSQL(java.lang.String,
	 *      java.lang.String, java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.CREATE_VIEW_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropConstraintSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.DROP_CONSTRAINT_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropIndexSQL(String, java.lang.String,
	 *      boolean, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		final Boolean cascadeNotSupported = null;
		return DialectUtils.getDropIndexSQL(tableName, indexName, cascadeNotSupported, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropSequenceSQL(java.lang.String,
	 *      boolean, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.DROP_SEQUENCE_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropViewSQL(java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.DROP_VIEW_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getInsertIntoSQL(java.lang.String,
	 *      java.util.List, java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
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
		String renameTableSql =
			DialectUtils.getRenameTableSQL(oldTableName, newTableName, qualifier, prefs, this);

		return DialectUtils.stripQuotesFromIdentifier(this, newTableName, renameTableSql);
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getSequenceInformationSQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.SEQUENCE_INFORMATION_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getUpdateSQL(java.lang.String,
	 *      java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String[],
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getUpdateSQL(String tableName, String[] setColumns, String[] setValues,
		String[] fromTables, String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String templateStr = "";

		if (fromTables != null)
		{
			templateStr = ST_UPDATE_CORRELATED_QUERY_STYLE_TWO;
		}
		else
		{
			templateStr = ST_UPDATE_STYLE_ONE;
		}

		final StringTemplate st = new StringTemplate(templateStr);

		return DialectUtils.getUpdateSQL(st, tableName, setColumns, setValues, fromTables, whereColumns,
			whereValues, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAccessMethods()
	 */
	public boolean supportsAccessMethods()
	{
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
		return false;
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
		return false;
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
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsDropConstraint()
	 */
	public boolean supportsDropConstraint()
	{
		return false;
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
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsDropView()
	 */
	public boolean supportsDropView()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsEmptyTables()
	 */
	public boolean supportsEmptyTables()
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsInsertInto()
	 */
	public boolean supportsInsertInto()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsMultipleRowInserts()
	 */
	public boolean supportsMultipleRowInserts()
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
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsSequenceInformation()
	 */
	public boolean supportsSequenceInformation()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsTablespace()
	 */
	public boolean supportsTablespace()
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAddColumn()
	 */
	public boolean supportsAddColumn()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsViewDefinition()
	 */
	public boolean supportsViewDefinition()
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
		throw new UnsupportedOperationException("getViewDefinitionSQL: MySQL 4 and below doesn't support views");
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getColumnDropSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		// MySQL disallows quoted column identifiers when dropping a column.
		prefs.setQuoteColumnNames(false);
		// MySQL disallows quoted constraint names
		prefs.setQuoteConstraintNames(false);
		
		return super.getColumnDropSQL(tableName, columnName, qualifier, prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getAddPrimaryKeySQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo[],
	 *      net.sourceforge.squirrel_sql.fw.sql.ITableInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] colInfos, ITableInfo ti,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{		
		// MySQL disallows quoted column identifiers when adding a primary key.
		prefs.setQuoteColumnNames(false);
		// MySQL disallows quoted constraint names
		prefs.setQuoteConstraintNames(false);
		
		return super.getAddPrimaryKeySQL(pkName, colInfos, ti, qualifier, prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getBinaryLiteralString(byte[])
	 */
	@Override
	public String getBinaryLiteralString(byte[] binaryData)
	{
		return "x" + DialectUtils.toHexString(binaryData);
	}

}
