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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;

class ValidationDialog extends JDialog
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(ValidationDialog.class);

	/** Application API. */
	private final IApplication _app;

	/** Validation properties object. */
	private final ValidateSQLAction.ValidationProps _valProps;

	/** Command to execute validation. */
	private final ICommand _valCmd;

	/** Application preferences panel. */
	private AppPreferencesPanel _appPrefsPnl;
	
	/** Confirmation panel. */
	private SessionSettingsPanel _confirmPnl;

	ValidationDialog(IApplication app, ValidateSQLAction.ValidationProps valProps,
					ICommand valCmd)
	{
		super(app.getMainFrame(), "SQL Validation", true);
		_app = app;
		_valProps = valProps;
		_valCmd = valCmd;

		createGUI();
	}

	/**
	 * Close this dialog.
	 */
	private void performClose()
	{
		dispose();
	}

	/**
	 * OK button pressed so validate SQL. If successful then close dialog.
	 */
	private void performOk()
	{
		_appPrefsPnl.save();
		_confirmPnl.save();

		ValidateSQLCommand cmd = new ValidateSQLCommand(_valProps._prefs,
										_valProps._sessionProps, _valProps._sql,
										_valProps._stmtSepChar, _valProps._solComment);
		try
		{
			cmd.execute();
			_valProps._msgHandler.showMessage(cmd.getResults());
			dispose();
		}
		catch (Throwable th)
		{
			final String msg = "Error occured when talking to the web service";
			s_log.error(msg, th);
			_app.showErrorDialog(msg, th);
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
		gbc.fill = gbc.HORIZONTAL;
		gbc.insets = new Insets(1, 4, 1, 4);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		_appPrefsPnl = new AppPreferencesPanel(_valProps._prefs);
		contentPane.add(_appPrefsPnl, gbc);

		++gbc.gridy;
		_confirmPnl = new SessionSettingsPanel(_valProps._prefs, _valProps._sessionProps);
		contentPane.add(_confirmPnl, gbc);

		++gbc.gridy;
		contentPane.add(createButtonsPanel(), gbc);

		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(false);
	}

	/**
	 * Create panel at bottom containing the buttons.
	 * 
	 * @return	New panel.
	 */
	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});
		JButton closeBtn = new JButton("Close");
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
