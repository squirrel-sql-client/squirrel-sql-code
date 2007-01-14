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

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

/**
 * An extension to the standard Hibernate DB2 dialect
 */
public class DB2Dialect extends org.hibernate.dialect.DB2Dialect 
                        implements HibernateDialect {

    public DB2Dialect() {
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
        registerColumnType(Types.DECIMAL, "decimal($p,$s)");
        registerColumnType(Types.DOUBLE, "float($p)");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "int");
        registerColumnType(Types.LONGVARBINARY, 32700, "long varchar for bit data");
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
        registerColumnType(Types.VARBINARY, 254, "varchar($l) for bit data");
        registerColumnType(Types.VARBINARY, "blob");
        registerColumnType(Types.VARCHAR, 4000, "varchar($l)");
        registerColumnType(Types.VARCHAR, 32700, "long varchar");
        // DB2 spec says max=2147483647, but the driver throws an exception
        registerColumnType(Types.VARCHAR, 1073741823, "clob($l)");
        registerColumnType(Types.VARCHAR, "clob(1073741823)");
        
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
            return 31;
        }
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#getMaxScale(int)
     */
    public int getMaxScale(int dataType) {
        if (dataType == Types.DOUBLE
                || dataType == Types.FLOAT)
        {
            // double and float have no scale - that is DECIMAL_DIGITS is null.
            // Assume that is because it's variable - "floating" point.
            return 0;
        } else {
            return getMaxPrecision(dataType);
        }
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
        return "DB2";
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
    	if (databaseProductName.trim().startsWith("DB2")) {
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
        ArrayList result = new ArrayList();
        
        StringBuffer addColumn = new StringBuffer();
        addColumn.append("ALTER TABLE ");
        addColumn.append(info.getTableName());
        addColumn.append(" ADD ");
        addColumn.append(info.getColumnName());
        addColumn.append(" ");
        addColumn.append(getTypeName(info.getDataType(), 
                                  info.getColumnSize(), 
                                  info.getColumnSize(), 
                                  info.getDecimalDigits()));
        if (info.getDefaultValue() != null) {
            addColumn.append(" WITH DEFAULT ");
            if (JDBCTypeMapper.isNumberType(info.getDataType())) {
                addColumn.append(info.getDefaultValue());
            } else {
                addColumn.append("'");
                addColumn.append(info.getDefaultValue());
                addColumn.append("'");                
            }
        }
        result.add(addColumn.toString());
        
        if (info.isNullable() == "NO") {
        // ALTER TABLE <TABLENAME> ADD CONSTRAINT NULL_FIELD CHECK (<FIELD> IS NOT
        //NULL)
            StringBuffer notnull = new StringBuffer();
            notnull.append("ALTER TABLE ");
            notnull.append(info.getTableName());
            notnull.append(" ADD CONSTRAINT ");
            notnull.append(info.getColumnName());
            notnull.append(" CHECK (");
            notnull.append(info.getColumnName());
            notnull.append(" IS NOT NULL)");            
            result.add(notnull.toString());
        } 
        
        if (info.getRemarks() != null && !"".equals(info.getRemarks())) {
            result.add(getColumnCommentAlterSQL(info));
        }
        
        return (String[])result.toArray(new String[result.size()]);
        
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
    public String getColumnCommentAlterSQL(String tableName, 
                                           String columnName, 
                                           String comment) 
        throws UnsupportedOperationException 
    {
        return DialectUtils.getColumnCommentAlterSQL(tableName, 
                                                     columnName, 
                                                     comment);
    }

    /**
     * Returns a boolean value indicating whether or not this database dialect
     * supports dropping columns from tables.
     * 
     * @return true if the database supports dropping columns; false otherwise.
     */
    public boolean supportsDropColumn() {
        return false;
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
        int featureId = DialectUtils.COLUMN_DROP_TYPE;
        String msg = DialectUtils.getUnsupportedMessage(this, featureId);
        throw new UnsupportedOperationException(msg);        
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
    public String getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints, boolean isMaterializedView){
        return DialectUtils.getTableDropSQL(iTableInfo, false, cascadeConstraints, false, DialectUtils.CASCADE_CLAUSE, false);
    }
    
    /**
     * Returns the SQL that forms the command to add a primary key to the 
     * specified table composed of the given column names.
     * 
     *    ALTER TABLE table_name ADD CONSTRAINT contraint_name PRIMARY KEY (column_name)
     * 
     * @param pkName the name of the constraint
     * @param columnNames the columns that form the key
     * @return
     */
    public String[] getAddPrimaryKeySQL(String pkName, 
                                        TableColumnInfo[] columns, 
                                        ITableInfo ti) 
    {
        return new String[] {
            DialectUtils.getAddPrimaryKeySQL(ti, pkName, columns, false)
        };
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
        return DialectUtils.getColumnCommentAlterSQL(info);
    }
    
    /**
     * Returns a boolean value indicating whether or not this database dialect
     * supports changing a column from null to not-null and vice versa.
     * 
     * @return true if the database supports dropping columns; false otherwise.
     */
    public boolean supportsAlterColumnNull() {
        return false;
    }    
    
    /**
     * Returns the SQL used to alter the specified column to not allow null 
     * values
     * 
     * This appears to work:
     * 
     * ALTER TABLE table_name ADD CONSTRAINT constraint_name CHECK (column_name IS NOT NULL)
     * 
     * However, the jdbc driver still reports the column as nullable - which means
     * I can't reliably display the correct value for this attribute in the UI.
     *  
     * I tried this alternate syntax and it fails with an exception:
     * 
     * ALTER TABLE table_name ALTER COLUMN column_name SET NOT NULL
     * 
     * Error: com.ibm.db2.jcc.b.SqlException: DB2 SQL error: 
     * SQLCODE: -104, 
     * SQLSTATE: 42601, 
     * SQLERRMC: NOT;ER COLUMN mychar SET;DEFAULT, 
     * SQL State: 42601, Error Code: -104
     * 
     * I don't see how I can practically support changing column nullability 
     * in DB2.
     * 
     * @param info the column to modify
     * @return the SQL to execute
     */
    public String getColumnNullableAlterSQL(TableColumnInfo info) {
        int featureId = DialectUtils.COLUMN_NULL_ALTER_TYPE;
        String msg = DialectUtils.getUnsupportedMessage(this, featureId);
        throw new UnsupportedOperationException(msg);
    }
    
    /**
     * Returns a boolean value indicating whether or not this database dialect
     * supports renaming columns.
     * 
     * @return true if the database supports changing the name of columns;  
     *         false otherwise.
     */
    public boolean supportsRenameColumn() {
        return false;
    }    
    
    /**
     * Returns the SQL that is used to change the column name.
     * 
     * 
     * @param from the TableColumnInfo as it is
     * @param to the TableColumnInfo as it wants to be
     * 
     * @return the SQL to make the change
     */
    public String getColumnNameAlterSQL(TableColumnInfo from, 
                                        TableColumnInfo to) 
    {
        int featureId = DialectUtils.COLUMN_NAME_ALTER_TYPE;
        String msg = DialectUtils.getUnsupportedMessage(this, featureId);
        throw new UnsupportedOperationException(msg);
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
     * ALTER TABLE table_name ALTER COLUMN column_name SET DATA TYPE data_type
     * 
     * @param from the TableColumnInfo as it is
     * @param to the TableColumnInfo as it wants to be
     * 
     * @return the SQL to make the change
     * @throw UnsupportedOperationException if the database doesn't support 
     *         modifying column types. 
     */
    public String getColumnTypeAlterSQL(TableColumnInfo from, 
                                        TableColumnInfo to)
        throws UnsupportedOperationException
    {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(from.getTableName());
        result.append(" ALTER COLUMN ");
        result.append(from.getColumnName());
        result.append(" SET DATA TYPE ");
        result.append(DialectUtils.getTypeName(to, this));
        return result.toString();
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
     * ALTER TABLE EMPLOYEE ALTER COLUMN WORKDEPTSET SET DEFAULT '123' 
     *   
     * @param info the column to modify and it's default value.
     * @return SQL to make the change
     */
    public String getColumnDefaultAlterSQL(TableColumnInfo info) {
        String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
        String defaultClause = DialectUtils.SET_DEFAULT_CLAUSE;
        return DialectUtils.getColumnDefaultAlterSQL(this, 
                                                     info, 
                                                     alterClause, 
                                                     false, 
                                                     defaultClause);
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
        return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false);
    }
    
}
