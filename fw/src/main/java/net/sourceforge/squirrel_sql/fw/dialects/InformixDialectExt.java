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

import org.antlr.stringtemplate.StringTemplate;
import org.hibernate.HibernateException;

/**
 * An extension to the standard Hibernate Informix dialect
 * 
 * @author manningr 
 */
public class InformixDialectExt extends CommonHibernateDialect implements HibernateDialect
{

	private class InformixDialectHelper extends org.hibernate.dialect.InformixDialect {
		public InformixDialectHelper() {
			super();
			registerColumnType(Types.BIGINT, "integer");
			registerColumnType(Types.BINARY, "byte");
			registerColumnType(Types.BIT, "smallint");
			registerColumnType(Types.BLOB, "byte");
			registerColumnType(Types.BOOLEAN, "smallint");
			registerColumnType(Types.CHAR, 32511, "char($l)");
			registerColumnType(Types.CHAR, "char(32511)");
			registerColumnType(Types.CLOB, "text");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.DECIMAL, "decimal($p,$s)");
			registerColumnType(Types.DOUBLE, 15, "float($l)");
			registerColumnType(Types.DOUBLE, "float(15)");
			registerColumnType(Types.FLOAT, 15, "float($l)");
			registerColumnType(Types.FLOAT, "float(15)");
			registerColumnType(Types.INTEGER, "integer");
			registerColumnType(Types.LONGVARBINARY, "byte");
			registerColumnType(Types.LONGVARCHAR, "text");
			registerColumnType(Types.NUMERIC, "numeric($p,$s)");
			registerColumnType(Types.REAL, "real");
			registerColumnType(Types.SMALLINT, "smallint");
			registerColumnType(Types.TIME, "datetime hour to second");
			registerColumnType(Types.TIMESTAMP, "datetime year to fraction(5)");
			registerColumnType(Types.TINYINT, "smallint");
			registerColumnType(Types.VARBINARY, "byte");
			registerColumnType(Types.VARCHAR, 255, "varchar($l)");
			registerColumnType(Types.VARCHAR, "text");			
		}
	}
	
	/** extended hibernate dialect used in this wrapper */
	private InformixDialectHelper _dialect = new InformixDialectHelper();

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.CommonHibernateDialect#getTypeName(int, int, int, int)
	 */
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}	

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#canPasteTo(net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo)
	 */
	public boolean canPasteTo(IDatabaseObjectInfo info)
	{
		if (info.getDatabaseObjectType() == DatabaseObjectType.SCHEMA)
		{
			return true;
		} else
		{
			return false;
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getMaxPrecision(int)
	 */
	public int getMaxPrecision(int dataType)
	{
		if (dataType == Types.DECIMAL || dataType == Types.NUMERIC)
		{
			return 32;
		}
		if (dataType == Types.DOUBLE)
		{
			return 16;
		}
		return 32;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getMaxScale(int)
	 */
	public int getMaxScale(int dataType)
	{
		return getMaxPrecision(dataType);
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
		return "Informix";
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
		if (databaseProductName.toLowerCase().contains("informix"))
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
	public String getColumnDropSQL(String tableName, String columnName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		prefs.setQuoteColumnNames(false);
		
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
		return DialectUtils.getTableDropSQL(iTableInfo,
			true,
			cascadeConstraints,
			false,
			DialectUtils.CASCADE_CLAUSE,
			false, qualifier, prefs, this);
	}

	/**
	 * Returns the SQL that forms the command to add a primary key to the specified table composed of the given
	 * column names. CREATE UNIQUE INDEX test_index ON test_table (test_field); ALTER TABLE test_table ADD
	 * CONSTRAINT PRIMARY KEY (test_field) CONSTRAINT test_constraint; alter table table_name add constraint
	 * primary key (column_names) constraint pkName
	 * 
	 * @param pkName
	 *           the name of the constraint
	 * @param columnNames
	 *           the columns that form the key
	 * @return
	 */
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO: should also make sure that each of the columns is made "NOT NULL"

		prefs.setQuoteColumnNames(false);
		prefs.setQuoteConstraintNames(false);
		
		return new String[] { DialectUtils.getAddIndexSQL(pkName, true, columns, qualifier, prefs, this),
				DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, true, qualifier, prefs, this) };
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
	public String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
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
	 * Returns the SQL used to alter the specified column to not allow null values
	 * 
	 * @param info
	 *           the column to modify
	 * @return the SQL to execute
	 */
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.MODIFY_CLAUSE;
		return new String[] { DialectUtils.getColumnNullableAlterSQL(info, this, alterClause, true, qualifier, prefs) };
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
		prefs.setQuoteColumnNames(false);
		return DialectUtils.getColumnRenameSQL(from, to, qualifier, prefs, this);
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
	public List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		String alterClause = DialectUtils.MODIFY_CLAUSE;
		String setClause = null;
		prefs.setQuoteColumnNames(false);
		return DialectUtils.getColumnTypeAlterSQL(this, alterClause, setClause, false, from, to, qualifier, prefs);
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
	 * Returns the SQL command to change the specified column's default value
	 * 
	 * @param info
	 *           the column to modify and it's default value.
	 * @return SQL to make the change
	 */
	public String getColumnDefaultAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.MODIFY_CLAUSE;
		String defaultClause = DialectUtils.DEFAULT_CLAUSE;
		prefs.setQuoteColumnNames(false);
		return DialectUtils.getColumnDefaultAlterSQL(this, info, alterClause, true, defaultClause, qualifier, prefs);
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
		prefs.setQuoteConstraintNames(false);
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, true, false, qualifier, prefs, this);
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
		return DialectUtils.getCreateTableSQL(tables, md, this, prefs, isJdbcOdbc);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDialectType()
	 */
	public DialectType getDialectType()
	{
		return DialectType.INFORMIX;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getIndexAccessMethodsTypes()
	 */
	public String[] getIndexAccessMethodsTypes()
	{
		return new String[] { "NORMAL", "CLUSTERED" };

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
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getAddAutoIncrementSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		/* This is another way to do this. Modifying the column type to serial is much easier. Thanks to 
		 * Doug Lawry for setting me straight!!
		 */
		// CREATE SEQUENCE testAutoIncrementTable_myid_seq
		// INCREMENT BY 1 MINVALUE 1 NOMAXVALUE START WITH 1 NOCYCLE;
		//
		// CREATE FUNCTION nextAutoVal () RETURNING INTEGER;
		// RETURN testAutoIncrementTable_myid_seq.NEXTVAL;
		// END FUNCTION;
		//
		// I finally did get a trigger to work, but I had to use a function:
		//
		// CREATE TRIGGER myid_trigger
		// INSERT ON testAutoIncrementTable
		// FOR EACH ROW (execute function nextAutoVal() into myid);
		//

		// Unfortunately we cannot simply call getColumnTypeAlterSQL since "serial" type is 4 (which is the same
		// as java.sql.Types.Integer, so we would get back an integer column, not a serial column).  So we piece
		// it together ourselves:
		//
		// ALTER TABLE dbcopydest:"informix".serialtest MODIFY myid integer
		StringBuilder result = new StringBuilder();
		result.append(DialectUtils.ALTER_TABLE_CLAUSE);
		result.append(" ");
		result.append(DialectUtils.shapeQualifiableIdentifier(column.getTableName(), qualifier, prefs, this));
		result.append(" ");
		result.append(DialectUtils.MODIFY_CLAUSE);
		result.append(" ");
		// must not qualify column name 
		result.append(column.getColumnName());
		result.append(" SERIAL");
		return new String[] { result.toString() };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddColumnSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		boolean addDefaultClause = true;
		boolean supportsNullQualifier = false;
		boolean addNullClause = true;

		// Informix doesn't allow quoting column names.
		prefs.setQuoteColumnNames(false);
		
		String sql =
			DialectUtils.getAddColumSQL(column,
				this,
				addDefaultClause,
				supportsNullQualifier,
				addNullClause,
				qualifier,
				prefs);

		return new String[] { sql };
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
		
		// This method no longer creates an index for the child column.  Informix does this automatically
		// when the FK constraint is created.
		
		prefs.setQuoteColumnNames(false);
		prefs.setQuoteConstraintNames(false);
		
		/**
		 * ALTER TABLE fkTestChildTable ADD CONSTRAINT FOREIGN KEY (fkchildid) REFERENCES fkTestParentTable
		 * (parentid) constraint fk_const_name
		 */
		StringBuilder result = new StringBuilder();
		result.append(DialectUtils.ALTER_TABLE_CLAUSE);
		result.append(" ");
		result.append(DialectUtils.shapeQualifiableIdentifier(localTableName, qualifier, prefs, this));
		result.append(" ADD CONSTRAINT FOREIGN KEY ");

		result.append(" (");

		ArrayList<String> localColumns = new ArrayList<String>();
		StringBuilder refColumns = new StringBuilder();
		for (String[] columns : localRefColumns)
		{
			// must not quote column names
			result.append(columns[0]);
			result.append(", ");
			localColumns.add(columns[0]);
			refColumns.append(columns[1]);
			refColumns.append(", ");
		}
		result.setLength(result.length() - 2); // deletes the last ", "
		refColumns.setLength(refColumns.length() - 2); // deletes the last ", "

		result.append(")\n REFERENCES ");
		result.append(DialectUtils.shapeQualifiableIdentifier(refTableName, qualifier, prefs, this));
		result.append(" (");
		result.append(refColumns.toString());
		result.append(")\n");

		result.append(" CONSTRAINT ");
		result.append(constraintName);
		

		return new String[] { result.toString() };
	}

	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		/*
		 * ALTER TABLE <tableName> ADD CONSTRAINT UNIQUE (<columnList>) CONSTRAINT <constraintName>
		 */

		StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.ALTER_TABLE_CLAUSE);
		sql.append(" ");
		sql.append(DialectUtils.shapeQualifiableIdentifier(tableName, qualifier, prefs, this));
		sql.append("\n");

		sql.append(" ");
		sql.append(DialectUtils.ADD_CONSTRAINT_CLAUSE);

		sql.append(" ");
		sql.append(DialectUtils.UNIQUE_CLAUSE);
		sql.append(" (");
		for (TableColumnInfo column : columns)
		{
			sql.append(column.getColumnName());
			sql.append(", ");
		}
		sql.delete(sql.length() - 2, sql.length()); // deletes the last ", "
		sql.append(")");

		sql.append(" CONSTRAINT ");
		sql.append(constraintName);

		return new String[] { sql.toString() };

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
		String cycleClause = DialectUtils.CYCLE_CLAUSE;
		if (!cycle)
		{
			cycleClause = DialectUtils.NOCYCLE_CLAUSE;
		}

		return new String[] { DialectUtils.getAlterSequenceSQL(sequenceName,
			increment,
			minimum,
			maximum,
			restart,
			cache,
			cycleClause,
			qualifier,
			prefs,
			this) };
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
		String access = (accessMethod == null || accessMethod.equalsIgnoreCase("NORMAL")) ? null : "CLUSTER";

		StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.CREATE_CLAUSE + " ");
		if (unique)
		{
			sql.append(DialectUtils.UNIQUE_CLAUSE + " ");
		} else
		{
			if (access != null)
			{
				sql.append(access);
			}
		}
		sql.append(" ");
		sql.append(DialectUtils.INDEX_CLAUSE);
		sql.append(" ");
		sql.append(indexName);
		sql.append(" ON ").append(DialectUtils.shapeQualifiableIdentifier(tableName, qualifier, prefs, this));

		sql.append("(");
		for (String column : columns)
		{
			sql.append(column);
			sql.append(", ");
		}
		sql.delete(sql.length() - 2, sql.length()); // deletes the last ", "
		sql.append(")");

		return sql.toString();
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
		String cycleClause = DialectUtils.CYCLE_CLAUSE;
		if (!cycle)
		{
			cycleClause = DialectUtils.NOCYCLE_CLAUSE;
		}

		String minimumClause = DialectUtils.MINVALUE_CLAUSE;
		if (minimum == null || "".equals(DialectUtils.NOMINVALUE_CLAUSE))
		{
			minimumClause = DialectUtils.NOMINVALUE_CLAUSE;
		}

		String maximumClause = DialectUtils.MAXVALUE_CLAUSE;
		if (maximum == null || "".equals(maximum))
		{
			maximumClause = DialectUtils.NOMAXVALUE_CLAUSE;
		}

		return DialectUtils.getCreateSequenceSQL(sequenceName,
			increment,
			minimumClause,
			minimum,
			maximumClause,
			maximum,
			start,
			cache,
			cycleClause,
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
		prefs.setQuoteConstraintNames(false);
		/*
		 * alter table test drop constraint u_test
		 */
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
		return DialectUtils.getDropViewSQL(viewName, cascade, qualifier, prefs, this);
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
		StringBuilder result = new StringBuilder();
		result.append("RENAME TABLE ");
		result.append(DialectUtils.shapeQualifiableIdentifier(oldTableName, qualifier, prefs, this));
		result.append(" TO ");
		// must not qualify the new table name
		result.append(newTableName);
		return result.toString();
	}

	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getSequenceInformationSQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		/*
		 * SELECT 'unknown' as last_value, T1.max_val AS max_value, T1.min_val AS min_value, T1.cache AS
		 * cache_size, T1.inc_val AS increment_by, case T1.cycle as is_cycled FROM informix.syssequences AS T1,
		 * informix.systables AS T2 WHERE T2.tabid = T1.tabid and T2.owner = '<schema>' and T2.tabname = '<sequenceName>'
		 */
		StringBuilder result = new StringBuilder();
		result.append("SELECT  ");
		result.append(sequenceName);
		result.append(".CURRVAL");
		result.append(" as last_value, ");
		result.append("T1.max_val   AS max_value, ");
		result.append("T1.min_val   AS min_value, ");
		result.append("T1.cache     AS cache_size, ");
		result.append("T1.inc_val   AS increment_by, ");
		result.append("T1.cycle 	 AS is_cycled ");
		result.append("FROM    informix.syssequences AS T1, informix.systables AS T2 ");
		result.append("WHERE   T2.tabid     = T1.tabid ");
		result.append("and T2.owner = ");
		result.append("'");
		result.append(qualifier.getSchema());
		result.append("'");
		result.append("and T2.tabname =");
		result.append("'");
		result.append(sequenceName);
		result.append("'");

		return result.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getUpdateSQL(java.lang.String,
	 *      java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String[],
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getUpdateSQL(String tableName, String[] setColumns, String[] setValues, String[] fromTables,
		String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// UPDATE t SET a = (SELECT a FROM t2 WHERE t.b = t2.b);
//		String templateStr = 
//			"UPDATE $destTableName$ SET $columnName$ = " +
//			"(SELECT $columnName$ FROM $sourceTableName$ " +
//			"WHERE $sourceTableName$.$whereColumnName$ = $destTableName$.$whereColumnValue$";
//		StringTemplate st = new StringTemplate(templateStr);

		String templateStr = "";
		
		if (fromTables != null) {
			templateStr = ST_UPDATE_CORRELATED_QUERY_STYLE_TWO;
		} else {
			templateStr = ST_UPDATE_STYLE_ONE;
		}
			
		StringTemplate st = new StringTemplate(templateStr);

		return DialectUtils.getUpdateSQL(st, tableName,
			setColumns,
			setValues,
			fromTables,
			whereColumns,
			whereValues,
			qualifier,
			prefs,
			this);
	}

	public boolean supportsAccessMethods()
	{
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsMultipleRowInserts()
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsViewDefinition()
	 */
	public boolean supportsViewDefinition() {
		return true;
	}
	
	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getViewDefinitionSQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		/*
		 * SELECT viewtext FROM informix.systables AS T1, informix.sysviews AS T2 WHERE tabname = 'a_view' AND
		 * T2.tabid = T1.tabid
		 */
		StringBuilder result = new StringBuilder();
		result.append("SELECT viewtext ");

		result.append("FROM informix.systables AS T1, informix.sysviews AS T2 ");
		result.append("WHERE tabname = '");
		result.append(viewName);
		result.append("' ");
		result.append("AND T2.tabid = T1.tabid");
		return result.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getQualifiedIdentifier(java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier, net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getQualifiedIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		// TODO: should I be adding quotes if user wants identifiers quoted??
		StringBuilder result = new StringBuilder();
		if (prefs.isQualifyTableNames()) {
			String catalog = qualifier.getCatalog();
			String schema = qualifier.getSchema();
		
			if (catalog != null && schema != null) {
				result.append(catalog);
				result.append(":");
				result.append(schema);
				result.append(".");
				result.append(identifier);
			}	
		} else {
			result.append(identifier);
		}
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
		return 8;
	}
	
}
