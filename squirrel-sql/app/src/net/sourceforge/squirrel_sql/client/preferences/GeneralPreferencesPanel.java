package net.sourceforge.squirrel_sql.client.preferences;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.OutputLabel;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

class GeneralPreferencesPanel implements IGlobalPreferencesPanel
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(GeneralPreferencesPanel.class);

	/** Panel to be displayed in preferences dialog. */
	private MyPanel _myPanel;

	/** Application API. */
	private IApplication _app;

	/**
	 * Default ctor.
	 */
	public GeneralPreferencesPanel()
	{
		super();
	}

	public void initialize(IApplication app)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}

		_app = app;

		((MyPanel) getPanelComponent()).loadData(_app, _app.getSquirrelPreferences());
	}

	public synchronized Component getPanelComponent()
	{
		if (_myPanel == null)
		{
			_myPanel = new MyPanel();
		}
		return _myPanel;
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(_app.getSquirrelPreferences());
	}

	public String getTitle()
	{
		return MyPanel.i18n.TAB_TITLE;
	}

	public String getHint()
	{
		return MyPanel.i18n.TAB_HINT;
	}

	private static final class MyPanel extends JPanel
	{
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface i18n
		{
			String DEBUG_JDBC = "JDBC Debug (can slow application)";
			String LOGIN_TIMEOUT = "Login Timeout (Seconds):";
			String SHOW_ALIASES_WINDOW_TOOLBAR = "Show Aliases Toolbar";
			String SHOW_CONTENTS = "Show Window Contents While Dragging";
			String SHOW_DRIVERS_WINDOW_TOOLBAR = "Show Drivers Toolbar";
			String SHOW_MAIN_STATUS_BAR = "Show Main Window Status Bar";
			String SHOW_MAIN_TOOL_BAR = "Show Main Window Tool Bar";
			String SHOW_TOOLTIPS = "Show Tooltips";
			String TAB_HINT = "General";
			String TAB_TITLE = "General";
		}

		private JCheckBox _showAliasesToolBar = new JCheckBox(i18n.SHOW_ALIASES_WINDOW_TOOLBAR);
		private JCheckBox _showDriversToolBar = new JCheckBox(i18n.SHOW_DRIVERS_WINDOW_TOOLBAR);
		private JCheckBox _showMainStatusBar = new JCheckBox(i18n.SHOW_MAIN_STATUS_BAR);
		private JCheckBox _showMainToolBar = new JCheckBox(i18n.SHOW_MAIN_TOOL_BAR);
		private JCheckBox _showContents = new JCheckBox(i18n.SHOW_CONTENTS);
		private JCheckBox _showToolTips = new JCheckBox(i18n.SHOW_TOOLTIPS);
		private JCheckBox _useScrollableTabbedPanes =
			new JCheckBox("Use Scrollable Tabbed Panes (JDK1.4 and above)");
		private JLabel _executionLogFileNameLbl = new OutputLabel(" ");
		// Must have at least 1 blank otherwise width gets set to zero.
		private JLabel _logConfigFileNameLbl = new OutputLabel(" ");
		// Must have at least 1 blank otherwise width gets set to zero.

		MyPanel()
		{
			super(new GridBagLayout());
			createUserInterface();
		}

		void loadData(IApplication app, SquirrelPreferences prefs)
		{
			final ApplicationFiles appFiles = new ApplicationFiles();

			_showContents.setSelected(prefs.getShowContentsWhenDragging());
			_showToolTips.setSelected(prefs.getShowToolTips());
			_useScrollableTabbedPanes.setSelected(prefs.useScrollableTabbedPanes());
			_showMainStatusBar.setSelected(prefs.getShowMainStatusBar());
			_showMainToolBar.setSelected(prefs.getShowMainToolBar());
			_showAliasesToolBar.setSelected(prefs.getShowAliasesToolBar());
			_showDriversToolBar.setSelected(prefs.getShowDriversToolBar());

			_executionLogFileNameLbl.setText(appFiles.getExecutionLogFile().getPath());

			String configFile = ApplicationArguments.getInstance().getLoggingConfigFileName();
			_logConfigFileNameLbl.setText(configFile != null ? configFile : "<unspecified>"); //i18n.
		}

		void applyChanges(SquirrelPreferences prefs)
		{
			prefs.setShowContentsWhenDragging(_showContents.isSelected());
			prefs.setShowToolTips(_showToolTips.isSelected());
			prefs.setUseScrollableTabbedPanes(_useScrollableTabbedPanes.isSelected());
			prefs.setShowMainStatusBar(_showMainStatusBar.isSelected());
			prefs.setShowMainToolBar(_showMainToolBar.isSelected());
			prefs.setShowAliasesToolBar(_showAliasesToolBar.isSelected());
			prefs.setShowDriversToolBar(_showDriversToolBar.isSelected());
		}

		private void createUserInterface()
		{
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			add(createAppearancePanel(), gbc);
			++gbc.gridy;
			add(createLoggingPanel(), gbc);
		}

		private JPanel createAppearancePanel()
		{
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
			++gbc.gridy;
			pnl.add(_showMainToolBar, gbc);
			++gbc.gridy;
			pnl.add(_showMainStatusBar, gbc);
			++gbc.gridy;
			pnl.add(_showDriversToolBar, gbc);
			++gbc.gridy;
			pnl.add(_showAliasesToolBar, gbc);

			return pnl;
		}

		private JPanel createLoggingPanel()
		{
			JPanel pnl = new JPanel();
			pnl.setBorder(BorderFactory.createTitledBorder("Logging"));

			pnl.setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = gbc.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			pnl.add(new JLabel("Execution Log File:", SwingConstants.RIGHT), gbc);

			++gbc.gridy;
			pnl.add(new JLabel("Configuration File:", SwingConstants.RIGHT), gbc);

			gbc.weightx = 1.0;

			gbc.gridy = 0;
			++gbc.gridx;
			pnl.add(_executionLogFileNameLbl, gbc);

			++gbc.gridy;
			pnl.add(_logConfigFileNameLbl, gbc);

			return pnl;
		}
	}
}