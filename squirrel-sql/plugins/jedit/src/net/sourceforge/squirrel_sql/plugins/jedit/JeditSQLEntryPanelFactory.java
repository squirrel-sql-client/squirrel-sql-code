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
import java.awt.Font;

import javax.swing.UIManager;

import net.sourceforge.squirrel_sql.fw.gui.FontInfo;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;

import net.sourceforge.squirrel_sql.plugins.jedit.textarea.JEditTextArea;

class JeditSQLEntryPanelFactory implements ISQLEntryPanelFactory {
	private JeditPlugin _plugin;

	/** The original Squirrel SQL CLient factory for creating SQL entry panels. */
	private ISQLEntryPanelFactory _originalFactory;

	JeditSQLEntryPanelFactory(JeditPlugin plugin, ISQLEntryPanelFactory originalFactory) {
		if (plugin == null) {
			throw new IllegalArgumentException("Null JeditPlugin passed");
		}
		if (originalFactory == null) {
			throw new IllegalArgumentException("Null originalFactory passed");
		}

		_plugin = plugin;
		_originalFactory = originalFactory;
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
		if (prefs.getUseJeditTextControl()) {
			JeditSQLEntryPanel pnl = (JeditSQLEntryPanel)session.getPluginObject(_plugin, JeditConstants.ISessionKeys.JEDIT_SQL_ENTRY_CONTROL);
			if (pnl == null) {
				pnl = new JeditSQLEntryPanel(session, _plugin, prefs);
//				final JEditTextArea ta = pnl.getTypedComponent();
				session.putPluginObject(_plugin, JeditConstants.ISessionKeys.JEDIT_SQL_ENTRY_CONTROL, pnl);
			}
			return pnl;
		}
		session.removePluginObject(_plugin, JeditConstants.ISessionKeys.JEDIT_SQL_ENTRY_CONTROL);
		return _originalFactory.createSQLEntryPanel(session);
	}
}
