/*
 * Copyright (C) 2003 Joseph Mocker
 * mock-sf@misfit.dhs.org
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

package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

/**
 * A class encapsulating an SQL Bookmark.
 *
 * @author      Joseph Mocker
 **/
public class Bookmark implements Cloneable {

    /** The name of the bookmark */
    protected String name;

    /** The SQL for the bookmark */
    protected String sql;

    public Bookmark() {
	this.name = null;
	this.sql = null;
    }

    public Bookmark(String name, String sql) {
	this.name = name;
	this.sql = sql;
    }

    public String getName() {
	return name;
    }
    
    public String getSql() {
	return sql;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setSql(String sql) {
	this.sql = sql;
    }

    public Object clone() {
	try {
	    return super.clone();
	}
	catch (CloneNotSupportedException e) {
	    // should not happen
	    return null;
	}
    }
}
