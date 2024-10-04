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

import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

public class MergeTableDialog extends AbstractRefactoringTabbedDialog implements IMergeTableDialog {

    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr =
            StringManagerFactory.getStringManager(MergeTableDialog.class);

    protected interface i18n {
        String DIALOG_TITLE =
                s_stringMgr.getString("MergeTableDialog.title");
        String TABBEDPANE_PROPERTIES_LABEL =
                s_stringMgr.getString("MergeTableDialog.propertiesTabName");

        String TABBEDPANE_COLUMNS_LABEL =
                s_stringMgr.getString("MergeTableDialog.columnsTabName");

        String PROPERTIES_TABLENAME_LABEL =
                s_stringMgr.getString("MergeTableDialog.tableNameLabel");

        String PROPERTIES_REFERENCES_LABEL =
                s_stringMgr.getString("MergeTableDialog.mergeTableLabel");

        String PROPERTIES_MERGE_DATA_LABEL =
                s_stringMgr.getString("MergeTableDialog.mergeDataLabel");

        String COLUMNS_REFERENCED_HEADER =
                s_stringMgr.getString("MergeTableDialog.mergeColumnsHeader");

        String COLUMNS_MERGE_TABLE_HEADER =
                s_stringMgr.getString("MergeTableDialog.mergeTableHeader");

        String COLUMNS_LOCAL_COLUMN_HEADER =
                s_stringMgr.getString("MergeTableDialog.localTableHeader");

        String COLUMNS_REFERENCING_LABEL =
                s_stringMgr.getString("MergeTableDialog.mergeLabel");

        String COLUMNS_ADD_BUTTON_LABEL =
                s_stringMgr.getString("MergeTableDialog.addButtonLabel");
        String COLUMNS_REMOVE_BUTTON_LABEL =
                s_stringMgr.getString("MergeTableDialog.removeButtonLabel");

    }

    private final String _localTableName;
    private String _referencedTable = "";
    private final TableColumnInfo[] _localTableColumns;
    private final HashMap<String, TableColumnInfo[]> _tables;

    private ColumnsTab _columnTab;
    private PropertiesTab _propertiesTab;


    public MergeTableDialog(String localTable, TableColumnInfo[] localColumns, HashMap<String, TableColumnInfo[]> tables) {
        super(new Dimension(430, 350));
        _localTableName = localTable;
        _localTableColumns = localColumns;
        _tables = tables;
        init();
    }


    private void init() {

        _columnTab = new ColumnsTab();
        _propertiesTab = new PropertiesTab();
        pane.addTab(i18n.TABBEDPANE_PROPERTIES_LABEL, _propertiesTab);
        pane.addTab(i18n.TABBEDPANE_COLUMNS_LABEL, _columnTab);
        setAllButtonEnabled(false);
        setTitle(i18n.DIALOG_TITLE);
    }


    private void checkInputCompletion() {
        if (_columnTab._columTable.getRowCount() == 0) {
            setAllButtonEnabled(false);
            return;
        }

        // if the check gets till here we have all the need information
        setAllButtonEnabled(true);
    }


    /**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.gui.IMergeTableDialog#getReferencedTable()
	 */
   public String getReferencedTable() {
        return _propertiesTab.getReferencesField();
    }


    /**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.gui.IMergeTableDialog#getWhereDataColumns()
	 */
   public Vector<String[]> getWhereDataColumns() {
        return _propertiesTab._columnTableModel.getRowData();
    }


    /**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.gui.IMergeTableDialog#getMergeColumns()
	 */
   public Vector<String> getMergeColumns() {
        return _columnTab.getMergeColumns();
    }


    /**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.gui.IMergeTableDialog#isMergeData()
	 */
   public boolean isMergeData() {
        return _propertiesTab.getMergeData();
    }


    class PropertiesTab extends JPanel {
        private JComboBox _referencesField;
        private JCheckBox _mergeDataBox;
        private JTable _columTable;
        private final ReferenceColumnTableModel _columnTableModel = new ReferenceColumnTableModel();

        private JComboBox localColumBox;
        private JComboBox referencingBox;
        private JButton removeButton, addButton;


        public PropertiesTab() {
            init();
            initData();
        }


