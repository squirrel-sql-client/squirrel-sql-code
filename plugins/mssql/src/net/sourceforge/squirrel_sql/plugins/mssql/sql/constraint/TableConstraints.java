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

public class TableConstraints {
    
    private ArrayList _constraints;
    
    /** Creates a new instance of TableConstraints */
    public TableConstraints() {
        _constraints = new ArrayList();
    }
    
    public MssqlConstraint[] getConstraints() {
        return (MssqlConstraint[]) _constraints.toArray();
    }
    
    public void addConstraint(MssqlConstraint constraint) {
        _constraints.add(constraint);
    }
    
    public Object[] getDefaultsForColumn(String columnName) {
        ArrayList results = new ArrayList();
        for (int i = 0; i < _constraints.size(); i++) {
            MssqlConstraint constraint = (MssqlConstraint) _constraints.get(i);
            if (constraint instanceof DefaultConstraint) {
                DefaultConstraint def = (DefaultConstraint) constraint;
                if (def.constrainsColumn(columnName))
                    results.add(def);
            }
        }
        return results.toArray();
    }
    
    public Object[] getCheckConstraints() {
        ArrayList results = new ArrayList();
        for (int i = 0; i < _constraints.size(); i++) {
            MssqlConstraint constraint = (MssqlConstraint) _constraints.get(i);
            if (constraint instanceof CheckConstraint) {
                results.add(constraint);
            }
        }
        return results.toArray();
    }
    
    public Object[] getForeignKeyConstraints() {
        ArrayList results = new ArrayList();
        for (int i = 0; i < _constraints.size(); i++) {
            MssqlConstraint constraint = (MssqlConstraint) _constraints.get(i);
            if (constraint instanceof ForeignKeyConstraint) {
                results.add(constraint);
            }
        }
        return results.toArray();
    }
    
    public Object[] getPrimaryKeyConstraints() {
        ArrayList results = new ArrayList();
        for (int i = 0; i < _constraints.size(); i++) {
            MssqlConstraint constraint = (MssqlConstraint) _constraints.get(i);
            if (constraint instanceof PrimaryKeyConstraint) {
                results.add(constraint);
            }
        }
        return results.toArray();
    }
    
}
