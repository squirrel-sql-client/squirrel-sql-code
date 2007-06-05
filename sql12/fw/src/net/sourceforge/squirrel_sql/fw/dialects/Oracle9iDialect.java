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
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.dialect.Oracle9Dialect;

/**
 * A description of this class goes here...
 */

public class Oracle9iDialect extends Oracle9Dialect 
                             implements HibernateDialect {

    public Oracle9iDialect() {
        super();
        registerColumnType(Types.BIGINT, "number($p)");
        registerColumnType(Types.BINARY, 2000, "raw($l)");
        registerColumnType(Types.BINARY, "blob");
        registerColumnType(Types.BIT, "smallint");
        registerColumnType(Types.BLOB, "blob");
        registerColumnType(Types.BOOLEAN, "smallint");
        registerColumnType(Types.CHAR, 2000, "char($l)");
        registerColumnType(Types.CHAR, 4000, "varchar2($l)");
        registerColumnType(Types.CHAR, "clob");
        registerColumnType(Types.CLOB, "clob");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.DECIMAL, "decimal($p)");
        registerColumnType(Types.DOUBLE, "float($p)");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.INTEGER, "int");        
        registerColumnType(Types.LONGVARBINARY, "blob");
        registerColumnType(Types.LONGVARCHAR, 4000, "varchar2($l)");
        registerColumnType(Types.LONGVARCHAR, "clob");
        registerColumnType(Types.NUMERIC, "number($p)");
        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.TIME, "date");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.TINYINT, "smallint");
        registerColumnType(Types.VARBINARY, "blob");
        registerColumnType(Types.VARCHAR, 4000, "varchar2($l)");
        registerColumnType(Types.VARCHAR, "clob");
        // Total Hack!  Type OTHER(1111) can be other types as well?
        registerColumnType(Types.OTHER, 4000, "varchar2(4000)");
        registerColumnType(Types.OTHER, "clob");
        
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
        return "Oracle";
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
    	if (databaseProductName.trim().toLowerCase().startsWith("oracle")) {
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
    public String[] getColumnAddSQL(TableColumnInfo info) throws UnsupportedOperationException {
        if (info.getRemarks() != null && !"".equals(info.getRemarks())) {
            return new String[] {
                DialectUtils.getColumnAddSQL(info, this, true, true, true),
                DialectUtils.getColumnCommentAlterSQL(info)
            };            
        } else {
            return new String[] {
                DialectUtils.getColumnAddSQL(info, this, true, true, true)
            };            
        }
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
     * @param tableName the name of the table to create the SQL for.
     * 
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
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(tableName);
        result.append(" DROP COLUMN ");
        result.append(columnName);
        return result.toString();
    }
        
    /**
     * Returns the SQL that forms the command to drop the specified table.  If
     * cascade contraints is supported by the dialect and cascadeConstraints is
     * true, then a drop statement with cascade constraints clause will be 
     * formed.
     * @param cascadeConstraints whether or not to drop any FKs that may 
     * reference the specified table.
     * @param ti the table to drop
     * 
     * @return the drop SQL command.
     */
    public List<String> getTableDropSQL(ITableInfo ti, 
                                  boolean cascadeConstraints, 
                                  boolean isMaterializedView)
    {
        String cascadeClause = "";
        if (!isMaterializedView) {
            cascadeClause = DialectUtils.CASCADE_CONSTRAINTS_CLAUSE;            
        }

        return DialectUtils.getTableDropSQL(ti,  
                                            true, 
                                            cascadeConstraints, 
                                            true, 
                                            cascadeClause, 
                                            isMaterializedView);
    }
    
    /**
     * Returns the SQL that forms the command to add a primary key to the 
     * specified table composed of the given column names.
     * @param columns the columns that form the key
     * @return
     */
    public String[] getAddPrimaryKeySQL(String pkName,
                                        TableColumnInfo[] columns, ITableInfo ti) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(ti.getQualifiedName());
        result.append(" ADD CONSTRAINT ");
        result.append(pkName);
        result.append(" PRIMARY KEY (");
        for (int i = 0; i < columns.length; i++) {
            result.append(columns[i].getColumnName());
            if (i + 1 < columns.length) {
                result.append(", ");
            }
        }
        result.append(")");
        return new String[] { result.toString() };
    }
    
    /**
     * Returns the SQL used to alter the specified column to allow/disallow null 
     * values, based on the value of isNullable.
     * 
     * alter table test modify mycol not null
     * 
     * @param info the column to modify
     * @return the SQL to execute
     */
    public String getColumnNullableAlterSQL(TableColumnInfo info) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" MODIFY ");
        result.append(info.getColumnName());
        if (info.isNullable().equals("YES")) {
            result.append(" NULL");
        } else {
            result.append(" NOT NULL");
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
        return true;
    }    
    
    /**
     * Returns the SQL that is used to change the column name.
     * 
     * @param from the TableColumnInfo as it is
     * @param to the TableColumnInfo as it wants to be
     * 
     * @return the SQL to make the change
     */
    public String getColumnNameAlterSQL(TableColumnInfo from, 
                                        TableColumnInfo to) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(from.getTableName());
        result.append(" RENAME COLUMN ");
        result.append(from.getColumnName());
        result.append(" TO ");
        result.append(to.getColumnName());
        return result.toString();
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
     * alter table test modify (mycol varchar(100))
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
        ArrayList<String> result = new ArrayList<String>();
        
        // Oracle won't allow in-place conversion between CLOB and VARCHAR 
        if ( (from.getDataType() == Types.VARCHAR && to.getDataType() == Types.CLOB)
                || (from.getDataType() == Types.CLOB && to.getDataType() == Types.VARCHAR) ) 
        {
            // add <columnName>_2 null as CLOB
            TableColumnInfo newInfo = 
                DialectUtils.getRenamedColumn(to, to.getColumnName()+"_2");
            
            String[] addSQL = this.getColumnAddSQL(newInfo);
            for (int i = 0; i < addSQL.length; i++) {
                result.add(addSQL[i]);
            }
            
            // update table set <columnName>_2 = <columnName>
            StringBuilder updateSQL = new StringBuilder();
            updateSQL.append("update ");
            updateSQL.append(from.getTableName());
            updateSQL.append(" set ");
            updateSQL.append(newInfo.getColumnName());
            updateSQL.append(" = ");
            updateSQL.append(from.getColumnName());
            result.add(updateSQL.toString());
            
            // drop <columnName>
            String dropSQL = 
                getColumnDropSQL(from.getTableName(), from.getColumnName());
            result.add(dropSQL);
            
            // rename <columnName>_2 to <columnName>
            String renameSQL = this.getColumnNameAlterSQL(newInfo, to);
            result.add(renameSQL);
        } 
        else 
        {
            StringBuffer tmp = new StringBuffer();
            tmp.append("ALTER TABLE ");
            tmp.append(from.getTableName());
            tmp.append(" MODIFY (");
            tmp.append(from.getColumnName());
            tmp.append(" ");
            tmp.append(DialectUtils.getTypeName(to, this));
            tmp.append(")");
            result.add(tmp.toString());
        }
        return result;
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
     * alter table test modify mychar default 'foo'
     * 
     * alter table test modify nullint default 0   
     *   
     * @param info the column to modify and it's default value.
     * @return SQL to make the change
     */
    public String getColumnDefaultAlterSQL(TableColumnInfo info) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" MODIFY ");
        result.append(info.getColumnName());
        result.append(" DEFAULT ");
        if (JDBCTypeMapper.isNumberType(info.getDataType())) {
            result.append(info.getDefaultValue());
        } else {
            result.append("'");
            result.append(info.getDefaultValue());
            result.append("'");
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
        return DialectUtils.getDropPrimaryKeySQL(pkName, tableName, false, false);
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
    
}
