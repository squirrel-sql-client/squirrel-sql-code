package net.sourceforge.squirrel_sql.fw.sql;
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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TableInfo extends DatabaseObjectInfo implements ITableInfo {
    /** Table Type. */
    private final String _type;

    /** Table remarks. */
    private final String _remarks;

    /** Catalogue separator. */
//  private String _catSep;

    /** Identifier quote string. */
//  private String _identifierQuoteString;

//  private String _qualifiedName;

    TableInfo(ResultSet rs, SQLConnection conn) throws SQLException {
        super(rs.getString(1), rs.getString(2), rs.getString(3),
                conn);
        _type = rs.getString(4);
        _remarks = rs.getString(5);
    }

    public String getType() {
            return _type;
    }

    public String getRemarks() {
        return _remarks;
    }
}