        private void init() {
            setLayout(new GridBagLayout());

            JLabel nameLabel = getBorderedLabel(i18n.PROPERTIES_TABLENAME_LABEL, emptyBorder);

            JTextField tableField = new JTextField(_localTableName);
            tableField.setPreferredSize(mediumField);
            tableField.setEnabled(false);

            JLabel referencesLabel = getBorderedLabel(i18n.PROPERTIES_REFERENCES_LABEL, emptyBorder);
            _referencesField = new JComboBox();
            _referencesField.setPreferredSize(mediumField);

            _referencesField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    _referencedTable = (String) _referencesField.getSelectedItem();
                    _columnTab.setReferencedColumns(_referencedTable);
                    setReferencedColumnBox(_referencedTable);

                }
            });

            JLabel mergeDataLabel = getBorderedLabel(i18n.PROPERTIES_MERGE_DATA_LABEL, emptyBorder);
            _mergeDataBox = new JCheckBox();
            _mergeDataBox.setSelected(true);

            JPanel keyPanel = new JPanel(new GridBagLayout());
            _columTable = new JTable();
            _columTable.setModel(_columnTableModel);
            _columTable.setRowSelectionAllowed(true);
            _columTable.setColumnSelectionAllowed(false);
            _columTable.getTableHeader().setReorderingAllowed(false);
            _columTable.setCellSelectionEnabled(false);
            _columTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            _columTable.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent event) {
                    removeButton.setEnabled(true);
                }
            });

            JScrollPane scrollTablePane = new JScrollPane(_columTable);
            scrollTablePane.setPreferredSize(new Dimension(240, 50));
            JLabel localColumLabel = getBorderedLabel(i18n.COLUMNS_LOCAL_COLUMN_HEADER, emptyBorder);
            JLabel referencingLabel = getBorderedLabel(i18n.COLUMNS_MERGE_TABLE_HEADER, emptyBorder);

            localColumBox = new JComboBox();
            localColumBox.setPreferredSize(mediumField);
            referencingBox = new JComboBox();
            referencingBox.setPreferredSize(mediumField);

            JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 4, 4));
            addButton = new JButton(i18n.COLUMNS_ADD_BUTTON_LABEL);
            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String local = (String) localColumBox.getSelectedItem();
                    String ref = (String) referencingBox.getSelectedItem();

                    localColumBox.removeItem(local);
                    referencingBox.removeItem(ref);

                    _columnTableModel.addColumn(new String[]{local, ref});
                    _propertiesTab.enableReferencedComboBox(false);
                    if (localColumBox.getItemCount() == 0 || referencingBox.getItemCount() == 0)
                        addButton.setEnabled(false);

                    MergeTableDialog.this.checkInputCompletion();
                }
            });

            removeButton = new JButton(i18n.COLUMNS_REMOVE_BUTTON_LABEL);
            removeButton.setEnabled(false);

            removeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int index = _columTable.getSelectedRow();
                    if (index != -1) {
                        String[] removed = _columnTableModel.deleteRow(index);

                        localColumBox.addItem(removed[0]);
                        referencingBox.addItem(removed[1]);
                        addButton.setEnabled(true);
                        if (_columnTableModel.getRowCount() == 0) {
                            removeButton.setEnabled(false);
                            _propertiesTab.enableReferencedComboBox(true);
                        } else {
                            int deleteIndex = 0;
                            if (index > 0) deleteIndex = index - 1;
                            _columTable.getSelectionModel().setSelectionInterval(0, deleteIndex);
                        }
                    }
                    MergeTableDialog.this.checkInputCompletion();
                }
            });
            Insets left = new Insets(5, 5, 5, 0);
            Insets right = new Insets(5, 0, 5, 5);
            keyPanel.add(scrollTablePane, new GridBagConstraints(0, 0, 3, 3, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, left, 0, 0));
            keyPanel.add(localColumLabel, new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, left, 0, 0));
            keyPanel.add(localColumBox, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, right, 0, 0));
            keyPanel.add(buttonPanel, new GridBagConstraints(2, 3, 1, 2, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, right, 0, 0));
            keyPanel.add(referencingLabel, new GridBagConstraints(0, 4, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, left, 0, 0));
            keyPanel.add(referencingBox, new GridBagConstraints(1, 4, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, right, 0, 0));


            buttonPanel.add(addButton);
            buttonPanel.add(removeButton);


            add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
            add(tableField, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));

            add(referencesLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
            add(_referencesField, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));

            add(mergeDataLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
            add(_mergeDataBox, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));

            add(keyPanel, new GridBagConstraints(0, 3, 2, 2, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

        }


        private void setReferencesTable(String[] tableList) {
            for (String table : tableList) {
                _referencesField.addItem(table);
            }
        }


        private void setLocalColumnBox() {
            for (TableColumnInfo column : _localTableColumns) {
                localColumBox.addItem(column.getColumnName());
            }
        }


        private void setReferencedColumnBox(String table) {
            referencingBox.removeAllItems();
            for (TableColumnInfo info : _tables.get(table)) {

                referencingBox.addItem(info.getColumnName());
            }
        }


        private void enableReferencedComboBox(boolean enable) {
            _referencesField.setEnabled(enable);
        }


        public String getReferencesField() {
            return (String) _referencesField.getSelectedItem();
        }


        public boolean getMergeData() {
            return _mergeDataBox.isSelected();
        }


        private void initData() {
            setLocalColumnBox();

            //all tables except the merge into table
            String[] tables = _tables.keySet().toArray(new String[]{});
            Arrays.sort(tables);
            setReferencesTable(tables);

            setReferencedColumnBox(tables[0]);
            // all local and columns for the other tables..
        }


    }

    class ColumnsTab extends JPanel {

        private JTable _columTable;

        private final MergeTableColumnTableModel _columnTableModel = new MergeTableColumnTableModel();

        private JComboBox referencingBox;

        private JButton removeButton, addButton;
        private String _tableName = "";


        public ColumnsTab() {
            init();
        }


        private void init() {

            setLayout(new GridBagLayout());

            _columTable = new JTable();

            _columTable.setModel(_columnTableModel);
            _columTable.setRowSelectionAllowed(true);
            _columTable.setColumnSelectionAllowed(false);
            _columTable.getTableHeader().setReorderingAllowed(false);
            _columTable.setCellSelectionEnabled(false);
            _columTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            _columTable.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent event) {
                    removeButton.setEnabled(true);
                }
            });

            JScrollPane scrollTablePane = new JScrollPane(_columTable);

            JLabel referencingLabel = getBorderedLabel(i18n.COLUMNS_REFERENCING_LABEL, emptyBorder);

            referencingBox = new JComboBox();
            referencingBox.setPreferredSize(mediumField);

            JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 10, 10));
            addButton = new JButton(i18n.COLUMNS_ADD_BUTTON_LABEL);
            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String ref = (String) referencingBox.getSelectedItem();


                    referencingBox.removeItem(ref);

                    _columnTableModel.addColumn(ref);
                    _propertiesTab.enableReferencedComboBox(false);
                    if (referencingBox.getItemCount() == 0)
                        addButton.setEnabled(false);

                    MergeTableDialog.this.checkInputCompletion();
                }
            });

            removeButton = new JButton(i18n.COLUMNS_REMOVE_BUTTON_LABEL);
            removeButton.setEnabled(false);

            removeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int index = _columTable.getSelectedRow();
                    if (index != -1) {
                        String removed = _columnTableModel.deleteRow(index);

                        referencingBox.addItem(removed);
                        addButton.setEnabled(true);
                        if (_columnTableModel.getRowCount() == 0) {
                            removeButton.setEnabled(false);
                            _propertiesTab.enableReferencedComboBox(true);
                        } else {
                            int deleteIndex = 0;
                            if (index > 0) deleteIndex = index - 1;
                            _columTable.getSelectionModel().setSelectionInterval(0, deleteIndex);
                        }
                    }
                    MergeTableDialog.this.checkInputCompletion();
                }
            });

            buttonPanel.add(addButton);
            buttonPanel.add(removeButton);

            //adding all Colums together
            add(scrollTablePane, new GridBagConstraints(0, 0, 2, 3, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            add(referencingLabel, new GridBagConstraints(0, 4, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0));
            add(referencingBox, new GridBagConstraints(1, 4, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 3, 0), 0, 0));

            add(buttonPanel, new GridBagConstraints(0, 5, 2, 0, 1, 0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        }


        public void setReferencedColumns(String tableName) {
            if (!tableName.equals(_tableName)) {
                _columnTableModel.clear();
            }
            _tableName = tableName;
            TableColumnInfo[] tableColumns = _tables.get(tableName);
            Vector<String> columns = new Vector<String>();
            for (TableColumnInfo columnInfo : tableColumns) {
                columns.add(columnInfo.getColumnName());
            }
            setReferencedColumns(columns.toArray(new String[]{}));
        }


        private void setReferencedColumns(String[] refColums) {
            referencingBox.removeAllItems();
            ArrayList<String> localColumnNames = new ArrayList<String>();

            for (TableColumnInfo localColumn : _localTableColumns) {
                localColumnNames.add(localColumn.getColumnName());
            }

            for (String ref : refColums) {
                if (localColumnNames.contains(ref)) continue;
                referencingBox.addItem(ref);
            }

        }


        public Vector<String> getMergeColumns() {
            return _columnTableModel.getRowData();
        }
    }

    class ReferenceColumnTableModel extends AbstractTableModel {
        private final Vector<String[]> rowData = new Vector<String[]>();
        private final String[] columnNames = new String[]{i18n.COLUMNS_LOCAL_COLUMN_HEADER, i18n.COLUMNS_MERGE_TABLE_HEADER};


        public String getColumnName(int col) {
            return columnNames[col];
        }


        public int getRowCount() {
            return rowData.size();
        }


        public int getColumnCount() {
            return columnNames.length;
        }


        public Object getValueAt(int row, int col) {
            return rowData.get(row)[col];
        }


        public boolean isCellEditable(int row, int col) {
            return false;
        }


        public void addColumn(String[] column) {
            rowData.add(column);
            fireTableDataChanged();
        }


        public String[] deleteRow(int row) {
            String[] removedRow = rowData.remove(row);
            fireTableDataChanged();
            return removedRow;
        }


        public Vector<String[]> getRowData() {
            return rowData;
        }
    }
}
