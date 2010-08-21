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
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.dbfile.DatabaseFile;

public class ShrinkDatabaseFileAction extends SquirrelAction implements ISessionAction {
	private ISession _session;
	private final MssqlPlugin _plugin;
    private String _catalogName;
    private DatabaseFile _databaseFile;

	public ShrinkDatabaseFileAction(IApplication app, Resources rsrc, MssqlPlugin plugin, String catalogName, DatabaseFile databaseFile) {
		super(app, rsrc);
        
        /* the constructor above sets this from resources, but we'll override it with
         * the name of the database file and its size. */
        putValue(javax.swing.Action.NAME,databaseFile.getName() + " (" + databaseFile.getSize() + ")");
        
		_plugin = plugin;
        _catalogName = catalogName;
        _databaseFile = databaseFile;
	}

	public void actionPerformed(ActionEvent evt) {
		if (_session != null) {
			new ShrinkDatabaseFileCommand(_session, _plugin, _catalogName, _databaseFile.getName()).execute();
		}
	}

	public void setSession(ISession session) {
		_session = session;
	}
}
