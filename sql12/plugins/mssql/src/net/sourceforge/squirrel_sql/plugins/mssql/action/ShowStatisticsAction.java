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

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;

public class ShowStatisticsAction extends SquirrelAction implements ISessionAction {

    private static final long serialVersionUID = 1L;
    transient private ISession _session;
	transient private final MssqlPlugin _plugin;
    
    private final ITableInfo _tableInfo;
    private final String _indexName;

	public ShowStatisticsAction(IApplication app, Resources rsrc, MssqlPlugin plugin, ITableInfo tableInfo, String indexName) {
		super(app, rsrc);
		/* the constructor above sets this from resources, but we'll override it with
         * the name of the index. */
        putValue(javax.swing.Action.NAME,indexName);
        _plugin = plugin;
        _tableInfo = tableInfo;
        _indexName = indexName;
	}

	public void actionPerformed(ActionEvent evt) {
		if (_session != null)
    		new ShowStatisticsCommand(_session, _plugin, _tableInfo, _indexName).execute();
	}

	public void setSession(ISession session) {
		_session = session;
	}
}
