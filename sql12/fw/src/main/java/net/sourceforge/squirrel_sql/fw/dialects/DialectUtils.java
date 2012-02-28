package net.sourceforge.squirrel_sql.fw.dialects;

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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IndexInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;

/**
 * A simple utility class in which to place common code shared amongst the dialects. Since the dialects all
 * inherit behavior from specific server dialects, it is not possible to inherit common behavior from a single
 * base class. So, this class is where common code is located.
 * 
 * @author manningr
 */
public class DialectUtils implements StringTemplateConstants
{

	/** Logger for this class. */
	private static final ILogger log = LoggerController.createLogger(DialectUtils.class);

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DialectUtils.class);

	// alter column clauses

	public static final String ALTER_COLUMN_CLAUSE = "ALTER COLUMN";

	public static final String MODIFY_COLUMN_CLAUSE = "MODIFY COLUMN";

	public static final String MODIFY_CLAUSE = "MODIFY";

	public static final String COLUMN_CLAUSE = "COLUMN";

	// alter name clauses

	public static final String RENAME_COLUMN_CLAUSE = "RENAME COLUMN";

	public static final String RENAME_CLAUSE = "RENAME";

	public static final String RENAME_TO_CLAUSE = "RENAME TO";

	public static final String TO_CLAUSE = "TO";

	// alter default clauses

	public static final String DEFAULT_CLAUSE = "DEFAULT";

	public static final String SET_DEFAULT_CLAUSE = "SET DEFAULT";

	public static final String SET_CLAUSE = "SET";

	public static final String ADD_DEFAULT_CLAUSE = "ADD DEFAULT";

	public static final String DROP_DEFAULT_CLAUSE = "DROP DEFAULT";

	// alter type clauses

	public static final String TYPE_CLAUSE = "TYPE";

	public static final String SET_DATA_TYPE_CLAUSE = "SET DATA TYPE";

	// drop column clauses

	public static final String DROP_CLAUSE = "DROP";

	public static final String DROP_COLUMN_CLAUSE = "DROP COLUMN";

	// cascade constraint clauses

	public static final String CASCADE_CLAUSE = "CASCADE";

	public static final String CASCADE_CONSTRAINTS_CLAUSE = "CASCADE CONSTRAINTS";

	// sequence clauses

	public static final String CACHE_CLAUSE = "CACHE";

	public static final String CYCLE_CLAUSE = "CYCLE";

	public static final String INCREMENT_CLAUSE = "INCREMENT";

	public static final String INCREMENT_BY_CLAUSE = "INCREMENT BY";

	public static final String NOCYCLE_CLAUSE = "NOCYCLE";

	public static final String NO_CYCLE_CLAUSE = "NO CYCLE";

	public static final String MAXVALUE_CLAUSE = "MAXVALUE";

	public static final String NO_MAXVALUE_CLAUSE = "NO MAXVALUE";

	public static final String NOMAXVALUE_CLAUSE = "NOMAXVALUE";

	public static final String MINVALUE_CLAUSE = "MINVALUE";

	public static final String NO_MINVALUE_CLAUSE = "NO MINVALUE";

	public static final String NOMINVALUE_CLAUSE = "NOMINVALUE";

	// view clauses

	public static final String WITH_CHECK_OPTION_CLAUSE = "WITH CHECK OPTION";

	// Clauses
	public static final String CREATE_CLAUSE = "CREATE";

	public static final String ALTER_CLAUSE = "ALTER";

	public static final String TABLE_CLAUSE = "TABLE";

	public static final String INDEX_CLAUSE = "INDEX";

	public static final String VIEW_CLAUSE = "VIEW";

	public static final String UPDATE_CLAUSE = "UPDATE";

	public static final String FROM_CLAUSE = "FROM";

	public static final String WHERE_CLAUSE = "WHERE";

	public static final String AND_CLAUSE = "AND";

	public static final String CREATE_TABLE_CLAUSE = CREATE_CLAUSE + " " + TABLE_CLAUSE;

	public static final String ALTER_TABLE_CLAUSE = ALTER_CLAUSE + " " + TABLE_CLAUSE;

	public static final String ALTER_VIEW_CLAUSE = ALTER_CLAUSE + " " + VIEW_CLAUSE;

	public static final String DROP_TABLE_CLAUSE = DROP_CLAUSE + " " + TABLE_CLAUSE;

	public static final String ADD_COLUMN_CLAUSE = "ADD " + COLUMN_CLAUSE;

	public static final String SEQUENCE_CLAUSE = "SEQUENCE";

	public static final String CREATE_SEQUENCE_CLAUSE = CREATE_CLAUSE + " " + SEQUENCE_CLAUSE;

	public static final String ALTER_SEQUENCE_CLAUSE = ALTER_CLAUSE + " " + SEQUENCE_CLAUSE;

	public static final String DROP_SEQUENCE_CLAUSE = DROP_CLAUSE + " " + SEQUENCE_CLAUSE;

	public static final String CREATE_INDEX_CLAUSE = CREATE_CLAUSE + " " + INDEX_CLAUSE;

	public static final String DROP_INDEX_CLAUSE = DROP_CLAUSE + " " + INDEX_CLAUSE;

	public static final String CREATE_VIEW_CLAUSE = CREATE_CLAUSE + " " + VIEW_CLAUSE;

	public static final String DROP_VIEW_CLAUSE = DROP_CLAUSE + " " + VIEW_CLAUSE;

	public static final String INSERT_INTO_CLAUSE = "INSERT INTO";

	public static final String PRIMARY_KEY_CLAUSE = "PRIMARY KEY";

	public static final String FOREIGN_KEY_CLAUSE = "FOREIGN KEY";

	public static final String NOT_NULL_CLAUSE = "NOT NULL";

	public static final String UNIQUE_CLAUSE = "UNIQUE";

	public static final String RESTRICT_CLAUSE = "RESTRICT";

	public static final String CONSTRAINT_CLAUSE = "CONSTRAINT";

	public static final String ADD_CONSTRAINT_CLAUSE = "ADD " + CONSTRAINT_CLAUSE;

	public static final String DROP_CONSTRAINT_CLAUSE = "DROP " + CONSTRAINT_CLAUSE;

	// features

	public static final int COLUMN_COMMENT_ALTER_TYPE = 0;

	public static final int COLUMN_DEFAULT_ALTER_TYPE = 1;

	public static final int COLUMN_DROP_TYPE = 2;

	public static final int COLUMN_NAME_ALTER_TYPE = 3;

	public static final int COLUMN_NULL_ALTER_TYPE = 4;

	public static final int COLUMN_TYPE_ALTER_TYPE = 5;

	public static final int ADD_PRIMARY_KEY_TYPE = 6;

	public static final int DROP_PRIMARY_KEY_TYPE = 7;

	public static final int CREATE_TABLE_TYPE = 8;

	public static final int RENAME_TABLE_TYPE = 9;

	public static final int CREATE_VIEW_TYPE = 10;

	public static final int RENAME_VIEW_TYPE = 11;

	public static final int DROP_VIEW_TYPE = 12;

	public static final int CREATE_INDEX_TYPE = 13;

	public static final int DROP_INDEX_TYPE = 14;

	public static final int CREATE_SEQUENCE_TYPE = 15;

	public static final int ALTER_SEQUENCE_TYPE = 16;

	public static final int SEQUENCE_INFORMATION_TYPE = 17;

	public static final int DROP_SEQUENCE_TYPE = 18;

	public static final int ADD_FOREIGN_KEY_TYPE = 19;

	public static final int ADD_UNIQUE_TYPE = 20;

	public static final int ADD_AUTO_INCREMENT_TYPE = 21;

	public static final int DROP_CONSTRAINT_TYPE = 22;

	public static final int INSERT_INTO_TYPE = 23;

	public static final int UPDATE_TYPE = 24;

	public static final int VIEW_DEFINITION_TYPE = 25;

	public static final int ADD_COLUMN_TYPE = 26;

	public static String appendDefaultClause(TableColumnInfo info, StringBuilder buffer)
	{

		if (info.getDefaultValue() != null && !"".equals(info.getDefaultValue()))
		{
			buffer.append(" DEFAULT ");
			if (JDBCTypeMapper.isNumberType(info.getDataType()))
			{
				buffer.append(info.getDefaultValue());
			}
			else
			{
				buffer.append("'");
				buffer.append(info.getDefaultValue());
				buffer.append("'");
			}
		}
		return buffer.toString();
	}

	/**
	 * Returns the SQL statement to use to add a comment to the specified column of the specified table.
	 * 
	 * @param tableName
	 *           the name of the table to create the SQL for.
	 * @param columnName
	 *           the name of the column to create the SQL for.
	 * @param comment
	 *           the comment to add.
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect representing the target database.
	 * @return
	 * @throws UnsupportedOperationException
	 *            if the database doesn't support annotating columns with a comment.
	 */
	public static String getColumnCommentAlterSQL(String tableName, String columnName, String comment,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		final StringBuilder result = new StringBuilder();
		result.append("COMMENT ON COLUMN ");
		result.append(shapeQualifiableIdentifier(tableName, qualifier, prefs, dialect));
		result.append(".");
		if (prefs.isQuoteColumnNames())
		{
			result.append(shapeIdentifier(columnName, prefs, dialect));
		}
		else
		{
			result.append(columnName);
		}
		result.append(" IS '");
		if (comment != null && !"".equals(comment))
		{
			result.append(comment);
		}
		result.append("'");
		return result.toString();
	}

	/**
	 * Returns the SQL statement to use to add a comment to the specified column of the specified table.
	 * 
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect representing the target database.
	 * @param tableName
	 *           the name of the table to create the SQL for.
	 * @param columnName
	 *           the name of the column to create the SQL for.
	 * @param comment
	 *           the comment to add.
	 * @return
	 * @throws UnsupportedOperationException
	 *            if the database doesn't support annotating columns with a comment.
	 */
	public static String getColumnCommentAlterSQL(TableColumnInfo info, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		if (info == null) { throw new IllegalArgumentException("parameter info cannot be null"); }
		return getColumnCommentAlterSQL(info.getTableName(), info.getColumnName(), info.getRemarks(),
			qualifier, prefs, dialect);
	}

	/**
	 * @param tableName
	 * @param columnName
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect representing the target database.
	 * @return
	 */
	public static String getColumnDropSQL(String tableName, String columnName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		return getColumnDropSQL(tableName, columnName, "DROP", false, null, qualifier, prefs, dialect);
	}

	/**
	 * @param tableName
	 *           the unqualified table
	 * @param columnName
	 *           the name of the column
	 * @param addConstraintClause
	 *           whether or not to add a constraint clause
	 * @param constraintClause
	 *           the constraint clause to add
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return
	 */
	public static String getColumnDropSQL(String tableName, String columnName, String dropClause,
		boolean addConstraintClause, String constraintClause, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		final StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(shapeQualifiableIdentifier(tableName, qualifier, prefs, dialect));
		result.append(" ");
		result.append(dropClause);
		result.append(" ");
		if (prefs.isQuoteColumnNames())
		{
			result.append(shapeIdentifier(columnName, prefs, dialect));
		}
		else
		{
			result.append(columnName);
		}
		if (addConstraintClause)
		{
			result.append(" ");
			result.append(constraintClause);
		}
		return result.toString();
	}

	/**
	 * Returns the SQL that forms the command to drop the specified table. If cascade contraints is supported
	 * by the dialect and cascadeConstraints is true, then a drop statement with cascade constraints clause
	 * will be formed.
	 * 
	 * @param iTableInfo
	 *           the table to drop
	 * @param supportsCascade
	 *           whether or not the cascade clause should be added.
	 * @param cascadeValue
	 *           whether or not to drop any FKs that may reference the specified table.
	 * @param supportsMatViews
	 *           whether or not the dialect supports materialized views
	 * @param cascadeClause
	 *           what cascade clause to append.
	 * @param isMatView
	 *           true if the tableInfo represents a materialized view.
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect representing the target database.
	 * @return the drop SQL command.
	 */
	public static List<String> getTableDropSQL(ITableInfo iTableInfo, boolean supportsCascade,
		boolean cascadeValue, boolean supportsMatViews, String cascadeClause, boolean isMatView,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		final StringBuilder result = new StringBuilder();
		if (supportsMatViews && isMatView)
		{
			result.append("DROP MATERIALIZED VIEW ");
		}
		else
		{
			result.append("DROP TABLE ");
		}
		result.append(shapeQualifiableIdentifier(iTableInfo.getSimpleName(), qualifier, prefs, dialect));
		if (supportsCascade && cascadeValue)
		{
			result.append(" ");
			result.append(cascadeClause);
		}
		return Arrays.asList(new String[] { result.toString() });
	}

	/**
	 * @param info
	 * @param dialect
	 * @return
	 */
	public static String getTypeName(TableColumnInfo info, HibernateDialect dialect)
	{
		return dialect.getTypeName(info.getDataType(), info.getColumnSize(), info.getColumnSize(),
			info.getDecimalDigits());
	}

	/**
	 * Returns the SQL used to alter the specified column to allow/disallow null values. <br>
	 * ALTER TABLE table_name &lt;alterClause&gt; column_name TYPE NULL | NOT NULL <br>
	 * ALTER TABLE table_name &lt;alterClause&gt; column_name NULL | NOT NULL
	 * 
	 * @param info
	 *           the column to modify
	 * @param dialect
	 *           the HibernateDialect representing the target database.
	 * @param alterClause
	 *           the alter column clause (e.g. ALTER COLUMN )
	 * @param specifyType
	 *           whether or not the column type needs to be specified
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return the SQL to execute
	 */
	public static String getColumnNullableAlterSQL(TableColumnInfo info, HibernateDialect dialect,
		String alterClause, boolean specifyType, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final boolean nullable = info.isNullable().equalsIgnoreCase("YES");
		return getColumnNullableAlterSQL(info, nullable, dialect, alterClause, specifyType, qualifier, prefs);
	}

	/**
	 * Returns the SQL used to alter the specified column to allow/disallow null values. <br>
	 * ALTER TABLE table_name &lt;alterClause&gt; column_name TYPE NULL | NOT NULL <br>
	 * ALTER TABLE table_name &lt;alterClause&gt; column_name NULL | NOT NULL
	 * 
	 * @param info
	 *           the column to modify
	 * @param nullable
	 *           whether or not the column should allow nulls
	 * @param dialect
	 *           the HibernateDialect representing the target database.
	 * @param alterClause
	 *           the alter column clause (e.g. ALTER COLUMN )
	 * @param specifyType
	 *           whether or not the column type needs to be specified
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return the SQL to execute
	 */
	public static String getColumnNullableAlterSQL(TableColumnInfo info, boolean nullable,
		HibernateDialect dialect, String alterClause, boolean specifyType, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		final StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(shapeQualifiableIdentifier(info.getTableName(), qualifier, prefs, dialect));
		result.append(" ");
		result.append(alterClause);
		result.append(" ");
		if (prefs.isQuoteColumnNames())
		{
			result.append(shapeIdentifier(info.getColumnName(), prefs, dialect));
		}
		else
		{
			result.append(info.getColumnName());
		}
		if (specifyType)
		{
			result.append(" ");
			result.append(getTypeName(info, dialect));
			result.append(" ");
		}
		if (nullable)
		{
			result.append(" NULL");
		}
		else
		{
			result.append(" NOT NULL");
		}
		return result.toString();
	}

	/**
	 * Populates the specified ArrayList with SQL statement(s) required to convert each of the columns to not
	 * null. This is typically needed in some databases when adding a primary key (some dbs do this step
	 * automatically)
	 * 
	 * @param colInfos
	 *           the columns to be made not null
	 * @param dialect
	 * @param result
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 */
	public static void getMultiColNotNullSQL(TableColumnInfo[] colInfos, HibernateDialect dialect,
		String alterClause, boolean specifyType, ArrayList<String> result, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		for (final TableColumnInfo colInfo : colInfos)
		{
			final StringBuilder notNullSQL = new StringBuilder();
			notNullSQL.append("ALTER TABLE ");
			notNullSQL.append(shapeQualifiableIdentifier(colInfo.getTableName(), qualifier, prefs, dialect));
			notNullSQL.append(" ");
			notNullSQL.append(alterClause);
			notNullSQL.append(" ");
			notNullSQL.append(shapeIdentifier(colInfo.getColumnName(), prefs, dialect));
			if (specifyType)
			{
				notNullSQL.append(" ");
				notNullSQL.append(DialectUtils.getTypeName(colInfo, dialect));
			}
			notNullSQL.append(" NOT NULL");
			result.add(notNullSQL.toString());
		}
	}

	/**
	 * Returns the SQL for creating a primary key consisting of the specified colInfos. ALTER TABLE table_name
	 * ADD CONSTRAINT pkName PRIMARY KEY (col,...); or ALTER TABLE table_name ADD CONSTRAINT PRIMARY KEY
	 * (col,...) CONSTRAINT pkName;
	 * 
	 * @param ti
	 *           the ITableInfo representing the table to add a primary key to
	 * @param colInfos
	 *           the TableColumnInfos representing all of the columns that represent a primary key
	 * @param appendConstraintName
	 *           whether or not the pkName (constraint name) should be placed at the end of the statement.
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return
	 */
	public static String getAddPrimaryKeySQL(ITableInfo ti, String pkName, TableColumnInfo[] colInfos,
		boolean appendConstraintName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs,
		HibernateDialect dialect)
	{
		final StringBuilder pkSQL = new StringBuilder();

		String shapedPkName = pkName;
		if (prefs.isQuoteConstraintNames())
		{
			shapedPkName = shapeIdentifier(pkName, prefs, dialect);
		}

		pkSQL.append("ALTER TABLE ");
		pkSQL.append(shapeQualifiableIdentifier(ti.getSimpleName(), qualifier, prefs, dialect));
		pkSQL.append(" ADD CONSTRAINT ");
		if (!appendConstraintName)
		{
			pkSQL.append(shapedPkName);
		}
		pkSQL.append(" PRIMARY KEY ");
		pkSQL.append(getColumnList(colInfos, qualifier, prefs, dialect));
		if (appendConstraintName)
		{
			pkSQL.append(" CONSTRAINT ");
			pkSQL.append(shapedPkName);
		}
		return pkSQL.toString();
	}

	/**
	 * @param fkST
	 * @param fkValuesMap
	 * @param childIndexST
	 * @param ckIndexValuesMap
	 * @param localRefColumns
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return
	 */
	public static String[] getAddForeignKeyConstraintSQL(StringTemplate fkST,
		HashMap<String, String> fkValuesMap, StringTemplate childIndexST,
		HashMap<String, String> ckIndexValuesMap, Collection<String[]> localRefColumns,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{

		final ArrayList<String> result = new ArrayList<String>();

		bindAttributes(dialect, fkST, fkValuesMap, qualifier, prefs);

		final String[] childColumnNames = new String[localRefColumns.size()];
		int i = 0;
		for (final String[] localRefColumn : localRefColumns)
		{
			final String childColumnName = localRefColumn[0];
			childColumnNames[i++] = childColumnName;
			final String parentColumnName = localRefColumn[1];
			bindAttribute(dialect, fkST, ST_CHILD_COLUMN_KEY, childColumnName, qualifier, prefs);
			bindAttribute(dialect, fkST, ST_PARENT_COLUMN_KEY, parentColumnName, qualifier, prefs);
		}

		result.add(fkST.toString());

		// Additional Index Creation
		if (childIndexST != null)
		{
			result.add(getAddIndexSQL(dialect, childIndexST, ckIndexValuesMap, childColumnNames, qualifier,
				prefs));
		}

		return result.toArray(new String[result.size()]);
	}

	/**
	 * Gets the SQL command to add a foreign key constraint to a table.
	 * 
	 * @param localTableName
	 *           name of the table where the foreign key should be stored.
	 * @param refTableName
	 *           name of the table where the foreign key should reference to.
	 * @param constraintName
	 *           name of the constraint. Leave it empty and it won't create a CONSTRAINT name.
	 * @param deferrable
	 *           true if the constraint is deferrable, false if not. Can be null for dialects that don't
	 *           support this
	 * @param initiallyDeferred
	 *           true if the constraint is deferrable and initially deferred, false if not. Can be null for
	 *           dialects that don't support this
	 * @param matchFull
	 *           true if the referenced columns using MATCH FULL. Can be null for dialects that don't support
	 *           this
	 * @param autoFKIndex
	 *           true to create an additional INDEX with the given fkIndexName Name.
	 * @param fkIndexName
	 *           name of the foreign key index name.
	 * @param localRefColumns
	 *           local and referenced column collection. In the first Element of the String Array should be the
	 *           local column name and in the second Element the referenced Table column name.
	 * @param onUpdateAction
	 *           update action. For example "RESTRICT".
	 * @param onDeleteAction
	 *           delete action. For exampel "NO ACTION".
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return the sql command to add a foreign key constraint.
	 */
	public static String[] getAddForeignKeyConstraintSQL(String localTableName, String refTableName,
		String constraintName, Boolean deferrable, Boolean initiallyDeferred, Boolean matchFull,
		boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction,
		String onDeleteAction, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs,
		HibernateDialect dialect)
	{
		final ArrayList<String> result = new ArrayList<String>();

		// ALTER TABLE localTableName
		// ADD CONSTRAINT constraintName FOREIGN KEY (localColumn1,
		// localColumn2)
		// REFERENCES referencedTableName (referencedColumn1, referencedColumn2)
		// MATCH FULL ON UPDATE RESTRICT ON DELETE NO ACTION DEFERRABLE
		// INITIALLY DEFERRED;
		final StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.ALTER_TABLE_CLAUSE + " ");
		sql.append(shapeQualifiableIdentifier(localTableName, qualifier, prefs, dialect));
		sql.append("\n");

		if (constraintName != null && !constraintName.equals(""))
		{
			sql.append(" ");
			sql.append(DialectUtils.ADD_CONSTRAINT_CLAUSE);
			sql.append(" ");
			if (prefs.isQuoteConstraintNames())
			{
				sql.append(shapeIdentifier(constraintName, prefs, dialect));
			}
			else
			{
				sql.append(constraintName);
			}
			sql.append("\n");
		}

		sql.append(" ");
		sql.append(DialectUtils.FOREIGN_KEY_CLAUSE);
		sql.append(" (");

		final ArrayList<String> localColumns = new ArrayList<String>();
		final StringBuilder refColumns = new StringBuilder();
		for (final String[] columns : localRefColumns)
		{
			if (prefs.isQuoteColumnNames())
			{
				sql.append(shapeIdentifier(columns[0], prefs, dialect));
			}
			else
			{
				sql.append(columns[0]);
			}
			sql.append(", ");
			localColumns.add(columns[0]);
			if (prefs.isQuoteColumnNames())
			{
				refColumns.append(shapeIdentifier(columns[1], prefs, dialect));
			}
			else
			{
				refColumns.append(columns[1]);
			}
			refColumns.append(", ");
		}
		sql.setLength(sql.length() - 2); // deletes the last ", "
		refColumns.setLength(refColumns.length() - 2); // deletes the last ", "

		sql.append(")\n REFERENCES ");
		sql.append(shapeQualifiableIdentifier(refTableName, qualifier, prefs, dialect));
		sql.append(" (");
		sql.append(refColumns.toString());
		sql.append(")\n");

		// Options
		if (matchFull != null && matchFull)
		{
			sql.append(" MATCH FULL");
		}

		if (onUpdateAction != null && !onUpdateAction.equals(""))
		{
			sql.append(" ON UPDATE ");
			sql.append(onUpdateAction);
		}

		if (onDeleteAction != null && !onDeleteAction.equals(""))
		{
			sql.append(" ON DELETE ");
			sql.append(onDeleteAction);
		}

		if (deferrable != null && deferrable)
		{
			sql.append(" DEFERRABLE");
		}
		if (initiallyDeferred != null && initiallyDeferred)
		{
			sql.append(" INITIALLY DEFERRED");
		}

		result.add(sql.toString());

		// Additional Index Creation
		if (autoFKIndex && !fkIndexName.equals(""))
		{
			result.add(getAddIndexSQL(dialect, fkIndexName, localTableName, null,
				localColumns.toArray(new String[localColumns.size()]), false, null, null, qualifier, prefs));
		}

		return result.toArray(new String[result.size()]);
	}

	/**
	 * Gets the SQL command to add a unique constraint to a table.
	 * 
	 * @param tableName
	 *           name of the table where the unique constraint should be added to.
	 * @param constraintName
	 *           name of the constraint.
	 * @param columns
	 *           the unique columns.
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return the sql command to add a unique constraint.
	 */
	public static String getAddUniqueConstraintSQL(String tableName, String constraintName,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs,
		HibernateDialect dialect)
	{
		// ALTER TABLE tableName
		// ADD CONSTRAINT constraintName UNIQUE (column1, column2);
		final StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.ALTER_TABLE_CLAUSE);
		sql.append(" ");
		sql.append(DialectUtils.shapeQualifiableIdentifier(tableName, qualifier, prefs, dialect));
		sql.append("\n");

		sql.append(" ");
		sql.append(DialectUtils.ADD_CONSTRAINT_CLAUSE);
		sql.append(" ");
		if (prefs.isQuoteConstraintNames())
		{
			sql.append(DialectUtils.shapeIdentifier(constraintName, prefs, dialect));
		}
		else
		{
			sql.append(constraintName);
		}

		sql.append(" ");
		sql.append(DialectUtils.UNIQUE_CLAUSE);
		sql.append(" (");
		for (final TableColumnInfo column : columns)
		{
			if (prefs.isQuoteColumnNames())
			{
				sql.append(DialectUtils.shapeIdentifier(column.getColumnName(), prefs, dialect));
			}
			else
			{
				sql.append(column.getColumnName());
			}
			sql.append(", ");
		}
		sql.delete(sql.length() - 2, sql.length()); // deletes the last ", "
		sql.append(")");

		return sql.toString();
	}

	/**
	 * @param st
	 * @param valuesMap
	 * @param columns
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return
	 */
	public static String getAddUniqueConstraintSQL(StringTemplate st, HashMap<String, String> valuesMap,
		TableColumnInfo[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs,
		HibernateDialect dialect)
	{
		bindAttributes(dialect, st, valuesMap, qualifier, prefs);
		for (final TableColumnInfo column : columns)
		{
			bindAttribute(dialect, st, ST_COLUMN_NAME_KEY, column.getColumnName(), qualifier, prefs);
		}
		return st.toString();
	}

	/**
	 * Returns: (column1, column2, ...)
	 * 
	 * @param colInfos
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect representing the target database.
	 * @return
	 */
	private static String getColumnList(TableColumnInfo[] colInfos, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		final StringBuilder result = new StringBuilder();
		result.append("(");
		for (int i = 0; i < colInfos.length; i++)
		{
			String shapedColumnName = colInfos[i].getColumnName();
			if (prefs.isQuoteColumnNames())
			{
				shapedColumnName = shapeIdentifier(colInfos[i].getColumnName(), prefs, dialect);
			}
			result.append(shapedColumnName);
			if (i + 1 < colInfos.length)
			{
				result.append(", ");
			}
		}
		result.append(")");
		return result.toString();
	}

	/**
	 * Returns the SQL that is used to change the column name. <code>
	 * For example:
	 * 
	 * ALTER TABLE table_name [alterClause] column_name [renameToClause] column_name
	 * </code>
	 * 
	 * @param from
	 *           the TableColumnInfo as it is
	 * @param to
	 *           the TableColumnInfo as it wants to be
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect representing the target database.
	 * @return the SQL to make the change
	 */
	public static String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to, String alterClause,
		String renameToClause, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs,
		HibernateDialect dialect)
	{
		final String shapedTable = shapeQualifiableIdentifier(from.getTableName(), qualifier, prefs, dialect);
		String shapedFromColumn = from.getColumnName();
		if (prefs.isQuoteColumnNames())
		{
			shapedFromColumn = shapeIdentifier(from.getColumnName(), prefs, dialect);
		}
		String shapedToColumn = to.getColumnName();
		if (prefs.isQuoteColumnNames())
		{
			shapedToColumn = shapeIdentifier(to.getColumnName(), prefs, dialect);
		}

		final StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(shapedTable);
		result.append(" ");
		result.append(alterClause);
		result.append(" ");
		result.append(shapedFromColumn);
		result.append(" ");
		result.append(renameToClause);
		result.append(" ");
		result.append(shapedToColumn);
		return result.toString();
	}

	/**
	 * Returns the SQL command to change the specified column's default value. <code>
	 * For example: 
	 * 
	 * ALTER TABLE table_name ALTER COLUMN column_name [defaultClause] 'defaultVal'
	 *  
	 * or
	 *  
	 * ALTER TABLE table_name ALTER COLUMN column_name [defaultClause] 1234
	 * </code>
	 * 
	 * @param dialect
	 *           the HibernateDialect representing the target database.
	 * @param info
	 *           the column to modify and it's default value.
	 * @param alterClause
	 *           alter phrase that precedes the column name (e.g. ALTER COLUMN)
	 * @param specifyType
	 *           whether or not to specify the column type
	 * @param defaultClause
	 *           alter phrase that precedes the default value (e.g. SET DEFAULT)
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return SQL to make the change
	 */
	public static String getColumnDefaultAlterSQL(HibernateDialect dialect, TableColumnInfo info,
		String alterClause, boolean specifyType, String defaultClause, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{

		final StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(shapeQualifiableIdentifier(info.getTableName(), qualifier, prefs, dialect));
		result.append(" ");
		result.append(alterClause);
		result.append(" ");
		if (prefs.isQuoteColumnNames())
		{
			result.append(shapeIdentifier(info.getColumnName(), prefs, dialect));
		}
		else
		{
			result.append(info.getColumnName());
		}
		result.append(" ");
		if (specifyType)
		{
			result.append(getTypeName(info, dialect));
		}
		result.append(" ");
		result.append(defaultClause);
		result.append(" ");
		if (JDBCTypeMapper.isNumberType(info.getDataType()))
		{
			result.append(info.getDefaultValue());
		}
		else
		{
			result.append("'");
			result.append(info.getDefaultValue());
			result.append("'");
		}
		return result.toString();
	}

	/**
	 * Returns the SQL that is used to change the column type. For example: <code>
	 * ALTER TABLE table_name alter_clause column_name [setClause] data_type 
	 * </code> or <code>
	 * ALTER TABLE table_name alter_clause column_name column_name [setClause] data_type
	 * </code>
	 * 
	 * @param from
	 *           the TableColumnInfo as it is
	 * @param to
	 *           the TableColumnInfo as it wants to be
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return the SQL to make the change
	 * @throw UnsupportedOperationException if the database doesn't support modifying column types.
	 */
	public static List<String> getColumnTypeAlterSQL(HibernateDialect dialect, String alterClause,
		String setClause, boolean repeatColumn, TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) throws UnsupportedOperationException
	{
		final String shapedTable = shapeQualifiableIdentifier(to.getTableName(), qualifier, prefs, dialect);

		String shapedToColumn = to.getColumnName();
		if (prefs.isQuoteColumnNames())
		{
			shapedToColumn = shapeIdentifier(to.getColumnName(), prefs, dialect);
		}

		final ArrayList<String> list = new ArrayList<String>();
		final StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(shapedTable);
		result.append(" ");
		result.append(alterClause);
		result.append(" ");
		if (repeatColumn)
		{
			result.append(shapedToColumn);
			result.append(" ");
		}
		result.append(shapedToColumn);
		result.append(" ");
		if (setClause != null && !"".equals(setClause))
		{
			result.append(setClause);
			result.append(" ");
		}
		result.append(getTypeName(to, dialect));
		list.add(result.toString());
		return list;
	}

	/**
	 * Returns the SQL that is used to change the column name. For example: <code>
	 * RENAME COLUMN table_name.column_name TO new_column_name
	 * </code>
	 * 
	 * @param from
	 *           the TableColumnInfo as it is
	 * @param to
	 *           the TableColumnInfo as it wants to be
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect representing the target database.
	 * @return the SQL to make the change
	 */
	public static String getColumnRenameSQL(TableColumnInfo from, TableColumnInfo to,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		final StringBuilder result = new StringBuilder();

		final String shapedTable = shapeQualifiableIdentifier(from.getTableName(), qualifier, prefs, dialect);
		String shapedFromColumn = from.getColumnName();
		if (prefs.isQuoteColumnNames())
		{
			shapedFromColumn = shapeIdentifier(from.getColumnName(), prefs, dialect);
		}
		String shapedToColumn = to.getColumnName();
		if (prefs.isQuoteColumnNames())
		{
			shapedToColumn = shapeIdentifier(to.getColumnName(), prefs, dialect);
		}

		result.append("RENAME COLUMN ");
		result.append(shapedTable);
		result.append(".");
		result.append(shapedFromColumn);
		result.append(" TO ");
		result.append(shapedToColumn);
		return result.toString();
	}

	/**
	 * @param dialect
	 *           the HibernateDialect representing the target database.
	 * @param featureId
	 * @return
	 * @throws UnsupportedOperationException
	 */
	public static String getUnsupportedMessage(HibernateDialect dialect, int featureId)
		throws UnsupportedOperationException
	{
		String msg = null;
		switch (featureId)
		{
		case COLUMN_COMMENT_ALTER_TYPE:
			// i18n[DialectUtils.columnCommentUnsupported={0} doesn''t support
			// column comments]
			msg = s_stringMgr.getString("DialectUtils.columnCommentUnsupported", dialect.getDisplayName());
			break;
		case COLUMN_DEFAULT_ALTER_TYPE:
			// i18n[DialectUtils.columnDefaultUnsupported={0} doesn''t support
			// altering a column''s default
			// value]
			msg = s_stringMgr.getString("DialectUtils.columnDefaultUnsupported", dialect.getDisplayName());
			break;

		case COLUMN_DROP_TYPE:
			// i18n[DialectUtils.columnDropUnsupported={0} doesn''t support
			// dropping a column]
			msg = s_stringMgr.getString("DialectUtils.columnDropUnsupported", dialect.getDisplayName());
			break;
		case COLUMN_NAME_ALTER_TYPE:
			// i18n[DialectUtils.columnNameUnsupported={0} doesn''t support
			// altering a column''s name]
			msg = s_stringMgr.getString("DialectUtils.columnNameUnsupported", dialect.getDisplayName());
			break;
		case COLUMN_NULL_ALTER_TYPE:
			// i18n[DialectUtils.columnNullUnsupported={0} doesn''t support
			// altering a column's nullable
			// attribute]
			msg = s_stringMgr.getString("DialectUtils.columnNullUnsupported", dialect.getDisplayName());
			break;
		case COLUMN_TYPE_ALTER_TYPE:
			// i18n[DialectUtils.columnTypeUnsupported={0} doesn''t support
			// altering a column's type attribute]
			msg = s_stringMgr.getString("DialectUtils.columnTypeUnsupported", dialect.getDisplayName());
			break;
		case ADD_PRIMARY_KEY_TYPE:
			// i18n[DialectUtils.addPrimaryKeyUnsupported={0} doesn''t support
			// adding primary keys]
			msg = s_stringMgr.getString("DialectUtils.addPrimaryKeyUnsupported", dialect.getDisplayName());
			break;
		case DROP_PRIMARY_KEY_TYPE:
			// i18n[DialectUtils.dropPrimaryKeyUnsupported={0} doesn''t support
			// dropping primary keys]
			msg = s_stringMgr.getString("DialectUtils.dropPrimaryKeyUnsupported", dialect.getDisplayName());
			break;
		case CREATE_TABLE_TYPE:
			return s_stringMgr.getString("DialectUtils.createTableUnsupported", dialect.getDisplayName());
		case RENAME_TABLE_TYPE:
			return s_stringMgr.getString("DialectUtils.renameTableUnsupported", dialect.getDisplayName());
		case CREATE_VIEW_TYPE:
			return s_stringMgr.getString("DialectUtils.createViewUnsupported", dialect.getDisplayName());
		case RENAME_VIEW_TYPE:
			return s_stringMgr.getString("DialectUtils.renameViewUnsupported", dialect.getDisplayName());
		case DROP_VIEW_TYPE:
			return s_stringMgr.getString("DialectUtils.dropViewUnsupported", dialect.getDisplayName());
		case CREATE_INDEX_TYPE:
			return s_stringMgr.getString("DialectUtils.createIndexUnsupported", dialect.getDisplayName());
		case DROP_INDEX_TYPE:
			return s_stringMgr.getString("DialectUtils.dropIndexUnsupported", dialect.getDisplayName());
		case CREATE_SEQUENCE_TYPE:
			return s_stringMgr.getString("DialectUtils.createSequenceUnsupported", dialect.getDisplayName());
		case ALTER_SEQUENCE_TYPE:
			return s_stringMgr.getString("DialectUtils.alterSequenceUnsupported", dialect.getDisplayName());
		case SEQUENCE_INFORMATION_TYPE:
			return s_stringMgr.getString("DialectUtils.sequenceInformationUnsupported", dialect.getDisplayName());
		case DROP_SEQUENCE_TYPE:
			return s_stringMgr.getString("DialectUtils.dropSequenceUnsupported", dialect.getDisplayName());
		case ADD_FOREIGN_KEY_TYPE:
			return s_stringMgr.getString("DialectUtils.addForeignKeyUnsupported", dialect.getDisplayName());
		case ADD_UNIQUE_TYPE:
			return s_stringMgr.getString("DialectUtils.addUniqueUnsupported", dialect.getDisplayName());
		case ADD_AUTO_INCREMENT_TYPE:
			return s_stringMgr.getString("DialectUtils.addAutoIncrementUnsupported", dialect.getDisplayName());
		case DROP_CONSTRAINT_TYPE:
			return s_stringMgr.getString("DialectUtils.dropConstraintUnsupported", dialect.getDisplayName());
		case INSERT_INTO_TYPE:
			return s_stringMgr.getString("DialectUtils.insertIntoUnsupported", dialect.getDisplayName());
		case UPDATE_TYPE:
			return s_stringMgr.getString("DialectUtils.updateUnsupported", dialect.getDisplayName());
		case VIEW_DEFINITION_TYPE:
			return s_stringMgr.getString("DialectUtils.viewDefinitionUnsupported", dialect.getDisplayName());
		case ADD_COLUMN_TYPE:
			return s_stringMgr.getString("DialectUtils.addColumnUnsupported", dialect.getDisplayName());

		default:
			throw new IllegalArgumentException("Unknown featureId: " + featureId);
		}
		return msg;
	}

	/**
	 * Returns the SQL command to drop the specified table's primary key. For example: <code>
	 * alter table table_name drop primary key 
	 * </code> or <code>
	 * alter table table_name drop constraint [pkName]
	 * </code>
	 * 
	 * @param pkName
	 *           the name of the primary key that should be dropped
	 * @param tableName
	 *           the name of the table whose primary key should be dropped
	 * @param useConstraintName
	 *           if true, the constraint name is used - like 'DROP CONSTRAINT pkName'; otherwise a generic
	 *           'DROP PRIMARY KEY' is used instead.
	 * @param cascadeConstraints
	 *           whether or not to append 'CASCADE' to the end.
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect representing the target database.
	 * @return
	 */
	public static String getDropPrimaryKeySQL(String pkName, String tableName, boolean useConstraintName,
		boolean cascadeConstraints, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs,
		HibernateDialect dialect)
	{
		final StringBuilder result = new StringBuilder();
		result.append("ALTER TABLE ");
		result.append(shapeQualifiableIdentifier(tableName, qualifier, prefs, dialect));
		if (useConstraintName)
		{
			result.append(" DROP CONSTRAINT ");
			if (prefs.isQuoteConstraintNames())
			{
				result.append(shapeIdentifier(pkName, prefs, dialect));
			}
			else
			{
				result.append(pkName);
			}
		}
		else
		{
			result.append(" DROP PRIMARY KEY");
		}
		if (cascadeConstraints)
		{
			result.append(" CASCADE");
		}
		return result.toString();
	}

	/**
	 * Gets the SQL command to drop an index. As follows: DROP INDEX indexName [ CASCADE ];
	 * 
	 * @param indexName
	 *           name of the index
	 * @param cascade
	 *           true if automatically drop object that depend on the view (such as other views).
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect to use for identifier quoting behavior
	 * @return the sql command to drop an index.
	 */
	public static String getDropIndexSQL(String indexName, Boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		// DROP INDEX indexName [ CASCADE ];
		return getDropIndexSQL(null, indexName, cascade, qualifier, prefs, dialect);
	}

	/**
	 * Gets the SQL command to drop an index. As follows: DROP INDEX indexName [ CASCADE ] [ ON <tableName> ];
	 * 
	 * @param indexName
	 *           name of the index
	 * @param cascade
	 *           true if automatically drop object that depend on the view (such as other views).
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect to use for identifier quoting behavior
	 * @return the sql command to drop an index.
	 */
	public static String getDropIndexSQL(String tableName, String indexName, Boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		// DROP INDEX indexName [ CASCADE ] [ ON <tableName> ];
		final StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.DROP_INDEX_CLAUSE);
		sql.append(" ");
		sql.append(shapeQualifiableIdentifier(indexName, qualifier, prefs, dialect)).append(" ");
		if (cascade != null)
		{
			sql.append(cascade ? DialectUtils.CASCADE_CLAUSE : DialectUtils.RESTRICT_CLAUSE);
		}
		if (tableName != null)
		{
			sql.append(" ON ");
			sql.append(shapeQualifiableIdentifier(tableName, qualifier, prefs, dialect));
		}
		return sql.toString();
	}

	/**
	 * Gets the SQL command to drop a sequence.
	 * 
	 * @param sequenceName
	 *           name of the sequence
	 * @param cascade
	 *           true if automatically drop object that depend on the view (such as other views).
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect to generate the SQL for.
	 * @return the sql command to drop a sequence.
	 */
	public static String getDropSequenceSQL(String sequenceName, Boolean cascade,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		// DROP SEQUENCE sequenceName CASCADE;
		final StringBuilder sql = new StringBuilder();

		sql.append("DROP SEQUENCE ");
		sql.append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs, dialect));
		if (cascade != null)
		{
			sql.append(" ");
			sql.append(cascade ? "CASCADE" : "RESTRICT");
		}

		return sql.toString();

	}

	public static String getDropConstraintSQL(String tableName, String constraintName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		// ALTER TABLE tableName
		// DROP CONSTRAINT constraintName
		final StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.ALTER_TABLE_CLAUSE + " ");
		sql.append(shapeQualifiableIdentifier(tableName, qualifier, prefs, dialect)).append("\n");

		sql.append(" " + DialectUtils.DROP_CONSTRAINT_CLAUSE + " ");
		if (prefs.isQuoteConstraintNames())
		{
			sql.append(shapeIdentifier(constraintName, prefs, dialect));
		}
		else
		{
			sql.append(constraintName);
		}
		return sql.toString();
	}

	/**
	 * Gets the SQL command to drop a view.
	 * 
	 * @param viewName
	 *           name of the view
	 * @param cascade
	 *           cascade true if automatically drop object that depend on the view (such as other views).
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect to generate the SQL for.
	 * @return the SQL command to drop a view.
	 */
	public static String getDropViewSQL(String viewName, Boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		// DROP VIEW viewName CASCADE;
		final StringBuffer sql = new StringBuffer();

		sql.append(DialectUtils.DROP_VIEW_CLAUSE + " ");
		sql.append(shapeQualifiableIdentifier(viewName, qualifier, prefs, dialect));
		if (cascade != null)
		{
			sql.append(" ");
			sql.append(cascade ? DialectUtils.CASCADE_CLAUSE : DialectUtils.RESTRICT_CLAUSE);
		}
		return sql.toString();
	}

	/**
	 * Returns SQL that can be executed to create the specified index. Like: CREATE UNIQUE INDEX indexName ON
	 * tableName (columns);
	 * 
	 * @param indexName
	 *           the name of the index
	 * @param columns
	 *           the columns that are indexed
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect to generate the SQL for.
	 * @return
	 */
	public static String getAddIndexSQL(String indexName, boolean unique, TableColumnInfo[] columns,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		final StringBuilder result = new StringBuilder();

		final String shapedTable =
			shapeQualifiableIdentifier(columns[0].getTableName(), qualifier, prefs, dialect);
		String shapedIndexName = indexName;
		if (prefs.isQuoteConstraintNames())
		{
			shapedIndexName = shapeIdentifier(indexName, prefs, dialect);
		}

		if (unique)
		{
			result.append("CREATE UNIQUE INDEX ");
		}
		else
		{
			result.append("CREATE INDEX ");
		}
		result.append(shapedIndexName);
		result.append(" ON ");
		result.append(shapedTable);
		result.append(" ");
		result.append(getColumnList(columns, qualifier, prefs, dialect));
		return result.toString();
	}

	/**
	 * Constructs the SQL for adding an index, as follows: CREATE UNIQUE INDEX indexName ON tableName USING
	 * btree (column1, column2) TABLESPACE <tableSpace> WHERE constraints;
	 * 
	 * @param indexName
	 *           name of the index to be created
	 * @param tableName
	 *           name of the table
	 * @param columns
	 *           columns where the index should be stored for
	 * @param unique
	 *           true if the index should be unique
	 * @param accessMethod
	 *           the index access method to use (for example, b-tree, r-tree, hash, etc.)
	 * @param tablespace
	 *           tablespace for the index (leave empty for no tablespace)
	 * @param constraints
	 *           constraints for the index (leave empty for no constraints)
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return the sql command to create an index.
	 */
	public static String getAddIndexSQL(HibernateDialect dialect, String indexName, String tableName,
		String accessMethod, String[] columns, boolean unique, String tablespace, String constraints,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		// CREATE UNIQUE INDEX indexName ON tableName USING btree (column1,
		// column2) TABLESPACE
		// WHERE constraints;
		final StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.CREATE_CLAUSE + " ");
		if (unique)
		{
			sql.append(DialectUtils.UNIQUE_CLAUSE + " ");
		}
		sql.append(DialectUtils.INDEX_CLAUSE + " ");
		sql.append(shapeIdentifier(indexName, prefs, dialect));
		sql.append(" ON ").append(shapeQualifiableIdentifier(tableName, qualifier, prefs, dialect)).append(" ");
		if (accessMethod != null && !"".equals(accessMethod))
		{
			sql.append(" USING ");
			sql.append(accessMethod);
			sql.append(" ");
		}
		sql.append("(");
		for (final String column : columns)
		{
			sql.append(shapeIdentifier(column, prefs, dialect)).append(", ");
		}
		sql.delete(sql.length() - 2, sql.length()); // deletes the last ", "
		sql.append(")");

		if (tablespace != null && !tablespace.equals(""))
		{
			sql.append(" \n TABLESPACE ").append(tablespace);
		}

		if (constraints != null && !constraints.equals(""))
		{
			sql.append(" \n " + DialectUtils.WHERE_CLAUSE + " ").append(constraints);
		}

		return sql.toString();
	}

	/**
	 * @param dialect
	 * @param st
	 * @param valuesMap
	 * @param columns
	 * @param qualifier
	 * @param prefs
	 * @return
	 */
	public static String getAddIndexSQL(HibernateDialect dialect, StringTemplate st,
		HashMap<String, String> valuesMap, String[] columns, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		bindAttributes(dialect, st, valuesMap, qualifier, prefs);
		for (final String columnName : columns)
		{
			bindAttribute(dialect, st, ST_COLUMN_NAME_KEY, columnName, qualifier, prefs);
		}
		return st.toString();
	}

	public static TableColumnInfo getRenamedColumn(TableColumnInfo info, String newColumnName)
	{
		final TableColumnInfo result =
			new TableColumnInfo(info.getCatalogName(), info.getSchemaName(), info.getTableName(), newColumnName,
				info.getDataType(), info.getTypeName(), info.getColumnSize(), info.getDecimalDigits(),
				info.getRadix(), info.isNullAllowed(), info.getRemarks(), info.getDefaultValue(),
				info.getOctetLength(), info.getOrdinalPosition(), info.isNullable());
		return result;
	}

	/**
	 * Gets the SQL command to rename a table.
	 * 
	 * @param oldTableName
	 *           old name of the table
	 * @param newTableName
	 *           new name of the table
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect to generate the SQL for.
	 * @return the sql command to rename a table.
	 */
	public static String getRenameTableSQL(String oldTableName, String newTableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		// ALTER TABLE oldTableName RENAME TO newTableName;
		final StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.ALTER_TABLE_CLAUSE);
		sql.append(" ");
		sql.append(shapeQualifiableIdentifier(oldTableName, qualifier, prefs, dialect));
		sql.append(" RENAME TO ");
		sql.append(shapeQualifiableIdentifier(newTableName, qualifier, prefs, dialect));

		return sql.toString();
	}

	/**
	 * Gets the SQL command to rename a view. Looks like <commandPrefix> <oldViewName> <renameClause>
	 * <newViewName>
	 * 
	 * @param commandPrefix
	 *           the beginning of the command (ALTER VIEW / RENAME )
	 * @param renameClause
	 *           the part between the old and new names.
	 * @param oldViewName
	 *           old name of the view
	 * @param newViewName
	 *           new name of the view
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect to generate the SQL for.
	 * @return the sql command
	 */
	public static String getRenameViewSQL(String commandPrefix, String renameClause, String oldViewName,
		String newViewName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs,
		HibernateDialect dialect)
	{
		// ALTER VIEW oldTableName RENAME TO newTableName;
		// RENAME oldTableName TO newTableName;

		final StringBuilder sql = new StringBuilder();

		sql.append(commandPrefix);
		sql.append(" ");
		sql.append(shapeQualifiableIdentifier(oldViewName, qualifier, prefs, dialect)).append(" ");
		sql.append(renameClause);
		sql.append(" ");
		sql.append(shapeIdentifier(newViewName, prefs, dialect));

		return sql.toString();
	}

	public static String getRenameViewSql(StringTemplate st, HashMap<String, String> valuesMap,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		bindAttributes(dialect, st, valuesMap, qualifier, prefs);
		return st.toString();
	}

	/**
	 * Returns the SQL command to drop the specified table's foreign key constraint.
	 * 
	 * @param fkName
	 *           the name of the foreign key that should be dropped
	 * @param tableName
	 *           the name of the table whose foreign key should be dropped
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect to generate the SQL for.
	 * @return
	 */
	public static String getDropForeignKeySQL(String fkName, String tableName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		final StringBuilder tmp = new StringBuilder();
		tmp.append("ALTER TABLE ");
		tmp.append(shapeQualifiableIdentifier(tableName, qualifier, prefs, dialect));
		tmp.append(" DROP CONSTRAINT ");
		tmp.append(shapeIdentifier(fkName, prefs, dialect));
		return tmp.toString();
	}

	/**
	 * Gets the SQL command to create a new table.
	 * 
	 * @param tableName
	 *           simple name of the table
	 * @param columns
	 *           columns of the table
	 * @param primaryKeys
	 *           primary keys of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param qualifier
	 *           qualifier of the table
	 * @return the sql command to create a table.
	 */
	public static String getCreateTableSQL(String simpleName, List<TableColumnInfo> columns,
		List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier,
		HibernateDialect dialect)
	{
		if (columns.isEmpty() && !dialect.supportsEmptyTables()) { throw new IllegalArgumentException(
			dialect.getDisplayName()
				+ " does not support empty tables. (parameter 'columns' has to contain at least one column)"); }

		// CREATE TABLE tableName (
		// column1 int,
		// column2 varchar(20) NOT NULL DEFAULT 'Hello World'
		// CONSTRAINT tableName_pkey PRIMARY KEY(column1,column2)
		// );
		final StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.CREATE_TABLE_CLAUSE + " ");
		sql.append(shapeQualifiableIdentifier(simpleName, qualifier, prefs, dialect)).append(" (\n");
		for (final TableColumnInfo column : columns)
		{
			sql.append(" ");
			if (prefs.isQuoteColumnNames())
			{
				sql.append(shapeIdentifier(column.getColumnName(), prefs, dialect));
			}
			else
			{
				sql.append(column.getColumnName());
			}
			sql.append(" ");
			sql.append(dialect.getTypeName(column.getDataType(), column.getColumnSize(), column.getColumnSize(),
				column.getDecimalDigits()));

			if (primaryKeys != null && primaryKeys.size() == 1
				&& primaryKeys.get(0).getColumnName().equals(column.getColumnName()))
			{
				sql.append(" " + DialectUtils.PRIMARY_KEY_CLAUSE);
			}
			else if (column.isNullAllowed() == 0)
			{
				sql.append(" " + DialectUtils.NOT_NULL_CLAUSE);
			}
			if (column.getDefaultValue() != null)
			{
				sql.append(" " + DialectUtils.DEFAULT_CLAUSE + " ").append(column.getDefaultValue());
			}

			sql.append(",\n");
		}

		if (primaryKeys != null && primaryKeys.size() > 1)
		{
			sql.append(" " + DialectUtils.CONSTRAINT_CLAUSE + " ")
				.append(shapeIdentifier(simpleName + "_pkey", prefs, dialect))
				.append(" " + DialectUtils.PRIMARY_KEY_CLAUSE + "(");
			for (final TableColumnInfo pkPart : primaryKeys)
			{
				sql.append(shapeIdentifier(pkPart.getColumnName(), prefs, dialect)).append(",");
			}
			sql.setLength(sql.length() - 1);
			sql.append(")");
		}
		else
		{
			sql.setLength(sql.length() - 2);
		}

		sql.append(")");
		return sql.toString();

	}

	public static List<String> getCreateTableSQL(List<ITableInfo> tables, ISQLDatabaseMetaData md,
		HibernateDialect dialect, CreateScriptPreferences prefs, boolean isJdbcOdbc) throws SQLException
	{
		return getCreateTableSQL(tables, md, dialect, prefs, isJdbcOdbc, false);
	}

	public static List<String> getCreateTableSQL(List<ITableInfo> tables, ISQLDatabaseMetaData md,
		HibernateDialect dialect, CreateScriptPreferences prefs, boolean isJdbcOdbc, boolean sortColumns)
		throws SQLException
	{
		final List<String> sqls = new ArrayList<String>();
		final List<String> allconstraints = new ArrayList<String>();

		for (final ITableInfo ti : tables)
		{
			final StringBuilder result = new StringBuilder();
			result.append("CREATE TABLE ");
			result.append(formatQualifIntern(ti.getSimpleName(), ti.getSchemaName(), prefs));
			result.append("\n(");

			final List<PrimaryKeyInfo> pkInfos = getPrimaryKeyInfo(md, ti, isJdbcOdbc);
			final List<String> pks = getPKSequenceList(pkInfos);
			final TableColumnInfo[] infos = md.getColumnInfo(ti);
			if (sortColumns)
			{
				Arrays.sort(infos);
			}
			for (final TableColumnInfo tcInfo : infos)
			{
				final String columnName = tcInfo.getColumnName();
				final String defaultVal = tcInfo.getDefaultValue();
				final String columnType = dialect.getTypeName(tcInfo);

				result.append("\n   ");
				result.append(columnName);
				result.append(" ");
				result.append(columnType);
				final String isNullable = tcInfo.isNullable();
				if (pks.size() == 1 && pks.get(0).equals(columnName))
				{
					result.append(" PRIMARY KEY");
				}
				else
				{
					// in Sybase, DEFAULT keyword must appear prior to NULL/NOT
					// NULL
					if (defaultVal != null && !"".equals(defaultVal))
					{
						result.append(" DEFAULT ");
						result.append(defaultVal);
					}
				}

				if ("NO".equalsIgnoreCase(isNullable))
				{
					result.append(" NOT NULL");
				}
				result.append(",");
			}

			if (pks.size() > 1)
			{
				result.append("\n   CONSTRAINT ");
				result.append(pkInfos.get(0).getSimpleName());
				result.append(" PRIMARY KEY (");
				for (int i = 0; i < pks.size(); i++)
				{
					result.append(pks.get(i));
					result.append(",");
				}
				result.setLength(result.length() - 1);
				result.append("),");
			}
			result.setLength(result.length() - 1);

			result.append("\n)");
			sqls.add(result.toString());

			if (isJdbcOdbc)
			{
				continue;
			}

			final List<String> constraints = createConstraints(ti, tables, prefs, md);
			addConstraintsSQLs(sqls, allconstraints, constraints, prefs);

			final List<String> indexes = createIndexes(ti, ti.getSimpleName(), ti.getSchemaName(), md, pkInfos, prefs);
			addConstraintsSQLs(sqls, allconstraints, indexes, prefs);
		}

		if (prefs.isConstraintsAtEnd())
		{
			sqls.addAll(allconstraints);
		}
		return sqls;
	}

	/**
	 * Gets the SQL command to create an index.
	 * 
	 * @param indexName
	 *           name of the index to be created
	 * @param tableName
	 *           name of the table
	 * @param accessMethod
	 *           the index access method to use (for example, b-tree, r-tree, hash, etc.)
	 * @param columns
	 *           columns where the index should be stored for
	 * @param unique
	 *           true if the index should be unique
	 * @param tablespace
	 *           tablespace for the index (leave empty for no tablespace)
	 * @param constraints
	 *           constraints for the index (leave empty for no constraints)
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the hibernate dialect to use.
	 * @return the sql command to create an index.
	 */
	public static String getCreateIndexSQL(String indexName, String tableName, String accessMethod,
		String[] columns, boolean unique, String tablespace, String constraints,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		final StringBuilder result = new StringBuilder();
		result.append("CREATE ");

		if (unique)
		{
			result.append("UNIQUE ");
		}
		result.append(" INDEX ");
		result.append(DialectUtils.shapeQualifiableIdentifier(indexName, qualifier, prefs, dialect));
		result.append(" ON ");
		result.append(DialectUtils.shapeQualifiableIdentifier(tableName, qualifier, prefs, dialect));
		result.append("(");
		for (final String column : columns)
		{
			result.append(column);
			result.append(",");
		}
		result.setLength(result.length() - 1);
		result.append(")");
		return result.toString();
	}

	/**
	 * Gets the SQL command to create a view.
	 * 
	 * @param viewName
	 *           name of the view
	 * @param definition
	 *           old definition of the view.
	 * @param checkOption
	 *           CHECK OPTION. CASCADE, LOCAL or null for no check option.
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the HibernateDialect to use for identifier quoting behavior
	 * @return the sql command to create a view.
	 */
	public static String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		// CREATE VIEW viewName
		// AS definition;
		final StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.CREATE_VIEW_CLAUSE + " ")
			.append(shapeQualifiableIdentifier(viewName, qualifier, prefs, dialect))
			.append("\n");
		sql.append(" AS ").append(definition);
		if (dialect.supportsCheckOptionsForViews() && checkOption != null && !checkOption.equals(""))
		{
			sql.append("\n WITH ").append(checkOption).append(" CHECK OPTION");
		}

		return sql.toString();
	}

	/**
	 * Gets the SQL command to create a sequence.
	 * 
	 * @param sequenceName
	 *           name of the sequence
	 * @param increment
	 *           increment value
	 * @param minimumClause
	 *           MINVALUE, NO MINVALUE, NOMINVALUE
	 * @param minimum
	 *           minimum value
	 * @param maximumClause
	 *           MAXVALUE, NO MAXVALUE, NOMAXVALUE
	 * @param maximum
	 *           maximum value
	 * @param start
	 *           start value (leave empty for default)
	 * @param cache
	 *           cache value, how many sequences should be preallocated (leave empty for default)
	 * @param cycleClause
	 *           the cycle clause to use
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return the sql command to create a sequence.
	 */
	public static String getCreateSequenceSQL(String sequenceName, String increment, String minimumClause,
		String minimum, String maximumClause, String maximum, String start, String cache, String cycleClause,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		final StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.CREATE_SEQUENCE_CLAUSE).append(" ");
		sql.append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs, dialect)).append("\n");

		if (increment != null && !increment.equals(""))
		{
			sql.append("INCREMENT BY ");
			sql.append(increment);
			sql.append(" ");
		}

		if (minimum != null && !minimum.equals(""))
		{
			sql.append(minimumClause);
			sql.append(" ");
			sql.append(minimum);
			sql.append(" ");
		}
		else
		{
			sql.append(minimumClause);
			sql.append(" ");
		}

		if (maximum != null && !maximum.equals(""))
		{
			sql.append(maximumClause);
			sql.append(" ");
			sql.append(maximum);
			sql.append(" ");
		}
		else
		{
			sql.append(maximumClause);
		}
		sql.append("\n");

		if (start != null && !start.equals(""))
		{
			sql.append("START WITH ");
			sql.append(start).append(" ");
		}

		if (cache != null && !cache.equals(""))
		{
			sql.append("CACHE ");
			sql.append(cache).append(" ");
		}

		if (cycleClause != null)
		{
			sql.append(cycleClause);
		}

		return sql.toString();

	}

	/**
	 * @param st
	 * @param sequenceName
	 * @param increment
	 * @param minimum
	 * @param maximum
	 * @param start
	 * @param cache
	 * @param qualifier
	 * @param prefs
	 * @param dialect
	 * @return
	 */
	public static String getCreateSequenceSQL(StringTemplate st, HashMap<String, String> valuesMap,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		return bindTemplateAttributes(dialect, st, valuesMap, qualifier, prefs);
	}

	/**
	 * Gets the SQL command to create a sequence.
	 * 
	 * @param sequenceName
	 *           name of the sequence
	 * @param increment
	 *           increment value
	 * @param minimum
	 *           minimum value (leave empty for NO MINVALUE)
	 * @param maximum
	 *           maximum value (leave empty for NO MINVALUE)
	 * @param start
	 *           start value (leave empty for default)
	 * @param cache
	 *           cache value, how many sequences should be preallocated (leave empty for default)
	 * @param cycleClause
	 *           the cycle clause to use
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return the sql command to create a sequence.
	 */
	public static String getCreateSequenceSQL(String sequenceName, String increment, String minimum,
		String maximum, String start, String cache, String cycleClause, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		// CREATE SEQUENCE sequenceName
		// INCREMENT BY increment MINVALUE minimum MAXVALUE maxvalue
		// RESTART WITH restart CACHE cache CYCLE;

		String minimumClause = "";
		if (minimum != null && !minimum.equals(""))
		{
			minimumClause = DialectUtils.MINVALUE_CLAUSE;
		}
		else
		{

			minimumClause = DialectUtils.NO_MINVALUE_CLAUSE;
		}

		String maximumClause = "";
		if (maximum != null && !maximum.equals(""))
		{
			maximumClause = DialectUtils.MAXVALUE_CLAUSE;
		}
		else
		{
			maximumClause = DialectUtils.NO_MAXVALUE_CLAUSE;
		}

		return getCreateSequenceSQL(sequenceName, increment, minimumClause, minimum, maximumClause, maximum,
			start, cache, cycleClause, qualifier, prefs, dialect);

	}

	/**
	 * Gets the SQL command to alter a sequence.
	 * 
	 * @param sequenceName
	 *           name of the sequence.
	 * @param increment
	 *           increment value.
	 * @param minimum
	 *           minimum value.
	 * @param maximum
	 *           maximum value.
	 * @param restart
	 *           start value.
	 * @param cache
	 *           cache value, how many sequences should be preallocated.
	 * @param cycleClause
	 *           true if the sequence should wrap around when the max-/minvalue has been reached.
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return the sql command
	 */
	public static String getAlterSequenceSQL(String sequenceName, String increment, String minimum,
		String maximum, String restart, String cache, String cycleClause, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		// ALTER SEQUENCE sequenceName
		// INCREMENT BY increment MINVALUE minimum MAXVALUE maxvalue
		// RESTART WITH restart CACHE cache CYCLE;
		final StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.ALTER_SEQUENCE_CLAUSE + " ");
		sql.append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs, dialect)).append("\n");

		if (increment != null && !increment.equals(""))
		{
			sql.append("INCREMENT BY ");
			sql.append(increment).append(" ");
		}

		if (minimum != null && !minimum.equals(""))
		{
			sql.append("MINVALUE ");
			sql.append(minimum).append(" ");
		}
		else
		{
			sql.append("NO MINVALUE ");
		}

		if (maximum != null && !maximum.equals(""))
		{
			sql.append("MAXVALUE ");
			sql.append(maximum).append("\n");
		}
		else
		{
			sql.append("NO MAXVALUE\n");
		}

		if (restart != null && !restart.equals(""))
		{
			sql.append("RESTART WITH ");
			sql.append(restart).append(" ");
		}

		if (cache != null && !cache.equals(""))
		{
			sql.append("CACHE ");
			sql.append(cache).append(" ");
		}

		if (cycleClause != null)
		{
			sql.append(cycleClause);
		}

		return sql.toString();
	}

	/**
	 * Simulates alter statement for sequence for those dialects that have no "ALTER SEQUENCE ..." support.
	 * This will return a drop followed by a create.
	 * 
	 * @param sequenceName
	 *           name of the sequence.
	 * @param increment
	 *           increment value.
	 * @param minimum
	 *           minimum value.
	 * @param maximum
	 *           maximum value.
	 * @param restart
	 *           start value.
	 * @param cache
	 *           cache value, how many sequences should be preallocated.
	 * @param cycleClause
	 *           true if the sequence should wrap around when the max-/minvalue has been reached.
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @return the sql command
	 */
	public static String[] getSimulatedAlterSequenceSQL(String sequenceName, String increment, String minimum,
		String maximum, String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		final ArrayList<String> result = new ArrayList<String>();

		// Since dialect doesn't support altering sequences, drop then
		// re-create.
		result.add(dialect.getDropSequenceSQL(sequenceName, false, qualifier, prefs));
		result.add(dialect.getCreateSequenceSQL(sequenceName, increment, minimum, maximum, minimum, cache,
			cycle, qualifier, prefs));

		return result.toArray(new String[result.size()]);
	}

	/**
	 * Gets the SQL command to insert data into a table.
	 * <p/>
	 * If the list of columns is empty or null the SQL will look like: INSERT INTO tablename query;
	 * <p/>
	 * instead of: INSERT INTO tablename ( column1, column2, ... ) query;
	 * 
	 * @param tableName
	 *           simple name of the table
	 * @param columns
	 *           columns of the table
	 * @param query
	 *           the query that retrieves the values to insert
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 * @return
	 */
	public static String getInsertIntoSQL(String tableName, List<String> columns, String query,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		if (query == null || query.length() == 0) { return ""; }

		// INSERT INTO tableName (column1, column2)
		// query;
		final StringBuilder sql = new StringBuilder();

		sql.append(DialectUtils.INSERT_INTO_CLAUSE + " ");
		sql.append(shapeQualifiableIdentifier(tableName, qualifier, prefs, dialect));
		if (columns != null && !columns.isEmpty())
		{
			sql.append(" (");
			for (final String column : columns)
			{
				sql.append(shapeIdentifier(column, prefs, dialect)).append(", ");
			}
			sql.setLength(sql.length() - 2);
			sql.append(")");
		}
		sql.append("\n");

		sql.append(" ").append(query);

		return sql.toString();
	}

	/**
	 * Returns the update SQL for the specified table and values, using either a correlated sub-query (if
	 * fromTables != null) in one statement, or setValues (if fromTables == null) and returning an update SQL
	 * using each setValue.
	 * 
	 * @param st
	 *           The StringTemplate that provides the form for the SQL (some dbs allow table name aliases, some
	 *           don't, etc.)
	 * @param tableName
	 *           simple name of the table
	 * @param setColumns
	 *           columns to be set
	 * @param setValues
	 *           values the columns should be set with
	 * @param fromTables
	 *           simple names of the tables in the FROM clause
	 * @param whereColumns
	 *           columns in the WHERE clause
	 * @param whereValues
	 *           values of the columns in the WHERE clause
	 * @param qualifier
	 *           qualifier of the table
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           the dialect to use
	 * @return a String array of one or more SQL statements
	 */
	public static String[] getUpdateSQL(StringTemplate st, String destTableName, String[] setColumns,
		String[] setValues, String[] fromTables, String[] whereColumns, String[] whereValues,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		if ((setColumns == null && setValues == null)
			|| (setColumns != null && setValues != null && setColumns.length == 0 && setValues.length == 0)) { return new String[] {}; }
		if (fromTables == null
			&& ((setColumns != null && setValues != null && setColumns.length != setValues.length)
				|| setColumns == null || setValues == null)) { throw new IllegalArgumentException(
			"The amount of SET columns and values must be the same!"); }
		if ((whereColumns != null && whereValues != null && whereColumns.length != whereValues.length)
			|| (whereColumns == null && whereValues != null) || (whereColumns != null && whereValues == null)) { throw new IllegalArgumentException(
			"The amount of WHERE columns and values must be the same!"); }
		if (fromTables == null && setValues == null) { throw new IllegalArgumentException(
			"One of fromTables or setValues args must be non-null"); }

		// Since we can use a correlated sub-query to update all rows in one
		// statement, we don't care about the
		// set values, unless fromTables is null, in which case we go with a
		// normal update. Using the set
		// values would require an update statement for each row in the merged
		// table for each column that was
		// merged in, which is incredibly inefficient. However, the API is
		// intended to support database
		// dialects that can't handle correlated sub-queries.

		final ArrayList<String> result = new ArrayList<String>();
		// String templateStr = null;
		String columnName = null;
		String whereColumnName = null;
		String whereValueName = null;

		// if (fromTables != null) {
		// // update <destTableName> dest
		// // set <setColumnName> = (
		// // select s.<setColumnName>
		// // from <sourceTableName> f where f.<whereColumn> = s.<whereValue>)
		//
		// templateStr =
		// "UPDATE $destTableName$ dest SET $columnName$ = " +
		// "(SELECT src.$columnName$ " +
		// "FROM $sourceTableName$ src " +
		// "where src.$whereColumnName$ = dest.$whereValue$)";
		// } else {
		// // update <destTableName> dest
		// // set <setColumnName> = <setValue>
		// // where f.<whereColumn> = s.<whereValue>)
		//
		// templateStr =
		// "UPDATE $destTableName$ " +
		// "SET $columnName$ = $columnValue$ " +
		// "where $whereColumnName$ = $whereValue$";
		// }

		for (int idx = 0; idx < setColumns.length; idx++)
		{
			columnName = setColumns[idx]; // desc_t1

			whereColumnName = whereColumns[idx]; // myid
			whereValueName = whereValues[idx]; // myid
			// StringTemplate st = new StringTemplate(templateStr);

			st.setAttribute(ST_DEST_TABLE_NAME_KEY, destTableName);
			st.setAttribute(ST_COLUMN_NAME_KEY, columnName);
			if (fromTables != null)
			{
				st.setAttribute(ST_SOURCE_TABLE_NAME_KEY, fromTables[idx]);
			}
			else
			{
				st.setAttribute(ST_COLUMN_VALUE_KEY, setValues[idx]);
			}
			st.setAttribute(ST_WHERE_COLUMN_NAME_KEY, whereColumnName);
			st.setAttribute(ST_WHERE_VALUE_KEY, whereValueName);
			result.add(st.toString());
		}

		return result.toArray(new String[result.size()]);
	}

	private static void addConstraintsSQLs(List<String> sqls, List<String> allconstraints,
		List<String> sqlsToAdd, CreateScriptPreferences prefs)
	{
		if (sqlsToAdd.size() > 0)
		{
			if (prefs.isConstraintsAtEnd())
			{
				allconstraints.addAll(sqlsToAdd);
			}
			else
			{
				sqls.addAll(sqlsToAdd);
			}
		}
	}

	/**
	 * Get a list of statements needed to create indexes for the specified table
	 * 
	 * @param ti
	 * @param md
	 * @param primaryKeys
	 *           can be null
	 * @param prefs
	 * @return
	 */
	public static List<String> createIndexes(ITableInfo ti, String destSimpleTableName, String destSchema, ISQLDatabaseMetaData md,
                                            List<PrimaryKeyInfo> primaryKeys, CreateScriptPreferences prefs)
	{
		if (ti == null) { throw new IllegalArgumentException("ti cannot be null"); }
		if (md == null) { throw new IllegalArgumentException("md cannot be null"); }
		final List<String> result = new ArrayList<String>();
		if (ti.getDatabaseObjectType() == DatabaseObjectType.VIEW) { return result; }

		final List<IndexColInfo> pkCols = new ArrayList<IndexColInfo>();
		if (primaryKeys != null)
		{
			for (final PrimaryKeyInfo pkInfo : primaryKeys)
			{
				pkCols.add(new IndexColInfo(pkInfo.getColumnName()));
			}
			Collections.sort(pkCols, IndexColInfo.NAME_COMPARATOR);
		}

		List<IndexInfo> indexInfos = null;
		try
		{
			indexInfos = md.getIndexInfo(ti);
		}
		catch (final SQLException e)
		{
			// i18n[DialectUtils.error.getprimarykey=Unable to get primary key
			// info for table {0}]
			final String msg = s_stringMgr.getString("DialectUtils.error.getprimarykey", ti.getSimpleName());
			log.error(msg, e);
			return result;
		}

		// Group all columns by index
		final Hashtable<String, TableIndexInfo> buf = new Hashtable<String, TableIndexInfo>();
		for (final IndexInfo indexInfo : indexInfos)
		{
			final String indexName = indexInfo.getSimpleName();
			if (StringUtils.isEmpty(indexName))
			{
				continue;
			}
			final String columnName = indexInfo.getColumnName();
			if (StringUtils.isEmpty(columnName))
			{
				continue;
			}
			final TableIndexInfo ixi = buf.get(indexName);
			if (null == ixi)
			{
				final List<IndexColInfo> ixCols = new ArrayList<IndexColInfo>();

				ixCols.add(new IndexColInfo(columnName, indexInfo.getOrdinalPosition()));
				buf.put(indexName, new TableIndexInfo(indexInfo.getTableName(), indexInfo.getSchemaName(),
					indexName, ixCols, !indexInfo.isNonUnique()));
			}
			else
			{
				ixi.cols.add(new IndexColInfo(indexInfo.getColumnName(), indexInfo.getOrdinalPosition()));
			}
		}

		final TableIndexInfo[] ixs = buf.values().toArray(new TableIndexInfo[buf.size()]);
		for (final TableIndexInfo ix : ixs)
		{
			Collections.sort(ix.cols, IndexColInfo.NAME_COMPARATOR);

			if (pkCols.equals(ix.cols))
			{
				// Serveral DBs automatically create an index for primary key
				// fields
				// and return this index in getIndexInfo(). We remove this index
				// from the script
				// because it would break the script with an index already
				// exists error.
				continue;
			}

			Collections.sort(ix.cols, IndexColInfo.ORDINAL_POSITION_COMPARATOR);

			final StringBuilder indexSQL = new StringBuilder();
			indexSQL.append("CREATE");
			indexSQL.append(ix.unique ? " UNIQUE " : " ");
			indexSQL.append("INDEX ");
			indexSQL.append(ix.ixName);
			indexSQL.append(" ON ");

			indexSQL.append(formatQualifIntern(destSimpleTableName, destSchema, prefs));

			if (ix.cols.size() == 1)
			{
				indexSQL.append("(").append(ix.cols.get(0));

				for (int j = 1; j < ix.cols.size(); j++)
				{
					indexSQL.append(",").append(ix.cols.get(j));
				}
			}
			else
			{
				indexSQL.append("\n(\n");
				for (int j = 0; j < ix.cols.size(); j++)
				{
					indexSQL.append("  ");
					indexSQL.append(ix.cols.get(j));
					if (j < ix.cols.size() - 1)
					{
						indexSQL.append(",\n");
					}
					else
					{
						indexSQL.append("\n");
					}
				}
			}
			indexSQL.append(")");
			result.add(indexSQL.toString());
		}
		return result;
	}

	private static List<String> createConstraints(ITableInfo ti, List<ITableInfo> tables,
		CreateScriptPreferences prefs, ISQLDatabaseMetaData md) throws SQLException
	{

		final List<String> result = new ArrayList<String>();
		final StringBuffer sbToAppend = new StringBuffer();

		final ConstraintInfo[] cis = getConstraintInfos(ti, md);

		for (final ConstraintInfo ci : cis)
		{
			if (!prefs.isIncludeExternalReferences())
			{
				boolean found = false;
				for (final ITableInfo table : tables)
				{
					if (table.getSimpleName().equalsIgnoreCase(ci.pkTable))
					{
						found = true;
						break;
					}
				}
				if (false == found)
				{
					continue;
				}
			}

			sbToAppend.append("ALTER TABLE " + formatQualifIntern(ci.fkTable, ci.fkTableSchema, prefs) + "\n");
			sbToAppend.append("ADD CONSTRAINT " + ci.fkName + "\n");

			if (ci.fkCols.size() == 1)
			{
				sbToAppend.append("FOREIGN KEY (").append(ci.fkCols.get(0));

				for (int j = 1; j < ci.fkCols.size(); j++)
				{
					sbToAppend.append(",").append(ci.fkCols.get(j));
				}
				sbToAppend.append(")\n");

				sbToAppend.append("REFERENCES " + formatQualifIntern(ci.pkTable, ci.pkTableSchema, prefs) + "(");
				sbToAppend.append(ci.pkCols.get(0));
				for (int j = 1; j < ci.pkCols.size(); j++)
				{
					sbToAppend.append(",").append(ci.pkCols.get(j));
				}
			}
			else
			{
				sbToAppend.append("FOREIGN KEY\n");
				sbToAppend.append("(\n");
				for (int j = 0; j < ci.fkCols.size(); j++)
				{
					sbToAppend.append("  ");
					sbToAppend.append(ci.fkCols.get(j));
					if (j < ci.fkCols.size() - 1)
					{
						sbToAppend.append(",");
					}
					sbToAppend.append("\n");

				}
				sbToAppend.append(")\n");

				sbToAppend.append("REFERENCES ");
				sbToAppend.append(formatQualifIntern(ci.pkTable, ci.pkTableSchema, prefs));
				sbToAppend.append("\n");
				sbToAppend.append("(\n");
				for (int j = 0; j < ci.pkCols.size(); j++)
				{
					sbToAppend.append("  ");
					sbToAppend.append(ci.pkCols.get(j));
					if (j < ci.pkCols.size() - 1)
					{
						sbToAppend.append(",");
					}
					sbToAppend.append("\n");
				}
			}

			sbToAppend.append(")");

			boolean overrideUpdate = prefs.isDeleteRefAction();
			String conditionClause = " ON DELETE ";
			String overrideAction = prefs.getRefActionByType(prefs.getDeleteAction());
			int rule = ci.deleteRule;

			final String onDeleteClause =
				constructFKContraintActionClause(overrideUpdate, conditionClause, overrideAction, rule);

			sbToAppend.append(onDeleteClause);

			overrideUpdate = prefs.isUpdateRefAction();
			conditionClause = " ON UPDATE ";
			overrideAction = prefs.getRefActionByType(prefs.getUpdateAction());
			rule = ci.updateRule;

			final String onUpdateClause =
				constructFKContraintActionClause(overrideUpdate, conditionClause, overrideAction, rule);

			sbToAppend.append(onUpdateClause);
			// sbToAppend.append("\n");
			result.add(sbToAppend.toString());
			sbToAppend.setLength(0);
		}

		return result;
	}

	private static String formatQualifIntern(String table, String schema, CreateScriptPreferences prefs)
	{
		return formatQualified(table, schema, prefs.isQualifyTableNames(), prefs.isUseDoubleQuotes());
	}

	public static String formatQualified(String table, String schema, boolean qualifyTableNames,
		boolean useDoubleQuotes)
	{
		if (qualifyTableNames && null != schema && 0 < schema.trim().length())
		{
			if (useDoubleQuotes)
			{
				return "\"" + schema + "\".\"" + table + "\"";
			}
			else
			{
				return schema + "." + table;
			}
		}
		else
		{
			return table;
		}
	}

	private static String constructFKContraintActionClause(boolean override, String conditionClause,
		String overrideAction, int rule)
	{
		// Bug 2531193: Oracle create table script the "ON UPDATE" is wrong
		final StringBuilder tmp = new StringBuilder();
		if (override)
		{
			if ("NO ACTION".equals(overrideAction))
			{
				return "";
			}
			else
			{
				tmp.append(conditionClause);
				tmp.append(overrideAction);
				return tmp.toString();
			}
		}

		switch (rule)
		{
		case DatabaseMetaData.importedKeyCascade:
			tmp.append(conditionClause);
			if (override)
			{
				tmp.append(overrideAction);
			}
			else
			{
				tmp.append("CASCADE");
			}
			break;
		case DatabaseMetaData.importedKeySetNull:
			if (override)
			{
				tmp.append(overrideAction);
			}
			else
			{
				tmp.append("SET NULL");
			}
			break;
		case DatabaseMetaData.importedKeySetDefault:
			if (override)
			{
				tmp.append(overrideAction);
			}
			else
			{
				tmp.append("SET DEFAULT");
			}
			break;
		case DatabaseMetaData.importedKeyRestrict:
		case DatabaseMetaData.importedKeyNoAction:
		default:
			// Append nothing - standard says this is equivalent to NO ACTION
			// and some DBs
			// (e.g. Oracle don't accept ... NO ACTION)
		}
		return tmp.toString();
	}

	private static ConstraintInfo[] getConstraintInfos(ITableInfo ti, ISQLDatabaseMetaData md)
		throws SQLException
	{
		final ArrayList<ConstraintInfo> ret = new ArrayList<ConstraintInfo>();
		final ForeignKeyInfo[] fkinfos = md.getImportedKeysInfo(ti);
		for (final ForeignKeyInfo fkinfo : fkinfos)
		{
			final Vector<String> fkCols = new Vector<String>();
			final Vector<String> pkCols = new Vector<String>();

			for (final ForeignKeyColumnInfo fkCol : fkinfo.getForeignKeyColumnInfo())
			{
				fkCols.add(fkCol.getForeignKeyColumnName());
				pkCols.add(fkCol.getPrimaryKeyColumnName());
			}

			final ConstraintInfo ci =
				new ConstraintInfo(fkinfo.getForeignKeyTableName(), fkinfo.getForeignKeySchemaName(),
					fkinfo.getPrimaryKeyTableName(), fkinfo.getPrimaryKeySchemaName(), fkinfo.getSimpleName(),
					fkCols, pkCols, (short) fkinfo.getDeleteRule(), (short) fkinfo.getUpdateRule());

			ret.add(ci);

		}
		return ret.toArray(new ConstraintInfo[ret.size()]);
	}

	private static List<PrimaryKeyInfo> getPrimaryKeyInfo(ISQLDatabaseMetaData md, ITableInfo ti,
		boolean isJdbcOdbc)
	{
		List<PrimaryKeyInfo> result = new ArrayList<PrimaryKeyInfo>();
		if (isJdbcOdbc) { return result; }
		try
		{
			result = Arrays.asList(md.getPrimaryKey(ti));
		}
		catch (final SQLException e)
		{
			// i18n[CreateTableScriptCommand.error.getprimarykey=Unable to get
			// primary key info for table {0}]
			final String msg = s_stringMgr.getString("DialectUtils.error.getprimarykey", ti.getSimpleName());
			log.error(msg, e);
		}
		return result;
	}

	private static List<String> getPKSequenceList(List<PrimaryKeyInfo> infos)
	{
		final String[] result = new String[infos.size()];
		for (final PrimaryKeyInfo info : infos)
		{
			final int iKeySeq = info.getKeySequence() - 1;
			result[iKeySeq] = info.getColumnName();
		}
		return Arrays.asList(result);
	}

	/**
	 * Shapes the table name depending on the prefereneces. If isQualifyTableNames is true, the qualified name
	 * of the table is returned.
	 * 
	 * @param identifier
	 *           identifier to be shaped
	 * @param qualifier
	 *           qualifier of the identifier
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           hibernate dialect
	 * @return the shaped table name
	 */
	public static String shapeQualifiableIdentifier(String identifier, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		if (prefs.isQualifyTableNames())
		{
			return dialect.getQualifiedIdentifier(identifier, qualifier, prefs);
		}
		else
		{
			return shapeIdentifier(identifier, prefs, dialect);
		}
	}

	/**
	 * Shapes the identifier depending on the preferences. If isQuoteIdentifiers is true, the identifier is
	 * quoted with dialect-specific delimiters.
	 * 
	 * @param identifier
	 *           identifier to be shaped
	 * @param prefs
	 *           preferences for generated sql scripts
	 * @param dialect
	 *           hibernate dialect for the dialect specific quotes
	 * @return the shaped identifier
	 */
	public static String shapeIdentifier(String identifier, SqlGenerationPreferences prefs,
		HibernateDialect dialect)
	{
		if (prefs.isQuoteIdentifiers())
		{
			return dialect.openQuote() + identifier + dialect.closeQuote();
		}
		else
		{
			return identifier;
		}
	}

	/**
	 * @param column
	 * @param qualifier
	 * @param prefs
	 * @param dialect
	 * @return
	 */
	public static String[] getAddSimulatedAutoIncrementColumn(TableColumnInfo column,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs, HibernateDialect dialect)
	{
		final ArrayList<String> result = new ArrayList<String>();
		final String tableName = shapeQualifiableIdentifier(column.getTableName(), qualifier, prefs, dialect);
		final String columnName = shapeIdentifier(column.getColumnName(), prefs, dialect);
		final String sequenceName = shapeIdentifier(tableName + "_" + columnName + "_" + "seq", prefs, dialect);

		// TODO Probably want to allow the user to set these sequence properties
		// ??
		// Sequence settings.
		final String sequenceIncrement = "1";
		final String minimum = "1";
		final String maximum = null;
		final String start = "1";
		final String cacheClause = null;
		final boolean cycle = false;

		result.add(dialect.getCreateSequenceSQL(sequenceName, sequenceIncrement, minimum, maximum, start,
			cacheClause, cycle, qualifier, prefs));

		final StringBuilder triggerSql = new StringBuilder();
		triggerSql.append("CREATE TRIGGER ");
		triggerSql.append(columnName);
		triggerSql.append("_trigger \n");
		triggerSql.append("NO CASCADE BEFORE INSERT ON ");
		triggerSql.append(tableName);
		triggerSql.append(" REFERENCING NEW AS n \n");
		triggerSql.append("FOR EACH ROW \n");
		triggerSql.append("SET n.");
		triggerSql.append(columnName);
		triggerSql.append(" = NEXTVAL FOR ");
		triggerSql.append(sequenceName);

		result.add(triggerSql.toString());

		return result.toArray(new String[result.size()]);

	}

	/**
	 * Returns the SQL statement to use to add a column to the specified table using the information about the
	 * new column specified by info.
	 * 
	 * @param info
	 *           information about the new column such as type, name, etc.
	 * @param dialect
	 *           the HibernateDialect to use to resolve the type
	 * @param addDefaultClause
	 *           whether or not the dialect's SQL supports a DEFAULT clause for columns.
	 * @param addNullClause
	 *           whether or not to add the NULL / NOT NULL clause
	 * @return
	 * @throws UnsupportedOperationException
	 *            if the database doesn't support adding columns after a table has already been created.
	 */
	public static String getAddColumSQL(TableColumnInfo info, HibernateDialect dialect,
		boolean addDefaultClause, boolean supportsNullQualifier, boolean addNullClause,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
		throws UnsupportedOperationException, HibernateException
	{
		final StringBuilder result = new StringBuilder();
		result.append(DialectUtils.ALTER_TABLE_CLAUSE + " ");
		result.append(shapeQualifiableIdentifier(info.getTableName(), qualifier, prefs, dialect));
		result.append(" ");
		result.append(dialect.getAddColumnString().toUpperCase());

		result.append(" ");
		if (prefs.isQuoteColumnNames())
		{
			result.append(shapeIdentifier(info.getColumnName(), prefs, dialect));
		}
		else
		{
			result.append(info.getColumnName());
		}
		result.append(" ");
		result.append(dialect.getTypeName(info.getDataType(), info.getColumnSize(), info.getColumnSize(),
			info.getDecimalDigits()));

		if (addDefaultClause)
		{
			appendDefaultClause(info, result);
		}
		if (addNullClause)
		{
			if (info.isNullable().equals("NO"))
			{
				result.append(" NOT NULL ");
			}
			else
			{
				if (supportsNullQualifier)
				{
					result.append(" NULL ");
				}
			}
		}
		return result.toString();
	}

	private static void bindAttribute(HibernateDialect dialect, StringTemplate st, String key, String value,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		if (value == null || "".equals(value)) { return; }
		if (ST_TABLE_NAME_KEY.equals(key))
		{
			value = DialectUtils.shapeQualifiableIdentifier(value, qualifier, prefs, dialect);
		}
		if (ST_VIEW_NAME_KEY.equals(key))
		{
			value = DialectUtils.shapeQualifiableIdentifier(value, qualifier, prefs, dialect);
		}
		if (ST_OLD_OBJECT_NAME_KEY.equals(key))
		{
			value = DialectUtils.shapeQualifiableIdentifier(value, qualifier, prefs, dialect);
		}
		if (ST_NEW_OBJECT_NAME_KEY.equals(key))
		{
			value = DialectUtils.shapeQualifiableIdentifier(value, qualifier, prefs, dialect);
		}
		if (ST_COLUMN_NAME_KEY.equals(key) && prefs.isQuoteColumnNames())
		{
			value = DialectUtils.shapeIdentifier(value, prefs, dialect);
		}
		st.setAttribute(key, value);
	}

	public static String bindAttributes(HibernateDialect dialect, StringTemplate st,
		HashMap<String, String> valuesMap, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		for (final Entry<String, String> entry : valuesMap.entrySet())
		{
			final String key = entry.getKey();
			final String value = entry.getValue();
			bindAttribute(dialect, st, key, value, qualifier, prefs);
		}

		return st.toString();
	}

	/**
	 * @param dialect
	 * @param st
	 * @param valuesMap
	 * @param qualifier
	 * @param prefs
	 * @return
	 */
	public static String bindTemplateAttributes(HibernateDialect dialect, StringTemplate st,
		HashMap<String, String> valuesMap, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		bindAttributes(dialect, st, valuesMap, qualifier, prefs);
		return st.toString();
	}

	/**
	 * @param dialect
	 *           the dialect that this attribute binding is meant for
	 * @param st
	 *           the String template
	 * @param valuesMap
	 *           a map of key/value pairs to bind into the template
	 * @param columns
	 *           the TableColumnInfos to use for column names in the column list
	 * @param qualifier
	 *           the specifics regarding schema / catalog
	 * @param prefs
	 *           user's preferences regarding qualifying identifiers
	 * @return the SQL result of binding key/values and column list into the specified template
	 */
	public static String bindTemplateAttributes(HibernateDialect dialect, StringTemplate st,
		HashMap<String, String> valuesMap, TableColumnInfo[] columns, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		bindAttributes(dialect, st, valuesMap, qualifier, prefs);
		for (final TableColumnInfo column : columns)
		{
			bindAttribute(dialect, st, ST_COLUMN_NAME_KEY, column.getColumnName(), qualifier, prefs);
		}
		return st.toString();
	}

	public static String bindTemplateAttributes(HibernateDialect dialect, StringTemplate st,
		HashMap<String, String> valuesMap, String[] columns, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		bindAttributes(dialect, st, valuesMap, qualifier, prefs);
		for (final String column : columns)
		{
			bindAttribute(dialect, st, ST_COLUMN_NAME_KEY, column, qualifier, prefs);
		}
		return st.toString();
	}

	public static HashMap<String, String> getValuesMap(Object... elts)
	{
		final HashMap<String, String> valuesMap = new HashMap<String, String>();
		for (int i = 0; i < elts.length - 1; i++)
		{
			valuesMap.put(elts[i].toString(), elts[i + 1].toString());
		}
		return valuesMap;
	}

	public static boolean isNotEmptyString(String value)
	{
		return (value != null) && (!"".equals(value));
	}

	/**
	 * This allows to counteract the effects of quoting identifiers in odd cases where the dialect may not
	 * allow it.
	 * 
	 * @param dialect
	 *           the dialect to use to determine the open and close quotes
	 * @param identifier
	 *           the identifier to look for to remove quotes from
	 * @param strWithQuotes
	 *           the string which contains the identifier and may or may not have quotes.
	 * @return the string with quotes stripped from any occurrences of the specified identifier.
	 */
	public static String stripQuotesFromIdentifier(HibernateDialect dialect, String identifier,
		String strWithQuotes)
	{
		// Strip quotes from the column name
		final StringBuilder tmp = new StringBuilder("\\" + dialect.openQuote());
		tmp.append(identifier);
		tmp.append("\\" + dialect.closeQuote());
		return strWithQuotes.replaceAll(tmp.toString(), identifier);
	}

	/**
	 * Converts the specified byte array into a String containing hex characters that represent the nibbles
	 * that form the butes in the array.
	 * 
	 * @param bytes
	 *           the byte array to convert
	 * @return a hex string representing the bytes in the specified byte array.
	 */
	public static String toHexString(byte[] bytes)
	{
		final char[] hexArray =
			{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		final char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++)
		{
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v / 16];
			hexChars[j * 2 + 1] = hexArray[v % 16];
		}
		return new String(hexChars);
	}
}
