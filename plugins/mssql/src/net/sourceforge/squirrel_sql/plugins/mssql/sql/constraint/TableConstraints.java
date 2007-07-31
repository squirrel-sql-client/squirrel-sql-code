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
import java.util.List;

public class TableConstraints {
    
    private ArrayList<MssqlConstraint> _constraints;
    
    /** Creates a new instance of TableConstraints */
    public TableConstraints() {
        _constraints = new ArrayList<MssqlConstraint>();
    }
    
    public MssqlConstraint[] getConstraints() {
        return (MssqlConstraint[]) _constraints.toArray();
    }
    
    public void addConstraint(MssqlConstraint constraint) {
        _constraints.add(constraint);
    }
    
    public List<DefaultConstraint> getDefaultsForColumn(String columnName) {
        ArrayList<DefaultConstraint> results = new ArrayList<DefaultConstraint>();
        for (int i = 0; i < _constraints.size(); i++) {
            MssqlConstraint constraint = _constraints.get(i);
            if (constraint instanceof DefaultConstraint) {
                DefaultConstraint def = (DefaultConstraint) constraint;
                if (def.constrainsColumn(columnName))
                    results.add(def);
            }
        }
        return results;
    }
    
    public List<CheckConstraint> getCheckConstraints() {
        ArrayList<CheckConstraint> results = new ArrayList<CheckConstraint>();
        for (int i = 0; i < _constraints.size(); i++) {
            MssqlConstraint constraint = _constraints.get(i);
            if (constraint instanceof CheckConstraint) {
                results.add((CheckConstraint)constraint);
            }
        }
        return results;
    }
    
    public List<ForeignKeyConstraint> getForeignKeyConstraints() {
        ArrayList<ForeignKeyConstraint> results = 
            new ArrayList<ForeignKeyConstraint>();
        for (int i = 0; i < _constraints.size(); i++) {
            MssqlConstraint constraint = _constraints.get(i);
            if (constraint instanceof ForeignKeyConstraint) {
                results.add((ForeignKeyConstraint)constraint);
            }
        }
        return results;
    }
    
    public List<PrimaryKeyConstraint> getPrimaryKeyConstraints() {
        ArrayList<PrimaryKeyConstraint> results = 
            new ArrayList<PrimaryKeyConstraint>();
        for (int i = 0; i < _constraints.size(); i++) {
            MssqlConstraint constraint = _constraints.get(i);
            if (constraint instanceof PrimaryKeyConstraint) {
                results.add((PrimaryKeyConstraint)constraint);
            }
        }
        return results;
    }
    
}
