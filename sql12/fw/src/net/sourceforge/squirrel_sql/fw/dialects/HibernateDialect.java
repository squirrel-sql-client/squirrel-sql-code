/*
 * Copyright (C) 2006 Rob Manning
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
package net.sourceforge.squirrel_sql.fw.dialects;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import java.util.List;

import org.hibernate.HibernateException;

/**
 * An interface for methods implemented by database dialects for the purpose of 
 * handling standard and non-standard SQL and database types. 
 */
public interface HibernateDialect {
    
    /**
     * Get the name of the database type associated with the given
     * <tt>java.sql.Types</tt> typecode.
     * @param code      <tt>java.sql.Types</tt> typecode
     * @param length    the length or precision of the column
     * @param precision the precision of the column
     * @param scale the scale of the column
     *
     * @return the database type name
     * @throws HibernateException
     */    
    String getTypeName(int code, int length, 
                       int precision, int scale) throws HibernateException;
    
    /**
     * Returns a boolean indicating whether or not the specified database object
     * can be pasted into for this database dialect.  Some databases support the
     * notion of schemas where tables live, and in those cases pasting to a 
     * database object in the object tree is not really appropriate.  However, 
     * other databases don't support schemas (like Axion, Firebird)
     * 
     * @param info
     * @return
     */
    boolean canPasteTo(IDatabaseObjectInfo info);
    
    /**
     * Returns a boolean value indicating whether or not this database dialect
     * supports table creation statements where the table name is prefixed by
     * the schema.
     * 
     * @return
     */
    boolean supportsSchemasInTableDefinition();
    
    /**
     * Returns the string that should be appended to a column declaration when
     * nulls are allowed in the column.
     * 
     * @return the "nulls are allowed in this column" string for a table 
     *         declaration
     */
    String getNullColumnString();
    
    /**
     * Returns the name of the aggregate function that determines the max value
     * of an expression.
     *  
     * @return the name of the function to be applied to a set to determine the 
     *         element with the highest numeric value.
     */
    String getMaxFunction();
    
    /**
     * Returns the name of the function that measures the length of a character
     * string.
     * 
     * @param dataType the java.sql.Types data type.  Some databases have 
     *                 different length functions for different data types. 
     * 
     * @return the name of the function to be applied to a column to determine
     *         the length of that column in a particular record.
     */
    String getLengthFunction(int dataType);
    
    /**
     * Returns the maximum precision allowed by the database for number type 
     * fields that specify the length of the number to the left of the decimal
     * point in digits.  If the HibernateDialect implementation doesn't ever
     * use $p in any call to registerColumnType(), then this maximum precsision
     * will not be used.
     * 
     * @param dataType the java.sql.Types data type.
     * 
     * @return the maximum number that can be used in a column declaration for 
     *         precision for the specified type.
     */
    int getMaxPrecision(int dataType);
    
    /**
     * Returns the maximum scale allowed by the database for number type 
     * fields that specify the length of the number to the right of the decimal
     * point in digits.  If the HibernateDialect implementation doesn't ever
     * use $s in any call to registerColumnType(), then this maximum scale
     * will not be used.
     * 
     * @param dataType the java.sql.Types data type.
     * 
     * @return the maximum number that can be used in a column declaration for 
     *         scale for the specified type.
     */
    int getMaxScale(int dataType);
    
    /**
     * Returns the number of digits of precision is represented by the specifed
     * columnSize for the specified dataType. Some DBs represent precision as 
     * the total number of digits on the right or left of the decimal.  That is
     * what we want.  Others (like PostgreSQL) give the number of bytes of 
     * storage a column can use - less than useful, since the SQL-92 says 
     * "number of digits" and this is what most other DBs use. 
     * 
     * @param columnSize the size of the column as reported by the driver. 
     * @param dataType the java.sql.Types data type.
     * 
     * @return a number indicating the total number of digits (includes both
     *         sides of the decimal point) the column can represent.
     */
    int getPrecisionDigits(int columnSize, int dataType);
    
    /**
     * Some jdbc drivers are hopelessly broken with regard to reporting the 
     * COLUMN_SIZE.  For example, MaxDB has a "long byte" data type which can
     * store up to 2G of data, yet the driver reports that the column size is 
     * "8" - real helpful.  So for drivers that have this problem, return the 
     * "proper" maximum column length for the specified dataType.  If the driver
     * doesn't have this problem, just return the columnSize.
     * 
     * @param columnSize the size of the column as reported by the jdbc driver
     * @param dataType the type of the column.
     * 
     * @return the specified columnSize if the jdbc driver isn't broken; 
     *         otherwise, the maximum column size for the specified dataType if
     *         the driver is broken. 
     */
    int getColumnLength(int columnSize, int dataType);
    
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
    boolean supportsProduct(String databaseProductName, 
    					    String databaseProductVersion);
    
    /**
     * The string which identifies this dialect in the dialect chooser.
     * 
     * @return a descriptive name that tells the user what database this dialect
     *         is design to work with.
     */    
    String getDisplayName();
    
