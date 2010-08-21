package net.sourceforge.squirrel_sql.plugins.sqlval;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.fw.gui.OutputLabel;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

class SessionSettingsPanel extends JPanel
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SessionSettingsPanel.class);


	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(SessionSettingsPanel.class);

	/** Preferences object. */
	private final WebServicePreferences _prefs;

	/** Session properties object. */
	private final WebServiceSessionProperties _sessionProps;

	/** Use anonymous DBMS. */
	// i18n[sqlval.settingsAnon=Anonymous]
	private JCheckBox _anonDBMSChk = new JCheckBox(s_stringMgr.getString("sqlval.settingsAnon"));

	/** DBMS name. */
	private OutputLabel _dbmsNameLbl = new OutputLabel(" ");

	/** DBMS version. */
	private OutputLabel _dbmsVersionLbl = new OutputLabel(" ");

	/** Technology name. */
	private OutputLabel _techNameLbl = new OutputLabel(" ");

	/** Technology version. */
	private OutputLabel _techVersionLbl = new OutputLabel(" ");

	SessionSettingsPanel(WebServicePreferences prefs,
								WebServiceSessionProperties sessionProps)
	{
		super(new GridBagLayout());

		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences == null");
		}
		if (sessionProps == null)
		{
			throw new IllegalArgumentException("WebServiceSessionProperties == null");
		}

		_prefs = prefs;
		_sessionProps = sessionProps;
		createGUI();
		loadData();
	}

	void loadData()
	{
		_anonDBMSChk.setSelected(_sessionProps.getUseAnonymousDBMS());
		_dbmsNameLbl.setText(_sessionProps.getTargetDBMSName());
		_dbmsVersionLbl.setText(_sessionProps.getTargetDBMSVersion());
		_techNameLbl.setText(_sessionProps.getConnectionTechnology());
		_techVersionLbl.setText(_sessionProps.getConnectionTechnologyVersion());
	}

	/**
	 * Save panel contents to perferences.
	 */
	void save()
	{
		_sessionProps.setUseAnonymousDBMS(_anonDBMSChk.isSelected());
	}

	/**
	 * Create this panel.
	 */
	private void createGUI()
	{
		setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(1, 4, 1, 4);
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(createDBMSPanel(), gbc);
	}

	/**
	 * This creates the panel containing the DBMS information.
	 * 
	 * @return	New panel.
	 */
	private JPanel createDBMSPanel()
	{
		JPanel pnl = new JPanel();
		// i18n[sqlval.dbms=DBMS]
		pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("sqlval.dbms")));

		pnl.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 4, 2, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		pnl.add(_anonDBMSChk, gbc);

		++gbc.gridy;
		// i18n[sqlval.dbmsName=DBMS Name:]
		pnl.add(new JLabel(s_stringMgr.getString("sqlval.dbmsName"), JLabel.RIGHT), gbc);

		++gbc.gridy;
		// i18n[sqlval.dbmsVersion=DBMS Version:]
		pnl.add(new JLabel(s_stringMgr.getString("sqlval.dbmsVersion"), JLabel.RIGHT), gbc);

		++gbc.gridy;
		// i18n[sqlval.technology=Technology:]
		pnl.add(new JLabel(s_stringMgr.getString("sqlval.technology"), JLabel.RIGHT), gbc);

		++gbc.gridy;
		// i18n[sqlval.technologyVersion=Technology Version:]
		pnl.add(new JLabel(s_stringMgr.getString("sqlval.technologyVersion"), JLabel.RIGHT), gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1;
		pnl.add(_dbmsNameLbl, gbc);

		++gbc.gridy;
		pnl.add(_dbmsVersionLbl, gbc);

		++gbc.gridy;
		pnl.add(_techNameLbl, gbc);

		++gbc.gridy;
		pnl.add(_techVersionLbl, gbc);

		return pnl;
	}
}

