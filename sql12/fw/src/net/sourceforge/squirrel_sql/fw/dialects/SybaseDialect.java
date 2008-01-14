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
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

/**
 * An extension to the standard Hibernate Sybase dialect
 */
public class SybaseDialect extends org.hibernate.dialect.SybaseDialect implements HibernateDialect
{

	public SybaseDialect()
	{
		super();
		registerColumnType(Types.BIGINT, "numeric($p)");
		registerColumnType(Types.BINARY, "image");
		registerColumnType(Types.BIT, "tinyint");
		registerColumnType(Types.BLOB, "image");
		registerColumnType(Types.BOOLEAN, "tinyint");
		registerColumnType(Types.CHAR, 255, "char($l)");
		registerColumnType(Types.CHAR, "text");
		registerColumnType(Types.CLOB, "text");
		registerColumnType(Types.DATE, "datetime");
		registerColumnType(Types.DECIMAL, "decimal($p,2)");
		registerColumnType(Types.DOUBLE, "float($p)");
		registerColumnType(Types.FLOAT, "float($p)");
		registerColumnType(Types.INTEGER, "int");
		registerColumnType(Types.LONGVARBINARY, "image");
		registerColumnType(Types.LONGVARCHAR, "text");
		registerColumnType(Types.NUMERIC, "numeric($p)");
		registerColumnType(Types.REAL, "real");
		registerColumnType(Types.SMALLINT, "smallint");
		registerColumnType(Types.TIME, "time");
		registerColumnType(Types.TIMESTAMP, "datetime");
		registerColumnType(Types.TINYINT, "tinyint");
		registerColumnType(Types.VARBINARY, "image");
		registerColumnType(Types.VARCHAR, 255, "varchar($l)");
		registerColumnType(Types.VARCHAR, "text");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#canPasteTo(net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType)
	 */
	public boolean canPasteTo(IDatabaseObjectInfo info)
	{
		boolean result = true;
		DatabaseObjectType type = info.getDatabaseObjectType();
		if (type.getName().equalsIgnoreCase("database") || type.getName().equalsIgnoreCase("catalog"))
		{
			result = false;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#supportsSchemasInTableDefinition()
	 */
	public boolean supportsSchemasInTableDefinition()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getLengthFunction()
	 */
	public String getLengthFunction(int dataType)
	{
		return "datalength";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getMaxFunction()
	 */
	public String getMaxFunction()
	{
		return "max";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getMaxPrecision(int)
	 */
	public int getMaxPrecision(int dataType)
	{
		if (dataType == Types.DOUBLE || dataType == Types.FLOAT)
		{
			return 48;
		} else
		{
			return 38;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getMaxScale(int)
	 */
	public int getMaxScale(int dataType)
	{
		return getMaxPrecision(dataType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getPrecisionDigits(int, int)
	 */
	public int getPrecisionDigits(int columnSize, int dataType)
	{
		return columnSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getColumnLength(int, int)
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
		return "Sybase";
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
		if (lname.startsWith("sybase") || lname.startsWith("adaptive") || lname.startsWith("sql server"))
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

		// SQL-Server doesn't support a cascade clause.
		List<String> dropTableSQL =
			DialectUtils.getTableDropSQL(iTableInfo,
				false,
				cascadeConstraints,
				false,
				DialectUtils.CASCADE_CLAUSE,
				false);
		if (cascadeConstraints)
		{
			ArrayList<String> result = new ArrayList<String>();
			ForeignKeyInfo[] fks = iTableInfo.getExportedKeys();
			if (fks != null && fks.length > 0)
			{
				for (int i = 0; i < fks.length; i++)
				{
					ForeignKeyInfo info = fks[i];
					String fkName = info.getForeignKeyName();
					String fkTable = info.getForeignKeyTableName();
					StringBuilder tmp = new StringBuilder();
					tmp.append("ALTER TABLE ");
					tmp.append(fkTable);
					tmp.append(" DROP CONSTRAINT ");
					tmp.append(fkName);
					result.add(tmp.toString());
				}
			}
			result.addAll(dropTableSQL);
			return result;
		} else
		{
			return dropTableSQL;
		}
	}

	/**
	 * Returns the SQL that forms the command to add a primary key to the specified table composed of the given
	 * column names. alter table test add primary key (mychar)
	 * 
	 * @param pkName
	 *           the name of the constraint
	 * @param columnNames
	 *           the columns that form the key
	 * @return
	 */
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti)
	{
		return new String[] { DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, false) };
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
		int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
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
	 * Returns the SQL used to alter the specified column to not allow null values alter table table_name
	 * modify column_name not null
	 * 
	 * @param info
	 *           the column to modify
	 * @return the SQL to execute
	 */
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.MODIFY_CLAUSE;
		return new String[] {
			DialectUtils.getColumnNullableAlterSQL(info, this, alterClause, false)
		};
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
	 * Returns the SQL that is used to change the column name. exec sp_rename 'test.renameCol', newNameCol
	 * 
	 * @param from
	 *           the TableColumnInfo as it is
	 * @param to
	 *           the TableColumnInfo as it wants to be
	 * @return the SQL to make the change
	 */
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to)
	{
		StringBuffer result = new StringBuffer();
		result.append("exec sp_rename ");
		result.append("'");
		result.append(from.getTableName());
		result.append(".");
		result.append(from.getColumnName());
		result.append("'");
		result.append(", ");
		result.append(to.getColumnName());
		return result.toString();
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
	 * Returns the SQL that is used to change the column type. alter table table_name modify column_name
	 * datatype
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
		String alterClause = DialectUtils.MODIFY_CLAUSE;
		String setClause = "";
		return DialectUtils.getColumnTypeAlterSQL(this, alterClause, setClause, false, from, to);
	}

	/**
	 * Returns a boolean value indicating whether or not this database dialect supports changing a column's
	 * default value.
	 * 
	 * @return true if the database supports modifying column defaults; false otherwise
	 */
	public boolean supportsAlterColumnDefault()
	{
		return false;
	}

	/**
	 * Returns the SQL command to change the specified column's default value
	 * 
	 * @param info
	 *           the column to modify and it's default value.
	 * @return SQL to make the change
	 */
	public String getColumnDefaultAlterSQL(TableColumnInfo info)
	{
		int featureId = DialectUtils.COLUMN_DEFAULT_ALTER_TYPE;
		String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
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
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, true, false);
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
		return DialectType.SYBASEASE;
	}

	public String[] getAccessMethodsTypes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();

		boolean addDefaultClause = true;
		boolean supportsNullQualifier = true;
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

	public String getDropIndexSQL(String indexName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
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

}
