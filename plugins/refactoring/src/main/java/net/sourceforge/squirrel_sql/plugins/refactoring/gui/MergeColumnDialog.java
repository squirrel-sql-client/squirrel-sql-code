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

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MergeColumnDialog extends AbstractRefactoringTabbedDialog
{

	private static final long serialVersionUID = 3883169067774317873L;

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(MergeColumnDialog.class);

	protected interface i18n
	{
		String DIALOG_TITLE = s_stringMgr.getString("MergeColumnDialog.title");

		String PROPERTIES_TABLENAME_LABEL = s_stringMgr.getString("MergeColumnDialog.tableNameLabel");

		String PROPERTIES_COLUMN1_LABEL = s_stringMgr.getString("MergeColumnDialog.column1Label");

		String PROPERTIES_MERGE_IN_NEW_COLUMN_LABEL =
			s_stringMgr.getString("MergeColumnDialog.mergeInNewColumnLabel");

		String PROPERTIES_MERGE_IN_EXISTING_COLUMN_LABEL =
			s_stringMgr.getString("MergeColumnDialog.mergeInExistingColumnLabel");

		String PROPERTIES_MERGE_IN_NEW_COLUMN_NAME_LABEL =
			s_stringMgr.getString("MergeColumnDialog.mergeNewColumnNameLabel");

		String TABBEDPANE_PROPERTIES_LABEL = s_stringMgr.getString("MergeColumnDialog.propertiesTabName");

		String PROPERTIES_COLUMN2_LABEL = s_stringMgr.getString("MergeColumnDialog.column2Label");

		String PROPERTIES_MERGE_TO_LABEL = s_stringMgr.getString("MergeColumnDialog.mergeToLabel");

		String PROPERTIES_JOIN_STRING_LABEL = s_stringMgr.getString("MergeColumnDialog.joinStringLabel");

	}

	private final String _localTableName;

	private final String[] _localTableColumns;

	// private ColumnsTab _columnTab;
	private PropertiesTab _propertiesTab;

	public MergeColumnDialog(String localTable, String[] localTableColumns)
	{
		super(new Dimension(430, 350));
		_localTableName = localTable;
		_localTableColumns = localTableColumns;
		init();
	}

	private void init()
	{

		// _columnTab = new MergeColumnDialog.ColumnsTab();
		_propertiesTab = new MergeColumnDialog.PropertiesTab();
		pane.addTab(MergeColumnDialog.i18n.TABBEDPANE_PROPERTIES_LABEL, _propertiesTab);
		// pane.addTab(MergeColumnDialog.i18n.TABBEDPANE_COLUMNS_LABEL, _columnTab);
		setAllButtonEnabled(false);
		setTitle(MergeColumnDialog.i18n.DIALOG_TITLE);
	}

	private void checkInputCompletion()
	{
		if (isNewColumn() && getNewColumnName().equals(""))
		{
			setAllButtonEnabled(false);
			return;
		}
		// if the check gets till here we have all the need information
		setAllButtonEnabled(true);
	}

	public boolean isNewColumn()
	{
		return _propertiesTab._newColumnRadio.isSelected();
	}

	public String getNewColumnName()
	{
		return _propertiesTab._newColumnNameField.getText();
	}

	public String getJoinString()
	{
		return _propertiesTab._joinStringField.getText();
	}

	public String getSecondColumn()
	{
		return (String) _propertiesTab._columnSecondBox.getSelectedItem();
	}

	public String getFirstColumn()
	{
		return (String) _propertiesTab._columnFirstBox.getSelectedItem();
	}

	public String getMergeInExistingColumn()
	{
		if (_propertiesTab._mergeInExistingBox.getSelectedItem().equals(i18n.PROPERTIES_COLUMN1_LABEL))
		{
			return getFirstColumn();
		}
		return getSecondColumn();
	}

	public boolean isMergeInExistingColumn()
	{
		return _propertiesTab._mergeInExistingColumnRadio.isSelected();
	}

	class PropertiesTab extends JPanel
	{
		private static final long serialVersionUID = -2466076386547185573L;

		private JComboBox _columnFirstBox;

		private JComboBox _columnSecondBox;

		private JComboBox _mergeInExistingBox;

		private JLabel _mergeNewColumnNameLabel;

		private JRadioButton _newColumnRadio;

		private JTextField _newColumnNameField;

		private JRadioButton _mergeInExistingColumnRadio;

		private JTextField _joinStringField;

		public PropertiesTab()
		{
			init();
		}

		private void init()
		{
			setLayout(new GridBagLayout());

			JLabel tableNameLabel = getBorderedLabel(i18n.PROPERTIES_TABLENAME_LABEL, emptyBorder);

			JTextField tableNameField = new JTextField();
			tableNameField.setPreferredSize(mediumField);
			tableNameField.setText(_localTableName);
			tableNameField.setEnabled(false);

			JLabel column1Label = getBorderedLabel(i18n.PROPERTIES_COLUMN1_LABEL, emptyBorder);
			_columnFirstBox = new JComboBox(_localTableColumns);
			_columnFirstBox.setPreferredSize(mediumField);
			_columnFirstBox.setActionCommand("ColumnFirst");
			_columnFirstBox.addActionListener(new ColumnActionListener());

			JLabel column2Label = getBorderedLabel(i18n.PROPERTIES_COLUMN2_LABEL, emptyBorder);
			_columnSecondBox = new JComboBox(_localTableColumns);
			_columnSecondBox.setSelectedIndex(1);
			_columnSecondBox.setActionCommand("ColumnSecond");
			_columnSecondBox.addActionListener(new ColumnActionListener());
			_columnSecondBox.setPreferredSize(mediumField);

			JLabel mergeToLabel = getBorderedLabel(i18n.PROPERTIES_MERGE_TO_LABEL, emptyBorder);
			_newColumnRadio = new JRadioButton();
			_newColumnRadio.setSelected(true);

			_newColumnRadio.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent actionEvent)
				{
					enableNewColumn(true);
					checkInputCompletion();
				}
			});

			_newColumnNameField = new JTextField();
			_newColumnNameField.setPreferredSize(new Dimension(83, 20));
			_newColumnNameField.addKeyListener(new KeyAdapter()
			{
				public void keyReleased(KeyEvent keyEvent)
				{
					checkInputCompletion();
				}
			});

			_mergeInExistingBox =
				new JComboBox(new String[] { i18n.PROPERTIES_COLUMN1_LABEL, i18n.PROPERTIES_COLUMN2_LABEL });
			_mergeInExistingBox.setPreferredSize(mediumField);
			_mergeInExistingBox.setEnabled(false);

			JLabel mergeNewColumnLabel =
				getBorderedLabel(i18n.PROPERTIES_MERGE_IN_NEW_COLUMN_LABEL, emptyBorder);
			mergeNewColumnLabel.setHorizontalAlignment(SwingConstants.LEFT);

			_mergeNewColumnNameLabel =
				getBorderedLabel(i18n.PROPERTIES_MERGE_IN_NEW_COLUMN_NAME_LABEL, emptyBorder);
			_mergeNewColumnNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
			_mergeNewColumnNameLabel.setPreferredSize(new Dimension(45, 20));
			JPanel mergeNewColumnNamePanel = new JPanel(new GridBagLayout());
			mergeNewColumnNamePanel.add(_mergeNewColumnNameLabel,
				new GridBagConstraints(	0,
												0,
												1,
												1,
												0,
												0,
												GridBagConstraints.WEST,
												GridBagConstraints.NONE,
												new Insets(0, 0, 0, 0),
												0,
												0));
			mergeNewColumnNamePanel.add(_newColumnNameField, new GridBagConstraints(1,
																											0,
																											1,
																											1,
																											0,
																											0,
																											GridBagConstraints.WEST,
																											GridBagConstraints.NONE,
																											new Insets(0, 0, 0, 0),
																											0,
																											0));

			JLabel mergeInExistingColumnLabel =
				getBorderedLabel(i18n.PROPERTIES_MERGE_IN_EXISTING_COLUMN_LABEL, emptyBorder);
			mergeInExistingColumnLabel.setHorizontalAlignment(SwingConstants.LEFT);
			_mergeInExistingColumnRadio = new JRadioButton();
			_mergeInExistingColumnRadio.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent actionEvent)
				{
					enableNewColumn(false);
					checkInputCompletion();

				}
			});

			ButtonGroup group = new ButtonGroup();
			group.add(_newColumnRadio);
			group.add(_mergeInExistingColumnRadio);

			JPanel mergeNewColumnPanel = new JPanel(new GridBagLayout());
			mergeNewColumnPanel.add(_newColumnRadio, new GridBagConstraints(	0,
																									0,
																									1,
																									1,
																									0,
																									0,
																									GridBagConstraints.WEST,
																									GridBagConstraints.NONE,
																									new Insets(0, 0, 0, 0),
																									0,
																									0));
			mergeNewColumnPanel.add(mergeNewColumnLabel, new GridBagConstraints(	1,
																										0,
																										1,
																										1,
																										1,
																										0,
																										GridBagConstraints.WEST,
																										GridBagConstraints.HORIZONTAL,
																										new Insets(0, 0, 0, 0),
																										0,
																										0));

			JPanel mergeinExistingColumnPanel = new JPanel(new GridBagLayout());
			mergeinExistingColumnPanel.add(_mergeInExistingColumnRadio,
				new GridBagConstraints(	0,
												0,
												1,
												1,
												0,
												0,
												GridBagConstraints.WEST,
												GridBagConstraints.NONE,
												new Insets(0, 0, 0, 0),
												0,
												0));
			mergeinExistingColumnPanel.add(mergeInExistingColumnLabel,
				new GridBagConstraints(	1,
												0,
												1,
												1,
												4,
												0,
												GridBagConstraints.WEST,
												GridBagConstraints.HORIZONTAL,
												new Insets(0, 0, 0, 0),
												0,
												0));

			JLabel joinStringLabel = getBorderedLabel(i18n.PROPERTIES_JOIN_STRING_LABEL, emptyBorder);
			_joinStringField = new JTextField();
			_joinStringField.setPreferredSize(mediumField);

			Insets insets = new Insets(5, 0, 5, 5);
			add(tableNameLabel, new GridBagConstraints(	0,
																		0,
																		1,
																		1,
																		1,
																		0,
																		GridBagConstraints.WEST,
																		GridBagConstraints.HORIZONTAL,
																		insets,
																		0,
																		0));
			add(tableNameField, new GridBagConstraints(	1,
																		0,
																		1,
																		1,
																		0,
																		0,
																		GridBagConstraints.WEST,
																		GridBagConstraints.NONE,
																		insets,
																		0,
																		0));

			add(column1Label, new GridBagConstraints(	0,
																	1,
																	1,
																	1,
																	1,
																	0,
																	GridBagConstraints.WEST,
																	GridBagConstraints.HORIZONTAL,
																	insets,
																	0,
																	0));
			add(_columnFirstBox, new GridBagConstraints(	1,
																		1,
																		1,
																		1,
																		0,
																		0,
																		GridBagConstraints.WEST,
																		GridBagConstraints.NONE,
																		insets,
																		0,
																		0));

			add(column2Label, new GridBagConstraints(	0,
																	2,
																	1,
																	1,
																	1,
																	0,
																	GridBagConstraints.WEST,
																	GridBagConstraints.HORIZONTAL,
																	insets,
																	0,
																	0));
			add(_columnSecondBox, new GridBagConstraints(1,
																		2,
																		1,
																		1,
																		0,
																		0,
																		GridBagConstraints.WEST,
																		GridBagConstraints.NONE,
																		insets,
																		0,
																		0));

			add(mergeToLabel, new GridBagConstraints(	0,
																	3,
																	1,
																	1,
																	1,
																	0,
																	GridBagConstraints.WEST,
																	GridBagConstraints.HORIZONTAL,
																	insets,
																	0,
																	0));
			add(mergeNewColumnPanel, new GridBagConstraints(1,
																			3,
																			1,
																			1,
																			1,
																			0,
																			GridBagConstraints.WEST,
																			GridBagConstraints.NONE,
																			insets,
																			0,
																			0));

			add(mergeNewColumnNamePanel, new GridBagConstraints(	1,
																					4,
																					1,
																					1,
																					1,
																					0,
																					GridBagConstraints.WEST,
																					GridBagConstraints.NONE,
																					insets,
																					0,
																					0));

			add(mergeinExistingColumnPanel, new GridBagConstraints(	1,
																						5,
																						1,
																						1,
																						1,
																						0,
																						GridBagConstraints.WEST,
																						GridBagConstraints.NONE,
																						insets,
																						0,
																						0));
			add(_mergeInExistingBox, new GridBagConstraints(1,
																			6,
																			1,
																			1,
																			0,
																			0,
																			GridBagConstraints.WEST,
																			GridBagConstraints.NONE,
																			insets,
																			0,
																			0));

			add(joinStringLabel, new GridBagConstraints(	0,
																		7,
																		1,
																		1,
																		1,
																		0,
																		GridBagConstraints.WEST,
																		GridBagConstraints.HORIZONTAL,
																		insets,
																		0,
																		0));
			add(_joinStringField, new GridBagConstraints(1,
																		7,
																		1,
																		1,
																		0,
																		0,
																		GridBagConstraints.WEST,
																		GridBagConstraints.NONE,
																		insets,
																		0,
																		0));

		}

		private void enableNewColumn(boolean aFlag)
		{
			_newColumnNameField.setEnabled(aFlag);
			_mergeNewColumnNameLabel.setEnabled(aFlag);
			_mergeInExistingBox.setEnabled(!aFlag);

		}

	}

	class ColumnActionListener implements ActionListener
	{

		public void actionPerformed(ActionEvent actionEvent)
		{

			if (_propertiesTab._columnFirstBox.getSelectedItem() == _propertiesTab._columnSecondBox.getSelectedItem())
			{
				JComboBox box = (JComboBox) actionEvent.getSource();

				if (box.getItemCount() > box.getSelectedIndex() + 1)
				{
					box.setSelectedIndex(box.getSelectedIndex() + 1);
				} else
				{
					box.setSelectedIndex(box.getSelectedIndex() - 1);
				}
			}

		}
	}

	public static void main(String[] args)
	{
		String tableName = "Customers";
		String[] columns = new String[] { "Id", "PhoneCountryCode", "PhoneAreaCode", "PhoneLocal" };

		MergeColumnDialog dialog = new MergeColumnDialog(tableName, columns);
		dialog.setVisible(true);
	}

}
