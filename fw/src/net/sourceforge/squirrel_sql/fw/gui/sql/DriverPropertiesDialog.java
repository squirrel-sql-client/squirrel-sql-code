package net.sourceforge.squirrel_sql.fw.gui.sql;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Driver;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

public class DriverPropertiesDialog extends JDialog
{
	private final Driver _driver;
	private final String _url;

	public DriverPropertiesDialog(Dialog owner, Driver driver, String url)
		throws SQLException
	{
		super(owner);
		if (driver == null)
		{
			throw new IllegalArgumentException("Driver == null");
		}
		if (url == null)
		{
			throw new IllegalArgumentException("url == null");
		}

		_driver = driver;
		_url = url;
		createUserInterface();
	}

	public DriverPropertiesDialog(Frame owner, Driver driver, String url)
		throws SQLException
	{
		super(owner);
		if (driver == null)
		{
			throw new IllegalArgumentException("Driver == null");
		}
		if (url == null)
		{
			throw new IllegalArgumentException("url == null");
		}

		_driver = driver;
		_url = url;
		createUserInterface();
	}

	private void createUserInterface() throws SQLException
	{
		final JPanel pnl = new JPanel(new BorderLayout());
		setContentPane(pnl);
		DriverPropertiesTable tbl = new DriverPropertiesTable(_driver, _url);
		pnl.add(new JScrollPane(tbl), BorderLayout.CENTER);
		pnl.add(createButtonsPanel(), BorderLayout.SOUTH);
		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(true);
	}

	private JPanel createButtonsPanel()
	{
		final JPanel pnl = new JPanel();

		final JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});

		final JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performClose();
			}
		});

		pnl.add(okBtn);
		pnl.add(closeBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] {okBtn, closeBtn});
		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}

	private void performClose()
	{
		dispose();
	}

	private void performOk()
	{
	}

}

