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

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * An extension to the standard DB2 dialect. Much of the behavior of DB2 is found in Derby.
 */
public class DerbyDialect extends DB2Dialect implements HibernateDialect
{

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DerbyDialect.class);

	private static interface i18n
	{
		// i18n[DerbyDialect.typeMessage=Derby doesn't allow the column type to
		// be altered]
		String TYPE_MESSAGE = s_stringMgr.getString("DerbyDialect.typeMessage");

		// i18n[DerbyDialect.varcharMessage=Derby only allows varchar columns
		// to be altered]
		String VARCHAR_MESSAGE = s_stringMgr.getString("DerbyDialect.varcharMessage");

		// i18n[DerbyDialect.columnLengthMessage=Derby only allows varchar
		// column length to be increased]
		String COLUMN_LENGTH_MESSAGE = s_stringMgr.getString("DerbyDialect.columnLengthMessage");
	}

	public DerbyDialect()
	{
		super();
		registerColumnType(Types.BIGINT, "bigint");
		registerColumnType(Types.BINARY, 254, "char($l) for bit data");
		registerColumnType(Types.BINARY, "blob");
		registerColumnType(Types.BIT, "smallint");
		// DB2 spec says max=2147483647, but the driver throws an exception
		registerColumnType(Types.BLOB, 1073741823, "blob($l)");
		registerColumnType(Types.BLOB, "blob(1073741823)");
		registerColumnType(Types.BOOLEAN, "smallint");
		registerColumnType(Types.CHAR, 254, "char($l)");
		registerColumnType(Types.CHAR, 4000, "varchar($l)");
		registerColumnType(Types.CHAR, 32700, "long varchar");
		registerColumnType(Types.CHAR, 1073741823, "clob($l)");
		registerColumnType(Types.CHAR, "clob(1073741823)");
		// DB2 spec says max=2147483647, but the driver throws an exception
		registerColumnType(Types.CLOB, 1073741823, "clob($l)");
		registerColumnType(Types.CLOB, "clob(1073741823)");
		registerColumnType(Types.DATE, "date");
		registerColumnType(Types.DECIMAL, "decimal($p)");
		// Derby is real close to DB2. Only difference I've found so far is 48
		// instead of 53 for float length llimit.
		registerColumnType(Types.DOUBLE, "float($p)");
		registerColumnType(Types.FLOAT, "float($p)");
		registerColumnType(Types.INTEGER, "int");
		registerColumnType(Types.LONGVARBINARY, 32700, "long varchar for bit data");
		// DB2 spec says max=2147483647, but the driver throws an exception
		registerColumnType(Types.LONGVARBINARY, 1073741823, "blob($l)");
		registerColumnType(Types.LONGVARBINARY, "blob(1073741823)");
		registerColumnType(Types.LONGVARCHAR, 32700, "long varchar");
		// DB2 spec says max=2147483647, but the driver throws an exception
		registerColumnType(Types.LONGVARCHAR, 1073741823, "clob($l)");
		registerColumnType(Types.LONGVARCHAR, "clob(1073741823)");
		registerColumnType(Types.NUMERIC, "bigint");
		registerColumnType(Types.REAL, "real");
		registerColumnType(Types.SMALLINT, "smallint");
		registerColumnType(Types.TIME, "time");
		registerColumnType(Types.TIMESTAMP, "timestamp");
		registerColumnType(Types.TINYINT, "smallint");
		registerColumnType(Types.VARBINARY, 254, "long varchar for bit data");
		registerColumnType(Types.VARBINARY, "blob");
		registerColumnType(Types.VARCHAR, 4000, "varchar($l)");
		registerColumnType(Types.VARCHAR, 32700, "long varchar");
		// DB2 spec says max=2147483647, but the driver throws an exception
		registerColumnType(Types.VARCHAR, 1073741823, "clob($l)");
		registerColumnType(Types.VARCHAR, "clob(1073741823)");

	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#canPasteTo(net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo)
	 */
	@Override
	public boolean canPasteTo(final IDatabaseObjectInfo info)
	{
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#supportsSchemasInTableDefinition()
	 */
	@Override
	public boolean supportsSchemasInTableDefinition()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getMaxPrecision(int)
	 */
	@Override
	public int getMaxPrecision(final int dataType)
	{
		if (dataType == Types.DOUBLE || dataType == Types.FLOAT)
		{
			return 48;
		} else
		{
			return 31;
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getMaxScale(int)
	 */
	@Override
	public int getMaxScale(final int dataType)
	{
		return getMaxPrecision(dataType);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getColumnLength(int, int)
	 */
	@Override
	public int getColumnLength(final int columnSize, final int dataType)
	{
		return columnSize;
	}

	/**
	 * The string which identifies this dialect in the dialect chooser.
	 * 
	 * @return a descriptive name that tells the user what database this dialect is design to work with.
	 */
	@Override
	public String getDisplayName()
	{
		return "Derby";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#supportsProduct(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean supportsProduct(final String databaseProductName, final String databaseProductVersion)
	{
		if (databaseProductName == null)
		{
			return false;
		}
		if (databaseProductName.trim().startsWith("Apache Derby"))
		{
			// We don't yet have the need to discriminate by version.
			return true;
		}
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getAddColumnSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
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

		final String sql =
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#supportsDropColumn()
	 */
	@Override
	public boolean supportsDropColumn()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getColumnDropSQL(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public String getColumnDropSQL(final String tableName, final String columnName)
	{
		return DialectUtils.getColumnDropSQL(tableName, columnName);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getTableDropSQL(net.sourceforge.squirrel_sql.fw.sql.ITableInfo,
	 *      boolean, boolean)
	 */
	@Override
	public List<String> getTableDropSQL(final ITableInfo iTableInfo, final boolean cascadeConstraints,
		final boolean isMaterializedView)
	{
		return DialectUtils.getTableDropSQL(iTableInfo,
			false,
			cascadeConstraints,
			false,
			DialectUtils.CASCADE_CLAUSE,
			false);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getAddPrimaryKeySQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo[],
	 *      net.sourceforge.squirrel_sql.fw.sql.ITableInfo)
	 */
	@Override
	public String[] getAddPrimaryKeySQL(final String pkName, final TableColumnInfo[] colInfos,
		final ITableInfo ti)
	{
		final ArrayList<String> result = new ArrayList<String>();
		final String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;

		// convert each column that will be a member key to non-null - this
		// doesn't hurt if they are already null.
		DialectUtils.getMultiColNotNullSQL(colInfos, this, alterClause, false, result);

		result.add(DialectUtils.getAddPrimaryKeySQL(ti, pkName, colInfos, false));

		return result.toArray(new String[result.size()]);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#supportsColumnComment()
	 */
	@Override
	public boolean supportsColumnComment()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getColumnCommentAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo)
	 */
	@Override
	public String getColumnCommentAlterSQL(final TableColumnInfo info) throws UnsupportedOperationException
	{
		final int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#supportsAlterColumnNull()
	 */
	@Override
	public boolean supportsAlterColumnNull()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getColumnNullableAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getColumnNullableAlterSQL(final TableColumnInfo info,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
	{
		final String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		final boolean specifyColumnType = false;
		return new String[] {
			DialectUtils.getColumnNullableAlterSQL(info, this, alterClause, specifyColumnType)
		};
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#supportsRenameColumn()
	 */
	@Override
	public boolean supportsRenameColumn()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getColumnNameAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo)
	 */
	@Override
	public String getColumnNameAlterSQL(final TableColumnInfo from, final TableColumnInfo to)
	{
		final int featureId = DialectUtils.COLUMN_NAME_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#supportsAlterColumnType()
	 */
	@Override
	public boolean supportsAlterColumnType()
	{
		return true;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getColumnTypeAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public List<String> getColumnTypeAlterSQL(final TableColumnInfo from, final TableColumnInfo to,
		final DatabaseObjectQualifier qualifier, final SqlGenerationPreferences prefs)
		throws UnsupportedOperationException
	{
		final ArrayList<String> list = new ArrayList<String>();
		if (from.getDataType() != to.getDataType())
		{
			throw new UnsupportedOperationException(i18n.TYPE_MESSAGE);
		}
		if (from.getDataType() != Types.VARCHAR)
		{
			throw new UnsupportedOperationException(i18n.VARCHAR_MESSAGE);
		}
		if (from.getColumnSize() > to.getColumnSize())
		{
			throw new UnsupportedOperationException(i18n.COLUMN_LENGTH_MESSAGE);
		}
		final StringBuffer result = new StringBuffer();
		result.append("ALTER TABLE ");
		result.append(to.getTableName());
		result.append(" ALTER COLUMN ");
		result.append(to.getColumnName());
		result.append(" SET DATA TYPE ");
		result.append(DialectUtils.getTypeName(to, this));
		list.add(result.toString());
		return list;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#supportsAlterColumnDefault()
	 */
	@Override
	public boolean supportsAlterColumnDefault()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getColumnDefaultAlterSQL(net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo)
	 */
	@Override
	public String getColumnDefaultAlterSQL(final TableColumnInfo info)
	{
		final int featureId = DialectUtils.COLUMN_DEFAULT_ALTER_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getDropPrimaryKeySQL(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public String getDropPrimaryKeySQL(final String pkName, final String tableName)
	{
		return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getDropForeignKeySQL(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public String getDropForeignKeySQL(final String fkName, final String tableName)
	{
		return DialectUtils.getDropForeignKeySQL(fkName, tableName);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDialectType()
	 */
	@Override
	public DialectType getDialectType()
	{
		return DialectType.DERBY;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#supportsCreateSequence()
	 */
	@Override
	public boolean supportsCreateSequence()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getCreateSequenceSQL(java.lang.String,
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#supportsDropSequence()
	 */
	@Override
	public boolean supportsDropSequence()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getDropSequenceSQL(java.lang.String, boolean,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#supportsSequence()
	 */
	@Override
	public boolean supportsSequence()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#supportsSequenceInformation()
	 */
	@Override
	public boolean supportsSequenceInformation()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getSequenceInformationSQL(java.lang.String,
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final int featureId = DialectUtils.SEQUENCE_INFORMATION_TYPE;
		final String msg = DialectUtils.getUnsupportedMessage(this, featureId);
		throw new UnsupportedOperationException(msg);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#supportsAlterSequence()
	 */
	@Override
	public boolean supportsAlterSequence()
	{
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getAlterSequenceSQL(java.lang.String,
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
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getAddUniqueConstraintSQL(java.lang.String,
	 *      java.lang.String, net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo[],
	 *      net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier,
	 *      net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String[] getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();

		ArrayList<String> columnNotNullAlters = new ArrayList<String>();
		// Derby requires that columns be not-null before applying a unique constraint
		final boolean specifyColumnType = false;
		final String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
		for (TableColumnInfo column : columns)
		{
			if (column.isNullable().equalsIgnoreCase("YES"))
			{
				columnNotNullAlters.add(DialectUtils.getColumnNullableAlterSQL(column,
					false,
					this,
					alterClause,
					specifyColumnType));
			}
		}
		result.addAll(columnNotNullAlters);

		result.add(DialectUtils.getAddUniqueConstraintSQL(tableName,
			constraintName,
			columns,
			qualifier,
			prefs,
			this));

		return result.toArray(new String[result.size()]);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#getViewDefinitionSQL(java.lang.String, net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier, net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences)
	 */
	@Override
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		/*
		select v.VIEWDEFINITION 
		from sys.SYSVIEWS v, sys.SYSTABLES t, sys.SYSSCHEMAS s 
		where v.TABLEID = t.TABLEID 
		and s.SCHEMAID = t.SCHEMAID 
		and UPPER(t.TABLENAME) = 'VIEWNAME'
		and UPPER(s.SCHEMANAME) = 'SCHEMANAME'
		 */
		
		StringBuilder result = new StringBuilder();
		result.append("select v.VIEWDEFINITION ");
		result.append("from sys.SYSVIEWS v, sys.SYSTABLES t, sys.SYSSCHEMAS s ");
		result.append("where v.TABLEID = t.TABLEID ");
		result.append("and s.SCHEMAID = t.SCHEMAID ");
		result.append("and UPPER(t.TABLENAME) = '");
		result.append(viewName.toUpperCase());
		result.append("' and UPPER(s.SCHEMANAME) = '");
		result.append(qualifier.getSchema().toUpperCase());
		result.append("'");
		return result.toString();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.dialects.DB2Dialect#supportsAutoIncrement()
	 */
	@Override
	public boolean supportsAutoIncrement()
	{
		return false;
	}

}
