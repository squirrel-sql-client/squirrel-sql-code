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

public class DefaultConstraint extends MssqlConstraint {
    
    /**
     * Holds value of property defaultExpression.
     */
    private String _defaultExpression;
    
    /** Creates a new instance of DefaultConstraint */
    public DefaultConstraint() {
        super();
    }
    
    /**
     * Getter for property defaultExpression.
     * @return Value of property defaultExpression.
     */
    public String getDefaultExpression() {
        return this._defaultExpression;
    }
    
    /**
     * Setter for property defaultExpression.
     * @param defaultExpression New value of property defaultExpression.
     */
    public void setDefaultExpression(String defaultExpression) {
        this._defaultExpression = defaultExpression;
    }
    
}
