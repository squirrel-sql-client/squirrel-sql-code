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
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * An extension to the standard Derby dialect
 */
public class DerbyDialect extends DB2Dialect 
                          implements HibernateDialect {

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DerbyDialect.class);   
    
    private static interface i18n {
        //i18n[DerbyDialect.typeMessage=Derby doesn't allow the column type to 
        //be altered]
        String TYPE_MESSAGE = s_stringMgr.getString("DerbyDialect.typeMessage"); 
        
        //i18n[DerbyDialect.varcharMessage=Derby only allows varchar columns 
        //to be altered]
        String VARCHAR_MESSAGE = 
            s_stringMgr.getString("DerbyDialect.varcharMessage");
        
        //i18n[DerbyDialect.columnLengthMessage=Derby only allows varchar 
        //column length to be increased]
        String COLUMN_LENGTH_MESSAGE = 
            s_stringMgr.getString("DerbyDialect.columnLengthMessage");
    }
    
    public DerbyDialect() {
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
        registerColumnType(Types.DECIMAL, "decimal($p)");
        // Derby is real close to DB2.  Only difference I've found so far is 48
        // instead of 53 for float length llimit.
        registerColumnType(Types.DOUBLE, "float($p)");
        registerColumnType(Types.FLOAT, "float($p)");        
        registerColumnType(Types.INTEGER, "int");        
        registerColumnType(Types.LONGVARBINARY, 32700, "long varchar for bit data");
        // DB2 spec says max=2147483647, but the driver throws an exception
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
        registerColumnType(Types.VARBINARY, 254, "long varchar for bit data");
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
        // TODO Auto-generated method stub
        return true;
    }    
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.HibernateDialect#supportsSchemasInTableDefinition()
     */
    public boolean supportsSchemasInTableDefinition() {
        return true;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.DB2Dialect#getMaxPrecision(int)
     */
    public int getMaxPrecision(int dataType) {
        if (dataType == Types.DOUBLE
                || dataType == Types.FLOAT)
        {
            return 48;
        } else {
            return 31;
        }
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.DB2Dialect#getMaxScale(int)
     */
    public int getMaxScale(int dataType) {
        return getMaxPrecision(dataType);
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
        return "Derby";
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
    	if (databaseProductName.trim().startsWith("Apache Derby")) {
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
 	public String[] getAddColumnSQL(TableColumnInfo column, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
 	{

 		boolean addDefaultClause = true;
		boolean supportsNullQualifier = false;
		boolean addNullClause = true;
   	 
		String sql =
			DialectUtils.getAddColumSQL(column,
				this,
				addDefaultClause,
				supportsNullQualifier,
				addNullClause,
				qualifier,
				prefs);
		
		
        return new String[] { sql };
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
     * ALTER TABLE table-Name DROP [ COLUMN ] column-name [ CASCADE | RESTRICT ]
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
    public List<String> getTableDropSQL(ITableInfo iTableInfo, boolean cascadeConstraints, boolean isMaterializedView){
        return DialectUtils.getTableDropSQL(iTableInfo, false, cascadeConstraints, false, DialectUtils.CASCADE_CLAUSE, false);
    }
   
    /**
     * Returns the SQL that forms the command to add a primary key to the 
     * specified table composed of the given column names.
     * 
     * @param pkName the name of the constraint
     * @param columnInfos the columns that form the key
     * @return
     */
    public String[] getAddPrimaryKeySQL(String pkName, 
                                        TableColumnInfo[] colInfos, 
                                        ITableInfo ti) 
    {
        ArrayList<String> result = new ArrayList<String>();
        String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
        
        // convert each column that will be a member key to non-null - this 
        // doesn't hurt if they are already null.        
        DialectUtils.getMultiColNotNullSQL(colInfos, 
                                           this, 
                                           alterClause, 
                                           false, 
                                           result);
        
        result.add(DialectUtils.getAddPrimaryKeySQL(ti, pkName, colInfos, false));
        
        return result.toArray(new String[result.size()]);
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
     * @param info information about the column such as type, name, etc.
     * @return
     * @throws UnsupportedOperationException if the database doesn't support 
     *         annotating columns with a comment.
     */
    public String getColumnCommentAlterSQL(TableColumnInfo info) 
        throws UnsupportedOperationException
    {        
        int featureId = DialectUtils.COLUMN_COMMENT_ALTER_TYPE;
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
     * ALTER TABLE table-Name ALTER [ COLUMN ] column-name [ NOT ] NULL
     * 
     * @param info the column to modify
     * @return the SQL to execute
     */
    public String getColumnNullableAlterSQL(TableColumnInfo info) {
        String alterClause = DialectUtils.ALTER_COLUMN_CLAUSE;
        return DialectUtils.getColumnNullableAlterSQL(info, 
                                                      this, 
                                                      alterClause, 
                                                      false);
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
     * RENAME COLUMN table-Name.simple-Column-Name TO simple-Column-Name
     * 
     * @param from the TableColumnInfo as it is
     * @param to the TableColumnInfo as it wants to be
     * 
     * @return the SQL to make the change
     */
    public String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to) {
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
     * ALTER [ COLUMN ] column-Name SET DATA TYPE VARCHAR(integer)
     * 
     * Note: Only allowed to increase size of existing varchar as long as it is
     *       not a member of a PK that is a parent of a FK.  Also not allowed to
     *       change the type to anything else.  Oh, and only during a full moon
     *       while chanting - sheesh!  I think I'll need a dozen or so methods
     *       to describe all of those restrictions.
     * 
     * @param from the TableColumnInfo as it is
    * @param to the TableColumnInfo as it wants to be
     * @return the SQL to make the change
     * @throw UnsupportedOperationException if the database doesn't support 
     *         modifying column types. 
     */
    public List<String> getColumnTypeAlterSQL(TableColumnInfo from, 
                                        TableColumnInfo to, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
        throws UnsupportedOperationException
    {
        ArrayList<String> list = new ArrayList<String>();
        if (from.getDataType() != to.getDataType()) {
            throw new UnsupportedOperationException(i18n.TYPE_MESSAGE);
        }
        if (from.getDataType() != Types.VARCHAR) {
            throw new UnsupportedOperationException(i18n.VARCHAR_MESSAGE);
        }
        if (from.getColumnSize() > to.getColumnSize()) {
            throw new UnsupportedOperationException(i18n.COLUMN_LENGTH_MESSAGE);
        }
        StringBuffer result = new StringBuffer();
        result.append("ALTER TABLE ");
        result.append(to.getTableName());
        result.append(" ALTER COLUMN ");
        result.append(to.getColumnName());
        result.append(" SET DATA TYPE ");
        result.append(DialectUtils.getTypeName(to, this));
        list.add(result.toString());
        return list;
    }

    /**
     * Returns a boolean value indicating whether or not this database dialect
     * supports changing a column's default value.
     * 
     * @return true if the database supports modifying column defaults; false 
     *         otherwise
     */
    public boolean supportsAlterColumnDefault() {
        return false;
    }
    
    /**
     * Returns the SQL command to change the specified column's default value
     *   
     * @param info the column to modify and it's default value.
     * @return SQL to make the change
     */
    public String getColumnDefaultAlterSQL(TableColumnInfo info) {
        int featureId = DialectUtils.COLUMN_DEFAULT_ALTER_TYPE;
        String msg = DialectUtils.getUnsupportedMessage(this, featureId);
        throw new UnsupportedOperationException(msg);        
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
     * @see net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect#getDialectType()
     */
    public DialectType getDialectType() {
       return DialectType.DERBY;
    }
    
}