    /**
     * Returns the SQL statement to use to add a column to the specified table
     * using the information about the new column specified by info.
     * @param info information about the new column such as type, name, etc.
     * 
     * @return
     * @throws UnsupportedOperationException if the database doesn't support 
     *         adding columns after a table has already been created.
     * @throws HibernateException if the type in the specified info isn't 
     *         supported by this dialect.
     */
    String[] getColumnAddSQL(TableColumnInfo info) 
        throws HibernateException, UnsupportedOperationException;
    
    /**
     * Returns a boolean value indicating whether or not this dialect supports
     * adding comments to columns.
     * 
     * @return true if column comments are supported; false otherwise.
     */
    boolean supportsColumnComment();
    
    /**
     * Returns the SQL statement to use to add a comment to the specified 
     * column of the specified table.
     * @param info information about the column such as type, name, etc.
     * @return
     * @throws UnsupportedOperationException if the database doesn't support 
     *         annotating columns with a comment.
     */
    public String getColumnCommentAlterSQL(TableColumnInfo info) throws UnsupportedOperationException; 
    

    /**
     * Returns a boolean value indicating whether or not this database dialect
     * supports dropping columns from tables.
     * 
     * @return true if the database supports dropping columns; false otherwise.
     */
    boolean supportsDropColumn();

    /**
     * Returns a boolean value indicating whether or not this database dialect
     * supports changing a column from null to not-null and vice versa.
     * 
     * @return true if the database supports dropping columns; false otherwise.
     */
    boolean supportsAlterColumnNull();
    
    
    /**
     * Returns the SQL that forms the command to drop the specified colum in the
     * specified table.
     * 
     * @param tableName the name of the table that has the column
     * @param columnName the name of the column to drop.
     * @return
     * @throw UnsupportedOperationException if the database doesn't support 
     *         dropping columns. 
     */
    String getColumnDropSQL(String tableName, String columnName)
        throws UnsupportedOperationException;
    
    /**
     * Returns the SQL that forms the command to drop the specified table.  If
     * cascade contraints is supported by the dialect and cascadeConstraints is
     * true, then a drop statement with cascade constraints clause will be 
     * formed.
     * 
     * @param iTableInfo the table to drop
     * @param cascadeConstraints whether or not to drop any FKs that may 
     * reference the specified table.
     * @param isMaterializedView TODO
     * @return the drop SQL command.
     */
    List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints, boolean isMaterializedView);
    
    /**
     * Returns the SQL that forms the command to add a primary key to the 
     * specified table composed of the given column names.
     * 
     * @param pkName the name of the constraint
     * @param ti TODO
     * @param columnNames the columns that form the key
     * @return
     */
    String[] getAddPrimaryKeySQL(String pkName, TableColumnInfo[] colInfos, ITableInfo ti);
 
    /**
     * Returns the SQL fragment for adding a column in an alter table statment.
     * @return
     */
    String getAddColumnString();
    
    /**
     * Returns the SQL used to alter the nullability of the specified column 
     * 
     * @param info the column to modify
     * @return the SQL to execute
     */
    String getColumnNullableAlterSQL(TableColumnInfo info);
        
    /**
     * Returns a boolean value indicating whether or not this database dialect
     * supports renaming columns.
     * 
     * @return true if the database supports changing the name of columns;  
     *         false otherwise.
     */
    boolean supportsRenameColumn();    
    
    /**
     * Returns the SQL that is used to change the column name.
     * 
     * @param from the TableColumnInfo as it is
     * @param to the TableColumnInfo as it wants to be
     * 
     * @return the SQL to make the change
     */
    String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to);
    
    /**
     * Returns a boolean value indicating whether or not this dialect supports 
     * modifying a columns type.
     * 
     * @return true if supported; false otherwise
     */
    boolean supportsAlterColumnType();
    
    
    /**
     * Returns the SQL that is used to change the column type.
     * 
     * @param from the TableColumnInfo as it is
     * @param to the TableColumnInfo as it wants to be
     * 
     * @return the SQL to make the change
     * @throw UnsupportedOperationException if the database doesn't support 
     *         modifying column types. 
     */
    List<String> getColumnTypeAlterSQL(TableColumnInfo from, TableColumnInfo to)
        throws UnsupportedOperationException;
    
    /**
     * Returns a boolean value indicating whether or not this database dialect
     * supports changing a column's default value.
     * 
     * @return true if the database supports modifying column defaults; false 
     *         otherwise
     */
    boolean supportsAlterColumnDefault();
    
    /**
     * Returns the SQL command to change the specified column's default value
     *   
     * @param info the column to modify and it's default value.
     * @return SQL to make the change
     */
    String getColumnDefaultAlterSQL(TableColumnInfo info);
    

    /**
     * Returns the SQL command to drop the specified table's primary key.
     * 
     * @param pkName the name of the primary key that should be dropped
     * @param tableName the name of the table whose primary key should be 
     *                  dropped
     * @return
     */
    String getDropPrimaryKeySQL(String pkName, String tableName);
    
}
