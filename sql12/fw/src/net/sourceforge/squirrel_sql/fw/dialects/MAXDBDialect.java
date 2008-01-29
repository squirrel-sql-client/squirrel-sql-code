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
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.dialect.SAPDBDialect;

public class MAXDBDialect extends SAPDBDialect implements HibernateDialect
{

	public MAXDBDialect()
	{
		super();
		registerColumnType(Types.BIGINT, "fixed(19,0)");
		registerColumnType(Types.BINARY, 8000, "char($l) byte");
		registerColumnType(Types.BINARY, "long varchar byte");
		registerColumnType(Types.BIT, "boolean");
		registerColumnType(Types.BLOB, "long byte");
		registerColumnType(Types.BOOLEAN, "boolean");
		registerColumnType(Types.CLOB, "long varchar");
		registerColumnType(Types.CHAR, 8000, "char($l) ascii");
		registerColumnType(Types.CHAR, "long varchar ascii");
		registerColumnType(Types.DECIMAL, "decimal($p,$s)");
		registerColumnType(Types.DOUBLE, "double precision");
		registerColumnType(Types.DATE, "date");
		registerColumnType(Types.FLOAT, "float($p)");
		registerColumnType(Types.INTEGER, "int");
		registerColumnType(Types.LONGVARBINARY, 8000, "varchar($l) byte");
		registerColumnType(Types.LONGVARBINARY, "long byte");
		registerColumnType(Types.LONGVARCHAR, "long ascii");
		registerColumnType(Types.NUMERIC, "fixed($p,$s)");
		registerColumnType(Types.REAL, "float($p)");
		registerColumnType(Types.SMALLINT, "smallint");
		registerColumnType(Types.TIME, "time");
		registerColumnType(Types.TIMESTAMP, "timestamp");
		registerColumnType(Types.TINYINT, "fixed(3,0)");
		registerColumnType(Types.VARBINARY, "long byte");
		registerColumnType(Types.VARCHAR, 8000, "varchar($l)");
		registerColumnType(Types.VARCHAR, "long ascii");
	}

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

	public boolean supportsSchemasInTableDefinition()
	{
		return true;
	}

	public String getMaxFunction()
	{
		return "max";
	}

	public String getLengthFunction(int dataType)
	{
		return "length";
	}

	public int getMaxPrecision(int dataType)
	{
		return 38;
	}

	public int getMaxScale(int dataType)
	{
		return getMaxPrecision(dataType);
	}

