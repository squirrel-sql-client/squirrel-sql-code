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

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

/**
 * An extension to the standard Hibernate Informix dialect
 * 
 */
public class InformixDialect extends org.hibernate.dialect.InformixDialect
        implements HibernateDialect {

    public InformixDialect() {
        super();
        registerColumnType(Types.BIGINT, "integer");
        registerColumnType(Types.BINARY, "byte");
        registerColumnType(Types.BIT, "byte");
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
        registerColumnType(Types.TIMESTAMP, "datetime year to fraction");
        registerColumnType(Types.TINYINT, "smallint");
        registerColumnType(Types.VARBINARY, "byte");
        registerColumnType(Types.VARCHAR, 255, "varchar($l)");
        registerColumnType(Types.VARCHAR, "text");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#canPasteTo(net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType)
     */
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#supportsSchemasInTableDefinition()
     */
    public boolean supportsSchemasInTableDefinition() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getLengthFunction()
     */
    public String getLengthFunction(int dataType) {
        return "length";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getMaxFunction()
     */
    public String getMaxFunction() {
        return "max";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getMaxPrecision(int)
     */
    public int getMaxPrecision(int dataType) {
        if (dataType == Types.DECIMAL || dataType == Types.NUMERIC) {
            return 32;
        }
        if (dataType == Types.DOUBLE || dataType == Types.DOUBLE) {
            return 16;
        }
        return 32;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getMaxScale(int)
     */
    public int getMaxScale(int dataType) {
        return getMaxPrecision(dataType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getPrecisionDigits(int,
     *      int)
     */
    public int getPrecisionDigits(int columnSize, int dataType) {
        return columnSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getColumnLength(int,
     *      int)
     */
    public int getColumnLength(int columnSize, int dataType) {
        return columnSize;
    }

    /**
     * The string which identifies this dialect in the dialect chooser.
     * 
     * @return a descriptive name that tells the user what database this dialect
     *         is design to work with.
     */
    public String getDisplayName() {
        return "Informix";
    }

    /**
     * Returns boolean value indicating whether or not this dialect supports the
     * specified database product/version.
     * 
     * @param databaseProductName
     *            the name of the database as reported by
     *            DatabaseMetaData.getDatabaseProductName()
     * @param databaseProductVersion
     *            the version of the database as reported by
     *            DatabaseMetaData.getDatabaseProductVersion()
     * @return true if this dialect can be used for the specified product name
     *         and version; false otherwise.
     */
    public boolean supportsProduct(String databaseProductName,
            String databaseProductVersion) {
        if (databaseProductName == null) {
            return false;
        }
        if (databaseProductName.trim().startsWith("Informix")) {
            // We don't yet have the need to discriminate by version.
            return true;
        }
        return false;
    }

    /**
     * Returns the SQL statement to use to add a column to the specified table
     * using the information about the new column specified by info.
     * 
     * @param info
     *            information about the new column such as type, name, etc.
     * 
     * @return
     * @throws UnsupportedOperationException
     *             if the database doesn't support adding columns after a table
     *             has already been created.
     */
    public String[] getColumnAddSQL(TableColumnInfo info)
            throws UnsupportedOperationException {
        return new String[] { DialectUtils.getColumnAddSQL(info, this, true,
                false) };
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
     * @param tableName
     *            the name of the table that has the column
     * @param columnName
     *            the name of the column to drop.
     * @return
     * @throws UnsupportedOperationException
     *             if the database doesn't support dropping columns.
     */
    public String getColumnDropSQL(String tableName, String columnName) {
        return DialectUtils.getColumnDropSQL(tableName, columnName);
    }

    /**
     * Returns the SQL that forms the command to drop the specified table. If
     * cascade contraints is supported by the dialect and cascadeConstraints is
     * true, then a drop statement with cascade constraints clause will be
     * formed.
     * 
     * @param tableName
     *            the table to drop
     * @param cascadeConstraints
     *            whether or not to drop any FKs that may reference the
     *            specified table.
     * 
     * @return the drop SQL command.
     */
    public String getTableDropSQL(String tableName, boolean cascadeConstraints) {
        return DialectUtils
                .getTableDropSQL(tableName, true, cascadeConstraints);
    }

    /**
     * Returns the SQL that forms the command to add a primary key to the
     * specified table composed of the given column names.
     * 
     * CREATE UNIQUE INDEX test_index ON test_table (test_field);
     * 
     * ALTER TABLE test_table ADD CONSTRAINT PRIMARY KEY (test_field) CONSTRAINT
     * test_constraint;
     * 
     * alter table table_name add constraint primary key (column_names)
     * constraint pkName
     * 
     * @param pkName
     *            the name of the constraint
     * @param columnNames
     *            the columns that form the key
     * @return
     */
    public String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] columns) {
        return new String[] { 
            DialectUtils.getAddIndexSQL(pkName, true, columns),
            DialectUtils.getAddPrimaryKeySQL(pkName, columns, true) 
        };
    }

    /**
     * Returns a boolean value indicating whether or not this dialect supports
     * adding comments to columns.
     * 
     * @return true if column comments are supported; false otherwise.
     */
    public boolean supportsColumnComment() {
        return false;
    }

    /**
     * Returns the SQL statement to use to add a comment to the specified column
     * of the specified table.
     * 
     * @param info
     *            information about the column such as type, name, etc.
     * @return
     * @throws UnsupportedOperationException
     *             if the database doesn't support annotating columns with a
     *             comment.
     */
    public String getColumnCommentAlterSQL(TableColumnInfo info)
        throws UnsupportedOperationException 
    {
        int featureId = DialectUtils.COLUMN_COMMENT_TYPE;
        String msg = DialectUtils.getUnsupportedMessage(this, featureId);
        throw new UnsupportedOperationException(msg);
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
     * Returns the SQL used to alter the specified column to not allow null
     * values
     * 
     * @param info
     *            the column to modify
     * @return the SQL to execute
     */
    public String getColumnNullableAlterSQL(TableColumnInfo info) {
        String alterClause = DialectUtils.MODIFY_CLAUSE;
        return DialectUtils.getColumnNullableAlterSQL(info, 
                                                      this, 
                                                      alterClause,
                                                      true);
    }

    /**
     * Returns a boolean value indicating whether or not this database dialect
     * supports renaming columns.
     * 
     * @return true if the database supports changing the name of columns; false
     *         otherwise.
     */
    public boolean supportsRenameColumn() {
        return true;
    }

    /**
     * Returns the SQL that is used to change the column name.
     * 
     * 
     * @param from
     *            the TableColumnInfo as it is
     * @param to
     *            the TableColumnInfo as it wants to be
     * 
     * @return the SQL to make the change
     */
    public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to) {
        return DialectUtils.getColumnRenameSQL(from, to);
    }

    /**
     * Returns the SQL that is used to change the column type.
     * 
     * alter table table_name modify column_name datatype
     * 
     * @param from
     *            the TableColumnInfo as it is
     * @param to
     *            the TableColumnInfo as it wants to be
     * 
     * @return the SQL to make the change
     * @throw UnsupportedOperationException if the database doesn't support
     *        modifying column types.
     */
    public String getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to)
            throws UnsupportedOperationException {
        String alterClause = DialectUtils.MODIFY_CLAUSE;
        String setClause = null;
        return DialectUtils.getColumnTypeAlterSQL(this, 
                                                  alterClause, 
                                                  setClause,
                                                  false, 
                                                  from, 
                                                  to);
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
     * @param info
     *            the column to modify and it's default value.
     * @return SQL to make the change
     */
    public String getColumnDefaultAlterSQL(TableColumnInfo info) {
        String alterClause = DialectUtils.MODIFY_CLAUSE;
        String defaultClause = DialectUtils.DEFAULT_CLAUSE;
        return DialectUtils.getColumnDefaultAlterSQL(this, 
                                                     info, 
                                                     alterClause,
                                                     true, 
                                                     defaultClause);   
    }

    /**
     * Returns the SQL command to drop the specified table's primary key.
     * 
     * @param pkName
     *            the name of the primary key that should be dropped
     * @param tableName
     *            the name of the table whose primary key should be dropped
     * @return
     */
    public String getDropPrimaryKeySQL(String pkName, String tableName) {
        return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, true);
    }

}
