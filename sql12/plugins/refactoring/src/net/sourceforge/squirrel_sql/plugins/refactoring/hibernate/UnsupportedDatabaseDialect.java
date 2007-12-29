/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.refactoring.hibernate;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.dialects.CreateScriptPreferences;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.HibernateException;

public class UnsupportedDatabaseDialect implements IHibernateDialectExtension
{

	public String[] getAccessMethodsTypes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getAddAutoIncrementSQL(TableColumnInfo column, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, boolean deferrable, boolean initiallyDeferred, boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getAddUniqueConstraintSQL(String tableName, String constraintName, String[] columns,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum,
		String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getCreateIndexSQL(String indexName, String tableName, String[] columns, boolean unique,
		String tablespace, String constraints, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
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

	public String getRenameViewSQL(String oldViewName, String newViewName, DatabaseObjectQualifier qualifier,
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

	public boolean canPasteTo(IDatabaseObjectInfo info)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public String getAddColumnString()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] colInfos, ITableInfo ti)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getColumnAddSQL(TableColumnInfo info) throws HibernateException,
		UnsupportedOperationException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getColumnCommentAlterSQL(TableColumnInfo info) throws UnsupportedOperationException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getColumnDefaultAlterSQL(TableColumnInfo info)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getColumnDropSQL(String tableName, String columnName) throws UnsupportedOperationException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int getColumnLength(int columnSize, int dataType)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getColumnNullableAlterSQL(TableColumnInfo info)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to)
		throws UnsupportedOperationException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getCreateTableSQL(List<ITableInfo> tables, ISQLDatabaseMetaData md,
		CreateScriptPreferences prefs, boolean isJdbcOdbc) throws SQLException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public DialectType getDialectType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getDisplayName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getDropForeignKeySQL(String fkName, String tableName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getDropPrimaryKeySQL(String pkName, String tableName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getLengthFunction(int dataType)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getMaxFunction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int getMaxPrecision(int dataType)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMaxScale(int dataType)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public String getNullColumnString()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int getPrecisionDigits(int columnSize, int dataType)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public List<String> getTableDropSQL(ITableInfo tableInfo, boolean cascadeConstraints,
		boolean isMaterializedView)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean supportsAlterColumnDefault()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsAlterColumnNull()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsAlterColumnType()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsColumnComment()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsDropColumn()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsRenameColumn()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean supportsSchemasInTableDefinition()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
