package net.sourceforge.squirrel_sql.client.preferences;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

class GeneralPreferencesPanel implements IGlobalPreferencesPanel {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(GeneralPreferencesPanel.class);

	private MyPanel _myPanel;

	private IApplication _app;

	public GeneralPreferencesPanel() {
		super();
	}

	public void initialize(IApplication app)
			throws IllegalArgumentException {
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}

		_app = app;

		((MyPanel)getPanelComponent()).loadData(_app, _app.getSquirrelPreferences());
	}

	public synchronized Component getPanelComponent() {
		if (_myPanel == null) {
			_myPanel = new MyPanel();
		}
		return _myPanel;
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

	private static final class MyPanel extends Box {
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface i18n {
			String DEBUG_JDBC = "JDBC Debug";
			String JDBC_PERF_WARNING = "Note: JDBC debug will slow down this application.";
			String LOGIN_TIMEOUT = "Login Timeout (Seconds):";
			String SHOW_CONTENTS = "Show Window Contents While Dragging";
			String SHOW_TOOLTIPS = "Show Tooltips";
			String TAB_HINT = "General";
			String TAB_TITLE = "General";
		}

		private JCheckBox _showContents = new JCheckBox(i18n.SHOW_CONTENTS);
		private JCheckBox _showToolTips = new JCheckBox(i18n.SHOW_TOOLTIPS);
		private JCheckBox _useScrollableTabbedPanes = new JCheckBox("Use Scrollable Tabbed Panes (JDK1.4 and above)");
		private IntegerField _loginTimeout = new IntegerField();
		private JLabel _executionLogFileNameLbl = new OutputLabel(" ");// Must have at least 1 blank otherwise width gets set to zero.
		private JLabel _logConfigFileNameLbl = new OutputLabel(" ");// Must have at least 1 blank otherwise width gets set to zero.
		private JCheckBox _debugJdbc = new JCheckBox(i18n.DEBUG_JDBC);
		private JLabel _jdbcDebugLogFileNameLbl = new OutputLabel(" ");// Must have at least 1 blank otherwise width gets set to zero.

		MyPanel() {
			super(BoxLayout.Y_AXIS);
			createUserInterface();
		}

		void loadData(IApplication app, SquirrelPreferences prefs) {
			final ApplicationFiles appFiles = new ApplicationFiles();

			_showContents.setSelected(prefs.getShowContentsWhenDragging());
			_showToolTips.setSelected(prefs.getShowToolTips());
			_useScrollableTabbedPanes.setSelected(prefs.useScrollableTabbedPanes());

			_loginTimeout.setInt(prefs.getLoginTimeout());
			_executionLogFileNameLbl.setText(appFiles.getExecutionLogFile().getPath());

			String configFile = ApplicationArguments.getInstance().getLoggingConfigFileName();
			_logConfigFileNameLbl.setText(configFile != null ? configFile : "<unspecified>");//i18n.

			_debugJdbc.setSelected(prefs.getDebugJdbc());
			_jdbcDebugLogFileNameLbl.setText(appFiles.getJDBCDebugLogFile().getPath());
		}

		void applyChanges(SquirrelPreferences prefs) {
			prefs.setShowContentsWhenDragging(_showContents.isSelected());
			prefs.setShowToolTips(_showToolTips.isSelected());
			prefs.setUseScrollableTabbedPanes(_useScrollableTabbedPanes.isSelected());
			prefs.setLoginTimeout(_loginTimeout.getInt());
			prefs.setDebugJdbc(_debugJdbc.isSelected());
		}

		private void createUserInterface() {
			add(createAppearancePanel());
			add(createSQLPanel());
			add(createLoggingPanel());
			add(createJDBCDebugPanel());
		}

		private JPanel createAppearancePanel() {
			_loginTimeout.setColumns(4);
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("Appearance"));

			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			pnl.add(_showContents, gbc);
			++gbc.gridy;
			pnl.add(_showToolTips, gbc);
			++gbc.gridy;
			pnl.add(_useScrollableTabbedPanes, gbc);

			return pnl;
		}

		private JPanel createSQLPanel() {
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("SQL"));

			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = gbc.WEST;

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(new JLabel(i18n.LOGIN_TIMEOUT), gbc);

			++gbc.gridx;
			gbc.weightx = 1.0;
			pnl.add(_loginTimeout, gbc);

			++gbc.gridx;
			gbc.weightx = 0.0;
			pnl.add(new JLabel("Zero means unlimited"), gbc);

			return pnl;
		}

		private JPanel createLoggingPanel() {
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("Logging"));

			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(new RightLabel("Execution Log File:"), gbc);

			++gbc.gridy;
			pnl.add(new RightLabel("Configuration File:"), gbc);

			gbc.weightx = 1.0;

			gbc.gridy = 0;
			++gbc.gridx;
			pnl.add(_executionLogFileNameLbl, gbc);

			++gbc.gridy;
			pnl.add(_logConfigFileNameLbl, gbc);

			return pnl;
		}

		private JPanel createJDBCDebugPanel() {
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("JDBC Debug"));

			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = gbc.WEST;

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 2;
			pnl.add(_debugJdbc, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			gbc.gridwidth = 1;
			pnl.add(new RightLabel("JDBC Debug File:"), gbc);

			++gbc.gridx;
//			gbc.weightx = 1;
			pnl.add(_jdbcDebugLogFileNameLbl, gbc);

			gbc.weightx = 0;
			gbc.gridx = 0;
			++gbc.gridy;
//			gbc.gridy = gbc.RELATIVE;
			gbc.gridwidth = 2;
//			gbc.gridwidth = GridBagConstraints.REMAINDER;
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
		OutputLabel(String text) {
			super(text);
			setToolTipText(text);
			Dimension ps = getPreferredSize();
			ps.width = 150;
			setPreferredSize(ps);
		}
		public void setText(String text) {
			super.setText(text);
			setToolTipText(text);
		}
	}
}
