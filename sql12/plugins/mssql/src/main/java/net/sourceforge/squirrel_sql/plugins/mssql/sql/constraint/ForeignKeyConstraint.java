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

public class ForeignKeyConstraint extends MssqlConstraint {
    
    private ArrayList<String> _primaryColumns;
    
    /**
     * Holds value of property referencedTable.
     */
    private String _referencedTable;
    
      // the columns in the referenced table
    
    /** Creates a new instance of ForeignKeyConstraint */
    public ForeignKeyConstraint() {
        super();
        _primaryColumns = new ArrayList<String>();
    }
    
    public void addPrimaryColumn(String columnName) {
        _primaryColumns.add(columnName);
    }
    
    public Object[] getPrimaryColumns() {
        return _primaryColumns.toArray();
    }
    
    /**
     * Getter for property referencedTable.
     * @return Value of property referencedTable.
     */
    public String getReferencedTable() {
        return this._referencedTable;
    }
    
    /**
     * Setter for property referencedTable.
     * @param referencedTable New value of property referencedTable.
     */
    public void setReferencedTable(String referencedTable) {
        this._referencedTable = referencedTable;
    }
    
}
