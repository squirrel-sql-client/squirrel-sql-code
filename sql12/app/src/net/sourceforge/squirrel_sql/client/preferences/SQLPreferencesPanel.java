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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
/**
 * This preferences panel allows maintenance of SQL preferences.
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLPreferencesPanel implements IGlobalPreferencesPanel
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(SQLPreferencesPanel.class);

	/** Panel to be displayed in preferences dialog. */
	private SQLPrefsPanel _myPanel;

	/** Application API. */
	private IApplication _app;

	/**
	 * Default ctor.
	 */
	public SQLPreferencesPanel()
	{
		super();
	}

	/**
	 * Initialize this panel. Called prior to it being displayed.
	 * 
	 * @param	app	Application API.
	 * 
	 * @throws	IllegalArgumentException
	 * 			if <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	public void initialize(IApplication app)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}

		_app = app;

		SQLPrefsPanel pnl = (SQLPrefsPanel)getPanelComponent();
		pnl.loadData(_app, _app.getSquirrelPreferences());
	}

	public synchronized Component getPanelComponent()
	{
		if (_myPanel == null)
		{
			_myPanel = new SQLPrefsPanel();
		}
		return _myPanel;
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(_app.getSquirrelPreferences());
	}

	public String getTitle()
	{
		return SQLPrefsPanel.SQLPrefsPanelI18n.TAB_TITLE;
	}

	public String getHint()
	{
		return SQLPrefsPanel.SQLPrefsPanelI18n.TAB_HINT;
	}

	private static final class SQLPrefsPanel extends JPanel
	{
		/**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface SQLPrefsPanelI18n
		{
			String DEBUG_JDBC = "JDBC Debug (can slow application)";
			String LOGIN_TIMEOUT = "Login Timeout (secs):";
			String TAB_HINT = "SQL";
			String TAB_TITLE = "SQL";
		}

		private IntegerField _loginTimeout = new IntegerField();
		private JCheckBox _debugJdbc = new JCheckBox(SQLPrefsPanelI18n.DEBUG_JDBC);
		private JLabel _jdbcDebugLogFileNameLbl = new OutputLabel(" ");

		SQLPrefsPanel()
		{
			super(new GridBagLayout());
			createUserInterface();
		}

		void loadData(IApplication app, SquirrelPreferences prefs)
		{
			final ApplicationFiles appFiles = new ApplicationFiles();
			_loginTimeout.setInt(prefs.getLoginTimeout());
			_debugJdbc.setSelected(prefs.getDebugJdbc());
			_jdbcDebugLogFileNameLbl.setText(appFiles.getJDBCDebugLogFile().getPath());
		}

		void applyChanges(SquirrelPreferences prefs)
		{
			prefs.setLoginTimeout(_loginTimeout.getInt());
			prefs.setDebugJdbc(_debugJdbc.isSelected());
		}

		private void createUserInterface()
		{
			_loginTimeout.setColumns(4);

			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = gbc.WEST;
			gbc.fill = gbc.NONE;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.weightx = 0;

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(new JLabel(SQLPrefsPanelI18n.LOGIN_TIMEOUT), gbc);

			++gbc.gridx;
			add(_loginTimeout, gbc);

			++gbc.gridx;
			add(new JLabel("Zero means unlimited"), gbc);

			gbc.fill = gbc.HORIZONTAL;
			gbc.gridx = 0;
			++gbc.gridy;
			gbc.gridwidth = gbc.REMAINDER;
			add(_debugJdbc, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			gbc.gridwidth = 1;
			add(new JLabel("JDBC Debug File:", SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			gbc.weightx = 1;
			gbc.gridwidth = gbc.REMAINDER;
			add(_jdbcDebugLogFileNameLbl, gbc);
		}

	}
}