package net.sourceforge.squirrel_sql.plugins.dbinfo;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import java.io.Serializable;

public class DBInfo implements Serializable {
    private String _author;
    private String _title;
//  private char _variableIndicator;
    private String _procSourceSql;

    public DBInfo() {
        super();
    }

    public String getAuthor() {
        return _author;
    }

    public void setAuthor(String value) {
        _author = value;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String value) {
        _title = value;
    }

    public String getProcSourceSql() {
        return _procSourceSql;
    }

    public void setProcSourceSql(String value) {
        _procSourceSql = value;
    }

    public static class DBInfoEntry implements Serializable {
        private String _title;
        private String _sql;
    }
}

