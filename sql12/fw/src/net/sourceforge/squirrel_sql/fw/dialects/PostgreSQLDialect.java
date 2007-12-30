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
import java.util.Vector;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

/**
 * An extension to the standard Hibernate PostgreSQL dialect 
 */

public class PostgreSQLDialect 
    extends org.hibernate.dialect.PostgreSQLDialect 
    implements HibernateDialect {

    public PostgreSQLDialect() {
        super();
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.BINARY, "bytea");
        // PostgreSQL follows the standard for SQL BIT.  It's a string of BITs.
        // So bit(10) is a string of 10 bits.  JDBC treats SQL BIT as if it
        // were only a single BIT.  It specifies that BIT is equivalent to 
        // BOOLEAN.  It claims that the PreparedStatement set method that should
        // be used with BIT is setBoolean and getBoolean.  This is not compliant
        // with the standard.  So SQL BIT type support is broken in Java, there
        // is nothing we can do about that.
        // Best thing to do for now, is try to convert the BIT type to a 
        // boolean like the JDBC spec says and hope for the best.  Hope that the
        // source database isn't using the BIT column as a sequence of multiple
        // BITs.
        registerColumnType(Types.BIT, "bool");
        registerColumnType(Types.BLOB, "bytea");
        registerColumnType(Types.BOOLEAN, "bool");
        registerColumnType(Types.CHAR, 8000,  "char($l)");
        registerColumnType(Types.CHAR, "text");
        registerColumnType(Types.CLOB, "text");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p,2)");
        registerColumnType(Types.DOUBLE, "float($p)");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "int"); 
        registerColumnType(Types.LONGVARBINARY, "bytea");
        registerColumnType(Types.LONGVARCHAR, "text");
        registerColumnType(Types.NUMERIC, "numeric($p)");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "int");
        registerColumnType(Types.VARBINARY, "bytea");
        registerColumnType(Types.VARCHAR, 8000, "varchar($l)");
        registerColumnType(Types.VARCHAR, "text");
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#canPasteTo(net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType)
     */
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        boolean result = true;
        DatabaseObjectType type = info.getDatabaseObjectType();
        if (type.getName().equalsIgnoreCase("database")) {
            result = false;
        }
        return result;
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#supportsSchemasInTableDefinition()
     */
    public boolean supportsSchemasInTableDefinition() {
        return true;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getLengthFunction()
     */
    public String getLengthFunction(int dataType) {
        return "length";
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getMaxFunction()
     */
    public String getMaxFunction() {
        return "max";
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getMaxPrecision(int)
     */
    public int getMaxPrecision(int dataType) {
        if (dataType == Types.DOUBLE
                || dataType == Types.FLOAT)
        {
            return 53;
        } else {
            return 38;
        }
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getMaxScale(int)
     */
    public int getMaxScale(int dataType) {
        return getMaxPrecision(dataType);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getPrecisionDigits(int, int)
     */
    public int getPrecisionDigits(int columnSize, int dataType) {
        if (columnSize == 2) {
            return 5;
        }
        if (columnSize == 4) {
            return 10;
        }
        return 19;
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getColumnLength(int, int)
     */
    public int getColumnLength(int columnSize, int dataType) {
        if (dataType == Types.VARCHAR && columnSize == -1) { 
            // PostgreSQL 8.0 reports length as -1 sometimes. Why??
            return 2000;
        } 
        return columnSize;
    }

    /**
     * The string which identifies this dialect in the dialect chooser.
     * 
     * @return a descriptive name that tells the user what database this dialect
     *         is design to work with.
     */
    public String getDisplayName() {
        return "PostgreSQL";
    }    
    
    /**
     * Returns boolean value indicating whether or not this dialect supports the
     * specified database product/version.
     * 
     * @param databaseProductName the name of the database as reported by 
     * 							  DatabaseMetaData.getDatabaseProductName()
     * @param databaseProductVersion the version of the database as reported by
     *                              DatabaseMetaData.getDatabaseProductVersion()
     * @return true if this dialect can be used for the specified product name
     *              and version; false otherwise.
     */
    public boolean supportsProduct(String databaseProductName, 
								   String databaseProductVersion) 
	{
    	if (databaseProductName == null) {
    		return false;
    	}
    	if (databaseProductName.trim().toLowerCase().startsWith("postgresql")) {
    		// We don't yet have the need to discriminate by version.
    		return true;
    	}
		return false;
	}
    
    /**
     * Returns the SQL statement to use to add a column to the specified table
     * using the information about the new column specified by info.
     * @param info information about the new column such as type, name, etc.
     * 
     * @return
     * @throws UnsupportedOperationException if the database doesn't support 
     *         adding columns after a table has already been created.
     */
    public String[] getColumnAddSQL(TableColumnInfo info) 
        throws UnsupportedOperationException 
    {
        ArrayList<String> result = new ArrayList<String>();
        result.add(DialectUtils.getColumnAddSQL(info, this, true, true, true));
        if (info.getRemarks() != null && !"".equals(info.getRemarks())) {
            result.add(getColumnCommentAlterSQL(info));
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * Returns a boolean value indicating whether or not this dialect supports
     * adding comments to columns.
     * 
     * @return true if column comments are supported; false otherwise.
     */
    public boolean supportsColumnComment() {
        return true;
    }    
        
    /**
     * Returns a boolean value indicating whether or not this database dialect
     * supports dropping columns from tables.
     * 
     * @return true if the database supports dropping columns; false otherwise.
     */
    public boolean supportsDropColumn() {
        return true;
    }

    /**
     * Returns the SQL that forms the command to drop the specified colum in the
     * specified table.
     * 
     * @param tableName the name of the table that has the column
     * @param columnName the name of the column to drop.
     * @return
     * @throws UnsupportedOperationException if the database doesn't support 
     *         dropping columns. 
     */
    public String getColumnDropSQL(String tableName, String columnName) {      
        return DialectUtils.getColumnDropSQL(tableName, columnName);
    }
    
    /**
     * Returns the SQL that forms the command to drop the specified table.  If
     * cascade contraints is supported by the dialect and cascadeConstraints is
     * true, then a drop statement with cascade constraints clause will be 
     * formed.
     * 
     * @param iTableInfo the table to drop
     * @param cascadeConstraints whether or not to drop any FKs that may 
     * reference the specified table.
     * @return the drop SQL command.
     */
    public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints, boolean isMaterializedView){
        // TODO: Need to verify this
        return DialectUtils.getTableDropSQL(iTableInfo, true, cascadeConstraints, false, DialectUtils.CASCADE_CLAUSE, false);
    }
    
    /**
     * Returns the SQL that forms the command to add a primary key to the 
     * specified table composed of the given column names.
     * 
     * @param pkName the name of the constraint
     * @param columnNames the columns that form the key
     * @return
     */
    public String[] getAddPrimaryKeySQL(String pkName, 
                                        TableColumnInfo[] colInfos, 
                                        ITableInfo ti) 
    {
        return new String[] { 
            DialectUtils.getAddPrimaryKeySQL(ti, pkName, colInfos, false)
        };
    }
    
    /**
     * Returns the SQL statement to use to add a comment to the specified 
     * column of the specified table.
     * @param info information about the column such as type, name, etc.
     * @return
     * @throws UnsupportedOperationException if the database doesn't support 
     *         annotating columns with a comment.
     */
    public String getColumnCommentAlterSQL(TableColumnInfo info) 
        throws UnsupportedOperationException
    {
        return DialectUtils.getColumnCommentAlterSQL(info.getTableName(), 
                                                     info.getColumnName(), 
                                                     info.getRemarks());

    }
    
    /**
     * Returns the SQL used to alter the specified column to not allow null 
     * values
     *
     * ALTER TABLE products ALTER COLUMN product_no SET NOT NULL
     * 
     * ALTER TABLE products ALTER COLUMN product_no DROP NOT NULL
     * 
     * @param info the column to modify
     * @return the SQL to execute
     */
    public String getColumnNullableAlterSQL(TableColumnInfo info) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" ALTER COLUMN ");
        result.append(info.getColumnName());
        if (info.isNullable().equalsIgnoreCase("YES")) {
            result.append(" DROP NOT NULL");
        } else {
            result.append(" SET NOT NULL");
        }
        return result.toString();
    }

    /**
     * Returns a boolean value indicating whether or not this database dialect
     * supports renaming columns.
     * 
     * @return true if the database supports changing the name of columns;  
     *         false otherwise.
     */
    public boolean supportsRenameColumn() {
        // TODO: need to verify this
        return true;
    }
    
    /**
     * Returns the SQL that is used to change the column name.
     * 
     * ALTER TABLE a RENAME COLUMN x TO y
     * 
     * @param from the TableColumnInfo as it is
     * @param to the TableColumnInfo as it wants to be
     * 
     * @return the SQL to make the change
     */
    public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to) {
        String alterClause = DialectUtils.RENAME_COLUMN_CLAUSE;        
        String toClause = DialectUtils.TO_CLAUSE;
        return DialectUtils.getColumnNameAlterSQL(from, to, alterClause, toClause);
    }
    
    /**
     * Returns a boolean value indicating whether or not this dialect supports 
     * modifying a columns type.
     * 
     * @return true if supported; false otherwise
     */
    public boolean supportsAlterColumnType() {
        return true;
    }
    
    /**
     * Returns the SQL that is used to change the column type.
     * 
     * ALTER TABLE products ALTER COLUMN price TYPE numeric(10,2);
     * 
     * @param from the TableColumnInfo as it is
     * @param to the TableColumnInfo as it wants to be
     * 
     * @return the SQL to make the change
     * @throw UnsupportedOperationException if the database doesn't support 
     *         modifying column types. 
     */
    public List<String> getColumnTypeAlterSQL(TableColumnInfo from, 
                                        TableColumnInfo to)
        throws UnsupportedOperationException
    {
        ArrayList<String> list = new ArrayList<String>();
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(from.getTableName());
        result.append(" ALTER COLUMN ");
        result.append(to.getColumnName());
        result.append(" TYPE ");
        result.append(DialectUtils.getTypeName(to, this));
        list.add(result.toString());
        return list;
    }

    /**
     * Returns a boolean value indicating whether or not this database dialect
     * supports changing a column from null to not-null and vice versa.
     * 
     * @return true if the database supports dropping columns; false otherwise.
     */    
    public boolean supportsAlterColumnNull() {
        return true;
    }
    
    /**
     * Returns a boolean value indicating whether or not this database dialect
     * supports changing a column's default value.
     * 
     * @return true if the database supports modifying column defaults; false 
     *         otherwise
     */
    public boolean supportsAlterColumnDefault() {
        return true;
    }
    
    
    /**
     * Returns the SQL command to change the specified column's default value
     *   
     * @param info the column to modify and it's default value.
     * @return SQL to make the change
     */
    public String getColumnDefaultAlterSQL(TableColumnInfo info) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" ALTER COLUMN ");
        result.append(info.getColumnName());
        String defVal = info.getDefaultValue();
        if (defVal == null || "".equals(defVal)) {
            result.append(" DROP DEFAULT");
        } else {
            result.append(" SET DEFAULT ");
            if (JDBCTypeMapper.isNumberType(info.getDataType())) {
                result.append(defVal);
            } else {
                result.append("'");
                result.append(defVal);
                result.append("'");
            }
        }
        return result.toString();
    }
    
    /**
     * Returns the SQL command to drop the specified table's primary key.
     * 
     * @param pkName the name of the primary key that should be dropped
     * @param tableName the name of the table whose primary key should be 
     *                  dropped
     * @return
     */
    public String getDropPrimaryKeySQL(String pkName, String tableName) {
        return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, true, false);
    }
    
    /**
     * Returns the SQL command to drop the specified table's foreign key 
     * constraint.
     * 
     * @param fkName the name of the foreign key that should be dropped
     * @param tableName the name of the table whose foreign key should be 
     *                  dropped
     * @return
     */
    public String getDropForeignKeySQL(String fkName, String tableName) {
        return DialectUtils.getDropForeignKeySQL(fkName, tableName);
    }
    
    /**
     * Returns the SQL command to create the specified table.
     * 
     * @param tables the tables to get create statements for
     * @param md the metadata from the ISession
     * @param prefs preferences about how the resultant SQL commands should be 
     *              formed.
     * @param isJdbcOdbc whether or not the connection is via JDBC-ODBC bridge.
     *  
     * @return the SQL that is used to create the specified table
     */
    public List<String> getCreateTableSQL(List<ITableInfo> tables, 
                                          ISQLDatabaseMetaData md,
                                          CreateScriptPreferences prefs,
                                          boolean isJdbcOdbc)
        throws SQLException
    {
        return DialectUtils.getCreateTableSQL(tables, md, this, prefs, isJdbcOdbc);
    }
    
    /**
     * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDialectType()
     */
    public DialectType getDialectType() {
       return DialectType.POSTGRES;
    }

    /**
     * Access Methods Field. Values "btree", "hash", "gist", "gin".
     */
    private static final String[] ACCESS_METHODS = {"btree", "hash", "gist", "gin"};


    /**
     * Shapes the table name depending on the prefereneces.
     * If isQualifyTableNames is true, the qualified name of the table is returned.
     *
     * @param identifier identifier to be shaped
     * @param qualifier  qualifier of the identifier
     * @param prefs      preferences for generated sql scripts
     * @return the shaped table name
     */
    private String shapeQualifiableIdentifier(String identifier, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        return DialectUtils.shapeQualifiableIdentifier(identifier, qualifier, prefs, this);
    }


    /**
     * Shapes the identifier depending on the preferences.
     * If isQuoteIdentifiers is true, the identifier is quoted with dialect-specific delimiters.
     *
     * @param identifier identifier to be shaped
     * @param prefs      preferences for generated sql scripts
     * @return the shaped identifier
     */
    private String shapeIdentifier(String identifier, SqlGenerationPreferences prefs) {
        return DialectUtils.shapeIdentifier(identifier, prefs, this);
    }


    public boolean supportsSequence() {
        return supportsSequences();
    }


    public boolean supportsCheckOptionsForViews() {
        return false;
    }


    public boolean supportsIndexes() {
        return true;
    }


    public boolean supportsTablespace() {
        return true;
    }


    public boolean supportsAccessMethods() {
        return true;
    }


    public boolean supportsDropView() {
        return true;
    }


    public boolean supportsRenameView() {
        return true;
    }


    public boolean supportsAutoIncrement() {
        return true;
    }


    public boolean supportsEmptyTables() {
        return true;
    }


    public boolean supportsMultipleRowInserts() {

        return true;
    }


    public boolean supportsAddForeignKeyConstraint() {
        return true;
    }


    public boolean supportsAddUniqueConstraint() {
        return true;
    }


    public boolean supportsAlterSequence() {
        return true;
    }


    public boolean supportsCreateIndex() {
        return true;
    }


    public boolean supportsCreateSequence() {
        return true;
    }


    public boolean supportsCreateTable() {
        return true;
    }


    public boolean supportsCreateView() {
        return true;
    }


    public boolean supportsDropConstraint() {
        return true;
    }


    public boolean supportsDropIndex() {
        return true;
    }


    public boolean supportsDropSequence() {
        return true;
    }


    public boolean supportsInsertInto() {
        return true;
    }


    public boolean supportsRenameTable() {
        return true;
    }


    public boolean supportsSequenceInformation() {
        return true;
    }


    public boolean supportsUpdate() {
        return true;
    }


    public String[] getAccessMethodsTypes() {
        return ACCESS_METHODS;
    }


    public String getCreateTableSQL(String simpleName, List<TableColumnInfo> columns, List<TableColumnInfo> primaryKeys, SqlGenerationPreferences prefs, DatabaseObjectQualifier qualifier) {
        if (columns.isEmpty() && !supportsEmptyTables()) {
            throw new IllegalArgumentException(getDisplayName() + " does not support empty tables. (parameter 'columns' has to contain at least one column)");
        }

        // CREATE TABLE tableName (
        //  column1 int,
        //  column2 varchar(20) NOT NULL DEFAULT 'Hello World'
        //  CONSTRAINT tableName_pkey PRIMARY KEY(column1,column2)
        // );
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtils.CREATE_TABLE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(simpleName, qualifier, prefs)).append(" (\n");
        for (TableColumnInfo column : columns) {
            sql.append(" ").append(shapeIdentifier(column.getColumnName(), prefs)).append(" ");
            sql.append(getTypeName(column.getDataType(), column.getColumnSize(), column.getColumnSize(), column.getDecimalDigits()));

            if (primaryKeys != null && primaryKeys.size() == 1 &&
                    primaryKeys.get(0).getColumnName().equals(column.getColumnName())) {
                sql.append(" " + DialectUtils.PRIMARY_KEY_CLAUSE);
            } else if (column.isNullAllowed() == 0) {
                sql.append(" " + DialectUtils.NOT_NULL_CLAUSE);
            }
            if (column.getDefaultValue() != null)
                sql.append(" " + DialectUtils.DEFAULT_CLAUSE + " ").append(column.getDefaultValue());

            sql.append(",\n");
        }

        if (primaryKeys != null && primaryKeys.size() > 1) {
            sql.append(" " + DialectUtils.CONSTRAINT_CLAUSE + " ").append(shapeIdentifier(simpleName + "_pkey", prefs))
                    .append(" " + DialectUtils.PRIMARY_KEY_CLAUSE + "(");
            for (TableColumnInfo pkPart : primaryKeys) {
                sql.append(shapeIdentifier(pkPart.getColumnName(), prefs)).append(",");
            }
            sql.setLength(sql.length() - 1);
            sql.append(")");
        } else {
            sql.setLength(sql.length() - 2);
        }
        sql.append("\n)").append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getRenameTableSQL(String oldTableName, String newTableName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // ALTER TABLE oldTableName RENAME TO newTableName;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtils.ALTER_TABLE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(oldTableName, qualifier, prefs)).append(" ");
        sql.append("RENAME TO ").append(shapeIdentifier(newTableName, prefs)).append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getCreateViewSQL(String viewName, String definition, String checkOption, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // CREATE VIEW viewName
        //  AS definition;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtils.CREATE_VIEW_CLAUSE + " ").append(shapeQualifiableIdentifier(viewName, qualifier, prefs)).append("\n");
        sql.append(" AS ").append(definition);
        if (supportsCheckOptionsForViews() && checkOption != null && !checkOption.equals("")) {
            sql.append("\n WITH ").append(checkOption).append(" CHECK OPTION");
        }
        sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getRenameViewSQL(String oldViewName, String newViewName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // Renaming a view has the same syntax as renaming a table.
        return getRenameTableSQL(oldViewName, newViewName, qualifier, prefs);
    }


    public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // DROP VIEW viewName CASCADE;
        StringBuffer sql = new StringBuffer();

        sql.append(DialectUtils.DROP_VIEW_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(viewName, qualifier, prefs));
        sql.append(" ");
        sql.append(cascade ? DialectUtils.CASCADE_CLAUSE : DialectUtils.RESTRICT_CLAUSE);
        sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getCreateIndexSQL(String indexName, String tableName, String[] columns, boolean unique, String tablespace, String constraints, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // CREATE UNIQUE INDEX indexName ON tableName (column1, column2) TABLESPACE
        //  WHERE constraints;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtils.CREATE_CLAUSE + " ");
        if (unique) sql.append(DialectUtils.UNIQUE_CLAUSE + " ");
        sql.append(DialectUtils.INDEX_CLAUSE + " ");
        sql.append(shapeIdentifier(indexName, prefs));
        sql.append(" ON ").append(shapeQualifiableIdentifier(tableName, qualifier, prefs)).append(" ");

        sql.append("(");
        for (String column : columns) {
            sql.append(shapeIdentifier(column, prefs)).append(", ");
        }
        sql.delete(sql.length() - 2, sql.length());     // deletes the last ", "
        sql.append(")");

        if (tablespace != null && !tablespace.equals("")) {
            sql.append(" \n TABLESPACE ").append(tablespace);
        }

        if (constraints != null && !constraints.equals("")) {
            sql.append(" \n " + DialectUtils.WHERE_CLAUSE + " ").append(constraints);
        }
        sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getDropIndexSQL(String indexName, boolean cascade, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // DROP INDEX indexName CASCADE;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtils.DROP_INDEX_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(indexName, qualifier, prefs)).append(" ");
        sql.append(cascade ? DialectUtils.CASCADE_CLAUSE : DialectUtils.RESTRICT_CLAUSE);
        sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getCreateSequenceSQL(String sequenceName, String increment, String minimum, String maximum, String start, String cache, boolean cycle, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // CREATE SEQUENCE sequenceName
        //  INCREMENT BY increment MINVALUE minimum MAXVALUE maxvalue
        //  RESTART WITH restart CACHE cache CYCLE;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtils.CREATE_SEQUENCE_CLAUSE).append(" ");
        sql.append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs)).append("\n");

        if (increment != null && !increment.equals("")) {
            sql.append("INCREMENT BY ");
            sql.append(increment).append(" ");
        }

        if (minimum != null && !minimum.equals("")) {
            sql.append("MINVALUE ");
            sql.append(minimum).append(" ");
        } else sql.append("NO MINVALUE ");

        if (maximum != null && !maximum.equals("")) {
            sql.append("MAXVALUE ");
            sql.append(maximum).append("\n");
        } else sql.append("NO MAXVALUE\n");


        if (start != null && !start.equals("")) {
            sql.append("START WITH ");
            sql.append(start).append(" ");
        }

        if (cache != null && !cache.equals("")) {
            sql.append("CACHE ");
            sql.append(cache).append(" ");
        }

        if (!cycle) sql.append("NO ");
        sql.append("CYCLE").append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getAlterSequenceSQL(String sequenceName, String increment, String minimum, String maximum, String restart, String cache, boolean cycle, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // ALTER SEQUENCE sequenceName
        //  INCREMENT BY increment MINVALUE minimum MAXVALUE maxvalue
        //  RESTART WITH restart CACHE cache CYCLE;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtils.ALTER_SEQUENCE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs)).append("\n");

        if (increment != null && !increment.equals("")) {
            sql.append("INCREMENT BY ");
            sql.append(increment).append(" ");
        }

        if (minimum != null && !minimum.equals("")) {
            sql.append("MINVALUE ");
            sql.append(minimum).append(" ");
        } else sql.append("NO MINVALUE ");

        if (maximum != null && !maximum.equals("")) {
            sql.append("MAXVALUE ");
            sql.append(maximum).append("\n");
        } else sql.append("NO MAXVALUE\n");

        if (restart != null && !restart.equals("")) {
            sql.append("RESTART WITH ");
            sql.append(restart).append(" ");
        }

        if (cache != null && !cache.equals("")) {
            sql.append("CACHE ");
            sql.append(cache).append(" ");
        }

        if (!cycle) sql.append("NO ");
        sql.append("CYCLE").append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getSequenceInformationSQL(String sequenceName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // SELECT last_value, max_value, min_value, cache_value, increment_by, is_cycled FROM sequenceName;
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT last_value, max_value, min_value, cache_value, increment_by, is_cycled FROM ");
        sql.append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs));
        sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getDropSequenceSQL(String sequenceName, boolean cascade, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // DROP SEQUENCE sequenceName CASCADE;
        StringBuilder sql = new StringBuilder();

        sql.append("DROP SEQUENCE ").append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs)).append(" ");
        sql.append(cascade ? "CASCADE" : "RESTRICT").append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getAddForeignKeyConstraintSQL(String localTableName, String refTableName, String constraintName, boolean deferrable, boolean initiallyDeferred, boolean matchFull, boolean autoFKIndex, String fkIndexName, Collection<String[]> localRefColumns, String onUpdateAction, String onDeleteAction, DatabaseObjectQualifier qualifier,
                                                SqlGenerationPreferences prefs) {
        // ALTER TABLE localTableName
        //  ADD CONSTRAINT constraintName FOREIGN KEY (localColumn1, localColumn2)
        //  REFERENCES referencedTableName (referencedColumn1, referencedColumn2)
        //  MATCH FULL ON UPDATE RESTRICT ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtils.ALTER_TABLE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(localTableName, qualifier, prefs)).append("\n");

        if (constraintName != null && !constraintName.equals("")) {
            sql.append(" " + DialectUtils.ADD_CONSTRAINT_CLAUSE + " ");
            sql.append(shapeIdentifier(constraintName, prefs)).append("\n");
        }

        sql.append(" " + DialectUtils.FOREIGN_KEY_CLAUSE + " (");

        Vector<String> localColumns = new Vector<String>();
        StringBuilder refColumns = new StringBuilder();
        for (String[] columns : localRefColumns) {
            sql.append(shapeIdentifier(columns[0], prefs)).append(", ");
            localColumns.add(columns[0]);
            refColumns.append(shapeIdentifier(columns[1], prefs)).append(", ");
        }
        sql.setLength(sql.length() - 2);                    // deletes the last ", "
        refColumns.setLength(refColumns.length() - 2);      // deletes the last ", "

        sql.append(")\n REFERENCES ");
        sql.append(shapeQualifiableIdentifier(refTableName, qualifier, prefs)).append(" (");
        sql.append(refColumns.toString()).append(")\n");

        // Options
        if (matchFull) sql.append(" MATCH FULL");

        if (onUpdateAction != null && !onUpdateAction.equals("")) {
            sql.append(" ON UPDATE ");
            sql.append(onUpdateAction);
        }

        if (onDeleteAction != null && !onDeleteAction.equals("")) {
            sql.append(" ON DELETE ");
            sql.append(onDeleteAction);
        }

        if (deferrable) sql.append(" DEFERRABLE");
        if (initiallyDeferred) sql.append(" INITIALLY DEFERRED");
        sql.append(prefs.getSqlStatementSeparator());

        // Additional Index Creation
        if (autoFKIndex && !fkIndexName.equals("")) {
            sql.append("\n");
            sql.append(getCreateIndexSQL(fkIndexName, localTableName,
                    localColumns.toArray(new String[localColumns.size()]), false, null, null,
                    qualifier, prefs));
        }

        return sql.toString();
    }


    public String getAddUniqueConstraintSQL(String tableName, String constraintName, String[] columns, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // ALTER TABLE tableName
        //  ADD CONSTRAINT constraintName UNIQUE (column1, column2);
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtils.ALTER_TABLE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(tableName, qualifier, prefs)).append("\n");

        sql.append(" " + DialectUtils.ADD_CONSTRAINT_CLAUSE + " ");
        sql.append(shapeIdentifier(constraintName, prefs));

        sql.append(" " + DialectUtils.UNIQUE_CLAUSE + " (");
        for (String column : columns) {
            sql.append(shapeIdentifier(column, prefs)).append(", ");
        }
        sql.delete(sql.length() - 2, sql.length());     // deletes the last ", "
        sql.append(")").append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getAddAutoIncrementSQL(TableColumnInfo column, SqlGenerationPreferences prefs) {
        DatabaseObjectQualifier qualifier = new DatabaseObjectQualifier(column.getCatalogName(), column.getSchemaName());
        // ALTER TABLE tableName
        //  ALTER COLUMN columnName
        //  SET DEFAULT nextval('tableName_columnName_seq');
        // ALTER SEQUENCE tableName_columnName_seq OWNED BY tableName.columnName;
        StringBuilder sql = new StringBuilder();

        // In PostgreSQL we need to add a sequence to support auto-increment (name: tablename_colname_seq)
        String sequenceName = column.getTableName() + "_" + column.getColumnName() + "_seq";
        sql.append(getCreateSequenceSQL(sequenceName, null, null, null, null, null, false,
                qualifier, prefs)).append("\n\n");

        sql.append(DialectUtils.ALTER_TABLE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(column.getTableName(), qualifier, prefs)).append("\n");

        sql.append(" " + DialectUtils.ALTER_COLUMN_CLAUSE + " ");
        sql.append(shapeIdentifier(column.getColumnName(), prefs)).append("\n");

        sql.append(" " + DialectUtils.SET_DEFAULT_CLAUSE + " nextval('")
                .append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs)).append("')");
        sql.append(prefs.getSqlStatementSeparator()).append("\n\n");

        sql.append(DialectUtils.ALTER_SEQUENCE_CLAUSE + " ").
                append(shapeQualifiableIdentifier(sequenceName, qualifier, prefs)).append("\n");
        sql.append(" OWNED BY ");
        sql.append(shapeQualifiableIdentifier(column.getTableName(), qualifier, prefs)).append(".")
                .append(shapeIdentifier(column.getColumnName(), prefs));
        sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getDropConstraintSQL(String tableName, String constraintName, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // ALTER TABLE tableName
        //  DROP CONSTRAINT constraintName;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtils.ALTER_TABLE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(tableName, qualifier, prefs)).append("\n");

        sql.append(" " + DialectUtils.DROP_CONSTRAINT_CLAUSE + " ");
        sql.append(shapeIdentifier(constraintName, prefs));
        sql.append(prefs.getSqlStatementSeparator());
        return sql.toString();
    }


    public String getInsertIntoSQL(String tableName, List<String> columns, String query, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        if (query == null || query.length() == 0) return "";

        // INSERT INTO tableName (column1, column2)
        //  query;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtils.INSERT_INTO_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(tableName, qualifier, prefs));
        if (columns != null && !columns.isEmpty()) {
            sql.append(" (");
            for (String column : columns) {
                sql.append(shapeIdentifier(column, prefs)).append(", ");
            }
            sql.setLength(sql.length() - 2);
            sql.append(")");
        }
        sql.append("\n");

        sql.append(" ").append(query);
        if (sql.charAt(sql.length() - 1) != ';') sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getUpdateSQL(String tableName, String[] setColumns, String[] setValues, String[] fromTables, String[] whereColumns, String[] whereValues, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        if ((setColumns == null && setValues == null) || (setColumns != null && setValues != null && setColumns.length == 0 && setValues.length == 0))
            return "";
        if ((setColumns != null && setValues != null && setColumns.length != setValues.length) || setColumns == null || setValues == null)
            throw new IllegalArgumentException("The amount of SET columns and values must be the same!");
        if ((whereColumns != null && whereValues != null && whereColumns.length != whereValues.length) || (whereColumns == null && whereValues != null) || (whereColumns != null && whereValues == null))
            throw new IllegalArgumentException("The amount of WHERE columns and values must be the same!");

        // UPDATE tableName SET setColumn1 = setValue1, setColumn2 = setValue2
        //  FROM fromTable1, fromTable2
        //  WHERE whereColumn1 = whereValue1 AND whereColumn2 = whereValue2;
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtils.UPDATE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(tableName, qualifier, prefs));
        sql.append(" " + DialectUtils.SET_CLAUSE + " ");
        for (int i = 0; i < setColumns.length; i++) {
            sql.append(shapeIdentifier(setColumns[i], prefs));
            if (setValues[i] == null) sql.append(" = NULL");
            else sql.append(" = ").append(setValues[i]);
            sql.append(", ");
        }
        sql.setLength(sql.length() - 2);

        if (fromTables != null) {
            sql.append("\n " + DialectUtils.FROM_CLAUSE + " ");
            for (String from : fromTables) {
                sql.append(shapeQualifiableIdentifier(from, qualifier, prefs)).append(", ");
            }
            sql.setLength(sql.length() - 2);
        }

        if (whereColumns != null && whereColumns.length != 0) {
            sql.append("\n " + DialectUtils.WHERE_CLAUSE + " ");
            for (int i = 0; i < whereColumns.length; i++) {
                sql.append(shapeIdentifier(whereColumns[i], prefs));
                if (whereValues[i] == null) sql.append(" IS NULL");
                else sql.append(" = ").append(whereValues[i]);
                sql.append(" " + DialectUtils.AND_CLAUSE + " ");
            }
            sql.setLength(sql.length() - 5);
        }
        sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }


    public String getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs) {
        // ALTER TABLE tableName
        //  ADD COLUMN columnName columnType NOT NULL DEFAULT 'defaultValue';
        StringBuilder sql = new StringBuilder();

        sql.append(DialectUtils.ALTER_TABLE_CLAUSE + " ");
        sql.append(shapeQualifiableIdentifier(column.getTableName(), qualifier, prefs)).append("\n");

        sql.append(" " + DialectUtils.ADD_COLUMN_CLAUSE + " ");
        sql.append(shapeIdentifier(column.getColumnName(), prefs)).append(" ");
        sql.append(getTypeName(column.getDataType(), column.getColumnSize(), column.getColumnSize(), column.getDecimalDigits()));

        if (column.isNullAllowed() == 0) {
            sql.append(" " + DialectUtils.NOT_NULL_CLAUSE);
        }
        if (column.getDefaultValue() != null) {
            sql.append(" DEFAULT ").append(column.getDefaultValue());
        }

        sql.append(prefs.getSqlStatementSeparator());

        return sql.toString();
    }
    
}

