package net.sourceforge.squirrel_sql.fw.gui.sql;
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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
/**
 * This dialog allows the user to review and maintain
 * the properties for a JDBC driver.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DriverPropertiesDialog extends JDialog
{
	private interface i18n
	{
		String CLOSE = "Close";
		String OK = "OK";
	}

	private DriverPropertiesPanel _propsPnl;

	/** The driver properties. This is only available once OK pressed. */
	private SQLDriverPropertyCollection _driverPropInfo;

	public static SQLDriverPropertyCollection showDialog(Dialog owner,
									SQLDriverPropertyCollection props)
	{
		final DriverPropertiesDialog dlog = new DriverPropertiesDialog(owner, props);
		dlog.setModal(true);
		dlog.setVisible(true);
		return dlog.getSQLDriverPropertyCollection();
	}

	public static SQLDriverPropertyCollection showDialog(Frame owner,
									SQLDriverPropertyCollection props)
	{
		final DriverPropertiesDialog dlog = new DriverPropertiesDialog(owner, props);
		dlog.setModal(true);
		dlog.setVisible(true);
		return dlog.getSQLDriverPropertyCollection();
	}

	public DriverPropertiesDialog(Dialog owner, SQLDriverPropertyCollection props)
	{
		super(owner, "Driver Properties");
		if (props == null)
		{
			throw new IllegalArgumentException("SQLDriverPropertyCollection == null");
		}

		createUserInterface(props);
	}

	public DriverPropertiesDialog(Frame owner, SQLDriverPropertyCollection props)
	{
		super(owner, "Driver Properties");
		if (props == null)
		{
			throw new IllegalArgumentException("SQLDriverPropertyCollection == null");
		}

		createUserInterface(props);
	}

	/**
	 * Retrieve the database driver properties. This is only valid if the
	 * OK button was pressed.
	 *
	 * @return		the database driver properties.
	 */
	public SQLDriverPropertyCollection getSQLDriverPropertyCollection()
	{
		return _driverPropInfo;
	}

	private void createUserInterface(SQLDriverPropertyCollection props)
	{
		Box pnl = Box.createVerticalBox();
		_propsPnl = new DriverPropertiesPanel(props);
		pnl.add(_propsPnl);
		pnl.add(createButtonsPanel());
		setContentPane(pnl);
		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(true);
	}

	private void performClose()
	{
		dispose();
	}

	private void performOk()
	{
		_driverPropInfo = _propsPnl.getSQLDriverProperties();
		dispose();
	}

	private JPanel createButtonsPanel()
	{
		final JPanel pnl = new JPanel();

		JButton okBtn = new JButton(i18n.OK);
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});

		final JButton closeBtn = new JButton(i18n.CLOSE);
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

		return pnl;
	}
}

