/*
 * Copyright 2004, 2005, 2006 H2 Group.
 */
package net.sourceforge.squirrel_sql.fw.dialects;

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
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.antlr.stringtemplate.StringTemplate;
import org.hibernate.Hibernate;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.ViolatedConstraintNameExtracter;

public class H2Dialect extends Dialect implements HibernateDialect
{

	static final String DEFAULT_BATCH_SIZE = "15";

	public H2Dialect()
	{
		super();
		registerColumnType(Types.ARRAY, "array");
		registerColumnType(Types.BIGINT, "bigint");
		registerColumnType(Types.BINARY, "binary");
		registerColumnType(Types.BIT, "boolean");
		registerColumnType(Types.BOOLEAN, "boolean");
		registerColumnType(Types.BLOB, "blob");
		registerColumnType(Types.CHAR, "varchar($l)");
		registerColumnType(Types.CLOB, "clob");
		registerColumnType(Types.DATE, "date");
		registerColumnType(Types.DECIMAL, "decimal($p,$s)");
		registerColumnType(Types.DOUBLE, "double");
		registerColumnType(Types.FLOAT, "float");
		registerColumnType(Types.INTEGER, "integer");
		registerColumnType(Types.LONGVARBINARY, "longvarbinary");
		registerColumnType(Types.LONGVARCHAR, "longvarchar");
		registerColumnType(Types.NUMERIC, "numeric");
		registerColumnType(Types.REAL, "real");
		registerColumnType(Types.SMALLINT, "smallint");
		registerColumnType(Types.TIME, "time");
		registerColumnType(Types.TIMESTAMP, "timestamp");
		registerColumnType(Types.TINYINT, "tinyint");
		registerColumnType(Types.VARBINARY, "binary($l)");
		registerColumnType(Types.VARCHAR, "varchar($l)");

		// select topic, syntax from information_schema.help
		// where section like 'Function%' order by section, topic

		// registerFunction("abs", new StandardSQLFunction("abs"));
		registerFunction("acos", new StandardSQLFunction("acos", Hibernate.DOUBLE));
		registerFunction("asin", new StandardSQLFunction("asin", Hibernate.DOUBLE));
		registerFunction("atan", new StandardSQLFunction("atan", Hibernate.DOUBLE));
		registerFunction("atan2", new StandardSQLFunction("atan2", Hibernate.DOUBLE));
		registerFunction("bitand", new StandardSQLFunction("bitand", Hibernate.INTEGER));
		registerFunction("bitor", new StandardSQLFunction("bitor", Hibernate.INTEGER));
		registerFunction("bitxor", new StandardSQLFunction("bitxor", Hibernate.INTEGER));
		registerFunction("ceiling", new StandardSQLFunction("ceiling", Hibernate.DOUBLE));
		registerFunction("cos", new StandardSQLFunction("cos", Hibernate.DOUBLE));
		registerFunction("cot", new StandardSQLFunction("cot", Hibernate.DOUBLE));
		registerFunction("degrees", new StandardSQLFunction("degrees", Hibernate.DOUBLE));
		registerFunction("exp", new StandardSQLFunction("exp", Hibernate.DOUBLE));
		registerFunction("floor", new StandardSQLFunction("floor", Hibernate.DOUBLE));
		registerFunction("log", new StandardSQLFunction("log", Hibernate.DOUBLE));
		registerFunction("log10", new StandardSQLFunction("log10", Hibernate.DOUBLE));
		// registerFunction("mod", new StandardSQLFunction("mod", Hibernate.INTEGER));
		registerFunction("pi", new NoArgSQLFunction("pi", Hibernate.DOUBLE));
		registerFunction("power", new StandardSQLFunction("power", Hibernate.DOUBLE));
		registerFunction("radians", new StandardSQLFunction("radians", Hibernate.DOUBLE));
		registerFunction("rand", new NoArgSQLFunction("rand", Hibernate.DOUBLE));
		registerFunction("round", new StandardSQLFunction("round", Hibernate.DOUBLE));
		registerFunction("roundmagic", new StandardSQLFunction("roundmagic", Hibernate.DOUBLE));
		registerFunction("sign", new StandardSQLFunction("sign", Hibernate.INTEGER));
		registerFunction("sin", new StandardSQLFunction("sin", Hibernate.DOUBLE));
		// registerFunction("sqrt", new StandardSQLFunction("sqrt", Hibernate.DOUBLE));
		registerFunction("tan", new StandardSQLFunction("tan", Hibernate.DOUBLE));
		registerFunction("truncate", new StandardSQLFunction("truncate", Hibernate.DOUBLE));

		registerFunction("compress", new StandardSQLFunction("compress", Hibernate.BINARY));
		registerFunction("expand", new StandardSQLFunction("compress", Hibernate.BINARY));
		registerFunction("decrypt", new StandardSQLFunction("decrypt", Hibernate.BINARY));
		registerFunction("encrypt", new StandardSQLFunction("encrypt", Hibernate.BINARY));
		registerFunction("hash", new StandardSQLFunction("hash", Hibernate.BINARY));

		registerFunction("ascii", new StandardSQLFunction("ascii", Hibernate.INTEGER));
		// registerFunction("bit_length", new StandardSQLFunction("bit_length", Hibernate.INTEGER));
		registerFunction("char", new StandardSQLFunction("char", Hibernate.CHARACTER));
		registerFunction("concat", new VarArgsSQLFunction(Hibernate.STRING, "(", "||", ")"));
		registerFunction("difference", new StandardSQLFunction("difference", Hibernate.INTEGER));
		registerFunction("hextoraw", new StandardSQLFunction("hextoraw", Hibernate.STRING));
		registerFunction("lower", new StandardSQLFunction("lower", Hibernate.STRING));
		registerFunction("insert", new StandardSQLFunction("lower", Hibernate.STRING));
		registerFunction("left", new StandardSQLFunction("left", Hibernate.STRING));
		// registerFunction("length", new StandardSQLFunction("length", Hibernate.INTEGER));
		// registerFunction("locate", new StandardSQLFunction("locate", Hibernate.INTEGER));
		// registerFunction("lower", new StandardSQLFunction("lower", Hibernate.STRING));
		registerFunction("lcase", new StandardSQLFunction("lcase", Hibernate.STRING));
		registerFunction("ltrim", new StandardSQLFunction("ltrim", Hibernate.STRING));
		registerFunction("octet_length", new StandardSQLFunction("octet_length", Hibernate.INTEGER));
		registerFunction("position", new StandardSQLFunction("position", Hibernate.INTEGER));
		registerFunction("rawtohex", new StandardSQLFunction("rawtohex", Hibernate.STRING));
		registerFunction("repeat", new StandardSQLFunction("repeat", Hibernate.STRING));
		registerFunction("replace", new StandardSQLFunction("replace", Hibernate.STRING));
		registerFunction("right", new StandardSQLFunction("right", Hibernate.STRING));
		registerFunction("rtrim", new StandardSQLFunction("rtrim", Hibernate.STRING));
		registerFunction("soundex", new StandardSQLFunction("soundex", Hibernate.STRING));
		registerFunction("space", new StandardSQLFunction("space", Hibernate.STRING));
		registerFunction("stringencode", new StandardSQLFunction("stringencode", Hibernate.STRING));
		registerFunction("stringdecode", new StandardSQLFunction("stringdecode", Hibernate.STRING));
		// registerFunction("substring", new StandardSQLFunction("substring", Hibernate.STRING));
		// registerFunction("upper", new StandardSQLFunction("upper", Hibernate.STRING));
		registerFunction("ucase", new StandardSQLFunction("ucase", Hibernate.STRING));

		registerFunction("stringtoutf8", new StandardSQLFunction("stringtoutf8", Hibernate.BINARY));
		registerFunction("utf8tostring", new StandardSQLFunction("utf8tostring", Hibernate.STRING));

		registerFunction("current_date", new NoArgSQLFunction("current_date", Hibernate.DATE));
		registerFunction("current_time", new NoArgSQLFunction("current_time", Hibernate.TIME));
		registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", Hibernate.TIMESTAMP));
		registerFunction("datediff", new NoArgSQLFunction("datediff", Hibernate.INTEGER));
		registerFunction("dayname", new StandardSQLFunction("dayname", Hibernate.STRING));
		registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth", Hibernate.INTEGER));
		registerFunction("dayofweek", new StandardSQLFunction("dayofweek", Hibernate.INTEGER));
		registerFunction("dayofyear", new StandardSQLFunction("dayofyear", Hibernate.INTEGER));
		// registerFunction("hour", new StandardSQLFunction("hour", Hibernate.INTEGER));
		// registerFunction("minute", new StandardSQLFunction("minute", Hibernate.INTEGER));
		// registerFunction("month", new StandardSQLFunction("month", Hibernate.INTEGER));
		registerFunction("monthname", new StandardSQLFunction("monthname", Hibernate.STRING));
		registerFunction("quater", new StandardSQLFunction("quater", Hibernate.INTEGER));
		// registerFunction("second", new StandardSQLFunction("second", Hibernate.INTEGER));
		registerFunction("week", new StandardSQLFunction("week", Hibernate.INTEGER));
		// registerFunction("year", new StandardSQLFunction("year", Hibernate.INTEGER));

		registerFunction("curdate", new NoArgSQLFunction("curdate", Hibernate.DATE));
		registerFunction("curtime", new NoArgSQLFunction("curtime", Hibernate.TIME));
		registerFunction("curtimestamp", new NoArgSQLFunction("curtimestamp", Hibernate.TIME));
		registerFunction("now", new NoArgSQLFunction("now", Hibernate.TIMESTAMP));

		registerFunction("database", new NoArgSQLFunction("database", Hibernate.STRING));
		registerFunction("user", new NoArgSQLFunction("user", Hibernate.STRING));

		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);

	}

	public String getAddColumnString()
	{
		return "add column";
	}

	public boolean supportsIdentityColumns()
	{
		return true;
	}

	public String getIdentityColumnString()
	{
		return "generated by default as identity"; // not null is implicit
	}

	public String getIdentitySelectString()
	{
		return "call identity()";
	}

	public String getIdentityInsertString()
	{
		return "null";
	}

	public String getForUpdateString()
	{
		return " for update";
	}

	/**
	 * @see org.hibernate.dialect.Dialect#supportsUnique()
	 */
	public boolean supportsUnique()
	{
		return true;
	}

	/**
	 * @see org.hibernate.dialect.Dialect#supportsLimit()
	 */
	public boolean supportsLimit()
	{
		return true;
	}

	/**
	 * @see org.hibernate.dialect.Dialect#getLimitString(java.lang.String, boolean)
	 */
	public String getLimitString(String sql, boolean hasOffset)
	{
		return new StringBuffer(sql.length() + 20).append(sql).append(hasOffset ? " limit ? offset ?"
			: " limit ?").toString();
	}

	/**
	 * @see org.hibernate.dialect.Dialect#bindLimitParametersInReverseOrder()
	 */
	public boolean bindLimitParametersInReverseOrder()
	{
		return true;
	}

	/**
	 * @see org.hibernate.dialect.Dialect#bindLimitParametersFirst()
	 */
	public boolean bindLimitParametersFirst()
	{
		return false;
	}

	/**
	 * @see org.hibernate.dialect.Dialect#supportsIfExistsAfterTableName()
	 */
	public boolean supportsIfExistsAfterTableName()
	{
		return true;
	}

	/**
	 * @see org.hibernate.dialect.Dialect#getCreateSequenceStrings(java.lang.String)
	 */
	public String[] getCreateSequenceStrings(String sequenceName)
	{
		return new String[] { "create sequence " + sequenceName };
	}

	/**
	 * @see org.hibernate.dialect.Dialect#getDropSequenceStrings(java.lang.String)
	 */
	public String[] getDropSequenceStrings(String sequenceName)
	{
		return new String[] { "drop sequence " + sequenceName };
	}

	/**
	 * @see org.hibernate.dialect.Dialect#getSelectSequenceNextValString(java.lang.String)
	 */
	public String getSelectSequenceNextValString(String sequenceName)
	{
		return "next value for " + sequenceName;
	}

	/**
	 * @see org.hibernate.dialect.Dialect#getSequenceNextValString(java.lang.String)
	 */
	public String getSequenceNextValString(String sequenceName)
	{
		return "call next value for " + sequenceName;
	}

	/**
	 * @see org.hibernate.dialect.Dialect#getQuerySequencesString()
	 */
	public String getQuerySequencesString()
	{
		return "select name from information_schema.sequences";
	}

	/**
	 * @see org.hibernate.dialect.Dialect#supportsSequences()
	 */
	public boolean supportsSequences()
	{
		return true;
	}

	/**
	 * @see org.hibernate.dialect.Dialect#getViolatedConstraintNameExtracter()
	 */
	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter()
	{
		return EXTRACTER;
	}

	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter()
	{

		/**
		 * Extract the name of the violated constraint from the given SQLException.
		 * 
		 * @param sqle
		 *           The exception that was the result of the constraint violation.
		 * @return The extracted constraint name.
		 */
		public String extractConstraintName(SQLException sqle)
		{
			String constraintName = null;
			// 23000: Check constraint violation: {0}
			// 23001: Unique index or primary key violation: {0}
			if (sqle.getSQLState().startsWith("23"))
			{
				String message = sqle.getMessage();
				int idx = message.indexOf("violation: ");
				if (idx > 0)
				{
					constraintName = message.substring(idx + "violation: ".length());
				}
			}
			return constraintName;
		}

	};

	/**
	 * @see org.hibernate.dialect.Dialect#supportsTemporaryTables()
	 */
	public boolean supportsTemporaryTables()
	{
		return true;
	}

	/**
	 * @see org.hibernate.dialect.Dialect#getCreateTemporaryTableString()
	 */
	public String getCreateTemporaryTableString()
	{
		return "create temporary table if not exists";
	}

	/**
	 * @see org.hibernate.dialect.Dialect#supportsCurrentTimestampSelection()
	 */
	public boolean supportsCurrentTimestampSelection()
	{
		return true;
	}

	/**
	 * @see org.hibernate.dialect.Dialect#isCurrentTimestampSelectStringCallable()
	 */
	public boolean isCurrentTimestampSelectStringCallable()
	{
		return false;
	}

	/**
	 * @see org.hibernate.dialect.Dialect#getCurrentTimestampSelectString()
	 */
	public String getCurrentTimestampSelectString()
	{
		return "call current_timestamp()";
	}

	/**
	 * @see org.hibernate.dialect.Dialect#supportsUnionAll()
	 */
	public boolean supportsUnionAll()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#canPasteTo(net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo)
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
		return Integer.MAX_VALUE;
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#supportsSchemasInTableDefinition()
	 */
	public boolean supportsSchemasInTableDefinition()
	{
		return true;
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
		return "H2";
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
		if (databaseProductName.trim().startsWith("H2"))
		{
			// We don't yet have the need to discriminate by version.
			return true;
		}
		return false;
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
		return DialectUtils.getColumnDropSQL(tableName, columnName, "DROP COLUMN", false, null);
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
	 * column names. alter table test alter column mychar char(10) not null alter table test add primary key
	 * (mychar) alter table pktest add constraint pk_pktest primary key (pkcol)
	 * 
	 * @param pkName
	 *           the name of the constraint
	 * @param columns
	 *           the columns that form the key
	 * @return
	 */
	public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns, ITableInfo ti)
	{
		ArrayList<String> result = new ArrayList<String>();
		StringBuffer addPKSQL = new StringBuffer();
		addPKSQL.append("ALTER TABLE ");
		addPKSQL.append(ti.getQualifiedName());
		addPKSQL.append(" ADD CONSTRAINT ");
		addPKSQL.append(pkName);
		addPKSQL.append(" PRIMARY KEY (");
		for (int i = 0; i < columns.length; i++)
		{
			TableColumnInfo info = columns[i];
			if (info.isNullable().equals("YES"))
			{
				result.add(getColumnNullableAlterSQL(info, false));
			}
			addPKSQL.append(info.getColumnName());
			if (i + 1 < columns.length)
			{
				addPKSQL.append(", ");
			}
		}
		addPKSQL.append(")");
		result.add(addPKSQL.toString());
		return result.toArray(new String[result.size()]);
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
	 * Returns the SQL used to alter the nullability of the specified column ALTER TABLE tableName ALTER COLUMN
	 * columnName dataType [DEFAULT expression] [NOT [NULL]]
	 * 
	 * @param info
	 *           the column to modify
	 * @return the SQL to execute
	 */
	public String[] getColumnNullableAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		return new String[] { DialectUtils.getColumnNullableAlterSQL(info, this, alterClause, true) };
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
	 * Returns the SQL used to alter the nullability of the specified column
	 * 
	 * @param info
	 *           the column to modify
	 * @return the SQL to execute
	 */
	private String getColumnNullableAlterSQL(TableColumnInfo info, boolean isNullable)
	{
		StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(info.getTableName());
		result.append(" ALTER COLUMN ");
		result.append(info.getColumnName());
		result.append(" ");
		result.append(DialectUtils.getTypeName(info, this));
		if (isNullable)
		{
			result.append(" NULL ");
		} else
		{
			result.append(" NOT NULL ");
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
	 * Returns the SQL that is used to change the column name. ALTER TABLE tableName ALTER COLUMN columnName
	 * RENAME TO name
	 * 
	 * @param from
	 *           the TableColumnInfo as it is
	 * @param to
	 *           the TableColumnInfo as it wants to be
	 * @return the SQL to make the change
	 */
	public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String renameToClause = DialectUtils.RENAME_TO_CLAUSE;
		return DialectUtils.getColumnNameAlterSQL(from, to, alterClause, renameToClause);
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
	 * data_type
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
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
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
		return true;
	}

	/**
	 * Returns the SQL command to change the specified column's default value ALTER TABLE table_name ALTER
	 * COLUMN column_name SET DEFAULT 'default value'
	 * 
	 * @param info
	 *           the column to modify and it's default value.
	 * @return SQL to make the change
	 */
	public String getColumnDefaultAlterSQL(TableColumnInfo info)
	{
		String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		String defaultClause = DialectUtils.SET_DEFAULT_CLAUSE;
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
		return DialectType.H2;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getIndexAccessMethodsTypes()
	 */
	public String[] getIndexAccessMethodsTypes()
	{
		return new String[] { "DEFAULT", "HASH" };
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getIndexStorageOptions()
	 */
	public String[] getIndexStorageOptions()
	{
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
		// "ALTER TABLE $tableName$ ALTER COLUMN $columnName$ IDENTITY";
		StringTemplate st = new StringTemplate(ST_ADD_AUTO_INCREMENT_STYLE_TWO);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY,
				column.getTableName(),
				ST_COLUMN_NAME_KEY,
				column.getColumnName());

		return new String[] { DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs) };
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

		if (column.getRemarks() != null && !"".equals(column.getRemarks()))
		{
			result.add(getColumnCommentAlterSQL(column));
		}

		return result.toArray(new String[result.size()]);
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
		// FOREIGN KEY (columnName [,...])
		// REFERENCES [refTableName] [(refColumnName[,...])]
		// [ON DELETE {CASCADE | RESTRICT | NO ACTION | SET DEFAULT | SET NULL}]
		// [ON UPDATE {CASCADE | SET DEFAULT | SET NULL}]

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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getAddUniqueConstraintSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo[],
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// "ALTER TABLE $tableName$ " +
		// "ADD $constraint$ $constraintName$ UNIQUE $index$ $indexName$ $indexType$ ( $indexColumnName$ )";

		StringTemplate st = new StringTemplate(ST_ADD_UNIQUE_CONSTRAINT_STYLE_ONE);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY,
				tableName,
				ST_CONSTRAINT_KEY,
				"CONSTRAINT",
				ST_CONSTRAINT_NAME_KEY,
				constraintName);

		return new String[] { DialectUtils.bindTemplateAttributes(this,
			st,
			valuesMap,
			columns,
			qualifier,
			prefs) };

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

		// "ALTER SEQUENCE $sequenceName$ " +
		// "$restartWith$ $startValue$ " +
		// "$incrementBy$ $incrementValue$ ";

		StringTemplate st = new StringTemplate(ST_ALTER_SEQUENCE_STYLE_ONE);

		HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_SEQUENCE_NAME_KEY, sequenceName);
		if (DialectUtils.isNotEmptyString(restart))
		{
			valuesMap.put(ST_RESTART_WITH_KEY, "RESTART WITH");
			valuesMap.put(ST_START_VALUE_KEY, restart);
		}
		if (DialectUtils.isNotEmptyString(increment))
		{
			valuesMap.put(ST_INCREMENT_BY_KEY, "INCREMENT BY");
			valuesMap.put(ST_INCREMENT_VALUE_KEY, increment);
		}

		return new String[] { DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs) };
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
		// CREATE {[UNIQUE [HASH]] INDEX [[IF NOT EXISTS] newIndexName]
		// | PRIMARY KEY [HASH]} ON (columnName [,...])

		StringTemplate st = new StringTemplate(ST_CREATE_INDEX_STYLE_TWO);
		// "CREATE $unique$ $storageOption$ INDEX $indexName$ " +
		// "ON $tableName$ ( $columnName; separator=\",\"$ )";

		HashMap<String, String> valuesMap = new HashMap<String, String>();

		if (unique)
		{
			valuesMap.put(ST_UNIQUE_KEY, "UNIQUE");
			if (accessMethod != null && "HASH".equalsIgnoreCase(accessMethod))
			{
				valuesMap.put(ST_STORAGE_OPTION_KEY, "HASH");
			}
		}
		valuesMap.put(ST_INDEX_NAME_KEY, indexName);
		valuesMap.put(ST_TABLE_NAME_KEY, tableName);

		return DialectUtils.getAddIndexSQL(this, st, valuesMap, columns, qualifier, prefs);
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
		// CREATE SEQUENCE [IF NOT EXISTS] newSequenceName
		// [START WITH long]
		// [INCREMENT BY long]
		// [CACHE long]

		// "CREATE SEQUENCE $sequenceName$ START WITH $startValue$ " +
		// "INCREMENT BY $incrementValue$ $cache$ $cacheValue$";

		StringTemplate st = new StringTemplate(ST_CREATE_SEQUENCE_STYLE_ONE);

		HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_SEQUENCE_NAME_KEY, sequenceName);
		if (DialectUtils.isNotEmptyString(cache))
		{
			valuesMap.put(ST_CACHE_KEY, "CACHE");
			valuesMap.put(ST_CACHE_VALUE_KEY, cache);
		}
		if (DialectUtils.isNotEmptyString(increment))
		{
			valuesMap.put(ST_INCREMENT_VALUE_KEY, increment);
		}
		if (DialectUtils.isNotEmptyString(start))
		{
			valuesMap.put(ST_START_VALUE_KEY, start);
		}

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
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
		// CREATE [FORCE] VIEW [IF NOT EXISTS] newViewName [(columnName [,..])]
		// AS select
		// "CREATE VIEW $viewName$ " +
		// "AS $selectStatement$ $with$ $checkOptionType$ $checkOption$";

		StringTemplate st = new StringTemplate(ST_CREATE_VIEW_STYLE_ONE);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_VIEW_NAME_KEY, viewName, ST_SELECT_STATEMENT_KEY, definition);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropConstraintSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// ALTER TABLE $tableName$ DROP CONSTRAINT $constraintName$
		StringTemplate st = new StringTemplate(ST_DROP_CONSTRAINT_STYLE_ONE);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_TABLE_NAME_KEY, tableName, ST_CONSTRAINT_NAME_KEY, constraintName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropIndexSQL(java.lang.String,
	 *      java.lang.String, boolean, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String getDropIndexSQL(String tableName, String indexName, boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// "DROP INDEX $indexName$";
		StringTemplate st = new StringTemplate(ST_DROP_INDEX_STYLE_THREE);

		HashMap<String, String> valuesMap = DialectUtils.getValuesMap(ST_INDEX_NAME_KEY, indexName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropSequenceSQL(java.lang.String,
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDropViewSQL(java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
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
		// "ALTER TABLE $oldObjectName$ RENAME TO $newObjectName$";
		StringTemplate st = new StringTemplate(ST_RENAME_OBJECT_STYLE_ONE);

		HashMap<String, String> valuesMap =
			DialectUtils.getValuesMap(ST_OLD_OBJECT_NAME_KEY, oldTableName, ST_NEW_OBJECT_NAME_KEY, newTableName);

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getRenameViewSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String msg = DialectUtils.getUnsupportedMessage(this, DialectUtils.RENAME_VIEW_TYPE);
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
		//"SELECT CURRENT_VALUE, 'NONE', 'NONE', CACHE, INCREMENT, 0 " +
		// "FROM INFORMATION_SCHEMA.SEQUENCES " +
		// "WHERE SEQUENCE_SCHEMA = ? " +
		// "AND SEQUENCE_NAME = ? ";
		String templateStr =
			"SELECT CURRENT_VALUE, 'NONE', 'NONE', CACHE, INCREMENT, 0 "
				+ "FROM INFORMATION_SCHEMA.SEQUENCES " + "WHERE SEQUENCE_SCHEMA = '$schemaName$' "
				+ "AND SEQUENCE_NAME = '$sequenceName$' " + "AND SEQUENCE_CATALOG = '$catalogName$'";
		StringTemplate st = new StringTemplate(templateStr);

		st.setAttribute(ST_SCHEMA_NAME_KEY, qualifier.getSchema());
		st.setAttribute(ST_CATALOG_NAME_KEY, qualifier.getCatalog());
		st.setAttribute(ST_SEQUENCE_NAME_KEY, sequenceName);

		return st.toString();

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
		return new String[] { DialectUtils.getUpdateSQL(tableName,
			setColumns,
			setValues,
			fromTables,
			whereColumns,
			whereValues,
			qualifier,
			prefs,
			this) };
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
		return true;
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
		// "select view_definition " +
		// "from information_schema.views " +
		// "where table_schema = ? " +
		// "and table_name = ? ";
		String templateStr =
			"select view_definition from information_schema.views "
				+ "where table_schema = '$schemaName$' and UPPER(table_name) = UPPER('$viewName$') ";

		StringTemplate st = new StringTemplate(templateStr);
		st.setAttribute(ST_SCHEMA_NAME_KEY, qualifier.getSchema());
		st.setAttribute(ST_VIEW_NAME_KEY, viewName);

		return st.toString();
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
		// TODO Auto-generated method stub
		return false;
	}
	
}
