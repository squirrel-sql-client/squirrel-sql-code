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

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;

public class UpdateStatisticsCommand implements ICommand {
	private ISession _session;
	private final MssqlPlugin _plugin;

	public UpdateStatisticsCommand(ISession session, MssqlPlugin plugin) {
		super();
		if (session == null)
			throw new IllegalArgumentException("ISession == null");

		_session = session;
		_plugin = plugin;
	}

	public void execute() {
        final String sqlSep = _session.getQueryTokenizer().getSQLStatementSeparator();
        final IObjectTreeAPI api = _session.getSessionInternalFrame().getObjectTreeAPI();
		final IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();

		// Get the names of all the selected tables in a comma separated list,
        final StringBuffer cmd = new StringBuffer(512);
		for (int i = 0; i < dbObjs.length; ++i) {
            cmd.append("UPDATE STATISTICS ");
            cmd.append(dbObjs[i].getCatalogName());
            cmd.append(".");
            cmd.append(dbObjs[i].getSchemaName());
            cmd.append(".");
            cmd.append(dbObjs[i].getSimpleName());
            cmd.append(" WITH FULLSCAN, ALL\n");
            cmd.append(sqlSep);
            cmd.append("\n");
		}

        if (cmd != null && cmd.length() > 0) {
			_session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript(cmd.toString(), true);
			_session.getSessionInternalFrame().getSQLPanelAPI().executeCurrentSQL();
			_session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
		}
	}
}
