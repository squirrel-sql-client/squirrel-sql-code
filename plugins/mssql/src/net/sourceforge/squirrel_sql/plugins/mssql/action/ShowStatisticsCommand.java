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
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;

public class ShowStatisticsCommand implements ICommand {
	private ISession _session;
	private final MssqlPlugin _plugin;

	private final ITableInfo _tableInfo;
    private final String _indexName;

	public ShowStatisticsCommand(ISession session, MssqlPlugin plugin, ITableInfo tableInfo, String indexName) {
		super();
		if (session == null)
			throw new IllegalArgumentException("ISession == null");
		if (tableInfo == null)
			throw new IllegalArgumentException("ITableInfo == null");

		_session = session;
		_plugin = plugin;
		_tableInfo = tableInfo;
        _indexName = indexName;
	}

	public void execute() {
        StringBuffer sqlBuffer = new StringBuffer();
        final String sqlSep = 
            _session.getQueryTokenizer().getSQLStatementSeparator();
        sqlBuffer.append("DBCC SHOW_STATISTICS([");
        sqlBuffer.append(_tableInfo.getCatalogName());
        sqlBuffer.append(".");
        sqlBuffer.append(_tableInfo.getSchemaName());
        sqlBuffer.append(".");
        sqlBuffer.append(_tableInfo.getSimpleName());
        sqlBuffer.append("],");
        sqlBuffer.append(_indexName);
        sqlBuffer.append(")\n");
        sqlBuffer.append(sqlSep);
        sqlBuffer.append("\n");
        
        _session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript(sqlBuffer.toString(), true);
		_session.getSessionInternalFrame().getSQLPanelAPI().executeCurrentSQL();
		_session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
	}
}
