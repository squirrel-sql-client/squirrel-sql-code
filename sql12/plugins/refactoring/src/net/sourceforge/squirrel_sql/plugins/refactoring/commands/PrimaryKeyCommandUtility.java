package net.sourceforge.squirrel_sql.plugins.refactoring.commands;
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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.*;

import java.sql.SQLException;

/**
 * Implements an utility Class for various Primary Key operations.
 */
public class PrimaryKeyCommandUtility {
    /**
     * Current session
     */
    protected ISession _session;

    /**
     * Selected table(s)
     */
    protected final IDatabaseObjectInfo[] _info;


    /**
     * Constructor of the PrimaryKeyCommandUtility Class.
     *
     * @param session ClientSession.
     * @param info    DatabaseObjectInfo.
     */
    public PrimaryKeyCommandUtility(ISession session,
                                    IDatabaseObjectInfo[] info) {
        if (session == null) throw new IllegalArgumentException("ISession cannot be null");
        if (info == null) throw new IllegalArgumentException("IDatabaseObjectInfo[] cannot be null");

        _session = session;
        _info = info;
    }


    /**
     * Returns true if table has a Primary key.
     *
     * @return true if table is having a primary key, otherwise returns false.
     * @throws SQLException if there sql statement can not be completed or an error occures.
     */
    protected boolean tableHasPrimaryKey() throws SQLException {
        if (!(_info[0] instanceof ITableInfo)) {
            return false;
        }
        ITableInfo ti = (ITableInfo) _info[0];
        SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();
        PrimaryKeyInfo[] pks = md.getPrimaryKey(ti);
        return (pks != null && pks.length > 0);
    }
}
