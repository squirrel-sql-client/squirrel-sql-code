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

    /**
     * Returns the SQL statement to use to add a column to the specified table
     * using the information about the new column specified by info.
     * 
     * @param tableName the name of the table to create the SQL for.
     * @param info information about the new column such as type, name, etc.
     * @param dialect the HibernateDialect to use to resolve the type
     * @param addDefaultClause whether or not the dialect's SQL supports a 
     *        DEFAULT clause for columns.  
     * 
     * @return
     * @throws UnsupportedOperationException if the database doesn't support 
     *         adding columns after a table has already been created.
     */
    public static String getColumnAddSQL(String tableName, 
                                  TableColumnInfo info,
                                  HibernateDialect dialect,
                                  boolean addDefaultClause,
                                  boolean supportsNullQualifier) 
        throws UnsupportedOperationException, HibernateException 
    {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(tableName);
        result.append(" ADD ");
        result.append(info.getColumnName());
        result.append(" ");
        result.append(dialect.getTypeName(info.getDataType(), 
                                                info.getColumnSize(), 
                                                info.getColumnSize(), 
                                                info.getDecimalDigits()));

        if (addDefaultClause) {
            if (info.getDefaultValue() != null 
                    && !"".equals(info.getDefaultValue())) 
            {
                result.append(" DEFAULT ");
                if (JDBCTypeMapper.isNumberType(info.getDataType())) {
                    result.append(info.getDefaultValue());
                } else {
                    result.append("'");
                    result.append(info.getDefaultValue());
                    result.append("'");                
                }
            }            
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
        result.append(comment);
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
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(tableName);
        result.append(" DROP ");
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
}
