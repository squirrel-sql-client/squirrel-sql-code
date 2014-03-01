/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.fw.util.IResources;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Action to store the result of the current SQL directly into a file.
 * 
 * @author Stefan Willinger
 * 
 */
public class CreateFileOfCurrentSQLAction extends SquirrelAction implements ISQLPanelAction {

	private SQLScriptPlugin plugin;

	/**
	 * Current session.
	 */
	private ISession session;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            the application
	 * @param rsrc
	 *            The resources of the {@link SQLScriptPlugin}
	 * @param sqlScriptPlugin
	 *            the {@link SQLScriptPlugin}
	 */
	public CreateFileOfCurrentSQLAction(IApplication app, IResources rsrc, SQLScriptPlugin sqlScriptPlugin) {
		super(app, rsrc);
		this.plugin = sqlScriptPlugin;
	}

	/**
	 * Executes the Action on the current active session.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * @see CreateFileOfCurrentSQLCommand#execute()
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		new CreateFileOfCurrentSQLCommand(session, plugin)
				.execute((JFrame) SessionUtils.getOwningFrame(session));
	}

	/**
	 * If no SQLPanel is active, disable this one.
	 * @see net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction#setSQLPanel(net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI)
	 */
	public void setSQLPanel(ISQLPanelAPI panel) {
		if (null != panel) {
			session = panel.getSession();
		} else {
			session = null;
		}
		setEnabled(null != session);
	}

}
