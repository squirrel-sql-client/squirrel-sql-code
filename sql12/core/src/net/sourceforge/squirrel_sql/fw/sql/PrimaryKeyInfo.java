package net.sourceforge.squirrel_sql.fw.sql;


/*
 * Copyright (C) 2003 Colin Bell
 * colbell@users.sourceforge.net
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
/**
 * This represents the primary key definition for a table.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class PrimaryKeyInfo extends DatabaseObjectInfo
{
    /**
     * the name of the column which belongs to a list of columns that form a 
     * unique key for a table
     */
    private String columnName = null;
    
    /** sequence number within primary key */
    private short keySequence;
    
    /**
     * The table that has this primary key constraint
     */
    private String tableName = null;
    
    /**
     * @deprecated use the version of the constructor that accepts args to 
     *             provide complete information about this key.
     */
    PrimaryKeyInfo() {
        super(null, null, null, null, null);
    }
    
    /**
     * Create a new PrimaryKeyInfo object.
     * 
     * @param catalog catalog name
     * @param schema schema name
     * @param aColumnName the name of the column that either by itself or along
     *                    with others form(s) a unique index value for a single
     *                    row in a table. 
     * @param aKeySequence sequence number within primary key
     * @param aPrimaryKeyName the name of the primary key
     * @param md
     */
	public PrimaryKeyInfo(String catalog, 
                   String schema,
                   String aTableName,
                   String aColumnName, 
                   short aKeySequence, 
                   String aPrimaryKeyName,
                   ISQLDatabaseMetaData md)
	{
		super(catalog, schema, aPrimaryKeyName, DatabaseObjectType.PRIMARY_KEY, md);
        columnName = aColumnName;
        tableName = aTableName;
        keySequence = aKeySequence;
	}

    /**
     * @param columnName The columnName to set.
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return Returns the columnName.
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param keySequence The keySequence to set.
     */
    public void setKeySequence(short keySequence) {
        this.keySequence = keySequence;
    }

    /**
     * @return Returns the keySequence.
     */
    public short getKeySequence() {
        return keySequence;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    public String getQualifiedColumnName() {
        if (tableName != null && !"".equals(tableName)) {
            return tableName + "." + columnName;
        }
        return columnName;
    }
}
