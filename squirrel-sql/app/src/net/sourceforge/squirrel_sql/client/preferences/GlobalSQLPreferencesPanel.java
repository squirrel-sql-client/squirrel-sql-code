package net.sourceforge.squirrel_sql.client.preferences;
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
//import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

//import javax.swing.Icon;
//import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
//import javax.swing.JTextArea;
//import javax.swing.SwingConstants;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
//import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;

import net.sourceforge.squirrel_sql.client.IApplication;
//import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
//import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
//import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
//import net.sourceforge.squirrel_sql.client.session.properties.SQLPropertiesPanel;

class GlobalSQLPreferencesPanel implements IGlobalPreferencesPanel {

	private IApplication _app;

	private MyPanel _myPanel = new MyPanel();

	public void initialize(IApplication app)
			throws IllegalArgumentException {
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}

		_app = app;
		_myPanel.loadData(_app.getSquirrelPreferences());
	}

	public void applyChanges() {
		_myPanel.applyChanges(_app.getSquirrelPreferences());
	}

	public String getTitle() {
		return MyPanel.i18n.TAB_TITLE;
	}

	public String getHint() {
		return MyPanel.i18n.TAB_HINT;
	}

	public Component getPanelComponent() {
		return _myPanel;
	}

	private static final class MyPanel extends JPanel {
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		private interface i18n {
//			String DEBUG_JDBC = "JDBC Debug Info to Standard Output:";
			String LOGIN_TIMEOUT = "Login Timeout (Seconds):";
			String TAB_HINT = "Global SQL";
			String TAB_TITLE = "Global SQL";
		}

		private boolean _initialized = false;

		private IntegerField _loginTimeout = new IntegerField();

		MyPanel() {
			super();
		}

		void loadData(SquirrelPreferences prefs) {
			_loginTimeout.setInt(prefs.getLoginTimeout());
			if (!_initialized) {
				createUserInterface(prefs);
				_initialized = true;
			}
		}

		void applyChanges(SquirrelPreferences prefs) {
			prefs.setLoginTimeout(_loginTimeout.getInt());
		}

		private void createUserInterface(SquirrelPreferences prefs) {
			_loginTimeout.setColumns(4);

			setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = gbc.WEST;
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.gridx = 0;
			gbc.gridy = 0;
			add(new JLabel(i18n.LOGIN_TIMEOUT), gbc);
			++gbc.gridx;
			add(_loginTimeout, gbc);
		}
	}
}
