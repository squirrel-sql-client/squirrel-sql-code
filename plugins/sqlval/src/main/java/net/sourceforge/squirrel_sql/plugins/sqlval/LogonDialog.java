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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlval.cmd.ConnectCommand;
import net.sourceforge.squirrel_sql.plugins.sqlval.cmd.DisconnectCommand;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class LogonDialog extends JDialog
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(LogonDialog.class);


	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(LogonDialog.class);

	/** Current session. */
	private final ISession _session;

	private final WebServicePreferences _prefs;

	private final WebServiceSessionProperties _sessionProps;

	/** Application preferences panel. */
	private AppPreferencesPanel _appPrefsPnl;

	/** Confirmation panel. */
	private SessionSettingsPanel _confirmPnl;

	public LogonDialog(ISession session, WebServicePreferences prefs,
				WebServiceSessionProperties sessionProps)
	{
		// i18n[sqlval.logon=SQL Validation Logon]
		super(session.getApplication().getMainFrame(), s_stringMgr.getString("sqlval.logon"), true);

		if (session == null)
		{
			throw new IllegalArgumentException("ISession = null");
		}
		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences = null");
		}
		if (sessionProps == null)
		{
			throw new IllegalArgumentException("WebServiceSessionProperties = null");
		}

		_session = session;
		_prefs = prefs;
		_sessionProps = sessionProps;

		createGUI();

		// Close existing session.
		try
		{
			new DisconnectCommand(_session, _prefs, _sessionProps).execute();
		}
		catch (BaseException ex)
		{
			s_log.error(ex);
		}
	}

	/**
	 * Close this dialog.
	 */
	private void performClose()
	{
		dispose();
	}

	/**
	 * OK button pressed so logon to web service.
	 */
	private void performOk()
	{
		_appPrefsPnl.save();
		_confirmPnl.save();

		// Connect.
		ConnectCommand cmd = new ConnectCommand(_session, _prefs, _sessionProps);
		try
		{
			cmd.execute();
			dispose();
		}
		catch (Throwable th)
		{
			final String msg = "Error occured when talking to the web service";
			s_log.error(msg, th);
			_session.getApplication().showErrorDialog(msg, th);
		}
	}

	/**
	 * Create this sheets user interface.
	 */
	private void createGUI()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		final JPanel contentPane = new JPanel(new GridBagLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder());
		setContentPane(contentPane);

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(1, 4, 1, 4);
		gbc.weightx = 1;

		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		_appPrefsPnl = new AppPreferencesPanel(_prefs);
		contentPane.add(_appPrefsPnl, gbc);

		gbc.weighty = 0;
		++gbc.gridy;
		_confirmPnl = new SessionSettingsPanel(_prefs, _sessionProps);
		contentPane.add(_confirmPnl, gbc);

		++gbc.gridy;
		contentPane.add(createButtonsPanel(), gbc);

		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(true);
	}

	/**
	 * Create panel at bottom containing the buttons.
	 * 
	 * @return	New panel.
	 */
	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		// i18n[sqlval.logonOk=OK]
		JButton okBtn = new JButton(s_stringMgr.getString("sqlval.logonOk"));
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});
		// i18n[sqlval.logonClose=Close]
		JButton closeBtn = new JButton(s_stringMgr.getString("sqlval.logonClose"));
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performClose();
			}
		});

		GUIUtils.setJButtonSizesTheSame(new JButton[] { okBtn, closeBtn });

		pnl.add(okBtn);
		pnl.add(closeBtn);

		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}
}

