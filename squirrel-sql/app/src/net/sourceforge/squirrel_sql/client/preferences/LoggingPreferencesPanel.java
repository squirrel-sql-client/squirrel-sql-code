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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

class LoggingPreferencesPanel implements IGlobalPreferencesPanel {
	/** Application API. */
	private IApplication _app;

	private MyPanel _myPanel;

	public void initialize(IApplication app)
			throws IllegalArgumentException {
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}

		_app = app;
		_myPanel = new MyPanel(_app);
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
			String DEBUG = "Debug";
			String DEBUG_JDBC = "JDBC Debug";
			String TAB_HINT = "Logging and Debug settings";
			String TAB_TITLE = "Logging/Debug";
			String PERF_WARNING = "Note: Turning on debug options will have performance implications.";
		}

		/** Application API. */
		private IApplication _app;

		private boolean _initialized = false;

		private JCheckBox _debug = new JCheckBox(i18n.DEBUG);
		private JCheckBox _debugJdbc = new JCheckBox(i18n.DEBUG_JDBC);

		MyPanel(IApplication app) {
			super();
			_app = app;
		}

		void loadData(SquirrelPreferences prefs) {
			_debug.setSelected(prefs.isDebugMode());
			_debugJdbc.setSelected(prefs.getDebugJdbc());
			if (!_initialized) {
				createUserInterface(prefs);
				_initialized = true;
			}
		}

		void applyChanges(SquirrelPreferences prefs) {
			prefs.setDebugMode(_debug.isSelected());
			prefs.setDebugJdbc(_debugJdbc.isSelected());
		}

		private void createUserInterface(SquirrelPreferences prefs) {
			final ApplicationFiles appFiles = _app.getApplicationFiles();

			setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = gbc.WEST;
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.gridx = 0;
			gbc.gridy = 0;
			add(_debug, gbc);

			++gbc.gridx;
			add(new OutputLabel(appFiles.getDebugLogFile().getPath()), gbc);

			++gbc.gridy;
			gbc.gridx = 0;
			add(_debugJdbc, gbc);

			++gbc.gridx;
			add(new OutputLabel(appFiles.getJDBCDebugLogFile().getPath()), gbc);

			++gbc.gridy;
			gbc.gridx = 0;
			add(new JLabel("Execution Log file:"), gbc);

			++gbc.gridx;
			add(new OutputLabel(appFiles.getExecutionLogFile().getPath()), gbc);

			// Right at the bottom we put the performance warning.
			gbc.gridx = 0;
			gbc.gridy = gbc.RELATIVE;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			add(new MultipleLineLabel(i18n.PERF_WARNING), gbc);
		}
	}
	
	private static final class OutputLabel extends JLabel {
		OutputLabel(String title) {
			super(title);
			setToolTipText(title);
			Dimension ps = getPreferredSize();
			ps.width = 150;
			setPreferredSize(ps);
		}
	}
}
