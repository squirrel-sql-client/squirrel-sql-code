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
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class AddLookupTableDialog extends AbstractRefactoringTabbedDialog
{
	private static final long serialVersionUID = -4903417767342627807L;

	/** Logger for this class. */
	@SuppressWarnings("unused")
	private static final ILogger s_log = LoggerController.createLogger(AddLookupTableDialog.class);

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AddLookupTableDialog.class);

	static interface i18n
	{
		String DIALOG_TITLE = s_stringMgr.getString("AddLookupTableDialog.dialogTitle");

		String SOURCE_LABEL = s_stringMgr.getString("AddLookupTableDialog.sourceTabLabel");

		String LOOKUP_LABEL = s_stringMgr.getString("AddLookupTableDialog.lookupTabLabel");

		String BEHAVIOUR_LABEL = s_stringMgr.getString("AddLookupTableDialog.behaviourTabLabel");

		String SOURCE_TABLE_LABEL = s_stringMgr.getString("AddLookupTableDialog.sourceTableLabel");

		String SOURCE_COLUMN_LABEL = s_stringMgr.getString("AddLookupTableDialog.sourceColumnsLabel");

		String SOURCE_FOREIGNKEY_LABEL = s_stringMgr.getString("AddLookupTableDialog.sourceForeignKeyLabel");

		String SOURCE_MODE_LABEL = s_stringMgr.getString("AddLookupTableDialog.sourceModeLabel");

		String SOURCE_MODE_KEEP_LABEL = s_stringMgr.getString("AddLookupTableDialog.sourceModeKeepLabel");

		String SOURCE_MODE_KEEP_TOOLTIP = s_stringMgr.getString("AddLookupTableDialog.sourceModeKeepTooltip");

		String SOURCE_MODE_REPLACE_LABEL = s_stringMgr.getString("AddLookupTableDialog.sourceModeReplaceLabel");

		String SOURCE_MODE_REPLACE_TOOLTIP =
			s_stringMgr.getString("AddLookupTableDialog.sourceModeReplaceTooltip");

		String LOOKUP_TABLE_LABEL = s_stringMgr.getString("AddLookupTableDialog.lookupTableLabel");

		String LOOKUP_COLUMNS_LABEL = s_stringMgr.getString("AddLookupTableDialog.lookupColumnsLabel");

		String LOOKUP_COLUMNSTABLE_HEADER1 =
			s_stringMgr.getString("AddLookupTableDialog.lookupColumnsTableHeader1");

		String LOOKUP_COLUMNSTABLE_HEADER2 =
			s_stringMgr.getString("AddLookupTableDialog.lookupColumnsTableHeader2");

		String BEHAVIOUR_DROP_LABEL = s_stringMgr.getString("AddLookupTableDialog.behaviourDropLabel");

		String BEHAVIOUR_DROP_CASCADE_LABEL =
			s_stringMgr.getString("AddLookupTableDialog.behaviourDropCascadeLabel");

		String BEHAVIOUR_DROP_CASCADE_TOOLIP =
			s_stringMgr.getString("AddLookupTableDialog.behaviourDropCascadeToolTip");
	}

	private SourceTab _sourceTab;

	private LookupTab _lookupTab;

	private BehaviourTab _behaviourTab;

	private final String _tableName;

	private final String[] _columnNames;

	public static final int MODE_KEEP = 1;

	public static final int MODE_REPLACE = 2;

	public AddLookupTableDialog(String tableName, String[] columnNames)
	{
		super(new Dimension(430, 350));
		_tableName = tableName;
		_columnNames = columnNames;
		init();
	}

	private void init()
	{
		_sourceTab = new SourceTab();
		_lookupTab = new LookupTab();
		_behaviourTab = new BehaviourTab();
		pane.addTab(i18n.SOURCE_LABEL, _sourceTab);
		pane.addTab(i18n.LOOKUP_LABEL, _lookupTab);

		setAllButtonEnabled(false);
		setTitle(i18n.DIALOG_TITLE);
	}

	public String getSourceColumn()
	{
		return _sourceTab.getColumn();
	}

	public String getForeignKeyName()
	{
		return _sourceTab.getForeignKey();
	}

	public int getMode()
	{
		return _sourceTab.getMode();
	}

	public String getLookupTableName()
	{
		return _lookupTab.getTableName();
	}

	public String getLookupPrimaryKey()
	{
		return _lookupTab.getPrimaryKey();
	}

	public String getLookupSecondColumn()
	{
		return _lookupTab.getSecondColumn();
	}

	public boolean getDropCascade()
	{
		return _behaviourTab.getDropCascade();
	}

	private JPanel emptyBorderPanel(JComponent c)
	{
		JPanel panel = new JPanel(new GridLayout(1, 1));
		panel.setBorder(emptyBorder);
		panel.add(c);
		return panel;
	}

	private class SourceTab extends JPanel
	{
		private static final long serialVersionUID = -1731428049078527137L;

		private JList _columnList;

		private JTextField _foreignKeyTextField;

		private ButtonGroup _modeButtonGroup;

		private JRadioButton _keepRadioButton;

		private JRadioButton _replaceRadioButton;

		public SourceTab()
		{
			init();
		}

		private void init()
		{
			setLayout(new GridBagLayout());
			setBorder(new EmptyBorder(10, 0, 0, 30));

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = -1;

			// Table
			JLabel tableLabel = getBorderedLabel(i18n.SOURCE_TABLE_LABEL + " ", emptyBorder);
			add(tableLabel, getLabelConstraints(gbc));

			JTextField tableTextField = getSizedTextField(mediumField);
			tableTextField.setText(_tableName);
			tableTextField.setEditable(false);
			add(emptyBorderPanel(tableTextField), getFieldConstraints(gbc));

			// Column
			JLabel columnLabel = getBorderedLabel(i18n.SOURCE_COLUMN_LABEL + " ", emptyBorder);
			add(columnLabel, getLabelConstraints(gbc));

			_columnList = new JList(_columnNames);
			_columnList.addListSelectionListener(new ColumnListSelectionListener());
			_columnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			JScrollPane columnScrollPane = new JScrollPane(_columnList);
			gbc = getFieldConstraints(gbc);
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.BOTH;
			add(emptyBorderPanel(columnScrollPane), gbc);

			// Foreign Key
			JLabel foreignKeyLabel = getBorderedLabel(i18n.SOURCE_FOREIGNKEY_LABEL + " ", emptyBorder);
			add(foreignKeyLabel, getLabelConstraints(gbc));

			_foreignKeyTextField = getSizedTextField(mediumField);
			add(emptyBorderPanel(_foreignKeyTextField), getFieldConstraints(gbc));

			// Mode
			JLabel modeLabel = getBorderedLabel(i18n.SOURCE_MODE_LABEL + " ", emptyBorder);
			add(modeLabel, getLabelConstraints(gbc));

			_keepRadioButton = new JRadioButton(i18n.SOURCE_MODE_KEEP_LABEL);
			_keepRadioButton.setToolTipText(i18n.SOURCE_MODE_KEEP_TOOLTIP);
			_keepRadioButton.setSelected(true);
			_keepRadioButton.addItemListener(new ModeItemListener());

			_replaceRadioButton = new JRadioButton(i18n.SOURCE_MODE_REPLACE_LABEL);
			_replaceRadioButton.setToolTipText(i18n.SOURCE_MODE_REPLACE_TOOLTIP);
			_replaceRadioButton.addItemListener(new ModeItemListener());

			_modeButtonGroup = new ButtonGroup();
			_modeButtonGroup.add(_keepRadioButton);
			_modeButtonGroup.add(_replaceRadioButton);

			JPanel modePanel = new JPanel(new GridLayout(2, 1));
			modePanel.add(_keepRadioButton);
			modePanel.add(_replaceRadioButton);
			add(modePanel, getFieldConstraints(gbc));
		}

		public String getColumn()
		{
			return String.valueOf(_columnList.getSelectedValue());
		}

		public String getForeignKey()
		{
			return _foreignKeyTextField.getText();
		}

		public int getMode()
		{
			if (_keepRadioButton.isSelected())
			{
				return MODE_KEEP;
			} else if (_replaceRadioButton.isSelected())
			{
				return MODE_REPLACE;
			} else
			{
				throw new IllegalStateException("There should always be at least one mode selected!");
			}
		}

		private class ColumnListSelectionListener implements ListSelectionListener
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (_columnList.getSelectedValue() == null)
				{
					setAllButtonEnabled(false);
				} else
				{
					String selected = String.valueOf(_columnList.getSelectedValue());
					_lookupTab.onSourceColumnChange(selected);
					_foreignKeyTextField.setText("fk_" + selected);
					setAllButtonEnabled(true);
				}
			}
		}

		private class ModeItemListener implements ItemListener
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					_lookupTab.onSourceModeChange(getMode());
					if (e.getItem().equals(_replaceRadioButton))
						pane.addTab(i18n.BEHAVIOUR_LABEL, _behaviourTab);
					else
						pane.remove(_behaviourTab);
				}
			}
		}
	}

	private class LookupTab extends JPanel
	{
		private static final long serialVersionUID = 6999302068227311098L;

		private JTextField _tableTextField;

		private final JTable _columnTable = new JTable();

		private final AddLookupTableColumnTableModel _columnTableModel = new AddLookupTableColumnTableModel();

		public LookupTab()
		{
			init();
		}

		private void init()
		{
			setLayout(new GridBagLayout());
			setBorder(new EmptyBorder(10, 0, 0, 30));

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = -1;

			// Table
			JLabel tableLabel = getBorderedLabel(i18n.LOOKUP_TABLE_LABEL + " ", emptyBorder);
			add(tableLabel, getLabelConstraints(gbc));

			_tableTextField = getSizedTextField(mediumField);
			_tableTextField.setEditable(true);
			add(emptyBorderPanel(_tableTextField), getFieldConstraints(gbc));

			// Columns
			JLabel columnLabel = getBorderedLabel(i18n.LOOKUP_COLUMNS_LABEL + " ", emptyBorder);
			add(columnLabel, getLabelConstraints(gbc));

			_columnTable.setModel(_columnTableModel);
			_columnTable.setRowSelectionAllowed(true);
			_columnTable.setColumnSelectionAllowed(false);
			_columnTable.setCellSelectionEnabled(true);
			_columnTable.getTableHeader().setReorderingAllowed(false);
			_columnTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			JScrollPane columnScrollPane = new JScrollPane(_columnTable);
			gbc = getFieldConstraints(gbc);
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.BOTH;
			add(emptyBorderPanel(columnScrollPane), gbc);
		}

		public void onSourceColumnChange(String column)
		{
			_tableTextField.setText(column);
			onChange(column, _sourceTab.getMode());
		}

		public void onSourceModeChange(int mode)
		{
			onChange(_sourceTab.getColumn(), mode);
		}

		private void onChange(String column, int mode)
		{
			if (mode == MODE_KEEP)
			{
				_columnTableModel.clear();
				_columnTableModel.addRow(new String[] { column, "Primary Key" });
			} else if (mode == MODE_REPLACE)
			{
				_columnTableModel.clear();
				_columnTableModel.addRow(new String[] { column + "Nr", "Primary Key" });
				_columnTableModel.addRow(new String[] { column, "" });
			}
		}

		public String getTableName()
		{
			return _tableTextField.getText();
		}

		public String getPrimaryKey()
		{
			if (_columnTableModel.getRowCount() > 0)
				return String.valueOf(_columnTableModel.getValueAt(0, 0));
			else
				return null;
		}

		public String getSecondColumn()
		{
			if (_columnTableModel.getRowCount() > 1)
				return String.valueOf(_columnTableModel.getValueAt(1, 0));
			else
				return null;
		}
	}

	private class BehaviourTab extends JPanel
	{
		private static final long serialVersionUID = -1528361503293831825L;

		private JCheckBox _dropCascade;

		public BehaviourTab()
		{
			init();
		}

		private void init()
		{
			setLayout(new GridBagLayout());

			// Drop Conflicts
			_dropCascade = new JCheckBox(i18n.BEHAVIOUR_DROP_CASCADE_LABEL);
			_dropCascade.setToolTipText(i18n.BEHAVIOUR_DROP_CASCADE_TOOLIP);

			JPanel dropPanel = new JPanel(new GridLayout(1, 1));
			dropPanel.setBorder(BorderFactory.createTitledBorder(i18n.BEHAVIOUR_DROP_LABEL));
			dropPanel.add(_dropCascade);

			add(dropPanel);
		}

		public boolean getDropCascade()
		{
			return _dropCascade.isSelected();
		}
	}

	private JTextField getSizedTextField(Dimension mediumField)
	{
		JTextField result = new JTextField();
		result.setPreferredSize(mediumField);
		return result;
	}

	public static void main(String[] args)
	{
		final AddLookupTableDialog dialog =
			new AddLookupTableDialog("City", new String[] { "CityNr", "Name", "State", "Country" });
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				dialog.setVisible(true);
			}
		});
	}
}
