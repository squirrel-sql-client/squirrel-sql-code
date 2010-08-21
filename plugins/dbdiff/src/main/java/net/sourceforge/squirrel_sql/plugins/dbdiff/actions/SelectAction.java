package net.sourceforge.squirrel_sql.plugins.dbdiff.actions;
/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.dbdiff.DBDiffPlugin;
import net.sourceforge.squirrel_sql.plugins.dbdiff.commands.SelectCommand;

public class SelectAction extends SquirrelAction
                                     implements ISessionAction {

	/** Current session. */
    private ISession _session;

	/** Current plugin. */
	private final DBDiffPlugin _plugin;

    public SelectAction(IApplication app, 
                              Resources rsrc,
                              DBDiffPlugin plugin) 
    {
        super(app, rsrc);
        _plugin = plugin;
    }

    public void actionPerformed(ActionEvent evt) {
        if (_session != null) {
            new SelectCommand(_session, _plugin).execute();
        }
    }

	/**
	 * Set the current session.
	 * 
	 * @param	session		The current session.
	 */
    public void setSession(ISession session) {
        _session = session;
    }
}