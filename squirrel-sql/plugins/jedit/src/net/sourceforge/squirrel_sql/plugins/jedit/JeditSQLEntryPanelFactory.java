package net.sourceforge.squirrel_sql.plugins.jedit;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.JEditTextArea;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.SyntaxStyle;
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.SyntaxUtilities;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;

class JeditSQLEntryPanelFactory implements ISQLEntryPanelFactory {
	private JeditPlugin _plugin;
	private JeditPreferences _globalPrefs;

	JeditSQLEntryPanelFactory(JeditPlugin plugin, JeditPreferences globalPrefs)
			throws IllegalArgumentException {
		if (plugin == null) {
			throw new IllegalArgumentException("Null JeditPlugin passed");
		}
		if (globalPrefs == null) {
			throw new IllegalArgumentException("Null JeditPreferences passed");
		}

		_plugin = plugin;
		_globalPrefs = globalPrefs;
	}

	/**
	 * @see ISQLEntryPanelFactory#createSQLEntryPanel()
	 */
	public ISQLEntryPanel createSQLEntryPanel(ISession session)
			throws IllegalArgumentException{
		if (session == null) {
			throw new IllegalArgumentException("Null ISession passed");
		}
		JeditPreferences prefs = (JeditPreferences)session.getPluginObject(_plugin, JeditConstants.ISessionKeys.PREFS);
		if (prefs == null) {
			prefs = _globalPrefs;
		}
		final JeditSQLEntryPanel pnl = new JeditSQLEntryPanel(session, _plugin, prefs);
		final JEditTextArea ta = pnl.getTypedComponent();
		session.putPluginObject(_plugin, JeditConstants.ISessionKeys.JEDIT_SQL_ENTRY_CONTROL, pnl);
		return pnl;
	}
}
