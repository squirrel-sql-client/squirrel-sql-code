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
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.gui.OutputLabel;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

class AppPreferencesPanel extends JPanel
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(AppPreferencesPanel.class);

	private static final String INFO = "This plugin uses version 1.0 of the " +
		"SQL Validator Web Service developed by MimerSQL " +
		"http://sqlvalidator.mimer.com. " +
		"The SQL Statement is stored anonymously to be used by " +
		" the ISO SQL Standards committee.";

	/** Preferences object. */
	private final WebServicePreferences _prefs;

	/** Logon on as Anonymus checkbox. */
	private JCheckBox _anonLogonChk = new JCheckBox("Anonymous");

	/** Use anonymous client. */
	private JCheckBox _anonClientChk = new JCheckBox("Anonymous");

	/** User name D/E. */
	private JTextField _userNameText = new JTextField();

	/** Password D/E. */
	private JPasswordField _passwordText = new JPasswordField();

	/** Client name. */
	private OutputLabel _clientNameLbl = new OutputLabel(" ");

	/** Client version. */
	private OutputLabel _clientVersionLbl = new OutputLabel(" ");

	/** Show confirmation dialog checkbox. */
//	private JCheckBox _showConfirmationDialogChk = new JCheckBox("Show confirmation dialog");

	AppPreferencesPanel(WebServicePreferences prefs)
	{
		super(new GridBagLayout());

		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences == null");
		}

		_prefs = prefs;
		createGUI();
		loadData();
	}

	void loadData()
	{
		_anonLogonChk.setSelected(_prefs.getUseAnonymousLogon());
		_userNameText.setText(_prefs.getUserName());
		_passwordText.setText(_prefs.retrievePassword());
		_anonClientChk.setSelected(_prefs.getUseAnonymousClient());
		_clientNameLbl.setText(_prefs.getClientName());
		_clientVersionLbl.setText(_prefs.getClientVersion());
//		_showConfirmationDialogChk.setSelected(_prefs.getShowConfirmationDialog());

		setControlState();
	}

	/**
	 * Save panel contents to preferences.
	 */
	void save()
	{
		_prefs.setUseAnonymousLogon(_anonLogonChk.isSelected());
		_prefs.setUserName(_userNameText.getText());
		_prefs.setPassword(new String(_passwordText.getPassword()));
		_prefs.setUseAnonymousClient(_anonClientChk.isSelected());
		_prefs.setClientName(_clientNameLbl.getText());
		_prefs.setClientVersion(_clientVersionLbl.getText());
//		_prefs.setShowConfirmationDialog(_showConfirmationDialogChk.isSelected());
	}

	/**
	 * Set the state of the controls from the preferences.
	 */
	private void setControlState()
	{
		setAnonymousUserControlState(_prefs.getUseAnonymousLogon());
	}

	/**
	 * Set control state depending on the current state of the "anonymous user"
	 * checkbox.
	 */
	private void setAnonymousUserControlState(boolean state)
	{
		_userNameText.setEnabled(!state);
		_passwordText.setEnabled(!state);
	}

	/**
	 * Create this panel.
	 */
	private void createGUI()
	{
		setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = gbc.BOTH;
		gbc.insets = new Insets(1, 4, 1, 4);
		gbc.weightx = 1;

		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(createInfoPanel(), gbc);

		gbc.weighty = 0;
		++gbc.gridy;
		add(createLogonPanel(), gbc);

		++gbc.gridy;
		add(createClientPanel(), gbc);
//
//		++gbc.gridy;
//		add(createGeneralPanel(), gbc);
	}

	/**
	 * This creates the info panel.
	 * 
	 * @return	New panel.
	 */
	private JPanel createInfoPanel()
	{
		final JPanel pnl = new JPanel();
		pnl.setBorder(BorderFactory.createTitledBorder("Info"));
		pnl.setLayout(new BorderLayout());
		final MultipleLineLabel lbl = new MultipleLineLabel(INFO);
		lbl.setCaretPosition(0);
		lbl.setRows(3);
		lbl.setColumns(30);
		final JScrollPane sp = new JScrollPane(lbl);
		sp.setBorder(BorderFactory.createEmptyBorder());
		pnl.add(sp, BorderLayout.CENTER);
		return pnl;
	}

	/**
	 * This creates the panel containing the logon information.
	 * 
	 * @return	New panel.
	 */
	private JPanel createLogonPanel()
	{
		_userNameText.setColumns(15);
		_passwordText.setColumns(15);
	
		JPanel pnl = new JPanel();
		pnl.setBorder(BorderFactory.createTitledBorder("Log on as"));

		pnl.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 4, 2, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.anchor = gbc.NORTHWEST;
		pnl.add(_anonLogonChk, gbc);

		gbc.fill = gbc.HORIZONTAL;
		gbc.anchor = gbc.EAST;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		++gbc.gridx;
		pnl.add(new JLabel("User:", JLabel.RIGHT), gbc);

		++gbc.gridy;
		pnl.add(new JLabel("Password:", JLabel.RIGHT), gbc);
		
		gbc.fill = gbc.NONE;
		++gbc.gridx;
		gbc.gridy = 0;
		gbc.weightx = 0;
		pnl.add(_userNameText, gbc);

		++gbc.gridy;
		pnl.add(_passwordText, gbc);

		_anonLogonChk.addActionListener(new AnonymousCheckBoxListener());

		return pnl;
	}

	/**
	 * This creates the panel containing the client information.
	 * 
	 * @return	New panel.
	 */
	private JPanel createClientPanel()
	{
		JPanel pnl = new JPanel();
		pnl.setBorder(BorderFactory.createTitledBorder("Client"));

		pnl.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = gbc.HORIZONTAL;
		gbc.insets = new Insets(2, 4, 2, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		pnl.add(_anonClientChk, gbc);

		gbc.gridwidth = 1;
		++gbc.gridy;
		pnl.add(new JLabel("Client:", JLabel.RIGHT), gbc);

		++gbc.gridy;
		pnl.add(new JLabel("Version:", JLabel.RIGHT), gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1;
		pnl.add(_clientNameLbl, gbc);

		++gbc.gridy;
		pnl.add(_clientVersionLbl, gbc);

		return pnl;
	}

//	private JPanel createGeneralPanel(/*WebServicePreferences prefs*/)
//	{
//		JPanel pnl = new JPanel();
//		pnl.setBorder(BorderFactory.createTitledBorder("General"));
//
//		pnl.setLayout(new GridBagLayout());
//		final GridBagConstraints gbc = new GridBagConstraints();
//		gbc.insets = new Insets(0, 0, 0, 0);
//		gbc.anchor = gbc.WEST;
//
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		pnl.add(_showConfirmationDialogChk, gbc);
//
//		return pnl;
//	}

	/**
	 * Listener for the "Anonymous Logon" checkbox. This keeps the
	 * user and password fields enabled/disabled appropriately.
	 */
	private final class AnonymousCheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			setAnonymousUserControlState(_anonLogonChk.isSelected());
		}

	}
}
