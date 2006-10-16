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

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

/**
 * An extension to the standard Hibernate dialect
 * 
 * TODO: Progress specification says that the sum of all column lengths of a 
 *       table row may not exceed 31960.  Need to add an interface method to 
 *       the HibernateDialect interface that takes an array of lengths and 
 *       and checks the sum of columns for a row.  This maximum number may be 
 *       different across databases so this check method needs to be 
 *       implemented by each dialect.
 */
public class FrontBaseDialect extends org.hibernate.dialect.FrontBaseDialect 
                             implements HibernateDialect {
    
    public FrontBaseDialect() {
        super();
        registerColumnType(Types.BIGINT, "longint");
        // I tried to use the length from the source database (PostgreSQL) which
        // yielded 8192 for this test. So the column def in FB was 
        // binary_column bit varying(8192).  Yet it gave me an exception that
        // indicated truncation (string data right truncation???)
        // So for now, go back to hard-coded maximal length.
        //registerColumnType(Types.BINARY, 2147000000,"bit varying($l)");
        registerColumnType(Types.BINARY, "bit varying(2147000000)");
        registerColumnType(Types.BIT, 2147000000, "bit($l)");
        registerColumnType(Types.BIT, "bit(2147000000)");
        // Anticipate the same issue for BLOBS as for BINARY and LONGVARBINARY.
        //registerColumnType(Types.BLOB, 2147000000, "bit varying($l)");
        registerColumnType(Types.BLOB, "bit varying(2147000000)");
        // Don't use bit(1) for boolean as Frontbase then reports it as BINARY
        // type instead of BIT.
        registerColumnType(Types.BOOLEAN, "tinyint");
        registerColumnType(Types.CHAR, 2147000000, "char($l)");
        registerColumnType(Types.CHAR, "char(2147000000)");
        registerColumnType(Types.CLOB, 2147000000, "varchar($l)");
        registerColumnType(Types.CLOB, "varchar(2147000000)");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p,2)");
        registerColumnType(Types.DOUBLE, "double precision");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "integer");       
        // When I tried using a length for LONGVARBINARY that is the max 
        // length of the source records, I get the following exception:
        //
        // Exception condition 239. Data exception - string data, right truncation.
        // I tried bit varying(32767) with data that had max length of 16384. So
        // it's back to maximum length hard-coded for now.
        // registerColumnType(Types.LONGVARBINARY, 2147000000, "bit varying($l)");
        registerColumnType(Types.LONGVARBINARY, "bit varying(2147000000)");
        registerColumnType(Types.LONGVARCHAR, 2147000000, "varchar($l)");
        registerColumnType(Types.LONGVARCHAR, "varchar(2147000000)");
        registerColumnType(Types.NUMERIC, 19, "numeric($p,$s)");
        registerColumnType(Types.NUMERIC, "double precision");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "tinyint");
        // Anticipate the same issue for VARBINARY as for BINARY and LONGVARBINARY.
        // registerColumnType(Types.VARBINARY, 2147000000, "bit varying($l)");
        registerColumnType(Types.VARBINARY, "bit varying(2147000000)");
        registerColumnType(Types.VARCHAR, 2147000000,"varchar($l)");
        registerColumnType(Types.VARCHAR, "varchar(2147000000)");
    }    
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#canPasteTo(net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType)
     */
    public boolean canPasteTo(IDatabaseObjectInfo info) {
        boolean result = true;
        DatabaseObjectType type = info.getDatabaseObjectType();
        if (type.getName().equalsIgnoreCase("catalog")
                || type.getName().equalsIgnoreCase("database")) {
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
        return "character_length";
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
        if (dataType == Types.NUMERIC
                || dataType == Types.FLOAT) 
        {
            return 19;
        }
        return 36;
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
        return columnSize;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getColumnLength(int, int)
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
        return "FrontBase";
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
    	if (databaseProductName.trim().startsWith("FrontBase")) {
    		// We don't yet have the need to discriminate by version.
    		return true;
    	}
		return false;
	}        
    
    /**
     * Returns the SQL statement to use to add a column to the specified table
     * using the information about the new column specified by info.
     * 
     * @param tableName the name of the table to create the SQL for.
     * @param info information about the new column such as type, name, etc.
     * @return
     * @throws UnsupportedOperationException if the database doesn't support 
     *         adding columns after a table has already been created.
     */
    public String[] getColumnAddSQL(String tableName, TableColumnInfo info) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This database dialect doesn't support adding columns to tables");
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
     * Returns the SQL statement to use to add a comment to the specified 
     * column of the specified table.
     * 
     * @param tableName the name of the table to create the SQL for.
     * @param columnName the name of the column to create the SQL for.
     * @param comment the comment to add.
     * @return
     * @throws UnsupportedOperationException if the database doesn't support 
     *         annotating columns with a comment.
     */
    public String getColumnCommentAlterSQL(String tableName, String columnName, String comment) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This database dialect doesn't support adding comments to columns");
    }
    
}
