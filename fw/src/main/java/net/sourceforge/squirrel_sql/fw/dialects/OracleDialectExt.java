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

import static java.sql.DatabaseMetaData.importedKeyNoAction;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.CYCLE_CLAUSE;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.NOCYCLE_CLAUSE;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.RENAME_CLAUSE;
import static net.sourceforge.squirrel_sql.fw.dialects.DialectUtils.TO_CLAUSE;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
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
import org.hibernate.HibernateException;


/**
 * A dialect delegate for the Oracle database.
 */
public class OracleDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class OracleDialectHelper extends org.hibernate.dialect.Oracle9Dialect {
		public OracleDialectHelper()
		{
			super();
			registerColumnType(Types.BIGINT, "number($p)");
			registerColumnType(Types.BINARY, 2000, "raw($l)");
			registerColumnType(Types.BINARY, "blob");
			registerColumnType(Types.BIT, "smallint");
			registerColumnType(Types.BLOB, "blob");
			registerColumnType(Types.BOOLEAN, "smallint");
			registerColumnType(Types.CHAR, 2000, "char($l)");
			registerColumnType(Types.CHAR, 4000, "varchar2($l)");
			registerColumnType(Types.CHAR, "clob");
			registerColumnType(Types.CLOB, "clob");
			registerColumnType(Types.NCLOB, "nclob");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p)");
			registerColumnType(Types.DOUBLE, "float($p)");
			registerColumnType(Types.FLOAT, "float($p)");
			registerColumnType(Types.INTEGER, "int");
			registerColumnType(Types.LONGNVARCHAR, 2000, "nvarchar2($l)");
			registerColumnType(Types.LONGNVARCHAR, "nclob");
			registerColumnType(Types.LONGVARBINARY, "blob");
			registerColumnType(Types.LONGVARCHAR, 4000, "varchar2($l)");
			registerColumnType(Types.LONGVARCHAR, "clob");
			registerColumnType(Types.NCHAR, 2000, "nchar($l)");
			registerColumnType(Types.NUMERIC, "number($p)");
			registerColumnType(Types.NVARCHAR, 2000, "nvarchar2($l)");
			registerColumnType(Types.NVARCHAR, "nclob");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "date");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "smallint");
			registerColumnType(Types.VARBINARY, "blob");
			registerColumnType(Types.VARCHAR, 4000, "varchar2($l)");
			registerColumnType(Types.VARCHAR, "clob");
			// Total Hack! Type OTHER(1111) can be other types as well?
			registerColumnType(Types.OTHER, 4000, "varchar2(4000)");
			registerColumnType(Types.OTHER, "clob");

		}
		
	}
	
	/** extended hibernate dialect used in this wrapper */
	private OracleDialectHelper _dialect = new OracleDialectHelper();
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getTypeName(int, int, int, int)
	 */
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getJavaTypeForNativeType(java.lang.String)
	 */
	@Override
	public int getJavaTypeForNativeType(String nativeColumnTypeName)
	{
		if (nativeColumnTypeName.toLowerCase().equals("nvarchar2")) {
			return Types.NVARCHAR;
		}
		if (nativeColumnTypeName.toLowerCase().equals("nchar")) {
			return Types.NCHAR;
		}
		if (nativeColumnTypeName.toLowerCase().equals("nclob")) {
			return Types.NCLOB;
		}
		if (nativeColumnTypeName.toLowerCase().startsWith("TIMESTAMP")) {
			return Types.TIMESTAMP;
		}
		return super.getJavaTypeForNativeType(nativeColumnTypeName);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#canPasteTo(net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo)
	 */
	@Override
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getMaxPrecision(int)
	 */
	@Override
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getDisplayName()
	 */
	@Override
	public String getDisplayName()
	{
		return "Oracle";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsProduct(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		if (databaseProductName.trim().toLowerCase().startsWith("oracle"))
		{
			// We don't yet have the need to discriminate by version.
			return true;
		}
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#supportsColumnComment()
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
	 * @param tableName
	 *           the name of the table to create the SQL for.
	 * @return
	 * @throws UnsupportedOperationException
	 *            if the database doesn't support annotating columns with a comment.
	 */
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		return DialectUtils.getColumnCommentAlterSQL(info, qualifier, prefs, this);
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
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(DialectUtils.shapeQualifiableIdentifier(tableName, qualifier, prefs, this));
		result.append(" DROP COLUMN ");
		result.append(columnName);
		return result.toString();
	}

	/**
	 * Returns the SQL that forms the command to drop the specified table. If cascade contraints is supported
	 * by the dialect and cascadeConstraints is true, then a drop statement with cascade constraints clause
	 * will be formed.
	 * @param cascadeConstraints
	 *           whether or not to drop any FKs that may reference the specified table.
	 * @param ti
	 *           the table to drop
	 * 
	 * @return the drop SQL command.
	 */
	public List<String> getTableDropSQL(ITableInfo ti, boolean cascadeConstraints, boolean isMaterializedView, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String cascadeClause = "";
		if (!isMaterializedView)
		{
			cascadeClause = DialectUtils.CASCADE_CONSTRAINTS_CLAUSE;
		}

		return DialectUtils.getTableDropSQL(ti,
			true,
			cascadeConstraints,
			true,
			cascadeClause,
			isMaterializedView, qualifier, prefs, this);
	}

	/**
	 * Returns the SQL that forms the command to add a primary key to the specified table composed of the given
	 * column names.
	 * @param columns
	 *           the columns that form the key
	 * 
	 * @return
	 */
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(DialectUtils.shapeQualifiableIdentifier(ti.getSimpleName(), qualifier, prefs, this));
		result.append(" ADD CONSTRAINT ");
		result.append(pkName);
		result.append(" PRIMARY KEY (");
		for (int i = 0; i < columns.length; i++)
		{
			result.append(columns[i].getColumnName());
			if (i + 1 < columns.length)
			{
				result.append(", ");
			}
		}
		result.append(")");
		return new String[] { result.toString() };
	}

	/**
	 * Returns the SQL used to alter the specified column to allow/disallow null values, based on the value of
	 * isNullable. alter table test modify mycol not null
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
		result.append(" MODIFY ");
		result.append(info.getColumnName());
		if (info.isNullable().equals("YES"))
		{
			result.append(" NULL");
		} else
		{
			result.append(" NOT NULL");
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
	 * Returns the SQL that is used to change the column name.
	 * 
	 * @param from
	 *           the TableColumnInfo as it is
	 * @param to
	 *           the TableColumnInfo as it wants to be
	 * @return the SQL to make the change
	 */
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// "ALTER TABLE $tableName$ RENAME COLUMN $oldColumnName$ to $newColumnName$";
		StringTemplate st = new StringTemplate(ST_ALTER_COLUMN_NAME_STYLE_ONE);
		
		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY,
				from.getTableName(),
				ST_OLD_COLUMN_NAME_KEY,
				from.getColumnName(),
				ST_NEW_COLUMN_NAME_KEY,
				to.getColumnName());
		
		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
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
	 * Returns the SQL that is used to change the column type. alter table test modify (mycol varchar(100))
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
		ArrayList<String> result = new ArrayList<String>();

		// Oracle won't allow in-place conversion between CLOB and VARCHAR
		if ((from.getDataType() == Types.VARCHAR && to.getDataType() == Types.CLOB)
			|| (from.getDataType() == Types.CLOB && to.getDataType() == Types.VARCHAR))
		{
			// add <columnName>_2 null as CLOB
			TableColumnInfo newInfo = DialectUtils.getRenamedColumn(to, to.getColumnName() + "_2");

			String[] addSQL = this.getAddColumnSQL(newInfo, qualifier, prefs);
			for (int i = 0; i < addSQL.length; i++)
			{
				result.add(addSQL[i]);
			}

			// update table set <columnName>_2 = <columnName>
			StringBuilder updateSQL = new StringBuilder();
			updateSQL.append("update ");
			updateSQL.append(from.getTableName());
			updateSQL.append(" set ");
			updateSQL.append(newInfo.getColumnName());
			updateSQL.append(" = ");
			updateSQL.append(from.getColumnName());
			result.add(updateSQL.toString());

			// drop <columnName>
			String dropSQL = getColumnDropSQL(from.getTableName(), from.getColumnName(), qualifier, prefs);
			result.add(dropSQL);

			// rename <columnName>_2 to <columnName>
			String renameSQL = this.getColumnNameAlterSQL(newInfo, to, qualifier, prefs);
			result.add(renameSQL);
		} else
		{
			StringBuffer tmp = new StringBuffer();
			tmp.append("ALTER TABLE ");
			tmp.append(from.getTableName());
			tmp.append(" MODIFY (");
			tmp.append(from.getColumnName());
			tmp.append(" ");
			tmp.append(DialectUtils.getTypeName(to, this));
			tmp.append(")");
			result.add(tmp.toString());
		}
		return result;
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
	 * Returns the SQL command to change the specified column's default value alter table test modify mychar
	 * default 'foo' alter table test modify nullint default 0
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
		result.append(" MODIFY ");
		result.append(info.getColumnName());
		result.append(" DEFAULT ");
		if (JDBCTypeMapper.isNumberType(info.getDataType()))
		{
			result.append(info.getDefaultValue());
		} else
		{
			result.append("'");
			result.append(info.getDefaultValue());
			result.append("'");
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
		// Set ON UPDATE action to NO ACTION as Oracle doesn't currently support this.  NO ACTION causes the 
		// update clause to be omitted.
		prefs.setUpdateRefAction(true);
		prefs.setUpdateAction(importedKeyNoAction);
		
		
		return DialectUtils.getCreateTableSQL(tables, md, this, prefs, isJdbcOdbc);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDialectType()
	 */
	public DialectType getDialectType()
	{
		return DialectType.ORACLE;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getIndexAccessMethodsTypes()
	 */
	public String[] getIndexAccessMethodsTypes()
	{
		return new String[] { "default", "unique", "bitmap" };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getIndexStorageOptions()
	 */
	public String[] getIndexStorageOptions()
	{
		// TODO Auto-generated method stub		
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddAutoIncrementSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      DatabaseObjectQualifier, net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		// Cannot use a sequence for the default value of a column. However, we could always reference the
		// ssequence in a trigger:

		// :new.id := seq_name@nextval;

		//throw new UnsupportedOperationException("Oracle doesn't support using sequences for column defaults");
		
		// create add sequence sql
		String seqName = column.getColumnName() + "_AUTOINC_SEQ";
		String sequenceSql = getCreateSequenceSQL(seqName, "1", "1", null, "1", null, false, qualifier, prefs);
		
		// create trigger sql for column that is to be auto-incremented
		String tableName = column.getTableName();
		String trigName = column.getColumnName() + "_AUTOINC_TRIG";
		String triggerTemplateStr = 
			"CREATE OR REPLACE TRIGGER $triggerName$ \n" +
			"BEFORE INSERT ON $tableName$ \n" +
			"FOR EACH ROW \n" +
			"DECLARE \n" +
			"    nextid number(8) := 0; \n" +
			"BEGIN \n" +
			"    SELECT $sequenceName$.nextval into nextid from dual; \n" +
			"    :new.$columnName$ := nextid; \n" +
			"END; ";

		StringTemplate st = new StringTemplate(triggerTemplateStr);
		st.setAttribute("triggerName", trigName);
		st.setAttribute("tableName", tableName);
		st.setAttribute("sequenceName", seqName);
		st.setAttribute("columnName", column.getColumnName());
		
		return new String[] { sequenceSql, st.toString() };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddColumnSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getAddColumnSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		boolean addDefaultClause = true;
		boolean supportsNullQualifier = true;
		boolean addNullClause = true;

		prefs.setQuoteColumnNames(false);
		
		String addColumnSql =
			DialectUtils.getAddColumSQL(info,
				this,
				addDefaultClause,
				supportsNullQualifier,
				addNullClause,
				qualifier,
				prefs);

		if (info.getRemarks() != null && !"".equals(info.getRemarks()))
		{
			return new String[] { addColumnSql,
					DialectUtils.getColumnCommentAlterSQL(info, qualifier, prefs, this) };
		} else
		{
			return new String[] { addColumnSql };
		}

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
		// In Oracle, ON UPDATE actions are not supported
		String onUpdateNotSupported = null;
		// In Oracle, no action is implied when the ON DELETE clause is omitted
		String onDeleteNoAction = null;
		// In Oracle, matchFull modifier is not supported
		Boolean matchFullNotSupported = null;

		return DialectUtils.getAddForeignKeyConstraintSQL(localTableName,
			refTableName,
			constraintName,
			deferrable,
			initiallyDeferred,
			matchFullNotSupported,
			autoFKIndex,
			fkIndexName,
			localRefColumns,
			onUpdateNotSupported,
			onDeleteNoAction,
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
		prefs.setQuoteConstraintNames(false);
		prefs.setQuoteColumnNames(false);
		
		return new String[] { DialectUtils.getAddUniqueConstraintSQL(tableName,
			constraintName,
			columns,
			qualifier,
			prefs,
			this) };
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
		String[] result = null;
		Boolean cascadeNotSupported = null;
		String cycleClause = cycle ? CYCLE_CLAUSE : NOCYCLE_CLAUSE;

		if (restart != null)
		{
			String comment = "-- Oracle cannot change the start value of a sequence.";
			String comment2 = "-- Must drop and re-create.";
			String dropSql =
				DialectUtils.getDropSequenceSQL(sequenceName, cascadeNotSupported, qualifier, prefs, this);
			String createSql =
				DialectUtils.getCreateSequenceSQL(sequenceName,
					increment,
					minimum,
					maximum,
					restart,
					cache,
					cycleClause,
					qualifier,
					prefs,
					this);
			result = new String[] { comment, comment2, dropSql, createSql };
		} else
		{
			String restartNotSupported = null;
			String sql =
				DialectUtils.getAlterSequenceSQL(sequenceName,
					increment,
					minimum,
					maximum,
					restartNotSupported,
					cache,
					cycleClause,
					qualifier,
					prefs,
					this);
			result = new String[] { sql };
		}
		return result;
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
		StringBuilder result = new StringBuilder();
		result.append("CREATE ");

		// Oracle doesn't allow unique bitmap indexes.
		if (unique && ! accessMethod.equalsIgnoreCase("bitmap") )
		{
			result.append("UNIQUE ");
		}
		if (accessMethod != null && accessMethod.equalsIgnoreCase("bitmap"))
		{
			result.append(accessMethod);
			result.append(" ");
		}
		result.append("INDEX ");
		result.append(DialectUtils.shapeQualifiableIdentifier(indexName, qualifier, prefs, this));
		result.append(" ON ");
		result.append(DialectUtils.shapeQualifiableIdentifier(tableName, qualifier, prefs, this));
		result.append("(");
		for (String column : columns)
		{
			result.append(column);
			result.append(",");
		}
		result.setLength(result.length() - 1);
		result.append(")");
		return result.toString();
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
		String minValue = minimum;
		String minClause = DialectUtils.MINVALUE_CLAUSE;
		if (minValue == null || "".equals(minValue)) {
			minValue = DialectUtils.NOMINVALUE_CLAUSE;
			minClause = "";
		}
		String maxValue = maximum; 
		String maxClause = DialectUtils.MAXVALUE_CLAUSE;
		if (maxValue == null || "".equals(maxValue)) {
			maxValue = DialectUtils.NOMAXVALUE_CLAUSE;
			maxClause = "";
		}
		
		return DialectUtils.getCreateSequenceSQL(sequenceName,
			increment,
			minClause,
			minValue,
			maxClause,
			maxValue,
			start,
			cache,
			null,
			qualifier,
			prefs,
			this);
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropIndexSQL(String,
	 *      java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier, net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		Boolean cascadeNotSupported = null;
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
		Boolean cascadeNotSupported = null;
		return DialectUtils.getDropSequenceSQL(sequenceName, cascadeNotSupported, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropViewSQL(java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		Boolean cascadeNotSupported = null;
		return DialectUtils.getDropViewSQL(viewName, cascadeNotSupported, qualifier, prefs, this);
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

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getRenameTableSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		return DialectUtils.getRenameTableSQL(oldTableName, newTableName, qualifier, prefs, this);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getRenameViewSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getRenameViewSQL(String oldViewName, String newViewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return new String[] { DialectUtils.getRenameViewSQL(RENAME_CLAUSE,
			TO_CLAUSE,
			oldViewName,
			newViewName,
			qualifier,
			prefs,
			this) };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getSequenceInformationSQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// SELECT last_number, max_value, min_value, cache_size, increment_by, cycle_flag
		// FROM USER_SEQUENCES WHERE sequence_name = ?;

		StringBuilder result = new StringBuilder();
		result.append("SELECT last_number, max_value, min_value, cache_size, increment_by, ");
		result.append("case cycle_flag when 'N' then 0 else 1 end as cycle_flag ");
		result.append("FROM USER_SEQUENCES ");
		result.append("WHERE sequence_name = upper(?)");
		return result.toString();
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsDropView()
	 */
	public boolean supportsDropView()
	{
		return true;
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
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsSequence()
	 */
	public boolean supportsSequence()
	{
		return true;
	}

	/**
	 * contains
	 * 
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
	public boolean supportsViewDefinition() {
		return true;
	}	
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getViewDefinitionSQL(java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier, net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) {

		StringBuilder result = new StringBuilder();
		result.append("select  'CREATE OR REPLACE VIEW ' || VIEW_NAME ||' AS ', TEXT ");
      result.append("FROM SYS.ALL_VIEWS ");
      result.append("WHERE OWNER = ");
      result.append("'");
      result.append(qualifier.getSchema());
      result.append("'");
      result.append(" AND VIEW_NAME = ");
      result.append("'");
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
		return identifier;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsCorrelatedSubQuery()
	 */
	public boolean supportsCorrelatedSubQuery()
	{
		return true;
	}
	
	
}
