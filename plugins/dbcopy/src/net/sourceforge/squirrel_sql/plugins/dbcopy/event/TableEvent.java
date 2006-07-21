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
package net.sourceforge.squirrel_sql.plugins.dbcopy.event;

import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;

/**
 * Contains info about a table that is about to be copied.  The table is one
 * of possibly a set of tables to be copied.
 */
public class TableEvent extends AbstractCopyEvent {
    
    /** the number of the table in the set. 1 indicates the first table */
    private int tableNumber;
    
    /** the total number of tables in the set. This will be >= 1 */
    private int tableCount;
    
    /** the name of the table to be copied */
    private String tableName;
    
    public TableEvent(SessionInfoProvider provider) {
        super(provider);
    }

    /**
     * @param tableNumber The tableNumber to set.
     */
    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    /**
     * @return Returns the tableNumber.
     */
    public int getTableNumber() {
        return tableNumber;
    }

    /**
     * @param tableCount The tableCount to set.
     */
    public void setTableCount(int tableCount) {
        this.tableCount = tableCount;
    }

    /**
     * @return Returns the tableCount.
     */
    public int getTableCount() {
        return tableCount;
    }

    /**
     * @param tableName The tableName to set.
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return Returns the tableName.
     */
    public String getTableName() {
        return tableName;
    }
    
    
}
