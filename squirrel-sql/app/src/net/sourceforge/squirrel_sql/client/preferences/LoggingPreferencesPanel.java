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

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.log.LoggingLevel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

class LoggingPreferencesPanel implements IGlobalPreferencesPanel {
	/** Application API. */
	private IApplication _app;

	private MyPanel _myPanel;

	LoggingPreferencesPanel(IApplication app) {
		super();
		_app = app;
		_myPanel = new MyPanel(_app);
	}

	public void initialize(IApplication app)
			throws IllegalArgumentException {
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
			String DEBUG_JDBC = "JDBC Debug";
			String LOGGING_LEVEL = "Logging level:";
			String TAB_HINT = "Logging";
			String TAB_TITLE = "Logging";
			String JDBC_PERF_WARNING = "Note: JDBC debug will slow down this application.";
			String LOG_PERF_WARNING = "Note: The higher the level of logging the slower the application.";
		}

		/** Application API. */
		private IApplication _app;

		private boolean _initialized = false;

		private LoggingLevelCombo _logCmb = new LoggingLevelCombo();
		private JCheckBox _debugJdbc = new JCheckBox(i18n.DEBUG_JDBC);

		MyPanel(IApplication app) {
			super();
			_app = app;
		}

		void loadData(SquirrelPreferences prefs) {
			if (!_initialized) {
				createUserInterface(prefs);
				_initialized = true;
			}
			_debugJdbc.setSelected(prefs.getDebugJdbc());
			_logCmb.setSelectedItem(LoggingLevel.get(prefs.getLoggingLevel()));
		}

		void applyChanges(SquirrelPreferences prefs) {
			prefs.setDebugJdbc(_debugJdbc.isSelected());
			prefs.setLoggingLevel(_logCmb.getSelectedLoggingLevel().getLevel());
		}

		private void createUserInterface(SquirrelPreferences prefs1) {
			final ApplicationFiles appFiles = _app.getApplicationFiles();

			setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = gbc.WEST;
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(createLoggingPanel(appFiles), gbc);

			++gbc.gridy;
			add(createJDBCDebugPanel(appFiles), gbc);
		}

		private JPanel createLoggingPanel(ApplicationFiles appFiles) {
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("Logging"));
			
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
	
			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(new RightLabel(i18n.LOGGING_LEVEL), gbc);
	
			++gbc.gridx;
			pnl.add(_logCmb, gbc);
	
			gbc.gridx = 0;
			++gbc.gridy;
			pnl.add(new RightLabel("Log File:"), gbc);
	
			++gbc.gridx;
			pnl.add(new OutputLabel(appFiles.getExecutionLogFile().getPath()), gbc);

			gbc.gridx = 0;
			gbc.gridy = gbc.RELATIVE;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			pnl.add(new MultipleLineLabel(i18n.LOG_PERF_WARNING), gbc);
			
			return pnl;
		}

		private JPanel createJDBCDebugPanel(ApplicationFiles appFiles) {
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("JDBC Debug"));
			
			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
	
			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(_debugJdbc, gbc);
	
			gbc.gridx = 0;
			++gbc.gridy;
			pnl.add(new RightLabel("JDBC Debug File:"), gbc);
	
			++gbc.gridx;
			pnl.add(new OutputLabel(appFiles.getJDBCDebugLogFile().getPath()), gbc);

			gbc.gridx = 0;
			gbc.gridy = gbc.RELATIVE;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			pnl.add(new MultipleLineLabel(i18n.JDBC_PERF_WARNING), gbc);
			
			return pnl;
		}
	}

	private static final class RightLabel extends JLabel {
		RightLabel(String title) {
			super(title, SwingConstants.RIGHT);
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

	private static final class LoggingLevelCombo extends JComboBox {
		LoggingLevelCombo() {
			super();
			addItem(LoggingLevel.DEBUG);
			addItem(LoggingLevel.INFO);
			addItem(LoggingLevel.WARN);
			addItem(LoggingLevel.ERROR);
			addItem(LoggingLevel.OFF);
		}

		LoggingLevel getSelectedLoggingLevel() {
			return (LoggingLevel)getSelectedItem();	
		}
	}
}