	public int getPrecisionDigits(int columnSize, int dataType)
	{
		return columnSize * 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getColumnLength(int, int)
	 */
	public int getColumnLength(int columnSize, int dataType)
	{
		// driver returns 8 for "long byte", yet it can store 2GB of data.
		if (dataType == Types.LONGVARBINARY)
		{
			return Integer.MAX_VALUE;
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
		return "MaxDB";
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
		String lname = databaseProductName.trim().toLowerCase();
		if (lname.startsWith("sap") || lname.startsWith("maxdb"))
		{
			// We don't yet have the need to discriminate by version.
			return true;
		}
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
	public String getColumnDropSQL(String tableName, String columnName)
	{
		return DialectUtils.getColumnDropSQL(tableName, columnName);
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
		boolean isMaterializedView)
	{
		return DialectUtils.getTableDropSQL(iTableInfo,
			true,
			cascadeConstraints,
			false,
			DialectUtils.CASCADE_CLAUSE,
			false);
	}

	/**
	 * Returns the SQL that forms the command to add a primary key to the specified table composed of the given
	 * column names. ALTER TABLE test ADD constraint test_pk PRIMARY KEY (notnullint)
	 * 
	 * @param pkName
	 *           the name of the constraint
	 * @param columnNames
	 *           the columns that form the key
	 * @return
	 */
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti)
	{
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < columns.length; i++)
		{
			TableColumnInfo info = columns[i];
			result.add(getColumnNullableAlterSQL(info, false));
		}
		result.add(DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, false));
		return result.toArray(new String[result.size()]);
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
	public String getColumnCommentAlterSQL(TableColumnInfo info) throws UnsupportedOperationException
	{
		return DialectUtils.getColumnCommentAlterSQL(info);
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
	 * Returns the SQL used to alter the specified column to not allow null values ALTER TABLE table_name
	 * COLUMN column_name DEFAULT NULL ALTER TABLE table_name COLUMN column_name NOT NULL
	 * 
	 * @param info
	 *           the column to modify
	 * @return the SQL to execute
	 */
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		boolean nullable = info.isNullable().equalsIgnoreCase("YES");
		return new String[] {
			getColumnNullableAlterSQL(info, nullable) 
		};
	}

	/**
	 * Returns the SQL used to alter the specified column to not allow null values ALTER TABLE table_name
	 * COLUMN column_name DEFAULT NULL ALTER TABLE table_name COLUMN column_name NOT NULL
	 * 
	 * @param info
	 *           the column to modify
	 * @param nullable
	 *           whether or not the column should allow nulls
	 * @return the SQL to execute
	 */
	public String getColumnNullableAlterSQL(TableColumnInfo info, boolean nullable)
	{
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(info.getTableName());
		result.append(" COLUMN ");
		result.append(info.getColumnName());
		if (nullable)
		{
			result.append(" DEFAULT NULL");
		} else
		{
			result.append(" NOT NULL");
		}
		return result.toString();
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
	 * Returns the SQL that is used to change the column name. RENAME COLUMN table_name.column_name TO
	 * new_column_name
	 * 
	 * @param from
	 *           the TableColumnInfo as it is
	 * @param to
	 *           the TableColumnInfo as it wants to be
	 * @return the SQL to make the change
	 */
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to)
	{
		return DialectUtils.getColumnRenameSQL(from, to);
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
		String alterClause = DialectUtils.MODIFY_CLAUSE;
		return DialectUtils.getColumnTypeAlterSQL(this, alterClause, "", false, from, to);
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
	 * Returns the SQL command to change the specified column's default value alter table test column mychar
	 * drop default alter table test column mychar add default 'a default'
	 * 
	 * @param info
	 *           the column to modify and it's default value.
	 * @return SQL to make the change
	 */
	public String getColumnDefaultAlterSQL(TableColumnInfo info)
	{
		String alterClause = DialectUtils.COLUMN_CLAUSE;
		String newDefault = info.getDefaultValue();
		String defaultClause = null;
		if (newDefault != null && !"".equals(newDefault))
		{
			defaultClause = DialectUtils.ADD_DEFAULT_CLAUSE;
		} else
		{
			defaultClause = DialectUtils.DROP_DEFAULT_CLAUSE;
		}
		return DialectUtils.getColumnDefaultAlterSQL(this, info, alterClause, false, defaultClause);
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
	public String getDropPrimaryKeySQL(String pkName, String tableName)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false);
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
	public String getDropForeignKeySQL(String fkName, String tableName)
	{
		return DialectUtils.getDropForeignKeySQL(fkName, tableName);
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
		return DialectType.MAXDB;
	}

	public String[] getIndexAccessMethodsTypes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getIndexStorageOptions()
	 */
	public String[] getIndexStorageOptions()
	{
		// TODO Auto-generated method stub		
		return null;
	}

	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddColumnSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();

		boolean addDefaultClause = true;
		boolean supportsNullQualifier = false;
		boolean addNullClause = true;

		String sql =
			DialectUtils.getAddColumSQL(column,
				this,
				addDefaultClause,
				supportsNullQualifier,
				addNullClause,
				qualifier,
				prefs);

		result.add(sql);

		if (column.getRemarks() != null && !"".equals(column.getRemarks()))
		{
			result.add(getColumnCommentAlterSQL(column));
		}

		return result.toArray(new String[result.size()]);

	}

	public String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName, TableColumnInfo[] columns,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getCreateIndexSQL(String indexName, String tableName, String accessMethod, String[] columns,
		boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getCreateTableSQL(String tableName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getInsertIntoSQL(String tableName, List<String> columns, String valuesPart,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getRenameViewSQL(String oldViewName, String newViewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getUpdateSQL(String tableName, String[] setColumns, String[] setValues, String[] fromTables,
		String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean supportsAccessMethods()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsAddForeignKeyConstraint()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsAddUniqueConstraint()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsAlterSequence()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsAutoIncrement()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsCheckOptionsForViews()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsCreateIndex()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsCreateSequence()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsCreateTable()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsCreateView()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsDropConstraint()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsDropIndex()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsDropSequence()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsDropView()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsEmptyTables()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsIndexes()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsInsertInto()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsMultipleRowInserts()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsRenameTable()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsRenameView()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsSequence()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsSequenceInformation()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsTablespace()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsUpdate()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsAddColumn()
	 */
	public boolean supportsAddColumn()
	{
		// TODO verify this is correct
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsViewDefinition()
	 */
	public boolean supportsViewDefinition() {
		// TODO verify this is correct
		return false;
	}	
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getViewDefinitionSQL(java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier, net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs) {
		return null;
	}
	
}
