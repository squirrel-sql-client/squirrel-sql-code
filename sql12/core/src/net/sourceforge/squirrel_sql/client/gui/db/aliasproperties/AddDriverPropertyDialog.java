/*
 * Copyright (C) 2009 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import org.apache.commons.lang.StringUtils;

/**
 * Dialog that gets displayed when a user chooses to add a driver property to the table.
 */
public class AddDriverPropertyDialog extends JDialog
{
	private static final long serialVersionUID = 4889632277323001185L;

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AddDriverPropertyDialog.class);

	private static interface i18n
	{
		// i18n[AddDriverPropertyDialog.addButtonLabel=Add]
		String addButtonLabel = s_stringMgr.getString("AddDriverPropertyDialog.addButtonLabel");

		// i18n[AddDriverPropertyDialog.invalidNameMessage=The driver property name must be provided]
		String invalidNameMessage = s_stringMgr.getString("AddDriverPropertyDialog.invalidNameMessage");

		// i18n[AddDriverPropertyDialog.invalidNameTitle=Invalid Name]
		String invalidNameTitle = s_stringMgr.getString("AddDriverPropertyDialog.invalidNameTitle");

		// i18n[AddDriverPropertyDialog.propertyDescriptionLabel=Property Description:]
		String propertyDescriptionLabel =
			s_stringMgr.getString("AddDriverPropertyDialog.propertyDescriptionLabel");

		// i18n[AddDriverPropertyDialog.propertyNameLabel=Property Name:]
		String propertyNameLabel = s_stringMgr.getString("AddDriverPropertyDialog.propertyNameLabel");

		// i18n[AddDriverPropertyDialog.propertyValueLabel=Property Value:]
		String propertyValueLabel = s_stringMgr.getString("AddDriverPropertyDialog.propertyValueLabel");

	}

	JLabel propertyNameLbl = new JLabel(i18n.propertyNameLabel);

	JTextField propertyNameTF = new JTextField();

	JLabel propertyValueLbl = new JLabel(i18n.propertyValueLabel);

	JTextField propertyValueTF = new JTextField();

	JLabel propertyDescriptionLbl = new JLabel(i18n.propertyDescriptionLabel);

	JTextField propertyDescriptionTF = new JTextField();

	JButton addButton = new JButton(i18n.addButtonLabel);

	DriverPropertiesTable driverPropertiesTable = null;

	public AddDriverPropertyDialog(DriverPropertiesTable table)
	{
		this.driverPropertiesTable = table;
		JPanel panel = new JPanel(new GridLayout(3, 2));
		JPanel buttonPanel = new JPanel(new FlowLayout());
		panel.add(propertyNameLbl);
		panel.add(propertyNameTF);
		panel.add(propertyValueLbl);
		panel.add(propertyValueTF);
		panel.add(propertyDescriptionLbl);
		panel.add(propertyDescriptionTF);

		addButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				String name = propertyNameTF.getText();
				if (!StringUtils.isEmpty(name))
				{
					String value = propertyValueTF.getText();
					String desc = propertyDescriptionTF.getText();
					driverPropertiesTable.addProperty(name, value, desc);
					AddDriverPropertyDialog.this.setVisible(false);
				}
				else
				{
					JOptionPane.showMessageDialog(AddDriverPropertyDialog.this, i18n.invalidNameMessage,
						i18n.invalidNameTitle, JOptionPane.ERROR_MESSAGE);
				}
				
			}

		});
		buttonPanel.add(addButton);

		super.getContentPane().setLayout(new BorderLayout());
		super.getContentPane().add(panel, BorderLayout.CENTER);
		super.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setSize(295, 120);
	}

}