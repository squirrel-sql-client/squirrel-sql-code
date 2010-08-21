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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;

public class TruncateLogCommand implements ICommand {
	private ISession _session;
	private final MssqlPlugin _plugin;

	private final IDatabaseObjectInfo[] _dbs;
    
	public TruncateLogCommand(ISession session, MssqlPlugin plugin, IDatabaseObjectInfo[] dbs) {
		super();
		if (session == null)
			throw new IllegalArgumentException("ISession == null");
		if (dbs == null)
			throw new IllegalArgumentException("Databases array is null");

        _session = session;
		_plugin = plugin;
		_dbs = dbs;
	}

	public void execute() {
		if (_dbs.length > 0) {
			final String sqlSep = _session.getQueryTokenizer().getSQLStatementSeparator();
			final StringBuffer buf = new StringBuffer();
			for (int i = 0; i < _dbs.length; i++) {
				final IDatabaseObjectInfo ti = _dbs[i];
				buf.append("BACKUP LOG " + ti.getSimpleName() + " WITH TRUNCATE_ONLY\n");
				buf.append(sqlSep);
                buf.append("\n");
			}
            _session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript(buf.toString(), true);
            _session.getSessionInternalFrame().getSQLPanelAPI().executeCurrentSQL();
            _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
		}
	}
}
