package net.sourceforge.squirrel_sql.plugins.sessionscript;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import net.sourceforge.squirrel_sql.client.IApplication;

public class ViewSessionScriptsPanel extends JPanel
{
	private SessionScriptPlugin _plugin;
	private IApplication _app;
	private SQLALiasesCombo _aliasesCmb = new SQLALiasesCombo();
	private JTextArea _sqlEntry = new JTextArea();
	private JButton _saveBtn;

	ViewSessionScriptsPanel(SessionScriptPlugin plugin, IApplication app)
	{
		super();
		if (plugin == null)
		{
			throw new IllegalArgumentException("SessionScriptPlugin == null");
		}
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_plugin = plugin;
		_app = app;

		createUserInterface();
		refreshScript();
	}

	private void refreshScript()
	{
		boolean setText = false;
		ISQLAlias alias = _aliasesCmb.getSelectedSQLAlias();
		if (alias != null)
		{
			AliasScript script = _plugin.getScriptsCache().get(alias);
			String sql = script.getSQL();
			if (sql != null && sql.length() > 0)
			{
				_sqlEntry.setText(sql);
				setText = true;
			}
		}
		if (!setText)
		{
			_sqlEntry.setText("");
		}
		_saveBtn.setEnabled(false);
	}

	private void createUserInterface()
	{
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		_aliasesCmb.load(_app);
		_aliasesCmb.addActionListener(new AliasesComboListener(this));

		_sqlEntry.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		_sqlEntry.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent evt)
			{
				_saveBtn.setEnabled(true);
			}
			public void insertUpdate(DocumentEvent evt)
			{
				_saveBtn.setEnabled(true);
			}
			public void removeUpdate(DocumentEvent evt)
			{
				_saveBtn.setEnabled(true);
			}
		});

		_saveBtn = new JButton("Save");
		_saveBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				synchronized (_sqlEntry)
				{
					ISQLAlias alias = _aliasesCmb.getSelectedSQLAlias();
					if (alias != null)
					{
						AliasScript script = _plugin.getScriptsCache().get(alias);
						script.setSQL(_sqlEntry.getText());
						_saveBtn.setEnabled(false);
					}
				}
			}
		});

		setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = gbc.NORTHWEST;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4, 4, 4, 4);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = gbc.HORIZONTAL;
		add(_aliasesCmb, gbc);

		gbc.fill = gbc.BOTH;
		gbc.weighty = 1.0;
		++gbc.gridy;
		add(new JScrollPane(_sqlEntry), gbc);

		gbc.fill = gbc.HORIZONTAL;
		gbc.weightx = 0;
		gbc.weighty = 0;
		++gbc.gridx;
		add(_saveBtn, gbc);
	}

	/**
	 * THis listener keeps the script text area synched with the Aliases
	 * combobox. I.E. as a new alias is selected the script for that alias
	 * is displayed.
	 */
	private static final class AliasesComboListener implements ActionListener
	{
		private ViewSessionScriptsPanel _pnl;

		AliasesComboListener(ViewSessionScriptsPanel pnl)
		{
			super();
			_pnl = pnl;
		}

		public void actionPerformed(ActionEvent evt)
		{
			_pnl.refreshScript();
		}

	}
}
