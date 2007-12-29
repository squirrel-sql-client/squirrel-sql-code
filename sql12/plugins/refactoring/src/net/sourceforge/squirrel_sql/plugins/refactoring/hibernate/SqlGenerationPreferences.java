package net.sourceforge.squirrel_sql.plugins.refactoring.hibernate;
/*
* Copyright (C) 2007 Daniel Regli & Yannick Winiger
* http://sourceforge.net/projects/squirrel-sql
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

/**
 * A simple object to store user preferences regarding generated SQL scripts.
 * When we are generating an SQL script, we want to take into account the
 * user's preferences.
 */
public class SqlGenerationPreferences {
    private boolean qualifyTableNames = true;
    private boolean quoteIdentifiers = true;
    private String sqlStatementSeparator = ";";


    /**
     * Sets if table names have to be qualified.
     *
     * @param qualifyTableNames true if table names have to be qualified, false otherwise.
     */
    public void setQualifyTableNames(boolean qualifyTableNames) {
        this.qualifyTableNames = qualifyTableNames;
    }


    /**
     * @return true if table names have to be qualified, false otherwise.
     */
    public boolean isQualifyTableNames() {
        return qualifyTableNames;
    }


    /**
     * Sets if identifiers have to be quoted.
     *
     * @param quoteIdentifiers true if identifiers have to be quoted, false otherwise.
     */
    public void setQuoteIdentifiers(boolean quoteIdentifiers) {
        this.quoteIdentifiers = quoteIdentifiers;
    }


    /**
     * @return true if identifiers have to be quoted, false otherwise.
     */
    public boolean isQuoteIdentifiers() {
        return quoteIdentifiers;
    }


    /**
     * Sets the separator for sql statements.
     *
     * @param sqlStatementSeparator the separator for sql statements
     */
    public void setSqlStatementSeparator(String sqlStatementSeparator) {
        this.sqlStatementSeparator = sqlStatementSeparator;
    }


    /**
     * @return the separator for sql statements
     */
    public String getSqlStatementSeparator() {
        return sqlStatementSeparator;
    }
}
