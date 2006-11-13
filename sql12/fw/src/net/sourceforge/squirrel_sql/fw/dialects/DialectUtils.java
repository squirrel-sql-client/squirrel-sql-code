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

import java.util.ArrayList;

import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.hibernate.HibernateException;

/**
 * A simple utility class in which to place common code shared amongst the 
 * dialects. Since the dialects all inherit behavior from specific server 
 * dialects, it is not possible to inherit common behavior from a single base 
 * class.  So, this class is where common code is located.
 * 
 * @author rmmannin
 */
public class DialectUtils {

    public static final String ALTER_COLUMN_CLAUSE = "ALTER COLUMN";
    
    public static final String MODIFY_COLUMN_CLAUSE = "MODIFY COLUMN";
    
    /**
     * Returns the SQL statement to use to add a column to the specified table
     * using the information about the new column specified by info.
     * @param info information about the new column such as type, name, etc.
     * @param dialect the HibernateDialect to use to resolve the type
     * @param addDefaultClause whether or not the dialect's SQL supports a 
     *        DEFAULT clause for columns.  
     * 
     * @return
     * @throws UnsupportedOperationException if the database doesn't support 
     *         adding columns after a table has already been created.
     */
    public static String getColumnAddSQL(TableColumnInfo info, 
                                         HibernateDialect dialect,
                                         boolean addDefaultClause,
                                         boolean supportsNullQualifier) 
        throws UnsupportedOperationException, HibernateException 
    {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" ");
        result.append(dialect.getAddColumnString().toUpperCase());
        result.append(" ");
        result.append(info.getColumnName());
        result.append(" ");
        result.append(dialect.getTypeName(info.getDataType(), 
                                                info.getColumnSize(), 
                                                info.getColumnSize(), 
                                                info.getDecimalDigits()));

        if (addDefaultClause) {
            appendDefaultClause(info, result);
        }
        
        if (info.isNullable().equals("NO")) {
            result.append(" NOT NULL ");
        } else {
            if (supportsNullQualifier) {
                result.append(" NULL ");
            }
        }
        return result.toString();
    }
    
    public static String appendDefaultClause(TableColumnInfo info, 
                                             StringBuffer buffer) {

        if (info.getDefaultValue() != null 
                && !"".equals(info.getDefaultValue())) 
        {
            buffer.append(" DEFAULT ");
            if (JDBCTypeMapper.isNumberType(info.getDataType())) {
                buffer.append(info.getDefaultValue());
            } else {
                buffer.append("'");
                buffer.append(info.getDefaultValue());
                buffer.append("'");                
            }
        }                    
        return buffer.toString();
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
    public static String getColumnCommentAlterSQL(String tableName, 
                                                  String columnName, 
                                                  String comment) 
    {
        StringBuffer result = new StringBuffer();
        result.append("COMMENT ON COLUMN ");
        result.append(tableName);
        result.append(".");
        result.append(columnName);
        result.append(" IS '");
        if (comment != null && !"".equals(comment)) {
            result.append(comment);
        }
        result.append("'");
        return result.toString();
    }
    
    /**
     * 
     * @param tableName
     * @param columnName
     * @return
     */
    public static String getColumnDropSQL(String tableName, 
                                          String columnName) {
        return getColumnDropSQL(tableName, columnName, "DROP");
    }

    /**
     * 
     * @param tableName
     * @param columnName
     * @return
     */
    public static String getColumnDropSQL(String tableName, 
                                          String columnName,
                                          String dropClause) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(tableName);
        result.append(" ");
        result.append(dropClause);
        result.append(" ");
        result.append(columnName);
        return result.toString();
    }
    
    /**
     * Returns the SQL that forms the command to drop the specified table.  If
     * cascade contraints is supported by the dialect and cascadeConstraints is
     * true, then a drop statement with cascade constraints clause will be 
     * formed.
     * 
     * @param tableName the table to drop
     * @param supportsCascade whether or not the cascade clause should be added.
     * @param cascadeValue whether or not to drop any FKs that may 
     * reference the specified table.
     * 
     * @return the drop SQL command.
     */
    public static String getTableDropSQL(String tableName, 
                                         boolean supportsCascade, 
                                         boolean cascadeValue) 
    {
        StringBuffer result = new StringBuffer();
        result.append("DROP TABLE ");
        result.append(tableName);
        if (supportsCascade && cascadeValue) {
            result.append(" CASCADE ");
        }
        return result.toString();
    }
    
    public static String getTypeName(TableColumnInfo info, 
                                     HibernateDialect dialect) 
    {
        return dialect.getTypeName(info.getDataType(), 
                                   info.getColumnSize(), 
                                   info.getColumnSize(), 
                                   info.getDecimalDigits());
    }
    
    /**
     * Returns the SQL used to alter the specified column to not allow null 
     * values
     * 
     * @param info the column to modify
     * @param dialect the HibernateDialect representing the target database.
     * @param alterClause the alter column clause (e.g. ALTER COLUMN )
     * @param specifyType whether or not the column type needs to be specified
     * 
     * @return the SQL to execute
     */
    public static String getColumnNullableAlterSQL(TableColumnInfo info, 
                                                   HibernateDialect dialect,
                                                   String alterClause,
                                                   boolean specifyType) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" ");
        result.append(alterClause);
        result.append(" ");
        result.append(info.getColumnName());
        if (specifyType) {
            result.append(" ");
            result.append(getTypeName(info, dialect));
            result.append(" ");
        }
        if (info.isNullable().equalsIgnoreCase("YES")) { 
            result.append(" NULL");
        } else {
            result.append(" NOT NULL");
        }
        return result.toString();
    }
    
    /**
     * Populates the specified ArrayList with SQL statement(s) required to 
     * convert each of the columns to not null.  This is typically needed in 
     * some databases when adding a primary key (some dbs do this step 
     * automatically)
     * 
     * @param colInfos the columns to be made not null
     * @param dialect 
     * @param result
     */
    public static void getMultiColNotNullSQL(TableColumnInfo[] colInfos,  
                                             HibernateDialect dialect,
                                             String alterClause,
                                             boolean specifyType,
                                             ArrayList result) 
    {
        for (int i = 0; i < colInfos.length; i++) {
            StringBuffer notNullSQL = new StringBuffer();
            notNullSQL.append("ALTER TABLE ");
            notNullSQL.append(colInfos[i].getTableName());
            notNullSQL.append(" ");
            notNullSQL.append(alterClause);
            notNullSQL.append(" ");
            notNullSQL.append(colInfos[i].getColumnName());
            if (specifyType) {
                notNullSQL.append(" ");
                notNullSQL.append(DialectUtils.getTypeName(colInfos[i], dialect));
            }
            notNullSQL.append(" NOT NULL");
            result.add(notNullSQL.toString());
        }
    }
    
    /**
     * Returns the SQL for creating a primary key consisting of the specified 
     * colInfos.
     *  
     * @param colInfos
     * @return
     */
    public static String getAddPrimaryKeySQL(String pkName, 
                                             TableColumnInfo[] colInfos) {
        StringBuffer pkSQL = new StringBuffer();
        pkSQL.append("ALTER TABLE ");
        pkSQL.append(colInfos[0].getTableName());
        pkSQL.append(" ADD CONSTRAINT ");
        pkSQL.append(pkName);
        pkSQL.append(" PRIMARY KEY (");
        for (int i = 0; i < colInfos.length; i++) {
            pkSQL.append(colInfos[i].getColumnName());
            if (i + 1 < colInfos.length) {
                pkSQL.append(", ");
            }
        }
        pkSQL.append(")");
        return pkSQL.toString();
    }
}
