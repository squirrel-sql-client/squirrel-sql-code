/*
 * Copyright (C) 2005 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.dbcopy.dialects;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import org.hibernate.HibernateException;

/**
 * An interface for methods needed by database dialects in order to copy 
 * database objects to or from them.  
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
    
}
