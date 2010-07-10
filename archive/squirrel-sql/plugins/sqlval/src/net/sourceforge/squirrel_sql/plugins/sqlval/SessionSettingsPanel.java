package net.sourceforge.squirrel_sql.plugins.sqlval;
/*
 * Copyright (C) 2002 Colin Bell
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

class SessionSettingsPanel extends JPanel
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(SessionSettingsPanel.class);

	/** Preferences object. */
	private final WebServicePreferences _prefs;

	/** Session properties object. */
	private final WebServiceSessionProperties _sessionProps;

	/** Use anonymous DBMS. */
	private JCheckBox _anonDBMSChk = new JCheckBox("Anonymous");

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
		gbc.fill = gbc.HORIZONTAL;
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
		pnl.setBorder(BorderFactory.createTitledBorder("DBMS"));

		pnl.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = gbc.HORIZONTAL;
		gbc.insets = new Insets(2, 4, 2, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = gbc.NORTHWEST;
		pnl.add(_anonDBMSChk, gbc);

		++gbc.gridy;
		pnl.add(new JLabel("DBMS Name:", JLabel.RIGHT), gbc);

		++gbc.gridy;
		pnl.add(new JLabel("DBMS Version:", JLabel.RIGHT), gbc);

		++gbc.gridy;
		pnl.add(new JLabel("Technology:", JLabel.RIGHT), gbc);

		++gbc.gridy;
		pnl.add(new JLabel("Technology Version:", JLabel.RIGHT), gbc);

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
