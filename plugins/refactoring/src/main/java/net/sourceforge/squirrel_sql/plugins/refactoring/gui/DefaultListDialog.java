package net.sourceforge.squirrel_sql.plugins.refactoring.gui;

/*
 * Copyright (C) 2007 Daniel Regli & Yannick Winiger
 * http://sourceforge.net/projects/squirrel-sql
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

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class DefaultListDialog extends JDialog
{

	private static final long serialVersionUID = 2908275309430303054L;

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DefaultListDialog.class);

	private final IDatabaseObjectInfo[] _objectInfo;

	private String _selectItem = "";

	/**
	 * If the dialog is used to select indexes.
	 */
	public static final int DIALOG_TYPE_INDEX = 3;

	/**
	 * If the dilaog is used to select unique constraints.
	 */
	public static final int DIALOG_TYPE_UNIQUE_CONSTRAINTS = 4;

	/**
	 * If the dialog is used to drop sequences.
	 */
	public static final int DIALOG_TYPE_FOREIGN_KEY = 5;

	private interface i18n
	{
		String TABLE_NAME_LABEL = s_stringMgr.getString("DefaultListDialog.tableNameLabel");

		String CANCEL_BUTTON_LABEL = s_stringMgr.getString("AbstractRefactoringDialog.cancelButtonLabel");

		String FOREIGN_KEY_LABEL = s_stringMgr.getString("DefaultListDialog.foreignKeyNameLabel");

		String INDEX_LABEL = s_stringMgr.getString("DefaultListDialog.indexNameLabel");

		String UNIQUE_CONSTRAINT_LABEL = s_stringMgr.getString("DefaultListDialog.uniqueConstraintLabel");

		String OK_BUTTON_LABEL = s_stringMgr.getString("DefaultListDialog.selectButtonLabel");
	}

	private JButton _executeButton = null;

	private JList _columnList;

	public DefaultListDialog(IDatabaseObjectInfo[] objectInfo, String tableName, int dialogType)
	{
		this._objectInfo = objectInfo;

		setTypeByID(dialogType);
		init(tableName);
	}

	/**
	 * Finds and sets the correct title for the specific type dialog.
	 * 
	 * @param dialogType
	 *           dialog type.
	 */
	private void setTypeByID(int dialogType)
	{
		String object = "";
		switch (dialogType)
		{

		case DIALOG_TYPE_INDEX:
			object = i18n.INDEX_LABEL;
			break;

		case DIALOG_TYPE_FOREIGN_KEY:
			object = i18n.FOREIGN_KEY_LABEL;
			break;

		case DIALOG_TYPE_UNIQUE_CONSTRAINTS:
			object = i18n.UNIQUE_CONSTRAINT_LABEL;
			break;
		default:
		}
		_selectItem = object;
		setTitle(s_stringMgr.getString("DefaultDropDialog.title", object));
	}

	public String getSelectedIndex()
	{
		return _columnList.getSelectedValue().toString();
	}

	public ArrayList<IDatabaseObjectInfo> getSelectedItems()
	{
		ArrayList<IDatabaseObjectInfo> idbo = new ArrayList<IDatabaseObjectInfo>();
		String[] simpleNames = getSimpleNames(_objectInfo);
		for (int index : _columnList.getSelectedIndices())
		{
			for (IDatabaseObjectInfo info : _objectInfo)
			{
				if (info.getSimpleName().equals(simpleNames[index]))
				{
					idbo.add(info);
					break;
				}
			}
		}
		return idbo;
	}

	public void addColumnSelectionListener(ActionListener columnListSelectionActionListener)
	{
		_executeButton.addActionListener(columnListSelectionActionListener);
	}

	/**
	 * Creates the UI for this dialog.
	 * 
	 * @param tableName
	 *           the name of the table where the index is used.
	 */
	private void init(String tableName)
	{
		super.setModal(true);

		setSize(425, 250);
		EmptyBorder border = new EmptyBorder(new Insets(5, 5, 5, 5));
		Dimension mediumField = new Dimension(126, 20);

		JPanel pane = new JPanel();
		pane.setLayout(new GridBagLayout());
		pane.setBorder(new EmptyBorder(10, 0, 0, 30));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = -1;

		// Table name
		JLabel tableNameLabel = getBorderedLabel(i18n.TABLE_NAME_LABEL, border);
		pane.add(tableNameLabel, getLabelConstraints(c));

		JTextField tableNameTextField = new JTextField(tableName);
		tableNameTextField.setPreferredSize(mediumField);
		tableNameTextField.setEditable(false);
		pane.add(tableNameTextField, getFieldConstraints(c));

		// Column list
		JLabel columnListLabel = getBorderedLabel(_selectItem, border);
		columnListLabel.setVerticalAlignment(JLabel.NORTH);
		pane.add(columnListLabel, getLabelConstraints(c));

		_columnList = new JList(getSimpleNames(_objectInfo));
		_columnList.addListSelectionListener(new ColumnListSelectionListener());
		_columnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		JScrollPane sp = new JScrollPane(_columnList);
		c = getFieldConstraints(c);
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		pane.add(sp, c);

		Container contentPane = super.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(pane, BorderLayout.CENTER);

		contentPane.add(getButtonPanel(), BorderLayout.SOUTH);
	}

	private JPanel getButtonPanel()
	{
		JPanel result = new JPanel();
		_executeButton = new JButton(i18n.OK_BUTTON_LABEL);

		JButton cancelButton = new JButton(i18n.CANCEL_BUTTON_LABEL);
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
			}
		});
		result.add(_executeButton);
		result.add(cancelButton);
		return result;
	}

	private String[] getSimpleNames(IDatabaseObjectInfo[] dbInfo)
	{
		ArrayList<String> simpleNames = new ArrayList<String>();
		for (IDatabaseObjectInfo info : dbInfo)
		{
			if (!simpleNames.contains(info.getSimpleName()))
				simpleNames.add(info.getSimpleName());
		}
		return simpleNames.toArray(new String[] {});
	}

	private GridBagConstraints getLabelConstraints(GridBagConstraints c)
	{
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.NORTHEAST;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		return c;
	}

	private GridBagConstraints getFieldConstraints(GridBagConstraints c)
	{
		c.gridx++;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		return c;
	}

	private JLabel getBorderedLabel(String text, Border border)
	{
		JLabel result = new JLabel(text);
		result.setBorder(border);
		result.setPreferredSize(new Dimension(115, 20));
		result.setHorizontalAlignment(SwingConstants.RIGHT);
		return result;
	}

	private class ColumnListSelectionListener implements ListSelectionListener
	{

		/**
		 * Rules to handle enabling/disabling the buttons in this dialog. Handle all cases where buttons should
		 * be disable first; if every rule passes then activate.
		 */
		public void valueChanged(ListSelectionEvent e)
		{
			int[] selected = _columnList.getSelectedIndices();

			if (selected == null || selected.length == 0)
			{
				activate(_executeButton, false);
				return;
			}

			// All rules passed, so activate the button
			activate(_executeButton, true);
		}

	}

	private void activate(JButton button, boolean enable)
	{
		if (button != null)
		{
			button.setEnabled(enable);
		}
	}
}
