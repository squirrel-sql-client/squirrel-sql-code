/*
 * Copyright (C) 2005 Rob Manning
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
package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

/**
 * A simple class to encapsulate the result of executing one drop sql statement
 * in a database session.
 */
public class DropTableResult {
    
    /** the table to be dropped */
    private ITableInfo _tableInfo = null;
    
    /** the SQL statement used to drop the table */
    private String _sql = null;
    
    /** the outcome of executing the statement */
    boolean result = false;
    
    public DropTableResult(ITableInfo ti, String sql) {
        _tableInfo = ti;
        _sql = sql;
    }

    /**
     * @param result The result to set.
     */
    public void setResult(boolean result) {
        this.result = result;
    }

    /**
     * @return Returns the result. false if either the statement wasn't 
     *         executed, or it failed
     */
    public boolean getResult() {
        return result;
    }

    /**
     * @param _tableInfo The _tableInfo to set.
     */
    public void setTableInfo(ITableInfo _tableInfo) {
        this._tableInfo = _tableInfo;
    }

    /**
     * @return Returns the _tableInfo.
     */
    public ITableInfo getTableInfo() {
        return _tableInfo;
    }

    /**
     * @param _sql The _sql to set.
     */
    public void setSql(String _sql) {
        this._sql = _sql;
    }

    /**
     * @return Returns the _sql.
     */
    public String getSql() {
        return _sql;
    }
    
}