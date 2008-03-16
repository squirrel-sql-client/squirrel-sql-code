/*
 * Copyright (C) 2008 Dieter Engelhardt
 * dieter@ew6.org
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
package net.sourceforge.squirrel_sql.plugins.sqlreplace;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.action.SelectInternalFrameCommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * @author Dieter
 *
 */
public class SQLReplaceExecutionListener implements ISQLExecutionListener {

	private final static ILogger log = LoggerController.createLogger(SQLReplacePlugin.class);
	private ISession session = null;
	private SQLReplacePlugin plugin = null;
	/**
	 * 
	 */
	public SQLReplaceExecutionListener() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor we use
	 * @param plugin
	 * @param session
	 */
	public SQLReplaceExecutionListener(SQLReplacePlugin plugin, ISession session) {
		this.session = session;
		this.plugin = plugin;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener#statementExecuted(java.lang.String)
	 */
	public void statementExecuted(String sql) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener#statementExecuting(java.lang.String)
	 */
	public String statementExecuting(String sql) {
		StringBuffer buffer = new StringBuffer(sql);

		// Here we do the Replacement
		ReplacementManager repMan = plugin.getReplacementManager();
		String replacedStmnt = repMan.replace(buffer);
		
		GUIUtils.processOnSwingEventThread(new Runnable() {
			public void run() {
				new SelectInternalFrameCommand(session.getActiveSessionWindow()).execute();
			}
		});
		// log.info("SQL passing to execute: " + buffer.toString());
		return replacedStmnt;
	}

}
