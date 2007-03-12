package net.sourceforge.squirrel_sql.plugins.mssql.action;

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

import java.sql.Connection;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.WrappedSQLException;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;
import net.sourceforge.squirrel_sql.plugins.mssql.util.MssqlIntrospector;

public class ScriptProcedureCommand implements ICommand {
	private ISession _session;
	private final MssqlPlugin _plugin;

	private final IDatabaseObjectInfo[] _dbObjs;

	public ScriptProcedureCommand(ISession session, MssqlPlugin plugin, IDatabaseObjectInfo[] dbObjs) {
		super();
		if (session == null)
			throw new IllegalArgumentException("ISession == null");
		if (dbObjs == null)
			throw new IllegalArgumentException("IDatabaseObjectInfo array is null");

		_session = session;
		_plugin = plugin;
		_dbObjs = dbObjs;
	}

	public void execute() throws BaseException {
        try {
            if (_dbObjs.length > 0) {
                Connection conn = _session.getSQLConnection().getConnection();
                final String sqlSep = 
                    _session.getQueryTokenizer().getSQLStatementSeparator();
                final StringBuffer buf = new StringBuffer();

                for (int i = 0; i < _dbObjs.length; i++) {
                    final IDatabaseObjectInfo ti = _dbObjs[i];
                    /* TODO: what I really want to do here is get the SQL script and replace
                     * "CREATE PROCEDURE" with "ALTER PROCEDURE", then put that in the SQL pane
                     * and *NOT* execute it. */

                    /* NOTE: a procedure may also be a UDF! */
                    
                    if (!conn.getCatalog().equals(ti.getCatalogName()))
                        conn.setCatalog(ti.getCatalogName());
                    
                    buf.append(MssqlIntrospector.getHelpTextForObject(MssqlIntrospector.getFixedVersionedObjectName(ti.getSimpleName()),conn));
                    buf.append("\n");
                    buf.append(sqlSep);
                    buf.append("\n\n");
                }

                _session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript(buf.toString());
                _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
            }
        }
        catch (java.sql.SQLException ex) {
            ex.printStackTrace();
			throw new WrappedSQLException(ex);
		}
	}
}
