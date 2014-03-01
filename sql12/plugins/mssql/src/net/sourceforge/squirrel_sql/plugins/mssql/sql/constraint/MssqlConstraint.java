package net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint;

/*
 * Copyright (C) 2004 Ryan Walberg <generalpf@yahoo.com>
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

import java.util.ArrayList;

public class MssqlConstraint {
    
    /**
     * Holds value of property constraintName.
     */
    private String _constraintName;
    private ArrayList<String> _constraintColumns;
    
    /** Creates a new instance of MssqlConstraint */
    public MssqlConstraint() {
        _constraintColumns = new ArrayList<String>();
    }
    
    /**
     * Getter for property constraintName.
     * @return Value of property constraintName.
     */
    public String getConstraintName() {
        return this._constraintName;
    }
    
    /**
     * Setter for property constraintName.
     * @param constraintName New value of property constraintName.
     */
    public void setConstraintName(String constraintName) {
        this._constraintName = constraintName;
    }
    
    public void addConstraintColumn(String columnName) {
        _constraintColumns.add(columnName);
    }
    
    public Object[] getConstraintColumns() {
        return _constraintColumns.toArray();
    }
    
    public boolean constrainsColumn(String columnName) {
        for (int i = 0; i < _constraintColumns.size(); i++)
            if (columnName.equals(_constraintColumns.get(i)))
                return true;
        return false;
    }
    
}
