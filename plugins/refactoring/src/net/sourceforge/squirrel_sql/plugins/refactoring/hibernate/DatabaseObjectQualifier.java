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

public class DatabaseObjectQualifier {
    private String catalog;
    private String schema;


    public DatabaseObjectQualifier() {
    }


    public DatabaseObjectQualifier(String catalog, String schema) {
        this.catalog = catalog;
        this.schema = schema;
    }


    public String getCatalog() {
        return catalog;
    }


    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }


    public String getSchema() {
        return schema;
    }


    public void setSchema(String schema) {
        this.schema = schema;
    }
}
