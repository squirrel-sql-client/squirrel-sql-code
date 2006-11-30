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
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

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

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DialectUtils.class);
    
    // alter column clauses
    
    public static final String ALTER_COLUMN_CLAUSE = "ALTER COLUMN";
    
    public static final String MODIFY_COLUMN_CLAUSE = "MODIFY COLUMN";
    
    public static final String MODIFY_CLAUSE = "MODIFY";
    
    public static final String COLUMN_CLAUSE = "COLUMN";
    
    // alter name clauses
    
    public static final String RENAME_COLUMN_CLAUSE = "RENAME COLUMN";
    
    public static final String RENAME_TO_CLAUSE = "RENAME TO";
    
    public static final String TO_CLAUSE = "TO";
    
    // alter default clauses
    
    public static final String DEFAULT_CLAUSE = "DEFAULT";
    
    public static final String SET_DEFAULT_CLAUSE = "SET DEFAULT";
    
    public static final String ADD_DEFAULT_CLAUSE = "ADD DEFAULT";
    
    public static final String DROP_DEFAULT_CLAUSE = "DROP DEFAULT";
    
    // alter type clauses
    
    public static final String TYPE_CLAUSE = "TYPE";
    
    public static final String SET_DATA_TYPE_CLAUSE = "SET DATA TYPE";
    
    
    // features
    
    public static final int COLUMN_COMMENT_TYPE = 0;
    public static final int COLUMN_DEFAULT_ALTER_TYPE = 1;
    public static final int COLUMN_DROP_TYPE = 2;
    public static final int COLUMN_NAME_ALTER_TYPE = 3;
    public static final int COLUMN_NULL_ALTER_TYPE = 4;
    
    
    
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
    public static String getColumnCommentAlterSQL(TableColumnInfo info) {
        return getColumnCommentAlterSQL(info.getTableName(), 
                                        info.getColumnName(), 
                                        info.getRemarks());
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
     * Returns the SQL used to alter the specified column to allow/disallow null 
     * values.
     * <br>
     * ALTER TABLE table_name &lt;alterClause&gt; column_name TYPE NULL | NOT NULL
     * <br>
     * ALTER TABLE table_name &lt;alterClause&gt; column_name NULL | NOT NULL
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
     * ALTER TABLE table_name ADD CONSTRAINT pkName PRIMARY KEY (col,...);
     * 
     * or
     * 
     * ALTER TABLE table_name ADD CONSTRAINT PRIMARY KEY (col,...) CONSTRAINT pkName;
     *  
     * @param colInfos
     * @param appendConstraintName whether or not the pkName (constraint name) 
     *                             should be placed at the end of the statement.
     *  
     * @return
     */
    public static String getAddPrimaryKeySQL(String pkName, 
                                             TableColumnInfo[] colInfos, 
                                             boolean appendConstraintName) {
        StringBuffer pkSQL = new StringBuffer();
        pkSQL.append("ALTER TABLE ");
        pkSQL.append(colInfos[0].getTableName());
        pkSQL.append(" ADD CONSTRAINT ");
        if (!appendConstraintName) {
            pkSQL.append(pkName);
        }
        pkSQL.append(" PRIMARY KEY ");
        pkSQL.append(getColumnList(colInfos));
        if (appendConstraintName) {
            pkSQL.append(" CONSTRAINT ");
            pkSQL.append(pkName);
        }
        return pkSQL.toString();
    }
    
    /**
     * Returns:
     * 
     *  (column1, column2, ...)
     * 
     * @param colInfos
     * @return
     */
    private static String getColumnList(TableColumnInfo[] colInfos) {
        StringBuffer result = new StringBuffer();
        result.append("(");
        for (int i = 0; i < colInfos.length; i++) {
            result.append(colInfos[i].getColumnName());
            if (i + 1 < colInfos.length) {
                result.append(", ");
            }
        }
        result.append(")");
        return result.toString();
    }
    
    /**
     * Returns the SQL that is used to change the column name.
     * 
     * ALTER TABLE table_name [alterClause] column_name [renameToClause] column_name
     * 
     * @param from the TableColumnInfo as it is
     * @param to the TableColumnInfo as it wants to be
     * 
     * @return the SQL to make the change
     */
    public static String getColumnNameAlterSQL(TableColumnInfo from, 
                                               TableColumnInfo to,
                                               String alterClause,
                                               String renameToClause) 
    {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(from.getTableName());
        result.append(" ");
        result.append(alterClause);
        result.append(" ");
        result.append(from.getColumnName());
        result.append(" ");
        result.append(renameToClause);
        result.append(" ");
        result.append(to.getColumnName());
        return result.toString();
    }
    
    /**
     * Returns the SQL command to change the specified column's default value
     *   
     * ALTER TABLE table_name ALTER COLUMN column_name [defaultClause] 'defaultVal'  
     * 
     * ALTER TABLE table_name ALTER COLUMN column_name [defaultClause] 1234
     * 
     * @param dialect TODO
     * @param info the column to modify and it's default value.
     * @param specifyType TODO
     *   
     * @return SQL to make the change
     */
    public static String getColumnDefaultAlterSQL(HibernateDialect dialect,
                                                  TableColumnInfo info,
                                                  String alterClause, 
                                                  boolean specifyType, 
                                                  String defaultClause) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" ");
        result.append(alterClause);
        result.append(" ");
        result.append(info.getColumnName());
        result.append(" ");
        if (specifyType) {
            result.append(getTypeName(info, dialect));
        }
        result.append(" ");
        result.append(defaultClause);
        result.append(" ");
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
     * Returns the SQL that is used to change the column type.
     * 
     * ALTER TABLE table_name alter_clause column_name [setClause] data_type
     *  
     * ALTER TABLE table_name alter_clause column_name column_name [setClause] data_type
     * 
     * @param from the TableColumnInfo as it is
     * @param to the TableColumnInfo as it wants to be
     * 
     * @return the SQL to make the change
     * @throw UnsupportedOperationException if the database doesn't support 
     *         modifying column types. 
     */
    public static String getColumnTypeAlterSQL(HibernateDialect dialect,
                                               String alterClause,
                                               String setClause,
                                               boolean repeatColumn,
                                               TableColumnInfo from, 
                                               TableColumnInfo to)
        throws UnsupportedOperationException
    {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(to.getTableName());
        result.append(" ");
        result.append(alterClause);
        result.append(" ");
        if (repeatColumn) {
            result.append(to.getColumnName());
            result.append(" ");
        }
        result.append(to.getColumnName());
        result.append(" ");
        if (setClause != null && !"".equals(setClause)) {
            result.append(setClause);
            result.append(" ");
        }
        result.append(getTypeName(to, dialect));
        return result.toString();
    }    
    
    /**
     * Returns the SQL that is used to change the column name.
     * 
     * RENAME COLUMN table_name.column_name TO new_column_name
     * 
     * @param from the TableColumnInfo as it is
     * @param to the TableColumnInfo as it wants to be
     * 
     * @return the SQL to make the change
     */
    public static String getColumnRenameSQL(TableColumnInfo from, 
                                            TableColumnInfo to) {
        StringBuffer result = new StringBuffer();
        result.append("RENAME COLUMN ");
        result.append(from.getTableName());
        result.append(".");
        result.append(from.getColumnName());
        result.append(" TO ");
        result.append(to.getColumnName());
        return result.toString();
    }
    
    public static String getUnsupportedMessage(HibernateDialect dialect,
                                               int featureId) 
        throws UnsupportedOperationException
    {
        String msg = null;
        switch (featureId) {
            case COLUMN_COMMENT_TYPE:
                //i18n[DialectUtils.columnCommentUnsupported={0} doesn''t support
                //column comments]
                msg = s_stringMgr.getString("DialectUtils.columnCommentUnsupported",
                                            dialect.getDisplayName());
                break;
            case COLUMN_DEFAULT_ALTER_TYPE:
                //i18n[DialectUtils.columnDefaultUnsupported={0} doesn''t support
                //altering a column''s default value]
                msg = s_stringMgr.getString("DialectUtils.columnDefaultUnsupported",
                                            dialect.getDisplayName());
                break;                
                
            case COLUMN_DROP_TYPE:
                //i18n[DialectUtils.columnDropUnsupported={0} doesn''t support
                //dropping a column]
                msg = s_stringMgr.getString("DialectUtils.columnDropUnsupported",
                                            dialect.getDisplayName());
                break;                                
            case COLUMN_NAME_ALTER_TYPE:
                //i18n[DialectUtils.columnNameUnsupported={0} doesn''t support 
                //altering a column''s name]
                msg = s_stringMgr.getString("DialectUtils.columnNameUnsupported",
                                            dialect.getDisplayName());
                break;                                
            case COLUMN_NULL_ALTER_TYPE:
                //i18n[DialectUtils.columnNullUnsupported={0} doesn''t support
                //altering a column's nullable attribute]
                msg = s_stringMgr.getString("DialectUtils.columnCommentUnsupported",
                                            dialect.getDisplayName());
                break;
            default:
                throw new IllegalArgumentException("Unknown featureId: "+featureId);
        }
        return msg;
    }
    
    /**
     * Returns the SQL command to drop the specified table's primary key.
     * 
     * alter table table_name drop primary key
     * 
     * or 
     * 
     * alter table table_name drop constraint [pkName]
     * 
     * @param pkName the name of the primary key that should be dropped
     * @param tableName the name of the table whose primary key should be 
     *                  dropped
     * @param useConstraintName TODO
     * @return
     */
    public static String getDropPrimaryKeySQL(String pkName, 
                                              String tableName, 
                                              boolean useConstraintName) {
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(tableName);
        if (useConstraintName) {
            result.append(" DROP CONSTRAINT ");
            result.append(pkName);
        } else {
            result.append(" DROP PRIMARY KEY");
        }
        return result.toString();
    }
    
    /**
     * CREATE UNIQUE INDEX indexName ON tableName (columns);
     * 
     * @param indexName
     * @param tableName
     * @param columns
     * @return
     */
    public static String getAddIndexSQL(String indexName,
                                        boolean unique,
                                        TableColumnInfo[] columns) 
    {
        StringBuffer result = new StringBuffer();
        if (unique) {
            result.append("CREATE UNIQUE INDEX ");
        } else {
            result.append("CREATE INDEX ");
        }
        result.append(indexName);
        result.append(" ON ");
        result.append(columns[0].getTableName());
        result.append(" ");
        result.append(getColumnList(columns));
        return result.toString();
    }
    
}
